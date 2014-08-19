package com.dhcc.pos.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.dhcc.pos.packets.util.CommonUtil;
import com.dhcc.pos.packets.util.ConvertUtil;
import com.dhcc.pos.parse.CnFieldParseInfo;

public class Packet {
	CnMessage m = null;
	private String TPDU;
	private String msgHeader;

	private static Packet instance = null;

	private Packet() {
		this.TPDU = "6000040000";
		this.msgHeader = "603110000000"; 
	}

	public static synchronized Packet getInstance() {
		if (instance == null) {
			return new Packet();
		} else {
			return instance;
		}
	}
	/**
	 * @功能函数 注册请求报文(根据域值赋给相对应的消息工厂和消息类) _（该值从配置文件解析的值取得）：
	 * @功能描述 tpdu+头+cnValue（CnType+CnFormat+value+length）
	 * <blockquote>
	 * {该cnValue 在CnMessage类中以fieldId做key放进fields中}
	 * </blockquote>
	 * @param 	m	
	 * 			消息类
	 * @param	context
	 * 			上下文
	 * @return CnMessage
	 */
	public CnMessage registerReqMsg(CnMessageFactory mfact, Map<String,Object> req_map) {
		System.out.println("\t####################【RegisterReqMsg】####################" + "\r");
		String msgType = null;
		msgType = (String) req_map.get("msgType");
		//每次注册请求报文都要实例化消息类
		this.m = new CnMessage(msgType, TPDU.length(), msgHeader.length());


		Map<Integer, CnFieldParseInfo> parseMap = null;

		// 根据模板创建并初始化一个报文对象
		// m = mfact.newMessagefromTemplate(msgType);

		// 对于域不使用二进制
		//		m.setBinary(true);

		/**
		 * 设置TPDU的数据
		 * 在一个if-else判断中，如果我们程序是按照我们预想的执行，到最后我们需要停止程序，那么我们使用System.exit(0)，而System.exit(1)一般放在catch块中，当捕获到异常，需要停止程序，我们使用System.exit(1)。这个status=1是用来表示这个程序是非正常退出。
		 * */
		if (m.setMessageTPDUData(0, TPDU.getBytes()) == false) {
			String desc = "设置TPDU出错。";
			System.err.println(desc);
			System.exit(-1);
		}
		/**
		 * 设置报文头的数据
		 * */
		if (m.setMessageHeaderData(0, msgHeader.getBytes()) == false) {
			String desc = "设置报文头出错。";
			System.err.println(desc);
			System.exit(-1);
		}
		
		parseMap = mfact.getParseMap(msgType);
		if(parseMap == null){
			String desc = "交易码：【" + msgType + "】 配置报文中没有和该交易码相匹配的信息";
			throw new NullPointerException(desc);
		}
		Iterator<Integer> it = null;
		it = parseMap.keySet().iterator();
		/**为位图map赋值 
		 * 以fieldid做key ；new cnValue<Object>(cnFormat, value, length)/new cnValue<Object>(cnFormat, value)做value存入位图map（Map<Integer, cnValue<?>> fields）中
		 * 根据 解析的配置文件交易域（parseMap） 和 请求报文的交易域（req_Map） 为message赋值 
		 * 
		 * */
		// if(!req_map.isEmpty()){
		while (it.hasNext()) {
			/** =================变量声明================= */
			//			 字段 
			int fieldId = 0;
			//			 字段值 
			Object value = null;
			CnFieldParseInfo xFieldParseInfo = null;
			//			 数据格式 
			CnFormat cnFormat = null;
			//			 消息类型
			CnType cnType = null;
			boolean must = false;
			int length = -1;
			boolean addLen = false;
			String align = null;

			fieldId = it.next();

			if (null != parseMap.get(fieldId) && !parseMap.get(fieldId).equals("")) {

				xFieldParseInfo = parseMap.get(fieldId);

				cnFormat = xFieldParseInfo.getCnFormat();
				cnType = xFieldParseInfo.getCnType();
				length = xFieldParseInfo.getLength();
				must = xFieldParseInfo.isMust();
				addLen = xFieldParseInfo.isAddLen();
				align = xFieldParseInfo.getAlign();
				/**
				 * 屏蔽请求报文的无值域 req_map中不为空时有效
				 * 
				 * */
				if (req_map.get("field" + String.valueOf(fieldId)) != null && !req_map.get("field" + String.valueOf(fieldId)).equals("")) {
					value = req_map.get("field" + String.valueOf(fieldId));
					/** 当等于0时为变量域 故此拿请求过来的值求出大小 */
					if (cnFormat == CnFormat.AMOUNT) {
						m.setValue(fieldId,cnFormat,cnType, BigDecimal.valueOf(Double.parseDouble(value.toString()) / 100.00), xFieldParseInfo.getLength(), must, addLen, align);
					} else {
						if (xFieldParseInfo.getLength() != 0 && xFieldParseInfo.isMust() == true) {
							if (value == null || value.equals("")) {
								throw new IllegalArgumentException(String.valueOf(fieldId)+ " field is must input! ");
							} else if (value.toString().length() != xFieldParseInfo.getLength()) {
								if (value.toString().length() < xFieldParseInfo.getLength()) {
									throw new IllegalArgumentException(String.valueOf(fieldId) + " 域值 Too Short! ");
								} else {
									throw new IllegalArgumentException(String.valueOf(fieldId) + " 域值 Too Long! ");
								}
							}
							m.setValue(fieldId, cnFormat, cnType, value, xFieldParseInfo.getLength(), must, addLen, align);
						} else if (xFieldParseInfo.getLength() == 0 && xFieldParseInfo.isMust() == true) {
							if (value == null) {
								throw new IllegalArgumentException(String.valueOf(fieldId) + " 域 is must input! ");
							}
							m.setValue(fieldId, cnFormat, cnType, value, value.toString().length(), must, addLen, align);
						} else if (xFieldParseInfo.getLength() != 0 && xFieldParseInfo.isMust() == false) {
							if (value != null && !value.equals("null")) {
								if(!(cnType == CnType.BINARY)){
									if (value.toString().length() != xFieldParseInfo.getLength()) {
										if (value.toString().length() < xFieldParseInfo.getLength()) {
											throw new IllegalArgumentException(String.valueOf(fieldId) + " 域值 Too Short! ");
										} else {
											throw new IllegalArgumentException(String.valueOf(fieldId) + " 域值 Too Long!");
										}
									}
								}else{
									if (value.toString().length()/2 != xFieldParseInfo.getLength()) {
										if (value.toString().length() < xFieldParseInfo.getLength()) {
											throw new IllegalArgumentException(String.valueOf(fieldId) + " 域值 Too Short! ");
										} else {
											throw new IllegalArgumentException(String.valueOf(fieldId) + " 域值 Too Long!");
										}
									}
								}
							}else{
								value = "";
							}
							m.setValue(fieldId, cnFormat, cnType, value, xFieldParseInfo.getLength(), must, addLen, align);
						} else if (xFieldParseInfo.getLength() == 0 && xFieldParseInfo.isMust() == false) {
							if (value != null) {
								if (!value.toString().trim().equals(""))
									m.setValue(fieldId,cnFormat, cnType, value, value.toString().length(), must, addLen, align);
							}
						}
					}
				}
			} else {
				System.err.println("没有此'" + parseMap.get(fieldId) + "'的value");
			}
		}
		//打印请求报文
		print(m);

		return m;
	}

