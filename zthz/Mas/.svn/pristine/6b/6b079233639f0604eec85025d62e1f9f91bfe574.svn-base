package cn.zthz.actor.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.account.AccountException;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.cache.RedisCache;
import cn.zthz.tool.cache.user.UserCacheKeys;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.user.User;
import cn.zthz.tool.user.UserException;
import cn.zthz.tool.user.UserOperationImpl;

public class SetupRest extends FunctionalRest {

	public void setupInfo(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		Map<String, Object> result = new HashMap<String, Object>();
		User user = UserProxy.getUserInfo(userId);
		if (StringUtils.isNotEmpty(user.getWeiboUid()) && StringUtils.isNotEmpty(user.getWeiboAccessToken())) {
			result.put("isBindingWeibo", true);
		} else {
			result.put("isBindingWeibo", false);
		}
		if (StringUtils.isNotEmpty(user.getQqUid()) && StringUtils.isNotEmpty(user.getQqAccessToken())) {
			result.put("isBindingQq", true);
		} else {
			result.put("isBindingQq", false);
		}
		result.put("allowPush", UserProxy.allowPush(userId));
		result.put("allowPushNewRequirement", UserProxy.allowPushNewRequirement(userId));

		try {
			result.putAll(AccountService.instance.getBasicInfo(userId));
			if (StringUtils.isEmpty((String) result.get("cardNumber"))) {
				result.put("isBindedBank", false);
			} else {
				result.put("isBindedBank", true);
			}

		} catch (AccountException e) {
			putError(request, response, e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
		}
		putJson(request, response, result);
	}

	public void unbindLoginAccount(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		String type = getString(request, response, "type", true);
		Map<String, Object> newVules = new HashMap<>(2);
		if ("qq".equalsIgnoreCase(type)) {
			newVules.put("qqUid", null);
			newVules.put("qqAccessToken", null);
		}
		if ("weibo".equalsIgnoreCase(type)) {
			newVules.put("weiboUid", null);
			newVules.put("weiboAccessToken", null);
		}
		try {
			UserOperationImpl.instance.update(userId, newVules);
			putSuccess(request, response);
		} catch (UserException e) {
			putError(request, response, e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
		}

	}

	public void bindLoginAccount(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		String weiboUid = request.getParameter("weiboUid");
		String weiboAccessToken = request.getParameter("weiboAccessToken");
		String qqUid = request.getParameter("qqUid");
		String qqAccessToken = request.getParameter("qqAccessToken");
		Map<String, Object> newVules = new HashMap<>(2);
		try {
			if (StringUtils.isNotEmpty(weiboUid) && StringUtils.isNotEmpty(weiboAccessToken)) {
				if (UserOperationImpl.instance.hasBind(weiboUid, "weibo")) {
					putError(request, response, "weiUid has already binded!", ErrorCodes.SERVER_DATA_NO_INCONSISTENT);
				}
				checkAccessToken(weiboUid, weiboAccessToken, "weibo");
				newVules.put("weiboUid", weiboUid);
				newVules.put("weiboAccessToken", weiboAccessToken);
			}
			if (StringUtils.isNotEmpty(qqUid) && StringUtils.isNotEmpty(qqAccessToken)) {
				if (UserOperationImpl.instance.hasBind(qqUid, "qq")) {
					putError(request, response, "qq has already binded!", ErrorCodes.SERVER_DATA_NO_INCONSISTENT);
				}
				checkAccessToken(qqUid, qqAccessToken, "qq");
				newVules.put("qqUid", qqUid);
				newVules.put("qqAccessToken", qqAccessToken);
			}
			if (StringUtils.isEmpty(weiboUid) && StringUtils.isEmpty(qqUid)) {
				putError(request, response, "weiboUid and qqUid is empty!", ErrorCodes.PARAMETER_INVALID);
			}

			UserOperationImpl.instance.update(userId, newVules);
			putSuccess(request, response);
		} catch (UserException e) {
			putError(request, response, e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
		} catch (HzException e){
			putError(request, response, e);
		}
	}

	public void allowPush(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);

		Map<String, Object> newVules = new HashMap<>(1);
		boolean allowPush = getBoolean(request, response, "allowPush", true);
		newVules.put("allowPush", allowPush);
		try {
			UserOperationImpl.instance.update(userId, newVules);
			RedisCache.instance.set(UserCacheKeys.allowPush(userId), Boolean.toString(allowPush));
			putSuccess(request, response);
		} catch (UserException e) {
			putError(request, response, "set allowPush:" + request.getParameter("allowPush") + " failed", ErrorCodes.SERVER_INNER_ERROR);
		}
	}

	public void allowPushNewRequirement(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		Map<String, Object> newVules = new HashMap<>(1);
		boolean allowPushNewRequirement = getBoolean(request, response, "allowPushNewRequirement", true);
		newVules.put("allowPushNewRequirement", allowPushNewRequirement);
		try {
			UserOperationImpl.instance.update(userId, newVules);
			RedisCache.instance.set(UserCacheKeys.allowPushNewRequirement(userId), Boolean.toString(allowPushNewRequirement));
			putSuccess(request, response);
		} catch (UserException e) {
			putError(request, response, "set allowPushNewRequirement:" + request.getParameter("allowPushNewRequirement") + " failed",
					ErrorCodes.SERVER_INNER_ERROR);
		}
	}

	public void bindBankAccount(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		String cardNumber = getString(request, response, "cardNumber", true);
		String accountPassword = getString(request, response, "accountPassword", true);
		Integer bankType = getInt(request, response, "bankType", false);
		String accountName = getString(request, response, "accountName", false);
		Set<String> keys = request.getParameterMap().keySet();
		Map<String, Object> newVules = new HashMap<>(1);
		newVules.put("cardNumber", cardNumber);
		// if (keys.contains("accountPassword")) {
		// newVules.put("password", accountPassword);
		// }
		if (keys.contains("bankType")) {
			newVules.put("bankType", bankType);
		}
		if (keys.contains("accountName")) {
			newVules.put("accountName", accountName);
		}
		try {
			// password , bankType , cardNumber
			UserOperationImpl.instance.checkUserPassword(userId, accountPassword);
			AccountService.instance.updateAccount(userId, newVules);
			putSuccess(request, response);
		} catch (AccountException e) {
			log.error(e.getMessage() ,e);
			putError(request, response, "bindBankAccount failed", ErrorCodes.SERVER_INNER_ERROR);
		} catch (UserException e) {
			log.error(e.getMessage() ,e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(e.getMessage() ,e);
			putError(request, response, e);
		}
	}

	public void unbindBankAccount(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		// String cardNumber = request.getParameter("cardNumber");
		// Integer bankType = getInt(request, response, "bankType", false);
		Map<String, Object> newVules = new HashMap<>(1);
		// Set<String> keys = request.getParameterMap().keySet();
		// if(keys.contains("cardNumber")){
		newVules.put("cardNumber", null);
		newVules.put("accountName", null);
		// }
		// if(keys.contains("bankType")){
		newVules.put("bankType", null);
		// }
		try {
			// password , bankType , cardNumber
			AccountService.instance.updateAccount(userId, newVules);
			putSuccess(request, response);
		} catch (AccountException e) {
			putError(request, response, "unbindBankAccount failed", ErrorCodes.SERVER_INNER_ERROR);
		}
	}
}
