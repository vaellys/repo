package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.log.Log;

import cn.zthz.tool.common.ClassUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.requirement.ConnectionUtils;

public class DbOperations implements DbGeneralOperation {
	public static final DbOperations instance = new DbOperations();

	@Override
	public void save(Connection connection, Object object, boolean isAutoCommitBatch) throws SQLException {
		if (null == object) {
			return;
		}
		if (object instanceof Collection<?>) {
			connection.setAutoCommit(false);
			Collection<?> objects = (Collection<?>) object;
			Map<Class<?>, List<Object>> classifiedObjects = ClassUtils.classify(objects);
			for (Map.Entry<Class<?>, List<Object>> entry : classifiedObjects.entrySet()) {
				NamedParameterStatement namedParameterStatement = null;
				try {
					String table = ObjectTableInfo.getTableName(entry.getKey());
					List<String> names = ObjectTableInfo.getFieldNames(entry.getKey());
					String sql = SimpleSqlFactory.createNamedInsertSql(table, names);
					namedParameterStatement = new NamedParameterStatement(connection, sql);
					for (Object bean : entry.getValue()) {
						Map<String, Object> args = ObjectTableInfo.getObjectValues(bean);
						for (Map.Entry<String, Object> item : args.entrySet()) {
							namedParameterStatement.setObject(item.getKey(), item.getValue());
						}
						namedParameterStatement.addBatch();
					}
					namedParameterStatement.executeBatch();
					if (isAutoCommitBatch) {
						connection.commit();
					}
				} finally {
					if (null != namedParameterStatement) {
						namedParameterStatement.close();
					}
				}
			}
		} else {
			Class<?> clazz = object.getClass();
			String table = ObjectTableInfo.getTableName(clazz);

			List<String> names = ObjectTableInfo.getFieldNames(clazz);
			String sql = SimpleSqlFactory.createNamedInsertSql(table, names);
			System.out.println(sql);
			NamedParameterStatement namedParameterStatement = new NamedParameterStatement(connection, sql);
			Map<String, Object> args = ObjectTableInfo.getObjectValues(object);
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
			namedParameterStatement.execute();
			namedParameterStatement.close();
		}

	}

	@Override
	public void delete(Connection connection, Object id) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public int update(Connection connection, String table, Map<String, Object> conditionMap, Map<String, Object> newProperties) throws SQLException {
		if(null == conditionMap || conditionMap.isEmpty() || null == newProperties || newProperties.isEmpty()){
			return 0;
		}
		String sql = SimpleSqlFactory.createNamedUpdateSql(table, newProperties.keySet(), conditionMap.keySet());
		NamedParameterStatement namedParameterStatement = new NamedParameterStatement(connection, sql);
		for (Map.Entry<String, Object> entry : conditionMap.entrySet()) {
			namedParameterStatement.setObject(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, Object> entry : newProperties.entrySet()) {
			namedParameterStatement.setObject(entry.getKey(), entry.getValue());
		}
		int result= namedParameterStatement.executeUpdate();
		ConnectionUtils.closeStatement(namedParameterStatement);
		return result;
	}

	@Override
	public <T> T get(Connection connection, Object id, Class<T> clazz)throws SQLException {
		LinkedList<String> conditionNames = new LinkedList<>();
		String idName = ObjectTableInfo.getIdName(clazz);
		conditionNames.add(idName);
		String sql = SimpleSqlFactory.createNamedQuerySql(ObjectTableInfo.getTableName(clazz), null, conditionNames );
		int fetchSize = 1;
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(idName, id);
		RowObjectMapping mapping = new SimpleObjectMapping();
		List<T> result = query(connection, sql,args ,clazz, mapping , fetchSize);
		if(null == result || result.isEmpty()){
			return null;
		}else{
			return result.get(0);
		}
	}

	@Override
	public <T> List<T> gets(Connection connection, Collection<T> ids, Class<T> clazz) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeSql(Connection connection, String sql, Object... args) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		if (null != args) {
			for (int i = 1; i <= args.length; i++) {
				statement.setObject(i, args[i]);
			}
		}
		statement.execute();
		statement.close();
	}
	@Override
	public int executeUpdate(Connection connection, String sql, Object... args) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		if (null != args) {
			for (int i = 1; i <= args.length; i++) {
				statement.setObject(i, args[i]);
			}
		}
		int result = statement.executeUpdate();
		statement.close();
		return result;
	}

	@Override
	public <T> List<T> getBySql(Connection connection, String sql, Class<T> clazz, Map<String, Object> args) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> getBySql(Connection connection, String sql, Class<T> clazz, Object... args) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> query(Connection connection, String sql, RowObjectMapping mapping,  Class<T> clazz,int fetchSize, Object... args)
			throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		if (null != args) {
			for (int i = 1; i <= args.length; i++) {
				preparedStatement.setObject(i, args[i]);
			}
		}
		ResultSet resultSet = preparedStatement.executeQuery();
		List<T> result = new LinkedList<>();
		if (null == resultSet) {
			return result;
		}
		while (resultSet.next()) {
			result.add(mapping.mapping(resultSet ,clazz));
		}
		preparedStatement.close();
		return result;
	}

	@Override
	public <T> List<T> query(Connection connection, String sql, Map<String, Object> args, Class<T> clazz, RowObjectMapping mapping, int fetchSize)
			throws SQLException {
		NamedParameterStatement namedParameterStatement = new NamedParameterStatement(connection, sql);
		if (null != args) {
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
		}
		ResultSet resultSet = namedParameterStatement.executeQuery();
		List<T> result = new LinkedList<>();
		if (null == resultSet) {
			return result;
		}
		resultSet.setFetchSize(fetchSize);
		while (resultSet.next()) {
			result.add(mapping.mapping(resultSet , clazz));
		}
		namedParameterStatement.close();
		return result;
	}
	
	public void grid() throws SQLException{
		ResultSet resultSet = null;
		ResultSetMetaData metaData = resultSet.getMetaData();
		
	}

	@Override
	public void executeSql(Connection connection, String sql, Map<String, Object> args) throws SQLException {
		NamedParameterStatement namedParameterStatement = new NamedParameterStatement(connection, sql);
		if (null != args) {
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
		}
		namedParameterStatement.close();
	}

}
