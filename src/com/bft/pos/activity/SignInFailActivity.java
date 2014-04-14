package com.bft.pos.activity;
/**
 * 我的管理/签到/签到失败
 * */
import android.os.Bundle;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class SignInFailActivity extends MenuBaseActivity {

		@Override
		public void onCreate( Bundle savedInstanceState){
			super.index = 0;
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_signin_fail);
			super.onCreate( savedInstanceState);
			
			this.initTitlebar("签到");
			
			
		}

	}