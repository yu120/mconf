package cn.ms.mconf.zookeeper.crud;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
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
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.support.MconfParamType;
import cn.ms.mconf.support.NotifyMessage;
import cn.ms.mconf.zookeeper.ZkCrud;
import cn.ms.micro.common.ConcurrentHashSet;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;

/**
 * Implementation of Zookeeper based on Curator CRUD operation.
 * 
 * @author lry
 */
@SpiMeta(name = "curator")
public class CuratorCrud implements ZkCrud {

	private static final Logger logger = LoggerFactory.getLogger(CuratorCrud.class);

	private CuratorFramework client;

	// PATH list of monitoring node data
	private final Set<String> subscribeNodeKey = new ConcurrentHashSet<String>();
	// Parent list of PATH for monitoring child node data
	private final Set<String> subscribeChildNodeKey = new ConcurrentHashSet<String>();
	// Monitor node data, data structure: Map<Path, Data>
	private final Map<String, Map<String, Object>> subscribeConfDataMap = new ConcurrentHashMap<String, Map<String, Object>>();
	// The listener monitor node data
	private final Map<String, NodeCache> subscribeNodeNodeCacheListener = new ConcurrentHashMap<String, NodeCache>();
	// The listener monitor sub node data
	private final Map<String, PathChildrenCache> subscribePathChildNodeDataListener = new ConcurrentHashMap<String, PathChildrenCache>();
	// Monitor sub node
	private final Map<String, ConcurrentHashSet<NotifyMessage<Map<String, Object>>>> notifyMessages = new ConcurrentHashMap<String, ConcurrentHashSet<NotifyMessage<Map<String, Object>>>>();
	ConnectionState globalState = null;
	
	@Override
	public void connection(URL url) {
		String connAddrs = url.getBackupAddress();
		int timeout = url.getParameter(MconfParamType.TIMEOUT.getName(), MconfParamType.TIMEOUT.getIntValue());
		int session = url.getParameter(MconfParamType.SESSION.getName(), MconfParamType.SESSION.getIntValue());
		
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
	public boolean isAvailable() {
		return client.getZookeeperClient().isConnected();
	}

	@Override
	public void addData(String path, String data, CreateMode createMode) {
		byte[] dataByte = null;
		try {
			dataByte = data.getBytes(Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException("Serialized data exception", e);
		}

		try {
			client.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, dataByte);
		} catch (NodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Add data exception", e);
		}
	}

	@Override
	public void delData(String path) {
		try {
			client.delete().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Delete data exception", e);
		}
	}

	@Override
	public void setData(String path, String data) {
		byte[] dataByte = null;

		try {
			dataByte = data.getBytes(Charset.forName("UTF-8"));
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
	public String getData(String path) {
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
			return new String(dataByte, Charset.forName("UTF-8"));
		} catch (Exception e) {
			throw new IllegalStateException("UnSerialized data exception", e);
		}
	}
	
	@Override
	public Stat getStat(String path) {
		try {
			return client.checkExists().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Modify data exception", e);
		}
		
		return null;
	}

	@Override
	public List<String> getChildNodes(String path) {
		try {
			return client.getChildren().forPath(path);
		} catch (NoNodeException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Gets all child node exceptions", e);
		}
		
		return null;
	}
	
	@Override
	public void subscribeData(final String path, final NotifyMessage<Map<String, Object>> dataListener) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		if (subscribeNodeKey.contains(path)) {//Already subscribed
			logger.debug("[{}] node DATA has been listening, do not need to repeat the monitor", path);
		} else {
			ConcurrentHashSet<NotifyMessage<Map<String, Object>>> pathListeners = new ConcurrentHashSet<NotifyMessage<Map<String, Object>>>();
			pathListeners.add(dataListener);
			notifyMessages.put(path, pathListeners);
			subscribeNodeKey.add(path);
			try {
				final NodeCache nodeCache = new NodeCache(client, path, false);
				nodeCache.start(true);
				NodeCacheListener nodeCacheListener = new NodeCacheListener() {
					@Override
					public void nodeChanged() throws Exception {
						ConcurrentHashMap<String, Object> subscribeNodeDataMap = new ConcurrentHashMap<String, Object>();
						String object = new String(nodeCache.getCurrentData().getData(), Charset.forName("UTF-8"));
						subscribeNodeDataMap.put(nodeCache.getCurrentData().getPath(), object);
						dataListener.notify(subscribeNodeDataMap);// Notification specified node data change
					}
				};
				nodeCache.getListenable().addListener(nodeCacheListener);
				subscribeNodeNodeCacheListener.put(path, nodeCache);
			} catch (NoNodeException e) {
			} catch (Exception e) {
				throw new IllegalStateException("Subscription node data exception", e);
			}
		}
	}
	
	@Override
	public void unsubscribeData(String path) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		NodeCache nodeCache = subscribeNodeNodeCacheListener.get(path);
		if(nodeCache!=null){
			try {
				nodeCache.close();
			} catch (IOException e) {
				logger.error("NodeCache close exception", e);
			}
		}
		if (subscribeNodeKey.contains(path)) {
			subscribeNodeKey.remove(path);
		}
	}
	
