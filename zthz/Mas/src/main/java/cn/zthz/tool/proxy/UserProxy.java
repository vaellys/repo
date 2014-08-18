package cn.zthz.tool.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.cache.RedisCache;
import cn.zthz.tool.cache.user.UserCache;
import cn.zthz.tool.cache.user.UserCacheKeys;
import cn.zthz.tool.common.HzRuntimeException;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.user.User;
import cn.zthz.tool.user.UserException;
import cn.zthz.tool.user.UserOperationImpl;

public class UserProxy {
	private static final Log log = LogFactory.getLog(UserProxy.class);

	public static void setUserStatus(String userId, int userStatus) {
		RedisCache.instance.set(UserCacheKeys.userStatus(userId), String.valueOf(userStatus));
	}

	public static int getUserStatus(String userId) {
		return UserCache.getUserStatus(userId);
	}

	public static String getUserToken(String userId) {
		String userToken = RedisCache.instance.get(UserCacheKeys.userToken(userId));
		if (StringUtils.isEmpty(userToken)) {
			refreshUserCaches(userId);
		} else {
			return userToken;
		}
		return RedisCache.instance.get(UserCacheKeys.userToken(userId));
	}

	public static User getUserInfo(String userId) {
//		UserProxy.refreshUserCaches(userId);
		User user = (User) RedisCache.instance.getObject(UserCacheKeys.userInfo(userId));
		if (null == user) {
			Connection connection = null;
			Statement statement = null;
			try {
				connection = Connections.instance.get();
				statement = connection.createStatement();
				user = DbOperations.instance.get(connection, userId, User.class);
				String userSkillSql = "select id,name,description from UserSkills where userId='" +userId+ "'";
				List<Map<String, Object>> userSkills = ResultSetMap.maps(statement.executeQuery(userSkillSql));
				user.setSkillss(userSkills);
				RedisCache.instance.setObject(UserCacheKeys.userInfo(userId), user);
				return user;
			} catch (SQLException e) {
				log.error("", e);
				return null;
			} finally {
				if (null != connection) {
					try {
						connection.close();
					} catch (SQLException e) {
						log.error("", e);
					}
				}
			}

		}
		return user;
	}

	public static boolean allowPush(String userId) {
		String result = RedisCache.instance.get(UserCacheKeys.allowPush(userId));
		if (null == result) {
			refreshUserCaches(userId);
		}
		return Boolean.valueOf(RedisCache.instance.get(UserCacheKeys.allowPush(userId)));

	}

	public static boolean allowPushNewRequirement(String userId) {
		String result = RedisCache.instance.get(UserCacheKeys.allowPushNewRequirement(userId));
		if (null == result) {
			refreshUserCaches(userId);
		}
		return Boolean.valueOf(RedisCache.instance.get(UserCacheKeys.allowPushNewRequirement(userId)));

	}

	public static Map<String, Object> getUserDevice(String userId) {
		Object result = RedisCache.instance.getObject(UserCacheKeys.userDevice(userId));
		if (null == result) {
			refreshUserCaches(userId);
		}
		return (Map<String, Object>) RedisCache.instance.getObject(UserCacheKeys.userDevice(userId));
	}

	public static void resetUserStatus(String userId) {
		RedisCache.instance.resetExpire(UserCacheKeys.userStatus(userId));

	}

	public static void expireUser(String userId) {
		List<String> keys = new LinkedList<>();
		keys.add(UserCacheKeys.userInfo(userId));
		keys.add(UserCacheKeys.userToken(userId));
		keys.add(UserCacheKeys.allowPush(userId));
		keys.add(UserCacheKeys.allowPushNewRequirement(userId));
		keys.add(UserCacheKeys.userDevice(userId));
		RedisCache.instance.expire(keys);
	}

	public static void refreshUserCaches(String userId) {
		User user;
		try {
			user = UserOperationImpl.instance.get(userId);
			if (null == user) {
				return;
			}
			RedisCache.instance.setObject(UserCacheKeys.userInfo(userId), user);
			RedisCache.instance.set(UserCacheKeys.userToken(userId), user.getUserToken());
			RedisCache.instance.set(UserCacheKeys.allowPush(userId),
					(null == user.getAllowPush()) ? "true" : String.valueOf(user.getAllowPush()));
			RedisCache.instance.set(UserCacheKeys.allowPushNewRequirement(userId), String.valueOf(user.getAllowPushNewRequirement()));
			Map<String, Object> device = QuickDB.get("select deviceToken , type as deviceType from UserDevice where userId='" + userId
					+ "'");
			RedisCache.instance.setObject(UserCacheKeys.userDevice(userId), device);
		} catch (UserException e) {
			log.error("userId:" + userId, e);
			throw new HzRuntimeException(e.errorCode());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
