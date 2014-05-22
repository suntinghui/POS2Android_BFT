package com.dhcc.pos.packets.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.util.Log;


public class ConvertUtil {
	
	static final byte[] HEX = new byte[] { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	public static final String nLine = "----------------------------------------------------------------------------";
	public static final String nLine2 = "----------------------------------------";
	final static int BUFFER_SIZE = 4096;
	
	/**字节数组转字符串 
	 * 字符编码： 默认为UTF-8
	 * @param b
	 * @param charset
	 * @return
	 */
	public static String byte2String(byte[] b,Charset charset){
		String result = null;
			if(b!=null){
				if(charset!=null){
					result = new String(b,charset);
				}else{
					result = new String(b,Charset.forName("UTF-8"));
				}
			}	
		return result;
	}
	
	static String HexToBinary(String s){
		return Long.toBinaryString(Long.parseLong(s, 16));
	}
	
	/**把一个16进制字符串转换为2进制字符串
	 * @param hexString 16进制字符串
	 * @return 2进制字符串
	 */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}
	
	  /**把一个2进制字符串转换为16进制字符串
	 * @param bString 二进制字符串
	 * @return	16进制字符串
	 */
	public static String binaryString2hexString(String bString) {
		if (bString == null || bString.equals("") || bString.length() % 8 != 0)
			return null;
		StringBuffer tmp=new StringBuffer();
		int iTmp = 0;
		for (int i = 0; i < bString.length(); i += 4) {
			iTmp = 0;
			for (int j = 0; j < 4; j++) {
				iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
			}
			tmp.append(Integer.toHexString(iTmp));
		}
		return tmp.toString();
	}

	/**
	 * 把一个字节数组的串格式化成十六进制形式. 
	 * 格式化后的样式如下<br></br>
	 * <blockquote> 
	 *  00000H  61 62 63 64 D6 D0 B9 FA 73 73 73 73 73 73 73 73 ; abcd中国ssssssss  <br></br>
 	 *  00016H  73 73 73 73 73 73 73 73 73 B1 B1 BE A9 64 64 64 ; sssssssss北京ddd  <br></br>
 	 *  00032H  64 64 64 64 64 64 64 64 64 64 64 64 64 64 64 64 ; dddddddddddddddd  <br></br>
 	 * </blockquote> 
	 * @param b 需要格式化的字节数组
	 * @return 格式化后的串，其内容如上。可以直接输出。
	 */
	public static String formatbytes2Hex(byte[] b) {
		String result_str = "";
		byte[] chdata = new byte[19];  // 只保存十六进制串后面的字符串 (" : " 就占了三个字节，后面为16个字节)
		for (int i = 0; i < b.length; i++) {
			String hex_of_one_byte = Integer.toHexString((int) b[i]).toUpperCase();
			if (i % 16 == 0) {		
				result_str = result_str + new String(chdata) + "\n ";
				Arrays.fill(chdata, (byte)0x00);
				System.arraycopy(" ; ".getBytes(), 0, chdata, 0, 3);
				for (int j = 0; j < 5 - String.valueOf(i).length(); j++) 
					result_str = result_str + "0";
				result_str = result_str + i + "H ";
			}
			if (hex_of_one_byte.length() >= 2) 
				result_str = result_str + " " + hex_of_one_byte.substring(hex_of_one_byte.length() - 2);
			else 
				result_str = result_str + " 0" + hex_of_one_byte.substring(hex_of_one_byte.length() - 1);
			System.arraycopy(b, i, chdata, 3 + (i %16), 1);
		}
		for(int j = 0; j < (16 - (b.length % 16 )) %16; j++)
			result_str = result_str + "   ";
		result_str = result_str + new String(chdata);
		return result_str;
	}
	
