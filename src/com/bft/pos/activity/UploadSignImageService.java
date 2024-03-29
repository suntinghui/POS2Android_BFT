package com.bft.pos.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.agent.client.db.UploadSignImageDBHelper;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;


/**
 * 上传签名图片至服务器
 * 
 * 每次有新的签名图片的时候触发服务，检查数据库是否有数据，如果有数据则在手机存储中查找图片并批量上传
 * 图片上传成功后删掉对应的数据库中的数据
 * 
 * 由于签名图片要求必须上传成功，所以采用类似于冲正的逻辑，每次有新的签名图片需要上传的时候一并检查,
 * 但这也带来问题，也有可能导致有图片不能上传成功。
 * 
 * 
 * @author sth
 *
 */
public class UploadSignImageService extends Service {
	
	private static final long FIVEMIN = 5 * 60 * 1000;

	Timer timer = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try{
			timer = new Timer();
			timer.schedule(new UploadSignImageTimerTask(), 0, FIVEMIN);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return START_NOT_STICKY;
	}
	
	class UploadSignImageTimerTask extends TimerTask{
		@Override
		public void run() {
			Log.e("签购单", "正在检查签购单...");
			BaseActivity.getTopActivity().runOnUiThread(new Runnable(){
				@Override
				public void run() {
					UploadSignImageDBHelper helper = new UploadSignImageDBHelper();
					HashMap<String, String> transferContentMap = helper.queryAUploadSignImageTransfer();
					if (null == transferContentMap){
						Log.e("签购单", "没有检测到需要上传的签购单，停止后台服务...");
						timer.cancel();
						
						UploadSignImageService.this.stopSelf();
					} else {
						Log.e("签购单", "检测到需要上传的签购单，正在发起上传...");
						
						Event event = new Event(null, "uploadSignImage", null);
						event.setTransfer("089014");
						//1个是商户手机号，一个是消费者手机号（接收签购单用）
						// 获取PSAM卡号
						String fsk = "Get_PsamNo|null";
						event.setFsk(fsk);
						
//						transferContentMap.put("mobNo", ApplicationEnvironment.getInstance().getPreferences().getString(Constant.PHONENUM, ""));
						transferContentMap.put("mobNo", Constant.recievePhone);
						transferContentMap.put("signImg", transferContentMap.get("img") );
						transferContentMap.put("tmnOrdNo", transferContentMap.get("field11") );
						transferContentMap.put("orderNo", transferContentMap.get("local_log") );
						transferContentMap.put("tmnNo", transferContentMap.get("field41") );
						transferContentMap.put("mctNo", transferContentMap.get("merchant_id") );
						transferContentMap.put("tmnTime", transferContentMap.get("field12") );
						transferContentMap.put("tmnDate", transferContentMap.get("field13") );
						
						event.setStaticActivityDataMap(transferContentMap);
						try {
							event.trigger();
						} catch (ViewException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			});
		}
	}
	
}