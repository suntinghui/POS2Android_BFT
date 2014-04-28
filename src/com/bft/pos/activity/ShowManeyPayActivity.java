package com.bft.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;

public class ShowManeyPayActivity extends BaseActivity implements
		OnClickListener {

	private Button btn_back, btn_confirm;
	private PasswordWithIconView et_pwd_pay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_manypay);
		btn_back = (Button) findViewById(R.id.btn_back);
		et_pwd_pay = (PasswordWithIconView) findViewById(R.id.et_pwd_pay);
		et_pwd_pay.setHint("请输入交易密码");
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			Intent intent = new Intent(ShowManeyPayActivity.this,
					ShowMoneyActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_confirm:

		default:
			break;
		}
	}

}
