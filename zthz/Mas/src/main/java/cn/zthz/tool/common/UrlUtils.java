package cn.zthz.tool.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class UrlUtils {

	public static Pattern NOT_ASCIIS = Pattern.compile("[^\u0000-\u00ff]+");

	/**
	 * 仅转译url中非ascii部分 <br/>
	 * .eg http://www.baidu.com/s?wd=你好 ->
	 * http://www.baidu.com/s?wd=%C4%E3%BA%C3
	 * 
	 * @param url
	 * @param urlCharset
	 * @return
	 */
	public static String foramtUrl(String url, String urlCharset) {
		Matcher matcher = NOT_ASCIIS.matcher(url);
		String result = url;
		try {
			while (matcher.find()) {
				result = matcher.replaceAll(URLEncoder.encode(matcher.group(), urlCharset));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static Map<String, String> getParamMap(String url) {
		Map<String, String> map = new HashMap<>();
		String paramString = parseUrl(url).query;
		if (null != paramString) {
			for (String kvString : paramString.split("&")) {
				if (-1 == kvString.indexOf("=")) {
					continue;
				}
				String[] kv = kvString.split("=");
				if (kv.length > 1) {
					map.put(kv[0], kv[1]);
				} else {
					map.put(kv[0], null);
				}
			}
		}
		return map;
	}

	/**
	 * 
	 * @param url
	 *            http://www.qileke.com:80/1000/hello.html;id=1?name=jim&age=20#
	 *            middle
	 * @return
	 */
	public static UrlParts parseUrl(String url) {
		UrlParts urlParts = new UrlParts();

		String deUrl = url;
		// 分离FRAGMENT #.*
		int fragmentIndex = deUrl.indexOf("#");
		if (-1 != fragmentIndex) {
			urlParts.fragment = deUrl.substring(fragmentIndex + 1);
			deUrl = deUrl.substring(0, fragmentIndex);
		}
		// 分离查询 ?k=v
		int queryIndex = deUrl.indexOf("?");
		if (-1 != queryIndex) {
			urlParts.query = deUrl.substring(queryIndex + 1);
			deUrl = deUrl.substring(0, queryIndex);
		}
		// 分离参数 ;k=v
		int parameterIndex = deUrl.indexOf(";");
		if (-1 != parameterIndex) {
			urlParts.parameters = deUrl.substring(parameterIndex + 1);
			deUrl = deUrl.substring(0, parameterIndex);
		}
		// 分离协议 .*://
		int protocolIndex = deUrl.indexOf("://");
		if (-1 != protocolIndex) {
			urlParts.protocol = deUrl.substring(0, protocolIndex);
			deUrl = deUrl.substring(protocolIndex + 3);
		}

		// 分离路径 /.*
		int pathIndex = deUrl.indexOf("/");
		if (-1 != pathIndex) {
			urlParts.path = deUrl.substring(pathIndex);
			deUrl = deUrl.substring(0, pathIndex);
		}

		// 分离端口 :\d+
		int portIndex = deUrl.indexOf(":");
		if (-1 != portIndex) {
			urlParts.port = Integer.valueOf(deUrl.substring(portIndex + 1));
			deUrl = deUrl.substring(0, portIndex);
		}

		// 最后剩下host
		if (!deUrl.isEmpty()) {
			urlParts.hostname = deUrl;
		}

		return urlParts;

	}

	public static Tuple<String, String> parseDomains(String host) {
		String[] splittedUrl = host.split("\\.");
		if (2 >= splittedUrl.length) {
			return new Tuple<>(host, null);
		}
		if (3 == splittedUrl.length) {
			return new Tuple<>(splittedUrl[1] + "." + splittedUrl[2], splittedUrl[0]);

		}
		if (4 <= splittedUrl.length) {
			StringBuilder firstDomain = new StringBuilder();
			StringBuilder secondDomain = new StringBuilder();
			if (2 == splittedUrl[splittedUrl.length - 1].length()) {
				for (int i = 0; i < splittedUrl.length - 3; i++) {
					secondDomain.append(splittedUrl[i]);
					secondDomain.append(".");
				}
				secondDomain.deleteCharAt(secondDomain.length() - 1);
				for (int i = splittedUrl.length - 3; i < splittedUrl.length; i++) {
					firstDomain.append(splittedUrl[i]);
					firstDomain.append(".");
				}
				firstDomain.deleteCharAt(firstDomain.length() - 1);
			} else {
				for (int i = 0; i < splittedUrl.length - 2; i++) {
					secondDomain.append(splittedUrl[i]);
					secondDomain.append(".");
				}
				secondDomain.deleteCharAt(secondDomain.length() - 1);
				for (int i = splittedUrl.length - 2; i < splittedUrl.length; i++) {
					firstDomain.append(splittedUrl[i]);
					firstDomain.append(".");
				}
				firstDomain.deleteCharAt(firstDomain.length() - 1);
			}
			return new Tuple<>(firstDomain.toString(), secondDomain.toString());
		}
		return new Tuple<>(null, null);
	}

	public static String getProtocol(String url) {
		int indexOfProtocol = url.indexOf("://");
		if (-1 == indexOfProtocol || 10 < indexOfProtocol) {
			return null;
		} else {
			return url.substring(0, indexOfProtocol);
		}
	}

	public static String retrieveResource(String url, String method, Map<String, String> params, String charset, Map<String, String> headers)
			throws IOException {
		if (StringUtils.isEmpty(charset)) {
			charset = "utf8";
		}
		HttpClient httpClient = new HttpClient();
		HttpMethod httpMethod = null;
		if ("post".equalsIgnoreCase(method)) {
			httpMethod = new PostMethod(url);
		} else {
			httpMethod = new GetMethod(url);
		}
		httpMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		if (null != headers && !headers.isEmpty()) {
			for (Map.Entry<String, String> requestHeader : headers.entrySet()) {
				httpMethod.addRequestHeader(requestHeader.getKey(), requestHeader.getValue());
			}
		}
		String urlQueryString = httpMethod.getQueryString();
		String[] urlQueries = null;
		int urlQueryCount = 0;
		if (null != urlQueryString) {
			urlQueries = urlQueryString.split("&");
			urlQueryCount = urlQueries.length;
		}
		if (null != params && !params.isEmpty()) {
			NameValuePair[] queries = new NameValuePair[urlQueryCount + params.size()];
			int i = 0;
			if (0 != urlQueryCount) {
				for (; i < urlQueryCount; i++) {
					String[] kv = urlQueries[i].split("=");
					queries[i] = new NameValuePair(kv.length < 1 ? "" : kv[0], kv.length < 2 ? null : kv[1]);
				}
			}
			for (Map.Entry<String, String> entry : params.entrySet()) {
				queries[i] = new NameValuePair(entry.getKey(), entry.getValue());
				i++;
			}
			httpMethod.setQueryString(queries);
		}
		httpClient.executeMethod(httpMethod);
		return new String(httpMethod.getResponseBody(), charset);
	}

}
