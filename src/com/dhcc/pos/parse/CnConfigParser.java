package com.dhcc.pos.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dhcc.pos.packets.CnFormat;
import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;
import com.dhcc.pos.packets.CnType;

/**
 * 配置文件解析器
 * 
 * @author Maple Leaves
 * 
 */
public class CnConfigParser {

	static CnMessageFactory mfact = null;

	/**
	 * 通过xml文件创建消息工厂，
	 * 
	 * @param filepath
	 *            xml 文件完整路径
	 * @return
	 * @throws Exception
	 */
	public static CnMessageFactory createFromXMLConfigFile(InputStream stream) throws Exception {
		/**实例化消息工厂*/
		mfact = CnMessageFactory.getInstance();
		try {
			if (stream != null) {
					// 解析
				parse(mfact, stream);
			}
		} catch (FileNotFoundException e) {
			System.out.println("文件找不到、解析配置文件发生IO异常:" + e);
//			log.error("文件找不到、解析配置文件发生IO异常",e);
			// throws new FileNotFoundException("找不到文件");
		}finally {
			try {
				stream.close();
			} catch (IOException e) {
				System.out.println("关闭流发生IO异常:"+e);
			}
		}
		return mfact;
	}

	/**
	 * 解析xml文件并初始化相关配置信息
	 * 
	 * @param mfact
	 * @param stream
	 * @throws IOException
	 */
	protected static void parse(CnMessageFactory mfact, InputStream stream) throws IOException {
		final DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();

		/**
		 * 变量
		 * */
		DocumentBuilder docb = null;
		Document doc = null;
		NodeList nodes = null;
		Element root, elem = null;
		CnMessage m = null;
		//字段默认填充类型（当没有设定类型时默认填充为ascii）
		String defaultValue = "ASCII";
		//没有长度的字段 默认为：0
		int length = 0;
		///没有必输的字段 默认为：false
		boolean must = false;
		//没有添加长度的字段 默认为：false ； 该添加长度是组装报文时 是否有域值长度
		boolean addLen = false;
		//没有对齐字段的值 默认为：null ；此为BCD压缩时左对齐（右补零）还是右对齐（左补零）
		String align = null;

		try {
			docb = docfact.newDocumentBuilder();
			doc = docb.parse(stream);
		} catch (ParserConfigurationException ex) {
			System.out.println("Cannot parse XML configuration:"+ ex);
			return;
		} catch (SAXException ex) {
			System.out.println("Parsing XML configuration:"+ex);
			return;
		}
		root = doc.getDocumentElement();
		nodes = root.getElementsByTagName("header");
		System.out.println("############\t【header 】\t############");

		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			int headerLength = Integer.parseInt(elem.getAttribute("headerLength"));
			int tpduLength = Integer.parseInt(elem.getAttribute("tpduLength"));

			if (elem.getChildNodes() == null
					|| elem.getChildNodes().getLength() == 0) {
				throw new IOException("Invalid header element");
			}

			String msgtypeid = elem.getChildNodes().item(0).getNodeValue();

			if (msgtypeid.length() != 4) {
				throw new IOException("Invalid msgtypeid for header: "
						+ elem.getAttribute("msgtypeid"));
			}
			/**设置tpdu 报文头*/
			mfact.setTPDUlengthAttr(msgtypeid, tpduLength);
			mfact.setHeaderLengthAttr(msgtypeid, headerLength);

		}

		/* Read the message templates*/
		nodes = root.getElementsByTagName("template");

		System.out.println("############\t【template:new cnMessage()】\t############");

		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			String msgtypeid = elem.getAttribute("msgtypeid");
			if (msgtypeid.length() != 4) {
				throw new IOException("Invalid type for template: " + msgtypeid);
			}
			NodeList fields = elem.getElementsByTagName("field");

			/**实例化消息类*/
			m = CnMessage.getInstance();
			/**为消息类赋消息类型（交易码）*/
			m.setMsgTypeID(msgtypeid);

