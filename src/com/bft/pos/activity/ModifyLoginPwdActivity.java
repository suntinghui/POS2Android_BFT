package com.bft.pos.activity;

import java.util.HashMap;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ModifyLoginPwdActivity extends BaseActivity implements OnClickListener{
	private PasswordWithIconView et_pwd_new;//新的登陆密码
	private PasswordWithIconView et_pwd_confirm;//确认密码
	private Button btn_back, btn_sms, btn_confirm;
	private EditText et_sms;//验证码
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_login_pwd);
		init();
	}
	/*
	 * 初始化控件
	 */
	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);// 获取验证码
		btn_sms.setOnClickListener(this);
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);// 确定
		btn_confirm.setOnClickListener(this);
		et_pwd_new = (PasswordWithIconView) this.findViewById(R.id.et_pwd_new);// 新登陆密码
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新登陆密码");
		et_pwd_confirm = (PasswordWithIconView) this.findViewById(R.id.et_pwd_confirm);// 确认登陆密码
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认登陆密码");
		et_sms = (EditText) this.findViewById(R.id.et_sms);// 短信校验码
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
		case R.id.btn_sms:
			this.showToast("短信已发送，请注意查收!");
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("new_password", et_pwd_new.getEncryptPWD());
				map.put("smscode", et_sms.getText().toString());
				map.put("type", "1");
				map.put("tel", ApplicationEnvironment.getInstance()
						.getPreferences().getString(Constant.PHONENUM, ""));
			}
			break;
		default:
			break;
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