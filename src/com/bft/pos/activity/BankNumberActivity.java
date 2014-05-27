package com.bft.pos.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.activity.view.TextWithIconViewTwo;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.PopupMessageUtil;

/**
 * 修改银行卡
 */
public class BankNumberActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_ok;
	private TextView titleBank;
	private EditText et_idcard, et_sms;
	private Spinner spinner0;
	private TextWithIconViewTwo old_backcard, et_banknum;
	private TextWithIconView et_name, et_id_card;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.bankcard);
		super.onCreate(savedInstanceState);
		// spinner0 = (Spinner) findViewById(R.id.spinner0);
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);// 获取短信验证码
		btn_sms.setOnClickListener(this);
		btn_ok = (Button) this.findViewById(R.id.btn_ok);// 下一步
		btn_ok.setOnClickListener(this);
		et_name = (TextWithIconView) this.findViewById(R.id.et_name);// 真实姓名
		et_name.setHintString("真实姓名");
		et_id_card = (TextWithIconView) this.findViewById(R.id.et_id_card);// 身份证号
		et_id_card.setIcon(R.drawable.icon_idcard);
		et_id_card.setHintString("身份证");
		et_id_card.getEditText().setFilters(
				
				new InputFilter[] { new InputFilter.LengthFilter(18) });
		old_backcard = (TextWithIconViewTwo) this.findViewById(R.id.old_backcard);
		//old_backcard.setIcon(R.drawable.icon_bankcard);
		old_backcard.setHintString("原银行卡号");
		old_backcard.setIcon(R.drawable.icon_bankcard);
		et_banknum = (TextWithIconViewTwo) this.findViewById(R.id.et_banknum);
		et_banknum.setIcon(R.drawable.icon_bankcard);
		et_banknum.setHintString("新银行卡号");
		et_sms = (EditText) this.findViewById(R.id.et_sms);// 验证码
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_ok:
			if (checkValue()) {
				try {
					Event event = new Event(null, "modify-bk", null);
					event.setTransfer("089029");
					String fsk = "Get_ExtPsamNo|null";
					event.setFsk(fsk);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("name", et_name.getText().toString());// name
					map.put("pldNo", et_id_card.getText().toString());// 身份证号
					map.put("oldBkCardNo", old_backcard.getText().toString());// 原银行卡号
					map.put("bankNo", "111111111111");// 银行卡开户
					map.put("bkCardNo", et_banknum.getText().toString());// 银行卡号
					map.put("verifyCode", et_sms.getText().toString());// 验证码
					event.setStaticActivityDataMap(map);
					event.trigger();
				} catch (ViewException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.btn_sms:
			if (checkValue()) {
				PopupMessageUtil.showMSG_middle2("短信已发送，请注意查收!");
				actionGetSms();
			}
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
			map.put("mobNo", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			// map.put("mobNo", Constant.MOBILENO);
			map.put("sendTime", date);
			map.put("type", "7");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean checkValue() {
		if (et_id_card.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("身份证号不能为空!");
			return false;
		}
		if (et_name.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("姓名不能为空！");
			return false;
		}
		if (et_banknum.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("银行卡号不能为空！");
			return false;
		}
		if (old_backcard.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("原卡号不能为空！");
			return false;
		}
		return true;
	}
}
