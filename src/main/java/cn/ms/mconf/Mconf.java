package cn.ms.mconf;

import java.util.List;

import cn.ms.mconf.support.MconfQuery;
import cn.ms.mconf.support.NotifyMessage;
import cn.ms.micro.extension.Scope;
import cn.ms.micro.extension.Spi;

/**
 * The Micro Service Configuration Center.
 * 
 * @author lry
 */
@Spi(scope = Scope.SINGLETON)
public interface Mconf extends MconfService {

	/**
	 * Add data<br>
	 * <br>
	 * Support automatic cycle to create multi-level node<br>
	 * 
	 * @param data Must enter：appId/confId/dataId/data
	 */
	<T> void addConf(T data);

	/**
	 * Delete data
	 * 
	 * @param data Must enter：appId/confId/dataId
	 */
	<T> void delConf(T data);

	/**
	 * Set  data
	 * 
	 * @param data Must enter：appId/confId/dataId/data
	 */
	<T> void setConf(T data);

	/**
	 * Get data
	 * 
	 * @param data Must enter：appId/confId/dataId
	 * @return
	 */
	<T> T getConf(T data);

	/**
	 * Get list
	 * 
	 * @param data Must enter：appId/confId
	 * @return
	 */
	<T> List<T> getConfs(T data);

	/**
	 * subscribe to child node data <br>
	 * <br>
	 * Note: you can only notify the PATH and DATA of the direct child nodes, and indirect sub nodes use the cyclic PATH to subscribe to <br>
	 *
	 * @param data subscription configuration needs to be set: appId/confId, subscription data needs to be set: appId/confId/dataId
	 * @param notifyMessage
	 */
	<T> void subscribe(T data, NotifyMessage<List<T>> notifyMessage);

	/**
	 * Unsubscribe child node data
	 * 
	 * @param data Unsubscribe configuration needs to be set：appId/confId, Unsubscribe data needs to be set：appId/confId/dataId
	 */
	<T> void unsubscribe(T data);

	/**
	 * Query configuration center data
	 * 
	 * @return
	 */
	MconfQuery query();
	
}
