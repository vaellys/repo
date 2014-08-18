package cn.zthz.tool.requirement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;


public class RequirementVisitorService {
	private static final Log log = LogFactory.getLog(RequirementCommentService.class);
	public static final RequirementVisitorService instance = new RequirementVisitorService();
	/**
	 * 查询浏览次数和前十条浏览记录
	 * @param statement
	 * @param requirementId
	 * @param startIndex
	 * @param pageSize
	 * @return {startIndex:0 , pageSize :8, totalCount :100,contents:[{}]}
	 * @throws UserRequirementException
	 */
	public Map<String, Object> getVisitors(Statement statement, String requirementId, int startIndex, int pageSize) throws UserRequirementException{
		Map<String, Object> result = new HashMap<>();
		try {
			String viewCountSql = "select count(*) from RequirementVisitor where requirementId='" + requirementId + "'";
			if(log.isDebugEnabled()){
				log.debug(viewCountSql);
			}
			int viewCounts = ResultSetMap.mapInt(statement.executeQuery(viewCountSql));
			result.put("totalCount", viewCounts);
//			String visitorsSql = "select u.name, u.mainPicture, r.id from RequirementVisitor r left join User u on r.userId=u.id where requirementId='" + requirementId + "'";
//			if(log.isDebugEnabled()){
//				log.debug(visitorsSql);
//			}
//			List<Map<String, Object>> visitors = ResultSetMap.maps(statement.executeQuery(visitorsSql));
//			result.put("contents", visitors);
			return result;	
		}catch (SQLException e) {
			log.error("query visitors failed! requirementId:'" + requirementId + "'");
			throw new UserRequirementException("requirementId:'" + requirementId + "'"  ,e);
		}
	}
	/**
	 * @param statement
	 * @param requirementId
	 * @param userId
	 * @throws UserRequirementException
	 */
	public void insertViewHistoryRecord(Statement statement, String requirementId, String userId) throws UserRequirementException {
		Connection connection = null;
		try {
			if (null == statement) {
				connection = Connections.instance.get();
				statement = connection.createStatement();
			}
			String recordCountsSql = "select count(*) from RequirementVisitor where userId='" + userId + "' and requirementId='" + requirementId + "'" ;
//			String viewCountSql = "select count(*) as viewCounts from RequirementVisitor where requirementId='" + requirementId + "'";
			if(log.isDebugEnabled()){
				log.debug(recordCountsSql);
			}
			int recordCounts = ResultSetMap.mapInt(statement.executeQuery(recordCountsSql));
			//查询是否有该用户的访问记录
			if(recordCounts == 0){
				String newRequirementVisitorSql = "insert into RequirementVisitor(userId, requirementId, viewTime, viewCount) values('" + userId + "','" + requirementId + "','" + new Timestamp(System.currentTimeMillis()) + "', 1)";
				log.debug(newRequirementVisitorSql);
				statement.executeUpdate(newRequirementVisitorSql);
			}
//				else{
//				//在一小时内，若该用户重复访问，则不更新，否则更新浏览次数，和访问时间
//				Map<String, Object> viewRecord = getViewRecord(statement, userId);
//				int viewCount = (int)viewRecord.get("viewCount");
//				int newViewCount = viewCount + 1;
//				Timestamp viewTime = (Timestamp)viewRecord.get("viewTime");
//				long newCurrentTimeMillis = System.currentTimeMillis();
//				if(newCurrentTimeMillis - viewTime.getTime() >= 3600 * 1000){
//					String modifyViewTimeSql = "update RequirementVisitor set viewTime='" + new Timestamp(newCurrentTimeMillis) + "' , viewCount=" + newViewCount + " where userId='" + userId + "'";
//					log.debug(modifyViewTimeSql);
//					statement.executeUpdate(modifyViewTimeSql);
//				}
//			}
//			if(log.isDebugEnabled()){
//				log.debug(viewCountSql);
//			}
//			int viewCounts = ResultSetMap.mapInt(statement.executeQuery(viewCountSql));
//			return viewCounts;
		} catch (Exception e) {
			log.error("insert history records failed! userId:'" + userId + "'  requirementId:'" + requirementId + "'");
			throw new UserRequirementException("insert history records failed! userId:'" + userId + "'  requirementId:'" + requirementId
					+ "'", e);
		} 
	}
	
	private Map<String, Object> getViewRecord(Statement statement, String userId) throws SQLException{
		String viewTimeSql = "select viewTime, viewCount from RequirementVisitor where userId='" + userId + "' limit 1";
		if(log.isDebugEnabled()){
			log.debug(viewTimeSql);
		}
		Map<String, Object> viewRecord = ResultSetMap.map(statement.executeQuery(viewTimeSql));
		return viewRecord;
	}
}
