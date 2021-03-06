package cn.zthz.actor.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.requirement.RequirementOperations.QueryOrder;
import cn.zthz.tool.user.EmployeeService;
import cn.zthz.tool.user.EmployerService;
import cn.zthz.tool.user.UserQueryOrder;

/**
 * 威客，被雇佣
 * @author uzoice
 *
 */
public class EmployeeRest extends FunctionalRest{
	/**
	 * 威客主页
	 * @param request
	 * @param response
	 */
	public void home(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try{
			Map<String, Object> result = EmployeeService.instance.home(userId);
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void requirementManagement(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		String rawStatus = request.getParameter("status");
		String rawType = request.getParameter("type");
		Map<String, Object> conditionArgs = new HashMap<>(2);
		if(StringUtils.isNotEmpty(rawStatus)){
			checkParameterIsNumber(request, response, "status", rawStatus);
			conditionArgs.put("status", rawStatus);
		}
		if (StringUtils.isNotEmpty(rawType)) {
			checkParameterIsNumber(request, response, "type", rawType);
			conditionArgs.put("type", Integer.parseInt(rawType));
		}
		try{
			List<Map<String, Object>> result = EmployeeService.instance.requirementManagement(userId, conditionArgs, getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void employmentSuccess(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try{
			Map<String, Object> result = EmployeeService.instance.employmentSuccess(userId, getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	public void addTags(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try{
			List<String> list = getMultiValues(request, "tnum");
			List<Integer> tnums = new ArrayList<>(list.size());
			for (String i: list) {
				tnums.add(Integer.valueOf(i.trim()));
			}
			EmployeeService.instance.addTags(userId, tnums);
			putSuccess(request, response);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void deleteTags(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try{
			List<String> list = getMultiValues(request, "tnum");
			List<Integer> tnums = new ArrayList<>(list.size());
			for (String i: list) {
				tnums.add(Integer.valueOf(i.trim()));
			}
			EmployeeService.instance.deleteTags(userId, tnums);
			putSuccess(request, response);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	public void findByTag(HttpServletRequest request, HttpServletResponse response) {
		try{
			String userId = request.getParameter(USER_ID);
			Integer tnum = getInt(request, response, "tnum", false);
			UserQueryOrder queryOrder = (UserQueryOrder) getEnum(request, response, "queryOrder", false, UserQueryOrder.class);
			Map<String, Object> params = new HashMap<>();;
			if(UserQueryOrder.nearest.equals(queryOrder)){
				params.put("longitude", getDouble(request, response, "longitude", true));
				params.put("latitude", getDouble(request, response, "latitude", true));
			}
			if(null != userId){
				params.put("userId", userId);
			}
			List<Map<String, Object>> result = EmployeeService.instance.findByTag(tnum,params , queryOrder,getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void employmentInfo(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		String targetUserId = getTargetUserId(request, response);
		try{
			Map<String, Object> result = EmployeeService.instance.employeeInfo(targetUserId,userId);
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void requirements(HttpServletRequest request, HttpServletResponse response) {
		String targetUserId = getTargetUserId(request, response);
		try{
			Map<String, Object> result = EmployeeService.instance.requirements(targetUserId , getInt(request, response, "status", false),getInt(request, response, "type", false) ,getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true) );
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void abilityTags(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try{
			Map<String, List<Object>> result = EmployeeService.instance.abilityTags(userId);
			putJson(request, response, result);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void addAbility(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try{
			List<String> list = getMultiValues(request, "tnum");
			List<Integer> tnums = new ArrayList<>(list.size());
			for (String i: list) {
				tnums.add(Integer.valueOf(i.trim()));
			}
			EmployeeService.instance.addAbility(userId, tnums);
			putSuccess(request, response);
		}catch(HzException e){
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
}
