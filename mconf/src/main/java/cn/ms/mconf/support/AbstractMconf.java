package cn.ms.mconf.support;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Mconf;
import cn.ms.micro.common.URL;

import com.alibaba.fastjson.JSON;

public abstract class AbstractMconf implements Mconf {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMconf.class);

	public static final String ID_KEY = "id";
	public static final String DEFAULT_KEY = "default_";

	protected String ROOT;
	protected Category category = new Category();
	protected Map<String, String[]> structureMap = new ConcurrentHashMap<String, String[]>();

	@Override
	public void connect(URL url) {
		this.ROOT = url.getPath();
		this.structureMap.put(Cmd.ROOT_KEY, url.getParameter(Cmd.ROOT_KEY, new String[] {}));
		this.structureMap.put(Cmd.APP_KEY, url.getParameter(Cmd.APP_KEY, new String[] {}));
		this.structureMap.put(Cmd.CONF_KEY, url.getParameter(Cmd.CONF_KEY, new String[] {}));
		this.structureMap.put(Cmd.DATA_KEY, url.getParameter(Cmd.DATA_KEY, new String[] {}));

		try {
			BeanUtils.copyProperties(category, url.getParameters());
		} catch (Exception e) {
			logger.error("The copyProperties exception.", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T json2Obj(String json, Class<T> clazz) {
		try {
			if (clazz == null) {
				return (T) JSON.parseObject(json);
			} else {
				return JSON.parseObject(json, clazz);
			}
		} catch (Exception e) {
			logger.error("Serialization exception", e);
			throw e;
		}
	}

	protected String obj2Json(Object obj) {
		return JSON.toJSONString(obj);
	}

}
