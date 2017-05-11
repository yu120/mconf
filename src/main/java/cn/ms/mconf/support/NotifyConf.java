package cn.ms.mconf.support;

import java.util.List;

/**
 * The Notify Message
 * 
 * @author lry
 *
 * @param <C>
 */
public interface NotifyConf<T> {

	/**
	 * Do Notify
	 * 
	 * @param conf
	 */
	void notify(List<T> confs);

}
