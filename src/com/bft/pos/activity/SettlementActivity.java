package com.bft.pos.activity;

/**
 * 我的管理/结算
 * */
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.dynamic.core.Event;

public class SettlementActivity extends BaseActivity implements OnClickListener{

	private Button btn_back;
	private Button btn_confirm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_settlement);
		super.onCreate(savedInstanceState);

		this.initTitlebar("结	算");
		initControl();
	}
	
	@Override
	public void initControl() {
		btn_back = (Button) findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			this.finish();
			break;
		case R.id.btn_confirm:
			this.settlementAction();
			break;	
		}
	}
	
	public void settlementAction() {
		try {
			Event event = new Event(null, "settlement", null);
			event.setTransfer("050000");
			String fsk = "Get_PsamNo|null#Get_VendorTerID|null";
			event.setFsk(fsk);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}