package cn.ms.mconf.redis;

import java.util.ArrayList;
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
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.NotifyConf;
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
		this.group = url.getParameter("group", "mconf");

		JedisPoolConfig config = new JedisPoolConfig();
		try {
			BeanUtils.copyProperties(config, url.getParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}

		jedisPool = new JedisPool(config, url.getHost(), url.getPort());
	}

	@Override
	public boolean available() {
		return (jedisPool == null) ? false : (!jedisPool.isClosed());
	}

	@Override
	public <T> void addConf(T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2Mconf(data);
		
		try {
			jedis = jedisPool.getResource();
			String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
			jedis.hset(key, metaData.getDataId(), metaData.getData());
		} catch (Exception e) {
			logger.error("The add conf exception.", e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public <T> void delConf(T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2Mconf(data);
		
		try {
			jedis = jedisPool.getResource();
			String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
			jedis.hdel(key, metaData.getDataId());
		} catch (Exception e) {
			logger.error("The delete conf exception.", e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public <T> void setConf(T data) {
		this.addConf(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T pull(T data) {
		Jedis jedis = null;
		MetaData metaData = this.obj2Mconf(data);
		
		try {
			jedis = jedisPool.getResource();
			String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
			String json = jedis.hget(key, metaData.getDataId());
			return (T)json2Obj(json, data.getClass());
		} catch (Exception e) {
			logger.error("The pull conf exception.", e);
		} finally {
			jedis.close();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> pulls(T data) {
		this.pushSubscribe();
		
		Jedis jedis = null;
		MetaData metaData = this.obj2Mconf(data);
		
		try {
			jedis = jedisPool.getResource();
			String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
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
		if(isSubscribe){
			isSubscribe = false;
			this.pushSubscribe();
		}
		
		Jedis jedis = null;
		MetaData metaData = this.obj2Mconf(data);
		
		try {
			jedis = jedisPool.getResource();
			String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
			if(!pushClassMap.containsKey(key)){
				pushClassMap.put(key, data.getClass());
			}
			
			//第一次拉取式通知
			List<T> list = pulls(data);
			notifyConf.notify(list);
		} catch (Exception e) {
			logger.error("The push conf exception.", e);
		} finally {
			jedis.close();
		}
	}
	
	@Override
	public <T> void unpush(T data) {
		MetaData metaData = this.obj2Mconf(data);
		String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
		
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T> void unpush(T data, NotifyConf<T> notifyConf) {
		MetaData metaData = this.obj2Mconf(data);
		String key = group + SEQ + metaData.getAppId() + SEQ + metaData.getConfId();
		
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
    	
    	long retryPeriod = 5000;
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
                			Map<String, String> oldMap = pushValueMap.get(entry.getKey());
                			if(!oldMap.equals(newMap)){//已变更
                				Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(entry.getKey());
                				for (NotifyConf notifyConf:notifyConfs) {
                					List list = new ArrayList();
                					for (Map.Entry<String, String> tempEntry:newMap.entrySet()) {
                						list.add(JSON.parseObject(tempEntry.getValue(), entry.getValue()));
                					}
                					
                					notifyConf.notify(list);
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
