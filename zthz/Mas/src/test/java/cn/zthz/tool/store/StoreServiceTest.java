package cn.zthz.tool.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;

public class StoreServiceTest {
	@BeforeClass
	public static void init(){
		System.out.println("init");
	}
	@AfterClass
	public static void destroy(){
		System.out.println("destroy");
	}
	@Before
	public void setUp(){
		
	}
	@After
	public void tearDown(){
		
	}

	@Test
	public void testAdd() throws HzException {
		StoreService.instance.add("402880ff3e4f6af9013e4f6afa880001", "402880ff3e7d91c8013e7dc59c800003");
	}
	@Ignore
	@Test
	public void testDelete() throws HzException {
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		StoreService.instance.delete(list, "402880173e3f056e013e44f9bb390033");
	}

	@Test
	public void testQueryStore() throws HzException {
		List<Map<String, Object>> results = StoreService.instance.query("402880173dce7ef6013de27726070127", 0, 10);
		System.out.println(LogUtils.format("k", results));
	}
	
}
