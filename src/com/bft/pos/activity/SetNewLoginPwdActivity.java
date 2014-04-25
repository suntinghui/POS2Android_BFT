package com.bft.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;

/**
 * 找回密码,验证身份后设置新的密码
 */
public class SetNewLoginPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_pwd_new;// 新的登陆密码
	private PasswordWithIconView et_pwd_confirm;// 确认密码
	private Button btn_back, btn_confirm;
	private TextWithIconView et_sms;// 验证码
	private String smscode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_new_login_pwd);
		init();
		// 身份验证后直接收取到一个短信验证码
		actionGetSms();
	}

	/*
	 * 初始化控件
	 */
	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);// 确定
		btn_confirm.setOnClickListener(this);
		et_pwd_new = (PasswordWithIconView) this.findViewById(R.id.et_pwd_new);// 新登陆密码
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新登陆密码");
		et_pwd_confirm = (PasswordWithIconView) this
				.findViewById(R.id.et_pwd_confirm);// 确认登陆密码
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认登陆密码");
		et_sms = (TextWithIconView) this.findViewById(R.id.et_sms);// 短信校验码
		et_sms.setHintString("短信校验码");
		et_sms.setIcon(R.drawable.icon_mail);
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
			map.put("mobNo", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			map.put("sendTime", date);
			map.put("type", "0");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 按钮监听事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				setNewPwd();
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 设置新的登陆密码
	 */
	private void setNewPwd() {
		try {
			String type = "0";
			Event event = new Event(null, "getPassword", null);
			event.setTransfer("089015");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("smscode", smscode);
			map.put("type", type);
			map.put("tel", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			String pwd = et_pwd_confirm.getEncryptPWD();
			map.put("logpass", pwd);
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
		if (et_pwd_new.getText().length() == 0) {
			this.showToast("密码不能为空！");
			return false;
		}
		if (et_pwd_confirm.getText().length() == 0) {
			this.showToast("确认密码不能为空！");
			return false;
		}
		if (!et_pwd_new.getText().equals(et_pwd_confirm.getText())) {
			this.showToast("密码输入不一致，请重新输入！");
			et_pwd_new.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		return true;
	}
}