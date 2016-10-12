package com.boco.gis.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JOptionPane;

import twaver.AbstractElement;
import twaver.BaseEquipment;
import twaver.Element;
import twaver.TDataBox;

import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.utils.exception.UserException;
import com.boco.graphkit.ext.ChildHoleNode;
import com.boco.graphkit.ext.ClientConsts;
import com.boco.graphkit.ext.RoundHoleNode;
import com.boco.graphkit.ext.SquareHoleNode;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DuctChildHole;
import com.boco.transnms.common.dto.DuctHole;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.DuctHoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctSegBO;

public class MergeDuctSegsHandler {
	
	public static GenericDO doMergeDuctSegs(DataObjectList list) throws Exception{
		GenericDO segss = null;
		double lengthall = 0.0d;
	    DuctSeg ductSegfirst = (DuctSeg) list.get(0);
	    
	    BoActionContext actionContext = ActionContextUtil.getActionContext();
	    
	    if (ductSegfirst.getAttrValue(DuctSeg.AttrName.length) != null) {
	    	lengthall = (Double) ductSegfirst.getAttrValue(DuctSeg.AttrName.length); //1.循环:第一段 与下一段的长度累加
	    }
	    Map mapi = new HashMap();
	    DataObjectList origendlist = new DataObjectList(); //合并后生成的 头尾点取出
	    for (int n = 0; n < list.size() - 1; n++) {
	    	mapi.put(1 + n, list.get(n + 1)); //把合并点装入 mapi,用与弹出的界面显示 路由点
	    }
	    list.sort(DuctSeg.AttrName.indexInBranch, true);
	    //列出 合并后的 头尾点 ,把 合并后生成的 头尾点取出 ,用与弹出的界面显示 路由点
	    GenericDO origgdo1 = (GenericDO) list.get(0).getAttrValue(DuctSeg.AttrName.origPointCuid);
	    GenericDO destgdo1 = (GenericDO) list.get(list.size() - 1).getAttrValue(DuctSeg.AttrName.destPointCuid);
	    origendlist.add(origgdo1);
	    origendlist.add(destgdo1);
	    
	    DuctSeg ductSeg1= (DuctSeg) list.get(0); //循环段,LIST里面每个段都合并到第一个段上
	    String seg1sql = WireToDuctline.AttrName.lineSegCuid + "='" + ductSeg1.getCuid() + "'"; //选种的前一段 下有多少 敷设
	    
	    for (int n = 0; n < list.size() - 1; n++) {
	    	DuctSeg ductSegOne = (DuctSeg) list.get(0); //循环段,LIST里面每个段都合并到第一个段上
	        DuctSeg ductSegTwo = (DuctSeg) list.get(n + 1);
	        GenericDO destOne = (GenericDO) ductSegOne.getAttrValue(DuctSeg.AttrName.destPointCuid);
	        GenericDO origTwo = (GenericDO) ductSegTwo.getAttrValue(DuctSeg.AttrName.origPointCuid);
	        GenericDO destTwo = (GenericDO) ductSegTwo.getAttrValue(DuctSeg.AttrName.destPointCuid);
	        GenericDO origOne = (GenericDO) ductSegOne.getAttrValue(DuctSeg.AttrName.origPointCuid);
	        String brdto = DMHelper.getRelatedCuid(ductSegOne.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
	        String brdto2 = DMHelper.getRelatedCuid(ductSegTwo.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
	        if (!(brdto).equals(brdto2)) { // 判断分支是否相同
	        	throw new UserException("选择的段不在统一分支上,不能合并!");
	        } else { // 分支相同
	        	DataObjectList ductholes = new DataObjectList(); //一段所有管孔
	            DataObjectList ductholes2 = new DataObjectList(); //二段所有管孔
	            DataObjectList childholelistOne = new DataObjectList(); //一段子孔
	            DataObjectList childholelistTwo = new DataObjectList(); //二段子孔
	            List rupductholeOne = new ArrayList(); //一段的阻断管孔
	            List rupductholeTwo = new ArrayList(); //二段的阻断管孔
	            List rupchildholeOne = new ArrayList(); //一段的阻断子孔
	            List rupchildholeTwo = new ArrayList(); //二段的阻断子孔

				int count = 0; // 保存阻断孔的标记
				TDataBox boxOne = new TDataBox();
				TDataBox boxTwo = new TDataBox();
				PicBlobUtils.getAllPicBlobBytes(ductSegOne.getCuid(), null, 6022L, boxOne, null); // 取一段的BOX
				PicBlobUtils.getAllPicBlobBytes(ductSegTwo.getCuid(), null, 6022L, boxTwo, null);

				List<Element> allElements = boxOne.getAllElements();
				List<Element> allElements2 = boxTwo.getAllElements();
				getMapValue(ductSegOne, allElements, count, ductholes, // 把BOX里面的元素都放到对应的LIST
						rupductholeOne, rupchildholeOne, childholelistOne,actionContext);
				getMapValue(ductSegTwo, allElements2, count, ductholes2, // 把BOX里面的元素都放到对应的LIST
						rupductholeTwo, rupchildholeTwo, childholelistTwo,actionContext);
				list.sort(DuctSeg.AttrName.indexInBranch, true);
				DataObjectList moductlinelist = new DataObjectList(); // 存放 wiretoductline用与修改
				DataObjectList delductlinelist = new DataObjectList(); // 存放wiretoductline用与删除
	            if ((destOne.getCuid()).equals(origTwo.getCuid()) || (origOne.getCuid()).equals(destTwo.getCuid())) {
	                if (ductholes.size() != ductholes2.size()) { //管孔个数 不相同
	                	throw new UserException("管孔个数不相同,不能合并");
	                } else { // 管孔个数 相同
	                    //阻断管孔个数是否一致    add by libo 2008.11.19
	                    if (rupductholeOne.size() != rupductholeTwo.size()) { //阻断管孔个数 不相同
	                    	throw new UserException("阻断管孔个数不相同");
	                    } else { // 管孔个数 相同
	                        ductholes.sort(DuctHole.AttrName.destNo, true); //A-B,B-C,第一段的尾的序号要与第二的头相同
	                        ductholes2.sort(DuctHole.AttrName.origNo, true);
	                        //直接合并,看敷设情况 BUG:10853
	                        String segsql = WireToDuctline.AttrName.lineSystemCuid + "='" + ductSegOne.getRelatedSystemCuid()+ "' and "+ WireToDuctline.AttrName.lineSegCuid + "='" + ductSegOne.getCuid() + "'"; //选种的前一段 下有多少 敷设
	                        DataObjectList wire2ductlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, segsql); //根据第一段 查他下面的wire_to_ductline
	                        String segsql2 = WireToDuctline.AttrName.lineSystemCuid + "='" + ductSegTwo.getRelatedSystemCuid()+ "' and "+ WireToDuctline.AttrName.lineSegCuid + "='" + ductSegTwo.getCuid() + "'"; //选种的后一段 下有多少 敷设
	                        DataObjectList wire2ductlist2 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, segsql2); //根据第一段 查他下面的wire_to_ductline
	                        if (wire2ductlist != null && wire2ductlist2 != null && (wire2ductlist.size() == wire2ductlist2.size())) { //都 有穿缆,且穿的同一个缆
	                        	//满足 合并条件, 管道子孔下敷设的光缆相同,则合并前先对其敷设光缆关系进行处理:修改一段的,删除二段的
	                            //修改前一段endPointCuid,删除后一段的 WireToDuctline 信息
	                            //  在循环外面 调用BO 修改,删除方法
	                        	wire2ductlist.sort(WireToDuctline.AttrName.wireSegCuid, true);
	                            wire2ductlist2.sort(WireToDuctline.AttrName.wireSegCuid, true);
	                            for (int c = 0; c < wire2ductlist.size(); c++) {
									WireToDuctline wire2duli = (WireToDuctline) wire2ductlist.get(c);
									WireToDuctline wire2duli2 = (WireToDuctline) wire2ductlist2.get(c);
									if (wire2duli.getAttrValue(WireToDuctline.AttrName.wireSegCuid).equals(wire2duli2.getAttrValue(WireToDuctline.AttrName.wireSegCuid))) {
										wire2duli.setAttrValue(WireToDuctline.AttrName.endPointCuid, wire2duli2.getEndPointCuid());
										moductlinelist.add(wire2duli);
										delductlinelist.add(wire2duli2);
									} else {
										throw new UserException("选择的段的敷设信息不相同,不能合并!");
									}
	                            }
	                        } else if (wire2ductlist != null && wire2ductlist2 != null && wire2ductlist.size() != wire2ductlist2.size()) {
	                        	throw new UserException("选择的段的敷设信息不相同,不能合并!");
	                        }
	                        if (rupductholeOne.size() > 0) { //比较阻断管孔的形状  add by libo 2008.11.19
	                        	for (int t = 0; t < rupductholeOne.size(); t++) {
	                        		Element rupholeOne = (Element) rupductholeOne.get(t); //一段的阻断管孔
	                                Element rupholeTwo = (Element) rupductholeTwo.get(t); //二段的阻断管孔
	                                String nodd1 = ((AbstractElement) rupholeOne).getIconURL();
	                                String nodd2 = ((AbstractElement) rupholeTwo).getIconURL();
	                                if (!nodd1.equals(nodd2)) { //判断方型和圆型类型是否相同
	                                	throw new UserException("阻断管孔的形状不相同,不能合并!");
	                                }
	                            }
	                        }

	                        if (ductholes.size() == 0) { //都为O,没有管孔子孔的情况
	                            
	                        } else { // 管孔个数相同且大于0,存在相同个数的管孔
	                        	for (int t = 0; t < ductholes.size(); t++) {
	                                DuctHole ductHole = (DuctHole) ductholes.get(t); //一段管孔
	                                DuctHole ductHole2 = (DuctHole) ductholes2.get(t); //二段管孔
	                                if (!ductHole.getDestNo().equals(ductHole2.getOrigNo())) { //编号不同
	                                	throw new UserException("管孔编号不同,不能合并");
	                                }
	                                
	                                //考虑敷设到管孔孔的情况  BUG:10853
	                                String squl =WireToDuctline.AttrName.lineSystemCuid + "='" + ductHole.getRelatedSystemCuid()+ "' and "+WireToDuctline.AttrName.lineSegCuid + "='" + ductHole.getRelatedSegCuid()+ "' and "+  WireToDuctline.AttrName.holeCuid + "='" + ductHole.getCuid() + "'"; //选种的前一段 下有多少 敷设
	                                DataObjectList wire2duline = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, squl); //根据第一段 查他下面的wire_to_ductline
	                                String squl2 = WireToDuctline.AttrName.lineSystemCuid + "='" + ductHole2.getRelatedSystemCuid()+ "' and "+WireToDuctline.AttrName.lineSegCuid + "='" + ductHole2.getRelatedSegCuid()+ "' and "+WireToDuctline.AttrName.holeCuid + "='" + ductHole2.getCuid() + "'"; //选种的后一段 下有多少 敷设
	                                DataObjectList wire2duline2 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, squl2); //根据第一段 查他下面的wire_to_ductline
	                                if (wire2duline != null && wire2duline2 != null && (wire2duline.size() == wire2duline2.size())) { //都 有穿缆,且穿的同一个缆
	                                	//满足 合并条件, 管道子孔下敷设的光缆相同,则合并前先对其敷设光缆关系进行处理:修改一段的,删除二段的
	                                    //修改前一段endPointCuid,删除后一段的 WireToDuctline 信息
	                                    //  在循环外面 调用BO 修改,删除方法
	                                    for (int c = 0; c < wire2duline.size(); c++) {
	                                    	WireToDuctline wire2duli = (WireToDuctline) wire2duline.get(c);
	                                        WireToDuctline wire2duli2 = (WireToDuctline) wire2duline2.get(c);
	                                        if (wire2duli.getAttrValue(WireToDuctline.AttrName.wireSegCuid).equals(wire2duli2.getAttrValue(WireToDuctline.AttrName.wireSegCuid))) {
	                                        	wire2duli.setAttrValue(WireToDuctline.AttrName.endPointCuid, wire2duli2.getEndPointCuid());
	                                            moductlinelist.add(wire2duli);
	                                            delductlinelist.add(wire2duli2);
	                                        } else {
	                                        	throw new UserException("选择的段的敷设信息不相同,不能进行合并段");
	                                        }
	                                    }
	                                } else if (wire2duline != null && wire2duline2 != null && wire2duline.size() != wire2duline2.size()) {
	                                	throw new UserException("选择的段的敷设信息不相同,不能进行合并段");
	                                }
	                                 
	                                if ((ductHole.getDestNo().equals(ductHole2.getOrigNo())) || (ductHole.getOrigNo().equals(ductHole2.getDestNo()))) { //编号相同
	                                    //修改管孔 编号A-B:B—A=》A-A
	                                    ductHole.setAttrValue(DuctHole.AttrName.destNo, ductHole2.getAttrValue(DuctHole.AttrName.destNo));
	                                    childholelistOne.sort(DuctChildHole.AttrName.ductChildHoldNum, true); //A-B,B-C,第一段的尾的序号要与第二的头相同
	                                    childholelistTwo.sort(DuctChildHole.AttrName.ductChildHoldNum, true); //子孔排序,按照子孔编号进行
	                                    //判断:方型和圆型都是 BaseEquipment 类型
	                                    BaseEquipment element = (BaseEquipment) boxOne.getElementByTag(ductHole.getCuid());
	                                    BaseEquipment element2 = (BaseEquipment) boxTwo.getElementByTag(ductHole2.getCuid());
	                                    if (!element.getClass().equals(element2.getClass())) { //判断方型和圆型类型是否相同
	                                    	throw new UserException("管孔形状不同,不能合并.");
	                                    }
	                                    if (childholelistOne.size() != childholelistTwo.size()) { //子孔的
	                                    	throw new UserException("子孔数量不相等,不能合并.");
	                                    } else { //管孔下的子孔个数相同
	                                        for (int r = 0; r < childholelistOne.size(); r++) {
	                                            DuctChildHole childhole = (DuctChildHole) childholelistOne.get(r); //一段子孔
	                                            DuctChildHole childhole2 = (DuctChildHole) childholelistTwo.get(r); //二段子孔
	                                            if (!childhole.getDuctChildHoldNum().equals(childhole2.getDuctChildHoldNum())) {
	                                            	throw new UserException("子孔数量不相等,不能合并.");
	                                            } else { //子孔编号相同
	                                                if (childhole.getUsageState() != childhole2.getUsageState()) { //使用状态不同
	                                                	throw new UserException("子孔使用状态不同,不能合并.");
	                                                } else { //使用状态相同
	                                                    //   子孔判断: 编号是否相同(敷设状态)穿缆信息(A,B面),和 使用状态,
	                                                    //   根据子孔的CUID到 ?表  里查光缆的CUID是否相同,相同就看管道截面图
	                                                    if (childhole.getUsageState() == 2L) { //子孔使用状态为 2,占用
	                                                        //考虑敷设到管孔孔的情况  BUG:10853
	                                                        String sqll = WireToDuctline.AttrName.lineSystemCuid + "='" + childhole.getRelatedSystemCuid()+ "' and "+WireToDuctline.AttrName.lineSegCuid + "='" + childhole.getRelatedSegCuid()+ "' and "+WireToDuctline.AttrName.childHoleCuid + "='" + childhole.getCuid() + "'"; //选种的前一段 下有多少 敷设
	                                                        DataObjectList wire2duline1 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
	                                                                WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, sqll); //根据第一段 查他下面的wire_to_ductline

	                                                        String sqll2 =WireToDuctline.AttrName.lineSystemCuid + "='" + childhole2.getRelatedSystemCuid()+ "' and "+WireToDuctline.AttrName.lineSegCuid + "='" + childhole2.getRelatedSegCuid()+ "' and "+ WireToDuctline.AttrName.childHoleCuid + "='" + childhole2.getCuid() + "'"; //选种的后一段 下有多少 敷设
	                                                        DataObjectList wire2duline22 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
	                                                                WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, actionContext, sqll2); //根据第一段 查他下面的wire_to_ductline
	                                                        
	                                                        if (wire2duline1 != null && wire2duline22 != null && (wire2duline1.size() == wire2duline22.size())) { //都 有穿缆,且穿的同一个缆
	                                                            //满足 合并条件, 管道子孔下敷设的光缆相同,则合并前先对其敷设光缆关系进行处理:修改一段的,删除二段的
	                                                            //修改前一段endPointCuid,删除后一段的 WireToDuctline 信息
	                                                            //  在循环外面 调用BO 修改,删除方法
	                                                            for (int c = 0; c < wire2duline1.size(); c++) {
	                                                                WireToDuctline wire2duli = (WireToDuctline) wire2duline1.get(c);
	                                                                WireToDuctline wire2duli2 = (WireToDuctline) wire2duline22.get(c);
	                                                                if (wire2duli.getAttrValue(WireToDuctline.AttrName.wireSegCuid).equals(wire2duli2.getAttrValue(WireToDuctline.AttrName.wireSegCuid))) {
	                                                                	
	                                                                } else {
	                                                                	throw new UserException("敷设光缆信息不同,不能进行合并段");
	                                                                }
	                                                            }
	                                                        } else if (wire2duline1 != null && wire2duline22 != null && wire2duline1.size() != wire2duline22.size()) {
	                                                        	throw new UserException("敷设光缆信息不同,不能进行合并段");
	                                                        }
	                                                    }
	                                                }
	                                            }
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            } else {
	            	throw new UserException("选择段没有共同点,不能进行合并段");
	            }

	            int statt = 1; //入库标志判断
	            if (origendlist.size() != 0) {
//	                IView view = ViewFactory.getInstance().createView("SplitPointView", ShellFactoryName.MainModalDialogShellFactory);
//	                ViewParam vp = new ViewParam();
//	                vp.addViewParam("CHOOSE", true);
//	                vp.addViewParam("POINTLIST", origendlist);
//	                vp.addViewParam("VIEWNAME", PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.view.dm.system.duct.DuctSystemSegView.java60"));
//	                view.openView(vp);
//	                Object o = view.getViewResult();
//	                if (o == null) { //关闭和取消,不做任何操作
//	                }
//	                if (o instanceof java.util.List) { //返回node
//	                    LinkedList LinkedList = (LinkedList) o;
//	                    if (LinkedList.size() == 0) {
//	                        return;
//	                    }
	                    //修改管孔
	                    try {
	                        DataObjectList back = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctHoleBOHelper.ActionName.modifyDuctHoles,
	                                actionContext, ductholes); //修改新段下的管孔的
	                    } catch (Exception ex5) {
	                    }
	                    try {
	                    	if (ductSegTwo.getAttrValue(DuctSeg.AttrName.length) != null) {
	                    		lengthall = Double.valueOf(((Double) ductSegTwo.getAttrValue(DuctSeg.AttrName.length)).doubleValue() + lengthall);
	                        }
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                    ductSegOne.setAttrValue(DuctSeg.AttrName.length, lengthall);
	                    GenericDO destend = (GenericDO) ductSegTwo.getAttrValue(DuctSeg.AttrName.destPointCuid);
	                    ductSegOne.setAttrValue(DuctSeg.AttrName.destPointCuid, destend);
	                    ductSegOne.setLabelCn(origOne.getAttrString(DuctSeg.AttrName.labelCn) + "--" + destTwo.getAttrString(DuctSeg.AttrName.labelCn));
	                    segss = (GenericDO) BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.modifyDuctSeg, actionContext, ductSegOne); //修改第一段

	                    //修改段上的 wire_to_ductline add by libo 一段的不变,二段的删除
	                    try {
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                    origendlist.clear();
	                    statt = 2;
//	                }
	            }
	            if (origendlist.size() == 0 && statt == 1) {
	            	try {
	            		if ((Double) ductSegTwo.getAttrValue(DuctSeg.AttrName.length) != null) {
	            			lengthall = Double.valueOf(((Double) ductSegTwo.getAttrValue(DuctSeg.AttrName.length)).doubleValue() + lengthall);
	                    }
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
	                ductSegOne.setAttrValue(DuctSeg.AttrName.length, lengthall);
	                GenericDO destend = (GenericDO) ductSegTwo.getAttrValue(DuctSeg.AttrName.destPointCuid);
	                ductSegOne.setAttrValue(DuctSeg.AttrName.destPointCuid, destend);
	                ductSegOne.setLabelCn(origOne.getAttrString(DuctSeg.AttrName.labelCn) + "--" + destTwo.getAttrString(DuctSeg.AttrName.labelCn));
	                segss = (GenericDO) BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.modifyDuctSeg, actionContext, ductSegOne); //修改第一段
	            }

	            //修改的阻断,一定要入库
	            if (count == 1) { //含有阻断子孔管孔,合并后会删除阻断子孔管孔.
	                //进行处理    删除阻断管孔子孔把阻断的放到LIST中,然后从BOX中REMOVE.
	                for (int r = 0; r < rupductholeOne.size(); r++) { //一段的阻断管孔
	                    Element e = (Element) rupductholeOne.get(r);
	                    boxOne.removeElement((twaver.Element) e);
	                }
	                for (int r = 0; r < rupductholeTwo.size(); r++) { //二段的阻断管孔
	                    Element e = (Element) rupductholeTwo.get(r);
	                    boxTwo.removeElement((twaver.Element) e);
	                }
					for (int r = 0; r < rupchildholeOne.size(); r++) { // 一段的阻断子孔
						Element e = (Element) rupchildholeOne.get(r);
						boxOne.removeElement((twaver.Element) e);
					}
					for (int r = 0; r < rupchildholeTwo.size(); r++) { // 二段的阻断子孔
						Element e = (Element) rupchildholeTwo.get(r);
						boxTwo.removeElement((twaver.Element) e);
					}
					// 入库 boxone　 boxtwo
					PicBlobUtils.createXmlAllPics(ductSegOne.getCuid(), null, 6022, boxOne);
					PicBlobUtils.createXmlAllPics(ductSegTwo.getCuid(), null, 6022, boxTwo);
				}
	        }
	    }
		ductSegfirst = (DuctSeg) list.get(0);
		DataObjectList deleteSegList = new DataObjectList();
		deleteSegList.addAll(list.subList(1, list.size()));
		DataObjectList modifyWire2Ductlines = MergeSHUSegsHandler.deleteWire2Ductlines(ductSegfirst, deleteSegList, null);

		ductSegfirst.setOrigPointCuid(origgdo1.getCuid());
		ductSegfirst.setDestPointCuid(destgdo1.getCuid());
		MergeSHUSegsHandler.addModifyWire2Ductline(ductSegfirst, modifyWire2Ductlines, list.size() - 1, null);

		IDuctSegBO bo = BoHomeFactory.getInstance().getBO(IDuctSegBO.class);
		bo.deleteDuctSegs(actionContext, deleteSegList);

	    //剩下其后面的序号,要跟着减去 list.size() - 1 ,每次克隆一个新段在入库前,调用一个BO方法:updateDuctindex
	    //把 该新段的curDuctSeg.getIndexInBranch() + i,作为参数,
	    //update 库中大于(curDuctSeg.getIndexInBranch() + i)的序号的段,
	    if (list.size() >= 2) {
	        BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.updateDuctindex2, actionContext, (long) (list.size() - 1), (DuctSeg) mapi.get(list.size() - 1));
	        //MergeSHUSegsHandler.mergeDisplaySegs(list);
	    }

	    DuctSeg ductSegOne = (DuctSeg) list.get(0);
	    String segsql = WireToDuctline.AttrName.lineSystemCuid + "='" + ductSegOne.getRelatedSystemCuid()+ "' and "+ WireToDuctline.AttrName.lineSegCuid + "='" + ductSegOne.getCuid() + "'"; //选种的前一段 下有多少 敷设WireToDuctline.AttrName.lineSegCuid + "='" + ductSegOne.getCuid() + "'";
	    DataObjectList wire2ductlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,
	        actionContext, segsql); //根据第一段 查他下面的wire_to_ductline

	    return segss;
//	    refreshSystemsByW2ds(wire2ductlist);
//	        
//	    refreshSystemsBySeg(list);
	}

	private static void getMapValue(DuctSeg ductSegone, List<Element> allElements, // 把BOX里面的元素都放到对应的LIST
			int count, DataObjectList ductholes, List rupductholeone, List rupchildholeone, DataObjectList childholelistone,BoActionContext actionContext) throws Exception {
		Map map2 = getDuctSegHoles(ductSegone,actionContext);
		Map ductHoleMap = (Map) map2.get(DuctHole.CLASS_NAME);
		Map childHoleMap = (Map) map2.get(DuctChildHole.CLASS_NAME);
		for (Element element : allElements) {
			if (element instanceof SquareHoleNode || element instanceof RoundHoleNode) {
				String tag = ((BaseEquipment) element).getTag();
				GenericDO dbo = (GenericDO) ductHoleMap.get(tag);
				if (dbo != null) {
					List objs = ductholes.getObjectByCuid(dbo.getCuid());
					if (objs.size() == 0)
						ductholes.add(dbo);
				} else { // 阻断管孔
					rupductholeone.add(element);
					count = 1;
				}
			} else if (element instanceof ChildHoleNode) {
				String tag = ((ChildHoleNode) element).getTag();
				GenericDO dbo = (GenericDO) childHoleMap.get(tag);
				if (dbo != null) {
					List objs = childholelistone.getObjectByCuid(dbo.getCuid());
					if (objs.size() == 0)
						childholelistone.add(dbo);
				} else {
					count = 1;
					rupchildholeone.add(element);
				}
			}
		}
	}
	 private static Map getDuctSegHoles(DuctSeg ductSeg,BoActionContext actionContext) throws Exception {
		 Map rstMap = (Map) BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.getHoleAndChildHole, new Object[] {actionContext, ductSeg});
	     ductSeg.setAttrValue(ClientConsts.DUCTSEGHOLENUM, ((Map) rstMap.get("DUCT_HOLE")).size());
	     return rstMap;
	}
}
