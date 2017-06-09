package cn.ms.mconf.local;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Conf;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.Notify;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;

/**
 * The base of local Mconf.
 * 
 * @author lry
 */
@SpiMeta(name = "local")
public class LocalMconf extends AbstractMconf {

	private static final Logger logger = LoggerFactory.getLogger(LocalMconf.class);
	
	private boolean available = false;
	private Conf conf = Conf.INSTANCE;
	
	@Override
	public void connect(URL url) {
		super.connect(url);
		String confName = url.getParameter("confName", "mconf.properties");
		
		try {
			available = conf.connection(confName);
		} catch (Exception e) {
			logger.error("读取配置文件异常[" + confName + "]", e);
		}
	}
	
	@Override
	public boolean available() {
		return this.available;
	}
	
	@Override
	public <T> void addConf(URL url, T data) {
		throw new IllegalStateException("The No Support addConf.");
	}
	
	@Override
	public void delConf(URL url) {
		throw new IllegalStateException("The No Support delConf.");
	}

	@Override
	public <T> void upConf(URL url, T data) {
		throw new IllegalStateException("The No Support setConf.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(URL url, Class<T> cls) {
		MetaData metaData = this.obj2MetaData(data);
		Object obj = conf.getConf(metaData.getApp(), metaData.getConf(), metaData.getData(), data.getClass());
		if(obj == null){
			return null;
		}
		
		return (T)obj;
	}

	@Override
	public <T> List<T> pulls(URL url, Class<T> cls) {
		MetaData metaData = this.obj2MetaData(data);
		return (List<T>) conf.getConfs(metaData.getApp(), metaData.getConf(), cls);
	}

	@Override
	public <T> void push(URL url, Class<T> cls, Notify<T> notifyMessage) {
		logger.warn("The local mconf no support push.");
		List<T> list = this.pulls(url, cls);
		notifyMessage.notify(list);
	}

	@Override
	public void unpush(URL url) {
		logger.warn("The local mconf no support unpush.");
	}

	@Override
	public <T> void unpush(URL url, Notify<T> notifyMessage) {
		logger.warn("The local mconf no support unpush.");
	}
	
	//$NON-NLS-The Node Governor$
	@Override
	public Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> structures() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
