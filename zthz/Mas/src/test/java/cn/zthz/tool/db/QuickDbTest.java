package cn.zthz.tool.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.LogUtils;

public class QuickDbTest {
	
	@Test
	public void testGets() throws SQLException{
		List<Map<String, Object>> r = QuickDB.gets("select * from UserDevice");
		System.out.println(LogUtils.format("r" , r));
	}
	@Test
	public void testGetSingle() throws SQLException{
		Object r = QuickDB.getSingle("select price from Requirement where id='ff8080813c05aec1013c05aec3f60001'");
		System.out.println(r.getClass());
		System.out.println(LogUtils.format("r" , r));
	}

}
