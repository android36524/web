package com.boco.gis.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.DuctBranch;
import com.boco.transnms.common.dto.DuctChildHole;
import com.boco.transnms.common.dto.DuctDisplaySeg;
import com.boco.transnms.common.dto.DuctHole;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.helper.dm.DuctBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctChildHoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctHoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;

public class MergeDuctBranchsHandler {
	private static final String changesate = "CHANGE";
	  /**
     * 合并给定的两条管道段
	 * @param request TODO
	 * @param segone 待合并的管道段1
	 * @param segtwo 待合并的管道段2
     */
    public static void doMergeDuctBranch(DuctBranch ductBranchone, DuctBranch ductBranchtwo) {
        Object origoneobj = ductBranchone.getAttrValue(DuctSeg.AttrName.origPointCuid);
        Object destoneobj = ductBranchone.getAttrValue(DuctSeg.AttrName.destPointCuid);
        Object origtwoobj = ductBranchtwo.getAttrValue(DuctSeg.AttrName.origPointCuid);
        Object desttwoobj = ductBranchtwo.getAttrValue(DuctSeg.AttrName.destPointCuid);
        BoActionContext context = ActionContextUtil.getActionContext();
        
        GenericDO origone = getRelatedDto(origoneobj);
        GenericDO destone = getRelatedDto(destoneobj);
        GenericDO origtwo = getRelatedDto(origtwoobj);
        GenericDO desttwo = getRelatedDto(desttwoobj);

        if (origone != null && destone != null && origtwo != null && desttwo != null) {
            //分四种情况: 先把顺序都整理好,然后调用同一个处理合并的方法.合并后把起止点列出来
            //1判断是否有合并点,
            int count = 0;
            //在最开始给要反序的段设置上标记 changesate
            if (((origone.getCuid()).equals(origtwo.getCuid()))) { // A-B,A-C;
                count = count + 1; 
                ductBranchone.setAttrValue(changesate, changesate);
                ductBranchtwo.setAttrValue(changesate, "");
            }
            if (((origone.getCuid()).equals(desttwo.getCuid()))) { // A-B,C-A
                count = count + 1;
                ductBranchone.setAttrValue(changesate, changesate);
                ductBranchtwo.setAttrValue(changesate, changesate);
            }
            if (((destone.getCuid()).equals(origtwo.getCuid()))) { //A-B,B-C;
                count = count + 1; 
                ductBranchone.setAttrValue(changesate, "");
                ductBranchtwo.setAttrValue(changesate, "");
            }
            if (((destone.getCuid()).equals(desttwo.getCuid()))) { // A-B,C-B;
                count = count + 1; 
                ductBranchone.setAttrValue(changesate, "");
                ductBranchtwo.setAttrValue(changesate, changesate);
            }
            //保证都把第二段加到第一段上
            if (count > 1) {
                JOptionPane.showMessageDialog(null, "有多个共同点，不能合并!");
                return;
            } else if (count == 0) {
                JOptionPane.showMessageDialog(null, "没有共同点，无法合并!");
                return;
            } else if (count == 1) { //存在一个合并点的时候,继续,否则提示不能合并
                DataObjectList origendlist = new DataObjectList(); //合并后生成的 头尾点取出.
                if (ductBranchone.getAttrValue(changesate).equals(changesate)) {
                    origendlist.add(destone);
                } else {
                    origendlist.add(origone);
                }
                if (ductBranchtwo.getAttrValue(changesate).equals(changesate)) {
                    origendlist.add(origtwo);
                } else {
                    origendlist.add(desttwo);
                }
                //能够合并分支的 进入方法进行处理
                doBranchJoint(origendlist, ductBranchone, ductBranchtwo,context);
            }
//            refreshSystemsByBranch(ductBranchone);
        }
    }
    
