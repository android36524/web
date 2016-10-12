package com.boco.gis.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelperX;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.Optical;
import com.boco.transnms.common.dto.OpticalToFiber;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.helper.cm.JumpFiberBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.helper.dm.OpticalBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireRemainBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

import flex.messaging.io.amf.ActionContext;


public class MergeWireSegsAction {
	private static final String changesate = "CHANGE";
	
	public String saveAllData(HttpServletRequest request,String systemCuid, String firstPointCuid, String lastPointCuid) throws Exception {
		
		DmDesignerTools.setActionContext(new ServiceActionContext(request));
		BoActionContext actionContext = DmDesignerTools.getActionContext();
		
		//获取要合并的点所在的段
		List<String> pointCuidList1 = new ArrayList<String>(); 
		List<String> pointCuidList2 = new ArrayList<String>(); 
		Map<String, GenericDO> map1 = new HashMap<String, GenericDO>();
		Map<String, GenericDO> map2 = new HashMap<String, GenericDO>();
		String sql1 = WireSeg.AttrName.origPointCuid + " = '" + firstPointCuid + "' or " + WireSeg.AttrName.destPointCuid + " = '" + firstPointCuid + "'";
		DataObjectList wireSegList1 = getDuctManagerBO().getObjectsBySql(sql1, new WireSeg());
		if(wireSegList1 != null && wireSegList1.size() > 0){
			for(GenericDO wireSeg : wireSegList1){
				String origPointCuid = wireSeg.getAttrString(WireSeg.AttrName.origPointCuid);
				String destPointCuid = wireSeg.getAttrString(WireSeg.AttrName.destPointCuid);
				if(!pointCuidList1.contains(origPointCuid) && !origPointCuid.equals(firstPointCuid)){
					pointCuidList1.add(origPointCuid);
					map1.put(origPointCuid, wireSeg);
				}
				if(!pointCuidList1.contains(destPointCuid) && !destPointCuid.equals(firstPointCuid)){
					pointCuidList1.add(destPointCuid);
					map1.put(destPointCuid, wireSeg);
				}
			}
		}
		
		String sql2 = WireSeg.AttrName.origPointCuid + " = '" + lastPointCuid + "' or " + WireSeg.AttrName.destPointCuid + " = '" + lastPointCuid + "'";
		DataObjectList wireSegList2 = getDuctManagerBO().getObjectsBySql(sql2, new WireSeg());
		if(wireSegList2 != null && wireSegList2.size() > 0){
			for(GenericDO wireSeg : wireSegList2){
				String origPointCuid = wireSeg.getAttrString(WireSeg.AttrName.origPointCuid);
				String destPointCuid = wireSeg.getAttrString(WireSeg.AttrName.destPointCuid);
				if(!pointCuidList2.contains(origPointCuid) && !origPointCuid.equals(lastPointCuid)){
					pointCuidList2.add(origPointCuid);
					map2.put(origPointCuid, wireSeg);
				}
				if(!pointCuidList2.contains(destPointCuid) && !destPointCuid.equals(lastPointCuid)){
					pointCuidList2.add(destPointCuid);
					map2.put(destPointCuid, wireSeg);
				}
			}
		}
		List<String> mergeSegsCuid = new ArrayList<String>();
		for(String pointCuid : pointCuidList1){
			for(String cuid : pointCuidList2){
				if(pointCuid.equals(cuid)){
					GenericDO wireSeg1 = map1.get(cuid);
					GenericDO wireSeg2 = map2.get(cuid);
					String wireSegcuid1 = wireSeg1.getCuid();
					String wireSegcuid2 = wireSeg2.getCuid();
					mergeSegsCuid.add(wireSegcuid1);
					mergeSegsCuid.add(wireSegcuid2);
				}
			}
		}
		
		
		DataObjectList mergeSegs = getMergeWireSegs(mergeSegsCuid, systemCuid);
		if (mergeSegs == null || mergeSegs.size() != 2){
			throw new UserException("光缆段合并失败！");
			//return null;
		}		
		try {
			doMergeWireSeg((WireSeg) mergeSegs.get(0), (WireSeg) mergeSegs.get(1),actionContext);
		} catch (Exception ex) {
			LogHome.getLog().error("", ex);
			throw new UserException("光缆段合并失败！");
		}
		
		GenericDO systemGdo = getDuctManagerBO().getObjByCuid(actionContext, systemCuid);
		return "光缆段合并成功";
		//return systemGdo;
	}
	
