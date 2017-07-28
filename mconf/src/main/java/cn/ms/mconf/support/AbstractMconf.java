package cn.ms.mconf.support;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Mconf;
import cn.ms.mconf.annotation.MconfEntity;
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
