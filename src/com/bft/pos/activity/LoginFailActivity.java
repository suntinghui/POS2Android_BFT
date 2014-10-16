package com.bft.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;

/**
 * 注册失败
 */
public class LoginFailActivity extends BaseActivity implements OnClickListener {
	private Button btn_confirm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mDraggingEnabled = true;
		this.mSlideTitleBar = true;
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_fail);
		super.onCreate(savedInstanceState);
		initControl();
	}

	@Override
	public void initControl() {
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		Intent intent = this.getIntent();
		TextView tv_prompt = (TextView) findViewById(R.id.tv_prompt);
		tv_prompt.setText(intent.getStringExtra("prompt"));
	}

	// ：处理点击手机菜单键出现侧滑菜单的问题
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Toggle the menu on menu key press.
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			Intent intent = new Intent(LoginFailActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
}