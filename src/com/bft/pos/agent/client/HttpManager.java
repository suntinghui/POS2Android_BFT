package com.bft.pos.agent.client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import android.util.Log;

import com.bft.pos.client.exception.HttpException;
import com.bft.pos.util.JSONUtil;
import com.bft.pos.util.TrafficUtil;

public class HttpManager {
	
	private static boolean isHttpsFlag				= false;
	
	private static HttpManager instance 			= null;
	
	private final static int ConnectionTimeout 		= 27000;
	private final static int SocketTimeout 			= 32000;
	private final static int SocketBufferSize 		= 8192;
	
	byte[] reqHeaderLenght = new byte[2];// 报文头
	
	private HttpClient httpClient 					= null;
	private PostMethod postMethod 						= null;
	private boolean aborted 						= false;
	
	private static final int port = 9200;

	public static final int URL_XML_TYPE = 1;
	public static final int URL_JSON_TYPE  = 2;

	public static HttpManager getInstance() {

		if (instance == null) {
			instance = new HttpManager();
		}
		return instance;
	}
	
	private HttpClient getHttpClient(){
		if (null == httpClient){
			if(isHttpsFlag){
				httpClient = initHttps();
			} else {
				httpClient = initHttp();
			}
		}
		
		return httpClient;
	}
	
	/***
	public static String getUrl() {
		return replace(URL, "{0}", serverIp);
	}

	public String replace(String text, String oldStr, String newStr) {
		int oldLen = oldStr.length();
		int k = 0;
		while (k + oldLen < text.length()) {
			k = text.indexOf(oldStr, k);
			if (k == -1) {
				return text;
			}

			text = text.substring(0, k) + newStr
					+ text.substring(k += oldLen, text.length());
		}
		return text;
	}
	
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
		if(httpClient != null){
			return httpClient;
		}else{
			httpClient = new HttpClient();
			
			 //设置 HttpClient 接收 Cookie,用与浏览器一样的策略
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			
		}
		return httpClient;
	}
	
	private HttpClient initHttps() {
		return null;
	}
	
	/**
	 * @param type
	 * @param transferCode
	 * @param outBytes
	 * @param MREntity 实体
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public byte[] sendRequest(int type, String transferCode ,byte[] outBytes,Part[] parts) throws HttpException, IOException{
//		Part[] partss = new Part[]{};
//		partss[0] = new FilePart("signImg", new File(filename+"图片1.png");
//		partss[1] = new FilePart("signImg", new File(filename+"图片1.png");
//		partss[2] = new FilePart("signImg", new File(filename+"图片1.png");
		// 记录上行流量
		TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_SEND, outBytes.length);
		
		byte[] bArray = null;
		int status = -1;
		Map<String,Object> req_map;
		
		if (type == HttpManager.URL_JSON_TYPE){
			//2确定请求方式 new UTF8PostMethod()解决中文乱码
			postMethod = new ENCODEPostMethod(Constant.JSONURL+AppDataCenter.getMethod_Json(transferCode));
		} else {
			//2确定请求方式 new UTF8PostMethod()解决中文乱码
			postMethod = new ENCODEPostMethod(Constant.XMLURL);
		}
		
		try {
			String req_json = new String(outBytes);
			/*==============由于各连接系统不同，做特殊处理，故在此解包处理再组包(json)：================*/
			req_map = JSONUtil.JSONStr2MAP(req_json);
			req_json = (String) req_map.get("arg");
			req_map.clear();
			req_map = JSONUtil.JSONStr2MAP(req_json);
			
			req_json = JSONUtil.MAP2JSONStr(req_map);
			/*==============================*/
			