    private DataObjectList getMergeWireSegs(List<String> mergeSegsCuid, String systemCuid) {
        if (mergeSegsCuid == null || mergeSegsCuid.size() != 2) return null;
        DataObjectList allSegs = null;
        try {
            allSegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    WireSegBOHelper.ActionName.getWireSegsByWireSystemCuid, new BoActionContext(), systemCuid);
        } catch (Exception e) {
            LogHome.getLog().error("", e);
        }
        if (allSegs == null) return null;
        DataObjectList merge = new DataObjectList();
        for (String branchCuid : mergeSegsCuid) {
            for (GenericDO dto : allSegs) {
                if (!(dto instanceof WireSeg)) continue;
                WireSeg wireSeg = (WireSeg) dto;
                if (branchCuid.equals(wireSeg.getCuid()) || branchCuid.equals(wireSeg.getRelatedBranchCuid())) {
                    merge.add(wireSeg);
                }
            }
        }
        if (merge == null || merge.size() != 2) return null;
        List<String> pointCuidList = new ArrayList<String>();
        Map<String, String> pointCuidMap = new HashMap<String, String>();
        for (GenericDO dto : merge) {
            if (!(dto instanceof WireSeg)) continue;
            WireSeg ws = (WireSeg) dto;
            if (!pointCuidMap.containsKey(ws.getOrigPointCuid()))
                pointCuidList.add(ws.getOrigPointCuid());
            if (!pointCuidMap.containsKey(ws.getDestPointCuid()))
                pointCuidList.add(ws.getDestPointCuid());
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
            if (!(dto instanceof WireSeg)) continue;
            WireSeg ws = (WireSeg) dto;
            String origPointCuid = DMHelperX.getRelatedCuid(ws.getAttrValue(WireSeg.AttrName.origPointCuid));
            String destPointCuid = DMHelperX.getRelatedCuid(ws.getAttrValue(WireSeg.AttrName.destPointCuid));
            ws.setAttrValue(WireSeg.AttrName.origPointCuid, pointMap.get(origPointCuid));
            ws.setAttrValue(WireSeg.AttrName.destPointCuid, pointMap.get(destPointCuid));
        }
        return merge;
    }
    
    /**
     * 合并给定的两条光缆段
     *
     * @param segone 待合并的光缆段1
     * @param segtwo 待合并的光缆段2
     */
    public static void doMergeWireSeg(WireSeg segone, WireSeg segtwo,BoActionContext actionContext) {
        //1.判断合并点 是否相同:第一段的连接终点与第二连接起点相同；/第二段的连接终点与第一连接起点相同；
        GenericDO origone = (GenericDO) segone.getAttrValue(WireSeg.AttrName.origPointCuid);
        GenericDO destone = (GenericDO) segone.getAttrValue(WireSeg.AttrName.destPointCuid);
        GenericDO origtwo = (GenericDO) segtwo.getAttrValue(WireSeg.AttrName.origPointCuid);
        GenericDO desttwo = (GenericDO) segtwo.getAttrValue(WireSeg.AttrName.destPointCuid);
        if (origone != null && destone != null && origtwo != null && desttwo != null) {
            //分四种情况: 先把顺序都整理好,然后调用同一个处理合并的方法.合并后把起止点列出来
            //A-B,B-C;      // A-B,C-B;    // A-B,A-C;       // A-B,C-A
            //1判断是否有合并点,
            int count = 0; //    String satesize = "";
            //在最开始给要反序的段设置上标记
            if (((origone.getCuid()).equals(origtwo.getCuid()))) {
                count = count + 1; //  satesize = "ABAC";
                segone.setAttrValue(changesate, changesate);
                segtwo.setAttrValue(changesate, "");
            } else if (((origone.getCuid()).equals(desttwo.getCuid()))) {
                count = count + 1;
//                WireSeg seg = segone;
//                segone = segtwo;
//                segtwo = seg; //交换段1 和段2//  satesize = "ABBC"; //"CAAB"  ;//"ABCA";
                segone.setAttrValue(changesate, changesate);
                segtwo.setAttrValue(changesate, changesate);
            } else if (((destone.getCuid()).equals(origtwo.getCuid()))) {
                count = count + 1; //   satesize = "ABBC";
                segone.setAttrValue(changesate, "");
                segtwo.setAttrValue(changesate, "");
            } else if (((destone.getCuid()).equals(desttwo.getCuid()))) {
                count = count + 1; //  satesize = "ABCB";
                segone.setAttrValue(changesate, "");
                segtwo.setAttrValue(changesate, changesate);
            }
            //保证都把第二段加到第一段上
            if (count > 1) {
                JOptionPane.showMessageDialog(null, "选择多个光缆段，不能进行合并!");
                return;
            } else if (count == 0) {
                JOptionPane.showMessageDialog(null, "选择的光缆段没有合并点，不能进行合并!");
                return;
            } else if (count == 1) { //存在一个合并点的时候,继续,否则提示不能合并
                compareFiber(segone, segtwo, destone, desttwo, origone, origtwo,actionContext);
            }
        }
    }
    
	private static void compareFiber(WireSeg segone, WireSeg segtwo, GenericDO destone, GenericDO desttwo, GenericDO origone, GenericDO origtwo,BoActionContext actionContext) {
		// 判断纤芯 个数,根据编号判断状态
		DataObjectList fiberone = null;
		try {
			fiberone = searchfiber(segone.getCuid()); // 根据 段CUID查光纤
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		DataObjectList fibertwo = null;
		try {
			fibertwo = searchfiber(segtwo.getCuid());
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}
		if (fiberone != null && fibertwo != null) {
			if (fiberone.size() != fibertwo.size()) { // 纤芯个数 不同
				throw new UserException("纤芯个数不同，不可合并！");
			} else { // 纤芯个数 一致
				DataObjectList origendlist = new DataObjectList(); // 合并后生成的 头尾点取出.
				if (segone.getAttrValue(changesate).equals(changesate)) {
					origendlist.add(destone);
				} else {
					origendlist.add(origone);
				}
				if (segtwo.getAttrValue(changesate).equals(changesate)) {
					origendlist.add(origtwo);
				} else {
					origendlist.add(desttwo);
				}
				try {
					doJoineWireSeg(origendlist, segone, segtwo, fiberone, fibertwo,actionContext); // 执行合并origone, desttwo,
				} catch (Exception ex2) {
					ex2.printStackTrace();
					throw new UserException("合并纤芯失败！");
				}
			}
		}
	}
	
    private static DataObjectList searchfiber(String segcuid) throws Exception { //根据段CUID查光纤
        DataObjectList fiberone = null;
        try {
            fiberone = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.getFibersByWireSegCuid,
                new BoActionContext(), segcuid);
            return fiberone;
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }
    
	private static void doJoineWireSeg(DataObjectList origendlist, WireSeg segone, WireSeg segtwo, DataObjectList fiberone, DataObjectList fibertwo,BoActionContext actionContext) throws Exception { // 执行合并
		// 合并点,段一,段二,被修改的点,修改后要放入的点,一段下光纤,二段下光纤 GenericDO modify, GenericDO modifynew,
		String middlePointCuid = getMiddlePointCuid(origendlist, segone);
		double segOneLength = segone.getLength();
		double segTwoLength = segtwo.getLength();
		if (segone.getAttrValue(changesate).equals("") && segtwo.getAttrValue(changesate).equals("")) { // ABBC
			String fiber1DestPointCuid = "";
			String fiber2OrigPointCuid = "";
			String destSiteCuid = "";
			Object seg_destPtCuid = (Object) segtwo.getAttrValue(WireSeg.AttrName.destPointCuid);
			if (seg_destPtCuid instanceof GenericDO) {
				destSiteCuid = ((GenericDO) seg_destPtCuid).getCuid();
			} else if (seg_destPtCuid instanceof String) {
				destSiteCuid = (String) seg_destPtCuid;
			}
			for (int i = 0; i < fiberone.size(); i++) {
				Fiber fiber = (Fiber) fiberone.get(i);
				fiber1DestPointCuid = fiber.getAttrString(Fiber.AttrName.destPointCuid);
				if (fiber1DestPointCuid == null || "".equals(fiber1DestPointCuid)) {
					fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
					fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
					fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
				} else {
					boolean isCon = false;
					for (int j = 0; j < fibertwo.size(); j++) {
						Fiber fiber2 = (Fiber) fibertwo.get(j);
						fiber2OrigPointCuid = fiber2.getAttrString(Fiber.AttrName.origPointCuid);
						if (fiber2OrigPointCuid == null || "".equals(fiber2OrigPointCuid)) {
							continue;
						} else if (fiber1DestPointCuid.equals(fiber2OrigPointCuid)) {
							fiber.setAttrValue(Fiber.AttrName.destPointCuid, fiber2.getAttrValue(Fiber.AttrName.destPointCuid));
							fiber.setAttrValue(Fiber.AttrName.destSiteCuid, fiber2.getAttrValue(Fiber.AttrName.destSiteCuid));
							fiber.setAttrValue(Fiber.AttrName.destEqpCuid, fiber2.getAttrValue(Fiber.AttrName.destEqpCuid));
							isCon = true;
							break;
						}
					}
					if (!isCon) {
						fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
						fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
						fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
					}
				}
				fiber.setLength(segOneLength + segTwoLength);
			}
		} else if (segone.getAttrValue(changesate).equals("") && segtwo.getAttrValue(changesate).equals(changesate)) { // ABCB
			String fiber1DestPointCuid = "";
			String fiber2DestPointCuid = "";
			String destSiteCuid = "";
			Object seg_OrigPtCuid = segtwo.getAttrValue(WireSeg.AttrName.origPointCuid);

			if (seg_OrigPtCuid instanceof GenericDO) {
				destSiteCuid = ((GenericDO) seg_OrigPtCuid).getCuid();
			} else if (seg_OrigPtCuid instanceof String) {
				destSiteCuid = (String) seg_OrigPtCuid;
			}
			for (int i = 0; i < fiberone.size(); i++) {
				Fiber fiber = (Fiber) fiberone.get(i);
				fiber1DestPointCuid = fiber.getAttrString(Fiber.AttrName.destPointCuid);
				if (fiber1DestPointCuid == null || "".equals(fiber1DestPointCuid)) {
					fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
					fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
					fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
				} else {
					boolean isCon = false;
					for (int j = 0; j < fibertwo.size(); j++) {
						Fiber fiber2 = (Fiber) fibertwo.get(j);
						fiber2DestPointCuid = fiber2.getAttrString(Fiber.AttrName.destPointCuid);
						if (fiber2DestPointCuid == null || "".equals(fiber2DestPointCuid)) {
							continue;
						} else if (fiber1DestPointCuid.equals(fiber2DestPointCuid)) {
							fiber.setAttrValue(Fiber.AttrName.destPointCuid, fiber2.getAttrValue(Fiber.AttrName.origPointCuid));
							fiber.setAttrValue(Fiber.AttrName.destSiteCuid, fiber2.getAttrValue(Fiber.AttrName.origSiteCuid));
							fiber.setAttrValue(Fiber.AttrName.destEqpCuid, fiber2.getAttrValue(Fiber.AttrName.origEqpCuid));
							isCon = true;
							break;
						}
					}
					if (!isCon) {
						fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
						fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
						fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
					}
				}
				fiber.setLength(segOneLength + segTwoLength);
			}
		} else if (segone.getAttrValue(changesate).equals(changesate)
				&& segtwo.getAttrValue(changesate).equals("")) { // ABAC

			String destSiteCuid = "";
			Object seg_destPtCuid = (Object) segtwo.getAttrValue(WireSeg.AttrName.destPointCuid);
			if (seg_destPtCuid instanceof GenericDO) {
				destSiteCuid = ((GenericDO) seg_destPtCuid).getCuid();
			} else if (seg_destPtCuid instanceof String) {
				destSiteCuid = (String) seg_destPtCuid;
			}
			for (int i = 0; i < fiberone.size(); i++) {
				Fiber fiber = (Fiber) fiberone.get(i);
				String tempdestPointCuid = fiber.getAttrString(Fiber.AttrName.destPointCuid);
				String tempdestSiteCuid = fiber.getAttrString(Fiber.AttrName.destSiteCuid);
				String tempdestEqpCuid = fiber.getAttrString(Fiber.AttrName.destEqpCuid);

				String fiber1OrigPointCuid = fiber.getAttrString(Fiber.AttrName.origPointCuid);
				if (fiber1OrigPointCuid == null || "".equals(fiber1OrigPointCuid)) {
					fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
					fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
					fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
				} else {
					boolean isCon = false;
					for (int j = 0; j < fibertwo.size(); j++) {
						Fiber fiber2 = (Fiber) fibertwo.get(j);
						String fiber2OrigPointCuid = fiber2.getAttrString(Fiber.AttrName.origPointCuid);
						if (fiber2OrigPointCuid == null || "".equals(fiber2OrigPointCuid)) {
							continue;
						} else if (fiber1OrigPointCuid.equals(fiber2OrigPointCuid)) {
							fiber.setAttrValue(Fiber.AttrName.destPointCuid, fiber2.getAttrValue(Fiber.AttrName.destPointCuid));
							fiber.setAttrValue(Fiber.AttrName.destSiteCuid, fiber2.getAttrValue(Fiber.AttrName.destSiteCuid));
							fiber.setAttrValue(Fiber.AttrName.destEqpCuid, fiber2.getAttrValue(Fiber.AttrName.destEqpCuid));
							isCon = true;
							break;
						}
					}
					if (!isCon) {
						fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
						fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
						fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
					}
				}

				fiber.setAttrValue(Fiber.AttrName.origPointCuid, tempdestPointCuid);
				fiber.setAttrValue(Fiber.AttrName.origSiteCuid, tempdestSiteCuid);
				fiber.setAttrValue(Fiber.AttrName.origEqpCuid, tempdestEqpCuid);
				fiber.setLength(segOneLength + segTwoLength);
			}
		} else if (segone.getAttrValue(changesate).equals(changesate) && segtwo.getAttrValue(changesate).equals(changesate)) { // ABCA
			String destSiteCuid = "";
			Object seg_OrigPtCuid = segtwo.getAttrValue(WireSeg.AttrName.origPointCuid);
			if (seg_OrigPtCuid instanceof GenericDO) {
				destSiteCuid = ((GenericDO) seg_OrigPtCuid).getCuid();
			} else if (seg_OrigPtCuid instanceof String) {
				destSiteCuid = (String) seg_OrigPtCuid;
			}
			for (int i = 0; i < fiberone.size(); i++) {
				Fiber fiber = (Fiber) fiberone.get(i);
				String tempdestPointCuid = fiber.getAttrString(Fiber.AttrName.destPointCuid);
				String tempdestSiteCuid = fiber.getAttrString(Fiber.AttrName.destSiteCuid);
				String tempdestEqpCuid = fiber.getAttrString(Fiber.AttrName.destEqpCuid);
				String fiber1OrigPointCuid = fiber.getAttrString(Fiber.AttrName.origPointCuid);

				if (fiber1OrigPointCuid == null || "".equals(fiber1OrigPointCuid)) {
					fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
					fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
					fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
				} else {
					boolean isCon = false;
					for (int j = 0; j < fibertwo.size(); j++) {
						Fiber fiber2 = (Fiber) fibertwo.get(j);
						String fiber2DestPointCuid = fiber2.getAttrString(Fiber.AttrName.destPointCuid);
						if (fiber2DestPointCuid == null || "".equals(fiber2DestPointCuid)) {
							continue;
						} else if (fiber1OrigPointCuid.equals(fiber2DestPointCuid)) {
							fiber.setAttrValue(Fiber.AttrName.destPointCuid, fiber2.getAttrValue(Fiber.AttrName.origPointCuid));
							fiber.setAttrValue(Fiber.AttrName.destSiteCuid, fiber2.getAttrValue(Fiber.AttrName.origSiteCuid));
							fiber.setAttrValue(Fiber.AttrName.destEqpCuid, fiber2.getAttrValue(Fiber.AttrName.origEqpCuid));
							isCon = true;
							break;
						}
					}
					if (!isCon) {
						fiber.setAttrValue(Fiber.AttrName.destPointCuid, "");
						fiber.setAttrValue(Fiber.AttrName.destSiteCuid, destSiteCuid);
						fiber.setAttrValue(Fiber.AttrName.destEqpCuid, "");
					}
				}
				fiber.setAttrValue(Fiber.AttrName.origPointCuid, tempdestPointCuid);
				fiber.setAttrValue(Fiber.AttrName.origSiteCuid, tempdestSiteCuid);
				fiber.setAttrValue(Fiber.AttrName.origEqpCuid, tempdestEqpCuid);
				fiber.setLength(segOneLength + segTwoLength);
			}
		}
		DataObjectList fiberOneList = new DataObjectList();
		fiberOneList.addAll(fiberone);
		DataObjectList fiberTwoList = new DataObjectList();
		fiberTwoList.addAll(fibertwo);

		if (fibertwo.size() > 0) {
			try {
				DataObjectList deletefibers = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
						FiberBOHelper.ActionName.deletefibers, actionContext, fibertwo); // 删除原来第二段的纤芯,处理LIST批量
			} catch (Exception ex) {
				LogHome.getLog().error("光缆段合并时删除纤芯出错", ex);
			}
		}
		if (fiberone.size() > 0) {
			try {
				DataObjectList modifyfibers = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
						"IFiberBO.modifyFibersForMergeWireSeg", actionContext, fiberone);
			} catch (Exception e) {
				LogHome.getLog().error("光缆段合并时修改纤芯出错", e);
			}
		}
		DataObjectList optical1List = new DataObjectList();
		DataObjectList optical2List = new DataObjectList();
		DataObjectList opticalList = new DataObjectList();
		Map<String, DataObjectList> fiberOptical2Map = new HashMap<String, DataObjectList>();
		for (int i = 0; i < fiberTwoList.size(); i++) {
			Fiber f1 = (Fiber) fiberOneList.get(i);
			Fiber f2 = (Fiber) fiberTwoList.get(i);
			optical1List = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					OpticalBOHelper.ActionName.getOpticalByFiberCuid, actionContext, f1.getCuid());
			optical2List = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					OpticalBOHelper.ActionName.getOpticalByFiberCuid, actionContext, f2.getCuid());
			if (optical1List != null && optical1List.size() > 0) {
				opticalList.addAll(optical1List);
			}
			if (optical2List != null && optical2List.size() > 0) {
				opticalList.addAll(optical2List);
				fiberOptical2Map.put(f2.getCuid(), optical2List);
			}
		}
		Set<Map.Entry<String, DataObjectList>> set = fiberOptical2Map.entrySet();
		Iterator<Map.Entry<String, DataObjectList>> iterator = set.iterator();
		DataObjectList opticalToFiberList = new DataObjectList();
		while (iterator.hasNext()) {
			Map.Entry<String, DataObjectList> entry = iterator.next();
			String fiberCuid = entry.getKey();
			DataObjectList list = entry.getValue();
			for (int i = 0, size = list.size(); i < size; i++) {
				String sql = " OPTICAL_CUID='" + list.get(i).getCuid() + "' AND FIBER_CUID='" + fiberCuid + "'";
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
				DataObjectList tempList = ductmanagerBo.getObjectsBySql(sql, new OpticalToFiber());
				opticalToFiberList.addAll(tempList);
			}
		}
		// 删除optialToFiber表中的数据
		BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteObjects, actionContext, opticalToFiberList);
		deleteJumpFibers(middlePointCuid, fiberTwoList, fiberOptical2Map);
		// 修改光纤的状态
		for (int i = 0; i < opticalList.size(); i++) {
			Optical optical = (Optical) opticalList.get(i);
			optical.setMakeFlag(1L);
		}
		BoCmdFactory.getInstance().execBoCmd(OpticalBOHelper.ActionName.modifyOpticals, actionContext, opticalList);
		// 6、合并后光缆段的长度是原光缆段长度之和,
		Double seglength = segone.getLength();
		Double seglength2 = segtwo.getLength();
		segone.setAttrValue(WireSeg.AttrName.length, (seglength + seglength2));
		// 具体路由:两个段具体路由之和.BO查段下的具体路由段:WIRETODUCTLINE
		List wireseg1 = (List) BoCmdFactory.getInstance().execBoCmd(
				WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg, actionContext, segone); // 根据一段CUID查Wiretoductline得:具体路由段:wireseg1
		List wireseg2 = (List) BoCmdFactory.getInstance().execBoCmd(
				WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg, actionContext, segtwo); // 根据二段查Wiretoductline得:具体路由段:wireseg2
		if (wireseg1 != null && wireseg2 != null) {
			DataObjectList w2d1 = new DataObjectList();
			w2d1.addAll(wireseg1);
			w2d1.sort(WireToDuctline.AttrName.indexInRoute, true);
			if (segone.getAttrValue(changesate).equals(changesate)) {
				Collections.reverse(w2d1); // 倒序 = B-A;A-C
				for (int i = 0; i < w2d1.size(); i++) { // 一段颠倒顺序后,序号要修改
					WireToDuctline line1 = (WireToDuctline) w2d1.get(i);
					long line1Direction = 0 == line1.getDirection() ? 1 : line1.getDirection();
					line1.setDirection(3 - line1Direction);
				}
			}
			for (int i = 0; i < w2d1.size(); i++) { // 一段颠倒顺序后,序号要修改
				WireToDuctline line1 = (WireToDuctline) w2d1.get(i);
				line1.setIndexInRoute((i + 1));
			}
			if (w2d1.size() > 0) {
				DataObjectList modifywtdback = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
						WireToDuctLineBOHelper.ActionName.modifyWireToDuctlines, actionContext, w2d1); // 修改一段的WIRETODUCTLINE
			}
			DataObjectList w2d2 = new DataObjectList();
			w2d2.addAll(wireseg2);
			w2d2.sort(WireToDuctline.AttrName.indexInRoute, true);
			if (segtwo.getAttrValue(changesate).equals(changesate)) {
				Collections.reverse(w2d2); // 倒序 = A-B;B-C
				for (int i = 0; i < w2d2.size(); i++) { // 一段颠倒顺序后,序号要修改
					WireToDuctline line1 = (WireToDuctline) w2d2.get(i);
					long line1Direction = 0 == line1.getDirection() ? 1 : line1.getDirection();
					line1.setDirection(3 - line1Direction);
				}
			}
			int wireSeg1DuctlineSize = w2d1.size();
			for (int i = 0; i < w2d2.size(); i++) {
				WireToDuctline line2 = (WireToDuctline) w2d2.get(i);
				line2.setIndexInRoute((wireSeg1DuctlineSize + i + 1));
				line2.setAttrValue(WireToDuctline.AttrName.wireSegCuid, segone.getCuid());
			}
			if (w2d2.size() > 0) {
				DataObjectList modifywtdback = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
						WireToDuctLineBOHelper.ActionName.modifyWireToDuctlines, actionContext, w2d2); // 修改二段的WIRETODUCTLINE
			}
		}
		// 显示路由为两个段显示路由之和 //在BO加个根据段查 显示路由的方法
		// 先根据段查出分支,再删除分支
		WireBranch newWirebranch1 = new WireBranch();
		if (segone.getAttrValue(WireSeg.AttrName.relatedBranchCuid) instanceof String) {
			String brcuid = (String) segone.getAttrValue(WireSeg.AttrName.relatedBranchCuid);
			String sqls = " CUID ='" + brcuid + "'";
			List linelist = (List) BoCmdFactory.getInstance().execBoCmd(
					WireBranchBOHelper.ActionName.getWireBranchsBySql, actionContext, sqls);
			newWirebranch1 = (WireBranch) linelist.get(0);
		} else if (segone.getAttrValue(WireSeg.AttrName.relatedBranchCuid) instanceof GenericDO) {
			GenericDO newbrdto1 = (GenericDO) segone.getAttrValue(WireSeg.AttrName.relatedBranchCuid);
			newWirebranch1.setObjectNum(newbrdto1.getObjectNum());
			newWirebranch1.setCuid(newbrdto1.getCuid());
		}
		// 用处:1.删除第二段的分支 2.根据分支查显示路由段
		WireBranch newWirebranch = new WireBranch();
		if (segtwo.getAttrValue(WireSeg.AttrName.relatedBranchCuid) instanceof String) {
			String brcuid = (String) segtwo.getAttrValue(WireSeg.AttrName.relatedBranchCuid);
			String sqls = " CUID ='" + brcuid + "'";
			List linelist = (List) BoCmdFactory.getInstance().execBoCmd(
					WireBranchBOHelper.ActionName.getWireBranchsBySql, actionContext, sqls);
			newWirebranch = (WireBranch) linelist.get(0);
		} else if (segtwo.getAttrValue(WireSeg.AttrName.relatedBranchCuid) instanceof GenericDO) {
			GenericDO newbrdto1 = (GenericDO) segtwo.getAttrValue(WireSeg.AttrName.relatedBranchCuid);
			newWirebranch.setObjectNum(newbrdto1.getObjectNum());
			newWirebranch.setCuid(newbrdto1.getCuid());
		}
		// 7、合并后光缆段的 预留处理合并到前一点. 直接根据列出的VIEW提示框中得到段的头尾点
		// 修改第一段,删除段时 同时删除分支, (合并的时候 要删除第2段)
		GenericDO origdto = (GenericDO) origendlist.get(0);
		GenericDO destdto = (GenericDO) origendlist.get(1);
		segone.setAttrValue(WireSeg.AttrName.origPointCuid, origdto);
		segone.setAttrValue(WireSeg.AttrName.destPointCuid, destdto);
		segone.setAttrValue(WireSeg.AttrName.labelCn, (origdto.getAttrValue(WireSeg.AttrName.labelCn).toString()
				+ "--" + destdto.getAttrValue(WireSeg.AttrName.labelCn).toString()));
