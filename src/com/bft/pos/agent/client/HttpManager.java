package com.bft.pos.agent.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import android.util.Log;

import com.bft.pos.client.exception.HttpException;
import com.bft.pos.util.TrafficUtil;

public class HttpManager {
	private static String cookie = null;

	private static boolean isHttpsFlag = false;
	/**
	 * 类的实例
	 */
	private static HttpManager instance = null;

	private final static int ConnectionTimeout = 27000;
	private final static int SocketTimeout = 32000;
	private final static int SocketBufferSize = 8192;

	byte[] reqHeaderLenght = new byte[2];// 报文头

	/**
	 * HttpClient实例
	 */
	private HttpClient httpClient = getHttpClient();
	private PostMethod postMethod = null;
	private boolean aborted = false;


	public static final int URL_XML_TYPE = 1;
	public static final int URL_JSON_TYPE = 2;

	/**
	 * 链接的超时数,默认为30秒,此处要做成可配置
	 */
	private static final int CONNECTION_TIME_OUT = 30000;

	/**
	 * 每个主机的最大并行链接数，默认为2
	 */
	private static final int MAX_CONNECTIONS_PER_HOST = 10;

	/**
	 * 客户端总并行链接最大数，默认为20
	 */
	private static final int MAX_TOTAL_CONNECTIONS = 20;

	/** 日志对象 */
	// private static final DebugLog logger =
	// LogFactory.getDebugLog(HttpTools.class);

	/**
	 * http的header中的content-type属性的名字
	 */
	private static final String CONTENT_TYPE_NAME = "content-type";

	/**
	 * http的header中的content-type属性的内容
	 */
	private static final String CONTENT_TYPE_VALUE_XML_UTF_8 = "text/xml; charset=UTF-8";

	/**
	 * http的header中的content-type属性的传输类型
	 */
	private static final String TEXT_XML = "text/xml";

	/**
	 * http的header中的content-type属性的字符编码
	 */
	private static final String UTF_8 = "UTF-8";

	public static HttpManager getInstance() {

		if (instance == null) {
			instance = new HttpManager();
		}
		return instance;
	}

	/**
	 * 构造Http客户端对象 <功能详细描述>
	 * 
	 * @return [参数说明]
	 * 
	 * @return HttpClient [返回类型说明]
	 * @exception throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	private HttpClient initHttpClient() {
		// 此处运用连接池技术。
		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();

		// 设定参数：与每个主机的最大连接数
		int maxConnectionsI = MAX_CONNECTIONS_PER_HOST;
		manager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionsI);

		// 设定参数：客户端的总连接数
		int maxTotalConnections = MAX_TOTAL_CONNECTIONS;
		manager.getParams().setMaxTotalConnections(maxTotalConnections);

		// 使用连接池技术创建HttpClient对象
		HttpClient client = new HttpClient(manager);

		// 设置超时时间
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(CONNECTION_TIME_OUT);

		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		return client;
	}

	private HttpClient getHttpClient() {
		if (null == httpClient) {
			if (isHttpsFlag) {
				httpClient = initHttps();
			} else {
				// httpClient = initHttp();
				httpClient = initHttpClient();
			}
		}

		return httpClient;
	}

	/***
	 * public static String getUrl() { return replace(URL, "{0}", serverIp); }
	 * 
	 * public String replace(String text, String oldStr, String newStr) { int
	 * oldLen = oldStr.length(); int k = 0; while (k + oldLen < text.length()) {
	 * k = text.indexOf(oldStr, k); if (k == -1) { return text; }
	 * 
	 * text = text.substring(0, k) + newStr + text.substring(k += oldLen,
	 * text.length()); } return text; }
	 ****/

	public void abort() {
		if (!aborted && null != postMethod) {
			try {
				postMethod.abort();
			} catch (Exception e) {
				e.toString();
			}
		}
		aborted = true;
	}

	private HttpClient initHttp() {
		if (httpClient != null) {
			return httpClient;
		} else {
			httpClient = new HttpClient();
		}
		return httpClient;
	}

	private HttpClient initHttps() {
		return null;
	}

	/**
	 * 设置cookie参数
	 */
	public void setCookie() {
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);

		HttpState initalState = new HttpState();

