package com.bft.pos.activity;

/**
 * 登陆界面
 * 这个界面也是不需要侧滑的
 * */
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Instrumentation.ActivityResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import com.bft.pos.agent.client.DownloadFileRequest;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.SecurityCodeUtil;
import com.bft.pos.util.StringUtil;

public class LoginActivity extends BaseActivity {
	// 获取组件
	private EditText userNameET;
	// 新添加的两个验证码的组件
	private EditText inputverifyCode;
	private ImageView verifyCode;
	// 设置验证码内容的字符串
	private String code = null;

	private PasswordWithIconView et_pwd;
	private ImageView rememberIV;
	private Button getPwdButton;
	private Button registerButton;
	private Button loginButton;
	// 设定是否记住账号
	private Boolean isRemember;
	private String url = null;

	private SharedPreferences sp = ApplicationEnvironment.getInstance()
			.getPreferences();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// 设置标题
		initTitleBar("登 录", false);
		this.getIntent().setAction("com.bft.login");
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		code = bundle.getString("code");
		// 获取有关验证码的组件
		inputverifyCode = (EditText) findViewById(R.id.verifycode01);
		verifyCode = (ImageView) findViewById(R.id.verifycode02);
		verifyCode.setImageBitmap(SecurityCodeUtil.getInstance()
				.createCodeBitmap(code));
		verifyCode.setOnClickListener(listener);
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

		// getVersion();
	}

	private void getVersion() {
		try {

			Event event = new Event(null, "version", null);
			event.setTransfer("089018");
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取图片验证码的方法
	private void getverifycode() {
		// 获取即时时间
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "verifyCodes", null);
			event.setTransfer("089021");
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("sendTime", date);
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	// 在manifest里标注 android:launchMode="singleTop"之后，再有Intent传来就必须走这里。
	// 算是一个比较另类的异步刷新，更新的只是局部，用户输入的内容都在。
	@Override
	protected void onNewIntent(Intent intent) {
		String code01 = intent.getStringExtra("code");
		verifyCode.setImageBitmap(SecurityCodeUtil.getInstance()
				.createCodeBitmap(code01));
		System.out.println("new intent");
	}

	public void showAlertView(Integer version, String url) {
		this.url = url;
		String appVersion = "";
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			appVersion = info.versionName; // 版本名
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer tmp_ver = Integer.valueOf(appVersion.replace(".", ""));
		if (tmp_ver < version) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("提示");
			dialog.setMessage("有新版本，是否下载更新？");
			dialog.setCancelable(false);
			dialog.setPositiveButton("立即更新",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							Update(LoginActivity.this.url);
						}
					});
			dialog.setNegativeButton("暂不更新",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});

			dialog.create().show();
		} else {
		}

	}

	private void Update(String url) {

		DownloadFileRequest.sharedInstance().downloadAndOpen(this, url,
				"bft.apk");
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
			case R.id.verifycode02: {// 获取图片验证码
				getverifycode();
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
				// ed.putString(Constant.LOGINPWD, et_pwd.getText()
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
		} else if (inputverifyCode.getText().length() == 0) {
			this.showToast("验证码不能为空！");
			return false;
		}
		return true;
	}

	// 跳转，这里直接跳转到目录页
	private void loginAction() {
		/**
		 * 直接跳转到主菜单
		 **/
		// Intent intent = new Intent(LoginActivity.this,
		// CatalogActivity.class);
		// startActivity(intent);

		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
				.edit();
		editor.putBoolean(Constant.kISREMEBER, isRemember);
		editor.putString(Constant.PHONENUM, userNameET.getText().toString());// userNameET.getText().toString()
		editor.commit();
		try {
			Event event = new Event(null, "login", null);
			event.setTransfer("089016");
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login", userNameET.getText().toString());
			String pwd = StringUtil.MD5Crypto(StringUtil.MD5Crypto(userNameET
					.getText().toString().toUpperCase()
					+ et_pwd.getText())
					+ "www.payfortune.com");
			map.put("lgnPass", pwd);
			map.put("verifyCode", inputverifyCode.getText().toString());
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	 */
	private void registerAction() {
		Intent register_intent = new Intent(LoginActivity.this,
				RegisterActivity.class);
		startActivity(register_intent);
	}

	/*
	 * 找回密码
	 */
	private void getPwdAction() {
		Intent getpwd_intent = new Intent(LoginActivity.this,
				FindPasswordActivity.class);
		startActivity(getpwd_intent);
	}

}
