package com.bft.pos.activity;
/**
 * 收款界面
 * */
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

public class GatherActivity extends MenuBaseActivity {
//    定义列表组件
	private ListView listView;
//	定义适配器
	private ManagerAdapter adapter = null;
//	定义两个图片资源
	private Integer[] imageIds = { R.drawable.gather_left_0,
			R.drawable.gather_left_1};
//	定义两个文字,用以显示
	private String[] titles = { "收款", "收款撤销"};

	@Override
	public void onCreate( Bundle savedInstanceState){
		super.index = 0;
//		依旧是侧滑界面和
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_manage);
		super.onCreate( savedInstanceState);
//		设置标题	
		this.initTitlebar("我要收款");
//		获取组件
//		获取列表视图
		listView = (ListView)this.findViewById(R.id.listview);
//		定义适配器
		adapter = new ManagerAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
//			现在后面什么都没有,所以以下都是空
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				switch (arg2) {
				case 0:
					Intent intent0 = new Intent(GatherActivity.this,
							InputMoneyActivity.class);
					intent0.putExtra("TAG", arg2);
					startActivity(intent0);
					break;
				case 1:
					break;

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
//	这里应该是吧图片和文字组合在一起
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