package com.bft.pos.activity;

/**
 * 登陆界面
 * 这个界面也是不需要侧滑的
 * */
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.bft.pos.util.APKUtil;
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.LocationUtil;
import com.bft.pos.util.PatternUtil;
import com.bft.pos.util.PopupMessageUtil;
import com.bft.pos.util.RSAUtil;
import com.bft.pos.util.SecurityCodeUtil;
import com.bft.pos.util.StringUtil;

public class LoginActivity extends BaseActivity {
	private boolean hasInit = false;
	// 获取组件
	private EditText userNameET;
	// 新添加的两个验证码的组件
	private EditText inputverifyCode;
	private ImageView verifyCode;
	// 设置验证码内容的字符串
	private String code = "";

	private PasswordWithIconView et_pwd;
	private ImageView rememberIV;
	private Button getPwdButton;
	private Button registerButton;
	private Button loginButton;
	// 设定是否记住账号
	private Boolean isRemember;
	private String url = null;

	private SharedPreferences sp = ApplicationEnvironment.getInstance().getPreferences();

	/**
	 * 下载公钥
	 */
	private void getPublicKey() {
		try {
			Event event = new Event(null, "getPublicKey", null);
			event.setTransfer("089034");
			HashMap<String, String> map = new HashMap<String, String>();

			map.put("version", "1");// 软件版本号
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mDraggingEnabled = true;
		this.mSlideTitleBar = true;
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_login);
		super.onCreate(savedInstanceState);

		/** ==下载公钥== */
		// getPublicKey();
		/** ==获取图片验证码== */
		getverifycode();
		// 设置标题
		initTitleBar("登 录", false);
		this.getIntent().setAction("com.bft.login");
		// 获取有关验证码的组件
		inputverifyCode = (EditText) findViewById(R.id.verifycode01);
		verifyCode = (ImageView) findViewById(R.id.verifycode02);
		verifyCode.setImageBitmap(SecurityCodeUtil.getInstance().createCodeBitmap(code));
		verifyCode.setOnClickListener(listener);
		// 账号和密码的输入框
		userNameET = (EditText) this.findViewById(R.id.usernameET);
		userNameET.setText(ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
		et_pwd = (PasswordWithIconView) this.findViewById(R.id.et_pwd);
		et_pwd.setIconAndHint(R.drawable.icon_pwd, "密码");
		// 这里是是否记住密码那儿的那个小勾勾
		rememberIV = (ImageView) this.findViewById(R.id.rememberIV);
		rememberIV.setOnClickListener(listener);
		isRemember = ApplicationEnvironment.getInstance().getPreferences().getBoolean(Constant.kISREMEBER, false);
		setRemeberImageView(isRemember);
		if (isRemember) {
			et_pwd.setText(ApplicationEnvironment.getInstance().getPreferences().getString(Constant.LOGINPWD, ""));
		}
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
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "verifyCodes", null);
			event.setTransfer("089021");
			String fsk = "Get_PsamNo|null";
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
		verifyCode.setImageBitmap(SecurityCodeUtil.getInstance().createCodeBitmap(code01));
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
			dialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					Update(LoginActivity.this.url);
				}
			});
			dialog.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
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
		DownloadFileRequest.sharedInstance().downloadAndOpen(this, url, "bft.apk");
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

				inputverifyCode.setText("");
				getverifycode();
				break;
			}
			case R.id.registerButton: {// 注册
				registerAction();

				break;
			}
			case R.id.loginButton: {// 登陆
				if (checkValue()) {
					SharedPreferences.Editor ed = sp.edit();
					ed.putBoolean(Constant.kISREMEBER, isRemember);
					if (isRemember) {
						ed.putString(Constant.LOGINPWD, et_pwd.getText().toString());
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
					// 处理登陆时干涉的任务
					// new LoginTask().execute();
				}
				break;
			}
			}
		}
	};

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

			PopupMessageUtil.showMSG_middle2("用户名不能为空！");
			return false;
		} else if (et_pwd.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("密码不能为空！");
			return false;
		} else if (inputverifyCode.getText().length() == 0) {
			PopupMessageUtil.showMSG_middle2("验证码不能为空！");
			return false;
		} else if (inputverifyCode.getText().length() < 4) {
			PopupMessageUtil.showMSG_middle2("验证码小于4位！");
			return false;
		} else if (inputverifyCode.getText().length() > 4) {
			PopupMessageUtil.showMSG_middle2("验证码不可大于4位！");
			return false;
		}

		// 校验登录密码
		if (!PatternUtil.checkLoginPWD(this, et_pwd.getText())) {
			return false;
		}

		return true;
	}

	// 跳转，这里直接跳转到目录页
	private void loginAction() {
		/**
		 * 直接跳转到主菜单
		 **/
		// Intent intent = new Intent(BaseActivity.getTopActivity(),
		// CatalogActivity.class);
		// startActivity(intent);

		Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
		editor.putBoolean(Constant.kISREMEBER, isRemember);
		editor.putString(Constant.PHONENUM, userNameET.getText().toString());// userNameET.getText().toString()
		editor.commit();
		try {
			Event event = new Event(null, "login", null);
			event.setTransfer("089016");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login", userNameET.getText().toString());

			// String pwd = StringUtil.MD5Crypto(StringUtil.MD5Crypto(et_pwd
			// .getText().toString().toUpperCase()
			// + et_pwd.getText())
			// + "www.payfortune.com");
			String pwd = null;
			String pk = FileUtil.convertStreamToString(FileUtil.readerFile("publicKey.xml"));
			if (pk != null) {
//				pwd = RSAUtil.encryptToHexStr(pk, (et_pwd.getText().toString() + "FF").getBytes(), 1);
				pwd = RSAUtil.encryptToHexStr(pk, (StringUtil.cover(et_pwd.getText().toString(), 8)).getBytes(), 1);
			}
			map.put("lgnPass", pwd);
			map.put("verifyCode", inputverifyCode.getText().toString());
			map.put("version", "1.0");// 软件版本号
			// 校验apk包的完整性（防止被篡改）
			map.put("APKInfo", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.APKMD5VALUE, ""));
			event.setStaticActivityDataMap(map);
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 注册
	 */
	private void registerAction() {
		Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(register_intent);
		
//		Intent intent = new Intent(LoginActivity.this,
//				CatalogActivity.class);
//		BaseActivity.getTopActivity().startActivity(intent);
	}

	/*
	 * 找回密码
	 */
	private void getPwdAction() {
		Intent getpwd_intent = new Intent(LoginActivity.this, AuthenticationActivity.class);
		startActivity(getpwd_intent);
	}

	/*
	 * private void exit(){ AlertDialog.Builder builder = new Builder(this); builder.setMessage("\n您确定要退出佰付通吗？"); builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); ApplicationEnvironment.getInstance().ForceLogout(); finish(); } });
	 * 
	 * builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); } });
	 * 
	 * builder.show(); }
	 */

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if(keyCode == KeyEvent.KEYCODE_BACK){
	//
	// this.exit();
	// return false;
	// } else{
	// return super.onKeyDown(keyCode, event);
	// }
	// }
	// 处理点击手机菜单键出现侧滑菜单的问题
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		case KeyEvent.KEYCODE_BACK:
			this.exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("\n您确定要退出佰付通吗？");
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				try {
					for (Activity activity : BaseActivity.getAllActiveActivity()) {
						activity.finish();
					}
				} catch (Exception e) {

				}

				// 必须关闭整个系统。缺点是也会关闭服务
				// android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	// 因为取得APK信息和读取位置信息的时间会占用很长时间，会卡死主界面，所以另起线程。
	class LoginTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// 保存手机号
			// AppDataCenter.setPhoneNum(usernameET.getText().toString());

			LoginActivity.this.showDialog(PROGRESS_DIALOG, LoginActivity.this.getResources().getString(R.string.initSystem));
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			if (!hasInit) {
				if (!Constant.isStatic) {
					LoginActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							LocationUtil.getInstance().initLocation(LoginActivity.this.getApplication());
						}

					});
				}
				// 取APK数据的md5值
				APKUtil.getApkSignatureMD5(LoginActivity.this.getApplicationInfo().publicSourceDir);

				hasInit = true;
			}

			return null;
		}
	}
}
