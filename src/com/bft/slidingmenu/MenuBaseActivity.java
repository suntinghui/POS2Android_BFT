package com.bft.slidingmenu;
/**
 * 这里主要是写定侧滑出来的界面的内容
 * */
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.activity.CatalogActivity;
import com.bft.pos.activity.GatherActivity;
import com.bft.pos.activity.ManageActivity;
import com.bft.pos.activity.QueryActivity;
import com.bft.pos.activity.QueryBusinessDepositActivity;
import com.bft.pos.activity.SystemActivity;

public class MenuBaseActivity extends SlidingMenuActivity implements OnClickListener {
	protected Button backButton = null;
	protected TextView titleView = null;
	
	private ListView listView;
	private Integer[] imageIds= {R.drawable.left_icon_1_n, R.drawable.left_icon_2_n, R.drawable.left_icon_3_n,
    		R.drawable.left_icon_4_n, R.drawable.left_icon_5_n};
    private LinearLayout layout;
    private String[] title = {"我的管理", "我要查询", "我要收款", "我的存款", "系统相关"};
    private SimpleAdapter itemsAdapter;
    public  int contentLayout;
    public	int menuLayout;
    public  int index = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(menuLayout, contentLayout);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);
		
		layout = (LinearLayout) findViewById(R.id.layout);
		layout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		
		
		listView = (ListView)this.findViewById(R.id.listView);
		
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0; i<title.length; i++){
			HashMap<String, Object>map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[i]);
			map.put("ItemTitle", title[i]);
			listItem.add(map);
		
		}
		
		itemsAdapter = new SimpleAdapter(this, listItem, R.layout.left_listitem, 
				new String[]{"ItemTitle","ItemImage"}, new int[]{R.id.ItemTitle, R.id.ItemImage});
		listView.setAdapter(itemsAdapter);
		listView.setSelection(index);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0:
//				点击跳转:我的管理
					Intent intent0 = new Intent(MenuBaseActivity.this,
							ManageActivity.class);
							 intent0.putExtra("TAG", arg2);
							 startActivity(intent0);
							 finish();
					break;
				case 1:
//					点击跳转:我的查询
					Intent intent1 = new Intent(MenuBaseActivity.this,
							QueryActivity.class);
							 intent1.putExtra("TAG", arg2);
							 startActivity(intent1);
							 finish();
					break;
//还有两个按钮,签退和修改用户密码,暂时没有添加
				case 2:
//					点击跳转:我要收款
					Intent intent2 = new Intent(MenuBaseActivity.this,
							GatherActivity.class);
							 intent2.putExtra("TAG", arg2);
							 startActivity(intent2);
							 finish();
					break;
				case 3:
//					点击跳转:我的存款
					Intent intent3 = new Intent(MenuBaseActivity.this,
							QueryBusinessDepositActivity.class);
							 intent3.putExtra("TAG", arg2);
							 startActivity(intent3);
							 finish();
					break;
				case 4:
//					点击跳转:系统相关
					Intent intent4 = new Intent(MenuBaseActivity.this,
							SystemActivity.class);
							 intent4.putExtra("TAG", arg2);
							 startActivity(intent4);
							 finish();
					break;
				default:
					break;
				}
				
				
			}
		});
		
	}
	
	protected void initTitlebar(String title) {
		
		backButton = (Button) this.findViewById(R.id.backButton);
		titleView = (TextView) this.findViewById(R.id.title);
		
		titleView.setText(title);
		
		backButton.setOnClickListener(this);
	}

	public void setLayoutIdsTest(int menuLayoutId, int contentLayoutId) {
		menuLayout = menuLayoutId;
        contentLayout = contentLayoutId;
    }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
//			this.finish();
			Intent intent4 = new Intent(MenuBaseActivity.this,
					CatalogActivity.class);
					 startActivity(intent4);
			break;

		default:
			break;
		}
		
	}
}