package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.requirement.ConnectionUtils;

public class TestConnections {

	@Test
	public void testGet() throws SQLException {
		Connection connection = Connections.instance.get();

		System.out.println("connection:" + connection);
		Statement statement = connection.createStatement();
		PreparedStatement sm = connection.prepareStatement("update Requirement set expire=?");
		sm.setTimestamp(1, new Timestamp(System.currentTimeMillis() + 24L * 3600 * 1000 * 30));
		sm.executeUpdate();
		// ResultSetMetaData meta = rs.getMetaData();
		// System.out.println(LogUtils.format("meta",meta));
		// int columnCount = meta.getColumnCount();
		// long t1 = System.currentTimeMillis();
		// for(int i = 1 ; i <= columnCount ;i++){
		// System.out.println("getColumnLabel:"+meta.getColumnLabel(i));
		// System.out.println("getColumnName:"+meta.getColumnName(i));
		// System.out.println("getColumnDisplaySize:"+meta.getColumnDisplaySize(i));
		// System.out.println("getCatalogName:"+meta.getCatalogName(i));
		// System.out.println("getColumnClassName:"+meta.getColumnClassName(i));
		// System.out.println("getColumnType:"+meta.getColumnType(i));
		// System.out.println("getSchemaName:"+meta.getSchemaName(i));
		// System.out.println("getTableName:"+meta.getTableName(i));
		// System.out.println("============");
		//
		// }
		// long t2 = System.currentTimeMillis();
		// System.out.println("time cost:"+(t2-t1)+"ms");
		// Map<String, Object> result = new HashMap<String, Object>();
		// if(rs.next()){
		// for(int i = 1 ; i <= columnCount ;i++){
		// result.put(meta.getColumnLabel(i), rs.getObject(i));
		// }
		// }
		// System.out.println(result);
		statement.close();
		connection.close();
	}

	@Test
	public void testConnections() throws SQLException {
		for (int i = 0; i < 1000; i++) {

			Connection connection = Connections.instance.get();
			System.out.println(connection.getAutoCommit());
			connection.setAutoCommit(false);
			ConnectionUtils.closeConnection(connection);
//			connection.close();
		}

	}
}
