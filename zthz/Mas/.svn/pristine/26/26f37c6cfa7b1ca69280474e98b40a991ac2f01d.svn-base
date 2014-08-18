package cn.zthz.tool.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.rest.ErrorCodes;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.RequirementUtils;

public class StoreService {
	private static final Log log = LogFactory.getLog(StoreService.class);
	public static final StoreService instance = new StoreService();
	
	public void add(String requirementId, String userId) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String checkStoreSql = "select count(*) from Store where requirementId='" + requirementId + "' and userId='" + userId +"'";
			if(log.isDebugEnabled()){
				log.debug(checkStoreSql);
			}
			int storeCount = ResultSetMap.mapInt(statement.executeQuery(checkStoreSql));
			if(0 != storeCount){
				log.debug("not add store repeatability! requirementId: '" + requirementId + "', userId: '" + userId + "'");
				throw new HzException(ErrorCodes.ALREADY_STORED, "not add store repeatability! requirementId: '" + requirementId + "', userId: '" + userId + "'");
			}else{
				String addStoreSql = "insert into Store(requirementId, createTime, userId) values('" + requirementId + "','" + new Timestamp(System.currentTimeMillis()) + "','" + userId + "')";
				if(log.isDebugEnabled()){
					log.debug(addStoreSql);
				}
				QuickDB.insert(addStoreSql);
			}
		}catch(SQLException e){
			log.debug("add store failed! requirementId: '" + requirementId + "', userId: '" + userId + "'");
			throw new HzException("add store failed! requirementId: '" + requirementId + "', userId: '" + userId + "'", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public void delete(List<String> ids, String userId) throws HzException{
		if(log.isDebugEnabled()){
			log.debug("store delete params:'" + ids.toString() + "'");
		}
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			if(null != ids){
				String deleteStoreSql = "delete from Store where id in ('"+ StringUtils.join(ids , "','")+"') and userId='" +userId + "'" ;
				if(log.isDebugEnabled()){
					log.debug(deleteStoreSql);
				}
				statement.executeUpdate(deleteStoreSql);
			}
		}catch(Exception e){
			log.debug("delete store failed! ids:'" + ids.toString() + "'");
			throw new HzException("delete store failed! ids:'" + ids.toString() + "'", e);
		}finally{
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
			String queryStoreSql = "select r.id,r.title,r.expire,r.price,r.status,(select count(rc.userId) from RequirementCandidate rc where rc.requirementId = s.requirementId ) as applyCount, u.name ,s.id as storeId from Store s right join (Requirement r right join User u on r.userId=u.id) on s.requirementId = r.id  where s.userId='" + userId + "'  order by s.createTime desc limit " + startIndex +"," +  pageSize;
			if(log.isDebugEnabled()){
				log.debug(queryStoreSql);
			}
			List<Map<String, Object>> storeInfo = ResultSetMap.maps(statement.executeQuery(queryStoreSql));
			if(null != storeInfo){
				for(Map<String, Object> map : storeInfo){
					String remainTime = RequirementUtils.remainTime(System.currentTimeMillis(), ((Timestamp)map.get("expire")).getTime());
					map.put("expire", remainTime);
				}
			}
			return storeInfo;
		}catch(Exception e){
			log.debug("query store failed! userId:'" + userId + "'");
			throw new HzException("query store failed! userId:'" + userId + "'", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public void cancel(String requirementId, String userId) throws HzException{
		if(log.isInfoEnabled()){
			log.info("cancel store params userId:'" + userId + "' requirementId:'" + requirementId+"'");
		}
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String storeSql = "delete from Store where requirementId='" + requirementId + "' and userId='" + userId + "'";
			if(log.isDebugEnabled()){
				log.debug(storeSql);
			}
			statement.executeUpdate(storeSql);
		}catch(Exception e) {
			log.debug("cancel store failed! userId:'" + userId + "' requirementId:'" + requirementId + "'");
			throw new HzException("cancel store failed! userId:'" + userId + "' requirementId:'" + requirementId + "'");
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
}
