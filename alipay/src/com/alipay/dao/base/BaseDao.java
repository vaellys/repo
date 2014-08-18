package com.alipay.dao.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.alipay.util.Constants;

public class BaseDao {
	
	private static final String url = Constants.getJdbcUrl();
	private static final String username = Constants.getJdbcUsername();
	private static final String password = Constants.getJdbcPassword();
	
	public BaseDao() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	
}
