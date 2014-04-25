package com.bft.pos.activity;
/**
 * 余额查询成功
 * */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class ASBalanceSuccessActivity extends MenuBaseActivity implements OnClickListener {
	
	private TextView tv_balance = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
// 		依旧添加侧滑界面
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_aishua_balance_success);
			super.onCreate( savedInstanceState);
		this.findViewById(R.id.topInfoView);
		this.initTitlebar("账户交易查询");

		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm02);
		btn_confirm.setOnClickListener(this);
		
		tv_balance = (TextView) this.findViewById(R.id.tv_balance);
		
		int type = this.getIntent().getIntExtra("TYPE", 1);
		
		if (type == 1) {
			tv_balance.setText("余额：100.00");
		} else {
			tv_balance.setText("交易成功");
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
			Intent intent = new Intent(ASBalanceSuccessActivity.this,ASBalancePwdActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_confirm02:
			this.setResult(RESULT_OK);
			Intent intent1 = new Intent(ASBalanceSuccessActivity.this,CatalogActivity.class);
			startActivity(intent1);
			this.finish();
			
			break;

		default:
			break;
		}

	}

}
