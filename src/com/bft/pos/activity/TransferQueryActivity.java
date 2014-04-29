package com.bft.pos.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bft.pos.R;
import com.bft.pos.activity.view.PickerDateView;

//選擇日历
public class TransferQueryActivity extends BaseActivity implements
		OnClickListener {

	private PickerDateView date_picker = null;
	private String interval = "7"; // 设置开始日期和结束日期之间相差的天数。默认为7天。

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_transfer_query);
		super.onCreate(savedInstanceState);
		this.findViewById(R.id.topInfoView);

		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);

		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		date_picker = (PickerDateView) this.findViewById(R.id.date_picker);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.backButton:
			this.finish();
			break;
		case R.id.btn_confirm:
			if (validator()) {
				String date_s = date_picker.getStartDate();
				String date_e = date_picker.getEndDate();
				Intent intent = new Intent(this,
						TransferDetailListActivity.class);
				intent.putExtra("date_s", date_s);
				intent.putExtra("date_e", date_e);
				this.startActivity(intent);
			}

			break;

		default:
			break;
		}
	}

	public void setInterval(String interval) {
		if (null == interval || "".equals(interval))
			return;

		// 判断是否为数字
		Pattern pattern = Pattern.compile("[0-9]*");
		if (!pattern.matcher(interval).matches()) {
			Log.e("datePicker", "interval must be number!");
			return;
		}

		this.interval = interval;
	}

	public int getInterval() {
		return Integer.parseInt(this.interval);
	}

	/**
	 * Parse date and time like yyyyMMdd
	 */
	public static java.util.Date parseShortDate(String dt) {
		try {
			return new SimpleDateFormat("yyyyMMdd").parse(dt);
		} catch (Exception e) {
			//
		}
		return null;
	}

	public static String dateFormat(java.util.Date date) {
		try {
			return new SimpleDateFormat("yyyyMMdd").format(date);
		} catch (Exception e) {
			//
		}
		return null;
	}

	// 某月增加一天
	public static String addDays(String date, int days) {
		java.util.Date in = parseShortDate(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(in);
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + days);
		java.util.Date d = cal.getTime();

		return dateFormat(d);
	}

	public boolean validator() {
		// 注意这里采用的是java.sql.Date。
		Date startDate = Date.valueOf(date_picker.getStartDate());
		Date endDate = Date.valueOf(date_picker.getEndDate());

		// 检查结束日期是否大于开始日期
		if (startDate.compareTo(endDate) > 0) {
			Toast.makeText(this, "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
			return false;
		}

		// 检查结束日期与开始日期之间的差距不能大于设置的时间间隔
		if (daysBetween(startDate, endDate) > this.getInterval()) {
			Toast.makeText(this, "开始日期和结束日期相差不能超过" + this.interval + "天",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	// 计算两个日期之间相差的天数。注意这里采用的是java.sql.Date。
	private int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
}
