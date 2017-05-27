package cn.ms.mconf.local;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Conf;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.Category;
import cn.ms.mconf.support.MParamType;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.NotifyConf;
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
		String confName = url.getParameter(MParamType.confName.getName(), MParamType.confName.getValue());
		
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
	public <T> void addConf(T data) {
		this.addConf(category, data);
	}
	
	@Override
	public <T> void addConf(Category category, T data) {
		throw new IllegalStateException("The No Support addConf.");
	}
	
	@Override
	public <T> void delConf(T data) {
		this.delConf(category, data);
	}

	@Override
	public <T> void delConf(Category category, T data) {
		throw new IllegalStateException("The No Support delConf.");
	}

	@Override
	public <T> void setConf(T data) {
		this.setConf(category, data);
	}
	
	@Override
	public <T> void setConf(Category category, T data) {
		throw new IllegalStateException("The No Support setConf.");
	}

	@Override
	public <T> T pull(T data) {
		return this.pull(category, data);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(Category category, T data) {
		MetaData metaData = this.obj2MetaData(data, category);
		Object obj = conf.getConf(metaData.getApp(), metaData.getConf(), metaData.getData(), data.getClass());
		if(obj == null){
			return null;
		}
		
		return (T)obj;
	}

	@Override
	public <T> List<T> pulls(T data) {
		return this.pulls(category, data);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> pulls(Category category, T data) {
		MetaData metaData = this.obj2MetaData(data, category);
		return (List<T>) conf.getConfs(metaData.getApp(), metaData.getConf(), data.getClass());
	}

	@Override
	public <T> void push(T data, NotifyConf<T> notifyConf) {
		this.push(category, data, notifyConf);
	}
	
	@Override
	public <T> void push(Category category, T data, NotifyConf<T> notifyMessage) {
		logger.warn("The local mconf no support push.");
		List<T> list = this.pulls(data);
		notifyMessage.notify(list);
	}

	@Override
	public <T> void unpush(T data) {
		this.unpush(category, data);
	}
	
	@Override
	public <T> void unpush(Category category, T data) {
		logger.warn("The local mconf no support unpush.");
	}

	@Override
	public <T> void unpush(T data, NotifyConf<T> notifyConf) {
		this.unpush(category, data, notifyConf);
	}
	
	@Override
	public <T> void unpush(Category category, T data, NotifyConf<T> notifyConf) {
		logger.warn("The local mconf no support unpush.");
	}
	
}
