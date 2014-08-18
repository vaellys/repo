package cn.zthz.tool.user;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.share.QqShare;

public class QqShareTest {

	@Test
	public void testShare() throws HzException {
		QqShare.instance.share("FD1C478F059E762E7796E29CDC9BB9A9", "hello", null,"","","");
	}
	@Test
	public void testShare2() throws HzException {
		Map<String, Object> map = new HashMap<>();
		map.put("title", "123");
		map.put("summary", "12234556");
		map.put("comment", "1235465");
		QqShare.instance.share("CD355F2293471260BE586E9C1E7D04D5", map);
		
	}
	
	@Test
	public void testPublicWeiBoWithPic() throws HzException {
		QqShare.instance.publicWeiBoWithPic("FD1C478F059E762E7796E29CDC9BB9A9", "hello", new File("C:\\Users\\samul\\Desktop\\log.txt"));
		
	}
	
	@Test
	public void testShareZone() throws HzException {
		//QqShare.instance.shareZone("CD355F2293471260BE586E9C1E7D04D5", "寻找简客网络", "http://220.113.10.142/images/ea/58/ea58f39a3c6110cff6eb22f62ad6d371.jpg");
		
	}
	
	
	
	
}
