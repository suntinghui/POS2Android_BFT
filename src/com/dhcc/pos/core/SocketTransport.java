package com.dhcc.pos.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;
import com.dhcc.pos.packets.Packet;
import com.dhcc.pos.packets.util.ConvertUtil;
import com.dhcc.pos.parse.CnConfigParser;
import com.dhcc.pos.parse.Parse;

public class SocketTransport {
	private static SocketTransport instance = null;
	CnMessageFactory mfact = null;
	private String host;
	private int port;
	private int timeout;
	private int headlength;
	private int TPDUlength;
	private Packet packet;
	private Parse parse;

	/** 静动开关，true为动态 非true为静态（回路） */
	private String isDynamic;
	/** 是否为linux 环境true为是 false为不是 */
	private String isLinux;
	/** 报文配置文件 */
	private String msgConfig;
	String fieldTrancode = null;
	String msgType = null;

	/** 返回的报文 */
	String readLine = null;
	// byte[] lenBuffer = new byte[in.available()];// 报文头
	byte[] lenBuffer = null;// 报文头
	/** 2字节的报文长度值 */
	byte[] reqHeaderLenght = new byte[2];// 报文头
	/** 2字节的报文长度值 */
	byte[] respHeaderLenght = new byte[2];// 报文头
	Socket socket = null;
	/** 输出流 */
	OutputStream out = null;
	/** 输入流 */
	InputStream is = null;
	/** 响应报文 */
	byte[] respMsg = null;
	/** 响应报文 */
	Map<String, Object> resp_map = null;

	public static synchronized SocketTransport getInstance(){
		if(instance == null)
			instance = new SocketTransport();
		return instance;
	}
	public SocketTransport() {
		this.host = "114.80.227.152";
//		this.host = "www.payfortune.com";
		this.port = 2003; 
//		this.host = "61.132.75.110";//优乐通
//		this.port = 9999; 
		
		this.timeout = 45000;
		this.headlength = 12;
		
		this.TPDUlength = 10;
		
	}

