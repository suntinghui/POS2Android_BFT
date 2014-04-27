package com.bft.pos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

/*
 * * 提供加密和解密功能
 * 
 * @author maple
 */
public class RSA {

	/**
	 * 加密一个char的东西，即一个ASCII码
	 * 
	 * @param key
	 *            密钥
	 * @param b
	 *            明文
	 * @return 密文
	 */
	public static BigInteger encrypt(RSAPublicKeySpec key, char c) {
		BigInteger src = new BigInteger(String.valueOf((int) c));
		return src.modPow(key.getPublicExponent(), key.getModulus());
	}

	/**
	 * 加密一个字符串，结果存入ArrayList中
	 * 
	 * @param key
	 *            密钥
	 * @param str
	 *            明文
	 * @return 密文
	 */
	public static List<BigInteger> encrypt(RSAPublicKeySpec key, String str) {
		ArrayList<BigInteger> list = new ArrayList<BigInteger>();
		char[] cs = str.toCharArray();
		for (int i = 0, j = cs.length; i < j; i++) {
			list.add(encrypt(key, cs[i]));
		}
		return list;
	}

	/**
	 * 以覆盖的方式加密一个文本文件
	 * 
	 * @param key
	 *            密钥
	 * @param file
	 *            要加密的文件
	 * @return
	 */
	public static void encrypt(RSAPublicKeySpec key, File file)
			throws FileNotFoundException, IOException {
		// 用来保存加密结果的临时list
		ArrayList<BigInteger> list = new ArrayList<BigInteger>();
		readEncrypt(list, key, file);
		writeEncrypt(list, file);

	}

	/**
	 * 加密一个文本文件
	 * 
	 * @param key
	 *            密钥（公钥）
	 * @param file
	 *            要加密的文件
	 * @param toFile
	 *            加密后的文件
	 * @return
	 */
	public static void encryptToFile(RSAPublicKeySpec key, File file,
			File toFile) throws FileNotFoundException, IOException {
		// 用来保存加密结果的临时list
		ArrayList<BigInteger> list = new ArrayList<BigInteger>();
		readEncrypt(list, key, file);
		writeEncrypt(list, toFile);

	}

	/**
	 * 解密一个经过加密的数
	 * 
	 * @param key
	 *            解钥
	 * @param cipher
	 *            密文
	 * @return 明文
	 */
	public static char decrypt(RSAPrivateKeySpec key, BigInteger cipher) {
		// 解密过程
		BigInteger src = cipher.modPow(key.getPrivateExponent(),
				key.getModulus());
		// 转换成char
		char c = (char) src.intValue();
		return c;
	}

	/**
	 * 解密一个经过加密的字符串
	 * 
	 * @param key
	 *            解钥
	 * @param list
	 *            密文
	 * @return 明文
	 */
	public static String decrypt(RSAPrivateKeySpec key, List<BigInteger> list) {
		int len = list.size();
		char[] cs = new char[len];
		for (int i = 0; i < len; i++) {
			cs[i] = decrypt(key, list.get(i));
		}
		return String.valueOf(cs);
	}

	/**
	 * 解密一个文件
	 * 
	 * @param key
	 *            密钥
	 * @param file
	 *            文件名
	 */
	public static void decrypt(RSAPrivateKeySpec key, File file)
			throws IOException {
		List<BigInteger> list = new ArrayList<BigInteger>();
		readDecrypt(list, file);
		writeDecrypt(key, list, file);

	}

	/**
	 * 解密一个文件
	 * 
	 * @param key
	 *            密钥
	 * @param file
	 *            解密的文件
	 * 
	 */
	public static void decryptToFile(RSAPrivateKeySpec key, File file,
			File toFile) throws IOException {
		List<BigInteger> list = new ArrayList<BigInteger>();
		readDecrypt(list, file);
		writeDecrypt(key, list, toFile);

	}

	/**
	 * 读取file中的内容，并加密，用list保存结果
	 * 
	 * @param list
	 * @param key
	 * @param file
	 */
	private static void readEncrypt(List<BigInteger> list,
			RSAPublicKeySpec key, File file) throws FileNotFoundException,
			IOException {
		// 用来读取文件内容
		BufferedReader reader = new BufferedReader(new FileReader(file));
		// 临时保存一行内容
		String str = null;
		// 读取所有内容，放入list中
		while ((str = reader.readLine()) != null) {
			// 加上换行符
			str += System.getProperty("line.separator").substring(1);
			System.out.println(str);
			list.addAll(encrypt(key, str));
		}
		reader.close();
	}

