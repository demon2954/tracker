package com.lizikj.tracker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class HttpClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60 * 1000)
			.setConnectTimeout(60 * 1000).setConnectionRequestTimeout(60 * 1000).build();

	private static final String DEFAULT_CHARSET = "UTF-8";
	
	private static final String TEXT_CONTENT_TYPE = "text/xml; charset=UTF-8";
	/**
	 * @param url
	 * @param headers
	 * @param postContent
	 * @param defaultCharset
	 * @return
	 * @throws IOException
	 */
	public static String httpPost(String url, Map<String, String> headers, String postContent, String defaultCharset)
			throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			HttpEntity entity = new StringEntity(postContent, defaultCharset);
			Header[] params = new Header[headers.size()];
			Set<String> set = headers.keySet();
			Iterator<String> it = set.iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();
				String value = String.valueOf(headers.get(key));
				params[i] = new BasicHeader(key, value);
				i++;
			}
			httpPost.setHeaders(params);
			httpPost.setEntity(entity);
			httpPost.setConfig(requestConfig);
			HttpResponse response = httpClient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), defaultCharset);
		} finally {
			if (null != httpPost) {
				httpPost.abort();
			}
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param url
	 * @param map
	 * @param charset
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String doPost(String url, Object object, Map<String, String> header, String charset, String sslPath, String password) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			// httpClient = new SSLClient();
			if(StringUtils.isBlank(sslPath)){
				httpClient = HttpClientUtils.createSSLClientDefault();
			}else{
				httpClient = createAuthClientDefault(sslPath, password);
			}
			httpPost = new HttpPost(url);
			if (header != null && !header.isEmpty()) {
				for(Map.Entry<String, String> entry: header.entrySet()){
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			
			// 设置参数
			if(object instanceof Map){
				Map<String, Object> map = (Map<String, Object>) object;
				
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				
				for(Map.Entry<String, Object> entry: map.entrySet()){
					if(entry.getValue() instanceof String){
						list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
					}else{
						list.add(new BasicNameValuePair(entry.getKey(), JsonUtils.toJSONString(entry.getValue())));
					}
				}
				
				if (list.size() > 0) {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
					httpPost.setEntity(entity);
				}
			}else if(object instanceof String){
				httpPost.setEntity(new StringEntity(object.toString(), charset));
			}
			
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != httpPost) {
				httpPost.abort();
			}
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (IOException expect) {
				}
			}
		}
		return result;
	}

	public static String doPost(String url, Map<String, String> map) {
		return doPost(url, map, null);
	}
	
	
	public static String doPost(String url, Map<String, ? extends Object> map, Map<String, String> header) {
		return doPost(url, map, header, DEFAULT_CHARSET);
	}

	public static String doPost(String url, Map<String, ? extends Object> map, Map<String, String> header, String charset) {
		return doPost(url, map, header, charset, null, null);
	}
	
	public static String doPost(String url, Map<String, ? extends Object> map, String sslPath, String password) {
		return doPost(url, map, null, DEFAULT_CHARSET, null, null);
	}
	
	public static String doPost(String url, String text, String sslPath, String password) {
		return doPost(url, text, null, DEFAULT_CHARSET, sslPath, password);
	}

	/**
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {
		CloseableHttpClient httpClient = null;
		HttpGet httpGet = null;
		String result = null;
		try {
			// httpClient = new SSLClient();
			httpClient = HttpClientUtils.createSSLClientDefault();
			httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, Charset.forName(DEFAULT_CHARSET));
				}
			}
		} catch (Exception ex) {
			logger.error("HttpClientUtils.doGet报错,url=" + url, ex);
		} finally {
			if (null != httpGet) {
				httpGet.abort();
			}
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (IOException expect) {
				}
			}
		}
		return result;
	}

	/**
	 * 通过http get获取数据
	 *
	 * @param url
	 * @return
	 * @date 2017年3月24日
	 */
	public static byte[] doGetRawData(String url) {
		CloseableHttpClient httpClient = null;
		HttpGet httpGet = null;
		byte[] result = null;
		try {
			httpClient = HttpClientUtils.createSSLClientDefault();
			httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toByteArray(resEntity);
				}
			}
		} catch (Exception ex) {
			logger.error("HttpClientUtils.doGet报错,url=" + url, ex);
		} finally {
			if (null != httpGet) {
				httpGet.abort();
			}
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (IOException expect) {
				}
			}
		}
		return result;
	}

	public static String doPost(String url, String content) {
		return doPost(url, content, null, DEFAULT_CHARSET, null, null);
	}

	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	/**
	 * 创建证书认证httpclient
	 * @param sslPath 所使用的证书地址
	 * @param password 访问存储证书的密码（同时也是key的密码）
	 * @return CloseableHttpClient
	 * @author lijundong
	 * @date 2017年6月16日 下午3:57:19
	 */
	public static CloseableHttpClient createAuthClientDefault(String sslPath, String password) {
		if (StringUtils.isNotBlank(sslPath)) {
			SSLConnectionSocketFactory sslsf = null;
			try {
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				
				InputStream inputStream = null;
				//先拿绝对路径的
				try {
					inputStream = new FileInputStream(new File(sslPath));
				} catch (Exception e) {
					//如果为null，拿相对路径的
					if(inputStream == null)
						inputStream = HttpClientUtils.class.getResourceAsStream(sslPath);
				}
				
				try {
					keyStore.load(inputStream, password.toCharArray());
				} finally {
					inputStream.close();
				}
				SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).build();
				sslsf = new SSLConnectionSocketFactory(sslcontext);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		}
		return null;
	}

}
