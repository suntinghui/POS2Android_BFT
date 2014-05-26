package com.bft.pos.activity;

/**
 * 这里是查询界面
 * 
 * **/
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

public class QueryActivity extends BaseActivity {
	// 获取组件
	private ListView listView;
	private ManagerAdapter adapter = null;
	// 依旧是图片的数组
	// 要泽宇：添加账号交易查询图片，与第一个图片相同的图片
	/*
	 * private Integer[] imageIds = { R.drawable.query_left_0,
	 * R.drawable.query_left_1, R.drawable.query_left_2,
	 * R.drawable.query_left_3, R.drawable.query_left_4, R.drawable.query_left_0
	 * };//新加入的图片
	 */
	private Integer[] imageIds = { R.drawable.query_left_0,
			R.drawable.query_left_0, R.drawable.query_left_0,
			R.drawable.query_left_1, R.drawable.query_left_3 };// 新加入的图片
	// private String[] titles = { "账户余额查询", "银行卡交易查询", "签购单查询", "公告查询",
	// "流量统计","账户交易查询" };
	private String[] titles = { "银行卡余额查询", "账户余额查询", "账户交易查询", "银行卡交易查询",
			"公告查询" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 这里添加了两个布局,一个是manage 标题加listView的模板
		// 一个是侧滑的ws_munday_slidingmenu_test_menu listView
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_manage);
		super.onCreate(savedInstanceState);
		// 设置标题
		this.initTitlebar("我的查询");
		// 获取列表组件
		listView = (ListView) this.findViewById(R.id.listview);
		// 适配器
		adapter = new ManagerAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					// 银行卡余额查询
					Intent intent0 = new Intent(QueryActivity.this,
							QueryBankBalanceActivity.class);
					intent0.putExtra("TAG", arg2);
					startActivity(intent0);
					break;
				case 1:
					// 账户余额查询
					Intent intent1 = new Intent(QueryActivity.this,
							ASBalancePwdActivity.class);
					intent1.putExtra("TAG", arg2);
					startActivity(intent1);
					break;
				case 2:
					// 账户交易查询
					Intent intent2 = new Intent(QueryActivity.this, QBPwd.class);
					intent2.putExtra("TAG", arg2);
					startActivity(intent2);
					break;
				case 3:
					// 银行卡交易查询
					Intent intent3 = new Intent(QueryActivity.this,
							CardPayQueryActivity.class);
					intent3.putExtra("TAG", arg2);
					startActivity(intent3);
					break;
				/*
				 * case 2: // 签购单查询 Intent intent2 = new
				 * Intent(QueryActivity.this, SignBillActivity.class);
				 * intent2.putExtra("TAG", arg2); startActivity(intent2); break;
				 */
				// 还有两个按钮 一个是公告查询，一个是流量统计
				case 4:
					// 公告查询
					Intent intent4 = new Intent(QueryActivity.this,
							AnnouncementListActivity.class);
					intent4.putExtra("TAG", arg2);
					startActivity(intent4);
					break;

				default:
					break;
				}
			}
		});
	}

	// 适配器，适配器
	public final class ManagerViewHolder {
		public ImageView mLeftIV;
		public TextView mRightTV;
	}

	// 依旧是通过适配器把图片和字段统一起来
	public class ManagerAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ManagerAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return imageIds.length;
		}

		public Object getItem(int arg0) {
			return imageIds.length;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ManagerViewHolder holder = null;
			if (null == convertView) {
				holder = new ManagerViewHolder();

				convertView = mInflater.inflate(R.layout.item_left_right, null);

				holder.mLeftIV = (ImageView) convertView
						.findViewById(R.id.leftIV);
				holder.mRightTV = (TextView) convertView
						.findViewById(R.id.rightTV);

				convertView.setTag(holder);
			} else {
				holder = (ManagerViewHolder) convertView.getTag();
			}

			holder.mLeftIV.setImageResource(imageIds[position]);
			holder.mRightTV.setText(titles[position]);
			return convertView;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent(QueryActivity.this,
					CatalogActivity.class);
			startActivity(intent);
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
