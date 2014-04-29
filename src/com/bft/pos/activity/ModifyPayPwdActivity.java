package com.bft.pos.activity;

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
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;

/**
 * 修改支付密码
 */
public class ModifyPayPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_pwd_old;
	private PasswordWithIconView et_pwd_new;
	private PasswordWithIconView et_pwd_confirm;
	private Button btn_back, btn_sms, btn_confirm;
	private EditText et_sms;
	private int recLen = 10;
	Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_modify_pay_pwd);
		super.onCreate(savedInstanceState);
		initControl();
	}

	@Override
	public void initControl() {
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);// 获取验证码
		btn_sms.setOnClickListener(this);
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);// 确定
		btn_confirm.setOnClickListener(this);
		et_pwd_old = (PasswordWithIconView) this.findViewById(R.id.et_pwd_old);// 原支付密码
		et_pwd_old.setIconAndHint(R.drawable.icon_pwd, "原支付密码");
		et_pwd_new = (PasswordWithIconView) this.findViewById(R.id.et_pwd_new);// 新支付密码
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新支付密码");
		et_pwd_confirm = (PasswordWithIconView) this
				.findViewById(R.id.et_pwd_confirm);// 确认支付密码
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认支付密码");
		et_sms = (EditText) this.findViewById(R.id.et_sms);// 短信校验码
	}

	/*
	 * 按钮监听事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_sms:
			ModifyPayPwdActivity.this.showToast("短信已发送，请注意查收!");
			actionGetSms();
			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("oldPass", et_pwd_old.getEncryptPWD());
				map.put("newPass", et_pwd_new.getEncryptPWD());
				map.put("verifyCode", et_sms.getText().toString());
				map.put("type", "1");
//				map.put("tel", ApplicationEnvironment.getInstance()
//						.getPreferences().getString(Constant.PHONENUM, ""));
				try {
					Event event = new Event(null, "modifyPayPwd", null);
					event.setTransfer("089003");
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
	 * 判断输入框的输入内容
	 */
	private Boolean checkValue() {
		if (et_pwd_old.getText().length() == 0) {
			this.showToast("原密码不能为空！");
			return false;
		}
		if (et_pwd_new.getText().length() == 0) {
			this.showToast("新密码不能为空！");
			return false;
		}
		if (et_pwd_confirm.getText().length() == 0) {
			this.showToast("确认密码不能为空！");
			return false;
		}
		if (!et_pwd_new.getText().equals(et_pwd_confirm.getText())) {
			this.showToast("密码输入不一致，请重新输入！");
			et_pwd_new.setText("");
			et_pwd_confirm.setText("");
			return false;
		}
		return true;
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