			for (int j = 0; j < fields.getLength(); j++) {
				length = 0;
				//没有必输的字段 默认为：false
				must = false;
				//没有添加长度的字段 默认为：false ； 该添加长度是组装报文时 是否有域值长度
				addLen = false;
				//没有对齐字段的值 默认为：null ；此为BCD压缩时左对齐（右补零）还是右对齐（左补零）
				align = null;

				Element f = (Element) fields.item(j);
				int fieldid = Integer.parseInt(f.getAttribute("id"));
				CnFormat format = CnFormat.valueOf(f.getAttribute("format"));

				CnType cnType = CnType.valueOf(f.getAttribute("type"));

				if (f.getAttribute("length").length() > 0) {
					length = Integer.parseInt(f.getAttribute("length"));
				}

				if (f.getAttribute("must") != null && (f.getAttribute("must").equalsIgnoreCase("true"))) {
					must = Boolean.parseBoolean(f.getAttribute("must"));
				}

				if(f.getAttribute("addLen") != null && f.getAttribute("addLen").equalsIgnoreCase("true")){
					addLen = Boolean.parseBoolean(f.getAttribute("addLen"));
				}

				if(f.getAttribute("align") != null && !f.getAttribute("align").equals("")){
					align = f.getAttribute("align");
				}

				String init_filed_data = f.getChildNodes().item(0) == null ? null
						: f.getChildNodes().item(0).getNodeValue();
				m.setValue(fieldid, format, cnType, init_filed_data, length, must, addLen, align);
			}
			mfact.addMessageTemplate(m);
		}

		// Read the parsing guides
		nodes = root.getElementsByTagName("parseinfo");
		System.out.println("############\t 【parseinfo: map】\t############");
		for (int i = 0; i < nodes.getLength(); i++) {
			elem = (Element) nodes.item(i);
			String msgtypeid = elem.getAttribute("msgtypeid");

			if (msgtypeid.length() != 4) {
				throw new IOException("Invalid type for parse guide: "
						+ msgtypeid);
			}
			NodeList fields = elem.getElementsByTagName("field");

			/**
			 * parseMap：存放 域和域的所有信息（以fieldid作为key cnFieldParseInfo内值为value）
			 *例： new cnFieldParseInfo(format, cnType, length, must, addLen, align) 给解析的字段赋值
			 * 
			 * */
			Map<Integer, CnFieldParseInfo> parseMap = new TreeMap<Integer, CnFieldParseInfo>();

			for (int j = 0; j < fields.getLength(); j++) {
				length = 0;
				//没有必输的字段 默认为：false
				must = false;
				//没有添加长度的字段 默认为：false ； 该添加长度是组装报文时 是否有域值长度
				addLen = false;
				//没有对齐字段的值 默认为：null ；此为BCD压缩时左对齐（右补零）还是右对齐（左补零）
				align = null;
				Element f = (Element) fields.item(j);
				int fieldid = Integer.parseInt(f.getAttribute("id"));

				CnFormat format = CnFormat.valueOf(f.getAttribute("format"));
				CnType cnType = !f.getAttribute("type").equals("")?CnType.valueOf(f.getAttribute("type")):CnType.valueOf(defaultValue);
				if (f.getAttribute("length").length() > 0) {
					length = Integer.parseInt(f.getAttribute("length"));
				}

				if (f.getAttribute("must") != null && (f.getAttribute("must").equalsIgnoreCase("true"))) {
					must = Boolean.parseBoolean(f.getAttribute("must"));
				}

				if(f.getAttribute("addLen") != null && f.getAttribute("addLen").equalsIgnoreCase("true")){
					addLen = Boolean.parseBoolean(f.getAttribute("addLen"));
				}

				if(f.getAttribute("align") != null && !f.getAttribute("align").equals("")){
					align = f.getAttribute("align");
				}
				/**
				 * new cnFieldParseInfo(format, cnType, length, must, addLen) 给解析的字段赋值
				 * 然后以fieldid作为key 把上面内容放入parseMap中
				 * */
				parseMap.put(fieldid, new CnFieldParseInfo(format, cnType, length, must, addLen, align));

				// System.out.println(String.format("fieldid: %s, format: %s,length: %d",fieldid,
				// format.name(),length));
			}
			/**
			 * 以msgtypeid作为key 将parseMap放入（CnMessageFactory）的ParseMap之中
			 * 
			 * */
			mfact.setParseMap(msgtypeid, parseMap);
		}
	}
}
