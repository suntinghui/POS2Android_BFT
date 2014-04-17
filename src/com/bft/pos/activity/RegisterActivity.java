package com.bft.pos.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;

/**
 * 注册界面
 * 
 * @创建者 Fancong
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_next;
	private TextWithIconView et_name, et_id_card, et_phone_num;
	private EditText et_sms;
	private ImageButton ib_agree;
	private Button btn_protocol;

	private boolean isAgree = false;// 是否同意服务协议
	private int recLen = 10;
	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		init();
	}

	/*
	 * 初始化控件
	 */
	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);// 获取短信验证码
		btn_sms.setOnClickListener(this);
		btn_next = (Button) this.findViewById(R.id.btn_next);// 下一步
		btn_next.setOnClickListener(this);
		btn_protocol = (Button) this.findViewById(R.id.btn_protocol);// 用户协议
		btn_protocol.setOnClickListener(this);
		et_name = (TextWithIconView) this.findViewById(R.id.et_name);// 真实姓名
		et_name.setHintString("真实姓名");
		et_id_card = (TextWithIconView) this.findViewById(R.id.et_id_card);// 身份证号
		et_id_card.setIcon(R.drawable.icon_idcard);
		et_id_card.setHintString("身份证");
		et_id_card.getEditText().setFilters(
				new InputFilter[] { new InputFilter.LengthFilter(18) });
		et_phone_num = (TextWithIconView) this.findViewById(R.id.et_phone_num);// 手机号
		et_phone_num.setInputType(InputType.TYPE_CLASS_NUMBER);
		et_phone_num.setHintString("手机号码");
		et_phone_num.setIcon(R.drawable.icon_phone);
		et_sms = (EditText) this.findViewById(R.id.et_sms);// 验证码
		et_sms.setInputType(InputType.TYPE_CLASS_NUMBER);
		ib_agree = (ImageButton) this.findViewById(R.id.ib_agree);// 同意服务协议
		ib_agree.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_sms:
			if (et_phone_num.getText().length() == 0) {
				RegisterActivity.this.showToast("手机号不能为空!");
			} else {
				RegisterActivity.this.showToast("短信已发送，请注意查收!");
				actionGetSms();
			}
			break;
		case R.id.ib_agree:
			if (isAgree) {
				ib_agree.setBackgroundResource(R.drawable.remeberpwd_n);
			} else {
				ib_agree.setBackgroundResource(R.drawable.remeberpwd_s);
			}
			isAgree = !isAgree;
			break;
		case R.id.btn_protocol:
			actionShowProtocol();
			break;
		case R.id.btn_next:
			if (checkValue()) {
				actionNext();
			}
			break;
		}
	}

	/*
	 * 填完个人信息后进入密码设置界面
	 */
	private void actionNext() {
		Intent next = new Intent(RegisterActivity.this,
				SetLoginPwdActivity.class);
		startActivity(next);
	}

	/*
	 * 输入框不能为空
	 */
	private Boolean checkValue() {
		if (et_id_card.getText().length() == 0) {
			this.showToast("身份证号不能为空!");
			return false;
		}
		if (et_name.getText().length() == 0) {
			this.showToast("姓名不能为空！");
			return false;
		}
		if (et_phone_num.getText().length() == 0) {
			this.showToast("手机号不能为空！");
			return false;
		}
		if (!isAgree) {
			this.showToast("请先阅读并同意服务协议！");
			return false;
		}
		return true;
	}

	/*
	 * 获取验证码
	 */
	@SuppressLint("SimpleDateFormat")
	private void actionGetSms() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = dateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "getSms", null);
			event.setTransfer("089006");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", et_phone_num.getText());
			map.put("time", date);
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (ViewException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				btn_sms.setText(String.format("请等待短信，%d秒", recLen));
				if (recLen < 0) {
					btn_sms.setText("获取短信校验码");
					timer.cancel();
				}
			}
		}
	};

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			recLen--;
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	/*
	 * 显示服务协议
	 */
	private void actionShowProtocol() {
		Intent intent = new Intent(RegisterActivity.this,
				ShowProtocolActivity.class);
		RegisterActivity.this.startActivity(intent);
	}
}