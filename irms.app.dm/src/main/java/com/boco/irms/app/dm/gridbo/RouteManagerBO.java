package com.boco.irms.app.dm.gridbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class RouteManagerBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 * @param branch  分支  用一个标识来说明这个branch是新增还是修改
	 * @param routeNodes  分支对应的路由点
	 * @throws UserException
	 */
	public  void  saveRouteInfo(BoActionContext ac,GenericDO branch,DataObjectList routeNodes) throws UserException{
		if(!(branch instanceof UpLine || branch instanceof HangWall)){
			Integer objectstate = (Integer)branch.getAttrValue("OBJECT_STATE");
			if(DMHelper.DBO_NEW.equals(objectstate)){
				addBranch(branch);
			}else{
				modityBranch(ac,branch);
			}
		}		

		if(checkRoute(routeNodes)){
			modifyRoute(ac,branch,routeNodes);
		}
	}

	private void addBranch(GenericDO branch){
/*		String className= "DUCT_BRANCH";
		String labelcn = "123";
		String remark = "ssssss";
		String systemcuid = "";
		GenericDO gDO = new GenericDO();
		gDO.setClassName(className);
		gDO = gDO.createInstanceByClassName();
        gDO.setAttrValue(GenericDO.AttrName.labelCn, labelcn);
        gDO.setAttrValue("REMARK", remark);
        gDO.setAttrValue("RELATED_SYSTEM_CUID", systemcuid);*/
		branch.setCuid();
        DMHelper.setDboNew(branch, DMHelper.DBO_NEW);
	}
	
	
	private void modityBranch(BoActionContext context,GenericDO branch) throws UserException{
		try {
			DataObjectList branchs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjsByCuid", context,new String[]{branch.getCuid()});
			if(branchs != null && branchs.size()>0){
				GenericDO gdo = branchs.get(0);
				gdo.setAttrValue("LABEL_CN", branch.getAttrValue("LABEL_CN"));
				gdo.setAttrValue("REMARK", branch.getAttrValue("REMARK"));
				gdo.setAttrValue("ORIG_POINT_CUID",branch.getAttrValue("ORIG_POINT_CUID"));
				gdo.setAttrValue("DEST_POINT_CUID",branch.getAttrValue("DEST_POINT_CUID"));
				branch = gdo;
			    BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.updateDbos", context, branchs);
			}
		} catch (Exception e) {
			//logger.error("修改分支信息失败", e);
			throw new UserException(e.getMessage());
		}
	}
    /**
     * 检查路由合法性
     */
    protected  boolean checkRoute(DataObjectList lmodel) throws UserException{
        if (lmodel.size() < 2) {
        	throw new UserException("路由点不能少于2个点");
        } else {
        	GenericDO sdbo = lmodel.get(0);
        	GenericDO edbo = lmodel.get(lmodel.size() - 1);
            if (sdbo instanceof PresetPoint || edbo instanceof PresetPoint) {
            	throw new UserException("路由点不能少于2个点");
            }
            if (!sdbo.getAttrBool("ISSELECTED") || !edbo.getAttrBool("ISSELECTED")) {
            	throw new UserException("起止点必须作为显示路由点");
            }
            if (sdbo.getCuid().equals(edbo.getCuid())) {
            	throw new UserException("起止点不能相同");
            }
        }
        for (int i = 1; i < lmodel.size() - 1; i++) {
            GenericDO point = lmodel.get(i);
            if (!(point instanceof PresetPoint)) {
                continue;
            }
        }
        return true;
    }

    /**
     * 保存路由信息
     * @param branch
     * @param lmodel
     * @throws UserException
     */
    private void modifyRoute(BoActionContext ac,GenericDO branch,DataObjectList lmodel) throws UserException {
        DataObjectList routePoints = getRoutePoints(lmodel);
		try {
	        for (GenericDO routePoint : routePoints) {
	            if (!DMHelper.isDboNew(routePoint) && !DMHelper.isDboModify(routePoint)) {
	                GenericDO copyRoutePoint = new GenericDO(routePoint.getClassName());
	                copyRoutePoint = copyRoutePoint.createInstanceByClassName();
	                copyRoutePoint.setCuid(routePoint.getCuid());
	                Object obj = routePoint.getAttrValue("DISPLAY_POINT");
	                copyRoutePoint.setAttrValue("DISPLAY_POINT", obj);
	                routePoint = copyRoutePoint;
	            }
	        }
	        DataObjectList list = new DataObjectList();
	        if (branch != null && DMHelper.isBranchClassName(branch.getClassName())) {
	            list.add(branch);  //branch是分支
	        } else {
	            list.add(branch);  //branch是系统
	        }
	        DataObjectList segtemplalist = new DataObjectList();
	        if(branch instanceof DuctBranch){
	        	segtemplalist.add(new DuctSeg()); 
	        }
	        else if(branch instanceof PolewayBranch){
	        	segtemplalist.add(new PolewaySeg());
	        }
	        else if(branch instanceof StonewayBranch){
	        	segtemplalist.add(new StonewaySeg());	        	
	        }
	        else if(branch instanceof UpLine){
	        	segtemplalist.add(new UpLineSeg());
	        }
	        else if(branch instanceof HangWall){
	        	segtemplalist.add(new HangWallSeg());
	        }
	        
			BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.saveDbosAndRoute", ac,list, routePoints, segtemplalist);
		} catch (Exception e) {
			logger.error("保存路由信息失败",e);
			throw new UserException("保存路由信息失败");
		}
    }
    
    protected static DataObjectList getRoutePoints(DataObjectList listmodel) {
        DataObjectList routes = new DataObjectList();
        for (int i = 0; i < listmodel.size(); i++) {
            GenericDO gdo = listmodel.get(i);
            routes.add(gdo);
            if (gdo.getAttrBool("ISSELECTED")) {
                gdo.setAttrValue("DISPLAY_POINT", true);
            }else{
            	gdo.setAttrValue("DISPLAY_POINT", false);
            }
            
            if(Accesspoint.CLASS_NAME.equals(gdo.getClassName())){ 
				if(gdo.getAttrValue(Accesspoint.AttrName.vpnCuid)==null){
					gdo.setAttrValue(Accesspoint.AttrName.vpnCuid, " ");
				}
				if (gdo.getAttrValue(Accesspoint.AttrName.vpCuid)==null){
					gdo.setAttrValue(Accesspoint.AttrName.vpCuid, " ");
				}
				if (gdo.getAttrValue(Accesspoint.AttrName.siteCuid)==null){
					gdo.setAttrValue(Accesspoint.AttrName.siteCuid, " ");
				}
				if(gdo.getAttrValue(Accesspoint.AttrName.districtCuid) ==  null){
					gdo.setAttrValue(Accesspoint.AttrName.districtCuid, "DISTRICT-00001");
				}
	        }else if(Site.CLASS_NAME.equals(gdo.getClassName())){
	        	if(gdo.getAttrValue(Site.AttrName.relatedSpaceCuid) == null){
		        	gdo.setAttrValue(Site.AttrName.relatedSpaceCuid, "DISTRICT-00001");
	        	}
	        }else{
	        	if(gdo.getAttrValue(Manhle.AttrName.relatedDistrictCuid) == null){
		        	gdo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, "DISTRICT-00001");
	        	}
	        }
        }
        return routes;
    }
    /**
     * 删除分支
     * @param branch
     * @throws UserException
     */
    public void deleteBranch(BoActionContext actionContext,GenericDO branch) throws UserException{
    	
    	try {
    		if(branch instanceof DuctBranch){
    			BoCmdFactory.getInstance().execBoCmd("IDuctBranchBO.deleteDuctBranchWithSlotDuct", actionContext, branch);
    		}
    		else if(branch instanceof PolewayBranch){
    			BoCmdFactory.getInstance().execBoCmd("IPolewayBranchBO.deletePolewayBranch", actionContext, branch);
    		}
    		else if(branch instanceof StonewayBranch){
    			BoCmdFactory.getInstance().execBoCmd("IStonewayBranchBO.deleteStonewayBranch", actionContext, branch);
    		}
			
		} catch (Exception e) {
			logger.error("删除分支失败",e);
			throw new UserException("删除分支失败");
		}
    }
}


