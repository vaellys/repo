package cn.zthz.tool.share;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.HzException;

public class WeiboShare implements Share {
	public static final String WEIBO_PUBLISH_MULTI = "https://upload.api.weibo.com/2/statuses/upload.json";
	public static final String WEIBO_PUBLISH_IL = "https://api.weibo.com/2/statuses/upload_url_text.json";

	public static final String WEIBO_PUBLISH= "https://api.weibo.com/2/statuses/update.json";
	public static final String WEIBO_TOKEN_INFO = "https://api.weibo.com/oauth2/get_token_info";
	public static final WeiboShare instance = new WeiboShare();
	
	public static String tokenInfo(String token) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("access_token", token);
		return HttpUtils.doPosts(WEIBO_TOKEN_INFO, params);

	}

	public static void main(String[] args) throws Exception {
		String token ="2.00vgJBxBmg4XTE30275e8ce1rrY7eE";
//		System.out.println(tokenInfo(token));
//		params.put("pic", new File("/home/uzoice/Pictures/4.jpg"));
		Map<String, Object> opt = new HashMap<>();
		opt.put("body", "hzhzhz");
		opt.put("pic",new File("/home/uzoice/Pictures/4.jpg"));

//<<<<<<< .mine
//		new WeiboShare().share(token , "haha1" , new File("C:\\Users\\samul\\Desktop\\1.jpg"));
//=======
		new WeiboShare().share(token ,opt );
	}

	@Override
	public void share(String token ,Map<String , Object> opt) throws HzException {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("access_token", token);
			params.put("url",opt.get("url"));
			Object pic = opt.get("pic");
			String result;
			if(null!=pic && !(pic instanceof String)){
				params.put("pic", pic);
				params.put("status", URLEncoder.encode((String)opt.get("body"),"utf-8"));
				result = HttpUtils.doMultipart(WEIBO_PUBLISH_MULTI, params);
			}else{
				params.put("status",(String)opt.get("body"));
				result = HttpUtils.doPosts(WEIBO_PUBLISH , params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
