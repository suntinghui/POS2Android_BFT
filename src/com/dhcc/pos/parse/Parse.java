package com.dhcc.pos.parse;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import com.dhcc.pos.packets.CnFormat;
import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;
import com.dhcc.pos.packets.CnType;
import com.dhcc.pos.packets.CnValue;
import com.dhcc.pos.packets.util.CommonUtil;
import com.dhcc.pos.packets.util.ConvertUtil;

public class Parse {
	private static Parse instance = null;
	
	private CnMessageFactory mfact = null;
	
	public static synchronized Parse getInstance(){
		if(instance == null)
			instance = new Parse();
		return instance;
	}
	
	/**
	 * 通过字节数组来创建消息
	 * @param respMsg 8583 响应消息字节数组【交易类型+位图+】
	 * @param msgTPDUlength TPDU长度
	 * @param msgheaderlength 报文长度
	 * @return
	 */
	public CnMessage parse(CnMessageFactory mfact, byte[] respMsg,int msgTPDUlength, int msgHeaderLength){
		System.out.println("\t####################【ParseMessage】####################" + "\r");
		if(mfact == null){
			this.mfact = CnMessageFactory.getInstance();
		}else{
			this.mfact = mfact;
		}
		CnMessage m = null;
		byte[] msgTypeByte = new byte[2];
		/**消息类型*/
		String msgType = null;
		String TPDU = null;
		String msgHeader = null;
		/**位图起止位置*/
		int bitmapStart = -1;
		/**位图结束位置*/
		int bitmapEnd = -1;
		/**
		 * 拿到消息类型  
		 * */
		System.arraycopy(respMsg, msgTPDUlength/2+msgHeaderLength/2, msgTypeByte, 0, 2);
		
		msgType = ConvertUtil.bcd2str(msgTypeByte);
		
		/**
		 * 每次解包都必须要实例化消息工厂
		 * */
		m = new CnMessage(msgType,msgTPDUlength,msgHeaderLength);
		System.out.println("respMsg msgType: \t[" + m.getMsgTypeID() + "]");

		/**
		 * 得到TPDU信息 由于报头为bcd压缩故此除2
		 * */ 
		msgTPDUlength = msgTPDUlength/2;
		byte[] msgTPDUData = new byte[msgTPDUlength];
		System.arraycopy(respMsg, 0, msgTPDUData, 0, msgTPDUlength);
		TPDU = ConvertUtil._bcd2Str(msgTPDUData);
		/**
		 * 设置TPDU的数据
		 * */
		if (m.setMessageTPDUData(0, TPDU.getBytes()) == false) {
			System.err.println("设置TPDU出错。");
			System.exit(-1);
		}
		
		/**
		 * 得到报文头信息 由于报头为bcd压缩故此除2
		 * */ 
		msgHeaderLength = msgHeaderLength/2;
		byte[] msgHeaderData = new byte[msgHeaderLength];
		System.arraycopy(respMsg, msgTPDUlength, msgHeaderData, 0, msgHeaderLength);
		msgHeader = ConvertUtil._bcd2Str(msgHeaderData);
		/**
		 * 设置报文头的数据
		 * */
		if (m.setMessageHeaderData(0, msgHeader.getBytes()) == false) {
			System.err.println("设置报文头出错。");
			System.exit(-1);
		}
		
		System.out.println("respMsg TPDU: \t\t[" + new String(m.getmsgTPDU()) + "]");
		System.out.println("respMsg Hearder: \t[" + new String(m.getmsgHeader()) + "]");
		
		/**
		 * 位图
		 */
		byte[] bitmap = new byte[8];
		
		System.arraycopy(respMsg, msgTPDUlength + msgHeaderLength+msgTypeByte.length, bitmap, 0, bitmap.length);
		/**位图以16进制格式显示*/
		String bitmapStr = ConvertUtil.bytes2HexString(bitmap);
		System.out.println("BITMAP: \t【" + bitmapStr + "】");

		/**位图以二级制格式显示*/
		String BbitmapStr = ConvertUtil.hexString2binaryString(bitmapStr);
		String BbitmapStrFormat = CommonUtil.str2FormatStr(BbitmapStr);
		System.out.println("二进制位图：\t" + BbitmapStrFormat);
		
		// Parse the bitmap (primary first)
		BitSet bs = new BitSet(64);
		int pos = 0;
		
		bitmapStart = msgTPDUlength + msgHeaderLength + msgTypeByte.length;
		bitmapEnd = msgTPDUlength + msgHeaderLength + msgTypeByte.length+8;
		
		for (int i = bitmapStart; i < bitmapEnd; i++) {
			int bit = 128;
			for (int b = 0; b < 8; b++) {
				/**(respMsg[i] & bit) != 0 时此域为有效域*/
				bs.set(++pos, (respMsg[i] & bit) != 0);
				/**右移一位代表初始值128/2的值*/
				bit >>= 1;
			}
		}
	
		/**
		 * 当bs.get(0)为true时代表有扩展位图此为128位 16个字节；
		 * */
		// Check for secondary bitmap and parse if necessary
		if (bs.get(0)) {
			for (int i = bitmapStart + 8 ; i < bitmapStart + 8*2; i++) {
				int bit = 128;
				for (int b = 0; b < 8; b++) {
					bs.set(++pos, (respMsg[i] & bit) != 0);
					/**右移一位代表初始值除2的值*/
					bit >>= 1;
				}
			}
			/**tpdu长+头文件长度+消息类型+位图16（当位图第0位为true时 代表是128位位图，故此占16个字节）*/
			pos = bitmapStart + 8*2;
			bitmapEnd = bitmapEnd + 8;
		} else {
			/**tpdu长+头文件长度+消息类型+位图8（当位图第0位为false时 代表是64位位图，故此占8个字节）*/
			pos = bitmapEnd;
		}
		
		System.out.println("\t收到位图域: \t"+bs.toString());
		
		/**=====打印位图=====*/
		// 将BitSet中的位图格式化成二进制形式和域ID数字形式
				/*78代表此中加入了14个空格（每8位两个空格）*/
			/*	int bit_str_length = bs.length() <= 64 ? 64 : 158;
				log.debug("bit_str_length:\t" + bit_str_length);
				StringBuffer bitmap_binstr = new StringBuffer(bit_str_length); // 每8个二进制位串之间用2个空格隔开
				StringBuffer bitmap_str = new StringBuffer("{");
//				log.debug("bs:\t" + bs);
				for(int i = 1; i <= bit_str_length; i++) {
					if(bs.get(i-1))  {
						bitmap_binstr.append("1");	
						bitmap_str.append(i).append(", ");
					}else{ 
						bitmap_binstr.append("0");
					}
						if((i)%8==0)
							bitmap_binstr.append("  ");
					
				
//					if( i %10 == 7 && i < bit_str_length - 1 ) {
//						bitmap_binstr.append("  ");
//						i += 2;
//					}
				}
				if(bitmap_str.substring(bitmap_str.length() - 2).equals(", ")) 
					bitmap_str.delete(bitmap_str.length() - 2, bitmap_str.length());
				bitmap_str.append("}");
				log.debug("bitmap data(binary format): [" + bitmap_binstr + "]");
			//	log.debug("parsed bitmap(0-127): [" + bs.toString() + "]");
				log.debug("parsed bitmap(1-128): [" + bitmap_str + "]");
				*/
		/**==========*/
		
		//Parse each field
		List<Integer> index = null;
		if(this.mfact.getParseOrder(m.getMsgTypeID()) == null) {
			RuntimeException e = new RuntimeException("在XML文件中未定义报文类型[" + m.getMsgTypeID() + "]的解析配置, 无法解析该类型的报文,建议检查或完善XML配置文件!");
			System.err.println(e.getMessage());
			throw e;
			/**或者*/
//			log.error("在XML文件中未定义报文类型[" + m.getMsgTypeID() + "]的解析配置, 无法解析该类型的报文, 建议检查或完善XML配置文件!");
//			System.exit(-1);
		}
		index = this.mfact.getParseOrder(m.getMsgTypeID());	// 该类型报文应该存在的域ID集合
		System.out.println("\t系统配置中的位图域:\t" + index);
		
		// 检查2到128，如果收到的报文位图中指示有此域，而XML配置文件中确未指定，则报警告！
		for(int fieldnum = 2; fieldnum <= 128; fieldnum++){
			if(bs.get(fieldnum )) {	
				if(! index.contains(Integer.valueOf(fieldnum))) { // 不包含时
					System.err.println("收到类型为["  + m.getMsgTypeID() 
							+ "]的报文中的位图指示含有第[" + fieldnum 
							+ "]域,但XML配置文件中未配置该域. 这可能会导致解析错误,建议检查或完善XML配置文件！");
				}
			}
		}
		Map<Integer, CnFieldParseInfo> parseGuide = this.mfact.getParseMap(m.getMsgTypeID());
		for (Integer i : index) {
			CnFieldParseInfo fpi = parseGuide.get(i);
			if (bs.get(i)) {
				CnValue<? extends Object> cnValue = null;
				
				cnValue = fpi.parse(respMsg, pos, i);

				m.setField(i, cnValue);
				System.out.println("\tField=【" + i + "】\t"
						+ "v  " + m.getField(i).getCnType() + "  v\t"
						+ "< " + m.getField(i).getCnFormat() + " >\t"
						+ "^" + pos + "^\t"
						+ "(" + m.getField(i).getLength() + ")\t"
						+ "[" + m.getField(i).toString() + "]\t"
						+ "--->\t"
						+ "[" + m.getObjectValue(i) + "]");
				/***/
				
					
				/***/
				if (cnValue.getCnType() == CnType.BCD && (cnValue.getCnFormat() == CnFormat.LLVAR || cnValue.getCnFormat() == CnFormat.LLLVAR) || cnValue.getCnFormat() == CnFormat.DATE10 || cnValue.getCnFormat() == CnFormat.DATE4 || cnValue.getCnFormat() == CnFormat.DATE_EXP || cnValue.getCnFormat() == CnFormat.TIME || cnValue.getCnFormat() == CnFormat.AMOUNT || cnValue.getCnFormat() == CnFormat.NUMERIC) {
					pos += (cnValue.getLength() / 2) + (cnValue.getLength() % 2);
				} else {
					pos += cnValue.getLength();
				}
				
				if (cnValue.getIsAddLen() == true && cnValue.getCnFormat() == CnFormat.LLVAR) {
					pos += 1;
				} else if (cnValue.getIsAddLen() == true && cnValue.getCnFormat() == CnFormat.LLLVAR) {
					pos += 2;
				}
			}
		}
//		输出一个报文内容
//		print(m);
		return m;
	}
	// 输出一个报文内容
	private void print(CnMessage m) {
		System.out.println("----------------------------------------------------- "
				+ m.getField(11));
		System.out.println("Message TPDU = \t[" + new String(m.getmsgTPDU()) + "]");
		System.out.println("Message Header = \t[" + new String(m.getmsgHeader()) + "]");
		System.out.println("Message TypeID = \t[" + m.getMsgTypeID() + "]");
		m.hasField(1);
		for(int i=2; i<128; i++){
			if (m.hasField(i)) {
				System.out.println("\tField=【" + i + "】\t"
						+ "v  " + m.getField(i).getCnType() + "  v\t"
						+ "< " + m.getField(i).getCnFormat() + " >\t"
						//				+ "^" + pos + "^\t"
						+ "(" + m.getField(i).getLength() + ")\t"
						+ "[" + m.getField(i).toString() + "]\t"
						+ "--->\t"
						+ "[" + m.getObjectValue(i) + "]");
			}
		}
	}
}