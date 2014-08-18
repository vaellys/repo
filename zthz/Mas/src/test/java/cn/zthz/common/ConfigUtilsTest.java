package cn.zthz.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigUtilsTest {
	public static final Map<String, String> map = new HashMap<>();
	public static final Map<String, String> properties = new HashMap<>();
	private static final Log log = LogFactory.getLog(ConfigUtilsTest.class);
	static {
		properties.putAll(propertyMap("mas-default.properties", true));
//		properties.putAll(propertyMap("jdbc.properties", true));
	}
	public static Map<String, String> propertyMap(String name, boolean isTrim){
		String newName = ensureStartWith(name, "/");
		InputStream is = ConfigUtilsTest.class.getResourceAsStream(newName);
		Properties p = new Properties();
		try {
			p.load(is);
			for(Map.Entry<Object, Object> entry : p.entrySet()){
				if(isTrim){
					 map.put(entry.getKey().toString(), entry.getValue().toString());
				}else{
					map.put(entry.getKey().toString().trim(), entry.getValue().toString().trim());
				}
			}
			return map;
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	public static void main(String[] args) {
		log.debug((String)properties.get("redis.lruHost") + ":" + Integer.parseInt(properties.get("redis.lruPort")));
	}
	
	public static String ensureStartWith(String name, String head){
		return name.startsWith(head) ? name : head + name;
	}
	
}
