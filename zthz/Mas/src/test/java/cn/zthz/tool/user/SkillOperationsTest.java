package cn.zthz.tool.user;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;

public class SkillOperationsTest {

	@Test
	public void testAddSkill() throws HzException {
		SkillOperations.instance.addSkill("402880173dfc0e14013dfca0b8d800a9", "2", "12345");
	}

	@Test
	public void testDeleteSkill() throws HzException {
		SkillOperations.instance.deleteSkill(5);
	}

	@Test
	public void testUpdateSkill() throws HzException {
		SkillOperations.instance.updateSkill(80, "qgb", "qgb");
	}

	@Test
	public void testGet() throws HzException {
		List<Map<String, Object>> result = SkillOperations.instance.get("402880173dfc0e14013dfca0b8d800a9");
		System.out.println(LogUtils.format("r", result));
	}

}
