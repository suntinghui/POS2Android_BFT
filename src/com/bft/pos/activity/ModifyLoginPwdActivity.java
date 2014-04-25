package com.bft.pos.activity;

import java.util.Timer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;

/**
 * 修改登录密码
 */
public class ModifyLoginPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_pwd_old;
	private PasswordWithIconView et_pwd_new;
	private PasswordWithIconView et_pwd_confirm;
	private EditText et_sms;
	private Button btn_back,btn_sms,btn_confirm;
	private int recLen = 10;
	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_modify_login_pwd);
		init();
	}

	/*
	 * 初始化控件
	 */
	private void init() {
		et_pwd_old = (PasswordWithIconView) this.findViewById(R.id.et_pwd_old);
		et_pwd_old.setIconAndHint(R.drawable.icon_pwd, "原登录密码");
		et_pwd_new = (PasswordWithIconView) this.findViewById(R.id.et_pwd_new);
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新登录密码");
		et_pwd_confirm = (PasswordWithIconView) this
				.findViewById(R.id.et_pwd_confirm);
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认登陆密码");
		et_sms=(EditText) this.findViewById(R.id.et_sms);
		btn_sms=(Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		btn_back=(Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm=(Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_sms:
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				
			}
			break;
		default:
			break;
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