package cn.zthz.redis;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import cn.zthz.actor.assemble.GlobalConfig;

public class TestRedisConnection {
	private static final Log log = LogFactory.getLog(TestRedisConnection.class);
	public static final Map<String, JedisPool> jedisPools = new HashMap<>();
	public static JedisPool instance(String host, int port){
		String key = host.trim() + ":" + port;
		if(jedisPools.containsKey(key)){
			return jedisPools.get(key);
		}else {
			JedisPool jedisPool = new JedisPool(host, port);
			jedisPools.put(key, jedisPool);
			return jedisPool;
		}
	}
	
	public static boolean exists(String key){
		JedisPool jedisPool = null;
		Jedis jedis = null;
		try{
			jedisPool = TestRedisConnection.instance(GlobalConfig.get("redis.lruHost"), GlobalConfig.getInt("redis.lruPort"));
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		}finally{
			if(null != jedisPool && jedis != null){
				jedisPool.returnResource(jedis);
			}
			
		}
	}
	
	public static void main(String[] args) {
		log.debug(exists("test"));
	}
}
