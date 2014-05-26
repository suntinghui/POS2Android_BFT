package com.bft.pos.model;

import java.io.Serializable;

public class CardPayModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String transtype;// 交易类型
	private String type;// 卡类型
	private String pan;// 卡号
	private String instflag;// 发卡机构
	private String amttrans;// 交易金额
	private String date;// 交易日期
	private String state;// 交易状态

	public String getTranstype() {
		return transtype;
	}

	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getInstflag() {
		return instflag;
	}

	public void setInstflag(String instflag) {
		this.instflag = instflag;
	}

	public String getAmttrans() {
		return amttrans;
	}

	public void setAmttrans(String amttrans) {
		this.amttrans = amttrans;
	}


	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
