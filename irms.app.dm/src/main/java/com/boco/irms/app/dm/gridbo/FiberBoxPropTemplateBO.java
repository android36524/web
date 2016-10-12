package com.boco.irms.app.dm.gridbo;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.irms.app.utils.DMNameUtils;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.cm.IRoomBO;

public class FiberBoxPropTemplateBO extends AbstractPropTemplateBO {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public EditorPanelMeta insert(EditorPanelMeta editorMeta) throws UserException {
	    BoActionContext actionContext = ActionContextUtil.getActionContext();
        logger.debug("context:::insert:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("insert");
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();		
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			String name = editorMeta.getCuid();
			if(name != null && !name.equals("")){
	    		if(name.equals("IRMS.RMS.FIBERJOINTBOX")){
	    			dbo.setAttrValue(FiberJointBox.AttrName.kind, 1);
	    		}else if(name.equals("IRMS.RMS.FIBERTERMINALBOX")){
	    			dbo.setAttrValue(FiberJointBox.AttrName.kind, 2);
	    		}
	    	}
			if(StringUtils.isEmpty(dbo.getAttrValue("LABEL_CN"))){
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
					list.add(newDbo);
				}
			}else {
				//---集客接入场景赋值---by wq ---20150318--
				if(className != null && !className.equals("")){
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
							
							if(LocaCuid != null)
							{
								String locationClassName = LocaCuid.split("-")[0];
								if(locationClassName.equals(Room.CLASS_NAME)){
									IRoomBO roomBO = (IRoomBO)BoHomeFactory.getInstance().getBO(IRoomBO.class);	
									Room roomObj = roomBO.getRoomByCuid(actionContext, LocaCuid);
									String relatedSiteCuid = roomObj.getRelatedSiteCuid();
									dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid,relatedSiteCuid );
									dbo.setAttrValue(FiberCab.AttrName.relatedRoomCuid, LocaCuid);
								}else{
									dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid, LocaCuid);							
								}
							}
						}
					}
				}
				//--end--
				dbo.setCuid();
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
	public EditorPanelMeta update(EditorPanelMeta editorMeta) throws UserException {
	    BoActionContext actionContext = ActionContextUtil.getActionContext();
        logger.debug("context:::update:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		String className=editorMeta.getClassName();
		String method = editorMeta.getRemoteMethod("update");	
		List<Map> result = JSON.parseArray(editorMeta.getParas(),Map.class);
		if(result != null && result.size() > 0){
			DataObjectList list = new DataObjectList();	
			Map keyValueMap = result.get(0);
			GenericDO dbo = WebDMUtils.createInstanceByClassName(className,keyValueMap);
			String name = editorMeta.getCuid();
			if(name != null && !name.equals("")){
	    		if(name.equals("IRMS.RMS.FIBERJOINTBOX")){
	    			dbo.setAttrValue(FiberJointBox.AttrName.kind, 1);
	    		}else if(name.equals("IRMS.RMS.FIBERTERMINALBOX")){
	    			dbo.setAttrValue(FiberJointBox.AttrName.kind, 2);
	    		}
	    	}
			dbo.removeAttr("LAST_MODIFY_TIME");
			//---集客接入场景赋值---by wq ---20150318--
			if(className != null && !className.equals("")){
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
							if(locationClassName.equals(Room.CLASS_NAME)){
								IRoomBO roomBO = (IRoomBO)BoHomeFactory.getInstance().getBO(IRoomBO.class);	
								Room roomObj = roomBO.getRoomByCuid(actionContext, locaCuid);
								String relatedSiteCuid = roomObj.getRelatedSiteCuid();
								dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid,relatedSiteCuid );
								dbo.setAttrValue(FiberCab.AttrName.relatedRoomCuid, locaCuid);
							}else{
								dbo.setAttrValue(FiberCab.AttrName.relatedSiteCuid, locaCuid);							
							}
						}
					}
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
	
}
