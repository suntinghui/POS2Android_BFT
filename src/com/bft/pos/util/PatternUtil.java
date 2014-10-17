package com.bft.pos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class PatternUtil {

	public static boolean isValidEmail(String email) {
		String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher mat = pattern.matcher(email);
		return mat.matches();
	}

	public static boolean isLegalPassword(String pswd) {
		boolean ret = false;
		String ftmt = "[0-9a-zA-Z_]{5,10}";
		Pattern pattern = Pattern.compile(ftmt);
		Matcher mat = pattern.matcher(pswd);
		ret = mat.matches();
		return ret;
	}

	public static boolean isValidIDNum(String idNum) {
		String regex = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
		Pattern pattern = Pattern.compile(regex);
		Matcher mat = pattern.matcher(idNum);
		return mat.matches();
	}

	/** 
     * 验证手机格式 
     */  
    public static boolean isMobileNO(String mobiles) {  
        /* 
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 
        联通：130、131、132、152、155、156、185、186 
        电信：133、153、180、189、（1349卫通） 
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
        */  
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。  
        if (TextUtils.isEmpty(mobiles)) return false;  
        else return mobiles.matches(telRegex);  
       }  
    
	/**
	 * 校验登录密码是否合法
	 * 
	 * 输入长度是8－20位长 密码必须同时包含大小写字母，可以有数字与特殊字符
	 * 
	 * @param cnt
	 * @param str
	 *            输入框的内容
	 * @return
	 */
	public static boolean checkLoginPWD(Context cnt, String str) {
		if (null == str || "".equals(str.trim())) {
			//Toast.makeText(cnt, "密码不能为空", Toast.LENGTH_SHORT).show();
			PopupMessageUtil.showMSG_middle2("密码不能为空");
			return false;
		}

		if (str.trim().length() < 8 || str.trim().length() > 20) {
			//Toast.makeText(cnt, "密码长度必须在8－20位之间", Toast.LENGTH_SHORT).show();
			PopupMessageUtil.showMSG_middle2("密码长度必须在8－20位之间");
			return false;
		}

		if (!Pattern.compile(".*[a-z]+.*").matcher(str.trim()).matches()) {
			//Toast.makeText(cnt, "密码必须同时包含大小写字母", Toast.LENGTH_SHORT).show();
			PopupMessageUtil.showMSG_middle2("密码必须同时包含大小写字母");
			return false;
		}

		if (!Pattern.compile(".*[A-Z]+.*").matcher(str.trim()).matches()) {
			//Toast.makeText(cnt, "密码必须同时包含大小写字母", Toast.LENGTH_SHORT).show();
			PopupMessageUtil.showMSG_middle2("密码必须同时包含大小写字母");
			return false;
		}

		return true;
	}

}
