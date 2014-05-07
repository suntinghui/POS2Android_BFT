package com.bft.slidingmenu;
/**
 * 这里主要是写定侧滑出来的界面的内容
 * */
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bft.pos.R;
import com.bft.pos.activity.CatalogActivity;
import com.bft.pos.activity.DrawingsActivity;
import com.bft.pos.activity.GatherActivity;
import com.bft.pos.activity.ManageActivity;
import com.bft.pos.activity.QueryActivity;
import com.bft.pos.activity.QueryBusinessDepositActivity;
import com.bft.pos.activity.SystemActivity;
import com.bft.pos.activity.TimeoutService;
import com.bft.pos.agent.client.AppDataCenter;
import com.bft.pos.agent.client.HttpManager;
import com.bft.pos.agent.client.SystemConfig;
import com.bft.pos.agent.client.TransferLogic;

public class MenuBaseActivity extends SlidingMenuActivity implements OnClickListener {
	protected Button backButton = null;
	protected TextView titleView = null;
	
	private ListView listView;
	private Integer[] imageIds= {R.drawable.left_icon_1_n, R.drawable.left_icon_2_n, R.drawable.left_icon_3_n,
    		R.drawable.left_icon_4_n, R.drawable.left_icon_5_n};
    private LinearLayout layout;
    private String[] title = {"我的管理", "我要查询", "我要收款", "我要提款", "系统相关"};
    private SimpleAdapter itemsAdapter;
    public  int contentLayout;
    public	int menuLayout;
    public  int index = 0;
    
	private static Stack<MenuBaseActivity> stack = new Stack<MenuBaseActivity>();

	public static final int PROGRESS_DIALOG = 0; // 带滚动条的提示框
	public static final int SIMPLE_DIALOG = 1; // 简单提示框，不带滚动条，需要手动调用hide方法取消
	public static final int MODAL_DIALOG = 2; // 带确定按纽的提示框，需要用户干预才能消失
	public static final int NONMODAL_DIALOG = 3; // 2秒后自动消失的提示框
	public static final int COUNTUP_DIALOG = 4; // 正数计时带滚动条的提示框

	private ProgressDialog progressDialog = null;
	private AlertDialog.Builder simpleDialog = null;
	private AlertDialog.Builder modalDialog = null;
	private AlertDialog.Builder nonModalDialog = null;
	private ProgressDialog countUpDialog = null;

	private AlertDialog tempSimpleDialog = null; // for close 'SimpleDialog'
	private AlertDialog tempModalDialog = null; // for close 'ModalDialog'
	private AlertDialog tempNonModalDialog = null; // for close 'NonModalDialog'

	private String message = null;

