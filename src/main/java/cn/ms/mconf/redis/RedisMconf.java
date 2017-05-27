package cn.ms.mconf.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.Category;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.NotifyConf;
import cn.ms.micro.common.ConcurrentHashSet;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;
import cn.ms.micro.threadpool.NamedThreadFactory;

import com.alibaba.fastjson.JSON;

/**
 * 基于Redis的配置中心<br>
 * <br>
 * 数据结构：<group@appId@confId, Map<dataId, JSON>
 * 
 * @author lry
 */
@SpiMeta(name = "redis")
public class RedisMconf extends AbstractMconf {

	private static final Logger logger = LoggerFactory.getLogger(RedisMconf.class);

	public static final String SEQ = "@";
	
	private String group;
	private JedisPool jedisPool;
	private long retryPeriod = 5000;
	private boolean isSubscribe = true;
	
	@SuppressWarnings("unused")
	private ScheduledFuture<?> retryFuture;
    private final Map<String, Class<?>> pushClassMap = new ConcurrentHashMap<String, Class<?>>();
    @SuppressWarnings("rawtypes")
	private final ConcurrentMap<String, Set<NotifyConf>> pushNotifyConfMap = new ConcurrentHashMap<String, Set<NotifyConf>>();
    private final ConcurrentMap<String, Map<String, String>> pushValueMap = new ConcurrentHashMap<String, Map<String, String>>();
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("RedisMconfTimer", true));
    
	@Override
	public void connect(URL url) {
		super.connect(url);
		this.group = url.getParameter("group", "mconf");
		this.retryPeriod = url.getParameter("retryPeriod", retryPeriod);

		JedisPoolConfig config = new JedisPoolConfig();
		try {
			BeanUtils.copyProperties(config, url.getParameters());
		} catch (Exception e) {
			logger.error("The copy properties exception.", e);
		}

		jedisPool = new JedisPool(config, url.getHost(), url.getPort());
	}

	@Override
	public boolean available() {
		return (jedisPool == null) ? false : (!jedisPool.isClosed());
	}
	
	public String toBuildKey(MetaData metaData) {
		return group + "-" + metaData.getNode() + SEQ + metaData.getApp() + SEQ + metaData.getConf();
	}

	@Override
	public <T> void addConf(T data) {
		this.addConf(category, data);
	}
	
	@Override
	public <T> void addConf(Category category, T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2MetaData(data, category);
		
		try {
			jedis = jedisPool.getResource();
			String key = toBuildKey(metaData);
			jedis.hset(key, metaData.toBuildDataId(), String.valueOf(metaData.getBody()));
		} catch (Exception e) {
			logger.error("The add conf exception.", e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public <T> void delConf(T data) {
		this.delConf(category, data);
	}
	
	@Override
	public <T> void delConf(Category category, T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2MetaData(data, category);
		
		try {
			jedis = jedisPool.getResource();
			String key = toBuildKey(metaData);
			jedis.hdel(key, metaData.toBuildDataId());
		} catch (Exception e) {
			logger.error("The delete conf exception.", e);
		} finally {
			jedis.close();
		}
	}
	
	@Override
	public <T> void setConf(T data) {
		this.setConf(category, data);
	}

	@Override
	public <T> void setConf(Category category, T data) {
		this.addConf(data);
	}

	@Override
	public <T> T pull(T data) {
		return this.pull(category, data);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(Category category, T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2MetaData(data, category);
		
		try {
			jedis = jedisPool.getResource();
			String key = toBuildKey(metaData);
			String json = jedis.hget(key, metaData.toBuildDataId());
			return (T)json2Obj(json, data.getClass());
		} catch (Exception e) {
			logger.error("The pull conf exception.", e);
		} finally {
			jedis.close();
		}

		return null;
	}
	
	@Override
	public <T> List<T> pulls(T data) {
		return this.pulls(category, data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> pulls(Category category, T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2MetaData(data, category);
		
		try {
			jedis = jedisPool.getResource();
			String key = toBuildKey(metaData);
			Map<String, String> dataMap = jedis.hgetAll(key);
			
			List<T> list = new ArrayList<T>();
			for (Map.Entry<String, String> entry:dataMap.entrySet()) {
				list.add((T)JSON.parseObject(entry.getValue(), data.getClass()));
			}
			
			return list;
		} catch (Exception e) {
			logger.error("The pulls conf exception.", e);
		} finally {
			jedis.close();
		}

		return null;
	}
	
	@Override
	public <T> void push(T data, NotifyConf<T> notifyConf) {
		this.push(category, data, notifyConf);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> void push(Category category, T data, NotifyConf<T> notifyConf) {
		if(isSubscribe){
			this.pushSubscribe();
			isSubscribe = false;
		}
		
		Jedis jedis = null;
		MetaData metaData = this.obj2MetaData(data, category);
		
		try {
			jedis = jedisPool.getResource();
			String key = toBuildKey(metaData);
			if(!pushClassMap.containsKey(key)){
				pushClassMap.put(key, data.getClass());
			}
			
			Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(key);
			if(notifyConfs == null){
				pushNotifyConfMap.put(key, notifyConfs = new ConcurrentHashSet<NotifyConf>());
			}
			notifyConfs.add(notifyConf);
			
			//第一次拉取式通知
			Map<String, String> dataMap = jedis.hgetAll(key);
			if(dataMap==null){
				dataMap = new HashMap<String, String>();
			}
			List<T> list = new ArrayList<T>();
			for (Map.Entry<String, String> entry:dataMap.entrySet()) {
				list.add((T)JSON.parseObject(entry.getValue(), data.getClass()));
			}
			pushValueMap.put(key, dataMap);
			notifyConf.notify(list);
		} catch (Exception e) {
			logger.error("The push conf exception.", e);
		} finally {
			jedis.close();
		}
	}
	
	@Override
	public <T> void unpush(T data) {
		this.unpush(category, data);
	}
	
	@Override
	public <T> void unpush(Category category, T data) {
		MetaData metaData = this.obj2MetaData(data, category);
		String key = toBuildKey(metaData);
		
		if(pushClassMap.containsKey(key)){
			pushClassMap.remove(key);
		}
		
		if(pushNotifyConfMap.containsKey(key)){
			pushNotifyConfMap.remove(key);
		}
		
		if(pushValueMap.containsKey(key)){
			pushValueMap.remove(key);
		}
	}
	
	@Override
	public <T> void unpush(T data, NotifyConf<T> notifyConf) {
		this.unpush(category, data, notifyConf);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T> void unpush(Category category, T data, NotifyConf<T> notifyConf) {
		MetaData metaData = this.obj2MetaData(data, category);
		String key = toBuildKey(metaData);
		
		Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(key);
		notifyConfs.remove(notifyConf);
		
		if(pushNotifyConfMap.get(key) == null){
			pushValueMap.remove(key);
		}
	}
	
	/**
	 * 定时拉取数据
	 */
	private void pushSubscribe() {
    	if(!isSubscribe){
    		return;
    	}
    	
    	this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
			public void run() {
                try {
                	if(pushClassMap.isEmpty()){
                		return;
                	}
                	
                	for (Map.Entry<String, Class<?>> entry:pushClassMap.entrySet()) {
                		Jedis jedis = null;
                		try {
                			jedis = jedisPool.getResource();
                			Map<String, String> newMap = jedis.hgetAll(entry.getKey());
                			if(newMap == null){
                				newMap = new HashMap<String, String>();
                			}
                			Map<String, String> oldMap = pushValueMap.get(entry.getKey());
                			if(!newMap.equals(oldMap)){//已变更
                				Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(entry.getKey());
                				if(notifyConfs == null){
                					continue;
                				} else {
                					pushValueMap.put(entry.getKey(), newMap);
	                				for (NotifyConf notifyConf:notifyConfs) {
	                					List list = new ArrayList();
	                					for (Map.Entry<String, String> tempEntry:newMap.entrySet()) {
	                						list.add(JSON.parseObject(tempEntry.getValue(), entry.getValue()));
	                					}
	                					
	                					notifyConf.notify(list);
									}
                				}
                			}
                		} catch (Exception e) {
                			logger.error("The push conf exception.", e);
                		} finally {
                			jedis.close();
                		}
					}
                } catch (Throwable t) { // 防御性容错
                    logger.error("Unexpected error occur at failed retry, cause: " + t.getMessage(), t);
                }
            }
        }, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
	}

}
