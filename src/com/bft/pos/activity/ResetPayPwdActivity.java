package com.bft.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.activity.view.TextWithIconView;

/**
 * 重置支付密码
 * 
 * @创建者 Fancong
 */
public class ResetPayPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_new_pwd;// 重置的支付密码
	private TextWithIconView et_id_card;// 身份证号
	private TextWithIconView et_bank_card;// 银行卡号
	private EditText et_sms;// 短信验证码
	private Button btn_back, btn_sms, btn_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pay_pwd);
		init();
	}

	private void init() {
		// 返回
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		// 获取短信验证码
		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		// 确定重置
		btn_ok = (Button) this.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		// 短信验证码
		et_sms = (EditText) this.findViewById(R.id.et_sms);
		// 重置的支付密码
		et_new_pwd = (PasswordWithIconView) this.findViewById(R.id.et_new_pwd);
		et_new_pwd.setIconAndHint(R.drawable.icon_pwd, "新的支付密码");
		// 身份证号
		et_id_card = (TextWithIconView) this.findViewById(R.id.et_id_card);
		et_id_card.setIcon(R.drawable.icon_idcard);
		et_id_card.setHintString("身份证号");
		// 银行卡号
		et_bank_card = (TextWithIconView) this.findViewById(R.id.et_bank_card);
		et_bank_card.setIcon(R.drawable.icon_login_1);
		et_bank_card.setHintString("银行卡号");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_sms:
			break;
		case R.id.btn_ok:
			break;
		default:
			break;
		}
	}
}