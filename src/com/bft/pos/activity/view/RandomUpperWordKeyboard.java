package com.bft.pos.activity.view;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.inputmethodservice.Keyboard;

/**
 * 字母密码键盘
 */
public class RandomUpperWordKeyboard extends Keyboard {
	
	public RandomUpperWordKeyboard(Context context, int xmlLayoutResId, String randomString) {
		super(context, xmlLayoutResId);

		char[] c = randomString.toUpperCase().toCharArray();
		List<Key> list = this.getKeys();

		for (Key key : list) {
			if (key.codes[0] >= 65 && key.codes[0] <= 90) {
				key.label = String.valueOf(c[key.codes[0] - 65]);
				int[] newcodes = new int[] { key.codes[0], c[key.codes[0] - 65] };
				key.codes = newcodes;
			}
		}
	}

	public RandomUpperWordKeyboard(Context context, int xmlLayoutResId, int modeId) {
		super(context, xmlLayoutResId, modeId);
	}

	public RandomUpperWordKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}

}
