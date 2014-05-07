package com.bft.pos.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bft.pos.R;

public class DialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		View view = LayoutInflater.from(DialogActivity.this).inflate(
				R.layout.shap, null);
		TextView tv_text = (TextView) view
				.findViewById(R.id.dialog_textview_text);
		tv_text.setText("已经是最后一题了");
		AlertDialog.Builder builder = new AlertDialog.Builder(
				DialogActivity.this);
		final AlertDialog dialog = builder.setView(view).create();
		dialog.show();
		view.findViewById(R.id.dialog_textview_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
	}
}
