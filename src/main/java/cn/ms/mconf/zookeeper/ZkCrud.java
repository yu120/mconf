package cn.ms.mconf.zookeeper;

import java.util.List;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import cn.ms.mconf.support.NotifyMessage;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.Scope;
import cn.ms.micro.extension.Spi;

/**
 * ZookeeperCRUD
 * 
 * @author lry
 */
@Spi(scope = Scope.SINGLETON)
public interface ZkCrud {

	/**
	 * The connection zookeeper server
	 * 
	 * @param url
	 */
	void connection(URL url);

	/**
	 * View configuration center status
	 * 
	 * @return
	 */
	boolean isAvailable();

	/**
	 * Add data<br>
	 * <br>
	 * Support automatic cycle to create multi-level node<br>
	 * <br>
	 * 
	 * @param path
	 * @param data
	 * @param createMode
	 *            PERSISTENT:Persistent directory node;
	 *            PERSISTENT_SEQUENTIAL:Persistent automatic numbering node;
	 *            EPHEMERAL:Temporary node;
	 *            EPHEMERAL_SEQUENTIAL:Temporary automatic numbering node
	 */
	void addData(String path, String data, CreateMode createMode);

	/**
	 * Delete data
	 * 
	 * @param path
	 */
	void delData(String path);

	/**
	 * Set data
	 * 
	 * @param path
	 * @param data
	 */
	void setData(String path, String data);

	/**
	 * Get data
	 * 
	 * @param path
	 * @return
	 */
	String getData(String path);

	/**
	 * Get node status
	 * 
	 * @param path
	 * @return
	 */
	Stat getStat(String path);

	/**
	 * Get all direct nodes
	 * 
	 * @param path
	 * @return
	 */
	List<String> getChildNodes(String path);

	/**
	 * Subscription node data
	 * 
	 * @param path
	 * @param notifyMessage
	 */
	void subscribeData(String path, NotifyMessage<Map<String, Object>> notifyMessage);

	/**
	 * Unsubscribe data
	 * 
	 * @param path
	 */
	void unsubscribeData(String path);

	/**
	 * Subscription node data<br>
	 * <br>
	 * Note: you can only notify the PATH and DATA of the direct child nodes, and the indirect child nodes can use the cyclic PATH to subscribe<br>
	 * <br>
	 * 
	 * @param path
	 * @param notifyMessage
	 */
	void subscribeChildNodeData(String path, NotifyMessage<Map<String, Object>> notifyMessage);

	/**
	 * Unsubscribe child node data
	 * 
	 * @param path
	 */
	void unsubscribeChildNodeData(String path);

}
