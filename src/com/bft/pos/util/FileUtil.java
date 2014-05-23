package com.bft.pos.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.bft.pos.R;
import com.bft.pos.activity.BaseActivity;
import com.bft.pos.activity.view.ShowProgressHudTask;
import com.bft.pos.agent.client.Constant;

public class FileUtil {

	/**
	 * 不可追加内容（每执行一次就会替换一次文件内容）
	 * 
	 * @param fileName	(自动填充文件夹路径)
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
	public static boolean writeFile(String fileName, String content, boolean append) {
		System.out.println("\t############写文件#############");
		boolean flag = false;
		StringBuffer buf = new StringBuffer();
		buf.append(content.trim());

		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(Constant.ASSETSPATH+fileName), append);
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
	/**inputstream转换字符串
	 * @param is
	 * @return 字符串
	 */
	public static String convertStreamToString(InputStream is) {   
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
		StringBuilder sb = new StringBuilder();   
		String line = null;   
		try {   
			while ((line = reader.readLine()) != null) {   
				sb.append(line + "/n");   
			}   
		} catch (IOException e) {   
			e.printStackTrace();   
		} finally {   
			try {   
				is.close();   
			} catch (IOException e) {   
				e.printStackTrace();   
			}   
		}   
		return sb.toString();   
	}   
	// 从手机中读取文件
	public static InputStream readerFile(String fileName){
		InputStream stream = null;
		String path = Constant.ASSETSPATH + fileName;
		if (!fileName.endsWith(".xml"))
			path += ".xml";
		File file = new File(path);
		FileInputStream fileIS = null;
			try {
				fileIS = new FileInputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
//				throw new FileNotFoundException("文件未找到！",e1);
			}
		StringBuffer sb=new StringBuffer();
		BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
		String readString = new String();

		try{
			while((readString = buf.readLine())!= null){
				sb.append(readString);
			}
			stream = StringUtil.getInputStream(sb.toString());
			return stream;
		} catch(IOException e){
			return null;
		}
	}
	
	// 附件及所有文件都存放在本目录下
	public static String getDownloadPath(){
		// 其它程序无法访问
		// String path = ApplicationEnvironment.getInstance().getApplication().getFilesDir().getPath()+"/download/";
		String path = Environment.getExternalStorageDirectory()+"/YLT/install/";
		File file = new File(path);
		if (!file.exists()){
			// file.mkdir();
			// creating missing parent directories if necessary
			file.mkdirs(); 
		}
		
		return path;
	}
	
	// 判断文件是否存在
	public static boolean fileExists(String fileName){
		File file = new File(getDownloadPath()+fileName);
		return file.exists();
	}
	
	// 删除目录下的所有文件
	public static void deleteFiles(){
		File fileDir = new File(getDownloadPath());
		for (File file : fileDir.listFiles()){
			file.delete();
			Log.e("delete file", file.getPath());
		}
		
		Log.e("delete file", "已删除所有附件");
	}
	
	public static void deleteFile(String fileName){
		File file = new File(getDownloadPath()+fileName);
		if (file.exists()){
			file.delete();
		}
	}
	
	 public static void openHTML(String url){
		 if (null==url || url.trim().length()==0){
			 PopupMessageUtil.showMSG_middle2("无效的地址");
			 return;
		 }
		 
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 intent.setData(Uri.parse(url));
         BaseActivity.getTopActivity().startActivity(intent);
	 }
	
	// Open File
	
	public static void openFile(String fileName){
		
		new ShowProgressHudTask().execute("2000", "正在调用第三方程序");
		
		try{
			File file = new File(getDownloadPath()+fileName);
			
			if(null != file && file.isFile())
	        {
	            Intent intent = null;
	            Resources res = BaseActivity.getTopActivity().getResources();
	            
	            if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingText))){
	                intent = getTextFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingPdf))){
	                intent = getPdfFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingWord))){
	                intent = getWordFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingExcel))){
	                intent = getExcelFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingPPT))){
	                intent = getPPTFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingImage))){
	                intent = getImageFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingWebText))){
	                intent = getHtmlFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingPackage))){
	                intent = getApkFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);

	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingAudio))){
	                intent = getAudioFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingVideo))){
	                intent = getVideoFileIntent(file);
	                BaseActivity.getTopActivity().startActivity(intent);
	                
	            }else if(checkEndsWithInStringArray(fileName, res.getStringArray(R.array.fileEndingChm))){
	            	intent = getChmFileIntent(file);
	            	BaseActivity.getTopActivity().startActivity(intent);
	            	
	            }else{
	                BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "不支持此格式的文件！");
	            }
	            
	        } else {
	        	BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "文件下载出错，请重新下载");
	        }
		} catch(ActivityNotFoundException e){
			e.printStackTrace();
			BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "对不起，您需要先在手机中安装打开[ "+getFileExtension(fileName)+" ]文件的软件。");
		} catch(Exception e){
			e.printStackTrace();
			BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "对不起，打开文件出错，请与相关人员联系获取解决此问题的解决方案。");
		}
	}
	
	// 定义用于检查要打开的文件的后缀是否在遍历后缀数组中
	private static boolean checkEndsWithInStringArray(String checkItsEnd,  String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	private static String getFileExtension(String fileName){
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}
	
	//android获取一个用于打开HTML文件的intent
    private static Intent getHtmlFileIntent(File file)
    {
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }
  //android获取一个用于打开图片文件的intent
    private static Intent getImageFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }
    //android获取一个用于打开PDF文件的intent
    private static Intent getPdfFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/pdf");
      return intent;
    }
  //android获取一个用于打开文本文件的intent
  private static Intent getTextFileIntent(File file)
  {    
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Uri uri = Uri.fromFile(file);
    intent.setDataAndType(uri, "text/plain");
    return intent;
  }
 
  //android获取一个用于打开音频文件的intent
    private static Intent getAudioFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra("oneshot", 0);
      intent.putExtra("configchange", 0);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "audio/*");
      return intent;
    }
    //android获取一个用于打开视频文件的intent
    private static Intent getVideoFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra("oneshot", 0);
      intent.putExtra("configchange", 0);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "video/*");
      return intent;
    }
 
 
    //android获取一个用于打开CHM文件的intent
    private static Intent getChmFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/x-chm");
      return intent;
    }
 
  //android获取一个用于打开Word文件的intent
    private static Intent getWordFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/msword");
      return intent;
    }
  //android获取一个用于打开Excel文件的intent
    private static Intent getExcelFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/vnd.ms-excel");
      return intent;
    }
  //android获取一个用于打开PPT文件的intent
    private static Intent getPPTFileIntent(File file)
    {
      Intent intent = new Intent("android.intent.action.VIEW");
      intent.addCategory("android.intent.category.DEFAULT");
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Uri uri = Uri.fromFile(file);
      intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
      return intent;
    }
    //android获取一个用于打开apk文件的intent
    private static Intent getApkFileIntent(File file)
    {
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(android.content.Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(file),  "application/vnd.android.package-archive");  
        return intent;
    }
	

}
