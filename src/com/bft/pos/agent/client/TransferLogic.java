package com.bft.pos.agent.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.bft.pos.activity.ASBalanceSuccessActivity;
import com.bft.pos.activity.BaseActivity;
import com.bft.pos.activity.CardPayListActivity;
import com.bft.pos.activity.CatalogActivity;
import com.bft.pos.activity.FailActivity;
import com.bft.pos.activity.LoginActivity;
import com.bft.pos.activity.LoginFailActivity;
import com.bft.pos.activity.PayPwdSuccess;
import com.bft.pos.activity.PrintReceiptActivity;
import com.bft.pos.activity.QBTransferHistory;
import com.bft.pos.activity.ReceiptSuccessActivity;
import com.bft.pos.activity.RegisterSuccessActivity;
import com.bft.pos.activity.SetNewLoginPwdActivity;
import com.bft.pos.activity.SetPayPwdActivity;
import com.bft.pos.activity.SettlementSuccessActivity;
import com.bft.pos.activity.ShowBalanceActivity;
import com.bft.pos.activity.SuccessActivity;
import com.bft.pos.activity.TimeoutService;
import com.bft.pos.activity.UploadSignImageService;
import com.bft.pos.agent.client.db.ReversalDBHelper;
import com.bft.pos.agent.client.db.TransferSuccessDBHelper;
import com.bft.pos.agent.client.db.UploadSignImageDBHelper;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.dynamic.core.ViewPage;
import com.bft.pos.fsk.FSKOperator;
import com.bft.pos.model.CardPayModel;
import com.bft.pos.model.FieldModel;
import com.bft.pos.model.TransferDetailModel1;
import com.bft.pos.model.TransferModel;
import com.bft.pos.model.TransferSuccessModel;
import com.bft.pos.util.AssetsUtil;
import com.bft.pos.util.FileUtil;
import com.bft.pos.util.PhoneUtil;
import com.bft.pos.util.PopupMessageUtil;
import com.bft.pos.util.StringUtil;

public class TransferLogic {
	private String verndor = null;
	private String terid = null;
	private static final String GENERALTRANSFER = "GENERALTRANSFER";
	private static final String UPLOADSIGNIMAGETRANSFER = "UPLOADSIGNIMAGETRANSFER";

	public static TransferLogic instance = null;

	private HashMap<String, TransferPacketThread> transferMap = null;

	public static TransferLogic getInstance() {
		if (null == instance) {
			instance = new TransferLogic();
		}
		return instance;
	}

	public TransferLogic() {
		transferMap = new HashMap<String, TransferPacketThread>();
	}

