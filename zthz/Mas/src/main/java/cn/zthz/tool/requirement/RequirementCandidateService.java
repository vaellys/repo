package cn.zthz.tool.requirement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.ResultSetMap;

public class RequirementCandidateService extends AbstractService {
	private static final Log log = LogFactory.getLog(RequirementCandidateService.class);
	public static final RequirementCandidateService instance = new RequirementCandidateService();

	/**
	 * 参与竞争
	 * 
	 * @param userId
	 * @param requirementId
	 * @param words
	 * @param pictureId
	 * @return
	 * @throws UserRequirementException
	 */
	public String compete(String userId, String requirementId, String words, String picturePath,String thumbPath, Double candidateLongitude, Double candidateLatitude, String candidateAddress) throws UserRequirementException {
		if (log.isInfoEnabled()) {
			log.info("save candidate");
		}
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			ResultSet countRs = statement.executeQuery("select count(*) from RequirementCandidate where userId='" + userId
					+ "' and requirementId='" + requirementId + "'");
			countRs.next();
			int count = countRs.getInt(1);
			if (count > 0) {
				return null;
			}
			RequirementCandidate candidate = new RequirementCandidate();
			String id = HashUtils.uuid();
			candidate.setId(id);
			candidate.setCreateTime(new Timestamp(System.currentTimeMillis()));
			candidate.setUserId(userId);
			candidate.setRequirementId(requirementId);
			candidate.setWords(words);
			candidate.setMainPicture(picturePath);
			candidate.setThumbs(thumbPath);
			candidate.setLongitude(candidateLongitude);
			candidate.setLatitude(candidateLatitude);
			candidate.setAddress(candidateAddress);
			
			DbOperations.instance.save(connection, candidate, true);
			try {
				/**
				 * publish select candidate message 
				 * requirementId ,requirementTitle,candidateId, candidateName  ,userId  ,
				 * candidateId,
				 */
				String querySql = "select id  ,userId ,title from Requirement where id='"+requirementId+"'";
				Map<String, Object> message = ResultSetMap.map(statement.executeQuery(querySql));
				message.put("candidateId", userId);
//				String applyCountSql = "select applyCount from User where id='" + userId+"'";
//				Map<String, Object> map = ResultSetMap.map(statement.executeQuery(applyCountSql));
//				int applyCount = map.get("applyCount")==null?0:(int)map.get("applyCount")+1;
				String updateSql = "update User set applyCount=applyCount+1 where id='"+userId+"'";
				if(log.isDebugEnabled()){
					log.debug(updateSql);
				}
				statement.executeUpdate(updateSql);
				Global.queue.publish(QueueSubjects.REQUIREMENT_COMPETE, message);
			} catch (Exception e) {
				log.error("", e);
			}
			return id;
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId, "requirementId", requirementId, "words", words, "picturePath", picturePath), e);
			throw new UserRequirementException("save candidate failed!", e);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * 指定一个候选者 update Requirement set selectedCandidate='111' where id='222' and
	 * status=0 and userId!='111'
	 * 
	 * @param requirementId
	 * @param candidateId
	 * @throws UserRequirementException
	 */
	public void selectCandidate(String requirementId,String userId, String candidateId) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			StringBuilder updateCandidateSql = new StringBuilder();
			RequirementOperations.instance.checkUserRequirement(statement, userId, null, requirementId, RequirementStatus.published);
			updateCandidateSql.append("update Requirement set selectedCandidate='");
			updateCandidateSql.append(candidateId);
			updateCandidateSql.append("' where id='");
			updateCandidateSql.append(requirementId);
			updateCandidateSql.append("' and status=0 and userId!='");
			updateCandidateSql.append(candidateId);
			updateCandidateSql.append('\'');
			statement.executeUpdate(updateCandidateSql.toString());
			String updateRequirementSql = "update Requirement set status=" + RequirementStatus.selected + " where id='" + requirementId
					+ "'";
			statement.executeUpdate(updateRequirementSql);
			AccountService.instance.setPrepayTargetAccount(statement, candidateId, requirementId);
			connection.commit();
			try {
				/**
				 * publish select candidate message 
				 * requirementId ,requirementTitle, userId ,userName ,
				 * candidateId,
				 */
				String querySql = "select  id  ,userId , selectedCandidate  ,title from Requirement where id='"+requirementId+"'";
				Map<String, Object> message = ResultSetMap.map(statement.executeQuery(querySql));
				Global.queue.publish(QueueSubjects.REQUIREMENT_SELECTED_CANDIDATE, message);
			} catch (Exception e) {
				log.error("", e);
			}
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.error("failed to roll back!", e);
			}
			log.error("roll back for " + LogUtils.format("requirementId", requirementId, "userId", candidateId), e);
			throw new UserRequirementException("selectCandidate failed!", e);
		} finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("close statement error!", e);
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
			}
		}
	}

	/**
	 * delete from Requirement where requirementId=':requirementId' and
	 * userId=':userId'
	 * 
	 * @param requirementId
	 * @param userId
	 * @throws UserRequirementException
	 */
	public void giveUpCompete(String requirementId, String userId) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("delete from Requirement where requirementId='");
			updateSql.append(requirementId);
			updateSql.append("' and userId='");
			updateSql.append(userId);
			updateSql.append('\'');

			statement.executeUpdate(updateSql.toString());

		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId, "userId", userId), e);
			throw new UserRequirementException("selectCandidate failed!", e);
		} finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("close statement error!", e);
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
			}
		}
	}

	/**
	 * 查询用户已参与竞选的需求 select r.id ,r.title , r.status from RequirementCandidate rc
	 * left join Requirement r on r.id=rc.requirementId where
	 * rc.userId=':userId' select r.id ,r.title , r.status , (select count(*)
	 * from RequirementCandidate rc where rc.requirementId=r.id ) as
	 * candidatesCount from Requirement r where r.userId=':userId' order by
	 * createTime desc limit startIndex , pageSize
	 * 
	 * @param userId
	 * @return
	 * @throws UserRequirementException
	 */
	public List<Map<String, Object>> queryUserCompeteRequirements(String userId, int startIndex, int pageSize)
			throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			// List<Map<String, Object>> result = new LinkedList<>();
			statement = connection.createStatement();
			
			String sql = "select r.id ,r.title , r.status, r.price , r.createTime, r.expire, (select count(*) from RequirementCandidate rc where rc.requirementId=r.id ) as candidatesCount from RequirementCandidate rc left join Requirement r on r.id=rc.requirementId where rc.userId='"
					+ userId + "' order by rc.createTime desc limit " + startIndex + " , " + pageSize;
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.setFetchSize(pageSize);
			// while (rs.next()) {
			// Map<String, Object> record = new HashMap<>(4);
			// record.put("id", rs.getString("id"));
			// record.put("title", rs.getString("title"));
			// record.put("status", rs.getInt("status"));
			// record.put("price", rs.getInt("price"));
			// record.put("candidatesCount", rs.getInt("candidatesCount"));
			// result.add(record);
			// }
			List<Map<String, Object>> requirementCandidates = (List<Map<String, Object>>)ResultSetMap.maps(resultSet);
			queryNewRequirementCandidate(requirementCandidates);
			return requirementCandidates;
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId), e);
			throw new UserRequirementException("query user compete equirements failed!", e);
		} finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error("close statement error!", e);
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
			}
		}
	}

	/**
	 * select r.id ,r.title , r.status from Requirement r where
	 * r.selectedCandidate='userId' and status=status order by r.createTime
	 * limit 0 ,10
	 * 
	 * @param userId
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws UserRequirementException
	 */
	public List<Map<String, Object>> queryUserAcceptedRequirements(String userId, Integer status, int startIndex, int pageSize)
			throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			// List<Map<String, Object>> result = new LinkedList<>();
			statement = connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("select r.id ,r.title , r.status ,r.hasMandate, r.price ,r.address , r.expire, r.createTime ,(select count(*) from RequirementCandidate rc where rc.requirementId=r.id ) as candidatesCount  from Requirement r where r.selectedCandidate='");
			sql.append(userId);
			sql.append('\'');
			if (null != status) {
				sql.append(" and status=");
				sql.append(status);
			}
			sql.append(" order by r.createTime desc limit ");
			sql.append(startIndex);
			sql.append(",");
			sql.append(pageSize);
			ResultSet rs = statement.executeQuery(sql.toString());
			rs.setFetchSize(pageSize);
			List<Map<String, Object>> result = ResultSetMap.maps(rs);
			for (Map<String, Object> map : result) {
				map.put("remainTime", RequirementUtils.remainTime(System.currentTimeMillis(), ((Timestamp) map.get("expire")).getTime()));
			}
			return result;
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId), e);
			throw new UserRequirementException("query user compete equirements failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	/**
	 * selectedCandidates select u.id , u.name ,u.address,u.mainPicture ,
	 * u.mainPicture as picture , u.credit ,u.sex , rc.words as candidateWords
	 * ,prc.remotePath candidateAppendedPicture from User u left join
	 * (RequirementCandidate rc left join Picture prc on rc.pictureId=prc.id) on
	 * u.id=rc.userId where exists (select * from Requirement r where
	 * r.id='requirementId' and r.selectedCandidate=u.id)
	 * 
	 * candidates select u.id , u.name ,u.address, u.mainPicture , u.mainPicture
	 * as picture , u.credit ,u.sex from User u left join RequirementCandidate
	 * rc on rc.userId=u.id where rc.requirementId='requirementId' order by
	 * rc.createTime asc
	 * 
	 * 
	 * {selectedCandidates:{} ,candidates:[{}]}
	 * 
	 * @param userId
	 * @param status
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws UserRequirementException
	 */
	public Map<String, Object> queryRequirementCandidate(String requirementId, int startIndex, int pageSize)
			throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			Map<String, Object> result = new HashMap<>(2);
			statement = connection.createStatement();
			// String sqlHeader =
			// "select u.id , u.name ,u.address, p.remotePath as picture , u.credit ,u.sex,(select count(*) from Requirement sr where status=2 and sr.userId=u.id) as completeRequirementCount from ";
			String sqlHeader = "select u.id , u.name ,u.address, u.mainPicture as picture , u.mainPictureSmall as userPictureSmall, u.mainPictureMid as userPictureMid, u.mainPictureBig as userPictureBig, u.sponsorScore, u.completeScore ,u.sex,u.latestLongtitude as longitude,u.latestLatitude as latitude, (select count(*) from Requirement where userId=u.id and status="+RequirementStatus.complete+") as crc, rc.words as candidateWords ,0.001+distance(r.longitude , r.latitude ,rc.longitude , rc.latitude ) as distance ,rc.mainPicture as candidateAppendedPicture, rc.thumbs as thumbs from ";

			// String selectedCandidateSql =
			// sqlHeader+"User u left join Picture p on u.mainPictureId=p.id where exists (select * from Requirement r where r.id='"+requirementId+"' and r.selectedCandidate=u.id)";
			// String candidatesSql =
			// sqlHeader+"(User u left join Picture p on u.mainPictureId=p.id ) left join RequirementCandidate rc on rc.userId=u.id where rc.requirementId='"+requirementId+"' order by rc.createTime asc limit "+startIndex+","+pageSize;
			String selectedCandidateSql = sqlHeader
					+ "(RequirementCandidate rc left join User u on u.id=rc.userId) left join Requirement r on r.id=rc.requirementId and r.selectedCandidate=u.id  where rc.requirementId='" +requirementId + "' and exists (select * from Requirement r where r.id='"
					+ requirementId + "' and r.selectedCandidate=u.id)";
			if(log.isDebugEnabled()){
				log.debug(selectedCandidateSql);
			}
			String candidatesSql = sqlHeader
					+ "(RequirementCandidate rc left join User u on rc.userId=u.id) left join Requirement r on r.id=rc.requirementId where rc.requirementId='"
					+ requirementId + "' order by rc.createTime asc limit " + startIndex + "," + pageSize;
			if(log.isDebugEnabled()){
				log.debug(candidatesSql);
			}
			Map<String, Object> selectedCandidate = ResultSetMap.map(statement.executeQuery(selectedCandidateSql));
			ResultSet candidatesResultSet = statement.executeQuery(candidatesSql);
			candidatesResultSet.setFetchSize(pageSize);
			List<Map<String, Object>> candidates = ResultSetMap.maps(candidatesResultSet);
			result.put("selectedCandidates", selectedCandidate);
			result.put("candidates", candidates);
			return result;
		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId), e);
			throw new UserRequirementException("query user compete equirements failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}
	
	/**
	 * 查询竞选者的的数量和前十条竞选者记录
	 * @param statement
	 * @param requirementId
	 * @param startIndex
	 * @param pageSize
	 * @return {startIndex:0 , pageSize :8, totalCount :100,contents:[{}]}
	 * @throws UserRequirementException 
	 */
	public Map<String, Object> getCandidates(Statement statement, String requirementId, int startIndex, int pageSize) throws UserRequirementException{
		Map<String, Object> result = new HashMap<>();
		Connection connection = null;
		try {
			if (null == statement) {
				connection = Connections.instance.get();
				statement = connection.createStatement();
			}
			String viewCandidateCountsSql = "select count(*) from RequirementCandidate where requirementId='" + requirementId + "'";
			if(log.isDebugEnabled()){
				log.debug(viewCandidateCountsSql);
			}
			int candidateCounts = ResultSetMap.mapInt(statement.executeQuery(viewCandidateCountsSql));
			result.put("totalCount", candidateCounts);
			String viewCandidatesSql = "select (select count(*) from Requirement where userId=u.id and status="+RequirementStatus.complete+") as crc , u.name, u.mainPicture, u.sex, r.id, u.id as userId,distance(r.longitude , r.latitude , rt.longitude , rt.latitude) as distance, u.latestLongtitude as longitude,u.latestLatitude as latitude from (User u left join RequirementCandidate r on u.id=r.userId) left join Requirement rt on rt.id=r.requirementId where requirementId='" + requirementId + "'";
			if(log.isDebugEnabled()){
				log.debug(viewCandidatesSql);
			}
			List<Map<String, Object>> candidates = ResultSetMap.maps(statement.executeQuery(viewCandidatesSql));
			result.put("contents", candidates);
			result.put("startIndex", "0");
			result.put("pageSize", candidates.size());
		}catch (SQLException e) {
			log.error("query candidates failed! requirementId:'" + requirementId + "'");
			throw new UserRequirementException("requirementId:'" + requirementId + "'"  ,e);
		}
		return result;
	}
	
	public Map<String, Object> getSelectedCandidate(Statement statement, String requirementId) throws UserRequirementException{
		Map<String, Object> result = new HashMap<>();
		Connection connection = null;
		try {
			if (null == statement) {
				connection = Connections.instance.get();
				statement = connection.createStatement();
			}
			String selectedCandidateSql = "select (select count(*) from Requirement where userId=u.id and status="+RequirementStatus.complete+") as crc, u.name as selectedCandidateName, u.mainPicture as selectedCandidatePicture, u.mainPictureBig as selectedCandidatePictureBig, u.mainPictureMid as selectedCandidatePictureMid, u.mainPictureSmall as selectedCandidatePictureSmall, u.address, distance(r.longitude , r.latitude , rc.longitude,rc.latitude) as distance , u.id as selectedCandidateId from (Requirement r left join User u on r.selectedCandidate=u.id) left join RequirementCandidate rc on rc.requirementId=r.id and rc.userId=u.id" +
					" where r.id='" + requirementId + "'";
			if(log.isDebugEnabled()){
				log.debug(selectedCandidateSql);
			}
			result = ResultSetMap.map(statement.executeQuery(selectedCandidateSql));
			if(null == result.get("selectedCandidateName")){
				return Collections.EMPTY_MAP;
			}else{
				return result;
			}
		}catch (SQLException e) {
			log.error("query candidates failed! requirementId:'" + requirementId + "'");
			throw new UserRequirementException("requirementId:'" + requirementId + "'"  ,e);
		}
	}
	
	private void queryNewRequirementCandidate(List<Map<String, Object>> requirementCandidates){
		Map<String, Object> e = null;
		for(Map<String, Object> requirementCandidate : requirementCandidates){
			Timestamp expire = (Timestamp)requirementCandidate.get("expire");
			if(null == expire){
				e = requirementCandidate;
				continue;
			}
			requirementCandidate.put("remainTime", RequirementUtils.remainTime(System.currentTimeMillis(), expire.getTime()));
		}
		requirementCandidates.remove(e);
	}
}
