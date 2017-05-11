package cn.ms.mconf.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import cn.ms.mconf.support.AbstractMconf;
import cn.ms.mconf.support.MetaData;
import cn.ms.mconf.support.NotifyConf;
import cn.ms.micro.common.URL;
import cn.ms.micro.extension.SpiMeta;

/**
 * 基于Redis的配置中心<br>
 * <br>
 * 数据结构：<group@appId@confId, Map<dataId, JSON>
 * 
 * @author lry
 */
@SpiMeta(name = "redis")
public class RedisMconf extends AbstractMconf {

	private static final Logger logger = LoggerFactory
			.getLogger(RedisMconf.class);

	public static final String SEQ = "@";
	private String group;
	JedisPool jedisPool;

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
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (Exception e) {
			logger.error("The push conf exception.", e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public <T> void unpush(T data) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (Exception e) {
			logger.error("The unpush conf exception.", e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public <T> void unpush(T data, NotifyConf<T> notifyConf) {
		Jedis jedis = null;

		try {
			jedis = jedisPool.getResource();
		} catch (Exception e) {
			logger.error("The unpush conf exception.", e);
		} finally {
			jedis.close();
		}
	}

}
