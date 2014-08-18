package cn.zthz.tool.requirement;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.Test;

import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.requirement.RequirementOperations.QueryOrder;

public class RequirementOperationsTest {
	DataFactory dataFactory = new DataFactory();

	@Test
	public void testSave() throws UserRequirementException, InterruptedException {
		for (int i = 0; i < 100; i++) {
			RequirementOperations.instance.save(createRequirement());

		}

	}
	
//	@Test
//	public void testPublish() throws UserRequirementException{
//		Requirement requirement = createRequirement();
//		RequirementOperations.instance.publish(requirement);
//	}

	/**
	 * 39.977474,116.311975 中关村公馆
	 * 
	 * @return
	 */
	private Requirement createRequirement() {
		Requirement requirement = new Requirement();
		requirement.setAddress(dataFactory.getAddress());
		requirement.setCreateTime(new Timestamp(System.currentTimeMillis()));
		requirement.setTitle(dataFactory.getName());
		requirement.setDescription(dataFactory.getRandomText(1, 20));
		requirement.setExpire(new Timestamp(System.currentTimeMillis() + (long) Math.random() * 1000000));
		requirement.setId(HashUtils.uuid());
		requirement.setLatitude(39.977474 + (1 - 2 * Math.random()));
		requirement.setLongitude(116.311975 + (1 - 2 * Math.random()));
		requirement.setMandateFee(0f);
		requirement.setPrice(new BigDecimal(Math.random() * 1000));
		requirement.setStatus(0);
		requirement.setUserId("1");
		requirement.setViewCount(1);
		requirement.setHasMandate(true);
		requirement.setPublishAddress(requirement.getAddress());
		requirement.setPublishLatitude(requirement.getLatitude());
		requirement.setPublishLongitude(requirement.getLongitude());
//		requirement.setMainPictureId("1");
		return requirement;
	}
	
	public static void main(String[] args) {
		for (int i= 0 ; i< 100; i++) {
//			System.out.println(new DataFactory().getA);
		}
	}

	@Test
	public void testGet() throws UserRequirementException {
		System.out.println(LogUtils.format("requirement", RequirementOperations.instance.get("ff8080813c05b124013c05b125c500af")));
	}

	@Test
	public void testQueryNewest() throws UserRequirementException {
		Map<String, Object> orderArgs = null;
		Map<String, Object> conditionArgs = null;
		;
		List<Requirement> result = RequirementOperations.instance.query(conditionArgs, orderArgs, QueryOrder.newest, 0, 10);
		System.out.println(LogUtils.format("result", result));

	}

	@Test
	public void testQueryHotest() throws UserRequirementException {
		Map<String, Object> orderArgs = null;
		Map<String, Object> conditionArgs = null;
		;
		List<Requirement> result = RequirementOperations.instance.query(conditionArgs, orderArgs, QueryOrder.hotest, 0, 10);
		System.out.println(LogUtils.format("result", result));

	}

	@Test
	public void testQueryNearest() throws UserRequirementException {
		Map<String, Object> orderArgs = new HashMap<>(2);
		Map<String, Object> conditionArgs = new HashMap<>(2);
		orderArgs.put("longitude", 39.977474);
		orderArgs.put("latitude", 116.311975);
		conditionArgs.put("type", 0);
		conditionArgs.put("status", 0);
		List<Requirement> result = RequirementOperations.instance.query(conditionArgs, orderArgs, QueryOrder.nearest, 1, 10);
		System.out.println(LogUtils.format("result", result));

	}

	@Test
	public void testGetUserRequirements() throws UserRequirementException {
		Map<String, Object> args = new HashMap<String, Object>();
		List<Requirement> result = RequirementOperations.instance.getUserRequirements("111", 0, 0, 0, 10);
		System.out.println(LogUtils.format("result", result));

	}

	@Test
	public void testViewRequirement() throws UserRequirementException {
		
		Map<String, Object> r = RequirementOperations.instance.viewRequirement("402880173ef44b79013ef453d2a10004","402880173dca28fc013dca35d6c40008");
		System.out.println(LogUtils.format("r",r));
		// RequirementOperations.instance.view("ff8080813c05aec1013c05aec3f60001",
		// "402880173c1394dc013c1394dc680001");
		
	}
	
	@Test
	public void testShareWB() throws UserRequirementException {
		
		int r = RequirementOperations.instance.shareWB("402880173dce7ef6013dd89f42020077","402880ff3ea5d67a013ea5d67acf0000");
		System.out.println(LogUtils.format("r",r));
		// RequirementOperations.instance.view("ff8080813c05aec1013c05aec3f60001",
		// "402880173c1394dc013c1394dc680001");
		
	}
	
	@Test
	public void testShareWBQQ() throws UserRequirementException {
		
		RequirementOperations.instance.shareWBQQ("402880173df1f04c013df29942330019","402880ff3ea6f716013ea70caa7a0003", new Integer(2), new Integer(1));
		// RequirementOperations.instance.view("ff8080813c05aec1013c05aec3f60001",
		// "402880173c1394dc013c1394dc680001");
		
	}
	@Test
	public void testVisit() throws UserRequirementException {

		RequirementOperations.instance.view("402880173df1f04c013df29942330019", "1");
		// RequirementOperations.instance.view("ff8080813c05aec1013c05aec3f60001",
		// "402880173c1394dc013c1394dc680001");

	}

