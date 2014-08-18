package cn.zthz.tool.requirement;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.actor.rest.ErrorCodes;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.Formats;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.ResourceUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.common.WithBlob;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.NamedParameterStatement;
import cn.zthz.tool.db.ObjectTableInfo;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.db.SimpleObjectMapping;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.share.QqShare;
import cn.zthz.tool.share.ShareAction;
import cn.zthz.tool.share.WeiboShare;
import cn.zthz.tool.user.User;
import cn.zthz.tool.user.UserTypes;

public class RequirementOperations extends AbstractService {
	private static final Log log = LogFactory.getLog(RequirementOperations.class);

	public static final RequirementOperations instance = new RequirementOperations();

	public String save(Requirement requirement) throws UserRequirementException {
		Connection connection = null;

		try {
			connection = Connections.instance.get();
			String id = HashUtils.uuid();
			if (StringUtils.isNotEmpty(id)) {
				requirement.setId(id);
			}
			requirement.setCreateTime(new Timestamp(System.currentTimeMillis()));
			DbOperations.instance.save(connection, requirement, true);
			return id;
		} catch (SQLException e) {
			log.error(LogUtils.format("user", requirement), e);
			throw new UserRequirementException("save requirement failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}

		}
	}
	
	private static final List<String> R_SAVE_KEYS = new ArrayList<>(26); 
	static{
		R_SAVE_KEYS.add("id");
		R_SAVE_KEYS.add("title");
		R_SAVE_KEYS.add("description");
		R_SAVE_KEYS.add("mainPicture");
		R_SAVE_KEYS.add("expire");
		R_SAVE_KEYS.add("price");
		R_SAVE_KEYS.add("address");
		R_SAVE_KEYS.add("type");
		R_SAVE_KEYS.add("hasPrivateMessageContact");
		R_SAVE_KEYS.add("hasPhoneContact");
		R_SAVE_KEYS.add("status");
		R_SAVE_KEYS.add("longitude");
		R_SAVE_KEYS.add("latitude");
		R_SAVE_KEYS.add("viewCount");
		R_SAVE_KEYS.add("createTime");
		R_SAVE_KEYS.add("userId");
		R_SAVE_KEYS.add("hasMandate");
		R_SAVE_KEYS.add("publishAddress");
		R_SAVE_KEYS.add("publishLongitude");
		R_SAVE_KEYS.add("publishLatitude");
//		R_SAVE_KEYS.add("completeScore");
//		R_SAVE_KEYS.add("sponsorComment");
//		R_SAVE_KEYS.add("sponsorScore");
		R_SAVE_KEYS.add("mainPictureSmall");
		R_SAVE_KEYS.add("mainPictureMid");
		R_SAVE_KEYS.add("mainPictureBig");
		R_SAVE_KEYS.add("sound");
		R_SAVE_KEYS.add("soundTime");
	}

	public String publish(Requirement requirement) throws UserRequirementException {
		Connection connection = null;
		InputStream sound  = null;
		Statement st = null;
		NamedParameterStatement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setReadOnly(false);
			connection.setAutoCommit(false);
			st = connection.createStatement();
			String id = HashUtils.uuid();
			requirement.setId(id);
			requirement.setStatus(RequirementStatus.published);
			requirement.setCreateTime(new Timestamp(System.currentTimeMillis()));
			if (requirement.getHasMandate()) {
				AccountService.instance.prePay(st, requirement.getUserId(), Formats.formatMoney(requirement.getPrice()),
						requirement.getId());
			}
//			DbOperations.instance.save(connection, requirement, true);
			String sql = "insert Requirement("+StringUtils.join(R_SAVE_KEYS,",")+") values(:"+StringUtils.join(R_SAVE_KEYS,",:")+")";
			statement = new NamedParameterStatement(connection, sql );
			statement.setString("id",requirement.getId());
			statement.setString("title",requirement.getTitle());
			statement.setString("description",requirement.getDescription());
			statement.setString("mainPicture",requirement.getMainPicture());
			statement.setTimestamp("expire",requirement.getExpire());
			statement.setBigDecimal("price",requirement.getPrice());
			statement.setString("address",requirement.getAddress());
			statement.setInt("type",requirement.getType());
			statement.setBoolean("hasPrivateMessageContact",null==requirement.isHasPrivateMessageContact()?false : requirement.isHasPrivateMessageContact());
			statement.setBoolean("hasPhoneContact",null==requirement.isHasPhoneContact()?false : requirement.isHasPhoneContact());
			statement.setInt("status",requirement.getStatus());
			statement.setDouble("longitude",requirement.getLongitude());
			statement.setDouble("latitude",requirement.getLatitude());
			statement.setInt("viewCount",requirement.getViewCount());
			statement.setTimestamp("createTime",requirement.getCreateTime());
			statement.setString("userId",requirement.getUserId());
			statement.setBoolean("hasMandate",requirement.getHasMandate());
			statement.setString("publishAddress",requirement.getPublishAddress());
			statement.setDouble("publishLongitude",requirement.getPublishLongitude());
			statement.setDouble("publishLatitude",requirement.getPublishLatitude());
//			statement.setInt("completeScore",requirement.getCompleteScore());
//			statement.setInt("sponsorScore",requirement.getSponsorScore());
			statement.setString("mainPictureSmall",requirement.getMainPictureSmall());
			statement.setString("mainPictureMid",requirement.getMainPictureMid());
			statement.setString("mainPictureBig",requirement.getMainPictureBig());
			statement.setInt("soundTime",null == requirement.getSoundTime()?0: requirement.getSoundTime());
			if(null!=requirement.getSound()){
				sound = new FileInputStream(requirement.getSound());
			}
			statement.setBlob("sound",sound );
			statement.executeUpdate();
			connection.commit();
			Global.queue.publish(QueueSubjects.REQUIREMENT_PUBLISHED, requirement);
			return id;
		} catch (HzException e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("user", requirement), e);
			throw new UserRequirementException("save requirement failed!", e);
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("user", requirement), e);
			throw new UserRequirementException("save requirement failed!", e);
		} finally {
			ConnectionUtils.close(sound);
			ConnectionUtils.closeStatement(st);
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	/**
	 * 查看任务详情
	 * 
	 * @param requirementId
	 * @return
	 * @throws UserRequirementException
	 */
	public Map<String, Object> viewRequirement(String requirementId, String userId) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		Map<String, Object> result = null;// new HashMap<String, Object>(4);
		int startIndex = 0;
		int pageSize = 10;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			//(select count(*) from Requirement where userId='"+userId+"' and status="+RequirementStatus.complete+") as crc 
			String viewRequirementSql = "select r.id, r.title , r.description , r.price, r.selectedCandidate, r.expire, r.address, r.hasMandate,r.type,r.hasPrivateMessageContact,r.hasPhoneContact,r.status,r.longitude,r.latitude,r.createTime,r.viewCount,r.userId,r.mainPictureSmall , r.mainPictureMid,r.mainPictureBig, r.soundTime,u.id as userId, u.name as userName , u.mainPicture as userPicture , u.mainPictureSmall as userPictureSmall, u.mainPictureMid as userPictureMid, u.mainPictureBig as userPictureBig,u.latestLongtitude, u.latestLatitude,u.telephone, u.completeScore as userCompleteScore, u.sponsorScore as userSponsorScore,(select count(*)>0 from RequirementCandidate rcc where rcc.userId='"+userId+"' and rcc.requirementId='"+requirementId+"') as hasCandidated ,!isnull(s.id) as hasStore from (Requirement r left join User u on u.id=r.userId) left join Store s on r.id=s.requirementId and s.userId='" + userId +"' where r.id='"
					+ requirementId + "' limit 1"; 
			if (log.isDebugEnabled()) {
				log.debug(viewRequirementSql);
			}
			// Map<String, Object> result = new HashMap<>(1);
			// 根据requirementId查看任务详细信息
			result = ResultSetMap.map(statement.executeQuery(viewRequirementSql));
			Object h = result.get("hasPhoneContact");
			boolean hasPhoneContact = (null==h)?false : (boolean)h;
			if(!hasPhoneContact){
//				String telephone = (String)result.get("telephone");
				result.put("telephone","");
			}
			// list.add(result);
			// 查看任务的候选者
			Map<String, Object> candidates = RequirementCandidateService.instance.getCandidates(statement, requirementId, startIndex,
					pageSize);
			result.put("candidates", candidates);
			// list.add(candidates);
			// 查看任务的评论
			Map<String, Object> comments = RequirementCommentService.instance.getComments(statement, requirementId, startIndex, pageSize);
			result.put("comments", comments);
			// list.add(comments);
			// 增加访问历史记录
			RequirementVisitorService.instance.insertViewHistoryRecord(statement, requirementId, userId);
//			result.put("viewCount", viewCounts);
			// 查看承接者的信息
			Map<String, Object> selectedCandidate = RequirementCandidateService.instance.getSelectedCandidate(statement, requirementId);
//			Object count = selectedCandidate.get("crc");
//			if(0 != (long)count){
//				result.put("selectedCandidate", selectedCandidate);
//			}else{
//				result.put("selectedCandidate", Collections.EMPTY_MAP);
//			}
			result.put("selectedCandidate", selectedCandidate);
			// 查看任务的访问者
			Map<String, Object> visitors = RequirementVisitorService.instance.getVisitors(statement, requirementId, startIndex, pageSize);
			result.put("visitors", visitors);
			incrViewCount(statement, requirementId , userId);
			Vtop.instance.update(statement, requirementId, userId);
			// list.add(visitors);
			connection.commit();
//			//share微博
//			String title = (String)result.get("title");
//			Object desc = result.get("description");
//			String description = (null==desc)? null : (String)desc;
//			String body = title;
//			Object img = result.get("mainPicture");
//			String images = (null==img)? null : (String)img;
//			Map<String, Object> params = new HashMap<String, Object>();
//			if(images != null){
//				byte[] pic = HttpUtils.doGetInBytes(images);
//				params.put("pic", pic);
//			}
//			String url = GlobalConfig.get("share.requirement.url") + requirementId;
//			params.put("body", body);
//			params.put("images", images);
//			params.put("title", title);
//			params.put("summary", description);
//			params.put("url", url);
//			Object qq = result.get("qqAccessToken");
//			String qqAccessToken = (null==qq)? "" : (String)qq;
//			Object WB = result.get("weiboAccessToken");
//			String weiboAccessToken = (null==WB)? "" : (String)WB;
//			int shareqq = (null!=shareQQ)?(int)shareQQ:0;
//			int sharewb = (null!=shareWB)?(int)shareWB:0;
//			if(ShareAction.SHARE_QQ == shareqq){
//				QqShare.instance.share(qqAccessToken, params);
//			}
//			if(ShareAction.SHARE_WB == sharewb){
//				WeiboShare.instance.share(weiboAccessToken, params);
//			}
			return result;
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error("View the requirementId:'" + requirementId + "',error!");
			throw new UserRequirementException("View the requirementId:'" + requirementId + "',error!", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	private void incrViewCount(Statement statement , String rid , String uid) throws SQLException{
		String sql = "update Requirement set viewCount=viewCount+1 where id='"+rid+"' and userId!='"+uid+"'";
		if(log.isDebugEnabled()){
			log.debug(sql);
		}
		statement.executeUpdate(sql);
	}

	public void update(String requirementId, Map<String, Object> newVules) throws UserRequirementException {
		Connection connection = null;

		try {
			connection = Connections.instance.get();
			Map<String, Object> conditionMap = new HashMap<>(1);
			conditionMap.put("id", requirementId);
			DbOperations.instance.update(connection, ObjectTableInfo.getTableName(Requirement.class), conditionMap, newVules);
		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId, "newVules", newVules), e);
			throw new UserRequirementException("update requirement failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	public List<Requirement> getUserRequirements(String userId, Integer status, Integer type, int startIndex, int pageSize)
			throws UserRequirementException {
		Connection connection = null;

		try {
			connection = Connections.instance.get();
			StringBuilder sql = createQueryHeader();
			sql.append(" where r.userId='");
			sql.append(userId);
			sql.append("'");
			if (null != status) {
				sql.append(" and r.status=");
				sql.append(status);
			}
			if (null != type) {
				sql.append(" and r.type=");
				sql.append(type);
			}
			sql.append(" order by r.createTime desc limit ");
			sql.append(startIndex);
			sql.append(pageSize);
			Map<String, Object> args = null;
			return DbOperations.instance.query(connection, sql.toString(), args, Requirement.class, SimpleObjectMapping.instance, pageSize);
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId, "startIndex", startIndex, "pageSize", pageSize), e);
			throw new UserRequirementException("get user requirements requirement failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	public static enum QueryOrder {
		newest, hotest, nearest,price,mandate,credit
	}

	public List<Requirement> query(Map<String, Object> conditionArgs, Map<String, Object> orderArgs, QueryOrder queryOrder, int startIndex,
			int pageSize) throws UserRequirementException {
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			String sql = createQuerySql(conditionArgs, queryOrder, startIndex,
					pageSize);
			// System.out.println(sql);
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			Map<String, Object> args = new HashMap<>();
			if (null != conditionArgs)
				args.putAll(conditionArgs);
			if (null != orderArgs)
				args.putAll(orderArgs);
			return DbOperations.instance.query(connection, sql, args, Requirement.class, SimpleObjectMapping.instance, pageSize);
		} catch (SQLException e) {
			log.error(LogUtils.format("queryOrder", queryOrder, "startIndex", startIndex, "pageSize", pageSize), e);
			throw new UserRequirementException("qurey requirement failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}
	
	public List<Map<String, Object>> query2(Map<String, Object> conditionArgs, Map<String, Object> orderArgs, QueryOrder queryOrder, int startIndex,
			int pageSize) throws UserRequirementException {
		Connection connection = null;
		NamedParameterStatement namedParameterStatement = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try{
			connection = Connections.instance.get();
			//long t1 = System.currentTimeMillis();
			String sql = createQuerySql(conditionArgs , queryOrder, startIndex,pageSize);
//			String sql = createQuerySql(null != conditionArgs ? conditionArgs.keySet() : Collections.EMPTY_SET, queryOrder, startIndex,
//					pageSize);
//			long t2 = System.currentTimeMillis();
//			if(log.isDebugEnabled()){
//				log.debug("-------------------estimated Time1---------------------: " + (t2 - t1));
//			}
			if(log.isDebugEnabled()){
				log.debug(sql);
			}
			Map<String, Object> args = new HashMap<>();
			if (null != conditionArgs)
				args.putAll(conditionArgs);
//			if(null != conditionArgs.get("status")){
//				args.put("status", (Integer)conditionArgs.get("status"));
//			}
			if (null != orderArgs)
				args.putAll(orderArgs);
//			long t3 = System.currentTimeMillis();
			namedParameterStatement = new NamedParameterStatement(connection, sql);
			if(log.isDebugEnabled()){
				log.debug(namedParameterStatement.toString());
			}
//			long t4 = System.currentTimeMillis();
//			if(log.isDebugEnabled()){
//				log.debug("-------------------estimated Time2---------------------: " + (t4 - t3));
//			}
			Set<Map.Entry<String, Object>> set = args.entrySet();
			for(Map.Entry<String, Object> entry: set){
				namedParameterStatement.setObject(entry.getKey(), entry.getValue());
			}
			long t5 = System.currentTimeMillis();
			results = ResultSetMap.maps(namedParameterStatement.executeQuery());
			long t6 = System.currentTimeMillis();
			if(log.isDebugEnabled()){
				log.debug("-------------------estimated Time3---------------------: " + (t6 - t5));
			}
			if(!results.isEmpty()){
				List<String> rids = new ArrayList<String>(results.size());
//				long t7 = System.currentTimeMillis();
				for(Map<String, Object> map : results){
					rids.add((String)map.get("id"));
				}
//				long t8 = System.currentTimeMillis();
//				if(log.isDebugEnabled()){
//					log.debug("-------------------estimated Time4---------------------: " + (t8 - t7));
//				}
//				String rvsql = "select u.mainPictureSmall as visitorPictureSmall,r.requirementId, u.id as visitorId from  RequirementVisitor r left join User u  on u.id=r.userId where r.requirementId in ('"+StringUtils.join(rids,"','")+"') and u.mainPictureSmall is not null order by r.viewTime desc";
				String rvsql = "select u.mainPictureSmall as visitorPictureSmall,v.requirementId, u.id as visitorId from  VTop v left join User u  on u.id=v.visitorId where v.requirementId in ('"+StringUtils.join(rids,"','")+"') and u.mainPictureSmall is not null order by v.ts desc";
				Map<String, List<Map<String, Object>>> rvs = new HashMap<>();
//				long t9 = System.currentTimeMillis();
				List<Map<String, Object>> vs = ResultSetMap.maps(namedParameterStatement.statement.executeQuery(rvsql));
//				long t10 = System.currentTimeMillis();
//				if(log.isDebugEnabled()){
//					log.debug("-------------------estimated Time5---------------------: " + (t10 - t9));
//				}
//				long t11 = System.currentTimeMillis();
				for (Map<String, Object> map : vs) {
					String rid = (String) map.get("requirementId");
					if(rvs.containsKey(rid)){
//						if(rvs.get(rid).size()>=3){
//							continue;
//						}
						rvs.get(rid).add(map);
					}else{
						List<Map<String, Object>> irvs = new LinkedList<>();
						irvs.add(map);
						rvs.put(rid , irvs);
					}
				}
//				long t12 = System.currentTimeMillis();
//				if(log.isDebugEnabled()){
//					log.debug("-------------------estimated Time6---------------------: " + (t12 - t11));
//				}
//				long t13 = System.currentTimeMillis();
				for(Map<String, Object> map : results){
					map.put("vistors" ,rvs.get(map.get("id")));
				}
//				long t14 = System.currentTimeMillis();
//				if(log.isDebugEnabled()){
//					log.debug("-------------------estimated Time7---------------------: " + (t14 - t13));
//				}
			}
			return results;
			
		}catch(Exception e){
			log.error(LogUtils.format("queryOrder", queryOrder, "startIndex", startIndex, "pageSize", pageSize), e);
			throw new UserRequirementException("qurey requirement failed!", e);
		}finally{
			ConnectionUtils.closeStatement(namedParameterStatement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	public List<Map<String, Object>> getUserRequirements(String targetUserId, Integer status) throws SQLException, UserRequirementException{
		Connection connection = null;
		Statement statement = null;
		List<Map<String, Object>> results = new ArrayList<>();
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String targetUserRequirementsSql = "select r.id, r.type,r.title,r.hasMandate, r.price, r.expire, r.viewCount, r.userId, r.longitude, r.latitude, r.selectedCandidate , r.createTime, u.name as userName , u.mainPicture as userPicture,u.mainPictureSmall as userPictureSmall, u.mainPictureMid as userPictureMid, u.mainPictureBig as userPictureBig," +
					"u.credit as userCredit ,r.mainPicture, (select count(userId) from RequirementCandidate rc where r.id=rc.requirementId ) as applyCount from Requirement r left join User u on r.userId=u.id where r.userId='" + targetUserId + "' and r.status=" + status;
			if(log.isDebugEnabled()){
				log.debug(targetUserRequirementsSql);
			}
			results = ResultSetMap.maps(statement.executeQuery(targetUserRequirementsSql));
			return results;
		}catch(Exception e){
			log.error("query target user requirement failed! targertUserId:'" + targetUserId + "', status:" + status);
			throw new UserRequirementException("query target user requirement failed! targertUserId:'" + targetUserId + "', status:" + status, e);
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	
	private String types(String tname,Integer ptype){
		if(ptype<10){
			int btype = ptype*100;
			return "("+tname+">="+btype+" and "+tname+"<"+(btype+100)+" or "+tname+"=:type)";
		}
		return tname+"=:type";
	}
	

	/**
	 * select r.* ,u.name as userName , u.mainPicture as
	 * userPicture,r.mainPicture from Requirement r left join User u on
	 * r.userId=u.id where u.id='ff8080813c05aec1013c05aec3f60001'
	 * 
	 * @param conditionKeys
	 * @param queryOrder
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	private String createQuerySql(Map<String, Object> conditionArgs, QueryOrder queryOrder, int startIndex, int pageSize) {
		Set<String> conditionKeys = (null != conditionArgs ? conditionArgs.keySet() : Collections.EMPTY_SET);
//		StringBuilder sql = createQueryHeader();
		StringBuilder sql = new StringBuilder();
		StringBuilder csql = createQueryHeader();
		sql.append("select r.id, r.type,r.title,r.hasMandate, r.price, r.expire, r.viewCount, r.userId, r.longitude, r.latitude, r.selectedCandidate , r.createTime, r.mainPictureSmall,r.mainPictureMid, r.mainPictureBig, r.status, u.name as userName , u.mainPicture as userPicture, u.mainPictureSmall as userPictureSmall ,u.mainPictureMid as userPictureMid ,u.mainPictureBig as userPictureBig ,u.credit as userCredit,  u.completeScore ,r.mainPicture, r.mainPictureSmall, r.mainPictureMid, r.mainPictureBig, (select count(userId) from RequirementCandidate rc where r.id=rc.requirementId ) as applyCount from (");
		sql.append("select * from Requirement ri where 1=1 ");
		if (null != conditionKeys && !conditionKeys.isEmpty()) {
			for (String key : conditionKeys) {
				if("type".equals(key)){
					sql.append(" and ");
					sql.append(types("ri.type",(Integer)conditionArgs.get("type")));
					continue;
				}
				sql.append(" and ri.");
				sql.append(key);
				sql.append("=:");
				sql.append(key);
			}
		}
		switch (queryOrder) {
		case price:
			sql.append(" order by ri.pri desc, ri.price desc, ri.createTime desc ,ri.viewCount desc ");
			break;
		case credit:
			csql.append("where 1=1 ");
			if (null != conditionKeys && !conditionKeys.isEmpty()) {
				for (String key : conditionKeys) {
					csql.append(" and r.");
					csql.append(key);
					csql.append("=:");
					csql.append(key);
				}
			}
			csql.append(" order by r.pri desc, u.completeScore desc,r.createTime desc ,r.viewCount desc ");
			csql.append("limit ");
			csql.append(startIndex);
			csql.append(',');
			csql.append(pageSize);
			return csql.toString();
		case mandate:
			sql.append(" order by ri.pri desc, ri.hasMandate desc ,ri.createTime desc ,ri.viewCount desc ");
			break;
		case newest:
			sql.append(" order by ri.pri desc, ri.createTime desc ,ri.viewCount desc ");
			break;
		case hotest:
			sql.append(" order by ri.pri desc, ri.viewCount desc,ri.createTime desc ");
			break;
		case nearest:
			sql.append(" and ri.longitude is not null and ri.latitude is not null");
//			sql.append(" order by power((r.longitude-:longitude) ,2)+ power((r.latitude-:latitude),2) asc, r.createTime desc ");
			sql.append(" order by ri.pri desc, distance(ri.longitude , ri.latitude , :longitude,:latitude)  asc, ri.createTime desc  ");
			break;

		}
		sql.append("limit ");
		sql.append(startIndex);
		sql.append(',');
		sql.append(pageSize);
		sql.append(") r left join User u on r.userId=u.id");
		switch (queryOrder) {
		case price:
			sql.append(" order by r.pri desc, r.price desc, r.createTime desc ,r.viewCount desc ");
			break;
		case mandate:
			sql.append(" order by r.pri desc, r.hasMandate desc ,r.createTime desc ,r.viewCount desc ");
			break;
		case newest:
			sql.append(" order by r.pri desc, r.createTime desc ,r.viewCount desc ");
			break;
		case hotest:
			sql.append(" order by r.pri desc, r.viewCount desc,r.createTime desc ");
			break;
		case nearest:
			sql.append(" order by r.pri desc, distance(r.longitude , r.latitude , :longitude,:latitude)  asc, r.createTime desc  ");
			break;
		}
		return sql.toString();
	}

	private StringBuilder createQueryHeader(String c ,String order,int si, int ps) {
		StringBuilder sql = new StringBuilder();
		sql.append("select r.id, r.type,r.title,r.hasMandate, r.price, r.expire, r.viewCount, r.userId, r.longitude, r.latitude, r.selectedCandidate , r.createTime, r.mainPictureSmall,r.mainPictureMid, r.mainPictureBig, u.name as userName , u.mainPicture as userPicture, u.mainPictureSmall as userPictureSmall ,u.mainPictureMid as userPictureMid ,u.mainPictureBig as userPictureBig ,u.credit as userCredit,  u.completeScore ,r.mainPicture, r.mainPictureSmall, r.mainPictureMid, r.mainPictureBig, (select count(userId) from RequirementCandidate rc where r.id=rc.requirementId ) as applyCount from (select * from Requirement r ");
		if(StringUtils.isNotEmpty(c)){
			sql.append(c);
		}
		if(StringUtils.isNotEmpty(order)){
			sql.append(order);
		}
		sql.append(" limit ");
		sql.append(si);
		sql.append(',');
		sql.append(ps);
				
		sql.append(") left join User u on r.userId=u.id ");
		return sql;
	}
	private StringBuilder createQueryHeader2() {
		StringBuilder sql = new StringBuilder();
		sql.append("select r.id, r.type,r.title,r.hasMandate, r.price, r.expire, r.viewCount, r.userId, r.longitude, r.latitude, r.selectedCandidate , r.createTime, r.mainPictureSmall,r.mainPictureMid, r.mainPictureBig, u.name as userName , u.mainPicture as userPicture, u.mainPictureSmall as userPictureSmall ,u.mainPictureMid as userPictureMid ,u.mainPictureBig as userPictureBig ,u.credit as userCredit,  u.completeScore ,r.mainPicture, r.mainPictureSmall, r.mainPictureMid, r.mainPictureBig, (select count(userId) from RequirementCandidate rc where r.id=rc.requirementId ) as applyCount from (select * from Requirement where 1=1 order by xx limit 0 , 20) r left join User u on r.userId=u.id ");
		return sql;
	}
	private StringBuilder createQueryHeader() {
		StringBuilder sql = new StringBuilder();
		sql.append("select r.id, r.type,r.title,r.hasMandate, r.price, r.expire, r.viewCount, r.userId, r.longitude, r.latitude, r.selectedCandidate , r.createTime, r.mainPictureSmall,r.mainPictureMid, r.mainPictureBig, u.name as userName , u.mainPicture as userPicture, u.mainPictureSmall as userPictureSmall ,u.mainPictureMid as userPictureMid ,u.mainPictureBig as userPictureBig ,u.credit as userCredit,  u.completeScore ,r.mainPicture, r.mainPictureSmall, r.mainPictureMid, r.mainPictureBig, (select count(userId) from RequirementCandidate rc where r.id=rc.requirementId ) as applyCount from Requirement r left join User u on r.userId=u.id ");
		return sql;
	}

	protected void checkUserRequirement(Statement statement, String ownerId, String selectedCandidate, String requirementId,
			Integer... statuses) throws UserRequirementException {
		StringBuilder sql = new StringBuilder(130);
		sql.append("select count(*) from Requirement where id='");
		sql.append(requirementId);
		sql.append('\'');
		if (StringUtils.isNotEmpty(ownerId)) {
			sql.append(" and userId='");
			sql.append(ownerId);
			sql.append('\'');
			sql.append(" or 0<(select count(*) from User where id='"+ownerId+"' and type>="+UserTypes.ADMIN+")");
		}
		if (StringUtils.isNotEmpty(selectedCandidate)) {
			sql.append(" and selectedCandidate='");
			sql.append(selectedCandidate);
			sql.append('\'');
		}
		if (null != statuses && statuses.length > 0) {
			sql.append(" and status in (");
			for (Integer status : statuses) {
				sql.append(status);
				sql.append(',');
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(')');
		}
		if(log.isDebugEnabled()){
			log.debug(sql.toString());
		}
		try {
			if (0 >= ResultSetMap.mapInt(statement.executeQuery(sql.toString()))) {
				log.error("ownerId:" + ownerId + " selectedCandidate:" + selectedCandidate + " requirementId:" + requirementId + " status:"
						+ Arrays.toString(statuses) + " not consistent!");
				throw new UserRequirementException(ErrorCodes.REQUIREMENT_STATUS_ERROR, "the requirement data not consistent");
			}
		} catch (SQLException e) {
			throw new UserRequirementException("check user requirement failed!", e);
		}
	}

	protected void alterUserSponsorScore(Statement statement, String requirementId, int score, String comment) throws SQLException {
		String userSql = "update User u set u.sponsorScore=u.sponsorScore+" + score + " where (u.sponsorScore+" + score
				+ ">=0) and u.id in (select r.userId from Requirement r where r.id='" + requirementId + "')";
		StringBuilder requirementSql = new StringBuilder();
		requirementSql.append("update Requirement set sponsorScore=");
		requirementSql.append(score);
		if (StringUtils.isNotEmpty(comment)) {
			requirementSql.append(",sponsorComment='");
			requirementSql.append(comment);
			requirementSql.append('\'');
		}
		requirementSql.append(" where id='");
		requirementSql.append(requirementId);
		requirementSql.append("'");
		statement.executeUpdate(requirementSql.toString());
		statement.executeUpdate(userSql);
	}

	protected void alterUserCompleteScore(Statement statement, String requirementId, int score, String comment) throws SQLException {
		String userSql = "update User u set u.completeScore=u.completeScore+" + score + " where (u.completeScore+" + score
				+ ">=0) and u.id in (select r.selectedCandidate from Requirement r where r.id='" + requirementId + "')";
		StringBuilder requirementSql = new StringBuilder(70);
		requirementSql.append("update Requirement set completeScore=");
		requirementSql.append(score);
		if (StringUtils.isNotEmpty(comment)) {
			requirementSql.append(",completeComment='");
			requirementSql.append(comment);
			requirementSql.append('\'');
		}
		requirementSql.append(" where id='");
		requirementSql.append(requirementId);
		requirementSql.append("'");
		int str = statement.executeUpdate(userSql);
		statement.executeUpdate(requirementSql.toString());
	}

	public void scoreSponsor(Statement statement, String completerId, String requirementId, int scoreLevel, String comment)
			throws SQLException, UserRequirementException {
		String checkScoreSql = "select sponsorScore from Requirement where id='" + requirementId + "' and sponsorScore is not null";
		Integer sponsorScore = (Integer)ResultSetMap.mapObject(statement.executeQuery(checkScoreSql));
		if (null != sponsorScore && sponsorScore.intValue() != 0) {
			log.error("requirement has already scored! completerId:" + completerId + " requirementId:" + requirementId + " scoreLevel:"
					+ scoreLevel + " comment:" + comment);
			throw new UserRequirementException("requirement has already scored!");
		}
		checkUserRequirement(statement, null, completerId, requirementId, RequirementStatus.complete);
		alterUserSponsorScore(statement, requirementId, getScore(scoreLevel), comment);
	}

	public void scoreSponsor(String completerId, String requirementId, int scoreLevel, String comment) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String checkScoreSql = "select sponsorScore from Requirement where id='" + requirementId + "' and sponsorScore is not null";
			if (null != ResultSetMap.mapObject(statement.executeQuery(checkScoreSql))) {
				log.error("requirement has already scored! completerId:" + completerId + " requirementId:" + requirementId + " scoreLevel:"
						+ scoreLevel + " comment:" + comment);
				throw new UserRequirementException("requirement has already scored!");
			}
			checkUserRequirement(statement, null, completerId, requirementId, RequirementStatus.complete);
			alterUserSponsorScore(statement, requirementId, getScore(scoreLevel), comment);
		} catch (SQLException e) {
			log.error("completerId:" + completerId + " requirementId:" + requirementId + " scoreLevel:" + scoreLevel + " comment:"
					+ comment, e);
			throw new UserRequirementException("scoreSponsor failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	public void scoreComplete(Statement statement, String sponsorId, String requirementId, int scoreLevel, String comment)
			throws UserRequirementException, SQLException {
		String checkScoreSql = "select completeScore from Requirement where id='" + requirementId + "' and completeScore is not null";
		Integer sponsorScore = (Integer)ResultSetMap.mapObject(statement.executeQuery(checkScoreSql));
		if (null != sponsorScore && sponsorScore != 0) {
			log.error("requirement has already scored! sponsorId:" + sponsorId + " requirementId:" + requirementId + " scoreLevel:"
					+ scoreLevel + " comment:" + comment);
			throw new UserRequirementException("requirement has already scored!");
		}
		checkUserRequirement(statement, sponsorId, null, requirementId, RequirementStatus.complete);
		alterUserCompleteScore(statement, requirementId, getScore(scoreLevel), comment);

	}

	public void scoreComplete(String sponsorId, String requirementId, int scoreLevel, String comment) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String checkScoreSql = "select completeScore from Requirement where id='" + requirementId + "' and completeScore is not null";
			if (null != ResultSetMap.mapObject(statement.executeQuery(checkScoreSql))) {
				log.error("requirement has already scored! sponsorId:" + sponsorId + " requirementId:" + requirementId + " scoreLevel:"
						+ scoreLevel + " comment:" + comment);
				throw new UserRequirementException("requirement has already scored!");
			}
			checkUserRequirement(statement, sponsorId, null, requirementId, RequirementStatus.complete);
			alterUserCompleteScore(statement, requirementId, getScore(scoreLevel), comment);
		} catch (SQLException e) {
			log.error("sponsorId:" + sponsorId + " requirementId:" + requirementId + " scoreLevel:" + scoreLevel + " comment:" + comment, e);
			throw new UserRequirementException("scoreComplete failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}

	}

	protected int getScore(int scoreLevel) throws UserRequirementException {
		if (0 == scoreLevel) {
			return 0;
		}
		if (1 == scoreLevel) {
			return 1;
		}
		if (2 == scoreLevel) {
			return 2;
		}
		throw new UserRequirementException("scoreLevel error,scoreLevel:" + scoreLevel);
	}

	public void close(String userId, String requirementId) throws UserRequirementException {

		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			checkUserRequirement(statement, userId, null, requirementId, RequirementStatus.published, RequirementStatus.selected);
			StringBuilder updateSql = new StringBuilder(100);
			updateSql.append("update Requirement set status=");
			updateSql.append(RequirementStatus.closed);
			updateSql.append(" where id='");
			updateSql.append(requirementId);
			updateSql.append("' and (userId='");
			updateSql.append(userId);
			updateSql.append("' or 0<(select count(*) from User where id='"+userId+"' and type>="+UserTypes.ADMIN+"))");
			if(log.isDebugEnabled()){
				log.debug(updateSql.toString());
			}
			statement.executeUpdate(updateSql.toString());
//			String isMandateSql = "select hasMandate from Requirement where id='" + requirementId + "'";
			String sql = "select  id ,title,userId , selectedCandidate,hasMandate,price  from Requirement where id='"
					+ requirementId + "'";
			Map<String, Object> map = ResultSetMap.map(statement.executeQuery(sql));
			String uSql="update User set applyCloseCount=applyCloseCount+1,closeCount=closeCount+1 where id='"+userId+"'";
			if(log.isDebugEnabled()){
				log.debug(uSql);
			}
			statement.executeUpdate(uSql);
			boolean hasMandate = (boolean)map.get("hasMandate");
			if (Boolean.TRUE.equals(hasMandate)) {
				AccountService.instance.payback(statement, requirementId);
			}
			
			/**
			 * 
			 * requirementId ,requirementTitle,candidateId,userId
			 */
			try {
				Global.queue.publish(QueueSubjects.REQUIREMENT_CLOSED, map);
				if (StringUtils.isNotEmpty((String) map.get("selectedCandidate"))) {
					alterUserCompleteScore(statement, requirementId, -1, null);
				}
				if (StringUtils.isNotEmpty((String) map.get("userId"))) {
					alterUserSponsorScore(statement, requirementId, -1, null);
				}
			} catch (Exception e) {
				log.error("failed to publish close message", e);
			}
			connection.commit();

		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("requirementId", requirementId, "userId", userId), e);
			throw new UserRequirementException("selectCandidate failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	/**
	 * 完成需求
	 * 
	 * @param userId
	 * @param requirementId
	 * @param password
	 * @throws UserRequirementException
	 */
	public void complete(String userId, String requirementId, String password) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			checkUserRequirement(statement, userId, null, requirementId, RequirementStatus.selected);
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update Requirement set status=");
			updateSql.append(RequirementStatus.complete);
			updateSql.append(",completeTime='");
			updateSql.append(new Timestamp(System.currentTimeMillis()));
			updateSql.append("'");
			updateSql.append(" where id='");
			updateSql.append(requirementId);
			updateSql.append("' and userId='");
			updateSql.append(userId);
			updateSql.append('\'');
			if(log.isDebugEnabled()){
				log.debug(updateSql.toString());
			}
			String isMandateSql = "select hasMandate,selectedCandidate,userId from Requirement where id='" + requirementId + "'";
			Map<String, Object> requirementInfo = ResultSetMap.map(statement.executeQuery(isMandateSql));
			Boolean hasMandate = (Boolean) requirementInfo.get("hasMandate");
			String selectedCandidate = (String) requirementInfo.get("selectedCandidate");
			String uaccsql = "update User set applyCompleteCount = applyCount+1 where id='"+selectedCandidate+"'";
			if(log.isDebugEnabled()){
				log.debug(uaccsql);
			}
			String uccSql = "update User set completeCount=completeCount+1 where id='"+ userId+"'";
			if(log.isDebugEnabled()){
				log.debug(uccSql);
			}
			statement.executeUpdate(uaccsql);
			statement.executeUpdate(uccSql);
			if (Boolean.TRUE.equals(hasMandate)) {
				AccountService.instance.pay(statement, requirementId, password);
			}else{
				AccountService.instance.quickPay(statement, requirementId, password);
			}
			statement.executeUpdate(updateSql.toString());
			scoreSponsor(statement , selectedCandidate, requirementId, 2, "no comment");
			scoreComplete(statement, userId, requirementId, 2, "no comment");
			
			/**
			 * 
			 * requirementId ,requirementTitle,candidateId,userId
			 */
			try {
				String sql = "select id ,title,userId , selectedCandidate from Requirement where id='"
						+ requirementId + "'";
				Map<String, Object> map = ResultSetMap.map(statement.executeQuery(sql));
				Global.queue.publish(QueueSubjects.REQUIREMENT_COMPLETE, map);
			} catch (Exception e) {
				log.error("publish complete message", e);
			}
			connection.commit();

		} catch (HzException e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("requirementId", requirementId, "userId", userId), e);
			throw new UserRequirementException("selectCandidate failed!", e);
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error(LogUtils.format("requirementId", requirementId, "userId", userId), e);
			throw new UserRequirementException("selectCandidate failed!", e);
		} 
		finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<Requirement> getUserCompeteRequirements(String userId) {

		return null;
	}

	/**
	 * select r.id ,r.title , r.status , r.price,r,type , r.createTime ,(select
	 * count(*) from RequirementCandidate rc where rc.requirementId=r.id ) as
	 * candidatesCount from Requirement r.userId='userId'
	 * 
	 * @param userId
	 * @param status
	 * @param type
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws UserRequirementException
	 */
	public List<Map<String, Object>> getUserIssuedRequirement(String userId, Integer status, Integer type, int startIndex, int pageSize)
			throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			// List<Map<String, Object>> result = new LinkedList<>();
			statement = connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("select r.id ,r.title , r.status , r.price,r.type , r.createTime ,r.hasMandate ,r.address , r.expire, r.selectedCandidate ,r.longitude, r.latitude, (select count(*) from RequirementCandidate rc where rc.requirementId=r.id ) as candidatesCount  from Requirement r where r.userId='");
			sql.append(userId);
			sql.append('\'');
			if (null != status) {
				sql.append(" and status=");
				sql.append(status);
			}
			if (null != type) {
				sql.append(" and type=");
				sql.append(type);
			}
			sql.append(" order by r.createTime desc limit ");
			sql.append(startIndex);
			sql.append(",");
			sql.append(pageSize);
			if (log.isDebugEnabled()) {
				log.debug(sql.toString());
			}
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

	public List<Requirement> getRequirementCandidates(String userId) {

		return null;
	}

	// public Requirement getRaw(String requirementId) throws
	// UserRequirementException {
	// Connection connection = null;
	// try {
	// connection = Connections.instance.get();
	// Statement statement = connection.createStatement();
	// String sql = "select * from Requirement where id='"+requirementId+"'";
	// ResultSet resultSet = statement.executeQuery(sql );
	// Requirement requirement = new Requirement();
	// while(resultSet.next()){
	// requirement.setAddress(resultSet.getString("address"));
	// requirement.setCreateTime(resultSet.getTimestamp("createTime"));
	// requirement.setAddress(resultSet.getString("address"));
	// requirement.setAddress(resultSet.getString("address"));
	// }
	// }catch (SQLException e) {
	// throw new UserRequirementException(e);
	// }
	//
	// }
	/**
	 * get basic info -> get comments -> update view count and viewer
	 * 
	 * @param requirementId
	 * @return
	 * @throws UserRequirementException
	 */
	public Map<String, Object> requirementInfo(String requirementId) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("select r.* ,u.name as userName , u.mainPicture as userPicture,u.mainPictureSmall as userPictureSmall, u.mainPictureMid as userPictureMid, u.mainPictureBig as userPictureBig,u.credit as userCredit ,r.mainPicture , (select count(*) from RequirementComment where requirementId="
					+ requirementId + ") as commentCount from Requirement r left join User u on r.userId=u.id ");
			sql.append(" where r.id='");
			sql.append(requirementId);
			sql.append('\'');
			sql.append(" limit 1");

			return ResultSetMap.map(statement.executeQuery(sql.toString()));

		} catch (SQLException e) {
			log.error("", e);
			throw new UserRequirementException(e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public Requirement get(String requirementId) throws UserRequirementException {
		Connection connection = null;
		StringBuilder sql = createQueryHeader();
		sql.append(" where r.id='");
		sql.append(requirementId);
		sql.append('\'');
		sql.append(" limit 1");
		try {
			connection = Connections.instance.get();
			// Map<String, Object> newProperties = new HashMap<>();

			List<Requirement> result = DbOperations.instance.query(connection, sql.toString(), (Map<String, Object>) null,
					Requirement.class, SimpleObjectMapping.instance, 1);
			if (null != result && !result.isEmpty()) {
				return result.get(0);
			} else {
				return null;
			}
		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId), e);
			throw new UserRequirementException("get requirement failed!", e);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * 一个用户一小时内只能浏览一次，自身浏览不计算
	 * 
	 * @param userId
	 * @param requirementId
	 * @throws UserRequirementException
	 */
	public void view(String requirementId, String userId) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;

		try {
			connection = Connections.instance.get();
			RequirementVisitor visitor = new RequirementVisitor();

			statement = connection.createStatement();
			String sql = "select userId , viewCount from Requirement where id='" + requirementId + "' limit 1";
			ResultSet resultSet = statement.executeQuery(sql);
			if (!resultSet.next()) {
				return;
			}
			String requirementUserId = resultSet.getString("userId");
			int viewCount = resultSet.getInt("viewCount");
			if (null == userId || requirementUserId.equals(userId)) {
				return;
			}
			String increseViewCountSql = "update Requirement set viewCount=" + (viewCount + 1) + " where id='" + requirementId + "'";
			statement.executeUpdate(increseViewCountSql);

			String countSql = "select count(*) from RequirementVisitor where userId='" + userId + "'";

			ResultSet rsCount = statement.executeQuery(countSql);
			rsCount.next();
			int count = rsCount.getInt(1);
			if (count <= 0) {

				String id = HashUtils.uuid();
				// visitor.setId(id);
				visitor.setRequirementId(requirementId);
				visitor.setUserId(userId);
				visitor.setViewTime(new Timestamp(System.currentTimeMillis()));
				DbOperations.instance.save(connection, visitor, true);
			} else {
				String updateVistorSql = "update RequirementVisitor set viewTime=:viewTime where requirementId=:requirementId and userId=:userId";
				NamedParameterStatement namedParameterStatement = new NamedParameterStatement(connection, updateVistorSql);
				namedParameterStatement.setTimestamp("viewTime", new Timestamp(System.currentTimeMillis()));
				namedParameterStatement.setString("requirementId", requirementId);
				namedParameterStatement.setString("userId", userId);
				namedParameterStatement.executeUpdate();
				namedParameterStatement.close();
			}
			// update(requirementId, newVules);
		} catch (SQLException e) {
			log.error(LogUtils.format("userId", userId, "requirementId", requirementId), e);
			throw new UserRequirementException("save visitor failed!", e);
		} finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}

		}
	}

	/**
	 * 
	 * select v.* ,u.name as userName , p.remotePath as userPicture from
	 * (RequirementVisitor v left join User u on v.userid=u.id) left join
	 * Picture p on u.mainPictureId=p.id where 1=1 and v.requirementId='12' and
	 * v.userId='21' order by v.visitTime desc limit 0 ,10
	 * 
	 * @param requirementId
	 * @param userId
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws UserRequirementException
	 */
//	public List<RequirementVisitor> getVisitors(String requirementId, String userId, int startIndex, int pageSize)
//			throws UserRequirementException {
//		StringBuilder sql = new StringBuilder();
//		sql.append("select v.* ,u.name as userName , u.mainPictureSmall, u.mainPictureMid, u.mainPictureBig,p.remotePath as userPicture from (RequirementVisitor v left join User u on v.userid=u.id) left join Picture p on u.mainPictureId=p.id where 1=1 ");
//		if (StringUtils.isNotEmpty(requirementId)) {
//			sql.append(" and v.requirementId='");
//			sql.append(requirementId);
//			sql.append('\'');
//		}
//		if (StringUtils.isNotEmpty(userId)) {
//			sql.append(" and v.userId='");
//			sql.append(userId);
//			sql.append('\'');
//		}
//		sql.append("order by v.viewTime desc limit ");
//		sql.append(startIndex);
//		sql.append(',');
//		sql.append(pageSize);
//		Connection connection = null;
//		try {
//			connection = Connections.instance.get();
//			// Map<String, Object> newProperties = new HashMap<>();
//
//			return DbOperations.instance.query(connection, sql.toString(), (Map<String, Object>) null, RequirementVisitor.class,
//					SimpleObjectMapping.instance, pageSize);
//		} catch (SQLException e) {
//			log.error(LogUtils.format("requirementId", requirementId, "userId", userId, "startIndex", startIndex, "pageSize", pageSize), e);
//			throw new UserRequirementException("get RequirementVisitor failed!", e);
//		} finally {
//			closeConnection(connection);
//		}
//	}

//	public List<Map<String, Object>> getVisitors1(String requirementId, String userId, int startIndex, int pageSize)
//			throws UserRequirementException {
//		StringBuilder sql = new StringBuilder();
//		sql.append("select v.* ,u.name as userName , u.mainPictureSmall, u.mainPictureMid, u.mainPictureBig,p.remotePath as userPicture from (RequirementVisitor v left join User u on v.userid=u.id) left join Picture p on u.mainPictureId=p.id where 1=1 ");
//		if (StringUtils.isNotEmpty(requirementId)) {
//			sql.append(" and v.requirementId='");
//			sql.append(requirementId);
//			sql.append('\'');
//		}
//		if (StringUtils.isNotEmpty(userId)) {
//			sql.append(" and v.userId='");
//			sql.append(userId);
//			sql.append('\'');
//		}
//		sql.append("order by v.viewTime desc limit ");
//		sql.append(startIndex);
//		sql.append(',');
//		sql.append(pageSize);
//		Connection connection = null;
//		try {
//			connection = Connections.instance.get();
//			// Map<String, Object> newProperties = new HashMap<>();
//			return QuickDB.gets(sql.toString());
//			// return DbOperations.instance.query(connection, sql.toString(),
//			// (Map<String, Object>) null, RequirementVisitor.class,
//			// SimpleObjectMapping.instance, pageSize);
//		} catch (SQLException e) {
//			log.error(LogUtils.format("requirementId", requirementId, "userId", userId, "startIndex", startIndex, "pageSize", pageSize), e);
//			throw new UserRequirementException("get RequirementVisitor failed!", e);
//		} finally {
//			closeConnection(connection);
//		}
//	}

	public void updateStatus(Statement statement, String userId, String requirementId, int status) throws SQLException {
		// Connection connection = null;
		// Statement statement = null;
		// try {
		// connection = Connections.instance.get();
		// statement = connection.createStatement();
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("update Requirement set status=");
		updateSql.append(status);
		updateSql.append(" where id='");
		updateSql.append(requirementId);
		updateSql.append("' and userId='");
		updateSql.append(userId);
		updateSql.append('\'');

		statement.executeUpdate(updateSql.toString());

		// } catch (SQLException e) {
		// log.error(LogUtils.format("requirementId", requirementId, "userId",
		// userId), e);
		// throw new UserRequirementException("selectCandidate failed!", e);
		// }
		// finally {
		// closeStatement(statement);
		// closeConnection(connection);
		// }
	}

	public void updateStatus(String userId, String requirementId, int status) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update Requirement set status=");
			updateSql.append(status);
			updateSql.append(" where id='");
			updateSql.append(requirementId);
			updateSql.append("' and userId='");
			updateSql.append(userId);
			updateSql.append('\'');

			statement.executeUpdate(updateSql.toString());

		} catch (SQLException e) {
			log.error(LogUtils.format("requirementId", requirementId, "userId", userId), e);
			throw new UserRequirementException("selectCandidate failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}
	

	/**
	 * 
	 * @param userId
	 * @param requirementId
	 * @param status
	 *            ref RequirementStatus
	 */
	public void updateRequirementStatus(String userId, String requirementId, int status) {

		// String update

	}
	
	public int shareWB(String requirementId, String userId) throws UserRequirementException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String requirementSql = "select title,description,mainPicture,price from Requirement where id='"
					+ requirementId + "' limit 1";
			if(log.isDebugEnabled()){
				log.debug(requirementSql);
			}
			Map<String, Object> result = ResultSetMap.map(statement.executeQuery(requirementSql));
			String title = (String)result.get("title");
			Object desc = result.get("description");
			String description = (null==desc)? null : (String)desc;
			String body = title;
			BigDecimal money = (BigDecimal)result.get("price");
			BigDecimal formatPrice = Formats.formatMoney(money);
			Object img = result.get("mainPicture");
			String images = (null==img)? null : (String)img;
			User user = UserProxy.getUserInfo(userId);
			Map<String, Object> params = new HashMap<String, Object>();
			if(images != null){
				byte[] pic = HttpUtils.doGetInBytes(images);
				params.put("pic", pic);
			}
			String url = GlobalConfig.get("share.requirement.url") + requirementId;
			params.put("comment", body + " " + GlobalConfig.get("share.url"));
			params.put("body", body + ",发布金额：" + "￥" + formatPrice + " " + GlobalConfig.get("share.url"));
			params.put("images", images);
			params.put("title", title + ",发布金额：" + "￥" + formatPrice);
			params.put("summary", description);
			params.put("url", url);
			String qqAccessToken = user.getQqAccessToken();
			String weiboAccessToken = user.getWeiboAccessToken();
			//Object qq = result.get("qqAccessToken");
			//String qqAccessToken = (null==qq)? null : (String)qq;
			//Object WB = result.get("weiboAccessToken");
			//String weiboAccessToken = (null==WB)? null : (String)WB;
			int c = 0;
			if(null != qqAccessToken){
				c=1;
				QqShare.instance.share(qqAccessToken, params);
			}
			if(null != weiboAccessToken){
				if(c == 1){
					c=3;
				}else{
					c=2;
				}
				WeiboShare.instance.share(weiboAccessToken, params);
			}
			return c;
		}catch(Exception e){
			log.error("share weibo failed!:'" + userId +"' requirementId: '"+ requirementId + "'");
			throw new UserRequirementException("share weibo failed!:'" + userId +"' requirementId: '"+ requirementId + "'", e);
		}finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}
	
	public void shareWBQQ(String requirementId, String userId, Integer shareWB, Integer shareQQ) throws UserRequirementException{
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String requirementSql = "select title,description,mainPicture,price from Requirement where id='"
					+ requirementId + "' limit 1";
			if(log.isDebugEnabled()){
				log.debug(requirementSql);
			}
			Map<String, Object> result = ResultSetMap.map(statement.executeQuery(requirementSql));
			String title = (String)result.get("title");
			Object desc = result.get("description");
			String description = (null==desc)? null : (String)desc;
			String body = title;
			BigDecimal money = (BigDecimal)result.get("price");
			BigDecimal formatMoney = money.setScale(0, RoundingMode.DOWN);
			Object img = result.get("mainPicture");
			String images = (null==img)? null : (String)img;
			User user = UserProxy.getUserInfo(userId);
			Map<String, Object> params = new HashMap<String, Object>();
			if(images != null){
				byte[] pic = HttpUtils.doGetInBytes(images);
				params.put("pic", pic);
			}
			String url = GlobalConfig.get("share.requirement.url") + requirementId;
			String abody = StringUtils.abbreviate(title, 40);
			String atitle = StringUtils.abbreviate(title, 20);
			params.put("comment", abody);
			String avbody = ResourceUtils.getContent(body, formatMoney).toString();
			String rebody = StringUtils.abbreviate(avbody + description, 100);
			params.put("body", rebody + url);
			params.put("images", images);
			StringBuilder retitle = ResourceUtils.getContent(atitle, formatMoney);
			String reatitle = retitle.delete(retitle.toString().length() - 3, retitle.toString().length()).toString();
			params.put("title", reatitle);
			String adescription = StringUtils.abbreviate(null != description?description:"", 40);
			params.put("summary", adescription + " " + url);
			params.put("url", url);
			String qqAccessToken = user.getQqAccessToken();
			String weiboAccessToken = user.getWeiboAccessToken();
			//Object qq = result.get("qqAccessToken");
			//String qqAccessToken = (null==qq)? null : (String)qq;
			//Object WB = result.get("weiboAccessToken");
			//String weiboAccessToken = (null==WB)? null : (String)WB;
			int shareqq = shareQQ != null?(int)shareQQ:0;
			int sharewb = shareWB != null?(int)shareWB:0;
			if(ShareAction.SHARE_QQ == shareqq){
				QqShare.instance.share(qqAccessToken, params);
			}
			if(ShareAction.SHARE_WB == sharewb){
				WeiboShare.instance.share(weiboAccessToken, params);
			}
		}catch(Exception e){
			log.error("share weibo failed!:'" + userId +"' requirementId: '"+ requirementId + "'");
			throw new UserRequirementException("share weibo failed!:'" + userId +"' requirementId: '"+ requirementId + "'", e);
		}finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	public void getSound(String requirementId, WithBlob withBlob) throws UserRequirementException {
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select sound from Requirement where id='"+requirementId+"' and sound is not null");  
			if(rs.next()){
				Blob blob = rs.getBlob(1);
				withBlob.with(blob);
				blob.free();
			}else{
				throw new UserRequirementException(ErrorCodes.NOT_EXISTS ,"requirement sound not exists");
			}
		}catch(Exception e){
			log.error("failed to get requirement's sound. requirement id:"+requirementId, e);
			throw new UserRequirementException("failed to get requirement's sound.", e);
		}finally {
			closeStatement(statement);
			closeConnection(connection);
		}

	}
}