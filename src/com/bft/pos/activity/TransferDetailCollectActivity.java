package com.bft.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class TransferDetailCollectActivity extends MenuBaseActivity {

	private ListView listView;
	private CollectAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 1;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_transfer_detail_collect);
		super.onCreate(savedInstanceState);

		this.initTitlebar("交易明细汇总");

		listView = (ListView) this.findViewById(R.id.listview);

		adapter = new CollectAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent0 = new Intent(TransferDetailCollectActivity.this,
						TransferDetailListActivity.class);
				intent0.putExtra("TAG", arg2);
				startActivity(intent0);
			}

		});

	}

	public final class CollectViewHolder {
		public TextView mTransferTypeTV;
		public TextView mTransferCountTV;
		public TextView mTransferMoneyTV;
	}

	public class CollectAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public CollectAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return 3;
		}

		public Object getItem(int arg0) {
			return 3;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			CollectViewHolder holder = null;
			if (null == convertView) {
				holder = new CollectViewHolder();

				convertView = mInflater.inflate(R.layout.item_trade_collect,
						null);

				holder.mTransferTypeTV = (TextView) convertView
						.findViewById(R.id.transferTypeTV);
				holder.mTransferCountTV = (TextView) convertView
						.findViewById(R.id.transferCountTV);
				holder.mTransferMoneyTV = (TextView) convertView
						.findViewById(R.id.transferMoneyTV);

				convertView.setTag(holder);
			} else {
				holder = (CollectViewHolder) convertView.getTag();
			}
			holder.mTransferTypeTV.setText("收款");
			holder.mTransferCountTV.setText("5");
			holder.mTransferMoneyTV.setText("￥1000");
			return convertView;
		}
	}
}