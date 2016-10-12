package com.boco.irms.app.dm.gridbo.opticalCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.dm.gridbo.AbstractPropTemplateBO;
import com.boco.transnms.common.dto.Optical;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.OpticalWayToPort;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IOpticalCheckBO;

public class OpticalPropTemplateBO extends AbstractPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String[] attrLabel;
	
	@Override
	public EditorPanelMeta getEditorData(EditorPanelMeta editorMeta) {
		String method = editorMeta.getRemoteMethod("query");
		String className=editorMeta.getClassName();
		editorColumnMetaMap.clear();
		for(EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()){
			editorColumnMetaMap.put(colMeta.getCuid(), colMeta);
		}
	
		List<Map> results = JSON.parseArray(editorMeta.getParas(),Map.class);
		Map<String,String> fiber = (Map<String,String>)results.get(0).get("RELATED_FIBER_CUID");
		String cuid = fiber.get("CUID");
		String failReason = (String)results.get(0).get("FAIL_REASON");
		if(cuid != null){
			try {
				IOpticalCheckBO checkBo = (IOpticalCheckBO)BoHomeFactory.getInstance().getBO(IOpticalCheckBO.class);
				GenericDO gdo= checkBo.getObjectByCuid(cuid);
				List<Map> list = new ArrayList<Map>();
				
				if(gdo != null){
					gdo.setClassName(className);				
					gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
					gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
					gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
					this.addFailReasonToGdo(gdo, failReason);
					if("OPTICAL".equals(className)){//光纤
						attrLabel=new String[]{							
								Optical.AttrName.destPointCuid,
								Optical.AttrName.origSiteCuid,
								Optical.AttrName.destSiteCuid,
								Optical.AttrName.origEqpCuid,
								Optical.AttrName.destEqpCuid,
								Optical.AttrName.origRoomCuid,
								Optical.AttrName.destRoomCuid,
								Optical.AttrName.origPointCuid
							};
					}else{//光路
						attrLabel=new String[]{
								OpticalWay.AttrName.nePortCuidA,
								OpticalWay.AttrName.nePortCuidZ,
								OpticalWay.AttrName.siteCuidA,
								OpticalWay.AttrName.siteCuidZ,
//	                			"ROOM_CUID_A",
//	                			"ROOM_CUID_Z",
	                			OpticalWay.AttrName.devInfoA,
	                			OpticalWay.AttrName.devInfoZ
							};
						String sql=OpticalWayToPort.AttrName.opticalWayCuid+"='"+gdo.getCuid()+"'";
                    	DataObjectList portList=checkBo.getObjectBySql(sql,new OpticalWayToPort(),GenericDO.ObjectLoadType.FULL);
                    	if(portList!=null&&portList.size()==2){
                    		gdo.setAttrValue(OpticalWay.AttrName.nePortCuidA, portList.get(0).getAttrString(OpticalWayToPort.AttrName.portCuid));
                    		gdo.setAttrValue(OpticalWay.AttrName.nePortCuidZ, portList.get(1).getAttrString(OpticalWayToPort.AttrName.portCuid));
                    	}
                    	if(gdo.getAttrString(OpticalWay.AttrName.siteCuidA).contains("ROOM")){
                    		gdo.setAttrValue("ROOM_CUID_A", gdo.getAttrString(OpticalWay.AttrName.siteCuidA));
                    		String siteCuid=((Room) checkBo.getObjectByCuid(gdo.getAttrString(OpticalWay.AttrName.siteCuidA))).getRelatedSiteCuid();
                    		gdo.setAttrValue(OpticalWay.AttrName.siteCuidA,siteCuid);
                    	}
                    	if(gdo.getAttrString(OpticalWay.AttrName.siteCuidZ).contains("ROOM")){
                    		gdo.setAttrValue("ROOM_CUID_Z", gdo.getAttrString(OpticalWay.AttrName.siteCuidZ));
                    		String siteCuid=((Room) checkBo.getObjectByCuid(gdo.getAttrString(OpticalWay.AttrName.siteCuidZ))).getRelatedSiteCuid();
                    		gdo.setAttrValue(OpticalWay.AttrName.siteCuidZ,siteCuid);
                    	}
					}
					gdo=checkBo.getDboCuidObj(gdo, attrLabel);
					Map map = new HashMap();
					for(Object columnName : gdo.getAllAttr().keySet()){
						String colName = columnName.toString();
						Object value = gdo.getAttrValue(colName);
						map.put(colName, convertObject(colName,value));
					}
					list.add(map);
				}		
				PageResult result = new PageResult(list, 1, 1, 1);
				editorMeta.setResult(JSON.toJSONString(result.getElements()));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}        
		}
		return editorMeta;
	}

	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)
			throws UserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta)
			throws UserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta)
			throws UserException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private GenericDO addFailReasonToGdo(GenericDO gdo, String failReason) {
		gdo.setAttrValue("BRANCH_ROUTE", "");
		gdo.setAttrValue("BRANCH_ROUTE_FAILPOINT", "");
		gdo.setAttrValue("BRANCH_ROUTE_FAILREASON", "");
		for (String str : failReason.split("seperate")) {
			if (str.contains("分支路由:")) {
				gdo.setAttrValue("BRANCH_ROUTE", str.replace("分支路由:", ""));
			} else if (str.contains("分支路由失败设备点:")) {
				gdo.setAttrValue("BRANCH_ROUTE_FAILPOINT",
						str.replace("分支路由失败设备点:", ""));
			} else if (str.contains("分支路由失败原因:")) {
				gdo.setAttrValue("BRANCH_ROUTE_FAILREASON",
						str.replace("分支路由失败原因:", ""));
			} else {
				gdo.setAttrValue("BRANCH_ROUTE_FAILREASON", failReason);
			}
		}
		return gdo;
	}
	
}
