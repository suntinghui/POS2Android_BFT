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
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.receipt2);
	
		
		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_autograph = (Button) this.findViewById(R.id.btn_autograph);
		btn_autograph.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
		
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
