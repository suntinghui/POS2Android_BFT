package com.bft.pos.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.model.TransferDetailModel;
import com.bft.pos.util.StringUtil;

public class TransferDetailActivity extends BaseActivity implements
		OnClickListener {

	private TransferDetailModel model = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_transfer_detail);

		this.findViewById(R.id.topInfoView);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		Button btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		model = (TransferDetailModel) getIntent().getSerializableExtra("model");

		TextView tv_merchant_name = (TextView) this
				.findViewById(R.id.tv_merchant_name);
		TextView tv_merchant_id = (TextView) this
				.findViewById(R.id.tv_merchant_id);
		TextView tv_terminal_id = (TextView) this
				.findViewById(R.id.tv_terminal_id);
		TextView tv_account1 = (TextView) this.findViewById(R.id.tv_account1);
		TextView tv_localdate = (TextView) this.findViewById(R.id.tv_localdate);
		TextView tv_systransid = (TextView) this
				.findViewById(R.id.tv_systransid);
		TextView tv_snd_log = (TextView) this.findViewById(R.id.tv_snd_log);
		TextView tv_local_log = (TextView) this.findViewById(R.id.tv_local_log);
		TextView tv_snd_cycle = (TextView) this.findViewById(R.id.tv_snd_cycle);
		TextView tv_amount = (TextView) this.findViewById(R.id.tv_amount);
		TextView tv_flag = (TextView) this.findViewById(R.id.tv_flag);
		ImageView iv_handsign = (ImageView) this.findViewById(R.id.iv_handsign);

		tv_merchant_name.setText(model.getMerchant_name());
		tv_merchant_id.setText(model.getMerchant_id());
		tv_terminal_id.setText(model.getTerminal_id());
		tv_account1.setText(StringUtil.formatAccountNo(model.getAccount1()));
		tv_localdate.setText(model.getLocaldate() + model.getLocaltime());
		String typeStr = null;
		if (model.getSystransid().equals("200000")) {
			typeStr = "消费撤销";
		} else if (model.getSystransid().equals("000000")) {
			typeStr = "消费";
		}
		tv_systransid.setText(typeStr);
		tv_snd_log.setText(model.getSnd_log());
		tv_local_log.setText(model.getLocal_log());
		tv_snd_cycle.setText(model.getSnd_cycle());
		tv_amount.setText("¥ " + model.getAmount());
		String flagStr = null;
		if (model.getFlag().equals("0")) {
			flagStr = "已完成";
		} else if (model.getFlag().equals("1")) {
			flagStr = "未完成";
		} else if (model.getFlag().equals("2")) {
			flagStr = "已冲正";
		} else if (model.getFlag().equals("3")) {
			flagStr = "已撤销";
		}
		tv_flag.setText(flagStr);
		iv_handsign.setImageBitmap(this.stringtoBitmap(model.getImg()));

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_confirm:
			this.finish();
			break;

		default:
			break;
		}
	}

	public Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}
