package com.bft.pos.activity;
/**
 * 我的管理/签到/签到失败
 * */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class SignInFailActivity extends BaseActivity {

	private Button btn_confirm;
	
		@Override
		public void onCreate( Bundle savedInstanceState){
			super.index = 0;
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_signin_fail);
			super.onCreate( savedInstanceState);
			
			this.initTitlebar("签到");
			
			btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
			btn_confirm.setOnClickListener(listener);
			
		}
		private OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		};

	}