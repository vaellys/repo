package cn.zthz.tool.message;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;


public class RMessageService {
	private static final Log log = LogFactory.getLog(RMessageService.class);
	public static final RMessageService instance = new RMessageService();
	
	public int save( String requirementId , String userId,String message ,int t ,int status) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String rowId = HashUtils.uuid();
			String sql = "insert into RMessage (rid, uid, message,rowId, t, createTime ,status ) values('"+(null==requirementId?" ":requirementId)+"','"+userId+"','"+message+"','"+rowId+"',"+t+",'"+new Timestamp(System.currentTimeMillis())+"',"+status+")";
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			statement.executeUpdate(sql);
			return ResultSetMap.mapInt(statement.executeQuery("select mid from RMessage where rowId='"+rowId+"'")); 
		} catch (SQLException e) {
			log.error("save requirement message failed! requirementId:" + requirementId);
			throw new HzException("save requirement message failed! requirementId:" + requirementId);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public int getUnreadRMessageCount(Statement statement ,String userId ) throws SQLException{
		String sql = "select count(*) from RMessage where uid='"+userId+"' and status=0";
		return ResultSetMap.mapInt(statement.executeQuery(sql));
	}
	
	public List<Map<String, Object>> getUnreadRMessages(Statement statement ,String userId ) throws SQLException{
		String sql = "select mid ,rid, uid, message, createTime, t from RMessage where uid='"+userId+"' and status=0 order by createTime desc";
		List<Map<String, Object>> result = ResultSetMap.maps(statement.executeQuery(sql));
		String updateSql = "update RMessage set status=1 where uid='"+userId+"' and status=0";
		statement.executeUpdate(updateSql);
		return result;
		
	}
	
	public Map<String, Object> getRMessage(int rMessageId, String userId) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String rMessageSql = "select rid, uid, message, createTime, t from RMessage where mid=" + rMessageId + " and uid='" + userId + "'";
			if(log.isDebugEnabled()){
				log.debug(rMessageSql);
			}
			Map<String, Object> rMessage = ResultSetMap.map(statement.executeQuery(rMessageSql));
			return rMessage;
		} catch (SQLException e) {
			log.error("query requirement message failed! rMessageId:" + rMessageId);
			throw new HzException("query requirement message failed! rMessageId:" + rMessageId);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
		
	}
}
