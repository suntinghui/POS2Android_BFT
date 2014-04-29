package com.bft.pos.activity;
/**
 * @author Yaozeyu
 * function：账户交易查询流水
 * */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.model.TransferDetailModel;
import com.bft.pos.model.TransferDetailModel1;
import com.bft.pos.util.ActivityUtil;
import com.bft.slidingmenu.MenuBaseActivity;

public class QBTransferHistory extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private Button btn_back = null;
	private Button btn_history = null;
	private ListView listView = null;
	private Adapter adapter = null;

	private int totalPage;
	private int currentPage = 0;

	private String date_s = null;
	private String date_e = null;
	private String t_date_s = null;
	private String t_date_e = null ;
//	传入的密码段
	private String pwdcode = null;
	private ArrayList<TransferDetailModel1> modelList = new ArrayList<TransferDetailModel1>();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
// 		依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.activity_transfer_detail_list1);
		super.onCreate( savedInstanceState);
		this.findViewById(R.id.topInfoView);
		System.out.println("走了oncreate方法");

		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		btn_history = (Button) this.findViewById(R.id.btn_history);
		btn_history.setOnClickListener(this);

		listView = (ListView) this.findViewById(R.id.listview);
//	 	ActivityUtil.setEmptyView(listView);
//		还没有想到什么好办法，暂时用这样来处理，虽然觉得似乎有点不靠谱
		Intent intent = this.getIntent();
			t_date_s = intent.getStringExtra("date_s");
			t_date_e = intent.getStringExtra("date_e");
//			获取传入的密码字段
			pwdcode =  intent.getStringExtra("pwdcode");
			System.out.println(pwdcode);
			gettranferdetail();

		if (t_date_s == null || t_date_s.length() == 0) {
			Calendar c = Calendar.getInstance();
			String year = c.get(Calendar.YEAR) + "";
			String month = String.format("%02d", (c.get(Calendar.MONTH) + 1));
			String day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
			date_s = date_e = year + month + day;
		} else {
			btn_history.setVisibility(View.GONE);
			date_s = t_date_s.replace("-", "");
			date_e = t_date_e.replace("-", "");
		}

		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	public void gettranferdetail(){
		try {
			Event event = new Event(null, "querybal", null);
			event.setTransfer("089028");

			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login",ApplicationEnvironment.getInstance().getPreferences()
					.getString(Constant.PHONENUM, ""));
			map.put("payPass", pwdcode);
			map.put("currPage", "1");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final class ViewHolder {
		public RelativeLayout contentLayout;
		public RelativeLayout moreLayout;

		public TextView tv_account1;
		public TextView tv_amount;
		public TextView tv_local_log;
		public ImageView iv_revoke;

		public Button moreButton;
	}

	public class Adapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			if (currentPage+1 < totalPage) {
				return modelList.size() + 1;
			} else {
				return modelList.size();
			}
		}

		public Object getItem(int arg0) {
			return modelList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.list_item_transfer,
						null);

				holder.contentLayout = (RelativeLayout) convertView
						.findViewById(R.id.contentLayout);
				holder.moreLayout = (RelativeLayout) convertView
						.findViewById(R.id.moreLayout);

				holder.tv_account1 = (TextView) convertView
						.findViewById(R.id.tv_account1);
				holder.tv_amount = (TextView) convertView
						.findViewById(R.id.tv_amount);
				holder.tv_local_log = (TextView) convertView
						.findViewById(R.id.tv_local_log);
				holder.moreButton = (Button) convertView
						.findViewById(R.id.moreButton);
				holder.moreButton
						.setOnClickListener(QBTransferHistory.this);
				holder.iv_revoke = (ImageView) convertView
						.findViewById(R.id.iv_revoke);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (currentPage+1 < totalPage) {
				if (position == modelList.size()) {
					holder.contentLayout.setVisibility(View.GONE);
					holder.moreLayout.setVisibility(View.VISIBLE);
				} else {
					holder.contentLayout.setVisibility(View.VISIBLE);
					holder.moreLayout.setVisibility(View.GONE);

					TransferDetailModel1 model = modelList.get(position);
					if (model.getTradeTypeKey().equals("3")) {
						holder.iv_revoke.setVisibility(View.VISIBLE);
					} else {
						holder.iv_revoke.setVisibility(View.GONE);
					}

					holder.tv_account1.setText(modelList.get(position)
							.getPayDate() == null ? "" : modelList.get(
							position).getPayDate());
					holder.tv_amount.setText(modelList.get(position)
							.getPayMoney() == null ? "" : ("¥ " + modelList.get(
							position).getPayMoney()));
					holder.tv_local_log.setText(modelList.get(position)
							.getOrderStatus() == null ? "" : modelList
							.get(position).getOrderStatus());
				}
			} else {
				holder.contentLayout.setVisibility(View.VISIBLE);
				holder.moreLayout.setVisibility(View.GONE);

				TransferDetailModel1 model = modelList.get(position);
				if (model.getTradeTypeKey().equals("3")) {
					holder.iv_revoke.setVisibility(View.VISIBLE);
				} else {
					holder.iv_revoke.setVisibility(View.GONE);
				}

				holder.tv_account1.setText(modelList.get(position)
						.getPayDate() == null ? "" : modelList.get(position)
						.getPayDate());
				holder.tv_amount
						.setText(modelList.get(position).getPayMoney() == null ? ""
								: ("¥ " + modelList.get(position).getPayMoney()));
				holder.tv_local_log.setText(modelList.get(position)
						.getOrderStatus() == null ? "" : modelList.get(position)
						.getOrderStatus());
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Intent intent = new Intent(QBTransferHistory.this,
				QBTransferDetail.class);
		intent.putExtra("model", modelList.get(arg2));
		startActivity(intent);
		QBTransferHistory.this.onPause();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			Intent intent = new Intent(QBTransferHistory.this,CatalogActivity.class);
			this.startActivity(intent);
			this.finish();
			break;
		case R.id.moreButton:
			loadMoreData();
			break;
		case R.id.btn_history:
//			Intent intent1 = new Intent(this, QBTransferTimer.class);
//			this.startActivity(intent1);
			break;
		default:
			break;
		}
	}

	public void refresh() {
		try {
			Event event = new Event(null, "querybal", null);
			event.setTransfer("089028");

			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("login",ApplicationEnvironment.getInstance().getPreferences()
					.getString(Constant.PHONENUM, ""));
			map.put("payPass", pwdcode);
			map.put("currPage", "2");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void fromLogic(HashMap<String, Object> map) {
		ArrayList<TransferDetailModel1> list = (ArrayList<TransferDetailModel1>) map
				.get("list");
		modelList.addAll(list);
		int count = Integer.parseInt((String) map.get("total"));

		totalPage = (count + Integer.parseInt(Constant.PAGESIZE) - 1)
				/ Integer.parseInt(Constant.PAGESIZE);

		if (modelList != null) {
			adapter.notifyDataSetChanged();
		} else {
			ActivityUtil.setEmptyView(listView);
		}
	}

	private void loadMoreData() {
//		refresh();
	}
}
