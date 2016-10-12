package com.boco.gis.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.graphkit.ext.gis.GraphkitUtils;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.CarryingCable;
import com.boco.transnms.common.dto.DisplaySeg;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.dm.CoordPoint;
import com.boco.transnms.server.bo.helper.dm.DuctDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireRemainBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;

public class MergeSHUSegsHandler {

    //-------------------------------开始段合并功能----------------------------------------
    public static GenericDO beginuniteseg(DataObjectList pointList, String breachcuid, DataObjectList seglist) {
    	
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
    	GenericDO segGdo = null;
        // 获取待合并的杆路段数据
        DataObjectList mergeSegList = new DataObjectList();
        Map<String, GenericDO> deletePointMap = new HashMap<String, GenericDO>();
        for (Object o : pointList) {
            if (!(o instanceof GenericDO)) continue;
            GenericDO point = (GenericDO)o;
            if (!deletePointMap.containsKey(point.getCuid()))
                deletePointMap.put(point.getCuid(), point);
        }
        for (GenericDO dto : seglist) {
            if (!(dto instanceof PhysicalJoin)) continue;
            PhysicalJoin pj = (PhysicalJoin)dto;
            String origPointCuid = DMHelper.getRelatedCuid(pj.getAttrValue(PhysicalJoin.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(pj.getAttrValue(PhysicalJoin.AttrName.destPointCuid));
            if (deletePointMap.containsKey(origPointCuid) || deletePointMap.containsKey(destPointCuid))
                mergeSegList.add(dto);
        }
        mergeSegList.sort("INDEX_IN_BRANCH", true);

        Map map = null;
        ArrayList<String> pointCuidList = new ArrayList<String>();
        DataObjectList modifySegList = new DataObjectList();
        DataObjectList modifywire2ductlineList = new DataObjectList();
        DataObjectList deleteSegList = new DataObjectList();
        DataObjectList savewire2ductlineList = new DataObjectList();
        for (int i = 0; i < pointList.size(); i++) {
            Map<String, GenericDO> segmap = new HashMap<String, GenericDO>();
            for (int ss = 0; ss < seglist.size(); ss++) {
                String origcuid = null;
                String destcuid = null;
                if (seglist.get(ss).getAttrValue(PolewaySeg.AttrName.origPointCuid) instanceof String) {
                    origcuid = (String) seglist.get(ss).getAttrValue(PolewaySeg.AttrName.origPointCuid);
                    segmap.put(origcuid, seglist.get(ss));
                } else if (seglist.get(ss).getAttrValue(PolewaySeg.AttrName.origPointCuid) instanceof GenericDO) {
                    GenericDO origgdo = (GenericDO) seglist.get(ss).getAttrValue(PolewaySeg.AttrName.origPointCuid);
                    segmap.put(origgdo.getCuid(), seglist.get(ss));
                }

                if (seglist.get(ss).getAttrValue(PolewaySeg.AttrName.destPointCuid) instanceof String) {
                    destcuid = (String) seglist.get(ss).getAttrValue(PolewaySeg.AttrName.destPointCuid);
                    segmap.put(destcuid, seglist.get(ss));
                } else if (seglist.get(ss).getAttrValue(PolewaySeg.AttrName.destPointCuid) instanceof GenericDO) {
                    GenericDO destgdo = (GenericDO) seglist.get(ss).getAttrValue(PolewaySeg.AttrName.destPointCuid);
                    segmap.put(destgdo.getCuid(), seglist.get(ss));
                }
            }

            GenericDO gdo = null;
            if (pointList.get(i) instanceof GenericDO) {
                gdo = (GenericDO) pointList.get(i);
            }
            String cuid = gdo.getCuid();
            if (segmap.get(cuid) != null) {
                GenericDO node = (GenericDO) segmap.get(cuid);
                GenericDO origgdo = (GenericDO) node.getAttrValue(PolewaySeg.AttrName.origPointCuid);
                GenericDO destgdo = (GenericDO) node.getAttrValue(PolewaySeg.AttrName.destPointCuid);
                long indexinbranch = (Long) node.getAttrValue(PolewaySeg.AttrName.indexInBranch);
                if (cuid.equals(origgdo.getCuid())) {
                    if (indexinbranch == 1) {

                    } else {
                        indexinbranch = indexinbranch - 1;
                    }
                }
                DataObjectList unitesegList = getuniteSeg(seglist, indexinbranch); //得到合并段并且排序
                if (unitesegList.size() == 1) {
                	throw new UserException("没有要合并的段数据!");
                }

                map = beginUniteHangWallorUplineSeg(unitesegList, null);
                if (map != null) {
                    DataObjectList deletewire2ductlineList1 = (DataObjectList) map.get("deletewire2ductline");
                    DataObjectList modifySegList1 = (DataObjectList) map.get("modifySegList");
                    DataObjectList modifywire2ductlineList1 = (DataObjectList) map.get("modifywire2ductline");
                    DataObjectList deleteSegList1 = (DataObjectList) map.get("deleteSegList");

                    GenericDO segdestpoint = (GenericDO) deleteSegList1.get(0).getAttrValue(PolewaySeg.AttrName.destPointCuid);
                    modifySegList1.get(0).setAttrValue(PolewaySeg.AttrName.destPointCuid, segdestpoint);
                    GenericDO segorigpoint = (GenericDO) modifySegList1.get(0).getAttrValue(PolewaySeg.AttrName.origPointCuid);
                    String origname = (String) segorigpoint.getAttrValue("LABEL_CN");
                    String destname = (String) segdestpoint.getAttrValue("LABEL_CN");
                    if (origname != null && destname != null) {

                    }
                    deletewire2ductlineList1.addAll(modifywire2ductlineList);
                    modifySegList.addAll(modifySegList1);
                    modifywire2ductlineList.addAll(modifywire2ductlineList1);
                    deleteSegList.addAll(deleteSegList1);
                    //给合并段更改起始点,并且把名字复制好
                    deleteSegList.sort(PolewaySeg.AttrName.indexInBranch, true);
                    if (i == 0) {
                        savewire2ductlineList.addAll(modifywire2ductlineList1);
                    }
                } else {
                    return null;
                }
            }
            pointCuidList.add(cuid);
        }

        if (deleteSegList.get(0).getAttrValue(PolewaySeg.AttrName.destPointCuid) instanceof GenericDO) {
            GenericDO segdestpoint = (GenericDO) deleteSegList.get(deleteSegList.size() - 1).getAttrValue(PolewaySeg.AttrName.destPointCuid);
            modifySegList.get(0).setAttrValue(PolewaySeg.AttrName.destPointCuid, segdestpoint.getCuid());
            GenericDO segorigpoint = (GenericDO) modifySegList.get(0).getAttrValue(PolewaySeg.AttrName.origPointCuid);
            String name2 = (String) segdestpoint.getAttrValue(PolewaySeg.AttrName.labelCn);
            String name1 = (String) segorigpoint.getAttrValue(PolewaySeg.AttrName.labelCn);
            modifySegList.get(0).setAttrValue(PolewaySeg.AttrName.labelCn, name1 + "--" + name2);
            modifySegList.get(0).setAttrValue("DESTOBJECT", segdestpoint);
            deleteSegList.removeObjectByCuid(modifySegList.get(0).getCuid());
        } else {
            String segdestpoint = (String) deleteSegList.get(deleteSegList.size() - 1).getAttrValue(PolewaySeg.AttrName.destPointCuid);
            modifySegList.get(0).setAttrValue(PolewaySeg.AttrName.destPointCuid, segdestpoint);
        }

        GenericDO modifyploewayseg = modifySegList.get(0);
        String origPointCuid =DMHelper.getRelatedCuid( modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid));
        String destPointCuid =DMHelper.getRelatedCuid( modifyploewayseg.getAttrValue(PolewaySeg.AttrName.destPointCuid));
        GenericDO origgdo = null;
        GenericDO destgdo = null;
        try {
        	
        	origgdo = getObjectByCuid(origPointCuid);
        	destgdo = getObjectByCuid(destPointCuid);
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
        double length = 0;
        CoordPoint origCoor = getCoordinate(origgdo);
        CoordPoint destCoor = getCoordinate(destgdo);
        length = Math.abs( getDistanceByCoor(origCoor, destCoor));
        modifyploewayseg.setAttrValue(PolewaySeg.AttrName.length, length);
        if (destgdo != null) {
            modifyploewayseg.setAttrValue(PolewaySeg.AttrName.destPointCuid, destgdo);
            if (modifywire2ductlineList != null && modifywire2ductlineList.size() > 0) {
                modifywire2ductlineList.get(0).setAttrValue(WireToDuctline.AttrName.endPointCuid, destgdo.getCuid());
            }
            modifyploewayseg.setAttrValue(PolewaySeg.AttrName.labelCn, origgdo.getAttrValue("LABEL_CN") + "--" + destgdo.getAttrValue("LABEL_CN"));
        }

        //处理光缆预留
        String origcuid = "";
        String origsitelabel = "";
        if (modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid) instanceof String) {
            origcuid = (String) modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid);
        } else {
            GenericDO pointgdo = (GenericDO) modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid);
            origcuid = pointgdo.getCuid();
            origsitelabel = pointgdo.getAttrString(GenericDO.AttrName.labelCn);
        }
        pointCuidList.add(origcuid);
        String pointsql = "";
        for (int i = 0; i < pointCuidList.size(); i++) {
            pointsql = pointsql + "'" + pointCuidList.get(i) + "',";
        }
        pointsql = "(" + pointsql.substring(0, pointsql.length() - 1) + ")";
        String wireRemainsql = WireRemain.AttrName.relatedLocationCuid + " in " + pointsql;
        wireRemainsql = wireRemainsql + " AND " + WireRemain.AttrName.relatedWireSegCuid + " in ";
        DataObjectList wireSegRemain = null;

