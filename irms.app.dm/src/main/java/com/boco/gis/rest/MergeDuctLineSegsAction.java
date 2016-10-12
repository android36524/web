package com.boco.gis.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jboss.netty.handler.codec.http.HttpResponse;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.CustomEnum;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.SeggroupToRes;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class MergeDuctLineSegsAction {
	
	private  String scene = "";//场景名
	private  String segGroupCuid ="";//单位工程
	
	private IDuctManagerBO getDuctManagerBO() {
		return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
	}

	@SuppressWarnings("all")
	public Map getIsDesignerDatas(String cuid) throws Exception {
		Map map = new HashMap();
		GenericDO dbo = getTnmsObjectByCuid(cuid);
		if (dbo != null) {
			String cid = dbo.getCuid();
			map.put("CUID", cid);
			map.put("ISTNMS", "TRUE");
			return map;
		}
		return map;
	}

	/**
	 * 用于调用现网数据??????
	 * 
	 * @return
	 */
	public GenericDO getTnmsObjectByCuid(String cuid) {
		GenericDO dbo = null;
		try {
			dbo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjByCuid", new BoActionContext(),cuid);
		} catch (Exception e) {
			throw new UserException(e.getMessage());
		}
		return dbo;
	}

	public String saveAllDataRes(HttpServletRequest request,String mergeCuid, String origMergeCuid, String destMergeCuid,String sceneValue,String seggroupCuid){
		if(StringUtils.isNotEmpty(sceneValue)){
			scene = sceneValue;
		}
		if(StringUtils.isNotEmpty(seggroupCuid)){
			segGroupCuid = seggroupCuid;
		}
		return saveAllData(request,mergeCuid,origMergeCuid,destMergeCuid);
	}
	/**
	 * 承载段合并
	 * @param mergeCuid
	 * @param origMergeCuid
	 * @param destMergeCuid
	 * @return
	 */
	public String saveAllData(HttpServletRequest request,String mergeCuid, String origMergeCuid, String destMergeCuid) {
		String result = "";
		GenericDO segGeo = null;
		GenericDO mergeDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), mergeCuid);
		if(mergeDto != null){
			DataObjectList allSegs = getAllSegs(mergeDto.getCuid());
			DataObjectList mergeSegs = getMergeSegs(allSegs, origMergeCuid, destMergeCuid);
			if (mergeSegs.size() < 2) {
				throw new UserException("请选择线设施中的两个不同的点设施进行合并!");
			}
			DataObjectList mergeSegList = mergeSegs;
			DataObjectList mergePoints = getMergePoints(mergeSegs);
			Map<String, GenericDO> mergePointsMap = new HashMap<String, GenericDO>();
			putListToMap(mergePoints, mergePointsMap);
			if (!(mergeDto instanceof DuctBranch)) {
//				if (!showConfirmView()){
//					return null;
//				}
			}
			try {
				if (mergeDto instanceof DuctBranch) {
					segGeo = MergeDuctSegsHandler.doMergeDuctSegs(mergeSegs);
				} else if (mergeDto instanceof PolewayBranch) {
					segGeo = MergePolewaySegsHandler.unitePolewaySeg(mergePointsMap, mergePoints,mergeDto.getCuid(), allSegs);
				} else if (mergeDto instanceof StonewayBranch || mergeDto instanceof UpLine || mergeDto instanceof HangWall) {
					segGeo = MergeSHUSegsHandler.beginuniteseg(mergePoints, mergeDto.getCuid(), allSegs);
				}
				if(segGeo!=null && StringUtils.isNotEmpty(scene)){//勘误场景，存关联关系表
					DataObjectList resList = new DataObjectList();
					resList.add(segGeo);
					createSegGroup(resList,segGroupCuid,request);
				}
			} catch (Exception ex) {
				LogHome.getLog().error("承载段合并失败!", ex);
				throw new UserException("承载段合并失败!");
			}
			//mergeDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), mergeDto.getCuid());
		}
		result = "承载段合并成功！";
		return result;
	}

	private DataObjectList getAllSegs(String relatedCuid) {
		DataObjectList allSegs = getSegsInBranch(relatedCuid);
		List<String> pointCuidList = new ArrayList<String>();
		Map<String, String> pointCuidMap = new HashMap<String, String>();
		for (GenericDO dto : allSegs) {
			if (!(dto instanceof PhysicalJoin))
				continue;
			PhysicalJoin pj = (PhysicalJoin) dto;
			if (!pointCuidMap.containsKey(pj.getOrigPointCuid()))
				pointCuidList.add(pj.getOrigPointCuid());
			if (!pointCuidMap.containsKey(pj.getDestPointCuid()))
				pointCuidList.add(pj.getDestPointCuid());
		}
		String[] pointCuid = new String[pointCuidList.size()];
		for (int i = 0; i < pointCuid.length; i++) {
			pointCuid[i] = pointCuidList.get(i);
		}
		DataObjectList pointList = (DataObjectList) getDuctManagerBO()
				.getObjsByCuid(new BoActionContext(), pointCuid);
		Map<String, GenericDO> pointMap = new HashMap<String, GenericDO>();
		for (GenericDO point : pointList) {
			pointMap.put(point.getCuid(), point);
		}
		for (GenericDO dto : allSegs) {
			PhysicalJoin pj = (PhysicalJoin) dto;
			String origPointCuid = DMHelper.getRelatedCuid(pj
					.getAttrValue(PhysicalJoin.AttrName.origPointCuid));
			String destPointCuid = DMHelper.getRelatedCuid(pj
					.getAttrValue(PhysicalJoin.AttrName.destPointCuid));
			pj.setAttrValue(PhysicalJoin.AttrName.origPointCuid,
					pointMap.get(origPointCuid));
			pj.setAttrValue(PhysicalJoin.AttrName.destPointCuid,
					pointMap.get(destPointCuid));
		}
		return allSegs;
	}

	public static DataObjectList getSegsInBranch(String relatedCuid) {
		try {
			return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					DuctManagerBOHelper.ActionName.getSegsBySystemCuid,
					new BoActionContext(), relatedCuid);
		} catch (Exception ex) {
			LogHome.getLog().error("获取系统段数据失败!", ex);
		}
		return null;
	}

	private DataObjectList getMergeSegs(DataObjectList allSegs, String origMergeCuid, String destMergeCuid) {
		DataObjectList mergeSegs = new DataObjectList();
		if (allSegs != null) {
			String origCuid = origMergeCuid;
			String destCuid = destMergeCuid;
			if (origCuid == null || origCuid.equals(destCuid)) {

				return mergeSegs;
			}
			allSegs.sort("INDEX_IN_BRANCH", true);
			for (int i = 0; i < allSegs.size(); i++) {
				GenericDO dto = allSegs.get(i);
				if (!(dto instanceof PhysicalJoin))
					continue;
				PhysicalJoin pj = (PhysicalJoin) dto;
				String origPointCuid = DMHelper.getRelatedCuid(pj
						.getAttrValue(PhysicalJoin.AttrName.origPointCuid));
				String destPointCuid = DMHelper.getRelatedCuid(pj
						.getAttrValue(PhysicalJoin.AttrName.destPointCuid));
				if (origCuid.equals(origPointCuid)) {
					for (; i < allSegs.size(); i++) {
						if (!(dto instanceof PhysicalJoin))
							continue;
						pj = (PhysicalJoin) allSegs.get(i);
						destPointCuid = DMHelper
								.getRelatedCuid(pj
										.getAttrValue(PhysicalJoin.AttrName.destPointCuid));
						mergeSegs.add(pj);
						if (destCuid.equals(destPointCuid))
							break;
					}
				}
			}
		}
		return mergeSegs;
	}
	
	private DataObjectList getMergePoints(DataObjectList mergeSegs) {
        DataObjectList mergePoints = new DataObjectList();
        Map<String, GenericDO> pointMap = new HashMap<String, GenericDO>();
        for (int i = 0; i < mergeSegs.size(); i++) {
            GenericDO dto = mergeSegs.get(i);
            if (!(dto instanceof PhysicalJoin)) continue;
            PhysicalJoin pj = (PhysicalJoin)dto;
            Object origObj = pj.getAttrValue(PhysicalJoin.AttrName.origPointCuid);
            Object destObj = pj.getAttrValue(PhysicalJoin.AttrName.destPointCuid);
            if (i != 0 &&
                origObj instanceof GenericDO &&
                !pointMap.containsKey(((GenericDO)origObj).getCuid())) {
                mergePoints.add((GenericDO)origObj);
                pointMap.put(((GenericDO)origObj).getCuid(), (GenericDO)origObj);
            }
            if (i != mergeSegs.size() - 1 &&
                destObj instanceof GenericDO &&
                !pointMap.containsKey(((GenericDO)destObj).getCuid())) {
                mergePoints.add((GenericDO)destObj);
                pointMap.put(((GenericDO)destObj).getCuid(), (GenericDO)destObj);
            }
        }
        return mergePoints;
    }
	
    public static void putListToMap(DataObjectList l, Map m) {
        if (l != null) {
            for (int i = 0; i < l.size(); i++) {
                GenericDO g = l.get(i);
                m.put(g.getCuid(), g);
            }
        }
    }
    private void createSegGroup(DataObjectList objList,String relatedSegGroupCuid,HttpServletRequest request) throws Exception{
    	try {
    		if(StringUtils.isNotEmpty(relatedSegGroupCuid)){
    			DataObjectList segGroupToResList = DmDesignerTools.createSegGroupToReses(objList,relatedSegGroupCuid);
    			if(segGroupToResList != null && segGroupToResList.size()>0){
    				for(GenericDO gdo : segGroupToResList){
    					if("erratum".equals(scene)){
    						gdo.setAttrValue(SeggroupToRes.AttrName.projectState, CustomEnum.DMProjectState._designDDM);
    					}
    				}
    				DmDesignerTools.setActionContext(new ServiceActionContext(request));
    				BoActionContext actionContext = DmDesignerTools.getActionContext();
    				getDuctManagerBO().createDMDOs(actionContext, segGroupToResList);
    			}
    		}
		} catch (Exception e) {
			LogHome.getLog().info("创建段落错误!", e);
			throw new UserException(e.getMessage());
		}
    }
}
