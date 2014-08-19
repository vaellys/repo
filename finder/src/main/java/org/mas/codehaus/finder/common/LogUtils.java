package org.mas.codehaus.finder.common;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class LogUtils {
	
	/**
	 * 
	 * @param object name,value
	 * @return
	 */
	public static String format(Object... args){
		StringBuilder message = new StringBuilder();
		for (int i = 0 ; i < args.length;i++) {
			message.append(args[i]);
			message.append(':');
			i++;
			if(i>=args.length){
				break;
			}
			message.append(args[i] instanceof String ? args[i]:JSON.toJSONString(args[i],SerializerFeature.WriteDateUseDateFormat ,SerializerFeature.PrettyFormat));
			message.append('\n');
		}
		message.deleteCharAt(message.length()-1);
		return message.toString();
	}
	public static void main(String[] args) {
		List<Object> os = new ArrayList<Object>();
		os.add(new Date());
		System.out.println(format("os" ,os));
	}

}
