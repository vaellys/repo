package cn.zthz.tool.user;

import org.junit.Test;

import cn.zthz.tool.common.HzException;

public class ThirdAccessTokenCheckImplTest {

	@Test
	public void testCheckWeiboAccessToken1() {
		ThirdAccessTokenCheck thirdAccessTokenCheck = new ThirdAccessTokenCheckImpl();
		try {
			thirdAccessTokenCheck.checkWeiboAccessToken("3226203843", "2.00rzo1WDRYT4DB9cd5086de00lHtEv");
			System.out.println("test success");
		} catch (HzException e) {
			e.printStackTrace();
		}
	}
	
	@Test(expected=ThirdAccessException.class)
	public void testCheckWeiboAccessToken2() {
		ThirdAccessTokenCheck thirdAccessTokenCheck = new ThirdAccessTokenCheckImpl();
		try {
			thirdAccessTokenCheck.checkWeiboAccessToken("11", "2313");
			System.out.println("test success");
		} catch (HzException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCheckQqAccessToken1() {
		ThirdAccessTokenCheck thirdAccessTokenCheck = new ThirdAccessTokenCheckImpl();
		try {
			thirdAccessTokenCheck.checkQqAccessToken("3C2C7441F12B00FA26B9941399860906", "E8A64730669F4A781C4B464372647B0E");
		} catch (HzException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCheckQqAccessToken2() {
		ThirdAccessTokenCheck thirdAccessTokenCheck = new ThirdAccessTokenCheckImpl();
		try {
			thirdAccessTokenCheck.checkQqAccessToken("1233", "12334");
		} catch (HzException e) {
			e.printStackTrace();
		}
	}
}
