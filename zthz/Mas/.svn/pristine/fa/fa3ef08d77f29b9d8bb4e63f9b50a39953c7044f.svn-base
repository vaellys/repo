package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import cn.zthz.tool.common.StringUtils;

public class DbMapOperations {

	public static final DbMapOperations instance = new DbMapOperations();

	public void saveMaps(Connection connection, String table, List<Map<String, Object>> data) throws SQLException {
		if(null == data || data.isEmpty()){
			return;
		}
		String sql = SimpleSqlFactory.createNamedInsertSql(table, data.get(0).keySet());
		NamedParameterStatement namedParameterStatement = null;
		try {
			namedParameterStatement = new NamedParameterStatement(connection, sql);
			for (Map<String, Object> item : data) {
				for (Map.Entry<String, Object> entry : item.entrySet()) {
					namedParameterStatement.setObject(entry.getKey(), entry.getValue());
				}
				namedParameterStatement.addBatch();
			}
			namedParameterStatement.executeBatch();
		} finally {
			if (null != namedParameterStatement) {
				namedParameterStatement.close();
			}
		}
	}

	
	public void saveMap(Connection connection, String table, Map<String, Object> data) throws SQLException {
		String sql = SimpleSqlFactory.createNamedInsertSql(table, data.keySet());
		NamedParameterStatement namedParameterStatement = null;
		try {
			namedParameterStatement = new NamedParameterStatement(connection, sql);
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
			namedParameterStatement.executeUpdate();
		} finally {
			if (null != namedParameterStatement) {
				namedParameterStatement.close();
			}
		}
	}

	public Map<String, Object> getMap(Connection connection, String table, String id, String... columns) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(StringUtils.join(columns, ","));
		sql.append(" from ");
		sql.append(table);
		sql.append(" where id=");
		if (id instanceof String) {
			sql.append('\'');
			sql.append(id);
			sql.append('\'');
		} else {
			sql.append(id);
		}
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql.toString());
			return ResultSetMap.map(resultSet);
		} finally {
			if (null != statement) {
				statement.close();
			}
		}
	}

	public Map<String, Object> getMap(Connection connection, String table, Object id, List<String> columns) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(StringUtils.link(columns, ","));
		sql.append(" from ");
		sql.append(table);
		sql.append(" where id=");
		if (id instanceof String) {
			sql.append('\'');
			sql.append(id);
			sql.append('\'');
		} else {
			sql.append(id);
		}
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql.toString());
			return ResultSetMap.map(resultSet);
		} finally {
			if (null != statement) {
				statement.close();
			}
		}
	}

	public void update(Connection connection, String table, Map<String, Object> newProperties, Map<String, Object> conditionProperties)
			throws SQLException {
		String sql = SimpleSqlFactory.createNamedUpdateSql(table, newProperties.keySet(), conditionProperties.keySet());
		NamedParameterStatement namedParameterStatement = null;
		try {
			namedParameterStatement = new NamedParameterStatement(connection, sql);
			for (Map.Entry<String, Object> entry : newProperties.entrySet()) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, Object> entry : conditionProperties.entrySet()) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
			namedParameterStatement.executeUpdate();
		} finally {
			if (null != namedParameterStatement) {
				namedParameterStatement.close();
			}

		}
	}

	public void batchSaveMap(Connection connection, String table, List<Map<String, Object>> data) throws SQLException {
		NamedParameterStatement namedParameterStatement = null;
		try {
			String sql = SimpleSqlFactory.createNamedInsertSql(table, data.get(0).keySet());
			namedParameterStatement = new NamedParameterStatement(connection, sql);
			for (Map<String, Object> map : data) {
				System.out.println(map);
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					namedParameterStatement.setObject(entry.getKey(), entry.getValue());
				}
				namedParameterStatement.addBatch();
			}
			namedParameterStatement.executeBatch();
		} finally {
			if (null != namedParameterStatement) {
				namedParameterStatement.close();
			}
		}
	}
	
	

}
