package qileke.jer.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class StringUtils {
	
	public static String ensureStartWith(String src , String head){
		return src.startsWith(head)?src : head + src;
	}
	
	public static String ensureEndWith(String src , String tail){
		return src.endsWith(tail)?src : src+tail;
	}
	
	public static String ensureSEWith(String src , String string){
		return ensureStartWith(ensureEndWith(src , string), string);
	}
	
}