	public SocketTransport(String host, int port, int timeout, int headlength,int TPDUlength) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.headlength = headlength;
		this.TPDUlength = TPDUlength;
	}

	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = 0;
		while ((b = is.read()) != -1)
			baos.write(b);

		/* 此处拿到的自己数组 应该减去前两个字节（前两个字节为报文总长度） */
		return baos.toByteArray();
	}

	/**
	 * 创建消息工厂并创建请求 context容器设置tranCode、req_map、req_xml；  模块、注册、zu'bao
	 * @return reqMsg (tpdu+头文件+报文类型+位图+位图对应的域值)
	 */
	@SuppressWarnings("null")
	public CnMessageFactory beforeProcess(Map<String, Object> req_map) {
		Log.i("\t####################【BeforeProcess】####################"
				+ "\r","");
		/**
		 * 通过指定的报文配置文件创建消息工厂（mfact）
		 * */
		try {
			/* 通过xml消息配置文件创建消息工厂， */
			if(!Constant.isAISHUA){
				// 通过xml消息配置文件创建消息工厂
				mfact = CnConfigParser.createFromXMLConfigFile(ApplicationEnvironment.getInstance().getApplication().getResources().openRawResource(R.raw.msg_config));
			}else{
				mfact = CnConfigParser.createFromXMLConfigFile(ApplicationEnvironment.getInstance().getApplication().getResources().openRawResource(R.raw.msg_config_aishua));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("REQ_MAP:\t" + req_map + "\r","");
		/**
		 * context容器设置交易码（消息类型）的值
		 * */
		msgType = (String) req_map.get("msgType");
		/**
		 * 设置交易码（消息类型）的值
		 * */
		fieldTrancode = (String) req_map.get("fieldTrancode");
		
		/**
		 * 根据模板创建并初始化一个报文对象（如果配置文件中没有配置template会跳过）
		 * */
//		mfact.newMessagefromTemplate(msgType);

		return mfact;
	}
	
	public Map<String, Object> afterProcess(CnMessageFactory mfact){
		CnMessage m = mfact.getCnMessage();
		resp_map = new HashMap<String, Object>();
		for (int i = 0; i < 128; i++) {
			if (m.hasField(i)) {
				resp_map.put("field" + i, m.getField(i).toString());
			}
		}
		resp_map.put("fieldTrancode", fieldTrancode);
		return resp_map;
	}

	/**
	 * 主处理函数
	 * 
	 */
	public byte[] process(Map<String, Object> req_map) {
		Log.i("\t####################【Process】####################" + "\r","");

		CnMessageFactory mfact = beforeProcess(req_map);
		Packet packet = Packet.getInstance();
		CnMessage m = null;
		m = packet.registerReqMsg(mfact, req_map);

		byte[] reqMsg = packet.packet(m);
		
//		respMsg = sendData(reqMsg);
//		
//		m = parse.parse(mfact, respMsg, TPDUlength, headlength);
//		
//		mfact.setCnMessage(m);
//		
//		afterProcess(mfact);
//		
//		Log.i("resp_map:" + resp_map,"");
		return reqMsg;
	}
	
	
	
	public byte[] sendData(byte reqMsg[]){
		System.out.println("####################【sendData】####################" + "\r");

		/** 第二种（合理） 拿到报文总字节长度（前两个字节定义的长度） */
		int reqMsgLen = reqMsg.length;
		reqHeaderLenght[0] = (byte) ((reqMsgLen & 0xff00) >> 8);
		reqHeaderLenght[1] = (byte) (reqMsgLen & 0xff);
		//		 int _length = ((lenght[0] & 0xff) << 8) | (lenght[1] & 0xff);
		/**
		 * 组装字节类型报文 数据长度+{头文件（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+
		 * 报文类型【BCD压缩2字节】+位图【8字节】&位图对应的域值}
		 * */
		System.out.println("reqMsgLen：" + String.valueOf(reqMsgLen));
		ByteBuffer sendBuf = ByteBuffer.allocate(reqHeaderLenght.length
				+ reqMsgLen);
		/** 2个字节的报文长度值 */
		sendBuf.put(reqHeaderLenght);
		/** 头文件（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+ 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值+ */
		sendBuf.put(reqMsg);

		reqMsg = sendBuf.array();
//		Log.debug("发送到的交易平台报文时间:\t【" + DateUtil.formatYearDateTime(new Date()) + "】","");
		System.out.println("发送报文msg 16进制:\r" + ConvertUtil.trace(reqMsg));

		try {
			// 获取一个连接到socket服务的套接字
			socket = new Socket(host, port);
			
			/* 设置超时时间 */
			socket.setSoTimeout(timeout);
			is = socket.getInputStream();
			
			try {
//				int count = respHeaderLenght.length;
//				int readCount = 0; // 已经成功读取的字节的个数
//				while (readCount < count) {
//					readCount += is.read(respHeaderLenght, readCount, count - readCount);
//				}
//				inputStream2Bytes(is);
				  is.read(respHeaderLenght);

				  System.out.print("respHeaderLenght:" + ConvertUtil.trace(respHeaderLenght));
			} catch (IOException e) {
				Log.e("读取流异常",e.toString());
				throw new IOException("读取响应报文异常",e);
			}
		if(is==null){
			System.out.print("is null");
		}
			out = socket.getOutputStream();
			out.write(reqMsg);
			//清除out数据
			out.flush();

			respMsg = revData(respHeaderLenght, is);
//			Log.i("接收到的交易平台报文时间:\t【"+ DateUtil.formatYearDateTime(new Date()) + "】");
			System.out.println("接收到的交易平台报文16进制:\r" + ConvertUtil.trace(respMsg));
		} catch (ConnectException e) {
			System.out.print("无法连接到地址:"+e);
//			throw new ConnectException("网络问题请重试");
		} catch (SocketException e) {
			System.out.print("socket协议有误:"+e);
//			log.error("socket协议有误",e);
//			throw new CommunicationException("网络问题请重试");
		} catch (IOException e) {
			System.out.print("发生IO异常:"+e);
//			log.error("发生IO异常",e);
//			throw new CommunicationException("网络问题请重试");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e("关闭流发生IO异常",e.getMessage());
//					throw new CommunicationException("网络问题请重试");
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Log.e("关闭流发生IO异常",e.getMessage());
//					throw new CommunicationException("网络问题请重试");
				}
			}
			if (socket != null) {
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
//					socket.close();
				} catch (IOException e) {
					Log.e("关闭流发生IO异常",e.getMessage());
//					throw new CommunicationException("网络问题请重试");
				}
			}
		}
		return respMsg;
	}
	public byte[] inputStream2Bytes(InputStream is) throws IOException{
		byte[] b = null;
		try {
			int count = 0;
			while (count == 0) {
				count = is.available();
			}
			b = new byte[count];
			
			is.read(b);
			
			
			System.out.println(ConvertUtil.trace(b));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException("读取流异常", e);
		}



		//或
		//			int readCount = 0; // 已经成功读取的字节的个数
		//			while (readCount < count) {
		//				readCount += is.read(respHeaderLenght, readCount, count - readCount);
		//				
		//			}


		return b;

	}
	
	
	
	/**
	 * @param respHeaderLenght
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public byte[] revData(byte[] respHeaderLenght, InputStream is) throws IOException {
	
		try {
//			int count = respHeaderLenght.length;
//			int readCount = 0; // 已经成功读取的字节的个数
//			while (readCount < count) {
//				readCount += is.read(respHeaderLenght, readCount, count - readCount);
//			}
//			inputStream2Bytes(is);
			  is.read(respHeaderLenght);
			  //====

			  //			resp_byte = getBytes(is);
//			System.arraycopy(resp_byte, 0, respHeaderLenght, 0, 2);
		} catch (IOException e) {
			Log.e("读取流异常",e.toString());
			throw new IOException("读取响应报文异常",e);
		}
		int size = ((respHeaderLenght[0] & 0xff) << 8)
				| (respHeaderLenght[1] & 0xff);
		

		respMsg = new byte[size];
		
		long k = 0;
		char c;
		int readCount = 0; // 已经成功读取的字节的个数
		while (readCount < size) {
			/** 一次性拿到 */
			try {
				readCount += is.read(respMsg, readCount, size - readCount);
			} catch (IOException e) {
//				log.error("读取流异常",e);
//				throw new CommunicationException("网络问题请重试");
			}
		}
		for (byte b : respMsg) {
			// convert byte to character
			if (b == 0)
				// if b is empty
				c = '-';
			else
				// if b is read
				c = (char) b;

			// prints character
			//				System.out.print(c);
		}
		if (k == -1) {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("关闭流异常",e.getMessage());
//				throw new CommunicationException("网络问题请重试");
			}
		}

		//			log.debug("\r 发送给交易平台的报文时间:\t"
		//					+ DateUtil.formatYearDateTime(new Date()));
		//			log.debug("接收到的交易平台报文16进制:\r" + ConvertUtil.trace(respMsg));


		return respMsg;
	}

	public static String format(Integer value, int length) {

		char[] c = new char[length];
		char[] x = Integer.toString(value).toCharArray();
		if (x.length > length) {
			throw new IllegalArgumentException(
					"Numeric value is larger than intended length: " + value
							+ " LEN " + length);
		}
		// 数字长度不足左补0
		int lim = c.length - x.length;
		for (int i = 0; i < lim; i++) {
			c[i] = '0';
		}
		System.arraycopy(x, 0, c, lim, x.length);
		return new String(c);
	}

	/* set get 方法 */

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setHeadlength(int headlength) {
		this.headlength = headlength;
	}

	public void setTPDUlength(int tPDUlength) {
		TPDUlength = tPDUlength;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public void setIsDynamic(String isDynamic) {
		this.isDynamic = isDynamic;
	}

	public void setMsgConfig(String msgConfig) {
		this.msgConfig = msgConfig;
	}

	public void setIsLinux(String isLinux) {
		this.isLinux = isLinux;
	}

	public void setParse(Parse parse) {
		this.parse = parse;
	}
}
