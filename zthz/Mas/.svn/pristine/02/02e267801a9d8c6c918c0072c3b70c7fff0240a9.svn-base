package cn.zthz.tool.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import cn.zthz.tool.push.MySSLSocketFactory;

public class HttpUtils {

//	public static final DefaultHttpClient POOL_HTTP_CLIENT = getNewHttpClient();

	public static DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			// ClientConnectionManager ccm = new
			// PoolingClientConnectionManager(registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static String doPosts(String url, Map<String, ?> params)
			throws Exception {
		return doPosts(url, params, "UTF-8");
	}

	private static final String BOUNDARY = "----------hz--";
	public static String doMultipart(String url, Map<String, ?> params)
			throws Exception {
		DefaultHttpClient httpclient = getNewHttpClient();
		try {

			HttpPost post = new HttpPost(url);
			post.setHeader("Content-Type", "multipart/form-data;boundary="+BOUNDARY);
			if (null != params) {
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT, BOUNDARY,Charset.forName("utf-8"));
				Object value = null;
				String key = null;
				ContentBody contentBody = null;
				for (Map.Entry<String, ?> entry : params.entrySet()) {
					key = entry.getKey();
					value = entry.getValue();
					if (null == value) {
						contentBody = new StringBody("");
					} else if (value instanceof String) {
						contentBody = new StringBody((String)value );
					} else if (value instanceof InputStream) {
						contentBody = new InputStreamBody((InputStream) value,
								key);
					} else if (value instanceof File) {
						contentBody = new FileBody((File) value);
					} else if (value instanceof byte[]) {
						contentBody = new ByteArrayBody((byte[]) value, key);
					}
					entity.addPart(key, contentBody);
				}
				post.setEntity(entity);
			}
			HttpResponse response = httpclient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				return EntityUtils.toString(responseEntity);
			}
			return null;
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

	public static String doPostsEntity(String url, String content,
			ContentType contentType) throws Exception {
		DefaultHttpClient httpclient = getNewHttpClient();
		try {

			HttpPost post = new HttpPost(url);
			HttpEntity requestEntity = new StringEntity(content, contentType);
			post.setEntity(requestEntity);
			HttpResponse response = httpclient.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
			return null;
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

	public static String doPosts(String url, Map<String, ?> params,
			String charset) throws Exception {
		DefaultHttpClient httpclient = getNewHttpClient();
		try {

			HttpPost post = new HttpPost(url);
			List<BasicNameValuePair> pairs = new LinkedList<>();
			if (null != params) {

				for (Map.Entry<String, ?> entry : params.entrySet()) {
					pairs.add(new BasicNameValuePair(entry.getKey(),
							(null != entry.getValue()) ? entry.getValue()
									.toString() : ""));
				}
			}
			HttpEntity requestEntity = new UrlEncodedFormEntity(pairs, charset);

			post.setEntity(requestEntity);
			HttpResponse response = httpclient.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
			return null;
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

	public static String doGet(String url) throws IOException {
		return doGet(url, "utf-8");
	}

	public static byte[] doGetInBytes(String url) throws IOException {
		InputStream inputStream = null;
		// OutputStream outputStream = null;
		URL u = new URL(url);
		URLConnection connection = u.openConnection();
		inputStream = connection.getInputStream();
		// outputStream = connection.getOutputStream();
		int size = connection.getContentLength();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(
				size <= 0 ? 1024 : size);
		IOUtils.copy(inputStream, arrayOutputStream);
		// outputStream.close();
		inputStream.close();
		return arrayOutputStream.toByteArray();
	}

	public static String doGet(String url, String charset) throws IOException {
		InputStream inputStream = null;
		// OutputStream outputStream = null;
		URL u = new URL(url);
		URLConnection connection = u.openConnection();
		inputStream = connection.getInputStream();
		// outputStream = connection.getOutputStream();
		int size = connection.getContentLength();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(
				size <= 0 ? 1024 : size);
		IOUtils.copy(inputStream, arrayOutputStream);
		// outputStream.close();
		inputStream.close();
		return new String(arrayOutputStream.toByteArray(), charset);
	}

	static ExecutorService executor = Executors.newFixedThreadPool(100);

	public static void test() throws InterruptedException {
		long t1 = System.currentTimeMillis();
		// for (int i = 0; i < 10; i++) {
		// executor.execute(new Runnable() {
		//
		// @Override
		// public void run() {
		try {
			String url = "http://192.168.0.151/userRequirement/requirementInfo.json?requirementId=ff8080813c05aec1013c05aec3f60001";
			// Map<String, Object> params = new HashMap<String, Object>(2);
			// params.put("requirementId", "ff8080813c05aec1013c05aec3f60001");
			// params.put("hello", 1);
			String result = doGet(url, "utf-8");
			System.out.println(result);
			// synchronized (ExecutorService.class) {
			// System.out.println(result.length());
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
		// });
		// }
		long t2 = System.currentTimeMillis();
		System.out.println("time cost:" + (t2 - t1) + "ms");
		// Thread.sleep(10000);
	}

	public static void main(String[] args) throws Exception {
		// for (int i = 0; i < 2000000; i++)
		test();
		System.out.println("over--------------------------");
	}
}
