package cn.zthz.common;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.entity.mime.content.StringBody;

public class HttpClientTest {
	public void doMultiPart(Map<String, Object> params, String url){
		HttpClient httpClient = new HttpClient();
		HttpMethod method = new PostMethod();
		if(null == params){
			return;
		}
		for(Map.Entry<String, Object> entry : params.entrySet()){
			if((String)entry.getValue() instanceof String){
			}
		}
	}
}
