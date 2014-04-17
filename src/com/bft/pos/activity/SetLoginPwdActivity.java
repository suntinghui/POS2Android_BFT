package com.bft.pos.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;

/**
 * 填写完个人注册信息后设置登陆密码
 * 
 * @创建者 Fancong
 */
public class SetLoginPwdActivity extends BaseActivity implements
		OnClickListener {
	private Button btn_back, btn_register;
	private PasswordWithIconView et_login_pwd, et_login_pwd_again;
	private TextWithIconView et_login_name;
	private EditText et_sms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_login_pwd);
		init();
	}
	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_register = (Button) this.findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
//		btn_sms = (Button) this.findViewById(R.id.btn_sms);
//		btn_sms.setOnClickListener(this);
		et_login_name=(TextWithIconView) this.findViewById(R.id.et_login_name);
		et_login_name.setIcon(R.drawable.icon_login_1);
		et_login_name.setHintString("登录名");
		et_login_pwd=(PasswordWithIconView) this.findViewById(R.id.et_login_pwd);
		et_login_pwd.setIconAndHint(R.drawable.icon_pwd, "登陆密码");
		et_login_pwd_again=(PasswordWithIconView) this.findViewById(R.id.et_login_pwd_again);
		et_login_pwd_again.setIconAndHint(R.drawable.icon_pwd, "再次输入登陆密码");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:// 返回
			finish();
			break;
		case R.id.btn_register:// 立即注册
			if(checkValue()){
				actionRegister();
			}
			break;
		default:
			break;
		}
	}
	
	/*
	 * 注册
	 */
	private void actionRegister(){
		try {
			Event event = new Event(null, "register", null);
			event.setTransfer("089001");
			String fsk = "Get_PsamNo|null";
			if (Constant.isAISHUA) {
				fsk = "getKsn|null";
			}
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", et_login_name.getText());
			map.put("smscode", et_sms.getText().toString());
			map.put("logpass", et_login_pwd_again.getEncryptPWD());
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 输入框不能为空
	 */
	private Boolean checkValue() {
		if (et_login_name.getText().length() == 0) {
			this.showToast("姓名不能为空！");
			return false;
		}
		if (et_login_pwd.getText().length() == 0) {
			this.showToast("密码不能为空！");
			return false;
		}
		if (et_login_pwd_again.getText().length() == 0) {
			this.showToast("密码不能为空！");
			return false;
		}
		if (!(et_login_pwd.getMd5PWD().equals(et_login_pwd_again.getMd5PWD()))) {
			this.showToast("密码输入不一致，请重新输入！");
			et_login_pwd.setText("");
			et_login_pwd_again.setText("");
			return false;
		}
		return true;
	}
}