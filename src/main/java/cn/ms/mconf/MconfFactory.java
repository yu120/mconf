package cn.ms.mconf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import cn.ms.mconf.support.MconfParamType;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.ExtensionLoader;

/**
 * Configuration center for factory<br>
 * <br>
 * 使用场景:<br>
 * 1.读取普通Key-Value<br>
 * 2.读取Zookeeper中的Bean配置<br>
 * 3.读取本地资源文件中的Bean配置<br>
 * 4.先读取本地支援文件中的Bena配置,再读取Zookeeper中的配置进行内存覆盖<br>
 * @author lry
 */
public enum MconfFactory {

	MCONF;
	
	public static final String confName= "confName";
	
	// Configuration center acquisition process lock
	private static final ReentrantLock LOCK = new ReentrantLock();
	private ConcurrentHashMap<String, Mconf> MCONFS = new ConcurrentHashMap<String, Mconf>();
	private ConcurrentHashMap<String, Conf> CONFS = new ConcurrentHashMap<String, Conf>();

	public void connection(URL url) {
		String confName = url.getParameter(MconfParamType.confName.getName(), MconfParamType.confName.getValue());
		this.getConf(confName);
		this.getMconf(url);
	}
	
	public Conf getConf(String confName) {
		LOCK.lock();
		
		try {
			Conf conf = CONFS.get(confName);
			if (conf != null) {
				return conf;
			}
			
			conf = new Conf();
			conf.connection(confName);
			
			CONFS.put(confName, conf);
			return conf;
		} finally {
			LOCK.unlock();// Release lock
		}
	}
	
	public Mconf getMconf() {
		if(MCONFS != null){
			for (Map.Entry<String, Mconf> entry:MCONFS.entrySet()) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public Mconf getMconf(URL url) {
		String key = url.getHost();
		// Lock configuration center to obtain the process, to ensure a single instance of the configuration center
		LOCK.lock();
		
		try {
			Mconf mconf = MCONFS.get(key);
			if (mconf != null) {
				return mconf;
			}
			
			mconf = ExtensionLoader.getExtensionLoader(Mconf.class).getExtension(url.getProtocol());
			mconf.connection(url);
			if (!mconf.isAvailable()) {
				throw new IllegalStateException("No mconf center available: " + url);
			}
			
			MCONFS.put(key, mconf);
			return mconf;
		} finally {
			LOCK.unlock();// Release lock
		}
	}

}
