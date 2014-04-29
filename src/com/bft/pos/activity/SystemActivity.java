package com.bft.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

/**
 * 系統相关
 *
 */
public class SystemActivity extends BaseActivity {

	private ListView listView;
	private ManagerAdapter adapter = null;
	
	private Integer[] imageIds = {
			R.drawable.system_left_1, R.drawable.system_left_2,
			R.drawable.system_left_3,};
	
	private String[] titles = {  "意见反馈", "关于系统", "检查更新"};
	@Override
	public void onCreate( Bundle savedInstanceState){
		super.index = 1;
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_manage);
		super.onCreate( savedInstanceState);
		
		this.initTitlebar("系统相关");
		
		listView = (ListView)this.findViewById(R.id.listview);
		
		adapter = new ManagerAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.e("_--", "000");
				switch (arg2) {

				default:
					break;
				}
			}
			
		});
		
	}

	public final class ManagerViewHolder{
		public ImageView mLeftIV;
		public TextView mRightTV;
	}
	
	public class ManagerAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		public ManagerAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount(){
			return imageIds.length;
		}
		
		public Object getItem(int arg0){
			return imageIds.length;
		}
		
		public long getItemId(int arg0){
			return arg0;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			ManagerViewHolder holder = null;
			if (null == convertView){
				holder = new ManagerViewHolder();
				
				convertView = mInflater.inflate(R.layout.item_left_right, null);
				
				
				holder.mLeftIV = (ImageView) convertView.findViewById(R.id.leftIV);
				holder.mRightTV = (TextView) convertView.findViewById(R.id.rightTV);
				
				convertView.setTag(holder);
			} else {
				holder = (ManagerViewHolder) convertView.getTag();
			}
			
			holder.mLeftIV.setImageResource(imageIds[position]);
			holder.mRightTV.setText(titles[position]);
			return convertView;
		}
	}	
}