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
 * 设置支付密码
 * 
 * @创建者 Fancong
 */
public class SetPayPwdActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_confirm;
	private PasswordWithIconView et_pay_pwd, et_pay_pwd_again;
	private TextWithIconView et_id_card;
	private EditText et_sms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_pay_pwd);
		init();
	}

	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		et_id_card = (TextWithIconView) this.findViewById(R.id.et_id_card);
		et_id_card.setIcon(R.drawable.icon_login_1);
		et_id_card.setHintString("身份证号码");
		et_pay_pwd = (PasswordWithIconView) this.findViewById(R.id.et_pay_pwd);
		et_pay_pwd.setIconAndHint(R.drawable.icon_pwd, "请输入支付密码");
		et_pay_pwd_again = (PasswordWithIconView) this
				.findViewById(R.id.et_pay_pwd_again);
		et_pay_pwd_again.setIconAndHint(R.drawable.icon_pwd, "请再次输入支付密码");
		et_sms = (EditText) this.findViewById(R.id.et_sms);
	}

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

			break;
		default:
			break;
		}
	}
}