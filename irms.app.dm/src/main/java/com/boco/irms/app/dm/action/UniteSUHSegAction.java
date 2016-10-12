package com.boco.irms.app.dm.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.debug.LogHome;
import com.boco.graphkit.ext.gis.GraphkitUtils;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.dm.CoordPoint;

public class UniteSUHSegAction extends UnitePWSegAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 合并标石路由、引上、挂墙段
	 */
	public void uniteSegs(List<Map> segsLists){
		DataObjectList pointList = new DataObjectList();//合并点
		DataObjectList segLists = new DataObjectList();
		String breachcuid = "";
		if(segsLists != null && segsLists.size()>0){
			for(int i=0; i<segsLists.size(); i++){
				Map segMaps = segsLists.get(i);
				String segCuid = segMaps.get("CUID").toString();
				String segClassName = GenericDO.parseClassNameFromCuid(segCuid);
				if(!segClassName.equals(null) && !segClassName.equals("")){
					GenericDO gdo = WebDMUtils.createInstanceByClassName(segClassName, segMaps);
					DataObjectList uniteSegs = new DataObjectList();
					String sqls = " 1 = 1 AND CUID = '" + segCuid + "'";
			  		Object startPoint = null;
			  		Object endPoint = null;
					if(segClassName.equals("STONEWAY_SEG")){
						StonewaySeg stonewaySeg = (StonewaySeg)gdo;
						String getSegMethod = "IStonewaySegBO.getStonewaySegBySql";
						try {
							uniteSegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(getSegMethod, new BoActionContext(),sqls);
						} catch (Exception e) {
							logger.error("获取系统段对象失败",e);
						}
						if (uniteSegs != null && uniteSegs.size() != 0) {
							GenericDO lastSeg = uniteSegs.get(0);
							startPoint = lastSeg.getAttrValue("ORIG_POINT_CUID");
							endPoint = lastSeg.getAttrValue("DEST_POINT_CUID");
						}
						if (endPoint != null && !endPoint.equals(null)&& !endPoint.equals("")) {
							stonewaySeg.setAttrValue("DEST_POINT_CUID", endPoint);
						}
						if (startPoint != null && !startPoint.equals(null)&& !startPoint.equals("")) {
							stonewaySeg.setAttrValue("ORIG_POINT_CUID", startPoint);
						}
						stonewaySeg.removeAttr("LAST_MODIFY_TIME");
						stonewaySeg.removeAttr("CREATE_TIME");
						segLists.add(stonewaySeg);
						breachcuid = segLists.get(0).getAttrValue("RELATED_BRANCH_CUID").toString();
					}else if(segClassName.equals("UP_LINE_SEG")){
						UpLineSeg upLineSeg = (UpLineSeg)gdo;
						String getSegMethod = "IUpLineSegBO.getSegmentsBySql";
						try {
							uniteSegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(getSegMethod, new BoActionContext(),sqls);
						} catch (Exception e) {
							logger.error("获取系统段对象失败",e);
						}
						if (uniteSegs != null && uniteSegs.size() != 0) {
							GenericDO lastSeg = uniteSegs.get(0);
							startPoint = lastSeg.getAttrValue("ORIG_POINT_CUID");
							endPoint = lastSeg.getAttrValue("DEST_POINT_CUID");
						}
						if (endPoint != null && !endPoint.equals(null)&& !endPoint.equals("")) {
							upLineSeg.setAttrValue("DEST_POINT_CUID", endPoint);
						}
						if (startPoint != null && !startPoint.equals(null)&& !startPoint.equals("")) {
							upLineSeg.setAttrValue("ORIG_POINT_CUID", startPoint);
						}
						upLineSeg.removeAttr("LAST_MODIFY_TIME");
						upLineSeg.removeAttr("CREATE_TIME");
						segLists.add(upLineSeg);
						breachcuid = "UP_LINE";
					}else if(segClassName.equals("HANG_WALL_SEG")){
						HangWallSeg hangWallSeg = (HangWallSeg)gdo;
						String getSegMethod = "IHangWallSegBO.getSegmentsBySql";
						try {
							uniteSegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(getSegMethod, new BoActionContext(),sqls);
						} catch (Exception e) {
							logger.error("获取系统段对象失败",e);
						}
						if (uniteSegs != null && uniteSegs.size() != 0) {
							GenericDO lastSeg = uniteSegs.get(0);
							startPoint = lastSeg.getAttrValue("ORIG_POINT_CUID");
							endPoint = lastSeg.getAttrValue("DEST_POINT_CUID");
						}
						if (endPoint != null && !endPoint.equals(null)&& !endPoint.equals("")) {
							hangWallSeg.setAttrValue("DEST_POINT_CUID", endPoint);
						}
						if (startPoint != null && !startPoint.equals(null)&& !startPoint.equals("")) {
							hangWallSeg.setAttrValue("ORIG_POINT_CUID", startPoint);
						}
						hangWallSeg.removeAttr("LAST_MODIFY_TIME");
						hangWallSeg.removeAttr("CREATE_TIME");
						segLists.add(hangWallSeg);
						breachcuid = "HANG_WALL";
					}
				}
				
			}
		}
		if(segLists != null && segLists.size() > 0){
			for(int i=0; i < segLists.size()-1; i++){
				//对比起止点，找到相同的点，将其放入到pointList中
				GenericDO uniteSegOne = (GenericDO) segLists.get(i);  // 第一条管道段
				GenericDO uniteSegTwo = (GenericDO) segLists.get(i + 1);  // 后一条管道段
				GenericDO origOne = (GenericDO) uniteSegOne.getAttrValue("ORIG_POINT_CUID"); //第一条记录的起点
				GenericDO destOne = (GenericDO) uniteSegOne.getAttrValue("DEST_POINT_CUID"); //第一条记录的终点
				GenericDO origTwo = (GenericDO) uniteSegTwo.getAttrValue("ORIG_POINT_CUID"); //最后一条记录的起点
				GenericDO destTwo = (GenericDO) uniteSegTwo.getAttrValue("DEST_POINT_CUID"); //最后一条记录的终点
				if(origOne.getCuid().equals(destTwo.getCuid())){
					pointList.add(origOne);
				}
				if(destOne.getCuid().equals(origTwo.getCuid())){
					pointList.add(destOne);
				}
			}
		}
		beginuniteseg(pointList,breachcuid,segLists);
	}
	/**
	 *  段合并
	 * @param pointList
	 * @param breachcuid
	 * @param seglist
	 * @return
	 */
	public DataObjectList beginuniteseg(DataObjectList pointList, String breachcuid, DataObjectList seglist) {
		seglist.sort("INDEX_IN_BRANCH", true);
        // 获取待合并的段数据
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
			GenericDO origOne = (GenericDO) dto.getAttrValue("ORIG_POINT_CUID"); //第一条记录的起点
			GenericDO destOne = (GenericDO) dto.getAttrValue("DEST_POINT_CUID"); //第一条记录的终点
            if (deletePointMap.containsKey(origOne.getCuid()) || deletePointMap.containsKey(destOne.getCuid()))
                mergeSegList.add(dto);
        }
        mergeSegList.sort("INDEX_IN_BRANCH", true);

        Map map = null;
        UnitePWSegAction action = new UnitePWSegAction();
        ArrayList<String> pointCuidList = new ArrayList<String>();
        DataObjectList modifySegList = new DataObjectList();
        DataObjectList modifywire2ductlineList = new DataObjectList();
        DataObjectList deleteSegList = new DataObjectList();
        DataObjectList savewire2ductlineList = new DataObjectList();
        for (int i = 0; i < pointList.size(); i++) {
            Map<String, GenericDO> segmap = new HashMap<String,GenericDO>();
            for (int j = 0; j < seglist.size(); j++) {
                String origcuid = null;
                String destcuid = null;
                if (seglist.get(j).getAttrValue("ORIG_POINT_CUID") instanceof GenericDO) {
                    GenericDO origgdo = (GenericDO) seglist.get(j).getAttrValue("ORIG_POINT_CUID");
                    segmap.put(origgdo.getCuid(), seglist.get(j));
                }
                if (seglist.get(j).getAttrValue("DEST_POINT_CUID") instanceof GenericDO) {
                    GenericDO destgdo = (GenericDO) seglist.get(j).getAttrValue("DEST_POINT_CUID");
                    segmap.put(destgdo.getCuid(), seglist.get(j));
                }
            }

            GenericDO gdo = null;
            if (pointList.get(i) instanceof GenericDO) {
                gdo = (GenericDO) pointList.get(i);
            }
            String cuid = gdo.getCuid();
            if (segmap.get(cuid) != null) {
                GenericDO node = (GenericDO) segmap.get(cuid);
                GenericDO origgdo = (GenericDO) node.getAttrValue("ORIG_POINT_CUID");
                GenericDO destgdo = (GenericDO) node.getAttrValue("DEST_POINT_CUID");
                long indexinbranch = (Long) node.getAttrValue("INDEX_IN_BRANCH");
                if (cuid.equals(origgdo.getCuid())) {
                    if (indexinbranch == 1) {
                    } else {
                        indexinbranch = indexinbranch - 1;
                    }
                }
                DataObjectList unitesegList = action.getuniteSeg(seglist, indexinbranch); //得到合并段并且排序
                if (unitesegList.size() == 1) {
                	JOptionPane.showMessageDialog(null,"没有要合并的段数据!!");
                    return null;
                }
                map = beginUniteHangWallorUplineSeg(unitesegList);
                if (map != null) {
                    DataObjectList deletewire2ductlineList1 = (DataObjectList) map.get("deletewire2ductline");
                    DataObjectList modifySegList1 = (DataObjectList) map.get("modifySegList");
                    DataObjectList modifywire2ductlineList1 = (DataObjectList) map.get("modifywire2ductline");
                    DataObjectList deleteSegList1 = (DataObjectList) map.get("deleteSegList");

                    GenericDO segdestpoint = (GenericDO) deleteSegList1.get(0).getAttrValue("DEST_POINT_CUID");
                    modifySegList1.get(0).setAttrValue("DEST_POINT_CUID", segdestpoint);
                    GenericDO segorigpoint = (GenericDO) modifySegList1.get(0).getAttrValue("ORIG_POINT_CUID");
                    String origname = (String) segorigpoint.getAttrValue("LABEL_CN");
                    String destname = (String) segdestpoint.getAttrValue("LABEL_CN");
                    if (origname != null && destname != null) {
                    }
                    deletewire2ductlineList1.addAll(modifywire2ductlineList);
                    modifySegList.addAll(modifySegList1);
                    modifywire2ductlineList.addAll(modifywire2ductlineList1);
                    deleteSegList.addAll(deleteSegList1);
                    //给合并段更改起始点,并且把名字复制好
                    deleteSegList.sort("INDEX_IN_BRANCH", true);
                    if (i == 0) {
                        savewire2ductlineList.addAll(modifywire2ductlineList1);
                    }
                } else {
                    return null;
                }
            }
            pointCuidList.add(cuid);
        }

        if (deleteSegList.get(0).getAttrValue("DEST_POINT_CUID") instanceof GenericDO) {
            GenericDO segdestpoint = (GenericDO) seglist.get(seglist.size()-1).getAttrValue("DEST_POINT_CUID");
            modifySegList.get(0).setAttrValue("DEST_POINT_CUID", segdestpoint);
            GenericDO segorigpoint = (GenericDO) modifySegList.get(0).getAttrValue("ORIG_POINT_CUID");
            String name2 = (String) segdestpoint.getAttrValue("LABEL_CN");
            String name1 = (String) segorigpoint.getAttrValue("LABEL_CN");
            modifySegList.get(0).setAttrValue("LABEL_CN", name1 + "--" + name2);
            modifySegList.get(0).setAttrValue("DESTOBJECT", segdestpoint);
            deleteSegList.removeObjectByCuid(modifySegList.get(0).getCuid());
        } else {
        	GenericDO segdestpoint = (GenericDO) deleteSegList.get(deleteSegList.size() - 1).getAttrValue("DEST_POINT_CUID");
            modifySegList.get(0).setAttrValue("DEST_POINT_CUID", segdestpoint);
        }

        GenericDO modifyploewayseg = modifySegList.get(0);
        GenericDO origgdo = (GenericDO)modifyploewayseg.getAttrValue("ORIG_POINT_CUID");
        GenericDO destgdo = (GenericDO)modifyploewayseg.getAttrValue("DEST_POINT_CUID");
        double length = 0;
        CoordPoint origCoor = getCoordinate(origgdo);
        CoordPoint destCoor = getCoordinate(destgdo);
        length = Math.abs( getDistanceByCoor(origCoor, destCoor));
        modifyploewayseg.setAttrValue("LENGTH", length);
        if (destgdo != null) {
            modifyploewayseg.setAttrValue("DEST_POINT_CUID", destgdo);
            if (modifywire2ductlineList != null && modifywire2ductlineList.size() > 0) {
                modifywire2ductlineList.get(0).setAttrValue("END_POINT_CUID", destgdo.getCuid());
            }
            modifyploewayseg.setAttrValue("LABEL_CN", origgdo.getAttrValue("LABEL_CN") + "--" + destgdo.getAttrValue("LABEL_CN"));
        }

        //处理光缆预留
        String origcuid = "";
        String origsitelabel = "";
        if (modifyploewayseg.getAttrValue("ORIG_POINT_CUID") instanceof String) {
            origcuid = (String) modifyploewayseg.getAttrValue("ORIG_POINT_CUID");
        } else {
            GenericDO pointgdo = (GenericDO) modifyploewayseg.getAttrValue("ORIG_POINT_CUID");
            origcuid = pointgdo.getCuid();
            origsitelabel = pointgdo.getAttrString("LABEL_CN");
        }
        pointCuidList.add(origcuid);
        String pointsql = "";
        for (int i = 0; i < pointCuidList.size(); i++) {
            pointsql = pointsql + "'" + pointCuidList.get(i) + "',";
        }
        pointsql = "(" + pointsql.substring(0, pointsql.length() - 1) + ")";
        String wireRemainsql = " RELATED_LOCATION_CUID in " + pointsql;
        wireRemainsql = wireRemainsql + " AND RELATED_WIRE_SEG_CUID in ";
        DataObjectList wireSegRemain = null;

        String wireSegcuidsql = "";
        if (modifywire2ductlineList != null && modifywire2ductlineList.size() > 0) {
            GenericDO gdo1 = modifywire2ductlineList.get(modifywire2ductlineList.size() - 1);
            String endpointcuid = (String) gdo1.getAttrValue("END_POINT_CUID");
            gdo1.setAttrValue("END_POINT_CUID", endpointcuid);
            for (int n = 0; n < savewire2ductlineList.size(); n++) {
                GenericDO gdo = savewire2ductlineList.get(n);
                String wireSegcuid = (String) gdo.getAttrValue("WIRE_SEG_CUID");
                wireSegcuidsql = wireSegcuidsql + "'" + wireSegcuid + "',";
            }
            wireRemainsql = wireRemainsql + " (" + wireSegcuidsql.substring(0, wireSegcuidsql.length() - 1) + ")";
            try {
                wireSegRemain = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IWireRemainBO.getWireRemainBySql",
                    new BoActionContext(), wireRemainsql);
            } catch (Exception ex) {
            }
        }
        DataObjectList wiresegremainlist = new DataObjectList();
        DataObjectList deletewiresegremainlist = new DataObjectList();
        double remainlength = 0;
        for (int n = 0; n < savewire2ductlineList.size(); n++) {
            GenericDO gdo = savewire2ductlineList.get(n);
            String wireSegcuid = (String) gdo.getAttrValue("WIRE_SEG_CUID");
            List<GenericDO> wiresegreamlist = wireSegRemain.getObjectByAttr("RELATED_WIRE_SEG_CUID", wireSegcuid);
            Map<String, GenericDO> remainmap = new HashMap<String, GenericDO>();
            for (int s = 0; s < wiresegreamlist.size(); s++) {
                GenericDO remaingdo = wiresegreamlist.get(s);
                remainlength = remainlength + remaingdo.getAttrDouble("REMAIN_LENGTH");
                remainmap.put(remaingdo.getAttrString("RELATED_LOCATION_CUID"), remaingdo);
                deletewiresegremainlist.add(remaingdo);
            }
        }
        if (wireSegRemain != null && wireSegRemain.size() > 0) {
            GenericDO remgdo = wireSegRemain.get(0);
            remgdo.setAttrValue("REMAIN_LENGTH", remainlength);
            remgdo.setAttrValue("RELATED_LOCATION_CUID", origcuid);
            remgdo.setAttrValue("LABEL_CN", origsitelabel);

            wiresegremainlist.add(remgdo);
        }
        if (deletewiresegremainlist != null && deletewiresegremainlist.size() > 0) {
            String sentence = "";
            double alllength = 0;
            for (int i = 0; i < deletewiresegremainlist.size(); i++) {
                GenericDO gdo = deletewiresegremainlist.get(i);
                String labelcn = (String) gdo.getAttrValue("LABEL_CN");
                double lengthwireremain = gdo.getAttrDouble("REMAIN_LENGTH");
                alllength = alllength + lengthwireremain;
                sentence = labelcn + "的长度" + ":" + lengthwireremain + ",";
            }
            sentence = sentence.substring(0, sentence.length() - 1) + "名称为 "+ origsitelabel + "的长度:" + remainlength;
            JOptionPane.showMessageDialog(null,sentence);
        }
        //光缆预留完成
        try {
        	GenericDO modifySeg = modifySegList.get(0);
            //先把预留信息全部删除在作存储
            BoCmdFactory.getInstance().execBoCmd("IWireRemainBO.deleteWireRemains",
                new BoActionContext(),deletewiresegremainlist);
            //添加光缆预留
            BoCmdFactory.getInstance().execBoCmd("IWireRemainBO.addWireRemains",
                new BoActionContext(),wiresegremainlist);
            //调用SplitSegHandler同一个的光缆敷设处理算法
            DataObjectList modifyWire2Ductlines = UnitePWSegAction.deleteWire2Ductlines(modifySeg, deleteSegList);
            //删除标石路由段信息
            if (breachcuid.contains("STONEWAY_BRANCH")) {
                BoCmdFactory.getInstance().execBoCmd("IStonewaySegBO.deleteStonewaySegs",
                    new BoActionContext(),deleteSegList);
            } else if (breachcuid.contains("UP_LINE")) {
                BoCmdFactory.getInstance().execBoCmd("IUpLineSegBO.deleteUpLineSegs",
                    new BoActionContext(),deleteSegList);
            } else if (breachcuid.contains("HANG_WALL")) {
                BoCmdFactory.getInstance().execBoCmd("IHangWallSegBO.deleteHangWallSegs",
                    new BoActionContext(),deleteSegList);
            }
            deleteSegList.get(deleteSegList.size() - 1).convAllObjAttrToCuid();
            //修改标石路由段信息
            if (breachcuid.contains("STONEWAY_BRANCH")) {
                BoCmdFactory.getInstance().execBoCmd("IStonewaySegBO.modfiyStonewaySegindexByStartSeg",new BoActionContext(),
                    new Long(0 - modifySegList.size()), (StonewaySeg) deleteSegList.get(deleteSegList.size() - 1));
                
                BoCmdFactory.getInstance().execBoCmd("IStonewaySegBO.modifyStonewaySeg",
                    new BoActionContext(),modifySeg);
                
            } else if (breachcuid.contains("UP_LINE")) {
                BoCmdFactory.getInstance().execBoCmd(
                    "IUpLineSegBO.modfiyUplineSegindexByStartSeg",new BoActionContext(),
                    new Long(0 - modifySegList.size()), (UpLineSeg) deleteSegList.get(deleteSegList.size() - 1));
                
                BoCmdFactory.getInstance().execBoCmd("IUpLineSegBO.modifyUpLineSeg",
                    new BoActionContext(),modifySeg);
            } else if (breachcuid.contains("HANG_WALL")) {
                BoCmdFactory.getInstance().execBoCmd("IHangWallSegBO.modifyHangwallSegIndexByStartSeg",new BoActionContext(),
                    new Long(0 - modifySegList.size()), (HangWallSeg) deleteSegList.get(deleteSegList.size() - 1));

                BoCmdFactory.getInstance().execBoCmd("IHangWallSegBO.modifyHangWallSeg",
                    new BoActionContext(), modifySeg);
            }
            //调用SplitSegHandler同一个的光缆敷设处理算法
            UnitePWSegAction.addModifyWire2Ductline(modifySeg, modifyWire2Ductlines,  modifySegList.size());
            UnitePWSegAction.mergeDisplaySegs(mergeSegList);
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
                    seggdo.setAttrValue("DEST_POINT_CUID", gdo);
                }
            }
            seglist.sort("INDEX_IN_BRANCH", true);
            for (int i = 0; i < seglist.size(); i++) {
                seglist.get(i).setAttrValue("INDEX_IN_BRANCH", new Long(i + 1));
            }
        } catch (Exception ex1) {
            LogHome.getLog().error("调用方法不正确，合并失败。", ex1);
        }