	// for countUp
	private String transferCode = null;
	private CountUpTask countUpTask = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(menuLayout, contentLayout);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		layout = (LinearLayout) findViewById(R.id.layout);
		if(this.mDraggingEnabled==false){
			layout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					toggleMenu();
				}
			});
		}else{
			
		}
		
		
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
							DrawingsActivity.class);
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
		// 更新超时时间
			TimeoutService.LastSystemTimeMillis = System.currentTimeMillis();
			stack.push(this);
	}
	
	public void initControl(){
		}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			this.setResult(Activity.RESULT_OK);
			this.finish();
		}
	}
	public void initTitleBar(String title, Boolean hasBack) {
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(listener);
		if (!hasBack) {
			backButton.setVisibility(View.GONE);
		}
		titleView = (TextView) this.findViewById(R.id.title);
		titleView.setText(title);
		titleView.setTextColor(getResources().getColor(R.color.white));
		titleView.setTextSize(20);
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
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// 然后会调用 startActivityForResult();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);

		// 保证跳转页面后本页面没有弹出窗
		this.hideDialog(COUNTUP_DIALOG);
		this.hideDialog(MODAL_DIALOG);
		this.hideDialog(NONMODAL_DIALOG);
		this.hideDialog(PROGRESS_DIALOG);
		this.hideDialog(SIMPLE_DIALOG);
	}

	@Override
	public void finish() {
		synchronized (this) {
			if (!stack.empty()) {
				stack.pop();
			}
		}

		super.finish();
	}

	public static MenuBaseActivity getTopActivity() {
		// TODO 应该怎么处理？
		try {
			return stack.peek();
		} catch (EmptyStackException e) {
			return stack.peek();
		}
	}

	public static ArrayList<MenuBaseActivity> getAllActiveActivity() {
		if (null == stack || stack.isEmpty()) {
			return null;
		}

		ArrayList<MenuBaseActivity> list = new ArrayList<MenuBaseActivity>();
		for (int i = 0; i < stack.size(); i++) {
			list.add(stack.get(i));
		}

		return list;
	}

	// 根据类型弹出不同的等待提示框
	public void showDialog(String message, String transferCode) {
		this.message = message;
		this.transferCode = transferCode;

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showDialog(COUNTUP_DIALOG);
			}

		});

	}

	public void showDialog(final int type, String message) {
		this.message = message;

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showDialog(type);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case PROGRESS_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);

			this.createDefaultDialog();
			progressDialog.setMessage(null == message ? "" : message);
			progressDialog.show();
			break;

		case SIMPLE_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);

			this.createSimpleDialog();
			simpleDialog.setMessage(null == message ? "" : message);
			tempSimpleDialog = simpleDialog.show();
			break;

		case MODAL_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);

			this.createModalDialog();
			modalDialog.setMessage(null == message ? "" : message);
			tempModalDialog = modalDialog.show();
			break;

		case NONMODAL_DIALOG:
			// 这里应该关闭其它提示型的对话框
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(COUNTUP_DIALOG);
			this.hideDialog(MODAL_DIALOG);

			this.createNonModalDialog();
			nonModalDialog.setMessage(null == message ? "请稍候" : message);
			tempNonModalDialog = nonModalDialog.show();
			new NonModalTask().execute();
			break;

		case COUNTUP_DIALOG:
			// 这里应该关闭其它提示型的对话框
			// 不能由于弹出其他窗口而关闭，因为这涉及到交易。比如交易时拔出点付宝会弹出窗口，但是在交易时也不应该关闭。
			this.hideDialog(PROGRESS_DIALOG);
			this.hideDialog(SIMPLE_DIALOG);
			this.hideDialog(NONMODAL_DIALOG);
			this.hideDialog(MODAL_DIALOG);

			this.createCountUpDialog();
			countUpDialog.setMessage(null == message ? this.getResources()
					.getString(R.string.pWaitAMonment) : message);
			countUpDialog.show();

			countUpTask = new CountUpTask();
			countUpTask.execute();

			break;
		}

		return super.onCreateDialog(id);
	}

	private void createDefaultDialog() {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
		}
	}

	private void createSimpleDialog() {
		if (null == simpleDialog) {
			simpleDialog = new AlertDialog.Builder(this);
			simpleDialog.setTitle("提示");
			simpleDialog.setCancelable(false);
		}
	}

	private void createModalDialog() {
		if (null == modalDialog) {
			modalDialog = new Builder(this);
			modalDialog.setTitle("提示");
			modalDialog.setCancelable(false);
			modalDialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							tempModalDialog.dismiss();
							dialog.dismiss();
						}
					});
		}
	}

	private void createNonModalDialog() {
		if (null == nonModalDialog) {
			nonModalDialog = new AlertDialog.Builder(this);
			nonModalDialog.setTitle("提示");
			nonModalDialog.setCancelable(false);
		}
	}

	private void createCountUpDialog() {
		if (null == countUpDialog) {
			countUpDialog = new ProgressDialog(this);
			countUpDialog.setIndeterminate(true);
			countUpDialog.setCancelable(false);
		}
	}

	public void hideDialog(int type) {
		switch (type) {

		case PROGRESS_DIALOG:
			if (null != progressDialog && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			break;

		case SIMPLE_DIALOG:
			if (null != tempSimpleDialog && tempSimpleDialog.isShowing()) {
				tempSimpleDialog.dismiss();
			}
			break;

		case NONMODAL_DIALOG:
			if (null != tempNonModalDialog && tempNonModalDialog.isShowing()) {
				tempNonModalDialog.dismiss();
			}
			break;

		case MODAL_DIALOG: // 一般不要关闭模式对话框，而应该由用户去触发

			break;

		case COUNTUP_DIALOG:
			if (null != countUpTask) {
				countUpTask.cancel(true);
			}

			if (null != countUpDialog && countUpDialog.isShowing()) {
				countUpDialog.dismiss();
			}
			break;
		}
	}

	class NonModalTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (null != tempNonModalDialog && tempNonModalDialog.isShowing()) {
				tempNonModalDialog.dismiss();
			}
		}

	}

	final class CountUpTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			int i = 1;
			while (i <= SystemConfig.getMaxTransferTimeout()) {

				this.publishProgress(i++);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			if (null != countUpDialog && countUpDialog.isShowing()) {
				countUpDialog.setMessage(message + " " + (Integer) values[0]
						+ "s");
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			if (null != countUpDialog && countUpDialog.isShowing()) {
				countUpDialog.dismiss();

				if (null != transferCode) {
					if (AppDataCenter.getReversalMap()
							.containsKey(transferCode)) {
						// TransferLogic.getInstance().gotoCommonFaileActivity("交易超时");
						TransferLogic.getInstance().reversalAction();
					} else if (AppDataCenter.getReversalMap().containsValue(
							transferCode)) {
						// TransferLogic.getInstance().gotoCommonFaileActivity("冲正超时");
					} else {
						// TransferLogic.getInstance().gotoCommonFaileActivity("交易超时，请重试");
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			HttpManager.getInstance().abort();
			super.onCancelled();
		}

	}

	public void showToast(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	// 更新短信计时器
	public void refreshSMSBtn() {

	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.backButton:
				finish();
				break;
			}

		}
	};
}