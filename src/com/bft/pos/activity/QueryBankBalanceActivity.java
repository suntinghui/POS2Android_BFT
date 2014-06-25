package com.bft.pos.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.dynamic.core.Event;

/**
 * 银行卡余额查询
 */
public class QueryBankBalanceActivity extends BaseActivity implements
		OnClickListener {
	private Button btn_back;
	private Button btn_swip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_query_bank_balance);
		super.onCreate(savedInstanceState);
		initControl();
	}
	@Override
	public void initControl() {
		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_swip = (Button) this.findViewById(R.id.btn_swip);
		btn_swip.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_swip:
			swipAction();
			break;
		default:
			break;
		}
	}

	public void swipAction() {
		try {
			Event event = new Event(null, "swip", null);
			event.setTransfer("020001");
//			String fsk = "Get_PsamNo|null#Get_VendorTerID|null#Get_EncTrack|int:0,int:0,string:null,int:60#Get_PIN|int:0,int:0,string:0,string:null,string:__PAN,int:60";
			String fsk = "Get_PsamNo|null#Get_VendorTerID|null#Get_CardTrack|int:60#Get_PIN|int:0,int:0,string:0,string:null,string:__PAN,int:60";
			event.setFsk(fsk);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}