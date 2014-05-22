package com.dhcc.pos.packets.util;

import java.util.ArrayList;

import android.util.Log;


public class CommonUtil {

	/**2进制字符串 转 每10位一组字符串
	 * 0000000000 0000000000 0000000000 0000000000 0000000000 0000000000 0000
	 * @param str 2进制字符串（64位）
	 * @return
	 */
	public static String str2FormatStr(String str){
		ArrayList<String> al = new ArrayList<String>();
		if(str.length() != 64)
			Log.e("传入的参数不够64位！！！","") ;
//		if(str.length() != 128)
//			log.error("传入的参数不够128位！！！") ;
		
		//		for(int i=0; i<6;i++){
		//			if(i!=6){
		//				al.add(i, str.substring(i*10,i+1*10));
		//			}else{
		//				al.add(i, str.substring(i*10));
		//
		//			}
		//		}
		if(str.length() == 64){
			al.add(0, str.substring(0,10));
			al.add(1, str.substring(10,20));
			al.add(2, str.substring(20,30));
			al.add(3, str.substring(30,40));
			al.add(4, str.substring(40,50));
			al.add(5, str.substring(50,60));
			al.add(6, str.substring(60));
		}else if(str.length() == 128){
			al.add(0, str.substring(0,10));
			al.add(1, str.substring(10,20));
			al.add(2, str.substring(20,30));
			al.add(3, str.substring(30,40));
			al.add(4, str.substring(40,50));
			al.add(5, str.substring(50,60));
			al.add(5, str.substring(60,70));
			al.add(5, str.substring(70,80));
			al.add(5, str.substring(80,90));
			al.add(5, str.substring(90,100));
			al.add(5, str.substring(100,110));
			al.add(5, str.substring(110,120));
			al.add(6, str.substring(120));
		}
		return al.toString();
	}
}
