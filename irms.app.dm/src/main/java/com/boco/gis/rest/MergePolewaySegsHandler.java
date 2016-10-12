package com.boco.gis.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.graphkit.ext.GenericNode;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.CarryingCable;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireDisplaySeg;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.helper.dm.CarryingCableBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireRemainBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;

public class MergePolewaySegsHandler {
	
	 /**
     * 开始合并杆路段
     * @param segmap Map
     * @param pointList ArrayList
     * @param breachcuid String
     */
    public static GenericDO unitePolewaySeg(Map segmap, ArrayList pointList, String breachcuid, DataObjectList seglist) {
    	
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
    	GenericDO poleSeg = null;
        // 获取待合并的杆路段数据
        DataObjectList mergePolywaySegList = new DataObjectList();
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
                mergePolywaySegList.add(dto);
        }
        mergePolywaySegList.sort("INDEX_IN_BRANCH", true);

        DataObjectList wireToDuctlinelist = new DataObjectList();
        
        DataObjectList deletecarrycableList = new DataObjectList();
        DataObjectList modifywire2ductlineList = new DataObjectList();
        DataObjectList modifycarrycableList = new DataObjectList();
        DataObjectList modifyploewayseglist = new DataObjectList();
        DataObjectList deleteploewayseglist = new DataObjectList();
        ArrayList<String> pointCuidList = new ArrayList();
        DataObjectList savewire2ductlineList = new DataObjectList();
        Map<String, GenericDO> pointmap = new HashMap<String, GenericDO>();

        //循环合并点,得到合并点两端段信息并且判断是否可以合并
        for (int i = 0; i < pointList.size(); i++) {
            GenericDO gdo = null;
            if (pointList.get(i) instanceof GenericNode) {
                GenericNode gdonode = (GenericNode) pointList.get(i);
                gdo = gdonode.getNodeValue();
            } else if (pointList.get(i) instanceof GenericDO) {
                gdo = (GenericDO) pointList.get(i);
            }
            String cuid = gdo.getCuid();
            pointmap.put(cuid, gdo);
            segmap.clear();
            for (int ss = 0; ss < seglist.size(); ss++) {
                GenericDO origgdo = (GenericDO) seglist.get(ss).getAttrValue(PolewaySeg.AttrName.origPointCuid);
                GenericDO destgdo = (GenericDO) seglist.get(ss).getAttrValue(PolewaySeg.AttrName.destPointCuid);
                if (origgdo == null || destgdo == null) {
                    String name = (String) seglist.get(ss).getAttrValue("LABEL_CN");
                    throw new UserException(name+"这条记录中起止点信息不完全,无法进行合并!");
                }
                segmap.put(origgdo.getCuid(), seglist.get(ss));
                segmap.put(destgdo.getCuid(), seglist.get(ss));
            }
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
                } else if (cuid.equals(destgdo.getCuid())) {

                }
                DataObjectList unitesegList = MergeSHUSegsHandler.getuniteSeg(seglist, indexinbranch); //得到合并段并且排序

