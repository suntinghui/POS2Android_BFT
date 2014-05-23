package com.dhcc.pos.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dhcc.pos.packets.util.ConvertUtil;

/**
 * iso 8583 消息信息 报文总长度（2-4字节）+报头信息+报文类型+位图（8（64位图）或16（128位图））+各字段域+结束符
 */
public class CnMessage {
	private static CnMessage instance = null;
	
	public String bitMap = null;

	/** 消息类型 */
	private String msgtypeid;

	/** 如果设置为true, 报文中的各报文域按照二进制组成报文 */
	private boolean isbinary;

	/** bit map 位图 */
	public Map<Integer, CnValue<?>> fields = new ConcurrentHashMap<Integer, CnValue<?>>();

	/**
	 * 报头信息
	 */
	private byte[] msgTPDU;

	/**
	 * 报头信息
	 */
	private byte[] msgHeader;

	public CnMessage() {

	}
	public static synchronized CnMessage getInstance(){
		if(instance == null){
			instance = new CnMessage();
		}
		return instance;
	}

	/**
	 * 创建 一个指定类型的8583消息
	 * 
	 * @param msgtypeid
	 *            消息类型
	 * @param msgTPDUlength ,msgHeaderlength
	 */
	public CnMessage(String msgtypeid,int msgTPDUlength, int msgHeaderlength) {
		this.msgtypeid = msgtypeid;
		
		/*赋给msgHeader的容量*/
		msgHeader = new byte[msgHeaderlength];
		
		/*赋给msgTPDU的容量*/
		msgTPDU = new byte[msgTPDUlength];
	}

	
	/** 设置消息类型. 应该为4字节字符串 */
	public void setMsgTypeID(String msgtypeid) {
		this.msgtypeid = msgtypeid;
	}

	/**  获取消息类型. */
	public String getMsgTypeID() {
		return msgtypeid;
	}

	/**
	 * 设置报文头的数据，由于不同的报文报文的格式完全不同，所以直接设置报文的字节数据。
	 * 
	 * @param startindex
	 *            待设置报文头的起始字节位置。（0为第一个位置）
	 * @param data
	 *            要设置的数据，（长度为data的长度，startindex和data的长度的和应小于报文头的总长度）
	 * @return 是否设置成功
	 */
	public boolean setMessageHeaderData(int startindex, byte[] data) {
		if (startindex + data.length > msgHeader.length) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			msgHeader[startindex + i] = data[i];
		}
		return true;
	}

	public boolean setMessageTPDUData(int startindex, byte[] data) {
		if (startindex + data.length > msgTPDU.length) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			msgTPDU[startindex + i] = data[i];
		}
		return true;
	}
	
	/** 获取报头信息 */
	public byte[] getmsgHeader() {
		return msgHeader;
	}

	/** 获取TPDU信息 */
	public byte[] getmsgTPDU() {
		return msgTPDU;
	}
	
	/**
	 * 从报文头中取得数据
	 * 
	 * @param startindex
	 *            起始字节位置（0为第一个位置，应小于报文头的总长度）
	 * @param count
	 *            需要取得的字节数（正整数） 如遇到报文尾，则取得实际能取道的最大字节数
	 * @return 取得的数据（如未取得则返回null）
	 */
	public byte[] getMessageHeaderData(int startindex, int count) {
		if (startindex >= msgHeader.length) {
			return null;
		}
		byte[] b = null;
		if (msgHeader.length - startindex < count)
			b = new byte[msgHeader.length - startindex];
		else
			b = new byte[count];
		for (int i = 0; i < b.length; i++) {
			b[i] = msgHeader[startindex + i];
		}
		return b;
	}

	/**
	 * 设置各报文域是否按照料二进制组成报文,  默认false
	 * 如果设置为true, 报文中的各报文域按照二进制组成报文。(报文头、报文类型标示和位图不受影响)
	 */
	public void setBinary(boolean flag) {
		isbinary = flag;
	}

	/**
	 * 
	 * 报文中的各报文域按照二进制组成报文。(报文头、报文类型标示和位图不受影响)
	 */
	public boolean isBinary() {
		return isbinary;
	}

	/**
	 * 返回字段域数值（ 应该在2-128范围，1字段域用来存放位图）
	 * @param fieldid 字段域id 
	 * @return
	 */
	public Object getObjectValue(int fieldid) {
		CnValue<?> cnValue = fields.get(fieldid);
		if (cnValue == null) {
			return null;
		}
		return cnValue.getValue();
	}

	/**
	 * 返回字段域数值（ 应该在2-128范围，1字段域用来存放位图）
	 * @param fieldid 字段域id 
	 * @return
	 */
	public CnValue<?> getField(int fieldid) {
		return fields.get(fieldid);
	}

	/**
	 * 设置字段域，由于字段域1被 用来存放位图，设置字段域应从2开始
	 * @param fieldid 字段域id 
	 * @param field 字段数值
	 */
	public void setField(int fieldid, CnValue<?> field) {
		if (fieldid < 2 || fieldid > 128) {
			throw new IndexOutOfBoundsException(
					"Field index must be between 2 and 128");
		}
		if (field == null) {
			fields.remove(fieldid);
		} else {
			fields.put(fieldid, field);
		}
	}

	/**
	 * 设置字段域，由于字段域1被 用来存放位图，设置字段域应从2开始
	 * 以fieldid做key ；cnValue（cnFormat, type,value、length）做value存入位图map（Map<Integer, cnValue<?>> fields）中
	 * @param fieldid 字段域id 
	 * @param value  数值
	 * @param t  类型
	 * @param length 长度
	 */
	public void setValue(int fieldid, CnFormat cnFormat, CnType cnType, Object value, int length, boolean must, boolean addLen, String align) {
		CnValue<?> cnValue = null;
		
		if (fieldid < 2 || fieldid > 128) {
			throw new IndexOutOfBoundsException(
					"Field index must be between 2 and 128");
		}
		if (value == null) {
			fields.remove(fieldid);
		} else {
//			if (cnFormat.needsLength()) {
				cnValue = new CnValue<Object>(cnFormat,cnType, value, length, must, addLen, align);
//			} else {
//				cnValue = new cnValue<Object>(cnFormat, cnType, value);
//			}
			fields.put(fieldid, cnValue);
		}
	}

	/**
	 * 是否存在该字段
	 * @param fieldid
	 * @return
	 */
	public boolean hasField(int fieldid) {
		return fields.get(fieldid) != null;
	}

	/**
	 * 根据当前的报文内容，估计最终报文的的长度（单位为字节）
	 * 
	 * @return 估算出来的报文字节个数（含报文头、报文类型标示、位图和各个有效的报文域）
	 */
	public int estimatetotalmsglength() {
		int totalmsglen = 0;
		// 报文头长度
		if (msgHeader != null) 
			totalmsglen += msgHeader.length;
		
		//报文类型标示长度
		if (msgtypeid != null) // 报文类型标示
			totalmsglen += msgtypeid.length();

		// 位图
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(fields.keySet());
		Collections.sort(keys);
		if (keys.get(keys.size() - 1) <= 64) // 如果最大的一个域ID小于等于64
			totalmsglen += 8;
		else
			totalmsglen += 16;

		// 报文域
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for (Integer i : keys) {
			CnValue<?> cnValue = fields.get(i);
			try {
				cnValue.write(bout,i);
			} catch (IOException ex) {
				// should never happen, writing to a ByteArrayOutputStream
			}
		}
		totalmsglen += bout.toByteArray().length;
		return totalmsglen;
	}
}