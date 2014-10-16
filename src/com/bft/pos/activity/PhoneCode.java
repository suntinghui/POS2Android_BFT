package com.bft.pos.activity;
/**
 * @author 
 * function: 进行验证码和短信验证码的调试用类。
 * 一般放在点击登录按钮之后。
 * 
 * */
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bft.pos.R;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.util.SecurityCodeUtil;

public class PhoneCode extends Activity{
	
	private Button getCode = null;
	private EditText getString = null;
	private ImageView show01 = null;
	private String code = "a2sd";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_code);
		
		getCode = (Button)findViewById(R.id.getcode);
//		getString = (EditText)findViewById(R.id.getString01);
		show01 = (ImageView)findViewById(R.id.show007);
//		code = getString.getText().toString();
		show01.setImageBitmap(SecurityCodeUtil.getInstance().createCodeBitmap(code));
		getCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				verifyCodes();
			}
		});
	}
	
	public void verifyCodes(){
//		图片验证码接口 目前已接通
//		SimpleDateFormat sDateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd hh:mm:ss");
//		String date = sDateFormat.format(new java.util.Date());
//		try {
//			Event event = new Event(null, "verifyCodes", null);
//			event.setTransfer("089021");
//			String fsk = "Get_ExtPsamNo|null";
//			event.setFsk(fsk);
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("sendTime", date);
//			event.setStaticActivityDataMap(map);
//			event.trigger();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		账户余额查询
		try {
			Event event = new Event(null, "querybal", null);
			event.setTransfer("089027");
//			String fsk = "Get_ExtPsamNo|null";
//			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login", "admin");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		SimpleDateFormat sDateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd hh:mm:ss");
//		String date = sDateFormat.format(new java.util.Date());
//		try {
//			Event event = new Event(null, "getSms", null);
//			event.setTransfer("089006");
//			String fsk = "Get_ExtPsamNo|null";
//			event.setFsk(fsk);
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("mobNo","13753102373");
//			map.put("sendTime", date);
//			map.put("type", "0");
//			event.setStaticActivityDataMap(map);
//			event.trigger();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
	
