package cn.ms.mconf.support;

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

	@Override
	public void connect(URL url) {
		this.ROOT = url.getPath();
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
