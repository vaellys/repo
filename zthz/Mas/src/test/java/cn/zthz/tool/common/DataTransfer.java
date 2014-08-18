package cn.zthz.tool.common;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.ResultSetMap;

public class DataTransfer {
	private static final Log log = LogFactory.getLog(DataTransfer.class);
	public static void dataTransfer(){
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String dataSql = "select * from RequirementVisitor";
			List<Map<String, Object>> results = ResultSetMap.maps(statement.executeQuery(dataSql));
			List<String> rids = new ArrayList<>();
			for(Map<String, Object> map : results){
				rids.add((String)map.get("requirementId"));
			}
			String dataVSql = "select requirementId,userId,viewTime from RequirementVisitor where requirementId in('"+StringUtils.join(rids,"','")+"') order by viewTime desc";
			if(log.isDebugEnabled()){
				log.debug(dataSql);
			}
			List<Map<String, Object>> list = ResultSetMap.maps(statement.executeQuery(dataVSql));
			Map<String, List<Map<String, Object>>> rvs = new HashMap<>();
			for (Map<String, Object> map : list) {
				String rid = (String) map.get("requirementId");
				if(rvs.containsKey(rid)){
					if(rvs.get(rid).size()>=3){
						continue;
					}
					rvs.get(rid).add(map);
				}else{
					List<Map<String, Object>> irvs = new LinkedList<>();
					irvs.add(map);
					rvs.put(rid , irvs);
				}
			}
			for(Map.Entry<String, List<Map<String, Object>>> entry : rvs.entrySet()){
				
				for(Map<String, Object> map : entry.getValue()){
					String vTopSql = "insert into VTop(requirementId, visitorId, ts) values('"+ entry.getKey() + "','" + map.get("userId") + "'," + ((Timestamp)map.get("viewTime")).getTime() + ")";
					statement.executeUpdate(vTopSql);
				}
			}
		}catch(Exception e){
			log.error("", e);
		}
	}
	public static void main(String[] args) {
//		dataTransfer();
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("1", "2");
		list.add(map);
		System.out.println(LogUtils.format("k", CollectionUtils.extract(list, "1", true)));
	}
	
	
}
