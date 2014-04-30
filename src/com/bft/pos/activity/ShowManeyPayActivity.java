package com.bft.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.RSAUtil;

public class ShowManeyPayActivity extends BaseActivity implements
		OnClickListener {
	private EditText et_sms;
	private Button btn_back, btn_confirm, btn_sms;
	private PasswordWithIconView et_pwd_pay;
	private TextView tv_money;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.show_manypay);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String money = bundle.getString("et_money");
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_money.setText("￥" + money);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		et_pwd_pay = (PasswordWithIconView) findViewById(R.id.et_pwd_pay);
		et_pwd_pay.setHint("请输入交易密码");
		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		et_sms = (EditText) findViewById(R.id.et_sms);
		btn_confirm = (Button) findViewById(R.id.btn_confirm01);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			Intent intent = new Intent(ShowManeyPayActivity.this,
					ShowMoneyActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_confirm01:
			try {
				Event event = new Event(null, "draw-cash", null);
				event.setTransfer("089025");
				// 获取PSAM卡号
				String fsk = "Get_ExtPsamNo|null";
				event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();
				String payPass = RSAUtil.encryptToHexStr(Constant.PUBLICKEY,
						(et_pwd_pay.getText().toString() + "FF").getBytes(), 1);
				map.put("payPass", payPass);
				map.put("money", tv_money.getText().toString());
				map.put("verifyCode", et_sms.getText().toString());
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_sms:
			actionGetSms();
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
			HashMap<String, String> map = new HashMap<String, String>();
			// map.put("mobNo", ApplicationEnvironment.getInstance()
			// .getPreferences().getString(Constant.PHONENUM, ""));
			map.put("mobNo", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			map.put("sendTime", date);
			map.put("type", "6");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void getnum(String testNum,String newNum) {
	// double testNum = 15.44;
	// int newNum = (int) (testNum * 100);
	// System.out.println(new MathTest().lpad(12, newNum));

	private static String lpad(int length, int number) {
		String f = "%0" + length + "d";
		return String.format(f, number * 100);

	}
}
