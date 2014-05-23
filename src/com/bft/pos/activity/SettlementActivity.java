package com.bft.pos.activity;

/**
 * 我的管理/结算
 * */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.agent.client.TransferLogic;
import com.bft.slidingmenu.MenuBaseActivity;

public class SettlementActivity extends BaseActivity implements OnClickListener{

	private Button btn_back;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_settlement);
		super.onCreate(savedInstanceState);

		this.initTitlebar("结	算");
		
		btn_back=(Button) findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);

	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
		
		}
		
	}
	

}