package cn.zthz.tool.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.actor.rest.ErrorCodes;
import cn.zthz.tool.cache.RedisCache;
import cn.zthz.tool.cache.user.UserCache;
import cn.zthz.tool.cache.user.UserCacheKeys;
import cn.zthz.tool.cache.user.UserStatus;
import cn.zthz.tool.common.CollectionUtils;
import cn.zthz.tool.common.DateUtils;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.common.WithBlob;
import cn.zthz.tool.common.WithInputStream;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbMapOperations;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.push.PushException;
import cn.zthz.tool.push.PushService;
import cn.zthz.tool.push.ReceiveStatus;
import cn.zthz.tool.push.SendStatus;
import cn.zthz.tool.requirement.AbstractService;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.user.User;

public class MessageService extends AbstractService{
	private static final Log log = LogFactory.getLog(MessageService.class);
	public static final MessageService instance = new MessageService();
	
	public void sound(String userId,int mid , WithBlob withBlob) throws PushException{
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			  statement = connection.createStatement();  
		      ResultSet rs = statement.executeQuery("select sound from Message where id="+mid+" and (senderId='"+userId+"' or receiverId='"+userId+"')");  
		      if(rs.next()){
		    	  Blob blob = rs.getBlob(1);
//		    	  inputStream = blob.getBinaryStream();  
		    	  withBlob.with(blob);
		    	  blob.free();
		      }else{
		    	  throw new PushException(ErrorCodes.NOT_EXISTS , "message sound not exists.");
		      }
		} catch (Exception e) {
			log.error("", e);
			throw new PushException(e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}
	
	public void send(String senderId, String receiverId, File message, Integer soundLength) throws PushException {
		Connection connection = null;
		PreparedStatement statement = null;
		InputStream inputStream = null;
		Message messageObject = new Message();
		messageObject.setSenderId(senderId);
		messageObject.setReceiverId(receiverId);
		messageObject.setSoundLength(soundLength);
		messageObject.type = MessageTypes.INSTANT_MESSAGE;
		messageObject.setSendStatus(SendStatus.UNSEND);
		messageObject.setReceiveStatus(ReceiveStatus.UNRECEIVED);
		messageObject.setSendTime(new Timestamp(System.currentTimeMillis()));
		try {
			connection = Connections.instance.get();
			String sql = "insert into Message(senderId,sendTime,sendStatus,receiverId,receiveStatus,sound,soundLength) values(?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, senderId);
			statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			statement.setInt(3, SendStatus.UNSEND);
			statement.setString(4, receiverId);
			statement.setInt(5, ReceiveStatus.UNRECEIVED);
			inputStream = new FileInputStream(message);
			statement.setBlob(6, inputStream );
			statement.setInt(7, soundLength);
			statement.executeUpdate();
			long mid = ResultSetMap.mapInt(statement.getGeneratedKeys());
			String isSoundSql = "select ISNULL(sound) as mType from Message where id=" + mid; 
			Map<String, Object> map = ResultSetMap.map(statement.executeQuery(isSoundSql));
			messageObject.setId(mid);
			messageObject.mType = (Long)map.get("mType");
			Global.queue.publish(QueueSubjects.NEW_MESSSAGE , messageObject);


		} catch (SQLException | FileNotFoundException e) {
			log.error("", e);
			throw new PushException(e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
			ConnectionUtils.close(inputStream);
		}
	}
	
	
	public void send(String senderId, String receiverId, String message) throws PushException {
		Message messageObject = new Message();
		messageObject.setMessage(message);
		messageObject.type = MessageTypes.INSTANT_MESSAGE;
		messageObject.setSenderId(senderId);
		messageObject.setReceiverId(receiverId);
		String uuid = HashUtils.uuid();
		messageObject.setUuid(uuid);
		messageObject.setSendStatus(SendStatus.UNSEND);
		messageObject.setReceiveStatus(ReceiveStatus.UNRECEIVED);
		messageObject.setSendTime(new Timestamp(System.currentTimeMillis()));
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			Statement statement = connection.createStatement();
			DbOperations.instance.save(connection, messageObject, true);
			long  id =ResultSetMap.mapInt(statement.executeQuery("select id from Message where uuid='"+uuid+"'" ));
			messageObject.setId(id);
			Global.queue.publish(QueueSubjects.NEW_MESSSAGE , messageObject);

//			int receicerStatus = UserProxy.getUserStatus(receiverId);
//			User senderInfo = UserProxy.getUserInfo(senderId);
//			Map<String, Object> conditionProperties = new HashMap<>(1);
//			Map<String, Object> newProperties = new HashMap<>(1);
//			conditionProperties.put("uuid", uuid);
//			if (UserStatus.EXIT != receicerStatus) {
//				Map<String, Object> device = UserProxy.getUserDevice(receiverId);
//				if (null == device) {
//					return;
//				}
//				Map<String, Object> attach = new HashMap<>(4);
//				attach.put("t", MessageTypes.INSTANT_MESSAGE);
//				attach.put("senderId", senderId);
//				// attach.put("senderName", senderInfo.getName());
//				// attach.put("senderPicture", senderInfo.getMainPicture());
//				attach.put("receiverId", receiverId);
//				// attach.put("message", message.length() >= 20 ?
//				// message.substring(0, 20) : message);
//				attach.put("sendTime", DateUtils.format(messageObject.getSendTime()));
//				try {
//					PushService.instance.push(senderId, receiverId, senderInfo.getName() + ":" + (message.length() >= 20 ? message.substring(0, 20) + "..." : message), attach);
//					newProperties.put("sendStatus", SendStatus.SEND_SUCCESS);
//					DbMapOperations.instance.update(connection, "Message", newProperties, conditionProperties);
//				} catch (PushException e) {
//					log.error("senderId:'" + senderId + "', receiverId:'" + receiverId + "'", e);
//					newProperties.put("sendStatus", SendStatus.SEND_FAILED);
//					DbMapOperations.instance.update(connection, "Message", newProperties, conditionProperties);
//					throw e;
//				}
//			}

		} catch (SQLException e) {
			log.error("", e);
			throw new PushException(e);
		} finally {
			closeConnection(connection);
		}
	}
	
	public void save(Message message) throws MessageException{
	}
	
	
	/**
	 *  select m.id ,m.message ,m.senderId , m.senderTime,m.senderStatus , m.receiverId , m.receiveStatus from Message m where receiverId='receiverId' and senderId='senderId' limit startIndex ,pageSize
	 * @return 
	 */
	public List<Map<String, Object>> queryMessages(String receiverId , String senderId ,Long id , int startIndex , int pageSize) throws MessageException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("select m.id ,m.message ,m.senderId , m.sendTime,m.sendStatus , m.receiverId , m.receiveStatus from Message m where receiverId='");
			sql.append(receiverId);
			sql.append("' and senderId='");
			sql.append(senderId);
			sql.append('\'');
			if(id!=null){
				sql.append(" and m.id>");
				sql.append(id);
			}
			sql.append(" order by id desc limit ");
			sql.append(startIndex);
			sql.append(",");
			sql.append(pageSize);
			return ResultSetMap.maps(statement.executeQuery(sql.toString()));
		} catch (SQLException e) {
			log.error(LogUtils.format("receiverId", receiverId ,"senderId" ,"senderId"), e);
			throw new MessageException("query user compete requirements failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
		
	}
	/**
	 * Message表可能会很大，最好不要做连接
	 * select  m.senderId , m.id,m.sendTime  from Message m where m.receiverId='receiverId' m.sendTime=(select max(sendTime) from Message mm where mm.senderId = m.senderId) group by m.senderId ,m.id , m.sendTime ;
	 * select u.id as senderId ,u.name as senderName , u.mainPicture as senderPicture from User u where in ('senderId');
	 * @param userId
	 * @param startIndex
	 * @param pageSize
	 * @return [{senderId:"" , senderName:"" , senderPicture:"" , senderTime:"",refRequirementName:"" , latestMessage:"message" , latestMessageStatus:0}]
	 * @throws MessageException
	 */
	public List<Map<String, Object>> getSenders(String receiverId , int startIndex , int pageSize ) throws MessageException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String senderSql  = "select  m.senderId , m.id,m.sendTime,m.message , m.receiveStatus  from Message m where m.receiverId='"+receiverId+"' and m.id=(select max(mm.id) from Message mm where mm.senderId = m.senderId) group by m.senderId ,m.id , m.sendTime, m.receiveStatus, m.message limit "+startIndex+","+pageSize;
			ResultSet resultSet = statement.executeQuery(senderSql);
			List<Map<String, Object>> senderMessages = ResultSetMap.maps(resultSet );
			if(senderMessages.isEmpty()){
				return senderMessages;
			}
			List<Object> senderIds = CollectionUtils.extract(senderMessages, "senderId", true);
			String userSql = "select u.id as senderId ,u.name as senderName , u.mainPicture as senderPicture from User u where u.id in ('"+StringUtils.link(senderIds, "','")+"') ";
//			System.out.println(userSql);
			ResultSet userResultSet = statement.executeQuery(userSql);
			Map<Object, Map<String, Object>> users = ResultSetMap.map(userResultSet, "senderId");
			Map<String, Object> userInfo = null;
			for (Map<String, Object> record : senderMessages) {
				userInfo = users.get(record.get("senderId"));
				if(null  == userInfo){
					continue;
				}
				record.putAll(userInfo);
			}
			return senderMessages;
		} catch (SQLException e) {
			log.error(LogUtils.format("receiverId", receiverId , "startIndex" , startIndex , "pageSize" , pageSize), e);
			throw new MessageException("query user compete equirements failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}
	
	public void adminSend(String userId , List<String> uids ,String message) throws MessageException{
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String sql = "select id from User "+(null == uids?"":0==uids.size()?"where false":"where id in ('"+StringUtils.join(uids,"','")+"')") +" order by id asc limit ";
			if(log.isDebugEnabled())log.debug(sql);
			int si =0 ,ps =100;
			while(true){
				ResultSet rs = statement.executeQuery(sql+si+","+ps);
				rs.setFetchSize(ps);
				boolean isEmpty = true;
				while(rs.next()){
					isEmpty = false;
					MessageService.instance.send(userId, rs.getString(1) , message);
				}
				if(isEmpty){
					break;
				}
				si=ps*(si+1);
//				Thread.sleep(3000);
			}
		
		} catch (SQLException | PushException e) {
//			log.error(LogUtils.format("receiverId", receiverId , "startIndex" , startIndex , "pageSize" , pageSize), e);
			throw new MessageException("send message failed!", e);
		} finally {
			closeStatement(statement);
			closeConnection(connection);
		}
	}

	

}
