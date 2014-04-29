package com.bft.pos.activity;

/**
 *这里是我的管理界面 
 *每一个界面用到的都基本上是一个模板
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

public class ManageActivity extends BaseActivity {
	// 定义组件 列表视图
	private ListView listView;
	// 适配器
	private ManagerAdapter adapter = null;
	// 四个按钮
	private Integer[] imageIds = { R.drawable.manager_right_0_n,
			R.drawable.manager_right_1_n, R.drawable.manager_right_3_n,
			R.drawable.manager_right_2_n, R.drawable.manager_right_3_n,
			R.drawable.manager_right_3_n, R.drawable.manager_right_3_n,R.drawable.manager_right_3_n };
	// 四个文字内容
	private String[] titles = { "签到", "结算", "签退", "实名认证", "修改银行卡", "支付密码设置",
			"修改支付密码","重置支付密码" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_manage);
		super.onCreate(savedInstanceState);
		// 设置标题
		this.initTitlebar("我的管理");
		listView = (ListView) this.findViewById(R.id.listview);
		// 获取列表视图
		listView = (ListView) this.findViewById(R.id.listview);
		// 依旧是适配器去整合部件
		adapter = new ManagerAdapter(this);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 点击四个内容
				switch (arg2) {
				case 0:
					// 点击跳转:签到
					Intent intent0 = new Intent(ManageActivity.this,
							SignInActivity.class);
					intent0.putExtra("TAG", arg2);
					startActivity(intent0);
					break;
				case 1:
					// 点击跳转:结算
					Intent intent1 = new Intent(ManageActivity.this,
							SettlementActivity.class);
					intent1.putExtra("TAG", arg2);
					startActivity(intent1);
					break;
				// 签退
				case 2:
					break;
				// 实名认证
				case 3:
					Intent intent2 = new Intent(ManageActivity.this,
							AuthenticationUpImageActivity.class);
					intent2.putExtra("TAG", arg2);
					startActivity(intent2);
					break;
				// 修改银行卡
				case 4:
					Intent intent4 = new Intent(ManageActivity.this,
							BankNumberActivity.class);
					intent4.putExtra("TAG", arg2);
					startActivity(intent4);
					break;
				case 5:
					// 支付密码设置
					Intent intent = new Intent(ManageActivity.this,
							SetPayPwdActivity.class);
					intent.putExtra("TAG", arg2);
					startActivity(intent);
					break;

				// 提现
				/*
				 * case 5: Intent intent5 = new Intent(ManageActivity.this,
				 * ShowMoneyActivity.class); intent5.putExtra("TAG", arg2);
				 * startActivity(intent5); break;
				 */
				// 修改支付密码
				case 6:
					Intent intent6 = new Intent(ManageActivity.this,
							ModifyPayPwdActivity.class);
					intent6.putExtra("TAG", arg2);
					startActivity(intent6);
					break;
					// 重置支付密码
				case 7:
					Intent intent7 = new Intent(ManageActivity.this,
							ResetPayPwdActivity.class);
					intent7.putExtra("TAG", arg2);
					startActivity(intent7);
					break;	
				default:
					break;
				}
			}
		});
	}

	// 适配器,字段和图片
	public final class ManagerViewHolder {
		public ImageView mLeftIV;
		public TextView mRightTV;
	}

	// 依旧是适配器把图片和文字整合在一起
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
}
