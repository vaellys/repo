package cn.zthz.tool.requirement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.db.NamedParameterStatement;

public class AbstractService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	public void closeConnection(Connection connection){
		ConnectionUtils.closeConnection(connection);
	}
	public void closeStatement(Statement statement){
		try {
			if(null!=statement){
				statement.close();
			}
		} catch (SQLException e) {
			log.error("failed to close statement",e);
		}
	}
	public void closeStatement(NamedParameterStatement statement){
		try {
			if(null!=statement){
				statement.close();
			}
		} catch (SQLException e) {
			log.error("failed to close statement",e);
		}
	}
	
}
