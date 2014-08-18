package cn.zthz.tool.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassUtils {

	public static Map<Class<?>, List<Object>> classify(Iterable<?> objects){
		Map<Class<?>, List<Object>> result = new HashMap<Class<?>, List<Object>>();
		for (Object object : objects) {
			if(null == object){
				continue;
			}
			Class<?> clazz = object.getClass();
			if(result.containsKey(clazz)){
				result.get(clazz).add(object);
			}else{
				List<Object> oneClassObjects = new LinkedList<>();
				oneClassObjects.add(object);
				result.put(clazz, oneClassObjects);
			}
		}
		return result;
	}
}
