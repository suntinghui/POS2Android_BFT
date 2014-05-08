package com.bft.pos.activity;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bft.pos.util.RSAUtil;

/**
 * 设置支付密码
 */
public class SetPayPwdActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_confirm;
	private PasswordWithIconView et_pay_pwd, et_pay_pwd_again;
	private TextWithIconView et_id_card;
	private EditText et_sms;
	private int recLen = 10;
	Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_set_pay_pwd);
		super.onCreate(savedInstanceState);
		initControl();
	}

	@Override
	public void initControl() {
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
			actionGetSms();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("pIdNo", et_id_card.getText().toString());
				String payPass = null;
				String pk = FileUtil.convertStreamToString(FileUtil.readerFile("publicKey.xml"));
					if(pk!=null){
						payPass = RSAUtil.encryptToHexStr(pk,
								(et_pay_pwd_again.getText().toString() + "FF").getBytes(), 1);
					}
				map.put("payPass", payPass);
				map.put("verifyCode", et_sms.getText().toString());
				try {
					Event event = new Event(null, "SetPayPwd", null);
					event.setTransfer("089022");
					String fsk = "Get_ExtPsamNo|null";
					event.setFsk(fsk);
					event.setStaticActivityDataMap(map);
					event.trigger();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
			map.put("mobNo", Constant.PHONENUM);
			map.put("sendTime", date);
			map.put("type", "8");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 判断输入框的输入内容
	 */
	private Boolean checkValue() {
		if (et_id_card.getText().length() == 0) {
			this.showToast("身份证不能为空！");
			return false;
		}
		if (et_pay_pwd.getText().length() == 0) {
			this.showToast("密码不能为空！");
			return false;
		}
		if (et_pay_pwd_again.getText().length() == 0) {
			this.showToast("确认密码不能为空！");
			return false;
		}
		if (!et_pay_pwd.getText().equals(et_pay_pwd_again.getText())) {
			this.showToast("密码输入不一致，请重新输入！");
			et_pay_pwd.setText("");
			et_pay_pwd_again.setText("");
			return false;
		}
		return true;
	}
	
	@SuppressLint("HandlerLeak")
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				btn_sms.setText(String.format("请等待短信，%d秒", recLen));
				if (recLen < 0) {
					btn_sms.setText("获取短信校验码");
					timer.cancel();
				}
			}
		}
	};

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			recLen--;
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	public void refreshSMSBtn() {
		timer.schedule(task, 1000, 1000); // timeTask
	}
}