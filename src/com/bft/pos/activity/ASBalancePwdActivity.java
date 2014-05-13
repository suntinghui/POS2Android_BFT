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
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.RSAUtil;

public class ASBalancePwdActivity extends BaseActivity implements OnClickListener {
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
//		获取组件
		this.findViewById(R.id.topInfoView);
		this.initTitlebar("账户余额查询");
		editText = (EditText) this.findViewById(R.id.text);
//		为按钮添加响应
		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm01);
		btn_confirm.setOnClickListener(this);
//设置输入框中的内容
		et_pwd = (PasswordWithLabelView) this.findViewById(R.id.et_pwd);
		et_pwd.setHintWithLabel("密码", "请输入支付密码");

	}
//确认输入密码是六位数字
	private boolean checkValue() {
		if (et_pwd.getText().length() != 6) {
			Toast.makeText(this, this.getResources().getString(R.string.pInputNewPwd), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
//按钮的点击相应
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		返回按钮要返回我要查询界面
		case R.id.backButton:
			System.out.println("返回按钮");
			Intent intent0 = new Intent(ASBalancePwdActivity.this,QueryActivity.class);
			startActivity(intent0);
			break;
//			确认按钮向客户端发送获余额请求
		case R.id.btn_confirm01:
			if (checkValue()) {
				getbalance();
			}
			break;
		default:
			break;
		}
	}
//	获取余额请求
	public void getbalance(){
		try {
			Event event = new Event(null, "querybal", null);
			event.setTransfer("089027");
//	加密输入密码
			String pwd = null;
			String pk = FileUtil.convertStreamToString(FileUtil
					.readerFile("publicKey.xml"));
			if (pk != null) {
				pwd = RSAUtil.encryptToHexStr(pk,
						(et_pwd.getText().toString() + "FF").getBytes(), 1);
			}
//			String pwd01 = et_pwd.getEncryptPWD();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login",ApplicationEnvironment.getInstance().getPreferences()
					.getString(Constant.PHONENUM, ""));
			map.put("payPass", pwd);
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
