package cn.zthz.tool.user;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.db.QuickDB;

public class ReportService {
	private static final Log log = LogFactory.getLog(ReportService.class);
	public static final ReportService instance = new ReportService();
	public void report(String userId , String requirementId , String report ,Integer type) throws UserException{
		StringBuilder sql = new StringBuilder();
		sql.append("insert into Report (userId, requirementId , report , type , status , createTime ) values('");
		sql.append(userId);
		sql.append("','");
		sql.append(requirementId);
		sql.append("','");
		sql.append(report);
		sql.append("',");
		sql.append(type);
		sql.append(",");
		sql.append(UserReportStatus.UN_HANDLED);
		sql.append(",'");
		sql.append(new Timestamp(System.currentTimeMillis()));
		sql.append("')");
		if(log.isDebugEnabled()){
			log.debug(sql.toString());
		}
		try {
			QuickDB.insert(sql.toString());
		} catch (SQLException e) {
			log.error("user report failed! userId:'" + userId + "',requirementId:'" + requirementId + "',type:" + type);
			throw new UserException("user report failed! userId:'" + userId + "',requirementId:'" + requirementId + "',type:" + type);
		}
		
	}
}
