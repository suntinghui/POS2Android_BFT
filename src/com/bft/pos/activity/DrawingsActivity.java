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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.slidingmenu.MenuBaseActivity;

public class DrawingsActivity extends MenuBaseActivity {
//	定义各种组件
//	定义列表视图
	private ListView listView;
//	定义适配器
	private ManagerAdapter adapter = null;
//	只有一张图片的数组
	private Integer[] imageIds = { R.drawable.gather_left_0,R.drawable.query_left_0,R.drawable.query_left_0};
//	
	private String[] titles = { "提现","账户余额查询","手机充值"};

	@Override
	public void onCreate( Bundle savedInstanceState){
		super.index = 0;
//	依旧是添加了侧滑的内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_manage);
		super.onCreate( savedInstanceState);
//	设置标题
		this.initTitlebar("我要提款");
//		获取控件
//		获取列表组件
		listView = (ListView)this.findViewById(R.id.listview);
//		适配器 在ListView里显示所需要的内容
		adapter = new ManagerAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				点击事件 目前没有后面的内容，所以没有添加
				switch (arg2) {
				case 0:
					//提款
					Intent intent0 = new Intent(DrawingsActivity.this,
							ShowMoneyActivity.class);
					intent0.putExtra("TAG", arg2);
					startActivity(intent0);
					break;
				case 1:
					//账户余额查询
					 Intent intent1 = new Intent(DrawingsActivity.this,
							 ASBalancePwdActivity.class);
					 intent1.putExtra("TAG", arg2);
					 startActivity(intent1);
					break;

				default:
					break;
				}
			}
			
		});
		
	}
//适配器
	public final class ManagerViewHolder{
		public ImageView mLeftIV;
		public TextView mRightTV;
	}
//	依旧是适配器把文字和图片整合在一起
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