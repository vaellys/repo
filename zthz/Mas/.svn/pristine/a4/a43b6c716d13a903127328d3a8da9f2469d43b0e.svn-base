package cn.zthz.tool.requirement;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.db.ResultSetMap;

public class Vtop {
	private static final Log log = LogFactory.getLog(Vtop.class);
	public static final Vtop instance = new Vtop();
	public void update(Statement statement, String requirementId, String userId) throws HzException{
		if(StringUtils.isEmpty(userId)){
			return;
		}
		try{
			String vTopSql = "select *  from VTop where requirementId = '" + requirementId + "' order by ts asc";
			if(log.isDebugEnabled()){
				log.debug(vTopSql);
			}
			List<Map<String, Object>> topVistors = ResultSetMap.maps(statement.executeQuery(vTopSql),3);
			boolean hasVisited = false;
			for (Map<String, Object> map : topVistors) {
				if(userId.equals(map.get("visitorId"))){
					hasVisited = true;
					break;
				}
			}
			if(topVistors.size() >= 3 || hasVisited){
				long ts = (Long)(topVistors.get(0).get("ts"));
				String vTopUSql = "update VTop set visitorId='" + userId + "', ts="+System.currentTimeMillis()+" where requirementId='"+requirementId+"' and ts="+ts;
				if(log.isDebugEnabled()){
					log.debug(vTopUSql);
				}
				statement.executeUpdate(vTopUSql);
			}else{
				String vTopISql = "insert into VTop(requirementId,visitorId,ts) values('" + requirementId + "','" + userId + "',"+System.currentTimeMillis()+")";
				if(log.isDebugEnabled()){
					log.debug(vTopISql);
				}
				statement.executeUpdate(vTopISql);
			}
			
		}catch(Exception e){
			log.error("modify visit top failed!" ,e);
			throw new HzException("modify visit top failed!", e);
		}
	}
}
