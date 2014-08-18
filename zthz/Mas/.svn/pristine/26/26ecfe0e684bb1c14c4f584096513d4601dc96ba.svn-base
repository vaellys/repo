package cn.zthz.tool.cache.user;

import cn.zthz.tool.cache.RedisCache;

/**
 * key [user.userId.properties]
 * 
 * @author uzoice
 * 
 */
public class UserCache {

	public static void setUserStatus(String userId, int userStatus) {
		RedisCache.instance.set(UserCacheKeys.userStatus(userId), String.valueOf(userStatus));
	}

	public static int getUserStatus(String userId) {
		String status = RedisCache.instance.get(UserCacheKeys.userStatus(userId));
		if (null == status) {
			return UserStatus.OFFLINE;
		} else {
			return Integer.valueOf(status);
		}
	}

	public static void main(String[] args) {
//		setUserStatus("1", UserStatus.OFFLINE);
		System.out.println(getUserStatus("402880173c193e49013c195307ab000b"));
	}

}
