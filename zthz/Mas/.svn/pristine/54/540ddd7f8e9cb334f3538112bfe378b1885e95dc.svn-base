package cn.zthz.actor.message;

import java.io.IOException;

import cn.zthz.tool.common.HttpUtils;

public class SolrRequirementTools {
	
	public void addIndex(){
		
	}
	
	public void deleteIndex(String id){
		String deleteUrl= "http://hz1/solr/user/update/?stream.body=%3Cdelete%3E%3Cid%3E402880173c193e49013c19538fd5000d%3C/id%3E%3C/delete%3E&stream.contentType=text/xml;charset=utf-8&commit=true";
		try {
			String response = HttpUtils.doGet(deleteUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
