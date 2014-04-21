package com.bft.pos.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class PhotoFileUtils {

	private String SDCardRoot;

	public PhotoFileUtils() {
		// 得到当前外部存储设备目录
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
	}

	/**
	 * 在SDCard上创建文件
	 * */
	public File createFileInSDCard(String fileName, String dir)
			throws Exception {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		System.out.println("file-->" + file);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SDCard上创建目录
	 * */
	public File createSDDir(String dir) {
		File file = new File(SDCardRoot + dir + File.separator);
		System.out.println(file.mkdirs());
		return file;
	}

	/**
	 * 判断SDCard上的文件是否存在
	 * */
	public boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}

	/**
	 * 获取文件
	 * 
	 * @param fileName
	 * @param path
	 * @return
	 */
	public File getFile(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @param path
	 */
	public void deleteFile(String fileName, String path) {
		System.out.println("delete");
		File file = new File(SDCardRoot + path + File.separator + fileName);
		file.delete();
	}

	/**
	 * 将一个InputStream写入SDCard
	 * */
	public File writeToSDCardFromInput(String path, String fileName,
			InputStream inputStream) {
		File file = null;
		OutputStream outputStream = null;

		try {
			createSDDir(path);
			file = createFileInSDCard(fileName, path);

			outputStream = new FileOutputStream(file);
			byte data[] = new byte[4 * 1024];
			int tmp;
			while ((tmp = inputStream.read(data)) != -1) {
				outputStream.write(data, 0, tmp);
			}
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * @param file
	 * @return
	 */
	public byte[] getBytesFromFile(File file) {
		if (file == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1) {
				out.write(b, 0, n);
			}

			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {

		}
		return null;
	}

}
