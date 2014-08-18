package cn.zthz.tool.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;

public class EmployeeServiceTest {

	@Test
	public void testHome() throws HzException {
		Map<String, Object> result = EmployeeService.instance.home("402880173f07abcf013f08a0f97f002d");
		System.out.println(LogUtils.format("r", result));
	}
	
	@Test
	public void testRequirementManagement() throws HzException{
		Map<String, Object> conditionArgs = new HashMap<>();
		conditionArgs.put("status", 0);
		List<Map<String, Object>> results = EmployeeService.instance.requirementManagement("402880173f07abcf013f08a0f97f002d", null, 1, 10);
		System.out.println(LogUtils.format("r", results));
	}
	
	@Test
	public void testRequirementManagement2() throws HzException{
		Map<String, Object> conditionArgs = new HashMap<>();
		conditionArgs.put("status", 3);
		List<Map<String, Object>> results = EmployeeService.instance.requirementManagement("402880173f07abcf013f08a0f97f002d", conditionArgs, 1, 10);
		System.out.println(LogUtils.format("r", results));
	}
	
	@Test
	public void testEmploymentSuccess() throws HzException{
		Map<String, Object> results = EmployeeService.instance.employmentSuccess("402880173f07abcf013f08a0f97f002d", 1, 10);
		System.out.println(LogUtils.format("r", results));
		
	}
	
	@Test
	public void testRequirements() throws HzException{
		Map<String, Object> results = EmployeeService.instance.requirements("402880173f07abcf013f08a0f97f002d", 0, null,0,10);
		System.out.println(LogUtils.format("r", results));
		
	}
	
	@Test
	public void testAbilityTags() throws HzException{
		Map<String, List<Object>> results = EmployeeService.instance.abilityTags("402880173f07abcf013f08a0f97f002d");
		System.out.println(LogUtils.format("k", results));
	}
	
	@Test
	public void tesAddTags() throws HzException{
		List<Integer> list = new ArrayList<>();
		list.add(0);
		EmployeeService.instance.addTags("402880173f07abcf013f08a0f97f002d", list);
	}

}
