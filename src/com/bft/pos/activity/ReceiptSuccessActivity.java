package com.bft.pos.activity;


import com.bft.pos.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ReceiptSuccessActivity extends BaseActivity implements OnClickListener{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.index = 0;
// 		依旧添加侧滑界面
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.receipt2);
			super.onCreate( savedInstanceState);
		this.findViewById(R.id.topInfoView);
	
	
		
		Button btn_back2 = (Button) this.findViewById(R.id.btn_back2);
		btn_back2.setOnClickListener(this);
		Button btn_autograph = (Button) this.findViewById(R.id.btn_autograph);
		btn_autograph.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back2:
		
			this.finish();
			break;
		case R.id.btn_autograph:
		
			Intent intent1 = new Intent(ReceiptSuccessActivity.this,ReceiptActivity.class);
			startActivity(intent1);
			this.finish();
			
			break;

		default:
			break;
		}

	}

	
}
