package haoframe.core.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;

public class WebClient {

	/**
	 * GET 请求指定的页面信息，并返回实体主体。 HEAD 类似于get请求，只不过返回的响应中没有具体的内容，用于获取报头 POST
	 * 向指定资源提交数据进行处理请求（例如提交表单或者上传文件）。数据被包含在请求体中。POST请求可能会导致新的资源的建立和/或已有资源的修改。 PUT
	 * 从客户端向服务器传送的数据取代指定的文档的内容。 DELETE 请求服务器删除指定的页面。 CONNECT
	 * HTTP/1.1协议中预留给能够将连接改为管道方式的代理服务器。 OPTIONS 允许客户端查看服务器的性能。 TRACE
	 * 回显服务器收到的请求，主要用于测试或诊断。
	 * 
	 * @author chianghao
	 *
	 */

	public enum HttpMethod {
		GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH;
	}

	private String url;

	private HttpMethod method;

	private Map<String, String> params;

	private Object entity;

	private CloseableHttpResponse response;

	private BasicCookieStore cookieStore;
	
	private HttpClientContext context;
	
	
	CloseableHttpClient client = null;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public HttpResponse getResponse() {
		return response;
	}

	
	
	public WebClient(String url, HttpMethod method) {
		this.url = url;
		this.method = method;
	}
	public WebClient(String url, HttpMethod method,BasicCookieStore cookieStore) {
		this.url = url;
		this.cookieStore= cookieStore;
		this.method = method;
	}

	public WebClient(String url, HttpMethod method, Map<String, String> params) {
		this.url = url;
		this.method = method;
		this.params = params;
	}

	public void setResponse(CloseableHttpResponse response) {
		this.response = response;
	}

	private String getQueryString(Map<String, String> params) {
		if (params != null && params.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("?");
			for (String key : params.keySet()) {
				sb.append(key + "=" + params.get(key) + "&");
			}
			String queryString = sb.substring(0, sb.lastIndexOf("&"));
			return queryString;
		}
		return "";
	}

	public WebClient request() {
		if (params!=null&&!params.isEmpty()) {
			url += getQueryString(params);
		}
		if (method == HttpMethod.GET) {
			HttpGet request = new HttpGet(url);
			request(request);
			
		} else if (method == HttpMethod.HEAD) {
			HttpHead request = new HttpHead(url);
			request(request);
		} else if (method == HttpMethod.POST) {
			HttpPost request = new HttpPost(url);
			if (entity != null) {
				HttpEntity a = new StringEntity(JSON.toJSONString(entity), "UTF-8");
				request.setEntity(a);
			}
			request(request);
		} else if (method == HttpMethod.PUT) {
			HttpPut request = new HttpPut(url);
			request(request);
		} else if (method == HttpMethod.DELETE) {
			HttpDelete request = new HttpDelete(url);
			request(request);
		} else if (method == HttpMethod.OPTIONS) {
			HttpOptions request = new HttpOptions(url);
			request(request);
		} else if (method == HttpMethod.TRACE) {
			HttpTrace request = new HttpTrace(url);
			request(request);
		} else if (method == HttpMethod.PATCH) {
			HttpPatch request = new HttpPatch(url);
			request(request);
		} else {
			throw new RuntimeException("不支持该类型的请求方法");
		}
		return this;
	}

	private void request(HttpUriRequest request) {
		
		try {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, null, null,
					NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", csf).build();
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
			// 最大连接数3000
			connectionManager.setMaxTotal(3000);
			// 路由链接数400
			connectionManager.setDefaultMaxPerRoute(400);

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000)
					.setConnectionRequestTimeout(10000).build();

			if(cookieStore==null) {
				cookieStore = new BasicCookieStore();
			}
			
			for (Cookie c : cookieStore.getCookies()) {
				System.out.println(c.getName() + ": " + c.getValue());
			}
			
			client = HttpClients.custom().setDefaultRequestConfig(requestConfig)
					.setDefaultCookieStore(cookieStore)
					.setConnectionManager(connectionManager).evictExpiredConnections()
					.evictIdleConnections(30, TimeUnit.SECONDS).build();

			request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
			context = HttpClientContext.create();
		    context.setCookieStore(cookieStore);
			response = client.execute(request,context);
//			for (Cookie c : cookieStore.getCookies()) {
//				System.out.println(c.getName() + ": " + c.getValue());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if(response!=null) {
			try {
				client.close();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getString() {
		try {
			//获取编码格式
			String charset = "UTF-8";
			Header[] headers = response.getHeaders("Content-Type");
			if(headers!=null&&headers.length>0) {
				for(Header h:headers) {
					if(h.getName().equals("Content-Type")) {
						String value = h.getValue();
						if(value.toLowerCase().contains("charset")) {
							String[] array = value.split(";");
							for(String a:array) {
								a=a.trim();
								if(a.toLowerCase().startsWith("charset")&&a.contains("=")) {
									charset = a.substring((a.indexOf("=")+1));
								}
							}
						}
					}
				}
			}
			return EntityUtils.toString(this.response.getEntity(),charset);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return "";
	}
	
	
	public InputStream getInputStream() {
		try {
			return this.response.getEntity().getContent();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		return null;
	}

	public BasicCookieStore getCookieStore() {
		BasicCookieStore newCookieStore = new BasicCookieStore();
		for(Cookie c :this.cookieStore.getCookies()) {
			BasicClientCookie cookie = (BasicClientCookie) c;
			try {
				newCookieStore.addCookie((BasicClientCookie) cookie.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return newCookieStore; 
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public HttpClientContext getContext() {
		return context;
	}

	public void setContext(HttpClientContext context) {
		this.context = context;
	}
	
}