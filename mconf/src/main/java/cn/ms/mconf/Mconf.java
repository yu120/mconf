package cn.ms.mconf;

import java.util.List;

import cn.ms.mconf.support.Cmd;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.Notify;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.Scope;
import cn.ms.micro.extension.Spi;

/**
 * The Micro Service Configuration Center.<br>
 * <br>
 * Configuration center data structure:<br>
 * ①--> /mconf?……<br>
 * ②--> /[app]?node=[node]&……<br>
 * ③--> /[conf]?env=[env]&group=[group]&version=[version]&……<br>
 * ④--> /[data]?……<br>
 * ⑤--> {JSON Data String}<br>
 * <br>
 * <br>
 * Connect URL:<br>
 * [zookeeper/redis]://127.0.0.1:2181/mconf?node=[node]&app=[app]&env=[env]&conf
 * =[conf]&category=[category]&version=[version]&data=[data]&……<br>
 * <br>
 * <br>
 * The data structure：<br>
 * prefixKey(①+②+③)--> /mconf?……/[app]?node=[node]&……/[conf]?env=[env]&……<br>
 * suffixKey(④)--> /[data]?group=[group]&version=[version]&……<br>
 * Data String(⑤)--> JSON String<br>
 * <br>
 * Zookeeper< Path, Data> ——> <①+②+③+④, ⑤> ——> Push<br>
 * Redis< Key, Value> ——> <①+②+③, Map<④, ⑤>> ——> Pull<br>
 * 
 * @author lry
 */
@Spi(scope = Scope.SINGLETON)
public interface Mconf {

	/**
	 * Connect configuration center
	 */
	void connect(URL url);

	/**
	 * Configuration center status
	 * 
	 * @return
	 */
	boolean available();

	/**
	 * The Add Configuration Data.
	 * 
	 * @param cmd
	 * @param obj
	 */
	void addConf(Cmd cmd, Object obj);

	/**
	 * The Delete Configuration Data.<br>
	 * <br>
	 * Prompt：<br>
	 * 1.Set parameter 'data'：Delete a data.<br>
	 * 2.Not set parameter 'data'：Delete a conf.<br>
	 * <br>
	 * 
	 * @param cmd
	 */
	void delConf(Cmd cmd);

	/**
	 * The Update Configuration Data.
	 * 
	 * @param cmd
	 * @param obj
	 */
	void upConf(Cmd cmd, Object obj);

	/**
	 * The Pull Configuration Data.
	 * 
	 * @param cmd
	 * @param cls
	 * @return
	 */
	<T> T pull(Cmd cmd, Class<T> cls);

	/**
	 * The Pulls Configuration Data.
	 * 
	 * @param cmd
	 * @param cls
	 * @return
	 */
	<T> List<T> pulls(Cmd cmd, Class<T> cls);

	/**
	 * The Push Configuration Data.
	 * 
	 * @param cmd
	 * @param cls
	 * @param notify
	 * @return
	 */
	<T> void push(Cmd cmd, Class<T> cls, Notify<T> notify);

	/**
	 * The UnPush Configuration Data.
	 * 
	 * @param cmd
	 */
	void unpush(Cmd cmd);

	//$NON-NLS-The Node Governor$
	/**
	 * Get the MetaData of App.
	 * 
	 * @return
	 */
	List<MetaData> getApps();

	/**
	 * Get the MetaData of Conf.
	 * 
	 * @return
	 */
	List<MetaData> getConfs();

	/**
	 * Get the MetaData of Body.
	 * 
	 * @return
	 */
	List<MetaData> getBodys();

}
