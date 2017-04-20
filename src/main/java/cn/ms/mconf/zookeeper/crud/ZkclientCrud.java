package cn.ms.mconf.zookeeper.crud;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;
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
 * Implementation of Zookeeper based on zkclient CRUD operation.
 * 
 * @author lry
 */
@SpiMeta(name = "zkclient")
public class ZkclientCrud implements ZkCrud {

	private static final Logger logger = LoggerFactory.getLogger(ZkclientCrud.class);

	private ZkClient client;
	private volatile KeeperState state = KeeperState.SyncConnected;
	
	// PATH list of monitoring node data
	private final Set<String> subscribeNodeKey = new ConcurrentHashSet<String>();
	// Parent list of PATH for monitoring child node data
	private final Set<String> subscribeChildNodeKey = new ConcurrentHashSet<String>();
	// Monitor node data, data structure: Map<Path, Data>
	private final Map<String, Map<String, Object>> subscribeConfDataMap = new ConcurrentHashMap<String, Map<String, Object>>();
	// The listener monitor node data
	private final Map<String, IZkDataListener> subscribeNodeDataListener = new ConcurrentHashMap<String, IZkDataListener>();
	// The listener monitor node data
	private final Map<String, IZkChildListener> subscribeChildNodeDataListener = new ConcurrentHashMap<String, IZkChildListener>();

	@Override
	public void connection(URL url) {
		String connAddrs = url.getBackupAddress();
		int timeout = url.getParameter(MconfParamType.TIMEOUT.getName(), MconfParamType.TIMEOUT.getIntValue());
		int session = url.getParameter(MconfParamType.SESSION.getName(), MconfParamType.SESSION.getIntValue());
		
		client = new ZkClient(connAddrs, timeout, session);
		client.subscribeStateChanges(new IZkStateListener() {
			@Override
			public void handleStateChanged(KeeperState keeperState) throws Exception {
				state = keeperState;
				if (state == KeeperState.Disconnected) {
					logger.info("The registration center connection status is changed to [{}]", "DISCONNECTED");
				} else if (state == KeeperState.SyncConnected) {
					logger.info("The registration center connection status is changed to [{}]", "CONNECTED");
				}
			}

			@Override
			public void handleNewSession() throws Exception {
				logger.info("The registration center connection status is changed to [{}]", "RECONNECTED");
			}

			@Override
			public void handleSessionEstablishmentError(Throwable t) throws Exception {
				logger.error("Registration Center exception", t);
			}
		});
	}

	@Override
	public boolean isAvailable() {
		return state == KeeperState.SyncConnected;
	}

	@Override
	public void addData(String path, String data, CreateMode createMode) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			addData(path.substring(0, i), null, createMode);
		}

		try {
			if (data == null) {
				client.createPersistent(path);
			} else {
				client.create(path, data, createMode);
			}
		} catch (ZkNodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Add data exception", e);
		}
	}

	@Override
	public void delData(String path) {
		try {
			client.delete(path);
		} catch (ZkNodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Delete data exception", e);
		}
	}

	@Override
	public void setData(String path, String data) {
		try {
			client.writeData(path, data);
		} catch (ZkNodeExistsException e) {
		} catch (Exception e) {
			throw new IllegalStateException("Modify data exception", e);
		}
	}

	@Override
	public String getData(String path) {
		try {
			Object object = client.readData(path);
			if(object!=null){
				return (String) object;
			}
		} catch (ZkNoNodeException e) {
		} catch (ZkNodeExistsException e) {
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Read data exception", e);
		}

		return null;
	}

	@Override
	public Stat getStat(String path) {
		//TODO
		return null;
	}
	
	@Override
	public List<String> getChildNodes(String path) {
		return client.getChildren(path);
	}
	
	//TODO Full amount notice?
	@Override
	public void subscribeData(String path, final NotifyMessage<Map<String, Object>> notifyMessage) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		if (subscribeNodeKey.contains(path)) {//Already subscribed
			logger.debug("[{}] node DATA has been listening, do not need to repeat the monitor", path);
		} else {
			subscribeNodeKey.add(path);
			subscribeConfDataMap.put(path, new ConcurrentHashMap<String, Object>());
			final String parentPath = path.substring(0, path.lastIndexOf("/"));
			IZkDataListener iZkDataListener = new IZkDataListener() {
				@Override
				public void handleDataDeleted(String dataPath) throws Exception {
					Map<String, Object> map = subscribeConfDataMap.get(parentPath);
					if(map!=null){
						map.remove(dataPath);
						notifyMessage.notify(map);
					}
				}
				@Override
				public void handleDataChange(String dataPath, Object data) throws Exception {
					Map<String, Object> map = subscribeConfDataMap.get(parentPath);
					if(map!=null){
						map.put(dataPath, data);
						notifyMessage.notify(map);
					}
				}
			};
			client.subscribeDataChanges(path, iZkDataListener);
			subscribeNodeDataListener.put(path, iZkDataListener);
		}
	}
	
	@Override
	public void unsubscribeData(String path) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		IZkDataListener iZkDataListener = subscribeNodeDataListener.get(path);
		if (iZkDataListener!=null) {
			client.unsubscribeDataChanges(path, iZkDataListener);
		}
		
		if (subscribeNodeKey.contains(path)) {
			subscribeNodeKey.remove(path);
		}
	}
	
	@Override
	public void subscribeChildNodeData(String path, final NotifyMessage<Map<String, Object>> notifyMessage) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		if (subscribeChildNodeKey.contains(path)) {
			logger.debug("The child node has [{}] monitor, does not need to be repeated listening", path);
		} else {
			subscribeChildNodeKey.add(path);
			IZkChildListener iZkChildListener = new IZkChildListener() {
				@Override
				public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
					for (String tempChild:currentChilds) {// Cyclic subscription node data
						subscribeData(parentPath+"/"+tempChild, notifyMessage);
					}
				}
			};
			client.subscribeChildChanges(path, iZkChildListener);
			subscribeChildNodeDataListener.put(path, iZkChildListener);
		}
	}
	
	@Override
	public void unsubscribeChildNodeData(String path) {
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("PATH cannot be empty, path=="+path);
		}
		
		IZkChildListener iZkChildListener = subscribeChildNodeDataListener.get(path);
		if(iZkChildListener!=null){
			client.unsubscribeChildChanges(path, iZkChildListener);
		}
		
		if (subscribeChildNodeKey.contains(path)) {
			subscribeChildNodeKey.remove(path);
		}
	}
	
}
