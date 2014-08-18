package cn.zthz.tool.message;

import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;

public class RMessageServiceTest {

	@Test
	public void testGetRMessage() throws HzException {
		Map<String, Object> RMessage = RMessageService.instance.getRMessage(1, "1");
		System.out.println(LogUtils.format(RMessage));
	}

}
