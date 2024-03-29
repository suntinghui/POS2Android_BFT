package com.bft.pos.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.PatternUtil;
import com.bft.pos.util.PopupMessageUtil;
import com.bft.pos.util.RSAUtil;
import com.bft.pos.util.StringUtil;

/**
 * 注册
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_sms, btn_ok;
	private TextWithIconView et_name, et_id_card, et_phone_num;
	private PasswordWithIconView et_login_pwd, et_login_pwd_again;
	private TextWithIconView et_login_name;
	private EditText et_sms;
	private ImageButton ib_agree;
	private Button btn_protocol;

	private boolean isAgree = false;// 是否同意服务协议
	private int recLen = 10;
	Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mDraggingEnabled = true;
		this.mSlideTitleBar = true;
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_register);
		super.onCreate(savedInstanceState);
		initControl();
	}

	@Override
	public void initControl() {
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_sms = (Button) this.findViewById(R.id.btn_sms);// 获取短信验证码
		btn_sms.setOnClickListener(this);
		btn_ok = (Button) this.findViewById(R.id.btn_ok);// 下一步
		btn_ok.setOnClickListener(this);
		btn_protocol = (Button) this.findViewById(R.id.btn_protocol);// 用户协议
		btn_protocol.setOnClickListener(this);
		et_name = (TextWithIconView) this.findViewById(R.id.et_name);// 真实姓名
		et_name.setHintString("真实姓名");
		et_id_card = (TextWithIconView) this.findViewById(R.id.et_id_card);// 身份证号
		et_id_card.setIcon(R.drawable.icon_idcard);
		et_id_card.setHintString("身份证");
		et_id_card.getEditText().setFilters(new InputFilter[] { new InputFilter.LengthFilter(18) });
		et_phone_num = (TextWithIconView) this.findViewById(R.id.et_phone_num);// 手机号
		et_phone_num.setInputType(InputType.TYPE_CLASS_NUMBER);
		et_phone_num.setHintString("手机号码");
		et_phone_num.setIcon(R.drawable.icon_phone);
		et_login_name = (TextWithIconView) this.findViewById(R.id.et_login_name);
		et_login_name.setIcon(R.drawable.icon_login_1);
		et_login_name.setHintString("登录名");
		et_login_pwd = (PasswordWithIconView) this.findViewById(R.id.et_login_pwd);
		et_login_pwd.setIconAndHint(R.drawable.icon_pwd, "登陆密码");
		et_login_pwd_again = (PasswordWithIconView) this.findViewById(R.id.et_login_pwd_again);
		et_login_pwd_again.setIconAndHint(R.drawable.icon_pwd, "再次输入登陆密码");
		et_sms = (EditText) this.findViewById(R.id.et_sms);// 验证码
		et_sms.setInputType(InputType.TYPE_CLASS_NUMBER);
		ib_agree = (ImageButton) this.findViewById(R.id.ib_agree);// 同意服务协议
		ib_agree.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_sms:
			et_sms.setText("");
			if(!PatternUtil.isMobileNO(et_phone_num.getText().toString())){
				PopupMessageUtil.showMSG_middle2("手机号不合法，请重新输入!");
			} else {
				PopupMessageUtil.showMSG_middle2("短信已发送，请注意查收!");
				actionGetSms();
			}
			break;
		case R.id.ib_agree:
			if (isAgree) {
				ib_agree.setBackgroundResource(R.drawable.remeberpwd_n);
			} else {
				ib_agree.setBackgroundResource(R.drawable.remeberpwd_s);
			}
			isAgree = !isAgree;
			break;
		case R.id.btn_protocol:
			actionShowProtocol();
			break;
		case R.id.btn_ok:
			if (checkValue()) {
				actionRegister();
			}
			break;
		}
	}

	/*
	 * 立即注册
	 */
	@SuppressLint("DefaultLocale")
	private void actionRegister() {
		try {
			Event event = new Event(null, "register", null);
			event.setTransfer("089001");
			// 获取PSAM卡号
			String fsk = "Get_PsamNo|null";
			if (Constant.isAISHUA) {
				fsk = "getKsn|null";
			}
			event.setFsk(fsk);
			Constant.MOBILENO = et_phone_num.getText().toString();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("sctMobNo", et_phone_num.getText().toString());// 安全手机号
			map.put("name", et_name.getText().toString());// 姓名
			map.put("pIdNo", et_id_card.getText().toString());// 身份证
			map.put("login", et_login_name.getText().toString());// 登录名
			// String pwd = StringUtil.MD5Crypto(StringUtil
			// .MD5Crypto(et_login_pwd_again.getText().toString()
			// .toUpperCase()
			// + et_login_pwd_again.getText())
			// + "www.payfortune.com");
		
			String pwd = null;
			String pk = FileUtil.convertStreamToString(FileUtil.readerFile("publicKey.xml"));
			if (pk != null) {
//				pwd = RSAUtil.encryptToHexStr(pk, (et_login_pwd_again.getText().toString() + "FF").getBytes(), 1);
				pwd = RSAUtil.encryptToHexStr(pk, (StringUtil.cover(et_login_pwd_again.getText().toString(), 8)).getBytes(), 1);
			}
			map.put("lgnPass", pwd);// 登陆密码
			map.put("verifyCode", et_sms.getText().toString());// 验证码

			map.put("version", "1.0");// 软件版本号
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
		if (et_name.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("姓名不能为空！");
			return false;
		}
		if(!PatternUtil.isMobileNO(et_phone_num.getText().toString())){
			PopupMessageUtil.showMSG_middle2("手机号不合法，请重新输入!");
			return false;
		}
		if (et_login_name.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("姓名不能为空！");
			return false;
		}
		
		// 校验登录密码
		if (!PatternUtil.checkLoginPWD(this, et_login_pwd.getText())){
			return false; 
		}
		
		if (!PatternUtil.checkLoginPWD(this, et_login_pwd_again.getText())){
			return false;
		}
		
		if (!(et_login_pwd.getMd5PWD().equals(et_login_pwd_again.getMd5PWD()))) {
			PopupMessageUtil.showMSG_middle2("密码输入不一致，请重新输入！");
			et_login_pwd.setText("");
			et_login_pwd_again.setText("");
			return false;
		}
		
		if (!isAgree) {
			PopupMessageUtil.showMSG_middle2("请先阅读并同意服务协议！");
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

		}
	}

	/*
	 * 获取验证码
	 */
	@SuppressLint("SimpleDateFormat")
	private void actionGetSms() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "getSms", null);
			event.setTransfer("089006");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mobNo", et_phone_num.getText().toString());
			map.put("sendTime", date);
			map.put("type", "0");
			map.put("money", "");
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
	};

	// 处理点击手机菜单键出现侧滑菜单的问题
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Toggle the menu on menu key press.
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 显示服务协议
	 */
	private void actionShowProtocol() {
		Intent intent = new Intent(RegisterActivity.this, ShowProtocolActivity.class);
		RegisterActivity.this.startActivity(intent);
	}
}