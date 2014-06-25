package com.bft.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.agent.client.db.TransferSuccessDBHelper;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.model.TransferSuccessModel;
import com.bft.pos.util.DateUtil;
import com.bft.pos.util.StringUtil;
import com.dhc.dynamic.parse.ParseView;

public class RevokeTransListActivity extends BaseActivity implements OnClickListener {
	
	private Button backButton 			= null;
	private ListView listView 			= null;
	
	private ArrayList<TransferSuccessModel> transList = new ArrayList<TransferSuccessModel>();
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
	private RevokeAdapter adapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.revoke_trans_list);
		super.onCreate(savedInstanceState);

		this.findViewById(R.id.topInfoView);
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		
		listView = (ListView) this.findViewById(R.id.transList);
		adapter = new RevokeAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TransferSuccessModel tmpModel = transList.get(arg2);
				if(tmpModel.getFlag().equals("0")){
					
				}else{
					// 跳转到确认页面
					if(Constant.isAISHUA){
						Intent intent = new Intent(RevokeTransListActivity.this, ASRevokePwd3Activity.class);
						
						try {

							Event event = new Event(null, "swip", null);
							String fsk = "swipeCard|null";
							event.setFsk(fsk);
							event.setActionType(ParseView.ACTION_TYPE_LOCAL);
							event.setAction("RevokeTransConfirmActivity");
							HashMap<String, String> map = transList.get(arg2).getContent();
							event.setStaticActivityDataMap(map);
							event.trigger();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						Intent intent = new Intent(RevokeTransListActivity.this, RevokeTransConfirmActivity.class);
						intent.putExtra("map", transList.get(arg2).getContent());
						startActivityForResult(intent, 0);		
					}			
				}
			}
			
		});
    	
    	new QueryRevokeListTask().execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 当撤销一笔交易结束时回退到本列表界面。
		/*
		if (resultCode == Activity.RESULT_OK){
			new QueryRevokeListTask().execute();
		}
		*/
		
		// 直接调用父类的方法，结果是撤销一笔交易结束时直接回到交易结果界面。
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.backButton:
			// 这一步的返回我觉得应该是连同关闭前一个输入密码的界面
			this.setResult(RESULT_OK);
			this.finish();
			break;
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		listView.setAdapter(adapter);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			this.setResult(RESULT_OK);
			this.finish();
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	class QueryRevokeListTask extends AsyncTask<Object, Object, Object>{

		@Override
		protected void onPreExecute() {
			RevokeTransListActivity.this.showDialog(PROGRESS_DIALOG, RevokeTransListActivity.this.getResources().getString(R.string.refreshingData));
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
			// 从数据库中查询出的所有需要进行撤销的交易
			transList = helper.queryNeedRevokeTransfer();
			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			
			// 设置EmptyView
	    	ImageView emptyView = new ImageView(RevokeTransListActivity.this);
			emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			emptyView.setImageResource(R.drawable.nodata);
			emptyView.setScaleType(ScaleType.CENTER_INSIDE);
			((ViewGroup)listView.getParent()).addView(emptyView);
			listView.setEmptyView(emptyView);
			
			adapter.notifyDataSetChanged();
			RevokeTransListActivity.this.hideDialog(PROGRESS_DIALOG);
			
		}
		
	}
	
	public final class RevokeViewHolder{
		public TextView revokeAccountNo;
		public TextView revokeDateTime;
		public TextView revokeAmount;
		public ImageView iv_revoke;
		
	}
	
	public class RevokeAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		public RevokeAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount(){
			return transList.size();
		}
		
		public Object getItem(int arg0){
			return transList.get(arg0);
		}
		
		public long getItemId(int arg0){
			return arg0;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			RevokeViewHolder holder = null;
			if (null == convertView){
				holder = new RevokeViewHolder();
				
				convertView = mInflater.inflate(R.layout.queryrevoke_listitem, null);
				
				holder.revokeAccountNo = (TextView) convertView.findViewById(R.id.revokeAccountNo);
				holder.revokeDateTime = (TextView) convertView.findViewById(R.id.revokeDateTime);
				holder.revokeAmount = (TextView) convertView.findViewById(R.id.revokeAmount);
				holder.iv_revoke = (ImageView) convertView.findViewById(R.id.iv_revoke);
				convertView.setTag(holder);
			} else {
				holder = (RevokeViewHolder) convertView.getTag();
			}
			TransferSuccessModel model = transList.get(position);
			if(model.getFlag().equals("0")){
				holder.iv_revoke.setVisibility(View.VISIBLE);
			}else{
				holder.iv_revoke.setVisibility(View.GONE);
			}
			holder.revokeAccountNo.setText(StringUtil.formatAccountNo(model.getContent().get("field2")));
			holder.revokeDateTime.setText(DateUtil.formatDateTime(DateUtil.getDate(1) + model.getContent().get("field13") + model.getContent().get("field12")));
			holder.revokeAmount.setText(StringUtil.String2SymbolAmount(model.getContent().get("field4")));
			return convertView;
		}
	}

}
