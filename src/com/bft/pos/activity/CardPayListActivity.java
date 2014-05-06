package com.bft.pos.activity;

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
import com.bft.pos.model.CardPayModel;
import com.bft.pos.util.ActivityUtil;

public class CardPayListActivity extends BaseActivity implements
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
	private String t_date_e = null;
	private int spinnerid = 0;
	// 传入的密码段
	// private String pwdcode = null;
	private ArrayList<CardPayModel> modelList = new ArrayList<CardPayModel>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_transfer_detail_list1);
		super.onCreate(savedInstanceState);
		this.findViewById(R.id.topInfoView);
		System.out.println("走了oncreate方法");

		btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		btn_history = (Button) this.findViewById(R.id.btn_back);
		btn_history.setOnClickListener(this);

		listView = (ListView) this.findViewById(R.id.listview);
		// ActivityUtil.setEmptyView(listView);
		Intent intent = this.getIntent();
		t_date_s = intent.getStringExtra("date_s");
		t_date_e = intent.getStringExtra("date_e");
		spinnerid = intent.getIntExtra("spinner", 0);
		System.out.println(spinnerid + "~~~~~~~~~~~~~");
		// 获取传入的密码字段
		// pwdcode = intent.getStringExtra("pwdcode");
		// System.out.println(pwdcode);
		gettranferdetail();

		if (t_date_s == null || t_date_s.length() == 0) {
			Calendar c = Calendar.getInstance();
			String year = c.get(Calendar.YEAR) + "";
			String month = String.format("%02d", (c.get(Calendar.MONTH) + 1));
			String day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
			date_s = date_e = year + month + day;
		} else {
			// btn_history.setVisibility(View.GONE);
			date_s = t_date_s.replace("-", "");
			date_e = t_date_e.replace("-", "");
		}
		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	public void gettranferdetail() {
		Event event = new Event(null, "queryTransList", null);
		event.setTransfer("089000");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("login", ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constant.PHONENUM, ""));
		// map.put("login","流川枫");
		map.put("currPage", ++currentPage + "");
		map.put("pageNum", Constant.PAGESIZE);
		map.put("startDt", CardPayQueryActivity.dateFormates(t_date_s));
		map.put("endDt", CardPayQueryActivity.dateFormates(t_date_e));
		map.put("type", spinnerid + "");
		event.setStaticActivityDataMap(map);
		try {
			event.trigger();
		} catch (ViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final class ViewHolder {
		public RelativeLayout contentLayout;
		public RelativeLayout moreLayout;

		public TextView tradedate;
		public TextView tradetotal;
		public TextView tradestatus;
		public ImageView iv_revoke;

		public Button moreButton;
	}

	public class Adapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			if (currentPage + 1 < totalPage) {
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

				holder.tradedate = (TextView) convertView
						.findViewById(R.id.tv_account1);
				holder.tradetotal = (TextView) convertView
						.findViewById(R.id.tradetotal);
				holder.tradestatus = (TextView) convertView
						.findViewById(R.id.tradestatus);
				holder.moreButton = (Button) convertView
						.findViewById(R.id.moreButton);
				holder.moreButton.setOnClickListener(CardPayListActivity.this);
				holder.iv_revoke = (ImageView) convertView
						.findViewById(R.id.iv_revoke);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (currentPage + 1 < totalPage) {
				if (position == modelList.size()) {
					holder.contentLayout.setVisibility(View.GONE);
					holder.moreLayout.setVisibility(View.VISIBLE);
				} else {
					holder.contentLayout.setVisibility(View.VISIBLE);
					holder.moreLayout.setVisibility(View.GONE);

					CardPayModel model = modelList.get(position);
					if (model.getTradestatus().equals("3")) {
						holder.iv_revoke.setVisibility(View.VISIBLE);
					} else {
						holder.iv_revoke.setVisibility(View.GONE);
					}

					holder.tradedate.setText(modelList.get(position)
							.getTradedata() == null ? "" : modelList.get(
							position).getTradedata());
					holder.tradetotal.setText(modelList.get(position)
							.getTradetotal() == null ? "" : ("¥ " + modelList
							.get(position).getTradetotal()));
					holder.tradestatus.setText(modelList.get(position)
							.getTradestatus() == null ? "" : modelList.get(
							position).getTradestatus());
				}
			} else {
				holder.contentLayout.setVisibility(View.VISIBLE);
				holder.moreLayout.setVisibility(View.GONE);

				CardPayModel model = modelList.get(position);
				if (model.getTradetype().equals("3")) {
					holder.iv_revoke.setVisibility(View.VISIBLE);
				} else {
					holder.iv_revoke.setVisibility(View.GONE);
				}

				holder.tradedate
						.setText(modelList.get(position).getTradedata() == null ? ""
								: modelList.get(position).getTradedata());
				holder.tradetotal.setText(modelList.get(position)
						.getTradetotal() == null ? "" : ("¥ " + modelList.get(
						position).getTradetotal()));
				holder.tradestatus.setText(modelList.get(position)
						.getTradestatus() == null ? "" : modelList
						.get(position).getTradestatus());
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Intent intent = new Intent(CardPayListActivity.this,
				CardPayDetailActivity.class);
		intent.putExtra("model", modelList.get(arg2));
		CardPayListActivity.this.startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			Intent intent = new Intent(CardPayListActivity.this,
					CardPayQueryActivity.class);
			this.startActivity(intent);
			this.finish();
			break;
		case R.id.moreButton:
			loadMoreData();
			break;
		// case R.id.btn_history:
		// Intent intent1 = new Intent(this, QBTransferTimer.class);
		// this.startActivity(intent1);
		// break;
		default:
			break;
		}
	}

	public void refresh() {
		Event event = new Event(null, "queryTransList", null);
		event.setTransfer("089000");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("login", ApplicationEnvironment.getInstance().getPreferences()
				.getString(Constant.PHONENUM, ""));
		// map.put("login","流川枫");
		map.put("currPage", ++currentPage + "");
		map.put("pageNum", Constant.PAGESIZE);
		map.put("startDt", CardPayQueryActivity.dateFormates(t_date_s));
		map.put("endDt", CardPayQueryActivity.dateFormates(t_date_e));
		map.put("type", "1");
		event.setStaticActivityDataMap(map);
		try {
			event.trigger();
		} catch (ViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
	@SuppressWarnings("unchecked")
	public void fromLogic(HashMap<String, Object> map) {
		ArrayList<CardPayModel> list = (ArrayList<CardPayModel>) map
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
		refresh();
	}
}