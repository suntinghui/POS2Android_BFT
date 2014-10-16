package com.bft.pos.activity.view;

import com.bft.pos.R;
import com.bft.pos.activity.BaseActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Vibrator;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class RandomPWDKeyboardView extends LinearLayout {

	private Context context = null;
	private Vibrator vibrator = null;

	private EditText editText = null;
	private PopupWindow popup = null;
	
	private KeyboardView keyboardView = null;
	private RandomLowerWordKeyboard randomLowerWordKeyboard = null;
	private RandomUpperWordKeyboard randomUpperWordKeyboard = null;
	
	private boolean isUpper = false;

	public RandomPWDKeyboardView(Context context) {
		super(context);

		init(context);
	}

	public RandomPWDKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	private void init(Context context) {
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.random_pwd_keyboardview, this);

		keyboardView = (KeyboardView) this.findViewById(R.id.keyboard_view);
		//randomDigitKeyboard = new RandomDigitKeyboard(context, R.xml.keyboard_digit);
		//randomLowerWordKeyboard = new RandomLowerWordKeyboard(context, R.xml.keyboard_word_lower);

		keyboardView.setKeyboard(new RandomDigitKeyboard(context, R.xml.keyboard_digit));
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setOnKeyboardActionListener(listener);
	}

	public void setEditText(EditText editText) {
		this.editText = editText;
	}

	public void setPopup(PopupWindow popup) {
		this.popup = popup;
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			// 震动提示
			if (null == vibrator)
				vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

			vibrator.vibrate(30);

			Editable editable = editText.getText();
			int start = editText.getSelectionStart();
			switch (primaryCode) {
			case Keyboard.KEYCODE_DONE: // 完成
				popup.dismiss();
				break;

			case Keyboard.KEYCODE_DELETE: // 回退
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
				break;
				
			case -10: // 切换大小写
				if (isUpper){
					keyboardView.setKeyboard(randomLowerWordKeyboard);
				} else {
					randomUpperWordKeyboard = new RandomUpperWordKeyboard(context, R.xml.keyboard_word_upper, randomLowerWordKeyboard.randomString);
					keyboardView.setKeyboard(randomUpperWordKeyboard);
				}
				keyboardView.invalidate();
				isUpper = !isUpper;
				break;
				
			case -12: // 切换到数字键盘
				keyboardView.setKeyboard(new RandomDigitKeyboard(context, R.xml.keyboard_digit));
				break;
				
			case -13: // 切换到字母键盘
				randomLowerWordKeyboard = new RandomLowerWordKeyboard(context, R.xml.keyboard_word_lower);
				keyboardView.setKeyboard(randomLowerWordKeyboard);
				keyboardView.invalidate();
				break;
				
			case 32: // 空格
				Toast.makeText(context, "密码不能包含空格", Toast.LENGTH_SHORT).show();
				break;

			case 57420: // 关于
				showAboutRandomKeyBoard();
				
				break;

			case 57419: // Go Left
				if (start > 0) {
					editText.setSelection(start - 1);
				}
				break;

			case 57421: // Go Right
				if (start < editText.length()) {
					editText.setSelection(start + 1);
				}
				break;

			case 57422: // 清空
				editText.setText("");
				break;

			default:
				editable.insert(start, Character.toString((char) keyCodes[1]));
				break;

			}
		}
	};

	private void showAboutRandomKeyBoard() {
		AlertDialog.Builder builder = new Builder(BaseActivity.getTopActivity());
		builder.setTitle("关于");
		builder.setMessage(R.string.pwdKeyboardTips);
		builder.setPositiveButton("知道了", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

}