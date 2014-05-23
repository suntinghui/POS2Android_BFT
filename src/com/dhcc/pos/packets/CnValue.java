package com.dhcc.pos.packets;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import com.dhcc.pos.packets.util.ConvertUtil;


/**
 * *字段域所存储的数据
 * 
 * @author Maple Leaves
 */
public class CnValue<T> {
	/** 数据格式 */
	private CnFormat cnFormat;
	/** 消息类型*/
	private CnType cnType;
	/** 数值 */
	private T value;
	/** 长度 */
	private int length;
	/** 是否必输项*/
	private boolean must;
	/** 没有添加长度的字段 默认为：false ； 该添加长度是组装报文时 是否添加域值长度*/
	private boolean addLen;
	/**对齐	：当类型为BCD码时进行左对齐（右补零）和右对齐（左补零）*/
	private String align;

	public CnValue(CnFormat cnFormat, CnType cnType, T value) {
		if (cnFormat.needsLength()) {
			throw new IllegalArgumentException("Fixed-value types must use constructor that specifies length");
		}
		this.cnFormat = cnFormat;
		this.value = value;
		this.cnType = cnType;

		if ((this.cnFormat == CnFormat.LLVAR) || (this.cnFormat == CnFormat.LLLVAR)) {

			// 获取可变长域可变长度
			//			 this.length = value.toString().getBytes().length;
			this.length = value.toString().length();

			if ((cnFormat == CnFormat.LLVAR) && (this.length > 99))
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");

			if ((cnFormat == CnFormat.LLLVAR) && (this.length > 999))
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
		} else {
			this.length = this.cnFormat.getLength();
		}
	}

	public CnValue(CnFormat cnFormat, CnType cnType, T value, int length, boolean must, boolean addLen, String align) {
		this.cnFormat = cnFormat;
		this.value = value;
		this.length = length;
		this.cnType = cnType;
		this.must = must;
		this.addLen = addLen;
		this.align = align;

		if ((this.length == 0) && (cnFormat.needsLength()))
			throw new IllegalArgumentException("Length must be greater than zero");
		if ((cnFormat == CnFormat.LLVAR) || (cnFormat == CnFormat.LLLVAR)) {
			// 设置变长域的长度
//			this.length = value.toString().getBytes().length;
			if ((cnFormat == CnFormat.LLVAR) && (this.length > 99))
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
			if ((cnFormat == CnFormat.LLLVAR) && (this.length > 999))
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
		} 
	}

	/**
	 * Returns the formatted value as a String. The formatting depends on the
	 * type of the receiver.
	 */
	public String toString() {
		if (value == null) {
			return "ISOValue<null>";
		}
		if (cnFormat == CnFormat.NUMERIC || cnFormat == CnFormat.AMOUNT) {
			if (cnFormat == CnFormat.AMOUNT) {
				// return datatype.format((Double)value ,12);
				return cnFormat.format((BigDecimal) value, 12);
			} else if (value instanceof Number) {
				return cnFormat.format(((Number) value).longValue(), length);
			} else {
				return cnFormat.format(value.toString(), length);
			}
		} else if (cnFormat == CnFormat.ALPHA) {
			return cnFormat.format(value.toString(), length);
		} else if (cnFormat == CnFormat.LLLVAR || cnFormat == CnFormat.LLLVAR) {
			return value.toString();
		} else if (value instanceof Date) {
			return cnFormat.formatDate((Date) value);
		}
		return value.toString();
	}

	public CnValue<T> clone() {
		return new CnValue<T>(this.cnFormat, this.cnType, this.value, this.length, this.must, this.addLen, this.align);
	}

	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof CnValue)) {
			return false;
		}
		CnValue<?> comp = (CnValue<?>) other;
		return (comp.getCnFormat() == getCnFormat())
				&& (comp.getValue().equals(getValue()))
				&& (comp.getLength() == getLength());
	}

	/**
	 * toString().getBytes()、toBcd()、ConvertUtil.hexStringToBytes(toString())
	 * 以上3个区别为：第一个30显示，第二个转为二进制并做了补零故此为bcd压缩，第三个转为二进制未做补零运算
	 * 
	 * @param outs
	 * @param fieldId
	 * @throws IOException
	 */
	public void write(OutputStream os, Object fieldId)
			throws IOException {
		byte[] buf = (byte[]) null;

		if (this.cnType == CnType.BCD) {
			buf = new byte[this.length / 2 + this.length % 2];
		} else if (this.cnType == CnType.BCD && (this.cnFormat == CnFormat.AMOUNT)) {
			buf = new byte[this.length/2];
		} else if (this.cnType == CnType.BCD && ( (this.cnFormat == CnFormat.DATE10)
				|| (this.cnFormat == CnFormat.DATE4)
				|| (this.cnFormat == CnFormat.DATE_EXP)
				|| (this.cnFormat == CnFormat.TIME)) ) {
			buf = new byte[this.length / 2];
		}

		/**如果buf不为null证明value被bcd码压缩后并写入到outs中，故此return跳出该函数，否则下面的write会重复性再写入一遍该值（不经过bcd码压缩的值）*/
		if(this.cnType == CnType.BCD){
			/* 进行BCD码压缩 */
			buf = toBCD(toString(), buf.length, align);
			os.write(buf);
			//当buf为空时默认为ascii下面判断可有可无
		}else if(this.cnType == CnType.ASCII){
			os.write(toString().getBytes());
		}else if(this.cnType == CnType.BINARY){
			buf = ConvertUtil.hexStringToBytes(toString());
			os.write(buf);
		}
	}

	/**BCD压缩
	 * @param value
	 * @param bufLen
	 * @param fieldId
	 */
	private byte[] toBCD(String value, int bufLen, String align) {
		byte[] buf = new byte[bufLen];

		/**当域值为奇数时 需要根据不同域名来指定左靠齐或右靠齐*/
		if (value.length() % 2 == 1) {
			if (align.equals("left")) {
				buf = ConvertUtil._str2bcd(value);
				// System.out.println("fieldId:" + fieldId + ";length:"+this.length +";" + ConvertUtil.trace(buf));
			} else {
				buf = ConvertUtil.str2bcd_(value);
				// System.out.println("fieldId:" + fieldId + ";length:"+this.length +";" + ConvertUtil.trace(buf));
			}
		} else {
			buf = ConvertUtil.str2bcd(value);
			// System.out.println("fieldId:" + fieldId + ";length:"+this.length  +";" + ConvertUtil.trace(buf));
		}
		return buf;
	}

	public CnFormat getCnFormat() {
		return this.cnFormat;
	}

	public CnType getCnType() {
		return cnType;
	}

	public int getLength() {
		return this.length;
	}

	public T getValue() {
		return this.value;
	}

	public boolean getIsMust() {
		return must;
	}

	public boolean getIsAddLen() {
		return addLen;
	}

	public String getAlign() {
		return align;
	}
}