package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.debug.LogHome;
import com.boco.graphkit.ext.GenericNode;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.CarryingCable;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

public class UnitePWSegAction extends UniteDuctSegAction{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 合并杆路段   
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void polewayUniteSegs(List<Map> segsLists){
		ArrayList pointList = new ArrayList();//合并点
		DataObjectList segLists = new DataObjectList();
		if(segsLists != null && segsLists.size()>0){
			for(int i=0; i<segsLists.size(); i++){
				Map segMaps = segsLists.get(i);
				String segCuid = segMaps.get("CUID").toString();
				String segClassName = GenericDO.parseClassNameFromCuid(segCuid);
				GenericDO gdo = WebDMUtils.createInstanceByClassName(segClassName, segMaps);
				PolewaySeg polewaySeg = (PolewaySeg) gdo;
				
				String getSegMethod = "IPolewaySegBO.getPolewaySegBySql";
				DataObjectList polewaySegs = new DataObjectList();
		  		String sqls = " 1 = 1 AND CUID = '" + segCuid + "'";
		  		try {
		  			polewaySegs = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(getSegMethod, new BoActionContext(),sqls);
		  		} catch (Exception e) {
		  			logger.error("获取系统段对象失败",e);
		  		}
		  		Object startPoint = null;
		  		Object endPoint = null;
		  		
		  		if(polewaySegs != null && polewaySegs.size() != 0){
		  			GenericDO lastSeg =polewaySegs.get(0);
		  			startPoint = lastSeg.getAttrValue("ORIG_POINT_CUID");
		  			endPoint = lastSeg.getAttrValue("DEST_POINT_CUID");
		  		}
		  		if(endPoint !=null && !endPoint.equals(null) && !endPoint.equals("")){
		  			polewaySeg.setAttrValue("DEST_POINT_CUID", endPoint);
		  		}
		  		if(startPoint != null && !startPoint.equals(null) && !startPoint.equals("")){
		  			polewaySeg.setAttrValue("ORIG_POINT_CUID", startPoint);
		  		}
		  		polewaySeg.removeAttr("LAST_MODIFY_TIME");
		  		polewaySeg.removeAttr("CREATE_TIME");
				segLists.add(polewaySeg);
			}
		}
		if(segLists != null && segLists.size() > 0){
			for(int i=0; i < segLists.size()-1; i++){
				//对比起止点，找到相同的点，将其放入到pointList中
				PolewaySeg polewaySegOne = (PolewaySeg) segLists.get(i);  // 第一条管道段
				PolewaySeg polewaySegTwo = (PolewaySeg) segLists.get(i + 1);  // 后一条管道段
				GenericDO origOne = (GenericDO) polewaySegOne.getAttrValue("ORIG_POINT_CUID"); //第一条记录的起点
				GenericDO destOne = (GenericDO) polewaySegOne.getAttrValue("DEST_POINT_CUID"); //第一条记录的终点
				GenericDO origTwo = (GenericDO) polewaySegTwo.getAttrValue("ORIG_POINT_CUID"); //最后一条记录的起点
				GenericDO destTwo = (GenericDO) polewaySegTwo.getAttrValue("DEST_POINT_CUID"); //最后一条记录的终点
				if(origOne.getCuid().equals(destTwo.getCuid())){
					pointList.add(origOne);
				}
				if(destOne.getCuid().equals(origTwo.getCuid())){
					pointList.add(destOne);
				}
			}
		}
		unitePolewaySeg(pointList,segLists);
	}
	/**
	 * 开始合并杆路段
	 * @param pointList
	 * @param seglist
	 * @return
	 */
	public DataObjectList unitePolewaySeg(ArrayList pointList, DataObjectList seglist) {
		seglist.sort("INDEX_IN_BRANCH", true);
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
            String origPointCuid = DMHelper.getRelatedCuid(pj.getAttrValue("ORIG_POINT_CUID"));
            String destPointCuid = DMHelper.getRelatedCuid(pj.getAttrValue("DEST_POINT_CUID"));
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
        ArrayList<String> pointCuidList = new ArrayList<String>();
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
            Map<String, Object> segmap = new HashMap<String, Object>();
            for (int j = 0; j < seglist.size(); j++) {
                GenericDO origgdo = (GenericDO) seglist.get(j).getAttrValue("ORIG_POINT_CUID");
                GenericDO destgdo = (GenericDO) seglist.get(j).getAttrValue("DEST_POINT_CUID");
                if (origgdo == null || destgdo == null) {
                    String name = (String) seglist.get(j).getAttrValue("LABEL_CN");
                    JOptionPane.showMessageDialog(null,name+"这条记录中起止点信息不完全,无法进行合并!");
                    return null;
                }
                segmap.put(origgdo.getCuid(), seglist.get(j));
                segmap.put(destgdo.getCuid(), seglist.get(j));
            }
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
                } else if (cuid.equals(destgdo.getCuid())) {
                }
                DataObjectList unitesegList = getuniteSeg(seglist, indexinbranch); //得到合并段并且排序 
                Map<String, DataObjectList> unitemap = beginUniteSeg(wireToDuctlinelist, unitesegList);
                if (unitemap != null) { //最后的入库,都在这里处理的.
                    DataObjectList modifywire2ductlineList1 = unitemap.get("modifywire2ductline");
                    GenericDO segdestpoint = (GenericDO) unitesegList.get(1).getAttrValue("DEST_POINT_CUID");//??segdestpoint
                    unitesegList.get(0).setAttrValue("DEST_POINT_CUID", segdestpoint);
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
        GenericDO modifyploewayseg = modifyploewayseglist.get(0);//??
        GenericDO destgdo = null;
        double length = 0;
        if (modifyploewayseg.getAttrValue("LENGTH") != null) {
            length = (Double) modifyploewayseg.getAttrValue("LENGTH");
        }
        for (int i = 0; i < deleteploewayseglist.size(); i++) {
            if (deleteploewayseglist.get(i).getAttrValue("LENGTH") != null) {
                double l = (Double) deleteploewayseglist.get(i).getAttrValue("LENGTH");
                length = length + l;
            }
            if (i == deleteploewayseglist.size() - 1) {
                destgdo = (GenericDO) deleteploewayseglist.get(i).getAttrValue("DEST_POINT_CUID");
            }
        }
        
        modifyploewayseg.setAttrValue("LENGTH", length);
        
        DataObjectList newsavewire2ductlineList = new DataObjectList();
        if (destgdo != null) {
            modifyploewayseg.setAttrValue("DEST_POINT_CUID", destgdo);
            if(modifywire2ductlineList!=null && modifywire2ductlineList.size()>0){
                for(int i=0; i<modifywire2ductlineList.size(); i++){
                    GenericDO wtdlgdo = new GenericDO();
                    modifywire2ductlineList.get(i).copyTo(wtdlgdo);
                    wtdlgdo.setAttrValue("END_POINT_CUID", destgdo.getCuid());
                    wtdlgdo.setCuid(null);
                    wtdlgdo.setCuid();
                    wtdlgdo.setObjectNum(0);
                    wtdlgdo.setObjectId("" + 0);
                    newsavewire2ductlineList.add(wtdlgdo);
                }
            }
            GenericDO origgdo = (GenericDO) modifyploewayseg.getAttrValue("ORIG_POINT_CUID");
            modifyploewayseg.setAttrValue("LABEL_CN", origgdo.getAttrValue("LABEL_CN") + "--" + destgdo.getAttrValue("LABEL_CN"));
        }
        //杆路段合并,处理光缆预留点信息
        String origcuid = "";
        String origsitelabel = "";
        if (modifyploewayseg.getAttrValue("ORIG_POINT_CUID") instanceof String) {
            origcuid = (String) modifyploewayseg.getAttrValue("ORIG_POINT_CUID");
        } else {
            GenericDO pointgdo = (GenericDO) modifyploewayseg.getAttrValue("ORIG_POINT_CUID");
            origcuid = pointgdo.getCuid();
            origsitelabel =pointgdo.getAttrString("LABEL_CN");
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
                wireSegRemain = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                		"IWireRemainBO.getWireRemainBySql", new BoActionContext(), wireRemainsql);
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
            Map<String, GenericDO> remainmap = new HashMap<String,GenericDO>();
            for (int s = 0; s < wiresegreamlist.size(); s++) {
                GenericDO remaingdo = wiresegreamlist.get(s);
                remainlength = remainlength + remaingdo.getAttrDouble("REMAIN_LENGTH");
                remainmap.put(remaingdo.getAttrString("RELATED_LOCATION_CUID"), remaingdo);
                deletewiresegremainlist.add(remaingdo);
            }
        }

