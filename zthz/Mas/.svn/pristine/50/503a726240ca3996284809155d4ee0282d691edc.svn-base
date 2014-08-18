package cn.zthz.tool.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;

public class EmployerCollectService {
	private static final Log log = LogFactory.getLog(EmployerCollectService.class);
	public static final EmployerCollectService instance = new EmployerCollectService();
	
	public void add(String userId, String fid) throws HzException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String addEmployerCollectSql = "insert ignore EmployerCollect(fid, createTime, userId) values('" + fid + "','"
					+ new Timestamp(System.currentTimeMillis()) + "','" + userId + "')";
			if (log.isDebugEnabled()) {
				log.debug(addEmployerCollectSql);
			}
			QuickDB.insert(addEmployerCollectSql);
		} catch (SQLException e) {
			log.debug("add employerCollect failed! requirementId: '" + fid + "', userId: '" + userId + "'");
			throw new HzException("add employerCollect failed! requirementId: '" + fid + "', userId: '" + userId + "'", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public void delete(List<String> ids, String userId) throws HzException {
		if (log.isDebugEnabled()) {
			log.debug("employerCollect delete params:'" + ids.toString() + "'");
		}
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			if (null != ids) {
				String deleteEmployerCollectSql = "delete from EmployerCollect where fid in ('" + StringUtils.join(ids, "','") + "') and userId='"
						+ userId + "'";
				if (log.isDebugEnabled()) {
					log.debug(deleteEmployerCollectSql);
				}
				statement.executeUpdate(deleteEmployerCollectSql);
			}
		} catch (Exception e) {
			log.debug("delete employerCollect failed! ids:'" + ids.toString() + "'");
			throw new HzException("delete employerCollect failed! ids:'" + ids.toString() + "'", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public List<Map<String, Object>> query(String userId, int startIndex, int pageSize) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String employerCollectSql = "select u.mainPictureSmall,u.completeScore,u.sponsorScore,u.rank,ec.fid,u.name,(select count(*) from Requirement r where r.userId=ec.fid) as totalCount,u.telephone,ec.id from EmployerCollect ec left join User u on ec.fid=u.id where ec.userId='"+userId+"' limit "+startIndex+","+pageSize;
			if(log.isDebugEnabled()){
				log.debug(employerCollectSql);
			}
			List<Map<String, Object>> employerCollectInfo = ResultSetMap.maps(statement.executeQuery(employerCollectSql));
			return employerCollectInfo;
		}catch(Exception e){
			log.debug("query employerCollect failed! userId:'" + userId + "'");
			throw new HzException("query employerCollect failed! userId:'" + userId + "'", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
}
