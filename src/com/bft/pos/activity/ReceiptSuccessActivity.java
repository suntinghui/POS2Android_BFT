package com.bft.pos.activity;


import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;

public class ReceiptSuccessActivity extends BaseActivity implements OnClickListener{
	HashMap<String,String> fieldMap = null;
	TextView tv_content;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
// 		依旧添加侧滑界面
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.receipt2);
			super.onCreate( savedInstanceState);
		this.findViewById(R.id.topInfoView);
	
		Intent intent = this.getIntent();
		
		tv_content = (TextView) findViewById(R.id.tv_content);
		
		fieldMap = (HashMap<String, String>) intent.getSerializableExtra("content");
		
		if(fieldMap.get("fieldTrancode").equals("020022")){
			tv_content.setText("收款成功");
		}else if(fieldMap.get("fieldTrancode").equals("020023")){
			tv_content.setText("收款撤销成功");
		}
		
		
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
		
			Intent intent = new Intent(ReceiptSuccessActivity.this,ReceiptActivity.class);
			intent.putExtra("content", fieldMap);
			
			startActivity(intent);
			this.finish();
			
			break;

		default:
			break;
		}
	}
}
