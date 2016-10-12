package com.boco.irms.app.utils;

import com.alibaba.fastjson.JSONObject;

public class DmDesignHelper {
	/**
	 * 
	 * @param column
	 * @param object
	 * @return String
	 */
	public static String convertJson2String(String column, Object object) {
		String value = "";
		if (object != null) {
			if (object instanceof JSONObject) {
				JSONObject json = (JSONObject) object;
				Object temp = json.get("value");
				if (temp == null) {
					temp = json.get(column);
				}
				if (temp instanceof JSONObject) {
					Object obj = ((JSONObject) temp).get(column);
					if (obj instanceof JSONObject) {
						value = String.valueOf(((JSONObject) object).getString(column));
					} else {
						value = String.valueOf(obj);
					}
				} else {
					value = String.valueOf(temp);
				}
			} else {
				value = String.valueOf(object);
			}
		}
		return value;
	}
}