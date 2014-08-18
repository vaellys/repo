package cn.zthz.tool.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.cache.RedisCache;
import cn.zthz.tool.requirement.RequirementOperations;
import cn.zthz.tool.requirement.RequirementOperations.QueryOrder;
import cn.zthz.tool.requirement.UserRequirementException;


public class RequirementProxy {
	private static final Log log = LogFactory.getLog(RequirementProxy.class);
	
	public static void setRequirements(List<Map<String, Object>> requirements, String queryOrder){
		QueryOrder queryOrderEnum = QueryOrder.valueOf(queryOrder);
		if(queryOrderEnum.equals(QueryOrder.newest)){
			RedisCache.instance.setObject("requirements.'" + queryOrder + "'", requirements);
		}else if(queryOrderEnum.equals(QueryOrder.hotest)){
			RedisCache.instance.setObject("requirements.'" + queryOrder + "'", requirements);
		}
	}
	
	public static List<Map<String, Object>> getRequirements(Map<String, Object> conditionArgs, Map<String, Object> orderArgs, String queryOrder, int startIndex, int pageSize) throws UserRequirementException, RequirementProxyException{
		QueryOrder querOrderEnum = QueryOrder.valueOf(queryOrder);
		List<Map<String, Object>> requirements = (List<Map<String, Object>>)RedisCache.instance.getObject("requirements.'" + queryOrder + "'");;
		try{
			if(requirements != null){
				return requirements;
			}else{
				requirements = RequirementOperations.instance.query2(conditionArgs, orderArgs, querOrderEnum, startIndex, pageSize);
				setRequirements(requirements, queryOrder);
				requirements = (List<Map<String, Object>>)RedisCache.instance.getObject("requirements.'" + queryOrder + "'");
				return requirements;
			}
		}catch(Exception e){
			log.error("query from redis failed!");
			throw new RequirementProxyException("query from redis failed!", e);
		}
	}
}