	@Override
	public void subscribeChildNodeData(final String path, final NotifyMessage<Map<String, Object>> notifyMessage) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		if (subscribeChildNodeKey.contains(path) && notifyMessages.containsKey(path)) {//Already subscribed
			notifyMessages.get(path).add(notifyMessage);
		} else {
			ConcurrentHashSet<NotifyMessage<Map<String, Object>>> pathListeners = new ConcurrentHashSet<NotifyMessage<Map<String, Object>>>();
			pathListeners.add(notifyMessage);
			notifyMessages.put(path, pathListeners);
			subscribeChildNodeKey.add(path);
			subscribeConfDataMap.put(path, new ConcurrentHashMap<String, Object>());
			try {
				PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);  
				PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {  
					@Override  
			        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
						ChildData data = event.getData();
						if(data == null){
							return;
						}
						if(data.getData() == null) {
							logger.warn("path([{}] node data is empty.", data.getPath());
						}
						
			            String objectData = new String(data.getData(), Charset.forName("UTF-8"));
			            Map<String, Object> map = subscribeConfDataMap.get(path);
			            if(map==null){
			            	throw new RuntimeException("Data inconsistencies, please check：subscribeConfDataMap=="+subscribeConfDataMap.toString());
			            }
			            
			            switch (event.getType()) {  
			            case CHILD_ADDED:
			            	map.put(data.getPath(), objectData);
			                logger.debug("CHILD_ADDED:{}-->{}", data.getPath(), objectData);
			                break;
			            case CHILD_REMOVED:  
			            	map.remove(data.getPath());
			            	logger.debug("CHILD_REMOVED:{}-->{}", data.getPath(), objectData);
			            	break;  
			            case CHILD_UPDATED:
			            	map.put(data.getPath(), objectData);
			                logger.debug("CHILD_UPDATED:{}-->{}", data.getPath(), objectData);
			                break;  
			            default:
			                logger.debug("Unknown action {}-->{}", event.getType()+":"+data.getPath(), objectData);
			                return;  
			            }
			            //Add, delete, modify will notice
						ConcurrentHashSet<NotifyMessage<Map<String, Object>>> pathListener = notifyMessages.get(path);
						for (NotifyMessage<Map<String, Object>> zkDataListener : pathListener) {
							zkDataListener.notify(map);
						}

					}
				};
			        
			    childrenCache.getListenable().addListener(childrenCacheListener);  
			    childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
			    subscribePathChildNodeDataListener.put(path, childrenCache);
			} catch (NoNodeException e) {
			} catch (Exception e) {
				throw new IllegalStateException("Subscription child node data exception", e);
			}
		}
	}
	
	@Override
	public void unsubscribeChildNodeData(String path) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		PathChildrenCache pathChildrenCache = subscribePathChildNodeDataListener.get(path);
		if(pathChildrenCache!=null){
			try {
				pathChildrenCache.close();
			} catch (IOException e) {
				logger.error("PathChildrenCache close exception", e);
			}
		}
		if (subscribeChildNodeKey.contains(path)) {
			subscribeChildNodeKey.remove(path);
		}
	}
	
}
