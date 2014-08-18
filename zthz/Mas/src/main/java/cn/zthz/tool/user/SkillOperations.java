package cn.zthz.tool.user;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;

public class SkillOperations {
	public static final Log log = LogFactory.getLog(SkillOperations.class);
	public static final SkillOperations instance = new SkillOperations();
	public void addSkill(String userId, String name, String des) throws UserException{
		try {
			QuickDB.insert("insert ignore UserSkills (name, userId, description) values('"+name+"','"+userId+"','"+des+"')");
		} catch (Exception e) {
			log.error("insert skills failed!");
			throw new UserException("insert skills failed!", e);
		}
	}
	
	public void deleteSkill(int id) throws UserException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "delete from UserSkills where id=" + id;
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			statement.executeUpdate(sql);
		}catch(Exception e){
			log.error("delete skill failed! id: " + id);
			throw new UserException("delete skill failed! id: " + id, e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public void updateSkill(int id, String name, String des) throws UserException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "update UserSkills set name='"+name+"',description='" + des + "' where id="+id;
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			statement.executeUpdate(sql);
		}catch(Exception e){
			log.error("update skill failed! id: " + id + " name:'" + name + "' description:'" + des + "'");
			throw new UserException("update skill failed! id: " + id + " name:'" + name + "' description:'" + des + "'", e);
		}
	}
	
	public List<Map<String, Object>> get(String userId) throws UserException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "select id, name, userId, description from UserSkills where userId='" + userId + "'";
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			List<Map<String, Object>> results = ResultSetMap.maps(statement.executeQuery(sql));
			return results;
		}catch(Exception e){
			log.error("query skills failed! userId:'" + userId + "'");
			throw new UserException("query skills failed! userId:'" + userId + "'", e);
		}
			
	}
}
