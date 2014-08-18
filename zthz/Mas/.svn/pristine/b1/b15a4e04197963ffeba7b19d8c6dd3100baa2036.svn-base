package cn.zthz.tool.cache;

import java.util.HashMap;
import java.util.Map;

import cn.zthz.actor.assemble.GlobalConfig;

import redis.clients.jedis.JedisPool;


public class RedisClientPool {


	public static final Map<String, JedisPool> pools = new HashMap<String, JedisPool>();
	
	public synchronized static JedisPool instance(String host , int port){
		String key = makeKey(host, port);
		if(pools.containsKey(key)){
			return pools.get(key);
		}else{
			JedisPool jedisPool = new JedisPool(host , port);
			pools.put(key, jedisPool);
			return jedisPool;
		}
	}
	
	public static JedisPool pubsubInstance(){
		return instance(GlobalConfig.get("redis.pubsubHost"), GlobalConfig.getInt("redis.pubsubPort"));
	}
	
	public static JedisPool cacheInstance(){
		return instance(GlobalConfig.get("redis.lruHost"), GlobalConfig.getInt("redis.lruPort"));
	}
	
	public static String makeKey(String host , int port){
		return host.trim()+":"+port;
	}
	
	
	// private static PoolableObjectFactory<Jedis> factory = new
	// PoolableObjectFactory<Jedis>() {
	//
	// @Override
	// public boolean validateObject(Jedis obj) {
	// return true;
	// }
	//
	// @Override
	// public void passivateObject(Jedis obj) throws Exception {
	//
	// }
	//
	// @Override
	// public Jedis makeObject() throws Exception {
	// return new
	// Jedis(GlobalConfig.get(GlobalConfig.KEY_PUBSUB_REDIS_HOST),
	// GlobalConfig.getInt(GlobalConfig.KEY_PUBSUB_REDIS_PORT));
	// }
	//
	// @Override
	// public void destroyObject(Jedis obj) throws Exception {
	// }
	//
	// @Override
	// public void activateObject(Jedis obj) throws Exception {
	//
	// }
	// };
	// public static GenericObjectPool<Jedis> pool = new
	// GenericObjectPool<>(factory, 2000,
	// GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 60000);

}
