package cn.ms.mconf.zookeeper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MParamType;
import cn.ms.mconf.support.NotifyConf;
import cn.ms.micro.common.ConcurrentHashSet;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;

import com.alibaba.fastjson.JSON;

/**
 * The base of Zookeeper Mconf.
 * 
 * @author lry
 */
@SpiMeta(name = "zookeeper")
public class ZookeeperMconf extends AbstractMconf {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperMconf.class);
	
	
	private CuratorFramework client;
	private ConnectionState globalState = null;
	
	private final ExecutorService pool = Executors.newFixedThreadPool(2);
	@SuppressWarnings("rawtypes")
	private final Map<String, Set<NotifyConf>> pushNotifyConfMap = new ConcurrentHashMap<String, Set<NotifyConf>>();
	private final Map<String, Map<String, Object>> pushMap = new ConcurrentHashMap<String, Map<String, Object>>();
	private final Map<String, PathChildrenCache> pathChildrenCacheMap = new ConcurrentHashMap<String, PathChildrenCache>();
	
	@Override
	public void connect(URL url) {
		super.connect(url);
		
		String connAddrs = url.getBackupAddress();
		int timeout = url.getParameter(MParamType.TIMEOUT.getName(), MParamType.TIMEOUT.getIntValue());
		int session = url.getParameter(MParamType.SESSION.getName(), MParamType.SESSION.getIntValue());
		
		Builder builder = CuratorFrameworkFactory.builder().connectString(connAddrs)
				.retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(timeout).sessionTimeoutMs(session);
		final CountDownLatch cd = new CountDownLatch(1);
		client = builder.build();
		client.getConnectionStateListenable().addListener(
				new ConnectionStateListener() {
					public void stateChanged(CuratorFramework client, ConnectionState state) {
						logger.info("The registration center connection status is changed to [{}]", state);
						if(globalState == null || state == ConnectionState.CONNECTED) {
							cd.countDown();
							globalState = state;
						}
					}
				});
		client.start();
		
		try {
			cd.await(timeout, TimeUnit.MILLISECONDS);
			if(ConnectionState.CONNECTED != globalState){
				throw new TimeoutException("The connection zookeeper is timeout.");
			}
		} catch (Exception e) {
			logger.error("The await exception.", e);
		}
	}

	@Override
	public boolean available() {
		return client.getZookeeperClient().isConnected();
	}

	@Override
	public <T> void addConf(URL url, T data) {
		String path = this.wrapperPaths(url);
		byte[] dataByte = null;
		try {
			String json = this.obj2Json(data);
			dataByte = json.getBytes(Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException("Serialized data exception", e);
		}

		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, dataByte);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Add data exception", e);
		}
	}

	@Override
	public void delConf(URL url) {
		String path = this.wrapperPaths(url);
		
		try {
			client.delete().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Delete data exception", e);
		}
	}

	@Override
	public <T> void upConf(URL url, T data) {
		String path = this.wrapperPaths(url);
		
		byte[] dataByte = null;
		try {
			dataByte = this.obj2Json(data).getBytes(Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException("Serialized data exception", e);
		}

		try {
			client.setData().forPath(path, dataByte);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Modify data exception", e);
		}
	}
	
	@Override
	public <T> T pull(URL url, Class<T> cls) {
		String path = this.wrapperPaths(url);
		
		byte[] dataByte = null;

		try {
			dataByte = client.getData().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Modify data exception", e);
		}

		if (dataByte == null) {
			return null;
		}

		try {
			String json = new String(dataByte, Charset.forName("UTF-8"));
			return (T)json2Obj(json, cls);
		} catch (Exception e) {
			throw new IllegalStateException("UnSerialized data exception", e);
		}
	}
	
	@Override
	public <T> List<T> pulls(URL url, Class<T> cls) {
		String path = this.wrapperPaths(url);
		List<T> list = new ArrayList<T>();
		
		//Query all dataId lists
		List<String> childNodeList= null;
		try {
			childNodeList = client.getChildren().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Gets all child node exceptions", e);
		}
		
		if(childNodeList!=null){
			for (String childNode:childNodeList) {
				String dataId = decode(childNode);
				if(StringUtils.isBlank(dataId)){
					throw new RuntimeException("Invalid data, dataId=="+dataId);
				}
				
				String tempPath = this.wrapperPaths(url);
				String json;
				byte[] dataByte = null;

				try {
					dataByte = client.getData().forPath(tempPath);
				} catch (NoNodeException e) {
				} catch (Exception e) {
					throw new IllegalStateException("Modify data exception", e);
				}

				if (dataByte == null) {
					return null;
				}

				try {
					json = new String(dataByte, Charset.forName("UTF-8"));
				} catch (Exception e) {
					throw new IllegalStateException("UnSerialized data exception", e);
				}
				
				
				T t = (T)json2Obj(json, cls);
				if(t!=null){
					list.add(t);
				}
			}
		}
		
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void push(final URL url, final Class<T> cls, final NotifyConf<T> notifyConf) {
		final String path = this.wrapperPaths(url);
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		//允许多个监听者监听同一个节点
		Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(path);
		if(notifyConfs == null){
			pushNotifyConfMap.put(path, notifyConfs = new ConcurrentHashSet<NotifyConf>());
		}
		notifyConfs.add(notifyConf);
		
		if(pushMap.containsKey(path)){//已被订阅
			List list = new ArrayList();
			list.addAll(pushMap.get(path).values());
			notifyConf.notify(list);//通知一次
		} else {
			final Map<String, Object> tempMap;
			pushMap.put(path, tempMap = new ConcurrentHashMap<String, Object>());
			
			try {
				final PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
				childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
				childrenCache.getListenable().addListener(
					new PathChildrenCacheListener() {
						private boolean isInit = false;
						@Override
						public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
							ChildData childData = event.getData();
							
							if(event.getInitialData() != null){
								isInit = true;
							}
							
							if (childData == null) {
								logger.debug("The is listenering PATH[{}], initialization notify all data[{}].", path, JSON.toJSONString(tempMap));
							} else {
								String tempPath = event.getData().getPath();
								String tempJsonData = new String(event.getData().getData(), Charset.forName("UTF-8"));
								T t = (T)JSON.parseObject(tempJsonData, cls);
								
								if(PathChildrenCacheEvent.Type.CHILD_ADDED == event.getType()
										|| PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()){
									tempMap.put(tempPath, t);
								} else if(PathChildrenCacheEvent.Type.CHILD_REMOVED == event.getType()){
									tempMap.remove(tempPath);
								}
								
								if(isInit){
									logger.debug("The changed PATH[{}] update data[{}].", tempPath, event.getType(), tempJsonData);
									logger.debug("The changed PATH[{}] notify all datas[{}].", path, JSON.toJSONString(tempMap));
									Set<NotifyConf> tempNotifyConfSet = pushNotifyConfMap.get(path);
									for (NotifyConf tempNotifyConf:tempNotifyConfSet) {//通知每一个监听器
										List list = new ArrayList();
										list.addAll(tempMap.values());
										tempNotifyConf.notify(list);
									}									
								}
							}
						}
					}, pool);
				pathChildrenCacheMap.put(path, childrenCache);
			} catch (Exception e) {
				logger.error("The PathChildrenCache add listener exception.", e);
			}
		}
	}
	
	@Override
	public void unpush(URL url) {
		String path = this.wrapperPaths(url);
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		PathChildrenCache pathChildrenCache = pathChildrenCacheMap.get(path);
		if(pathChildrenCache!=null){
			try {
				pathChildrenCache.close();
			} catch (IOException e) {
				logger.error("PathChildrenCache close exception", e);
			}
		}
		
		if (pushNotifyConfMap.containsKey(path)) {
			pushNotifyConfMap.remove(path);
		}
		
		if (pushMap.containsKey(path)) {
			pushMap.remove(path);
		}
	}
	
	@Override
	public <T> void unpush(URL url, NotifyConf<T> notifyConf) {
		String path = this.wrapperPaths(url);
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		PathChildrenCache pathChildrenCache = pathChildrenCacheMap.get(path);
		if(pathChildrenCache!=null){
			try {
				pathChildrenCache.close();
			} catch (IOException e) {
				logger.error("PathChildrenCache close exception", e);
			}
		}

		@SuppressWarnings("rawtypes")
		Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(path);
		if (notifyConfs != null) {
			if (notifyConfs.contains(notifyConf)) {
				notifyConfs.remove(notifyConf);
			}
		}
		
		if (pushNotifyConfMap.get(path) == null) {
			pushMap.remove(path);
		}
	}
	
	private String wrapperPaths(URL url) {
		StringBuffer sb = new StringBuffer();
		sb.append("/").append(this.path).append(this.wrapperPath(root, url));
		sb.append("/").append(url.getProtocol()).append(this.wrapperPath(app, url));
		
		String[] pathArray = url.getPath().split("\\/");
		if(2 != pathArray.length){
			throw new RuntimeException("The path must 'conf/data'.");
		}
		
		sb.append("/").append(pathArray[0]).append(this.wrapperPath(conf, url));
		sb.append("/").append(pathArray[1]).append(this.wrapperPath(data, url));
		
		return sb.toString();
	}
	
	//$NON-NLS-The Node Governor$
	
	@Override
	public Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> structures() {
		Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> map = new ConcurrentHashMap<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>>();
		
		try {
			List<String> rootChildNodeList = client.getChildren().forPath("/");
			for (String rootChildNode:rootChildNodeList) {
				if(rootChildNode.startsWith(path)){
					List<String> appChildNodeList = client.getChildren().forPath("/" + rootChildNode);
					for (String appChildNode:appChildNodeList) {
						URL rootChildNodeURL = URL.valueOf("/"+URL.decode(appChildNode));
						// setter node 
						String node = rootChildNodeURL.getParameter(NODO_KEY);
						if(StringUtils.isBlank(node)){
							node = DEFAULT_KEY+NODO_KEY;
						}
						Map<String, Map<String, Map<String, Map<String, Set<String>>>>> nodeMap = map.get(node);
						if(nodeMap == null){
							map.put(node, nodeMap = new ConcurrentHashMap<String, Map<String,Map<String,Map<String,Set<String>>>>>());
						}
						// setter app
						String app = rootChildNodeURL.getPath();
						Map<String,Map<String,Map<String,Set<String>>>> appMap = nodeMap.get(app);
						if(appMap == null){
							nodeMap.put(app, appMap = new ConcurrentHashMap<String, Map<String,Map<String,Set<String>>>>());
						}
						
						List<String> confChildNodeList = client.getChildren().forPath("/" + rootChildNode + "/" + appChildNode);
						for (String confChildNode:confChildNodeList) {
							URL confChildNodeURL = URL.valueOf("/"+URL.decode(confChildNode));
							// setter env
							String env = confChildNodeURL.getParameter(ENV_KEY);
							if(StringUtils.isBlank(env)){
								env = DEFAULT_KEY+ENV_KEY;
							}
							Map<String,Map<String,Set<String>>> envMap = appMap.get(env);
							if(envMap == null){
								appMap.put(env, envMap = new ConcurrentHashMap<String, Map<String,Set<String>>>());
							}
							// setter conf confChildNodeURL.getPath()
							String conf = confChildNodeURL.getPath();
							Map<String,Set<String>> confMap = envMap.get(conf);
							if(confMap == null){
								envMap.put(conf, confMap = new ConcurrentHashMap<String,Set<String>>());
							}
							
							List<String> dataChildNodeList = client.getChildren().forPath("/" + rootChildNode + "/" + appChildNode + "/" + confChildNode);
							for (String dataChildNode:dataChildNodeList) {
								URL dataChildNodeURL = URL.valueOf("/"+URL.decode(dataChildNode));
								// setter group
								String group = dataChildNodeURL.getParameter(GROUP_KEY);
								if(StringUtils.isBlank(group)){
									group = DEFAULT_KEY+GROUP_KEY;
								}
								Set<String> groupMap = confMap.get(group);
								if(groupMap == null){
									confMap.put(group, groupMap = new ConcurrentHashSet<String>());
								}
								// setter version
								String version = dataChildNodeURL.getParameter(VERSION_KEY);
								if(StringUtils.isBlank(version)){
									version = DEFAULT_KEY+VERSION_KEY;
								}
								groupMap.add(version);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("The get confs is exception.", e);
		}
		
		return map;
	}

}
