package com.bft.pos.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bft.pos.R;
import com.bft.pos.activity.view.TextWithIconView;
import com.bft.pos.activity.view.TextWithIconViewThree;

public class PhonePayActivity extends BaseActivity implements OnClickListener {
	TextWithIconViewThree et_phone;
	TextWithIconView et_phone2;
	Button btn_ok, btn_back;
	ImageView iv;
	String username, num;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_phonepay);
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		iv = (ImageView) findViewById(R.id.iv_icon);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setData(ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, 1);
			}
		});
		et_phone = (TextWithIconViewThree) findViewById(R.id.et_phone);
		et_phone.setTextViewText("手机号码");
		et_phone2 = (TextWithIconView) findViewById(R.id.et_phone2);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_back = (Button) findViewById(R.id.btn_back);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:

			if (resultCode == 1) {
				ContentResolver reContentResolverol = getContentResolver();
				Uri contactData = data.getData();
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(contactData, null, null, null,
						null);
				cursor.moveToFirst();
				username = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor phone = reContentResolverol.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phone.moveToNext()) {
					num = phone
							.getString(phone
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					et_phone.setText(num);
					System.out.println(num + "~~~~~~~~~~~");

				}

			}

		}
	}
}
