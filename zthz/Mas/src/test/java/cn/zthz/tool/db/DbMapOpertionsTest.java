package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.requirement.ConnectionUtils;

public class DbMapOpertionsTest {

	@Test
	public void testGetMap() throws SQLException {
		Connection connection = Connections.instance.get();
		// String table;
		// String id;
		Map<String, Object> result = DbMapOperations.instance.getMap(connection, "User", "1", "id", "name");
		System.out.println(LogUtils.format("result", result));
		connection.close();
	}

	@Test
	public void testSaveMap() throws SQLException {
		Connection connection = Connections.instance.get();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("deviceToken", "cf4c1e73137ca3b80180210aa82cf08822963d9effe336ba0d7482c2f8eaa185");
		data.put("createTime", new Timestamp(System.currentTimeMillis()));
		data.put("userId", "2");
		DbMapOperations.instance.saveMap(connection, "UserDevice", data );
		connection.close();
	}
	@Test
	public void testUpdate() throws SQLException {
		Connection connection = Connections.instance.get();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("deviceToken", "cf4c1e73137ca3b80180210aa82cf08822963d9effe336ba0d7482c2f8eaa185");
		data.put("createTime", new Timestamp(System.currentTimeMillis()));
		data.put("type", 0);
		Map<String, Object> conditionProperties = new HashMap<>(1);
		conditionProperties.put("userId", "1");
		DbMapOperations.instance.update(connection, "UserDevice", data, conditionProperties );
		connection.close();
	}
	
	@Test
	public void testBatchSaveMap(){
		Connection connection = null;
		try{
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			List<Map<String, Object>> data = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> map2 = new HashMap<>();
			map.put("name","123");
			map.put("description","你好");
			map.put("userId", "1");
			map2.put("name","456");
			map2.put("description","你好123");
			map2.put("userId", "1");
			data.add(map);
			data.add(map2);
			DbMapOperations.instance.batchSaveMap(connection, "UserSkills", data);
			connection.commit();
			connection.setAutoCommit(true);
		}catch(Exception e){
			ConnectionUtils.rollback(connection);
		}finally{
			ConnectionUtils.closeConnection(connection);
		}
		
	}
	
	
	@Test
	public void testSaveMaps(){
		Connection connection = null;
		try{
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			List<Map<String, Object>> data = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> map2 = new HashMap<>();
			map.put("name","123");
			map.put("description","你好");
			map.put("userId", "1");
			map2.put("name","456");
			map2.put("description","你好123");
			map2.put("userId", "1");
			data.add(map);
			data.add(map2);
			DbMapOperations.instance.saveMaps(connection, "UserSkills", data);
			connection.commit();
			connection.setAutoCommit(true);
		}catch(Exception e){
			ConnectionUtils.rollback(connection);
		}finally{
			ConnectionUtils.closeConnection(connection);
		}
	}

}