	@Test
	public void testGetVisitors() throws UserRequirementException {

//		String rId = "ff8080813c05aec1013c05aec3f60001";
//		String userId = "402880173c1394dc013c13954e130005";
//		List<RequirementVisitor> result = RequirementOperations.instance.getVisitors("402880173d33c235013d33f217020010", null, 0, 10);
//		System.out.println(LogUtils.format("result", result));
	}

	@Test
	public void testGetUserIssuedRequirement() throws UserRequirementException {

		String rId = "ff8080813c05aec1013c05aec3f60001";
		String userId = "111";
		List<Map<String, Object>> result = RequirementOperations.instance.getUserIssuedRequirement(userId, 1, 0, 0, 10);
		System.out.println(LogUtils.format("result", result));
	}
	@Test
	public void testClose() throws UserRequirementException {
		RequirementOperations.instance.close("402880173f07abcf013f08a0f039002c" , "402880173fc6616e013fc70e08900007");
//		System.out.println(LogUtils.format("result", result));
	}
	
	@Test
	public void testScoreComplete() throws UserRequirementException {
		RequirementOperations.instance.scoreComplete("1", "ff8080813c05aec1013c05aec3f60001", 1 , "good");
//		System.out.println(LogUtils.format("result", result));
	}
	
	@Test
	public void testScoreSponsor() throws UserRequirementException {
		RequirementOperations.instance.scoreSponsor("111", "ff8080813c05aec1013c05aec3f60001", 1 , "bad");
//		System.out.println(LogUtils.format("result", result));
	}
	
	@Test
	public void testComplete() throws UserRequirementException{
		RequirementOperations.instance.complete("402880173f07abcf013f08a0f039002c", "402880173f07abcf013f091c3600003f", "aaaaaa");
	}
	
	@Test
	public void testPublish() throws UserRequirementException{
		Requirement requirement = createRequirement();
		RequirementOperations.instance.publish(requirement);
	}
	@Test
	public void testPublish2() throws UserRequirementException{
		String url = "http://localhost:8080/userRequirement/publish.json";
		File sound_file = new File("E:\\download\\国歌.mp3");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", "402880173f07abcf013f08a0f97f002d");
		params.put("userToken", "e0ddde3a4c40439e8160f03f310e94e9");
		params.put("title", "test");
		params.put("price", "100");
		params.put("expire", "2014-07-12 00:00:00");
		params.put("type","1");
		params.put("sound", sound_file);
		params.put("hasMandate", "0");
		params.put("hasPrivateMessageContact", "1");
		params.put("hasPhoneContact", "1");
		params.put("longitude","105.1");
		params.put("latitude","36.02");
		params.put("publishLongitude","112.11");
		params.put("publishLatitude", "36.122");
		params.put("completeScore",2);
		params.put("sponsorScore", 2);
		params.put("soundTime", "1");
		
		try {
			HttpUtils.doMultipart(url,params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testQueryNewest2() throws UserRequirementException {
		Map<String, Object> orderArgs = null;
		Map<String, Object> conditionArgs = null;
		List<Map<String, Object>> result = RequirementOperations.instance.query2(conditionArgs, orderArgs, QueryOrder.newest, 0, 10);
		System.out.println(LogUtils.format("result", result));
	}

	@Test
	public void testQueryHotest2() throws UserRequirementException {
		Map<String, Object> orderArgs = null;
		Map<String, Object> conditionArgs = null;
		List<Map<String, Object>> result = RequirementOperations.instance.query2(conditionArgs, orderArgs, QueryOrder.hotest, 0, 10);
		System.out.println(LogUtils.format("result", result));

	}

	@Test
	public void testQueryNearest2() throws UserRequirementException {
		Map<String, Object> orderArgs = new HashMap<>(2);
		Map<String, Object> conditionArgs = new HashMap<>(2);
		orderArgs.put("longitude", 30);
		orderArgs.put("latitude", 20);
		conditionArgs.put("type", 1);
		conditionArgs.put("status", 0);
		List<Map<String, Object>> result = RequirementOperations.instance.query2(conditionArgs, orderArgs, QueryOrder.nearest, 1, 10);
		System.out.println(LogUtils.format("result", result));

	}
	
	@Test
	public void testGetUserRequirement() throws UserRequirementException, SQLException{
		List<Map<String, Object>> results = RequirementOperations.instance.getUserRequirements("1", 0);
		System.out.println(LogUtils.format("result", results));
	}
	
	@Test
	public void testQueryRequirement() throws UserRequirementException{
		Map<String, Object> conditionArgs = new HashMap();
		conditionArgs.put("type", 1);
		Map<String, Object> orderArgs = new HashMap();
		List<Map<String, Object>> results = RequirementOperations.instance.query2(conditionArgs, orderArgs, QueryOrder.credit, 0, 10);
		System.out.println(results);
	}
	
	

}
