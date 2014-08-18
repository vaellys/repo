package cn.zthz.tool.queue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.cache.user.UserCacheUpdater;
import cn.zthz.actor.daemon.RequirementExpirer;
import cn.zthz.actor.message.RequirementPushor;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.actor.queue.UserSubjects;
import cn.zthz.actor.solr.SolrUpdater;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.Requirement;

public class DUQueueTest {
	
	@Test
	public void testPublishStringObject() {
		
		Connection connection = null;
		Statement statement = null;
		try{
			Global.queue.start();
			RequirementPushor.instance.onRequirementSeletedCandidate();
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String querySql = "select  id  ,userId , selectedCandidate  ,title from Requirement where id='402880173c9528a8013ca83c20a10021'";
			Map<String, Object> message = ResultSetMap.map(statement.executeQuery(querySql));
			Global.queue.publish(QueueSubjects.REQUIREMENT_SELECTED_CANDIDATE, message);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
		
	}
	
	@Test
	public void testOnRequirementExpired() {
		Global.queue.start();
		RequirementPushor.instance.onRequirementExpired();
		RequirementExpirer re = new RequirementExpirer();
		re.start();
		
	}
	
	@Test
	public void testOnRequirementClosed(){
		Connection connection = null;
		Statement statement = null;
		try{
			Global.queue.start();
			RequirementPushor.instance.onRequirementClosed();
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "select  id ,title,userId , selectedCandidate,hasMandate,price  from Requirement where id='402880173d57dffb013d582dc87d0015'";
			Map<String, Object> map = ResultSetMap.map(statement.executeQuery(sql));
			Global.queue.publish(QueueSubjects.REQUIREMENT_CLOSED, map);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testQueryUser(){
		Connection connection = null;
		Statement statement = null;
		try{
			Global.queue.start();
			
			connection = Connections.instance.get();
			statement = connection.createStatement();
			
//			Global.queue.publish(QueueSubjects.REQUIREMENT_CLOSED, map);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	@Test
	public void testOnRequirementPublished(){
//		Connection connection = null;
//		Statement statement = null;
		try{
			Global.queue.start();
			RequirementPushor.instance.onRequirementPublished();
//			connection = Connections.instance.get();
//			statement = connection.createStatement();
			//String sql = "select  id ,title,userId , selectedCandidate,hasMandate,price  from Requirement where id='402880173d57dffb013d582dc87d0015'";
			//Map<String, Object> map = ResultSetMap.map(statement.executeQuery(sql));
			Requirement requirement = new Requirement();
			requirement.setId("402880173dce7ef6013dd7dd13a005f");
			requirement.setUserId("402880173dce7ef6013dd0be9a470051");
			requirement.setMainPictureBig("http://220.113.10.142/images/e6/3d/e63dda54b350736a6aeba68bb5aa480c.640x960.jpg");
			requirement.setHasMandate(true);
			requirement.setShareQQ(1);
			requirement.setShareWB(2);
			requirement.setTitle("fdsf");
			requirement.setPrice(new BigDecimal("25"));
			Global.queue.publish(QueueSubjects.REQUIREMENT_PUBLISHED, requirement);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOnUserUpdated() {
		Global.queue.start();
		SolrUpdater.onUserUpdated();
		Map<String, Object> message = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			Map<String , Object> skills = new HashMap<>(); 
			skills.put("name", "jkldsa" + i);
			skills.put("description", "12344566789");
			list.add(skills);
		}
		
		message.put("userId", "402880173dce7ef6013dcfd64052004e");
		message.put("skillss", list);
		
		Global.queue.publish(UserSubjects.USER_UPDATED, message);
		
	}
	
	@Test
	public void testShareQQ(){
//		Connection connection = null;
//		Statement statement = null;
		try{
			Global.queue.start();
			UserCacheUpdater.isShareQQ();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", "402880ff3ea5d67a013ea5d67acf0000");
			Global.queue.publish(UserSubjects.SHARE_QQ, map);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testShareWB(){
//		Connection connection = null;
//		Statement statement = null;
		try{
			Global.queue.start();
			UserCacheUpdater.isShareWB();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", "402880ff3e8c8965013e8c8965a90000");
			Global.queue.publish(UserSubjects.SHARE_WB, map);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
