package com.dhcc.pos.packets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.dhcc.pos.parse.CnFieldParseInfo;

/**
 * 消息工厂
 */
public class CnMessageFactory {

	private static CnMessageFactory instance = null;
	private CnMessage m = null;
	
	private CnMessageFactory() {
		try {
			// ConfigParser.createFromXMLConfigFile(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized CnMessageFactory getInstance() {
		if(instance == null){
			instance = new CnMessageFactory();
		}
		return instance;
	}

	/**typeTemplates在createFromXMLConfigFile时赋值
	 * 用来存放各种类型的消息 ,格式：( msgtypeid,cnMessage)
	 *  msgtypeid 消息类型
	 *  cnMessage 消息
	 */
	private Map<String, CnMessage> typeTemplates = new HashMap<String, CnMessage>();
	
	/**
	 * 存放用来解析的字段，格式:((msgtypeid, (fieldID, fieldInfo))
	 * <blockquote>
	 * </blockquote>
	 *   msgtypeid 消息类型
	 *  
	 *  fieldInfo 字段信息
	 */
	private Map<String, Map<Integer, CnFieldParseInfo>> parseMap = new TreeMap<String, Map<Integer, CnFieldParseInfo>>();
	
	/**
	 * 字段出现的消息中的顺序，格式 ：( msgtypeid, List<Integer>)
	 * 	 msgtypeid 消息类型
	 * 	 fieldID   字段id
	 */
	private Map<String, List<Integer>> parseOrder = new HashMap<String, List<Integer>>();

	
	/**
	 * 报头包含的消息类型,格式 (msgtypeid, headerlength)
	 *  msgtypeid 消息类型
	 *  headerlength 报头长度
	 */
	private Map<String, Integer> msgHeadersAttr = new HashMap<String, Integer>();
	
	/**
	 * 报头包含的消息类型,格式 (msgtypeid, TPDUlength)
	 *  msgtypeid 消息类型
	 *  TPDUlength TPDU头长度
	 */
	private Map<String, Integer> msgTPDUlengthAttr = new HashMap<String, Integer>();
	
	/**
	 * 是否使用当前日期，它出现在 fieldID为7的字段域中
	 */
	private boolean usecurrentdata;
	
	
	/**
	 * 二进消息标识符，用来表示创建或解析时，使用的是二进制消息 
	 * true  使用的是二进制消息 
	 *  false，用来表示创建或解析时，使用的是ASCII码
	 *  默认为 false，
	 */
	private boolean useBinary;
	/**
	 * 消息结束符，表示消息是否已完成，默认-1表示没结束
	 */
	private int etx = -1;


	/**
	 * 设置二进消息标识符，
	 * @param flag ：true  使用的是二进制消息 
	 */
	public void setUseBinary(boolean flag) {
		useBinary = flag;
	}
	/**
	 * 获取二进消息标识符，
	 * @return
	 */
	public boolean getUseBinary() {
		return useBinary;
	}

	/** 
	 * @param value The ASCII value of the ETX character or -1
	 *  to indicate no terminator should be used. */
	
	public void setEtx(int value) {
		etx = value;
	}

	
	/**
	 * 通过模板中指定的消息类型id创建报文（消息）
	 * 并根据该模板中已有的域给位图赋值
	 * 设定了：
	 * etx:		报文组装完成设置结束符
	 * Binary:	如果设置为true, 报文中的各报文域按照二进制组成报文
	 * Fields:	bit map 位图 设置字段域，由于字段域1被 用来存放位图，设置字段域应从2开始
	 * field7:	当前日期
	 * field11:	系统跟踪号
	 */
	public CnMessage newMessagefromTemplate(String msgtypeid) {
		System.out.println("\t####################【NewMessagefromTemplate】####################" + "\r");
		m = new CnMessage(msgtypeid,msgTPDUlengthAttr.get(msgtypeid), msgHeadersAttr.get(msgtypeid));
		CnMessage templ = null;
		//是否使用二进制
		m.setBinary(useBinary);

		/**
		 * Copy the values from the template (通过报文模板来赋初值)
		 * 当创建消息工厂时如果消息配置文件未配置template 此template为null （此template多为测试用）
		 * */
		
//		if(typeTemplates.get(msgtypeid)==null)
//			throw new IllegalArgumentException("无效的消息类型码：" + msgtypeid);
		templ = typeTemplates.get(msgtypeid);

		if (templ != null) {
			for (int i = 2; i < 128; i++) {
				if (templ.hasField(i)) {
					/*给bit map位图赋值*/
					m.setField(i, templ.getField(i).clone());
				}
			}
		}
		return m;
	}



	public void setUseCurrentDate(boolean flag) {
		usecurrentdata = flag;
	}

	public boolean getUseCurrentDate() {
		return usecurrentdata;
	}

	public void setHeaders(Map<String, Integer> value) {
		msgHeadersAttr.clear();
		msgHeadersAttr.putAll(value);
	}
	public void setTPDU(Map<String, Integer> value) {
		msgTPDUlengthAttr.clear();
		msgTPDUlengthAttr.putAll(value);
	}
	
	public void setTPDUlengthAttr(String msgtypeid, Integer TPDUlength) {
		msgTPDUlengthAttr.put(msgtypeid, TPDUlength);
	}


	public Integer getTPDUlengthAttr(String msgtypeid) {
		return msgTPDUlengthAttr.get(msgtypeid);
	}

	public void setHeaderLengthAttr(String msgtypeid, Integer headerlen) {
			msgHeadersAttr.put(msgtypeid, headerlen);
	}

	public Integer getHeaderLengthAttr(String msgtypeid) {
		return msgHeadersAttr.get(msgtypeid);
	}


	public void addMessageTemplate(CnMessage templ) {
		if (templ != null) {
			typeTemplates.put(templ.getMsgTypeID(), templ);
		}
	}

	public void removeMessageTemplate(String msgtypeid) {
		typeTemplates.remove(msgtypeid);
	}

	public void setMessageTemplate(String msgtypeid, CnMessage templ) {
		if (templ == null) {
			typeTemplates.remove(msgtypeid);
		} else {
			typeTemplates.put(msgtypeid, templ);
		}
	}

	public void setParseMap(String msgtypeid, Map<Integer, CnFieldParseInfo> map) {
		parseMap.put(msgtypeid, map);
		ArrayList<Integer> index = new ArrayList<Integer>();
		index.addAll(map.keySet());
	
		//升序排序
		Collections.sort(index);
		
		System.out.println("Adding parse map for type: [" + msgtypeid + "] with fields " + index);
		
		//升序排序的域赋给parseOrder
		parseOrder.put(msgtypeid, index);
	}

	public  Map<Integer, CnFieldParseInfo> getParseMap(String msgtypeid) {
		return parseMap.get(msgtypeid);
	}
	
	public CnMessage getCnMessage() {
		return m;
	}
	public void setCnMessage(CnMessage m) {
		this.m = m;
	}
	public  List<Integer> getParseOrder(String msgtypeid) {
		return parseOrder.get(msgtypeid);
	}
}