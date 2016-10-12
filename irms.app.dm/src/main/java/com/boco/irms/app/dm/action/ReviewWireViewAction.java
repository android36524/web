package com.boco.irms.app.dm.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;


public class ReviewWireViewAction {
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	public List<Map<String,String>> getRemainInfo(String segCuid){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        try {
        	DataObjectList wireToDuctlineList=getDuctManagerBO().getLayWireInfo(new BoActionContext(), segCuid);
            if(wireToDuctlineList.size() == 0){
            	return list;
            }
			for (int i = 0; i < wireToDuctlineList.size(); i++) {
				GenericDO point = wireToDuctlineList.get(i);
				Map<String,String> map = new HashMap<String,String>();
				map.put("CUID", point.getCuid());
				map.put("LINE_SYSTEM_CUID", point.getAttrString("LINE_SYSTEM_CUID"));
				map.put("LINE_SYSTEM_NAME",point.getAttrString("LINE_SYSTEM_NAME"));
				Object branchCuid = point.getAttrValue("LINE_BRANCH_CUID");
				if(branchCuid != null){
					map.put("LINE_BRANCH_CUID", String.valueOf(branchCuid));
					Object branchName = point.getAttrValue("LINE_BRANCH_NAME");
					if(branchName != null){
						map.put("LINE_BRANCH_NAME",String.valueOf(branchName));
					}
				}
				Object ductHoleNo = point.getAttrValue("DUCT_HOLE_NO");
				if(ductHoleNo != null){
					map.put("DUCT_HOLE_NO", String.valueOf(ductHoleNo));
				}
				Object childHoldNum = point.getAttrValue("DUCT_CHILD_HOLD_NUM");
				if(childHoldNum != null){
					map.put("DUCT_CHILD_HOLD_NUM", String.valueOf(childHoldNum));
				}
				map.put("LINE_SEG_CUID", point.getAttrString("LINE_SEG_CUID"));
				map.put("LINE_SEG_NAME", point.getAttrString("LINE_SEG_NAME"));
				map.put("WIRE_SEG_DEST_POINT",point.getAttrString("WIRE_SEG_DEST_POINT"));
				map.put("WIRE_SEG_ORIG_POINT",point.getAttrString("WIRE_SEG_ORIG_POINT"));
				map.put("WIRE_SYSTEM_NAME",point.getAttrString("WIRE_SYSTEM_NAME"));
				map.put("DIS_POINT_NAME", point.getAttrString("DIS_POINT_NAME"));
				map.put("END_POINT_NAME", point.getAttrString("END_POINT_NAME"));
				map.put("WIRE_SEG_NAME", point.getAttrString("WIRE_SEG_NAME"));
				map.put("WIRE_SYSTEM_LEVEL",point.getAttrString("WIRE_SYSTEM_LEVEL"));
				Object innerNo = point.getAttrValue("INNER_NO");
				if(innerNo != null){
					map.put("INNER_NO", String.valueOf(innerNo));
				}
				Object fiberCount = point.getAttrValue("FIBER_COUNT");
				if(fiberCount != null){
					map.put("FIBER_COUNT", String.valueOf(fiberCount));
				}
				map.put("LAY", point.getAttrString("LAY"));

				list.add(map);
			}
       }catch (Exception ex) {
    	   Map<String,String> errorMap = new HashMap<String,String>();
    	   errorMap.put("error", "查询经过的光缆出错!");
           LogHome.getLog().error("查询信息报错", ex);
       }
       return list;
	}
	
