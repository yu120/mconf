package cn.ms.mconf;

import java.util.List;

import cn.ms.mconf.support.NotifyConf;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.Scope;
import cn.ms.micro.extension.Spi;

/**
 * The Micro Service Configuration Center.<br>
 * <br>
 * /[mconf]/[envId]/[appId]/[confId]/[dataId]{JSON}
 * <br>
 * @author lry
 */
@Spi(scope = Scope.SINGLETON)
public interface Mconf {

	/**
	 * Connect configuration center
	 */
	void connect(URL url);

	/**
	 * View configuration center status
	 * 
	 * @return
	 */
	boolean available();
	
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
	<T> T pull(T data);

	/**
	 * Get list
	 * 
	 * @param data Must enter：appId/confId
	 * @return
	 */
	<T> List<T> pulls(T data);

	/**
	 * Push to child node data <br>
	 * <br>
	 * Note: you can only notify the PATH and DATA of the direct child nodes, and indirect sub nodes use the cyclic PATH to push to <br>
	 * @param <K>
	 *
	 * @param data push configuration needs to be set: appId/confId, push data needs to be set: appId/confId/dataId
	 * @param notifyConf
	 */
	<T> void push(T data, NotifyConf<T> notifyConf);

	/**
	 * Unpush child node data
	 * 
	 * @param data unpush configuration needs to be set：appId/confId, unpush data needs to be set：appId/confId/dataId
	 */
	<T> void unpush(T data);
	
	<T> void unpush(T data, NotifyConf<T> notifyConf);

}