	/**
	 * 把list中的值写入文件，加密用
	 * 
	 * @param list
	 * @param file
	 */
	private static void writeEncrypt(List<BigInteger> list, File file)
			throws IOException {
		// 获取要写入的文件，并构造一个BufferedWriter对象
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (BigInteger i : list) {
			writer.write(i.toString());
			writer.newLine();
		}
		writer.close();
	}

	/**
	 * 把要解密的文件夹的内容读入一个list中
	 * 
	 * @param list
	 * @param file
	 */
	private static void readDecrypt(List<BigInteger> list, File file)
			throws FileNotFoundException, IOException {
		// 用来读取文件内容
		BufferedReader reader = new BufferedReader(new FileReader(file));
		// 临时保存一行内容
		String str = null;
		// 读取所有内容，放入list中
		while ((str = reader.readLine()) != null) {
			list.add(new BigInteger(str));
		}
		reader.close();
	}

	/**
	 * 把list中的值写入文件，解密用
	 * 
	 * @param list
	 * @param file
	 */
	private static void writeDecrypt(RSAPrivateKeySpec key,
			List<BigInteger> list, File file) throws IOException {
		// 获取要写入的文件，并构造一个BufferedWriter对象
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (BigInteger i : list) {
			writer.write(decrypt(key, i));
		}
		writer.close();
	}

