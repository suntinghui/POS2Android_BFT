package com.bft.pos.activity;

import java.io.IOException;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.bft.pos.R;
import com.bft.pos.activity.view.LKAlertDialog;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.RSAUtil;

public class SplashActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mDraggingEnabled = true;
		this.mSlideTitleBar = true;
		super.index = 0;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.splash_activity);
		super.onCreate(savedInstanceState);
		
		/**
		 * 直接跳转到主菜单
		 **/
		 Intent intent = new Intent(BaseActivity.getTopActivity(),
		 CatalogActivity.class);
		 startActivity(intent);
		 
		 
		//设置商户号，终端号
//		PSAMNo 000018
//		setVendorTerId("108360759990977", "18000211");
		
//		setVendorTerId("108360750650935", "18000104");
		
		new SplashTask().execute();
	}

	// 要泽宇：处理点击菜单键出现侧滑菜单的问题
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	class SplashTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				// 利用闪屏界面初始化系统.
				long startTime = System.currentTimeMillis();
				ApplicationEnvironment.getInstance().initialize(
						SplashActivity.this.getApplication());
				long endTime = System.currentTimeMillis();
				long cashTime = endTime - startTime;
				Log.e("Splash Time", String.valueOf(cashTime));

				// 从效果上保证最少要保持闪屏界面不少于1500ms
				// 因为现在有取地址会占用一些时间，所以就不加额外等待时间了。
				if (cashTime < 1500) {
					Thread.sleep(1500 - cashTime);
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (ApplicationEnvironment.getInstance().checkNetworkAvailable()) {

				Intent intent = new Intent(SplashActivity.this,
						LoginActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
			} else {// 检查网络

				LKAlertDialog dialog = new LKAlertDialog(SplashActivity.this);
				// dialog.setTitle("提示");
				dialog.setCancelable(false);

				dialog.setMessage(SplashActivity.this.getResources().getString(
						R.string.noNetTips));
				dialog.setPositiveButton("设置网络",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);
								finish();
							}
						});
				dialog.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});
				dialog.create().show();
			}
		}
	}
	public void setVendorTerId(String vendor, String terid) {
		Event event = new Event(null, null, null);
		String fsk = String.format("Get_RenewVendorTerID|string:%s,string:%s",
				vendor, terid);
		event.setFsk(fsk);
		try {
			event.trigger();
		} catch (ViewException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