	/**
	 * 组装报文
	 * @param m 消息类
	 * @return reqMsg (tpdu+头文件+报文类型+位图+位图对应的域值)
	 */
	public byte[] packet(CnMessage m) {
		System.out.println("\t####################【Packet】####################" + "\r");
		this.m = m;

		/** 组装请求报文 */
		byte[] reqMsg = null;
		/** TPDU */
		byte[] msgTPDU = null;
		/** 头文件 */
		byte[] msgHeader = null;
		/** 报文类型 */
		byte[] msgtypeid = null;

		/**
		 * 进行BCD码压缩
		 * */
		msgTPDU = ConvertUtil.byte2bcd(this.m.getmsgTPDU());
		msgHeader = ConvertUtil.byte2bcd(this.m.getmsgHeader());
		msgtypeid = ConvertUtil.str2bcd(this.m.getMsgTypeID());

		/**
		 * data :位图[8字节]+ {所有域值（个别域值前加上BCD码压缩的2或者3个字节的长度值 并做左补零 或 右补零）}
		 * */
		byte[] data = depacketize();

		System.out.println("位图和域值长度:\t【" + data.length + "】");
		System.out.println("位图和域值:\t" + ConvertUtil.trace(data));

		/**
		 * 组装字节类型报文；（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+
		 * 报文类型【BCD压缩2字节】+位图【8字节】+ 位图对应的域值
		 * */
		ByteBuffer sendBuf = ByteBuffer.allocate(msgTPDU.length
				+ msgHeader.length + msgtypeid.length + data.length);
		/** TPDU */
		sendBuf.put(msgTPDU);
		/** 头文件 */
		sendBuf.put(msgHeader);
		/** 报文类型 */
		sendBuf.put(msgtypeid);
		/** 位图+位图对应的域值 */
		sendBuf.put(data);

		reqMsg = sendBuf.array();

		return reqMsg;
	}

