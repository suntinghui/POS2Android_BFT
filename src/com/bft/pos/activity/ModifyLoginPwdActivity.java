package com.bft.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.StringUtil;

/**
 * 修改登录密码
 */
public class ModifyLoginPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_pwd_old;
	private PasswordWithIconView et_pwd_new;
	private PasswordWithIconView et_pwd_confirm;
	private EditText et_sms;
	private Button btn_back, btn_sms, btn_confirm;
	private int recLen = 10;
	Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_modify_login_pwd);
		super.onCreate(savedInstanceState);
		initControl();
		// 身份验证后直接收取到一个短信验证码
		actionGetSms();
		this.setContentView(R.layout.activity_modify_login_pwd);
		initControl();
	}

	@Override
	public void initControl() {
		et_pwd_old = (PasswordWithIconView) this.findViewById(R.id.et_pwd_old);
		et_pwd_old.setIconAndHint(R.drawable.icon_pwd, "原登录密码");
		et_pwd_new = (PasswordWithIconView) this.findViewById(R.id.et_pwd_new);
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新登录密码");
		et_pwd_confirm = (PasswordWithIconView) this
				.findViewById(R.id.et_pwd_confirm);
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认登陆密码");
		et_sms = (EditText) this.findViewById(R.id.et_sms);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_sms:
			this.showToast("短信已发送，请注意查收!");
			actionGetSms();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				HashMap<String, String> map = new HashMap<String, String>();
				String oldpass = StringUtil.MD5Crypto(StringUtil
						.MD5Crypto(et_pwd_old.getText().toString()
								.toUpperCase()
								+ et_pwd_old.getText())
						+ "www.payfortune.com");
				map.put("oldPass", oldpass);
				String newpass = StringUtil.MD5Crypto(StringUtil
						.MD5Crypto(et_pwd_new.getText().toString()
								.toUpperCase()
								+ et_pwd_new.getText())
						+ "www.payfortune.com");
				map.put("newPass", newpass);
				map.put("verifyCode", et_sms.getText().toString());
				map.put("type", "1");
				try {
					Event event = new Event(null, "modifyLoginPwd", null);
					event.setTransfer("089024");
					// 获取PSAM卡号
					String fsk = "Get_PsamNo|null";
					if (Constant.isAISHUA) {
						fsk = "getKsn|null";
					}
					event.setFsk(fsk);
					event.setStaticActivityDataMap(map);
					event.trigger();
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	private Boolean checkValue() {
		if (et_pwd_old.getText().length() == 0) {
			this.showToast("原密码不能为空！");
			return false;
		}
		if (et_pwd_new.getText().length() == 0) {
			this.showToast("新密码不能为空！");
			return false;
		}
		if (et_pwd_confirm.getText().length() == 0) {
			this.showToast("确认密码不能为空！");
			return false;
		}
		if (!et_pwd_new.getMd5PWD().equals(et_pwd_confirm.getMd5PWD())) {
			this.showToast("密码输入不一致，请重新输入！");
			et_pwd_new.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		return true;
	}
}
