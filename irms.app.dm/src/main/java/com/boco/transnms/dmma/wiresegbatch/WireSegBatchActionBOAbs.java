package com.boco.transnms.dmma.wiresegbatch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
/**
 * 右键通用数据处理类
 * @author zhaodong
 */
public abstract class WireSegBatchActionBOAbs implements BatchActionView{
	

	protected <T extends GenericDO> List<T>  changeViewDataToWireSegs(String data, Class<?> cls,Class<T> t) {
		List<T> dataObjectList = new LinkedList<T>();
		JSONObject jsonObject = JSONObject.fromObject(data);
		if(StringUtils.equalsIgnoreCase(jsonObject.getString(_TYPE), _HEADER)){
			JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
			JSONObject valueJsonObject = JSONObject.fromObject(jsonObject.getString(_VALUE));
			Map wireSeg= getSimpleWireSegByViewDatas(jsonArray, valueJsonObject);
			JSONArray jsonDatas = JSONArray.fromObject(jsonObject.getString(_SELDATAS));
			dataObjectList = getWireSegs(wireSeg,jsonDatas,cls); 
		}else if(StringUtils.equalsIgnoreCase(jsonObject.getString(_TYPE), _CELL)){
			JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString(_DATA));
			JSONObject valueJsonObject = JSONObject.fromObject(jsonObject.getString(_VALUE));
			dataObjectList = getWireSegsByCell(jsonArray,valueJsonObject,cls,t); 
		}
		return dataObjectList;
	}

	private <T extends GenericDO>  List<T> getWireSegsByCell(JSONArray jsonArray,
			JSONObject valueJsonObject, Class<?> cls,Class<T> t) {
		 List<T> dataObjectList = new LinkedList<T>();
		 GenericDO genericDO = null;
		Object wireSegBO = null;
		String clsName = cls.getSimpleName();
		 for(Object dataObj:jsonArray){
			Map data = (Map)dataObj;
			if(data.get("CUID") != null && StringUtils.isEmpty((String)data.get("CUID"))){
				continue;
			}
			T nwireSeg = (T)CopyDbRecordValue(clsName,wireSegBO,(String)data.get("CUID"));
			if(nwireSeg == null){
				continue;
			}
			((GenericDO)nwireSeg).getAllAttr().put((String)data.get(_DATAINDEX), valueJsonObject.get((String)data.get(_DATAINDEX)));
			dataObjectList.add(nwireSeg);
		}
		return dataObjectList;
	}

	private <T extends GenericDO> List<T> getWireSegs(Map wireSeg, JSONArray jsonDatas, Class<?> cls) {
		List<T> dataObjectList = new LinkedList<T>();
		String clsName = cls.getSimpleName();
		IWireSegBO wireSegBO = null;
		for(int index=0;index<jsonDatas.size();index++){
		  Map data = (Map) jsonDatas.get(index);
		  if(data.get("CUID") != null && StringUtils.isEmpty((String)data.get("CUID"))){
				continue;
			}
		   T genericDOMap = (T) CopyDbRecordValue(clsName, wireSegBO,(String)data.get("CUID"));
		   if(genericDOMap != null){
			   ((GenericDO)genericDOMap).getAllAttr().putAll(wireSeg);
			   dataObjectList.add(genericDOMap);
		   }
		}
		return dataObjectList;
	}

	protected abstract <T extends GenericDO> T CopyDbRecordValue(String clsName, Object wireSegBO,String cuid) ;
	
	protected abstract <T extends GenericDO> Map saveData(List<T> wireSegs, Class<?> class1, Class<?> class2) ;
	
	private Map getSimpleWireSegByViewDatas(JSONArray jsonArray,
			JSONObject valueJsonObject) {
		Map wireSeg = new HashMap();
		for(Object obj:jsonArray){
			changeViewDataToWireSegAttrs(obj,valueJsonObject,wireSeg);
		}
		return wireSeg;
	}

	private Map changeViewDataToWireSegAttrs(Object obj,JSONObject value,Map wireSeg) {
		Map  data = (Map) obj;
		changeAttrValue(data.get(_COLINDEX),value,wireSeg);
		return wireSeg;
	}

	private void changeAttrValue(Object attr,  JSONObject value ,Map wireSeg) {
		Map valueMap = value;
		if(attr != null && StringUtils.isEmpty((String) attr)){
			return;
		}
		if(valueMap.get(attr) == null){
			return;
		}
		wireSeg.put(attr, valueMap.get(attr));
	}

	
}
