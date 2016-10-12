package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.except.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.helper.dm.DuctBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

/**
 * @author lizongyu
 * 查询管线长度
 */
public class DmQueryLengthAction {

	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	public List<Map> getQueryLineLength(String systemCuid){
		List<Map> resourceList = new ArrayList<Map>();
		
		try {
			GenericDO ductDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
			DataObjectList list = new DataObjectList(new GenericDO[] {ductDbo});
			DataObjectList res = getSystems(list);
			
			if ((res == null) || (res.size() <= 0)) {
			    return null;
			}
			
			if(systemCuid.startsWith(UpLine.CLASS_NAME) || systemCuid.startsWith(HangWall.CLASS_NAME)){
				resourceList = initSystemHaveNoBranch(res);
			}else{
				resourceList = initSystem(res);
			}
		} catch (UserException e) {
			e.printStackTrace();
		}
		
		return resourceList;
	}
	
    private List<Map> initSystem(DataObjectList res) {
        //从服务器加载管线分支、管线段、路由点信息
    	List systemList = new ArrayList();
    	Map systemMap = new HashMap();
        for (int i = 0; i < res.size(); i++) {
            //管线系统
            GenericDO gDO = (GenericDO) res.get(i);
            systemMap.put("CUID", gDO.getCuid());
        	systemMap.put("LABEL_CN", gDO.getAttrString(Manhle.AttrName.labelCn));
            DataObjectList branches = (DataObjectList) (List) gDO.getAttrList(DMCacheObjectName.SystemChildren);
            if ((branches == null) || (branches.size() <= 0)) {
                return null;
            }
            List branchList = new ArrayList();
            //管线分支列表
            for (int j = 0; j < branches.size(); j++) {
            	Map branchMap = new HashMap();
                GenericDO branch = (GenericDO) branches.get(j);
                Map map = getSegsAndPoints(branch);
                
                branchMap.put("CUID", branch.getCuid());
                branchMap.put("LABEL_CN", branch.getAttrValue(Manhle.AttrName.labelCn));
                branchMap.put("SEGS", map);
                branchList.add(branchMap);
            }
            systemMap.put("BRANCHS", branchList);
            systemList.add(systemMap);
        }
        return systemList;
    }
    
    private List<Map> initSystemHaveNoBranch(DataObjectList res) {
        //处理没有分支的情况,就是挂墙和引上系统.
        //从服务器加载管线段、路由点信息
    	//res是系统只有一个值
    	List<Map> systemList = new ArrayList<Map>();
    	Map systemMap = new HashMap();
        for (int i = 0; i < res.size(); i++) {
            //管线系统
            GenericDO gDO = (GenericDO) res.get(i);
            //管线分支列表
            Map map = getSegsAndPoints(gDO);
            String cuid = gDO.getCuid();
            String labelCn = gDO.getAttrString(Manhle.AttrName.labelCn);
            systemMap.put("CUID", cuid);
            systemMap.put("LABEL_CN", labelCn);
            systemMap.put("SEGS", map);
            systemList.add(systemMap);
        }
        return systemList;
    }
    
    private Map getSegsAndPoints(GenericDO gdo){
    	 Map map = new HashMap();
    	 DataObjectList segs = (DataObjectList) (List) gdo.getAttrList(DMCacheObjectName.SystemSegChildren);
         if ((segs != null) && (segs.size() > 0)) {
             segs.sort(DMCacheObjectName.IndexInBranch, true);
             List simpleSegs = getSimpleDbo(segs);
             map.put("SEGS", simpleSegs);
             //得到路由点cuid列表
             DataObjectList pointCuidList = getRoutePointBySeg(segs);
             //从服务器得到点设施名称
             DataObjectList points = getPoints(pointCuidList);
             
             List simplePoints = getSimpleDbo(points);
             map.put("POINTS", simplePoints);
         }
         return map;
    }
    
    private List getSimpleDbo(DataObjectList datas){
    	List<Map> simpleDatas = new ArrayList<Map>();
	 	String lonitude = Manhle.AttrName.longitude,
    	latitude = Manhle.AttrName.latitude;
    	if(datas != null && datas.size()>0){
    		for(GenericDO data : datas){
    			Map dboMap = new HashMap();
           	 	String cuid = data.getCuid(),
           	 	className = cuid.split("-")[0],
           	 	labelCn = data.getAttrString(Manhle.AttrName.labelCn);
           	 	dboMap.put("CUID", cuid);
           	 	dboMap.put("LABEL_CN", labelCn);
           	 	boolean pointClassName = DMHelper.isPointClassName(className);
           	 	if(pointClassName){
           	 		dboMap.put("LONGITUDE", data.getAttrValue(lonitude));
           	 		dboMap.put("LATITUDE", data.getAttrValue(latitude));
           	 	}else{
           	 		String origPointCuid = DMHelper.getRelatedCuid(data.getAttrValue(DuctSeg.AttrName.origPointCuid));
           	 		String destPointCuid = DMHelper.getRelatedCuid(data.getAttrValue(DuctSeg.AttrName.destPointCuid));
           	 		dboMap.put("ORIG_POINT_CUID", origPointCuid);
           	 		dboMap.put("DEST_POINT_CUID", destPointCuid);
           	 		dboMap.put("SEG_LENGTH", data.getAttrValue(DuctSeg.AttrName.length));
           	 	}
           	 	simpleDatas.add(dboMap);
            }
    	}
    	return simpleDatas;
    }
    
