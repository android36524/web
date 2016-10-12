package com.boco.transnms.dmma.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class DmmaDataTransUtils {
	public static List<Map> getElementsFromDataObjLst(DataObjectList data ,String jsonStr){
		JSONArray  jSONArray  = JSONArray.fromObject(jsonStr);
		List<Map>  elements = new  ArrayList<Map>();
		for(GenericDO  genericDO:data){
			Map element = new HashMap();
			for(Object jsonObject :jSONArray){
				Map dataObject = (Map) jsonObject;
				element.put(dataObject.get("name"),genericDO.getAttrValue((String)dataObject.get("name")));
			}
			elements.add(element);
		}
		return elements;
	}
	public static List<Map> getElementsFromDataObjLst(DataObjectList data ,String jsonStr,DmmaEnumChangeIntf taskEnumChangeIntf){
		JSONArray  jSONArray  = JSONArray.fromObject(jsonStr);
		List<Map>  elements = new  ArrayList<Map>();
		for(GenericDO  genericDO:data){
			Map element = new HashMap();
			for(Object jsonObject :jSONArray){
				Map dataObject = (Map) jsonObject;
				element.put(dataObject.get("name"),genericDO.getAttrValue((String)dataObject.get("name")));
				taskEnumChangeIntf.changEnumValue(dataObject.get("name"), genericDO.getAttrValue((String)dataObject.get("name")),element);
			}
			elements.add(element);
		}
		return elements;
	}
}
