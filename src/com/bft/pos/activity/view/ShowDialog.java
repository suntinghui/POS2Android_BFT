package com.bft.pos.activity.view;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.activity.BaseActivity;

public class ShowDialog {
	public static void setAlertDialog(String text) {
		View view = LayoutInflater.from(BaseActivity.getTopActivity()).inflate(
				R.layout.dialog, null);
		TextView tv_text = (TextView) view
				.findViewById(R.id.dialog_textview_text);
		tv_text.setText(text);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				BaseActivity.getTopActivity());
		final AlertDialog dialog = builder.create();
		dialog.show();
		// dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setContentView(view);
		view.findViewById(R.id.dialog_textview_ok).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
	}
}
