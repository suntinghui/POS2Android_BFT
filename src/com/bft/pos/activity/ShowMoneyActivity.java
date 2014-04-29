package com.bft.pos.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ShowMoneyActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, bt_next;
	private EditText et_account, et_paypassword, et_money;
	Spinner spinner0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_show_money);
		super.onCreate(savedInstanceState);
		et_account = (EditText) findViewById(R.id.et_account);
		et_money = (EditText) findViewById(R.id.et_money);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		et_money = (EditText) findViewById(R.id.et_money);
		bt_next = (Button) findViewById(R.id.bt_next);
		bt_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.bt_next:
			Intent intent = new Intent(ShowMoneyActivity.this,
					ShowManeyPayActivity.class);
			startActivity(intent);
			break;
		// try {
		// Event event = new Event(null, "modify-bk", null);
		// event.setTransfer("089025");
		// String fsk = "Get_ExtPsamNo|null";
		// event.setFsk(fsk);
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("money", et_account.getText().toString());
		// map.put("payPass", et_paypassword.getText().toString());
		// map.put("verifyCode", "123456");// 验证码
		// event.setStaticActivityDataMap(map);
		// event.trigger();
		// } catch (ViewException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// case R.id.btn_sms:
		// getCode();
		// this.finish();
		// break;
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