	/**
	 * <p> 将1~4字节的byte数组内容转换为一个int,如果byte数组的长度为1~3,则高位补零 </p>
	 * @param b
	 * @return 转化后的值，如果转化失败，则返回为 Integer.MIN_VALUE
	 * @author:张瑜平
	 * @throws IllegalArgumentException 参数非法时
	 */
	public static int byte2int(byte[] b) throws IllegalArgumentException {
		int count = b.length;
		if (count < 1 || count > 4) {
			return Integer.MIN_VALUE;
		}
		int result = 0; // 初始为0
		for (int i = 0; i < count; i++) {
			result = (b[i] & 0xFF) | result;
			if (i < count - 1) // 如不是最后一次循环才左移8为，最后一次不左移。
				result = result << 8;
		}
		return result;
	}
	
	/**
	 * <p>把一个int值（共占4字节），按字节转化成byte.（即对于每个字节，其存储的二进制不变，只是把int翻译成byte）</p>
	 * 转化的个数由count（count应大于0小于等于4）指定，顺序为从低到高。返回的长度为count的byte数组中，byte[0]放数学上的最高位，byte[count-1]放最低位。<br/>
	 * <blockquote> 
	 * 例如int数字为41111111，其存储二进制为 00000010     01110011    01001110     01000111 &nbsp &nbsp <br/>
	 * 当count为4时，则返回的byte分别对应为: &nbspbyte[0]&nbsp byte[1]&nbsp byte[2]&nbsp byte[3] &nbsp &nbsp <br/>
	 * 当count为3时，则返回的byte分别对应为: &nbsp &nbsp &nbsp &nbsp &nbsp byte[0]&nbsp byte[1]&nbsp byte[2] &nbsp &nbsp <br/>
	 * 当count为2时，则返回的byte分别对应为: &nbsp&nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp byte[0]&nbsp byte[1] &nbsp &nbsp <br/>
	 * 当count为1时，则返回的byte分别对应为: &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp byte[0] &nbsp &nbsp <br/>
	 * </blockquote> 
	 * @param value 待转换的int数值
	 * @param count 字节数组的长度
	 * @return 转化后的字节数组
	 */
	// 把一个int值（共占4字节），按字节转化成byte。（即对于每个字节，其存储的二进制不变，只是把int翻译成byte）
	// 转化的个数由count（count应大于0小于等于4）指定，顺序为从低到高。返回的长度为count的byte数组中，byte[0]放数学上的最高位，byte[count-1]放最低位。
	// 例如int数字为41111111，其存储二进制为 00000010  01110011  01001110  01000111
	// 当count为4时，则返回的byte分别对应为: byte[0]   byte[1]   byte[2]   byte[3]
	// 当count为3时，则返回的byte分别对应为:           byte[0]   byte[1]   byte[2]
	// 当count为2时，则返回的byte分别对应为:                     byte[0]   byte[1]
	// 当count为1时，则返回的byte分别对应为:                               byte[0]
	public static byte[] int2byte(int value, int count) {
		count = (count > 4 || count < 1) ? 4 : count;
		byte[] b = new byte[count];
		for (int i = 0; i < count; i++) {
			b[count - 1 - i] = (byte) Integer.rotateRight(value, i * 8);
		}
		return b;
	}
	
	/**
	 * 把字符形式的数字转化为二进制形式
	 * 如字符形式的 '0' (0x31)  将转化为  0 (0x01)     <br/>
	 * 如已经是二进制形式，则不进行转化
	 * @param digit_c 数字串
	 * @return 二进制形式的数字串
	 */
	public static byte[] digit_c2b(byte[] digit_c){
		byte [] digit_b = new byte[digit_c.length];
		for(int i = 0; i < digit_c.length; i++ ){
			if( ((digit_c[i] > 0x09) && (digit_c[i] < 0x30))    
				|| digit_c[i] > 0x39 || digit_b[i] < 0x00)
				Log.e("方法digit_c2b只能传入数字（字符或二进制形式）参数，方法调用失败! ","");
			
			if(digit_c[i] <= 0x09)
				digit_b[i] = digit_c[i];
			else
				digit_b[i] = (byte) (digit_c[i] - 0x30);
		}
		return digit_b;
	}
	