	/**
	 * @功能函数 返回报文内容 ,不包含前报文总长度及结束符
	 * 
	 * @return byte[] 
	 * 			位图[8字节]+ 所有域值（个别域值前加上BCD码压缩的2个字节的长度值）
	 */
	public byte[] depacketize() {
		System.out.println("\t####################【Depacketize】####################" + "\r");

		String bitmapStr = null;
		ByteArrayOutputStream bArrayOut = new ByteArrayOutputStream();
		// Bitmap
		BitSet bs = new BitSet(64);
		/**储存所有域key值 该key值做了排序*/
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(this.m.fields.keySet());
		/*升序排序*/
		Collections.sort(keys);
		System.out.println("KEYS:[" + keys + "]");

		for (Integer i : keys) { // BitSet可以自动扩展大小
			/*为何-1*/
			bs.set(i - 1, true);
			/*个人改*/
			//			bs.set(i, true);
		}
		System.out.println("BS:[" + bs + "]");
		// Extend to 128 if needed
		if (bs.length() > 64) {
			BitSet b2 = new BitSet(128);
			b2.or(bs); // 得到位图(根据域的个数，可能自动扩展)
			bs = b2;
			/*当bs长度大于64时 设定第一位为true*/
			//			bs.set(0, true);
			/*个人改*/
			bs.set(0, true);
		}
		// Write bitmap into stream
		int pos = 128; // 用来做位运算： -- 1000 0000（初值最高位为1，然后右移一位，等等...）
		int b = 0; // 用来做位运算：初值二进制位全0
		//		System.out.println("_b:" + b);
		for (int i = 0; i < bs.size(); i++) {
			//			System.out.println("i:" + i);
			if (bs.get(i)) {
				//				System.out.println("pos1:" + pos);
				b |= pos;
				//				System.out.println("pos2:" + pos);
				//				System.out.println("b:" + b);
			}
			//			System.out.println("pos3——:" + pos);
			/*每次右移一位该值除以2*/
			pos >>= 1;
		//			System.out.println("pos3:" + pos);
		// 到一个字节时（8位），就写入
		if (pos == 0) { 
			bArrayOut.write(b);
			pos = 128;
			b = 0;
		}
		}
		System.out.println("位图长度:\t【" + bArrayOut.toByteArray().length + "】\r十六进制位图：\r"+ConvertUtil.trace(bArrayOut.toByteArray()));
		/**位图以16进制格式显示*/
		bitmapStr = ConvertUtil.bytes2HexString(bArrayOut.toByteArray());
		System.out.println("BITMAP： \t【" + bitmapStr + "】");
		
		/**位图以二级制格式显示*/
		String BbitmapStr = ConvertUtil.hexString2binaryString(bitmapStr);
		String BbitmapStrFormat = CommonUtil.str2FormatStr(BbitmapStr);
		System.out.println("二进制位图：\t" + BbitmapStrFormat);

		/**Fields
		 * 紧跟着位图后面 位图所有域的值 
		 * 左补零可用函数 System.format("%04d",2) 第二个参数超过4位的话 那么此函数无效；
		 * */ 
		/*以keys的key值从fields中拿到相对应的数据(CnValue) */
		for (Integer i : keys) {
			CnValue<?> cnValue = m.fields.get(i);
			/**当i不等于该域时 证明该域需要加长度值*/
			if(cnValue.getIsAddLen() && cnValue.getCnFormat()==CnFormat.LLVAR){
				int length = cnValue.getValue().toString().length();
				byte[] byteFieldLLVAR = ConvertUtil.str2bcd(String.format("%02d", length));

				try {
					bArrayOut.write(byteFieldLLVAR);
				} catch (IOException e) {
					System.err.println("写入数据时发生IO异常");
					System.err.println(e.getMessage());
				}
			}else if(cnValue.getIsAddLen() && cnValue.getCnFormat()==CnFormat.LLLVAR){
				int length = cnValue.getValue().toString().length();
				byte[] byteFieldLLLVAR = ConvertUtil.str2bcd(String.format("%04d", length));

				try {
					bArrayOut.write(byteFieldLLLVAR);
				} catch (IOException e) {
					System.err.println("写入数据时发生IO异常");
					System.err.println(e.getMessage());
				}
			}
			try {
				cnValue.write(bArrayOut,i);
			} catch (IOException ex) {
				// should never happen, writing to a ByteArrayOutputStream
			}
		}
		return bArrayOut.toByteArray();
	}
	// 输出一个报文内容
	private void print(CnMessage m) {
		System.out.println("----------------------------------------------------- "
				+ m.getField(11));
		System.out.println("Message TPDU = \t[" + new String(m.getmsgTPDU()) + "]");
		System.out.println("Message Header = \t[" + new String(m.getmsgHeader()) + "]");
		System.out.println("Message TypeID = \t[" + m.getMsgTypeID() + "]");
		m.hasField(1);
		for (int i = 2; i < 128; i++) {
			//					log.info("Field: " + i 
			//							+ " <" + m.getField(i).getCnFormat()
			//							+ ">\t "
			//							+ "^" + m.getField(i).getCnType() +"^   \t" 
			//							+ "(" + m.getField(i).getLength() + ")\t"
			//							+ "[" + m.getField(i).toString() + "]" + "      \t"
			//							+ "[" + m.getObjectValue(i) + "]");
			//				}
			if (m.hasField(i)) {
				System.out.println("\tField=【" + i + "】\t"
						+ "v  " + m.getField(i).getCnType() + "  v\t"
						+ "< " + m.getField(i).getCnFormat() + " >\t"
						+ "(" + m.getField(i).getLength() + ")\t"
						+ "[" + m.getField(i).toString() + "]\t"
						+ "--->\t"
						+ "[" + m.getObjectValue(i) + "]");
			}
		}
	}
	
	/**set get 函数*/
	public void setTPDU(String tPDU) {
		TPDU = tPDU;
	}

	public void setMsgHeader(String msgHeader) {
		this.msgHeader = msgHeader;
	}
}