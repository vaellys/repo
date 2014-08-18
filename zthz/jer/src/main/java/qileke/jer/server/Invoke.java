package qileke.jer.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import qileke.jer.utils.StringUtils;

public class Invoke {
	/**
	 * {path : Actor}
	 */
	public final Map<String, Actor> pathActorMap = new HashMap<>();

	public void load(String... files) {
		List<String> fileList = new ArrayList<>(files.length);
		for (String string : files) {
			fileList.add(string);
		}
		load(fileList);
	}

	public void load(List<String> files) {
		Properties properties = new Properties();
		for (String name : files) {
			InputStream inputStream = null;
			try {
				String str = StringUtils.ensureStartWith(name, "/");
				inputStream = this.getClass().getResourceAsStream(StringUtils.ensureStartWith(name, "/"));
				properties.load(inputStream);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (null != inputStream)
					try {
						inputStream.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
			}
		}
		try {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String str = StringUtils.ensureSEWith(entry.getKey().toString().trim(), "/");
				pathActorMap.put(StringUtils.ensureSEWith(entry.getKey().toString().trim(), "/"), new Actor(Class.forName(entry.getValue().toString())
						.newInstance()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void invoke(String path, Object... args) throws ActionException {
		String dir = getDir(path);
		if(!pathActorMap.containsKey(dir)){
			throw new ActionException("no service for this path:"+path);
		}
		pathActorMap.get(getDir(path)).act(getMethod(path), args);
	}

	protected String getDir(String target) {
		return target.substring(0, target.lastIndexOf("/") + 1);
	}

	protected String getMethod(String target) {
		return target.substring(target.lastIndexOf("/") + 1);
	}

}
