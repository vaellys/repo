package cn.zthz.tool.requirement;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;

public class RequirementCandidateTest {
	
//	@Test
//	public void testCompete() throws UserRequirementException{
//		String id = RequirementCandidateService.instance.compete("122334543452", "402880173d334d2c013d334d2c7f0000", "haha", "402880173c0ede2f013c0ee75c6d0002", 116.2585, 39.2545, "北京市海淀区学院路29号");
//		System.out.println(id);
//	}
	
	@Test
	public void testQueryUserCompeteRequirements() throws UserRequirementException{
		List<Map<String, Object>> result = RequirementCandidateService.instance.queryUserCompeteRequirements("402880173cf01658013cf0215cf40001", 0, 10);
		System.out.println(LogUtils.format("result",result));
	}
	
	@Test
	public void testQueryUserAcceptedRequirements() throws UserRequirementException {
		List<Map<String, Object>> result = RequirementCandidateService.instance.queryUserAcceptedRequirements("1", null, 0, 10);
		System.out.println(LogUtils.format("result" , result));
	}
	@Test
	public void testQueryRequirementCandidate() throws UserRequirementException {
		Map<String, Object> result = RequirementCandidateService.instance.queryRequirementCandidate("ff8080813c05aec1013c05aec3f60001", 0, 10);
		System.out.println(LogUtils.format("result" , result));
	}
	@Test
	public void testQueryRequirementCandidate1() throws UserRequirementException {
		RequirementCandidateService.instance.selectCandidate("ff8080813c05b124013c05b124690003","",  "1");
	}

}
