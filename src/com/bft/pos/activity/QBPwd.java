package com.bft.pos.activity;

/**
 * 同样是输入密码
 * */
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
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.RSAUtil;

public class QBPwd extends BaseActivity implements OnClickListener {
//	获取组件，定义所需的控件和字符串
	private PasswordWithLabelView et_pwd = null;
	private LinearLayout rootLayout = null;
	private TextView textView = null;
	private EditText editText = null;
	private String pwdcode = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_aishua_balance_pwd);
		super.onCreate(savedInstanceState);
		this.findViewById(R.id.topInfoView);
		this.initTitlebar("账户交易查询");
//获取组件
		editText = (EditText) this.findViewById(R.id.text);
//为按钮添加点击监听
		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm01);
		btn_confirm.setOnClickListener(this);
//设置密码输入框显示内容
		et_pwd = (PasswordWithLabelView) this.findViewById(R.id.et_pwd);
		et_pwd.setHintWithLabel("密码", "请输入支付密码");

	}
//	检测输入的内容是否是6位数字
	private boolean checkValue() {
		if (et_pwd.getText().length() != 6) {
			Toast.makeText(this,
					this.getResources().getString(R.string.pInputNewPwd),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
//点击响应
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		返回按钮点击返回主界面
		case R.id.backButton:
			Intent intent0 = new Intent(QBPwd.this, CatalogActivity.class);
			startActivity(intent0);
			break;
//			确认按钮，将加密之后的密码发送给历史界面
		case R.id.btn_confirm01:
			if (checkValue()) {

				String pwd = null;
				String pk;
				pk = FileUtil.convertStreamToString(FileUtil
						.readerFile("publicKey.xml"));
				if (pk != null) {
					pwd = RSAUtil.encryptToHexStr(pk, (et_pwd.getText()
							.toString() + "FF").getBytes(), 1);
					// pwdcode = et_pwd.getEncryptPWD();
					Intent intent1 = new Intent(QBPwd.this,
							QBTransferHistory.class);
					intent1.putExtra("pwdcode", pwd);
					startActivity(intent1);
					this.finish();
				}
			}
			break;
		default:
			break;
		}
	}
}
