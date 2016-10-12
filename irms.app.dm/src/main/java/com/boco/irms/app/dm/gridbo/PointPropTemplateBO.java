package com.boco.irms.app.dm.gridbo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.DMNameUtils;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.irms.common.conf.ModelProperty.attrName;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.ProjectManagement;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.projectmanagement.ProjectManagementBO;
import com.boco.transnms.server.bo.ibo.cm.IRoomBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;
public class PointPropTemplateBO extends AbstractPropTemplateBO {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	private IWireSystemBO getWireSystemBO(){
		return BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
	}
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
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
			if(StringUtils.isEmpty((String) dbo.getAttrValue("LABEL_CN"))){
				Object value = keyValueMap.get(FIRST_NAME_BATCH);
				String firstName = WebDMUtils.convertJosn2Object(FIRST_NAME_BATCH,value).toString();
				
				value = keyValueMap.get(LAST_NAME_BATCH);
				String lastName = WebDMUtils.convertJosn2Object(LAST_NAME_BATCH,value).toString();
				
				value = keyValueMap.get(FIGURE_BATCH);
				String figure = WebDMUtils.convertJosn2Object(FIGURE_BATCH,value).toString();
				
				value = keyValueMap.get(START_NO_BATCH);
				int start = Integer.parseInt(WebDMUtils.convertJosn2Object(START_NO_BATCH,value).toString());
				
				value = keyValueMap.get(NO_BATCH);
				int num = Integer.parseInt(WebDMUtils.convertJosn2Object(NO_BATCH,value).toString());
				List<String> labelCn=DMNameUtils.getDMDataName(firstName, lastName, figure, start, num);
				for(int i=0;i<labelCn.size();i++){
					GenericDO newDbo=(GenericDO) dbo.deepClone();
					newDbo.setCuid();
					newDbo.setAttrValue("LABEL_CN", labelCn.get(i));
					newDbo.setAttrValue(FiberCab.AttrName.deviceType, 1);
					list.add(newDbo);
				}
			}else {
				//---集客接入场景赋值---by wq ---20150318--
				if(className != null && !className.equals("")){
					//根据所属工程所属作业设置工程作业状态
					if(!className.equals(Accesspoint.CLASS_NAME)){
						if(StringUtils.isNotBlank(dbo.getAttrString("RELATED_PROJECT_CUID"))){
							String relatedProjectCuid = dbo.getAttrString("RELATED_PROJECT_CUID");
							GenericDO project = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedProjectCuid);
							dbo.setAttrValue("PROJECT_STATE", project.getAttrValue("STATE"));
						}
						if(StringUtils.isNotBlank(dbo.getAttrString("RELATED_MAINT_CUID"))){							
							String relatedMaintCuid = dbo.getAttrString("RELATED_MAINT_CUID");
							GenericDO maint = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedMaintCuid);
							dbo.setAttrValue("MAINT_STATE", maint.getAttrValue("STATE"));
						}
						
					}
					if(className.equals(FiberCab.CLASS_NAME) || className.equals(FiberDp.CLASS_NAME) || className.equals(FiberJointBox.CLASS_NAME)){
						String accessScene = JSON.toJSONString(keyValueMap.get("ACCESS_SCENE"));
						List<Map> accessSceneList = JSON.parseArray("["+accessScene+"]",Map.class);
						if(accessSceneList != null && accessSceneList.size() > 0){
							Map accessSceneMap = accessSceneList.get(0);
							Object keyObj = accessSceneMap.get("CUID");
							if(keyObj == null){
								accessScene = JSON.toJSONString(accessSceneList.get(0).get("value"));
								accessSceneList = JSON.parseArray("["+accessScene+"]",Map.class);
								if(accessSceneList != null && accessSceneList.size() > 0){
									accessSceneMap = accessSceneList.get(0);
									keyObj = accessSceneMap.get("CUID");
								}
							}
							Long accessScence = Long.parseLong(keyObj.toString());
							dbo.setAttrValue(FiberCab.AttrName.accessScene, accessScence);
						}
						
					    String	relatedSite = JSON.toJSONString(keyValueMap.get("RELATED_SITE_CUID"));					   
						List<Map> devLocationList = JSON.parseArray("["+relatedSite+"]", Map.class);
						Map devLocMap = devLocationList.get(0);
						if(devLocMap != null)
						{
							String LocaCuid = (String) devLocMap.get("CUID");
							if(LocaCuid == null)
							{
								Object value = devLocMap.get("value");
								//JSONObject可能的引用可能来自两个不同的包
								if(value instanceof JSONObject)
								{
									LocaCuid = ((JSONObject) value).getString("CUID");
								}
								if(value instanceof com.alibaba.fastjson.JSONObject)
								{
									LocaCuid = ((com.alibaba.fastjson.JSONObject) value).getString("CUID");
								}
							}
							if(LocaCuid != null)
							{	
								String locationClassName = LocaCuid.split("-")[0];
								//根据分纤箱所属位置设置其经纬度
								if(className.equals(FiberDp.CLASS_NAME)&&(locationClassName.equals(Pole.CLASS_NAME)||locationClassName.equals("ACCESSPOINT"))){
//									GenericDO reslocation  = getDuctManagerBO().getObjByCuid(new BoActionContext(), LocaCuid);
//									if(StringUtils.isNotBlank((String) reslocation.getAttrValue(FiberDp.AttrName.longitude))){
//										dbo.setAttrValue(FiberDp.AttrName.longitude,reslocation.getAttrValue(FiberDp.AttrName.longitude));										
//									}
//									if(StringUtils.isNotBlank((String) reslocation.getAttrValue(FiberDp.AttrName.latitude))){
//										dbo.setAttrValue(FiberDp.AttrName.latitude,reslocation.getAttrValue(FiberDp.AttrName.latitude));										
//									}

									GenericDO reslocation  = getDuctManagerBO().getObjByCuid(new BoActionContext(), LocaCuid);
									Object longitude = reslocation.getAttrValue(FiberDp.AttrName.longitude);
									Object latitude = reslocation.getAttrValue(FiberDp.AttrName.latitude);
									if(longitude!=null){
										dbo.setAttrValue(FiberDp.AttrName.longitude,longitude.toString());										
									}
									if(latitude!=null){
										dbo.setAttrValue(FiberDp.AttrName.latitude,latitude.toString());										
									}								
								}
								if(locationClassName.equals(Room.CLASS_NAME)){
									IRoomBO roomBO = (IRoomBO)BoHomeFactory.getInstance().getBO(IRoomBO.class);	
									Room roomObj = roomBO.getRoomByCuid(new BoActionContext(), LocaCuid);
									String relatedSiteCuid = roomObj.getRelatedSiteCuid();
									dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid,relatedSiteCuid );
									dbo.setAttrValue(FiberCab.AttrName.relatedRoomCuid, LocaCuid);
								}
								else{
									dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid, LocaCuid);							
								}
							}
						}
						//判断光缆级别
