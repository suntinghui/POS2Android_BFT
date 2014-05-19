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

public class SignInFailActivity extends BaseActivity implements OnClickListener{

	private Button btn_confirm,btn_back;
	
		@Override
		public void onCreate( Bundle savedInstanceState){
			super.index = 0;
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_signin_fail);
			super.onCreate( savedInstanceState);
			
			this.initTitlebar("签到");
			
			btn_back=(Button) this.findViewById(R.id.backButton);
			btn_back.setOnClickListener(this);
			btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
			btn_confirm.setOnClickListener(this);
			
		}
		
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.backButton:
				this.finish();
				break;
			case R.id.btn_confirm:
				this.finish();
				break;

			default:
				break;
			}
		}

	}