package com.bft.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.PopupMessageUtil;
import com.bft.pos.util.RSAUtil;

/**
 * 重置支付密码
 */
public class ResetPayPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_new_pwd;// 重置的支付密码
	private TextWithIconView et_id_card;// 身份证号
	private TextWithIconView et_bank_card;// 银行卡号
	private EditText et_sms;// 短信验证码
	private Button btn_back, btn_sms, btn_ok;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_reset_pay_pwd);
		super.onCreate(savedInstanceState);
		initControl();
	}

	@Override
	public void initControl() {
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
		et_bank_card.setIcon(R.drawable.icon_bankcard);
		et_bank_card.setHintString("银行卡号");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_sms:
			if (ApplicationEnvironment.getInstance().getPreferences()
					.getString(Constant.PHONENUM, "").length() == 0) {
				PopupMessageUtil.showMSG_middle2("手机号不能为空!");
			} else {
				PopupMessageUtil.showMSG_middle2("短信已发送，请注意查收!");
				actionGetSms();
			}
			break;
		case R.id.btn_ok:
			if (checkValue()) {
				actionResertPwd();
			}
			break;
		default:
			break;
		}
	}

	private void actionResertPwd() {
		HashMap<String, String> map = new HashMap<String, String>();
		String pwd = null;
		String pk = FileUtil.convertStreamToString(FileUtil
				.readerFile("publicKey.xml"));
		if (pk != null) {
			pwd = RSAUtil.encryptToHexStr(pk,
					(et_new_pwd.getText().toString() + "FF").getBytes(), 1);
		}
		map.put("payPass", pwd);
		map.put("pIdNo", et_id_card.getText().toString());
		map.put("bkCardNo", et_bank_card.getText().toString());
		map.put("verifyCode", et_sms.getText().toString());
		try {
			Event event = new Event(null, "resertPayPwd", null);
			event.setTransfer("089023");
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
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
			// map.put("mobNo", Constant.MOBILENO);
			map.put("mobNo", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			map.put("sendTime", date);
			map.put("type", "4");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 输入框不能为空
	 */
	private Boolean checkValue() {
		if (et_id_card.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("身份证号不能为空!");
			return false;
		}
		if (et_new_pwd.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("密码不能为空！");
			return false;
		}
		// if (et_bank_card.getText().length() == 0) {
		// this.showToast("银行卡不能为空！");
		// return false;
		// }
		return true;
	}
}