//						if (!className.equals(FiberJointBox.CLASS_NAME)) {
//							pointCheck(keyValueMap);
//						}
					}
				}
				//--end--
				dbo.setCuid();
				dbo.setAttrValue(FiberCab.AttrName.deviceType, 1);
				list.add(dbo);
			}
			
			
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list);
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
		String className=editorMeta.getClassName();
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		logger.debug("context:::update:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);	
			dbo.removeAttr("LAST_MODIFY_TIME");
			//---集客接入场景赋值---by wq ---20150318--
			if(className != null && !className.equals("")){
				//根据所属工程所属作业设置工程作业状态
				if(!className.equals(Accesspoint.CLASS_NAME)){
					if(StringUtils.isNotBlank(dbo.getAttrString("RELATED_PROJECT_CUID"))){
						String relatedProjectCuid = dbo.getAttrString("RELATED_PROJECT_CUID");
						GenericDO project = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedProjectCuid);
						dbo.setAttrValue("PROJECT_STATE", project.getAttrValue("STATE"));
					}
					if(StringUtils.isNotBlank(dbo.getAttrString("RELATED_MAINT_CUID"))){							
						String relatedMaintCuid = dbo.getAttrString("RELATED_MAINT_CUID");
						GenericDO maint = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedMaintCuid);
						dbo.setAttrValue("MAINT_STATE", maint.getAttrValue("STATE"));
					}
					
				}
				if(className.equals(FiberCab.CLASS_NAME) || className.equals(FiberDp.CLASS_NAME) || className.equals(FiberJointBox.CLASS_NAME)){
					String accessScene = JSON.toJSONString(keyValueMap.get("ACCESS_SCENE"));
					List<Map> accessSceneList = JSON.parseArray("["+accessScene+"]",Map.class);
					if(accessSceneList != null && accessSceneList.size() > 0){
						Map accessSceneMap = accessSceneList.get(0);
						Object keyObj = accessSceneMap.get("CUID");
						if(keyObj == null){
							accessScene = JSON.toJSONString(accessSceneList.get(0).get("value"));
							accessSceneList = JSON.parseArray("["+accessScene+"]",Map.class);
							if(accessSceneList != null && accessSceneList.size() > 0){
								accessSceneMap = accessSceneList.get(0);
								keyObj = accessSceneMap.get("CUID");
							}
						}
						Long accessScence = Long.parseLong(keyObj.toString());
						dbo.setAttrValue(FiberCab.AttrName.accessScene, accessScence);
					}
					
				    String	relatedSite = JSON.toJSONString(keyValueMap.get("RELATED_SITE_CUID"));					   
					List<Map> devLocationList = JSON.parseArray("["+relatedSite+"]", Map.class);
					Map devLocMap = devLocationList.get(0);
					if(devLocMap != null)
					{
						String locaCuid = (String) devLocMap.get("CUID");
						
						if(!StringUtils.isEmpty(locaCuid))
						{
							String locationClassName = locaCuid.split("-")[0];
							//根据分纤箱所属位置设置其经纬度
							if(className.equals(FiberDp.CLASS_NAME)&&(locationClassName.equals(Pole.CLASS_NAME)||locationClassName.equals("ACCESSPOINT"))){
								GenericDO reslocation  = getDuctManagerBO().getObjByCuid(new BoActionContext(), locaCuid);
								Object longitude = reslocation.getAttrValue(FiberDp.AttrName.longitude);
								Object latitude = reslocation.getAttrValue(FiberDp.AttrName.latitude);
								if(longitude!=null){
									dbo.setAttrValue(FiberDp.AttrName.longitude,longitude.toString());										
								}
								if(latitude!=null){
									dbo.setAttrValue(FiberDp.AttrName.latitude,latitude.toString());										
								}
							}
							if(locationClassName.equals(Room.CLASS_NAME)){
								IRoomBO roomBO = (IRoomBO)BoHomeFactory.getInstance().getBO(IRoomBO.class);	
								Room roomObj = roomBO.getRoomByCuid(new BoActionContext(), locaCuid);
								String relatedSiteCuid = roomObj.getRelatedSiteCuid();
								dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid,relatedSiteCuid );
								dbo.setAttrValue(FiberCab.AttrName.relatedRoomCuid, locaCuid);
							}else{
								dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid, locaCuid);							
							}
						}
					}
