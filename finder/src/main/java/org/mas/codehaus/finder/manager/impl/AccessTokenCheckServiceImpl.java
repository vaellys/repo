package org.mas.codehaus.finder.manager.impl;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.mas.codehaus.finder.common.ErrorCodes;
import org.mas.codehaus.finder.common.LogUtils;
import org.mas.codehaus.finder.manager.AccessTokenCheckService;
import org.mas.codehaus.finder.manager.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

@Service("AccessTokenCheckService")
@Transactional
public class AccessTokenCheckServiceImpl implements AccessTokenCheckService {
	private static Logger log = LoggerFactory
			.getLogger(AccessTokenCheckServiceImpl.class);
	private static final String WEI_BO_URL = "https://api.weibo.com/oauth2/get_token_info";
	private static final String QQ_URL = "https://graph.qq.com/oauth2.0/me";

	@Override
	public void checkWeiboAccessToken(String weiboUid, String accessToken)
			throws UserException {
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(WEI_BO_URL);
		method.setQueryString("access_token=" + accessToken);
		try {
			client.executeMethod(method);
			String result = method.getResponseBodyAsString();
			if (log.isDebugEnabled()) {
				log.debug(LogUtils.format("r", result));
			}
			JSONObject jsonResult = JSONObject.parseObject(result);
			Long uid = jsonResult.getLong("uid");
			if (null == uid || !uid.equals(Long.valueOf(weiboUid))) {
				throw new UserException(ErrorCodes.TOKEN_EXPIRED,
						"accessToken is invalid!");
			}
		} catch (IOException e) {
			log.error("check accessToken failed!", e);
			throw new UserException("check accessToken failed!", e);
		}
	}

	@Override
	public void checkQqAccessToken(String qqUid, String accessToken)
			throws UserException {
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(QQ_URL);
		method.setQueryString("access_token=" + accessToken);
		try {
			client.executeMethod(method);
			String result = method.getResponseBodyAsString();
			if (log.isDebugEnabled()) {
				log.debug(LogUtils.format("r", result));
			}
			String str = result.substring(10, result.length() - 3);
			JSONObject jsonResult = JSONObject.parseObject(str);
			String uid = jsonResult.getString("openid");
			if (null == uid || !uid.equals(qqUid)) {
				throw new UserException(ErrorCodes.TOKEN_EXPIRED,
						"accessToken is invalid!");
			}
		} catch (IOException e) {
			log.error("check accessToken failed!", e);
			throw new UserException("user is invalid!", e);
		}
	}

}
