package cn.zthz.tool.user;

import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;

public class UserStaticsTest {

	@Test
	public void testUrs() throws UserException {
		Map<String, Object> map = UserStatics.instance.urs("402880173dce7ef6013de27726070127");
		System.out.println(LogUtils.format("m", map));
	}

}
