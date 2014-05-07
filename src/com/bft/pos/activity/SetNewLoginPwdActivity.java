package com.bft.pos.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.FileUtils;
import com.bft.pos.util.RSAUtil;

/**
 * 找回密码 验证身份后设置新的密码
 */
public class SetNewLoginPwdActivity extends BaseActivity implements
		OnClickListener {
	private PasswordWithIconView et_pwd_new;// 新的登陆密码
	private PasswordWithIconView et_pwd_confirm;// 确认密码
	private Button btn_back, btn_confirm;
	private TextWithIconView et_sms;// 验证码
	// private String smscode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_set_new_login_pwd);
		super.onCreate(savedInstanceState);
		init();
	}

	/*
	 * 初始化控件
	 */
	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);// 返回
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);// 确定
		btn_confirm.setOnClickListener(this);
		et_pwd_new = (PasswordWithIconView) this.findViewById(R.id.et_pwd_new);// 新登陆密码
		et_pwd_new.setIconAndHint(R.drawable.icon_pwd, "新登陆密码");
		et_pwd_confirm = (PasswordWithIconView) this
				.findViewById(R.id.et_pwd_confirm);// 确认登陆密码
		et_pwd_confirm.setIconAndHint(R.drawable.icon_pwd, "确认登陆密码");
		et_sms = (TextWithIconView) this.findViewById(R.id.et_sms);// 短信校验码
		et_sms.setHintString("短信校验码");
		et_sms.setIcon(R.drawable.icon_mail);
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
		case R.id.btn_confirm:
			if (checkValue()) {
				setNewPwd();
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 设置新的登陆密码
	 */
	private void setNewPwd() {
		try {
			Event event = new Event(null, "getPassword", null);
			event.setTransfer("089032");
			// 获取PSAM卡号
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("verifyCode", et_sms.getText().toString());
//			String pwd = StringUtil.MD5Crypto(StringUtil
//					.MD5Crypto(et_pwd_confirm.getText().toString()
//							.toUpperCase()
//							+ et_pwd_confirm.getText())
//					+ "www.payfortune.com");
			String pwd = null;
			String pk = FileUtil.convertStreamToString(FileUtil.readerFile("publicKey.xml"));
			if(pk!=null){
				pwd = RSAUtil.encryptToHexStr(pk, (et_pwd_confirm.getText().toString() + "FF").getBytes(), 1);
			}
			map.put("lgnPass", pwd);
			map.put("version", "1.0");
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
		if (et_pwd_new.getText().length() == 0) {
			this.showToast("密码不能为空！");
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
	
	//要泽宇：处理点击手机菜单键出现侧滑菜单的问题
	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        //Toggle the menu on menu key press.
	        switch (keyCode) {
	            case KeyEvent.KEYCODE_MENU:
	                return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
}
