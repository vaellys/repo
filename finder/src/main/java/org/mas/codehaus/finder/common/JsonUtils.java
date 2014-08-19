package org.mas.codehaus.finder.common;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtils {

	/**
	 * for render to view!
	 * 
	 * @param object
	 * @return
	 */
	public static String toJsonString(Object object) {
		SerializeConfig config = new SerializeConfig();
		config.put(Date.class, new ObjectSerializer() {
			
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
				serializer.write(DateUtils.format((Date)object));
			}
		});
		config.put(Time.class, new ObjectSerializer() {
			
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
				serializer.write(DateUtils.format((Time)object));
			}
		});
		config.put(Timestamp.class, new ObjectSerializer() {
			
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
				serializer.write(DateUtils.format((Timestamp)object));
			}
		});
		String json = JSON.toJSONString(object ,config,  SerializerFeature.WriteNullListAsEmpty , SerializerFeature.WriteNullStringAsEmpty , SerializerFeature.WriteDateUseDateFormat);
		return json;
//		return formatString(json);
	}
	
	public static void main(String[] args) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("k1", new Date(System.currentTimeMillis()));
		data.put("k2", new Time(System.currentTimeMillis()));
		data.put("k3", new Timestamp(System.currentTimeMillis()));
		System.out.println(toJsonString(data));
	}

	public static String formatString(String source) {
		return controlPattern.matcher(source).replaceAll("");
	}

	public static String encode(Object object) {
		if (null == object) {
			return null;
		}
		String json = JSON.toJSONString(object, SerializerFeature.WriteClassName);
		return json;
	}

	public static Object decode(String json) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		return JSON.parse(json);
	}


	public static final List<Character> controlChars = new ArrayList<Character>(33);
	public static Pattern controlPattern;

	static {
		StringBuilder controlRegBuilder = new StringBuilder(33);
		for (int i = 0; i < 0x1F; i++) {
			controlChars.add((char) i);
			controlRegBuilder.append((char) i);
			controlRegBuilder.append("|");
		}
		controlChars.add((char) 0x7F);
		controlRegBuilder.append((char) 0x7F);
		controlPattern = Pattern.compile(controlRegBuilder.toString());
	}

}