	 /**加密
	  * 注意填充方式：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	  * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	 * @param publicKey 公钥
	 * @param rawData 要加密的原始数据
	 * @return 密文
	 * @throws Exception
	 */
	public static byte[] encrypt_(PublicKey publicKey, byte[] rawData) throws Exception {
        try {
       	 /**填充方式*/
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte[] cipherData = cipher.doFinal(rawData);
            
            return cipherData;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
	
	 /**加密
	  * 注意填充方式：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	  * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	 * @param publicKey 公钥
	 * @param rawData 要加密的原始数据
	 * @return 密文
	 * @throws Exception
	 */
	public static byte[] encrypt(PublicKey publicKey, byte[] rawData) throws Exception {
         try {
        	 /**填充方式*/
             Cipher cipher = Cipher.getInstance("RSA");
             cipher.init(Cipher.ENCRYPT_MODE, publicKey);
             int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
             // 加密块大小为127
             // byte,加密后为128个byte;因此共有2个加密块，第一个127
             // byte第二个为1个byte
             int outputSize = cipher.getOutputSize(rawData.length);// 获得加密块加密后块大小
             int leavedSize = rawData.length % blockSize;
             int blocksSize = leavedSize != 0 ? rawData.length / blockSize + 1
                     : rawData.length / blockSize;
             byte[] cipherMsg = new byte[outputSize * blocksSize];
             int i = 0;
             while (rawData.length - i * blockSize > 0) {
                 if (rawData.length - i * blockSize > blockSize)
                     cipher.doFinal(rawData, i * blockSize, blockSize, cipherMsg, i
                             * outputSize);
                 else
                     cipher.doFinal(rawData, i * blockSize, rawData.length - i
                             * blockSize, cipherMsg, i * outputSize);
                 // 这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到
                 // ByteArrayOutputStream中，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了
                 // OutputSize所以只好用dofinal方法。
                 i++;
             }
             return cipherMsg;
         } catch (Exception e) {
             throw new Exception(e.getMessage());
         }
     }
	
	
	/**
	 * 
	 * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象。
	 * 
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	 * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	 * 
	 * @param modulus
	 *            系数。
	 * 
	 * @param publicExponent
	 *            专用指数。
	 * 
	 * @return RSA专用公钥对象。
	 */

	public static RSAPublicKeySpec getRSAPublidKey(String hexModulus,
			String hexPublicExponent) {
		BigInteger m = new BigInteger(hexModulus, 16);
		BigInteger e = new BigInteger(hexPublicExponent, 16);
		
		RSAPublicKeySpec PubKeySpec = new RSAPublicKeySpec(m, e);
		
		return PubKeySpec;
	}
	
	 /** 
	  * 使用模和指数生成RSA私钥 对象
	  * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	  * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	  *  
	  * @param modulus 
	  *            模 
	  * @param exponent 
	  *            指数 
	  * @return 私钥对象
	  */  
public static RSAPrivateKeySpec getRSAPrivateKey(String modulus, String exponent) {  
       BigInteger mod = new BigInteger(modulus);  
       BigInteger exp = new BigInteger(exponent);  
       RSAPrivateKeySpec rsaPrivateKeySpec = null;
		
       rsaPrivateKeySpec = new RSAPrivateKeySpec(mod, exp);  
    
   return rsaPrivateKeySpec;  
}  

	/**根据 公钥对象生成 公钥
	 * @param rsaPublicKeySpec 公钥对象
	 * @return
	 */
	public static PublicKey getPublidKey(RSAPublicKeySpec rsaPublicKeySpec) {
		KeyFactory fact;
		PublicKey publicKey = null;
		try {
			fact = KeyFactory.getInstance("RSA");
			
			publicKey = fact.generatePublic(rsaPublicKeySpec);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeySpecException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
				
		return publicKey;
	}
	
	/** 
	 * 使用私钥对象生成RSA私钥 
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	 * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	 *  
	 * @param modulus 
	 *            模 
	 * @param exponent 
	 *            指数 
	 * @return 私钥
	 */  
	public static PrivateKey getPrivateKey(RSAPrivateKeySpec rsaPrivateKeySpec) {  
	    KeyFactory keyFactory;
	    PrivateKey privateKey = null;
	    
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	 
		return privateKey;  
	}  
	
	/**
	 * 
	 * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象，并生成公钥。
	 * 
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	 * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	 * 
	 * @param modulus
	 *            系数。
	 * 
	 * @param publicExponent
	 *            专用指数。
	 * 
	 * @return RSA公钥。
	 */

	public static PublicKey getPublidKey(String hexModulus,
			String hexPublicExponent) {
		BigInteger m = new BigInteger(hexModulus, 16);
		BigInteger e = new BigInteger(hexPublicExponent, 16);
		
		RSAPublicKeySpec rsaPubKeySpec = new RSAPublicKeySpec(m, e);
		KeyFactory fact;
		PublicKey publicKey = null;
		try {
			fact = KeyFactory.getInstance("RSA");
			
			//PublicKey pubKey = fact.generatePublic(PubKeySpec);
			publicKey = fact.generatePublic(rsaPubKeySpec);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeySpecException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return publicKey;
	}
	 
	 /** 
	  * 使用模和指数生成RSA私钥 
	  * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
	  * /None/NoPadding】 如果调用了加密机【RSA/ECB/NoPadding】
	  *  
	  * @param modulus 
	  *            模 
	  * @param exponent 
	  *            指数 
	  * @return 私钥
	  */  
 public static PrivateKey getPrivateKey(String modulus, String exponent) {  
         BigInteger mod = new BigInteger(modulus);  
         BigInteger exp = new BigInteger(exponent);  
         KeyFactory keyFactory;
         PrivateKey privateKey = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec rsaPrivatekeySpec = new RSAPrivateKeySpec(mod, exp);  
			privateKey = keyFactory.generatePrivate(rsaPrivatekeySpec);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
      
     return privateKey;  
 }  
 
	/**
	 * 解析公钥字符串，得到Modulus Exponent
	 * 
	 * @param key
	 *            公钥的ascii
	 * @return mod exp 数组{mod,exp}
	 */
	public static String[] parsePublickey(String key) {
		// 计算key的字节长度并保存到数组
		String[] bytes = new String[key.length() / 2];
		for (int i = 0; i < key.length() / 2; i++) {
			bytes[i] = key.substring(i * 2, i * 2 + 2);
		}
		// Byte Length
		int index = 1;
		int length = Integer.parseInt(bytes[index], 16);
		if (length > 128)
			index += length - 128;

		// Modulus Length
		index += 2;
		length = Integer.parseInt(bytes[index], 16);
		if (length > 128) {
			int i = length - 128;
			String lenStr = "";
			for (int j = index + 1; j < index + i + 1; j++)
				lenStr += bytes[j];

			index += i;
			length = Integer.parseInt(lenStr, 16);
		}

		// 保存mod值
		StringBuffer modBuff = new StringBuffer();
		for (int i = index + 1; i < index + 1 + length; i++)
			modBuff.append(bytes[i]);

		// Exponent Length
		index += length + 2;
		length = Integer.parseInt(bytes[index], 16);
		if (length > 128) {
			int i = length - 128;
			String lenStr = "";
			for (int j = index + 1; j < index + i + 1; j++)
				lenStr += bytes[j];

			index += i;
			length = Integer.parseInt(lenStr, 16);
		}

		// 保存exponent值
		index += 1;
		StringBuffer expBuff = new StringBuffer();
		for (int i = index; i < index + length; i++)
			expBuff.append(bytes[i]);

		return new String[] { modBuff.toString(), expBuff.toString() };
	}

}
