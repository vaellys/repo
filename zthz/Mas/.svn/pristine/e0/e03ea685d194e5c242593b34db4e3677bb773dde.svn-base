package cn.zthz.actor.assemble;

import java.util.HashMap;
import java.util.Map;

import cn.zthz.tool.common.ConfigUtils;

public class GlobalConfig {
	
	private static final Map<String, String> config = new HashMap<>();
	static {
		config.putAll(ConfigUtils.getProperties("mas-default.properties" , true));
		config.putAll(ConfigUtils.getProperties("jdbc.properties" , true));
	}
	
	public static String get(String key){
		return config.get(key);
	}
	public static String get(String key ,String defaultValue){
		if(config.containsKey(key)){
			return config.get(key);
		}else{
			return defaultValue;
		}
	}
	
	public static int getInt(String key,int defaultValue){
		if(config.containsKey(key)){
			return Integer.valueOf(config.get(key));
		}else{
			return defaultValue;
		}
	}
	public static int getInt(String key){
		return Integer.valueOf(config.get(key));
	}
	
	public static long getLong(String key,long defaultValue){
		if(config.containsKey(key)){
			return Long.valueOf(config.get(key));
		}else{
			return defaultValue;
		}
	}
	public static long getLong(String key){
		return Long.valueOf(config.get(key));
	}
	public static boolean getBoolean(String key){
		return Boolean.valueOf(config.get(key));
	}
	
	
	

}
