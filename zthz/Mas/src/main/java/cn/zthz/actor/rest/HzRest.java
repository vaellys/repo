package cn.zthz.actor.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zthz.tool.common.ConfigUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.QuickDB;

public class HzRest extends FunctionalRest {
	private final static Map<String, String> aboutUs = ConfigUtils.getProperties("about-us.properties");

	public void aboutUs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		putJson(request, response, aboutUs);
	}

	public void feedback(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userId = request.getParameter(USER_ID);
		String userToken = request.getParameter(USER_TOKEN);
		String feedback = request.getParameter("feedback");
//		checkParameterNotNull(request, response, "userId", userId);
		checkParameterNotNull(request, response, "feedback", feedback);
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = Connections.instance.get();
			preparedStatement = connection.prepareStatement("insert into Feedback (userId , feedback ,createTime) values (?,?,?)");
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, feedback);
			preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					putError(request, response, e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
				}
			}
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e1) {
					putError(request, response, e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
				}
			}
		}

		putSuccess(request, response);
	}
	
	public void appVersion(HttpServletRequest request, HttpServletResponse response) {
		String version = getString(request, response, "version", true);
		int type = getInt(request, response, "type", true);
		try {
			putJson(request, response, QuickDB.get("select version , url , updateInfo from AppInfo where version>'"+version+"' and type="+type+" order by version desc limit 1"));
		} catch (SQLException e) {
			putError(request, response, "获取版本信息失败", ErrorCodes.SERVER_INNER_ERROR);
		}
	}

}
