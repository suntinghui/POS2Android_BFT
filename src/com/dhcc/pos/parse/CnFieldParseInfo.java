package com.dhcc.pos.parse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.dhcc.pos.packets.CnFormat;
import com.dhcc.pos.packets.CnType;
import com.dhcc.pos.packets.CnValue;
import com.dhcc.pos.packets.util.ConvertUtil;

/***
 * 解析字段域信息
 * 
 * @author maple 
 * 参考：此类中参数为配置文件中的<field id="3" datatype="NUMERIC" length="6" />
 */
public class CnFieldParseInfo {
	
	// 消息类型
	private CnType cnType;
	// 消息格式
	private CnFormat cnFormat;
	// 消息长度
	private int length;
	// 是否必输项
	private boolean must;
	/**没有添加长度的字段 默认为：false ； 该添加长度是组装报文时 是否添加域值长度*/
	private boolean addLen;
	/**对齐	：当类型为BCD码时进行左对齐（右补零）和右对齐（左补零）*/
	private String align;

	/**创建字段解析器
	 * @param cnFormat	字段类型 
	 * @param cnType
	 * @param length	字段类型为ALPHA或NUMERIC必须设置此长度
	 * @param must
	 * @param addLen
	 * @param align	对齐	：当类型为BCD码时进行左对齐（右补零）和右对齐（左补零）
	 */
	public CnFieldParseInfo(CnFormat cnFormat, CnType cnType, int length, boolean must, boolean addLen, String align) {
		if (cnFormat == null) {
			throw new IllegalArgumentException("cnFormat cannot be null");
		}else if(cnType == null){
			throw new IllegalArgumentException("cnType cannot be null");
		}

		this.cnFormat = cnFormat;
		this.length = length;
		this.must = must;
		this.cnType = cnType;
		this.addLen = addLen;
		this.align = align;
	}

