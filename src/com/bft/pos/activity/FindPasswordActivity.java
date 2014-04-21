package com.bft.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.dynamic.core.Event;

/**
 * 找回密码
 * 
 * @创建者 Fancong
 */
public class FindPasswordActivity extends BaseActivity implements
		OnClickListener {
	private Button btn_back;// 返回
	private Button btn_sms;// 获取短信验证码
	private Button btn_confirm;// 下一步
	private TextWithIconView et_phone;// 安全手机号
	private TextWithIconView et_name;// 真实姓名
	private TextWithIconView et_identy_card;// 身份证号

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
			if (et_phone.getText().length() == 0) {
				FindPasswordActivity.this.showToast("手机号不能为空!");
			} else {
				FindPasswordActivity.this.showToast("短信已发送，请注意查收!");
				actionGetSms();
			}
			break;
		case R.id.btn_confirm:// 下一步
			if (checkValue()) {
				Intent confirm = new Intent(FindPasswordActivity.this,
						ModifyLoginPwdActivity.class);
				startActivity(confirm);
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 获取验证码
	 */
	@SuppressLint("SimpleDateFormat")
	private void actionGetSms() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "getSms", null);
			event.setTransfer("089006");
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mobNo", et_phone.getText().toString());
			map.put("sendTime", date);
			map.put("type", "0");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 判断输入框的输入内容
	 */
	private Boolean checkValue() {
		if (et_phone.getText().length() == 0) {
			this.showToast("手机号不能为空！");
			return false;
		}
		if (et_name.getText().length() == 0) {
			this.showToast("真实姓名不能为空！");
			return false;
		}
		if (et_identy_card.getText().length() == 0) {
			this.showToast("身份证号不能为空！");
			return false;
		}
		return true;
	}
}