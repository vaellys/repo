package cn.zthz.tool.share;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.HzException;

import com.alibaba.fastjson.JSONObject;

public class QqShare implements Share {
	public static final String QQ_WEIBO_SHARE = "https://graph.qq.com/t/add_t";
	public static final String QQ_WEIBO_SHARE_MULTI = "https://graph.qq.com/t/add_pic_t";
	public static final String QQ_OPEN_ID = "https://graph.qq.com/oauth2.0/me";
	public static final String QQ_OPEN_WEIBO_SHARE_MULTI = "https://open.t.qq.com/api/t/add_pic";
	public static final String QQ_ZONE = "https://graph.qq.com/share/add_share";
	public static final QqShare instance = new QqShare();
	public static final Log log = LogFactory.getLog(QqShare.class);

	private static String getOpenId(String token) {
		Map<String, Object> params = new HashMap<>();
		params.put("access_token", token);
		try {
			String response = HttpUtils.doPosts(QQ_OPEN_ID, params);
			String interceptString = response.substring(response.indexOf("{"), response.lastIndexOf(")"));
			String openid = JSONObject.parseObject(interceptString).getString("openid");
			return openid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String openid = getOpenId("C4AF16D2B69F7CBB89EC16716D680B0A");
		System.out.println(openid);
	}

	@Override
	public void share(String token, Map<String , Object> opt) throws HzException {
		try {
			Map<String, Object> params = new TreeMap<>();
			params.put("access_token", token);
			params.put("oauth_consumer_key", GlobalConfig.get("oauth_consumer_key"));
			String openid = getOpenId(token);
			params.put("openid", openid);
			params.put("title", opt.get("title"));
			params.put("fromurl", GlobalConfig.get("share.fromurl"));
			params.put("site", GlobalConfig.get("share.site"));
			params.put("comment", opt.get("comment"));
			params.put("images", opt.get("images"));
			params.put("summary", opt.get("summary"));
			params.put("url", opt.get("url"));
			String response = HttpUtils.doPosts(QQ_ZONE, params, "UTF-8");
			if (log.isDebugEnabled()) {
				log.debug(response);
			}
		} catch (Exception e) {
			throw new HzException();
		}
	}
	/**
	 * 
	 * @param token
	 * @param body
	 * @param pic
	 * @throws HzException
	 */
	
	public void publicWeiBoWithPic(String token, String body, Object pic) throws HzException {
		try {
			Map<String, Object> params = new TreeMap<>();
			params.put("access_token", token);
			params.put("oauth_consumer_key", GlobalConfig.get("oauth_consumer_key"));
			String openid = getOpenId(token);
			params.put("openid", openid);
			params.put("clientip", "220.113.10.140");
			params.put("oauth_version","2.a");
			params.put("scope", "all");
			params.put("compatibleflag",0);
			params.put("format", "json");
	
			if (null != pic && !(pic instanceof String)) {
				params.put("pic", pic);
				String response = HttpUtils.doMultipart(QQ_ZONE, params);
				if(log.isDebugEnabled()){
					log.debug(response);
				}
			} else {
				String response = HttpUtils.doPosts(QQ_WEIBO_SHARE, params, "UTF-8");
				if (log.isDebugEnabled()) {
					log.debug(response);
				}
			}
		} catch (Exception e) {
			throw new HzException();
		}
	}
	
	public void share(String token, String body, Object pic, String title, String summary, String url) throws HzException {
		try {
			Map<String, Object> params = new TreeMap<>();
			params.put("access_token", token);
			params.put("oauth_consumer_key", GlobalConfig.get("oauth_consumer_key"));
			String openid = getOpenId(token);
			params.put("openid", openid);
			params.put("comment", body);
			params.put("fromurl", GlobalConfig.get("share.fromurl"));
			params.put("site", GlobalConfig.get("share.site"));
			params.put("url", url);
			//params.put("content", body);
			//params.put("images", pic);
			params.put("title", title);
			params.put("images", pic);
			params.put("summary", summary);
			String response = HttpUtils.doPosts(QQ_ZONE, params, "UTF-8");
			if (log.isDebugEnabled()) {
				log.debug(response);
			}
		} catch (Exception e) {
			throw new HzException();
		}
	}
}
