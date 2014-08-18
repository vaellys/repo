package cn.zthz.tool.user;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;



public class UserOperationImplTest {
	
	@Test
	public void testCheckUserToken() throws UserException {
		UserOperationImpl.instance.checkUserToken("1", "1");
	}
	@Test
	public void testSave() throws UserException, ParseException{
		
		User user = new User();
		user.setAddress("北京");
		String date1 = "2012-12-12 15:28:30";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date2 = sdf.parse(date1);
		Date date3 = new java.sql.Timestamp(date2.getTime());
		java.sql.Date date4 = new java.sql.Date(date3.getTime());
		
		user.setBirthday(date4);
		String id = UserOperationImpl.instance.save(user);
		System.out.println(id);
		
	}
	
	@Test
	public void testUpdate() throws UserException, SQLException{
		java.sql.Connection conn = Connections.instance.get();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		Map<String, Object> newProperties = new HashMap<String, Object>();
		conditionMap.put("id", "402880093ca3dfea013ca3dfeae30000");
		newProperties.put("name", "zhangdaqian");
		newProperties.put("sex", 1);
		DbOperations.instance.update(conn, "User", conditionMap, newProperties);
	}
	
	@Test
	public void testGet() throws UserException, SQLException{
		java.sql.Connection conn = Connections.instance.get();
		User user = DbOperations.instance.get(conn, "402880093ca3dfea013ca3dfeae30000", User.class);
		System.out.println(user);
		
	}
	
	@Test
	public void testModifyUserPassword() throws UserException{
		UserOperationImpl.instance.modifyUserPassword("402880173cf01658013cf0215cf40001", "617907", "617907", "6179");
	}
	@Test
	public void testUpdateUserLocation() throws UserException{
		UserOperationImpl.instance.updateUserLocation("402880173dc9a4ea013dca04bc3e0007", 1.02154, 89.2145);
	}
	
	@Test
	public void testApplyAuth() throws HzException{
		AProfile aProfile = new AProfile();
		aProfile.setUserId("1");
		aProfile.setName("lisi");
		aProfile.setCompany("中天华智");
		aProfile.setAddress("海淀中街");
		aProfile.setIdcard("421087198910046554");
		aProfile.setReason("玩玩");
		UserOperationImpl.instance.applyAuth(aProfile);
	}
	
	@Test
	public void testApplyAuth2() throws HzException{
		AProfile aProfile = new AProfile();
		aProfile.setUserId("1");
		aProfile.setName("lisi");
		aProfile.setCompany(null);
		aProfile.setAddress("海淀中街");
		aProfile.setIdcard("421087198910046554");
		aProfile.setReason("玩玩");
		UserOperationImpl.instance.applyAuth(aProfile);
	}

}
