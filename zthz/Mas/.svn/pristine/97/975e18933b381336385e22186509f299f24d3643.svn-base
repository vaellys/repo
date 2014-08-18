package cn.zthz.actor.rest;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.RequirementUtils;
import cn.zthz.tool.store.StoreService;
import cn.zthz.tool.user.UserCollectService;

public class UserCollectRest extends FunctionalRest{
	public void add(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try {
			UserCollectService.instance.add(userId, getString(request, response, "fid" , true));
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter(USER_ID);
		try {
			UserCollectService.instance.delete(getMultiValues(request, "fid"), userId);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "delete userCollect failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
	
	public void queryUserCollect(HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter(USER_ID);
		try {
			List<Map<String, Object>> result = UserCollectService.instance.query(userId, getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "query userCollect failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
}
