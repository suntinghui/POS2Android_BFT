package com.bft.pos.activity;
/**
 * 主界面
 * 这个界面不需要侧滑
 * */
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bft.pos.R;

// 目录
public class CatalogActivity extends BaseActivity {
//这里是五个按钮
	private Integer[] imageIds = { R.drawable.catalog_1_button, R.drawable.catalog_2_button, R.drawable.catalog_3_button, R.drawable.catalog_4_button, R.drawable.catalog_5_button };
//这里是五个按钮要的内容
	private String[] titles = { "我的管理", "我要查询", "我要收款", "我要提款", "系统相关" };
//	网格布局
	private GridView gridView = null;
//	适配器
	private CatalogAdapter adapter = null;
//    退出时间 长整型
	private long exitTimeMillis = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//	三列，好多行的布局
		setContentView(R.layout.catalog_activity);
//	获取组件 添加点击相应 绑定适配器
		initTitleBar("主界面", false);
		gridView = (GridView) findViewById(R.id.gridveiw);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setOnItemClickListener(onclickcistener);

		adapter = new CatalogAdapter(this);
		gridView.setAdapter(adapter);

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	// 点击事件
	private OnItemClickListener onclickcistener = new OnItemClickListener() {
//  还是写定了每个点击条目  没一个按钮写定之后，跳转到指定的Activity
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			switch (arg2) {
			case 0:
//			跳转：我的管理
				Intent intent0 = new Intent(CatalogActivity.this, ManageActivity.class);
				intent0.putExtra("TAG", arg2);
				startActivity(intent0);
				break;
			case 1:
//				跳转：我要查询
				Intent intent1 = new Intent(CatalogActivity.this, QueryActivity.class);
				intent1.putExtra("TAG", arg2);
				startActivity(intent1);
				break;
			case 2:
//				跳转：我要收款
				Intent intent2 = new Intent(CatalogActivity.this, GatherActivity.class);
				intent2.putExtra("TAG", arg2);
				startActivity(intent2);
				break;
			case 3:
//              跳转：我要提款
				Intent intent3 = new Intent(CatalogActivity.this, DrawingsActivity.class);
				intent3.putExtra("TAG", arg2);
				startActivity(intent3);
				break;
/*			case 3:
//				跳转：我的存款
				Intent intent3 = new Intent(CatalogActivity.this, QueryBusinessDepositActivity.class);
				intent3.putExtra("TAG", arg2);
				startActivity(intent3);
				break;*/
			case 4:
//				跳转：系统相关
				Intent intent4 = new Intent(CatalogActivity.this, SystemActivity.class);
				intent4.putExtra("TAG", arg2);
				startActivity(intent4);
				break;

			default:
				break;
			}
		}

	};

	// 程序退出 点击两次后退键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTimeMillis = System.currentTimeMillis();
			} else {
				ArrayList<BaseActivity> list = BaseActivity.getAllActiveActivity();
				for (BaseActivity activity : list) {
					activity.finish();
				}

				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public final class CatalogHolder {
//		这里是标题和标题内容
		public ImageView CatalogCellImage;
		public TextView catalogTitleText;
	}

	public class CatalogAdapter extends BaseAdapter {
//   适配器，把按钮和
		private LayoutInflater mInflater;

		public CatalogAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return imageIds.length;
		}

		public Object getItem(int arg0) {
			return titles[arg0];
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			CatalogHolder holder = null;

			if (null == convertView) {
				convertView = this.mInflater.inflate(R.layout.catalog_item, null);
				holder = new CatalogHolder();

				holder.CatalogCellImage = (ImageView) convertView.findViewById(R.id.catalogCellImage);
				holder.catalogTitleText = (TextView) convertView.findViewById(R.id.catalogTitleText);

				convertView.setTag(holder);
			} else {
				holder = (CatalogHolder) convertView.getTag();
			}

			holder.CatalogCellImage.setImageResource(imageIds[position]);
			holder.catalogTitleText.setText(titles[position]);

			return convertView;
		}
	}

}
