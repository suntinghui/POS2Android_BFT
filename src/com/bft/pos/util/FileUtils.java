package com.bft.pos.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;


public class FileUtils {
	
	public static byte[] file2byte(String filePath) {

		/** 输出流 */
		FileOutputStream fos = null;
		/** 输入流 */
		FileInputStream ins = null;
		byte[] lenbufMsg = new byte[128];
		try {
			/***/
			ins = new FileInputStream(filePath);
			
			long k = 0;
			char c;
			int readCount = 0; // 已经成功读取的字节的个数
			while (readCount < 128) {
				/** 一次性拿到 */
				readCount += ins.read(lenbufMsg, readCount, 128 - readCount);
			}
			if (k == -1) {
				ins.close();
			}
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lenbufMsg;
	}
	/**字节写入进文件
	 * @param filePath 写入的文件完整路径
	 * @param fileName	文件名
	 * @param content	写入的内容
	 * @throws IOException
	 */
	public static void byteWriteFile(String filePath, String fileName,
			byte[] content) throws IOException {
		Log.d("############写文件#############", "");
		File dir = new File(filePath);
		if (!dir.exists())
			dir.mkdir();
	
		filePath = filePath + File.separator + fileName;
		
		FileOutputStream fos = new FileOutputStream(filePath);
		fos.write(content);
		fos.flush();
		fos.close();
	}
	
	/**
	 * 追加内容
	 * 
	 * @param path
	 *            请求报文写入路径
	 * @param fileName
	 *            文件名
	 * @param content
	 *            内容（写入内容）
	 * @return
	 */
	public static boolean writeFile_add(String path, String fileName,
			String content) {
		Log.d("\t############写文件#############","");
		boolean flag = false;
		StringBuffer buf = new StringBuffer();
		buf.append(content.trim());

		String filePath = path;
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdir();

		filePath = filePath + File.separator + fileName;
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(filePath), true);
			fw.write("\n############################ Write:'" + fileName
					+ "' ########################################");
			fw.write("\n");
			fw.write(buf.toString());
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * 不可追加内容（每执行一次就会替换一次文件内容）
	 * 
	 * @param path
	 *            请求报文写入路径
	 * @param fileName
	 *            文件名
	 * @param content
	 *            内容（写入内容）
	 * @param append
	 * 			  追加（
	 * 					true：	在该文件中追加内容，不会替换;
	 * 					false：	不追加内容，每执行一次该文件都会替换一次全新内容;
	 * 					）        
	 * @return
	 */
	public static boolean writeFile(String path, String fileName, String content, boolean append) {
		Log.d("\t############写文件#############","");
		boolean flag = false;
		StringBuffer buf = new StringBuffer();
		buf.append(content.trim());

		String filePath = path;
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdir();

		filePath = filePath + File.separator + fileName;
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(filePath), append);
			fw.write(buf.toString());
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * BufferedReader
	 * 
	 * @param path
	 *            要读取文件所在的目录路径
	 * @param fileName
	 *            要读取文件名称
	 * @param type 读取函数类型 1：  BufferedReader 2、3、         
	 * @return
	 */
	public static String readerFile(String path, String fileName,int type) {
		Log.i("\t############读文件#############","");
		String result = null;
		String data = null;
		BufferedReader br = null;
		FileReader fr = null;
		StringBuffer sb = null;
		
		path += File.separator + fileName;

		File file = new File(path);
		
		if(!file.canRead()){
			return null;
		}
	
		switch (type) {
		case 1:
			sb = new StringBuffer();
			try {
				
				br = new BufferedReader(new InputStreamReader(new FileInputStream(
						file)));
				if (br != null) {
					while ((data = br.readLine())!= null) {
						sb.append(data);
					}
				}
				result =  sb.toString();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 2:
			int num = 0;
			try {
				
				fr = new FileReader(file);
				while((num = fr.read())!=-1){
					System.out.println("@@:" + num);
				}
				result = sb.toString();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 3:
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * 方法描述：根据指定的网址把该页面写入到文件
	 * 
	 * @param httpURL
	 *            网址地址
	 * @param charset
	 *            编码格式
	 * @param path_fileName
	 *            指定写入的路径及文件名
	 * @return false为写入失败 true成功
	 * @throws IOException
	 */
	public boolean webpageWriteFile(String httpURL, String charset,
			String path_fileName) throws IOException {
		URL url;
		boolean result = false;
		try {
			url = new URL(httpURL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			InputStream in = null;
			in = url.openStream();
			boolean isOk = inWriteFile(in, charset, path_fileName);
			if (isOk = false) {
				result = false;
			} else {
				result = true;
			}
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
//			throw new IOException(e);
		}
		return result;
	}

	/**
	 * 方法描述：根据指定的流写入到文件
	 * 
	 * @param in
	 *            输入流
	 * @param charset
	 *            编码格式
	 * @param path_fileName
	 *            指定写入的路径及文件名
	 * @return false为写入失败 true成功
	 * @throws IOException
	 */
	public static boolean inWriteFile(InputStream in, String charset,
			String path_fileName) throws IOException {
		boolean result = false;
		if (charset == null || "".equals(charset)) {
			charset = "utf-8";
		}
		String rLine = null;
		PrintWriter pw = null;
		if (in != null) {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					in, charset));
			FileOutputStream fo = new FileOutputStream(path_fileName);
			OutputStreamWriter writer = new OutputStreamWriter(fo, "utf-8");
			pw = new PrintWriter(writer);
			while ((rLine = bReader.readLine()) != null) {
				String tmp_rLine = rLine;
				int str_len = tmp_rLine.length();
				if (str_len > 0) {
					// 把行写入到文件
					pw.println(tmp_rLine);
					pw.flush();
				}
				tmp_rLine = null;
			}
			result = true;
			in.close();
			pw.close();
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * 方法描述：根据指定的流写入到文件（打印写入数据）
	 * 
	 * @param is
	 *            输入流
	 * @param charset
	 *            编码格式
	 * @param path_fileName
	 *            指定写入的路径及文件名
	 * @return 以字符串形式打印返回写入的数据
	 * @throws IOException
	 */
	public String inWriteFile2(InputStream is, String charset,
			String path_fileName) throws IOException {
		StringBuffer s = new StringBuffer();
		if (charset == null || "".equals(charset)) {
			charset = "utf-8";
		}
		String rLine = null;
		BufferedReader bReader = new BufferedReader(new InputStreamReader(is,
				charset));
		PrintWriter pw = null;

		FileOutputStream fo = new FileOutputStream(path_fileName);
		OutputStreamWriter writer = new OutputStreamWriter(fo, "utf-8");
		pw = new PrintWriter(writer);
		while ((rLine = bReader.readLine()) != null) {
			String tmp_rLine = rLine;
			int str_len = tmp_rLine.length();
			if (str_len > 0) {
				s.append(tmp_rLine);
				// 把行写入到文件
				pw.println(tmp_rLine);
				pw.flush();
			}
			tmp_rLine = null;
		}
		is.close();
		pw.close();
		return s.toString();
	}
}