    private static GenericDO getRelatedDto(Object obj) {
        GenericDO dto = new GenericDO();
        if (obj instanceof String) {
            dto = MergeSHUSegsHandler.getObjectByCuid((String) obj);
        } else if (obj instanceof GenericDO) {
            dto = (GenericDO) obj;
        }
        return dto;
    }

    
    private static void doBranchJoint(DataObjectList origendlist, DuctBranch dbone, DuctBranch dbtwo,BoActionContext context) {
//        IView view = ViewFactory.getInstance().createView("SplitPointView", ShellFactoryName.MainModalDialogShellFactory);
//        ViewParam vp = new ViewParam();
//        vp.addViewParam("CHOOSE", true);
//        vp.addViewParam("POINTLIST", origendlist);
//        vp.addViewParam("VIEWNAME", PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.view.dm.system.duct.DuctSystemSegView.java8"));
//        try {
//            view.openView(vp);
//        } catch (Exception ex13) {
//            ex13.printStackTrace();
//        }
//        Object o = view.getViewResult();
//        if (o == null) { //关闭和取消,不做任何操作
//        }
//        if (o instanceof java.util.List) { //返回node
//            LinkedList LinkedList = (LinkedList) o;
//            if (LinkedList.size() == 0) {
//                return;
//            }
            //要考虑:显示路由点 displayseg表
            //根据分支1,分支2去分别得到两分支下的显示路由段 管道段display1,display2, 放入LIST
            //同时放入MAP,过滤掉重复的,剩下不重复的全部放入一LIST,然后循环修改 RELATE_branch_cuid,(因为我的是把2分支加到一分支上).
            DataObjectList brsegone = null;
            try {
                brsegone = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctBranchBOHelper.ActionName.getDuctDisplaySegInBranch,
                        context, dbone); //根据一DuctBranch查ductseg得: 显示路由段:brsegone
                brsegone.sort(DuctDisplaySeg.AttrName.indexInBranch, true);
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            DataObjectList brsegtwo = null;
            try {
                brsegtwo = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctBranchBOHelper.ActionName.getDuctDisplaySegInBranch,
                        context, dbtwo); //根据二DuctBranch查ductseg得: 显示路由段:brsegtwo
                brsegtwo.sort(DuctDisplaySeg.AttrName.indexInBranch, true);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
            
            if (brsegone != null && brsegtwo != null) {
                DataObjectList modifyDispalySegs = new DataObjectList();
//                int onechilds = brsegone.size(); //一分支的显示路由段数
                long oneChilds = Long.valueOf(brsegone.size());
                if (dbone.getAttrValue(changesate).equals(changesate)) {
                    for (int i = 0; i < brsegone.size(); i++) {
                        DuctDisplaySeg display = (DuctDisplaySeg) brsegone.get(i);
                        display.setIndexInBranch(oneChilds - i);
                        Object temp = display.getAttrValue(DuctDisplaySeg.AttrName.origPointCuid);
                        display.setAttrValue(DuctDisplaySeg.AttrName.origPointCuid, display.getAttrValue(DuctDisplaySeg.AttrName.destPointCuid));
                        display.setAttrValue(DuctDisplaySeg.AttrName.destPointCuid, temp);
                        switchDtoLabelCn(display);
                        modifyDispalySegs.add(display);
                    }
                }
                for (int i = 0; i < brsegtwo.size(); i++) {
                    DuctDisplaySeg display = (DuctDisplaySeg) brsegtwo.get(i);
                    display.setRelatedBranchCuid(dbone.getCuid());
                    if (dbtwo.getAttrValue(changesate).equals(changesate)) {
                        display.setIndexInBranch(oneChilds + brsegtwo.size() - i);
                        Object temp = display.getAttrValue(DuctDisplaySeg.AttrName.origPointCuid);
                        display.setAttrValue(DuctDisplaySeg.AttrName.origPointCuid, display.getAttrValue(DuctDisplaySeg.AttrName.destPointCuid));
                        display.setAttrValue(DuctDisplaySeg.AttrName.destPointCuid, temp);
                        switchDtoLabelCn(display);
                    } else {
                        display.setIndexInBranch(oneChilds + 1L + i);
                    }
                    modifyDispalySegs.add(display);
                }
                try { //入库
                    BoCmdFactory.getInstance().execBoCmd(
                            DuctDisplaySegBOHelper.ActionName.modifyDuctDisplaySegs,
                            context, modifyDispalySegs);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            //修改第一分支下的段
            List segonelist = MergeSHUSegsHandler.getSegsInBranch(dbone.getCuid(), null);
            DataObjectList modifwire2list = new DataObjectList(); //修改 Wiretoductline 后入库用
            for (int r = 0; r < segonelist.size(); r++) {
                DuctSeg segdton = (DuctSeg) segonelist.get(r);
                long segOneListSize = Long.valueOf(segonelist.size());
                //段颠倒顺序
                if (dbone.getAttrValue(changesate).equals(changesate)) {
                    Object segdto = segdton.getAttrValue(DuctSeg.AttrName.origPointCuid);
                    segdton.setAttrValue(DuctSeg.AttrName.origPointCuid, segdton.getAttrValue(DuctSeg.AttrName.destPointCuid));
                    segdton.setAttrValue(DuctSeg.AttrName.destPointCuid, segdto);
                    GenericDO begin = getRelatedDto(segdton.getAttrValue(DuctSeg.AttrName.origPointCuid));
                    GenericDO end = getRelatedDto(segdton.getAttrValue(DuctSeg.AttrName.destPointCuid));
                    segdton.setAttrValue(DuctSeg.AttrName.labelCn,
                            begin.getAttrValue(DuctSeg.AttrName.labelCn).toString() + "--" + end.getAttrValue(DuctSeg.AttrName.labelCn).toString());
                    segdton.setAttrValue(DuctSeg.AttrName.indexInBranch, segOneListSize - (long) r);
                    //同时修改 Wiretoductline 上的 头尾点 和Direction
                    String squl = WireToDuctline.AttrName.lineSegCuid + "='" + segdton.getCuid() + "'";
                    try {
                        DataObjectList wire2ductback = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
                                context, squl); //取出   下的 Wiretoductline
                        if (wire2ductback != null && wire2ductback.size() == 1) {
                            WireToDuctline wireline = (WireToDuctline) wire2ductback.get(0);
                            if (wireline.getDirection() == 1) {
                                wireline.setAttrValue(WireToDuctline.AttrName.direction, 2);
                            } else if (wireline.getDirection() == 2) {
                                wireline.setAttrValue(WireToDuctline.AttrName.direction, 1);
                            }
                            wireline.setAttrValue(WireToDuctline.AttrName.disPointCuid, segdton.getAttrValue(DuctSeg.AttrName.origPointCuid));
                            wireline.setAttrValue(WireToDuctline.AttrName.endPointCuid, segdton.getAttrValue(DuctSeg.AttrName.destPointCuid));
                            modifwire2list.add(wireline);
                        }
                    } catch (Exception ex7) {
                    }
                } else {
                }
                try {
                    DuctSeg segsback = (DuctSeg) BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.modifyDuctSeg,
                            context, segdton); //修改段入库
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                DataObjectList list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.modifyWireToDuctlines,
                        context, modifwire2list); //修改 Wiretoductline 后入库
                modifwire2list.clear();
            } catch (Exception ex8) {
            }

            int onechilds = segonelist.size(); //一分支的孩子数
            long oneChildsize = Long.valueOf(onechilds);
            DataObjectList modifwire2list2 = new DataObjectList(); //修改 Wiretoductline 后入库用
//            LinkedList segslist = (LinkedList) twonode.getChildren(); //第二条分支下的段
            List segslist = MergeSHUSegsHandler.getSegsInBranch(dbtwo.getCuid(), null);
            int segsize = segslist.size();
            long segSize = Long.valueOf(segsize);
            for (int r = segsize - 1; r >= 0; r--) { //把第二条分支下的段加到第一条分支下
                DuctSeg dto = (DuctSeg) segslist.get(r);
                dto.setAttrValue(DuctSeg.AttrName.relatedBranchCuid, dbone.getCuid());
                //设置了具体路由 修改第二条分支下的段的setRelatedBranchCuid
                dto.setAttrValue(DuctSeg.AttrName.indexInBranch, oneChildsize + (long) r + 1L);
                //段颠倒顺序
                if (dbtwo.getAttrValue(changesate).equals(changesate)) {
                    dto.setAttrValue(DuctSeg.AttrName.indexInBranch, oneChildsize + segSize - (long) r);
                    Object segdto = dto.getAttrValue(DuctSeg.AttrName.origPointCuid);
                    dto.setAttrValue(DuctSeg.AttrName.origPointCuid, dto.getAttrValue(DuctSeg.AttrName.destPointCuid));
                    dto.setAttrValue(DuctSeg.AttrName.destPointCuid, segdto);
                    GenericDO beg = getRelatedDto(dto.getAttrValue(DuctSeg.AttrName.origPointCuid));
                    GenericDO end = getRelatedDto(dto.getAttrValue(DuctSeg.AttrName.destPointCuid));
                    dto.setAttrValue(DuctSeg.AttrName.labelCn,
                            beg.getAttrValue(DuctSeg.AttrName.labelCn).toString() + "--" + end.getAttrValue(DuctSeg.AttrName.labelCn).toString());
                    //同时修改 Wiretoductline 上的 头尾点 和Direction
                    String squl = WireToDuctline.AttrName.lineSegCuid + "='" + dto.getCuid() + "'";
                    try {
                        DataObjectList wire2ductback = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
                                context, squl); //取出   下的 Wiretoductline
                        if (wire2ductback != null && wire2ductback.size() == 1) {
                            WireToDuctline wireline = (WireToDuctline) wire2ductback.get(0);
                            if (wireline.getDirection() == 1) {
                                wireline.setAttrValue(WireToDuctline.AttrName.direction, 2);
                            } else if (wireline.getDirection() == 2) {
                                wireline.setAttrValue(WireToDuctline.AttrName.direction, 1);
                            }
                            wireline.setAttrValue(WireToDuctline.AttrName.disPointCuid, dto.getAttrValue(DuctSeg.AttrName.origPointCuid));
                            wireline.setAttrValue(WireToDuctline.AttrName.endPointCuid, dto.getAttrValue(DuctSeg.AttrName.destPointCuid));
                            modifwire2list2.add(wireline);
                        }
                    } catch (Exception ex7) {
                    }
                } else {
                }
                String sql = DuctHole.AttrName.relatedSegCuid + "='" + dto.getCuid() + "'";
                DataObjectList holes = null;
                try { //修改新段下的管孔的Related_Path_Cuid
                    holes = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctHoleBOHelper.ActionName.getDuctHoleBySql,
                            context, sql); //得到段下的管孔
                } catch (Exception ex3) {
                }

