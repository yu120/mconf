package cn.ms.mconf.support;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ms.mconf.Mconf;
import cn.ms.mconf.annotation.DataEntity;
import cn.ms.mconf.annotation.MconfEntity;

import com.alibaba.fastjson.JSON;

public abstract class AbstractMconf implements Mconf {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMconf.class);
	
	public static final String ID_KEY = "id";
	
	public <T> MetaData obj2Mconf(T data) {
		if (data == null) {
			throw new RuntimeException("data[" + data + "] cannot be empty");
		}
		if (data instanceof MetaData) {
			return (MetaData) data;
		}

		MetaData metaData = new MetaData();
		
		//$NON-NLS-@MconfEntity$
		MconfEntity mconfEntity = data.getClass().getAnnotation(MconfEntity.class);
		if (mconfEntity == null) {
			throw new RuntimeException("Configuration entity[" + data.getClass() + "] Must contain @MconfEntity annotations.");
		}
		
		metaData.setApp(mconfEntity.app());
		metaData.setConf(mconfEntity.conf());
		if ("$".equals(metaData.getConf())) {// If it is the default value, use the SimpleName
			metaData.setConf(data.getClass().getSimpleName());
		}
		
		Field dataIdField = FieldUtils.getField(data.getClass(), ID_KEY, true);
		if(dataIdField == null){
			throw new RuntimeException("Field '"+ID_KEY+"' is null.");
		}
		try {
			Object dataObj = dataIdField.get(data);
			if(dataObj != null){
				metaData.setData(String.valueOf(dataObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//$NON-NLS-@DataEntity$
		DataEntity dataEntity = data.getClass().getAnnotation(DataEntity.class);
		if (dataEntity != null) {
			metaData.setNode(dataEntity.node());
			metaData.setEnv(dataEntity.env());
			metaData.setGroup(dataEntity.group());
			metaData.setVersion(dataEntity.version());
		}
		
		//$NON-NLS-JSON Body$
		metaData.setBody(this.obj2Json(data));
		
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
