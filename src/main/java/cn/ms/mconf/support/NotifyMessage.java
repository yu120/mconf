package cn.ms.mconf.support;

/**
 * The Notify Message
 * 
 * @author lry
 */
public interface NotifyMessage<T> {

	/**
	 * Do Notify
	 * 
	 * @param message
	 */
	void notify(T message);

}
