package com.bft.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconView;

/**
 * 找回密码界面
 * 
 * @author Fancong
 */
public class FindPasswordActivity extends BaseActivity implements
		OnClickListener {
	private Button btn_back;// 返回
	private Button btn_sms;// 获取短信验证码
	private Button btn_confirm;// 下一步
	private TextWithIconView et_phone;
	private TextWithIconView et_name;
	private TextWithIconView et_identy_card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		init();
	}

	/*
	 * 初始化控件
	 */
	private void init() {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		et_phone = (TextWithIconView) this.findViewById(R.id.et_phone);
		et_phone.setInputType(InputType.TYPE_CLASS_NUMBER);
		et_phone.setHintString("手机号码");
		et_phone.setIcon(R.drawable.icon_phone);
		et_name = (TextWithIconView) this.findViewById(R.id.et_name);
		et_identy_card = (TextWithIconView) this
				.findViewById(R.id.et_identy_card);
		et_identy_card.setIcon(R.drawable.icon_idcard);
		et_name.setHintString("真实姓名");
		et_identy_card.setHintString("身份证");
		et_identy_card.getEditText().setFilters(
				new InputFilter[] { new InputFilter.LengthFilter(18) });
	}

	/*
	 * 按钮的点击监听事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:// 返回
			finish();
			break;
		case R.id.btn_sms:// 获取短信验证码

			break;
		case R.id.btn_confirm:// 下一步
			Intent confirm = new Intent(FindPasswordActivity.this,
					ModifyPaymentPwdActivity.class);
			startActivity(confirm);
			break;
		default:
			break;
		}
	}
}