//		GDMUtils.syschWireSegDisplayRoute(segone, true);//????????????
		// 处理预留点: 把第2段的 为预留点 修改放到第一段上.
		// 根据第2段光缆CUID,去查 该段下所有预留点,然后设置 点的RELATED_WIRE_SEG_CUID 为第1段的CUID
		DataObjectList wireSegRemain = new DataObjectList();
		DataObjectList modifywireSRemain = new DataObjectList();
		String seg2cuidsql = WireRemain.AttrName.relatedWireSegCuid + "= '" + segtwo.getCuid() + "'";
		try {
			wireSegRemain = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					WireRemainBOHelper.ActionName.getWireRemainBySql, actionContext, seg2cuidsql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (wireSegRemain != null && wireSegRemain.size() > 0) {
			for (int i = 0; i < wireSegRemain.size(); i++) {
				WireRemain remgdo = (WireRemain) wireSegRemain.get(i);
				remgdo.setAttrValue(WireRemain.AttrName.relatedWireSegCuid, segone.getCuid()); // 预留点 修改放到第一段上.
				modifywireSRemain.add(remgdo); // 修改入库
			}
		}
		try {
			wireSegRemain = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					WireRemainBOHelper.ActionName.modifyWireRemains, actionContext, modifywireSRemain);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		WireSeg wireSegback = (WireSeg) BoCmdFactory.getInstance().execBoCmd(
				WireSegBOHelper.ActionName.modifyWireSeg, actionContext, segone); // 修改原来第一段
		BoCmdFactory.getInstance().execBoCmd(
				WireSegBOHelper.ActionName.deleteWireSeg, actionContext, segtwo); // 删除原来第二段
		BoCmdFactory.getInstance().execBoCmd(
				WireBranchBOHelper.ActionName.deleteWireBranch, actionContext, newWirebranch); // 删除原来第二分支
	}
    
	private static String getMiddlePointCuid(DataObjectList origendList, WireSeg segone) {
		GenericDO origGdo = origendList.get(0);
		GenericDO destGdo = origendList.get(1);
		String origGdoCuid = origGdo.getCuid();
		String destGdoCuid = destGdo.getCuid();
		String origCuid = new String();
		String destCuid = new String();
		Object origObj = segone.getAttrValue(WireSeg.AttrName.origPointCuid);
		if (origObj instanceof GenericDO) {
			origCuid = ((GenericDO) origObj).getCuid();
		} else if (origObj instanceof String) {
			origCuid = origObj.toString();
		}
		Object destObj = segone.getAttrValue(WireSeg.AttrName.destPointCuid);
		if (destObj instanceof GenericDO) {
			destCuid = ((GenericDO) destObj).getCuid();
		} else if (destObj instanceof String) {
			destCuid = destObj.toString();
		}
		if (origCuid.equals(origGdoCuid) || origCuid.equals(destGdoCuid)) {
			return destCuid;
		}
		return origCuid;
	}

	private static void deleteJumpFibers(String ptCuid, DataObjectList deleteFibers, Map<String, DataObjectList> fiberOptical2Map) throws Exception {
		List<String> list = new ArrayList<String>();
		for (GenericDO fiber : deleteFibers) {
			String origSiteCuid = fiber.getAttrString(Fiber.AttrName.origSiteCuid);
			String destSiteCuid = fiber.getAttrString(Fiber.AttrName.destSiteCuid);
			if (ptCuid.equals(origSiteCuid)) {
				String origPtCuid = fiber.getAttrString(Fiber.AttrName.origPointCuid);
				if (origPtCuid != null && !"".equals(origPtCuid)) {
					list.add(origPtCuid);
				}
			}
			if (ptCuid.equals(destSiteCuid)) {
				String destPtCuid = fiber.getAttrString(Fiber.AttrName.destPointCuid);
				if (destPtCuid != null && !"".equals(destPtCuid)) {
					list.add(destPtCuid);
				}
			}
		}
		DataObjectList allFiberList = new DataObjectList();
		for (String cuid : list) {
			DataObjectList tempList = new DataObjectList();
			tempList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					JumpFiberBOHelper.ActionName.getJumpFiberByPointCuid, new BoActionContext(), cuid);
			if (tempList != null && tempList.size() > 0) {
				allFiberList.addAll(tempList);
			}
		}
		DataObjectList allOpticals = new DataObjectList();
		Collection<DataObjectList> collection = fiberOptical2Map.values();
		Iterator<DataObjectList> iterator = collection.iterator();
		while (iterator.hasNext()) {
			allOpticals.addAll(iterator.next());
		}
		DataObjectList opticalToFiberList = new DataObjectList();
		for (int i = 0; i < allFiberList.size(); i++) {
			String fiberCuid = allFiberList.get(i).getCuid();
			for (int j = 0; j < allOpticals.size(); j++) {
				String opticalCuid = allOpticals.get(j).getCuid();
				String sql = " OPTICAL_CUID='" + opticalCuid + "' AND FIBER_CUID='" + fiberCuid + "'";
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
				DataObjectList tempList = ductmanagerBo.getObjectsBySql(sql, new OpticalToFiber());
				if (tempList != null && tempList.size() > 0) {
					opticalToFiberList.addAll(tempList);
				}
			}
		}
		if (opticalToFiberList.size() > 0) {
			BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteObjects, new BoActionContext(), opticalToFiberList);
		}
		if (allFiberList != null && allFiberList.size() > 0) {
			BoCmdFactory.getInstance().execBoCmd(JumpFiberBOHelper.ActionName.deleteJumpFiberBatch, new BoActionContext(), allFiberList);
		}
	}
	
    private IDuctManagerBO getDuctManagerBO() {
        return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
    }
}
