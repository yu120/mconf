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
import cn.ms.mconf.support.NotifyConf;
import cn.ms.micro.common.ConcurrentHashSet;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;
import cn.ms.micro.threadpool.NamedThreadFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

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
	private final ConcurrentMap<String, Set<NotifyConf>> pushNotifyConfMap = new ConcurrentHashMap<String, Set<NotifyConf>>();
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
	public <T> void addConf(URL url, T data) {
		String key = this.buildKey(url);
		String field = this.buildParameters(this.data, url);
		String json = this.obj2Json(data);

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
	public void delConf(URL url) {
		String key = this.buildKey(url);

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String data = url.getParameter(this.data);
			if (StringUtils.isNotBlank(data)) {
				String field = this.buildParameters(this.data, url);
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
	public <T> void upConf(URL url, T data) {
		this.addConf(url, data);
	}

	@Override
	public <T> T pull(URL url, Class<T> cls) {
		String key = this.buildKey(url);
		if (StringUtils.isBlank(url.getParameter(this.data))) {
			throw new RuntimeException("The must set parameter 'data'[url.setParameter('data',…)].");
		}

		String field = this.buildParameters(this.data, url);
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
	public <T> List<T> pulls(URL url, Class<T> cls) {
		String key = this.buildKey(url);

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Map<String, String> dataMap = jedis.hgetAll(key);

			JSONArray jsonArray = new JSONArray();
			jsonArray.addAll(dataMap.values());
			return JSON.parseArray(jsonArray.toString(), cls);
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
	public <T> void push(URL url, Class<T> cls, NotifyConf<T> notifyConf) {
		if (isSubscribe) {
			this.pushSubscribe();
			isSubscribe = false;
		}

		String key = this.buildKey(url);
		Jedis jedis = null;

		try {
			jedis = jedisPool.getResource();
			if (!pushClassMap.containsKey(key)) {
				pushClassMap.put(key, cls);
			}

			Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(key);
			if (notifyConfs == null) {
				pushNotifyConfMap.put(key, notifyConfs = new ConcurrentHashSet<NotifyConf>());
			}
			notifyConfs.add(notifyConf);

			// 第一次拉取式通知
			Map<String, String> dataMap = jedis.hgetAll(key);
			if (dataMap == null) {
				dataMap = new HashMap<String, String>();
			}

			JSONArray jsonArray = new JSONArray();
			jsonArray.addAll(dataMap.values());
			List<T> list = JSON.parseArray(jsonArray.toString(), cls);
			pushValueMap.put(key, dataMap);
			notifyConf.notify(list);
		} catch (Exception e) {
			logger.error("The push conf exception.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void unpush(URL url) {
		String key = this.buildKey(url);

		if (pushClassMap.containsKey(key)) {
			pushClassMap.remove(key);
		}

		if (pushNotifyConfMap.containsKey(key)) {
			pushNotifyConfMap.remove(key);
		}

		if (pushValueMap.containsKey(key)) {
			pushValueMap.remove(key);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> void unpush(URL url, NotifyConf<T> notifyConf) {
		String key = this.buildKey(url);

		Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(key);
		notifyConfs.remove(notifyConf);

		if (pushNotifyConfMap.get(key) == null) {
			pushValueMap.remove(key);
		}
	}

	//$NON-NLS-The Node Governor$
	@Override
	public Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> structures() {
		// TODO Auto-generated method stub
		return null;
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
								Set<NotifyConf> notifyConfs = pushNotifyConfMap.get(entry.getKey());
								if (notifyConfs == null) {
									continue;
								} else {
									pushValueMap.put(entry.getKey(), newMap);
									for (NotifyConf notifyConf : notifyConfs) {
										List list = new ArrayList();
										for (Map.Entry<String, String> tempEntry : newMap.entrySet()) {
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

	/**
	 * The Build Key.
	 * 
	 * @param url
	 * @return
	 */
	private String buildKey(URL url) {
		// check app
		if (StringUtils.isBlank(url.getProtocol())) {
			throw new RuntimeException("The must set parameter 'app'[url.setProtocol(…)].");
		}
		// check conf
		if (StringUtils.isBlank(url.getPath())) {
			throw new RuntimeException("The must set parameter 'conf'[url.setPath(…)].");
		}

		StringBuffer sb = new StringBuffer();
		sb.append("/").append(this.path).append(this.buildParameters(root, url));
		sb.append("/").append(url.getProtocol()).append(this.buildParameters(app, url));
		sb.append("/").append(url.getPath()).append(this.buildParameters(conf, url));

		return sb.toString();
	}

}
