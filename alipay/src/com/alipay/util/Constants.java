package com.alipay.util;

public final class Constants {
	
	private static Configuration rc = new Configuration("/configuration.properties");
	
	public static String getJdbcUrl() {
		return rc.getValue("jdbc.url");
	}
	public static String getJdbcUsername() {
		return rc.getValue("jdbc.username");
	}
	public static String getJdbcPassword() {
		return rc.getValue("jdbc.password");
	}
}
