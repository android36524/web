package com.boco.gis.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import com.boco.common.util.debug.LogHome;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.LineBranch;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class MergeDuctLineBranchsAction {
	
	/**
	 * 承载分支合并
	 * @param mergeBranchsCuid
	 * @param systemCuid
	 * @return
	 */
    public Object saveAllData(HttpServletRequest request,String systemCuid, String firstBranchCuid, String lastBranchCuid) {
    	List<String> mergeBranchsCuid = new ArrayList<String>();
    	mergeBranchsCuid.add(firstBranchCuid);
    	mergeBranchsCuid.add(lastBranchCuid);
    	String className = systemCuid.split("-")[0];
        DataObjectList merge = getMergeDuctlineBranch(mergeBranchsCuid, systemCuid);
//        GDMUtils.setToolbarEnabled(network, true);
        if (merge == null || merge.size() != 2) return null;
        String mergePoint = getMergePoint(merge);//"null"
        if (mergePoint.equals(null)) {
        	return null;
        }
        try {
            if (className.equals(DuctSystem.CLASS_NAME)) {
            	MergeDuctBranchsHandler.doMergeDuctBranch((DuctBranch)merge.get(0), (DuctBranch)merge.get(1));
            } else if (className.equals(PolewaySystem.CLASS_NAME) || className.equals(StonewaySystem.CLASS_NAME)) {
                MergePoleStoneBranchsHandler.unitePolewayBranch(merge, mergePoint, getMergeDuctlineSeg(merge));
            }
        } catch (Exception ex) {
            LogHome.getLog().error("承载分支合并失败：", ex);
            JOptionPane.showMessageDialog(null,"承载分支合并失败。");
//            GDMUtils.setToolbarEnabled(network, true);
            return null;
        }
        GenericDO mergeDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), systemCuid);
        return mergeDto;
    }

    private DataObjectList getMergeDuctlineBranch(List<String> mergeBranchsCuid, String systemCuid) {
        if (mergeBranchsCuid == null || mergeBranchsCuid.size() != 2){
        	JOptionPane.showMessageDialog(null,"选择的分支数不为2,不能合并。");
        	return null;
        }
        DataObjectList allBranchs = null;
        try {
        	allBranchs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getBranchsBySystemCuid, new BoActionContext(), systemCuid);
        } catch (Exception e) {
            LogHome.getLog().error("获取分支失败", e);
        }
        if (allBranchs == null) return null;
        DataObjectList merge = new DataObjectList();
        for (String branchCuid : mergeBranchsCuid) {
            for (GenericDO dto : allBranchs) {
                if (dto instanceof LineBranch && branchCuid.equals(dto.getCuid())) {
                    merge.add(dto);
                }
            }
        }
        if (merge == null || merge.size() != 2) return null;
        List<String> pointCuidList = new ArrayList<String>();
        Map<String, String> pointCuidMap = new HashMap<String, String>();
        for (GenericDO dto : merge) {
            if (!(dto instanceof LineBranch)) continue;
            LineBranch lb = (LineBranch) dto;
            if (!pointCuidMap.containsKey(lb.getOrigPointCuid()))
                pointCuidList.add(lb.getOrigPointCuid());
            if (!pointCuidMap.containsKey(lb.getDestPointCuid()))
                pointCuidList.add(lb.getDestPointCuid());
        }
        String[] pointCuid = new String[pointCuidList.size()];
        for (int i = 0; i < pointCuid.length; i++) {
            pointCuid[i] = pointCuidList.get(i);
        }
        DataObjectList pointList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), pointCuid);
        Map<String, GenericDO> pointMap = new HashMap<String, GenericDO>();
        for (GenericDO point : pointList) {
            pointMap.put(point.getCuid(), point);
        }
        for (GenericDO dto : merge) {
            if (!(dto instanceof LineBranch)) continue;
            LineBranch lb = (LineBranch) dto;
            String origPointCuid = DMHelper.getRelatedCuid(lb.getAttrValue(LineBranch.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(lb.getAttrValue(LineBranch.AttrName.destPointCuid));
            lb.setAttrValue(LineBranch.AttrName.origPointCuid, pointMap.get(origPointCuid));
            lb.setAttrValue(LineBranch.AttrName.destPointCuid, pointMap.get(destPointCuid));
        }
        return merge;
    }
    
    private String getMergePoint(DataObjectList mergeBranch) {
        if (mergeBranch == null || mergeBranch.size() != 2 ||
            !(mergeBranch.get(0) instanceof LineBranch) ||
            !(mergeBranch.get(1) instanceof LineBranch)) return null;
        String mergePointCuid = null;
        String branchCuid1 = mergeBranch.get(0).getCuid();
        String branchCuid2 = mergeBranch.get(0).getCuid();
        GenericDO mergeBranch1 = getDuctManagerBO().getObjByCuid(new BoActionContext(), branchCuid1);
        GenericDO mergeBranch2 = getDuctManagerBO().getObjByCuid(new BoActionContext(), branchCuid2);
        
        LineBranch b0 = (LineBranch) mergeBranch1;
        LineBranch b1 = (LineBranch) mergeBranch2;
        String origB0 = (String) b0.getAttrValue(LineBranch.AttrName.origPointCuid);
        String destB0 = (String) b0.getAttrValue(LineBranch.AttrName.destPointCuid);
        String origB1 = (String) b1.getAttrValue(LineBranch.AttrName.origPointCuid);
        String destB1 = (String) b1.getAttrValue(LineBranch.AttrName.destPointCuid);
        if (origB0.equals(origB1) || origB0.equals(destB1)) {
        	mergePointCuid = origB0;
        } else if (destB0.equals(origB1) || destB0.equals(destB1)) {
        	mergePointCuid = destB0;
        } else {
        	JOptionPane.showMessageDialog(null,"获取要合并掉的点失败。");
        }
        return mergePointCuid;
    }
    
    private DataObjectList getMergeDuctlineSeg(DataObjectList mergeBranch) {
        DataObjectList mergeSeg = new DataObjectList();
        for (GenericDO dto : mergeBranch) {
            if (!(dto instanceof LineBranch)) continue;
            DataObjectList segs = MergeSHUSegsHandler.getSegsInBranch(dto.getCuid(), null);
            mergeSeg.addAll(segs);
        }
        List<String> pointCuidList = new ArrayList<String>();
        Map<String, String> pointCuidMap = new HashMap<String, String>();
        for (GenericDO dto : mergeSeg) {
            if (!(dto instanceof PhysicalJoin)) continue;
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
        DataObjectList pointList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), pointCuid);
        Map<String, GenericDO> pointMap = new HashMap<String, GenericDO>();
        for (GenericDO point : pointList) {
            pointMap.put(point.getCuid(), point);
        }
        for (GenericDO dto : mergeSeg) {
            PhysicalJoin pj = (PhysicalJoin) dto;
            String origPointCuid = DMHelper.getRelatedCuid(pj.getAttrValue(PhysicalJoin.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(pj.getAttrValue(PhysicalJoin.AttrName.destPointCuid));
            pj.setAttrValue(PhysicalJoin.AttrName.origPointCuid, pointMap.get(origPointCuid));
            pj.setAttrValue(PhysicalJoin.AttrName.destPointCuid, pointMap.get(destPointCuid));
        }
        return mergeSeg;
    }
    private IDuctManagerBO getDuctManagerBO() {
        return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
    }
}
