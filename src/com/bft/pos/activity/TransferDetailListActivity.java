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

public class TransferDetailListActivity extends MenuBaseActivity {

		private ListView listView;
		private CollectAdapter adapter = null;
		
		@Override
		public void onCreate( Bundle savedInstanceState){
			super.index = 1;
			setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_transfer_detail_list);
			super.onCreate( savedInstanceState);
			
			this.initTitlebar("交易明细");
			
			listView = (ListView)this.findViewById(R.id.listview);
			
			adapter = new CollectAdapter(this);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent intent0 = new Intent(TransferDetailListActivity.this,
							TransferDetailActivity.class);
							 intent0.putExtra("TAG", arg2);
							 startActivity(intent0);
				}
				
			});
			
		}

		public final class CollectViewHolder{
			public TextView mTransferTypeTV;
			public TextView mTransferMoneyTV;
			public TextView mTransferTimeTV;
			public TextView mTransferCardNumTV;
		}
		
		public class CollectAdapter extends BaseAdapter{
			private LayoutInflater mInflater;
			public CollectAdapter(Context context) {
				this.mInflater = LayoutInflater.from(context);
			}
			
			public int getCount(){
				return 30;
			}
			
			public Object getItem(int arg0){
				return 3;
			}
			
			public long getItemId(int arg0){
				return arg0;
			}
			
			public View getView(int position, View convertView, ViewGroup parent){
				CollectViewHolder holder = null;
				if (null == convertView){
					holder = new CollectViewHolder();
					
					convertView = mInflater.inflate(R.layout.item_trade, null);
					
					
					holder.mTransferTypeTV = (TextView) convertView.findViewById(R.id.transferTypeTV);
					holder.mTransferTimeTV = (TextView) convertView.findViewById(R.id.transferTimeTV);
					holder.mTransferMoneyTV = (TextView) convertView.findViewById(R.id.transferMoneyTV);
					holder.mTransferCardNumTV = (TextView) convertView.findViewById(R.id.transferCardNumTV);
					
					convertView.setTag(holder);
				} else {
					holder = (CollectViewHolder) convertView.getTag();
				}
				
				holder.mTransferTypeTV.setText("收款");
				holder.mTransferTimeTV.setText("2013-01-01 19:19:19");
				holder.mTransferMoneyTV.setText("￥1000");
				holder.mTransferCardNumTV.setText("6227999******9898");
				return convertView;
			}
		}	
	}