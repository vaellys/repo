package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;

public class TableMapTest {
	
	public static void main(String[] args) {
		List<String> ids = new LinkedList<>();
		ids.add("1");
		ids.add("2");
		ids.add("3");
		System.out.println(StringUtils.link(ids, "','"));
	}
	
	@Test
	public void testMap() throws SQLException{
		Connection connection = Connections.instance.get();
		
		Statement statement = connection.createStatement();
		String sql = "select * from Person limit 2";
		ResultSet resultSet = statement.executeQuery(sql );
		Map<Object, Map<String, Object>> result = ResultSetMap.map(resultSet , "name");
		System.out.println(LogUtils.format("result" , result));
		
		statement.close();
		connection.close();
	}

}
