package cn.ms.mconf.zookeeper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.beanutils.BeanUtils;
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
import cn.ms.mconf.support.Cmd;
import cn.ms.mconf.support.DataConf;
import cn.ms.mconf.support.Notify;
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
	private final Map<String, Set<Notify>> pushNotifyMap = new ConcurrentHashMap<String, Set<Notify>>();
	private final Map<String, Map<String, Object>> pushMap = new ConcurrentHashMap<String, Map<String, Object>>();
	private final Map<String, PathChildrenCache> pathChildrenCacheMap = new ConcurrentHashMap<String, PathChildrenCache>();

	public static void main(String[] args) {
		ZookeeperMconf zm = new ZookeeperMconf();
		URL mconfURL = URL.valueOf("zookeeper://127.0.0.1:2181/mconf?timeout=15000&session=60000&app=node&conf=env&data=group,version");
		zm.connect(mconfURL);
		try {
			System.out.println(zm.client.delete().deletingChildrenIfNeeded().forPath("/mconf/ms-gateway?node=node01/api?env=test/1?group=S01&version=1.0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void connect(URL url) {
		super.connect(url);

		String connAddrs = url.getBackupAddress();
		// Connection timeout, defaults to 60s
		int timeout = url.getParameter("timeout", 60 * 1000);
		// Expired cleanup time, defaults to 60s
		int session = url.getParameter("session", 60 * 1000);

		Builder builder = CuratorFrameworkFactory.builder()
				.connectString(connAddrs)
				.retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
				.connectionTimeoutMs(timeout).sessionTimeoutMs(session);
		final CountDownLatch cd = new CountDownLatch(1);
		client = builder.build();
		client.getConnectionStateListenable().addListener(
				new ConnectionStateListener() {
					public void stateChanged(CuratorFramework client, ConnectionState state) {
						logger.info("The registration center connection status is changed to [{}].", state);
						if (globalState == null || state == ConnectionState.CONNECTED) {
							cd.countDown();
							globalState = state;
						}
					}
				});
		client.start();

		try {
			cd.await(timeout, TimeUnit.MILLISECONDS);
			if (ConnectionState.CONNECTED != globalState) {
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
	public void addConf(Cmd cmd, Object obj) {
		String path = cmd.buildRoot(super.ROOT).buildKey();
		
		byte[] dataByte = null;
		try {
			String json = super.obj2Json(obj);
			logger.debug("The PATH[{}] add conf data[{}].", path, json);

			dataByte = json.getBytes(Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException("Serialized data exception.", e);
		}

		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, dataByte);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Add data exception.", e);
		}
	}

	@Override
	public void delConf(Cmd cmd) {
		try {
			String path;
			if (StringUtils.isNotBlank(cmd.getData())) {
				path = cmd.buildRoot(super.ROOT).buildKey();
				logger.debug("The PATH[{}] delete conf data.", path);
			} else {
				path = cmd.buildRoot(super.ROOT).buildPrefixKey();
				logger.debug("The PATH[{}] and SubPATH delete conf datas.", path);
			}
			
			client.delete().deletingChildrenIfNeeded().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Delete data exception.", e);
		}
	}

	@Override
	public void upConf(Cmd cmd, Object obj) {
		String path = cmd.buildRoot(super.ROOT).buildKey();
		
		byte[] dataByte = null;
		try {
			String json = super.obj2Json(obj);
			logger.debug("The PATH[{}] update conf data[{}].", path, json);
			
			dataByte = json.getBytes(Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException("Serialized data exception.", e);
		}

		try {
			client.setData().forPath(path, dataByte);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("UpConf data exception.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(Cmd cmd, Class<T> cls) {
		String path = cmd.buildRoot(super.ROOT).buildKey();
		logger.debug("The PATH[{}] pull conf data.", path);
		
		byte[] dataByte = null;
		try {
			dataByte = client.getData().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Pull data exception.", e);
		}

		if (dataByte == null) {
			return null;
		} else {
			String json = new String(dataByte, Charset.forName("UTF-8"));
			logger.debug("The PATH[{}] pulled conf data[{}].", path, json);
			
			try {
				if (cls == null) {
					return (T)json;
				} else {
					return super.json2Obj(json, cls);
				}
			} catch (Exception e) {
				throw new IllegalStateException("UnSerialized data exception.", e);
			}
		}
	}

	@Override
	public <T> List<T> pulls(Cmd cmd, Class<T> cls) {
		String path = cmd.buildRoot(super.ROOT).buildPrefixKey();
		logger.debug("The PATH[{}] pulls conf data.", path);
		
		// Query all dataId lists
		List<T> list = new ArrayList<T>();
		List<String> childNodeList = null;
		try {
			childNodeList = client.getChildren().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Gets all child node exceptions.", e);
		}

		if (childNodeList == null) {
			return list;
		}
		
		for (String childNode : childNodeList) {
			String json;
			byte[] dataByte = null;
			String allPath = path + "/" + childNode;
			
			try {
				dataByte = client.getData().forPath(allPath);
			} catch (NoNodeException e) {
			} catch (Exception e) {
				throw new IllegalStateException("Modify data exception.", e);
			}

			if (dataByte == null) {
				continue;
			}

			try {
				json = new String(dataByte, Charset.forName("UTF-8"));
				logger.debug("The PATH[{}] pullsed conf data[{}].", allPath, json);
			} catch (Exception e) {
				throw new IllegalStateException("UnSerialized data exception.", e);
			}
			
			if (StringUtils.isBlank(json)) {
				continue;
			} else {
				T t = super.json2Obj(json, cls);
				if (t != null) {
					list.add(t);
				}				
			}
		}

		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void push(final Cmd cmd, final Class<T> cls, final Notify<T> notify) {
		final String path = cmd.buildRoot(super.ROOT).buildPrefixKey();
		if (StringUtils.isBlank(path)) {
			throw new RuntimeException("The PATH cannot be empty, path==" + path);
		}

		// 允许多个监听者监听同一个节点
		Set<Notify> notifies = pushNotifyMap.get(path);
		if (notifies == null) {
			pushNotifyMap.put(path, notifies = new ConcurrentHashSet<Notify>());
		}
		notifies.add(notify);

		if (pushMap.containsKey(path)) {// 已被订阅
			List list = new ArrayList();
			list.addAll(pushMap.get(path).values());
			notify.notify(list);// 通知一次
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
								if (event.getInitialData() != null) {
									isInit = true;
								}

								if (childData == null) {
									logger.debug("The is listenering PATH[{}], initialization notify all data[{}].", path, JSON.toJSONString(tempMap));
								} else {
									String tempPath = event.getData().getPath();
									String tempJsonData = new String(event.getData().getData(), Charset.forName("UTF-8"));
									T t = (T) JSON.parseObject(tempJsonData, cls);

									if (PathChildrenCacheEvent.Type.CHILD_ADDED == event.getType()
											|| PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()) {
										tempMap.put(tempPath, t);
									} else if (PathChildrenCacheEvent.Type.CHILD_REMOVED == event.getType()) {
										tempMap.remove(tempPath);
									}

									if (isInit) {
										logger.debug("The changed PATH[{}] update data[{}].", tempPath, event.getType(), tempJsonData);
										logger.debug("The changed PATH[{}] notify all datas[{}].", path, JSON.toJSONString(tempMap));
										Set<Notify> tempNotifySet = pushNotifyMap.get(path);
										for (Notify tempNotify : tempNotifySet) {// 通知每一个监听器
											List list = new ArrayList();
											list.addAll(tempMap.values());
											tempNotify.notify(list);
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
	public void unpush(Cmd cmd) {
		String path = cmd.buildRoot(super.ROOT).buildPrefixKey();
		if (StringUtils.isBlank(path)) {
			throw new RuntimeException("The PATH cannot be empty, path==" + path);
		}

		PathChildrenCache pathChildrenCache = pathChildrenCacheMap.get(path);
		if (pathChildrenCache != null) {
			try {
				pathChildrenCache.close();
			} catch (IOException e) {
				logger.error("PathChildrenCache close exception.", e);
			}
		}

		if (pushNotifyMap.containsKey(path)) {
			pushNotifyMap.remove(path);
		}

		if (pushMap.containsKey(path)) {
			pushMap.remove(path);
		}
	}

	//$NON-NLS-The Node Governor$
	@Override
	public List<DataConf> getApps() {
		List<DataConf> dataConfs = new ArrayList<DataConf>();
		Map<String, DataConf> appConfMap = new HashMap<String, DataConf>();
		
		try {
			String rootPath = null;
			List<String> rootPathList = client.getChildren().forPath("/");
			for (String rp:rootPathList) {
				if(rp.startsWith(super.ROOT)){
					rootPath = rp;
					break;
				}
			}
			if(StringUtils.isBlank(rootPath)){
				return dataConfs;
			}
			
			List<String> appPathList = client.getChildren().forPath("/" + rootPath);
			for (String appPath:appPathList) {
				DataConf dataConf = new DataConf();
				// build root
				URL tempRootURL = URL.valueOf("/" + URL.decode(rootPath));
				dataConf.setRoot(tempRootURL.getPath());
				dataConf.setRootAttrs(tempRootURL.getParameters());
				// build app
				URL tempAppURL = URL.valueOf("/" + URL.decode(rootPath) + "/" + URL.decode(appPath));
				dataConf.setNode(tempAppURL.getParameter(Cmd.NODE_KEY));
				dataConf.setApp(tempAppURL.getPath());
				dataConf.setAppAttrs(tempAppURL.getParameters());
				// build others
				String tempPath = "/" + rootPath + "/" + appPath;
				dataConf.setSubNum(client.getChildren().forPath(tempPath).size());
				appConfMap.put(tempPath, dataConf);
			}
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Get data exception.", e);
		}
		
		if(!appConfMap.isEmpty()){
			dataConfs.addAll(appConfMap.values());
		}
		return dataConfs;
	}
	
	@Override
	public List<DataConf> getConfs() {
		List<DataConf> dataConfs = new ArrayList<DataConf>();
		Map<String, DataConf> confConfMap = new HashMap<String, DataConf>();
		
		try {
			String rootPath = null;
			List<String> rootPathList = client.getChildren().forPath("/");
			for (String rp:rootPathList) {
				if(rp.startsWith(super.ROOT)){
					rootPath = rp;
					break;
				}
			}
			if(StringUtils.isBlank(rootPath)){
				return dataConfs;
			}
			
			List<String> appPathList = client.getChildren().forPath("/" + rootPath);
			for (String appPath:appPathList) {
				DataConf tempDataConf = new DataConf();
				// build root
				URL tempRootURL = URL.valueOf("/" + URL.decode(rootPath));
				tempDataConf.setRoot(tempRootURL.getPath());
				tempDataConf.setRootAttrs(tempRootURL.getParameters());
				// build app
				URL tempAppURL = URL.valueOf("/" + URL.decode(appPath));
				tempDataConf.setNode(tempAppURL.getParameter(Cmd.NODE_KEY));
				tempDataConf.setApp(tempAppURL.getPath());
				tempDataConf.setAppAttrs(tempAppURL.getParameters());
				// build conf
				List<String> confPathList = client.getChildren().forPath("/" + rootPath + "/" + appPath);
				for (String confPath:confPathList) {
					DataConf dataConf = new DataConf();
					BeanUtils.copyProperties(dataConf, tempDataConf);
					
					URL tempConfURL = URL.valueOf("/" + URL.decode(confPath));
					dataConf.setEnv(tempConfURL.getParameter(Cmd.ENV_KEY));
					dataConf.setGroup(tempConfURL.getParameter(Cmd.GROUP_KEY));
					dataConf.setVersion(tempConfURL.getParameter(Cmd.VERSION_KEY));
					dataConf.setConf(tempConfURL.getPath());
					dataConf.setConfAttrs(tempConfURL.getParameters());
					// build others
					String tempPath = "/" + rootPath + "/" + appPath + "/" + confPath;
					dataConf.setSubNum(client.getChildren().forPath(tempPath).size());
					confConfMap.put(tempPath, dataConf);
				}
			}
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Get data exception.", e);
		}
			
		if(!confConfMap.isEmpty()){
			dataConfs.addAll(confConfMap.values());
		}

		return dataConfs;
	}
	
	@Override
	public List<DataConf> getBodys() {
		List<DataConf> dataConfs = new ArrayList<DataConf>();
		Map<String, DataConf> confConfMap = new HashMap<String, DataConf>();
		
		try {
			String rootPath = null;
			List<String> rootPathList = client.getChildren().forPath("/");
			for (String rp:rootPathList) {
				if(rp.startsWith(super.ROOT)){
					rootPath = rp;
					break;
				}
			}
			if(StringUtils.isBlank(rootPath)){
				return dataConfs;
			}
			
			List<String> appPathList = client.getChildren().forPath("/" + rootPath);
			for (String appPath:appPathList) {
				DataConf appDataConf = new DataConf();
				// build root
				URL tempRootURL = URL.valueOf("/" + URL.decode(rootPath));
				appDataConf.setRoot(tempRootURL.getPath());
				appDataConf.setRootAttrs(tempRootURL.getParameters());
				// build app
				URL tempAppURL = URL.valueOf("/" + URL.decode(appPath));
				appDataConf.setNode(tempAppURL.getParameter(Cmd.NODE_KEY));
				appDataConf.setApp(tempAppURL.getPath());
				appDataConf.setAppAttrs(tempAppURL.getParameters());
				// build conf
				List<String> confPathList = client.getChildren().forPath("/" + rootPath + "/" + appPath);
				for (String confPath:confPathList) {
					DataConf confDataConf = new DataConf();
					BeanUtils.copyProperties(confDataConf, appDataConf);
					
					URL tempConfURL = URL.valueOf("/" + URL.decode(confPath));
					confDataConf.setEnv(tempConfURL.getParameter(Cmd.ENV_KEY));
					confDataConf.setGroup(tempConfURL.getParameter(Cmd.GROUP_KEY));
					confDataConf.setVersion(tempConfURL.getParameter(Cmd.VERSION_KEY));
					confDataConf.setConf(tempConfURL.getPath());
					confDataConf.setConfAttrs(tempConfURL.getParameters());
					
					// build data
					List<String> dataPathList = client.getChildren().forPath("/" + rootPath + "/" + appPath + "/" + confPath);
					for (String dataPath:dataPathList) {
						DataConf dataConf = new DataConf();
						BeanUtils.copyProperties(dataConf, confDataConf);
						URL tempDataURL = URL.valueOf("/" + URL.decode(dataPath));
						dataConf.setData(tempDataURL.getPath());
						dataConf.setDataAttrs(tempDataURL.getParameters());
						
						// build others
						dataConf.setSubNum(0);
						String tempPath = "/" + rootPath + "/" + appPath + "/" + confPath + "/" + dataPath;
						byte[] dataByte = client.getData().forPath(tempPath);
						dataConf.setJson(new String(dataByte, Charset.forName("UTF-8")));
						dataConf.setBody(JSON.parseObject(dataConf.getJson(), Map.class));
						confConfMap.put(tempPath, dataConf);
					}
				}
			}
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Get data exception.", e);
		}
			
		if(!confConfMap.isEmpty()){
			dataConfs.addAll(confConfMap.values());
		}

		return dataConfs;
	}
	
}
