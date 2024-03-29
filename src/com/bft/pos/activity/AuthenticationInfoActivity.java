package com.bft.pos.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;

//实名认证 信息展示
public class AuthenticationInfoActivity extends BaseActivity implements
		OnClickListener {
	private TextView tv_merchant_name;
	private TextView tv_pid;
	private RelativeLayout relayout_confirm;
	private TextView tv_status;
	private String merchantId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_authentication_info);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_authentication_info);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		relayout_confirm = (RelativeLayout) this
				.findViewById(R.id.relayout_confirm);
		relayout_confirm.setOnClickListener(this);

		tv_merchant_name = (TextView) this.findViewById(R.id.tv_merchant_name);
		tv_pid = (TextView) this.findViewById(R.id.tv_pid);
		tv_status = (TextView) this.findViewById(R.id.tv_status);
		refresh();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.relayout_confirm:
			Intent intent_c = new Intent(AuthenticationInfoActivity.this,
					AuthenticationUpImageActivity.class);
			intent_c.putExtra("merchant_id", merchantId);
			AuthenticationInfoActivity.this.startActivityForResult(intent_c, 1);
			break;

		default:
			break;
		}

	}

	private void refresh() {
		try {
			// 获取商户注册信息
			Event event = new Event(null, "getMerchantInfo", null);
			event.setTransfer("089013");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			event.setStaticActivityDataMap(map);
			event.trigger();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fromLogic(UserModel model) {
		tv_merchant_name.setText(model.getMerchant_name() == null ? "无" : model
				.getMerchant_name());
		tv_pid.setText(model.getPid() == null ? "无" : model.getPid());
		merchantId = model.getMerchant_id();

		String tmp_status = model.getStatus();
		String status = null;

		relayout_confirm.setVisibility(View.VISIBLE);
		if (tmp_status.equals("0")) {
			status = "已注册未完善用户信息";
		} else if (tmp_status.equals("1")) {
			status = "已完善未审核";
		} else if (tmp_status.equals("2")) {
			status = "初审通过，等待复审";
		} else if (tmp_status.equals("3")) {
			status = "复审通过";
		} else if (tmp_status.equals("4")) {
			status = "审核不通过";
		} else if (tmp_status.equals("7")) {
			status = "已注销";
		} else if (tmp_status.equals("8")) {
			status = "已冻结";
		} else if (tmp_status.equals("9")) {
			status = "终审通过";
			relayout_confirm.setVisibility(View.GONE);
		}
		tv_status.setText(status);
	}
}
