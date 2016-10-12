package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.dm.gridbo.DuctTreeGridBO;
import com.boco.irms.app.dm.gridbo.RouteManagerBO;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
public class DMSystemAction {

	private static final String DUCT_TREE_GRID_BO = "DuctTreeGridBO";
	
	private static final String ROUTE_MANAGER_BO = "RouteManagerBO";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 根据分支cuid获得路有点
	 * @param request
	 * @param response
	 * @param cuid
	 * @author caoliang
	 */
	public List<Map> getDisplayPoints(HttpServletRequest request,String cuid){
		DataObjectList presetPointList=new DataObjectList();
		try {
			presetPointList=getDuctTreeGridBO().getRoutePointsByBranchCuid(cuid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("通过分支获取路由点出错", e);
		}		
		
		List<Map> list=new ArrayList<Map>();
		for(GenericDO dbo:presetPointList){
			Map map=new HashMap();
			list.add(map);
			String key=dbo.getCuid();
			String labelCn=dbo.getAttrString("LABEL_CN");
			boolean flag=dbo.getAttrBool("ISSELECTED");
			map.put("cuid", key);
			map.put("labelCn", labelCn);
			map.put("selected", flag);
			map.put("objectId", dbo.getObjectId());
			map.put("classtype", dbo.getAttrString("CLASS_TYPE"));
		}
		return list;
	}
	
	/**
	 * 修改添加
	 * @param request
	 * @param response
	 * @param cuid
	 * @author caoliang
	 */
	public void save(HttpServletRequest request,Map branch,String systemCuid,String type,List<Map> rountPoints) throws UserException{
		BoActionContext actionContext = ActionContextUtil.getActionContext();
        logger.debug("context:::deleteSystemForFlow:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		GenericDO branchDbo=new GenericDO();
		branchDbo.setClassName(type);
		branchDbo = branchDbo.createInstanceByClassName();
		
		if(UpLine.CLASS_NAME.equals(type) || HangWall.CLASS_NAME.equals(type)){
		    branchDbo.setAttrValue("CUID", systemCuid);
		}
		else{
			branchDbo.setAttrValue("REMARK", branch.get("REMARK"));
			branchDbo.setAttrValue("LABEL_CN", branch.get("LABEL_CN"));
			branchDbo.setAttrValue("RELATED_SYSTEM_CUID", systemCuid);
			int action=DMHelper.DBO_NEW;
			if(branch.containsKey("CUID")&&StringUtils.isNotEmpty((String) branch.get("CUID"))){
				branchDbo.setAttrValue("CUID", branch.get("CUID"));
				branchDbo.setObjectId( (String) branch.get("OBJECTID"));
				action=DMHelper.DBO_MODIFY;
			}
			branchDbo.setAttrValue("OBJECT_STATE", action);
		}
		DataObjectList list=new DataObjectList();
		for(int i=0;i<rountPoints.size();i++){
			Map rountPoint=rountPoints.get(i);
			GenericDO dbo=new GenericDO();
			dbo.setClassName(rountPoint.get("BM_CLASS_ID").toString());
			dbo = dbo.createInstanceByClassName();
			list.add(dbo);
			dbo.setAttrValue("LABEL_CN",rountPoint.get("LABEL_CN"));
			dbo.setAttrValue("CUID",rountPoint.get("CUID"));
			dbo.setAttrValue("ISSELECTED", Boolean.parseBoolean(rountPoint.get("IS_SELECT").toString()));
			dbo.setObjectId(rountPoint.get("OBJECTID").toString());
			if(!(UpLine.CLASS_NAME.equals(type) || HangWall.CLASS_NAME.equals(type))){
				if(i==0){
					branchDbo.setAttrValue("ORIG_POINT_CUID",rountPoint.get("CUID"));
				}
				if(i==rountPoints.size()-1){
					branchDbo.setAttrValue("DEST_POINT_CUID",rountPoint.get("CUID"));
				}
			}
			
		}
		getRouteManagerBO().saveRouteInfo(actionContext,branchDbo, list);
	}
	
	
	public void delObjByCuid(HttpServletRequest request,String cuid,String objectId) throws IOException{
	    BoActionContext actionContext = ActionContextUtil.getActionContext();
        logger.debug("context:::delObjByCuid:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
		GenericDO branchDbo =new GenericDO();
		branchDbo.setClassName(cuid.split("-")[0]);
		branchDbo = branchDbo.createInstanceByClassName();
		branchDbo.setAttrValue("CUID", cuid);
		branchDbo.setObjectId(objectId);
		getRouteManagerBO().deleteBranch(actionContext,branchDbo);
	}
	public DuctTreeGridBO getDuctTreeGridBO(){
		return (DuctTreeGridBO) SpringContextUtil.getBean(DUCT_TREE_GRID_BO);
	}
	
	public RouteManagerBO getRouteManagerBO(){
		return (RouteManagerBO) SpringContextUtil.getBean(ROUTE_MANAGER_BO);
	}
}
