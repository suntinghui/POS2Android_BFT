package com.bft.pos.activity;

import com.bft.pos.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 失败
 */
public class FailActivity extends BaseActivity implements OnClickListener{
	private Button btn_back,btn_confirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fail);
		initControl();
	}
	
	@Override
	public void initControl() {
		btn_back=(Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		btn_confirm=(Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backButton:
			finish();
			break;
		case R.id.btn_confirm:
			break;
		default:
			break;
		}
	}
}