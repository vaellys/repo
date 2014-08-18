package cn.zthz.actor.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.LogUtils;

import qileke.jer.server.ActionException;
import qileke.jer.server.ExceptionHandler;

public class MasRestExceptionHandler extends Rest implements ExceptionHandler {
	
	private static final Log log = LogFactory.getLog(MasRestExceptionHandler.class);

	@Override
	public void handle(ActionException e, HttpServletRequest request, HttpServletResponse response) {
		log.warn("no this service;request url:"+request.getRequestURL()+" "+ LogUtils.format("params" ,request.getParameterMap()) ,e);
		putJson(request, response, new ErrorMessage("we don't have this service", ErrorCodes.PARAMETER_INVALID, request.getRequestURI()));
	}

	@Override
	public void handle(Throwable e, HttpServletRequest request, HttpServletResponse response) {
		log.error("service internal error;request url:"+request.getRequestURL()+" "+ LogUtils.format("params" ,request.getParameterMap()),e);
		putJson(request, response, new ErrorMessage("service internal error!", ErrorCodes.SERVER_INNER_ERROR, request.getRequestURI()));
	}

}
