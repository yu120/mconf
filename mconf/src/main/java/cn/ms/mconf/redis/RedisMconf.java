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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.Cmd;
import cn.ms.mconf.support.DataConf;
import cn.ms.mconf.support.Notify;
import cn.ms.micro.common.ConcurrentHashSet;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;
import cn.ms.micro.threadpool.NamedThreadFactory;

import com.alibaba.fastjson.JSON;

/**
 * The base of Redis Mconf.
 * 
 * @author lry
 */
@SpiMeta(name = "redis")
public class RedisMconf extends AbstractMconf {

	private static final Logger logger = LoggerFactory.getLogger(RedisMconf.class);

	private JedisPool jedisPool;
	private long retryPeriod = 10000;
	private boolean isSubscribe = true;

	private final Map<String, Class<?>> pushClassMap = new ConcurrentHashMap<String, Class<?>>();
	@SuppressWarnings("rawtypes")
	private final ConcurrentMap<String, Set<Notify>> pushNotifyMap = new ConcurrentHashMap<String, Set<Notify>>();
	private final ConcurrentMap<String, Map<String, String>> pushValueMap = new ConcurrentHashMap<String, Map<String, String>>();

	@SuppressWarnings("unused")
	private ScheduledFuture<?> retryFuture;
	private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("RedisMconfTimer", true));

	@Override
	public void connect(URL url) {
		super.connect(url);
		this.retryPeriod = url.getParameter("retryPeriod", retryPeriod);

		JedisPoolConfig config = new JedisPoolConfig();
		Map<String, String> parameters = url.getParameters();
		if (parameters != null) {
			if (!parameters.isEmpty()) {
				try {
					BeanUtils.copyProperties(config, url.getParameters());
				} catch (Exception e) {
					logger.error("The copy properties exception.", e);
				}
			}
		}

		jedisPool = new JedisPool(config, url.getHost(), url.getPort());
	}

	@Override
	public boolean available() {
		return (jedisPool == null) ? false : (!jedisPool.isClosed());
	}

	@Override
	public void addConf(Cmd cmd, Object obj) {
		String key = cmd.buildRoot(super.url).getPrefixKey();
		String field = cmd.getSuffixKey();
		String json = this.obj2Json(obj);
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hset(key, field, json);
		} catch (Exception e) {
			logger.error("The add conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void delConf(Cmd cmd) {
		String key = cmd.buildRoot(super.url).getPrefixKey();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String field = cmd.getSuffixKey();
			if (StringUtils.isNotBlank(field)) {
				jedis.hdel(key, field);
			} else {
				jedis.hdel(key);
			}
		} catch (Exception e) {
			logger.error("The delete conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void upConf(Cmd cmd, Object obj) {
		this.addConf(cmd, obj);
	}

	@Override
	public <T> T pull(Cmd cmd, Class<T> cls) {
		String key = cmd.buildRoot(super.url).getPrefixKey();
		String field = cmd.getSuffixKey();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String json = jedis.hget(key, field);
			return (T) json2Obj(json, cls);
		} catch (Exception e) {
			logger.error("The pull conf exception.", e);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public <T> List<T> pulls(Cmd cmd, Class<T> cls) {
		String key = cmd.buildRoot(super.url).getPrefixKey();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Map<String, String> dataMap = jedis.hgetAll(key);
			List<T> list = new ArrayList<T>();
			for (String tempJson:dataMap.values()) {
				list.add(JSON.parseObject(tempJson, cls));
			}
			
			return list;
		} catch (Exception e) {
			logger.error("The pulls conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void push(Cmd cmd, Class<T> cls, Notify<T> notify) {
		if (isSubscribe) {
			this.pushSubscribe();
			isSubscribe = false;
		}
		
		String key = cmd.buildRoot(super.url).getPrefixKey();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (!pushClassMap.containsKey(key)) {
				pushClassMap.put(key, cls);
			}

			Set<Notify> notifies = pushNotifyMap.get(key);
			if (notifies == null) {
				pushNotifyMap.put(key, notifies = new ConcurrentHashSet<Notify>());
			}
			notifies.add(notify);

			// 第一次拉取式通知
			Map<String, String> dataMap = jedis.hgetAll(key);
			if (dataMap == null) {
				dataMap = new HashMap<String, String>();
			}

			List<T> list = new ArrayList<T>();
			for (String tempJson:dataMap.values()) {
				list.add(JSON.parseObject(tempJson, cls));
			}
			
			pushValueMap.put(key, dataMap);
			notify.notify(list);
		} catch (Exception e) {
			logger.error("The push conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void unpush(Cmd cmd) {
		String key = cmd.buildRoot(super.url).getPrefixKey();
		if (pushClassMap.containsKey(key)) {
			pushClassMap.remove(key);
		}
		if (pushNotifyMap.containsKey(key)) {
			pushNotifyMap.remove(key);
		}
		if (pushValueMap.containsKey(key)) {
			pushValueMap.remove(key);
		}
	}

	//$NON-NLS-The Node Governor$
	@Override
	public List<DataConf> getApps() {
		List<DataConf> appConfs = new ArrayList<DataConf>();
		Map<String, DataConf> appConfMap = new HashMap<String, DataConf>();
		
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> keySet = jedis.keys("/" + super.ROOT + "*");
			for (String key:keySet) {
				String[] keyArray = key.split("/");
				if(keyArray.length == 4){
					DataConf dataConf = new DataConf();
					// build root
					URL tempRootURL = URL.valueOf("/" + URL.decode(keyArray[1]));
					dataConf.setRoot(tempRootURL.getPath());
					dataConf.setRootAttrs(tempRootURL.getParameters());
					// build app
					URL tempAppURL = URL.valueOf("/" + URL.decode(keyArray[2]));
					dataConf.setNode(tempAppURL.getParameter(Cmd.NODE_KEY));
					dataConf.setApp(tempAppURL.getPath());
					dataConf.setAppAttrs(tempAppURL.getParameters());
					// build others
					dataConf.setSubNum(jedis.keys("/" + keyArray[1] + "/" + keyArray[2] + "/*").size());
					appConfMap.put("/" + keyArray[1] + "/" + keyArray[2], dataConf);
				}
			}
			
			if(!appConfMap.isEmpty()){
				appConfs.addAll(appConfMap.values());
			}
		} catch (Exception e) {
			logger.error("The pulls conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return appConfs;
	}
	
	@Override
	public List<DataConf> getConfs() {
		List<DataConf> confConfs = new ArrayList<DataConf>();
		Map<String, DataConf> confConfMap = new HashMap<String, DataConf>();
		
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> keySet = jedis.keys("/" + super.ROOT + "*");
			for (String key:keySet) {
				String[] keyArray = key.split("/");
				if(keyArray.length == 4){
					DataConf dataConf = new DataConf();
					// build root
					URL tempRootURL = URL.valueOf("/" + URL.decode(keyArray[1]));
					dataConf.setRoot(tempRootURL.getPath());
					dataConf.setRootAttrs(tempRootURL.getParameters());
					// build app
					URL tempAppURL = URL.valueOf("/" + URL.decode(keyArray[2]));
					dataConf.setNode(tempAppURL.getParameter(Cmd.NODE_KEY));
					dataConf.setApp(tempAppURL.getPath());
					dataConf.setAppAttrs(tempAppURL.getParameters());
					// build conf
					URL tempConfURL = URL.valueOf("/" + URL.decode(keyArray[3]));
					dataConf.setEnv(tempConfURL.getParameter(Cmd.ENV_KEY));
					dataConf.setGroup(tempConfURL.getParameter(Cmd.GROUP_KEY));
					dataConf.setVersion(tempConfURL.getParameter(Cmd.VERSION_KEY));
					dataConf.setConf(tempConfURL.getPath());
					dataConf.setConfAttrs(tempConfURL.getParameters());
					// build others
					dataConf.setSubNum(jedis.hkeys(key).size());
					
					confConfMap.put(key, dataConf);
				}
			}
			
			if(!confConfMap.isEmpty()){
				confConfs.addAll(confConfMap.values());
			}
		} catch (Exception e) {
			logger.error("The pulls conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return confConfs;
	}
	
	@Override
	public List<DataConf> getBodys() {
		List<DataConf> confConfs = new ArrayList<DataConf>();
		Map<String, DataConf> confConfMap = new HashMap<String, DataConf>();
		
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> keySet = jedis.keys("/" + super.ROOT + "*");
			for (String key:keySet) {
				String[] keyArray = key.split("/");
				if(keyArray.length == 4){
					Set<String> fieldSet = jedis.hkeys(key);
					for (String field:fieldSet) {
						String[] fieldArray = field.split("/");
						if(fieldArray.length != 2){
							continue;
						}
						
						DataConf dataConf = new DataConf();
						// build root
						URL tempRootURL = URL.valueOf("/" + URL.decode(keyArray[1]));
						dataConf.setRoot(tempRootURL.getPath());
						dataConf.setRootAttrs(tempRootURL.getParameters());
						// build app
						URL tempAppURL = URL.valueOf("/" + URL.decode(keyArray[2]));
						dataConf.setNode(tempAppURL.getParameter(Cmd.NODE_KEY));
						dataConf.setApp(tempAppURL.getPath());
						dataConf.setAppAttrs(tempAppURL.getParameters());
						// build conf
						URL tempConfURL = URL.valueOf("/" + URL.decode(keyArray[3]));
						dataConf.setEnv(tempConfURL.getParameter(Cmd.ENV_KEY));
						dataConf.setGroup(tempConfURL.getParameter(Cmd.GROUP_KEY));
						dataConf.setVersion(tempConfURL.getParameter(Cmd.VERSION_KEY));
						dataConf.setConf(tempConfURL.getPath());
						dataConf.setConfAttrs(tempConfURL.getParameters());
						// build data
						URL tempDataURL = URL.valueOf("/" + URL.decode(fieldArray[1]));
						dataConf.setData(tempDataURL.getPath());
						dataConf.setDataAttrs(tempDataURL.getParameters());
						// build others
						dataConf.setSubNum(0);
						dataConf.setJson(jedis.hget(key, field));
						dataConf.setBody(JSON.parseObject(dataConf.getJson(), Map.class));
						
						confConfMap.put(key + field, dataConf);
					}
				}
			}
			
			if(!confConfMap.isEmpty()){
				confConfs.addAll(confConfMap.values());
			}
		} catch (Exception e) {
			logger.error("The pulls conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return confConfs;
	}
	
	/**
	 * 定时拉取数据
	 */
	private void pushSubscribe() {
		if (!isSubscribe) {
			return;
		}

		this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void run() {
				try {
					if (pushClassMap.isEmpty()) {
						return;
					}

					for (Map.Entry<String, Class<?>> entry : pushClassMap.entrySet()) {
						Jedis jedis = null;
						try {
							jedis = jedisPool.getResource();
							Map<String, String> newMap = jedis.hgetAll(entry.getKey());
							if (newMap == null) {
								newMap = new HashMap<String, String>();
							}
							Map<String, String> oldMap = pushValueMap.get(entry.getKey());
							if (!newMap.equals(oldMap)) {// 已变更
								Set<Notify> notifies = pushNotifyMap.get(entry.getKey());
								if (notifies == null) {
									continue;
								} else {
									pushValueMap.put(entry.getKey(), newMap);
									for (Notify notify : notifies) {
										List list = new ArrayList();
										for (Map.Entry<String, String> tempEntry : newMap.entrySet()) {
											list.add(JSON.parseObject(tempEntry.getValue(), entry.getValue()));
										}

										notify.notify(list);
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
