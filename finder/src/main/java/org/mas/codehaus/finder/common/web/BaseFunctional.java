package org.mas.codehaus.finder.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mas.codehaus.finder.common.ErrorMessage;
import org.mas.codehaus.finder.common.JsonUtils;
import org.mas.codehaus.finder.common.web.exception.FinderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseFunctional{
	protected  Logger log = LoggerFactory.getLogger(BaseFunctional.class);
	
	protected static final String SUCCESS = "{\"result\":\"success\"}";
	
	protected void putError(HttpServletRequest request,
			HttpServletResponse response, Exception e) {
		FinderException e1 = (FinderException) e;
		ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
				e1.errorCode(), request.getRequestURI());
		ResponseUtils
				.renderJson(response, JsonUtils.toJsonString(errorMessage));
	}
	
	protected void putJson(HttpServletRequest request, HttpServletResponse response, Object result) {
		if (result instanceof String) {
			ResponseUtils.renderJson(response, (String)result);
		} else {
			String json = JsonUtils.toJsonString(result);
			ResponseUtils.renderJson(response, json);
		}
	}
	
	protected void putSuccess(HttpServletRequest request, HttpServletResponse response) {
		putJson(request, response, SUCCESS);
	}
}
