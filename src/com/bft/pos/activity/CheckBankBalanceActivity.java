package com.bft.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;

/**
 * 银行卡余额查询
 */
public class CheckBankBalanceActivity extends BaseActivity implements
		OnClickListener {
	private Button btn_back;
	private Button btn_swip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_check_bank_balance);
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_swip = (Button) this.findViewById(R.id.btn_swip);
		btn_swip.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_swip:
			swipAction();
			break;
		default:
			break;
		}
	}

	public void swipAction() {

	}
}