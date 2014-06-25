package com.dhcc.pos.packets;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 字段存储类型
 * @author Maple Leaves
 *
 */
public enum CnFormat
{
	//数字 BCD码压缩
	NUMERIC(true, 0), 
	//字符或数字长度不足右补空格
	ALPHA(true, 0), 
	//任意字符和符号 LL 最大99个长度值 可变长域的长度值(二位数)
	LLVAR(true, 0), 
	//任意字符和符号 LLL 最大999个长度值 可变长域的长度值(三位数)
	LLLVAR(true, 0), 

	//MMddHHmmss
	DATE10(false, 10), 
	//MMdd
	DATE4(false, 4), 
	//yyMM
	DATE_EXP(false, 4), 
	//HHmmss
	TIME(false, 6), 
	//金额
	AMOUNT(false, 12);

	//长度是否必须
	private boolean needsLen;
	//长度
	private int length;


	/**
	 * @param needsLen  是否必输长度 ；
	 * 					是：true
	 * 					不是：false
	 * @param length	该字段长度
	 */
	private CnFormat(boolean flag, int len) {
		this.needsLen = flag;
		this.length = len;
	}

	public boolean needsLength()
	{
		return this.needsLen;
	}

	public int getLength()
	{
		return this.length;
	}

	//日期格式化
	public String formatDate(Date value)
	{
		if (this == DATE10)
			return new SimpleDateFormat("MMddHHmmss").format(value);
		if (this == DATE4)
			return new SimpleDateFormat("MMdd").format(value);
		if (this == DATE_EXP)
			return new SimpleDateFormat("yyMM").format(value);
		if (this == TIME) {
			return new SimpleDateFormat("HHmmss").format(value);
		}
		throw new IllegalArgumentException("Cannot format date as " + this);
	}
	/////////////////////////////////////////

	public byte[] format(byte[] value,int length){
		return value;
	}

	/** Formats the string to the given length (length is only useful if type is ALPHA). */
	//格式化数值
	public String format(String value, int length) {
		if (this == ALPHA) {
			if (value == null) {
				value = "";
			}
			//当接收到为2进制时 而配置中的长度为原值 此处判断长度没有意义；
//			if (value.length() > length) {
//				return value.substring(0, length);
//			}
////			长度不足右补空格
//			char[] c = new char[length];
			
			char[] c = new char[value.length()];
			
			System.arraycopy(value.toCharArray(), 0, c, 0, value.length());
			for (int i = value.length(); i < c.length; i++) {
				c[i] = ' ';
			}
			return new String(c);
		} else if (this == LLVAR || this == LLLVAR) {
			return value;

		} else if (this == NUMERIC) {
			char[] c = new char[length];
			char[] x = value.toCharArray();
			if (x.length > length) {
				throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
			}
			//数字长度不足左补0
			int lim = c.length - x.length;
			for (int i = 0; i < lim; i++) {
				c[i] = '0';
			}
			// arraycopy(Object src, int srcPos,Object dest, int destPos,int length)
			System.arraycopy(x, 0, c, lim, x.length);
			return new String(c);
		}
		throw new IllegalArgumentException("Cannot format String as " + this);
	}

	/** Formats the integer value as a NUMERIC, an AMOUNT, or a String. */
	public String format(long value, int length) {
		if (this == NUMERIC) {
			char[] c = new char[length];
			char[] x = Long.toString(value).toCharArray();
			if (x.length > length) {
				throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
			}
			//数字长度不足左补0
			int lim = c.length - x.length;
			for (int i = 0; i < lim; i++) {
				c[i] = '0';
			}
			System.arraycopy(x, 0, c, lim, x.length);
			return new String(c);
		} else if (this == ALPHA || this == LLVAR || this == LLLVAR) {
			return format(Long.toString(value), length);
		} else if (this == AMOUNT) {
			String v = Long.toString(value);
			char[] digits = new char[12];
			//金额长度不足左补0
			for (int i = 0; i < 12; i++) {
				digits[i] = '0';
			}
			//No hay decimales asi que dejamos los dos ultimos digitos como 0
			System.arraycopy(v.toCharArray(), 0, digits, 10 - v.length(), v.length());
			return new String(digits);
		}
		throw new IllegalArgumentException("Cannot format number as " + this);
	}

	/** Formats the BigDecimal as an AMOUNT, NUMERIC, or a String. */
	public  String format(BigDecimal value, int length) {
		if (this == AMOUNT) {
			//金额格式化
			String v = new DecimalFormat("0000000000.00").format(value);
			return v.substring(0, 10) + v.substring(11);
		} else if (this == NUMERIC) {
			return format(value.longValue(), length);
		} else if (this == ALPHA || this == LLVAR || this == LLLVAR) {
			return format(value.toString(), length);
		}
		throw new IllegalArgumentException("Cannot format BigDecimal as " + this);
	}

	public static void main(String[] args) {
		double i = 22222222222.1;
		String v = new DecimalFormat("0000000000.00").format(i);
		System.out.println( v.substring(0, 10) + v.substring(11) );
	}
}