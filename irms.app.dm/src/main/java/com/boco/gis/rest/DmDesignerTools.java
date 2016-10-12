package com.boco.gis.rest;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.MicrowaveLineSeg;
import com.boco.transnms.common.dto.MicrowaveSystem;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.SeggroupToRes;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;


/**
 * designer工具类，只添加方法，不涉及到调用xrpc服务或入库操作
 * @author Administrator
 *
 */
public class DmDesignerTools {
	
	private static BoActionContext actionContext = null;
	private static Set<String> set = new HashSet<String>();
	public static void setAddRes(String cuid){
		set.add(cuid);
	}
	
	public static void setAddres(String[] cuids){
		if(null != cuids && cuids.length>0){
			for(String cuid : cuids){
				setAddRes(cuid);
			}
		}
	}
	
	public static void setAddres(List<String> cuidList){
		if(null != cuidList && cuidList.size()>0){
			for(String cuid : cuidList){
				setAddRes(cuid);
			}
		}
	}
	
	public static Set<String> getAddRes(){
		return set;
	}
	
	public static void setActionContext(ServiceActionContext ac) {
		if(actionContext == null){
			actionContext = new BoActionContext();
			//ac传递过来的userId就是原系统中的userName
			String userId = ac.getUserId();
			actionContext = new BoActionContext();
			actionContext.setUserName(userId);
		}
	}
	
	public static BoActionContext getActionContext(){
		return actionContext;
	}
	
    public static void setCreatorInfo(String userName,DataObjectList list) {
        if (list != null && list.size() > 0) {
            for (GenericDO dto : list) {
                dto.setAttrValue(Manhle.AttrName.creator, userName);
                dto.setLastModifyTime(new Date());
            }
        }
    }
    
    public static GenericDO getBranchByLineClassName(String className){
    	GenericDO dbo = null;
    	if(className.equals(DuctSeg.CLASS_NAME)){
    		dbo = new DuctBranch();
    	}else if(className.equals(PolewaySeg.CLASS_NAME)){
    		dbo = new PolewayBranch();
    	}else if(className.equals(StonewaySeg.CLASS_NAME)){
    		dbo = new StonewayBranch();
    	}else if(className.equals(WireSeg.CLASS_NAME)){
    		dbo = new WireBranch();
    	}
    	return dbo;
    }
    
    public static GenericDO getSystemByLineClassName(String className){
    	GenericDO dbo = null;
    	if(className.equals(DuctSeg.CLASS_NAME)){
    		dbo = new DuctSystem();
    	}else if(className.equals(PolewaySeg.CLASS_NAME)){
    		dbo = new PolewaySystem();
    	}else if(className.equals(StonewaySeg.CLASS_NAME)){
    		dbo = new StonewaySystem();
    	}else if(className.equals(UpLineSeg.CLASS_NAME)){
    		dbo = new UpLine();
    	}else if(className.equals(HangWallSeg.CLASS_NAME)){
    		dbo = new HangWall();
    	}else if(className.equals(MicrowaveLineSeg.CLASS_NAME)){
    		dbo = new MicrowaveSystem();
    	}else if(className.equals(WireSeg.CLASS_NAME)){
    		dbo = new WireSystem();
    	}
    	return dbo;
    }
    
    public static GenericDO getLineByClassName(String className){
    	GenericDO dbo = null;
    	if(className.equals(DuctSeg.CLASS_NAME)){
    		dbo = new DuctSeg();
    	}else if(className.equals(PolewaySeg.CLASS_NAME)){
    		dbo = new PolewaySeg();
    	}else if(className.equals(StonewaySeg.CLASS_NAME)){
    		dbo = new StonewaySeg();
    	}else if(className.equals(UpLineSeg.CLASS_NAME)){
    		dbo = new UpLineSeg();
    	}else if(className.equals(HangWallSeg.CLASS_NAME)){
    		dbo = new HangWallSeg();
    	}else if(className.equals(MicrowaveLineSeg.CLASS_NAME)){
    		dbo = new MicrowaveLineSeg();
    	}else if(className.equals(WireSeg.CLASS_NAME)){
    		dbo = new WireSeg();
    	}
    	return dbo;
    }
    
	public static GenericDO getPointByClassName(String className){
		GenericDO dbo = null;
		if(className.equals(Manhle.CLASS_NAME)){
			dbo = new Manhle();
		}else if(className.equals(Pole.CLASS_NAME)){
			dbo = new Pole();
		}else if(className.equals(Stone.CLASS_NAME)){
			dbo = new Stone();
		}else if(className.equals(FiberJointBox.CLASS_NAME)){
			dbo = new FiberJointBox();
		}else if(className.equals(FiberDp.CLASS_NAME)){
			dbo = new FiberDp();
		}else if(className.equals(FiberCab.CLASS_NAME)){
			dbo = new FiberCab();
		}else if(className.equals(Inflexion.CLASS_NAME)){
			dbo = new Inflexion();
		}/*else if(className.equals(Site.CLASS_NAME)){
			dbo = new Site();
		}*/else if(className.equals(Accesspoint.CLASS_NAME)){
			dbo = new Accesspoint();
			dbo.setAttrValue(Accesspoint.AttrName.isYjr, 0);
			dbo.setAttrValue(Accesspoint.AttrName.siteCuid, " ");
			dbo.setAttrValue(Accesspoint.AttrName.vpCuid, " ");
			dbo.setAttrValue(Accesspoint.AttrName.vpnCuid, " ");
		}
		return dbo;
	}
    
	public static DataObjectList createSegGroupToReses(DataObjectList segList,String segGroupCuid) {
		DataObjectList segGroupToResList = new DataObjectList();
		for (GenericDO seg : segList) {
			SeggroupToRes sgr = new SeggroupToRes();
			sgr.setCuid();
			sgr.setAttrValue(SeggroupToRes.AttrName.relatedSeggroupCuid,segGroupCuid);
			sgr.setAttrValue(SeggroupToRes.AttrName.relatedResCuid,seg.getCuid());
			sgr.setAttrValue(SeggroupToRes.AttrName.relatedBmclasstypeCuid,seg.getClassName());
			segGroupToResList.add(sgr);
		}
		return segGroupToResList;
	}
	
}