        if (wireSegRemain!=null && wireSegRemain.size() > 0) {
            GenericDO remgdo = wireSegRemain.get(0);
            remgdo.setAttrValue("REMAIN_LENGTH", remainlength);
            remgdo.setAttrValue("RELATED_LOCATION_CUID", origcuid);
            remgdo.setAttrValue("LABEL_CN", origsitelabel);

            wiresegremainlist.add(remgdo);
        }

        if (deletewiresegremainlist != null && deletewiresegremainlist.size() > 0) {
            JOptionPane.showMessageDialog(null,"名称为"+ origsitelabel + "的长度:" + remainlength);
        }
        //处理光缆预留信息结束
        DataObjectList modifyWireToDuctlinelist=null;
		try {
			// 删除所有相关的敷设信息,并返回修改段对应的敷设信息
		    modifyWireToDuctlinelist = deleteWire2Ductlines(modifyploewayseg, deleteploewayseglist);

			// 先把预留信息全部删除在作存储
			BoCmdFactory.getInstance().execBoCmd("IWireRemainBO.deleteWireRemains",
					new BoActionContext(), deletewiresegremainlist);

			// 添加光缆预留
			BoCmdFactory.getInstance().execBoCmd("IWireRemainBO.addWireRemains",
					new BoActionContext(), wiresegremainlist);
			// 删除杆路段信息
			BoCmdFactory.getInstance().execBoCmd("IPolewaySegBO.deletePolewaySegs",
					new BoActionContext(), deleteploewayseglist);
			// 修改杆路段信息
			BoCmdFactory.getInstance().execBoCmd("IPolewaySegBO.modifyPolewaySeg",
					new BoActionContext(), modifyploewayseg);
			// 修改吊线段信息
			BoCmdFactory.getInstance().execBoCmd("ICarryingCableBO.modifyCarryingCables",
					new BoActionContext(), modifycarrycableList);
			// 删除吊线段信息
			BoCmdFactory.getInstance().execBoCmd("ICarryingCableBO.deleteCarryingCables",
					new BoActionContext(), deletecarrycableList);
			
			addModifyWire2Ductline(modifyploewayseg, modifyWireToDuctlinelist,pointList.size());

			// 修改杆路段后入库
			BoCmdFactory .getInstance() .execBoCmd("IPolewaySegBO.modfiypolewaySegindexByStartSeg",new BoActionContext(),
				new Long(0 - deleteploewayseglist.size()),(PolewaySeg)deleteploewayseglist.get(deleteploewayseglist.size()-1));
		} catch (Exception ex1) {
			LogHome .getLog() .error("中文描述" ,ex1);
		}
		for (int i = 0; i < deleteploewayseglist.size(); i++) {
			String cuid = deleteploewayseglist.get(i).getCuid();
			seglist.removeObjectByAttr("CUID", cuid);
		}