                Map<String, DataObjectList> unitemap = beginUniteSeg(wireToDuctlinelist, unitesegList);
                if (unitemap != null) { //最后的入库,都在这里处理的.
                    DataObjectList modifywire2ductlineList1 = unitemap.get("modifywire2ductline");
                    GenericDO segdestpoint = (GenericDO) unitesegList.get(1).getAttrValue(PolewaySeg.AttrName.destPointCuid);
                    unitesegList.get(0).setAttrValue(PolewaySeg.AttrName.destPointCuid, segdestpoint);
                    modifyploewayseglist.add(unitesegList.get(0));
                    deleteploewayseglist.add(unitesegList.get(1));
                    modifywire2ductlineList.addAll(modifywire2ductlineList1);
                    if (i == 0) {
                        savewire2ductlineList.addAll(modifywire2ductlineList1);
                    }
                } else {
                    return null;
                }
            }
            pointCuidList.add(cuid);
        }
        GenericDO modifyploewayseg = modifyploewayseglist.get(0);
        GenericDO destgdo = null;
        double length = 0;
        if (modifyploewayseg.getAttrValue(PolewaySeg.AttrName.length) != null) {
            length = (Double) modifyploewayseg.getAttrValue(PolewaySeg.AttrName.length);
        }

        for (int i = 0; i < deleteploewayseglist.size(); i++) {
            if (deleteploewayseglist.get(i).getAttrValue(PolewaySeg.AttrName.length) != null) {
                double l = (Double) deleteploewayseglist.get(i).getAttrValue(PolewaySeg.AttrName.length);
                length = length + l;
            }
            if (i == deleteploewayseglist.size() - 1) {
                destgdo = (GenericDO) deleteploewayseglist.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
            }
        }

        
        
        modifyploewayseg.setAttrValue(PolewaySeg.AttrName.length, length);
        
        DataObjectList newsavewire2ductlineList = new DataObjectList();
        if (destgdo != null) {
            modifyploewayseg.setAttrValue(PolewaySeg.AttrName.destPointCuid, destgdo);
            if(modifywire2ductlineList!=null && modifywire2ductlineList.size()>0){
                for(int i=0; i<modifywire2ductlineList.size(); i++){
                    GenericDO wtdlgdo = new GenericDO();
                    modifywire2ductlineList.get(i).copyTo(wtdlgdo);
                    wtdlgdo.setAttrValue(WireToDuctline.AttrName.endPointCuid, destgdo.getCuid());
                    wtdlgdo.setCuid(null);
                    wtdlgdo.setCuid();
                    wtdlgdo.setObjectNum(0);
                    wtdlgdo.setObjectId("" + 0);
                    newsavewire2ductlineList.add(wtdlgdo);
                }
            }
            GenericDO origgdo = (GenericDO) modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid);
            modifyploewayseg.setAttrValue(PolewaySeg.AttrName.labelCn, origgdo.getAttrValue("LABEL_CN") + "--" + destgdo.getAttrValue("LABEL_CN"));
        }


        //杆路段合并,处理光缆预留点信息
        String origcuid = "";
        String origsitelabel = "";
        if (modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid) instanceof String) {
            origcuid = (String) modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid);
        } else {
            GenericDO pointgdo = (GenericDO) modifyploewayseg.getAttrValue(PolewaySeg.AttrName.origPointCuid);
            origcuid = pointgdo.getCuid();
            origsitelabel =pointgdo.getAttrString(GenericDO.AttrName.labelCn);
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


            Map<String, GenericDO> remainmap = new HashMap();
            for (int s = 0; s < wiresegreamlist.size(); s++) {
                GenericDO remaingdo = wiresegreamlist.get(s);
                remainlength = remainlength + remaingdo.getAttrDouble(WireRemain.AttrName.remainLength);
                remainmap.put(remaingdo.getAttrString(WireRemain.AttrName.relatedLocationCuid), remaingdo);
                deletewiresegremainlist.add(remaingdo);
            }


        }

        if (wireSegRemain!=null && wireSegRemain.size() > 0) {
            GenericDO remgdo = wireSegRemain.get(0);
            remgdo.setAttrValue(WireRemain.AttrName.remainLength, remainlength);
            remgdo.setAttrValue(WireRemain.AttrName.relatedLocationCuid, origcuid);
            remgdo.setAttrValue(WireRemain.AttrName.labelCn, origsitelabel);

            wiresegremainlist.add(remgdo);
        }

        if (deletewiresegremainlist != null && deletewiresegremainlist.size() > 0) {
//        	JOptionPane.showMessageDialog(null,"名称为"+ origsitelabel + "的长度是:" + remainlength);
        }
        //处理光缆预留信息结束
        DataObjectList modifyWireToDuctlinelist=null;
		try {
			// 删除所有相关的敷设信息,并返回修改段对应的敷设信息
			 modifyWireToDuctlinelist = MergeSHUSegsHandler.deleteWire2Ductlines(modifyploewayseg, deleteploewayseglist, null);

			// 先把预留信息全部删除在作存储
			BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.deleteWireRemains, actionContext, deletewiresegremainlist);

			// 添加光缆预留
			BoCmdFactory.getInstance().execBoCmd(WireRemainBOHelper.ActionName.addWireRemains, actionContext, wiresegremainlist);

			// 删除杆路段信息
			BoCmdFactory.getInstance().execBoCmd(PolewaySegBOHelper.ActionName.deletePolewaySegs, actionContext, deleteploewayseglist);
			// 修改杆路段信息
			poleSeg = (GenericDO)BoCmdFactory.getInstance().execBoCmd(PolewaySegBOHelper.ActionName.modifyPolewaySeg, actionContext, modifyploewayseg);
			// 修改吊线段信息
			BoCmdFactory.getInstance().execBoCmd(
					CarryingCableBOHelper.ActionName.modifyCarryingCables,
					actionContext, modifycarrycableList);

			// 删除吊线段信息
			BoCmdFactory.getInstance().execBoCmd(CarryingCableBOHelper.ActionName.deleteCarryingCables, actionContext, deletecarrycableList);

			MergeSHUSegsHandler.addModifyWire2Ductline(modifyploewayseg, modifyWireToDuctlinelist, pointList.size(), null);

			// 修改杆路段后入库
			BoCmdFactory .getInstance() .execBoCmd(
					PolewaySegBOHelper.ActionName.modfiypolewaySegindexByStartSeg,actionContext,
					new Long(0 - deleteploewayseglist.size()),(PolewaySeg) deleteploewayseglist.get(deleteploewayseglist.size() - 1));

		} catch (Exception ex1) {
			LogHome .getLog() .error("杆路段合并异常", ex1);
		}
		for (int i = 0; i < deleteploewayseglist.size(); i++) {
			String cuid = deleteploewayseglist.get(i).getCuid();
			seglist.removeObjectByAttr("CUID", cuid);
		}

		seglist.sort(PolewaySeg.AttrName.indexInBranch, true);
		for (int i = 0; i < seglist.size(); i++) {
			seglist.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch,
					new Long(i + 1));
		}
		syncWireSegDisplayRouteByW2D(modifyWireToDuctlinelist);
        try {
        	//MergeSHUSegsHandler.mergeDisplaySegs(mergePolywaySegList);
        } catch (Exception ex) {
            LogHome.getLog().error("杆路段合并失败!", ex);
        }
