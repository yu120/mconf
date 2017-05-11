package cn.ms.mconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.io.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件
 * 
 * @author lry
 */
public enum Conf {

	INSTANCE;
	
	private static final Logger logger = LoggerFactory.getLogger(Conf.class);
	
	private static String DEFAULT_CONF_NAME = "mconf.properties";
	private static final String SEG = ".";
	private static final String ID_KEY = "id";
	private PropertiesConfiguration conf;
	private FileHandler handler;
	
	public boolean connection(String confName) {
		try {
			conf = new PropertiesConfiguration();
			if (confName == null) {
				confName = DEFAULT_CONF_NAME;
			}
			handler = new FileHandler(conf);
			handler.setFileName(confName);
			handler.load();
			
			return true;
		} catch (Exception e) {
			logger.error("读取配置文件异常[" + confName + "]", e);
		}
		
		return false;
	}
	
	public Object getProperty(String key) {
		return conf.getProperty(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, T defaultValue) {
		Object value = this.getProperty(key);
		if(value == null) {
			return defaultValue;
		} else {
			return (T)value;
		}
	}
	
	public Map<String, Object> getPropertys() {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> idSubkeys = conf.getKeys();
		while (idSubkeys.hasNext()) {
			String tempSubKey = idSubkeys.next();
			map.put(tempSubKey, conf.getProperty(tempSubKey));
		}
		
		return map;
	}

	/**
	 * 读取一个配置模型
	 * 
	 * @param appId
	 * @param confId
	 * @param id
	 * @param cls
	 * @return
	 */
	public <T> T getConf(String appId, String confId, String id, Class<T> cls) {
		Configuration subset = conf.subset(appId + SEG + confId + SEG + id);
		Map<String, Object> subData = new HashMap<String, Object>();
		subData.put(ID_KEY, id);
		Iterator<String> idSubkeys = subset.getKeys();
		while (idSubkeys.hasNext()) {
			String tempSubKey = idSubkeys.next();
			subData.put(tempSubKey, subset.getProperty(tempSubKey));
		}
		
		T t = null;
		try {
			t = cls.newInstance();
		} catch (Exception e) {
			logger.error("The class newInstance exception.", e);
		}
		
		try {
			BeanUtils.copyProperties(t, subData);
		} catch (Exception e) {
			logger.error("The BeanUtils copyProperties exception.", e);
		}
		
		return t;
	}
	
	/**
	 * 读取多配置
	 * 
	 * @param appId
	 * @param confId
	 * @param cls
	 * @return
	 */
	public <T> List<T> getConfs(String appId, String confId, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		Configuration subset = conf.subset(appId + SEG + confId);
		List<String> dataIds = this.getDataIds(appId, confId);
		if(dataIds==null){
			return list;
		}
		
		for (String dataId : dataIds) {
			Configuration idSubset = subset.subset(dataId);
			Iterator<String> idSubkeys = idSubset.getKeys();
			Map<String, Object> subData = new HashMap<String, Object>();
			subData.put(ID_KEY, dataId);
			while (idSubkeys.hasNext()) {
				String tempSubKey = idSubkeys.next();
				subData.put(tempSubKey, idSubset.getProperty(tempSubKey));
			}
			
			T t = null;
			try {
				t = cls.newInstance();
			} catch (Exception e) {
				logger.error("The class newInstance exception.", e);
			}
			
			try {
				BeanUtils.copyProperties(t, subData);
				list.add(t);
			} catch (Exception e) {
				logger.error("The BeanUtils copyProperties exception.", e);
			}
		}
		
		return list;
	}
	
	private List<String> getDataIds(String appId, String confId) {
		return conf.getList(String.class, appId + SEG + confId + SEG + ID_KEY);
	}
	
}
