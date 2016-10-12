package com.boco.gis.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.PolewayBranch;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.StonewayBranch;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.dm.PolewayBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewayBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;

public class MergePoleStoneBranchsHandler {
	
	public static Map unitePolewayBranch(DataObjectList branchlist, String splitpointCuid, DataObjectList seglist) throws Exception {
		
		BoActionContext actionContext = ActionContextUtil.getActionContext();
		
		String branch0cuid = "";
		try {
			String branchcuid = "";
			for (int i = 0; i < branchlist.size(); i++) {
				if (branchcuid.length() == 0) {
					branch0cuid = branchlist.get(i).getCuid();
					branchcuid = PolewayBranch.AttrName.cuid + " = '" + branchlist.get(i).getCuid() + "'";
				} else {
					branchcuid = branchcuid + " or " + PolewayBranch.AttrName.cuid + " = '" + branchlist.get(i).getCuid() + "'";
				}
			}
			branchcuid = "";
			for (int i = 0; i < branchlist.size(); i++) {
				if (branchcuid.length() == 0) {
					branchcuid = PolewaySeg.AttrName.relatedBranchCuid + " = '" + branchlist.get(i).getCuid() + "'";
				} else {
					branchcuid = branchcuid + " or " + PolewaySeg.AttrName.relatedBranchCuid + " = '" + branchlist.get(i).getCuid() + "'";
				}
			}
		} catch (Exception ex) {
			LogHome.getLog() .error("" + "branchcuid" + "", ex);
		}
		GenericDO branch0 = branchlist.get(0);
		GenericDO branch1 = branchlist.get(1);
		
		String branch0dest = "";
		String branch0orig = "";
		String branch1dest = "";
		String branch1orig = "";

		if (branch0.getAttrValue(PolewayBranch.AttrName.destPointCuid) instanceof String) {
			branch0dest = (String) branch0.getAttrValue(PolewayBranch.AttrName.destPointCuid);
			branch0orig = (String) branch0.getAttrValue(PolewayBranch.AttrName.origPointCuid);
			branch1dest = (String) branch1.getAttrValue(PolewayBranch.AttrName.destPointCuid);
			branch1orig = (String) branch1.getAttrValue(PolewayBranch.AttrName.origPointCuid);
		} else {
			GenericDO branch0destgdo = (GenericDO) branch0.getAttrValue(PolewayBranch.AttrName.destPointCuid);
			branch0dest = branch0destgdo.getCuid();
			GenericDO branch0origgdo = (GenericDO) branch0.getAttrValue(PolewayBranch.AttrName.origPointCuid);
			branch0orig = branch0origgdo.getCuid();
			GenericDO branch1destgdo = (GenericDO) branch1.getAttrValue(PolewayBranch.AttrName.destPointCuid);
			branch1dest = branch1destgdo.getCuid();
			GenericDO branch1origgdo = (GenericDO) branch1.getAttrValue(PolewayBranch.AttrName.origPointCuid);
			branch1orig = branch1origgdo.getCuid();
		}
		Map<String, String> branchorigmap = new HashMap<String, String>();
		if (branchorigmap.get(branch0dest) == null) {
			branchorigmap.put(branch0dest, "D");
		} else {
			String disction = branchorigmap.get(branch0dest);
			branchorigmap.put(branch0dest, disction + "D");
		}
		if (branchorigmap.get(branch1dest) == null) {
			branchorigmap.put(branch1dest, "D");
		} else {
			String disction = branchorigmap.get(branch1dest);
			branchorigmap.put(branch1dest, disction + "D");
		}

		if (branchorigmap.get(branch0orig) == null) {
			branchorigmap.put(branch0orig, "O");
		} else {
			String disction = branchorigmap.get(branch0orig);
			branchorigmap.put(branch0orig, disction + "O");
		}

		if (branchorigmap.get(branch1orig) == null) {
			branchorigmap.put(branch1orig, "O");
		} else {
			String disction = branchorigmap.get(branch1orig);
			branchorigmap.put(branch1orig, disction + "O");
		}
		String distrion = "";
		if (branchorigmap.get(splitpointCuid) != null) {
			distrion = branchorigmap.get(splitpointCuid);
		}
		DataObjectList branch0seg = new DataObjectList();
		DataObjectList branch1seg = new DataObjectList();

		for (int s = 0; s < seglist.size(); s++) {
			String cuid = "";
			if (seglist.get(s).getAttrValue(PolewaySeg.AttrName.relatedBranchCuid) instanceof GenericDO) {
				GenericDO gdo = (GenericDO) seglist.get(s).getAttrValue(PolewaySeg.AttrName.relatedBranchCuid);
				cuid = gdo.getCuid();
			} else {
				cuid = (String) seglist.get(s).getAttrValue(PolewaySeg.AttrName.relatedBranchCuid);
			}
			if (cuid.equals(branch0.getCuid())) {
				branch0seg.add(seglist.get(s));
			} else if (cuid.equals(branch1.getCuid())) {
				branch1seg.add(seglist.get(s));
			}
		}
		String uniteBranchsql = "";
		for (int i = 0; i < branchlist.size(); i++) {
			Object lineSystem = branchlist.get(i).getAttrValue(PolewaySeg.AttrName.relatedSystemCuid);
			String lineSystemCuid = DMHelper.getRelatedCuid(lineSystem);
			if (uniteBranchsql.equals("")) {
				uniteBranchsql = "(" + WireToDuctline.AttrName.lineSystemCuid + " ='" + lineSystemCuid + "' AND "
						+ WireToDuctline.AttrName.lineBranchCuid + " ='" + branchlist.get(i).getCuid() + "')";
			} else {
				uniteBranchsql = uniteBranchsql + " or (" + WireToDuctline.AttrName.lineSystemCuid + " ='" + lineSystemCuid + "' AND "
						+ WireToDuctline.AttrName.lineBranchCuid + " ='" + branchlist.get(i).getCuid() + "')";
			}
		}
		
		DataObjectList wireToDuctlinelist = new DataObjectList();
		wireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
				WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, uniteBranchsql);
		
