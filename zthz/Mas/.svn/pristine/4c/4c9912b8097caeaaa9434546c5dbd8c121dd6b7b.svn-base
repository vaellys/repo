package cn.zthz.tool.user;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.Formats;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.NamedParameterStatement;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.message.Message;
import cn.zthz.tool.message.MessageService;
import cn.zthz.tool.message.MessageTypes;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.push.ReceiveStatus;
import cn.zthz.tool.push.SendStatus;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.RequirementStatus;
import cn.zthz.tool.requirement.RequirementUtils;

public class EmployerService {
	public static final EmployerService instance = new EmployerService();
	private static final Log log = LogFactory.getLog(EmployerService.class);
	/**
	 * sCount：累计发布,iCount:发布中,eCount:雇佣中,cCount:已完成,closedCount:已结束,expiredCount
	 * :已过期,soCount:搜藏件数 payCommission:支出佣金,employmentCount:成功雇佣,balance:账户余额
	 * 
	 * @param userId
	 * @return
	 * @throws HzException
	 */
	public Map<String, Object> home(String userId) throws HzException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			User user = UserProxy.getUserInfo(userId);
			Map<String, Object> result = new HashMap<>();
			result.put("name", user.getName());
			result.put("nick", user.getNick());
			result.put("email", user.getEmail());
			result.put("userPictureSmall", user.getMainPictureSmall());
			result.put("telphone", user.getTelephone());
			result.put("sponsorScore", user.getSponsorScore());
			//String sql = "select completeCount,closeCount from User where id='" + userId + "'";
			//Map<String, Object> employerMap = ResultSetMap.map(statement.executeQuery(sql));
			String sCountSql = "select count(*) as sCount from Requirement where userId='" + userId + "'";
			int sCount = ResultSetMap.mapInt(statement.executeQuery(sCountSql));
			result.put("sCount", sCount);
			String iCountSql = "select count(*) as iCount from Requirement where status=" + RequirementStatus.published + " and userId='"
					+ userId + "'";
			int iCount = ResultSetMap.mapInt(statement.executeQuery(iCountSql));
			result.put("iCount", iCount);
			String eCountSql = "select count(*) as eCount from Requirement where status=" + RequirementStatus.selected + " and userId='"
					+ userId + "'";
			int eCount = ResultSetMap.mapInt(statement.executeQuery(eCountSql));
			result.put("eCount", eCount);
			String expiredCountSql = "select count(*) as expiredCount from Requirement where status=" + RequirementStatus.expired
					+ " and userId='" + userId + "'";
			int expiredCount = ResultSetMap.mapInt(statement.executeQuery(expiredCountSql));
			result.put("expiredCount", expiredCount);
			String closedCountSql = "select count(*) as closedCount from Requirement where status="+RequirementStatus.closed+" and userId='"+userId+"'";
			int closedCount = ResultSetMap.mapInt(statement.executeQuery(closedCountSql));
			result.put("closedCount", closedCount);
			//result.put("closedCount", employerMap.get("closeCount") == null ? 0 : (int) employerMap.get("closeCount"));
			String soCountSql = "select count(*) as soCount from Store where userId='" + userId + "'";
			int soCount = ResultSetMap.mapInt(statement.executeQuery(soCountSql));
			result.put("soCount", soCount);
			String payCommissionSql = "select sum(price) as payCommission from Requirement where userId='" + userId + "' and status="+ RequirementStatus.complete;
			Map<String, Object> map = ResultSetMap.map(statement.executeQuery(payCommissionSql));
			BigDecimal payCommission = (BigDecimal) map.get("payCommission");
			BigDecimal formatPayCommission = Formats.formatMoney(payCommission == null ? new BigDecimal("0") : payCommission);
			result.put("payCommission", formatPayCommission);
			// String selectedCandidateSql =
			// "select count(*) from Requirement where status="+RequirementStatus.selected
			// + " and selectedCandidate is not null and userId='"+userId+"'";
			// int selectedCandidateCount =
			// ResultSetMap.mapInt(statement.executeQuery(selectedCandidateSql));
			// result.put("selectedCandidateCount", selectedCandidateCount);
			String employmentCountSql = "select count(*) as count from Requirement where status="+RequirementStatus.complete+" and userId='"+userId+"'";
			int employmentCount = ResultSetMap.mapInt(statement.executeQuery(employmentCountSql));
			String cCountSql = "select count(*) as cCount from Requirement where status in ("+RequirementStatus.complete+","+RequirementStatus.expired + "," + RequirementStatus.closed + ") and userId='" + userId + "'";
			int cCount = ResultSetMap.mapInt(statement.executeQuery(cCountSql));
			result.put("cCount", cCount);
			result.put("employmentCount", employmentCount);
			//result.put("employmentCount", employerMap.get("completeCount") == null ? 0 : (int) employerMap.get("completeCount"));
			String collectUserCountSql = "select count(*) as count from UserCollect where userId='" + userId + "'";
			int collectUserCount = ResultSetMap.mapInt(statement.executeQuery(collectUserCountSql));
			result.put("collectUserCount", collectUserCount);
			String balanceSql = "select balance from Account where userId='" + userId + "'";
			BigDecimal balance = (BigDecimal) ResultSetMap.map(statement.executeQuery(balanceSql)).get("balance");
			BigDecimal formatBalance = Formats.formatMoney(balance);
			result.put("balance", formatBalance);
			return result;
		} catch (Exception e) {
			log.error("employer home query failed!userId:'" + userId + "'", e);
			throw new HzException("employer home query failed!userId:'" + userId + "'");
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public List<Map<String, Object>> requirementManagement(String userId, Map<String, Object> conditionArgs, Integer startIndex,
			Integer pageSize) throws HzException {
		Connection connection = null;
		NamedParameterStatement namedParameterStatement = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			connection = Connections.instance.get();
			String sql = createQuerySql(userId, null != conditionArgs ? conditionArgs.keySet() : Collections.EMPTY_SET, startIndex,
					pageSize);
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			Map<String, Object> args = new HashMap<>();
			if (null != conditionArgs)
				args.putAll(conditionArgs);
			namedParameterStatement = new NamedParameterStatement(connection, sql);
			if (log.isDebugEnabled()) {
				log.debug(namedParameterStatement.toString());
			}
			Set<Map.Entry<String, Object>> set = args.entrySet();
			for (Map.Entry<String, Object> entry : set) {
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
			results = ResultSetMap.maps(namedParameterStatement.executeQuery());
			if (null != results) {
				for (Map<String, Object> map : results) {
					String remainTime = RequirementUtils.remainTime(System.currentTimeMillis(), ((Timestamp) map.get("expire")).getTime());
					map.put("expire", remainTime);
				}
			}
			return results;
		} catch (Exception e) {
			log.error("requirement query failed!userId:'" + userId + "'", e);
			throw new HzException("requirement query failed!userId:'" + userId + "'", e);
		} finally {
			ConnectionUtils.closeStatement(namedParameterStatement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	private String createQuerySql(String userId, Set<String> conditionKeys, int startIndex, int pageSize) {
		StringBuilder sql = new StringBuilder();
		sql.append("select r.id,r.title,r.expire,r.description,r.address,r.price,r.longitude,r.latitude,r.status,r.type,r.hasMandate,r.selectedCandidate,(select count(*) from RequirementCandidate where requirementId=r.id) as applyCount from Requirement r where ");
		if (null != conditionKeys && !conditionKeys.isEmpty()) {
			for (String key : conditionKeys) {
				sql.append("r.");
				sql.append(key);
				sql.append("=:");
				sql.append(key);
				sql.append(" and ");
			}
		}
		sql.append(" r.userId='");
		sql.append(userId);
		sql.append("'");
		sql.append(" order by r.createTime desc limit ");
		sql.append(startIndex);
		sql.append(",");
		sql.append(pageSize);
		return sql.toString();
	}

	public Map<String, Object> employmentSuccess(String userId, int startIndex, int pageSize) throws HzException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			Map<String, Object> result = new HashMap<>();
			String employmentSql = "select title,description,address,price,completeTime,type from Requirement where userId='" + userId
					+ "' and status=" + RequirementStatus.complete + " limit " + startIndex + "," + pageSize;
			List<Map<String, Object>> employments = ResultSetMap.maps(statement.executeQuery(employmentSql));
			result.put("employments", employments);
			// String payCommissionSql =
			// "select sum(price) as payCommission from Requirement where userId='"+userId+"' and status="+RequirementStatus.complete;
			// Map<String, Object> map =
			// ResultSetMap.map(statement.executeQuery(payCommissionSql));
			// BigDecimal payCommission = (BigDecimal)map.get("payCommission");
			// BigDecimal formatPayCommission =
			// Formats.formatMoney(payCommission);
			// result.put("payCommission", formatPayCommission);
			String balanceSql = "select balance from Account where userId='" + userId + "'";
			BigDecimal balance = (BigDecimal) ResultSetMap.map(statement.executeQuery(balanceSql)).get("balance");
			BigDecimal formatBalance = Formats.formatMoney(balance);
			result.put("balance", formatBalance);
			return result;
		} catch (Exception e) {
			log.error("employment query failed!userId:'" + userId + "'", e);
			throw new HzException("employment query failed!userId:'" + userId + "'", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public void inviteEmployee(String userId, List<String> employeeId, String requirementId) throws HzException {
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String requirementSql = "select title from Requirement where id='"+requirementId+"'";
			Map<String, Object> requirement = ResultSetMap.map(statement.executeQuery(requirementSql));
			Map<String, Object> message = new HashMap<>();
			Map<String, Object> mu = new HashMap<>();
			message.put("userId", userId);
			message.put("requirementId", requirementId);
			String employerName = UserProxy.getUserInfo(userId).getName();
			for(String receiverId : employeeId){
				String content = employerName+"邀请"+UserProxy.getUserInfo(receiverId).getName()+"完成任务:"+(String)requirement.get("title")+":"+requirementId;
				Message messageObject = new Message();
				messageObject.setMessage(content);
				messageObject.setiType(MessageTypes.EMPLOYER_INVITE);
				messageObject.setSenderId(userId);
				messageObject.setReceiverId(receiverId);
				String uuid = HashUtils.uuid();
				messageObject.setUuid(uuid);
				messageObject.setSendStatus(SendStatus.UNSEND);
				messageObject.setReceiveStatus(ReceiveStatus.UNRECEIVED);
				messageObject.setSendTime(new Timestamp(System.currentTimeMillis()));
				DbOperations.instance.save(connection, messageObject, true);
				long  mid =ResultSetMap.mapInt(statement.executeQuery("select id from Message where uuid='"+uuid+"'" ));
				//int mid = MessageService.instance.save(requirementId, receicerId, content,MessageTypes.EMPLOYER_INVITE, 0);
				mu.put(receiverId, mid);
			}
			message.put("employeeId", mu);
			Global.queue.publish(QueueSubjects.EMPLOYER_INVITE, message);
		}catch(Exception e){
			log.error("inviteEmployer message failed!", e);
			throw new HzException("inviteEmployer message failed!", e);
		}finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public void mandate(String requirementId) throws HzException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			String sql = "select userId,price,status from Requirement where id='" + requirementId + "'";
			Map<String, Object> requirement = ResultSetMap.map(statement.executeQuery(sql));
			BigDecimal price = (BigDecimal) requirement.get("price");
			BigDecimal formatPrice = Formats.formatMoney(price);
			int status = (int) requirement.get("status");
			if (status != RequirementStatus.published && status != RequirementStatus.selected) {
				log.debug("requirement is not status of publishing");
				throw new HzException("requirement is not status of publishing");
			}
			AccountService.instance.prePay(statement, (String) requirement.get("userId"), formatPrice, requirementId);
			String usql = "update Requirement set hasMandate=1 where id='" + requirementId + "'";
			statement.executeUpdate(usql);
			connection.commit();
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error("mandate requirement failed!requirementId:'" + requirementId + "'", e);
			throw new HzException("mandate requirement failed!requirementId:'" + requirementId + "'", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public Map<String, Object> employeeInfo(String targetUserId, String userId) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "select u.id,u.name , u.mainPictureSmall ,u.rank,u.nick ,u.name ,u.completeScore, u.sponsorScore,u.telephone,u.CompleteCount,(select sum(r.price) from Requirement r where r.selectedCandidate='"+targetUserId+"' and r.status="+RequirementStatus.complete+") as totalTranactionMoney ,(select count(*) from Requirement r where r.userId='"+targetUserId+"') as totalRequirements,(select count(*) from Requirement r where r.status="+RequirementStatus.published+" and r.userId='"+targetUserId+"') as campaignCount,(select count(*) from Requirement r where r.status="+RequirementStatus.selected+" and r.userId='"+targetUserId+"') as employmentCount,(select count(*) from Requirement r where r.status="+RequirementStatus.published+" and r.userId='"+targetUserId+"') as campaignCount,(select count(*) from Requirement r where r.status="+RequirementStatus.closed+" and r.userId='"+targetUserId+"') as closedCount, (select count(fid) from EmployerCollect where userId='"+userId+"' and fid='"+targetUserId+"') as hasEmployerCollect from User u where u.id='"+targetUserId+"' limit 1";
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			Map<String, Object> result = ResultSetMap.map(statement.executeQuery(sql));
			if(null == result.get("totalTranactionMoney")){
				result.put("totalTranactionMoney", 0);
			}
			return result;
		}catch(Exception e){
			log.error("user id:"+userId, e);
			throw new HzException("user id:"+userId, e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public Map<String , Object> requirements(String userId , Integer status, Integer type , int si , int ps) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			Map<String, Object> result = new HashMap<>();
			StringBuilder qsql = new StringBuilder();
			StringBuilder csql = new StringBuilder();
			StringBuilder con = new StringBuilder();
			qsql.append("select r.id , r.title ,r.price,r.expire,r.address,r.status,r.type as tags");
			csql.append("select count(*) ");
			con.append(" from Requirement r where userId='");
			con.append(userId);
			con.append('\'');
			if (null != status) {
				con.append(" and r.status=");
				con.append(status);
			}
			if (null != type) {
				con.append(" and r.type=");
				con.append(type);
			}
			qsql.append(con);
			qsql.append(" limit "+si+","+ps);
			if(log.isDebugEnabled()){
				log.debug(qsql);
			}
			csql.append(con);
			
			List<Map<String, Object>>  requirements = ResultSetMap.maps(statement.executeQuery(qsql.toString()));
			long totolCount = ResultSetMap.mapInt(statement.executeQuery(csql.toString()));
			result.put("requirements", requirements);			
			result.put("totolCount", totolCount);			
			return result;
		}catch(Exception e){
			log.error("employment query requirements failed!userId:'"+userId+"'", e);
			throw new HzException("employment query requirements failed!userId:'"+userId+"'", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
}
