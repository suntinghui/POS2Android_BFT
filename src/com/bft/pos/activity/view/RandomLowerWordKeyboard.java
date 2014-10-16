package com.bft.pos.activity.view;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.inputmethodservice.Keyboard;

/**
 * 字母密码键盘
 */
public class RandomLowerWordKeyboard extends Keyboard {
	
	public String randomString = "";

	public RandomLowerWordKeyboard(Context context, int xmlLayoutResId) {
		super(context, xmlLayoutResId);

		randomString = getRandomString();
		
		char[] c = randomString.toCharArray();
		List<Key> list = this.getKeys();

		for (Key key : list) {
			if (key.codes[0] >= 97 && key.codes[0] <= 122) {
				key.label = String.valueOf(c[key.codes[0] - 97]);
				int[] newcodes = new int[] { key.codes[0], c[key.codes[0] - 97] };
				key.codes = newcodes;
			}
		}
	}

	public RandomLowerWordKeyboard(Context context, int xmlLayoutResId, int modeId) {
		super(context, xmlLayoutResId, modeId);
	}

	public RandomLowerWordKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}

	private String getRandomString() {
		int[] a = new int[26];
		for (int i = 0; i < 26; i++) {
			a[i] = i+97;
		}

		Random r = new Random();
		for (int i = 0; i < 26; i++) {
			int m = a[i];
			int k = i + r.nextInt(26) % (26 - i);
			a[i] = a[k];
			a[k] = m;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 26; i++) {
			sb.append((char)a[i]);
		}

		return sb.toString();
	}
	
}
