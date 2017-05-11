package cn.ms.mconf.support;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.ms.mconf.Mconf;
import cn.ms.mconf.annotation.DataId;
import cn.ms.mconf.annotation.MconfEntity;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MetaData;

public abstract class AbstractMconf implements Mconf {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMconf.class);
	
	public <T> MetaData obj2Mconf(T data) {
		if (data == null) {
			throw new RuntimeException("data[" + data + "] cannot be empty");
		}
		if (data instanceof MetaData) {
			return (MetaData) data;
		}

		MetaData metaData = new MetaData();
		metaData.setData(this.obj2Json(data));

		//$NON-NLS-Get application ID and configuration name$
		MconfEntity mconfEntity = data.getClass().getAnnotation(MconfEntity.class);
		if (mconfEntity == null) {
			throw new RuntimeException("Configuration entity[" +
					data.getClass() + "] Must contain @MconfEntity annotations.");
		}

		metaData.setAppId(mconfEntity.appId());
		metaData.setConfId(mconfEntity.confId());
		if ("$".equals(metaData.getConfId())) {// If it is the default value, use the SimpleName
			metaData.setConfId(data.getClass().getSimpleName());
		}

		try {
			Field[] subFields = data.getClass().getDeclaredFields();
			Field dataIdField = null;
			for (Field field : subFields) {
				DataId dataId = field.getAnnotation(DataId.class);
				field.setAccessible(true);
				if (dataId != null) {
					dataIdField = field;
					break;
				}
			}
			
			if (dataIdField == null) {
				Field[] superFields = data.getClass().getSuperclass().getDeclaredFields();
				for (Field field : superFields) {
					DataId dataId = field.getAnnotation(DataId.class);
					field.setAccessible(true);
					if (dataId != null) {
						dataIdField = field;
						break;
					}
				}
			}

			if (null == dataIdField) {
				throw new RuntimeException("Configuration entity[" + 
						data.getClass() + "] The member variable must contain a @DataId comment field (Field)");
			}

			Object object = dataIdField.get(data);
			if (object != null) {
				metaData.setDataId(String.valueOf(object));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return metaData;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T json2Obj(String json, Class<T> clazz) {
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

	public String obj2Json(Object obj) {
		return JSON.toJSONString(obj);
	}
	
}
