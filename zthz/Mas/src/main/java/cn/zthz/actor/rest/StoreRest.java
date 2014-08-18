package cn.zthz.actor.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.store.StoreService;


public class StoreRest extends FunctionalRest{
	public void add(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try {
			StoreService.instance.add(getString(request, response, "requirementId" , true), userId);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter(USER_ID);
		try {
			StoreService.instance.delete(getMultiValues(request, "id"), userId);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "delete store failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
	
	public void queryStore(HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter(USER_ID);
		try {
			List<Map<String, Object>> result = StoreService.instance.query(userId, getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "query store failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
	
	public void cancel(HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter(USER_ID);
		try {
			StoreService.instance.cancel(getString(request, response, "requirementId", true), userId);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "cancel store failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
}
