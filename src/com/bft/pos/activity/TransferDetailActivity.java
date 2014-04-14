package com.bft.pos.activity;

import android.os.Bundle;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class TransferDetailActivity extends MenuBaseActivity {

		@Override
		public void onCreate( Bundle savedInstanceState){
			super.index = 0;
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_transfer_detail);
			super.onCreate( savedInstanceState);
			
			this.initTitlebar("交易明细");
			
			
		}

	}