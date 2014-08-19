package com.bft.pos.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bft.pos.R;
import com.bft.pos.activity.view.LKAlertDialog;
import com.bft.pos.activity.view.TextWithLabelView;
import com.bft.pos.util.GMailSenderUtil;

public class FeedBackActivity extends BaseActivity implements OnClickListener, TextWatcher {

	private static final int MAXINPUTCOUNT = 150;

	private EditText contentText = null;
	private TextView countView = null;
	private TextWithLabelView mailText = null;
	private Button backButton = null;
	private Button okButton = null;

	public void onCreate(Bundle savedInstanceState) {
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.input_money);

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_feedback);

		this.initTitlebar("意见反馈");

		contentText = (EditText) this.findViewById(R.id.contentText);
		contentText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAXINPUTCOUNT) });
		contentText.addTextChangedListener(this);

		countView = (TextView) this.findViewById(R.id.counter);
		countView.setText("您还可输入" + MAXINPUTCOUNT + "个字");

		mailText = (TextWithLabelView) this.findViewById(R.id.mailText);
		mailText.setHintWithLabel("您的邮箱", "选填，便于我们给您答复");

		okButton = (Button) this.findViewById(R.id.btn_ok);
		okButton.setOnClickListener(this);

		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
	}

	@Override
	public void afterTextChanged(Editable editable) {
		countView.setText("您还可输入" + (MAXINPUTCOUNT - editable.toString().length()) + "个字");
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.backButton:
			this.finish();
			break;

		case R.id.btn_ok:
			if (checkInput()) {

				new SendEmailTask().execute();

				LKAlertDialog dialog = new LKAlertDialog(this);
				dialog.setTitle("提示");
				dialog.setMessage("信息发送成功，真诚感谢您的支持,谢谢!");
				dialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});

				dialog.create().show();
			}
			break;
		}
	}

	private boolean checkInput() {
		if ("".equals(contentText.getText().toString())) {
			this.showToast("请填写反馈信息，谢谢您的支持！");
			return false;
		}

		if (!"".equals(mailText.getText()) && !mailText.getText().matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
			this.showToast("邮箱格式不正确");
			return false;
		}

		return true;
	}

	public void showToast(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	class SendEmailTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String content = contentText.getText().toString();
			if (!"".equals(mailText.getText().trim())) {
				this.sendEmail(content + "  【反馈信息来自:" + mailText.getText() + "】\n\n【佰付通】");
			} else {
				this.sendEmail(content + "\n\n【佰付通】");
			}
			return null;
		}

		private void sendEmail(String content) {
			try {
				GMailSenderUtil sender = new GMailSenderUtil();
				sender.sendMail("意见反馈", content);
			} catch (Exception e) {
				Log.e("SendMail", e.getMessage(), e);

			}
		}

	}

}