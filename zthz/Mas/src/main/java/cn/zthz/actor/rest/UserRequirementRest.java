package cn.zthz.actor.rest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.DateUtils;
import cn.zthz.tool.common.Formats;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.common.Tuple;
import cn.zthz.tool.common.WithBlob;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.message.MessageService;
import cn.zthz.tool.picture.IconSizes;
import cn.zthz.tool.push.PushException;
import cn.zthz.tool.requirement.Requirement;
import cn.zthz.tool.requirement.RequirementCandidateService;
import cn.zthz.tool.requirement.RequirementComment;
import cn.zthz.tool.requirement.RequirementCommentService;
import cn.zthz.tool.requirement.RequirementOperations;
import cn.zthz.tool.requirement.RequirementOperations.QueryOrder;
import cn.zthz.tool.requirement.RequirementStatus;
import cn.zthz.tool.requirement.UserRequirementException;

public class UserRequirementRest extends FunctionalRest {
	private static final Log log = LogFactory.getLog(UserRequirementRest.class);

	private void binaryPublish(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> map = uploadFields(request, response);
			if(log.isInfoEnabled())log.info(JsonUtils.toJsonString(map));
			Requirement requirement = new Requirement();
			File file = (File) map.get("mainPictureData_file");
			if (null != file) {
				Map<String, String> sizeUrls = savePictureThumbs(file, request, response, IconSizes.REQUIREMENT_ICON_SIZES);
				requirement.setMainPicture(sizeUrls.get("raw"));
				requirement.setMainPictureBig(sizeUrls.get("640x960"));
				requirement.setMainPictureMid(sizeUrls.get("100x100"));
				requirement.setMainPictureSmall(sizeUrls.get("50x50"));
			}
			requirement.setSound((File) map.get("sound_file"));
			requirement.setUserId(getms(map, "userId", request, response, true));
			requirement.setTitle(getms(map, "title", request, response, true));
			requirement.setDescription(getms(map, "description", request, response, false));
			requirement.setExpire(DateUtils.parseTimestamp(getms(map, "expire", request, response, true)));
			requirement.setPrice(Formats.formatMoney(new BigDecimal(getms(map, "price", request, response, true))));
			requirement.setAddress(getms(map, "address", request, response, false));
			requirement.setPublishAddress(getms(map, "publishAddress", request, response, false));
			requirement.setHasMandate(getmb(map, "hasMandate", request, response, false));
			requirement.setType(getmi(map, "type", request, response, true));
			requirement.setHasPrivateMessageContact(getmb(map, "hasPrivateMessageContact", request, response, false));
			requirement.setHasPhoneContact(getmb(map, "hasPhoneContact", request, response, false));
			requirement.setLongitude(getmd(map, "longitude", request, response, false));
			requirement.setLatitude(getmd(map, "latitude", request, response, false));
			requirement.setPublishLongitude(getmd(map, "publishLongitude", request, response, false));
			requirement.setPublishLatitude(getmd(map, "publishLatitude", request, response, false));
			requirement.setViewCount(0);
			requirement.setShareQQ(getmi(map, "shareQQ", request, response, false));
			requirement.setShareWB(getmi(map, "shareWB", request, response, false));
			requirement.setSoundTime(getmi(map, "soundTime", request, response, false));
			String id = RequirementOperations.instance.publish(requirement);
			if(log.isInfoEnabled()){
				log.info(LogUtils.format("r",map));
			}
			log.info("publish sucess id:" + id);
			putJson(request, response, genJson("id", id));
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}
	
	public void sound(final HttpServletRequest request, final HttpServletResponse response){
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment; filename=\"sound\"");
		
