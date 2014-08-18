package cn.zthz.tool.cache;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import cn.zthz.tool.cache.user.UserCache;
import cn.zthz.tool.cache.user.UserStatus;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.user.User;

public class RedisCacheTest extends TestCase{
	@Test
	public void testGet(){
		User result = UserProxy.getUserInfo("402880173c243c51013c287d4c9b000d");
		System.out.println(LogUtils.format("user" , result));
		
	}
	
	@Test
	public void testExpire(){
		UserProxy.expireUser("402880173c243c51013c287d4c9b000d");
	}
	
	@Test
	public void testHttps(){
		HttpClient httpClient = new HttpClient();
		HttpMethod method = new PostMethod();
	}
	
	@Test
	public void testRefreshUser(){
		UserProxy.refreshUserCaches("402880173f07abcf013f08a20b1b002e");
	}

}
