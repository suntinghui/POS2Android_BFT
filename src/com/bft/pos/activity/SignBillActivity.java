package com.bft.pos.activity;

/**
 * 我的查询/签购单查询/签购单
 * */
import android.os.Bundle;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class SignBillActivity extends MenuBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_sign_bill);
		super.onCreate(savedInstanceState);
		this.initTitlebar("签购单");
	}
}