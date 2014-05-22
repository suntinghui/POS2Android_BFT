package com.bft.pos.activity;

/**
 * 我的管理/结算
 * */
import android.os.Bundle;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class SettlementActivity extends BaseActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_settlement);
		super.onCreate(savedInstanceState);

		this.initTitlebar("结	算");

	}

}