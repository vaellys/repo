package cn.zthz.tool.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.common.Tuple;
import cn.zthz.tool.user.User;

public class ObjectTableInfo {
	public static String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (null == table) {
			return clazz.getSimpleName();
		} else {
			return table.name();
		}
	}

	public static List<String> getFieldNames(Class<?> clazz) {
		List<String> result = new LinkedList<>();
		String name = null;
		for (Field field : clazz.getDeclaredFields()) {
			name = getFieldName(field);
			if (null != name) {
				result.add(name);
			}
		}
		return result;
	}
	
	public static boolean isIgnoreField(Field field){
		cn.zthz.tool.db.Ignore ignoreAnnotaion = field.getAnnotation(cn.zthz.tool.db.Ignore.class);
		if (null != ignoreAnnotaion) {
			return true;
		}else{
			return false;
		}
	}
	public static boolean isQueryField(Field field){
		QueryField queryAnnotaion = field.getAnnotation(QueryField.class);
		if (null != queryAnnotaion) {
			return true;
		}else{
			return false;
		}
	}

	public static Map<String, Object> getObjectValues(Object object) {
		if (null == object) {
			return null;
		}
		Map<String, Object> result = new HashMap<>();
		Class<?> clazz = object.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			try {
				if(isIgnoreField(field)){
					continue;
				}
				field.setAccessible(true);
				result.put(getFieldName(field), field.get(object));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static Tuple<String, Class<?>> getFieldNameAndType(Field field) {
		cn.zthz.tool.db.Field fieldAnnotation = field.getAnnotation(cn.zthz.tool.db.Field.class);
		String name = field.getName();
		Class<?> type = field.getType();
		if (null != fieldAnnotation) {
			if (StringUtils.isNotEmpty(fieldAnnotation.name())) {
				name = fieldAnnotation.name();
			}
			if (!Object.class.equals(fieldAnnotation.type())) {
				type = fieldAnnotation.type();
			}
		}
		return new Tuple<String, Class<?>>(name, type);
	}

	public static String getFieldName(Field field) {
		if (isIgnoreField(field)) {
			return null;
		}
		cn.zthz.tool.db.Field fieldAnnotation = field.getAnnotation(cn.zthz.tool.db.Field.class);
		String name = field.getName();
		if (null != fieldAnnotation) {
			if (StringUtils.isNotEmpty(fieldAnnotation.name())) {
				name = fieldAnnotation.name();
			}
		}
		return name;
	}

	public static String getIdName(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Id idAnnotation = field.getAnnotation(Id.class);
			if (null != idAnnotation) {
				if (StringUtils.isEmpty(idAnnotation.name())) {
					return field.getName();
				} else {
					return idAnnotation.name();
				}
			}
		}
		for (Field field : fields) {
			if ("id".equals(field.getName())) {
				return "id";
			}
		}
		throw new RuntimeException("no id field!");
	}

	public static void main(String[] args) {
		System.out.println(getIdName(User.class));
	}
}
