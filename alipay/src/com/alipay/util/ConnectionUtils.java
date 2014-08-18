package com.alipay.util;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionUtils {
	private static final Log log = LogFactory.getLog(ConnectionUtils.class);

	public static void rollback(Connection connection) {
		try {
			if(null!=connection){
				connection.rollback();
			}
		} catch (SQLException e) {
			log.error("failed to rollback at connection:"+connection, e);
		}
	}
	public static void closeConnection(Connection connection) {
		try {
			if (null != connection) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error("failed to close connection", e);
		}
	}

	public static void closeStatement(Statement statement) {
		try {
			if (null != statement) {
				statement.close();
			}
		} catch (SQLException e) {
			log.error("failed to close statement", e);
		}
	}
	
	public static void closeStatement(NamedParameterStatement statement) {
		try {
			if (null != statement) {
				statement.close();
			}
		} catch (SQLException e) {
			log.error("failed to close statement", e);
		}
	}

}
