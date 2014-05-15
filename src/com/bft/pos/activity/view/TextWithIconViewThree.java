package com.bft.pos.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bft.pos.R;

// 左边汉子 中间输入 只能数字，右边图片
public class TextWithIconViewThree extends LinearLayout {

	private LinearLayout rootLayout;
	// private ImageView iv_icon;
	private EditText editText;
	private TextView tv_num;

	public TextWithIconViewThree(Context context) {
		super(context);

		init(context);
	}

	public TextWithIconViewThree(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.text_with_icon_three, this);

		tv_num = (TextView) findViewById(R.id.tv_num);

		rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
		// iv_icon = (ImageView) this.findViewById(R.id.iv_icon);
		editText = (EditText) this.findViewById(R.id.ed_text);
	}

	public TextView getTextView() {
		return this.tv_num;
	}

	public EditText getEditText() {
		return this.editText;
	}

	// public void setIcon(int resource) {
	// iv_icon.setBackgroundResource(resource);
	// }

	public void setHintString(String hint) {
		editText.setHint(hint);
	}

	public void setText(String text) {
		editText.setText(text);
	}

	public void setTextViewText(String text) {
		tv_num.setText(text);
	}

	public String getsetTextViewText() {
		return editText.getText().toString();
	}

	public String getText() {
		return editText.getText().toString();
	}

	public void setRootLayoutBg(int resid) {
		this.rootLayout.setBackgroundResource(resid);
	}

	public void setHint() {
		rootLayout.setVisibility(View.INVISIBLE);
	}

	public void setInputType(int type) {
		editText.setInputType(type);
	}

}
