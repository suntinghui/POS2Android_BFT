package com.bft.pos.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import android.util.Log;

public class JSONUtil {

	/**
	 * Android类库 JSON字符串 转 Map类型
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> JSONStr2MAP(String jsonStr) {
		JSONTokener parse = new JSONTokener(jsonStr);
		JSONObject content;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			content = (JSONObject) parse.nextValue();
			@SuppressWarnings("unchecked")
			Iterator<String> keys = content.keys();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				map.put(key, content.getString(key));

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;

	}

	/**
	 * Android类库 Map类型 转 JSON字符串
	 * 
	 * @param map
	 * @return String
	 */
	public static String MAP2JSONStr(Map<String, Object> map) {
		JSONStringer JSONString = new JSONStringer();
		try {
			JSONString.object();
			for (Iterator<String> keys = map.keySet().iterator(); keys
					.hasNext();) {
				String key = (String) keys.next();
				String value = (String) map.get(key);
				JSONString.key(key).value(value);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSONString.toString() + "}";
	}

	public static void main(String[] args) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> param1 = new HashMap<String, Object>();
		param1.put("fieldtrancode", "trancode");
		param1.put("field11", "11");
		param1.put("field41", "41");
		param1.put("field42", "42");
		param1.put("field60", "60");
		param1.put("fieldMerchID", "MerchID");
		param1.put("fieldMerchPWD", "MerchPWD");
		list.add(param1);

		Map<String, Object> param2 = new HashMap<String, Object>();
		param2.put("fieldtrancode", "f:trancode");
		param2.put("field11", "f:11");
		param2.put("field41", "f:41");
		param2.put("field42", "f:42");
		param2.put("field60", "f:60");
		param2.put("fieldMerchID", "f:MerchID");
		param2.put("fieldMerchPWD", "f:MerchPWD");

		list.add(param2);

		// String list_str= JSONUtil.toJSONString(list);
		// System.out.println(list_str);
		//
		// String src = JSONUtil.maptoString(param1);
		// Map<String, Object> map2 = JSONUtil.stringtoMap(src);
		// System.out.println("src:" + src);
		// System.out.println("map:" + map2);
		//
		//
		// List<Map<String, Object>> list2=JSONUtil.toList(list_str);
		//
		// for(Map<String, Object> enty:list2)System.out.println(enty);
	}

}
