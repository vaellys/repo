package cn.zthz.actor.rest.listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.db.Connections;
import cn.zthz.tool.requirement.RequirementStatus;

public class UserListener  {
	private static final Log log = LogFactory.getLog(UserListener.class);
	public static final UserListener instance = new UserListener();
	
	private UserListener(){}
	
	public void onUserLogined(String userId){
		
		checkUserExpiredRequirements(userId);
	}
	
	public static void main(String[] args) {
		instance.checkUserExpiredRequirements("402880173c049c92013c049c92f60001");
	}
	
	/**
	 * 
	 * update Requirement set status=5 where status=0 and userId='userId' and expire<=now()
	 * @param userId
	 */
	public void checkUserExpiredRequirements(String userId){
		String updateSql = "update Requirement set status="+RequirementStatus.expired+" where status="+RequirementStatus.published+" and userId='"+userId+"' and expire<=now()";
		Connection connection = null;
		Statement statement = null;
		
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			statement.executeUpdate(updateSql);
		} catch (SQLException e) {
			log.error("" , e);
		}finally{
			if(null!=statement){
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("" , e);
				}
			}
			if(null!=connection){
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("" , e);
				}
			}
		}
		
	}
	
	

}
