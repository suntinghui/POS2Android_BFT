package com.dhcc.pos.core;

import java.util.Map;

import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;

public class TxActionImp {

	CnMessageFactory mfact;

	CnMessage m;

	private String clientTransferCode = "";

	String TPDU = "6000050000";// 6000050000 6000140000
	String msgHeader = "603110000000";

	public byte[] first(Map<String, Object> req_map) {

		// 请求报文中取得交易码
//		clientTransferCode = (String) req_map.get("fieldTrancode");
//
//		if (clientTransferCode == null)
//			throw new IllegalArgumentException("请求报文未有消息类型(交易码)");
//		if (clientTransferCode.length() < 4)
//			throw new IllegalArgumentException("请求报文异常交易码:" + clientTransferCode);

//		String msgType = clientTransferCode.substring(0, 4);
		String msgType = (String) req_map.get("fieldTransType");
		
		req_map.put("msgType", msgType);

		return SocketTransport.getInstance().process(req_map);
	}

	public void setMfact(CnMessageFactory mfact) {
		this.mfact = mfact;
	}

}
