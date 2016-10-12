package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;

public class SystemPropTemplateBO extends AbstractPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	private IWireSystemBO getWireSystemBO(){
		return BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
	}
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta)
			throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::insert:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();		
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			dbo.setCuid();
//			//根据光缆级别（主干或末端）判断光交接箱，站点，分纤箱分纤点级别为主干末端 7：主干接入 8：末端接入
//			if(StringUtils.isNotBlank(newSysLevel)){
//				if(newSysLevel.equals("7")||newSysLevel.equals("8")){					
//					wireSysCheck(dbo,newSysLevel);
//				}
//			}			
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				BoCmdFactory.getInstance().execBoCmd(method,actionContext,dbo);		
			} catch (Exception e) {
				logger.error("添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	

	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta)
			throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::update:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			if(StringUtils.equals(dbo.getBmClassId(), "SYSTEM_LEVEL")){
				String newSysLevel = dbo.getAttrValue("SYSTEM_LEVEL").toString();
				String cuid =dbo.getCuid();
				WireSystem oldWireSystem = getWireSystemBO().getWireSystemByCuid(new BoActionContext(), cuid);
				String oldSysLevel = oldWireSystem.getAttrValue("SYSTEM_LEVEL").toString();
				//根据光缆级别（主干或末端）判断光交接箱，站点，分纤箱分纤点级别为主干末端 7：主干接入 8：末端接入
				if(StringUtils.isNotBlank(newSysLevel)&&!newSysLevel.equals(oldSysLevel)){
					if(newSysLevel.equals("7")||newSysLevel.equals("8")){					
						wireSysCheck(dbo,newSysLevel);
					}
				}			
			}
			dbo.removeAttr("LAST_MODIFY_TIME");
			list.add(dbo);
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				GenericDO oldSeg = bo.getObjByCuid(actionContext, dbo.getCuid());
				Map<String, Object> oldAttrsMap = getSystemAttrsMap(oldSeg);
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,dbo);
				updatePointsBySystem(actionContext,dbo,oldAttrsMap);
			} catch (Exception e) {
				logger.error("修改失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}

	@Override
	public EditorPanelMeta delete(EditorPanelMeta editorMeta)
			throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::delete:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("delete");	
		JSONArray  arr = JSON.parseArray(editorMeta.getParas());
		if(arr==null || arr.size() == 0){
			return null;
		}
		List<Map> result = arr.getObject(0, List.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();
			for(Map keyValueMap : result){
				GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
				list.add(dbo);
			}
			try {
				boolean bool = false;
				String check=editorMeta.getSql();
				if(check.equals("true")){
					bool=true;
				}else if(check.equals("false")){
					bool=false;
				}
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list,new Boolean(bool));
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	@SuppressWarnings("rawtypes")
	public void wireSysCheck(GenericDO dbo,String sysLevel) {
		String cuid = dbo.getCuid();
		DataObjectList wireSegs = getWireSegBO().getWireSegsByWireSystemCuid(new BoActionContext(),cuid);
		DataObjectList points = new DataObjectList();
		for(GenericDO wireSeg : wireSegs){
			String origcuid = wireSeg.getAttrString("ORIG_POINT_CUID");
			String destcuid = wireSeg.getAttrString("DEST_POINT_CUID");
//			GenericDO origPoint = getDuctManagerBO().getObjByCuid(new BoActionContext(), origcuid);
//			GenericDO destPoint = getDuctManagerBO().getObjByCuid(new BoActionContext(), destcuid);
			GenericDO origPoint = CheckPoint(origcuid,sysLevel,cuid);
			GenericDO destPoint = CheckPoint(destcuid,sysLevel,cuid);
			points.add(origPoint);
			points.add(destPoint);
		}
		try {
			getDuctManagerBO().updateDDMDuctLine(new BoActionContext(), points);
		} catch (Exception e) {
			throw new UserException(e.getMessage());
		}
	}
	/**
	 * @param cuid : 点设施的CUID
	 * @param sysLevel :所属光缆系统的级别
	 * @param sysCuid :所属光缆系统的CUID
	 * @return point :点设施
	 */
	public GenericDO CheckPoint(String cuid,String sysLevel,String sysCuid){
		String bmClassId = cuid.split("-")[0];
		if(bmClassId.equals("SITE")||bmClassId.equals("FIBER_DP")||bmClassId.equals("FIBER_CAB")){
			GenericDO point = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
			if(sysLevel.equals("7")){
				//主干分纤点:1  末端分纤点:2
				point.setAttrValue("DP_TYPE", "1");
			}else{
				List<String> sysLevels =new ArrayList<String>();
				sysLevels.add(sysLevel);
				String sql = "(" + WireSeg.AttrName.origPointCuid + "='" + cuid
						+ "' or " + WireSeg.AttrName.destPointCuid + " ='"
						+ cuid + "')";
				DataObjectList wireSegs = (DataObjectList) getWireSegBO()
						.getWireSegsBySql(new BoActionContext(), sql);
				for(GenericDO wireSeg : wireSegs){
					String wsCuid = DMHelper.getRelatedCuid(wireSeg
							.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
					WireSystem wsDbo = getWireSystemBO().getWireSystemByCuid(
							new BoActionContext(), wsCuid);
					String branchSyslevel = wsDbo.getAttrValue("SYSTEM_LEVEL").toString();
					if(wsCuid.equals(sysCuid)){
						continue;
					}
					sysLevels.add(branchSyslevel);
				}
				if(sysLevels.contains("7")){
					point.setAttrValue("DP_TYPE", "1");
				}else{
					point.setAttrValue("DP_TYPE", "2");
				}
			}
			return point;
		}
		return null;
	}
	
	/**
	 * 所有承载系统，以DUCT_SYSTEM为例
	 * DUCT_SYSTEM:建设单位\产权\维护方式\施工单位\施工时间\竣工时间
	 * DUCT_SEG:建设单位\产权\维护方式\施工单位\施工时间\竣工时间
	 * MANHLE:产权\维护方式\施工单位\施工时间\竣工时间
	 * DUCT_HOLE,DUCT_CHILD_HOLE:产权\维护方式\施工单位\施工时间\竣工时间---管孔子孔调用暂时去除
	 * 
	 * WIRE_SYSTEM:产权\维护方式\施工单位\施工时间\竣工时间
	 * WIRE_SEG:产权\维护方式\施工单位\施工时间\竣工时间
	 * FIBER_CAB\FIBER_DP\FIBER_JOINT_BOX:产权\维护方式
	 * FIBER:产权\维护方式\施工单位\施工时间\竣工时间
	 * @throws Exception 
	 * 
	 */
	private void updatePointsBySystem(BoActionContext actionContext,GenericDO systemDbo,Map<String, Object> oldAttrsMap) throws Exception{
		//segDbo为要修改的值，需要先查库，从库里得到原来判断，如果上面属性跟segDbo有不同，再调用修改方法，反之不调用
		IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		Map<String,Object> systemAttrsMap = getSystemAttrsMap(systemDbo);
		boolean flag = WebDMUtils.getIsSameAttrs(systemAttrsMap, oldAttrsMap);
		//如果建设单位和共建单位需要修改，并且只有系统和段需要修改，其他如点不需要修改，systemAttrsMap只包含共有属性，建设单位和共建单位需要在此再加判断，用以提高效率
		//flag
		if(flag){
			//根据系统查段
			DataObjectList segsList = bo.getSegsBySystemCuid(actionContext, systemDbo.getCuid());
			//修改段的属性
			if(null != segsList && segsList.size()>0){
				getModifyDbo(systemAttrsMap,segsList);
				bo.updateDatasByCuidOrObjectId(actionContext, segsList, true, false);
				//修改相关起止点属性
				modifyPointsBysegs(actionContext,segsList,systemAttrsMap);
			}
		}
	}
	
	/**
	 * 修改管孔子孔信息
	 * 暂且放在此处
	 * @param actionContext
	 * @param segsList
	 * @param systemAttrsMap
	 * @throws Exception
	 */
	private void modifyDuctHolesBysegs(BoActionContext actionContext,DataObjectList segsList,Map<String,Object> systemAttrsMap) throws Exception{
		//如果是管道段，则处理管子孔信息
		IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		IDuctSegBO ductsegBO = BoHomeFactory.getInstance().getBO(IDuctSegBO.class);
		DataObjectList allHoles = new DataObjectList();
		DataObjectList allChildHoles = new DataObjectList();
		for(GenericDO segDbo : segsList){
			DataObjectList ductholes = ductsegBO.getDuctHolesByDuctSeg(actionContext, segDbo);
			if(null != ductholes && ductholes.size()>0){
				allHoles.addAll(ductholes);
			}
			DataObjectList ductChildHoles = ductsegBO.getDuctChildHolesByDuctSeg(actionContext, segDbo);
			if(null != ductChildHoles && ductChildHoles.size()>0){
				allChildHoles.addAll(ductChildHoles);
			}
		}
		//可能子孔数据量太大，可能需要根据每个段单独修改？
		if(allHoles.size()>0){
			getModifyDbo(systemAttrsMap,allHoles);
			bo.updateDatasByCuidOrObjectId(actionContext, allHoles, true, false);
		}
		if(allChildHoles.size()>0){
			getModifyDbo(systemAttrsMap,allChildHoles);
			bo.updateDatasByCuidOrObjectId(actionContext, allChildHoles, true, false);
		}
	}
	private void modifyPointsBysegs(BoActionContext actionContext,DataObjectList segsList,Map<String,Object> systemAttrsMap) throws Exception{
		IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		Map<String, String> pointMap = new HashMap<String, String>();
        List<String> pointCuids = new ArrayList<String>();
        for (int i = 0; i < segsList.size(); i++) {
            PhysicalJoin seg = (PhysicalJoin) segsList.get(i);
            String origPointCuid = seg.getOrigPointCuid();
            if (!pointMap.containsKey(origPointCuid)) {
                pointCuids.add(origPointCuid);
                pointMap.put(origPointCuid, origPointCuid);
            }
            String destPointCuid = seg.getDestPointCuid();
            if (!pointMap.containsKey(destPointCuid)) {
                pointCuids.add(destPointCuid);
                pointMap.put(destPointCuid, destPointCuid);
            }
        }
       
		//修改段两端的点
        DataObjectList pointList = bo.getGenericDOListByCuids(pointCuids);
		if(null != pointList && pointList.size()>0){
			//站点和接入点不修改
			for (int i = pointList.size() - 1; i >= 0; i--) {
				GenericDO point = pointList.get(i);
				String pCname = point.getClassName();
				if (pCname.equals(Site.CLASS_NAME) || pCname.equals(Accesspoint.CLASS_NAME)) {
					pointList.remove(i);
				}
			}
			
			Map<String, DataObjectList> dboMap = getSameTypeListMapByClassName(pointList);
			if(dboMap.size()>0){
				for(Map.Entry<String, DataObjectList> entry : dboMap.entrySet()){
					//String key = entry.getKey();
					//承载段起止点有无接入点和站点，如果有这两个，后面需要过滤？？？
					DataObjectList value = entry.getValue();
					getModifyDbo(systemAttrsMap,value);
					bo.updateDatasByCuidOrObjectId(actionContext, value, true, false);
				}
			}
		}
		/*管孔子孔不关注，此处暂不处理，后面确认后，代码再删除
		 * if(className.equals(DuctSeg.CLASS_NAME)){
			modifyDuctHolesBysegs(actionContext,segsList,systemAttrsMap);
		}*/
	}
	/**
	 * 修改数据封装
	 * @param map
	 * @param list
	 */
	private void getModifyDbo(Map<String,Object> map,DataObjectList list){
		for(GenericDO dbo : list){
			for(Map.Entry<String,Object> entry : map.entrySet()){
				String key = String.valueOf(entry.getKey());
				Object value = entry.getValue();
				if(null != value){
					dbo.setAttrValue(key, value);
				}
			}
		}
	}
	
	/**
	 * 按类型分数据类型
	 * @param objList
	 * @return
	 */
	private Map<String, DataObjectList> getSameTypeListMapByClassName(DataObjectList objList) {
		Map<String, DataObjectList> mapCuids = new HashMap<String, DataObjectList>();
		for (GenericDO dbo : objList) {
			String className = dbo.getClassName();
			DataObjectList list = mapCuids.get(className);
			if (list == null) {
				list = new DataObjectList();
				mapCuids.put(className, list);
			}
			list.add(dbo);
		}
		return mapCuids;
	}
	
	/**
	 * 建设单位\产权\维护方式\施工单位\施工时间\竣工时间
	 * 封装数据，用于判断是否要调用后台数据
	 * @param dbo 需要修改的数据
	 * @return
	 */
	private Map<String,Object> getSystemAttrsMap(GenericDO dbo){
		Map<String,Object> attrMap = new HashMap<String,Object>();
		//建设单位
		//Object belongcom = dbo.getAttrValue(DuctSystem.AttrName.belongcom);
		//attrMap.put(DuctSystem.AttrName.belongcom, belongcom);
		//产权
		Object ownerShip = dbo.getAttrValue(DuctSystem.AttrName.ownership);
		attrMap.put(DuctSystem.AttrName.ownership, ownerShip);
		
		//维护方式
		Object maintMode = dbo.getAttrValue(DuctSystem.AttrName.maintMode);
		attrMap.put(DuctSystem.AttrName.maintMode, maintMode);
		
		//施工单位
		Object builder = dbo.getAttrValue(DuctSystem.AttrName.builder);
		attrMap.put(DuctSystem.AttrName.builder, builder);
		//施工时间
		Object buildDate = dbo.getAttrValue(DuctSystem.AttrName.buildDate);
		attrMap.put(DuctSystem.AttrName.buildDate, buildDate);
		//竣工时间
		Object finishDate = dbo.getAttrValue(DuctSystem.AttrName.finishDate);
		attrMap.put(DuctSystem.AttrName.finishDate, finishDate);
		return attrMap;
	}
	
}
