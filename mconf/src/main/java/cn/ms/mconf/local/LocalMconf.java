package cn.ms.mconf.local;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.DataConf;
import cn.ms.mconf.support.Notify;
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
	private Map<Object, Object> dataMap;
	
	@SuppressWarnings("unchecked")
	@Override
	public void connect(URL url) {
		super.connect(url);
		String confName = url.getParameter("confName", "mconf.yaml");
		
		try {
			Yaml yaml = new Yaml();
			java.net.URL mconfURL = LocalMconf.class.getClassLoader().getResource(confName);
			if (url != null) {
				Object obj = yaml.load(new FileInputStream(mconfURL.getFile()));
				dataMap = (Map<Object, Object>) obj;
			}
		} catch (Exception e) {
			logger.error("读取配置文件异常[" + confName + "]", e);
		}
	}
	
	@Override
	public boolean available() {
		return this.available;
	}
	
	@Override
	public <T> void addConf(URL url, T data) {
		throw new IllegalStateException("The No Support addConf.");
	}
	
	@Override
	public void delConf(URL url) {
		throw new IllegalStateException("The No Support delConf.");
	}

	@Override
	public <T> void upConf(URL url, T data) {
		throw new IllegalStateException("The No Support setConf.");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(URL url, Class<T> cls) {
		String id = url.getParameter(this.data);
		
		Iterator<String> keys = buildMapKey(url);
		Object obj = this.getListMap(keys, dataMap);
		if(obj instanceof Map){
			Map<Object, Object> tempMap = (Map<Object, Object>)obj;
			for (Map.Entry<Object, Object> entry:tempMap.entrySet()) {
				if(String.valueOf(entry.getKey()).equals(id)){
					Map<Object, Object> entityMap = (Map<Object, Object>)entry.getValue();
					entityMap.put(this.DATA_KEY, entry.getKey());
					try {
						T t = cls.newInstance();
						BeanUtils.copyProperties(t, entityMap);
						return t;
					} catch (Exception e) {
						logger.error("The copyProperties is exception.", e);
					}					
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> pulls(URL url, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		
		Iterator<String> keys = buildMapKey(url);
		Object obj = this.getListMap(keys, dataMap);
		if(obj instanceof Map){
			Map<String, Object> tempMap = (Map<String, Object>)obj;
			for (Map.Entry<String, Object> entry:tempMap.entrySet()) {
				Map<String, Object> entityMap = (Map<String, Object>)entry.getValue();
				entityMap.put(this.DATA_KEY, entry.getKey());
				try {
					T t = cls.newInstance();
					BeanUtils.copyProperties(t, entityMap);
					list.add(t);
				} catch (Exception e) {
					logger.error("The copyProperties is exception.", e);
				}
			}
		}
		
		return list;
	}

	@Override
	public <T> void push(URL url, Class<T> cls, Notify<T> notifyMessage) {
		List<T> list = this.pulls(url, cls);
		notifyMessage.notify(list);
	}

	@Override
	public void unpush(URL url) {
		logger.warn("The local mconf no support unpush.");
	}

	@Override
	public <T> void unpush(URL url, Notify<T> notifyMessage) {
		logger.warn("The local mconf no support unpush.");
	}
	
	//$NON-NLS-The Node Governor$
	@Override
	public List<DataConf> getApps() {
		logger.warn("The local mconf no support getApps.");
		return null;
	}
	
	@Override
	public List<DataConf> getConfs() {
		logger.warn("The local mconf no support getConfs.");
		return null;
	}
	
	@Override
	public List<DataConf> getKVDatas() {
		logger.warn("The local mconf no support getKVDatas.");
		return null;
	}
	
	@Override
	public Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> structures() {
		logger.warn("The local mconf no support structures.");
		return null;
	}
	
	private Iterator<String> buildMapKey(URL url) {
		List<String> keys = new ArrayList<String>();
		// setter root
		for (String rootParamter:structureMap.get(this.root)) {
			keys.add(url.getParameter(rootParamter));
		}
		keys.add(this.path);
		
		// setter app
		for (String rootParamter:structureMap.get(this.app)) {
			keys.add(url.getParameter(rootParamter));
		}
		keys.add(url.getProtocol());
		
		// setter conf
		for (String rootParamter:structureMap.get(this.conf)) {
			keys.add(url.getParameter(rootParamter));
		}
		keys.add(url.getPath());
		
		// setter data
		for (String rootParamter:structureMap.get(this.data)) {
			keys.add(url.getParameter(rootParamter));
		}
		
		return keys.iterator();
	}
	
	private Object getListMap(Iterator<String> keys, Map<Object, Object> map) {
		if(keys.hasNext()){
			String key = keys.next();
			Object obj = map.get(key);
			if(obj instanceof Map){
				@SuppressWarnings("unchecked")
				Map<Object, Object> tempMap = (Map<Object, Object>)obj;
				return this.getListMap(keys, tempMap);
			}
		}
		
		return map;
	}
}
