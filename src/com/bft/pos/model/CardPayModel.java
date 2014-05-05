package com.bft.pos.model;

import java.io.Serializable;

public class CardPayModel implements Serializable {

	private String tradetype;// 交易类型
	private String cardtype;// 卡类型
	private String cardnum;// 卡号
	private String cardinstitution;// 发卡机构
	private String tradetotal;// 交易金额
	private String tradedata;// 交易日期
	private String tradetime;// 交易时间
	private String tradestatus;// 交易状态

	public String getTradetype() {
		return tradetype;
	}

	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}

	public String getCardtype() {
		return cardtype;
	}

	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getCardinstitution() {
		return cardinstitution;
	}

	public void setCardinstitution(String cardinstitution) {
		this.cardinstitution = cardinstitution;
	}

	public String getTradetotal() {
		return tradetotal;
	}

	public void setTradetotal(String tradetotal) {
		this.tradetotal = tradetotal;
	}

	public String getTradedata() {
		return tradedata;
	}

	public void setTradedata(String tradedata) {
		this.tradedata = tradedata;
	}

	public String getTradetime() {
		return tradetime;
	}

	public void setTradetime(String tradetime) {
		this.tradetime = tradetime;
	}

	public String getTradestatus() {
		return tradestatus;
	}

	public void setTradestatus(String tradestatus) {
		this.tradestatus = tradestatus;
	}

}
