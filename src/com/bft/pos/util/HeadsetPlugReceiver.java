package com.bft.pos.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bft.pos.activity.BaseActivity;


public class HeadsetPlugReceiver extends BroadcastReceiver {  
	  
    @Override  
    public void onReceive(Context context, Intent intent) {  
    	String action = intent.getAction();
    	   if(action.equals("ksn")) {
    		   String ksnStr = intent.getStringExtra("ksn");
    		   if(ksnStr.equals("ksn_null")){
//    			  BaseActivity.getTopActivity().showDialog(BaseActivity.NONMODAL_DIALOG, "请插入购买的刷卡头");
    		   }
    	   }
          
    }  
  
}  