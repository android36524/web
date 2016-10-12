package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twaver.BaseEquipment;
import twaver.Element;
import twaver.TDataBox;

import com.boco.common.util.debug.LogHome;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.core.utils.exception.UserException;
import com.boco.graphkit.ext.ChildHoleNode;
import com.boco.graphkit.ext.ClientConsts;
import com.boco.graphkit.ext.RoundHoleNode;
import com.boco.graphkit.ext.SquareHoleNode;
import com.boco.irms.app.dm.gridbo.AbstractPropTemplateBO;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.DisplaySeg;
import com.boco.transnms.common.dto.DuctChildHole;
import com.boco.transnms.common.dto.DuctHole;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.DuctDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineDisplaySegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctSegBO;

public class UniteDuctSegAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    public UniteDuctSegAction() {
    }
    
    /**
     * 管道段合并功能实现
     * @param segsLists
     * @param labelCn
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void ductUniteSegs(List<Map> segsLists){
		DataObjectList segLists = new DataObjectList();
		if(segsLists != null && segsLists.size()>0){
			for(int i=0; i<segsLists.size(); i++){
				Map segMaps = segsLists.get(i);
				String segCuid = segMaps.get("CUID").toString();
				String segClassName = GenericDO.parseClassNameFromCuid(segCuid);
				GenericDO gdo = WebDMUtils.createInstanceByClassName(segClassName, segMaps);
				DuctSeg ductSeg = (DuctSeg) gdo;
				
				String getSegMethod = "IDuctSegBO.getDuctSegBySql";
				DataObjectList ductSegs = new DataObjectList();
		  		String sqls = " 1 = 1 AND CUID = '" + segCuid + "'";
		  		try {
		  			ductSegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(getSegMethod, new BoActionContext(),sqls);
		  		} catch (Exception e) {
		  			logger.error("获取系统段对象失败",e);
		  			e.printStackTrace();
		  		}
		  		Object startPoint = null;
		  		Object endPoint = null;
		  		
		  		if(ductSegs != null && ductSegs.size() != 0){
		  			GenericDO lastSeg =ductSegs.get(0);
		  			startPoint = lastSeg.getAttrValue("ORIG_POINT_CUID");
		  			endPoint = lastSeg.getAttrValue("DEST_POINT_CUID");
		  		}
		  		if(endPoint !=null && !endPoint.equals(null) && !endPoint.equals("")){
		  			ductSeg.setAttrValue("DEST_POINT_CUID", endPoint);
		  		}
		  		if(startPoint != null && !startPoint.equals(null) && !startPoint.equals("")){
		  			ductSeg.setAttrValue("ORIG_POINT_CUID", startPoint);
		  		}
		  		ductSeg.removeAttr("LAST_MODIFY_TIME");
		  		ductSeg.removeAttr("CREATE_TIME");
				segLists.add(ductSeg);
			}
		}
		DuctSeg ductSegfirst = (DuctSeg) segLists.get(0);
		double lengthall = 0.0d;
		if (ductSegfirst.getAttrValue("LENGTH") != null) {
			lengthall = (Double) ductSegfirst.getAttrValue("LENGTH"); // 1.循环:第一段 ,与下一段的长度累加
		}
		Map mapi = new HashMap();
		DataObjectList origendlist = new DataObjectList(); // 合并后的头尾点
		
		for (int i = 0; i < segLists.size() - 1; i++) {
			mapi.put(1 + i, segLists.get(i + 1)); // 把合并点装入 mapi,用于弹出的界面显示路由点
		}
		// 列出 合并后的 头尾点 ,把 合并后生成的 头尾点取出 ,用于弹出的界面显示路由点
		
		GenericDO origgdo1 = (GenericDO) segLists.get(0).getAttrValue("ORIG_POINT_CUID");
		GenericDO destgdo1 = (GenericDO) segLists.get(segLists.size()-1).getAttrValue("DEST_POINT_CUID");
		origendlist.add(origgdo1);
		origendlist.add(destgdo1);
		
		for (int n = 0; n < segLists.size() - 1; n++) {// 循环段,LIST里面每个段都合并到第一个段上
			DuctSeg ductSegOne = (DuctSeg) segLists.get(0);  // 第一条管道段
			DuctSeg ductSegTwo = (DuctSeg) segLists.get(n + 1);  // 后一条管道段
			GenericDO origOne = (GenericDO) ductSegOne.getAttrValue("ORIG_POINT_CUID"); //第一条记录的起点
			GenericDO destOne = (GenericDO) ductSegOne.getAttrValue("DEST_POINT_CUID"); //第一条记录的终点
			GenericDO origTwo = (GenericDO) ductSegTwo.getAttrValue("ORIG_POINT_CUID"); //最后一条记录的起点
			GenericDO destTwo = (GenericDO) ductSegTwo.getAttrValue("DEST_POINT_CUID"); //最后一条记录的终点
			
			// 分支相同
			DataObjectList ductholes = new DataObjectList(); // 一段所有管孔
			DataObjectList ductholes2 = new DataObjectList(); // 二段所有管孔
			DataObjectList childholelistOne = new DataObjectList(); // 一段子孔
			DataObjectList childholelistTwo = new DataObjectList(); // 二段子孔
			List rupductholeOne = new ArrayList(); // 一段的阻断管孔
			List rupductholeTwo = new ArrayList(); // 二段的阻断管孔
			List rupchildholeOne = new ArrayList(); // 一段的阻断子孔
			List rupchildholeTwo = new ArrayList(); // 二段的阻断子孔

			int count = 0; // 保存阻断孔的标记
			TDataBox boxOne = new TDataBox();
			TDataBox boxTwo = new TDataBox();
			// PicBlobUtils.getAllPicBlobBytes(ductSegOne.getCuid(), null,6022L,boxOne, null); // 取一段的BOX
			// PicBlobUtils.getAllPicBlobBytes(ductSegTwo.getCuid(), null,6022L,boxTwo, null);

			List<Element> allElements = boxOne.getAllElements();
			List<Element> allElements2 = boxTwo.getAllElements();
			try {
				getMapValue(ductSegOne, allElements, count, ductholes, // 把BOX里面的元素都放到对应的LIST
						rupductholeOne, rupchildholeOne, childholelistOne);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				getMapValue(ductSegTwo, allElements2, count, ductholes2, // 把BOX里面的元素都放到对应的LIST
						rupductholeTwo, rupchildholeTwo, childholelistTwo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			DataObjectList moductlinelist = new DataObjectList(); // 存放wiretoductline用于修改
			DataObjectList delductlinelist = new DataObjectList(); // 存放wiretoductline用于删除
            if ((destOne.getCuid()).equals(origTwo.getCuid()) || (origOne.getCuid()).equals(destTwo.getCuid())) {
            	if (ductholes.size() != ductholes2.size()) { //管孔个数 不相同
            		JOptionPane.showMessageDialog(null,"管孔个数不相同,不能合并");
                    return;
                } else { // 管孔个数 相同
                	//阻断管孔个数是否一致 
                    if (rupductholeOne.size() != rupductholeTwo.size()) { //阻断管孔个数 不相同
                    	JOptionPane.showMessageDialog(null, "阻断管孔个数不相同");
                        return;
                    } else { // 管孔个数 相同
                    	ductholes.sort("DEST_NO", true); //A-B,B-C,第一段的尾的序号要与第二的头相同
                        ductholes2.sort("ORIG_NO", true);
                        //直接合并,看敷设情况 
                        //选中的前一段 下有多少 敷设
                        String segsql = " LINE_SYSTEM_CUID ='" + ductSegOne.getRelatedSystemCuid()
                                      + "' and LINE_SEG_CUID ='" + ductSegOne.getCuid() + "'"; 
                        DataObjectList wire2ductlist = getWire2DuctLineBySql(segsql);

                        //选中的后一段 下有多少 敷设
                        String segsql2 = " LINE_SYSTEM_CUID='" + ductSegTwo.getRelatedSystemCuid()
                          		+ "' and LINE_SEG_CUID ='" + ductSegTwo.getCuid() + "'";  
                        DataObjectList wire2ductlist2 = getWire2DuctLineBySql(segsql2);

                        if (wire2ductlist != null && wire2ductlist2 != null &&
                           (wire2ductlist.size() == wire2ductlist2.size())) { //都 有穿缆,且穿的同一个缆
                        	//满足 合并条件, 管道子孔下敷设的光缆相同,则合并前先对其敷设光缆关系进行处理:修改一段的,删除二段的
                            wire2ductlist.sort("WIRE_SEG_CUID", true);
                            wire2ductlist2.sort("WIRE_SEG_CUID", true);
                            for (int c = 0; c < wire2ductlist.size(); c++) {
                                WireToDuctline wire2duli = (WireToDuctline) wire2ductlist.get(c);
                                WireToDuctline wire2duli2 = (WireToDuctline) wire2ductlist2.get(c);
                                if (wire2duli.getAttrValue("WIRE_SEG_CUID").equals(wire2duli2.getAttrValue("WIRE_SEG_CUID"))) {
                                    wire2duli.setAttrValue("END_POINT_CUID",wire2duli2.getEndPointCuid());
                                    moductlinelist.add(wire2duli);
                                    delductlinelist.add(wire2duli2);
                                } else {
                                	JOptionPane.showMessageDialog(null,"选择的段的敷设信息不相同,不能合并!");
                                    return;
                                }
                            }    
                        } else if (wire2ductlist != null && wire2ductlist2 != null && wire2ductlist.size() != wire2ductlist2.size()) {
                            JOptionPane.showMessageDialog(null, "选择的段的敷设信息不相同,不能合并!");
                            return;
                        }
                        if (rupductholeOne.size() > 0) { //比较阻断管孔的形状
                            for (int t = 0; t < rupductholeOne.size(); t++) {
                                Element rupholeOne = (Element) rupductholeOne.get(t); //一段的阻断管孔
                                Element rupholeTwo = (Element) rupductholeTwo.get(t); //二段的阻断管孔
                                String nodd1 = rupholeOne.getIconURL();
                                String nodd2 = rupholeTwo.getIconURL();
                                if (!nodd1.equals(nodd2)) { //判断方型和圆型类型是否相同
                                    JOptionPane.showMessageDialog(null, "阻断管孔的形状不相同,不能合并!");
                                    return;
                                }
                            }
                        }

                        if (ductholes.size() != 0 && ductholes.size()>0) { // 管孔个数相同且大于0,存在相同个数的管孔
                            for (int t = 0; t < ductholes.size(); t++) {
                                DuctHole ductHole = (DuctHole) ductholes.get(t); //一段管孔
                                DuctHole ductHole2 = (DuctHole) ductholes2.get(t); //二段管孔
                                if (!ductHole.getDestNo().equals(ductHole2.getOrigNo())) { //编号不同
                                    JOptionPane.showMessageDialog(null, "管孔编号不同,不能合并!");
                                    return;
                                }
                                  
                                //考虑敷设到管孔的情况 
                                //选种的前一段 下有多少 敷设
                                String squl = " LINE_SYSTEM_CUID ='" + ductHole.getRelatedSystemCuid()
                                  		+ "' and LINE_SYSTEM_CUID ='" + ductHole.getRelatedSegCuid()
                                  		+ "' and HOLE_CUID ='" + ductHole.getCuid() + "'";
                                //根据第一段 查他下面的wire_to_ductline
                                DataObjectList wire2duline = getWire2DuctLineBySql(squl);

                                //选种的后一段 下有多少 敷设
                                String squl2 = " LINE_SYSTEM_CUID='" + ductHole2.getRelatedSystemCuid()
                                  		+ "' and LINE_SEG_CUID ='" + ductHole2.getRelatedSegCuid()
                                  		+ "' and HOLE_CUID ='" + ductHole2.getCuid() + "'"; 
                                //根据第一段 查他下面的wire_to_ductline
                                DataObjectList wire2duline2 = getWire2DuctLineBySql(squl2);
                                  
                                
                                if (wire2duline != null && wire2duline2 != null &&
                                   (wire2duline.size() == wire2duline2.size())) { //都 有穿缆,且穿的同一个缆
                                    //满足 合并条件, 管道子孔下敷设的光缆相同,则合并前先对其敷设光缆关系进行处理:修改一段的,删除二段的
                                    for (int c = 0; c < wire2duline.size(); c++) {
                                        WireToDuctline wire2duli = (WireToDuctline) wire2duline.get(c);
                                        WireToDuctline wire2duli2 = (WireToDuctline) wire2duline2.get(c);
                                        if (wire2duli.getAttrValue("WIRE_SEG_CUID").equals(wire2duli2.getAttrValue("WIRE_SEG_CUID"))) {
                                            wire2duli.setAttrValue("END_POINT_CUID",wire2duli2.getEndPointCuid());
                                            moductlinelist.add(wire2duli);
                                            delductlinelist.add(wire2duli2);
                                        } else {
                                            JOptionPane.showMessageDialog(null, "选择的段的敷设信息不相同,不能进行合并段");
                                            return;
                                        }
                                    }
                                } else if (wire2duline != null && wire2duline2 != null && wire2duline.size() != wire2duline2.size()) {
                                    JOptionPane.showMessageDialog(null, "选择的段的敷设信息不相同,不能进行合并段");
                                    return;
                                }

                                if ((ductHole.getDestNo().equals(ductHole2.getOrigNo())) ||
                                    (ductHole.getOrigNo().equals(ductHole2.getDestNo()))) { //编号相同
                                    //修改管孔 编号A-B:B—A=》A-A
                                    ductHole.setAttrValue("DEST_NO", ductHole2.getAttrValue("DEST_NO"));

                                    childholelistOne.sort("DUCT_CHILD_HOLD_NUM", true); //A-B,B-C,第一段的尾的序号要与第二的头相同
                                    childholelistTwo.sort("DUCT_CHILD_HOLD_NUM", true); //子孔排序,按照子孔编号进行
                                    //判断:方型和圆型都是 BaseEquipment 类型
                                    BaseEquipment element = (BaseEquipment) boxOne.getElementByTag(ductHole.getCuid());
                                    BaseEquipment element2 = (BaseEquipment) boxTwo.getElementByTag(ductHole2.getCuid());
                                    if (!element.getClass().equals(element2.getClass())) { //判断方型和圆型类型是否相同
                                        JOptionPane.showMessageDialog(null,"管孔形状不同,不能合并.");
                                        return;
                                    }
                                    if (childholelistOne.size() != childholelistTwo.size()) { //子孔的
                                        JOptionPane.showMessageDialog(null, "子孔数量不相等,不能合并.");
                                        return;
                                    } else { //管孔下的子孔个数相同
                                    	for (int r = 0; r < childholelistOne.size(); r++) {
                                    		DuctChildHole childhole = (DuctChildHole) childholelistOne.get(r); //一段子孔
                                            DuctChildHole childhole2 = (DuctChildHole) childholelistTwo.get(r); //二段子孔
                                            if (!childhole.getDuctChildHoldNum().equals(childhole2.getDuctChildHoldNum())) {
                                                JOptionPane.showMessageDialog(null, "子孔数量不相等,不能合并.");
                                                return;
                                            } else { //子孔编号相同
                                                if (childhole.getUsageState() != childhole2.getUsageState()) { //使用状态不同
                                                    JOptionPane.showMessageDialog(null, "子孔使用状态不同,不能合并.");
                                                    return;
                                                } else { //使用状态相同
                                                    //   子孔判断: 编号是否相同(敷设状态)穿缆信息(A,B面),和 使用状态,
                                                    //   根据子孔的CUID到 ?表  里查光缆的CUID是否相同,相同就看管道截面图
                                                    if (childhole.getUsageState() == 2L) { //子孔使用状态为 2,占用
                                                    	//选种的前一段 下有多少 敷设
                                                    	//考虑敷设到管孔孔的情况
                                                        String sql1 = " LINE_SYSTEM_CUID='" + childhole.getRelatedSystemCuid()
                                                          		+ "' and LINE_SEG_CUID ='" + childhole.getRelatedSegCuid()
                                                          		+ "' and CHILD_HOLE_CUID ='" + childhole.getCuid() + "'"; 
                                                        //根据第一段 查他下面的wire_to_ductline
                                                        DataObjectList wire2duline1 = getWire2DuctLineBySql(sql1);

                                                        String sql2 = " LINE_SYSTEM_CUID ='" + childhole2.getRelatedSystemCuid()
                                                          		+ "' and LINE_SYSTEM_CUID ='" + childhole2.getRelatedSegCuid()
                                                          		+ "' and CHILD_HOLE_CUID ='" + childhole2.getCuid() + "'"; //选种的后一段 下有多少 敷设
                                                        //根据第一段 查他下面的wire_to_ductline
                                                        DataObjectList wire2duline22 = getWire2DuctLineBySql(sql2);

                                                        if (wire2duline1 != null && wire2duline22 != null &&
                                                           (wire2duline1.size() == wire2duline22.size())) { //都 有穿缆,且穿的同一个缆
                                                        	//满足 合并条件, 管道子孔下敷设的光缆相同,则合并前先对其敷设光缆关系进行处理:修改一段的,删除二段的
                                                            for (int c = 0; c < wire2duline1.size(); c++) {
                                                                WireToDuctline wire2duli = (WireToDuctline) wire2duline1.get(c);
                                                                WireToDuctline wire2duli2 = (WireToDuctline) wire2duline22.get(c);
                                                                if (!wire2duli.getAttrValue("WIRE_SEG_CUID").equals(wire2duli2.getAttrValue("WIRE_SEG_CUID"))) {
                                                                	JOptionPane.showMessageDialog(null, "管孔下敷设光缆信息不同,不能进行合并段");
                                                                    return;
                                                                }
                                                            }
                                                        } else if (wire2duline1 != null && wire2duline22 != null &&
                                                                   wire2duline1.size() != wire2duline22.size()) {
                                                            JOptionPane.showMessageDialog(null, "管道管孔下敷设光缆信息不同,不能合并.");
                                                            return;
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
            }
            
            int statt = 1; // 入库标志判断
  			if (origendlist.size() != 0) {
  				// 修改管孔
  				try {// 修改新段下的管孔的
  					DataObjectList back = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
  							"IDuctHoleBO.modifyDuctHoles",new BoActionContext(), ductholes); 
  				} catch (Exception ex5) {
  					ex5.printStackTrace();
  				}
  	  			modifyDuctSegByObject(ductSegOne, ductSegTwo, lengthall, origOne, destOne, origTwo, destTwo);
  				statt = 2;
  		}
  		if (origendlist.size() == 0 && statt == 1) {
  			modifyDuctSegByObject(ductSegOne, ductSegTwo, lengthall, origOne, destOne, origTwo, destTwo);
  		}
  		// 修改的阻断,一定要入库
  		if (count == 1) { // 含有阻断子孔管孔,合并后会删除阻断子孔管孔.
  			// 进行处理 删除阻断管孔子孔把阻断的放到LIST中,然后从BOX中REMOVE.
  			for (int r = 0; r < rupductholeOne.size(); r++) { // 一段的阻断管孔
  				Element e = (Element) rupductholeOne.get(r);
  				boxOne.removeElement(e);
  			}
  			for (int r = 0; r < rupductholeTwo.size(); r++) { // 二段的阻断管孔
  				Element e = (Element) rupductholeTwo.get(r);
  				boxTwo.removeElement(e);
  			}
  			for (int r = 0; r < rupchildholeOne.size(); r++) { // 一段的阻断子孔
  				Element e = (Element) rupchildholeOne.get(r);
  				boxOne.removeElement(e);
  			}
  			for (int r = 0; r < rupchildholeTwo.size(); r++) { // 二段的阻断子孔
  				Element e = (Element) rupchildholeTwo.get(r);
  				boxTwo.removeElement(e);
  			}
  			// 入库 boxone　 boxtwo
//  			PicBlobUtils.createXmlAllPics(ductSegOne.getCuid(), null, 6022,boxOne);
//  			PicBlobUtils.createXmlAllPics(ductSegTwo.getCuid(), null, 6022,boxTwo);
  		}
  	}
	for(int i=0; i< segLists.size()-1; i++){
		DataObjectList deleteSegList=new DataObjectList();
        deleteSegList.add((GenericDO) segLists.get(i+1));//subList(1, segLists.size())
        DataObjectList modifyWire2Ductlines = UniteDuctSegAction.deleteWire2Ductlines(ductSegfirst, deleteSegList);
        ductSegfirst.setOrigPointCuid(origgdo1.getCuid());
        ductSegfirst.setDestPointCuid(destgdo1.getCuid());
        UniteDuctSegAction.addModifyWire2Ductline(ductSegfirst, modifyWire2Ductlines, segLists.size()-1);
        IDuctSegBO bo = BoHomeFactory.getInstance().getBO(IDuctSegBO.class);
        bo.deleteDuctSegs( new BoActionContext(), deleteSegList);  
    }
	//剩下其后面的序号,要跟着减去 list.size() - 1 ,每次克隆一个新段在入库前,调用一个BO方法:updateDuctindex
    //把 该新段的curDuctSeg.getIndexInBranch() + i,作为参数,
    //update 库中大于(curDuctSeg.getIndexInBranch() + i)的序号的段,
    if (segLists.size() >= 2) {
    	try {
    		BoCmdFactory.getInstance().execBoCmd("IDuctSegBO.updateDuctindex2", new BoActionContext(),
				  	(long)(segLists.size()-1), (DuctSeg)mapi.get(segLists.size()-1));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			mergeDisplaySegs(segLists);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    //选中的前一段 下有多少 敷设
    String segsql = " LINE_SYSTEM_CUID ='" + ductSegfirst.getRelatedSystemCuid()
       	  + "' and LINE_SEG_CUID ='" + ductSegfirst.getCuid() + "'"; 
    DataObjectList wire2ductlist = getWire2DuctLineBySql(segsql);
  }
	/**
     * 获取敷设信息
     * @param sql
     * @return
     */
    public DataObjectList getWire2DuctLineBySql(String sql){
    	DataObjectList wire2ductlist = new DataObjectList();
		try {
			wire2ductlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
			  		"IWireToDuctLineBO.getWireToDuctLineBySql", new BoActionContext(), sql);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return wire2ductlist;
    	
    }
	/**
	 * 修改管道段
	 * @param ductSegOne
	 * @param ductSegTwo
	 * @param lengthall
	 * @param origOne
	 * @param destOne
	 * @param origTwo
	 * @param destTwo
	 */
	public void modifyDuctSegByObject(DuctSeg ductSegOne,DuctSeg ductSegTwo,double lengthall,
			GenericDO origOne,GenericDO destOne,GenericDO origTwo,GenericDO destTwo){
		if (ductSegTwo.getAttrValue("LENGTH") != null) {
				lengthall = Double.valueOf(((Double) ductSegTwo.getAttrValue("LENGTH")).doubleValue()+ lengthall);
	    }
		ductSegOne.setAttrValue("LENGTH", lengthall);
		if((destOne.getCuid()).equals(origTwo.getCuid())){
			GenericDO destend = (GenericDO) ductSegTwo.getAttrValue("DEST_POINT_CUID");
			ductSegOne.setAttrValue("DEST_POINT_CUID",destend);
			ductSegOne.setLabelCn(origOne.getAttrValue("LABEL_CN") + "--" + destTwo.getAttrString("LABEL_CN"));
		}
		if((origOne.getCuid()).equals(destTwo.getCuid())){
			GenericDO origstart = (GenericDO) ductSegTwo.getAttrValue("ORIG_POINT_CUID");
			ductSegOne.setAttrValue("ORIG_POINT_CUID",origstart);
			ductSegOne.setLabelCn(origTwo.getAttrValue("LABEL_CN") + "--" + destOne.getAttrString("LABEL_CN"));
		}
		try {
		BoCmdFactory.getInstance().execBoCmd(
				"IDuctSegBO.modifyDuctSeg",new BoActionContext(), ductSegOne);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static Map getDuctSegHoles(DuctSeg ductSeg) throws Exception {
        Map rstMap = (Map) BoCmdFactory.getInstance().execBoCmd("IDuctSegBO.getHoleAndChildHole", new Object[] {
            new BoActionContext(), ductSeg});
        ductSeg.setAttrValue(ClientConsts.DUCTSEGHOLENUM, ((Map) rstMap.get("DUCT_HOLE")).size());
        return rstMap;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static void getMapValue(DuctSeg ductSegone,
			List<Element> allElements, // 把BOX里面的元素都放到对应的LIST
			int count, DataObjectList ductholes, List rupductholeone,
			List rupchildholeone, DataObjectList childholelistone)
			throws Exception {
		Map map2 = getDuctSegHoles(ductSegone);
		Map ductHoleMap = (Map) map2.get("DUCT_HOLE");
		Map childHoleMap = (Map) map2.get("DUCT_CHILD_HOLE");
		for (Element element : allElements) {
			if (element instanceof SquareHoleNode || element instanceof RoundHoleNode) {
				String tag = ((BaseEquipment) element).getTag();
				GenericDO dbo = (GenericDO) ductHoleMap.get(tag);
				if (dbo != null) {
					List objs = ductholes.getObjectByCuid(dbo.getCuid());
					if (objs.size() == 0) {
						ductholes.add(dbo);
					}
				} else { // 阻断管孔
					rupductholeone.add(element);
					count = 1;
				}
			} else if (element instanceof ChildHoleNode) {
				String tag = ((ChildHoleNode) element).getTag();
				GenericDO dbo = (GenericDO) childHoleMap.get(tag);
				if (dbo != null) {
					List objs = childholelistone.getObjectByCuid(dbo.getCuid());
					if (objs.size() == 0) {
						childholelistone.add(dbo);
					}
				} else {
					count = 1;
					rupchildholeone.add(element);
				}
			}
		}
	}
	/**
     * 合并管线段的时候调用,必须要求是都合并到第一段上的情况才可以使用
     * 删除 段上经过的敷设信息,
     * 并且已经将要修改的段对应的敷设段删除并返回.目的是断开所有光缆和管线的关联,在所有信息都入库之后,在添加进去
     * @param modifySeg
     * @param deleteSegList
     * @return
     */
    public static DataObjectList deleteWire2Ductlines(GenericDO modifySeg,DataObjectList deleteSegList){
    	DataObjectList modifyWireToDuctlinelist =null;
    	try {
    		String currentSystemCuid=DMHelper.getRelatedCuid(modifySeg.getAttrValue("RELATED_SYSTEM_CUID"));
		    String currentBranchCuid=DMHelper.getRelatedCuid(modifySeg.getAttrValue("RELATED_BRANCH_CUID"));
				 
			//如果currentBranchCuid 为空 ,引上或者挂墙情况,不添加到sql中,该条sql 只是为了优化效率
			String systemSql=" LINE_SYSTEM_CUID ='" + currentSystemCuid + "' and "+
		        (currentBranchCuid==null?"":( " LINE_BRANCH_CUID ='" + currentBranchCuid + "' and "));
				 
			String unitesegsql = "";
			for (int i = 0; i < deleteSegList.size(); i++) {
				if (unitesegsql.equals("")) {
					unitesegsql = " LINE_SEG_CUID ='" + deleteSegList.get(i).getCuid() + "'";
				} else {
					unitesegsql = unitesegsql + " or " + " LINE_SEG_CUID ='" + deleteSegList.get(i).getCuid() + "'";
				}
			}
			if (!unitesegsql.equals("")) {
				try {
					DataObjectList deleteWireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
						 "IWireToDuctLineBO.getWireToDuctLineBySql",new BoActionContext(), systemSql+unitesegsql);
				    BoCmdFactory.getInstance().execBoCmd(
					     "IWireToDuctLineBO.deleteWireToDuctlines",new BoActionContext(),deleteWireToDuctlinelist);
					} catch (Exception e) {
						e.printStackTrace();
					}
				 }
				 
				 unitesegsql = "";
				 unitesegsql = " LINE_SEG_CUID ='" + modifySeg.getCuid() + "'";
				 if (!unitesegsql.equals("")) {
					 try {
						 modifyWireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
			   		 	     "IWireToDuctLineBO.getWireToDuctLineBySql",new BoActionContext(), systemSql+unitesegsql);
			   		     BoCmdFactory.getInstance().execBoCmd(
			   			     "IWireToDuctLineBO.deleteWireToDuctlines",new BoActionContext(),modifyWireToDuctlinelist);
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
     */
    public static void addModifyWire2Ductline(GenericDO modifySeg,DataObjectList modifyw2dList,int deletePointCount){
    	String destPointCuid=DMHelper.getRelatedCuid(modifySeg.getAttrValue("DEST_POINT_CUID")); //获取终止点的CUID
    	String origPointCuid=DMHelper.getRelatedCuid(modifySeg.getAttrValue("ORIG_POINT_CUID")); //获取起始点的CUID
        if (modifyw2dList != null) {
        	try {
        		DataObjectList modifyploewayseglist=new DataObjectList();
				for(GenericDO gdo : modifyw2dList){
					if(gdo instanceof WireToDuctline){	
						WireToDuctline wtd=(WireToDuctline) gdo;
						wtd.setEndPointCuid(destPointCuid);
						wtd.setDisPointCuid(origPointCuid);
						if (wtd.getDirection() == 2) {
							wtd.setIndexInRoute(wtd.getIndexInRoute()-deletePointCount);
					    }
						modifyploewayseglist.add(wtd);
					}
				}
				BoCmdFactory.getInstance().execBoCmd("IWireToDuctLineBO.addWireToDuctlines", 
						new BoActionContext(), modifyploewayseglist);
				for (GenericDO gdo : modifyploewayseglist) {
					BoCmdFactory.getInstance().execBoCmd( "IWireToDuctLineBO.modfiyWireToDuctlineIndexs",
							new BoActionContext(), (long) (0-deletePointCount), gdo); // 修改后续的序号
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    public static void mergeDisplaySegs(DataObjectList segLists) throws Exception{
        PhysicalJoin origPJ = (PhysicalJoin)segLists.get(0);
        PhysicalJoin destPJ = (PhysicalJoin)segLists.get(segLists.size() - 1);
        DataObjectList oldDisSegList = getOldDisplaySegsInBranch(origPJ);
        DataObjectList segList = getSegsInBranch(origPJ);
        oldDisSegList.sort("INDEX_IN_BRANCH", true);
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
        for (int i = 0; i < segList.size(); i++) {
            if (!(segList.get(i) instanceof PhysicalJoin)) continue;
            PhysicalJoin physicalJoin = (PhysicalJoin)segList.get(i);
            if (i == 0) realPointList.add(physicalJoin.getOrigPointCuid());
            realPointList.add(physicalJoin.getDestPointCuid());
        }
        String preDisPointCuid = null;
        String offDisPointCuid = null;
        String origPJCuid = DMHelper.getRelatedCuid(origPJ.getAttrValue("ORIG_POINT_CUID")); //获取起始点的CUID
        String destPJCuid = DMHelper.getRelatedCuid(destPJ.getAttrValue("DEST_POINT_CUID")); //获取终止点的CUID
        for (int i = realPointList.size()- 1; i > -1; i--) {
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
        DataObjectList delDisSegList = new DataObjectList();
        DataObjectList modDisSegList = new DataObjectList();
        long preIndex = 0L;
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
        List<String> disPointCuidList = new ArrayList<String>();
        disPointCuidList.add(preDisPointCuid);
        disPointCuidList.add(offDisPointCuid);
        DisplaySeg templateDisSeg = (DisplaySeg)delDisSegList.get(0);
        templateDisSeg.clearUnknowAttrs();
        templateDisSeg.convAllObjAttrToCuid();
        DataObjectList newDisSegList = new DataObjectList();
        for (int i = 0; i < disPointCuidList.size() - 1; i++) {
            DisplaySeg newDisSeg = (DisplaySeg) templateDisSeg.cloneByClassName();
            newDisSeg.setCuid();
            newDisSeg.setOrigPointCuid(disPointCuidList.get(i));
            newDisSeg.setDestPointCuid(disPointCuidList.get(i + 1));
            newDisSeg.setIndexInBranch(++preIndex);
            newDisSegList.add(newDisSeg);
        }
        for (int i = 0; i < modDisSegList.size(); i++) {
            if (!(modDisSegList.get(i) instanceof DisplaySeg)) continue;
            DisplaySeg disSeg = (DisplaySeg)modDisSegList.get(i);
            disSeg.setIndexInBranch(disSeg.getIndexInBranch() + newDisSegList.size() - delDisSegList.size());
        }
        BoCmdFactory.getInstance().execBoCmd(getDelActionName(origPJ.getClassName()), new BoActionContext(), delDisSegList);
        BoCmdFactory.getInstance().execBoCmd(getAddActionName(origPJ.getClassName()), new BoActionContext(), newDisSegList);
        BoCmdFactory.getInstance().execBoCmd(getModActionName(origPJ.getClassName()), new BoActionContext(), modDisSegList);
    }
    
    private static DataObjectList getOldDisplaySegsInBranch(PhysicalJoin splitedSeg){
        String relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_BRANCH_CUID"));
        if (splitedSeg instanceof UpLineSeg || splitedSeg instanceof HangWallSeg) {
            relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_SYSTEM_CUID"));
        }
        return getOldDisplaySegsInBranch(relatedBranchCuid);
    }
    
    private static DataObjectList getOldDisplaySegsInBranch(String relatedCuid){
        try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getDispalySegsBySystemCuid",
                    new BoActionContext(), relatedCuid);
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
        return getSegsInBranch(relatedBranchCuid);
    }
    
    public static DataObjectList getSegsInBranch(String relatedCuid){
        try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getSegsBySystemCuid",
                    new BoActionContext(), relatedCuid);
        } catch (Exception ex) {
            LogHome.getLog().error("获取系统段数据失败!", ex);
        }
        return null;
    }
    /**
     * 系统的BO删除方法
     * @param className
     * @return
     */
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
    /**
     * 系统的BO添加方法
     * @param className
     * @return
     */
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
    /**
     * 系统的BO修改方法
     * @param className
     * @return
     */
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
}
