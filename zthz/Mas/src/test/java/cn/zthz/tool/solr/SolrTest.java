package cn.zthz.tool.solr;

import java.io.IOException;

import org.junit.Test;

import cn.zthz.actor.solr.SolrUpdater;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.user.User;

public class SolrTest {
	
	@Test
	public void testGet() throws IOException{
		String result = HttpUtils.doGet("http://hz1/solr/user/select?q=jim&wt=json");
		System.out.println(result);
	}
	
	@Test
	public void testAddUser() throws Exception{
		User user = new User();
		user.setId("11111");
		user.setName("qgb");
		user.setSex(0);
		user.setSkills("软件开发，信息技术,fafd");
		user.setEmail("617907867@qq.com");
		user.setAddress("kk");
		
		SolrUpdater.addUser(user);
		
		
	}

}
