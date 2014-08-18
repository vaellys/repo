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

import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.db.SimpleObjectMapping;

public class RequirementCommentService {
	private static final Log log = LogFactory.getLog(RequirementCommentService.class);
	public static final RequirementCommentService instance = new RequirementCommentService();

	public String save(RequirementComment requirementComment) throws UserRequirementException {
		if (log.isInfoEnabled()) {
			log.info("save comment");
		}
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			String id = HashUtils.uuid();
			requirementComment.setId(id);
			requirementComment.setCommentTime(new Timestamp(System.currentTimeMillis()));
			DbOperations.instance.save(connection, requirementComment, true);
			return id;
		} catch (SQLException e) {
			log.error(LogUtils.format("comment", requirementComment), e);
			throw new UserRequirementException("save comment failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}

		}
	}

	/**
	 * select c.* ,u.mainPicture as userPicture,u.name as userName from Comment
	 * c left join user u on c.userId=u.id where requirementId='123' order by
	 * r.commentTime limit startIndex,pageSize
	 * 
	 * @param requirementId
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws UserRequirementException
	 */
	public List<Map<String , Object>> query(String requirementId, int startIndex, int pageSize) throws UserRequirementException {
		if (log.isInfoEnabled()) {
			log.info("query comments, requirementId:" + requirementId + " startIndex:" + startIndex + " pageSize:" + pageSize);
		}
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			StringBuilder sql = new StringBuilder();
			sql.append("select c.* ,u.mainPicture as userPicture,u.mainPictureSmall as userPictureSmall, u.mainPictureMid as userPictureMid, u.mainPictureBig as userPictureBig,u.name as userName ,u.completeScore as userCompleteScore, u.sponsorScore as userSponsorScore from RequirementComment c left join User u on c.userId=u.id where requirementId='");
			sql.append(requirementId);
			sql.append("' order by c.commentTime desc limit ");
			sql.append(startIndex);
			sql.append(',');
			sql.append(pageSize);
//			Map<String, Object> args = null;
			statement = connection.createStatement();
			return ResultSetMap.maps(statement.executeQuery(sql.toString()));
//			return DbOperations.instance.query(connection, sql.toString(), args, RequirementComment.class, SimpleObjectMapping.instance,
//					pageSize);
		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId, "startIndex", startIndex, "pageSize", pageSize), e);
			throw new UserRequirementException("query requirement's comments failed!", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}
	public Map<String , Object> queryComments(String requirementId, int startIndex, int pageSize) throws UserRequirementException {
		if (log.isInfoEnabled()) {
			log.info("query comments, requirementId:" + requirementId + " startIndex:" + startIndex + " pageSize:" + pageSize);
		}
		Connection connection = null;
		Statement statement =  null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			StringBuilder queryCommentSql = new StringBuilder();
			queryCommentSql.append("select c.id,c.requirementId, c.userId, c.comment, c.commentTime ,u.mainPicture as userPicture,u.mainPictureSmall as userPictureSmall, u.mainPictureMid as userPictureMid, u.mainPictureBig as userPictureBig,u.name as userName from RequirementComment c left join User u on c.userId=u.id where requirementId='");
			queryCommentSql.append(requirementId);
			queryCommentSql.append("' order by c.commentTime desc limit ");
			queryCommentSql.append(startIndex);
			queryCommentSql.append(',');
			queryCommentSql.append(pageSize);
//			List<RequirementComment> records = DbOperations.instance.query(connection, sql.toString(), args, RequirementComment.class, SimpleObjectMapping.instance,
//					pageSize);
			if(log.isDebugEnabled()){
				log.debug(queryCommentSql.toString());
			}
			List<Map<String, Object>> commentRecords = ResultSetMap.maps(statement.executeQuery(queryCommentSql.toString()));
			String queryCommentCounts = "select count(*) as totalCounts from RequirementComment where requirementId='" + requirementId + "'";
			if(log.isDebugEnabled()){
				log.debug(queryCommentCounts);
			}
			int totalCounts = ResultSetMap.mapInt(statement.executeQuery(queryCommentCounts));
			Map<String , Object > result = new HashMap<>(4);
			result.put("content" , commentRecords);
			result.put("startIndex" , startIndex);
			result.put("totalCount" , totalCounts);
			result.put("pageSize", commentRecords.size());
			return result;
		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId, "startIndex", startIndex, "pageSize", pageSize), e);
			throw new UserRequirementException("query requirement's comments failed!", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public int delete(String commentId) throws UserRequirementException {
		if (log.isInfoEnabled()) {
			log.info("delete comment id:" + commentId);
		}
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			StringBuilder sql = new StringBuilder();
			sql.append("delete from RequirementComment where id='");
			sql.append(commentId);
			sql.append('\'');
			return DbOperations.instance.executeUpdate(connection, sql.toString());
		} catch (SQLException e) {
			log.error(LogUtils.format("commentId", commentId), e);
			throw new UserRequirementException("delete requirement's comment failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	/**
	 * 查询评论次数和前十条评论记录
	 * @param statement
	 * @param requirement
	 * @param startIndex
	 * @param pageSize
	 * @return {startIndex:0 , pageSize :8, totalCount :100,contents:[{}]}
	 * @throws SQLException 
	 */
	public Map<String, Object> getComments(Statement statement, String requirementId, int startIndex, int pageSize)
			throws UserRequirementException {
		Map<String, Object> result = new HashMap<>(4);
		Connection connection = null;
		try {
			if (null == statement) {
				connection = Connections.instance.get();
				statement = connection.createStatement();
			}
			String countSql = "select count(*) from RequirementComment where requirementId='"+requirementId+"' limit "+startIndex+","+pageSize;
			if(log.isDebugEnabled()){
				log.debug(countSql);
			}
			String sql = "select rc.comment,rc.commentTime,rc.userId,u.mainPictureSmall,u.name  from RequirementComment rc left join User u on rc.userId=u.id where requirementId='"+requirementId+"' limit "+startIndex+","+pageSize;
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			int commentCounts = ResultSetMap.mapInt(statement.executeQuery(countSql));
			result.put("totalCount", commentCounts);
			List<Map<String, Object>> userComments = ResultSetMap.maps(statement.executeQuery(sql));
			result.put("contents", userComments);
			result.put("startIndex", 0);
			result.put("pageSize", userComments.size());
			return result;
		} catch (SQLException e) {
			log.error("aquired comments failed ! requirementId:'" + requirementId + "'");
			throw new UserRequirementException("aquired comments failed ! requirementId:'" + requirementId + "'"  ,e);
		}
	}
}
