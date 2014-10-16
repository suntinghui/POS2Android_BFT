package com.bft.pos.model;
/**
 * 
 * 这个类里存放的是有关账户交易查询的有关信息
 * 目前此类和优乐通的内容一致
 * 但是本项目中的报文和优乐通的不同，所以进行较为大的改动
 * 将几乎所有的内容全部改动，替换为我们现在需要的内容
 * */
import java.io.Serializable;

public class TransferDetailModel1 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tradeDate = null;
	private String payMoney = null;
	private String tradeTypeKey = null;
	private String payDate = null;
	private String orderStatus = null ;
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	public String getTradeTypeKey() {
		return tradeTypeKey;
	}
	public void setTradeTypeKey(String tradeTypeKey) {
		this.tradeTypeKey = tradeTypeKey;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	
}