//        UnitePWSegAction.refreshSystemsBySeg(seglist);
        return seglist;
    }
	
	/**
     * 得到挂墙,引上,标石路由的wiretoductline合并
     * @param unitesegList DataObjectList
     * @return Map
     */
    public Map beginUniteHangWallorUplineSeg(DataObjectList unitesegList) {
        DataObjectList deleteSegList = new DataObjectList();
        DataObjectList deletewire2ductlineList = new DataObjectList();
        DataObjectList modifySegList = new DataObjectList();
        DataObjectList modifywire2ductlineList = new DataObjectList();
        String seg1name = "";
        String seg2name = "";
        DataObjectList wireToDuctlinelist = new DataObjectList();
        UnitePWSegAction action = new UnitePWSegAction();
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
            String carrycablesql = " DUCTLINE_CUID ='" + seg1cuid + "' or DUCTLINE_CUID ='" + seg2cuid +"'";
            try {
                wireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    "IWireToDuctLineBO.getWireToDuctLineBySql", new BoActionContext(),carrycablesql);
            } catch (Exception ex) {
                LogHome.getLog().error("获取敷设信息失败.", ex);
            }
            if (action.getyesornoUniteCarrycable(carrycablelist1, carrycablelist2, wireToDuctlinelist, seg1name, seg2name)) {
                for (int i = 0; i < carrycablelist1.size(); i++) {
                    GenericDO gdo = carrycablelist1.get(i);
                    GenericDO gdo2 = carrycablelist2.get(i);
                    String destpointcuid = null;
                    if (gdo2.getAttrValue("DEST_POINT_CUID") instanceof GenericDO) {
                        destpointcuid = ((GenericDO) gdo2.getAttrValue("DEST_POINT_CUID")).getCuid();
                    } else {
                        destpointcuid = (String) gdo2.getAttrValue("DEST_POINT_CUID");
                    }
                    double gdolength = 0;
                    double gdo2length = 0;
                    if (gdo.getAttrValue("LENGTH") != null) {
                        gdolength = gdo.getAttrDouble("LENGTH");
                    }
                    if (gdo2.getAttrValue("LENGTH") != null) {
                        gdo2length = gdo2.getAttrDouble("LENGTH");
                    }
                    gdo.setAttrValue("LENGTH", gdolength + gdo2length);
                    gdo.setAttrValue("DEST_POINT_CUID", destpointcuid);
                    modifySegList.add(gdo);
                    deleteSegList.add(gdo2);
                    java.util.List<GenericDO> gdo1list = wireToDuctlinelist.getObjectByAttr("DUCTLINE_CUID", gdo.getCuid());
                    java.util.List<GenericDO> gdo2list = wireToDuctlinelist.getObjectByAttr("DUCTLINE_CUID", gdo2.getCuid());
                    if (gdo1list.size() > 0) {
                        for (int s = 0; s < gdo1list.size(); s++) {
                            GenericDO wiretoductline1 = gdo1list.get(s);
                            GenericDO wiretoductline2 = gdo2list.get(s);
                            String endpointcuid = (String) wiretoductline2.getAttrValue("END_POINT_CUID");
                            wiretoductline1.setAttrValue("END_POINT_CUID", endpointcuid);
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
    
    private CoordPoint getCoordinate(GenericDO point) {
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
    
    private double getDistanceByCoor(CoordPoint orig, CoordPoint dest) {
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
}
