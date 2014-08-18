package cn.zthz.tool.push;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.jpush.api.StringUtils;
import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.LogUtils;

import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class AndroidJPush extends AbstractPush {
	private static final long timeToLive = 86400l;//默认为保存1天的离线消息（86400秒）
	private static final String PUSH_URL = "http://api.jpush.cn:8800/sendmsg/sendmsg";
	private static final String BAIDU_PUSH_URL = "http://channel.api.duapp.com/rest/2.0/channel/channel";
	private static final String API = "http://sdk.open.api.igexin.com/apiex.htm"; // OpenService接口地址
	private static int sendno = 0;
	private static final Log log = LogFactory.getLog(AndroidJPush.class);
	
	public void androidJpush(String alert, Map<String, Object> attach, String deviceToken) throws PushException {
		//JSONObject jsonObject = new JSONObject(attach);
//		System.out.println(jsonObject);
		Map<String, Object> message = new HashMap<>();
		message.put("message", "");
		message.put("title", alert);
		message.put("extras",attach);
//		String msg = "{\"n_title\":\"hello\",\"n_content\":\"" + alert + "\",\"n_extras\":" + jsonObject + "}";
//		System.out.println(msg);
		BasicNameValuePair name = new BasicNameValuePair("username", "zthz2012");  //用户名   
        BasicNameValuePair sendno = new BasicNameValuePair("sendno", String.valueOf((++AndroidJPush.sendno)));  // 发送编号。由开发者自己维护，标识一次发送请求   
        BasicNameValuePair appkeys = new BasicNameValuePair("appkeys", "d6b22bbe5bd64f51ba4c9ba7");  // 待发送的应用程序(appKey)，只能填一个。   
        BasicNameValuePair receiver_type = new BasicNameValuePair("receiver_type", "3");  
        BasicNameValuePair receiver_value = new BasicNameValuePair("receiver_value", deviceToken);
        //验证串，用于校验发送的合法性。   
        BasicNameValuePair verification_code = new BasicNameValuePair("verification_code", getVerificationCode(deviceToken));  
        //发送消息的类型：1 通知 2 自定义   
        BasicNameValuePair msg_type = new BasicNameValuePair("msg_type", "2");  
        BasicNameValuePair msg_content = new BasicNameValuePair("msg_content", JsonUtils.toJsonString(message)); 
        if(log.isInfoEnabled()){
        	log.info(JsonUtils.toJsonString(message));
        }
        //目标用户终端手机的平台类型，如： android, ios 多个请使用逗号分隔。   
        BasicNameValuePair platform = new BasicNameValuePair("platform", "android");
        BasicNameValuePair time_to_live = new BasicNameValuePair("time_to_live", String.valueOf(AndroidJPush.timeToLive));
        List<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();  
        datas.add(name);  
        datas.add(sendno);  
        datas.add(appkeys);  
        datas.add(receiver_type);  
        datas.add(receiver_value);
        datas.add(verification_code);
        datas.add(msg_type);  
        datas.add(msg_content);  
        datas.add(platform);  
        datas.add(time_to_live);
        try {  
            HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");  
            HttpPost post = new HttpPost(PUSH_URL);  
            post.setEntity(entity);  
            HttpClient client = ClientUtil.getNewHttpClient();  
            HttpResponse reponse = client.execute(post);  
            HttpEntity resEntity = reponse.getEntity();  
            String response = EntityUtils.toString(resEntity); 
            JSONObject result = JSONObject.parseObject(response);
            if(0 != result.getInteger("errcode").intValue()){
            	throw new PushException(response);
            }
            
        } catch (Exception ex) {  
        	log.error(LogUtils.format("alert" , alert , "attach" , attach , "deviceToken" , deviceToken) , ex);
            throw new PushException("send message error" , ex);
        } 
	}
//	@Override
//	public void push(String alert, Map<String, Object> attach, String deviceToken) throws PushException {
//		Map<String, Object> message = new HashMap<>();
//		message.put("title", alert);
//		message.put("extras",attach);
//		TreeMap<String, String> args = new TreeMap<>();
//		String messageToJsonString = null;
//		try {
//			messageToJsonString = URLEncoder.encode(JsonUtils.toJsonString(message),"utf-8");
//			String timeStamp = String.valueOf((System.currentTimeMillis() + 10 * 60 * 1000));
//			String mid = HashUtils.uuid();
//			args.put("apikey",GlobalConfig.get("apikey.baidu"));
//			args.put("messages",messageToJsonString);
//			args.put("method", "push_msg");
//			args.put("msg_keys", mid);
//			args.put("push_type", "1");
//			args.put("user_id", deviceToken);
//			args.put("v", "1");
//			args.put("timestamp", timeStamp);
//			args.put("secret_key", GlobalConfig.get("secretKey.baidu"));
//			args.put("message_type", "1");
//			String sortedArgs = CollectionUtils.joinMap(args, "=", "&");
//			String replacedArgs = sortedArgs.replace("&", "");
//			String signParams = "POST"+BAIDU_PUSH_URL+replacedArgs+GlobalConfig.get("secretKey.baidu");
//			String sign;
//			sign = HashUtils.md5(URLEncoder.encode(signParams, "UTF-8"));
//			//String requestParams = BAIDU_PUSH_URL+"?"+sortedArgs+"&sign="+sign;
////			Map<String, Object> ps = new HashMap<>();
////			ps.put("messages" ,messageToJsonString );
//			args.put("sign" , sign);
//			String result = HttpUtils.doPosts(BAIDU_PUSH_URL , args );
//			if( null != JSONObject.parseObject(result).getString("error_code")){
//				log.error(result);
//				throw new PushException(LogUtils.format("alert" , alert , "attach" , attach , "deviceToken" , deviceToken));
//			}
//			if(0 >= JSONObject.parseObject(result).getJSONObject("response_params").getIntValue("success_amount")){
//				log.error(result);
//				throw new PushException("no target! deviceToken:" + deviceToken + "'");
//			}
//			if(log.isInfoEnabled()){
//				log.info(result);
//			}
//		} catch (Exception ex) {  
//        	log.error(LogUtils.format("alert" , alert , "attach" , attach , "deviceToken" , deviceToken) , ex);
//            throw new PushException("send message error" , ex);
//        } 
		
//        List<BasicNameValuePair> datas = createdRequestParams(args); 
//        datas.add(new BasicNameValuePair("sign", signedParam));
//        try {  
//            HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");  
//            HttpPost post = new HttpPost(BAIDU_PUSH_URL);  
//            post.setEntity(entity);  
//            HttpClient client = ClientUtil.getNewHttpClient();  
//            HttpResponse reponse = client.execute(post);  
//            HttpEntity resEntity = reponse.getEntity();  
//            String response = EntityUtils.toString(resEntity); 
//            log.info(response);
//        } catch (Exception ex) {  
//        	log.error(LogUtils.format("alert" , alert , "attach" , attach , "deviceToken" , deviceToken) , ex);
//            throw new PushException("send message error" , ex);
//        } 
//	}
	
	private static String getVerificationCode(String deviceToken) {  
        String username = "zthz2012";  //username 是开发者Portal帐户的登录帐户名   
        String password = "zthz617907";  
        int sendno = AndroidJPush.sendno;  
        int receiverType = 3;  
        String receiverValue = deviceToken;
        String md5Password = StringUtils.toMD5(password); //password 是开发者Portal帐户的登录密码   
        String input = username + sendno + receiverType + receiverValue + md5Password;  
        String verificationCode = StringUtils.toMD5(input);  
        return verificationCode;  
    }
	/**
	 * 生成签名
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String generatedSign(String params) throws UnsupportedEncodingException {  
		//拼接签名参数
		StringBuilder signParams = new StringBuilder();
		signParams.append("POST");
		signParams.append(BAIDU_PUSH_URL);
		signParams.append(params);
//		signParams.append()
		if(log.isInfoEnabled()){
			log.info(signParams.toString());
		}
		String signedParam = HashUtils.md5(URLEncoder.encode(signParams.toString(),"UTF-8"));
		return signedParam;
	}
	/**
	 * 生成请求参数
	 * @param params
	 * @return
	 */
	private static List<BasicNameValuePair> createdRequestParams(TreeMap<String ,String> params){
		List<BasicNameValuePair> datas = new LinkedList<>();
		for(Map.Entry<String, String> entry : params.entrySet()){
			datas.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return datas;
	}
	@Override
	public  void push(String alert, Map<String, Object> attach, String deviceToken) throws PushException{
		// 推送主类
        IIGtPush push = new IGtPush(API, GlobalConfig.get("appkey.gt.push"), GlobalConfig.get("mastersecret.gt.push"));
        try {
        	//单推消息类型 
			SingleMessage message = new SingleMessage();
			//通知模版：支持TransmissionTemplate
			TransmissionTemplate template = new TransmissionTemplate();
			template.setAppId(GlobalConfig.get("appid.gt.push"));
			template.setAppkey(GlobalConfig.get("appkey.gt.push"));
			Map<String, Object> missionContent = new HashMap<>();
			missionContent.put("alert", alert);
			missionContent.put("attach", attach);
			template.setTransmissionContent(JsonUtils.toJsonString(missionContent));
			//收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
			template.setTransmissionType(2);					
			message.setData(template);
			message.setOffline(true);					//用户当前不在线时，是否离线存储,可选
//			message.setOfflineExpireTime(72 * 3600 * 1000);	//离线有效时间，单位为毫秒，可选
			Target target1 = new Target();
			target1.setAppId(GlobalConfig.get("appid.gt.push"));
			target1.setClientId(deviceToken);
			//单推
			IPushResult ret = push.pushMessageToSingle(message, target1);
			Map<String, Object> responseContent = (Map<String, Object>)ret.getResponse();
			String result = (String)responseContent.get("result");
			if(!result.equals("ok")){
				log.error(responseContent.toString());
				throw new PushException("push message error! deviceToken:'" + deviceToken + "'");
			}
			if(log.isErrorEnabled()){
				log.info(responseContent.toString());
			}
        } catch (Exception e) {
	    	log.error("push message error! deviceToken:'" + deviceToken + "'");
	    	throw new PushException("send message error!" , e);
	    }
	}
}
	
