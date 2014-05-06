package com.bft.pos.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.bft.pos.R;
import com.bft.pos.activity.view.LKAlertDialog;
import com.bft.pos.agent.client.ApplicationEnvironment;

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
		new SplashTask().execute();
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
				// TODO
				LKAlertDialog dialog = new LKAlertDialog(SplashActivity.this);
				dialog.setTitle("提示");
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

}
