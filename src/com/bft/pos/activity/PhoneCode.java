package com.bft.pos.activity;
/**
 * @author yaozeyu
 * function:本类是要泽宇（本人）进行验证码和短信验证码的调试用类。
 * 一般放在点击登录按钮之后。
 * 很简单的Intent，看到就应该能明白
 * 祝大家编程愉快（笑）
 * */
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.dynamic.core.Event;

public class PhoneCode extends Activity{
	
	private Button getCode = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_code);
		
		getCode = (Button)findViewById(R.id.getcode);
		getCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				verifyCodes();
			}
		});
	}
	
	public void verifyCodes(){
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
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "getSms", null);
			event.setTransfer("089006");
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mobNo","13753102373");
			map.put("sendTime", date);
			map.put("type", "0");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	
