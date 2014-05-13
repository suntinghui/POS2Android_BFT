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

public class ASBalanceSuccessActivity extends BaseActivity implements
		OnClickListener {
//定义所需要用到的组件
	private TextView tv_balance = null;
	private String accBlc = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_aishua_balance_success);
		super.onCreate(savedInstanceState);
		this.findViewById(R.id.topInfoView);
		this.initTitlebar("余额查询");
//获取后台返回并经过处理的账户余额
		 Intent intent = getIntent();
		 Bundle bundle = intent.getExtras();
		 accBlc = bundle.getString("accBlc");
//为按钮添加点击响应
		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm02);
		btn_confirm.setOnClickListener(this);
//显示余额的view
		tv_balance = (TextView) this.findViewById(R.id.tv_balance);
//显示方式
		int type = this.getIntent().getIntExtra("TYPE", 1);
//根据上面的显示方式确定显示内容
		if (type == 1) {
			tv_balance.setText("余额：" + accBlc);
		} else {
			tv_balance.setText("交易成功");
		}
	}
//添加点击相应
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
//		返回按钮回到我的查询页面
		case R.id.backButton:
			Intent intent = new Intent(ASBalanceSuccessActivity.this,
					QueryActivity.class);
			startActivity(intent);
			this.finish();
			break;
//			确认按钮回到查询页面
		case R.id.btn_confirm02:
			this.setResult(RESULT_OK);
			Intent intent1 = new Intent(ASBalanceSuccessActivity.this,
					QueryActivity.class);
			startActivity(intent1);
			this.finish();
			break;

		default:
			break;
		}

	}

}
