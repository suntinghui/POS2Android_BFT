package com.bft.pos.activity;
/**
 * 几个输入密码的界面都一样
 * */
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bft.pos.R;
import com.dhcc.pos.packets.util.ConvertUtil;
import com.itron.android.ftf.Util;
import com.bft.pos.activity.view.PasswordWithLabelView;
import com.bft.pos.agent.client.AppDataCenter;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.ByteUtil;
import com.bft.pos.util.StringUtil;
import com.bft.pos.util.UnionDes;
import com.bft.pos.util.XORUtil;

public class ASRevokePwd3Activity extends BaseActivity implements OnClickListener {
	private PasswordWithLabelView et_pwd = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_aishua_balance_pwd);
		super.onCreate(savedInstanceState);

		this.findViewById(R.id.topInfoView);
		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		et_pwd = (PasswordWithLabelView) this.findViewById(R.id.et_pwd);
		et_pwd.setHintWithLabel("密码", "请输入密码");

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
			this.finish();

			break;
		case R.id.btn_confirm:
			if (checkValue()) {
				// Intent intent = new Intent(ASBalancePwdActivity.this,
				// ASBalanceSuccessActivity.class);
				// intent.putExtra("TYPE", 1);
				// ASBalancePwdActivity.this.startActivityForResult(intent, 0);
				try {

					Event event = new Event(null, "getBalance", null);
					event.setTransfer("020001");
					// String fsk="Get_VendorTerID|null";
					// event.setFsk(fsk);
					HashMap<String, String> map = new HashMap<String, String>();

					// 磁道密文加pinkey
					String str0 = AppDataCenter.getENCTRACK() + Constant.pinkey;
					// 16位异或 值作des key
					byte[] tmpByte = XORUtil.xorDataFor16(ByteUtil.hexStringToBytes(str0));
					
					String tmpStr = et_pwd.getText() + "00";

					String pin52= ConvertUtil.bytesToHexString(tmpStr.getBytes());
					byte[] desByte = UnionDes.TripleDES(tmpByte, ByteUtil.hexStringToBytes(pin52));//3131313131313030
//					map.put("AISHUAPIN", "B2A85787299895F6");
					map.put("AISHUAPIN", ByteUtil.bytesToHexString(desByte));
					
					String flagStr = "01";
					String randomStr = AppDataCenter.getRANDOM();
					String enctrackStr = AppDataCenter.getENCTRACK();
					String enctracks = flagStr + randomStr + enctrackStr;
					Log.i("randomStr", randomStr);
					Log.i("enctrackStr", enctrackStr);
					Log.i("enctracks", enctracks);
					map.put("ENCTRACKS", enctracks);
					Log.i("pwd", et_pwd.getText());
					event.setStaticActivityDataMap(map);
					event.trigger();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			break;

		default:
			break;
		}

	}
}
