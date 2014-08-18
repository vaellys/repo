package cn.zthz.tool.requirement;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.db.Connections;

public class RequirementCandidateServiceTest {

	@Test
	public void testQueryRequirementCandidate() throws UserRequirementException {
		try {
			Map<String, Object> candidates = RequirementCandidateService.instance.queryRequirementCandidate("402880173e120054013e1ae4ed0f0244", 0, 10);
			System.out.println(LogUtils.format("k",candidates));
		} catch (UserRequirementException e) {
			throw new UserRequirementException("query failed!");
		}
	}
	
	@Test
	public void testGetSelectedCandidate() throws UserRequirementException, SQLException{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			Map<String, Object> map = RequirementCandidateService.instance.getSelectedCandidate(statement, "1");
			System.out.println(JsonUtils.toJsonString(map));
		} catch (Exception e) {
			throw new UserRequirementException("query failed!", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testSelectCandidate() throws SQLException, UserRequirementException {
		try {
			RequirementCandidateService.instance.selectCandidate("402880173d96e11d013d9f67738a0034","", "402880173d008032013d051f574b0045");
		} catch (Exception e) {
			throw new UserRequirementException("select candidate failed!", e);
		}
	}
	
	@Test
	public void testCompete() throws SQLException, UserRequirementException {
		try {
			RequirementCandidateService.instance.compete("402880173f07abcf013f08a0f039002c","6","FDS","112","4",new Double(2),new Double(3),"fds");
		} catch (Exception e) {
			throw new UserRequirementException("select candidate failed!", e);
		}
	}

}
