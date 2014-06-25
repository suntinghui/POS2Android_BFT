package com.bft.pos.activity;

import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bft.pos.R;
import com.bft.pos.dynamic.core.Event;


public class PrintReceiptActivity extends BaseActivity {
	
	String printContent = "";
	Button closeButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.receipt);
		
		this.findViewById(R.id.closeButton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				PrintReceiptActivity.this.setResult(RESULT_OK);
				PrintReceiptActivity.this.finish();
			}
			
		});
		
		/**或者*/
		
//		closeButton = (Button) this.findViewById(R.id.closeButton);
//		closeButton.setOnClickListener(listener);
		
		
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, String> map = (HashMap<String, String>) this.getIntent().getSerializableExtra("content");
			StringBuffer sb = new StringBuffer();
			for (String key : map.keySet()){
				if (!key.equals("fieldMAB")){
					sb.append(key).append("=").append(map.get(key)).append(";");
				}
			}
			
			printContent = sb.deleteCharAt(sb.length()-1).toString();
			
			new PrintReceiptTask().execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 点击相应
	private OnClickListener listener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			PrintReceiptActivity.this.setResult(RESULT_OK);
			PrintReceiptActivity.this.finish();
		}
		
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    switch (keyCode) {
	        case KeyEvent.KEYCODE_BACK:
	        {
	        	PrintReceiptActivity.this.setResult(RESULT_OK);
				PrintReceiptActivity.this.finish();
	        }
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	class PrintReceiptTask extends AsyncTask<Object, Object, Object>{
		@Override
		protected void onPreExecute() {
			PrintReceiptActivity.this.showDialog(BaseActivity.PROGRESS_DIALOG, PrintReceiptActivity.this.getResources().getString(R.string.operatingDevice));
			
			try{
				Event event = new Event(null,"print", null);
		        String fskStr = "Set_PtrData|string:" + printContent;
		        event.setFsk(fskStr);
		        event.trigger();
				
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			PrintReceiptActivity.this.hideDialog(BaseActivity.PROGRESS_DIALOG);
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			
			return null;
			
		}
		
	}
	
}
