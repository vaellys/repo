package cn.zthz.actor.daemon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.tool.account.AccountException;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.CollectionUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.ConnectionUtils;
import cn.zthz.tool.requirement.RequirementStatus;

public class RequirementExpirer implements Runnable {
	private static final Log log = LogFactory.getLog(RequirementExpirer.class);
	public static final RequirementExpirer instance = new RequirementExpirer();
	public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private long period = 5;

	public void start() {
		scheduler.scheduleAtFixedRate(this, 1, period, TimeUnit.MINUTES);
	}

	@Override
	public void run() {
		try {
			expire();
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public static void main(String[] args) {
		instance.expire();
	}

	public void expire() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			connection.setAutoCommit(false);
			List<Map<String, Object>> result = null;
			int startIndex = 0;
			int pageSize = 100;
			do {
				String querySql = createExpireSql(startIndex, pageSize);
				ResultSet resultSet = statement.executeQuery(querySql);
				result = ResultSetMap.maps(resultSet, pageSize);
				if (result.isEmpty()) {
					break;
				}
				startIndex = startIndex + pageSize;
				// batchUpdate(statement, CollectionUtils.extract(result, "id",
				// true, String.class));
				batchUpdate(statement, result);
			} while (result != null && !result.isEmpty());
		} catch (SQLException e) {
			log.error("", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	private void batchUpdate(Statement statement, List<Map<String, Object>> result) {
		for (Map<String, Object> map : result) {
			String requirementId = (String)map.get("id");
			if(Boolean.TRUE.equals(map.get("hasMandate"))){
				try {
					AccountService.instance.payback(statement, requirementId );
					statement.executeUpdate(createUpdateSql(requirementId));
					statement.getConnection().commit();
					Global.queue.publish(QueueSubjects.REQUIREMENT_EXPIRED, map);
				} catch (Exception e) {
					log.error("expire requirement failed rollback this tranaction, requirementId:"+requirementId , e);
					try {
						ConnectionUtils.rollback(statement.getConnection());
					} catch (SQLException e1) {
						log.error("rollback expire requirement failed , requirementId:"+requirementId , e);
						e1.printStackTrace();
					}
				}
			}else{
				try {
					statement.executeUpdate(createUpdateSql(requirementId));
					statement.getConnection().commit();
					Global.queue.publish(QueueSubjects.REQUIREMENT_EXPIRED, map);
				} catch (Exception e) {
					log.error("expire requirement failed , requirementId:"+requirementId , e);
				}
			}
			
		}
	}

	protected String createExpireSql(int startIndex, int pageSize) {
		StringBuilder stringBuilder = new StringBuilder(110);
		stringBuilder.append("select id , title , userId ,hasMandate,price from Requirement where status=");
		stringBuilder.append(RequirementStatus.published);
		stringBuilder.append(" and expire<=now() order by id asc limit ");
		stringBuilder.append(startIndex);
		stringBuilder.append(",");
		stringBuilder.append(pageSize);
		return stringBuilder.toString();
	}

	protected String createUpdateSql(String requirementId) {
		StringBuilder stringBuilder = new StringBuilder(110);
		stringBuilder.append("update Requirement set status=");
		stringBuilder.append(RequirementStatus.expired);
		stringBuilder.append(" where id='");
		stringBuilder.append(requirementId);
		stringBuilder.append('\'');
		return stringBuilder.toString();
	}

	public void batchUpdate(Statement statement, Collection<String> requirementIds) throws SQLException {
		if (null == requirementIds || requirementIds.isEmpty()) {
			return;
		}
		for (String requirementId : requirementIds) {
			statement.addBatch(createUpdateSql(requirementId));
		}
		statement.executeBatch();

	}

}
