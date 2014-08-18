package cn.zthz.tool.user;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

import cn.zthz.tool.common.DateUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.proxy.UserProxy;

public class UserProxyTest {

	@Test
	public void testGetUserToken() {
		 UserProxy.getUserToken("402880173c243c51013c28524a22000b");
	}
	@Test
	public void testGetUserInfo(){
		User user = UserProxy.getUserInfo("402880173dfc0e14013dfca0b8d800a9");
		System.out.println(LogUtils.format("u",user));
	}
	@Test
	public void testDate(){
		long t = 1368777418155l;
		System.out.println( DateUtils.format(new Timestamp(t)));
	}
	
}