	/**
	 * 把二进制形式的数字转化为字符形式
	 * 如字符形式的  0 (0x01) 将转化为 '0' (0x31) <br/>
	 * 如已经是字符形式，则不进行转化
	 * @param digit_b 数字串
	 * @return 字符形式的数字串
	 */
	public static byte[] digit_b2c(byte[] digit_b) throws  IllegalArgumentException{
		byte [] digit_c = new byte[digit_b.length];
		for(int i = 0; i < digit_c.length; i++ ){
			if( ((digit_b[i] > 0x09) && (digit_b[i] < 0x30))    
					|| digit_b[i] > 0x39 || digit_b[i] < 0x00)
					Log.e("方法digit_b2c只能传入数字（字符或二进制形式）参数，方法调用失败! ","");
			
			if(digit_b[i] >= 0x30)
				digit_c[i] = digit_b[i];
			else
				digit_c[i] = (byte) (digit_b[i] + 0x30);	
		}
		return digit_c;
	}
	
	/**
	 * 判断传来的参数中全是都是数字（都在 0x00 ~ 0x09, 0x30 ~ 0x3F的范围）
	 * @param b 
	 * @return 如全是数字，则true，否则false。
	 */
	public static boolean isdigitAll(byte[] b) {
		for(int i = 0; i < b.length; i++) {
			if(b[i] < 0x00 || b[i] > 0x3F 
			|| (b[i] > 0x09 && b[i] < 0x30))
				return false;
		}
		return true;
	}