//					if (!className.equals(FiberJointBox.CLASS_NAME)) {
//						pointCheck(keyValueMap);
//					}
					
				}
			}
			//--end--
			list.add(dbo);
			try {
				boolean flag = compareBuildAndFinshDate(dbo);
				if(!flag){
					throw new UserException("施工时间不能大于竣工时间!");
				}
				BoCmdFactory.getInstance().execBoCmd(method,actionContext,list);
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
				BoCmdFactory.getInstance().execBoCmd(method, actionContext,list);
			} catch (Exception e) {
				logger.error("删除失败",e);
				throw new UserException(e);
			}
		}
		return editorMeta;
	}
//	public void pointCheck(Map points){
//		String sysLevel = new String();
//		String  cuid = JSON.toJSONString(points
//				.get("CUID"));
//		List<Map> cuidList = JSON.parseArray("["+cuid+"]",Map.class);
//	    cuid = cuidList.get(0).get("value").toString();
////		String dpType = JSON.toJSONString(points
////				.get("DP_TYPE"));
////	    String	origRelatedSite = JSON.toJSONString(points.get("RELATED_SITE_CUID"));
////	    List<Map> dpTypeList = JSON.parseArray("["+dpType+"]",Map.class);
////	    List<Map> RelatedSiteList = JSON.parseArray("["+origRelatedSite+"]",Map.class);
////	    dpType = dpTypeList.get(0).get("CUID").toString();
////	    origRelatedSite = RelatedSiteList.get(0).get("value").toString();
////		List<Map> RelatedCuidList = JSON.parseArray("["+origRelatedSite+"]",Map.class);
////		String RelatedCuid = RelatedCuidList.get(0).get("CUID").toString();
//	    String sql = "(" + WireSeg.AttrName.origPointCuid + "='" + cuid
//				+ "' or " + WireSeg.AttrName.destPointCuid + " ='"
//				+ cuid + "')";
//		DataObjectList wireSegs = (DataObjectList) getWireSegBO()
//					.getWireSegsBySql(new BoActionContext(),sql);
//		if (wireSegs != null && wireSegs.size() > 0) {
//			for (GenericDO wireseg : wireSegs) {
//				String origCuid  = wireseg.getAttrValue(WireSeg.AttrName.origPointCuid).toString();
//				String destCuid  = wireseg.getAttrValue(WireSeg.AttrName.destPointCuid).toString();
//				GenericDO origPoint = getDuctManagerBO().getObjByCuid(new BoActionContext(), origCuid);
//				GenericDO destPoint = getDuctManagerBO().getObjByCuid(new BoActionContext(), destCuid);
//				String origRelatedSiteCuid = origPoint.getAttrString("RELATED_SITE_CUID");					
//				String destRelatedSiteCuid = destPoint.getAttrString("RELATED_SITE_CUID");
//				String origDpType = origPoint.getAttrValue("DP_TYPE").toString();
//				String destDpType = destPoint.getAttrValue("DP_TYPE").toString();
//				if(StringUtils.isNotBlank(origDpType)&&StringUtils.isNotBlank(destDpType)){			
//					if(destRelatedSiteCuid.split("-")[0].equals("ACCESSPOINT")||origRelatedSiteCuid.split("-")[0].equals("ACCESSPOINT")){
//						sysLevel = "8";
//					}
//					else{
//						if(origDpType.equals("2")||destDpType.equals("2")){
//							sysLevel = "8";
//						}
//						else 
//						{
//							sysLevel = "7";
//						}
//					}
//					String wsCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
//					WireSystem wireSystem = getWireSystemBO().getWireSystemByCuid(new BoActionContext(),wsCuid);
//					wireSystem.setAttrValue(WireSystem.AttrName.systemLevel,sysLevel);
//					try {
//						getWireSystemBO().modifyWireSystem(new BoActionContext(),wireSystem);
//					} catch (Exception e) {
//						throw new UserException(e.getMessage());
//					}
//				}
//			}
//		}			
//	}
}
