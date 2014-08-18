package cn.zthz.actor.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.user.ThirdAccessTokenCheck;
import cn.zthz.tool.user.ThirdAccessTokenCheckImpl;
import cn.zthz.tool.user.UserOperationImpl;
import cn.zthz.tool.user.UserTypes;

public class AuthorityRest extends Rest {
	public static final String USER_ID = "userId";
	public static final String USER_TOKEN = "userToken";
	public static final String TARGET_USER_ID = "targetUserId";
	
	public String getTargetUserId(HttpServletRequest request, HttpServletResponse response) {
		String targetUserId = request.getParameter(TARGET_USER_ID);
		return StringUtils.isEmpty(targetUserId)?request.getParameter(USER_ID) : targetUserId;
	}
	
	public void checkAdmin(HttpServletRequest request, HttpServletResponse response){
		String userId = null;
		String userToken = null;
		if(isMultipart(request)){
			try {
				Map<String, Object> map = uploadFields(request, response);
				userId = getms(map, USER_ID, request, response, true);
				userToken = getms(map, USER_TOKEN, request, response, true);
				request.setAttribute(REQUEST_PARAM_MAP, map);
			} catch (Exception e) {
				putError(request, response, e);
			}
		}else{
			userId = request.getParameter(USER_ID);
			userToken = request.getParameter(USER_TOKEN);
			checkParameterNotNull(request, response, USER_ID, userId);
			checkParameterNotNull(request, response, USER_TOKEN, userToken);
		}
		try{
		if(StringUtils.isEmpty(userId)|| StringUtils.isEmpty(userToken)|| userId.length()>32 || userToken.length()>32 || !userToken.equals(UserProxy.getUserToken(userId)) || UserTypes.ADMIN>UserProxy.getUserInfo(userId).getType()){
			log.info("url:"+request.getRequestURI()+" userToken is invalid,id:"+userId +" userToken:"+userToken+" remote address:"+request.getRemoteAddr());
			putError(request, response, "url:"+request.getRequestURI()+" userToken is invalid,id:"+userId +" userToken:"+userToken, ErrorCodes.SERVER_DATA_NO_INCONSISTENT);
		}
		}catch(Exception e){
			putError(request ,response,e);
		}
	}


	protected void checkUserToken(HttpServletRequest request, HttpServletResponse response) {
		String userId = null;
		String userToken = null;
		if(isMultipart(request)){
			try {
				Map<String, Object> map = uploadFields(request, response);
				userId = getms(map, USER_ID, request, response, true);
				userToken = getms(map, USER_TOKEN, request, response, true);
				request.setAttribute(REQUEST_PARAM_MAP, map);
			} catch (Exception e) {
				putError(request, response, e);
			}
		}else{
			userId = request.getParameter(USER_ID);
			userToken = request.getParameter(USER_TOKEN);
			checkParameterNotNull(request, response, USER_ID, userId);
			checkParameterNotNull(request, response, USER_TOKEN, userToken);
		}
		try{
		if(StringUtils.isEmpty(userId)|| StringUtils.isEmpty(userToken)|| userId.length()>32 || userToken.length()>32 || !userToken.equals(UserProxy.getUserToken(userId)) ){
			log.info("url:"+request.getRequestURI()+" userToken is invalid,id:"+userId +" userToken:"+userToken+" remote address:"+request.getRemoteAddr());
			putError(request, response, "url:"+request.getRequestURI()+" userToken is invalid,id:"+userId +" userToken:"+userToken, ErrorCodes.SERVER_DATA_NO_INCONSISTENT);
		}
		}catch(Exception e){
			putError(request ,response,e);
		}
	}
	
	protected void checkAccessToken(String uid, String accessToken, String type) throws HzException {
		ThirdAccessTokenCheck thirdAccessTokenCheck = ThirdAccessTokenCheckImpl.instance;
		if("qq".equals(type)){
			thirdAccessTokenCheck.checkQqAccessToken(uid, accessToken);
		}
		if("weibo".equals(type)){
			thirdAccessTokenCheck.checkWeiboAccessToken(uid, accessToken);
		}
	}
}
