package com.bft.pos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
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
