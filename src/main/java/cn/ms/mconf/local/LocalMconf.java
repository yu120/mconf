package cn.ms.mconf.local;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MconfParamType;
import cn.ms.mconf.support.MconfQuery;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.NotifyMessage;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;

/**
 * The Micro service configuration center based on Zookeeper.<br>
 * <br>
 * The data structure：/[rootPath]/[appId]/[confId]/[dataId]{data}<br>
 * <br>
 * @author lry
 */
@SpiMeta(name = "local")
public class LocalMconf extends AbstractMconf {

	private static final Logger logger = LoggerFactory.getLogger(LocalMconf.class);
	
	private boolean available = false;
	private Conf conf;
	
	@Override
	public void connection(URL url) {
		super.connection(url);
		
		String confName = this.url.getParameter(MconfParamType.confName.getName(), MconfParamType.ZKTYPE.getValue());
		
		try {
			this.available = conf.connection(confName);
		} catch (Exception e) {
			logger.error("读取配置文件异常[" + confName + "]", e);
		}
	}
	
	@Override
	public boolean isAvailable() {
		return this.available;
	}
	
	
	@Override
	public <T> void addConf(T data) {
		throw new IllegalStateException("The No Support.");
	}

	@Override
	public <T> void delConf(T data) {
		throw new IllegalStateException("The No Support.");
	}

	@Override
	public <T> void setConf(T data) {
		throw new IllegalStateException("The No Support.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getConf(T data) {
		MetaData metaData = this.obj2Mconf(data);
		Object obj = conf.getConf(metaData.getAppId(), metaData.getConfId(), metaData.getDataId(), data.getClass());
		if(obj == null){
			return null;
		}
		
		return (T)obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getConfs(T data) {
		MetaData metaData = this.obj2Mconf(data);
		return (List<T>) conf.getConfs(metaData.getAppId(), metaData.getConfId(), data.getClass());
	}

	@Override
	public <T> void subscribe(T data, NotifyMessage<List<T>> notifyMessage) {
		logger.warn("The Local Mconf No Support subscribe.");
		List<T> list = this.getConfs(data);
		notifyMessage.notify(list);
	}

	@Override
	public <T> void unsubscribe(T data) {
		logger.warn("The Local Mconf No Support unsubscribe.");
	}

	@Override
	public MconfQuery query() {
		throw new IllegalStateException("The No Support.");
	}

}
