package qileke.jer.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esotericsoftware.reflectasm.MethodAccess;

public class Actor {
	public Object object;
	public final Map<String, Integer> methodIndex = new HashMap<String, Integer>();
	private static final Class<?>[] REQUEST_ARGS = new Class<?>[]{HttpServletRequest.class, HttpServletResponse.class};

	public Actor(Object object) {
		this.object = object;
		for (Method method : object.getClass().getMethods()) {
			if (0 != (method.getModifiers() & Modifier.STATIC)) {
				continue;
			}
			if(!Arrays.equals(method.getParameterTypes(), REQUEST_ARGS)){
				continue;
			}
			try {
				String methodName = method.getName();
				methodIndex.put(methodName,
						MethodAccess.get(object.getClass()).getIndex(methodName, HttpServletRequest.class, HttpServletResponse.class));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}

	public void act(String method, Object... args) throws ActionException {
		if (!methodIndex.containsKey(method)) {
			throw new ActionException("no service for this method:" + method);
		}
		MethodAccess.get(object.getClass()).invoke(object, methodIndex.get(method), args);
	}
}