	// 动态机制通过此方法执行相应的逻辑。
	public void transferAction(String transferCode, HashMap<String, String> map) {
		Handler transferHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				// 只能处理且只用处理正确的消息
				switch (msg.what) {
				case 0:
					actionDone((HashMap<String, String>) msg.obj);
					break;
				}
			}
		};

		/** 进行逻辑处理 **/
		TransferPacketThread thread = new TransferPacketThread(transferCode,
				map, transferHandler);

		if (transferCode.equals("089014")) { // 签购单上传 500000001
			transferMap.put(TransferLogic.UPLOADSIGNIMAGETRANSFER, thread);
		} else {
			transferMap.put(TransferLogic.GENERALTRANSFER, thread);
		}
		thread.start();
	}

	private void actionDone(HashMap<String, String> fieldMap) {
		String transferCode = fieldMap.get("fieldTrancode");

		if ("089016".equals(transferCode)) { // 登录
			this.loginDone(fieldMap);

		} else if ("089000".equals(transferCode)) { // 卡交易详单查询
			this.querytranslist(fieldMap);
		} else if ("089026".equals(transferCode)) { // 卡交易详单查询
			this.querycardtrade(fieldMap);
		} else if ("089001".equals(transferCode)) { // 注册
			this.registrDone(fieldMap);

		} else if ("089007".equals(transferCode)) { // 获取提款银行账号
			// BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
			this.getBankAccountDone(fieldMap);

		} else if ("089004".equals(transferCode)) { // 公告列表
			this.getAnnounceListDone(fieldMap);

		} else if ("089010".equals(transferCode)) { // 完善注册
			this.registImproveDone(fieldMap);

		} else if ("089011".equals(transferCode)) { // 获取支行
			this.getBranchBankDone(fieldMap);

		} else if ("089012".equals(transferCode)) { // 搜索支行
			this.getBranchBankDone(fieldMap);

		} else if ("089013".equals(transferCode)) { // 获取商户注册信息
			this.getMerchantInfoDone(fieldMap);

		} else if ("089022".equals(transferCode)) { // 设置新密码 支付
			this.SetNewPayPwdDone(fieldMap);

		} else if ("089018".equals(transferCode)) { // 版本号
			this.getVersionDone(fieldMap);

		} else if ("089025".equals(transferCode)) { // 提现
			this.drawCashDone(fieldMap);

		} else if ("089020".equals(transferCode)) { // 实名认证
			this.authenticationDone(fieldMap);

		} else if ("089021".equals(transferCode)) { // 验证码（生成图片用）
			this.getVerifyCodesDone(fieldMap);

		} else if ("089027".equals(transferCode)) { // 账户余额查询
			this.getbalanceDone(fieldMap);

		} else if ("089028".equals(transferCode)) { // 账户交易查询
			this.QBTDone(fieldMap);

		} else if ("089029".equals(transferCode)) { // 修改银行卡号
			this.modifyBankNoDone(fieldMap);

		} else if ("089024".equals(transferCode)) { // 修改密码
			this.modifyPwdDone(fieldMap);

		} else if ("089006".equals(transferCode)) { // 短信验证码
			this.getSmsDone(fieldMap);

		} else if ("089008".equals(transferCode)) { // 新增提款银行账号
			this.addBankAccountDone(fieldMap);

		} else if ("089009".equals(transferCode)) { // 修改提款银行账号
			this.modifyBankAccountDone(fieldMap);

		} else if ("100005".equals(transferCode)) { // 检验商户密码 登陆
			this.loginDone(fieldMap);

		} else if ("020001".equals(transferCode)) { // 银行卡余额查询
			this.queryBalanceDone(fieldMap);

		} else if ("080000".equals(transferCode)) { // 签到
			
			this.signDone(fieldMap);
		} else if("050000".equals(transferCode)){ // 批结算
			
			this.settlementDone(fieldMap);
		} else if ("020022".equals(transferCode)) { // 收款
			this.receiveTransDone(fieldMap);

		} else if ("020023".equals(transferCode)) { // 收款撤销
			this.revokeTransDone(fieldMap);
		
		} else if ("400000022".equals(transferCode)) { // 收款冲正
			this.reversalDone(fieldMap);
		} else if ("400000023".equals(transferCode)) { // 收款冲正
			this.reversalDone(fieldMap);
			
		} else if ("080003".equals(transferCode)) { // 商户余额查询
			this.balanceQueryDone(fieldMap);

		} else if ("089023".equals(transferCode)) { // 重置支付密码
			this.resetPayPwdDone(fieldMap);

		} else if ("089031".equals(transferCode)) { // 找回密码 验证用户信息
			this.findPwdDone(fieldMap);

		} else if ("089032".equals(transferCode)) { // 设置新密码 登录
			this.setNewPwdDone(fieldMap);

		} else if ("089034".equals(transferCode)) { // 下载RSA公钥
			this.getPublicKeyDone(fieldMap);

		} else if (AppDataCenter.getReversalMap().containsValue(transferCode)) { // 冲正
			gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));

		} else if ("200001111".equals(transferCode)) { // 银行卡付款
			this.bankTransferDone(fieldMap);

		} else if ("089014".equals(transferCode)) { // 签购单上传 500000001
			this.uploadReceiptDone(fieldMap);
		} else if ("600000001".equals(transferCode)) {
			this.queryHistoryGroupDone(fieldMap);

		} else if ("600000002".equals(transferCode)) {
			this.queryHistoryListDone(fieldMap);

		} else if ("999000002".equals(transferCode)) {
			this.checkUpdateAPK(fieldMap);

		} else if ("999000003".equals(transferCode)) {
			this.downloadSecurityCode(fieldMap);

		} else {
			gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));
		}
	}

	/**
	 * 冲正
	 */
	private void reversalDone(HashMap<String, String> fieldMap) {
	
	}
	
	private void getPublicKeyDone(HashMap<String, String> fieldMap) {
		System.out.println("###############下载公钥处理###############");
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "获取密钥成功" : desc;
			// 屏幕中间弹窗
			Toast toast = Toast.makeText(BaseActivity.getTopActivity(), desc,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			Map<String, Object> HEADER_MAP = (HashMap<String, Object>) Constant.HEADER_MAP;
			String pubKey = "";
			if (HEADER_MAP != null) {
				pubKey = (String) HEADER_MAP.get("pubKey") != null ? (String) HEADER_MAP
						.get("pubKey") : "";
				// 存储公钥
				FileUtil.writeFile("publicKey", pubKey, false);
			}
			// System.out.println("PUBLICKEY:\t" + Constant.PUBLICKEY);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "获取密钥失败" : desc;
			// 屏幕中间弹窗
			Toast toast = Toast.makeText(BaseActivity.getTopActivity(), desc,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	// 详单
	private void querytranslist(HashMap<String, String> fieldMap) {

	}

	// 卡交易
	private void querycardtrade(HashMap<String, String> fieldMap) {
		int i = 0;
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<CardPayModel> cpm = new ArrayList<CardPayModel>();

		String rtCd = fieldMap.get("rtCd");
		if (rtCd.equals("00")) {
			try {
				String jsonStr = fieldMap.get("pageList");
				String page_count = fieldMap.get("totalNum");
				map.put("total", page_count);
				JSONTokener parse = new JSONTokener(jsonStr);
				JSONArray jsonArray = (JSONArray) parse.nextValue();
				if (jsonArray != null && jsonArray.length() > 0) {
					for (i = 0; i < jsonArray.length(); i++) {
						JSONObject picsObj = (JSONObject) jsonArray.opt(i);
						CardPayModel model = new CardPayModel();
						model.setTranstype(picsObj.optString("transType",""));
						model.setType(picsObj.optString("type", ""));
						model.setPan(picsObj.optString("pan", ""));
						System.out.println(picsObj.getString("type")+"~~~~~~~~~~~~~~~~~~");

						model.setInstflag(picsObj.getString("instFlag"));
						model.setAmttrans(picsObj.getString("amtTrans"));
						model.setDate(picsObj.optString("date", ""));
						model.setState(picsObj.optString("state", ""));
						cpm.add(model);
					}
				}
				map.put("list", cpm);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			CardPayListActivity activity = (CardPayListActivity) BaseActivity
					.getTopActivity();
			activity.fromLogic(map);

		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("获取卡交易流水失败");
		}
	}

	/**
	 * 重置支付密码
	 */
	private void resetPayPwdDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "重置支付密码成功" : desc;
			TransferLogic.getInstance().gotoCommonPayPwdSuccessActivity(desc);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "重置支付密码失败" : desc;
//			TransferLogic.getInstance().gotoCommonFaileActivity(desc);
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 修改银行卡号
	 */
	private void modifyBankNoDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "修改银行卡号成功" : desc;
			/** 需要完善处理 此处暂已支付密码成功页面使用 */
			TransferLogic.getInstance().gotoCommonPayPwdSuccessActivity(desc);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "修改银行卡号失败" : desc;
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 登陆
	 */
	@SuppressLint("CommitPrefEdits")
	private void loginDone(HashMap<String, String> fieldMap) {
		String desc = null;
		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
				.edit();
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {

			/*
			 * String jsonStr = fieldMap.get("apires"); HashMap<String, String>
			 * receiveFieldMap = new HashMap<String, String>();
			 * 
			 * try { if(jsonStr != null){ JSONTokener parse = new
			 * JSONTokener(jsonStr); JSONObject content = (JSONObject)
			 * parse.nextValue();
			 * 
			 * Iterator<String> keys = content.keys(); while (keys.hasNext()) {
			 * String key = (String) keys.next(); receiveFieldMap.put(key,
			 * content.getString(key)); }
			 * 
			 * editor.putString(Constant.MD5KEY, receiveFieldMap.get("md5key"));
			 * editor.putString(Constant.MERCHERNAME,
			 * receiveFieldMap.get("merchant_name")); editor.commit();
			 * Constant.status = receiveFieldMap.get("status"); } } catch
			 * (JSONException e) { e.printStackTrace(); }
			 */
			// 登陆成功，跳转到菜单界面
			Intent intent = new Intent(BaseActivity.getTopActivity(),
					CatalogActivity.class);
			BaseActivity.getTopActivity().startActivity(intent);
			BaseActivity.getTopActivity().finish();

		} else if (fieldMap.get("rtCd") != null
				&& fieldMap.get("rtCd").equals("05")) {
			desc = "密钥过期，请更新最新密钥；";
			// 屏幕中间弹窗
			Toast toast = Toast.makeText(BaseActivity.getTopActivity(), desc,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			try {
				Event event = new Event(null, "getPublicKey", null);
				event.setTransfer("089034");
				// 获取PSAM卡号
				String fsk = "Get_PsamNo|null";
				if (Constant.isAISHUA) {
					fsk = "getKsn|null";
				}
				// event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("version", "1");// 软件版本号
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "登陆失败" : desc;
			// 屏幕下弹窗
			// Toast.makeText(BaseActivity.getTopActivity(), desc,2).show();
			// 屏幕中间弹窗
			// Toast toast = Toast.makeText(BaseActivity.getTopActivity(), desc,
			// Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
			// toast.show();
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 登陆和签到
	 */
	private void loginSignDone(HashMap<String, String> fieldMap) {
		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
				.edit();

		try {

			if (null != fieldMap) {

				// // 先判断公钥是否需要更新
				// if (fieldMap.get("keyFlag").equals("1")){ // 0-不需更新 1-需要更新
				// // 更新公钥信息
				// String[] keys = parsePublickey(fieldMap.get("field62"));
				//
				// editor.putString(Constant.PUBLICKEY_MOD, keys[0]);
				// editor.putString(Constant.PUBLICKEY_EXP, keys[1]);
				// editor.putString(Constant.PUBLICKEY_VERSION,
				// fieldMap.get("fieldnewVersion"));
				// editor.putString(Constant.PUBLICKEY_TYPE,
				// fieldMap.get("publicKeyType"));
				//
				// this.gotoCommonFaileActivity("由于安全密钥已更新，请您重新登陆");
				// return;
				// }

				// 保存商户名称
				if (fieldMap.containsKey("field43")) {
					editor.putString(Constant.MERCHERNAME,
							fieldMap.get("filed43"));
				}
				if (fieldMap.containsKey("field41")) {
					AppDataCenter.setField41(fieldMap.get("field41"));
				}
				if (fieldMap.containsKey("field42")) {
					AppDataCenter.setField42(fieldMap.get("field42"));
				}

				// 启动超时退出服务
				Intent intent = new Intent(BaseActivity.getTopActivity(),
						TimeoutService.class);
				BaseActivity.getTopActivity().startService(intent);

				// 根据62域字符串切割得到工作密钥
				String newKey = fieldMap.get("field62").replace(" ", "");
				String pinKey = null;
				String macKey = null;
				String stackKey = null;

				try {
					if (null != newKey && !"".equals(newKey)) {

						// 标准
						pinKey = newKey.substring(0, 40);
						macKey = newKey.substring(40, 56)
								+ newKey.substring(72);
						// macKey = newKey.substring(40, 80);
						stackKey = pinKey;

					} else {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 保存工作密钥
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case 0:
							// 登陆成功，跳转到菜单界面
							BaseActivity.getTopActivity().showDialog(
									BaseActivity.PROGRESS_DIALOG, "登录成功");
							BaseActivity.getTopActivity().startActivity(
									new Intent("com.bft.pos.lrcatalog"));
							BaseActivity.getTopActivity().finish();
							break;
						}
					}

				};
				StringBuffer sb = new StringBuffer();
				sb.append("Get_RenewKey|string:").append(pinKey)
						.append(",string:").append(macKey).append(",string:")
						.append(stackKey);
				FSKOperator.execute(sb.toString(), handler);
			} else {
				this.gotoLoginFaileActivity("服务器返回异常");
			}
		} catch (Exception e) {
			this.gotoLoginFaileActivity("服务器返回异常");
			e.printStackTrace();
		} finally {
			editor.commit();
		}
	}

	/**设置商户号、终端号 灌入机具中；
	 * @param vendor
	 * @param terid
	 */
	private void setVendorTerId(String vendor, String terid) {
		Event event = new Event(null, "login", null);
		String fsk = String.format("Get_RenewVendorTerID|string:%s,string:%s",
				vendor, terid);
		event.setFsk(fsk);
		try {
			event.trigger();
		} catch (ViewException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 签到
	 * 
	 * 当点击签到按纽后并从服务器返回JSON后执行此方法。
	 * 
	 * 执行此方法说明服务器端签到正常，没有其他的异常情况发生。 签到成功后 1、首先要更新工作密钥。按长度分别切割 2、然后将收款撤销表清空。
	 */
	private void signDone(final HashMap<String, String> fieldMap) {
		if(fieldMap.get("field39").equals("00")){
			
		}
		// 更新批次号
		String batchNum = fieldMap.get("field60").replace(" ", "")
				.substring(2, 8); // 60域不带长度信息
		verndor = fieldMap.get("field42");
		terid = fieldMap.get("field41");

		// 启动超时退出服务
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				TimeoutService.class);
		BaseActivity.getTopActivity().startService(intent);

		Editor editor = ApplicationEnvironment.getInstance().getPreferences()
				.edit();
		// 保存商户名称
		if (fieldMap.containsKey("field43")) {
			editor.putString(Constant.MERCHERNAME, fieldMap.get("filed43"));
		}
		editor.commit();

		AppDataCenter.setBatchNum(batchNum);

		// 清空上一个批次的交易成功的信息，即用于消费撤销和查询签购单的数据库表
		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
		if (helper.deleteTransfers()) {
			Log.e("debug", "更换批次后 成功 清空需清除的成功交易！");
		} else {
			Log.e("debug", "更换批次后清空需清除的成功交易 失败 ！");
		}

		// 根据62域字符串切割得到工作密钥
		String newKey = fieldMap.get("field62").replace(" ", "");
		String pinKey = null;
		String macKey = null;
		String stackKey = null;

		
		/**佰付通*/
		//由于前一个字节（两个长度）是秘钥索引
		newKey = newKey.substring(2);
		try{
			if (null != newKey && !"".equals(newKey)){
				
				// 标准
				pinKey = newKey.substring(0, 40);
				macKey = newKey.substring(40, 80);
				stackKey = newKey.substring(80, 120);
				
			} else { 
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/**众付宝*/
//		 try {
//			if (null != newKey && !"".equals(newKey)) {
//
//				// 标准
//				pinKey = newKey.substring(0, 40);
//				macKey = newKey.substring(40, 56) + newKey.substring(72);
//				stackKey = pinKey;
//
//			} else {
//				return;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		// 保存工作密钥
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
					String tips = "\t\t\t\t\t签到成功\n\n[设备已成功更新工作密钥]";
					Constant.isSign = true;
					if (Constant.isAISHUA) {
						tips = "签到成功！";
						AppDataCenter.setVENDOR(verndor);
						AppDataCenter.setTERID(terid);
					} else {
						//终端灌入商户号、终端号
//						setVendorTerId(verndor, terid);
						AppDataCenter.setVENDOR(verndor);
						AppDataCenter.setTERID(terid);
					}
					TransferLogic.getInstance().gotoCommonSuccessActivity(tips);
					break;
				}
			}

		};
		if (Constant.isAISHUA) {
			Constant.mackey = newKey.substring(40, 56);
			Constant.pinkey = newKey.substring(0, 32);
			String tips = "签到成功\n\n[设备已成功更新工作密钥]";
			Constant.isSign = true;
			tips = "签到成功！";
			AppDataCenter.setVENDOR(verndor);
			AppDataCenter.setTERID(terid);
			TransferLogic.getInstance().gotoCommonSuccessActivity(tips);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("Get_RenewKey|string:").append(pinKey).append(",string:")
			.append(macKey).append(",string:").append(stackKey);
			FSKOperator.execute(sb.toString(), handler);
			//			setVendorTerId(verndor, terid);
		}
	}

	/**
	 * 结算
	 */
	private void settlementDone(HashMap<String, String> fieldMap) {
		try {
			// 更新批次号
			String batchNum = fieldMap.get("field60").replace(" ", "")
					.substring(2, 8); // 60域不带长度信息
			
			AppDataCenter.setBatchNum(batchNum);
			
			// 清空上一个批次的交易成功的信息，即用于消费撤销和查询签购单的数据库表
			TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
			if (helper.deleteTransfers()) {
				Log.e("debug", "更换批次后 成功 清空需清除的成功交易！");
			} else {
				Log.e("debug", "更换批次后清空需清除的成功交易 失败 ！");
			}
			
			String field48 = fieldMap.get("field48");
			String debitAmount = field48.substring(0, 12);
			String debitCount = field48.substring(12, 15);
			String creditAmount = field48.substring(15, 27);
			String creditCount = field48.substring(27, 30);

			HashMap<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("fieldMessage", fieldMap.get("fieldMessage"));
			tempMap.put("debitAmount",
					StringUtil.String2SymbolAmount(debitAmount));
			tempMap.put("debitCount", debitCount);
			tempMap.put("creditAmount",
					StringUtil.String2SymbolAmount(creditAmount));
			tempMap.put("creditCount", creditCount);

			Intent intent = new Intent(BaseActivity.getTopActivity(),
					SettlementSuccessActivity.class);
			intent.putExtra("map", tempMap);
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);

		} catch (Exception e) {
			this.gotoCommonFaileActivity("结算统计失败，请重试");
		}

	}

	/**
	 * 签退
	 */
	private void signOffDone(HashMap<String, String> fieldMap) {
		gotoCommonSuccessActivity(fieldMap.get("fieldMessage"));
	}

	/**
	 * 注册
	 */
	private void registrDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt") && !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "已注册成功" : desc;
			
			String merchantNo = fieldMap.get("merchantNo").trim();
			String terminalNo = fieldMap.get("terminalNo").trim();
			
//			设置商户号终端号 灌入机具
			if(merchantNo!=null && merchantNo!=null){
				setVendorTerId(merchantNo, terminalNo);
			}else{
				Log.i("注册:", "收到收单系统的商户号或终端号为空，无法灌入进机具。严重！！！");
			}
			// TransferLogic.getInstance().gotoCommonSuccessActivity(desc);
			TransferLogic.getInstance().gotoCommonLoginSuccessActivity(desc);
			
		} else if (fieldMap.get("rtCd") != null
				&& fieldMap.get("rtCd").equals("05")) {
			desc = "密钥过期，请更新最新密钥；";
			
			PopupMessageUtil.showMSG_middle(desc);
			
			try {
				Event event = new Event(null, "getPublicKey", null);
				event.setTransfer("089034");
				// 获取PSAM卡号
				String fsk = "Get_PsamNo|null";
				if (Constant.isAISHUA) {
					fsk = "getKsn|null";
				}
				// event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("version", "1");// 软件版本号
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "注册失败" : desc;
//			BaseActivity.getTopActivity().finish();
//			TransferLogic.getInstance().gotoLoginFaileActivity(desc);
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 完善注册
	 */
	private void registImproveDone(HashMap<String, String> fieldMap) {
		if (fieldMap.containsKey("respmsg")) {
			if ("1".equals(fieldMap.get("respmsg"))) {
				gotoCommonSuccessActivity("注册信息已完善");
			} else if ("0".equals(fieldMap.get("respmsg"))) {
				TransferLogic.getInstance().gotoLoginFaileActivity("操作失败");
			}
		}
	}

	/**
	 * 实名认证
	 */
	private void authenticationDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");

			Intent intent = new Intent(BaseActivity.getTopActivity(),
					SetPayPwdActivity.class);
			BaseActivity.getTopActivity().startActivity(intent);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "认证失败" : desc;
			
			PopupMessageUtil.showMSG_middle2(desc);
		}

	}

	/**
	 * 找回密码 验证用户信息
	 */
	private void findPwdDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");

			Intent intent = new Intent(BaseActivity.getTopActivity(),
					SetNewLoginPwdActivity.class);
			BaseActivity.getTopActivity().startActivity(intent);
			;
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "验证失败" : desc;
			
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 找回密码 设置新登陆密码
	 */
	private void setNewPwdDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "设置登陆密码成功" : desc;
			// TransferLogic.getInstance().gotoCommonSuccessActivity(desc);
			TransferLogic.getInstance().gotoCommonLoginSuccessActivity(desc);
		} else if (fieldMap.get("rtCd") != null
				&& fieldMap.get("rtCd").equals("05")) {
			desc = "密钥过期，请更新最新密钥；";
			// 屏幕中间弹窗
			Toast toast = Toast.makeText(BaseActivity.getTopActivity(), desc,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			try {
				Event event = new Event(null, "getPublicKey", null);
				event.setTransfer("089034");
				// 获取PSAM卡号
				String fsk = "Get_PsamNo|null";
				if (Constant.isAISHUA) {
					fsk = "getKsn|null";
				}
				// event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("version", "1.0");// 软件版本号
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "设置登陆密码失败" : desc;
			// 屏幕中间弹窗
//			Toast toast = Toast.makeText(BaseActivity.getTopActivity(), desc,
//					Toast.LENGTH_LONG);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 设置支付密码
	 */
	private void SetNewPayPwdDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "设置支付密码成功" : desc;
			// Intent intent = new Intent(ApplicationEnvironment.getInstance()
			// .getApplication().getPackageName()
			// + ".showBalanceAishua");
			// intent.putExtra("balance", fieldMap.get("field54"));
			// intent.putExtra("availableBalance", fieldMap.get("field4"));
			// intent.putExtra("accountNo", fieldMap.get("field2"));
			// intent.putExtra("message", fieldMap.get("fieldMessage"));
			// BaseActivity.getTopActivity().startActivityForResult(intent, 0);

			TransferLogic.getInstance().gotoCommonPayPwdSuccessActivity(desc);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "设置支付密码失败" : desc;
			
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 版本号
	 */
	private void getVersionDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 提现
	 */
	private void drawCashDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "成功" : desc;
			TransferLogic.getInstance().gotoCommonSuccessActivity(desc);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "失败" : desc;
		
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 修改密码
	 */
	private void modifyPwdDone(HashMap<String, String> fieldMap) {
		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "修改密码成功" : desc;
			TransferLogic.getInstance().gotoCommonPayPwdSuccessActivity(desc);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "修改密码失败" : desc;
			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 短信验证码
	 */
	private void getSmsDone(HashMap<String, String> fieldMap) {
		// 以短信形式接收
		BaseActivity.getTopActivity().refreshSMSBtn();
	}

	/**
	 * 验证码(生成图片)
	 */
	private void getVerifyCodesDone(HashMap<String, String> fieldMap) {
		// String verifycode = fieldMap.get("verifyCode");
		// Intent intent = new
		// Intent(BaseActivity.getTopActivity().getIntent().getAction());
		// intent.putExtra("code", verifycode);
		// BaseActivity.getTopActivity().startActivity(intent);

		String verifycode = fieldMap.get("verifyCode");
		System.out.println("获得的验证码是：" + verifycode);
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				LoginActivity.class);
		intent.putExtra("code", verifycode);
		BaseActivity.getTopActivity().startActivity(intent);

	}

	/**
	 * 账户余额查询
	 */

	private void getbalanceDone(HashMap<String, String> fieldMap) {
		String rtCd = fieldMap.get("rtCd");
		if (rtCd.equals("00")) {
			String accBlc = fieldMap.get("accBlc");
			Intent intent = new Intent(BaseActivity.getTopActivity(),
					ASBalanceSuccessActivity.class);
			intent.putExtra("accBlc", accBlc);
			BaseActivity.getTopActivity().startActivity(intent);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("获取账户余额失败");
		}
	}

	/**
	 * 账户交易流水
	 */
	private void QBTDone(HashMap<String, String> fieldMap) {
		int i = 0;
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<TransferDetailModel1> arrayModel = new ArrayList<TransferDetailModel1>();
		String rtCd = fieldMap.get("rtCd");
		if (rtCd.equals("00")) {
			try {
				String jsonStr = fieldMap.get("pageList");
				String page_count = fieldMap.get("totalNum");
				map.put("total", page_count);
				JSONTokener parse = new JSONTokener(jsonStr);
				JSONArray jsonArray = (JSONArray) parse.nextValue();
				if (jsonArray != null && jsonArray.length() > 0) {
					for (i = 0; i < jsonArray.length(); i++) {
						JSONObject picsObj = (JSONObject) jsonArray.opt(i);
						TransferDetailModel1 model = new TransferDetailModel1();
						model.setTradeDate(picsObj.optString("tradeDate", ""));
						System.out.println(picsObj.optString("tradeDate", ""));
						model.setPayMoney(picsObj.optString("payMoney", ""));
						model.setTradeTypeKey(picsObj.optString("tradeTypeKey",
								""));
						model.setPayDate(picsObj.optString("payDate", ""));
						model.setOrderStatus(picsObj.optString("orderStatus",
								""));
						arrayModel.add(model);
					}
				}
				map.put("list", arrayModel);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			QBTransferHistory activity = (QBTransferHistory) BaseActivity
					.getTopActivity();
			activity.fromLogic(map);

		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("获取交易流水失败");
		}
	}

	/**
	 * 获取提款银行账号
	 */
	private void getBankAccountDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 交易流水
	 */
	private void QueryTransListDone(HashMap<String, String> fieldMap) {
		int i = 0;
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<TransferDetailModel1> arrayModel = new ArrayList<TransferDetailModel1>();

		String rtCd = fieldMap.get("rtCd");
		if (rtCd.equals("00")) {
			try {
				String jsonStr = fieldMap.get("pageList");
				String page_count = fieldMap.get("totalNum");
				map.put("total", page_count);
				JSONTokener parse = new JSONTokener(jsonStr);
				JSONArray jsonArray = (JSONArray) parse.nextValue();
				if (jsonArray != null && jsonArray.length() > 0) {
					for (i = 0; i < jsonArray.length(); i++) {
						JSONObject picsObj = (JSONObject) jsonArray.opt(i);
						TransferDetailModel1 model = new TransferDetailModel1();
						model.setTradeDate(picsObj.optString("tradeDate", ""));
						System.out.println(picsObj.optString("tradeDate", ""));
						model.setPayMoney(picsObj.optString("payMoney", ""));
						model.setTradeTypeKey(picsObj.optString("tradeTypeKey",
								""));
						model.setPayDate(picsObj.optString("payDate", ""));
						model.setOrderStatus(picsObj.optString("orderStatus",
								""));
						arrayModel.add(model);
					}
				}
				map.put("list", arrayModel);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			QBTransferHistory activity = (QBTransferHistory) BaseActivity
					.getTopActivity();
			activity.fromLogic(map);

		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity("获取交易流水失败");
		}
	}

	/**
	 * 公告查询
	 */
	private void getAnnounceListDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 获取商户注册信息
	 */
	private void getMerchantInfoDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 获取支行
	 */
	private void getBranchBankDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 新增提款银行帐号
	 */
	private void addBankAccountDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 修改提款银行帐号
	 */
	private void modifyBankAccountDone(HashMap<String, String> fieldMap) {

	}

	/**
	 * 解析公钥字符串，得到mod exp
	 * 
	 * @param key
	 *            公钥的ascii
	 * @return mod exp 数组
	 */
	private String[] parsePublickey(String key) {
		// 计算key的字节长度并保存到数组
		String[] bytes = new String[key.length() / 2];
		for (int i = 0; i < key.length() / 2; i++) {
			bytes[i] = key.substring(i * 2, i * 2 + 2);
		}
		// Byte Length
		int index = 1;
		int length = Integer.parseInt(bytes[index], 16);
		if (length > 128)
			index += length - 128;

		// Modulus Length
		index += 2;
		length = Integer.parseInt(bytes[index], 16);
		if (length > 128) {
			int i = length - 128;
			String lenStr = "";
			for (int j = index + 1; j < index + i + 1; j++)
				lenStr += bytes[j];

			index += i;
			length = Integer.parseInt(lenStr, 16);
		}

		// 保存mod值
		StringBuffer modBuff = new StringBuffer();
		for (int i = index + 1; i < index + 1 + length; i++)
			modBuff.append(bytes[i]);

		// Exponent Length
		index += length + 2;
		length = Integer.parseInt(bytes[index], 16);
		if (length > 128) {
			int i = length - 128;
			String lenStr = "";
			for (int j = index + 1; j < index + i + 1; j++)
				lenStr += bytes[j];

			index += i;
			length = Integer.parseInt(lenStr, 16);
		}

		// 保存exponent值
		index += 1;
		StringBuffer expBuff = new StringBuffer();
		for (int i = index; i < index + length; i++)
			expBuff.append(bytes[i]);

		return new String[] { modBuff.toString(), expBuff.toString() };
	}

	/**
	 * 这是第一次以后的查询交易明细，直接从第一次的请求报文中拿出所有的值，以后只是替换欲请求的页码。五个值包括密码也直接从原请求报文中取值
	 * 
	 * @param currentPage
	 *            要请求的当前页面
	 */
	public void queryHistoryAction(String currentPage) {
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER)
				.getSendMap();
		// 验证上次请求的信息是否还存在，如果存在则直接更新上次的请求页面直接请求数据，否则失败让用户重新操作

		if (null != sendMap && sendMap.containsKey("fieldTrancode")) {
			if (sendMap.containsKey("field11")) {
				sendMap.put("TRACEAUDITNUM", sendMap.get("field11")); // 还是使用一个流水号
			}
			sendMap.put("pageNo", currentPage); // 替换查询页码
			this.transferAction(sendMap.get("fieldTrancode"), sendMap);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity(
					"查询明细时出现异常，请重试！");
		}
	}

	private void queryHistoryGroupDone(HashMap<String, String> fieldMap) {
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER)
				.getSendMap();
		if (null != sendMap && sendMap.containsKey("BeginDate")
				&& sendMap.containsKey("EndDate")) {
			Intent intent = new Intent(
					"com.bft.pos.queryTransferHistoryGroupActivity");
			intent.putExtra("detail", fieldMap.get("detail"));
			intent.putExtra("BeginDate", sendMap.get("BeginDate"));
			intent.putExtra("EndDate", sendMap.get("EndDate"));
			BaseActivity.getTopActivity().startActivity(intent);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity(
					"查询明细时出现异常，请重试！");
		}
	}

	private void queryHistoryListDone(HashMap<String, String> fieldMap) {
		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER)
				.getSendMap();
		if (null != sendMap && sendMap.containsKey("totalCount")) {
			Intent intent = new Intent("com.bft.pos.queryTransferHistoryList");
			intent.putExtra("map", fieldMap);
			intent.putExtra("totalCount",
					Integer.parseInt(sendMap.get("totalCount")));
			BaseActivity.getTopActivity().startActivity(intent);
		} else {
			TransferLogic.getInstance().gotoCommonFaileActivity(
					"查询明细时出现异常，请重试！");
		}
	}

	/**
	 * 银行卡余额查询
	 */
	private void queryBalanceDone(final HashMap<String, String> fieldMap) {
		if (Constant.isAISHUA) {
			Intent intent = new Intent(ApplicationEnvironment.getInstance()
					.getApplication().getPackageName()
					+ ".showBalanceAishua");
			intent.putExtra("balance", fieldMap.get("field54"));
			intent.putExtra("availableBalance", fieldMap.get("field4"));
			intent.putExtra("accountNo", fieldMap.get("field2"));
			intent.putExtra("message", fieldMap.get("fieldMessage"));
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		} else {
			Intent intent = new Intent(BaseActivity.getTopActivity(),
					ShowBalanceActivity.class);
			intent.putExtra("balance", fieldMap.get("field54"));
			intent.putExtra("availableBalance", fieldMap.get("field4"));
			intent.putExtra("accountNo", fieldMap.get("field2"));
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		}
	}

	/**
	 * 收款或收款撤销成功，记录数据以备查询签购单
	 */
	private void recordSuccessTransfer(HashMap<String, String> fieldMap) {
		TransferSuccessModel model = new TransferSuccessModel();
		model.setAmount(fieldMap.get("field4"));
		model.setTraceNum(fieldMap.get("field11"));
		model.setTransCode(fieldMap.get("fieldTrancode"));
		model.setDate(fieldMap.get("field13"));
		model.setFlag(fieldMap.get("flag"));
		model.setContent(fieldMap);
		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
		boolean flag = helper.insertATransfer(model);
		if (!flag) {
			Log.e("DATABASE", "成功交易写入数据库时操作失败。。。");
		}
	}

	/**
	 * 收款
	 */
	private void receiveTransDone(HashMap<String, String> fieldMap) {
		fieldMap.put("flag", "0");
		recordSuccessTransfer(fieldMap);

		try {
			if (Constant.isAISHUA) {
				ViewPage transferViewPage = new ViewPage("transfersuccess");
				Event event = new Event(transferViewPage, "transfersuccess",
						"transfersuccess");
				event.setStaticActivityDataMap(fieldMap);
				transferViewPage.addAnEvent(event);
				event.trigger();
				
			} else {
//				旧 001903   新 001917
//				if (!AppDataCenter.getValue("__TERSERIALNO")
//						.startsWith("001917")) {
//					// 打印
//					Intent intent = new Intent(BaseActivity.getTopActivity(),PrintReceiptActivity.class);
//				
//					intent.putExtra("content", fieldMap);
//					BaseActivity.getTopActivity().startActivityForResult(intent, 0);
//				} else {
//					Intent intent = new Intent(BaseActivity.getTopActivity(), ReceiptSuccessActivity.class);
//					intent.putExtra("content", fieldMap);
//					BaseActivity.getTopActivity().startActivity(intent);
//				}
				Intent intent = new Intent(BaseActivity.getTopActivity(), ReceiptSuccessActivity.class);
				intent.putExtra("content", fieldMap);
				BaseActivity.getTopActivity().startActivity(intent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		/***
		 * try{ SharedPreferences sp =
		 * ApplicationEnvironment.getInstance().getPreferences(); String
		 * deviceIDs = sp.getString(Constant.DEVICEID, ""); String[] deviceArray
		 * = deviceIDs.split("\\|"); for (String str : deviceArray) { if
		 * (!str.equals("") &&
		 * str.equals(AppDataCenter.getValue("__TERSERIALNO").substring(13))){
		 * ViewPage transferViewPage = new ViewPage("transfersuccess"); Event
		 * event = new
		 * Event(transferViewPage,"transfersuccess","transfersuccess");
		 * event.setStaticActivityDataMap(fieldMap);
		 * transferViewPage.addAnEvent(event); event.trigger();
		 * 
		 * return; } }
		 * 
		 * // 打印 Intent intent = new Intent("com.bft.pos.printReceipt");
		 * intent.putExtra("content", fieldMap);
		 * BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		 * }catch(Exception e){ e.printStackTrace(); }
		 ****/
	}

	/**
	 * 收款撤销
	 */
	private void revokeTransDone(HashMap<String, String> fieldMap) {
		fieldMap.put("flag", "1");
		recordSuccessTransfer(fieldMap);

		HashMap<String, String> sendMap = transferMap.get(GENERALTRANSFER)
				.getSendMap();
		TransferSuccessDBHelper helper = new TransferSuccessDBHelper();
		helper.updateRevokeFalg(sendMap.get("field61").substring(6));

		try {
			if (Constant.isAISHUA) {
				ViewPage transferViewPage = new ViewPage("transfersuccess");
				Event event = new Event(transferViewPage, "transfersuccess",
						"transfersuccess");
				event.setStaticActivityDataMap(fieldMap);
				transferViewPage.addAnEvent(event);
				event.trigger();
			} else {
//				旧 001903   新 001917
//				if (AppDataCenter.getValue("__TERSERIALNO")
//						.startsWith("001917")) {
//					// 打印
//					Intent intent = new Intent(BaseActivity.getTopActivity(),PrintReceiptActivity.class);
//				
//					intent.putExtra("content", fieldMap);
//					BaseActivity.getTopActivity().startActivityForResult(intent, 0);
//
//				} else {
//					Intent intent = new Intent(BaseActivity.getTopActivity(), ReceiptSuccessActivity.class);
//					intent.putExtra("content", fieldMap);
//					BaseActivity.getTopActivity().startActivity(intent);
//				}
				Intent intent = new Intent(BaseActivity.getTopActivity(), ReceiptSuccessActivity.class);
				intent.putExtra("content", fieldMap);
				BaseActivity.getTopActivity().startActivity(intent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		/***
		 * try{ SharedPreferences sp =
		 * ApplicationEnvironment.getInstance().getPreferences(); String
		 * deviceIDs = sp.getString(Constant.DEVICEID, ""); String[] deviceArray
		 * = deviceIDs.split("\\|"); for (String str : deviceArray) { if
		 * (!str.equals("") &&
		 * str.equals(AppDataCenter.getValue("__TERSERIALNO").substring(13))){
		 * ViewPage transferViewPage = new ViewPage("transfersuccess"); Event
		 * event = new
		 * Event(transferViewPage,"transfersuccess","transfersuccess");
		 * event.setStaticActivityDataMap(fieldMap);
		 * transferViewPage.addAnEvent(event); event.trigger();
		 * 
		 * return; } }
		 * 
		 * // 打印 Intent intent = new Intent("com.bft.pos.printReceipt");
		 * intent.putExtra("content", fieldMap);
		 * BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		 * }catch(Exception e){ e.printStackTrace(); }
		 ***/
	}

	/**
	 * 商户余额查询
	 */
	private void balanceQueryDone(HashMap<String, String> fieldMap) {
		if (Constant.isAISHUA) {
			Intent intent = new Intent(ApplicationEnvironment.getInstance()
					.getApplication().getPackageName()
					+ ".showTiKuanBalanceAishua");
			intent.putExtra("balance", fieldMap.get("field54"));
			BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		} else {
			// Intent intent = new
			// Intent(ApplicationEnvironment.getInstance().getApplication().getPackageName()
			// + ".showBalance");
			// intent.putExtra("balance", fieldMap.get("field54"));
			// intent.putExtra("availableBalance", fieldMap.get("field4"));
			// intent.putExtra("accountNo", fieldMap.get("field2"));
			// intent.putExtra("message", fieldMap.get("fieldMessage"));
			// BaseActivity.getTopActivity().startActivityForResult(intent, 0);
		}
	}

	/**
	 * 银行账户转账 (付款)
	 */
	private void bankTransferDone(HashMap<String, String> fieldMap) {
		Intent intent = new Intent(ApplicationEnvironment.getInstance()
				.getApplication().getPackageName()
				+ ".transferSuccessSendSms");
		intent.putExtra("map", fieldMap);
		BaseActivity.getTopActivity().startActivityForResult(intent, 0);
	}

	/**
	 * 冲正
	 */
	public boolean reversalAction() {

		if (Constant.isStatic){ 
			return false; 
		}

		ReversalDBHelper helper = new ReversalDBHelper(); 
		HashMap<String, String> map = helper.queryNeedReversal();

		if (null == map || map.size() == 0){
			return false; 
		} else {
			BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG,
					"正在发起冲正交易，请稍候...");

			// 更新冲正表，则冲正次数加1。 // 注意这可能有问题，因为如果网络不通，直接没有从手机中发出交易，也已经使冲正次数发生变更
			ReversalDBHelper DBhelper = new ReversalDBHelper();
			DBhelper.updateReversalCount(map.get("field11"));


			// 将原交易的transferCode改为对应的冲正的transferCode map.put("fieldTrancode",
			AppDataCenter.getReversalMap().get(map.get("fieldTrancode"));

			map.put("fieldTrancode", AppDataCenter.getReversalMap().get(map.get("fieldTrancode")));

			if(map.get("fieldTrancode").equals("400000022")){
				map.remove("field26");
				map.remove("field52");
				map.put("fieldTransType", "0400");
			}

			if(map.get("fieldTrancode").equals("400200023")){
				map.remove("field26");
				map.remove("field52");
				map.put("fieldTransType", "0400");
			}

			this.transferAction(map.get("fieldTrancode"), map);

			return true; 
		}
	}

	/**
	 * 签购单上传
	 * 
	 * 上传签购单接口也用于只传送手机号，如转账
	 */
	public void uploadReceiptAction(HashMap<String, String> fieldsMap) {
		// 静态演示模式下不上传签购单。
		if (Constant.isStatic) {
			BaseActivity.getTopActivity().setResult(BaseActivity.RESULT_OK);
			BaseActivity.getTopActivity().finish();

			return;
		}

		try {
			HashMap<String, String> map = new HashMap<String, String>();
			// map.put("field41", AppDataCenter.getValue("__TERID"));
			// map.put("field42", AppDataCenter.getValue("__VENDOR"));
			// map.put("termMobile", AppDataCenter.getValue("__PHONENUM"));
			// map.put("ReaderID", AppDataCenter.getValue("__TERSERIALNO"));
			// map.put("PSAMID", AppDataCenter.getValue("__PSAMNO"));
			//
			// map.put("field7",
			// transferMap.get(GENERALTRANSFER).getSendMap().get("field7"));
			// map.put("field11", fieldsMap.get("field11"));
			// map.put("batchNum", fieldsMap.get("field60").substring(2, 8));

			map.put("local_log", fieldsMap.get("field37"));
			map.put("field41", fieldsMap.get("field41"));
			map.put("merchant_id", fieldsMap.get("field42"));
			map.put("filedIMEI", fieldsMap.get("imei"));
			map.put("fieldMobile", fieldsMap.get("receivePhoneNo"));
			map.put("field11", fieldsMap.get("field11"));
			map.put("field12", fieldsMap.get("field12"));
			map.put("field13", fieldsMap.get("field13"));

			if (fieldsMap.containsKey("signImageName")) {
				String imagePath = Constant.SIGNIMAGESPATH
						+ fieldsMap.get("signImageName") + ".JPEG";
				map.put("img", StringUtil.Image2Base64(imagePath));

				// 删除签名图片
				File f = new File(imagePath);
				if (f.exists()) {
					if (f.delete()) {
						Log.e("SignImage", "文件删除成功！");
					} else {
						Log.e("SignImage", "文件删除失败！");
					}
				} else {
					Log.e("SignImage", "签名图片不存在！");
				}
			} else {
				map.put("img", "");
			}

			// 写入数据库
			UploadSignImageDBHelper helper = new UploadSignImageDBHelper();
			if (helper.insertATransfer(fieldsMap.get("field11"),
					fieldsMap.get("receivePhoneNo"), map)) {
				Log.e("sign image", "成功写入数据库");
			} else {
				Log.e("sign image", "写入数据库失败");
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				BaseActivity.getTopActivity().startService(
						new Intent(BaseActivity.getTopActivity(),UploadSignImageService.class));

				BaseActivity.getTopActivity().setResult(BaseActivity.RESULT_OK);
//				BaseActivity.getTopActivity().finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 签购单上传完成
	 */
	private void uploadReceiptDone(HashMap<String, String> fieldMap) {

		String desc = null;
		if (fieldMap.get("rtCd") != null && fieldMap.get("rtCd").equals("00")) {
			String field11 = transferMap.get(UPLOADSIGNIMAGETRANSFER).getSendMap()
					.get("field11");
			
			UploadSignImageDBHelper helper = new UploadSignImageDBHelper();
			
			// 发送短信
			String receMobile = helper.queryReceMobile(field11);
			if (SystemConfig.isSendSMS() && !"".equals(receMobile)) {
				PhoneUtil.sendSMS(receMobile, fieldMap.get("field44"));
			}
			
			// 签购单上传成功更新数据库
			helper.updateUploadFlagSuccess(field11);
			
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "签购单上传成功" : desc;
			TransferLogic.getInstance().gotoCommonSuccessActivity(desc);
		} else {
			if (fieldMap.containsKey("rtCmnt")
					&& !fieldMap.get("rtCmnt").equals(""))
				desc = fieldMap.get("rtCmnt");
			desc = (desc == null) ? "签购单上传失败" : desc;
			TransferLogic.getInstance().gotoCommonFaileActivity(desc);
//			PopupMessageUtil.showMSG_middle2(desc);
		}
	}

	/**
	 * 检查更新
	 */
	private void checkUpdateAPK(HashMap<String, String> fieldMap) {
		Intent intent = new Intent("com.bft.pos.updateAPKService");
		intent.putExtra("flag", "response");
		intent.putExtra("apkName", fieldMap.get("version_name"));
		intent.putExtra("serverVersionCode", fieldMap.get("version_number"));
		BaseActivity.getTopActivity().startService(intent);
	}

	/**
	 * 取验证码
	 */
	private void downloadSecurityCode(HashMap<String, String> fieldMap) {

		// 跳转到哪一个页面不确定。注意一定要在欲跳转的页面设置setAction("');
		Intent intent = new Intent(BaseActivity.getTopActivity().getIntent()
				.getAction());
		intent.putExtra("flag", "getSecurityCode");
		intent.putExtra("securityCode", fieldMap.get("captcha"));
		BaseActivity.getTopActivity().startActivity(intent);
		BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
	}

	public TransferModel parseConfigXML(String confName)
			throws FileNotFoundException {
		TransferModel transfer = new TransferModel();

		InputStream stream = null;

		FieldModel field = null;
		try {
//			stream = AssetsUtil.getInputStreamFromPhone(confName);
			stream = AssetsUtil.getInputStreamFromAssets(confName,2);
		} catch (IOException e) {
			throw new FileNotFoundException("加载系统文件异常(" + confName + ")");
		}

		try {
			KXmlParser parser = new KXmlParser();
			parser.setInput(stream, "utf-8");
			// 获取事件类型
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("root".equalsIgnoreCase(parser.getName())) {
						transfer.setShouldMac(parser.getAttributeValue(null,
								"shouldMac"));
						transfer.setIsJson(parser.getAttributeValue(null,
								"isJson"));
					} else if ("field".equalsIgnoreCase(parser.getName())) {
						field = new FieldModel();
						field.setKey(parser.getAttributeValue(null, "key"));
						field.setValue(parser.getAttributeValue(null, "value"));
						// field.setMacField(parser.getAttributeValue(null,
						// "macField"));
					}

					break;
				case XmlPullParser.END_TAG:
					if ("field".equalsIgnoreCase(parser.getName())) {
						transfer.addField(field);
					}
					break;
				}
				eventType = parser.next();// 进入下一个元素并触发相应事件
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != stream)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return transfer;
	}

	/**
	 * 跳转到通用的成功界面，只显示一行提示信息
	 */
	public void gotoCommonSuccessActivity(String prompt) {
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				SuccessActivity.class);
		intent.putExtra("prompt", prompt);
		BaseActivity.getTopActivity().finish();
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
	}

	/**
	 * 注册后跳转到的成功界面，只显示一行提示信息
	 */
	public void gotoCommonLoginSuccessActivity(String prompt) {
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				RegisterSuccessActivity.class);
		intent.putExtra("prompt", prompt);
		BaseActivity.getTopActivity().finish();
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
	}

	/**
	 * 跳转到通用的成功界面，只显示一行提示信息
	 */
	public void gotoCommonSuccessActivity(String prompt,
			Map<String, Object> fieldMap) {
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				SuccessActivity.class);
		intent.putExtra("prompt", prompt);
		intent.putExtra("fieldTrancode", (String) fieldMap.get("fieldTrancode"));
		BaseActivity.getTopActivity().finish();
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
	}

	/**
	 * 支付密码设置成功后跳转到通用的成功界面，只显示一行提示信息
	 */
	public void gotoCommonPayPwdSuccessActivity(String prompt) {
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				PayPwdSuccess.class);
		intent.putExtra("prompt", prompt);
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
		BaseActivity.getTopActivity().finish();
	}

	/**
	 * 跳转到通用的失败界面，只显示一行错误提示信息。
	 */
	public void gotoCommonFaileActivity(String prompt) {
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				FailActivity.class);
		intent.putExtra("prompt", prompt);
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
	}

	/**
	 * 注册失败界面，只显示一行错误提示信息。
	 */
	public void gotoLoginFaileActivity(String prompt) {
		Intent intent = new Intent(BaseActivity.getTopActivity(),
				LoginFailActivity.class);
		intent.putExtra("prompt", prompt);
		BaseActivity.getTopActivity().startActivityForResult(intent, 1);
		BaseActivity.getTopActivity().finish();
	}

	/**
	 * 读取系统配置信息
	 */
	private boolean loadSystemConfig() {
		try {
			InputStream stream = AssetsUtil
					.getInputStreamFromAssets("systemconfig.xml",4);
			KXmlParser parser = new KXmlParser();
			parser.setInput(stream, "utf-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("item".equalsIgnoreCase(parser.getName())) {
						String key = parser.getAttributeValue(null, "key");
						if (key.equals("sendSMS")) {
							SystemConfig.setSendSMS(parser.getAttributeValue(
									null, "value"));
						} else if (key.equals("pageSize")) {
							SystemConfig.setPageSize(parser.getAttributeValue(
									null, "value"));
						} else if (key.equals("historyInterval")) {
							SystemConfig.setHistoryInterval(parser
									.getAttributeValue(null, "value"));
						} else if (key.equals("maxReversalCount")) {
							SystemConfig.setMaxReversalCount(parser
									.getAttributeValue(null, "value"));
						} else if (key.equals("maxTransferTimeout")) {
							SystemConfig.setMaxTransferTimeout(parser
									.getAttributeValue(null, "value"));
						} else if (key.equals("maxLockTimeout")) {
							SystemConfig.setMaxLockTimeout(parser
									.getAttributeValue(null, "value"));
						}
					}
					break;
				}
				eventType = parser.next();// 进入下一个元素并触发相应事件
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return false;
		}
	}
}
