package com.boco.gis.rest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import twaver.BaseEquipment;
import twaver.Element;
import twaver.Node;
import twaver.ResizableNode;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.TWaverUtil;

import com.boco.common.util.except.UserException;
import com.boco.graphkit.ext.ChildHoleNode;
import com.boco.graphkit.ext.ClientConsts;
import com.boco.graphkit.ext.IViewElement;
import com.boco.graphkit.ext.PipeHole;
import com.boco.graphkit.ext.RoundHoleNode;
import com.boco.graphkit.ext.SquareHoleNode;
import com.boco.graphkit.ext.background.DuctSegBackground;
import com.boco.graphkit.ext.component.TWDataBox;
import com.boco.graphkit.ext.component.TWNetwork;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.bussiness.consts.DuctEnum.ChildHoleState;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctChildHole;
import com.boco.transnms.common.dto.DuctHole;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class DuctUtils {

    public static final String NETWORK_SIZE = "network_size";

    public static void changeCellCounts(TWDataBox dataBox, SquareHoleNode hole, List<Integer> rowList,
        List<Integer> dummyList) {
        if (dummyList == null) {
            return;
        }
        if (rowList == null) {
            rowList = new ArrayList();
        }
        //delete row
        if (rowList.size() >= dummyList.size()) {
            for (int rowIndex = 0; rowIndex < rowList.size(); rowIndex++) {
                Integer oldRowCount = rowList.get(rowIndex);
                Integer newRowCount = new Integer(0);
                if (rowIndex < dummyList.size()) {
                    newRowCount = dummyList.get(rowIndex);
                } //不要修改dummylist
                modifyChildHoleCountInRow(dataBox, hole, rowIndex, oldRowCount, newRowCount);
            }
        } else { //insert row => rowList.size() < dummyList.size()
            for (int rowIndex = 0; rowIndex < dummyList.size(); rowIndex++) {
                Integer oldRowCount = new Integer(0);
                Integer newRowCount = dummyList.get(rowIndex);
                if (rowIndex < rowList.size()) {
                    oldRowCount = rowList.get(rowIndex);
                } else { //addrow
                    List<Node> allNodes = dataBox.getElementsByTag(hole.getTag());
                    for (Node node : allNodes) {
                        if (node instanceof SquareHoleNode) {
                            SquareHoleNode linkhole = (SquareHoleNode) node;
                            List<Integer> cellCounts = linkhole.getCellCounts();
                            cellCounts.add(oldRowCount);
                        }
                    }
                }
                modifyChildHoleCountInRow(dataBox, hole, rowIndex, oldRowCount, newRowCount);
            }
        }
    }

    public static DuctChildHole createDefaultDuctChildHole(DuctHole parent) {
        DuctChildHole childHole = new DuctChildHole();
        childHole.setCuid();
        childHole.setLabelCn("子孔");
        childHole.setUsageState(1);
        if (parent != null) {
            childHole.setAttrValue(DuctChildHole.AttrName.origPointCuid, parent.getAttrValue(DuctHole.AttrName.origPointCuid));
            childHole.setAttrValue(DuctChildHole.AttrName.destPointCuid, parent.getAttrValue(DuctHole.AttrName.destPointCuid));
            childHole.setAttrValue(DuctChildHole.AttrName.relatedSystemCuid, parent.getAttrValue(DuctHole.AttrName.relatedSystemCuid));
            childHole.setAttrValue(DuctChildHole.AttrName.relatedSegCuid, parent.getAttrValue(DuctHole.AttrName.relatedSegCuid));
            childHole.setAttrValue(DuctChildHole.AttrName.relatedHoleCuid, parent.getCuid());
            childHole.setAttrValue(DuctChildHole.AttrName.relatedPathCuid, parent.getRelatedPathCuid());
            if (parent.getDirection() > 0) {
                childHole.setDirection(parent.getDirection());
            }
            if (parent.getMaintMode() > 0) {
                childHole.setMaintMode(parent.getMaintMode());
            }
            if (parent.getOwnership() > 0) {
                childHole.setOwnership(parent.getOwnership());
            }
            if (parent.getPurpose() > 0) {
                childHole.setPurpose(parent.getPurpose());
            }
            childHole.setBuildDate(parent.getBuildDate());
            childHole.setBuilder(parent.getBuilder());
            childHole.setCheckDate(parent.getCheckDate());
            childHole.setFinishDate(parent.getFinishDate());
            childHole.setLength(parent.getLength());
            childHole.setMaintDep(parent.getMaintDep());
            childHole.setServicer(parent.getServicer());
            childHole.setUserName(parent.getUserName());
        }
        return childHole;
    }
    
    private static void modifyChildHoleCountInRow(TWDataBox dataBox, SquareHoleNode hole, int rowIndex,
                                                  int oldRowCount, int newRowCount) {
        DuctHole ductHole = (DuctHole) hole.getNodeValue();
        //remove child //从一行最后向前删
        if (newRowCount < oldRowCount) {
            for (int j = 0; j < oldRowCount - newRowCount; j++) {
                List<Node> allNodes = dataBox.getElementsByTag(hole.getTag());
                for (Node node : allNodes) {
                    if (node instanceof SquareHoleNode) {
                        ChildHoleNode child = ((SquareHoleNode) node).removeChildHole(rowIndex, false);
                        dataBox.removeElement(child);
                    }
                }

            }
        } else if (newRowCount > oldRowCount) { //add child//从一行最后向后加
            for (int j = 0; j < newRowCount - oldRowCount; j++) {
                DuctChildHole ductChildHole = createDefaultDuctChildHole(ductHole);
                List<Node> allNodes = dataBox.getElementsByTag(hole.getTag());
                for (Node node : allNodes) {
                    if (node instanceof SquareHoleNode) {
                        ChildHoleNode child = ((SquareHoleNode) node).addChildHole(rowIndex, ductChildHole);
                        child.putClientProperty(
                            DuctChildHole.AttrName.ductChildHoldNum,
                            Integer.toString(child.getHoleIndex() + 1));
                        child.putClientProperty(
                            DuctChildHole.AttrName.labelCn,
                            Integer.toString(child.getHoleIndex() + 1));

                        dataBox.addElement(child);
                    }
                }
            }
        }
    }

    public static double adjustBetweenMinToMax(double dest, double min, double max) {
        dest = Math.max(dest, min);
        dest = Math.min(dest, max);
        return dest;
    }

    public static void updateDuctHoleAndChildHoleCuid(TDataBox box) {
        updateDuctHoleAndChildHoleCuid(box, null);
    }

    public static void updateDuctHoleAndChildHoleCuid(TDataBox box, DuctSeg ductSeg) {

        List<Element> allElements = box.getAllElements();
        for (Element element : allElements) {
            if (element instanceof IViewElement) {
                GenericDO dbo = (GenericDO) ((IViewElement) element).getNodeValue();
                if (dbo != null) {
                    //修改BOX里面每个对象的属性 related_system_cuid,related_branch_cuid.
                    if (dbo instanceof DuctHole) {
                        ((DuctHole) dbo).setCuid();
                        if (ductSeg != null) {
                            ((DuctHole) dbo).setRelatedSegCuid(ductSeg.getCuid());
                            ((DuctHole) dbo).setRelatedSystemCuid(DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid)));
                            ((DuctHole) dbo).setOrigPointCuid(DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.origPointCuid)));
                            ((DuctHole) dbo).setDestPointCuid(DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.destPointCuid)));
                        }

                        ((BaseEquipment) element).setTag(dbo.getCuid());
                    } else if (dbo instanceof DuctChildHole) {
                        ((DuctChildHole) dbo).setCuid();
                        if (ductSeg != null) {
                            ((DuctChildHole) dbo).setRelatedSegCuid(ductSeg.getCuid());
                            ((DuctChildHole) dbo).setRelatedSystemCuid(DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.
                                relatedSystemCuid)));
                            ((DuctChildHole) dbo).setOrigPointCuid(DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.origPointCuid)));
                            ((DuctChildHole) dbo).setDestPointCuid(DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.destPointCuid)));
                        }
                        ((BaseEquipment) element).setTag(dbo.getCuid());

                        element.putClientProperty(DuctChildHole.AttrName.usageState, null);
                        ((DuctChildHole) dbo).setUsageState(DuctEnum.ChildHoleState._free);
                    }
                }
            }
        }
        for (Element element : allElements) {
            if (element instanceof IViewElement) {
                GenericDO dbo = (GenericDO) ((IViewElement) element).getNodeValue();
                if (dbo != null) {
                    //根据父子关系设置所属管孔
                    if (dbo instanceof DuctChildHole) {
                        Element parent = element.getParent();
                        DuctHole ductHole = (DuctHole) ((IViewElement) parent).getNodeValue();
                        ((DuctChildHole) dbo).setRelatedHoleCuid(ductHole.getCuid());

                    }
                }
            }
        }
    }


    public static void processDuctChildHoldNumModified(TWDataBox dataBox, Element emt, Object oldValue) {
        if (emt instanceof ChildHoleNode) {
            ChildHoleNode child = (ChildHoleNode) emt;
            if (child.getNodeValue() != null) {
                DuctChildHole childhole = (DuctChildHole) child.getNodeValue();
                if (child.getParent() != null && child.getParent() instanceof RoundHoleNode) {
                    RoundHoleNode rh = (RoundHoleNode) child.getParent();
                    List cdlist = rh.getChildren();
                    for (int i = 0; i < cdlist.size(); i++) {
                        DuctChildHole cdh = (DuctChildHole) ((ChildHoleNode) cdlist.get(i)).getNodeValue();
                        if (!childhole.getCuid().equals(cdh.getCuid())
                            && childhole.getDuctChildHoldNum().equals(cdh.getDuctChildHoldNum())) {
//                            MessagePane.showErrorMessage(PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.view.dm.system.duct.DuctUtils.java1") + "!");

                            List<Element> list = dataBox.getElementsByTag(child.getTag());
                            for (Element hole : list) {
                                if (hole instanceof ChildHoleNode) {
                                    ChildHoleNode holetemp = (ChildHoleNode) hole;
                                    if (holetemp.getNodeValue() != null) {
                                        DuctChildHole childholetemp = (DuctChildHole) holetemp.getNodeValue();
                                        childholetemp.setDuctChildHoldNum((String) oldValue);
                                    }
                                }
                            }
                        }
                    }
                }

                List<Element> list = dataBox.getElementsByTag(child.getTag());
                for (Element hole : list) {
                    if (hole instanceof ChildHoleNode) {
                        ChildHoleNode holetemp = (ChildHoleNode) hole;
                        if (holetemp.getNodeValue() != null) {
                            DuctChildHole childholetemp = (DuctChildHole) holetemp.getNodeValue();
                            childholetemp.setDuctChildHoldNum(childhole.getDuctChildHoldNum());
                        }
                    }
                }
            }
        }
    }

    public static void processDuctChildHoldUsageStateModified(TWDataBox dataBox, Element emt,
        DataObjectList wireToDuctlines) {
        if (emt instanceof ChildHoleNode) {
            ChildHoleNode child = (ChildHoleNode) emt;
            DuctChildHole childHole = (DuctChildHole) child.getNodeValue();
            if (childHole == null) {
                return;
            }
            List wtds = wireToDuctlines.getObjectByAttr(WireToDuctline.AttrName.childHoleCuid, childHole.getCuid());
            if (wtds != null && wtds.size() > 0) {
                childHole.setUsageState(2);
            }
            List<Element> list = dataBox.getElementsByTag(child.getTag());
            for (Element hole : list) {
                if (hole instanceof ChildHoleNode) {
                    ChildHoleNode holetemp = (ChildHoleNode) hole;
                    if (holetemp.getNodeValue() != null) {
                        DuctChildHole childholetemp = (DuctChildHole) holetemp.getNodeValue();
                        childholetemp.setUsageState(childHole.getUsageState());
                        refreshDuctHole((BaseEquipment) hole.getParent(), wireToDuctlines);
                    }
                }
            }
        }

    }

    public static void refreshDuctHole(BaseEquipment ductHoleNode, DataObjectList wireToDuctlines) {
        if (ductHoleNode instanceof IViewElement) {
            GenericDO hole = ((IViewElement) ductHoleNode).getNodeValue();
            if (hole == null) {
                return;
            }
            if (hole instanceof DuctHole) {
                List wtds = wireToDuctlines.getObjectByAttr(WireToDuctline.AttrName.holeCuid, hole.getCuid());
                if (wtds != null && wtds.size() > 0) {
                    ((DuctHole) hole).setUsageState(2);
                    return;
                }
            }
        }

        List list = ductHoleNode.getChildren();
        int freeCount = 0;
        int badCount = 0;
        int advanceCount = 0;
        int otherCount = 0;
        for (Object o : list) {
            if (o instanceof ChildHoleNode) {
                ChildHoleNode childHoleNode = (ChildHoleNode) o;
                Long usageState = (Long) childHoleNode.getClientProperty(DuctChildHole.AttrName.usageState);
                if (usageState == null) {
                    continue;
                }
                long state = usageState.longValue();
                if (state == ChildHoleState._use) { //只要有一个占用则为占用
                    ductHoleNode.putClientProperty(DuctChildHole.AttrName.usageState, state);
                    return;
                }
                if (state == ChildHoleState._free) {
                    freeCount++;
                } else if (state == ChildHoleState._bad) {
                    badCount++;
                } else if (state == ChildHoleState._advance) {
                    advanceCount++;
                } else {
                    otherCount++;
                }
            }
        }
        if (advanceCount > 0) { //只要一个预占,设置管孔为预占
            ductHoleNode.putClientProperty(DuctChildHole.AttrName.usageState, ChildHoleState._advance);
            return;
        }
        //其他设置空闲
        ductHoleNode.putClientProperty(DuctChildHole.AttrName.usageState, ChildHoleState._free);
        return;
    }

    public static void processPipeHoleColorModified(TWDataBox dataBox, Element emt) {
        if (emt instanceof ChildHoleNode) {
            ChildHoleNode child = (ChildHoleNode) emt;
            Color borderColor = child.getBorderColor();
            DuctChildHole childhole = (DuctChildHole) child.getNodeValue();
            if (borderColor != null) {
                long icolor = borderColor.getRGB();
                childhole.setAttrValue(DuctChildHole.AttrName.color, icolor);
            }
            List<Element> list = dataBox.getElementsByTag(child.getTag());
            for (Element hole : list) {
                if (hole instanceof ChildHoleNode) {
                    ((ChildHoleNode) hole).setBorderColor(borderColor);
                }
            }
        }
    }

    //add by libo 管道界面图中 管孔、子孔的 左下的 SHEET 中修改 属性,保持A,B面同时修改.
    public static void processDuctChildresMorepotereModified(TWDataBox dataBox, Element emt, String propertyName) {
        if (emt instanceof ChildHoleNode) {
            ChildHoleNode child = (ChildHoleNode) emt;
            if (child.getNodeValue() != null) {
                DuctChildHole childhole = (DuctChildHole) child.getNodeValue();
                List<Element> list = dataBox.getElementsByTag(child.getTag());
                for (Element hole : list) {
                    if (hole instanceof ChildHoleNode) {
                        ChildHoleNode holetemp = (ChildHoleNode) hole;
                        if (holetemp.getNodeValue() != null) {
                            DuctChildHole childholetemp = (DuctChildHole) holetemp.getNodeValue();
                            if (childhole.getAllAttr().containsKey(propertyName)
                                && !propertyName.equals(holetemp.getUserObject())) {
                                childholetemp.setAttrValue(propertyName, childhole.getAttrValue(propertyName));
                            }
                            // setattrvalue ,put属性,在 system/duct目录下查所有的
                            //阻断标志,LEFT,RIGHT标志 再MAP   childholetemp.getAllAttr() 中过滤
                        }
                    }
                }
            }
        } else if (emt instanceof RoundHoleNode) {
            RoundHoleNode ducthole = (RoundHoleNode) emt;
            if (ducthole.getNodeValue() != null) {
                DuctHole dhole = (DuctHole) ducthole.getNodeValue();
                List<Element> list = dataBox.getElementsByTag(ducthole.getTag());
                for (Element temhole : list) {
                    if (temhole instanceof RoundHoleNode) {
                        RoundHoleNode holetemp = (RoundHoleNode) temhole;
                        if (holetemp.getNodeValue() != null) {
                            DuctHole dholetemp = (DuctHole) holetemp.getNodeValue();
                            if (dhole.getAllAttr().containsKey(propertyName)
                                && !propertyName.equals(holetemp.getUserObject())) {
                                dholetemp.setAttrValue(propertyName, dhole.getAttrValue(propertyName));
                            }
                        }
                    }
                }
            }
        }
    }


    public static void processElementPropertyModified(TWDataBox dataBox, PropertyChangeEvent evt, DuctSegBackground mainBackground,
        DataObjectList wireToDuctlines) {
        String propertyName = TWaverUtil.getPropertyName(evt);
        Element emt = (Element) evt.getSource();
        if (emt instanceof ChildHoleNode) {
            ChildHoleNode child = (ChildHoleNode) emt;
            if (child.getNodeValue() != null) {
                DuctChildHole childhole = (DuctChildHole) child.getNodeValue();
                List<Element> list = dataBox.getElementsByTag(child.getTag());
                for (Element hole : list) {
                    if (hole instanceof ChildHoleNode) {
                        ChildHoleNode holetemp = (ChildHoleNode) hole;
                        if (holetemp.getNodeValue() != null) {
                            DuctChildHole childholetemp = (DuctChildHole) holetemp.getNodeValue();
                            if (childhole.getAllAttr().containsKey(propertyName)
                                && !propertyName.equals(holetemp.getUserObject())) {
                                childholetemp.setAttrValue(propertyName, childhole.getAttrValue(propertyName));
                            } // setattrvalue ,put属性,在 system/duct目录下查所有的
                            //阻断标志,LEFT,RIGHT标志 再MAP   childholetemp.getAllAttr() 中过滤
                        }
                    }
                }
            }
        } else if (emt instanceof RoundHoleNode) {
            RoundHoleNode ducthole = (RoundHoleNode) emt;
            if (ducthole.getNodeValue() != null) {
                DuctHole dhole = (DuctHole) ducthole.getNodeValue();
                List<Element> list = dataBox.getElementsByTag(ducthole.getTag());
                for (Element temhole : list) {
                    if (temhole instanceof RoundHoleNode) {
                        RoundHoleNode holetemp = (RoundHoleNode) temhole;
                        if (holetemp.getNodeValue() != null) {
                            DuctHole dholetemp = (DuctHole) holetemp.getNodeValue();
                            if (dhole.getAllAttr().containsKey(propertyName)
                                && !propertyName.equals(holetemp.getUserObject())) {
                                dholetemp.setAttrValue(propertyName, dhole.getAttrValue(propertyName));
                            }
                        }
                    }
                }
            }
        }

        if (propertyName.equals(DuctChildHole.AttrName.ductChildHoldNum)) {
            DuctUtils.processDuctChildHoldNumModified(dataBox, emt, evt.getOldValue());
        } else if (propertyName.equals(DuctChildHole.AttrName.usageState)) {
            DuctUtils.processDuctChildHoldUsageStateModified(dataBox, emt, wireToDuctlines);
        } else if (propertyName.equals(ClientConsts.PROPERTYNAME_PIPEHOLE_BORDERCOLOR)) {
            DuctUtils.processPipeHoleColorModified(dataBox, emt);
        } else if (propertyName.equals(DuctHole.AttrName.origNo)) {
            Element element = dataBox.getSelectionModel().lastElement();
            if ((element instanceof RoundHoleNode) || (element instanceof SquareHoleNode)) {
                BaseEquipment hole = (BaseEquipment) element;
                List<Element> list = dataBox.getElementsByTag(hole.getTag());
                for (Element ductHole : list) {
                    if (ductHole != element) {
                        ductHole.putClientProperty(DuctHole.AttrName.origNo,
                            hole.getClientProperty(DuctHole.AttrName.origNo));
                    }
                }
            }
        } else if (propertyName.equals(DuctHole.AttrName.destNo)) {
            Element element = dataBox.getSelectionModel().lastElement();
            if ((element instanceof RoundHoleNode) || (element instanceof SquareHoleNode)) {
                BaseEquipment hole = (BaseEquipment) element;
                List<Element> list = dataBox.getElementsByTag(hole.getTag());
                for (Element ductHole : list) {
                    if (ductHole != element) {
                        ductHole.putClientProperty(DuctHole.AttrName.destNo,
                            hole.getClientProperty(DuctHole.AttrName.destNo));
                    }
                }
            }
        } else if (propertyName.equals(ClientConsts.PROPERTYNAME_GRID_DUMMY_ROWS)) { //方孔编辑器变化
            if (emt instanceof SquareHoleNode) {
                SquareHoleNode hole = (SquareHoleNode) emt;
                List<Integer> rowList = hole.getCellCounts();
                List<Integer> dummyList = (List) evt.getNewValue();
                DuctUtils.changeCellCounts(dataBox, hole, rowList, dummyList);
                for (int i = 0; i < dummyList.size(); i++) {
                    Integer count = (Integer) dummyList.get(i);
                    if (count.intValue() == 0) {
                        dummyList.remove(i);
                    }
                }
                List<Node> allNodes = dataBox.getElementsByTag(hole.getTag());
                for (Node node : allNodes) {
                    if (node instanceof SquareHoleNode) {
                        List<Integer> cellCounts = new ArrayList<Integer>();
                        cellCounts.addAll(dummyList);
                        ((SquareHoleNode) node).setCellCounts(cellCounts);
                    }
                }
            }
        } else if (propertyName.equals(TWaverConst.PROPERTYNAME_SELECTED)) { //选择一侧孔,另一侧边框加粗
            int borderWidth;
            if (emt.isSelected()) {
                borderWidth = 6;
            } else {
                borderWidth = 4;
            }
            if (emt instanceof SquareHoleNode) {
                SquareHoleNode squareHole = (SquareHoleNode) emt;
                List<Element> list = dataBox.getElementsByTag(squareHole.getTag());
                for (Element hole : list) {
                    if (hole instanceof SquareHoleNode) {
                        ((SquareHoleNode) hole).setBorderWidth(borderWidth);
                    }
                }
            } else if (emt instanceof RoundHoleNode) {
                RoundHoleNode roundHole = (RoundHoleNode) emt;
                List<Element> list = dataBox.getElementsByTag(roundHole.getTag());
                for (Element hole : list) {
                    if (hole instanceof RoundHoleNode) {
                        ((RoundHoleNode) hole).setBorderWidth(borderWidth);
                    }
                }
            } else if (emt instanceof ChildHoleNode) {
                ChildHoleNode child = (ChildHoleNode) emt;
                List<Element> list = dataBox.getElementsByTag(child.getTag());
                for (Element hole : list) {
                    if (hole instanceof ChildHoleNode) {
                        ((ChildHoleNode) hole).setBorderWidth(borderWidth);
                    }
                }
            }
        } else if (propertyName.equals(
            ClientConsts.PROPERTYNAME_ROUNDPIPE_CENTERHOLE)) {
            if (emt instanceof RoundHoleNode) {
                RoundHoleNode hole = (RoundHoleNode) emt;
                List<Node> allNodes = dataBox.getElementsByTag(hole.getTag());
                for (Node node : allNodes) {
                    if (node instanceof RoundHoleNode) {
                        RoundHoleNode linkhole = (RoundHoleNode) node;
                        linkhole.setCenterHole((Boolean) evt.getNewValue());
                    }
                }
            }
        } else if (propertyName.equals(TWaverConst.PROPERTYNAME_SIZE)) {
            Dimension newDimen = (Dimension) evt.getNewValue();
            double sizeWidth = newDimen.getWidth();
            double sizeHeight = newDimen.getHeight();
            if (emt instanceof RoundHoleNode ||
                emt instanceof SquareHoleNode) {
                ResizableNode node = (ResizableNode) emt;
                sizeWidth = Math.max(sizeWidth, 50);
                sizeHeight = Math.max(sizeHeight, 50);
                double w = node.getX() + sizeWidth;
                double h = node.getY() + sizeHeight;
                if (emt.getUserObject().equals(ClientConsts.LeftHole)) {
                    if (!mainBackground.topContains(w, h)) {
                        node.setSize((Dimension) evt.getOldValue());
                    } else {
                        Dimension newd = new Dimension();
                        newd.width = (int) sizeWidth;
                        newd.height = (int) sizeHeight;
                        node.setSize(newd);
                    }
                } else if (emt.getUserObject().equals(ClientConsts.RightHole)) {
                    if (!mainBackground.bottomContains(w, h)) {
                        node.setSize((Dimension) evt.getOldValue());
                    } else {
                        Dimension newd = new Dimension();
                        newd.width = (int) sizeWidth;
                        newd.height = (int) sizeHeight;
                        node.setSize(newd);
                    }
                }
            }
        } else if (propertyName.equals(TWaverConst.PROPERTYNAME_LOCATION)) {
            Point2D oldP = (Point2D) evt.getOldValue();
            Point2D newP = (Point2D) evt.getNewValue();
            double locX = newP.getX();
            double locY = newP.getY();
            if (emt instanceof ChildHoleNode) {

            } else if (((emt instanceof RoundHoleNode ||
                         emt instanceof SquareHoleNode)) &&
                       ((emt.getUserObject().equals(ClientConsts.LeftHole)) ||
                        (emt.getUserObject().equals(ClientConsts.RightHole)))) {
                if ((emt.getName() == null) && (!"阻断管孔".equals(emt.getName()))) { //add by libo
                    if (emt.getUserObject().equals(ClientConsts.LeftHole)) {
                        Rectangle topRect = mainBackground.getTopRect();
                        locX = DuctUtils.adjustBetweenMinToMax(locX, topRect.getMinX(),
                            topRect.getMaxX() - emt.getWidth());
                        locY = DuctUtils.adjustBetweenMinToMax(locY, topRect.getMinY(),
                            topRect.getMaxY() - emt.getHeight());
                    } else if (emt.getUserObject().equals(ClientConsts.RightHole)) {
                        Rectangle bottomRect = mainBackground.getBottomRect();
                        locX = DuctUtils.adjustBetweenMinToMax(locX, bottomRect.getMinX(),
                            bottomRect.getMaxX() - emt.getWidth());
                        locY = DuctUtils.adjustBetweenMinToMax(locY, bottomRect.getMinY(),
                            bottomRect.getMaxY() - emt.getHeight());
                    }
                }
            }
            emt.setLocation(locX, locY);
        }
    }

    public static int getMaxLeftHeight(TWNetwork network) {
        int res = 0;
        List<Element> allElements = network.getDataBox().getAllElements();
        for (Element element : allElements) {
            if (element instanceof SquareHoleNode || element instanceof RoundHoleNode) {
                if (element.getUserObject() != null && element.getUserObject().equals(ClientConsts.LeftHole)) {
                    res = Math.max(res, element.getLocation().y + network.getElementBounds(element).height);
                }
            }
        }
        return res;
    }

    public static void processElementLocation(TWNetwork network) {
        int miny = network.getCanvas().getBounds().height / 2;
        int maxLeftY = DuctUtils.getMaxLeftHeight(network);
        miny = Math.max(miny, maxLeftY);

        int distance = 0;
        List<Element> allElements = network.getDataBox().getAllElements();
        for (Element element : allElements) {
            if (element instanceof SquareHoleNode || element instanceof RoundHoleNode) {
                if (element.getUserObject() != null && element.getUserObject().equals(ClientConsts.RightHole)) {
                    int tempDistance = element.getLocation().y - miny;
                    distance = Math.min(tempDistance, distance);
                }
            }
        }

        if (distance < 0) {
            for (Element element : allElements) {
                if (element instanceof SquareHoleNode || element instanceof RoundHoleNode) {
                    if (element.getUserObject() != null && element.getUserObject().equals(ClientConsts.RightHole)) {
                        element.setLocation(element.getLocation().x, element.getLocation().y + Math.abs(distance));
                    }
                }
            }
        }
    }

    public static void checkHoleNo(List nList, List list2) {
        Map origNoMap = new HashMap();
        Map destNoMap = new HashMap();
        for (int i = 0; i < nList.size(); i++) {
            Element element = (Element) nList.get(i);
            if ((element instanceof SquareHoleNode) ||
                (element instanceof RoundHoleNode)) {
                if (element.getUserObject().equals(ClientConsts.LeftHole)) {
                    DuctHole dbo = (DuctHole) ((IViewElement) element).getNodeValue();
                    if (dbo != null) {
                        origNoMap.put(dbo.getOrigNo(), dbo.getOrigNo());
                        destNoMap.put(dbo.getDestNo(), dbo.getDestNo());
                    }
                }
            }
        }
        for (int i = 0; i < list2.size(); i++) {
            Element element = (Element) list2.get(i);
            if ((element instanceof SquareHoleNode) ||
                (element instanceof RoundHoleNode)) {
                DuctHole dbo = (DuctHole) ((IViewElement) element).getNodeValue();
                if (dbo != null) {
                    Object o = origNoMap.put(dbo.getOrigNo(), dbo.getOrigNo());
                    if (o != null) {
                        throw new UserException("模板中A端编号 " + dbo.getOrigNo() + "与当前截面图中编号重复!");
                    }
                    o = destNoMap.put(dbo.getDestNo(), dbo.getDestNo());
                    if (o != null) {
                        throw new UserException("模板中B端编号" + dbo.getDestNo() + "与当前截面图中编号重复!");
                    }
                }
            }
        }
    }

    public static Color generateElementSelectColor(Element element) {
        Color color = null;
        if (element instanceof PipeHole) {
            ChildHoleNode childHoleNode = (ChildHoleNode) element;
            color = childHoleNode.getBorderColor();
        }
        return color;
    }


    //>> add by wangguodong 2009-06-10
   /*
   * @paramTWDataBox databox: 被复制数据所在的databox
   * @paramDuctSeg templateDuctSeg:复制后的数据(管孔子孔)所要依存的管道段.
   * @paramMap <String, String> cuidMap:要替换的xml键值对.key为要替换目标 ,value为要替换的值
   * @return String :根据dataBox生成的xml文件
   **/
   public static String getXMLFromDataboxAndDuctSeg(TWDataBox databox, DuctSeg templateDuctSeg,  Map <String, String> cuidMap,Boolean UsageState) {
       cuidMap.clear();
       String xml = "";

       String relatedSystemCuid = DMHelper.getRelatedCuid(templateDuctSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
       String relatedOrigCuid = DMHelper.getRelatedCuid(templateDuctSeg.getAttrValue(DuctSeg.AttrName.origPointCuid));
       String relatedDestCuid = DMHelper.getRelatedCuid(templateDuctSeg.getAttrValue(DuctSeg.AttrName.destPointCuid));
       String segCuid = templateDuctSeg.getCuid();

       cuidMap.put(relatedSystemCuid, relatedSystemCuid);
       cuidMap.put(relatedOrigCuid, relatedOrigCuid);
       cuidMap.put(relatedDestCuid, relatedDestCuid);
       cuidMap.put(segCuid, segCuid);


       List rootList = databox.getAllElements();
      for (int i = 0; i < rootList.size(); i++) {
          Element element = (Element) rootList.get(i);
          if ((element instanceof SquareHoleNode ||
               element instanceof RoundHoleNode) &&
              (((IViewElement) element).getNodeValue() instanceof DuctHole) &&
              ((element.getUserObject() != null) &&
               (element.getUserObject().equals(ClientConsts.LeftHole)))) {
              DuctHole ductHoleDbo = null;
              DuctHole gdo = (DuctHole) ((IViewElement) element).getNodeValue();
//              ductHoleDbo = (DuctHole) gdo.cloneByClassName();
              ductHoleDbo = (DuctHole) gdo.deepClone();
//              ductHoleDbo.setCuid();

              cuidMap.put(((BaseEquipment)element).getTag(),((BaseEquipment)element).getTag());

              ductHoleDbo.clearUnknowAttrs();
              ductHoleDbo.convAllObjAttrToCuid();

              ((DuctHole) ductHoleDbo).setRelatedSystemCuid(relatedSystemCuid);
              ((DuctHole) ductHoleDbo).setRelatedSegCuid(segCuid);
              ((DuctHole) ductHoleDbo).setOrigPointCuid(relatedOrigCuid);
              ((DuctHole) ductHoleDbo).setDestPointCuid(relatedDestCuid);
              ((IViewElement) element).setNodeValue(ductHoleDbo);
              List holeList = ((TWDataBox) databox).getElementsByTag(gdo.getCuid());
              for (int j = 0; j < holeList.size(); j++) {
                  BaseEquipment node = (BaseEquipment) holeList.get(j);
                  ((IViewElement) node).setNodeValue(ductHoleDbo);
              }

              List childList = element.getChildren();
              for (int j = 0; j < childList.size(); j++) {
                  Element childElement = (Element) childList.get(j);
                  GenericDO childgdo = ((IViewElement) childElement).getNodeValue();
                  DuctHole hole = (DuctHole) ((IViewElement) childElement.getParent()).getNodeValue();
//                  GenericDO childgdoDbo = (GenericDO) childgdo.cloneByClassName();
                  GenericDO childgdoDbo = (GenericDO) childgdo.deepClone();
//                  childgdoDbo.setCuid();
                  cuidMap.put(((BaseEquipment)childElement).getTag(), childgdoDbo.getCuid());

                  childgdoDbo.clearUnknowAttrs();
                  childgdoDbo.convAllObjAttrToCuid();
                  ((DuctChildHole) childgdoDbo).setRelatedSystemCuid(relatedSystemCuid);
                  ((DuctChildHole) childgdoDbo).setRelatedSegCuid(segCuid);
                  ((DuctChildHole) childgdoDbo).setOrigPointCuid(relatedOrigCuid);
                  ((DuctChildHole) childgdoDbo).setDestPointCuid(relatedDestCuid);
                  ((DuctChildHole) childgdoDbo).setRelatedHoleCuid(hole.getCuid());
                  if(UsageState){
                  ((DuctChildHole) childgdoDbo).setUsageState(DuctEnum.ChildHoleState._free);
                  }
                  ((IViewElement) childElement).setNodeValue(childgdoDbo);
                  List childHoleList = ((TWDataBox) databox).getElementsByTag(childgdo.getCuid());
                  for (int k = 0; k < childHoleList.size(); k++) {
                      BaseEquipment node = (BaseEquipment) childHoleList.get(k);
                      ((IViewElement) node).setNodeValue(childgdoDbo);
                  }
              }
          }
      }


    /*   try {
          TemplateHandler handler = new TemplateHandler();
          PersistenceManager.registerClassDelegate(DMElementTypeEnum.class, new EnumDelegate());
          PersistenceManager.registerClassDelegate(Timestamp.class, new DefaultPersistenceDelegate(new String[] {"time"}));
          PersistenceManager.setClientPropertyFilter(handler.persistentFilter);
          xml = PersistenceManager.writeByXML(databox, false);
          PersistenceManager.setClientPropertyFilter(null);
          PersistenceManager.unregisterClassDelegate(Timestamp.class);
          PersistenceManager.unregisterClassDelegate(DMElementTypeEnum.class);
      } catch (Exception ex) {
          ex.printStackTrace();
      }*/

       return TemplateUtils.getXmlByBox(databox);

   }





   /*
   * @param  TWDataBox databox: 被复制数据所在的databox
   * @param  DuctSeg templateDuctSeg:复制后的数据(管孔子孔)所要依存的管道段.
   * @param   String xml:要根据cuidMap键值替换的xml文件
   * @param  Map <String, String> cuidMap:要替换的xml键值对.key为要替换目标 ,value为要替换的值
   * @param  DuctSeg templateDuctSeg: 模板管道段(即传进来的xml和cuidMap中的键值relatedSystemCuid；relatedOrigCuid；relatedDestCuid；segCuid是根据此管道段生成的)
   * @return Map: ( key: "DuctSeg"       value :管道段；
                    key:"DuctHole"      value :复制后的管孔列表；
                    key:"childhole"     value :复制后的管孔列表；
                    key: "XML",          value :替换后的xml文件
                   key:"cuidMap",        value : 替换规则
                  )

   **/
   public static  Map getDuctSectionMap(TDataBox templateBox, DuctSeg ductSeg, String xml, Map<String, String> cuidMap, DuctSeg templateDuctSeg) {
       Map ductSectionMap = new HashMap();

       DataObjectList ductHoleList = new DataObjectList();
       DataObjectList childHoleList = new DataObjectList();
       GenericDO clonedDuctSeg = (GenericDO) ductSeg.deepClone();
       clonedDuctSeg.clearUnknowAttrs();
       clonedDuctSeg.convAllObjAttrToCuid();


       String relatedSystemCuid = DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
       String relatedbranchCuid = DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
       String relatedOrigCuid = DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.origPointCuid));
       String relatedDestCuid = DMHelper.getRelatedCuid(ductSeg.getAttrValue(DuctSeg.AttrName.destPointCuid));
       String segCuid = ductSeg.getCuid();

       String templateDuctSegRelatedSystemCuid = DMHelper.getRelatedCuid(templateDuctSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
       String templateDuctSegRelatedOrigCuid = DMHelper.getRelatedCuid(templateDuctSeg.getAttrValue(DuctSeg.AttrName.origPointCuid));
       String templateDuctSegRelatedDestCuid = DMHelper.getRelatedCuid(templateDuctSeg.getAttrValue(DuctSeg.AttrName.destPointCuid));
       String templateDuctSegCuid = templateDuctSeg.getCuid();

       replaceCuidMapValue(cuidMap, templateDuctSegRelatedSystemCuid, relatedSystemCuid);
       replaceCuidMapValue(cuidMap, templateDuctSegRelatedOrigCuid, relatedOrigCuid);
       replaceCuidMapValue(cuidMap, templateDuctSegRelatedDestCuid, relatedDestCuid);
       replaceCuidMapValue(cuidMap, templateDuctSegCuid, segCuid);

       List rootList = templateBox.getAllElements();
       for (int i = 0; i < rootList.size(); i++) {
           Element element = (Element) rootList.get(i);
           if ((element instanceof SquareHoleNode ||
                element instanceof RoundHoleNode) &&
               (((IViewElement) element).getNodeValue() instanceof DuctHole) &&
               ((element.getUserObject() != null) &&
                (element.getUserObject().equals(ClientConsts.LeftHole)))) {

               DuctHole oldDuctHole = (DuctHole) ((IViewElement) element).getNodeValue();
//               DuctHole newDuctHole = (DuctHole) oldDuctHole.cloneByClassName();
               DuctHole newDuctHole = (DuctHole) oldDuctHole.deepClone();
               newDuctHole.setCuid();
               cuidMap.put(((BaseEquipment) element).getTag(), newDuctHole.getCuid());
//               replaceCuidMapValue(cuidMap, ((RoundHoleNode) element).getTag(), newDuctHole.getCuid());
               newDuctHole.clearUnknowAttrs();
               newDuctHole.convAllObjAttrToCuid();
               newDuctHole.setLength(ductSeg.getLength()); 
               newDuctHole.setRelatedSystemCuid(relatedSystemCuid);
               newDuctHole.setRelatedPathCuid(relatedbranchCuid);
               newDuctHole.setRelatedSegCuid(segCuid);
               newDuctHole.setOrigPointCuid(relatedOrigCuid);
               newDuctHole.setDestPointCuid(relatedDestCuid);

               if (ductHoleList.getObjectByCuid(newDuctHole.getCuid()).size() == 0) {
                   ductHoleList.add(newDuctHole);
               }

               List childList = element.getChildren();
               for (int j = 0; j < childList.size(); j++) {
                   Element childElement = (Element) childList.get(j);
                   GenericDO oldChildHoleGdo = ((IViewElement) childElement).getNodeValue();
//                   DuctChildHole newChildHoleGdo = (DuctChildHole) oldChildHoleGdo.cloneByClassName();
                   DuctChildHole newChildHoleGdo = (DuctChildHole) oldChildHoleGdo.deepClone();
                   newChildHoleGdo.setCuid();
                   replaceCuidMapValue(cuidMap, ((BaseEquipment)childElement).getTag(), newChildHoleGdo.getCuid());
                   newChildHoleGdo.setLength(ductSeg.getLength()); 
                   newChildHoleGdo.setRelatedSystemCuid(relatedSystemCuid);
                   newChildHoleGdo.setRelatedSegCuid(segCuid);
                   newChildHoleGdo.setOrigPointCuid(relatedOrigCuid);
                   newChildHoleGdo.setDestPointCuid(relatedDestCuid);
                   newChildHoleGdo.setRelatedPathCuid(relatedbranchCuid);
                   newChildHoleGdo.setRelatedHoleCuid(newDuctHole.getCuid());
                   newChildHoleGdo.setUsageState(DuctEnum.ChildHoleState._free);

                   if (childHoleList.getObjectByCuid(newChildHoleGdo.getCuid()).size() == 0) {
                       childHoleList.add(newChildHoleGdo);
                   }

               }
           }
       }

       ductSectionMap.put("DuctSeg", clonedDuctSeg);
       ductSectionMap.put("DuctHole", ductHoleList);
       ductSectionMap.put("ChildHole", childHoleList);
       xml = getXMLBycuidMapAndXML(cuidMap, xml);
       ductSectionMap.put("XML", xml);
       ductSectionMap.put("cuidMap", cuidMap);


       return ductSectionMap;
   }

   private static String getXMLBycuidMapAndXML(Map<String, String> cuidMap, String xml) {

       Iterator iterator = cuidMap.keySet().iterator();
       while (iterator.hasNext()) {
           String keyCuid = (String) iterator.next();
           if (cuidMap.get(keyCuid) != null) {
               String valueCuid = cuidMap.get(keyCuid);
               xml = xml.replaceAll(keyCuid, valueCuid);
           }
       }
       return xml;
   }

   private static void replaceCuidMapValue(Map<String, String> cuidMap, String key, String newValue) {
      if (cuidMap.containsKey(key)) {
          cuidMap.put(key, newValue);
      }
  }

}
