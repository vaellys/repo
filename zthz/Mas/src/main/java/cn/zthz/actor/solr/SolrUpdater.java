package cn.zthz.actor.solr;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.actor.queue.UserSubjects;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.queue.OnMessage;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.Requirement;
import cn.zthz.tool.requirement.RequirementStatus;
import cn.zthz.tool.user.User;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;


public class SolrUpdater {
	private static final Log log = LogFactory.getLog(SolrUpdater.class);

	public static void registerAll() {
		log.info("register user update message");
		onUserAdd();
		onUserUpdated();
		onRequirementPublish();
		onRequirementUpdate();
	}

	public static void onUserAdd() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				try {
					addUser((User) message);
				} catch (Exception e) {
					log.error("add user to solr failed!", e);
				}
			}
		}, UserSubjects.USER_ADDED);
	}

	public static void onUserUpdated() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				try {
					Map<String, Object> map = (Map<String, Object>) message;
					String uid = (String) map.get("userId");
					User user = UserProxy.getUserInfo(uid);
					List<Map<String, String>> skillList = (List<Map<String, String>>) map.get("skillss");
					if (null != skillList) {
						StringBuilder skills = new StringBuilder();
						for (Map<String, String> i : skillList) {
							skills.append(i.get("name"));
							skills.append(',');
						}
						if(skills.length()>0)skills.deleteCharAt(skills.length() - 1);
						user.setSkills(skills);
					}
					addUser(user);
				} catch (Exception e) {
					log.error("add user to solr failed!", e);
				}
			}
		}, UserSubjects.USER_UPDATED);
	}

	public static void onRequirementUpdate() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				String requirementId = (String) map.get("requirementId");
				if (null == requirementId) {
					requirementId = (String) map.get("id");
				}
				try {
					deleteRequirment(requirementId);
				} catch (Exception e) {
					log.error("delete requirement to solr failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_CLOSED, QueueSubjects.REQUIREMENT_EXPIRED, QueueSubjects.REQUIREMENT_SELECTED_CANDIDATE);
	}

	public static void onRequirementPublish() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				try {
					addRequirment((Requirement) message);
				} catch (Exception e) {
					log.error("add requirement to solr failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_PUBLISHED);
	}

	private static final SerializeConfig config = new SerializeConfig();
	static {
		config.put(java.sql.Timestamp.class, new ObjectSerializer() {

			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
				serializer.write(DateFormatUtils.format((java.util.Date) object, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			}
		});
	}

	private static final String SOLR_REQUIREMENT_UPDATE_URL = "http://" + GlobalConfig.get("solr.innerAddress")
			+ "/solr/requirement/update?commit=true";
	private static final String SOLR_USER_UPDATE_URL = "http://" + GlobalConfig.get("solr.innerAddress") + "/solr/user/update?commit=true";

	/**
	 * select u.id, u.type , u.name , u.sex , u.skills
	 * ,u.credit,u.status,u.occupation, u.mainPicture as picture from User u
	 * 
	 * @param user
	 * @throws Exception
	 */
	public static void addUser(User user) throws Exception {
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			if (null == user) {
				return;
			}
			List<Map<String, Object>> users = new LinkedList<>();
			Map<String, Object> item = new HashMap<>(9);
			String userId = user.getId();
			item.put("id", userId);
			item.put("type", user.getType());
			item.put("name", user.getName());
			item.put("sex", user.getSex());
			Object ss = user.getSkillss();
			item.put("skills", user.getSkills());
			log.debug(user.getSkills());
			// item.put("skillss", user.getSkillss());
			String completeSql = "select count(r.id) from Requirement r left join User u on r.userId=u.id  where r.selectedCandidate='" + userId + "'" + " and r.status=" + RequirementStatus.complete;
			if(log.isDebugEnabled()){
				log.debug(completeSql);
			}
			int completeCounts = ResultSetMap.mapInt(statement.executeQuery(completeSql));
			item.put("completeCount", completeCounts);
			item.put("sponsorScore", user.getSponsorScore());
			item.put("completeScore", user.getCompleteScore());
			item.put("status", user.getStatus());
			item.put("occupation", user.getOccupation());
			item.put("picture", user.getMainPicture());
			item.put("pictureSmall", user.getMainPictureSmall());
			item.put("pictureMid", user.getMainPictureMid());
			item.put("pictureBig", user.getMainPictureBig());
			if(null != user.getLatestLatitude() && null != user.getLatestLongtitude()){
				item.put("location", user.getLatestLatitude() + "," + user.getLatestLongtitude());
			}
			users.add(item);
			String response = HttpUtils.doPostsEntity(SOLR_USER_UPDATE_URL, JSON.toJSONString(users, config), ContentType.APPLICATION_JSON);
			log.info("add user with id:" + user.getId() + " response:" + response);
		}catch(Exception e){
			log.error("add user failed！");
			throw new HzException("add user failed！");
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}

	}

	public static void deleteUser(String id) throws Exception {
		if (null == id) {
			return;
		}
		log.info("delete user with id:" + id + " in solr");
		Map<String, Map<String, Object>> operations = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("id", id);
		operations.put("delete", data);
		String response = HttpUtils.doPostsEntity(SOLR_USER_UPDATE_URL, JsonUtils.toJsonString(operations), ContentType.APPLICATION_JSON);
		log.info("delete user response:" + response);
	}

	/**
	 * select r.id, r.title ,r.description ,r.price ,r.type, r.status,r.expire
	 * ,(r.expire-now()) as remainTime, r.createTime,r.address
	 * ,cast(CONCAT(r.latitude,',',r.longitude) as char) as location ,u.name as
	 * userName , u.mainPicture as userPicture from Requirement r left join User
	 * u on r.userId=u.id where r.status=0 and r.expire > now()
	 * 
	 * @throws Exception
	 */

	public static void addRequirment(Requirement requirement) throws Exception {
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			if (null == requirement) {
				return;
			}
			// , u.mainPictureSmall as userPictureSmall, u.mainPictureMid as
			// userPictureMid, u.mainPictureBig as userPictureBig
			Map<String, Object> userInfo = ResultSetMap.map(statement.executeQuery("select u.name as userName , u.mainPicture as userPicture from User u where id='"
					+ requirement.getUserId() + "'"));
			List<Map<String, Object>> records = new LinkedList<>();
			Map<String, Object> item = new HashMap<>(14);
			String requirementId = requirement.getId();
			String visitedCountsSql = "select count(RequirementVisitor.id) from RequirementVisitor where requirementId='" + requirementId +"'";
			if(log.isDebugEnabled()){
				log.debug(visitedCountsSql);
			}
			int visitedCounts = ResultSetMap.mapInt(statement.executeQuery(visitedCountsSql));
			item.put("visitorCount", visitedCounts);
			String visitorInfoSql = "select u.mainPictureSmall from RequirementVisitor rv left join User u on rv.userId=u.id where rv.requirementId='" + requirementId+"'";
			if(log.isDebugEnabled()){
				log.debug(visitorInfoSql);
			}
			Map<String, Object> visitorInfo = ResultSetMap.map(statement.executeQuery(visitorInfoSql));
			item.put("visitorPictures", visitorInfo.get("mainPictureSmall"));
			item.put("completeScore", requirement.getCompleteScore());
			item.put("id", requirementId);
			item.put("title", requirement.getTitle());
			item.put("description", requirement.getDescription());
			item.put("price", requirement.getPrice());
			item.put("type", requirement.getType());
			item.put("status", requirement.getStatus());
			item.put("expire", requirement.getExpire());
			item.put("hasMandate", requirement.getHasMandate());
			// item.put("remainTime", requirement.getExpire().getTime() -
			// System.currentTimeMillis());
			item.put("createTime", requirement.getCreateTime());
			item.put("address", requirement.getAddress());
			if (null != requirement.getLatitude() && null != requirement.getLongitude())
				item.put("location", requirement.getLatitude() + "," + requirement.getLongitude());
			item.putAll(userInfo);
			// item.put("userName", requirement.getId());
			// item.put("userPicture", requirement.getId());
			records.add(item);
			String response = HttpUtils.doPostsEntity(SOLR_REQUIREMENT_UPDATE_URL, JSON.toJSONString(records, config),
					ContentType.APPLICATION_JSON);
			log.info("add requirement response:" + response);
			
		}catch(Exception e){
			log.error("add requiremnt failed!");
			throw new HzException("add requiremnt failed!");
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public static void deleteRequirment(String id) throws Exception {
		if (null == id) {
			return;
		}
		log.info("delete requirement with id:" + id + " in solr");
		Map<String, Map<String, Object>> operations = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("id", id);
		operations.put("delete", data);
		String response = HttpUtils.doPostsEntity(SOLR_REQUIREMENT_UPDATE_URL, JsonUtils.toJsonString(operations),
				ContentType.APPLICATION_JSON);
		log.info("delete requirement response:" + response);
	}

	public static void main(String[] args) throws Exception {

		log.error("nima");
		// Requirement r =
		// RequirementOperations.instance.get("402880173dca28fc013dca54aa24000e");
		// Requirement r = new Requirement();
		// r.setId("402880173dca28fc013dca54aa24000e");
		// r.setTitle("hello");
		// r.setDescription("1");
		// r.setPrice(new BigDecimal(25.0));
		// r.setType(new Integer(1));
		// r.setStatus(new Integer(0));
		// r.setExpire(new Timestamp(System.currentTimeMillis()));
		// r.setAddress("中关村");
		// addRequirment(r);
		// User user = new User();
		// user.setId("0001");
		// user.setName("cn");
		// user.setPassword("gg");
		// addUser(user );
		// deleteUser("0001");
		// Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// List<Timestamp> list = new LinkedList<>();
		// list.add(timestamp);
		// System.out.println(JSON.toJSONString(list,SerializerFeature.UseISO8601DateFormat));
		// String pattern ="yyyy-MM-dd'T'HH:mm:ss:SSSZZ";
		// String d = DateFormatUtils.format(timestamp, pattern );
		// System.out.println(d);

	}
}
