package cn.zthz.tool.requirement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.db.Connections;

public class RequirementCommentServiceTest {
	@Test
	public void testSave() throws UserRequirementException{
		RequirementComment comment = new RequirementComment();
		comment.setComment("五个和尚");
		comment.setRequirementId("ff8080813c05aec1013c05aec3f60001");
		comment.setUserId("402880173c049c92013c049c92f60001");
		String id =RequirementCommentService.instance.save(comment );
		System.out.println(id);
	}
	
	@Test
	public void testQuery() throws UserRequirementException {
		List<Map<String, Object>> result = RequirementCommentService.instance.query("ff8080813c05aec1013c05aec3f60001", 0, 10);
		System.out.println(LogUtils.format("result",result));
	}
	@Test
	public void testDelete() throws UserRequirementException {
		int result = RequirementCommentService.instance.delete("ff8080813c110b25013c110b25720000");
		System.out.println(LogUtils.format("result",result));
	}
	
	@Test
	public void testGetComments() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			Map<String, Object> map = RequirementCommentService.instance.getComments(statement, "ff8080813c05aec1013c05aec3f60001", 0, 10);
			System.out.println(JsonUtils.toJsonString(map));
		} catch (Exception e) {
			throw new UserRequirementException("query failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testGetCandidates() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			Map<String, Object> map = RequirementCandidateService.instance.getCandidates(statement, "1", 0, 10);
			System.out.println(JsonUtils.toJsonString(map));
		} catch (Exception e) {
			throw new UserRequirementException("query failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testGetVisitors() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			Map<String, Object> map = RequirementVisitorService.instance.getVisitors(statement, "ff8080813c05aec1013c05aec3f60001", 0, 10);
			System.out.println(JsonUtils.toJsonString(map));
		} catch (Exception e) {
			throw new UserRequirementException("query failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testInsertViewHistoryRecord() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			RequirementVisitorService.instance.insertViewHistoryRecord(statement, "402880173d33c235013d33f217020010", "402880173d15d333013d23fb6b0c0065");
		} catch (Exception e) {
			throw new UserRequirementException("insert failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testInsertViewHistoryRecord2() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			RequirementVisitorService.instance.insertViewHistoryRecord(statement, "402880173c9528a8013ca83c20a10021", "402880173c48eb92013c4a046a390002");
		} catch (Exception e) {
			throw new UserRequirementException("insert failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testInsertViewHistoryRecord3() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			RequirementVisitorService.instance.insertViewHistoryRecord(statement, "402880173c9528a8013ca83c20a10021", "402880173c48eb92013c4a046a390002");
		} catch (Exception e) {
			throw new UserRequirementException("insert failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testQueryComments() throws UserRequirementException{
		try {
			RequirementCommentService.instance.queryComments("ff8080813c05aec1013c05aec3f60001", 0, 10);
		} catch (UserRequirementException e) {
			System.out.println("查询用户前十条评论失败！");
			throw new UserRequirementException("query failed!", e);
		}
	}
	
}