package com.bft.pos.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ShowMoneyActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_submit;
	private EditText et_account, et_paypassword, et_sms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_money);
		et_account = (EditText) findViewById(R.id.et_account);
		et_paypassword = (EditText) findViewById(R.id.et_paypassword);
		et_sms = (EditText) findViewById(R.id.et_sms);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
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
		case R.id.btn_sms:
			getCode();
			this.finish();
			break;
		default:
			break;
		}
	}

	public void getCode() {
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
}
