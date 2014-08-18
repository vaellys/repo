package cn.zthz.androidpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.push.ClientUtil;
import cn.zthz.tool.push.PushException;





public class Androidpn {
	private static final Log log = LogFactory.getLog(Androidpn.class);
//	public static void androidPush(String userName){
//		HttpClient httpClient = new HttpClient();
//		HttpMethod method = new PostMethod("http://192.168.0.152:7070/notification.do");
//		method.setQueryString("action=send&broadcast=N&username='"+userName+"'&title=&message=uri=");
//		try{
//			httpClient.executeMethod(method);
//		}catch(Exception e){
//			log.error("");
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) {
//		Androidpn.androidPush("f194c41fe8ff49198a183f5d50d3a4c1");
//	}
	
	public static void push(String alert, Map<String, Object> attach, String deviceToken) throws PushException {
		BasicNameValuePair action = new BasicNameValuePair("action", "send"); 
		BasicNameValuePair broadcast = new BasicNameValuePair("broadcast", "N"); 
		BasicNameValuePair name = new BasicNameValuePair("username", deviceToken); 
		BasicNameValuePair title = new BasicNameValuePair("title", alert); 
		Map<String, Object> map = new HashMap<>();
		map.put("attach", attach);
		BasicNameValuePair message = new BasicNameValuePair("message", ""); 
		BasicNameValuePair uri = new BasicNameValuePair("uri", JsonUtils.toJsonString(map)); 
		
		//BasicNameValuePair messageType = new BasicNameValuePair("attach", alert); 
		
		List<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();  
		datas.add(action);
		datas.add(broadcast);
		datas.add(name);
		//datas.add(messageType);
		datas.add(title);
		datas.add(message);
		datas.add(uri);
		try {  
            HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");  
            HttpPost post = new HttpPost("http://192.168.0.152:7070/notification.do");  
            post.setEntity(entity);  
            HttpClient client = ClientUtil.getNewHttpClient();  
            HttpResponse reponse = client.execute(post);  
            log.info(reponse.toString());
        } catch (Exception ex) {  
        	log.error(LogUtils.format("alert" , alert , "attach" , attach , "deviceToken" , deviceToken) , ex);
            throw new PushException("send message error" , ex);
        } 
	}
	public static void main(String[] args) throws PushException {
		Androidpn.push("我的",null, "f194c41fe8ff49198a183f5d50d3a4c1");
	}
}