		try {
			RequirementOperations.instance.getSound(getString(request, response, "requirementId", true), new WithBlob() {
				
				@Override
				public void with(Blob blob) throws IOException, SQLException {
					response.setContentLength((int) blob.length());
					IOUtils.copy(blob.getBinaryStream(), response.getOutputStream());
				}
			});
		} catch (UserRequirementException e) {
			putError(request, response, e);
		}
	}

	public void publish(HttpServletRequest request, HttpServletResponse response) {
		if (isMultipart(request)) {
			binaryPublish(request, response);
			return;
		}
		String userId = request.getParameter("userId");
		String userToken = request.getParameter("userToken");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		// String pictures = request.getParameter("pictures");
		String rawExpire = request.getParameter("expire");
		String rawPrice = request.getParameter("price");
		String address = request.getParameter("address");
		String publishAddress = request.getParameter("publishAddress");
		String hasMandate = request.getParameter("hasMandate");
		String rawMandateFee = request.getParameter("mandateFee");
		String rawType = request.getParameter("type");
		String hasPrivateMessageContact = request.getParameter("hasPrivateMessageContact");
		String hasPhoneContact = request.getParameter("hasPhoneContact");
		String rawLongitude = request.getParameter("longitude");
		String rawLatitude = request.getParameter("latitude");
		String rawPublishLongitude = request.getParameter("publishLongitude");
		String rawPublishLatitude = request.getParameter("publishLatitude");
		String mainPictureData = request.getParameter("mainPictureData");
		String mainPictureUrl = request.getParameter("mainPictureUrl");
		// String mainPictureName = request.getParameter("mainPictureName");
		Integer shareQQ = getInt(request , response , "shareQQ", false);
		Integer shareWB = getInt(request , response , "shareWB", false);
		
		checkParameterNotNull(request, response, "userId", userId);
		checkParameterNotNull(request, response, "userToken", userToken);
		// checkUserToken(request, response);
		checkParameterNotNull(request, response, "title", title);
		checkParameterNotNull(request, response, "address", address);
		checkParameterIsNumber(request, response, "type", rawType);
		checkParameterNotNull(request, response, "expire", rawExpire);

		Requirement requirement = new Requirement();
		requirement.setUserId(userId);
		requirement.setTitle(title);
		requirement.setAddress(address);
		requirement.setPublishAddress(publishAddress);
		requirement.setDescription(description);
		requirement.setShareQQ(shareQQ);
		requirement.setShareWB(shareWB);
		if (StringUtils.isNotEmpty(mainPictureData)) {
			Map<String, String> sizeUrls = savePictureThumbs(mainPictureData, request, response, IconSizes.REQUIREMENT_ICON_SIZES);
			requirement.setMainPicture(sizeUrls.get("raw"));
			requirement.setMainPictureBig(sizeUrls.get("640x640"));
			requirement.setMainPictureMid(sizeUrls.get("100x100"));
			requirement.setMainPictureSmall(sizeUrls.get("50x50"));
			requirement.setMainPicture(savePicture(mainPictureData, request, response));
		} else {
			if (StringUtils.isNotEmpty(mainPictureUrl)) {
				requirement.setMainPicture(mainPictureUrl);
			}
		}
		try {
			requirement.setExpire(DateUtils.parseTimestamp(rawExpire));
		} catch (ParseException e) {
			log.error("expire time date format error!", e);
			putError(request, response, "expire time date format error!", ErrorCodes.PARAMETER_INVALID);
		}
		if (StringUtils.isNotEmpty(hasPrivateMessageContact)) {
			requirement.setHasPrivateMessageContact(getBoolean(request, response, "hasPrivateMessageContact", true));
		}
		if (StringUtils.isNotEmpty(hasPhoneContact)) {
			requirement.setHasPhoneContact(getBoolean(request, response, "hasPhoneContact", true));
		}
		if (StringUtils.isNotEmpty(rawLongitude)) {
			checkParameterIsFloat(request, response, "longitude", rawLongitude);
			requirement.setLongitude(Double.parseDouble(rawLongitude));
		}
		if (StringUtils.isNotEmpty(rawLatitude)) {
			checkParameterIsFloat(request, response, "latitude", rawLatitude);
			requirement.setLatitude(Double.parseDouble(rawLatitude));
		}
		if (StringUtils.isNotEmpty(rawPublishLongitude)) {
			checkParameterIsFloat(request, response, "publishLongitude", rawPublishLongitude);
			requirement.setPublishLongitude(Double.parseDouble(rawPublishLongitude));
		}
		if (StringUtils.isNotEmpty(rawPublishLatitude)) {
			checkParameterIsFloat(request, response, "publishLatitude", rawPublishLatitude);
			requirement.setPublishLatitude(Double.parseDouble(rawPublishLatitude));
		}
		if (StringUtils.isNotEmpty(hasMandate)) {
			requirement.setHasMandate(getBoolean(request, response, "hasMandate", false));
		}
		if (StringUtils.isNotEmpty(rawMandateFee)) {
			checkParameterIsFloat(request, response, "mandateFee", rawMandateFee);
			requirement.setMandateFee(Float.parseFloat(rawMandateFee));
		}
		if (StringUtils.isNotEmpty(rawPrice)) {
			checkParameterIsFloat(request, response, "price", rawPrice);
			requirement.setPrice(new BigDecimal(rawPrice));
		}
		// requirement.setStatus(0);
		requirement.setTitle(title);
		if (StringUtils.isNotEmpty(rawType)) {
			checkParameterIsFloat(request, response, "type", rawType);
			requirement.setType(Integer.parseInt(rawType));
		}
		requirement.setViewCount(0);
		try {
			String id = RequirementOperations.instance.publish(requirement);
			putJson(request, response, genJson("id", id));
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}

	}

	public void update(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter("userId");
		String userToken = request.getParameter("userToken");
		String requirementId = request.getParameter("requirementId");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		String rawExpire = request.getParameter("expire");
		String rawPrice = request.getParameter("price");
		String address = request.getParameter("address");
		String mandateFee = request.getParameter("mandateFee");
		// String hasMandate = request.getParameter("hasMandate");
		String rawType = request.getParameter("type");
		// String rawHasPrivateMessageContact =
		// request.getParameter("hasPrivateMessageContact");
		// String rawHasPhoneContact = request.getParameter("userId");
		String rawLongitude = request.getParameter("longitude");
		String rawLatitude = request.getParameter("latitude");
		// String rawStatus = request.getParameter("status");
		// String rawViewCount = request.getParameter("viewCount");
		String mainPictureData = request.getParameter("mainPictureData");
		String mainPictureName = request.getParameter("mainPictureName");

		checkParameterNotNull(request, response, "requirementId", requirementId);
		checkParameterNotNull(request, response, "userId", userId);
		checkParameterNotNull(request, response, "userToken", userToken);
		// checkUserToken(request, response);

		Map<String, Object> newVules = new HashMap<String, Object>();
		Set<String> keys = request.getParameterMap().keySet();
		if (keys.contains("mainPictureData")) {
			checkParameterNotNull(request, response, "mainPictureData", mainPictureData);
			newVules.put("mainPicture", savePicture(mainPictureData, request, response));
		}
		if (keys.contains("mainPictureName")) {
			checkParameterNotNull(request, response, "mainPictureName", mainPictureName);
			newVules.put("mainPictureName", mainPictureName);
		}
		if (keys.contains("address")) {
			newVules.put("address", address);
		}
		if (keys.contains("description")) {
			newVules.put("description", description);
		}
		if (keys.contains("expire")) {
			try {
				newVules.put("expire", DateUtils.parseTimestamp(rawExpire));
			} catch (ParseException e) {
				log.error("expire time date format error!", e);
				putError(request, response, "expire time date format error!", ErrorCodes.PARAMETER_INVALID);
			}
		}
		if (keys.contains("hasPrivateMessageContact")) {
			newVules.put("hasPrivateMessageContact", getBoolean(request, response, "hasPrivateMessageContact", true));
		}
		if (keys.contains("hasPhoneContact")) {
			newVules.put("hasPhoneContact", getBoolean(request, response, "hasPhoneContact", true));
		}
		if (keys.contains("longitude")) {
			checkParameterIsFloat(request, response, "longitude", rawLongitude);
			newVules.put("longitude", Double.parseDouble(rawLongitude));
		}
		if (keys.contains("latitude")) {
			checkParameterIsFloat(request, response, "latitude", rawLatitude);
			newVules.put("latitude", Double.parseDouble(rawLatitude));
		}
		if (keys.contains("manageFee")) {
			checkParameterIsFloat(request, response, "manageFee", mandateFee);
			newVules.put("manageFee", Float.parseFloat(mandateFee));
		}
		if (keys.contains("hasMandate")) {
			newVules.put("hasMandate", getBoolean(request, response, "hasMandate", true));
		}
		if (keys.contains("price")) {
			checkParameterIsFloat(request, response, "price", rawPrice);
			newVules.put("price", Float.parseFloat(rawPrice));
		}
		// if (keys.contains("status")) {
		// checkParameterIsNumber(request, response, "status", mandateFee);
		// newVules.put("status", Integer.parseInt(rawStatus));
		//
		// }
		if (keys.contains("title")) {
			newVules.put("title", title);

		}
		if (keys.contains("rawType")) {
			checkParameterIsNumber(request, response, "rawType", rawType);
			newVules.put("rawType", Integer.parseInt(mandateFee));

		}

		if (StringUtils.isNotEmpty(rawType)) {
			checkParameterIsNumber(request, response, "type", rawType);
			newVules.put("type", Integer.parseInt(rawType));
		}
		// if (StringUtils.isNotEmpty(rawType)) {
		// checkParameterIsNumber(request, response, "viewCount", rawType);
		// newVules.put("viewCount", Integer.parseInt(rawViewCount));
		// }

		try {
			RequirementOperations.instance.update(requirementId, newVules);
			putSuccess(request, response);
		} catch (HzException e) {
			// log.error(LogUtils.format("requestParams",
			// request.getParameterMap()), e);
			log.error("update error", e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error("update error", e);
			// log.error(LogUtils.format("requestParams",
			// request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void userRequirement(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter("userId");
		String userToken = request.getParameter("userToken");
		String startIndex = request.getParameter("startIndex");
		String pageSize = request.getParameter("pageSize");
		String rawstatus = request.getParameter("status");
		String rawType = request.getParameter("type");
		checkParameterNotNull(request, response, "userId", userId);
		checkParameterNotNull(request, response, "userToken", userToken);
		checkParameterIsNumber(request, response, "startIndex", startIndex);
		checkParameterIsNumber(request, response, "pageSize", pageSize);
		// checkUserToken(request, response);
		Integer status = null;
		Integer type = null;
		if (StringUtils.isNotEmpty(rawstatus)) {
			status = Integer.parseInt(rawstatus);
		}
		if (StringUtils.isNotEmpty(rawType)) {
			type = Integer.parseInt(rawType);
		}
		try {

			List<Requirement> result = RequirementOperations.instance.getUserRequirements(userId, status, type,
					Integer.valueOf(startIndex), Integer.valueOf(pageSize));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void getUserRequirements(HttpServletRequest request, HttpServletResponse response) {
		String targetUserId = getTargetUserId(request, response);
		try {
			Integer status = getInt(request, response, "status", false);
			List<Map<String, Object>> result = RequirementOperations.instance.getUserRequirements(targetUserId, status);
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	/*
	 * public void requirementInfo(HttpServletRequest request,
	 * HttpServletResponse response) { String requirementId =
	 * request.getParameter("requirementId"); String userId =
	 * request.getParameter(USER_ID); // String userToken =
	 * request.getParameter(USER_TOKEN); // String userToken =
	 * request.getParameter("userToken"); // String startIndex =
	 * request.getParameter("startIndex"); // String pageSize =
	 * request.getParameter("pageSize"); checkParameterNotNull(request,
	 * response, "requirementId", requirementId); //
	 * checkParameterNotNull(request, response, "userToken", userToken); //
	 * checkParameterIsNumber(request, response, "startIndex", startIndex); //
	 * checkParameterIsNumber(request, response, "pageSize", pageSize); try {
	 * 
	 * Requirement result = RequirementOperations.instance.get(requirementId);
	 * if(null == result){ putError(request, response,
	 * "no this requirement with id:"+requirementId,
	 * ErrorCodes.SERVER_DATA_NO_INCONSISTENT); } // if(StringU)
	 * RequirementOperations.instance.view(requirementId, userId);
	 * result.setVisitors
	 * (RequirementOperations.instance.getVisitors(requirementId, userId, 0,
	 * 10)); putJson(request, response, result); } catch
	 * (UserRequirementException e) { log.error(LogUtils.format("requestParams",
	 * request.getParameterMap()), e); putError(request, response,
	 * "failed to get user requirement ", ErrorCodes.SERVER_INNER_ERROR); } }
	 */
	/**
	 * 查询任务详情
	 * 
	 * @param request
	 * @param response
	 */
	public void requirementInfo(HttpServletRequest request, HttpServletResponse response) {
		String requirementId = request.getParameter("requirementId");
		String userId = request.getParameter(USER_ID);
		// 检查参数是否为空
		checkParameterNotNull(request, response, "requirementId", requirementId);
		//Integer shareQQ = getInt(request, response, "shareQQ", false);
		//Integer shareWB = getInt(request, response, "shareWB", false);
		try {
			Map<String, Object> result = RequirementOperations.instance.viewRequirement(requirementId, userId);
			if (null == result) {
				putError(request, response, "no this requirement with id:" + requirementId, ErrorCodes.SERVER_DATA_NO_INCONSISTENT);
			}
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void queryRequirement2(HttpServletRequest request, HttpServletResponse response) {
		// String userId = request.getParameter("userId");
		// String userToken = request.getParameter("userToken");

		String startIndex = request.getParameter("startIndex");
		String pageSize = request.getParameter("pageSize");
		String queryOrder = request.getParameter("queryOrder");
		String rawLongitude = request.getParameter("longitude");
		String rawLatitude = request.getParameter("latitude");
		String rawStatus = request.getParameter("status");
		String rawType = request.getParameter("type");
		// checkParameterNotNull(request, response, "userId", userId);
		// checkParameterNotNull(request, response, "userToken", userToken);
		checkParameterIsNumber(request, response, "startIndex", startIndex);
		checkParameterIsNumber(request, response, "pageSize", pageSize);
		// checkUserToken(request, response, userId, userToken);
		Map<String, Object> conditionArgs = new HashMap<>(2);
		Map<String, Object> orderArgs = new HashMap<>(2);
		try {

			QueryOrder order = QueryOrder.valueOf(queryOrder);
			if (StringUtils.isNotEmpty(rawStatus)) {
				checkParameterIsNumber(request, response, "status", rawStatus);
				conditionArgs.put("status", Integer.parseInt(rawStatus));
			}
			if (StringUtils.isNotEmpty(rawType)) {
				checkParameterIsNumber(request, response, "type", rawType);
				conditionArgs.put("type", Integer.parseInt(rawType));
			}
			if (order.equals(QueryOrder.nearest)) {
				checkParameterIsFloat(request, response, "longitude", rawLongitude);
				checkParameterIsFloat(request, response, "latitude", rawLatitude);
				orderArgs.put("longitude", Double.parseDouble(rawLongitude));
				orderArgs.put("latitude", Double.parseDouble(rawLatitude));
			}
			List<Requirement> result = RequirementOperations.instance.query(conditionArgs, orderArgs, QueryOrder.valueOf(queryOrder),
					Integer.valueOf(startIndex), Integer.valueOf(pageSize));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}

	}

	public void queryRequirement(HttpServletRequest request, HttpServletResponse response) {
		// String userId = request.getParameter("userId");
		// String userToken = request.getParameter("userToken");

		String startIndex = request.getParameter("startIndex");
		String pageSize = request.getParameter("pageSize");
		String queryOrder = request.getParameter("queryOrder");
		String rawLongitude = request.getParameter("longitude");
		String rawLatitude = request.getParameter("latitude");
		String rawStatus = request.getParameter("status");
		String rawType = request.getParameter("type");
		// checkParameterNotNull(request, response, "userId", userId);
		// checkParameterNotNull(request, response, "userToken", userToken);
		checkParameterIsNumber(request, response, "startIndex", startIndex);
		checkParameterIsNumber(request, response, "pageSize", pageSize);
		// checkUserToken(request, response, userId, userToken);
		Map<String, Object> conditionArgs = new HashMap<>(2);
		Map<String, Object> orderArgs = new HashMap<>(2);
		try {

			QueryOrder order = QueryOrder.valueOf(queryOrder);
			if (StringUtils.isNotEmpty(rawStatus)) {
				checkParameterIsNumber(request, response, "status", rawStatus);
				conditionArgs.put("status", Integer.parseInt(rawStatus));
			}
			if (StringUtils.isNotEmpty(rawType)) {
				checkParameterIsNumber(request, response, "type", rawType);
				conditionArgs.put("type", Integer.parseInt(rawType));
			}
			if (order.equals(QueryOrder.nearest)) {
				checkParameterIsFloat(request, response, "longitude", rawLongitude);
				checkParameterIsFloat(request, response, "latitude", rawLatitude);
				orderArgs.put("longitude", Double.parseDouble(rawLongitude));
				orderArgs.put("latitude", Double.parseDouble(rawLatitude));
			}
			List<Map<String, Object>> result = RequirementOperations.instance.query2(conditionArgs, orderArgs,
					QueryOrder.valueOf(queryOrder), Integer.valueOf(startIndex), Integer.valueOf(pageSize));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}

	}

	public void getComments(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		checkParameterNotNull(request, response, "requirementId");
		// checkParameterNotNull(request, response, "startIndex");
		// checkParameterNotNull(request, response, "pageSize");
		String requirementId = request.getParameter("requirementId");
		try {
			List<Map<String, Object>> comments = RequirementCommentService.instance.query(requirementId,
					getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, comments);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void queryComments(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		checkParameterNotNull(request, response, "requirementId");
		// checkParameterNotNull(request, response, "startIndex");
		// checkParameterNotNull(request, response, "pageSize");
		String requirementId = request.getParameter("requirementId");
		try {
			Map<String, Object> comments = RequirementCommentService.instance.queryComments(requirementId,
					getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, comments);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void comment(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String requirementId = request.getParameter("requirementId");
		String comment = request.getParameter("comment");
		String userId = request.getParameter(USER_ID);
		checkParameterNotNull(request, response, "requirementId", requirementId);
		checkParameterNotNull(request, response, "comment", comment);
		RequirementComment requirementComment = new RequirementComment();
		requirementComment.setComment(comment);
		requirementComment.setUserId(userId);
		requirementComment.setRequirementId(requirementId);
		try {
			RequirementCommentService.instance.save(requirementComment);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void deleteComment(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String commentId = request.getParameter("commentId");
		checkParameterNotNull(request, response, "commmentId", commentId);
		try {
			RequirementCommentService.instance.delete(commentId);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	private void binaryParticipate(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> map = uploadFields(request, response);
			List<Object> pictureUrls = new ArrayList<>(5);
			List<String> thumbs= new ArrayList<>(4);
			List<Tuple<Integer, Integer>> sizes = new ArrayList<>(1);
			sizes.add(new Tuple<>(100 ,100));
			for (int i = 1; i <= 4; i++) {
				File file = (File) map.get("pictureData" + i+"_file");
				if(null == file){
					continue;
				}
				Map<String, String> sizeUrls = savePictureThumbs(file, request, response, sizes);
				pictureUrls.add(sizeUrls.get("raw"));
				if(sizeUrls.containsKey("100x100")){
					thumbs.add(sizeUrls.get("100x100"));
				}else{
					thumbs.add(sizeUrls.get("raw"));
				}
//				String url = save(FileUtils.readFileToByteArray(file), request, response);
			}
//			pictureUrls.add(thumbs);
			Double candidateLongitude = getmd(map, "longitude", request, response, false); // Double.parseDouble(request.getParameter("longitude"));
			Double candidateLatitude = getmd(map, "latitude", request, response, false);// Double.parseDouble(request.getParameter("latitude"));
			String candidateAddress = getms(map, "address", request, response, false);
			RequirementCandidateService.instance.compete(getms(map, USER_ID, request, response, true),
					getms(map, "requirementId", request, response, true), getms(map, "words", request, response, false),
					JsonUtils.toJsonString(pictureUrls), JsonUtils.toJsonString(thumbs) , candidateLongitude, candidateLatitude, candidateAddress);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}

	}

	public void participate(HttpServletRequest request, HttpServletResponse response) {
		if (isMultipart(request)) {
			binaryParticipate(request, response);
			return;
		}
		// checkUserToken(request, response);
		try {
			// String pictureData = request.getParameter("pictureData");
			// List<String> pictureDatas = getMultiValues(request,
			// "pictureData");
			// List<String> picturePath = null;
			// if (null != pictureDatas) {
			// picturePath = savePictures(pictureDatas, request, response);
			// }
			List<String> pictureDatas = new ArrayList<>(4);
			String data = null;
			for (int i = 1; i <= 4; i++) {
				data = request.getParameter("pictureData" + i);
				if (StringUtils.isNotEmpty(data)) {
					pictureDatas.add(data);
				}
			}
			Double candidateLongitude = getDouble(request, response, "longitude", false); // Double.parseDouble(request.getParameter("longitude"));
			Double candidateLatitude = getDouble(request, response, "latitude", false);// Double.parseDouble(request.getParameter("latitude"));
			String candidateAddress = getString(request, response, "address", false);

			List<String> picturePath = new ArrayList<>();
//			if (null != pictureDatas && !pictureDatas.isEmpty()) {
//				picturePath = savePictures(pictureDatas, request, response);
//			}
			List<String> thumbPath  = new ArrayList<>();
			RequirementCandidateService.instance.compete(
					getString(request, response, USER_ID, true),
					// <<<<<<< .mine
					// getString(request, response, "requirementId", true),
					// getString(request, response, "words", false),
					// JsonUtils.toJsonString(picturePath));
					// =======
					getString(request, response, "requirementId", true), getString(request, response, "words", false),
					JsonUtils.toJsonString(picturePath) , JsonUtils.toJsonString(thumbPath) , candidateLongitude, candidateLatitude, candidateAddress);
			// >>>>>>> .r1744
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void selectCandidate(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String requirementId = getString(request, response, "requirementId", true);
		String candidateId = getString(request, response, "candidateId", true);
		String userId = request.getParameter(USER_ID);
		if (candidateId.equals(userId)) {
			putError(request, response, "can not set to youself to be selected candidate", ErrorCodes.PARAMETER_INVALID);
		}
		try {
			RequirementCandidateService.instance.selectCandidate(requirementId,userId, candidateId);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void participatedRequirements(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		try {
			List<Map<String, Object>> result = RequirementCandidateService.instance.queryUserCompeteRequirements(userId,
					getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		} catch (UserRequirementException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "failed to query participated requirements", ErrorCodes.SERVER_INNER_ERROR);
		}
	}

	public void selectedRequirements(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		try {
			List<Map<String, Object>> result = RequirementCandidateService.instance.queryUserCompeteRequirements(userId,
					getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void acceptedRequirements(HttpServletRequest request, HttpServletResponse response) {
		try {
			// checkUserToken(request, response);
			String rawStatus = request.getParameter("status");
			Integer status = null;
			if (StringUtils.isNotEmpty(rawStatus)) {
				status = getInt(request, response, "status", false);
			}
			String userId = getTargetUserId(request, response);
			List<Map<String, Object>> result = RequirementCandidateService.instance.queryUserAcceptedRequirements(userId, status,
					getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}

	}

	public void completeRequirement(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		try {
			// RequirementOperations.instance.updateStatus(userId,
			// getString(request, response, "requirementId", true),
			// RequirementStatus.complete);
			String requirementId = getString(request, response, "requirementId", true);
			String password = getString(request, response, "password", true);
			RequirementOperations.instance.complete(userId, requirementId, password);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void drawbackRequirement(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		try {
			RequirementOperations.instance.updateStatus(userId, getString(request, response, "requirementId", true),
					RequirementStatus.drawbacked);
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void closeRequirement(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String userId = request.getParameter(USER_ID);
		try {
			// RequirementOperations.instance.updateStatus(userId,
			// getString(request, response, "requirementId", true),
			// RequirementStatus.closed);
			RequirementOperations.instance.close(userId, getString(request, response, "requirementId", true));
			putSuccess(request, response);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	public void userIssuedRequirement(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		// String userId = request.getParameter(USER_ID);
		String targetUserId = getTargetUserId(request, response);
		try {
			List<Map<String, Object>> result = RequirementOperations.instance.getUserIssuedRequirement(targetUserId,
					getInt(request, response, "status", false), getInt(request, response, "type", false),
					getInt(request, response, "startIndex", true), getInt(request, response, "pageSize", true));
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}
	}

	/**
	 * 需求竞选者 {selectedCandidate:{id:"" , name:"" , picture:"" , sex:1 ,
	 * credit:0} , candidates:[{id:"" , name:"" , picture:"" , sex:1 , credit:0}
	 * ]}
	 * 
	 * @param request
	 * @param response
	 */
	public void requirementCandidates(HttpServletRequest request, HttpServletResponse response) {
		// checkUserToken(request, response);
		String requirementId = request.getParameter("requirementId");
		int startIndex = getInt(request, response, "startIndex", true);
		int pageSize = getInt(request, response, "pageSize", true);
		try {
			Map<String, Object> result = RequirementCandidateService.instance
					.queryRequirementCandidate(requirementId, startIndex, pageSize);
			putJson(request, response, result);
		} catch (HzException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, e);
		}

	}

	/**
	 * 需求浏览人 [{id:"" , name:"" , picture:"" , sex:1 , credit:0}]
	 * 
	 * @param request
	 * @param response
	 */
//	public void requirementVisitors(HttpServletRequest request, HttpServletResponse response) {
//		// checkUserToken(request, response);
//		String userId = request.getParameter(USER_ID);
//		String requirementId = getString(request, response, "requirementId", true);
//		try {
//			List<RequirementVisitor> result = RequirementOperations.instance.getVisitors(requirementId, userId, 0, 10);
//			putJson(request, response, result);
//		} catch (HzException e) {
//			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
//			putError(request, response, e);
//		} catch (Exception e) {
//			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
//			putError(request, response, e);
//		}
//	}

	/*
	 * public void scoreCompleter(HttpServletRequest request,
	 * HttpServletResponse response) { // checkUserToken(request, response);
	 * String userId = request.getParameter(USER_ID); String comment =
	 * request.getParameter("comment"); String requirementId =
	 * getString(request, response, "requirementId", true); int scoreLevel =
	 * getIntInRange(request, response, "scoreLevel", true, 0, 2); try {
	 * RequirementOperations.instance.scoreComplete(userId, requirementId,
	 * scoreLevel, comment); putSuccess(request, response); } catch (HzException
	 * e) { log.error(LogUtils.format("requestParams",
	 * request.getParameterMap()), e); putError(request, response, e); } catch
	 * (Exception e) { log.error(LogUtils.format("requestParams",
	 * request.getParameterMap()), e); putError(request, response, e); } }
	 * 
	 * 
	 * public void scoreSponsor(HttpServletRequest request, HttpServletResponse
	 * response) { // checkUserToken(request, response); String userId =
	 * request.getParameter(USER_ID); String comment =
	 * request.getParameter("comment"); String requirementId =
	 * getString(request, response, "requirementId", true); int scoreLevel =
	 * getIntInRange(request, response, "scoreLevel", true, 0, 2); try {
	 * RequirementOperations.instance.scoreSponsor(userId, requirementId,
	 * scoreLevel, comment); putSuccess(request, response); } catch (HzException
	 * e) { log.error(LogUtils.format("requestParams",
	 * request.getParameterMap()), e); putError(request, response, e); } catch
	 * (Exception e) { log.error(LogUtils.format("requestParams",
	 * request.getParameterMap()), e); putError(request, response, e); } }
	 */
	public void getRequirementsInRegion(HttpServletRequest request, HttpServletResponse response) {
		double maxLatitude = getDoubleInRange(request, response, "maxLatitude", true, -90, 90);
		double maxLongitude = getDoubleInRange(request, response, "maxLongitude", true, -180, 180);
		double minLatitude = getDoubleInRange(request, response, "minLatitude", true, -90, 90);
		double minLongitude = getDoubleInRange(request, response, "minLongitude", true, -180, 180);
		QueryOrder order = (QueryOrder) getEnum(request, response, "order", true, QueryOrder.class);
		double startLatitude = Math.min(maxLatitude, minLatitude);
		double endLatitude = Math.max(maxLatitude, minLatitude);
		double startLongitude = Math.min(maxLongitude, minLongitude);
		double endLongitude = Math.max(maxLongitude, minLongitude);

		// double radius = getDouble(request, response, "radius", true);
		String queryOrder = "order by ";
		switch (order) {
		case newest:
			queryOrder += "r.createTime desc,r.viewCount desc";
			break;
		case hotest:
			queryOrder += "r.viewCount desc,r.createTime desc";

			break;
		case nearest:
			double midLongitude = (endLongitude - startLongitude) / 2;
			double midLatitude = (endLatitude - startLatitude) / 2;
			queryOrder += "power((r.longitude-" + midLongitude + ") ,2)+ power((r.latitude-" + midLatitude + "),2) asc, r.createTime desc";
			break;

		default:
			queryOrder = "";
			break;
		}

		Integer size = getInt(request, response, "size", false);
		String sql = "select r.id, r.latitude ,longitude, r.title,u.mainPicture as userPicture, u.mainPictureSmall  as userPictureSmall, u.mainPictureMid  as userPictureMid, u.mainPictureBig as userPictureBig  from Requirement r left join User u on r.userId=u.id where r.status="+RequirementStatus.published+" and latitude between "
				+ startLatitude
				+ " and "
				+ endLatitude
				+ " and longitude between "
				+ startLongitude
				+ " and "
				+ endLongitude
				+ " "
				+ queryOrder + (null == size ? "" : " limit " + size);
		try {
			List<Map<String, Object>> result = QuickDB.gets(sql);
			Collections.sort(result, new Comparator<Map<String, Object>>() {

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					double latitude1 = (double) o1.get("latitude");
					double latitude2 = (double) o2.get("latitude");
					if (latitude1 > latitude2) {
						return -1;
					} else if (latitude1 == latitude2) {
						return 0;
					} else {
						return 1;
					}
				}
			});
			putJson(request, response, result);
		} catch (SQLException e) {
			log.error("", e);
			putError(request, response, "get requirements in region error!", ErrorCodes.SERVER_INNER_ERROR);
		}
	}
	
	public void shareWB(HttpServletRequest request, HttpServletResponse response) throws UserRequirementException {
		String userId = request.getParameter(USER_ID);
		try {
			int result = RequirementOperations.instance.shareWB(getString(request, response, "requirementId", true), userId);
			log.debug("result type:" + result);
			putJson(request, response, result);
		} catch (UserRequirementException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "share weibo failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
	
	public void shareWBQQ(HttpServletRequest request, HttpServletResponse response) throws UserRequirementException {
		String userId = request.getParameter(USER_ID);
		try {
			RequirementOperations.instance.shareWBQQ(getString(request, response, "requirementId", true), userId,getInt(request, response, "shareWB", false), getInt(request, response, "shareQQ", false));
			putSuccess(request, response);
		} catch (UserRequirementException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request, response, "share weibo failed!", ErrorCodes.PARAMETER_INVALID);
		}
	}
}
