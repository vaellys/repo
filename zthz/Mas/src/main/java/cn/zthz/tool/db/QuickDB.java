package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyleContext.NamedStyle;

import cn.zthz.tool.requirement.ConnectionUtils;

public class QuickDB {
	
	public static Object getSingle(String sql) throws SQLException {
		Connection connection = Connections.instance.get();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return ResultSetMap.mapSingle(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public static Map<String, Object> get(String sql) throws SQLException {
		Connection connection = Connections.instance.get();
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
	public static Map<String, Object> getV(String sql , Object... args) throws SQLException {
		Connection connection = Connections.instance.get();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				statement.setObject(i, args[i]);
			}
			ResultSet resultSet = statement.executeQuery();
			return ResultSetMap.map(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	public static Map<String, Object> getM(String sql , Map<String, Object> args) throws SQLException {
		Connection connection = Connections.instance.get();
		NamedParameterStatement statement = null;
		try {
			statement = new NamedParameterStatement(connection, sql);
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				statement.setObject(entry.getKey() , entry.getValue());
			}
			ResultSet resultSet = statement.executeQuery();
			return ResultSetMap.map(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	public static List<Map<String, Object>> getsV(String sql , Object... args) throws SQLException {
		Connection connection = Connections.instance.get();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				statement.setObject(i, args[i]);
			}
			ResultSet resultSet = statement.executeQuery();
			return ResultSetMap.maps(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	public static List<Map<String, Object>> getsM(String sql , Map<String, Object> args) throws SQLException {
		Connection connection = Connections.instance.get();
		NamedParameterStatement statement = null;
		try {
			statement = new NamedParameterStatement(connection, sql);
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				statement.setObject(entry.getKey() , entry.getValue());
			}
			ResultSet resultSet = statement.executeQuery();
			return ResultSetMap.maps(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	public static Map<Object , Map<String, Object>> getMM(String sql , Map<String, Object> args , String key) throws SQLException {
		Connection connection = Connections.instance.get();
		NamedParameterStatement statement = null;
		try {
			statement = new NamedParameterStatement(connection, sql);
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				statement.setObject(entry.getKey() , entry.getValue());
			}
			ResultSet resultSet = statement.executeQuery();
			return ResultSetMap.map(resultSet , key);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public static List<Map<String, Object>> gets(String sql) throws SQLException {
		Connection connection = Connections.instance.get();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return ResultSetMap.maps(resultSet);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public static int update(String sql) throws SQLException {
		Connection connection = Connections.instance.get();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public static int update(String sql , Map<String, Object> args) throws SQLException {
		Connection connection = Connections.instance.get();
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
	
	public static int insert(String sql) throws SQLException {
		Connection connection = Connections.instance.get();
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
		Connection connection = Connections.instance.get();
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
	public static int insert(String sql , Map<String , Object> args) throws SQLException {
		Connection connection = Connections.instance.get();
		NamedParameterStatement statement = null;
		try {
			statement = new NamedParameterStatement(connection , sql);
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				statement.setObject(entry.getKey() , entry.getValue());
			}
			return statement.executeUpdate();
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

}
