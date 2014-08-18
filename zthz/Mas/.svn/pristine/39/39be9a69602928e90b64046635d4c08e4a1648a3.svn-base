package cn.zthz.actor.rest;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.common.DateUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.user.UserException;
import cn.zthz.tool.user.UserStatics;

public class UserStaticsRest extends FunctionalRest  {
	
	public void getLoginCountByDay(HttpServletRequest request, HttpServletResponse response) {
		try {
			Date date = DateUtils.parseDate(getString(request , response,"date" , true));
			Timestamp start = new Timestamp(date.getTime());
			Timestamp end = new Timestamp(date.getTime()+24L*3600*1000);
			Map<String ,Object> args = new HashMap<>();
			args.put("start",start);
			args.put("end" ,end);
			putJson(request , response, QuickDB.getM("select count(id) as count from UserLogin where time between :start and :end",args ));
		} catch (SQLException e) {
			putError(request , response , "..." , ErrorCodes.SERVER_INNER_ERROR);
			e.printStackTrace();
		} catch (ParseException e) {
			putError(request , response , "date forat error , please use yyyy-MM-dd format!" , ErrorCodes.PARAMETER_INVALID);
		}
	}
	public void getLoginCountByMonth(HttpServletRequest request, HttpServletResponse response) {
		
	}
	public void getLoginCountByPeriod(HttpServletRequest request, HttpServletResponse response) {
		
	}
	public void urs(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try {
			Map<String, Object> result = UserStatics.instance.urs(userId);
			putJson(request, response, result);
		} catch (UserException e) {
			log.error(LogUtils.format("requestParams", request.getParameterMap()), e);
			putError(request , response , "user statics error" , ErrorCodes.PARAMETER_INVALID);
		}
	}
	
	


}
