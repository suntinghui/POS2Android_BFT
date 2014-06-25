package com.bft.pos.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconViewThree;

public class PhonePayActivity extends BaseActivity implements OnClickListener {
	private TextWithIconViewThree et_phone, et_phone2;
	private Button btn_ok, btn_back, bt_ok;
	private ImageView iv;
	private String username, num;
	private TextView tv;
	private EditText etnum;
	private Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.index = 0;
//		 添加了侧滑内容
		 setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
		 R.layout.activity_phonepay);
		super.onCreate(savedInstanceState);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// tv = (TextView) findViewById(R.id.tv1);
		spinner = (Spinner) findViewById(R.id.spinner0);
		String[] items = getResources().getStringArray(R.array.phonepay);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, items);
		spinner.setAdapter(adapter);
		et_phone = (TextWithIconViewThree) findViewById(R.id.et_phone);
		et_phone.setTextViewText("手机号码");
		et_phone2 = (TextWithIconViewThree) findViewById(R.id.et_phone2);
		et_phone2.setTextViewText("确认手机");

		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_back = (Button) findViewById(R.id.btn_back);
		iv = (ImageView) findViewById(R.id.iv_icon);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI), 0);
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(PhonePayActivity.this,
						DrawingsActivity.class));
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// ContentProvider展示数据类似一个单个数据库表
			// ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
			ContentResolver reContentResolverol = getContentResolver();
			// URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
			Uri contactData = data.getData();
			// 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			// 获得DATA表中的名字
			username = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			// 条件为联系人ID
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
			Cursor phone = reContentResolverol.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phone.moveToNext()) {
				num = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				et_phone.setText(num);
			}
		}

	}
}
