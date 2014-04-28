package com.bft.pos.activity;

import java.io.IOException;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;

public class BankNumberActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_ok;
	private TextView titleBank;
	private EditText et_name, et_idcard, old_backcard, et_banknum, et_sms;
	private Spinner bankspinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bankcard);
		et_name = (EditText) findViewById(R.id.et_name);

		et_idcard = (EditText) findViewById(R.id.et_idcard);
		old_backcard = (EditText) findViewById(R.id.et_idcard);
		et_banknum = (EditText) findViewById(R.id.et_idcard);
		et_sms = (EditText) findViewById(R.id.btn_sms);

		btn_back = (Button) findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);

		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backButton:
			this.finish();
			break;
		case R.id.btn_ok:
			try {
				Event event = new Event(null, "modify-bk", null);
				event.setTransfer("089029");
				String fsk = "Get_ExtPsamNo|null";
				event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", "张三");// name
				map.put("pIdNo", "140106198806070614");// 身份证号
				map.put("oldBkCardNo", "001");// 原银行卡号
				map.put("bankNo", "003");// 银行卡开户
				map.put("bkCardNo", "002");// 银行卡号
				map.put("verifyCode", "123456");// 验证码
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (ViewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		default:
			break;
		}
	}
}
