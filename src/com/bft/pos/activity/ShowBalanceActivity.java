package com.bft.pos.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.agent.client.TransferLogic;
import com.bft.pos.fsk.CommandControllerEx;
import com.bft.pos.fsk.FSKService;
import com.bft.pos.fsk.FSKStateChangeListener;
import com.dhc.dynamic.util.StringUtil;


/**
 * 因为点付宝将显示余额时触发OnWaitingOper。呈现效果不是太好，故单独写此类，提升用户体验。
 * 
 * @author sth
 *
 */

public class ShowBalanceActivity extends BaseActivity implements OnClickListener {
	
	private TextView messageView = null;
	private Button okButton = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		this.setContentView(R.layout.show_balance);
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.show_balance);
		
		super.onCreate(savedInstanceState);
		
		messageView = (TextView) this.findViewById(R.id.messageView);
		
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
		
		try{
			Intent intent = this.getIntent();
			
			StringBuffer sb = new StringBuffer();
			
			String amount = intent.getStringExtra("balance"); // 20 位，后12位为金额
			if (null != amount && amount.length() == 20){
				String balance = amount.substring(amount.length()-12, amount.length());
				sb.append("账面余额:" + StringUtil.String2SymbolAmount(balance));
			}
			
			String availableAmount = intent.getStringExtra("availableBalance");
			if (null != availableAmount && availableAmount.length() == 20){
				String availableBalance = availableAmount.substring(availableAmount.length()-12, availableAmount.length());
				sb.append("\n");
				sb.append("可用余额:" + StringUtil.String2SymbolAmount(availableBalance));
			}
			
			//String accountNo = intent.getStringExtra("accountNo");
			
			messageView.setText(sb.toString());
			
			// 不能阻塞主线程， 异步执行
			new ShowBalanceTask().execute(sb.toString());
			
		} catch(Exception e){
			TransferLogic.getInstance().gotoCommonFaileActivity("查询余额失败，请重试。");
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()){
		case R.id.okButton:
			this.setResult(RESULT_OK);
			this.finish();
			break;
		}
	}
	
	class ShowBalanceTask extends AsyncTask<Object, Object, Object>{
		
		// 这里是最终用户调用Excute时的接口，当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		@Override
		protected void onPreExecute() {
			//正在操作设备请稍候
			ShowBalanceActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, ShowBalanceActivity.this.getResources().getString(R.string.operatingDevice));
		}
		
		@Override
		protected void onPostExecute(Object result) {
//			ShowBalanceActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
			//余额已显示在设备屏幕上
//			ShowBalanceActivity.this.showDialog(BaseActivity.NONMODAL_DIALOG, ShowBalanceActivity.this.getResources().getString(R.string.showBalanceInDevice));
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			try{
				// 不触发等待用户操作的监听，否则体验不是很好。
				FSKStateChangeListener listener = new FSKStateChangeListener();
				final CommandControllerEx commandController = new CommandControllerEx(ShowBalanceActivity.this, listener);
				commandController.Init(FSKService.check_key);
				
				//余额已显示在设备屏幕上
				ShowBalanceActivity.this.showDialog(BaseActivity.NONMODAL_DIALOG, ShowBalanceActivity.this.getResources().getString(R.string.showBalanceInDevice));
			
				commandController.Cmd_Display((String)arg0[0], 20);
			} catch(Exception e){
				e.printStackTrace();
			}
			return null;
			
			
			/*****
			10-20 01:01:10.810: E/AndroidRuntime(14638): java.lang.RuntimeException: An error occured while executing doInBackground()
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at android.os.AsyncTask$3.done(AsyncTask.java:278)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.FutureTask$Sync.innerSetException(FutureTask.java:273)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.FutureTask.setException(FutureTask.java:124)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:307)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.FutureTask.run(FutureTask.java:137)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1076)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:569)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.lang.Thread.run(Thread.java:864)
			10-20 01:01:10.810: E/AndroidRuntime(14638): Caused by: java.lang.NullPointerException
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at com.itron.protol.android.CommandController.Cmd_Display(CommandController.java:1320)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at com.bft.pos.fsk.CommandControllerEx.Cmd_Display(CommandControllerEx.java:122)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at com.bft.pos.activity.ShowBalanceActivity$ShowBalanceTask.doInBackground(ShowBalanceActivity.java:75)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at android.os.AsyncTask$2.call(AsyncTask.java:264)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:305)
			10-20 01:01:10.810: E/AndroidRuntime(14638): 	... 4 more
			*****/
		}

	}

}
