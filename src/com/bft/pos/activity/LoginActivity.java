package com.bft.pos.activity;

/**
 * 登陆界面
 * 这个界面也是不需要侧滑的
 * */
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.StringUtil;

public class LoginActivity extends BaseActivity {
	// 获取组件
	private EditText userNameET;
	private PasswordWithIconView et_pwd;
	private ImageView rememberIV;
	private Button getPwdButton;
	private Button registerButton;
	private Button loginButton;
	// 设定是否记住账号
	private Boolean isRemember;

	private SharedPreferences sp = ApplicationEnvironment.getInstance()
			.getPreferences();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// 设置标题
		initTitleBar("登 录", false);
		// 账号和密码的输入框
		userNameET = (EditText) this.findViewById(R.id.usernameET);
		et_pwd = (PasswordWithIconView) this.findViewById(R.id.et_pwd);
		et_pwd.setIconAndHint(R.drawable.icon_pwd, "密码");
		// 这里是是否记住密码那儿的那个小勾勾
		rememberIV = (ImageView) this.findViewById(R.id.rememberIV);
		rememberIV.setOnClickListener(listener);
		isRemember = ApplicationEnvironment.getInstance().getPreferences()
				.getBoolean(Constant.kISREMEBER, false);
		setRemeberImageView(isRemember);
		// 取回密码
		getPwdButton = (Button) this.findViewById(R.id.getPwdButton);
		getPwdButton.setOnClickListener(listener);
		// 注册按钮
		registerButton = (Button) this.findViewById(R.id.registerButton);
		registerButton.setOnClickListener(listener);
		// 登陆按钮
		loginButton = (Button) this.findViewById(R.id.loginButton);
		loginButton.setOnClickListener(listener);
	}

	// 这就是具体的方法,在点击的情况下,钩钩
	private void setRemeberImageView(Boolean remember) {
		if (remember) {
			rememberIV.setBackgroundResource(R.drawable.remeberpwd_s);
		} else {
			rememberIV.setBackgroundResource(R.drawable.remeberpwd_n);
		}
	}

	// 点击相应
	private OnClickListener listener = new OnClickListener() {
		// 用这样的方法,
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rememberIV: {// 记住密码
				isRemember = !isRemember;
				setRemeberImageView(isRemember);
				break;
			}
			case R.id.getPwdButton: {// 取回密码
				getPwdAction();
				break;
			}
			case R.id.registerButton: {// 注册
				registerAction();
				break;
			}
			case R.id.loginButton: {// 登陆

				// if (checkValue()) {
				// SharedPreferences.Editor ed = sp.edit();
				// ed.putBoolean(Constant.kISREMEBER, isRemember);
				// if (isRemember) {
				// ed.putString(Constant.LOGINPWD, pwdET.getText()
				// .toString());
				// } else {
				// ed.putString(Constant.LOGINPWD, "");
				// }
				// Boolean firstLogin = sp.getBoolean("firstLogin", true);
				// if (firstLogin) {
				// ed.putBoolean("firstLogin", false);
				// } else {
				// loginAction();
				// }
				// ed.commit();
				// }

				if (checkValue()) {
					SharedPreferences.Editor ed = sp.edit();
					ed.putBoolean(Constant.kISREMEBER, isRemember);
					if (isRemember) {
						ed.putString(Constant.LOGINPWD, et_pwd.getText()
								.toString());
					} else {
						ed.putString(Constant.LOGINPWD, "");
					}
					Boolean firstLogin = sp.getBoolean("firstLogin", true);
					if (firstLogin) {
						ed.putBoolean("firstLogin", false);
					} else {
						loginAction();
					}
					ed.commit();
				}
				break;
			}
			}

		}

	};

	// // 不明觉厉
	// private void shangsong() {
	// try {
	// Event event = new Event();
	// event.setTransfer("032000");// 050000
	//
	// String fsk = "Get_VendorTerID|null";
	// event.setFsk(fsk);
	//
	// event.trigger();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// 这里是菜单键,按出来能得个Setting
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// 用来判断输入内容的
	private Boolean checkValue() {
		if (userNameET.length() == 0) {
			this.showToast("用户名不能为空！");
			return false;
		} else if (et_pwd.getText().length() == 0) {
			this.showToast("密码不能为空！");
			return false;
		}
		return true;
	}

	// 跳转，这里直接跳转到目录页
	@SuppressLint("DefaultLocale") private void loginAction() {
		/**
		 * 直接跳转到主菜单
		 * */
//		Intent intent = new Intent(LoginActivity.this, CatalogActivity.class);
//		startActivity(intent);
		Intent intent = new Intent(LoginActivity.this, PhoneCode.class);
		startActivity(intent);
		/**==============*/
		
		
		// if (checkValue()) {

		// Editor editor = ApplicationEnvironment.getInstance()
		// .getPreferences().edit();
		// editor.putBoolean(Constant.kISREMEBER, isRemember);
		// Log.i("phone:", userNameET.getText().toString());
		// Log.i("phone:", pwdET.getText().toString());
		// editor.putString(Constant.PHONENUM,
		// userNameET.getText().toString());
		// userNameET.getText().toString();
		// editor.commit();
		// try {
		// Event event = new Event(null, "login", null);
		// event.setTransfer("089016");
		// String fsk = "Get_ExtPsamNo|null";
		// event.setFsk(fsk);
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("login", userNameET.getText().toString());
		// map.put("lgnPass", pwdET.getText().toString());
		// map.put("verifyCode", "qwe123");
		// event.setStaticActivityDataMap(map);
		// event.trigger();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

//		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
//				.edit();
//		editor.putBoolean(Constant.kISREMEBER, isRemember);
//		editor.putString(Constant.PHONENUM, userNameET.getText().toString());// userNameET.getText().toString()
//		editor.commit();
//		try {
//			Event event = new Event(null, "login", null);
//			event.setTransfer("089016");
//			String fsk = "Get_ExtPsamNo|null";
//			event.setFsk(fsk);
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("login", userNameET.getText().toString());
//			String pwd = StringUtil.MD5Crypto(StringUtil.MD5Crypto(userNameET
//					.getText().toString().toUpperCase() + et_pwd.getText())
//					+ "www.payfortune.com");
//			map.put("lgnPass", pwd);
//			map.put("verifyCode", "qwe123");
//			event.setStaticActivityDataMap(map);
//			event.trigger();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	// if(checkValue()){
	//
	// Editor editor =
	// ApplicationEnvironment.getInstance().getPreferences().edit();
	// editor.putBoolean(Constant.kISREMEBER, isRemember);
	// editor.commit();
	// }

	/*
	 * 注册
	 * 
	 * @Fancong
	 */
	private void registerAction() {
		Intent register_intent = new Intent(LoginActivity.this,
				RegisterActivity.class);
		startActivity(register_intent);
	}

	/*
	 * 找回密码
	 * 
	 * @Fancong
	 */

	private void getPwdAction() {
		Intent getpwd_intent = new Intent(LoginActivity.this,
				FindPasswordActivity.class);
		startActivity(getpwd_intent);
	}
}
