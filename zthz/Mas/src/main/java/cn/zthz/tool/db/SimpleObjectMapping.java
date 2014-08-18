package cn.zthz.tool.db;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.StringUtils;

public class SimpleObjectMapping implements RowObjectMapping {
	private static final Log log = LogFactory.getLog(SimpleObjectMapping.class);
	
	public static final SimpleObjectMapping instance = new SimpleObjectMapping();
	
	@Override
	public <T> T mapping(ResultSet resultSet, Class<T> clazz) throws SQLException {
		Field[] fields = clazz.getDeclaredFields();
		T result = null;
		try {
			result = clazz.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				cn.zthz.tool.db.Field fieldAnnation = field.getAnnotation(cn.zthz.tool.db.Field.class);
				if(ObjectTableInfo.isIgnoreField(field) && !ObjectTableInfo.isQueryField(field)){
					continue;
				}
				String fieldName = null;
				if (null != fieldAnnation) {
					fieldName = fieldAnnation.name();
				}
				if (StringUtils.isEmpty(fieldName)) {
					fieldName = field.getName();
				}
				try{
				field.set(result, resultSet.getObject(fieldName));
				}catch (Exception e) {
					log.warn("" ,e);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SQLException(e);
		}
		return result;
	}

}
