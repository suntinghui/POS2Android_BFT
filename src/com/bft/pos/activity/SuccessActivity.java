package com.bft.pos.activity;

import com.bft.pos.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 成功
 */
public class SuccessActivity extends BaseActivity implements OnClickListener {
	private Button btn_confirm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mDraggingEnabled = true;
		this.mSlideTitleBar = true;
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_success);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			Intent intent = new Intent(SuccessActivity.this,
					CatalogActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}
}