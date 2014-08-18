package cn.zthz.actor.message;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.tool.cache.user.UserStatus;
import cn.zthz.tool.common.DateUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbMapOperations;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.message.Message;
import cn.zthz.tool.message.MessageTypes;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.push.PushException;
import cn.zthz.tool.push.PushService;
import cn.zthz.tool.push.SendStatus;
import cn.zthz.tool.queue.OnMessage;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.user.User;

public class Messagor {
	final static Log log = LogFactory.getLog(Messagor.class);
	public static final Messagor instance = new Messagor();
	
	public void subscribe(){
		this.onUserLoginForUnreadMesssages();
		onNewMessage();
		employerInvite();
	}
	
	
	public void onNewMessage(){
		Global.queue.subscribe( new OnMessage() {
			
			@Override
			public void handle(String subject, Object m) {
				Connection connection = null;
				try {
					connection = Connections.instance.get();
					Message messageObject = (Message) m;
					String message = null;
					
					if(messageObject.mType == null){
						message = messageObject.getMessage();					
					}else if(messageObject.mType == 0){
						message = "您有一条语音消息！";
					}
					String receiverId = messageObject.getReceiverId();
					String senderId =messageObject.getSenderId();
					String uuid = messageObject.getUuid();
					int receicerStatus = UserProxy.getUserStatus(receiverId);
					User senderInfo = UserProxy.getUserInfo(senderId);
					Map<String, Object> conditionProperties = new HashMap<>(1);
					Map<String, Object> newProperties = new HashMap<>(1);
					conditionProperties.put("uuid", uuid);
					if (UserStatus.EXIT != receicerStatus) {
						Map<String, Object> device = UserProxy.getUserDevice(receiverId);
						if (null == device) {
							return;
						}
						Map<String, Object> attach = new HashMap<>(4);
						attach.put("t", messageObject.type);
						attach.put("senderId", senderId);
						attach.put("id", messageObject.getId());
						// attach.put("senderName", senderInfo.getName());
						// attach.put("senderPicture", senderInfo.getMainPicture());
						attach.put("receiverId", receiverId);
						// attach.put("message", message.length() >= 20 ?
						// message.substring(0, 20) : message);
						attach.put("sendTime", DateUtils.format(messageObject.getSendTime()));
						try {
							PushService.instance.push(senderId, receiverId, senderInfo.getName() + ":" + (message.length() >= 20 ? message.substring(0, 20) + "..." : message), attach);
							newProperties.put("sendStatus", SendStatus.SEND_SUCCESS);
							DbMapOperations.instance.update(connection, "Message", newProperties, conditionProperties);
						} catch (PushException e) {
							log.error("senderId:'" + senderId + "', receiverId:'" + receiverId + "'", e);
							newProperties.put("sendStatus", SendStatus.SEND_FAILED);
							DbMapOperations.instance.update(connection, "Message", newProperties, conditionProperties);
							throw e;
						}
					}
				} catch (PushException | SQLException e) {
					log.error("" ,e);
				}finally{
					ConnectionUtils.closeConnection(connection);
				}
			}
		},QueueSubjects.NEW_MESSSAGE);
	}
	public void onUserLoginForUnreadMesssages(){
		Global.queue.subscribe( new OnMessage() {
			
			@Override
			public void handle(String subject, Object message) {
				User user = (User) message;
				String userId = user.getId();
				Map<String, Object> device = UserProxy.getUserDevice(userId);
				if(null == device || device.isEmpty()){
					log.warn("user id="+userId +" has no device");
					return;
				}
				try {
					int count =PushService.instance.getUnreceivedCount(userId);
					if(0>=count){
						return;
					}
					String alert = "您有"+count+"条未读消息！";
					Map<String, Object> attachment = new HashMap<String, Object>(1);
					attachment.put("t", MessageTypes.COUNT_MESSAGE);
					attachment.put("c", count);
					PushService.instance.push(Global.SYSTEM_USER_ID, userId, alert, attachment );
				} catch (PushException e) {
					log.error("" ,e);
				}
			}
		},QueueSubjects.USER_LOGIN);
	}
	
	public void employerInvite(){
		Global.queue.subscribe( new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> employeeMap = (Map<String, Object>)message;
				String userId = (String)employeeMap.get("userId");
				Map<String, Object> employee = (Map<String, Object>)employeeMap.get("employeeId");
				String requirementId = (String)employeeMap.get("requirementId");
				Connection connection = null;
				Statement statement = null;
				try{
					connection = Connections.instance.get();
					statement = connection.createStatement();
//					String employeeSql = "select id, name from User where id in ('"+StringUtils.join(employeeId, ",")+"')";
//					List<Map<String, Object>> employee = ResultSetMap.maps(statement.executeQuery(employeeSql));
					String requirementSql = "select title from Requirement where id='"+requirementId+"'";
					Map<String, Object> requirement = ResultSetMap.map(statement.executeQuery(requirementSql));
					User employer = UserProxy.getUserInfo(userId);
					String employerName = employer.getName();
					Map<String, Object> attach = new HashMap<>(1);
					attach.put("t",MessageTypes.EMPLOYER_INVITE);
					attach.put("senderId", userId);
					attach.put("requirementId", requirementId);
					if(null != employee){
						for(Map.Entry<String, Object> entry : employee.entrySet()){
							attach.put("mid", entry.getValue());
							PushService.instance.push(userId, entry.getKey(), (employerName+"邀请"+UserProxy.getUserInfo(entry.getKey()).getName()+"完成任务:"+(String)requirement.get("title")), attach);
						}
					}	
				}catch(Exception e){
					log.error("inviteEmployee failed!userId:'"+userId+"'", e);
				}finally{
					ConnectionUtils.closeStatement(statement);
					ConnectionUtils.closeConnection(connection);
				}	
			}
		},QueueSubjects.EMPLOYER_INVITE);
	}

}