                DataObjectList holebo = new DataObjectList(); //管孔集中入库
                DataObjectList childholebo = new DataObjectList(); //子孔集中入库
                for (int t = 0; t < holes.size(); t++) {
                    DuctHole ducthole = (DuctHole) holes.get(t);
                    ducthole.setAttrValue(DuctHole.AttrName.relatedPathCuid, dbone.getCuid());
                    holebo.add(ducthole);
                    //修改新段下的子孔的Related_Path_Cuid,根据管孔得子孔
                    String sql2 = DuctChildHole.AttrName.relatedHoleCuid + "='" + ducthole.getCuid() + "'";
                    DataObjectList childhole = null;
                    try {
                        childhole = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctChildHoleBOHelper.ActionName.getDuctChildHoleBySql,
                                context, sql2); //得到段下的管孔
                    } catch (Exception ex4) {
                    }
                    for (int n = 0; n < childhole.size(); n++) {
                        DuctChildHole ductchild = (DuctChildHole) childhole.get(n);
                        ductchild.setAttrValue(DuctChildHole.AttrName.relatedPathCuid, dbone.getCuid());
                        childholebo.add(ductchild);
                    }
                }

                try {
                    DataObjectList back = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctHoleBOHelper.ActionName.modifyDuctHoles,
                            context, holebo); //修改新段下的管孔的Related_Path_Cuid
                } catch (Exception ex5) {
                }
                try {
                    DataObjectList backchild = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctChildHoleBOHelper.ActionName.modifyDuctChildHoles,
                            context, childholebo); //修改新段下的子孔的Related_Path_Cuid
                } catch (Exception ex6) {
                }
                try {
                    DuctSeg segsback = (DuctSeg) BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.modifyDuctSeg,
                            context, dto); //修改段入库
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                DataObjectList list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.modifyWireToDuctlines,
                        context, modifwire2list2); //修改 Wiretoductline 后入库
                modifwire2list2.clear();
            } catch (Exception ex8) {
            }

            try { //  add by libo 2008.8.29 bug:10281
                String squl = WireToDuctline.AttrName.lineBranchCuid + "='" + dbtwo.getCuid() + "'";
                DataObjectList wire2ductback = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
                        context, squl); //取出 第二分支的 下的 Wiretoductline

                WireToDuctline wire2du = null;
                DataObjectList modiflist = new DataObjectList();
                if (wire2ductback != null && wire2ductback.size() > 0) {
                    for (int i = 0; i < wire2ductback.size(); i++) {
                        wire2du = (WireToDuctline) wire2ductback.get(i); //修改  Wiretoductline的分支 LineBranchCuid和 direction
                        wire2du.setLineBranchCuid(dbone.getCuid());
                        wire2du.setLineBranchCuid(dbone.getCuid());
                        modiflist.add(wire2du);
                    }
                }

                DataObjectList list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.modifyWireToDuctlines,
                        context, modiflist); //修改 Wiretoductline 后入库
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try { //4 直接删掉 第二个分支,第一个分支作为新分支
                DuctBranch deleBrback = (DuctBranch) BoCmdFactory.getInstance().execBoCmd(DuctBranchBOHelper.ActionName.deleteDuctBranch,
                        context, dbtwo); //删除第二条分支
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //5 修改第一条分支
            GenericDO origdto = (GenericDO) origendlist.get(0);
            GenericDO destdto = (GenericDO) origendlist.get(1);
            dbone.setAttrValue(DuctBranch.AttrName.origPointCuid, origdto);
            dbone.setAttrValue(DuctBranch.AttrName.destPointCuid, destdto);
            dbone.setAttrValue(DuctBranch.AttrName.labelCn,
                    (origdto.getAttrValue(DuctBranch.AttrName.labelCn).toString() + "--" +
                    destdto.getAttrValue(DuctBranch.AttrName.labelCn).toString()));
            try {
                DuctBranch ductBranch = (DuctBranch) BoCmdFactory.getInstance().execBoCmd(DuctBranchBOHelper.ActionName.modifyDuctBranch,
                        context, dbone); //修改了原来第一条的分支
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//        } else { //取消
//            return;
//        }
    }
    
    private static void switchDtoLabelCn(GenericDO dto) {
        String labelCn = dto.getAttrValue(GenericDO.AttrName.labelCn).toString();
        String[] temp = labelCn.split("--");
        if (temp.length == 2) {
            labelCn = temp[1] + "--" + temp[0];
            dto.setAttrValue(GenericDO.AttrName.labelCn, labelCn);
        }
    }
}
