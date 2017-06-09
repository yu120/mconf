package cn.ms.mconf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ms.mconf.support.Notify;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.Scope;
import cn.ms.micro.extension.Spi;

/**
 * The Micro Service Configuration Center.<br>
 * <br>
 * Configuration center data structure:<br>
 * ①--> /mconf<br>
 * ②--> /[app]?node=[node]<br>
 * ③--> /[conf]?env=[env]<br>
 * ④--> /[data]?group=[group]&version=[version]<br>
 * ⑤--> {JSON Data String}<br>
 * <br>
 * 
 * Connect URL:<br>
 * zookeeper://127.0.0.1:2181/mconf?app=node&conf=env&data=group,version<br>
 * Data URL:<br>
 * [app]://0.0.0.0:0/[conf]/[data]?node=[node]&env=[env]&group=[group]&version=[verison]<br>
 * <br>
 * <br>
 * The data structure：<br>
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
	 * @param url Format：[app]://0.0.0.0:0/[conf]?[data]=[data]&[node]=[node]&[env]=[env]&[group]=[group]&[version]=[verison]……
	 * @param data
	 */
	<T> void addConf(URL url, T data);

	/**
	 * The Delete Configuration Data.<br>
	 * <br>
	 * Prompt：<br>
	 * 1.Set parameter 'data'：Delete a data.<br>
	 * 2.Not set parameter 'data'：Delete a conf.<br>
	 * <br>
	 * @param url Format：[app]://0.0.0.0:0/[conf]?[data]=[data]&[node]=[node]&[env]=[env]&[group]=[group]&[version]=[verison]……
	 */
	void delConf(URL url);

	/**
	 * The Update Configuration Data.
	 * 
	 * @param url Format：[app]://0.0.0.0:0/[conf]?[data]=[data]&[node]=[node]&[env]=[env]&[group]=[group]&[version]=[verison]……
	 * @param data
	 */
	<T> void upConf(URL url, T data);

	/**
	 * The Pull Configuration Data.
	 * 
	 * @param url Format：[app]://0.0.0.0:0/[conf]?[data]=[data]&[node]=[node]&[env]=[env]&[group]=[group]&[version]=[verison]……
	 * @param cls
	 * @return
	 */
	<T> T pull(URL url, Class<T> cls);

	/**
	 * The Pulls Configuration Data.
	 * 
	 * @param url Format：[app]://0.0.0.0:0/[conf]?[node]=[node]&[env]=[env]&[group]=[group]&[version]=[verison]……
	 * @param cls
	 * @return
	 */
	<T> List<T> pulls(URL url, Class<T> cls);

	/**
	 * The Push Configuration Data.
	 * 
	 * @param url Format：[app]://0.0.0.0:0/[conf]?[node]=[node]&[env]=[env]&[group]=[group]&[version]=[verison]……
	 * @param cls
	 * @param notify
	 * @return
	 */
	<T> void push(URL url, Class<T> cls, Notify<T> notify);

	void unpush(URL url);

	<T> void unpush(URL url, Notify<T> notify);
	
	
	//$NON-NLS-The Node Governor$
	
	/**
	 * Query configuration center data structure.<br>
	 * <br>
	 * The Data Structures: Map< node, Map< app, Map< env, Map< conf, Map< group, Set< version>>>>>>
	 * 
	 * @return
	 */
	Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> structures();
	
}
