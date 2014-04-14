package com.bft.pos.activity;
/**
 * 这里貌似是一些侧面的相关，很笼统不知道在说什么4.4
 * */
import android.os.Bundle;

import com.bft.pos.R;
import com.bft.slidingmenu.SlidingMenuActivity;

public class DepositActivity extends SlidingMenuActivity {

	
	@Override
	public void onCreate( Bundle savedInstanceState){
		setLayoutIds(R.layout.ws_munday_slidingmenu_test_menu, R.layout.ws_munday_slidingmenu_test_content);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate( savedInstanceState);
		
	}

		
}