	/**
	 * 将InputStream转换成String
	 * 
	 * @param in
	 *            InputStream
	 * @return String
	 * @throws Exception
	 * 
	 */
	public static String InputStreamTOString(InputStream in) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray());
	}

	/**
	 * 将InputStream转换成某种字符编码的String
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String InputStreamTOString(InputStream in, String encoding)
			throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(), encoding);
	}

	/**
	 * 将String转换成InputStream
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream StringTOInputStream(String in) throws Exception {

		ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes());
		return is;
	}

	/**
	 * 将InputStream转换成byte数组
	 * 
	 * @param in
	 *            InputStream
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] InputStreamTOByte(InputStream in) throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return outStream.toByteArray();
	}

	/**
	 * 将byte数组转换成InputStream
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream byteTOInputStream(byte[] in) throws Exception {

		ByteArrayInputStream is = new ByteArrayInputStream(in);
		return is;
	}

	/**
	 * 将byte数组转换成String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String byteTOString(byte[] in) throws Exception {

		InputStream is = byteTOInputStream(in);
		return InputStreamTOString(is);
	}
	
	/**文件内容读取到字节数组中
	 * @param filePath
	 * @return 字节数组
	 */
	@SuppressWarnings("resource")
	public static byte[] file2byte(String filePath){
		FileInputStream is;
		ByteArrayOutputStream baos;
		byte[] tmpBuf = null;
		try {
			is = new FileInputStream(filePath);
			if(true){
				int count = 0;
				while (count == 0) {
					count = is.available();
				}
				tmpBuf = new byte[count];
				is.read(tmpBuf);
			}else if(false){
				baos = new ByteArrayOutputStream();
				int b = 0;
				while ((b = is.read()) != -1)
					baos.write(b);
				return baos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return tmpBuf;
	}
	// java二进制,字节数组,字符,十六进制,BCD编码转换2007-06-07 00:17

	/**
	 * 把一个字节数组的串格式化成十六进制形式 格式化后的样式如下 <blockquote>
	 * --------------------------------
	 * -------------------------------------------- 000: 00 38 60 00 12 00 00 60
	 * 22 10 00 00 00 08 00 00 .8`...` ".....:016 016: 20 00 00 00 C0 00 10
	 * 16 83 74 30 30 30 32 31 36 ...�. �t000216:032 032: 37 38 31 30 35 33 36
	 * 30 31 37 30 31 31 30 31 35 78105360 17011015:048 048: 39 00 14 00 00 00
	 * 02 00 10 00 9..... . :064
	 * ----------------------------------------------
	 * ------------------------------ </blockquote>
	 * @param inBytes
	 *            需要格式化的字节数组
	 * @return 格式化后的串，其内容如上。可以直接输出
	 */
	public static String trace(byte[] inBytes) {
		int i, j = 0;
		/** 每行字节数 */
		byte[] temp = new byte[76];

		bytesSet(temp, ' ');
		StringBuffer strc = new StringBuffer("");
		strc.append(nLine + "\n");
		for (i = 0; i < inBytes.length; i++) {
			if (j == 0) {
				/** 打印出来的前四位 000: */
				System.arraycopy(String.format("%03d: ", i).getBytes(), 0,
						temp, 0, 5);

			 	/** 打印出来的后四位 :015 */
				System.arraycopy(String.format(":%03d", i + 16).getBytes(), 0,
						temp, 72, 4);
			}
			System.arraycopy(String.format("%02X ", inBytes[i]).getBytes(), 0,
					temp, j * 3 + 5 + (j > 7 ? 1 : 0), 3);
			if (inBytes[i] == 0x00) {
				temp[j + 55 + ((j > 7 ? 1 : 0))] = '.';
			} else {
				temp[j + 55 + ((j > 7 ? 1 : 0))] = inBytes[i];
			}
			j++;
			/** 当j为16时换行，j重置为0 每行显示为16进制的16个字节 */
			if (j == 16) {
				strc.append(new String(temp)).append("\n");
				bytesSet(temp, ' ');
				j = 0;
			}
		}
		if (j != 0) {
			/*字符串*/
			strc.append(new String(temp)).append("\n");
			bytesSet(temp, ' ');
		}
		strc.append(nLine + "\n");
		// System.out.println(strc.toString());
		return strc.toString();
	}

	/**
	 * 
	 * 将十六进制A--F转换成对应数
	 * 
	 * @param ch
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static int getIntByChar(char ch) throws Exception {
		char t = Character.toUpperCase(ch);
		int i = 0;

		switch (t) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			i = Integer.parseInt(Character.toString(t));
			break;
		case 'A':
			i = 10;
			break;
		case 'B':
			i = 11;
			break;
		case 'C':
			i = 12;
			break;
		case 'D':
			i = 13;
			break;
		case 'E':
			i = 14;
			break;
		case 'F':
			i = 15;
			break;
		default:
			throw new Exception("getIntByChar was wrong");
		}
		return i;
	}

	/**
	 * 
	 * 将字符串转换成二进制数组
	 * 
	 * @param source
	 *            : 16字节
	 * 
	 * @return
	 */
	public static int[] str2Binary(String source) {
		int len = source.length();
		int[] dest = new int[len * 4];
		char[] arr = source.toCharArray();

		for (int i = 0; i < len; i++) {
			int t = 0;
			try {
				t = getIntByChar(arr[i]);
				// System.out.println(arr[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String[] str = Integer.toBinaryString(t).split("");
			int k = i * 4 + 3;
			for (int j = str.length - 1; j > 0; j--) {
				dest[k] = Integer.parseInt(str[j]);
				k--;
			}
		}
		return dest;
	}

	/**
	 * 
	 * 返回x的y次方
	 * 
	 * @param x
	 * 
	 * @param y
	 * 
	 * @return
	 */
	public static int getXY(int x, int y) {
		int temp = x;
		if (y == 0)
			x = 1;
		for (int i = 2; i <= y; i++) {
			x *= temp;
		}
		return x;
	}

	/**
	 * 
	 * s�?位长度的二进制字符串
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static String binary2Hex(String s) {

		int len = s.length();

		int result = 0;

		int k = 0;

		if (len > 4)
			return null;

		for (int i = len; i > 0; i--) {

			result += Integer.parseInt(s.substring(i - 1, i)) * getXY(2, k);

			k++;
		}
		switch (result) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return "" + result;
		case 10:
			return "A";
		case 11:
			return "B";
		case 12:
			return "C";
		case 13:
			return "D";
		case 14:
			return "E";
		case 15:
			return "F";
		default:
			return null;
		}
	}

	/**
	 * 
	 * 将二进制字符串转换成十六进制字符
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static String binary2ASC(String s) {
		String str = "";
		int ii = 0;
		int len = s.length();
		// 不够4bit左补0
		if (len % 4 != 0) {
			while (ii < 4 - len % 4) {
				s = "0" + s;
			}
		}
		for (int i = 0; i < len / 4; i++) {
			str += binary2Hex(s.substring(i * 4, i * 4 + 4));
		}
		return str;
	}

	/**
	 * 把fill的值替换整个inBytes 例如：
	 * 
	 * @param inBytes
	 * @param fill
	 */
	private static void bytesSet(byte[] inBytes, char fill) {
		if (inBytes.length == 0) {
			return;
		}
		for (int i = 0; i < inBytes.length; i++) {
			inBytes[i] = (byte) fill;
		}
	}

	public static byte[] cancat(byte[] a, byte[] b) {
		int alen = a.length;
		int blen = b.length;
		byte[] result = new byte[alen + blen];
		System.arraycopy(a, 0, result, 0, alen);
		System.arraycopy(b, 0, result, alen, blen);
		return result;
	}

	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 0] & 0xff) << 24)
				| ((bb[index + 1] & 0xff) << 16)
				| ((bb[index + 2] & 0xff) << 8) | ((bb[index + 3] & 0xff) << 0)));
	}

	public static short getShort(byte[] b, int index) {
		return (short) (((b[index] << 8) | b[index + 1] & 0xff));
	}

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}
	
	
	public static void hexStrToAscii(String hexStr){
		int code ;
		code = Integer.parseInt(hexStr, 16);

		// 如果30代表是 16进制的30话，就取16
		// 如果30代表是 10进制的30话，就取10
		// code = Integer.parseInt(source, 10);
		char result = (char) code;
		System.out.println(result);
	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = (b.length - 1); i >= 0; i--) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}
	
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return "0x" + str;// 0x表示十六进制
	}
	/**
	 * 把字节数组转换成16进制字符串；
	 * Convert byte[] to hex string。
	 * 1、将byte转换成int，
	 * 2、然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * @param bArray		需要进行hex的字节数组数据
	 * @return hex string	（小写）
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = null;
		String sTemp;
		if (bArray == null || bArray.length <= 0) {
			return null;
		}
		sb = new StringBuffer(bArray.length);
		for (int i = 0; i < bArray.length; i++) {
			int v = bArray[i] & 0xFF;
			sTemp = Integer.toHexString(v);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp);
		}
		return sb.toString();
	}
	/**
	 * 把字节数组转换成16进制字符串；Convert byte[] to hex string。
	 * 		1、将byte转换成int，
	 * 		2、然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * @param bArray		需要进行hex的字节数组数据
	 * @return hex string	（大写）
	 */
	public static final String bytes2HexString(byte[] bArray) {
		StringBuffer sb = null;
		String sTemp;
		if (bArray == null || bArray.length <= 0) {
			return null;
		}
		sb = new StringBuffer(bArray.length);
		for (int i = 0; i < bArray.length; i++) {
			int v = bArray[i] & 0xFF;
			sTemp = Integer.toHexString(v);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp);
		}
		return sb.toString().toUpperCase();
	}
	/**将指定byte数组以16进制的形式打印到控制台
	 * @param data
	 */
	public static void printHexString(byte[] data) {
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase());
		}
	}
	
	
	/**把16进制字符串转换成字节数组
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]s
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/** */
	/**
	 * 把字节数组转换为对象
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object bytesToObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	/** */
	/**
	 * 把可序列化对象转换成字节数组
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static final byte[] objectToBytes(Serializable s) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream ot = new ObjectOutputStream(out);
		ot.writeObject(s);
		ot.flush();
		ot.close();
		return out.toByteArray();
	}

	public static final String objectToHexString(Serializable s)
			throws IOException {
		return bytesToHexString(objectToBytes(s));
	}

	public static final Object hexStringToObject(String hex)
			throws IOException, ClassNotFoundException {
		return bytesToObject(hexStringToBytes(hex));
	}

	/**
	 * 检查其数据是否能进行BCD
	 * 
	 * @param val
	 *            待检查的数据
	 * @return 都在 0x00 ~ 0x09, 0x30 ~ 0x3F的范围中，则返回true， 否则false
	 */
	private static boolean canbeBCD(byte[] val) {
		for (int i = 0; i < val.length; i++) {
			if (val[i] < 0x00 || val[i] > 0x3F
					|| (val[i] > 0x09 && val[i] < 0x30))
				return false;
		}
		return true;
	}

	/**
	 * @han对给定的数据进行BCD装换
	 * 功能描述
	 * 			当传入参数为单数时	：左靠齐 右边补0
	 * 			当传入参数为复数时	：正常转码
	 * @param val
	 *            带装换数据，需满足canbeBCD()。
	 * @return
	 */
	public static byte[] byte2bcd(byte[] val) {
		if (val == null) { // 检查参数是否为null
			Log.e("不能进行BCD, 传入的参数为null","");
			return null;
		}
		byte[] ret_val = val;
		if (!canbeBCD(val)) { // 检查参数的内容是否合法
			Log.e("不能进行BCD, 传入的参数非法：含有 不在[0x00 ~ 0x09], [0x30 ~ 0x3F]的范围中的数据","");
			return ret_val;
		}
		if (val.length == 0) // 当参数的内容的长度为0时，不必进行装换
			return null;
		if (val.length % 2 == 0) { // 长度为偶数时
			ret_val = new byte[val.length / 2];
			for (int i = 0; i < ret_val.length; i++) {
				byte temp1 = (byte) (val[i * 2] < 0x30 ? val[i * 2]
						: val[i * 2] - 0x30);
				byte temp2 = (byte) (val[i * 2 + 1] < 0x30 ? val[i * 2 + 1]
						: val[i * 2 + 1] - 0x30);
				ret_val[i] = (byte) (((temp1 << 4) & 0xFF) // 前4位
				| ((temp2 & 0x0F) & 0xFF)); // 后4位
			}
		} else { // 长度为奇数时
			ret_val = new byte[val.length / 2 + 1];
			ret_val[0] = (byte) (val[0] & 0x0F);
			for (int i = 1; i < ret_val.length; i++) {
				byte temp1 = (byte) (val[i * 2 - 1] < 0x30 ? val[i * 2 - 1]
						: val[i * 2 - 1] - 0x30);
				byte temp2 = (byte) (val[i * 2] < 0x30 ? val[i * 2]
						: val[i * 2] - 0x30);
				ret_val[i] = (byte) (((temp1 << 4) & 0xFF) // 前4位
				| ((temp2 & 0x0F) & 0xFF)); // 后4位
			}
		}
		return ret_val;
	}
	/**
	 * @函数功能: BCD码转ASCII码
	 * @功能描述
	 * 			1个字节转换成2个字符串 2个字节转换为4个字符串 
	 * @输入参数: bytesBCD码
	 * @输出结果: 10进制ASCII码
	 */
	public static String bcd2str(byte[] bcdBytes) {
		StringBuffer temp = new StringBuffer(bcdBytes.length * 2);

		for (int i = 0; i < bcdBytes.length; i++) {
			 int h = ((bcdBytes[i] & 0xf0) >>> 4);
		     int l = (bcdBytes[i] & 0x0f);   
		     temp.append(h).append(l);
		     /**---或者---*/
//			temp.append((byte) ((bcdBytes[i] & 0xf0) >>> 4));
//			temp.append((byte) (bcdBytes[i] & 0x0f));
		}
		return temp.toString();
	}
	/** */
	/**
	 * 此函数做了特殊处理 默认会删掉左侧的零
	 * @函数功能: BCD码转ASCII码
	 * @功能描述
	 * 			1个字节转换成2个字符串 2个字节转换为4个字符串 然后删掉左侧零
	 * @输入参数: bytesBCD码
	 * @输出结果: 10进制ASCII码
	 */
	public static String bcd2str_(byte[] bcdBytes) {
		StringBuffer temp = new StringBuffer(bcdBytes.length * 2);

		for (int i = 0; i < bcdBytes.length; i++) {
			temp.append((byte) ((bcdBytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bcdBytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * 此函数做了特殊处理 默认会删掉右侧的零
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @功能描述
	 * 			1个字节转换成2个字符串 2个字节转换为4个字符串 然后删掉右侧零
	 * @输入参数: bytesBCD码
	 * @输出结果: 10进制ASCII码
	 */
	public static String _bcd2Str(byte[] bcdBytes) {
		StringBuffer temp = new StringBuffer(bcdBytes.length * 2);

		for (int i = 0; i < bcdBytes.length; i++) {
			temp.append((byte) ((bcdBytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bcdBytes[i] & 0x0f));
		}
		return temp.toString()
				.substring(temp.toString().length()-1, temp.toString().length()).equalsIgnoreCase("0") 
				?temp.toString().substring(0, temp.toString().length() - 1)
				:temp.toString();
//		return temp.toString().substring(0, temp.toString().length() - 1);
	}

/*
	*//**
	 * 右靠齐 左补0
	 * *//*
	public static String bcd2Str_(byte[] bcdBytes) {
		char temp[] = new char[bcdBytes.length * 2], val;

		for (int i = 0; i < bcdBytes.length; i++) {
			val = (char) (((bcdBytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bcdBytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	*/
	/**
	 * @函数功能: 10进制串(阿拉伯数据)ASCII码转为BCD码字节数组
	 * @功能描述
	 * 			当传入参数为复数时	：正常转码
	 * @输入参数: 10进制ASCII码
	 * @输出结果: BCD码字节数组
	 */
	public static byte[] str2bcd(String asciiStr) {
		int len = asciiStr.length();
		int mod = len % 2;

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asciiStr.getBytes();
		int j, k;

		for (int p = 0; p < asciiStr.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}
	/**
	 * @函数功能: 10进制串(阿拉伯数据)ASCII码转为BCD码字节数组
	 * @功能描述
	 * <blockquote>
	 * 当传入参数为单数时	：左靠齐 右边补0
	 * </br>
	 * 当传入参数为复数时	：正常转码
	 * </blockquote>
	 * @输入参数: 10进制ASCII码
	 * @输出结果: BCD码字节数组
	 */
	public static byte[] _str2bcd(String asciiStr) {
		int len = asciiStr.length();
		int mod = len % 2;

		if (mod != 0) {
			asciiStr += "0";
			len = asciiStr.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asciiStr.getBytes();
		int j, k;

		for (int p = 0; p < asciiStr.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	/**
	 * @函数功能: 10进制串(阿拉伯数据)ASCII码转为BCD码字节数组
	 * @功能描述
	 * <blockquote> 
	 * 	当传入参数为单数时	：右靠齐 左补0;
	 * 	</br>
	 * 	当传入参数为复数时	：正常转码
	 * </blockquote>
	 * @输入参数	10进制ASCII码
	 * @输出结果	BCD码字节数组
	 */
	public static byte[] str2bcd_(String asciiStr) {
		int len = asciiStr.length();
		int mod = len % 2;

		if (mod != 0) {
			asciiStr = "0" + asciiStr;
			len = asciiStr.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asciiStr.getBytes();
		int j, k;

		for (int p = 0; p < asciiStr.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}


	/**
	 * MD5加密字符串，返回加密后的16进制字符串
	 * 
	 * @param origin
	 * @return
	 */
	public static String MD5EncodeToHex(String origin) {
		return bytesToHexString(MD5Encode(origin));
	}

	/**
	 * MD5加密字符串，返回加密后的字节数组
	 * 
	 * @param origin
	 * @return
	 */
	public static byte[] MD5Encode(String origin) {
		return MD5Encode(origin.getBytes());
	}

	/**
	 * MD5加密字节数组，返回加密后的字节数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] MD5Encode(byte[] bytes) {
		MessageDigest md = null;
		try {
			//创建具有指定算法名称的信息摘要 
			md = MessageDigest.getInstance("MD5");
			//使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
}
