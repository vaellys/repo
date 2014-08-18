package cn.zthz.tool.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;

public class EmployerServiceTest {

	@Test
	public void testHome() throws HzException {
		Map<String, Object> result = EmployerService.instance.home("402880173f07abcf013f08a0f97f002d");
		System.out.println(LogUtils.format("r", result));
	}
	
	@Test
	public void testRequirementManagement() throws HzException{
		Map<String, Object> conditionArgs = new HashMap<>();
		conditionArgs.put("status", 0);
		List<Map<String, Object>> results = EmployerService.instance.requirementManagement("402880173f07abcf013f08a0f97f002d", null, 1, 10);
		System.out.println(LogUtils.format("r", results));
	}
	@Test
	public void testEmploymentSuccess() throws HzException{
		Map<String, Object> results = EmployerService.instance.employmentSuccess("402880173f07abcf013f08a0f97f002d", 1, 10);
		System.out.println(LogUtils.format("r", results));
		
	}
	
	@Test
	public void testMandate() throws HzException{
		EmployerService.instance.mandate("40288017401a2c3801402845888e001e");
	}

}
