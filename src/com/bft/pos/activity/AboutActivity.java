package com.bft.pos.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;

public class AboutActivity extends BaseActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 1;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_manage);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		this.initTitlebar("关于我们");
		
		Button btn_access = (Button) this.findViewById(R.id.btn_access);
		btn_access.setOnClickListener(this);

		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
			this.finish();
			break;
			
		case R.id.btn_access:
			Intent viewIntent = new 
		    Intent("android.intent.action.VIEW",Uri.parse("http://www.payfortune.com/assets/web/fr/index.html"));
			startActivity(viewIntent);
			break;

		}

	}

}