package cn.ms.mconf.support;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Mconf;
import cn.ms.mconf.annotation.MconfEntity;
import cn.ms.micro.common.URL;

import com.alibaba.fastjson.JSON;

public abstract class AbstractMconf implements Mconf {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractMconf.class);

	public static final String ID_KEY = "id";

	private Category category = new Category();
	protected String path;
	protected String root = "root", app = "app", conf = "conf", data = "data";
	protected String DEFAULT_KEY = "default_", NODO_KEY = "node",
			ENV_KEY = "env", GROUP_KEY = "group", VERSION_KEY = "version";
	protected Map<String, String[]> structureMap = new ConcurrentHashMap<String, String[]>();

	@Override
	public void connect(URL url) {
		this.path = url.getPath();
		this.structureMap.put(root, url.getParameter(root, new String[] {}));
		this.structureMap.put(app, url.getParameter(app, new String[] {}));
		this.structureMap.put(conf, url.getParameter(conf, new String[] {}));
		this.structureMap.put(data, url.getParameter(data, new String[] {}));

		try {
			BeanUtils.copyProperties(category, url.getParameters());
		} catch (Exception e) {
			logger.error("The copyProperties exception.", e);
		}
	}

	protected <T> MetaData obj2MetaData(T data, Category... categories) {
		if (data == null) {
			throw new RuntimeException("data[" + data + "] cannot be empty");
		}

		MetaData metaData = new MetaData();
		if (data instanceof MetaData) {
			metaData = (MetaData) data;
			//$NON-NLS-JSON$
			metaData.setBody(this.obj2Json(data));
		} else {
			//$NON-NLS-@MconfEntity$
			MconfEntity mconfEntity = data.getClass().getAnnotation(MconfEntity.class);
			metaData.setConf(mconfEntity == null ? data.getClass().getName() : mconfEntity.value());
			Field dataIdField = FieldUtils.getField(data.getClass(), ID_KEY, true);
			if (dataIdField == null) {
				throw new RuntimeException("Field '" + ID_KEY + "' is null.");
			}
			
			try {
				Object dataObj = dataIdField.get(data);
				if (dataObj != null) {
					metaData.setData(String.valueOf(dataObj));
				}
			} catch (Exception e) {
				logger.error("The field get is exception.", e);
			}

			//$NON-NLS-JSON$
			metaData.setBody(this.obj2Json(data));
		}

		Category category = null;
		if (categories == null) {
			category = this.category;
		} else {
			if (categories.length != 1) {
				throw new RuntimeException("The length of the categories must be 1.");
			} else {
				category = categories[0];
			}
		}

		//$NON-NLS-Category$
		metaData.setNode(category.getNode());
		metaData.setApp(category.getApp());
		metaData.setEnv(category.getEnv());
		metaData.setCategory(category.getCategory());
		metaData.setVersion(category.getVersion());

		return metaData;
	}

	protected String buildParameters(String keyPath, URL url) {
		StringBuffer sb = new StringBuffer();

		String[] keyPathArray = structureMap.get(keyPath);
		if (keyPathArray.length > 0) {
			sb.append("?");
			for (String keyP : keyPathArray) {
				String val = url.getParameter(keyP);
				if (StringUtils.isNotBlank(val)) {
					sb.append(keyP).append("=").append(val).append("&");
				}
			}
		}

		String keyAllPath = sb.toString();
		if (keyAllPath.endsWith("&")) {
			keyAllPath = keyAllPath.substring(0, keyAllPath.length() - 1);
		}

		return encode(keyAllPath);
	}

	/**
	 * Data encoding
	 * 
	 * @param data
	 * @return
	 */
	protected String encode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding exception", e);
		}
	}

	/**
	 * Data decoding
	 * 
	 * @param data
	 * @return
	 */
	protected String decode(String data) {
		try {
			return URLDecoder.decode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Decoding exception", e);
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