		// 这里是判断合并点方向以及合并是的顺序的.
		GenericDO savebranch = null;
		GenericDO delectbranch = null;
		DataObjectList saveList = new DataObjectList();
		DataObjectList modifyList = new DataObjectList();
		if (distrion.equals("DD")) {
			branch1seg.sort(PolewaySeg.AttrName.indexInBranch, true);
			Collections.reverse(branch1seg);
			savebranch = branch0;
			delectbranch = branch1;
			for (int i = 0; i < branch1seg.size(); i++) {
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch, new Long(i + 1 + branch0seg.size()));
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.relatedBranchCuid, branch0.getCuid());
				GenericDO origgod = (GenericDO) branch1seg.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
				GenericDO destgod = (GenericDO) branch1seg.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.origPointCuid, destgod);
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.destPointCuid, origgod);
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.labelCn, destgod.getAttrString(GenericDO.AttrName.labelCn) 
						+ "---" + origgod.getAttrString(GenericDO.AttrName.labelCn));
				List wiretoductline = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.lineSegCuid, branch1seg.get(i).getCuid());
				if (wiretoductline != null && wiretoductline.size() > 0) {
					for (int w = 0; w < wiretoductline.size(); w++) {
						GenericDO gdo = (GenericDO) wiretoductline.get(w);
						long direc = gdo.getAttrLong(WireToDuctline.AttrName.direction);
						gdo.setAttrValue(WireToDuctline.AttrName.disPointCuid, destgod.getCuid());
						gdo.setAttrValue(WireToDuctline.AttrName.endPointCuid, origgod.getCuid());
						gdo.setAttrValue(WireToDuctline.AttrName.lineBranchCuid, savebranch.getCuid());
						if (direc == 1) {
							gdo.setAttrValue(WireToDuctline.AttrName.direction, 2);
						} else if (direc == 2) {
							gdo.setAttrValue(WireToDuctline.AttrName.direction, 1);
						}
					}
				}
			}
			branch0seg.addAll(branch1seg);
			branch0seg.sort(PolewaySeg.AttrName.indexInBranch, true);
			saveList = branch0seg;
			modifyList.addAll(branch1seg);
		} else if (distrion.equals("OO")) {
			branch1seg.sort(PolewaySeg.AttrName.indexInBranch, true);
			Collections.reverse(branch1seg);
			savebranch = branch0;
			delectbranch = branch1;
			for (int i = 0; i < branch1seg.size(); i++) {
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch, new Long(i + 1));
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.relatedBranchCuid, branch0.getCuid());
				GenericDO origgod = (GenericDO) branch1seg.get(i).getAttrValue(PolewaySeg.AttrName.origPointCuid);
				GenericDO destgod = (GenericDO) branch1seg.get(i).getAttrValue(PolewaySeg.AttrName.destPointCuid);
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.origPointCuid, destgod);
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.destPointCuid, origgod);
				branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.labelCn, destgod.getAttrString(GenericDO.AttrName.labelCn) 
						+ "---" + origgod.getAttrString(GenericDO.AttrName.labelCn));
				List wiretoductline = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.lineSegCuid, branch1seg.get(i).getCuid());
				if (wiretoductline != null && wiretoductline.size() > 0) {
					for (int w = 0; w < wiretoductline.size(); w++) {
						GenericDO gdo = (GenericDO) wiretoductline.get(w);
						long direc = gdo.getAttrLong(WireToDuctline.AttrName.direction);
						gdo.setAttrValue(WireToDuctline.AttrName.disPointCuid, destgod.getCuid());
						gdo.setAttrValue(WireToDuctline.AttrName.endPointCuid, origgod.getCuid());
						gdo.setAttrValue(WireToDuctline.AttrName.lineBranchCuid, savebranch.getCuid());
						if (direc == 1) {
							gdo.setAttrValue(WireToDuctline.AttrName.direction, 2);
						} else if (direc == 2) {
							gdo.setAttrValue(WireToDuctline.AttrName.direction, 1);
						}
					}
				}
			}
			for (int i = 0; i < branch0seg.size(); i++) {
				branch0seg.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch, new Long(i + 1 + branch1seg.size()));
			}
			branch0seg.addAll(branch1seg);
			branch0seg.sort(PolewaySeg.AttrName.indexInBranch, true);
			saveList = branch0seg;
			modifyList.addAll(branch1seg);
		} else if (distrion.equals("OD") || distrion.equals("DO")) {
			if (branch0dest.equals(splitpointCuid)) {
				branch1seg.sort(PolewaySeg.AttrName.indexInBranch, true);
				savebranch = branch0;
				delectbranch = branch1;
				for (int i = 0; i < branch1seg.size(); i++) {
					branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch, new Long(i + 1 + branch0seg.size()));
					branch1seg.get(i).setAttrValue(PolewaySeg.AttrName.relatedBranchCuid, branch0.getCuid());
					List wiretoductline = wireToDuctlinelist.getObjectByAttr(WireToDuctline.AttrName.lineSegCuid, branch1seg.get(i).getCuid());
					if (wiretoductline != null && wiretoductline.size() > 0) {
						for (int w = 0; w < wiretoductline.size(); w++) {
							GenericDO gdo = (GenericDO) wiretoductline.get(w);
							gdo.setAttrValue(WireToDuctline.AttrName.lineBranchCuid, savebranch.getCuid());
						}
					}
				}
				
				branch0seg.addAll(branch1seg);
				branch0seg.sort(PolewaySeg.AttrName.indexInBranch, true);
				saveList = branch0seg;
				modifyList.addAll(branch1seg);

			} else {
				savebranch = branch1;
				delectbranch = branch0;
				branch0seg.sort(PolewaySeg.AttrName.indexInBranch, true);
				for (int i = 0; i < branch0seg.size(); i++) {
					branch0seg.get(i).setAttrValue(PolewaySeg.AttrName.indexInBranch, new Long(i + 1 + branch1seg.size()));
					branch0seg.get(i).setAttrValue(PolewaySeg.AttrName.relatedBranchCuid, branch1.getCuid());
					List wiretoductline = wireToDuctlinelist.getObjectByAttr(
							WireToDuctline.AttrName.lineSegCuid, branch0seg.get(i).getCuid());
					if (wiretoductline != null && wiretoductline.size() > 0) {
						for (int w = 0; w < wiretoductline.size(); w++) {
							GenericDO gdo = (GenericDO) wiretoductline.get(w);
							gdo.setAttrValue(WireToDuctline.AttrName.lineBranchCuid, savebranch.getCuid());
						}
					}
				}
				branch0seg.addAll(branch1seg);
				branch0seg.sort(PolewaySeg.AttrName.indexInBranch, true);
				saveList = branch0seg;
				modifyList.addAll(branch0seg);
			}
		}
		// ----------wxin 得到wiretoductline序号
		for (GenericDO gdo : branch1seg) {
			List wiretoductline = wireToDuctlinelist.getObjectByAttr(
					WireToDuctline.AttrName.lineSegCuid, gdo.getCuid());
			if (wiretoductline != null && wiretoductline.size() > 0) {
				for (int w = 0; w < wiretoductline.size(); w++) {
					GenericDO wtdline = (GenericDO) wiretoductline.get(w);
					wtdline.setAttrValue(WireToDuctline.AttrName.indexInRoute, gdo.getAttrLong(PolewaySeg.AttrName.indexInBranch));
				}
			}
		}
		for (GenericDO gdo : branch0seg) {
			List wiretoductline = wireToDuctlinelist.getObjectByAttr(
					WireToDuctline.AttrName.lineSegCuid, gdo.getCuid());
			if (wiretoductline != null && wiretoductline.size() > 0) {
				for (int w = 0; w < wiretoductline.size(); w++) {
					GenericDO wtdline = (GenericDO) wiretoductline.get(w);
					wtdline.setAttrValue(WireToDuctline.AttrName.indexInRoute, gdo.getAttrLong(PolewaySeg.AttrName.indexInBranch));
				}
			}
		}
		GenericDO savebranchorig = null;
		GenericDO savebranchdest = null;
		if (saveList.size() > 1) {

			savebranchorig = (GenericDO) saveList.get(0).getAttrValue(PolewaySeg.AttrName.origPointCuid);
			savebranchdest = (GenericDO) saveList.get(saveList.size() - 1).getAttrValue(PolewaySeg.AttrName.destPointCuid);
			savebranch.setAttrValue(PolewayBranch.AttrName.origPointCuid, savebranchorig.getCuid());
			savebranch.setAttrValue(PolewayBranch.AttrName.destPointCuid, savebranchdest.getCuid());
			String name1 = (String) savebranchorig.getAttrValue("LABEL_CN");
			String name2 = (String) savebranchdest.getAttrValue("LABEL_CN");
			savebranch.setAttrValue(PolewayBranch.AttrName.labelCn, name1 + "--" + name2);
		}
		DataObjectList modifypolesegs = new DataObjectList();
		if (branch0cuid.contains(PolewayBranch.CLASS_NAME)) {
			uniteDisPlaySegtoBranch(null,savebranch, delectbranch);
			BoCmdFactory.getInstance().execBoCmd(PolewayBranchBOHelper.ActionName.modifyPolewayBranch, actionContext, savebranch);

			modifypolesegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
					PolewaySegBOHelper.ActionName.modifySplitPolewaySegs, actionContext, saveList);
			BoCmdFactory.getInstance().execBoCmd(PolewayBranchBOHelper.ActionName.deletePolewayBranchandone, actionContext, delectbranch);
		} else if (branch0cuid.contains(StonewayBranch.CLASS_NAME)) {
			uniteDisPlaySegtoBranch(null,savebranch, delectbranch);
			BoCmdFactory.getInstance().execBoCmd(StonewayBranchBOHelper.ActionName.modifyStonewayBranch, actionContext, savebranch);

			BoCmdFactory.getInstance().execBoCmd(StonewaySegBOHelper.ActionName.modifyStonewaySegs, actionContext, saveList);

			BoCmdFactory.getInstance().execBoCmd(StonewayBranchBOHelper.ActionName.deleteStonewayBranchone, actionContext, delectbranch);
		}
		BoCmdFactory.getInstance().execBoCmd(
				WireToDuctLineBOHelper.ActionName.modifyWireToDuctlines, actionContext, wireToDuctlinelist);

		savebranch.setAttrValue(PolewayBranch.AttrName.origPointCuid, savebranchorig);
		savebranch.setAttrValue(PolewayBranch.AttrName.destPointCuid, savebranchdest);
