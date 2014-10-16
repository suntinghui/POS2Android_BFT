package com.dhcc.pos.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
	int port;
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
	
	
	
	
	private Thread td;// 线程，获取服务器端发送来的消息

    private String workStatus;// 当前工作状况，null表示正在处理，success表示处理成功，failure表示处理失败

    private String currAction; //标记当前请求头信息，在获取服务器端反馈的数据后，进行验证，以免出现反馈信息和当前请求不一致问题。比如现在发送第二个请求，但服务器端此时才响应第一个请求

	public static synchronized SocketTransport getInstance(){
		if(instance == null)
			instance = new SocketTransport();
		return instance;
	}
	
	SocketTransport() {
//		佰付通
//		this.host = "114.80.227.152";
		this.host = "www.payfortune.com";
		this.port = 2003; 
//		
		//公司网控器
//		this.host = "192.168.4.62";
//		this.port = 60000; 
		
		//优乐通
//		this.host = "61.132.75.110";
//		this.port = 9999; 
		
		//众付宝
//		this.host = "192.168.4.234";
//		this.port = 8088; 
		
		
		
		
		this.timeout = 50000;
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
	
	private void connectService(){
		try {
			// 获取一个连接到socket服务的套接字
			socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
			socket.connect(socketAddress, timeout);

			is = socket.getInputStream();
			out = socket.getOutputStream();

//			out.write(sendMsg);
//			out.flush();

		} catch (SocketException e) {
			Log.e("QLQ", e.getMessage());
		} catch (SocketTimeoutException e) {
			Log.e("QLQ", e.getMessage());
		} catch (IOException e) {
			Log.e("发生IO异常", e.getMessage());
		}
	}
	/**
     * 向服务器发送传入的byte数组数据信息
     * 
     * @param sendMsg
     */
    private void sendMessage(byte[] sendMsg) {
        if (socket == null)// 如果未连接到服务器，创建连接
            connectService();
        
        // isConnected（）返回的是是否曾经连接过，isClosed()返回是否处于关闭状态，只有当isConnected（）返回true，isClosed（）返回false的时候，网络处于连接状态
        if (!socket.isConnected() || (socket.isClosed())) 
        {
            Log.v("QLQ", "workStatus is not connected!");
            for (int i = 0; i < 3 && workStatus == null; i++) {// 如果连接处于关闭状态，重试三次，如果连接正常了，跳出循环
                socket = null;
                connectService();
                if (socket.isConnected() && (!socket.isClosed())) {
                    Log.v("QLQ", "workStatus is not connected!");
                    break;
                }
            }
            if (!socket.isConnected() || (socket.isClosed()))// 如果此时连接还是不正常，提示错误，并跳出循环
            {
                workStatus = Constant.TAG_CONNECT_FAILURE;
                Log.v("QLQ", "workStatus is not connected!");
                return;
            }

        }

        if (!socket.isOutputShutdown()) {// 输入输出流是否关闭
            try {
            	out.write(sendMsg);
            }catch(Exception e) {
                // TODO Auto-generated catch block
                Log.v("QLQ", "workStatus is not connected!55555666666");
                e.printStackTrace();
                workStatus = Constant.TAG_CONNECT_FAILURE;
            }
        } else {
            workStatus = Constant.TAG_CONNECT_FAILURE;
        }
    }


	public byte[] sendData(byte reqMsg[]) throws IOException{
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
//			// 获取一个连接到socket服务的套接字
//			socket = new Socket(host, 2003);
//			
//			/** 设置超时时间 */
//			socket.setSoTimeout(timeout);
//			is = socket.getInputStream();
//			out = socket.getOutputStream();
//			out.write(reqMsg);
//			//清除out数据
//			out.flush();
//			
			connectService();
			
			out.write(reqMsg);

//			//清除out数据
			out.flush();

			respMsg = revData(respHeaderLenght, is);
//			Log.i("接收到的交易平台报文时间:\t【"+ DateUtil.formatYearDateTime(new Date()) + "】");
			System.out.println("接收到的交易平台报文16进制:\r" + ConvertUtil.trace(respMsg));
		} catch (ConnectException e) {
			System.out.print("无法连接到地址:"+e);
//			throw new ConnectException("网络问题请重试");
			throw new SocketException("网络问题请重试");
		} catch (SocketException e) {
			System.out.print("socket协议有误:"+e);
//			log.error("socket协议有误",e);
//			throw new CommunicationException("网络问题请重试");
			throw new SocketException("网络问题请重试");
		} catch (IOException e) {
			System.out.print("发生IO异常:"+e+"\n");
//			log.error("发生IO异常",e);
			throw new IOException("网络问题请重试");
		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//					Log.e("关闭流发生IO异常",e.getMessage());
////					throw new CommunicationException("网络问题请重试");
//				}
//			}
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//					Log.e("关闭流发生IO异常",e.getMessage());
////					throw new CommunicationException("网络问题请重试");
//				}
//			}
			if (socket != null && socket.isConnected()) {
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
//					socket.close();
				} catch (IOException e) {
					Log.e("关闭流发生IO异常",e.getMessage());
					throw new IOException("网络问题请重试");
				}
			}
		}
		return respMsg;
	}
	
	
	
	/**
	 * @param respHeaderLenght
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public byte[] revData(byte[] respHeaderLenght, InputStream is) throws IOException {
		respMsg = null;
		try {
			respHeaderLenght = ConvertUtil.inputStreamToBytes(is, respHeaderLenght.length);
		} catch (IOException e) {
			throw new IOException("读取响应报文长度异常",e);
		}
		int size = ((respHeaderLenght[0] & 0xff) << 8)
				| (respHeaderLenght[1] & 0xff);
		
		respMsg = new byte[size];
		
		respMsg = ConvertUtil.inputStreamToBytes(is, size);

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
