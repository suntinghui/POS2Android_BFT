package com.bft.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;

/**
 * 失败
 */
public class FailActivity extends BaseActivity implements OnClickListener{
	private Button btn_confirm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mDraggingEnabled = true;
		this.mSlideTitleBar = true ;
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_fail);
		super.onCreate(savedInstanceState);
		initControl();
	}
	
	@Override
	public void initControl() {
		btn_confirm=(Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		
		Intent intent = this.getIntent();
		TextView tv_prompt = (TextView) findViewById(R.id.tv_prompt);
		tv_prompt.setText(intent.getStringExtra("prompt"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			finish();
			break;
		default:
			break;
		}
	}
}