	public List<Map> getWireRoute(String segCuid){
		String relatedSystemCuid = segCuid;
		if(segCuid.startsWith(WireSeg.CLASS_NAME)){
			GenericDO objByCuid = getDuctManagerBO().getObjByCuid(new BoActionContext(), segCuid);
			if(relatedSystemCuid != null){
				relatedSystemCuid = String.valueOf(objByCuid.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
			}
		}
		List<Map> list = new ArrayList<Map>();
		try {
			DataObjectList wireToDuctlineList = BoHomeFactory.getInstance().getBO(IWireSegBO.class).getWireRouteByWireSegCuid(new BoActionContext(),relatedSystemCuid);

			if (wireToDuctlineList.size() == 0) {
				return list;
			}
			for (int i = 0; i < wireToDuctlineList.size(); i++) {
				GenericDO point = wireToDuctlineList.get(i);
				
				String attrValue = point.getAttrString("WIRE_SEG_LUYOU");
				
				if (attrValue != null) {
					if (attrValue.indexOf("管道") > 0){
						if (attrValue.indexOf("引上") > 0){
								point.setAttrValue("LAY_TYPE", "混合");
						}else if (attrValue.indexOf("杆路") >0){
							point.setAttrValue("LAY_TYPE", "混合");
						}else {
							point.setAttrValue("LAY_TYPE", "管道");
						}
					}
					
					else if(attrValue.indexOf("引上")>0){
						if (attrValue.indexOf("杆路") >0){
							point.setAttrValue("LAY_TYPE", "混合");
						}else {
							point.setAttrValue("LAY_TYPE", "引上");
						}
					}
					else if (attrValue.indexOf("杆路") >0){
						point.setAttrValue("LAY_TYPE", "杆路");
					}else {
						point.setAttrValue("LAY_TYPE", "未知");
					}
				}else{
					point.setAttrValue("LAY_TYPE", "");
				}
				
//				Object attrValue = point.getAttrValue("LAY_TYPE");

//				if (attrValue != null) {
//					String laytype = String.valueOf(attrValue);
//					if ("0".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "未知");
//					}
//					if ("1".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "管道");
//					}
//					if ("2".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "架空");
//					}
//					if ("3".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "直埋");
//					}
//					if ("4".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "混合");
//					}
//					if ("5".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "海缆");
//					}
//					if ("6".equals(laytype)) {
//						point.setAttrValue("LAY_TYPE", "特种");
//					}
//				}

				Map<String,String> map = new HashMap<String,String>();
				map.put("CUID", point.getCuid());
				map.put("LABEL_CN", point.getAttrString("LABEL_CN"));
				map.put("WIRE_SEG_LUYOU", point.getAttrString("WIRE_SEG_LUYOU"));
				map.put("LAY_TYPE", point.getAttrString("LAY_TYPE"));

				list.add(map);
			}
		} catch (Exception ex) {
            LogHome.getLog().error("查询信息报错", ex);
        }
		return list;
	}
	
	public List<Map> getInterWires(String accessPointCuid)
	{
		List<Map> list = new ArrayList<Map>();
		if(StringUtils.isNotBlank(accessPointCuid))
		{
			String sql = " RELATED_SITE_CUID='" + accessPointCuid + "' ";
			BoQueryContext querycon = new BoQueryContext(0, 10000,false);
			DboCollection  results = new DboCollection();
			try {
				results = (DboCollection)BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getInterwireBySql", querycon, sql);
			} catch (Exception e) {
			}
			if(results != null && results.size()>0){
				for(int i=0;i<results.size();i++){
					GenericDO gdo = results.getAttrField(InterWire.CLASS_NAME, i);
					Map map = new HashMap();
					map.put("CUID", gdo.getCuid());
					map.put("NAME", gdo.getAttrString("LABEL_CN"));
					Object value = gdo.getAttrValue("ORIG_POINT_CUID");
					if(value instanceof GenericDO)
					{
						map.put("ORIG_POINT_CUID", ((GenericDO) value).getCuid());
						map.put("ORIG_POINT_NAME", ((GenericDO) value).getAttrString("LABEL_CN"));
					}
					value = gdo.getAttrValue("DEST_POINT_CUID");
					if(value instanceof GenericDO)
					{
						map.put("DEST_POINT_CUID", ((GenericDO) value).getCuid());
						map.put("DEST_POINT_NAME", ((GenericDO) value).getAttrString("LABEL_CN"));
					}
					list.add(map);
				}
			}
		}
		return list;
	}
}