//        refreshSystemsBySeg(seglist);
        return poleSeg ;
    }
    
    /**
     * 开始合并分支
     * @param wireToDuctlinelist DataObjectList
     * @param unitesegList DataObjectList
     * @param request TODO
     * @param seglist DataObjectList
     * @param displayseglist DataObjectList
     * @return DataObjectList
     */
    public static Map beginUniteSeg(DataObjectList wireToDuctlinelist, DataObjectList unitesegList) {
        
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
    	DataObjectList carrycablelist1 = new DataObjectList();
        DataObjectList carrycablelist2 = new DataObjectList();
        DataObjectList deleteSegList = new DataObjectList();
        DataObjectList deletewire2ductlineList = new DataObjectList();
        DataObjectList deletecarrycableList = new DataObjectList();

        DataObjectList modifySegList = new DataObjectList();
        DataObjectList modifywire2ductlineList = new DataObjectList();
        DataObjectList modifycarrycableList = new DataObjectList();

        String seg1name = "";
        String seg2name = "";
        DataObjectList carrywireToDuctlinelist = null;
        DataObjectList segwireToDuctlinelist = null;
        if (unitesegList.size() > 1) {
            GenericDO seg1 = unitesegList.get(0);
            GenericDO seg2 = unitesegList.get(1);
            String seg1cuid = seg1.getCuid();
            String seg2cuid = seg2.getCuid();
            seg1name = seg1.getAttrString("LABEL_CN");
            seg2name = seg2.getAttrString("LABEL_CN");
            //得到 吊线段信息  
            try {
                carrycablelist1 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    CarryingCableBOHelper.ActionName.getCarryingCablesByPoleWaySeg,
                    actionContext,
                    seg1cuid);
                carrycablelist2 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    CarryingCableBOHelper.ActionName.getCarryingCablesByPoleWaySeg,
                    actionContext,
                    seg2cuid);
                DataObjectList allcarrycable = new DataObjectList();
                allcarrycable.addAll(carrycablelist1);
                allcarrycable.addAll(carrycablelist2);
                String carrycablesql = "";
                for (int i = 0; i < allcarrycable.size(); i++) {
                    if (carrycablesql.equals("")) {
                        carrycablesql = WireToDuctline.AttrName.ductlineCuid + " ='" + allcarrycable.get(i).getCuid() + "'";
                    } else {
                        carrycablesql = carrycablesql + " or " + WireToDuctline.AttrName.ductlineCuid + " ='" + allcarrycable.get(i).getCuid() + "'";
                    }
                }
                String unitesegsql = "";
              for (int i = 0; i < unitesegList.size(); i++) {
                  if (unitesegsql.equals("")) {
                      unitesegsql = WireToDuctline.AttrName.ductlineCuid + " ='" + unitesegList.get(i).getCuid() + "'";
                  } else {
                      unitesegsql = unitesegsql + " or " + WireToDuctline.AttrName.ductlineCuid + " ='" + unitesegList.get(i).getCuid() + "'";
                  }
              }
              //得到光缆敷设信息
              carrywireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
                    actionContext,
                    carrycablesql);

              segwireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
                    actionContext,
                    unitesegsql);
                if(carrywireToDuctlinelist!=null){
                    wireToDuctlinelist.addAll(carrywireToDuctlinelist);
                }
                if(segwireToDuctlinelist!=null){
                    wireToDuctlinelist.addAll(segwireToDuctlinelist);
                }
            } catch (Exception ex) {
                LogHome.getLog().error("获取吊线段信息失败 ", ex);
            }
        }

        if (carrycablelist1.size() != carrycablelist2.size()) {
        	JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "吊线个数不相同!");
            return null;
        } else {
            DataObjectList segorigList = new DataObjectList();
            DataObjectList segdestList = new DataObjectList();
            segorigList.add(unitesegList.get(0));
            segdestList.add(unitesegList.get(1));
            if(getyesornoUniteCarrycable(carrycablelist1, carrycablelist2, carrywireToDuctlinelist, seg1name, seg2name)
                && getyesornoUniteCarrycable(segorigList, segdestList, segwireToDuctlinelist, seg1name, seg2name)){
                if (getyesornoUniteCarrycable(carrycablelist1, carrycablelist2, carrywireToDuctlinelist, seg1name, seg2name)) { //判断是否可合并
                	getwire2dlByUnitPoleWaySeg(carrycablelist1, carrycablelist2, modifycarrycableList, deletecarrycableList, carrywireToDuctlinelist,
                                               modifywire2ductlineList, deletewire2ductlineList);
                }
                if (getyesornoUniteCarrycable(segorigList, segdestList, segwireToDuctlinelist, seg1name, seg2name)) {
                	getwire2dlByUnitPoleWaySeg(segorigList, segdestList, null, null, segwireToDuctlinelist, modifywire2ductlineList, deletewire2ductlineList);
                } else {
                    return null;
                }
            }
        }
        Map<String, DataObjectList> remap = new HashMap();
        remap.put("deletewire2ductline", deletewire2ductlineList);
        remap.put("deletecarrycable", deletecarrycableList);
        remap.put("modifywire2ductline", modifywire2ductlineList);
        remap.put("modifycarrycable", modifycarrycableList);
        return remap;
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
                	//JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "的敷设信息不同!");
                    throw new UserException(seg1name + "---" + seg2name + "的敷设信息不同!");
                }else{
                	//JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "的敷设信息不同!");
                    throw new UserException(seg1name + "---" + seg2name + "的敷设信息不同!");
                }
            } else if (gdo1list.size() > 0 && gdo2list.size() > 0) {
                for (int s = 0; s < gdo1list.size(); s++) {
                    String wireseg1 = (String) gdo1list.get(s).getAttrValue(WireToDuctline.AttrName.wireSegCuid);
                    String wireseg2 = (String) gdo2list.get(s).getAttrValue(WireToDuctline.AttrName.wireSegCuid);
                    if (wireseg1.equals(wireseg2)) {
                        boo = true;
                    } else {
                    	//JOptionPane.showMessageDialog(null, seg1name + "---" + seg2name + "的敷设信息不相同!");
                        throw new UserException(seg1name + "---" + seg2name + "的敷设信息不相同!");
                    }
                }
            }
        }
        return boo;
    }
    
    private static void getwire2dlByUnitPoleWaySeg(DataObjectList carrycablelist1,DataObjectList carrycablelist2,DataObjectList modifycarrycableList,
            DataObjectList deletecarrycableList,DataObjectList wireToDuctlinelist,
			DataObjectList modifywire2ductlineList, DataObjectList deletewire2ductlineList) {
		for (int i = 0; i < carrycablelist1.size(); i++) {
			GenericDO gdo = carrycablelist1.get(i);
			GenericDO gdo2 = carrycablelist2.get(i);
			if (modifycarrycableList != null) {
				String destpointcuid = (String) gdo2.getAttrValue(CarryingCable.AttrName.destPointCuid);
				double gdolength = gdo.getAttrDouble(CarryingCable.AttrName.length);
				double gdo2length = gdo2.getAttrDouble(CarryingCable.AttrName.length);
				gdo.setAttrValue(CarryingCable.AttrName.length, gdolength + gdo2length);
				gdo.setAttrValue(CarryingCable.AttrName.destPointCuid, destpointcuid);
				modifycarrycableList.add(gdo);
				deletecarrycableList.add(gdo2);
			}
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
				}
			}
		}
	}
    
    public static void syncWireSegDisplayRouteByW2D(DataObjectList w2dList) {
        if (w2dList==null||w2dList.isEmpty()) return;
        Map<String, String> wireSegCuidMap = new HashMap<String, String>();
        for (GenericDO dto : w2dList) {
            if (!(dto instanceof WireToDuctline)) continue;
            Object wireSegObj = dto.getAttrValue(WireToDuctline.AttrName.wireSegCuid);
            String wireSegCuid = DMHelper.getRelatedCuid(wireSegObj);
            if (!wireSegCuidMap.containsKey(wireSegCuid)) {
                wireSegCuidMap.put(wireSegCuid, wireSegCuid);
            }
        }
        for (String wireSegCuid : wireSegCuidMap.keySet()) {
            sysWireSegDisplayRoute(wireSegCuid,false);
        }
    }
    public static void sysWireSegDisplayRoute(String wireSegCuid,boolean isPrompt) {
       
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
    	WireSeg wireSeg = new WireSeg();
        try {
            wireSeg = (WireSeg) BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegByCuid,
                actionContext, wireSegCuid);
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage(), ex);
        }
        syschWireSegDisplayRoute(wireSeg, isPrompt);
    }
    
    /**
     * add by wangguodong 2009-07-15 地图上同步光缆段显示路由。
     * @param request TODO
     */
    public static void syschWireSegDisplayRoute(WireSeg wireseg,boolean isPrompt) {
    	
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
    	if(wireseg==null) return;
        String wireBranchCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
        String wireSystemCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
        GenericDO branch = null;
        try {
            branch = (WireBranch) BoCmdFactory.getInstance().execBoCmd(WireBranchBOHelper.ActionName.getWireBranchByCuid, actionContext, wireBranchCuid);
        } catch (Exception ex) {
            LogHome.getLog().error("获取光缆信息失败" + ex.getMessage());
        }
        if (branch == null) {
            if (isPrompt) {
            	JOptionPane.showMessageDialog(null, "光缆信息为空,请选择!");
            }
            return;
        }
        //根据seg关系得到所有敷设信息
        DataObjectList wiretoductlines = new DataObjectList();
        DataObjectList wiredisplayseglists = new DataObjectList();
        List<String> deletePresetPoints=new ArrayList<String>();
        //根据关系得到所有敷设信息,可能是多种类型,对不同类型进行不同的处理
        try {
            wiretoductlines = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg, actionContext, wireseg);
        } catch (Exception ex) {
            LogHome.getLog().info("获取敷设信息失败"+ ex.getMessage());
        }
        wiretoductlines.sort(WireToDuctline.AttrName.indexInRoute, true); //用 INDEX_IN_ROUTE =1,2,3  进行排序
        try {
            List<String> pointCuidList = DMHelper.getPointCuidListByWireToDuctlines(wiretoductlines);
            String origPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.destPointCuid));

            List<String> displayPoints = getDisplayPointByWireBranch(DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid)));
            if(displayPoints==null){
                displayPoints=new ArrayList<String>();
            }
            boolean isReset = false;
            if (displayPoints.size() > 0) {
                for (int i = 0; i < displayPoints.size(); i++) {
                    String pointCuid = displayPoints.get(i);
                    if (pointCuid.startsWith(PresetPoint.CLASS_NAME)) {
                        isReset = true;
                        if(!pointCuidList.contains(pointCuid)){
                            deletePresetPoints.add(pointCuid);
                        }
                    }
                }
            }
            if (pointCuidList.size() == 0) {
                pointCuidList = displayPoints;
                if (!isReset) {
                    pointCuidList = new ArrayList<String>();
                }
            }
            if (!pointCuidList.contains(origPointCuid)) {
                pointCuidList.add(0, origPointCuid);
            }
            if (!pointCuidList.contains(destPointCuid)) {
                pointCuidList.add(destPointCuid);
            }
            String[] pointCuidArray = new String[pointCuidList.size()];
            pointCuidList.toArray(pointCuidArray);
            DataObjectList points = getObjectsByCuids(pointCuidArray);
            for (int j = 0; j < points.size() - 1; j++) {
                WireDisplaySeg wiredisplayseg = new WireDisplaySeg();
                String labelcn = "";
                if (points.get(j) != null) {
                    labelcn = "" + points.get(j).getAttrString("LABEL_CN");
                }
                if (points.get(j + 1) != null) {
                    labelcn += "--" + points.get(j + 1).getAttrString("LABEL_CN");
                }
                // "具体路由"
                wiredisplayseg.setLabelCn(labelcn);
                wiredisplayseg.setIndexInBranch(j + 1);
                wiredisplayseg.setAttrValue(WireDisplaySeg.AttrName.origPointCuid, points.get(j));
                wiredisplayseg.setAttrValue(WireDisplaySeg.AttrName.destPointCuid, points.get(j + 1));
                wiredisplayseg.setRelatedSystemCuid(wireSystemCuid);
                wiredisplayseg.setRelatedBranchCuid(wireBranchCuid);

                wiredisplayseglists.add(wiredisplayseg);
            }
        } catch (Exception e) {
            LogHome.getLog().error(e.getMessage(), e);
        }
        DMHelper.clearDto(wiredisplayseglists);
        //删除旧显示路由 具体路由代替 到服务器
        //删除数据 条件 根据WireSeg中的RELATED_BRANCH_CUID 和 RELATED_SYSTEM_CUID 的
        DataObjectList wiresegdisplay = new DataObjectList();
        if (wiredisplayseglists.size() > 0) {
            try {
                wiresegdisplay = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(
                    WireDisplaySegBOHelper.ActionName.deleteAndAddWireDisplaySegs, actionContext, wireseg, wiredisplayseglists);
            } catch (Exception ex) {
                LogHome.getLog().info(ex.getMessage());
            }
            branch.setAttrValue(DMCacheObjectName.SystemDisplaySegChildren, wiresegdisplay);
            try {
            	IWireSegBO bo = (IWireSegBO) BoHomeFactory.getInstance().getBO(IWireSegBO.class);
            	bo.modifyLayOrDeleteRelationWireSeg(actionContext, wireseg);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            WireSystem wireSystem = new WireSystem();
            DataObjectList refreshList = new DataObjectList();
            wireSystem.setCuid(wireSystemCuid);
            refreshList.add(wireSystem);
//            DMGisUtils.getDmMap().refreshSystems((DataObjectList) refreshList);//？？？？？？？？？
            try {
                if (deletePresetPoints.size() > 0) {
                	String[] tmpPointCuids = new String[deletePresetPoints.size()];
                    for (int i = 0; i < deletePresetPoints.size(); i++) {
                        tmpPointCuids[i] = deletePresetPoints.get(i);
                    }
                    DataObjectList list = getObjectsByCuids(tmpPointCuids);
//                    deletePointObjects(list);//？？？？？
                }
            } catch (Exception ex) {
                LogHome.getLog().info("删除预置点出错", ex);
            }
        }
    }
    
    public static List getDisplayPointByWireBranch(String branchCuid) {
        
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
    	DataObjectList displays = null;
        try {
            displays = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireDisplaySegBOHelper.ActionName.getDisplaySegByBranch, actionContext,
                new WireBranch(branchCuid));
        } catch (Exception ex) {
            LogHome.getLog().error("getDisplayPointByWireBranch出错",ex);
        };
        if (displays == null || displays.isEmpty()) return null;
        List<String> pointCuids = new ArrayList<String>();
        for (GenericDO dto : displays) {
            if (!(dto instanceof WireDisplaySeg)) continue;
            WireDisplaySeg display = (WireDisplaySeg) dto;
            if (!pointCuids.contains(display.getOrigPointCuid())) {
                pointCuids.add(display.getOrigPointCuid());
            }
            if (!pointCuids.contains(display.getDestPointCuid())) {
                pointCuids.add(display.getDestPointCuid());
            }
        }
        return pointCuids;
    }
    
    public static DataObjectList getObjectsByCuids(String[] cuids) {
    	
    	BoActionContext actionContext = ActionContextUtil.getActionContext();
    	
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
    
//    public static void deletePointObjects(DataObjectList list) throws Exception {
//        if (list != null && list.size() > 0) {
//            try {
//                //从服务器删除
//                BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteLocatePoints, actionContext, list);
//                //从内存删除及地图表删除
//               List strCuids= list.getCuidList();
//                for (int i = 0; i < list.size(); i++) {
//                	DMGisUtils.getDmMap().removeCachePoint(list.get(i));
//                    LocatePointCacheModel.getInstance().removeElement(list.get(i));
//
//                }
//                List allList = LocatePointCacheModel.getInstance().getElements();
//
//                if (allList != null && allList.size() > 0) {
//                    List tmpList=new ArrayList();
//                    for (int i = 0; i < allList.size(); i++) {
//                        GenericDO dto=(GenericDO) allList.get(i);
//                        String cuid=dto.getCuid();
//                        if(cuid!=null&&strCuids.contains(cuid)){
//                            tmpList.add(dto);
//                        }
//                    }
//                    if(tmpList.size()>0){
//                        allList.removeAll(tmpList);
//                    }
//                }
//            } catch (Exception ex) {
//                LogHome.getLog().error(ex.getMessage());
//            }
//
//        }
//    }
}
