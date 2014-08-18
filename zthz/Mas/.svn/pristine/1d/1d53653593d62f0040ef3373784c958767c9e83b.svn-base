package cn.zthz.tool.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ResultSetMap {
	public static Map<String, Object> map(ResultSet resultSet) throws SQLException {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		if (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				result.put(metaData.getColumnLabel(i), resultSet.getObject(i));
			}
		}
		return result;
	}

	public static Object mapSingle(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			return resultSet.getObject(1);
		}
		return null;
	}

	public static Object mapObject(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			return resultSet.getObject(1);
		} else {
			return null;
		}
	}

	public static int mapInt(ResultSet resultSet) throws SQLException {
		resultSet.next();
		return resultSet.getInt(1);
	}
	public static BigDecimal mapBigDecimal(ResultSet resultSet) throws SQLException {
		resultSet.next();
		return resultSet.getBigDecimal(1);
	}

	public static List<Map<String, Object>> maps(ResultSet resultSet, int fetchSize) throws SQLException {
		resultSet.setFetchSize(fetchSize);
		List<Map<String, Object>> result = new LinkedList<>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		Map<String, Object> record = null;
		while (resultSet.next()) {
			record = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				record.put(metaData.getColumnLabel(i), resultSet.getObject(i));
			}
			result.add(record);
		}
		return result;
	}

	public static List<Map<String, Object>> maps(ResultSet resultSet) throws SQLException {
		return maps(resultSet, 10);
	}

	public static Map<Object, Map<String, Object>> map(ResultSet resultSet, String keyField) throws SQLException {
		Map<Object, Map<String, Object>> result = new LinkedHashMap<>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		Map<String, Object> record = null;
		Object key = null;
		while (resultSet.next()) {
			record = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				String label = metaData.getColumnLabel(i);
				if (keyField.equals(label)) {
					key = resultSet.getObject(i);
					record.put(label, key);
					continue;
				}
				record.put(metaData.getColumnLabel(i), resultSet.getObject(i));
			}
			result.put(key, record);
		}
		return result;
	}


}
