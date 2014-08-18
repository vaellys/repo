package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HttpUtils;

public class DataTools {

	@Test
	public void testLong() throws SQLException {
		Object object = QuickDB.getSingle("select ip from UserLogin where id=1");
		System.out.println(object);
	}

	@Test
	public void test() throws SQLException {
		String countSql = "select count(*) from UserDevice where userId='402880173d008032013d06b49364004d'";
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		ResultSet contResultSet = statement.executeQuery(countSql);
		System.out.println((ResultSetMap.mapInt(contResultSet)));
	}

	@Test
	public void testPost() throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("deviceToken", "1");
		params.put("deviceType", "1");
		params.put("qqUid", "1");
		params.put("qqAccessToken", "1");
		HttpUtils.doPosts("http://192.168.0.151/user/login.json", params);
	}
}
