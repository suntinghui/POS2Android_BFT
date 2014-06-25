package com.bft.pos.activity;

/**
 * 我的管理/签到/签到
 */
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;

public class SignInActivity extends BaseActivity implements OnClickListener {
	private Button btn_sign,btn_back;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_signin);
		super.onCreate(savedInstanceState);

		this.initTitlebar("签	到");

		btn_back=(Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		
		btn_sign = (Button) this.findViewById(R.id.btn_confirm);
		btn_sign.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
			finish();
			break;
		case R.id.btn_confirm:
			 refresh();
			
//			Intent intent0 = new Intent(SignInActivity.this,SignInFailActivity.class);
//			startActivity(intent0);
			break;

		default:
			break;
		}
	}

	private void refresh() {
		try {

			Event event = new Event(null, "sign", null);
			event.setTransfer("080000");
			String fsk = "Get_PsamNo|null#Get_VendorTerID|null";
			if (Constant.isAISHUA) {
				fsk = "getKsn|null";
			}
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("username", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			event.setStaticActivityDataMap(map);
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fromLogic(UserModel model) {

	}
}