package com.bft.pos.activity;
/**
 * 结算成功
 * */
import android.os.Bundle;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class SettlementSuccessActivity extends MenuBaseActivity {

		@Override
		public void onCreate( Bundle savedInstanceState){
			super.index = 0;
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_settlement_success);
			super.onCreate( savedInstanceState);
			
			this.initTitlebar("结算成功");
			
			
		}

	}