		Cookie cookie = new Cookie();
		// cookie.setDomain(arg0);
		// cookie.setPath(path);
		// cookie.setName(name);
		// cookie.setValue(value);;
		initalState.addCookie(cookie);
	}

	/**
	 * m
	 * 
	 * @param type
	 * @param transferCode
	 * @param outBytes
	 * @param MREntity
	 *            实体
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public byte[] sendRequest(int type, String transferCode, byte[] outBytes,
			Part[] parts) throws HttpException {
		// 记录上行流量
		TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_SEND,
				outBytes.length);

		byte[] bArray = null;
		int status = -1;

		if (type == HttpManager.URL_JSON_TYPE) {
			// 2确定请求方式 new ENCODEPostMethod()解决中文乱码
			postMethod = new ENCODEPostMethod(Constant.JSONURL
					+ AppDataCenter.getMethod_Json(transferCode));
		} else {
			// 2确定请求方式 new UTF8PostMethod()解决中文乱码
			postMethod = new ENCODEPostMethod(Constant.XMLURL);
		}
		if (!transferCode.equals("089021")) {
			// 每次访问需授权的网址时需带上前面的 cookie 作为通行证;由于cookie 这个key被系统强行占用 故此定义cookie2；
			postMethod.setRequestHeader("cookie2", cookie);
		}

		/**
		 * 设置cookie
		 * */
		// setCookie();
		/** ================================ */
		try {
			String req_json = new String(outBytes);
			/* ==============由于各连接系统不同，做特殊处理，故在此解包处理再组包(json)：================ */
			// Map<String,Object> req_map;
			// req_map = JSONUtil.JSONStr2MAP(req_json);
			// req_json = (String) req_map.get("arg");
			// req_map.clear();
			// req_map = JSONUtil.JSONStr2MAP(req_json);
			//
			// req_json = JSONUtil.MAP2JSONStr(req_map);
			/* ============================== */

			if (parts != null) {
				/** =====带附件======== */
				//
				Part[] part = new Part[parts.length + 1];

				for (int i = 0; i <= parts.length - 1; i++) {
					part[i] = parts[i];
				}
				part[parts.length] = new StringPart("common", req_json,
						Constant.ENCODING_JSON);
				// 4 构建实体
				MultipartRequestEntity entity = new MultipartRequestEntity(
						part, postMethod.getParams());

				// 5 设置实体
				postMethod.setRequestEntity(entity);

			} else {
				/** ======普通======= */
				NameValuePair[] param = { new NameValuePair("common", req_json) };
				postMethod.setRequestBody(param);
			}
			Log.i("REQ_JSON:", req_json);

		} catch (Exception e1) {
			Log.i("error:", e1.getMessage());
			throw new HttpException(e1.getMessage());
		}
		try {
			// 6 执行请求
			status = httpClient.executeMethod(postMethod);

			Log.i("STATUS:", String.valueOf(status));
			// 7判断请求是否成功 200/HttpStatus.SC_OK ：成功
			if (status == HttpStatus.SC_OK) {
				bArray = postMethod.getResponseBody();
				// 所有头信息存入静态常量中
				if (postMethod.getRequestHeaders() != null) {

					Constant.HEADER_MAP = new HashMap<String, Object>();
					for (Header head : postMethod.getResponseHeaders()) {
						if (!head.getName().equals("Content-Type")
								&& !head.getName().equals("Set-Cookie"))
							System.out.println("Headers:\t" + head.getName()
									+ "=" + head.getValue());
						Constant.HEADER_MAP
								.put(head.getName(), head.getValue());
					}
				}
				// 记录下行流量
				TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_RECEIVE,
						bArray.length);
				Log.i("响应报文====》\t", new String(bArray, Constant.ENCODING_JSON));

			}
			/* 登录时拿到cookie值 */
			if (transferCode.equals("089021")) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 获得登陆后的 Cookie
				Cookie[] cookies_ = httpClient.getState().getCookies();
				for (Cookie c : cookies_) {
					String[] str = null;
					/** cookies所有拼接一块 */
					// cookies += c.toString()+";";
					str = c.toString().split("=");
					map.put(c.getName(), c.getValue());
					// Log.i("cookie:" , c.toString());
				}
				cookie = (String) map.get("uuid");
				Log.i("UUID:", cookie);
			} else {

			}

		} catch (IOException e1) {
			e1.printStackTrace();
			// IOException - in case of a problem or the connection was aborted
			throw new HttpException(903);
		} finally {
			if (null != httpClient)
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
			if (null != postMethod)
				postMethod.releaseConnection();
		}
		return bArray;
	}
	/**
	 * 重载PostMethod的getRequestCharSet()方法, 返回我们需要的编码(字符集)名称, 就可以解决 UTF-8
	 * 或者其它非默认编码提交 POST 请求时的乱码问题了
	 * */
	public static class ENCODEPostMethod extends PostMethod {
		public ENCODEPostMethod(String url) {
			super(url);
		}
		@Override
		public String getRequestCharSet() {
			// return super.getRequestCharSet();
			return Constant.ENCODING_JSON;
		}
	}
}
