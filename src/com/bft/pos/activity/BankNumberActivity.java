package com.bft.pos.activity;

import java.io.IOException;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;

public class BankNumberActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_submit;
	private TextView titleBank;
	private EditText name_account, shenfen_num, oldbanknum, banknum, et_sms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bankcard);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_submit:
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