	/**
	 * 解析
	 * 
	 * @param buf
	 *            字节数组报文
	 * @param pos
	 *            该buf中第x位
	 * @param field
	 *            字段
	 * @return
	 * @throws ParseException
	 */
	public CnValue<?> parse(byte[] buf, int pos, int field) {
		//字节类型的变量的长度
		byte[] fieldLength = null;
		//转换后变量的长度
		String len = null;
		//转换后的域值
		String value = null;

//		ConvertUtil.trace(buf);

		if (cnFormat == CnFormat.ALPHA) {
			return new CnValue<String>(cnFormat, cnType, new String(buf, pos, length), length, must, addLen, align);
		} else if (cnFormat == CnFormat.NUMERIC) {
			byte[] data = new byte[(length % 2 == 0) ? (length / 2) : (length / 2 + length % 2)];

			System.arraycopy(buf, pos, data, 0, (length % 2 == 0) ? (length / 2) : (length / 2 + length % 2));

			if(align != null && align.equalsIgnoreCase("left") && cnType == CnType.BCD){
				value = ConvertUtil._bcd2Str(data);
			}else if(align != null && align.equalsIgnoreCase("right") && cnType == CnType.BCD){
				value = ConvertUtil.bcd2str_(data);
			}else if(align == null && cnType == CnType.BCD){
				//					value = new String(buf, pos, length);
				value = ConvertUtil.bcd2str(data);
			}else if(cnType == CnType.ASCII){
				value = ConvertUtil.byte2String(data, Charset.forName("UTF-8"));
			}else if(cnType == CnType.BINARY){
				value = ConvertUtil.bytesToHexString(data);
			}
			return new CnValue<Number>(CnFormat.NUMERIC, cnType, new BigInteger(value), length, must, addLen, align);
		} else if (cnFormat == CnFormat.AMOUNT) {
			byte[] digits = new byte[6];

			System.arraycopy(buf, pos, digits, 0, 6);
			value = ConvertUtil.bcd2str(digits);

//			System.out.println(String.format("pos: %d, length: %d", pos,
//					length));

			// return new cnValue<BigDecimal>(cnType.AMOUNT, new BigDecimal(value));
			return new CnValue<BigDecimal>(CnFormat.AMOUNT, cnType, new BigDecimal(Double.parseDouble(value.toString()) / 100.00),length, must, addLen, align);
		} else if (cnFormat == CnFormat.LLVAR) {
			//				length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);
			//				String value = new String(buf, pos + 1,length);

			fieldLength = new byte[1];

			System.arraycopy(buf, pos, fieldLength, 0, 1);
			len = ConvertUtil.bcd2str(fieldLength);
			
			System.out.println(ConvertUtil.trace(fieldLength));
			
			length = Integer.parseInt(len);
			int tempLen = length;
			//				System.out.println(String.format("pos: %d, length: %d", pos,length));
			byte[] data = null;
			if(cnType == CnType.BCD){
				data = new byte[(length%2 == 0) ? (length/2) : (length/2 + length%2)];
				length = length/2 + length%2;
			}else{
				data = new byte[length];
			}

			System.arraycopy(buf, pos + 1, data, 0, length);
			if(cnType == CnType.BCD){
				if(tempLen%2 == 0){
					value = ConvertUtil.bcd2str(data);
				}else{
					if(align!=null){
						if(align.equalsIgnoreCase("left")){
							value = ConvertUtil._bcd2Str(data);
						}else if(align.equalsIgnoreCase("right") ){
							value = ConvertUtil.bcd2str_(data);
						}
					}
				}
			}else if(cnType == CnType.ASCII){
				value = ConvertUtil.byte2String(data, Charset.forName("UTF-8"));
			}else if(cnType == CnType.BINARY){
				value = ConvertUtil.bytes2HexString(data);
			}
			return new CnValue<String>(cnFormat, cnType, value,tempLen, must, addLen, align);
		} else if (cnFormat == CnFormat.LLLVAR) {
			fieldLength = new byte[2];

			System.arraycopy(buf, pos, fieldLength, 0, 2);

			len = ConvertUtil.bcd2str(fieldLength);
			System.out.println(ConvertUtil.trace(fieldLength));
			length = Integer.parseInt(len);
			int tempLen = length;
			byte[] data = null;
			if(cnType == CnType.BCD){
				data = new byte[(length%2 == 0) ? (length/2) : (length/2 + length%2)];
				length =  length/2 + length%2;
			}else{
				data = new byte[length];
			}
			System.arraycopy(buf, pos + 2, data, 0, length);

			if(cnType == CnType.BCD){
				if(tempLen%2 == 0){
					value = ConvertUtil.bcd2str(data);
				}else{
					if(align!=null){
						if(align.equalsIgnoreCase("left")){
							value = ConvertUtil._bcd2Str(data);
						}else if(align.equalsIgnoreCase("right") ){
							value = ConvertUtil.bcd2str_(data);
						}
					}else{
						System.err.println("field:【" + field +  "】配置报文中未指明靠齐。");
						System.exit(-1);
					}
				}
			}else if(cnType == CnType.ASCII){
				value = ConvertUtil.byte2String(data, Charset.forName("UTF-8"));
			}else if(cnType == CnType.BINARY){
				value = ConvertUtil.bytes2HexString(data);
			}
			return new CnValue<String>(cnFormat, cnType, value, tempLen, must, addLen, align);
		} else if (cnFormat == CnFormat.DATE10 || cnFormat == CnFormat.DATE4
				|| cnFormat == CnFormat.DATE_EXP || cnFormat == CnFormat.TIME) {

			int[] tens = new int[(cnFormat.getLength() / 2)
			                     + (cnFormat.getLength() % 2)];
			int start = 0;
			for (int i = pos; i < pos + tens.length; i++) {
				tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
			}
			Calendar cal = Calendar.getInstance();
			if (cnFormat == CnFormat.DATE10) {
				cal.set(Calendar.MONTH, tens[0] - 1);
				cal.set(Calendar.DATE, tens[1]);
				cal.set(Calendar.HOUR_OF_DAY, tens[2]);
				cal.set(Calendar.MINUTE, tens[3]);
				cal.set(Calendar.SECOND, tens[4]);
				if (cal.getTime().after(new Date())) {
					cal.add(Calendar.YEAR, -1);
				}
				return new CnValue<Date>(cnFormat, cnType, cal.getTime());
			} else if (cnFormat == CnFormat.DATE4) {
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				// Set the month in the date
				cal.set(Calendar.MONTH, tens[0] - 1);
				cal.set(Calendar.DATE, tens[1]);
				if (cal.getTime().after(new Date())) {
					cal.add(Calendar.YEAR, -1);
				}
				return new CnValue<Date>(cnFormat, cnType, cal.getTime());
			} else if (cnFormat == CnFormat.DATE_EXP) {
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.DATE, 1);
				// Set the month in the date
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) % 100) + tens[0]);
				cal.set(Calendar.MONTH, tens[1] - 1);
				return new CnValue<Date>(cnFormat, cnType, cal.getTime());
			} else if (cnFormat == CnFormat.TIME) {
				cal.set(Calendar.HOUR_OF_DAY, tens[0]);
				cal.set(Calendar.MINUTE, tens[1]);
				cal.set(Calendar.SECOND, tens[2]);
				return new CnValue<Date>(cnFormat, cnType, cal.getTime());
			}
			return new CnValue<Date>(cnFormat, cnType, cal.getTime());
		}
		return null;
	}	
	
	/**
	 * set get 函数
	 * */
	public int getLength() {
		return length;
	}

	public CnFormat getCnFormat() {
		return cnFormat;
	}

	public CnType getCnType() {
		return cnType;
	}

	public boolean isMust() {
		return must;
	}

	public boolean isAddLen() {
		return addLen;
	}

	public String getAlign() {
		return align;
	}
}
