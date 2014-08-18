package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.JsonUtils;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.mysql.jdbc.Driver;

public class Connections {
	public static final Connections instance = new Connections();
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("no jdbc Driver");
		}
	}
	private BoneCP boneCP;

	private Connections() {
		init();
	}

	public void init() {
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(GlobalConfig.get("mysql.url"));
		config.setUsername(GlobalConfig.get("mysql.username"));
		config.setPassword(GlobalConfig.get("mysql.password"));
		// config.setIdleConnectionTestPeriodInSeconds(GlobalConfig.getLong("bonecp.idleConnectionTestPeriod",240L));
		// config.setIdleMaxAgeInSeconds(GlobalConfig.getLong("bonecp.idleMaxAge"
		// ,60L));
		config.setMaxConnectionsPerPartition(GlobalConfig.getInt("bonecp.maxConnectionsPerPartition", 30));
		// config.setMinConnectionsPerPartition(GlobalConfig.getInt("bonecp.minConnectionsPerPartition"
		// ,1));
		// config.setPartitionCount(GlobalConfig.getInt("bonecp.partitionCount"
		// ,3));
		// config.setAcquireIncrement(GlobalConfig.getInt("bonecp.acquireIncrement"
		// ,2));
		// config.setStatementsCacheSize(GlobalConfig.getInt("bonecp.statementsCacheSize"
		// , 20));
		// config.setReleaseHelperThreads(GlobalConfig.getInt("bonecp.releaseHelperThreads"
		// ,3));
		System.out.println("config:" + config);
		try {
			boneCP = new BoneCP(config);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public Connection get() throws SQLException {
		Connection connection = boneCP.getConnection();
		connection.setAutoCommit(true);
		return connection;
	}

	public BoneCP getBoneCP() {
		return boneCP;
	}

}