    private DataObjectList getSystems(DataObjectList list) {
        DataObjectList res = null;
        try {
            res = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getSystems, new BoActionContext(), list);
        } catch (Exception ex) {
//            LogHome.getLog().info("getSystems" + PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.model.dm.DMCacheModel.java8") + ex.getMessage());
        }
        return res;
    }
    
    private DataObjectList getRoutePointBySeg(DataObjectList segs) {
        //得到路由点列表
        String priPointCuid = null;
        String curPointCuid = null;
        String nextPointCuid = null;

        String relatedSegCuid = "RELATED_SEG_CUID";
        DataObjectList list = new DataObjectList();
        if ((segs != null) && (segs.size() > 0)) {
            if (segs.size() < 2) {
                GenericDO gDO = (GenericDO) segs.get(0);
                String OrigPointCuid = DMHelper.getRelatedCuid(gDO.getAttrValue(DMCacheObjectName.origPointCuid));
                String DestPointCuid = DMHelper.getRelatedCuid(gDO.getAttrValue(DMCacheObjectName.destPointCuid));
                GenericDO on = new GenericDO();
                on.setCuid(OrigPointCuid);
                on.setAttrValue(relatedSegCuid, gDO.getCuid());
                list.add(on);

                GenericDO dn = new GenericDO();
                dn.setCuid(DestPointCuid);
                dn.setAttrValue(relatedSegCuid, gDO.getCuid());
                list.add(dn);
            } else {
                for (int i = 0; i < segs.size() - 1; i++) {
                    priPointCuid = null;
                    curPointCuid = null;
                    nextPointCuid = null;
                    GenericDO gDO = (GenericDO) segs.get(i);
                    GenericDO gDOnext = (GenericDO) segs.get(i + 1);
                    String OrigPointCuid = DMHelper.getRelatedCuid(gDO.getAttrValue(DMCacheObjectName.origPointCuid));
                    String DestPointCuid = DMHelper.getRelatedCuid(gDO.getAttrValue(DMCacheObjectName.destPointCuid));
                    String nOrigPointCuid = DMHelper.getRelatedCuid(gDOnext.getAttrValue(DMCacheObjectName.origPointCuid));
                    String nDestPointCuid = DMHelper.getRelatedCuid(gDOnext.getAttrValue(DMCacheObjectName.destPointCuid));
                    if (OrigPointCuid != null && nOrigPointCuid != null && OrigPointCuid.equals(nOrigPointCuid)) {
                        priPointCuid = DestPointCuid;
                        curPointCuid = OrigPointCuid;
                        nextPointCuid = nDestPointCuid;
                    } else if (OrigPointCuid != null && nDestPointCuid != null && OrigPointCuid.equals(nDestPointCuid)) {
                        priPointCuid = DestPointCuid;
                        curPointCuid = OrigPointCuid;
                        nextPointCuid = nOrigPointCuid;
                    } else if (DestPointCuid != null && nOrigPointCuid != null && DestPointCuid.equals(nOrigPointCuid)) {
                        priPointCuid = OrigPointCuid;
                        curPointCuid = DestPointCuid;
                        nextPointCuid = nDestPointCuid;
                    } else if (DestPointCuid != null && nDestPointCuid != null && DestPointCuid.equals(nDestPointCuid)) {
                        priPointCuid = OrigPointCuid;
                        curPointCuid = DestPointCuid;
                        nextPointCuid = nOrigPointCuid;
                    } else if (OrigPointCuid != null && DestPointCuid == null && nOrigPointCuid == null && nDestPointCuid != null) {
                        priPointCuid = OrigPointCuid;
                    }
                    GenericDO g = new GenericDO();
                    if (priPointCuid != null) {
                        g.setCuid(priPointCuid);
                        g.setAttrValue(relatedSegCuid, gDO.getCuid());
                        if (i == segs.size() - 2) {
                            list.add(g);
                            if (curPointCuid != null) {
                                GenericDO c = new GenericDO();
                                c.setCuid(curPointCuid);
                                c.setAttrValue(relatedSegCuid, gDO.getCuid());
                                list.add(c);
                            }
                            if (nextPointCuid != null) {
                                GenericDO n = new GenericDO();
                                n.setCuid(nextPointCuid);
                                n.setAttrValue(relatedSegCuid, gDO.getCuid());
                                list.add(n);
                            }
                        } else {
                            list.add(g);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private DataObjectList getPoints(DataObjectList pointlist) {
        DataObjectList points = null;
        try {
            points = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getPoints,
                new BoActionContext(), pointlist);
        } catch (Exception ex) {
//            LogHome.getLog().info("getSingleSystemsByCuids" + PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.model.dm.DMCacheModel.java8") + ex.getMessage());
        }
        return points;
    }
    
    /**
     * 查询管道系统管道分支
     * @param systemCuid
     * @return
     */
    public List<Map> getDuctBranchByDuctSystemCuid(String systemCuid) {
        //根据管道系统查询得到管线下所有分支
    	List<Map> systemList = new ArrayList<Map>();
        DataObjectList resList=new DataObjectList();
        try{
            if(systemCuid!=null){
                resList=(DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctBranchBOHelper.ActionName.getBranchInfoBySystemCuid,new BoActionContext(),systemCuid);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if(resList.size()>0){
        	for(GenericDO dbo : resList){
        		Map map = new HashMap();
        		String ductSystemName = dbo.getAttrString("DUCT_SYSTEM_NAME");
        		String labelCn = dbo.getAttrString("LABEL_CN");
        		String origPointName = dbo.getAttrString("ORIG_POINT_NAME");
        		String destPointName = dbo.getAttrString("DEST_POINT_NAME");
        		map.put("DUCT_SYSTEM_NAME", ductSystemName);
        		map.put("LABEL_CN", labelCn);
        		map.put("ORIG_POINT_NAME", origPointName);
        		map.put("DEST_POINT_NAME", destPointName);
        		systemList.add(map);
        	}
        }
        return systemList;
    }
}
