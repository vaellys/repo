package cn.zthz.tool.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import cn.zthz.tool.account.AccountService;

import com.alibaba.fastjson.JSON;

public class ConfigUtils {
	/**
	 * 
	 * @param fileName
	 *            filename in the classpath
	 * @return
	 */
	public static Map<String, String> getProperties(String fileName) {
		return getProperties(fileName, true);
	}

	public static Map<String, String> getProperties(String name, boolean isTrim) {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = getInputStreamFromClasspath(name);
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (isTrim) {
					map.put(entry.getKey().toString().trim(), entry.getValue().toString().trim());
				} else {
					map.put(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			return map;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static InputStream getInputStreamFromClasspath(String name) {
		return ConfigUtils.class.getResourceAsStream(StringUtils.ensureStartWith(name, "/"));
	}

	public static String getStringFileFromClasspath(String name) {
		InputStream inputStream = getInputStreamFromClasspath(name);
		Reader reader = new InputStreamReader(inputStream);
		StringBuilder builder = new StringBuilder();

		// File file = getFileFromClasspath(name);
		// byte[] buffer = new byte[(int) file.length()];
//		InputStream input = null;
		try {
			int c = -1;
			while (-1 != (c = reader.read())) {
				builder.append((char) c);
			}
			return builder.toString();
			// input = new FileInputStream(file);
			// IOUtils.read(input, buffer);
			// return new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static File getFileFromClasspath(String name) {
		return new File(ConfigUtils.class.getResource(StringUtils.ensureStartWith(name, "/")).getFile());
	}

	public static String get(Map<String, String> config, String key) {
		return config.get(key);
	}

	public static String[] gets(Map<String, String> config, String key, String seperatorRegx) {
		String[] values = config.get(key).split(seperatorRegx);
		String[] result = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = values[i].trim();
		}
		return result;
	}

	public static Map<String, String> getMap(Map<String, String> config, String key, String entrySeperatorRegx, String kvSeperatorRegx) {
		Map<String, String> result = new LinkedHashMap<>();
		String[] values = config.get(key).split(entrySeperatorRegx);
		for (String value : values) {
			if (StringUtils.isBlank(value)) {
				continue;
			}
			String[] kv = value.split(kvSeperatorRegx);
			if (kv.length <= 0) {
				continue;
			}
			if (kv.length == 1) {
				result.put(kv[0], null);
			}
			result.put(kv[0], kv[1]);
		}
		return result;
	}

	public static Map<String, String> getMap(Map<String, String> config, String key) {
		return getMap(config, key, "\\,", "\\-\\>");
	}

	public static Integer getInt(Map<String, String> config, String key) {
		String value = config.get(key);
		return StringUtils.isEmpty(value) ? 0 : Integer.valueOf(value);
	}

	public static Long getLong(Map<String, String> config, String key) {
		String value = config.get(key);
		return StringUtils.isEmpty(value) ? 0L : Long.valueOf(value);
	}

	public static Tuple<String, String> getTuple(String kv) {
		return getTuple(kv, "\\-\\>");
	}

	public static Tuple<String, String> getTuple(String kv, String seperatorRegx) {
		Tuple<String, String> tuple = new Tuple<>();
		String[] words = kv.split(seperatorRegx);
		if (words.length == 1) {
			tuple.key = words[0];
		}
		if (words.length >= 2) {
			tuple.key = words[0];
			tuple.key = words[1];
		}
		return tuple;
	}

}
