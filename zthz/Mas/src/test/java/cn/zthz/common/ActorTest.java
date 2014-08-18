package cn.zthz.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esotericsoftware.reflectasm.MethodAccess;

public class ActorTest {
	private Object object;
	public static final Map<String, Integer> methodIndex = new HashMap<>();
	public static final Class<?>[] PARAMETERS_TYPE = new Class[]{HttpServletRequest.class, HttpServletResponse.class};
	
	public ActorTest(Object object){
		this.object = object;
		for(Method method : object.getClass().getMethods()){
			if(1 == (method.getModifiers() & Modifier.STATIC)){
				continue;
			}
			if(!Arrays.equals(method.getParameterTypes(), PARAMETERS_TYPE)){
				continue;
			}
			String methodName = method.getName();
			try{
				methodIndex.put(methodName, MethodAccess.get(object.getClass()).getIndex(methodName, PARAMETERS_TYPE));
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void act(String methodName, Object... args){
		MethodAccess.get(object.getClass()).invoke(object, methodName,args);
	}
}
