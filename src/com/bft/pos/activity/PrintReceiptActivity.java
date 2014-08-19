package com.bft.pos.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;

public class PrintReceiptActivity extends BaseActivity {
	TextView tv_content;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
// 		依旧添加侧滑界面
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_print_receipt);
			super.onCreate(savedInstanceState);
		this.findViewById(R.id.topInfoView);
		
		initControl();
	}
	@Override
	public void initControl() {
		HashMap<String, String> fieldMap = (HashMap<String, String>) this.getIntent().getSerializableExtra("content");
		
		tv_content = (TextView) findViewById(R.id.tv_content);
		
		if(fieldMap.get("fieldTrancode").equals("020022")){
			tv_content.setText("收款成功");
		}else if(fieldMap.get("fieldTrancode").equals("020023")){
			tv_content.setText("收款撤销成功");
		}
		
		Button btn_back2 = (Button) this.findViewById(R.id.btn_back2);
		btn_back2.setOnClickListener(this);
		
		Button btn_commit = (Button) this.findViewById(R.id.btn_commit);
		btn_commit.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back2:
		
			this.finish();
			break;
		case R.id.btn_commit:
		
			Intent intent1 = new Intent(PrintReceiptActivity.this,GatherActivity.class);
			startActivity(intent1);
			this.finish();
			
			break;

		default:
			break;
		}
	}
}
