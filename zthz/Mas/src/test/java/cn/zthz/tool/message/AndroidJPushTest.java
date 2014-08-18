package cn.zthz.tool.message;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.push.AndroidJPush;
import cn.zthz.tool.push.Push;
import cn.zthz.tool.push.PushException;

public class AndroidJPushTest {

//	@Test
	public void testPush() throws PushException {
		Push push = new AndroidJPush();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("receiverId", "402880173c1394dc013c13b37c050009");
		map.put("sendTime", "2013-01-28 16:56:03");
		map.put("senderId", "402880173c1394dc013c13b37c050009");
		map.put("t", 1);
		push.push("这是我的第一个JPush测试！",map, "825B33B9CF5527B33BF00633263F79CE");
	}
	
//	@Test
	public void testPush2() throws PushException {
		Push push = new AndroidJPush();
		Map<String, Object> map = new HashMap<String, Object>();
		push.push("123321321",map, "fsfsa");
	}
	
	public static String joinMap(Map<String ,String> map , String jk ,String ji){
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> item : map.entrySet()) {
			builder.append(item.getKey());
			builder.append(jk);
			builder.append(item.getValue());
			builder.append(ji);
		}
		builder.delete(builder.length()-ji.length() , builder.length());
		
		return builder.toString();
	}
	@Test
	public void testBaiduPush() throws Exception{
//		AndroidJPush push = new AndroidJPush();
//		push.baiduPush("你好", null, "2871710468");
		String mid = HashUtils.uuid();
		TreeMap<String , String> args = new TreeMap<>();
		args.put("apikey", "CODtzVHNK5ENVaAFALcY4hQs");
		args.put("messages", "mm");
		args.put("method", "push_msg");
		args.put("message_type", "0");
		args.put("msg_keys", mid);
		args.put("push_type", "1");
		args.put("user_id", "2871710468");
		args.put("v", "1");
		args.put("timestamp", ""+System.currentTimeMillis());
		
		String q1 = joinMap(args , "=" , "&");
		System.out.println(q1);
		
		String u = "http://channel.api.duapp.com/rest/2.0/channel/channel";
		
		
//		String q1 = "apikey=CODtzVHNK5ENVaAFALcY4hQs&messages=%22title%22&method=push_msg&message_type=0&msg_keys=1&push_type=1&user_id=2871710468&v=1&timestamp=1365585514342";
		String s = q1.replaceAll("&" ,"");
		
		String signParams = "POST"+u+s+"DqwIeBG9vhuCzGQ0M2wOLAKhREjCuZZG";
		System.out.println(signParams);
		String sign = HashUtils.md5(URLEncoder.encode(signParams, "UTF-8"));
		String f = u+"?"+q1+"&sign="+sign;
		
		System.out.println(f);
		System.out.println(HttpUtils.doPosts(f , Collections.EMPTY_MAP));
		
				
	}
	
	@Test
	public void testBaiduAPush() throws Exception{
		AndroidJPush push = new AndroidJPush();
		Map<String, Object> att = new HashMap();
		att.put("t" ,1);
		push.push("你好KB", att , "2871710468");
	}
	
	@Test
	public void testBaiduAPush2() throws Exception{
		AndroidJPush push = new AndroidJPush();
		Map<String, Object> att = new HashMap();
		att.put("t" ,1);
		push.push("你好", att , "");
	}
	
	@Test
	public void testBaiduAPush3() throws Exception{
		AndroidJPush push = new AndroidJPush();
		Map<String, Object> att = new HashMap();
		att.put("t" ,1);
		push.push("你好", att , "1");
	}
	
	
	@Test
	public void testPush3() throws Exception{
		AndroidJPush push = new AndroidJPush();
		Map<String, Object> att = new HashMap<>();
		att.put("t" ,1);
		att.put("c" ,2);
		push.push("你好ppp", att , "7f058718f0a9a33f9f256b91b911fe80");
	}
	
	@Test
	public void testPush4() throws Exception{
		AndroidJPush push = new AndroidJPush();
		Map<String, Object> att = new HashMap<>();
		att.put("t" ,1);
		att.put("c" ,2);
		push.push("你好ppp", att , "213");
	}
}
