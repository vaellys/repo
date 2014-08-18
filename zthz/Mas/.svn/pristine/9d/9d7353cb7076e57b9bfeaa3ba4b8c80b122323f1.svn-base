package cn.zthz.actor.message;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.queue.OnMessage;

public class UserLoginCountor {
	final static Log log = LogFactory.getLog(UserLoginCountor.class);
	public static final UserLoginCountor instance = new UserLoginCountor();

	/**
	 * message : {userId:"" , time:TimeStamp , ip:""}
	 */
	public void onUserLoginForLoginCount() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				try {
					Map<String, Object> data = (Map<String, Object>) message;
					String sql = "insert into UserLogin (userId, time,ip) values(:userId ,:time ,:ip)";
					QuickDB.insert(sql, data);
				} catch (SQLException e) {
					log.error("insert user login record to db error!", e);
				}
			}
		}, QueueSubjects.USER_LOGIN_COUNT);
	}
}
