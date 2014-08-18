package com.alipay.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang.StringUtils {

	public static String formatCharsetName(String charset) {
		if ("utf-8".equalsIgnoreCase(charset)) {
			return "utf8";
		}

		return charset;
	}
	
	public static int compare(String string1, String string2) {
		if (!isBlank(string1) && isBlank(string2)) {
			return -1;
		}
		if (isBlank(string1) && !isBlank(string2)) {
			return 1;
		}
		if (isBlank(string1) && isBlank(string2)) {
			return 0;
		}

		return string1.compareTo(string2);
	}

	public static List<String> mergeTags(String... tags) {
		List<String> result = new LinkedList<String>();
		for (String tag : tags) {
			result.add(tag);
		}
		return mergeTags(result);

	}

	public static List<String> mergeTags(List<String> tags) {
		List<String> result = new LinkedList<String>();
		for (String tag : tags) {
			String[] splittedTags = tag.split(",|，");
			for (String string : splittedTags) {
				if (!result.contains(string)) {
					String t = string.trim();
					if (!"".equals(t)) {
						result.add(string.trim());
					}
				}
			}
		}
		return result;
	}

	public static String getSuffix(String name) {
		if (null == name) {
			return null;
		}
		int postion = name.lastIndexOf('.');
		if (-1 == postion || name.length() - 1 == postion) {
			return null;
		} else {
			return name.substring(postion + 1);
		}
	}

	public static String link(String seperator, String... strings) {
		if (null == strings || 0 == strings.length) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			if (org.apache.commons.lang.StringUtils.isBlank(string)) {
				continue;
			}
			builder.append(string);
			builder.append(seperator);
		}
		if (builder.length() > 0) {
			builder.delete(builder.length() - seperator.length(), builder.length());
		}
		return builder.toString();
	}
	

	public static String link(Iterable<?> strings, String seperator) {
		if (null == strings ) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Object string : strings) {
			if (null == string || org.apache.commons.lang.StringUtils.isBlank(string.toString())) {
				continue;
			}
			builder.append(string);
			builder.append(seperator);
		}
		if (builder.length() > 0) {
			builder.delete(builder.length() - seperator.length(), builder.length());
		}
		return builder.toString();
	}
	
	public static String getSuffix(String name, String seperator) {
		int postion = name.lastIndexOf(seperator);
		if (-1 == postion || name.length() - 1 == postion) {
			return null;
		} else {
			return name.substring(postion + seperator.length());
		}
	}

	public static String ensureNotStartWith(String src, String head) {
		return src.startsWith(head) ? src.substring(src.indexOf(head) + head.length()) : src;
	}

	public static String ensureNotEndWith(String src, String tail) {
		return src.endsWith(tail) ? src.substring(0, src.lastIndexOf(tail)) : src;
	}

	public static String ensureStartWith(String src, String head) {
		return src.startsWith(head) ? src : head + src;
	}

	public static String ensureEndWith(String src, String tail) {
		return src.endsWith(tail) ? src : src + tail;
	}

	public static String ensureSEWith(String src, String string) {
		return ensureStartWith(ensureEndWith(src, string), string);
	}

	public static final Pattern DECIMAL_PATTERN = Pattern.compile("\\d+");

	public static int getDecimal(String target) {
		Matcher matcher = DECIMAL_PATTERN.matcher(target);
		StringBuilder builder = new StringBuilder(target.length());
		while (matcher.find()) {
			builder.append(matcher.group());
		}
		if (0 != builder.length()) {
			return Integer.valueOf(builder.toString());
		} else {
			return 0;
		}
	}

	public static String ellipsisString(String source, int length) {
		return source.length() > length ? source.substring(0, length)+"..." : source;
	}
	public static String omitString(String source, int length) {
		return source.length() > length ? source.substring(0, length) : source;
	}
	
	public static final Pattern FLOAT_PATTERN = Pattern.compile("\\d+\\.?\\d*$|-\\d+\\.?\\d*$");//判断是否为小数
	public static final Pattern INT_PATTERN = Pattern.compile("^-?\\d+$");//判断是否为小数

	public static boolean isFloat(String str) {
		if(null == str){
			return false;
		}
		return FLOAT_PATTERN.matcher(str).matches();
	}
	public static boolean isInt(String string){
		if(null == string){
			return false;
		}
		return INT_PATTERN.matcher(string).matches();
		
	}
}
