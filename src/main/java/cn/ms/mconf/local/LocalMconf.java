package cn.ms.mconf.local;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Conf;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MconfParamType;
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
		String confName = url.getParameter(MconfParamType.confName.getName(), MconfParamType.confName.getValue());
		
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
		throw new IllegalStateException("The No Support addConf.");
	}

	@Override
	public <T> void delConf(T data) {
		throw new IllegalStateException("The No Support delConf.");
	}

	@Override
	public <T> void setConf(T data) {
		throw new IllegalStateException("The No Support setConf.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(T data) {
		MetaData metaData = this.obj2Mconf(data);
		Object obj = conf.getConf(metaData.getAppId(), metaData.getConfId(), metaData.getDataId(), data.getClass());
		if(obj == null){
			return null;
		}
		
		return (T)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> pulls(T data) {
		MetaData metaData = this.obj2Mconf(data);
		return (List<T>) conf.getConfs(metaData.getAppId(), metaData.getConfId(), data.getClass());
	}

	@Override
	public <T> void push(T data, NotifyConf<T> notifyMessage) {
		logger.warn("The local mconf no support push.");
		List<T> list = this.pulls(data);
		notifyMessage.notify(list);
	}

	@Override
	public <T> void unpush(T data) {
		logger.warn("The local mconf no support unpush.");
	}

	@Override
	public <T> void unpush(T data, NotifyConf<T> notifyConf) {
		logger.warn("The local mconf no support unpush.");
	}
	
}