//		refreshSystemsBySeg(branchlist);
		Map map = new HashMap();
		map.put("savebranch", savebranch);
		map.put("delectbranch", delectbranch);
		for (int i = 0; i < modifypolesegs.size(); i++) {
			String cuid = modifypolesegs.get(i).getCuid();
			modifypolesegs.get(i).getObjectNum();
		}
		map.put("saveList", saveList);
		return map;
	}
	
	  /**
     *  合并分支的时候显示段处理方式
	 * @param request TODO
	 * @param branchlist DataObjectList
     */
    public static void uniteDisPlaySegtoBranch(HttpServletRequest request,GenericDO savegdo,GenericDO deletegdo) {
       
    	DmDesignerTools.setActionContext(new ServiceActionContext(request));
    	BoActionContext actionContext = DmDesignerTools.getActionContext();
    	
    	String savecuid = DMHelper.getRelatedCuid(savegdo);
        String deletecuid = DMHelper.getRelatedCuid(deletegdo);
        String sql = "";
        if(!(savecuid!=null &&savecuid.length()>0 &&deletecuid!=null &&deletecuid.length()>0)){
            return;
        }
        sql = " RELATED_BRANCH_CUID in( '" + savecuid + "','" + deletecuid + "')";
        DataObjectList deplayseglist = null;
        try {
            if (savecuid.contains(PolewayBranch.CLASS_NAME)) {
                deplayseglist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    PolewayDisplaySegBOHelper.ActionName.getPolewayDisplaySegBySql, actionContext, sql);
            } else if (savecuid.contains(StonewayBranch.CLASS_NAME)) {
                deplayseglist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    StonewayDisplaySegBOHelper.ActionName.getStonewayDisplaySegBySql, actionContext, sql);
            }

        } catch (Exception ex) {
            LogHome.getLog().error("" + "cuid" + "", ex);
        }
        List<GenericDO> delgdolist = deplayseglist.getObjectByAttr("RELATED_BRANCH_CUID",deletecuid);
        List<GenericDO> savegdolist = deplayseglist.getObjectByAttr("RELATED_BRANCH_CUID",savecuid);
        DataObjectList modfiyList = new DataObjectList();
        for(GenericDO modfiygdo :delgdolist){
            modfiyList.add(modfiygdo);
        }
        modfiyList.sort("INDEX_IN_BRANCH",true);
        long i=0;
        long saveindex = savegdolist.size();
        for(GenericDO modfiygdo :modfiyList){
            i++;
            modfiygdo.setAttrValue("RELATED_BRANCH_CUID", savecuid);
            modfiygdo.setAttrValue("INDEX_IN_BRANCH", saveindex + i);
        }
        try {
            if (savecuid.contains(PolewayBranch.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                        PolewayDisplaySegBOHelper.ActionName.modifyPolewayDisplaySegs, actionContext, modfiyList);
            } else if (savecuid.contains(StonewayBranch.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                        StonewayDisplaySegBOHelper.ActionName.modifyStonewayDisplaySegs, actionContext, modfiyList);
            }

        } catch (Exception ex) {
            LogHome.getLog().error("", ex);
        }

    }
}
