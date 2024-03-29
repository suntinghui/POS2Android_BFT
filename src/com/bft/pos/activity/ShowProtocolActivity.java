package com.bft.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;

/**
 * 服务协议
 */
public class ShowProtocolActivity extends BaseActivity implements OnClickListener{
	
	private Button btn_back,btn_agree;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_protocol);
		super.onCreate(savedInstanceState);
		init();
	}
	private void init() {
		btn_back=(Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_agree=(Button) this.findViewById(R.id.btn_agree);
		btn_agree.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_agree:
			this.finish();
			break;
		case R.id.btn_back:
			ShowProtocolActivity.this.finish();
			break;
		default:
			break;
		}
	}
}