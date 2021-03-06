package cn.zthz.tool.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.actor.queue.UserSubjects;
import cn.zthz.actor.rest.ErrorCodes;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.CollectionUtils;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbMapOperations;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.ObjectTableInfo;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.db.SimpleObjectMapping;
import cn.zthz.tool.db.SimpleSqlFactory;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.requirement.AbstractService;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.RequirementStatus;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class UserOperationImpl extends AbstractService implements UserOperations {
	private static final Log log = LogFactory.getLog(UserOperationImpl.class);

	public static final UserOperationImpl instance = new UserOperationImpl();
	
	public void checkUserPassword(String userId , String password) throws  UserException{
		String sql = "select count(*) from User where id='"+userId+"' and password='"+password+"'";
		try {
			if(0>=(long)QuickDB.getSingle(sql)){
				throw new UserException(ErrorCodes.USER_PASSWORD_ERROR,"user password error");
			}
		} catch (SQLException e) {
			throw new UserException(ErrorCodes.SERVER_INNER_ERROR,"db error");
		}
	}
	public void checkUserPassword(Statement statement ,String userId , String password) throws SQLException, UserException{
		String sql = "select count(*) from User where userId='"+userId+"' and password='"+password+"'";
		if(0>=ResultSetMap.mapInt(statement.executeQuery(sql ))){
			throw new UserException(ErrorCodes.USER_PASSWORD_ERROR,"user password error");
		}
	}
	
	public void resetPassword(String userId , String resetToken , String password)throws  UserException {
		try {
			if(StringUtils.isEmpty(resetToken)||"0".equals(resetToken) || (long)QuickDB.getSingle("select count(*) from User where id='"+userId+"' and resetToken='"+resetToken+"'")<=0){
				throw new UserException(ErrorCodes.USER_RESETPASSWORD_ERROR , "resetToken is invalid");
			}
			QuickDB.update("update User set password='"+StringEscapeUtils.escapeSql(password)+"',resetToken='0' where id='"+StringEscapeUtils.escapeSql(userId)+"'");
			UserProxy.refreshUserCaches(userId);
		} catch (SQLException e) {
			throw new UserException(e.getMessage() ,e);
		}
	}
	public void forgetPassword(String userId)throws  UserException{
		
		try {
			Properties props=new Properties();
			String smtp = GlobalConfig.get("company.mail.smtp.host");
			props.put("mail.smtp.host",smtp);  
			props.put("mail.smtp.auth","true");
			Session session=Session.getInstance(props);  
			Message message=new MimeMessage(session);
			
			InternetAddress from=new InternetAddress(GlobalConfig.get("company.mail.address"));  
			message.setFrom(from);
			Object email = QuickDB.getSingle("select email from User where id='"+userId+"'");
			if(StringUtils.isEmpty((String)email)){
				throw new UserException(ErrorCodes.USER_EMAIL_ERROR , "用户没有邮箱");
			}
			InternetAddress to = new InternetAddress((String) email);
			message.setRecipient(Message.RecipientType.TO,to);  
			message.setSubject("简客修改密码");
			String resetToken = HashUtils.systemUuid();
			QuickDB.update("update User set resetToken='"+resetToken+"' where id='"+userId+"'");
			String url = "http://"+GlobalConfig.get("server.domain")+"/user/resetPasswordView.json?userId="+userId+"&resetToken="+resetToken;
			String text="请点击 <a href='"+url+"'>"+url+"</a>重置密码。";
			message.setContent(text, "text/html;charset=utf-8");  
			message.setSentDate(new Date());
			message.saveChanges();  
			Transport transport=session.getTransport("smtp");  
			transport.connect(smtp,GlobalConfig.get("company.mail.address"),GlobalConfig.get("company.mail.password"));  
			transport.sendMessage(message, message.getAllRecipients());  
			transport.close();  
		} catch ( MessagingException e) {
			throw new UserException(ErrorCodes.USER_EMAIL_ERROR , "用户邮箱错误");
		} catch (  SQLException  e) {
			throw new UserException(ErrorCodes.SERVER_INNER_ERROR , e.getMessage());
		}  
	}

	@Override
	public String save(User user) throws UserException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			String id = HashUtils.uuid();
			user.setId(id);
			user.setCreateTime(new Timestamp(System.currentTimeMillis()));
			AccountService.instance.createAccount( statement , user.getId());
			DbOperations.instance.save(connection, user, true);
			connection.commit();
			Global.queue.publish(UserSubjects.USER_ADDED, user);
			return id;
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("user", user), e);
			throw new UserException("save user failed!", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}

	}


	public boolean hasBind(String uid, String type) throws UserException {
		StringBuilder sql = new StringBuilder(44);
		sql.append("select count(*) from User where");
		if ("qq".equals(type)) {
			sql.append(" qqUid='");
			sql.append(uid);
			sql.append('\'');

		} else if ("weibo".equals(type)) {
			sql.append(" weiboUid='");
			sql.append(uid);
			sql.append('\'');

		} else {
			throw new UserException("bind type error:type=" + type);
		}
		try {
			return ((long) QuickDB.getSingle(sql.toString()) > 0) ? true : false;
		} catch (SQLException e) {
			throw new UserException(e);
		}
	}

	@Override
	public void update(String userId, Map<String, Object> newVules) throws UserException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			Map<String, Object> conditionMap = new HashMap<>(1);
			String skills = (String)newVules.get("skillss");
			List<Map<String, Object>> list = new ArrayList<>();
			if(null != skills){
				JSONArray skillsJsonArray = JSONArray.parseArray(skills);
				for(int i = 0; i < skillsJsonArray.size(); i++){
					Map<String, Object> skillsMap = new HashMap<>();
					JSONObject jsonObject = skillsJsonArray.getJSONObject(i);
					skillsMap.put("name", jsonObject.getString("name"));
					skillsMap.put("description", jsonObject.getString("description"));
					skillsMap.put("userId", userId);
					list.add(skillsMap);
				}
				String deleteUserSkillsSql = "delete from UserSkills where userId='" +userId +"'";
				if(log.isDebugEnabled()){
					log.debug(deleteUserSkillsSql);
				}
				statement.executeUpdate(deleteUserSkillsSql);
				DbMapOperations.instance.saveMaps(connection, "UserSkills", list);
				newVules.remove("skillss");
			}
			conditionMap.put("id", userId);
			DbOperations.instance.update(connection, "User", conditionMap, newVules);
			connection.commit();
			connection.setAutoCommit(true);
			// publish user update message
			Map<String, Object> message = new HashMap<>();
			message.put("userId", userId);
			message.put("skillss", list);
			Global.queue.publish(UserSubjects.USER_UPDATED, message);
		} catch (SQLException e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("userId", userId, "newVules", newVules), e);
			throw new UserException("save user failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	@Override
	public void checkUserToken(String userId, String userToken) throws UserException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "select count(*) from User where id='" + userId + "' and userToken='" + userToken + "'";
			int count = ResultSetMap.mapInt(statement.executeQuery(sql));
			if (count <= 0) {
				throw new UserException("userId not correspond to userToken");
			}
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId, "userToken", userToken), e);
			throw new UserException("checkUserToken userToken failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	public List<User> query(Map<String, Object> args) throws UserException {
		Connection connection = null;

		try {
			connection = Connections.instance.get();
			// Map<String, Object> newProperties = new HashMap<>();
			String sql = SimpleSqlFactory.createNamedQuerySql(ObjectTableInfo.getTableName(User.class), null, args.keySet());

			return DbOperations.instance.query(connection, sql, args, User.class, new SimpleObjectMapping(), 1);
		} catch (SQLException e) {
			log.error(LogUtils.format("args", args), e);
			throw new UserException("save user failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	@Override
	public User get(String userId) throws UserException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			 Map<String, Object> newProperties = new HashMap<>();
			statement = connection.createStatement();
			User user = DbOperations.instance.get(connection, userId, User.class);
			if(null == user){
				throw new UserException(ErrorCodes.USER_NOT_EXISTS , "user:"+userId+" not exists!");
			}
			String userSkillSql = "select id,name,description from UserSkills where userId='" +userId+ "'";
			List<Map<String, Object>> userSkills = ResultSetMap.maps(statement.executeQuery(userSkillSql));
			user.setSkillss(userSkills);
			// if(null== user){
			// return null;
			// }
			// fillUserImages(user);
			return user;
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId), e);
			throw new UserException("save user failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	private final Set<String> userPublicInfoKeys = new HashSet<>();
	{
		userPublicInfoKeys.add("id");
		userPublicInfoKeys.add("name");
		userPublicInfoKeys.add("mainPicture");
		userPublicInfoKeys.add("address");
		userPublicInfoKeys.add("company");
		userPublicInfoKeys.add("skills");
		userPublicInfoKeys.add("occupation");
//		userPublicInfoKeys.add("credit");
		userPublicInfoKeys.add("createTime");
		userPublicInfoKeys.add("sex");
		userPublicInfoKeys.add("sponsorScore");
		userPublicInfoKeys.add("completeScore");
		userPublicInfoKeys.add("latestLatitude");
		userPublicInfoKeys.add("latestLongtitude");
		userPublicInfoKeys.add("mainPictureSmall");
		userPublicInfoKeys.add("mainPictureMid");
		userPublicInfoKeys.add("mainPictureBig");
		
	}

	public Map<String, Object> getUserInfo(String userId, Set<String> keys) throws UserException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			Set<String> remainKeys = null;
			if(null!=keys && !keys.isEmpty()){
				remainKeys = CollectionUtils.intersect(keys, userPublicInfoKeys);
				if (remainKeys.isEmpty()) {
					return Collections.EMPTY_MAP;
				}
			}else{
				remainKeys = userPublicInfoKeys;
			}
			String sql = "select " + StringUtils.link(remainKeys, ",") + " from User where id='" + userId + "'";
			Map<String, Object> result = ResultSetMap.map(statement.executeQuery(sql));
			String userSkillSql = "select id,name,description from UserSkills where userId='" +userId+ "'";
			List<Map<String, Object>> userSkills = ResultSetMap.maps(statement.executeQuery(userSkillSql));
			result.put("skillss", userSkills);
			return result;
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId, "keys", keys), e);
			throw new UserException("save user failed!", e);
		} finally {
			closeConnection(connection);
		}
	}


	@Override
	public boolean checkExists(String uid, String accessToken, String type) throws UserException {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from User where ");
		if("qq".equals(type)){
			sql.append("qqUid='");
			sql.append(uid);
			sql.append('\'');
			sql.append(" and qqAccessToken='");
			sql.append(uid);
			sql.append('\'');
		}
		if("weibo".equals(type)){
			sql.append("weiboUid='");
			sql.append(uid);
			sql.append('\'');
			sql.append(" and weiboAccessToken='");
			sql.append(uid);
			sql.append('\'');
		}
		
		try {
			if(0==(int)QuickDB.getSingle(sql.toString())){
				return false;
			}else{
				return true;
			}
		} catch (SQLException e) {
			throw new UserException("failed to check user exists",e);
		}
		
	}

	// public void fillUserImages(User user) throws UserException{
	// if(StringUtils.isNotEmpty(user.getMainPictureId())){
	// try {
	// Picture picture =
	// PictureService.instance.getPicture(user.getMainPictureId());
	// if(null == picture){
	// return;
	// }
	// user.setMainPicture(picture.getRemotePath());
	// } catch (PictureServiceException e) {
	// throw new UserException(e);
	// }
	// }
	// }
	
	public void modifyUserPassword(String userId, String oldPassword, String newPassword, String reNewPassword) throws UserException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String userSql = "select password from User where id='" + userId + "'";
			if(log.isDebugEnabled()){
				log.debug(userSql);
			}
			Map<String, Object> user = ResultSetMap.map(statement.executeQuery(userSql));
			String rawPassword = (String)user.get("password");
			if(!oldPassword.equals(rawPassword)){
				log.debug("user password was not consistent! userId:'" + userId + "'");
				throw new UserException("user password was not consistent! userId:'" + userId + "'");
			}else if(!newPassword.equals(reNewPassword)){
				log.error("new password was not consistent! userId:'" + userId + "'");
				throw new UserException("new password was not consistent! userId:'" + userId + "'");
			}else{
				String updateUserSql = "update User set password='" + newPassword + "' where id='" + userId + "'";
				if(log.isDebugEnabled()){
					log.debug(updateUserSql);
				}
				statement.executeUpdate(updateUserSql);
			}
		}catch(Exception e){
			log.error("modify user password error! userId:'" + userId + "'");
			throw new UserException("modify user password error! userId:'" + userId + "'", e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public void updateUserLocation(String userId, Double longtitude, Double latitude) throws UserException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
//			User user = UserProxy.getUserInfo(userId);
//			Double oldLongtitude = user.getLatestLongtitude(); 
//			Double oldLatitude = user.getLatestLatitude();
			String updateUserLocationSql = "update User set latestLongtitude=" + longtitude + ", latestLatitude=" + latitude + " where id='" +userId +"'";//distance(" + oldLongtitude + "," + oldLatitude + "," + longtitude +"," + latitude + ")>=1000";
			if(log.isDebugEnabled()){
				log.debug(updateUserLocationSql);
			}
			statement.executeUpdate(updateUserLocationSql);
			UserProxy.refreshUserCaches(userId);
		}catch(Exception e){
			log.error("update user location failed! userId:'" + userId + "'");
			throw new UserException("update user location failed! userId:'" + userId + "'");
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	public void deleteUser(String userId, String userToken) throws UserException {
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "delete from User where id='"+StringEscapeUtils.escapeSql(userId)+"' and userToken='"+StringEscapeUtils.escapeSql(userToken)+"' and hasCompleteProfile=true";
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			statement.executeUpdate(sql);
			UserProxy.expireUser(userId);
		}catch(Exception e){
			log.error("delete user failed! userId:'" + userId + "'");
			throw new UserException("delete user failed! userId:'" + userId + "'");
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	
	public boolean checkEmailPassword(String email , String pw,Integer type){
		try {
			return 0<(long)QuickDB.getSingle("select count(*) from User where email='"+StringEscapeUtils.escapeSql(email)+"' and password='"+StringEscapeUtils.escapeSql(pw)+"'"+(null==type?"":" and type>="+type));			
		
		} catch (SQLException e) {
			return false;
		}
	}
	
	
	/**
	 * 
	 * 
	 * @param userId
	 * @return { name , icon , nick, phone , totalTranactionMoney,rank,tags,applyCompleteCount , comleteDegree
	 * @throws HzException
	 */
	public Map<String, Object> card(String userId) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "select u.id,u.name , u.mainPictureSmall ,u.rank,u.nick ,u.name ,u.telephone,u.applyCompleteCount,u.applyCompleteCount/(u.applyCompleteCount+u.applyCloseCount) as comleteDegree ,(select sum(r.price) from Requirement r where r.selectedCandidate='"+userId+"' and r.status="+RequirementStatus.complete+") as totalTranactionMoney ,(select GROUP_CONCAT(cast(t.tnum as char)) from UserTags t where t.userId='"+userId+"') as tags from User u where u.id='"+userId+"' limit 1";
			return ResultSetMap.map(statement.executeQuery(sql));
		}catch(Exception e){
			log.error("user id:"+userId, e);
			throw new HzException("user id:"+userId, e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	/**
	 * 申请认证 rank 0：未申请 1：申请认证：2：已通过认证
	 * @param aProfile
	 * @throws HzException
	 */
	public void applyAuth(AProfile aProfile) throws HzException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String checkAuthSql = "select count(*) from AProfile where userId='"+aProfile.getUserId()+"'";
			int checkAuthCount = ResultSetMap.mapInt(statement.executeQuery(checkAuthSql));
			if(0 != checkAuthCount){
				log.debug("not applyauth repeatability");
				throw new HzException(ErrorCodes.RA, "not applyauth repeatability");
			}
			StringBuilder sql = new StringBuilder();
			sql.append("insert into AProfile(userId, name, sex, company, address, idcard, telephone,reason, createTime) values('");
			sql.append(aProfile.getUserId());
			sql.append("','");
			sql.append(aProfile.getName());
			sql.append("',");
			sql.append(aProfile.getSex());
			sql.append(",'");
			sql.append(aProfile.getCompany());
			sql.append("','");
			sql.append(aProfile.getAddress());
			sql.append("','");
			sql.append(aProfile.getIdcard());
			sql.append("','");
			sql.append(aProfile.getTelephone());
			sql.append("','");
			sql.append(aProfile.getReason());
			sql.append("','");
			sql.append(new Timestamp(System.currentTimeMillis()));
			sql.append("')");
			if(log.isDebugEnabled()){
				log.debug(sql.toString());
			}
			QuickDB.insert(sql.toString());
			String updateSql = "update User set rank=1 where id='"+aProfile.getUserId()+"'";
			if(log.isDebugEnabled()){
				log.debug(updateSql);
			}
			statement.executeUpdate(updateSql);
		}catch(SQLException e){
			log.error("user id:"+aProfile.getUserId(), e);
			throw new HzException("user id:"+aProfile.getUserId(), e);
		}
		finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	


}
