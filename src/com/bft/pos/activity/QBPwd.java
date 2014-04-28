package com.bft.pos.activity;
/**
 * 同样是输入密码
 * */
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bft.pos.R;
import com.bft.pos.activity.view.PasswordWithLabelView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.StringUtil;
import com.bft.slidingmenu.MenuBaseActivity;

public class QBPwd extends MenuBaseActivity implements OnClickListener {
	private PasswordWithLabelView et_pwd = null;
	private LinearLayout rootLayout		= null;
	private TextView textView			= null;
	private EditText editText			= null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
// 		依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_aishua_balance_pwd);
		super.onCreate( savedInstanceState);
		this.findViewById(R.id.topInfoView);
		this.initTitlebar("账户交易查询");

		editText = (EditText) this.findViewById(R.id.text);
		
		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm01);
		btn_confirm.setOnClickListener(this);

		et_pwd = (PasswordWithLabelView) this.findViewById(R.id.et_pwd);
		et_pwd.setHintWithLabel("密码", "请输入支付密码");

	}

	private boolean checkValue() {
		if (et_pwd.getText().length() != 6) {
			Toast.makeText(this, this.getResources().getString(R.string.pInputNewPwd), Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backButton:
			Intent intent0 = new Intent(QBPwd.this,CatalogActivity.class);
			startActivity(intent0);
			break;
		case R.id.btn_confirm01:
			if (checkValue()) {
				gettranferdetail();
				Intent intent1 = new Intent(QBPwd.this,QBTransferHistory.class);
				startActivity(intent1);
			}
			break;
		default:
			break;
		}
	}
//	这里是获取交易信息的接口
	public void gettranferdetail(){
		try {
			Event event = new Event(null, "querybal", null);
			event.setTransfer("089028");

			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			String pwd01 = et_pwd.getEncryptPWD();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login",ApplicationEnvironment.getInstance().getPreferences()
					.getString(Constant.PHONENUM, ""));
			map.put("payPass", pwd01);
			map.put("currPage", "1");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}