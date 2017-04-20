package cn.ms.mconf;

import java.util.List;

import cn.ms.mconf.support.NotifyMessage;
import cn.ms.micro.common.URL;

/**
 * Configuration center services (CURL operation interface group)<br>
 * <br>
 * CURL: [appId]:///[confId]?dataId=[Conf Data Id Value]<br>
 * 
 * @author lry
 */
public interface MconfService {

	/**
	 * Connection configuration center
	 */
	void connection(URL url);

	/**
	 * View configuration center status
	 * 
	 * @return
	 */
	boolean isAvailable();

	/**
	 * Add data to <br>
	 * <br>
	 * support automatic cycle to create multi-level node <br>
	 *
	 * @param url needs to be set: appId/confId/dataId
	 * @param data
	 */
	<T> void addConf(URL url, T data);

	/**
	 * Delete data
	 * 
	 * @param url needs to be set：appId/confId/dataId
	 */
	<T> void delConf(URL url);

	/**
	 * Set data
	 * 
	 * @param url needs to be set：appId/confId/dataId
	 * @param data
	 */
	<T> void setConf(URL url, T data);

	/**
	 * Get data
	 * 
	 * @param url needs to be set：appId/confId/dataId
	 * @return
	 */
	<T> T getConf(URL url);

	/**
	 * Get list
	 * 
	 * @param url needs to be set：appId/confId
	 * @return
	 */
	<T> List<T> getConfs(URL url);

	/**
	 * subscribe to child node data <br>
	 * <br>
	 * Note: you can only notify the PATH and DATA of the direct child nodes,
	 * and indirect sub nodes use the cyclic PATH to subscribe to <br>
	 *
	 * @param url subscription configuration needs to be set: appId/confId, subscription data needs to be set: appId/confId/dataId
	 * @param notifyMessage
	 */
	<T> void subscribe(URL url, NotifyMessage<List<T>> notifyMessage);

	/**
	 * Unsubscribe child node data
	 * 
	 * @param url Unsubscribe configuration needs to be set: appId/confId, unsubscribe data need to be set: appId/confId/dataId
	 */
	<T> void unsubscribe(URL url);

}
