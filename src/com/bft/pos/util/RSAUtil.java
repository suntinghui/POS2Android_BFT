package com.bft.pos.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.dhcc.pos.packets.util.ConvertUtil;


public class RSAUtil {

	public static void main(String[] args) {

		/**pos端公钥
		 * 模长1024，DER明文输出
		 *  [public key] [length = 140]
		 */
		String _publicKey = "30818902818100B6FBD43211AE9B8AE9CC17529512F69CB8755C432F33E519242820091AE07F8038990F93B2CE52C5159EBCF09AF94EAF7208328D0E5FCB014FC426B3560C687598BBBF2795207F2A9B9881FE752D2D9F779BC18AD423380E1EAF662F39E960FFE298AA85B442447A56AFE5D4C2E9490F6EF666B9296ABD0055D9EDF7A86B07CB0203010001";
		String TheEncryptedData = encryptToHexStr(_publicKey, "111111FF".getBytes(), 1);
		System.out.println("经加密后数据:" + TheEncryptedData);

	}
	/**根据hex类型公钥 对数据进行加密
	 * 首先公钥得到mod和exp然后生成对象，根据对象对数据进行加密
	 * @param	publicKeyStr		公钥字符串
	 * @param 	data				待加密数据（不足8位或8的倍数补F）
	 * @param 	padding				注意填充方式：
	 * 								1:如果调用了加密机【RSA/ECB/NoPadding】
	 * 								2:此代码用了默认补位方式【RSA/None/PKCS1Padding】，不同JDK默认的补位方式可能不同，如Android默认是RSARSA/ECB/PKCS1Padding
	 * @return 	TheEncryptedData	经加密后的数据(16进制)
	 */
	public static String encryptToHexStr(String publicKeyStr, byte[] data, int padding){
		String TheEncryptedData = null;
		byte[] cipherData = encryptToByte(publicKeyStr, data, padding);
		TheEncryptedData = ConvertUtil.bytesToHexString(cipherData);
		return TheEncryptedData;
	}
	/**根据hex类型公钥 对数据进行加密
	 * 首先公钥得到mod和exp然后生成对象，根据对象对数据进行加密
	 * @param	publicKeyStr		公钥字符串
	 * @param 	data				待加密数据（不足8位或8的倍数补F）
	 * @param 	padding				注意填充方式：
	 * 								1:如果调用了加密机【RSA/ECB/NoPadding】
	 * 								2:此代码用了默认补位方式【RSA/None/PKCS1Padding】，不同JDK默认的补位方式可能不同，如Android默认是RSARSA/ECB/PKCS1Padding
	 * @return 	TheEncryptedData	经加密后的数据(二进制)
	 */
	public static byte[] encryptToByte(String publicKeyStr, byte[] data, int padding){
		RSAPublicKeySpec publicKeySpec = null;
		/**  
		 * 公钥  
		 */  
		PublicKey publicKey = null;
		
		byte[] TheEncryptedData = null;

		/**解析公钥串，拿到Modulus，Exponent*/
		String[] pubModExp = RSA.parsePublickey(publicKeyStr);

		/** 公钥初始化*/
		String mod, exp = null;
		mod = pubModExp[0];
		exp = pubModExp[1];
//		System.out.println("mod:" + mod + "  \t length：" + mod.length());
//		System.out.println("exp:" + exp + "   length:" + exp.length());

		/**根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象*/
		publicKeySpec = RSA.getRSAPublidKey(mod, exp);

//		System.out.println("##########公钥##########");
//		System.out.println("公钥exp 十进制:" + publicKeySpec.getPublicExponent());
//		System.out.println("公钥exp HEX:" + ConvertUtil.bytesToHexString(publicKeySpec.getPublicExponent().toByteArray()));
//		System.out.println("公钥mod 十进制:" + publicKeySpec.getModulus());
//		System.out.println("公钥mod HEX:" + ConvertUtil.bytesToHexString(publicKeySpec.getModulus().toByteArray()));

		/**根据公钥对象拿到公钥*/
		publicKey = RSA.getPublidKey(publicKeySpec);
//		System.out.println("Algorithm:" + publicKey.getAlgorithm());
//		System.out.println("公钥:" +  publicKey.toString());
//		System.out.println("serialVersionUID:" + publicKey.serialVersionUID);

		/**======加密======*/
		Cipher cipher = null;
		
		/** 填充方式 */
		try {
			switch (padding) {
			case 1:
				// JDK实现是"RSA/None/PKCS1Padding
				cipher = Cipher.getInstance("RSA/ECB/NoPadding");
				break;
			case 2:
				cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				break;
			default:
				// cipher = Cipher.getInstance("RSA/ECB/NoPadding",new org.bouncycastle.jce.provider.BouncyCastleProvider());
				// cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
				// Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");//android系统的RSA实现是"RSA/None/NoPadding
				break;
			}
			
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			/**加密数据*/
			byte[] cipherData = cipher.doFinal(data);
			
			TheEncryptedData = cipherData;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TheEncryptedData;
	}
	/**根据hex类型公钥 对数据进行加密（首先公钥得到mod和exp然后生成对象，根据对象对数据进行加密）
	 * @param publicKeyStr	公钥字符串
	 * @return
	 */
	public static String encrypt(String publicKeyStr, String path){
		RSAPublicKeySpec publicKeySpec = null;
		/**  
		 * 公钥  
		 */  
		PublicKey publicKey = null;

		/**解析公钥串，拿到Modulus，Exponent*/
		String[] pubModExp = RSA.parsePublickey(publicKeyStr);

		/** 公钥初始化*/
		String mod, exp = null;
		mod = pubModExp[0];
		exp = pubModExp[1];
		System.out.println("mod:" + mod + "  \t length：" + mod.length());
		System.out.println("exp:" + exp + "   length:" + exp.length());

		/**根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象*/
		publicKeySpec = RSA.getRSAPublidKey(mod, exp);

		System.out.println("##########公钥##########");
		System.out.println("公钥exp 十进制:" + publicKeySpec.getPublicExponent());
		System.out.println("公钥exp HEX:" + ConvertUtil.bytesToHexString(publicKeySpec.getPublicExponent().toByteArray()));
		System.out.println("公钥mod 十进制:" + publicKeySpec.getModulus());
		System.out.println("公钥mod HEX:" + ConvertUtil.bytesToHexString(publicKeySpec.getModulus().toByteArray()));

		/**根据公钥对象拿到公钥*/
		publicKey = RSA.getPublidKey(publicKeySpec);
		System.out.println("Algorithm:" + publicKey.getAlgorithm());
		System.out.println("公钥:" +  publicKey.toString());
		System.out.println("serialVersionUID:" + publicKey.serialVersionUID);

		/**======加密======*/
		Cipher cipher = null;
		
		/** 填充方式 */
		try {
			// JDK实现是"RSA/None/PKCS1Padding
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			// cipher = Cipher.getInstance("RSA/ECB/NoPadding",new org.bouncycastle.jce.provider.BouncyCastleProvider());
			// cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
			// Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");//android系统的RSA实现是"RSA/None/NoPadding
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
