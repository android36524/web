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
import com.alibaba.fastjson.JSONObject;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;

public class WireSegPropTemplateBO extends AbstractPropTemplateBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	private IWireSystemBO getWireSystemBO(){
		return BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
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
			Map keyValueMap = result.get(0);
			JSONObject parentNode = (JSONObject)keyValueMap.get("PARENTNODEID");
			if(parentNode == null){
				throw new UserException("为获取到所属系统CUID");
			}
			if(parentNode != null && parentNode.get("value") != null)
				keyValueMap.put("RELATED_SYSTEM_CUID", parentNode.get("value"));
			keyValueMap.remove("PARENTNODEID");
			GenericDO wireseg = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			wireseg.setCuid();
			wireseg.setAttrValue(WireSeg.AttrName.creator,editorMeta.getAc().getUserId());
//			String origCuid =  wireseg.getAttrString("ORIG_POINT_CUID");
//			String destCuid =  wireseg.getAttrString("DEST_POINT_CUID");
//			String origbmClassId = origCuid.split("-")[0];
//			String destbmClassId = destCuid.split("-")[0];
			GenericDO wirebranch = WebDMUtils.createInstanceByClassName(WireBranch.CLASS_NAME,keyValueMap);
			wirebranch.setCuid();
			
			String wireSysCuid = wireseg.getAttrString("RELATED_SYSTEM_CUID");
			WireSystem wireSystem = getWireSystemBO().getWireSystemByCuid(new BoActionContext(), wireSysCuid);
			String sysLevel = wireSystem.getAttrValue("SYSTEM_LEVEL").toString();
			if(StringUtils.isNotBlank(sysLevel)){
				if(sysLevel.equals("7")||sysLevel.equals("8")){					
					wireSegCheck(wireseg,wireSystem,sysLevel);
				}
			}
            try {
				BoCmdFactory.getInstance().execBoCmd("IWireBranchBO.addWireBranch", actionContext, wirebranch);
			} catch (Exception ex) {
				logger.error("光缆分支添加失败",ex);
				throw new UserException(ex);
			}
            wireseg.setAttrValue("RELATED_BRANCH_CUID", wirebranch.getAttrValue("CUID"));
			DataObjectList list=new DataObjectList();
			list.add(wireseg);
			try {
				boolean flag = compareBuildAndFinshDate(wireseg);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				
				long systemLevel=5L;
    			Object relatedSystem = wireseg.getAttrValue(WireSeg.AttrName.relatedSystemCuid);
    			if(relatedSystem instanceof GenericDO){
    				systemLevel=((GenericDO) relatedSystem).getAttrLong(WireSystem.AttrName.systemLevel);
    			}
    			long specialPurpose = ((WireSeg) wireseg).getSpecialPurpose();
    			long olevel = ((WireSeg) wireseg).getOlevel();
    			getSpecialPurposeAndOlevel(systemLevel,specialPurpose,olevel);
    			
				BoCmdFactory.getInstance().execBoCmd(method,actionContext,list);
			} catch (Exception e) {
				logger.error("光缆段添加失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	

	@Override
	public EditorPanelMeta update(EditorPanelMeta editorMeta)
			throws UserException {
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::insert:::"+actionContext.getUserId()+"======"+actionContext.getUserName());

		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
//			String origCuid =  dbo.getAttrString("ORIG_POINT_CUID");
//			String destCuid =  dbo.getAttrString("DEST_POINT_CUID");
//			String origbmClassId = origCuid.split("-")[0];
//			String destbmClassId = destCuid.split("-")[0];
			String wireSysCuid = dbo.getAttrString("RELATED_SYSTEM_CUID");
			WireSystem wireSystem = getWireSystemBO().getWireSystemByCuid(new BoActionContext(), wireSysCuid);
			String sysLevel = wireSystem.getAttrValue("SYSTEM_LEVEL").toString();
			if(StringUtils.isNotBlank(sysLevel)){
				if(sysLevel.equals("7")||sysLevel.equals("8")){					
					wireSegCheck(dbo,wireSystem,sysLevel);
				}
			}
			dbo.removeAttr("LAST_MODIFY_TIME");
			list.add(dbo);
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				long systemLevel=5L;
    			Object relatedSystem = dbo.getAttrValue(WireSeg.AttrName.relatedSystemCuid);
    			if(relatedSystem instanceof GenericDO){
    				systemLevel=((GenericDO) relatedSystem).getAttrLong(WireSystem.AttrName.systemLevel);
    			}
 		
    			long specialPurpose = ((WireSeg) dbo).getSpecialPurpose();
    			long olevel = ((WireSeg) dbo).getOlevel();
    			getSpecialPurposeAndOlevel(systemLevel,specialPurpose,olevel);
    			//先取出数据库里原有的值，判断看需不需要修改其他属性
    			IDuctManagerBO bo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
    			GenericDO oldSeg = bo.getObjByCuid(actionContext, dbo.getCuid());
    			Map<String, Object> oldAttrsMap = getAttrsMap(oldSeg);
    			//此处方法不变，还是先修改
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list);
				//广西数据质量管控需求
				updatePointsBySeg(actionContext,dbo,oldAttrsMap);
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
		//删除光缆段，先删除分支
		String className="WIRE_BRANCH";
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
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list);			
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
	/**
	 * @param dbo: 光缆段
	 * @param wireSystem:所属光缆系统
	 * @param sysLevel :所属光缆系统的级别
	 */
	@SuppressWarnings("rawtypes")
	public void wireSegCheck(GenericDO dbo,WireSystem wireSystem,String sysLevel) {
		String cuid = wireSystem.getCuid();
		String origcuid = dbo.getAttrString("ORIG_POINT_CUID");
		String destcuid = dbo.getAttrString("DEST_POINT_CUID");
		DataObjectList points = new DataObjectList();
		GenericDO origPoint = CheckPoint(origcuid,sysLevel,cuid);
		GenericDO destPoint = CheckPoint(destcuid,sysLevel,cuid);
		points.add(origPoint);
		points.add(destPoint);		
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
	
	//在修改段时，相关的点等需要跟着修改
	/**
	 * WIRE_SEG:产权/用途/维护方式/产权单位/使用单位/维护单位/巡检人/联系电话/检修时间/施工单位/施工时间/竣工时间/专线用途/重要性/光缆类型
	 * FIBER:产权/用途/维护方式/产权单位/使用单位/维护单位/巡检人/联系电话/检修时间/施工单位/施工时间/竣工时间/专线用途/重要性/光缆类型
	 * 光缆段起止点光交接箱、光分纤箱、光接头盒、光终端盒:同上
	 * @throws Exception 
	 * 
	 */
	
	private void updatePointsBySeg(BoActionContext actionContext,GenericDO wireSegDbo,Map<String, Object> oldAttrsMap) throws Exception{
		//segDbo为要修改的值，需要先查库，从库里得到原来判断，如果上面属性跟segDbo有不同，再调用修改方法，反之不调用
		IWireSegBO bo = BoHomeFactory.getInstance().getBO(IWireSegBO.class);
		IFiberBO fiberBO = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		Map<String,Object> modifyAttrsMap = getAttrsMap(wireSegDbo);
		IDuctManagerBO iductmanagerbo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		boolean flag = WebDMUtils.getIsSameAttrs(modifyAttrsMap, oldAttrsMap);
	
		if(flag){
			//修改段两端的点
			String origPointCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(DuctSeg.AttrName.origPointCuid));
			String destPointCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(DuctSeg.AttrName.destPointCuid));
			List<String> lst = new ArrayList<String>();
			if(null != origPointCuid){
				lst.add(origPointCuid);
			}
			
			if(null != destPointCuid){
				lst.add(destPointCuid);
			}
			
			DataObjectList pointList = iductmanagerbo.getGenericDOListByCuids(lst);
			if(null != pointList && pointList.size()>0){
				Map<String, Object> wireSegPointsAttrsMap = getWireSegPointsAttrsMap(wireSegDbo);
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
						//光缆段起止点接入点和站点不修改
						DataObjectList value = entry.getValue();
						//得到需要修改的属性
						getModifyDbo(wireSegPointsAttrsMap, value);
						iductmanagerbo.updateDatasByCuidOrObjectId(actionContext, value, true, false);
					}
				}
			}
			//修改纤芯
			DataObjectList fibers = fiberBO.getFibersByWireSegCuid(actionContext, wireSegDbo.getCuid());
			if(null != fibers && fibers.size()>0){
				getModifyDbo(modifyAttrsMap,fibers);
				fiberBO.modifyFibers(actionContext, fibers);
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
	 * 修改数据封装
	 * @param map
	 * @param list
	 */
	private void getModifyDbo(Map<String,Object> map,DataObjectList list){
		for(GenericDO dbo : list){
			for(Map.Entry entry : map.entrySet()){
				String key = String.valueOf(entry.getKey());
				Object value = entry.getValue();
				if(null != value){
					dbo.setAttrValue(key, value);
				}
			}
		}
	}
	
	/**
	 * WIRE_SEG:产权/用途/维护方式/产权单位/使用单位/维护单位/巡检人/联系电话/检修时间/施工单位/施工时间/竣工时间/专线用途/重要性/光缆类型
	 * FIBER:产权/用途/维护方式/产权单位/使用单位/维护单位/巡检人/联系电话/检修时间/施工单位/施工时间/竣工时间/专线用途/重要性/光缆类型
	 * 封装数据，用于判断是否要调用后台数据
	 * @param dbo 需要修改的数据
	 * @return
	 */
	private Map<String,Object> getAttrsMap(GenericDO dbo){
		Map<String,Object> attrMap = new HashMap<String,Object>();
		//产权
		Object ownerShip = dbo.getAttrValue(WireSeg.AttrName.ownership);
		attrMap.put(WireSeg.AttrName.ownership, ownerShip);
		//用途
		Object purpose = dbo.getAttrValue(WireSeg.AttrName.purpose);
		attrMap.put(WireSeg.AttrName.purpose, purpose);
		//维护方式
		Object maintMode = dbo.getAttrValue(WireSeg.AttrName.maintMode);
		attrMap.put(WireSeg.AttrName.maintMode, maintMode);
		
		//产权单位/
		Object resOwner = dbo.getAttrValue(WireSeg.AttrName.resOwner);
		attrMap.put(WireSeg.AttrName.resOwner, resOwner);
		//使用单位
		Object userName = dbo.getAttrValue(WireSeg.AttrName.userName);
		attrMap.put(WireSeg.AttrName.userName, userName);
		
		//维护单位
		Object maintDep = dbo.getAttrValue(WireSeg.AttrName.maintDep);
		attrMap.put(WireSeg.AttrName.maintDep, maintDep);
		//巡检人
		Object servicer = dbo.getAttrValue(WireSeg.AttrName.servicer);
		attrMap.put(WireSeg.AttrName.servicer, servicer);
		//联系电话
		Object phoneNo = dbo.getAttrValue(WireSeg.AttrName.phoneNo);
		attrMap.put(WireSeg.AttrName.phoneNo, phoneNo);
		
		//检修时间
		Object checkDate = dbo.getAttrValue(WireSeg.AttrName.checkDate);
		attrMap.put(WireSeg.AttrName.checkDate, checkDate);
		//施工单位
		Object builder = dbo.getAttrValue(WireSeg.AttrName.builder);
		attrMap.put(WireSeg.AttrName.builder, builder);
		//施工时间
		Object buildDate = dbo.getAttrValue(WireSeg.AttrName.buildDate);
		attrMap.put(WireSeg.AttrName.buildDate, buildDate);
		//竣工时间
		Object finishDate = dbo.getAttrValue(WireSeg.AttrName.finishDate);
		attrMap.put(WireSeg.AttrName.finishDate, finishDate);
		//专线用途
		Object specialPurpose = dbo.getAttrValue(WireSeg.AttrName.specialPurpose);
		attrMap.put(WireSeg.AttrName.specialPurpose, specialPurpose);
		//重要性
		Object olevel = dbo.getAttrValue(WireSeg.AttrName.olevel);
		attrMap.put(WireSeg.AttrName.olevel, olevel);
		//光缆类型
		Object wireType = dbo.getAttrValue(WireSeg.AttrName.wireType);
		attrMap.put(WireSeg.AttrName.wireType, wireType);
		
		return attrMap;
	}
	
	/**
	 * 光缆段起止点
	 * 接头盒(终端盒):产权归属\用途\维护方式\使用单位\维护单位
	 * 分纤箱、分纤箱：产权归属\用途\维护方式\使用单位\维护单位\巡检人
	 * 封装数据，用于判断是否要调用后台数据
	 * @param dbo 需要修改的数据
	 * @return
	 */
	private Map<String,Object> getWireSegPointsAttrsMap(GenericDO dbo){
		String className = dbo.getClassName();
		Map<String,Object> attrMap = new HashMap<String,Object>();
		//产权
		Object ownerShip = dbo.getAttrValue(WireSeg.AttrName.ownership);
		attrMap.put(WireSeg.AttrName.ownership, ownerShip);
		//用途
		Object purpose = dbo.getAttrValue(WireSeg.AttrName.purpose);
		attrMap.put(WireSeg.AttrName.purpose, purpose);
		//维护方式
		Object maintMode = dbo.getAttrValue(WireSeg.AttrName.maintMode);
		attrMap.put(WireSeg.AttrName.maintMode, maintMode);
		//使用单位
		Object userName = dbo.getAttrValue(WireSeg.AttrName.userName);
		attrMap.put(WireSeg.AttrName.userName, userName);
		
		//维护单位
		Object maintDep = dbo.getAttrValue(WireSeg.AttrName.maintDep);
		attrMap.put(WireSeg.AttrName.maintDep, maintDep);
		if(className.equals(FiberDp.CLASS_NAME) || className.equals(FiberCab.CLASS_NAME)){
			//巡检人
			Object servicer = dbo.getAttrValue(WireSeg.AttrName.servicer);
			attrMap.put(WireSeg.AttrName.servicer, servicer);
		}
		return attrMap;
	}
}
