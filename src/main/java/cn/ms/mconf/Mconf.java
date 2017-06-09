package cn.ms.mconf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ms.mconf.support.NotifyConf;
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
	 * @param url Format：[app]://0.0.0.0:0/[conf]/[data]?node=[node]&env=[env]&group=[group]&version=[verison]
	 * @param data
	 */
	<T> void addConf(URL url, T data);

	void delConf(URL url);

	<T> void upConf(URL url, T data);

	<T> T pull(URL url, Class<T> cls);

	/**
	 * Format：[app]://0.0.0.0:0/[conf]/[data]?node=[node]&env=[env]&group=[group]&version=[verison]
	 * 
	 * @param url
	 * @param cls
	 * @return
	 */
	<T> List<T> pulls(URL url, Class<T> cls);

	<T> void push(URL url, Class<T> cls, NotifyConf<T> notifyConf);

	void unpush(URL url);

	<T> void unpush(URL url, NotifyConf<T> notifyConf);
	
	
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