		seglist.sort("INDEX_IN_BRANCH", true);
		for (int i = 0; i < seglist.size(); i++) {
			seglist.get(i).setAttrValue("INDEX_IN_BRANCH",new Long(i + 1));
		}
		syncWireSegDisplayRouteByW2D(modifyWireToDuctlinelist);
        try {
            mergeDisplaySegs(mergePolywaySegList);
        } catch (Exception ex) {
            LogHome.getLog().error( "描述中文!", ex);
        }
//        refreshSystemsBySeg(seglist);
        return seglist;
    }
	/**
     * 得到合并段并且排序所有段信息//顺便也把合并方法新到这个方法中了
     *
     * @param seglist DataObjectList
     * @param indexinbranch long
     * @return DataObjectList
     */
    public DataObjectList getuniteSeg(DataObjectList seglist, long indexinbranch) {
        DataObjectList unitesegList = new DataObjectList();
        if(indexinbranch==seglist.size()){
            java.util.List<GenericDO> gdo = seglist.getObjectByAttr("INDEX_IN_BRANCH", indexinbranch-1);
            java.util.List<GenericDO> gdo1 = seglist.getObjectByAttr("INDEX_IN_BRANCH", indexinbranch);
            unitesegList.addAll(gdo);
            unitesegList.addAll(gdo1);
        }else{
            java.util.List<GenericDO> gdo = seglist.getObjectByAttr("INDEX_IN_BRANCH", indexinbranch);
            java.util.List<GenericDO> gdo1 = seglist.getObjectByAttr("INDEX_IN_BRANCH", indexinbranch + 1);
            unitesegList.addAll(gdo);
            unitesegList.addAll(gdo1);
        }
        return unitesegList;
    }
	
    /**
     * 开始合并分支
     * @param seglist DataObjectList
     * @param wireToDuctlinelist DataObjectList
     * @param displayseglist DataObjectList
     * @param unitesegList DataObjectList
     * @return DataObjectList
     */
    @SuppressWarnings("rawtypes")
	public Map beginUniteSeg(DataObjectList wireToDuctlinelist, DataObjectList unitesegList) {
        DataObjectList carrycablelist1 = new DataObjectList();
        DataObjectList carrycablelist2 = new DataObjectList();
        DataObjectList deletewire2ductlineList = new DataObjectList();
        DataObjectList deletecarrycableList = new DataObjectList();

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
            
            //得到 掉线段信息  
            try {
                carrycablelist1 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    "ICarryingCableBO.getCarryingCablesByPoleWaySeg",new BoActionContext(),seg1cuid);
                carrycablelist2 = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    "ICarryingCableBO.getCarryingCablesByPoleWaySeg",new BoActionContext(),seg2cuid);
                DataObjectList allcarrycable = new DataObjectList();
                allcarrycable.addAll(carrycablelist1);
                allcarrycable.addAll(carrycablelist2);
                String carrycablesql = "";
                for (int i = 0; i < allcarrycable.size(); i++) {
                    if (carrycablesql.equals("")) {
                        carrycablesql = " DUCTLINE_CUID ='" + allcarrycable.get(i).getCuid() + "'";
                    } else {
                        carrycablesql = carrycablesql + " or DUCTLINE_CUID ='" + allcarrycable.get(i).getCuid() + "'";
                    }
                }
                
                String unitesegsql = "";
                for (int i = 0; i < unitesegList.size(); i++) {
                	if (unitesegsql.equals("")) {
                		unitesegsql = " DUCTLINE_CUID ='" + unitesegList.get(i).getCuid() + "'";
                	} else {
                		unitesegsql = unitesegsql + " or DUCTLINE_CUID ='" + unitesegList.get(i).getCuid() + "'";
                	}
                }
                
                //得到光缆敷设信息
                carrywireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    "IWireToDuctLineBO.getWireToDuctLineBySql",new BoActionContext(),carrycablesql);
                segwireToDuctlinelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    "IWireToDuctLineBO.getWireToDuctLineBySql",new BoActionContext(),unitesegsql);
                if(carrywireToDuctlinelist!=null){
                    wireToDuctlinelist.addAll(carrywireToDuctlinelist);
                }
                if(segwireToDuctlinelist!=null){
                    wireToDuctlinelist.addAll(segwireToDuctlinelist);
                }
            } catch (Exception ex) {
                LogHome.getLog().error( "中文描述；；；cuid ；；；中文描述", ex);
            }
        }

        if (carrycablelist1.size() != carrycablelist2.size()) {
            JOptionPane.showMessageDialog(null,seg1name+"---"+seg2name +"吊线个数不相同!");
            return null;
        } else {
            DataObjectList segorigList = new DataObjectList();
            DataObjectList segdestList = new DataObjectList();
            segorigList.add(unitesegList.get(0));
            if(unitesegList.size()>1){
            	segdestList.add(unitesegList.get(1));
            }
            if(getyesornoUniteCarrycable(carrycablelist1, carrycablelist2, carrywireToDuctlinelist, seg1name, seg2name)
                &&getyesornoUniteCarrycable(segorigList, segdestList, segwireToDuctlinelist, seg1name, seg2name)){
                if (getyesornoUniteCarrycable(carrycablelist1, carrycablelist2, carrywireToDuctlinelist, seg1name, seg2name)) { //判断是否可合并
                    getwire2dlByUnitPoleWaySeg(carrycablelist1, carrycablelist2, modifycarrycableList, deletecarrycableList, 
                    		carrywireToDuctlinelist,modifywire2ductlineList, deletewire2ductlineList);
                }
                if (getyesornoUniteCarrycable(segorigList, segdestList, segwireToDuctlinelist, seg1name, seg2name)) {
                    getwire2dlByUnitPoleWaySeg(segorigList, segdestList, null, null, segwireToDuctlinelist,
                    		modifywire2ductlineList, deletewire2ductlineList);
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
//        for (String wireSegCuid : wireSegCuidMap.keySet()) {
//            GDMUtils.sysWireSegDisplayRoute(wireSegCuid,false);/////????????
//        }
    }
    
    /**
     * 得到能否合并数据
     * @param carrycablelist1 DataObjectList
     * @param carrycablelist2 DataObjectList
     */
    public boolean getyesornoUniteCarrycable(DataObjectList carrycablelist1, DataObjectList carrycablelist2, 
    		DataObjectList wireToDuctlinelist, String seg1name,String seg2name) {
        boolean boo = true;
        for (int i = 0; i < carrycablelist1.size(); i++) {
            GenericDO gdo1 = carrycablelist1.get(i);
            GenericDO gdo2 = carrycablelist2.get(i);
            java.util.List<GenericDO> gdo1list = wireToDuctlinelist.getObjectByAttr("DUCTLINE_CUID", gdo1.getCuid());
            java.util.List<GenericDO> gdo2list = wireToDuctlinelist.getObjectByAttr("DUCTLINE_CUID", gdo2.getCuid());
            if (gdo1list.size() != gdo2list.size()) {
                if(gdo1 instanceof CarryingCable){
                	JOptionPane.showMessageDialog(null,seg1name + "---" + seg2name +"敷设信息不同!");
                }else{
                	JOptionPane.showMessageDialog(null,seg1name + "---" + seg2name +"敷设信息不同!");
                }
            } else if (gdo1list.size() > 0 && gdo2list.size() > 0) {
                for (int s = 0; s < gdo1list.size(); s++) {
                    String wireseg1 = (String) gdo1list.get(s).getAttrValue("WIRE_SEG_CUID");
                    String wireseg2 = (String) gdo2list.get(s).getAttrValue("WIRE_SEG_CUID");
                    if (wireseg1.equals(wireseg2)) {
                        boo = true;
                    } else {
                    	JOptionPane.showMessageDialog(null,seg1name + "---" + seg2name +"的敷设信息不相同!");
                    }
                }
            }
        }
        return boo;
    }
    
	private void getwire2dlByUnitPoleWaySeg(DataObjectList carrycablelist1,
			DataObjectList carrycablelist2,DataObjectList modifycarrycableList,
			DataObjectList deletecarrycableList,DataObjectList wireToDuctlinelist,
			DataObjectList modifywire2ductlineList,DataObjectList deletewire2ductlineList) {
		for (int i = 0; i < carrycablelist1.size(); i++) {
			GenericDO gdo = carrycablelist1.get(i);
			GenericDO gdo2 = carrycablelist2.get(i);
			if (modifycarrycableList != null) {
				String destpointcuid = (String) gdo2.getAttrValue("DEST_POINT_CUID");
				double gdolength = gdo.getAttrDouble("LENGTH");
				double gdo2length = gdo2.getAttrDouble("LENGTH");
				gdo.setAttrValue("LENGTH", gdolength+ gdo2length);
				gdo.setAttrValue("DEST_POINT_CUID",destpointcuid);
				modifycarrycableList.add(gdo);
				deletecarrycableList.add(gdo2);
			}
			java.util.List<GenericDO> gdo1list = wireToDuctlinelist.getObjectByAttr("DUCTLINE_CUID",gdo.getCuid());
			java.util.List<GenericDO> gdo2list = wireToDuctlinelist.getObjectByAttr("DUCTLINE_CUID",gdo2.getCuid());
			if (gdo1list.size() > 0) {
				for (int s = 0; s < gdo1list.size(); s++) {
					GenericDO wiretoductline1 = gdo1list.get(s);
					GenericDO wiretoductline2 = gdo2list.get(s);
					String endpointcuid = (String) wiretoductline2.getAttrValue("END_POINT_CUID");
					wiretoductline1.setAttrValue("END_POINT_CUID", endpointcuid);
					modifywire2ductlineList.add(wiretoductline1);
					deletewire2ductlineList.add(wiretoductline2);
				}
			}
		}
	}
}
