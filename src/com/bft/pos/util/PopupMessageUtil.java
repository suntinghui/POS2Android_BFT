package com.bft.pos.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bft.pos.R;
import com.bft.pos.activity.BaseActivity;
import com.bft.pos.activity.SystemActivity;
import com.bft.pos.activity.view.LKAlertDialog;

/**
 * @功能函数 弹窗工具类
 * @author Maple Leaves
 *
 */
public class PopupMessageUtil{
	
	/**举例：message="您还没有签名，请先完成签名"；
	 * @param message
	 */
	public static void dialog(String message){
		AlertDialog.Builder builder = new Builder(null);
		builder.setMessage(message);
		builder.setPositiveButton("确定", null);
		builder.create().show();
		
	}
	

	/**
	 * @功能函数	屏幕下弹窗
	 * @param message	将被提醒的消息串
	 */
	public static void showMSG_below(String message){
		Toast.makeText(BaseActivity.getTopActivity(), message,Toast.LENGTH_SHORT).show();

	}

	/**
	 * @功能函数	屏幕中间弹窗
	 * @param message	将被提醒的消息串
	 */
	public static void showMSG_middle(String message){
		Toast toast = Toast.makeText(BaseActivity.getTopActivity(), message,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/**
	 * @功能函数	屏幕中间弹窗(带图片效果)
	 * @param message	将被提醒的消息串
	 */
	public static void showMSG_IMG_middle(String message){
		Toast toast = Toast.makeText(BaseActivity.getTopActivity(),
				"message", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(BaseActivity.getTopActivity());
		imageCodeProject.setImageResource(R.drawable.icon);
		toastView.addView(imageCodeProject, 0);
		toast.show();
	}

	/**
	 * @功能函数	屏幕中间弹窗（较美观 级别2）
	 * @param message	将被提醒的消息串
	 */
	public static void showMSG_middle2(String message) {
		View view = LayoutInflater.from(BaseActivity.getTopActivity()).inflate(
				R.layout.dialog, null);
		TextView tv_text = (TextView) view
				.findViewById(R.id.dialog_textview_text);
		tv_text.setText(message);
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
	
	/**
	 * @功能函数	屏幕中间弹窗（较美观 级别3）
	 * @param message	将被提醒的消息串
	 */
	public static void showMSG_middle3(Context context, String message){
		LKAlertDialog dialog = new LKAlertDialog(context);
		dialog.setTitle("提示");
		dialog.setMessage(message);
		dialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog.create().show();
	}
}