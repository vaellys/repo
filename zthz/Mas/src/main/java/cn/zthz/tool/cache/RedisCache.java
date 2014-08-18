package cn.zthz.tool.cache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.JsonUtils;

public class RedisCache {
	public static final int LRU_EXPIRE = GlobalConfig.getInt("redis.lruExpire");
	public static final RedisCache instance = new RedisCache();

	public void setObject(String key, Object object) {
		set(key, JsonUtils.encode(object), LRU_EXPIRE);
	}

	public Object getObject(String key) {
		return JsonUtils.decode(get(key));
	}

	public void set(String key, String content) {
		set(key, content, LRU_EXPIRE);
	}

	public boolean exists(String key) {
		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			return redis.exists(key);
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}

	}

	public void set(String key, String content, int seconds) {
		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			if (null == content) {
				redis.expire(key, 0);
			} else {
				redis.set(key, content);
				redis.expire(key, seconds);
			}
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}
	}

	public void resetExpire(String key) {
		setExpire(key, LRU_EXPIRE);
	}

	public void setExpire(List<String> keys, int seconds) {
		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			Pipeline pipeline = redis.pipelined();
			for (String key : keys) {
				pipeline.expire(key, seconds);
			}
			pipeline.sync();
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}
	}

	public void expire(List<String> keys) {
		setExpire(keys, 0);
	}

	public void expire(String key) {
		setExpire(key, 0);
	}

	public void setExpire(String key, int seconds) {

		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			redis.expire(key, seconds);
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}
	}

	public Map<String, String> gets(List<String> keys) {
		return gets(keys, LRU_EXPIRE);
	}

	public Map<String, String> gets(List<String> keys, int expire) {
		JedisPool pool = null;
		Jedis redis = null;
		if (null == keys || keys.isEmpty()) {
			return new LinkedHashMap<>();
		}
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			String[] temp = new String[keys.size()];
			List<String> result = redis.mget(keys.toArray(temp));
			Pipeline pipeline = redis.pipelined();
			for (String key : keys) {
				pipeline.expire(key, expire);
			}
			pipeline.sync();
			Map<String, String> map = new LinkedHashMap<>(keys.size());
			for (int i = 0; i < keys.size(); i++) {
				map.put(keys.get(i), result.get(i));
			}
			return map;
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}

	}

	public String get(String key) {
		return get(key, LRU_EXPIRE);
	}

	public String get(String key, int expire) {
		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			String result = redis.get(key);
			if (null != result) {
				redis.expire(key, expire);
			}
			return result;
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}
	}

	public void rpushList(String key, List<String> strings) {

		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			Pipeline pipeline = redis.pipelined();
			for (String string : strings) {
				pipeline.rpush(key, string);
			}
			pipeline.sync();
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}
	}

	public <T> T withRedis(RedisExecute<T> redisExecute) {

		JedisPool pool = null;
		Jedis redis = null;
		try {
			pool = RedisClientPool.cacheInstance();
			redis = pool.getResource();
			return redisExecute.execute(redis);
		} finally {
			if (null != pool && null != redis) {
				pool.returnResource(redis);
			}
		}
	}

	public List<String> getList(final String key) {
		return withRedis(new RedisExecute<List<String>>() {
			@Override
			public List<String> execute(Jedis redis) {
				return redis.lrange(key, 0, -1);
			}
		});
	}

}
