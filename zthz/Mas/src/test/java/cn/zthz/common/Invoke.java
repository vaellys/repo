package cn.zthz.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Invoke {
	private Log log = LogFactory.getLog(Invoke.class);
	Map<String, ActorTest> pathActorMap = new HashMap<>();
	
	public void load(String... file){
		load(file);
	}
	
	public void load(List<String> files){
		Properties properties = new Properties();
		for(String file : files){
			InputStream is = this.getClass().getResourceAsStream(ConfigUtilsTest.ensureStartWith(file,"/"));
			
			try {
				properties.load(is);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
					
		}
		
		for(Map.Entry<Object, Object> entry : properties.entrySet()){
			String path = ensureStartEnWith(entry.getKey().toString(), "/");
			try {
				pathActorMap.put(path, new ActorTest(Class.forName(entry.getValue().toString()).newInstance()));
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String ensureStartEnWith(String name, String head){
		return name.startsWith(head) ? ensureEndWith(name, head) : head + ensureEndWith(name, head);
	}
	
	private String ensureEndWith(String name, String tail){
		return name.endsWith(tail) ? name : name + tail;
	}
	
	public void invoke(String path, Object... args) throws Exception{
		if(!pathActorMap.containsKey(getDirecory(path))){
			throw new Exception("");
		}
		String methodName = path.substring(path.lastIndexOf("/") + 1);
		pathActorMap.get(getDirecory(path)).act(methodName, args);
	}
	
	public static void main(String[] args) {
		Invoke invoke = new Invoke();
		List<String> list = new ArrayList<>();
		list.add("mas-map.properties");
		invoke.load(list);
		
	}
	
	private String getDirecory(String path){
		return path.substring(0, path.lastIndexOf("/") + 1);
	}
	
}
