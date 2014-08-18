package cn.zthz.tool.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CollectionUtils {

	public static <T> List<T> extract(List<Map<String, Object>> result, String key, boolean isFilterNull , Class<T> t) {
		List<T> list = new ArrayList<>(result.size());
		T value = null;
		for (Map<String, Object> record : result) {
			value = (T) record.get(key);
			if (isFilterNull && null == value) {
				continue;
			}
			list.add(value);
		}
		return list;
	}
	public static List<Object> extract(List<Map<String, Object>> result, String key, boolean isFilterNull) {
		List<Object> list = new ArrayList<>(result.size());
		Object value = null;
		for (Map<String, Object> record : result) {
			value = record.get(key);
			if (isFilterNull && null == value) {
				continue;
			}
			list.add(value);
		}
		return list;
	}
	public static Map<Object, List<Map<String, Object>>> classify(List<Map<String, Object>> result, String key) {
		Map<Object, List<Map<String, Object>>> map = new HashMap<Object, List<Map<String,Object>>>();
		List<Map<String, Object>> values = null;
		for (Map<String, Object> record : result) {
			Object mapKey = record.get(key);
			if(!map.containsKey(mapKey)){
				values = new LinkedList<>();
				values.add(record);
			}else{
				map.get(mapKey).add(record);
			}
			map.put(mapKey, values);
		}
		return map;
	}
	
	public static <T> Set<T> intersect(Set<T> sourceSet , Set<T> targetSet){
		Set<T> set = new HashSet<>();
		int sourceSetSize = sourceSet.size();
		int targetSetSize = targetSet.size();
		Set<T> minSet = null;
		Set<T> maxSet = null;
		if(sourceSetSize<=targetSetSize){
			minSet = sourceSet;
			maxSet = targetSet;
		}else{
			minSet = targetSet;
			maxSet = sourceSet;
			
		}
				
		for (T t : minSet) {
			if(maxSet.contains(t)){
				set.add(t);
			}
		}
		return set;
	}
	
	public static String joinMap(Map<String ,String> map , String jk ,String ji){
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> item : map.entrySet()) {
			builder.append(item.getKey());
			builder.append(jk);
			builder.append(item.getValue());
			builder.append(ji);
		}
		builder.delete(builder.length()-ji.length() , builder.length());
		
		return builder.toString();
	}
	
}
