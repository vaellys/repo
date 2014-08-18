package qileke.jer.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassUtils {
	
	public static interface ClassFilter {
		boolean filter(Class<?> clazz);
	}
	
	public static List<Class<?>> getClasses(String packageName , ClassFilter filter) throws IOException  {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replaceAll("\\.", "/");
		Enumeration<URL> resources = classLoader.getResources(path);
		List<Class<?>> result = new LinkedList<>();
		while(resources.hasMoreElements()){
//			if(filter.filter(null)){
//				
//			}
			String target = resources.nextElement().getFile();
			System.out.println(target);
			File file = new File(target);
//			System.out.println(resources.nextElement().getFile());
		}
		
		return null;
	}
	
//	public static Map<String, List<Method>> getClassMethods(Class<?> clazz){
//		Map<String, List<Method>> result = new HashMap<String, List<Method>>();
//		Class<?> currentClass = clazz;
//		do{
//			
//			for(Method method : clazz.getMethods()){
//				if(result.containsKey(method.getName())){
//					result.get(method.getName()).add(method);
//				}else{
//					List<Method> value = new LinkedList<>();
//					value.add(method);
//					result.put(method.getName(), value );
//				}
//			}
//		}
//		while(true);
////		return null;
//		
//	}
	
	public static Method getMethod(Class<?> clazz ,String methodName){
		for(Method method : clazz.getMethods()){
			if(method.getName().equals(methodName)){
				return method;
			}
		}
		return null;
	}
	
	public  void hello(String name ){
		System.out.println("hello "+name );
	}
	
	public static Object invoke(Object object , String method , Class<?>[] paramTypes , Object[] params){
		try {
			return object.getClass().getMethod(method, (null == paramTypes || 0==paramTypes.length)?null :paramTypes).invoke(object, params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static void main(String[] args) throws Exception{
//		System.out.println(invoke(new ClassUtils(), "hashCode", null , null));
	}

}
