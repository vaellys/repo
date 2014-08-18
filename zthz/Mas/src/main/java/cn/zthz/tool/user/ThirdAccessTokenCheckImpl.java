package cn.zthz.tool.user;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.rest.ErrorCodes;
import cn.zthz.tool.common.HzException;

import com.alibaba.fastjson.JSONObject;


public class ThirdAccessTokenCheckImpl implements ThirdAccessTokenCheck {
	private static final Log log = LogFactory.getLog(ThirdAccessTokenCheckImpl.class);
	
	public static final ThirdAccessTokenCheckImpl instance = new ThirdAccessTokenCheckImpl();
	@Override
	public void checkWeiboAccessToken(String weiboUid, String accessToken)
			throws HzException {
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(
				"https://api.weibo.com/oauth2/get_token_info");
		method.setQueryString("access_token=" + accessToken);
		try {
			client.executeMethod(method);
			String result = method.getResponseBodyAsString();
			JSONObject jsonResult = JSONObject.parseObject(result);
			Long uid = jsonResult.getLong("uid");
			
			if (null == uid || !uid.equals(Long.valueOf(weiboUid))) {
				throw new HzException("user is invalid!");
			}
		} 
		catch (HzException e) {
			log.error("user is invalid", e);
			throw new HzException(ErrorCodes.TOKEN_EXPIRED, "user is invalid!", e);
		}catch (IOException e) {
			log.error("user is invalid", e);
			throw new HzException("user is invalid" , e);
		}

	}

	@Override
	public void checkQqAccessToken(String qqUid, String accessToken)
			throws HzException {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(
				"https://graph.qq.com/oauth2.0/me");
		method.setQueryString("access_token=" + accessToken);
		try {
			client.executeMethod(method);
			String result = method.getResponseBodyAsString();
			String str = result.substring(10, result.length() - 3);
			JSONObject jsonResult = JSONObject.parseObject(str);
			String uid = jsonResult.getString("openid");
			if (null == uid || !uid.equals(qqUid)) {
				throw new HzException("user is invalid!");
			}
		} catch (HzException e) {
			log.error("user is invalid", e);
			throw new HzException(ErrorCodes.TOKEN_EXPIRED, "user is invalid!", e);
		} catch (IOException e) {
			log.error("user is invalid", e);
			throw new HzException("user is invalid!", e);
		}
	}
}
