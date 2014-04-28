package com.bft.pos.activity;

import java.io.IOException;
import java.util.HashMap;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.PatternUtil;

/**
 * 找回密码
 */
public class FindLoginPwdActivity extends BaseActivity implements
		OnClickListener {
	private Button btn_back;// 返回
	private Button btn_confirm;// 身份验证
	private TextWithIconView et_phone_num;// 真实姓名
	private TextWithIconView et_identy_card;// 身份证号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		initControl();
	}

	@Override
	public void initControl() {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		et_phone_num = (TextWithIconView) this.findViewById(R.id.et_phone_num);
		et_identy_card = (TextWithIconView) this
				.findViewById(R.id.et_identy_card);
		et_identy_card.setIcon(R.drawable.icon_idcard);
		et_phone_num.setHintString("安全手机号");
		et_phone_num.setInputType(InputType.TYPE_CLASS_NUMBER);
		et_identy_card.setHintString("身份证");
		et_identy_card.getEditText().setFilters(
				new InputFilter[] { new InputFilter.LengthFilter(18) });
	}

	/*
	 * 按钮的点击监听事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:// 返回
			finish();
			break;
		case R.id.btn_confirm:// 身份验证
			if (checkValue()) {
				actionNext();
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 身份验证
	 */
	private void actionNext() {
		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
				.edit();
		editor.commit();
		try {
			Event event = new Event(null, "checkInfo", null);
			event.setTransfer("089002");
			//获取PSAM卡号
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("sctMobNo", et_phone_num.getText().toString());
			map.put("pIdNo", et_identy_card.getText().toString());
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (ViewException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 判断输入框的输入内容
	 */
	private Boolean checkValue() {
		if (et_phone_num.getText().length() == 0) {
			this.showToast("真实姓名不能为空！");
			return false;
		}
		if (et_identy_card.getText().length() == 0) {
			this.showToast("身份证号不能为空！");
			return false;
		}
		if (!PatternUtil.isValidIDNum(et_identy_card.getText())) {
			this.showToast("身份证号码不合法!");
			return false;
		}
		return true;
	}
}