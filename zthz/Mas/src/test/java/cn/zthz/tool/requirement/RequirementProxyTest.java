package cn.zthz.tool.requirement;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.proxy.RequirementProxy;
import cn.zthz.tool.proxy.RequirementProxyException;

public class RequirementProxyTest {

	@Test
	public void testSetRequirements() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRequirements() throws UserRequirementException, RequirementProxyException {
		Map<String, Object> conditionArgs = new HashMap<>();
		conditionArgs.put("status", 0);
		conditionArgs.put("type", 1);
		List<Map<String, Object>> requirements = RequirementProxy.getRequirements(conditionArgs, null, "newest", 0, 10);
		
		System.out.println(LogUtils.format(requirements));
	}

//	@Test
//	public void testExpireRequirements() {
//		RequirementProxy.expireRequirements("newest");
//	}

}
