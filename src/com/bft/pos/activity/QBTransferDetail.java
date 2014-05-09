package com.bft.pos.activity;
/**
 * @author Yaozeyu
 * function：账户交易查询明细
 * */
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.model.TransferDetailModel1;

public class QBTransferDetail extends BaseActivity implements OnClickListener {
//	定义要使用的组件和数据
	private Button backButton = null;
	private Button okButton = null;
	private TransferDetailModel1 model = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
// 		依旧添加侧滑界面
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu, R.layout.qbtransferdetail);
		super.onCreate( savedInstanceState);
		this.findViewById(R.id.topInfoView);
		this.initTitlebar("账户交易流水明细");
//		为按钮添加
		backButton = (Button) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(this);
//		获取实体类的数据
		model = (TransferDetailModel1) getIntent().getSerializableExtra("model");
		
//		@SuppressWarnings("unchecked")
//		HashMap<String, String> map = (HashMap<String, String>) this.getIntent().getSerializableExtra("map");
//		if (null == map || map.size() == 0){
//			TransferLogic.getInstance().gotoCommonFaileActivity("交易明细查询出错，请重试");
//			return;
//		}
		
		try{
//			((TextView)this.findViewById(R.id.qhTransferSerial)).setText(map.get("tranSerial"));
//			((TextView)this.findViewById(R.id.qhAccountNo)).setText(StringUtil.formatAccountNo(map.get("cardNo")));
//			((TextView)this.findViewById(R.id.qhBank)).setText(map.get("issueBank"));
//			((TextView)this.findViewById(R.id.qhBatchNo)).setText(map.get("batchNo"));
//			((TextView)this.findViewById(R.id.qhaimCardNo)).setText(map.get("aimCardNo"));
//			((TextView)this.findViewById(R.id.qhReferNo)).setText(map.get("hostSerial"));
//			((TextView)this.findViewById(R.id.qhSettleFlag)).setText(map.get("settleFlag").equals("0") ? "未清算" : "已清算");
			
//			这是将实体类里存入的数据显示在界面上
			((TextView)this.findViewById(R.id.qhTransType)).setText(model.getTradeTypeKey());
			((TextView)this.findViewById(R.id.qhTransTime)).setText(model.getTradeDate());
			((TextView)this.findViewById(R.id.qhAmount)).setText(model.getPayMoney());
			((TextView)this.findViewById(R.id.qhSettleDate)).setText(model.getPayDate());
			((TextView)this.findViewById(R.id.qhTranFlag)).setText(model.getOrderStatus());
			
			// 只有付款交易（以后可能会扩充）才会有转入卡号。
//			TextView aimCardNoView = (TextView) this.findViewById(R.id.qhaimCardNo);
//			if (map.get("tranCode").equals("200001111")){
//				aimCardNoView.setVisibility(View.VISIBLE);
//			} else {
//				aimCardNoView.setVisibility(View.GONE);
//			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
//为按钮添加点击响应
	@Override
	public void onClick(View view) {
		switch(view.getId()){
//		返回按钮，结束当前页面
		case R.id.backButton:
			QBTransferDetail.this.finish();
			break;
		case R.id.okButton:
			this.finish();
			break;
		}
	}
//	这个是要显示的状态，这里之前是按照传回的数据进行选择从而决定显示什么
	private String getTranFlag(String flag) {
		String str = "未知";
		try {
			int tranFlag = Integer.parseInt(flag);
			switch (tranFlag) {
			case 0:
				str = "正常";
				break;

			case 1:
				str = "被撤销";
				break;

			case 5:
				str = "失败";
				break;

			default:
				str = "未知";
				break;
			}
		} catch (Exception e) {
		}
		
		return str;
	}

}