			Log.i("REQ_JSON:", req_json);
			if(parts != null){
				/*=====带附件========*/
				//4 构建实体
				MultipartRequestEntity entity = new MultipartRequestEntity(parts, postMethod.getParams());

				//5 设置实体
				postMethod.setRequestEntity(entity);
			}else{
				/*======普通=======*/
				NameValuePair[] param = { new NameValuePair("common",req_json)};  
				postMethod.setRequestBody(param);   
				/*=============*/
			}
			
		} catch (Exception e1) {
			throw new HttpException(e1.getMessage());
		}
		
		try {

			//6 执行请求
			status = getHttpClient().executeMethod(postMethod);

			Log.i("STATUS:", String.valueOf(status));
			// 7判断请求是否成功 200/HttpStatus.SC_OK ：成功
			if(status == HttpStatus.SC_OK){
				bArray = postMethod.getResponseBody();
				// 记录下行流量
				TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_RECEIVE, bArray.length);
				Log.i("相应报文====》\t", new String(bArray,Constant.ENCODING_JSON));
			}
			/*登录时拿到cookie值*/
			if(transferCode.equals("089016")){
				//获得登陆后的 Cookie
				Cookie[] cookies = getHttpClient().getState().getCookies();
				
				String tmpcookies= "";
				for(Cookie c:cookies){
					tmpcookies += c.toString()+";";
				}
				System.out.println("tmpcookies:" + tmpcookies);
			}else{
				
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
			// IOException - in case of a problem or the connection was aborted
			throw new HttpException(903);
		}finally{
			if(null != getHttpClient())
				getHttpClient().getHttpConnectionManager().closeIdleConnections(0);
		}
		
		
		return bArray;
		
	}
	/**
	 * @param type
	 * @param transferCode
	 * @param outBytes
	 * @param MREntity 实体
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public byte[] sendRequest(int type, String transferCode ,byte[] outBytes) throws HttpException, IOException{
		
		// 记录上行流量
		TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_SEND, outBytes.length);
		
		byte[] bArray = null;
		int status = -1;
		Map<String,Object> req_map;
		
		///////////////////
//		int reqMsgLen = outBytes.length;
//
//		reqHeaderLenght[0] = (byte) ((reqMsgLen & 0xff00) >> 8);
//		reqHeaderLenght[1] = (byte) (reqMsgLen & 0xff);
//
//		/**
//		 * 组装字节类型报文 数据长度+{头文件（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+
//		 * 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值}
//		 * */
//		System.out.println("reqMsgLen：" + reqMsgLen);
//		ByteBuffer sendBuf = ByteBuffer.allocate(reqHeaderLenght.length + reqMsgLen);
//		/* 2个字节的报文长度值 */
//		sendBuf.put(reqHeaderLenght);
//		/* 头文件（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+ 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值+ */
//		sendBuf.put(outBytes);
//
//		outBytes = sendBuf.array();
		
		//////////////////
		
		if (type == HttpManager.URL_JSON_TYPE){
			postMethod = new ENCODEPostMethod(Constant.JSONURL+AppDataCenter.getMethod_Json(transferCode));
		} else {
			postMethod = new ENCODEPostMethod(Constant.XMLURL);
		}
		
		try {
			String req_json = new String(outBytes);
			/*==============由于各连接系统不同，做特殊处理，故在此解包处理再组包(json)：================*/
			req_map = JSONUtil.JSONStr2MAP(req_json);
			req_json = (String) req_map.get("arg");
			req_map.clear();
			req_map = JSONUtil.JSONStr2MAP(req_json);
			
			req_json = JSONUtil.MAP2JSONStr(req_map);
			/*==============================*/
			
			Log.i("REQ_JSON:", req_json);
			NameValuePair[] param = { new NameValuePair("common",req_json)};  
			postMethod.setRequestBody(param);   
		
		} catch (Exception e1) {
			throw new HttpException(e1.getMessage());
		}
		
		try {

			//6 执行请求
			status = getHttpClient().executeMethod(postMethod);

			Log.i("STATUS:", String.valueOf(status));
			// 7判断请求是否成功 200/HttpStatus.SC_OK ：成功
			if(status == HttpStatus.SC_OK){
				bArray = postMethod.getResponseBody();
				// 记录下行流量
				TrafficUtil.getInstance().setTraffic(TrafficUtil.TYPE_RECEIVE, bArray.length);
				Log.i("相应报文====》\t", new String(bArray,Constant.ENCODING_JSON));
			}
			/*登录时拿到cookie值*/
			if(transferCode.equals("089016")){
				//获得登陆后的 Cookie
				Cookie[] cookies = getHttpClient().getState().getCookies();
				
				String tmpcookies= "";
				for(Cookie c:cookies){
					tmpcookies += c.toString()+";";
				}
				System.out.println("tmpcookies:" + tmpcookies);
			}else{
				
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
			// IOException - in case of a problem or the connection was aborted
			throw new HttpException(903);
		}finally{
			if(null != getHttpClient())
				getHttpClient().getHttpConnectionManager().closeIdleConnections(0);
		}
		
		
		return bArray;
		
	}

	/**
	 * 重载PostMethod的getRequestCharSet()方法, 返回我们需要的编码(字符集)名称, 就可以解决 UTF-8 或者其它非默认编码提交 POST 请求时的乱码问题了
	 * */
	public static class ENCODEPostMethod extends PostMethod{     
		public ENCODEPostMethod(String url){     
			super(url);     
		}     
		@Override    
		public String getRequestCharSet() {     
			//return super.getRequestCharSet();     
			return Constant.ENCODING_JSON;     
		}     
	}  


}
