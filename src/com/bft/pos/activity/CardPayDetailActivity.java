package com.bft.pos.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.model.CardPayModel;
import com.bft.pos.util.StringUtil;

public class CardPayDetailActivity extends BaseActivity implements
		OnClickListener {

	private CardPayModel model = null;
	private Button btn_back, btn_confirm;
	TextView cardnum, tradedate, tradetype, cardadress, tradetime, cardtype,
			tradetotal, tradestatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_transfer_detail);
		this.findViewById(R.id.topInfoView);

		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		btn_confirm = (Button) this.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		model = (CardPayModel) getIntent().getSerializableExtra("model");

		// cardadress = (TextView) this.findViewById(R.id.cardadress);
		// tradetime = (TextView) this.findViewById(R.id.tradetime);
		// cardtype = (TextView) this.findViewById(R.id.cardtype);
		// tradetotal = (TextView) this.findViewById(R.id.tradetotal);
		// tradestatus = (TextView) this.findViewById(R.id.tradestatus);
		// // ImageView iv_handsign = (ImageView)
		// this.findViewById(R.id.iv_handsign);

		cardnum.setText(StringUtil.formatAccountNo(model.getPan()));
		tradedate.setText(model.getDate());
		tradetype.setText(model.getTranstype());
		cardadress.setText(model.getInstflag());
		tradetime.setText(model.getDate());
		cardtype.setText(model.getType());
		tradetotal.setText("¥ " + model.getAmttrans());

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
