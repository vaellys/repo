package cn.zthz.tool.user;

import org.junit.Test;

import cn.zthz.tool.common.HzException;

public class UserCollectServiceTest {

	@Test
	public void testAdd() throws HzException {
		UserCollectService.instance.add("402880173f07abcf013f08a20b1b002e", "402880173f0e6534013f12293cbb0004");
	}

}
