package com.bft.pos.activity;
/**
 * 结算成功
 * */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;

public class SettlementSuccessActivity extends BaseActivity {

	private Button btn_confirm;

	@Override
	public void onCreate( Bundle savedInstanceState){
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_settlement_success);
		//或者
		//this.setContentView(R.layout.activity_settlement_success);

		super.onCreate(savedInstanceState);

		this.initTitlebar("结算成功");
		
		initControl();
	}
	
	@Override
	public void initControl() {
		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(listener);
	}
	
	// 点击相应
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()){
			case R.id.btn_confirm:
				Intent intent1 = new Intent(BaseActivity.getTopActivity(),ManageActivity.class);
				startActivity(intent1);
				break;
			}
		}
	};
}