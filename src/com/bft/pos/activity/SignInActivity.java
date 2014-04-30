package com.bft.pos.activity;

/**
 * 我的管理/签到/签到
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;

public class SignInActivity extends BaseActivity {
	private Button btn_sign;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_signin);
		super.onCreate(savedInstanceState);

		this.initTitlebar("签	到");

		btn_sign = (Button) this.findViewById(R.id.btn_confirm);
		btn_sign.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Intent intent0 = new Intent(SignInActivity.this,
					SignInFailActivity.class);
			startActivity(intent0);

		}
	};
}