        String wireSegcuidsql = "";
        if (modifywire2ductlineList != null && modifywire2ductlineList.size() > 0) {
            GenericDO gdo1 = modifywire2ductlineList.get(modifywire2ductlineList.size() - 1);
            String endpointcuid = (String) gdo1.getAttrValue(WireToDuctline.AttrName.endPointCuid);
            gdo1.setAttrValue(WireToDuctline.AttrName.endPointCuid, endpointcuid);
            for (int n = 0; n < savewire2ductlineList.size(); n++) {
                GenericDO gdo = savewire2ductlineList.get(n);
                String wireSegcuid = (String) gdo.getAttrValue(WireToDuctline.AttrName.wireSegCuid);
                wireSegcuidsql = wireSegcuidsql + "'" + wireSegcuid + "',";
            }
            wireRemainsql = wireRemainsql + " (" + wireSegcuidsql.substring(0, wireSegcuidsql.length() - 1) + ")";
            try {
                wireSegRemain = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.getWireRemainBySql,
                    actionContext, wireRemainsql);
            } catch (Exception ex) {
            }
        }
        DataObjectList wiresegremainlist = new DataObjectList();
        DataObjectList deletewiresegremainlist = new DataObjectList();
        double remainlength = 0;
        for (int n = 0; n < savewire2ductlineList.size(); n++) {
            GenericDO gdo = savewire2ductlineList.get(n);
            String wireSegcuid = (String) gdo.getAttrValue(WireToDuctline.AttrName.wireSegCuid);
            List<GenericDO> wiresegreamlist = wireSegRemain.getObjectByAttr(WireRemain.AttrName.relatedWireSegCuid, wireSegcuid);
            Map<String, GenericDO> remainmap = new HashMap<String, GenericDO>();
            for (int s = 0; s < wiresegreamlist.size(); s++) {
                GenericDO remaingdo = wiresegreamlist.get(s);
                remainlength = remainlength + remaingdo.getAttrDouble(WireRemain.AttrName.remainLength);
                remainmap.put(remaingdo.getAttrString(WireRemain.AttrName.relatedLocationCuid), remaingdo);
                deletewiresegremainlist.add(remaingdo);
            }
        }

        if (wireSegRemain != null && wireSegRemain.size() > 0) {
            GenericDO remgdo = wireSegRemain.get(0);
            remgdo.setAttrValue(WireRemain.AttrName.remainLength, remainlength);
            remgdo.setAttrValue(WireRemain.AttrName.relatedLocationCuid, origcuid);
            remgdo.setAttrValue(WireRemain.AttrName.labelCn, origsitelabel);

            wiresegremainlist.add(remgdo);
        }

        if (deletewiresegremainlist != null && deletewiresegremainlist.size() > 0) {
            String sentence = "";
            double alllength = 0;
            for (int i = 0; i < deletewiresegremainlist.size(); i++) {
                GenericDO gdo = deletewiresegremainlist.get(i);
                String labelcn = (String) gdo.getAttrValue(GenericDO.AttrName.labelCn);
                double lengthwireremain = gdo.getAttrDouble(WireRemain.AttrName.remainLength);
                alllength = alllength + lengthwireremain;
                sentence = labelcn + "的长度:" + lengthwireremain + ",";
            }
            sentence = sentence.substring(0, sentence.length() - 1) + "名称为 "+ origsitelabel + "的长度:" + remainlength;
           // JOptionPane.showMessageDialog(null,sentence);
        }
        //光缆预留完成
        try {
        	GenericDO modifySeg = modifySegList.get(0);
            //先把预留信息全部删除在作存储
            BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.deleteWireRemains, actionContext, deletewiresegremainlist);
            
            //添加光缆预留
            BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.addWireRemains, actionContext, wiresegremainlist);
            
            //调用SplitSegHandler同一个的光缆敷设处理算法
            DataObjectList modifyWire2Ductlines = deleteWire2Ductlines(modifySeg, deleteSegList, null);
            
            //删除标石路由段信息
            if (breachcuid.contains(StonewayBranch.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(StonewaySegBOHelper.ActionName.deleteStonewaySegs, actionContext, deleteSegList);
            } else if (breachcuid.contains(UpLine.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(UpLineSegBOHelper.ActionName.deleteUpLineSegs, actionContext, deleteSegList);
            } else if (breachcuid.contains(HangWall.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(HangWallSegBOHelper.ActionName.deleteHangWallSegs, actionContext, deleteSegList);
            }
            deleteSegList.get(deleteSegList.size() - 1).convAllObjAttrToCuid();
            //修改标石路由段信息
            if (breachcuid.contains(StonewayBranch.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                	StonewaySegBOHelper.ActionName.modfiyStonewaySegindexByStartSeg,actionContext,
                    new Long(0 - modifySegList.size()), (StonewaySeg) deleteSegList.get(deleteSegList.size() - 1));
                
                segGdo = (GenericDO)BoCmdFactory.getInstance().execBoCmd(StonewaySegBOHelper.ActionName.modifyStonewaySeg, actionContext, modifySegList.get(0));
            } else if (breachcuid.contains(UpLine.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                    UpLineSegBOHelper.ActionName.modfiyUplineSegindexByStartSeg, actionContext,
                    new Long(0 - modifySegList.size()), (UpLineSeg) deleteSegList.get(deleteSegList.size() - 1));
                
                DataObjectList segGdos =(DataObjectList)BoCmdFactory.getInstance().execBoCmd(UpLineSegBOHelper.ActionName.modifyUpLineSegs, actionContext, modifySegList);
                segGdo = segGdos.get(0);
            } else if (breachcuid.contains(HangWall.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                    HangWallSegBOHelper.ActionName.modifyHangwallSegIndexByStartSeg, actionContext,
                    new Long(0 - modifySegList.size()), (HangWallSeg) deleteSegList.get(deleteSegList.size() - 1));
                
                DataObjectList segGdos = (DataObjectList)BoCmdFactory.getInstance().execBoCmd(
                    HangWallSegBOHelper.ActionName.modifyHangWallSegs, actionContext, modifySegList);//暂时不做处理,这里应该是 支取第一个
                segGdo = segGdos.get(0);
            }
            //调用SplitSegHandler同一个的光缆敷设处理算法
            addModifyWire2Ductline(modifySeg, modifyWire2Ductlines,  modifySegList.size(), null);
            refreshSystemsByW2ds(modifyWire2Ductlines);
           // mergeDisplaySegs(mergeSegList);
            //得到分支下所有的段
            //修改杆路段后入库
            for (int i = 0; i < deleteSegList.size(); i++) {
                String cuid = deleteSegList.get(i).getCuid();
                seglist.removeObjectByAttr("CUID", cuid);
            }
            if (modifySegList.get(0).getAttrValue("DESTOBJECT") != null) {
                GenericDO gdo = (GenericDO) modifySegList.get(0).getAttrValue("DESTOBJECT");
                if (seglist.getObjectByAttr("CUID", modifySegList.get(0).getCuid()) != null) {
                    java.util.List segList = (List) seglist.getObjectByAttr("CUID", modifySegList.get(0).getCuid());
                    GenericDO seggdo = (GenericDO) segList.get(0);
                    seggdo.setAttrValue(PolewaySeg.AttrName.destPointCuid, gdo);
                }
            }
            seglist.sort(PolewaySeg.AttrName.indexInBranch, true);
            for (int i = 0; i < seglist.size(); i++) {
                seglist.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch, new Long(i + 1));
            }
        } catch (Exception ex1) {
            LogHome.getLog().error("调用方法不正确，合并失败。", ex1);
        }
        refreshSystemsBySeg(seglist);
        return segGdo;
    }
    
    /**
     * 得到挂墙,引上,标石路由的wiretoductline合并
     * @param unitesegList DataObjectList
     * @param request TODO
     * @return Map
     */
    public static Map<String, DataObjectList> beginUniteHangWallorUplineSeg(DataObjectList unitesegList, HttpServletRequest request) {
        
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
    	DataObjectList deleteSegList = new DataObjectList();
        DataObjectList deletewire2ductlineList = new DataObjectList();
        DataObjectList modifySegList = new DataObjectList();
        DataObjectList modifywire2ductlineList = new DataObjectList();
        String seg1name = "";
        String seg2name = "";
        DataObjectList wireToDuctlinelist = new DataObjectList();
        if (unitesegList.size() > 1) {
            GenericDO seg1 = unitesegList.get(0);
            GenericDO seg2 = unitesegList.get(1);
            String seg1cuid = seg1.getCuid();
            String seg2cuid = seg2.getCuid();
            DataObjectList carrycablelist1 = new DataObjectList();
            DataObjectList carrycablelist2 = new DataObjectList();
            carrycablelist1.add(seg1);
            carrycablelist2.add(seg2);
            seg1name = seg1.getAttrString("LABEL_CN");
            seg2name = seg2.getAttrString("LABEL_CN");
            String carrycablesql = WireToDuctline.AttrName.ductlineCuid + " ='" + seg1cuid + "' or " + WireToDuctline.AttrName.ductlineCuid + " ='" + seg2cuid + "'";
            try {
                wireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, carrycablesql);
            } catch (Exception ex) {
            	LogHome.getLog().error("获取敷设信息失败.", ex);
            }
            if (getyesornoUniteCarrycable(carrycablelist1, carrycablelist2, wireToDuctlinelist, seg1name, seg2name)) {
                for (int i = 0; i < carrycablelist1.size(); i++) {
                    GenericDO gdo = carrycablelist1.get(i);
                    GenericDO gdo2 = carrycablelist2.get(i);
                    String destpointcuid = null;
                    if (gdo2.getAttrValue(UpLineSeg.AttrName.destPointCuid) instanceof GenericDO) {
                        destpointcuid = ((GenericDO) gdo2.getAttrValue(UpLineSeg.AttrName.destPointCuid)).getCuid();
                    } else {
                        destpointcuid = (String) gdo2.getAttrValue(UpLineSeg.AttrName.destPointCuid);
                    }
                    double gdolength = 0;
                    double gdo2length = 0;
                    if (gdo.getAttrValue(UpLineSeg.AttrName.length) != null) {
                        gdolength = gdo.getAttrDouble(UpLineSeg.AttrName.length);
                    }
                    if (gdo2.getAttrValue(UpLineSeg.AttrName.length) != null) {
                        gdo2length = gdo2.getAttrDouble(UpLineSeg.AttrName.length);
                    }
                    gdo.setAttrValue(UpLineSeg.AttrName.length, gdolength + gdo2length);
                    gdo.setAttrValue(UpLineSeg.AttrName.destPointCuid, destpointcuid);
                    modifySegList.add(gdo);
                    deleteSegList.add(gdo2);
                    java.util.List<GenericDO> gdo1list = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.ductlineCuid, gdo.getCuid());
                    java.util.List<GenericDO> gdo2list = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.ductlineCuid, gdo2.getCuid());
                    if (gdo1list.size() > 0) {
                        for (int s = 0; s < gdo1list.size(); s++) {
                            GenericDO wiretoductline1 = gdo1list.get(s);
                            GenericDO wiretoductline2 = gdo2list.get(s);
                            String endpointcuid = (String) wiretoductline2.getAttrValue(WireToDuctline.AttrName.endPointCuid);
                            wiretoductline1.setAttrValue(WireToDuctline.AttrName.endPointCuid, endpointcuid);
                            modifywire2ductlineList.add(wiretoductline1);
                            deletewire2ductlineList.add(wiretoductline2);
                            deletewire2ductlineList.add(wiretoductline1);
                        }
                    }
                }
            } else {
                return null;
            }
        }
        Map<String, DataObjectList> remap = new HashMap<String, DataObjectList>();
        remap.put("deletewire2ductline", deletewire2ductlineList);
        remap.put("modifySegList", modifySegList);
        remap.put("modifywire2ductline", modifywire2ductlineList);
        remap.put("deleteSegList", deleteSegList);
        return remap;
    }
    
    /**
     * 得到合并段并且排序所有段信息//顺便也把合并方法新到这个方法中了
     *
     * @param seglist DataObjectList
     * @param indexinbranch long
     * @return DataObjectList
     */
    public static DataObjectList getuniteSeg(DataObjectList seglist, long indexinbranch) {
        seglist.sort(PolewaySeg.AttrName.indexInBranch, true);
         DataObjectList unitesegList = new DataObjectList();
        if(indexinbranch==seglist.size()){
            java.util.List<GenericDO> gdo = seglist.getObjectByAttr(PolewaySeg.AttrName.indexInBranch, indexinbranch-1);
            java.util.List<GenericDO> gdo1 = seglist.getObjectByAttr(PolewaySeg.AttrName.indexInBranch, indexinbranch);
            unitesegList.addAll(gdo);
            unitesegList.addAll(gdo1);
        }else{
            java.util.List<GenericDO> gdo = seglist.getObjectByAttr(PolewaySeg.AttrName.indexInBranch, indexinbranch);
            java.util.List<GenericDO> gdo1 = seglist.getObjectByAttr(PolewaySeg.AttrName.indexInBranch, indexinbranch + 1);
            unitesegList.addAll(gdo);
            unitesegList.addAll(gdo1);
        }
        return unitesegList;
    }
    
    public static GenericDO getObjectByCuid(String branchCuid) {
        GenericDO dbo = null;
        DataObjectList resList = getObjectsByCuids(new String[] {branchCuid}, null);
        if (resList != null && resList.size() > 0) {
            dbo = resList.get(0);
        }
        return dbo;
    }
    public static DataObjectList getObjectsByCuids(String[] cuids, HttpServletRequest request) {
    	
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
        try {
            DataObjectList resList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getObjsByCuid,
                actionContext, cuids);
            DataObjectList dataList = new DataObjectList();
            for (GenericDO gdo : resList) {
                if (gdo == null) {
                    continue;
                }
                if (gdo.getClass() == GenericDO.class) {
                    GenericDO cloneDbo = gdo.createInstanceByClassName();
                    gdo.copyTo(cloneDbo);
                    dataList.add(cloneDbo);
                } else {
                    dataList.add(gdo);
                }
            }
            return dataList;
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
        return null;
    }
    
    private static CoordPoint getCoordinate(GenericDO point) {
    	double longitude = 0.0;
        double latitude = 0.0;
        if (point != null) {
            Object lonObj = point.getAllAttr().get("LONGITUDE");
            Object latObj = point.getAllAttr().get("LATITUDE");
            if (lonObj != null && lonObj instanceof Double) {
                longitude = (Double) lonObj;
            }
            if (latObj != null && latObj instanceof Double) {
                latitude = (Double) latObj;
            }
        }
        return new CoordPoint(longitude, latitude);
    }
    
    private static double getDistanceByCoor(CoordPoint orig, CoordPoint dest) {
        double distance = 0;
        if (DMHelper.isCoordAvailable(orig.getY(), orig.getX()) &&
            DMHelper.isCoordAvailable(dest.getY(), dest.getX())) {
            distance = GraphkitUtils.getDistance(orig.getX(), orig.getY(), dest.getX(), dest.getY());
            DecimalFormat formatter = new DecimalFormat(".00");
            String temp = formatter.format(distance);
            distance = Double.parseDouble(temp);
        }
        return distance;
    }
    
    /**
     * 合并管线段的时候调用,必须要求是都合并到第一段上的情况才可以使用
     * 删除 段上经过的敷设信息,
     * 并且已经将要修改的段对应的敷设段删除并返回.目的是断开所有光缆和管线的关联,在所有信息都入库之后,在添加进去
     * @param modifySeg
     * @param deleteSegList
     * @param request TODO
     * @return
     */
	public static DataObjectList deleteWire2Ductlines(GenericDO modifySeg, DataObjectList deleteSegList, HttpServletRequest request) {
		
		DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
		
		DataObjectList modifyWireToDuctlinelist = null;
		try {
			String currentSystemCuid = DMHelper.getRelatedCuid(modifySeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
			String currentBranchCuid = DMHelper.getRelatedCuid(modifySeg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));

			// 如果currentBranchCuid 为空 ,引上或者挂墙情况,不添加到sql中,该条sql 只是为了优化效率
			String systemSql = WireToDuctline.AttrName.lineSystemCuid + " ='" + currentSystemCuid + "' and "
					+ (currentBranchCuid == null ? "" : (WireToDuctline.AttrName.lineBranchCuid + " ='" + currentBranchCuid + "' and "));

			String unitesegsql = "";
			for (int i = 0; i < deleteSegList.size(); i++) {
				if (unitesegsql.equals("")) {
					unitesegsql = WireToDuctline.AttrName.lineSegCuid + " ='" + deleteSegList.get(i).getCuid() + "'";
				} else {
					unitesegsql = unitesegsql + " or " + WireToDuctline.AttrName.lineSegCuid + " ='" + deleteSegList.get(i).getCuid() + "'";
				}
			}
			if (!unitesegsql.equals("")) {
				try {
					DataObjectList deleteWireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
									WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, systemSql + unitesegsql);
					BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.deleteWireToDuctlines, actionContext, deleteWireToDuctlinelist);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			unitesegsql = "";
			unitesegsql = WireToDuctline.AttrName.lineSegCuid + " ='" + modifySeg.getCuid() + "'";
			if (!unitesegsql.equals("")) {
				try {
					modifyWireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
									actionContext, systemSql + unitesegsql);
					BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.deleteWireToDuctlines,
									actionContext, modifyWireToDuctlinelist);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modifyWireToDuctlinelist;
	}
	
	/**
     *  合并管线段的时候调用,必须要求是都合并到第一段上的情况才可以使用
     * 将已经删除的敷设信息 重新添加上,并且修改相应的敷设信息
     * @param modifySeg 修改的段,必须要求是都合并到第一段上的情况
	 * @param modifyw2dList
	 * @param deletePointCount
	 * @param request TODO
     */
    public static void addModifyWire2Ductline(GenericDO modifySeg,DataObjectList modifyw2dList,int deletePointCount, HttpServletRequest request){
    	
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
    	String destPointCuid=DMHelper.getRelatedCuid(modifySeg.getAttrValue(DuctSeg.AttrName.destPointCuid));
    	String origPointCuid=DMHelper.getRelatedCuid(modifySeg.getAttrValue(DuctSeg.AttrName.origPointCuid));
        if (modifyw2dList!=null) {
        	try {
				DataObjectList modifyploewayseglist=new DataObjectList();
				for(GenericDO gdo :modifyw2dList){
					if(gdo instanceof WireToDuctline){
						WireToDuctline wtd=(WireToDuctline) gdo;
						wtd.setEndPointCuid(destPointCuid);
						wtd.setDisPointCuid(origPointCuid);
						if (wtd.getDirection() == 1) {
							
					    } else if (wtd.getDirection() == 2) {
					    	wtd.setIndexInRoute(wtd.getIndexInRoute()-deletePointCount);
					    }
						modifyploewayseglist.add(wtd);
					}
				}
				BoCmdFactory.getInstance().execBoCmd(
						WireToDuctLineBOHelper.ActionName.addWireToDuctlines, actionContext, modifyploewayseglist);

				for (GenericDO gdo : modifyploewayseglist) {
					BoCmdFactory.getInstance().execBoCmd("IWireToDuctLineBO.modfiyWireToDuctlineIndexs",
							actionContext, (long) (0-deletePointCount), gdo); // 修改后续的序号
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    
    public static void refreshSystemsByW2ds(DataObjectList w2dList){
    	if(w2dList!=null&&w2dList.size()>0){
    		Map<String, String> wireSegCuidMap = new HashMap<String, String>();
            for (GenericDO dto : w2dList) {
                if (!(dto instanceof WireToDuctline)) continue;
                Object wireSegObj = dto.getAttrValue(WireToDuctline.AttrName.wireSystemCuid);
                String wireSegCuid = DMHelper.getRelatedCuid(wireSegObj);
                if (!wireSegCuidMap.containsKey(wireSegCuid)) {
                    wireSegCuidMap.put(wireSegCuid, wireSegCuid);
                }
            }
            DataObjectList dlist =new DataObjectList();
         
            for (String cuid : wireSegCuidMap.keySet()) {
           	 GenericDO ductSystem = new GenericDO();
           	ductSystem.setCuid(cuid);
            dlist.add(ductSystem);
            }
//         	DMGisUtils.getDmMap().refreshSystems(dlist);
//         	DMCacheModel.getInstance().refreshSystems(dlist); //????
        }
    }
    public static void refreshSystemsBySeg(DataObjectList list){
    	if(list.size()>0){
        	DataObjectList dlist =new DataObjectList();
        	GenericDO ductSystem = new GenericDO();
            ductSystem.setCuid(DMHelper.getRelatedCuid(list.get(0).getAttrValue(DuctSeg.AttrName.relatedSystemCuid)));
            dlist.add(ductSystem);
//        	DMGisUtils.getDmMap().refreshSystems(dlist);
        }
    }
    
    public static void mergeDisplaySegs(DataObjectList mergeSegs, HttpServletRequest request) throws Exception{
        
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
    	PhysicalJoin origPJ = (PhysicalJoin)mergeSegs.get(0);
        PhysicalJoin destPJ = (PhysicalJoin)mergeSegs.get(mergeSegs.size() - 1);
        DataObjectList oldDisSegList = getOldDisplaySegsInBranch(origPJ);
        DataObjectList segList = getSegsInBranch(origPJ);
        oldDisSegList.sort(DisplaySeg.AttrName.indexInBranch, true);
        segList.sort("INDEX_IN_BRANCH", true);
        Map<String, String> oldDisPointMap = new HashMap<String, String>();
        for (GenericDO dto : oldDisSegList) {
            if (dto instanceof DisplaySeg) {
                DisplaySeg oldDisSeg = (DisplaySeg)dto;
                oldDisPointMap.put(oldDisSeg.getOrigPointCuid(), oldDisSeg.getOrigPointCuid());
                oldDisPointMap.put(oldDisSeg.getDestPointCuid(), oldDisSeg.getDestPointCuid());
            }
        }
        List<String> realPointList = new ArrayList<String>();
        if(segList !=null && segList.size() > 0){
        	for (int i = 0; i < segList.size(); i++) {
                if (!(segList.get(i) instanceof PhysicalJoin)) continue;
                PhysicalJoin physicalJoin = (PhysicalJoin)segList.get(i);
                if (i == 0) realPointList.add(physicalJoin.getOrigPointCuid());
                realPointList.add(physicalJoin.getDestPointCuid());
            }
        }
        
        String preDisPointCuid = null;
        String offDisPointCuid = null;
        String origPJCuid = DMHelper.getRelatedCuid(origPJ.getAttrValue(PhysicalJoin.AttrName.origPointCuid));
        String destPJCuid = DMHelper.getRelatedCuid(destPJ.getAttrValue(PhysicalJoin.AttrName.destPointCuid));
        if(realPointList != null && realPointList.size() > 0){
        	for (int i = realPointList.size() - 1; i > -1; i--) {
                if (!realPointList.get(i).equals(origPJCuid)) continue;
                if (oldDisPointMap.containsKey(realPointList.get(i))) {
                    preDisPointCuid = oldDisPointMap.get(realPointList.get(i));
                    break;
                }
            }
            for (int i = 0; i < realPointList.size(); i ++) {
                if (!realPointList.get(i).equals(destPJCuid)) continue;
                if (oldDisPointMap.containsKey(realPointList.get(i))) {
                    offDisPointCuid = oldDisPointMap.get(realPointList.get(i));
                    break;
                }
            }
        }
        
        DataObjectList delDisSegList = new DataObjectList();
        DataObjectList modDisSegList = new DataObjectList();
        long preIndex = 0L;
        if(oldDisSegList != null && oldDisSegList.size() > 0){
            for (int i = 0; i < oldDisSegList.size(); i++) {
                if (!(oldDisSegList.get(i) instanceof DisplaySeg)) continue;
                DisplaySeg disSeg = (DisplaySeg)oldDisSegList.get(i);
                if (disSeg.getOrigPointCuid().equals(preDisPointCuid)) {
                    preIndex = disSeg.getIndexInBranch() - 1;
                    for (; i < oldDisSegList.size(); i++) {
                        if (!(oldDisSegList.get(i) instanceof DisplaySeg)) continue;
                        disSeg = (DisplaySeg)oldDisSegList.get(i);
                        delDisSegList.add(disSeg);
                        if (disSeg.getDestPointCuid().equals(offDisPointCuid)) {
                            for (i++; i < oldDisSegList.size(); i++) {
                                if (!(oldDisSegList.get(i) instanceof DisplaySeg)) continue;
                                modDisSegList.add(oldDisSegList.get(i));
                            }
                        }
                    }
                }
            }
        }
        List<String> disPointCuidList = new ArrayList<String>();
        disPointCuidList.add(preDisPointCuid);
        disPointCuidList.add(offDisPointCuid);
        DisplaySeg templateDisSeg = new DisplaySeg();
        if(delDisSegList != null && delDisSegList.size() > 0){
        	templateDisSeg = (DisplaySeg)delDisSegList.get(0);
            templateDisSeg.clearUnknowAttrs();
            templateDisSeg.convAllObjAttrToCuid();
        }
        
        DataObjectList newDisSegList = new DataObjectList();
        if(disPointCuidList != null && disPointCuidList.size() > 0){
        	for (int i = 0; i < disPointCuidList.size() - 1; i++) {
                DisplaySeg newDisSeg = (DisplaySeg) templateDisSeg.cloneByClassName();
                newDisSeg.setCuid();
                newDisSeg.setOrigPointCuid(disPointCuidList.get(i));
                newDisSeg.setDestPointCuid(disPointCuidList.get(i + 1));
                newDisSeg.setIndexInBranch(++preIndex);
                newDisSegList.add(newDisSeg);
            }
        }
        
        if(modDisSegList != null && modDisSegList.size() > 0){
        	for (int i = 0; i < modDisSegList.size(); i++) {
                if (!(modDisSegList.get(i) instanceof DisplaySeg)) continue;
                DisplaySeg disSeg = (DisplaySeg)modDisSegList.get(i);
                disSeg.setIndexInBranch(disSeg.getIndexInBranch() + newDisSegList.size() - delDisSegList.size());
            }
        }
        
        BoCmdFactory.getInstance().execBoCmd(getDelActionName(origPJ.getClassName()), actionContext, delDisSegList);
        BoCmdFactory.getInstance().execBoCmd(getAddActionName(origPJ.getClassName()), actionContext, newDisSegList);
        BoCmdFactory.getInstance().execBoCmd(getModActionName(origPJ.getClassName()), actionContext, modDisSegList);
    }
    
    private static DataObjectList getOldDisplaySegsInBranch(PhysicalJoin splitedSeg){
        String relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_BRANCH_CUID"));
        if (splitedSeg instanceof UpLineSeg || splitedSeg instanceof HangWallSeg) {
            relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_SYSTEM_CUID"));
        }
        return getOldDisplaySegsInBranch(relatedBranchCuid, null);
    }
    
    private static DataObjectList getOldDisplaySegsInBranch(String relatedCuid, HttpServletRequest request){
        
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
    	try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    DuctManagerBOHelper.ActionName.getDispalySegsBySystemCuid,
                    actionContext, relatedCuid);
        } catch (Exception ex) {
            LogHome.getLog().error("获取系统段数据失败!", ex);
        }
        return null;
    }
    
    public static DataObjectList getSegsInBranch(PhysicalJoin splitedSeg){
        String relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_BRANCH_CUID"));
        if (splitedSeg instanceof UpLineSeg || splitedSeg instanceof HangWallSeg) {
            relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_SYSTEM_CUID"));
        }
        return getSegsInBranch(relatedBranchCuid, null);
    }

    public static DataObjectList getSegsInBranch(String relatedCuid, HttpServletRequest request){
       
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
    	try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    DuctManagerBOHelper.ActionName.getSegsBySystemCuid,
                    actionContext, relatedCuid);
        } catch (Exception ex) {
            LogHome.getLog().error("获取系统段数据失败!", ex);
        }
        return null;
    }
    
    private static String getDelActionName(String className) {
        if (DuctSeg.CLASS_NAME.equals(className)) {
            return DuctDisplaySegBOHelper.ActionName.deleteDuctDisplaySegs;
        } else if (PolewaySeg.CLASS_NAME.equals(className)) {
            return PolewayDisplaySegBOHelper.ActionName.deletePolewayDisplaySegs;
        } else if (StonewaySeg.CLASS_NAME.equals(className)) {
            return StonewayDisplaySegBOHelper.ActionName.deleteStonewayDisplaySegs;
        } else if (UpLineSeg.CLASS_NAME.equals(className)) {
            return UpLineDisplaySegBOHelper.ActionName.deleteUpLineDisplaySegs;
        } else if (HangWallSeg.CLASS_NAME.equals(className)) {
            return HangWallDisplaySegBOHelper.ActionName.deleteHangWallDisplaySegs;
        } else {
            return null;
        }
    }
    private static String getAddActionName(String className) {
        if (DuctSeg.CLASS_NAME.equals(className)) {
            return DuctDisplaySegBOHelper.ActionName.addDuctDisplaySegs;
        } else if (PolewaySeg.CLASS_NAME.equals(className)) {
            return PolewayDisplaySegBOHelper.ActionName.addPolewayDisplaySegs;
        } else if (StonewaySeg.CLASS_NAME.equals(className)) {
            return StonewayDisplaySegBOHelper.ActionName.addStonewayDisplaySegs;
        } else if (UpLineSeg.CLASS_NAME.equals(className)) {
            return UpLineDisplaySegBOHelper.ActionName.addUpLineDisplaySegs;
        } else if (HangWallSeg.CLASS_NAME.equals(className)) {
            return HangWallDisplaySegBOHelper.ActionName.addHangWallDisplaySegs;
        } else {
            return null;
        }
    }
    private static String getModActionName(String className) {
        if (DuctSeg.CLASS_NAME.equals(className)) {
            return DuctDisplaySegBOHelper.ActionName.modifyDuctDisplaySegs;
        } else if (PolewaySeg.CLASS_NAME.equals(className)) {
            return PolewayDisplaySegBOHelper.ActionName.modifyPolewayDisplaySegs;
        } else if (StonewaySeg.CLASS_NAME.equals(className)) {
            return StonewayDisplaySegBOHelper.ActionName.modifyStonewayDisplaySegs;
        } else if (UpLineSeg.CLASS_NAME.equals(className)) {
            return UpLineDisplaySegBOHelper.ActionName.modifyUpLineDisplaySegs;
        } else if (HangWallSeg.CLASS_NAME.equals(className)) {
            return HangWallDisplaySegBOHelper.ActionName.modifyHangWallDisplaySegs;
        } else {
            return null;
        }
    }

    /**
     * 得到能否合并数据
     * @param carrycablelist1 DataObjectList
     * @param carrycablelist2 DataObjectList
     */
    public static boolean getyesornoUniteCarrycable(DataObjectList carrycablelist1, DataObjectList carrycablelist2, 
    		DataObjectList wireToDuctlinelist, String seg1name, String seg2name) {
        boolean boo = true;
        for (int i = 0; i < carrycablelist1.size(); i++) {
            GenericDO gdo1 = carrycablelist1.get(i);
            GenericDO gdo2 = carrycablelist2.get(i);
            java.util.List<GenericDO> gdo1list = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.ductlineCuid, gdo1.getCuid());
            java.util.List<GenericDO> gdo2list = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.ductlineCuid, gdo2.getCuid());
            if (gdo1list.size() != gdo2list.size()) {
                if(gdo1 instanceof CarryingCable){
                	JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "的敷设信息不同!");
                    throw new UserException(seg1name + "---" + seg2name + "的敷设信息不同!");
                }else{
                	JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "的敷设信息不同!");
                    throw new UserException(seg1name + "---" + seg2name + "的敷设信息不同!");
                }
            } else if (gdo1list.size() > 0 && gdo2list.size() > 0) {
                for (int s = 0; s < gdo1list.size(); s++) {
                    String wireseg1 = (String) gdo1list.get(s).getAttrValue(WireToDuctline.AttrName.wireSegCuid);
                    String wireseg2 = (String) gdo2list.get(s).getAttrValue(WireToDuctline.AttrName.wireSegCuid);
                    if (wireseg1.equals(wireseg2)) {
                        boo = true;
                    } else {
                    	JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "的敷设信息不相同!");
                        throw new UserException(seg1name + "---" + seg2name + "的敷设信息不相同!");
                    }
                }
            }
        }
        return boo;
    }
}
