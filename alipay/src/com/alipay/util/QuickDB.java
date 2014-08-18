package com.alipay.util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.alipay.dao.base.BaseDao;

public class QuickDB extends BaseDao{
	
	public QuickDB() throws Exception {
		super();
	}


	public static int update(String sql) throws SQLException {
		Connection connection = getConnection();
		Statement statement = null;
		try {
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			int c = statement.executeUpdate(sql);
			connection.commit();
			return c;
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	
	public static int insert(String sql) throws SQLException {
		Connection connection = getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	public static int insert(String sql , Object... args) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				statement.setObject(i+1, args[i]);
			}
			return statement.executeUpdate();
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public static Map<String, Object> get(String sql) throws SQLException {
		Connection connection = getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return ResultSetMap.map(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public static int update(String sql , Map<String, Object> args) throws SQLException {
		Connection connection = getConnection();
		NamedParameterStatement statement = null;
		try {
			statement = new NamedParameterStatement(connection, sql);
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				statement.setObject(entry.getKey(), entry.getValue());
			}
			return statement.executeUpdate();
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
}
