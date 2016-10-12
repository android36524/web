package com.boco.gis.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import twaver.PersistenceManager;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.graphkit.ext.EnumDelegate;
import com.boco.graphkit.ext.component.TWDataBox;
import com.boco.graphkit.ext.gis.GraphkitUtils;
import com.boco.irms.app.utils.CustomEnum;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.CarryingCable;
import com.boco.transnms.common.dto.DisplaySeg;
import com.boco.transnms.common.dto.DuctChildHole;
import com.boco.transnms.common.dto.DuctHole;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.PhysicalJoin;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.SeggroupToRes;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireBranch;
import com.boco.transnms.common.dto.WireDisplaySeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboBlob;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.dm.DMCacheObjectName.DMElementTypeEnum;
import com.boco.transnms.server.bo.helper.dm.CarryingCableBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctChildHoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctHoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.HangWallSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.PolewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewayDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.StonewaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.UpLineSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireBranchBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireDisplaySegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;

public class SplitMapDuctSegAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public DecimalFormat df2 = new DecimalFormat("0.00");
	private String relatedSegGroupCuid = "";
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	private BoActionContext actionContext = null;
	private void setActionContext(ServiceActionContext ac) {
		if(ac != null){
			DmDesignerTools.setActionContext(ac);
			actionContext = DmDesignerTools.getActionContext();
		}else{
			actionContext = new BoActionContext();
		}
	}
	private String sceneValue = "";
	
	/**
	 * 拆分资源段 --- 陕西方法
	 * @param request
	 * @param splitSegCuid
	 * @param pointList
	 * @param relatedSegGroupCuid
	 * @param scene
	 * @return
	 * @throws Exception
	 */
	public String splitSegRes(HttpServletRequest request,String splitSegCuid, List<Map> pointList,String relatedSegGroupCuid,String scene) throws Exception {
		if(scene != null && !scene.equals("")){
			sceneValue = scene;
		}
		return splitSeg(request, splitSegCuid, pointList, relatedSegGroupCuid);
	}
	/**
	 * 拆分段
	 * @param splitSegCuid
	 * @param pointList
	 * @param relatedSegGroupCuid
	 * @throws Exception
	 */
	@SuppressWarnings("all")
	public String splitSeg(HttpServletRequest request,String splitSegCuid, List<Map> pointList,String relatedSegGroupCuid) throws Exception {
		String result = "";
		try {
			ServiceActionContext ac = new ServiceActionContext(request);
			setActionContext(ac);
			
			this.relatedSegGroupCuid = relatedSegGroupCuid;
			GenericDO splitSegDbo = getDuctManagerBO().getObjByCuid(actionContext, splitSegCuid);
			DataObjectList splitors = new DataObjectList();
			if(pointList.size()>0){
				for(Map map : pointList){
//				Collection values = map.values();
					String cuid = String.valueOf(map.get("CUID"));
					String bmClassId = String.valueOf(map.get("bmClassId"));
					String labelCn = String.valueOf(map.get("LABEL_CN"));
					String longitude = String.valueOf(map.get("LONGITUDE"));
					String latitude = String.valueOf(map.get("LATITUDE"));
					String realtedDistrictCuid = String.valueOf(map.get("RELATED_DISTRICT_CUID"));
					Object objState = map.get("OBJECT_STATE");
					String className = cuid.split("-")[0];
					GenericDO dbo = DmDesignerTools.getPointByClassName(className);
					if(objState != null && objState.equals("1")){
						//新增加的点
						dbo.setAttrValue("OBJECT_STATE", 1);
						
						String relatedProjectCuid = String.valueOf(map.get("RELATED_PROJECT_CUID"));
						dbo.setAttrValue(Manhle.AttrName.relatedProjectCuid, relatedProjectCuid);
					}
					dbo.setCuid(cuid);
					if(bmClassId.equals(Accesspoint.CLASS_NAME)){
						dbo.setAttrValue(Accesspoint.AttrName.districtCuid, realtedDistrictCuid);
					}else if(bmClassId.equals(Site.CLASS_NAME)){
						dbo.setAttrValue(Site.AttrName.relatedSpaceCuid, realtedDistrictCuid);
					}else{
						dbo.setAttrValue(Manhle.AttrName.relatedDistrictCuid, realtedDistrictCuid);
					}
					dbo.setAttrValue(Manhle.AttrName.labelCn, labelCn);
					dbo.setAttrValue(Manhle.AttrName.longitude, Double.parseDouble(longitude));
					dbo.setAttrValue(Manhle.AttrName.latitude, Double.parseDouble(latitude));
					dbo.setAttrValue(Manhle.AttrName.realLongitude, Double.parseDouble(longitude));
					dbo.setAttrValue(Manhle.AttrName.realLatitude, Double.parseDouble(latitude));
					splitors.add(dbo);
				}
			}
			if(splitSegDbo instanceof DuctSeg){
				splitDuctSeg((DuctSeg)splitSegDbo,splitors);
			}else if(splitSegDbo instanceof PolewaySeg){
				spiltPolewaySeg((PolewaySeg)splitSegDbo,splitors);
			}else{
				splitOtherSeg(splitSegDbo, splitors);
			}
			result = "承载段拆分成功！";
		} catch (Exception e) {
			result = "承载段拆分失败！";
			LogHome.getLog().info("承载拆分出错：",e);
			throw new UserException(e.getMessage());
		}
		return result;
	}
	
    /**
     * 拆分管道段
     * @param splitedSeg
     * @param splitors
     * @throws Exception
     */
	@SuppressWarnings("all")
    public void splitDuctSeg(DuctSeg splitedSeg, DataObjectList splitors) throws Exception {
        DataObjectList realSplitors = getRealSplitors(splitors);
        Map<String, Map<String,String>> w2dInfos=new HashMap<String, Map<String,String>>();
        //根据老段查 管孔,子孔后,再复制到新段的管孔,子孔上.
        DataObjectList newDuctSegList = new DataObjectList(); //放新管道段,批量入库
        DataObjectList oldHoleList = new DataObjectList();
        DataObjectList oldChildeHoleList = new DataObjectList();
        DataObjectList newDuctHoleList = new DataObjectList(); //放新管孔孔,批量入库
        DataObjectList newChildHoleList = new DataObjectList(); //放新子孔,批量入库
//        DataObjectList oldW2DList = new DataObjectList(); //放旧wiretoductline的,批量入库
//        DataObjectList newW2DList = new DataObjectList(); //放新wiretoductline的,批量入库

        splitedSeg.setLabelCn(realSplitors.get(0).getAttrValue(Site.AttrName.labelCn) + "--" + realSplitors.get(1).getAttrValue(Site.AttrName.labelCn));
        splitedSeg.setDestPointCuid((realSplitors.get(1)).getCuid());
        splitedSeg.setLength(getSegLength(realSplitors.get(0), realSplitors.get(1), splitors));
        Map<String, String> spW2dInfo = new HashMap<String, String>();
        w2dInfos.put(splitedSeg.getCuid(), spW2dInfo);
        spW2dInfo.put(splitedSeg.getCuid(), splitedSeg.getCuid());

        GenericDO curDuctSegdto = new GenericDO(); //查库中的管孔等要求GenericDO类型参数
        curDuctSegdto.setCuid(splitedSeg.getCuid());
        curDuctSegdto.setObjectNum(splitedSeg.getObjectNum());
        oldHoleList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.getHolesAndChildHoles, actionContext, curDuctSegdto);
        for (GenericDO dto : oldHoleList) {
            if (!(dto instanceof DuctHole)) continue;
            DuctHole dh = (DuctHole)dto;
            dh.setOrigPointCuid(DMHelper.getRelatedCuid(splitedSeg.getAttrValue(DuctSeg.AttrName.origPointCuid)));
            dh.setDestPointCuid(DMHelper.getRelatedCuid(splitedSeg.getAttrValue(DuctSeg.AttrName.destPointCuid)));
            dh.setLength(splitedSeg.getLength());
            spW2dInfo.put(dto.getCuid(), dto.getCuid());
            DataObjectList chList = (DataObjectList) dh.getAttrValue(DMCacheObjectName.DuctHoleChildren);
            for (GenericDO dbo : chList) {
                if (!(dbo instanceof DuctChildHole)) continue;
                DuctChildHole ch = (DuctChildHole)dbo;
                ch.setOrigPointCuid(DMHelper.getRelatedCuid(splitedSeg.getAttrValue(DuctSeg.AttrName.origPointCuid)));
                ch.setDestPointCuid(DMHelper.getRelatedCuid(splitedSeg.getAttrValue(DuctSeg.AttrName.destPointCuid)));
                ch.setLength(splitedSeg.getLength());
                spW2dInfo.put(dbo.getCuid(), dbo.getCuid());
            }
            oldChildeHoleList.addAll(chList);
        }
        TWDataBox holeBox = new TWDataBox();
        PicBlobUtils.getAllPicBlobBytes(splitedSeg.getCuid(), null, 6022L, holeBox, null);
        Map<String, String> oldHoleInfo = new HashMap<String, String>();
        String oldxml = DuctUtils.getXMLFromDataboxAndDuctSeg(holeBox, splitedSeg, oldHoleInfo, false);

        String w2dSQL = WireToDuctline.AttrName.lineSystemCuid + "='" + splitedSeg.getRelatedSystemCuid()+ "' and "+ WireToDuctline.AttrName.lineSegCuid + "='" + splitedSeg.getCuid() + "'";
//        oldW2DList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql, new BoActionContext(), w2dSQL);
        DataObjectList wire2dlbySegcuidList = new DataObjectList();
            wire2dlbySegcuidList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,actionContext,w2dSQL);
            //删除原有的wiretoductline
            BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.deleteWireToDuctlines, actionContext,wire2dlbySegcuidList);
           
        for (int i = 1; i < realSplitors.size() - 1; i++) { //  去掉头尾点,list只留拆分点
            GenericDO point = realSplitors.get(i);
            GenericDO pointNext = realSplitors.get(i + 1);
            Map<String, String> w2dInfo = new HashMap<String, String>();
            DuctSeg newSeg = (DuctSeg) splitedSeg.clone(); //新生成的段
            newSeg.setCuid();
            w2dInfos.put(newSeg.getCuid(), w2dInfo);
            w2dInfo.put(splitedSeg.getCuid(), newSeg.getCuid());
            newSeg.setLabelCn(point.getAttrValue(Site.AttrName.labelCn) + "--" + pointNext.getAttrValue(Site.AttrName.labelCn)); //"管道段"
            newSeg.setOrigPointCuid(point.getCuid());
            newSeg.setDestPointCuid(pointNext.getCuid());
            newSeg.setLength(getSegLength(point, pointNext, splitors));
            newSeg.setIndexInBranch(splitedSeg.getIndexInBranch() + i);
            // 为新增段新增管孔、子孔以及管道截面图
            Map holeInfo = DuctUtils.getDuctSectionMap(holeBox, newSeg, oldxml, oldHoleInfo, splitedSeg);
            DataObjectList ductHoleList = (DataObjectList) holeInfo.get("DuctHole");
            DataObjectList childHoleList = (DataObjectList) holeInfo.get("ChildHole");
            for (int n = 0; n < ductHoleList.size(); n++) { //循环管孔
                DuctHole ductHole = (DuctHole) oldHoleList.get(n); //管孔
                String newductHoleCuid = oldHoleInfo.get(ductHole.getCuid());
                DuctHole newDuctHole = (DuctHole) ductHoleList.getObjectByCuid(newductHoleCuid).get(0);
                w2dInfo.put(ductHole.getCuid(), newDuctHole.getCuid());
                newDuctHole.setLabelCn("管孔");
                newDuctHole.setUsageState(ductHole.getUsageState());
                newDuctHole.setLength(newSeg.getLength());
                DataObjectList ductchildholelist = (DataObjectList) ductHole.getAttrValue(DMCacheObjectName.DuctHoleChildren);
                for (int t = 0; t < ductchildholelist.size(); t++) {
                    DuctChildHole childhole = (DuctChildHole) ductchildholelist.get(t); //子孔
                    String newChildHoleCuid = oldHoleInfo.get(childhole.getCuid());
                    DuctChildHole newChildHole = (DuctChildHole) childHoleList.getObjectByCuid(newChildHoleCuid).get(0);
                    w2dInfo.put(childhole.getCuid(), newChildHole.getCuid());
                    newChildHole.setLength(newSeg.getLength());
                    newChildHole.setUsageState(childhole.getUsageState());
                    newChildHoleList.add(newChildHole);
                }
                newDuctHoleList.add(newDuctHole);
            }
            PersistenceManager.registerClassDelegate(DMElementTypeEnum.class, new EnumDelegate()); //解决红色阻断的枚举报错
            DboBlob blob = new DboBlob();
            String xml = (String) holeInfo.get("XML");
            blob.setBlobBytes(xml.getBytes());
            boolean isSaveSuccess = PicBlobUtils.createAllPics(newSeg.getCuid(), null, 6022L, blob);
            PersistenceManager.unregisterClassDelegate(DMElementTypeEnum.class); //解决红色阻断的枚举报错

            newDuctSegList.add(newSeg);
        }

        //将拆分时新增加的段放到这个编辑范围中，用于后面判断资源是否可以删除
        if(newDuctSegList.size()>0){
        	DmDesignerTools.setAddres(newDuctSegList.getCuidList());
        	newDuctSegList = getRessList(newDuctSegList);
        }
        
        long offset = (long)newDuctSegList.size();
        // 批量入库
        addNewDMPoint(splitors);
        BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.modifyDuctSeg, actionContext, splitedSeg); //修改了原来第一条的段
        if (!newDuctSegList.isEmpty()){
            newDuctSegList.sort(DuctSeg.AttrName.indexInBranch, true);
            BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.updateDuctindex, actionContext, offset, newDuctSegList.get(0));
        }
        BoCmdFactory.getInstance().execBoCmd(DuctSegBOHelper.ActionName.addDuctSegs, actionContext, newDuctSegList); //新增的管道段S 入库
        BoCmdFactory.getInstance().execBoCmd(DuctHoleBOHelper.ActionName.modifyDuctHoles, actionContext, oldHoleList); //管孔入库
        BoCmdFactory.getInstance().execBoCmd(DuctHoleBOHelper.ActionName.addDuctHoles, actionContext, newDuctHoleList); //管孔入库
        BoCmdFactory.getInstance().execBoCmd(DuctChildHoleBOHelper.ActionName.modifyDuctChildHoles, actionContext, oldChildeHoleList); //子孔入库
        BoCmdFactory.getInstance().execBoCmd(DuctChildHoleBOHelper.ActionName.addDuctChildHoles, actionContext, newChildHoleList); //子孔入库

        createSegGroup(newDuctSegList);
		
        newDuctSegList.add(0,splitedSeg);
        wire2ductlineSpiltSeg(wire2dlbySegcuidList, newDuctSegList, w2dInfos);
        // 处理管道段显示路由
//        splitDisplaySegs(splitedSeg, splitors);
    }
	
    private void splitDisplaySegs(PhysicalJoin splitedSeg, DataObjectList splitors) throws Exception{
        DataObjectList oldDisSegList = getOldDisplaySegsInBranch(splitedSeg);
        DataObjectList segList = getSegsInBranch(splitedSeg);
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
        for (int i = 0; i < segList.size(); i++) {
            if (!(segList.get(i) instanceof PhysicalJoin)) continue;
            PhysicalJoin physicalJoin = (PhysicalJoin)segList.get(i);
            if (i == 0) realPointList.add(physicalJoin.getOrigPointCuid());
            realPointList.add(physicalJoin.getDestPointCuid());
        }
        String preDisPointCuid = null;
        String offDisPointCuid = null;
        for (int i = realPointList.size() - 1; i > -1; i--) {
            if (!realPointList.get(i).equals(splitors.get(0).getCuid())) continue;
            if (oldDisPointMap.containsKey(realPointList.get(i))) {
                preDisPointCuid = oldDisPointMap.get(realPointList.get(i));
                break;
            }
        }
        for (int i = 0; i < realPointList.size(); i ++) {
            if (!realPointList.get(i).equals(splitors.get(splitors.size() - 1).getCuid())) continue;
            if (oldDisPointMap.containsKey(realPointList.get(i))) {
                offDisPointCuid = oldDisPointMap.get(realPointList.get(i));
                break;
            }
        }
        DataObjectList delDisSegList = new DataObjectList();
        DataObjectList modDisSegList = new DataObjectList();
        long preIndex = 0L;
        long offIndex = 0L;
        for (int i = 0; i < oldDisSegList.size(); i++) {
            if (!(oldDisSegList.get(i) instanceof DisplaySeg)) continue;
            DisplaySeg disSeg = (DisplaySeg)oldDisSegList.get(i);
            if (disSeg.getOrigPointCuid().equals(preDisPointCuid)) {
//                delDisSegList.add(disSeg);
                preIndex = disSeg.getIndexInBranch() - 1;
                for (; i < oldDisSegList.size(); i++) {
                    if (!(oldDisSegList.get(i) instanceof DisplaySeg)) continue;
                    disSeg = (DisplaySeg)oldDisSegList.get(i);
                    delDisSegList.add(disSeg);
                    if (disSeg.getDestPointCuid().equals(offDisPointCuid)) {
                        offIndex = disSeg.getIndexInBranch() + 1;
                        for (i++; i < oldDisSegList.size(); i++) {
                            if (!(oldDisSegList.get(i) instanceof DisplaySeg)) continue;
                            modDisSegList.add(oldDisSegList.get(i));
                        }
                    }
                }
            }
        }
        List<String> disPointCuidList = new ArrayList<String>();
        Map<String, String> disPointCuidMap = new HashMap<String, String>();
        disPointCuidList.add(preDisPointCuid);
        for (int i = 1; i < splitors.size() - 1; i++) {
            GenericDO splitor = splitors.get(i);
            if (isDMPoint(splitor) &&
                !disPointCuidMap.containsKey(splitor.getCuid())){
                disPointCuidList.add(splitor.getCuid());
                disPointCuidMap.put(splitor.getCuid(), splitor.getCuid());
            }
        }
        for (int i = 0; i < delDisSegList.size(); i++) {
            if (!(delDisSegList.get(i) instanceof DisplaySeg)) continue;
            DisplaySeg disSeg = (DisplaySeg)delDisSegList.get(i);
            if (disSeg.getOrigPointCuid().startsWith(PresetPoint.CLASS_NAME) &&
                !disPointCuidMap.containsKey(disSeg.getOrigPointCuid())) {
                disPointCuidList.add(disSeg.getOrigPointCuid());
                disPointCuidMap.put(disSeg.getOrigPointCuid(), disSeg.getOrigPointCuid());
            }
            if (disSeg.getDestPointCuid().startsWith(PresetPoint.CLASS_NAME) &&
                !disPointCuidMap.containsKey(disSeg.getDestPointCuid())){
                disPointCuidList.add(disSeg.getDestPointCuid());
                disPointCuidMap.put(disSeg.getDestPointCuid(), disSeg.getDestPointCuid());
            }
        }
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
        BoCmdFactory.getInstance().execBoCmd(getDelActionName(splitedSeg.getClassName()), actionContext, delDisSegList);
        BoCmdFactory.getInstance().execBoCmd(getAddActionName(splitedSeg.getClassName()), actionContext, newDisSegList);
        BoCmdFactory.getInstance().execBoCmd(getModActionName(splitedSeg.getClassName()), actionContext, modDisSegList);
    }
    
    private DataObjectList getOldDisplaySegsInBranch(PhysicalJoin splitedSeg){
        String relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_BRANCH_CUID"));
        if (splitedSeg instanceof UpLineSeg || splitedSeg instanceof HangWallSeg) {
            relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_SYSTEM_CUID"));
        }
        return getOldDisplaySegsInBranch(relatedBranchCuid);
    }

    private DataObjectList getOldDisplaySegsInBranch(String relatedCuid){
        try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    DuctManagerBOHelper.ActionName.getDispalySegsBySystemCuid,
                    actionContext, relatedCuid);
        } catch (Exception ex) {
            LogHome.getLog().error("得到敷设信息错误!", ex);
        }
        return null;
    }
    
    public DataObjectList getSegsInBranch(PhysicalJoin splitedSeg){
        String relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_BRANCH_CUID"));
        if (splitedSeg instanceof UpLineSeg || splitedSeg instanceof HangWallSeg) {
            relatedBranchCuid = DMHelper.getRelatedCuid(splitedSeg.getAttrValue("RELATED_SYSTEM_CUID"));
        }
        return getSegsInBranch(relatedBranchCuid);
    }

    public DataObjectList getSegsInBranch(String relatedCuid){
        try {
            return (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    DuctManagerBOHelper.ActionName.getSegsBySystemCuid,
                    actionContext, relatedCuid);
        } catch (Exception ex) {
            LogHome.getLog().error("获取管线段出错！", ex);
        }
        return null;
    }
    
    private String getDelActionName(String className) {
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
    
    private String getAddActionName(String className) {
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
    private String getModActionName(String className) {
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
    
    private boolean isDMPoint(GenericDO point){
        return point instanceof FiberJointBox ||
               point instanceof FiberCab ||
               point instanceof FiberDp ||
               point instanceof Manhle ||
               point instanceof Pole ||
               point instanceof Stone ||
               point instanceof Inflexion||
               point instanceof PresetPoint||
               point instanceof Accesspoint;
    }
    
    /**
     * 拆分的时候调用
     * @param wire2dlbySegcuidList 拆分前查询出来 并删除的敷设信息
     * @param segList 拆分后的段
     * @param w2dInfos 用于存放段下 对应的管孔子孔,以及吊线的对应关系
     */
    public void wire2ductlineSpiltSeg(DataObjectList wire2dlbySegcuidList, DataObjectList segList,Map<String, Map<String,String>> w2dInfos  ) {

        try {
        	
        	for (GenericDO gdo : wire2dlbySegcuidList) {
				BoCmdFactory.getInstance().execBoCmd( "IWireToDuctLineBO.modfiyWireToDuctlineIndexs",
						actionContext, (long) (segList.size()-1), gdo); // 修改后续的序号
				
			}
        	
			String wirecuid = null;
			//开始进行wiretoductline拆分工作
			DataObjectList newwire2dlineList = new DataObjectList();
			for (int i = 0; i < wire2dlbySegcuidList.size(); i++) {
			    
			    GenericDO w2dlgdo = wire2dlbySegcuidList.get(i);
			    WireToDuctline oldW2d=(WireToDuctline) w2dlgdo;
			    Long indexroute = oldW2d.getIndexInRoute();
			    for (int s = 0; s < segList.size(); s++) {
			    	GenericDO lineSeg = segList.get(s);
			    	
			        WireToDuctline wtd = (WireToDuctline) w2dlgdo.deepClone();
			        
			        wtd.setCuid();
			        wtd.setObjectNum(0);
			        
			        String disPointCuid =DMHelper.getRelatedCuid(lineSeg.getAttrValue(DuctSeg.AttrName.origPointCuid));
			        String endPointCuid =DMHelper.getRelatedCuid(lineSeg.getAttrValue(DuctSeg.AttrName.destPointCuid));
			        wtd.setDisPointCuid(disPointCuid);
			        wtd.setEndPointCuid(endPointCuid);
			        wtd.setLineSegCuid(lineSeg.getCuid());
			        Map<String,String> w2dInfo=null;
			        if(w2dInfos!=null){
			        	w2dInfo=w2dInfos.get(lineSeg.getCuid());
			        }
			       
			        if(w2dInfo!=null){
			        	if (wtd.getDuctlineCuid() != null)
			        		wtd.setDuctlineCuid(w2dInfo.get(wtd.getDuctlineCuid()));
			             if (wtd.getHoleCuid() != null)
			            	 wtd.setHoleCuid(w2dInfo.get(wtd.getHoleCuid()));
			             if (wtd.getChildHoleCuid() != null)
			            	 wtd.setChildHoleCuid(w2dInfo.get(wtd.getChildHoleCuid()));
			        }else{
			        	 wtd.setDuctlineCuid(lineSeg.getCuid());
			        }
			        
		             
			        if (wtd.getDirection() == 1) {
			        	wtd.setIndexInRoute(indexroute.longValue() + s);
			      } else if (wtd.getDirection() == 2) {
			    	  wtd.setIndexInRoute(indexroute.longValue() + (segList.size() - (s + 1)));
			      }
			      newwire2dlineList.add(wtd);

			    }
			}
			BoCmdFactory.getInstance().execBoCmd(
					WireToDuctLineBOHelper.ActionName.addWireToDuctlines, actionContext, newwire2dlineList);
			syncWireSegDisplayRouteByW2D(newwire2dlineList);
		} catch (Exception e) {
			e.printStackTrace();
		}
    
    	
    }
    public void syncWireSegDisplayRouteByW2D(DataObjectList w2dList) {
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
    
    public void sysWireSegDisplayRoute(String wireSegCuid,boolean isPrompt) {
        WireSeg wireSeg = new WireSeg();
        try {
            wireSeg = (WireSeg) BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegByCuid,
            		actionContext, wireSegCuid);
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage(), ex);
        }
        syschWireSegDisplayRoute(wireSeg, isPrompt);
    }
    
    public void syschWireSegDisplayRoute(WireSeg wireseg,boolean isPrompt) {
    	if(wireseg==null)
    		return;
        String wireBranchCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
        String wireSystemCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
        GenericDO branch = null;
        try {
            branch = (WireBranch) BoCmdFactory.getInstance().execBoCmd(WireBranchBOHelper.ActionName.getWireBranchByCuid,actionContext, wireBranchCuid);
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage());
        }

        if (branch == null) {
            if (isPrompt) {
            	//光缆段所在光缆分支为空,生成显示路由失败
//                result = NmsUtils.getStringBufferErrJson("光缆段所在光缆分支为空,生成显示路由失败!");
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
                WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg,
                actionContext, wireseg);
        } catch (Exception ex) {
            LogHome.getLog().info("读取光缆敷设信息对象出错" + ex.getMessage());
        }

        wiretoductlines.sort(WireToDuctline.AttrName.indexInRoute, true); //用 INDEX_IN_ROUTE =1,2,3  进行排序
        try {

            List<String> pointCuidList = DMHelper.getPointCuidListByWireToDuctlines(wiretoductlines);
            String origPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.origPointCuid));
            String destPointCuid = DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.destPointCuid));

            List<String> displayPoints = getDisplayPointByWireBranch(DMHelper.getRelatedCuid(wireseg.getAttrValue(WireSeg.AttrName.relatedBranchCuid)));
            if(displayPoints==null){
                displayPoints=new ArrayList();
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
                    WireDisplaySegBOHelper.ActionName.deleteAndAddWireDisplaySegs,
                    actionContext, wireseg, wiredisplayseglists);
            } catch (Exception ex) {
            	//删除显示路由,增加具体路由信息对象出错
                LogHome.getLog().info("删除显示路由时增加具体路由信息对象出错" + ex.getMessage());
            }

            branch.setAttrValue(DMCacheObjectName.SystemDisplaySegChildren, wiresegdisplay);
            
            try {
            	IWireSegBO bo = (IWireSegBO) BoHomeFactory.getInstance().getBO(IWireSegBO.class);
            	bo.modifyLayOrDeleteRelationWireSeg(actionContext, wireseg);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            /*if(isPrompt){
                MessagePane.showConfirmMessage(PropertyMessage.getMessage("il8nKey_com.boco.transnms.client.view.dm.system.SystemSpecSegView.java31") + "!");
            }*/
            WireSystem wireSystem = new WireSystem();
            DataObjectList refreshList = new DataObjectList();
            wireSystem.setCuid(wireSystemCuid);
            refreshList.add(wireSystem);
          //arcgis版本中不做处理，此处如果要处理老版本，需要加上判断
//            DMGisUtils.getDmMap().refreshSystems((DataObjectList) refreshList);
            try {
                if (deletePresetPoints.size() > 0) {
                    String[] tmpPointCuids = new String[deletePresetPoints.size()];
                    for (int i = 0; i < deletePresetPoints.size(); i++) {
                        tmpPointCuids[i] = deletePresetPoints.get(i);
                    }
                    DataObjectList list = getObjectsByCuids(tmpPointCuids);
                   deletePointObjects(list);
                }
            } catch (Exception ex) {
                LogHome.getLog().info("删除预置点出错", ex);
            }
        }
    }
    private void deletePointObjects(DataObjectList list) throws Exception {
        if (list != null && list.size() > 0) {
            try {
                //从服务器删除
                BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.deleteLocatePoints, actionContext, list);
                //从内存删除及地图表删除,以下是非arcgis版本用到，注释
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

            } catch (Exception ex) {
                LogHome.getLog().error("删除地图显示点出错!", ex);
            }
        }
    }
    private List getDisplayPointByWireBranch(String branchCuid) {
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
    
    private DataObjectList getObjectsByCuids(String[] cuids) {
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
    public void addNewDMPoint(DataObjectList splitors) throws Exception {
    	try {
    		DataObjectList newSplitors = getNewSplitors(splitors);
//            String curMapCuid = MapModel.getInstance().getCurSystemMap().getCuid();
//            DataObjectList mapToObjects = new DataObjectList();
//            for (GenericDO dto : newSplitors) {
//                MapToObject mapToObject = new MapToObject();
//                mapToObject.setMapCuid(curMapCuid);
//                mapToObject.setObjectCuid(dto.getCuid());
//                mapToObjects.add(mapToObject);
//            }

            IDuctManagerBO ductManagerBO = (IDuctManagerBO)BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
            if(newSplitors.size()>0){
                //将拆分时新增加的段放到这个编辑范围中，用于后面判断资源是否可以删除
                DmDesignerTools.setAddres(newSplitors.getCuidList());
//                for (int i = 0; i < newSplitors.size(); i++) {
//                    GenericDO dbo = newSplitors.get(i);
//                    dbo.removeAttr("OBJECT_STATE");
//                }
                newSplitors = getRessList(newSplitors);
            	ductManagerBO.createPoints(actionContext, newSplitors);
            	createSegGroup(newSplitors);
            }

//            BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.createDMObjects, new BoActionContext(), "", mapToObjects);
        	
        	
        } catch (Exception ex) {
            LogHome.getLog().error(ex);
            throw new Exception(ex.getMessage());
        }
    }
	
    private DataObjectList getNewSplitors(DataObjectList splitors) {
        DataObjectList newSplitors = new DataObjectList();
        DMHelper.putNewDboToList(splitors, newSplitors);
        return newSplitors;
    }
    
    public double getSegLength(GenericDO orig, GenericDO dest, DataObjectList splitors) {
        if (orig == null || dest == null || splitors.size() < 2) {
            return 0d;
        }
        double length = 0d;
        for (int i = 0; i < splitors.size(); i++) {
            if (orig.getCuid().equals(splitors.get(i).getCuid())) {
                double origLatitude = orig.getAttrDouble("LATITUDE", 0D);
                double origLongitude = orig.getAttrDouble("LONGITUDE", 0D);
                for (i += 1; i < splitors.size(); i++) {
                    GenericDO point = splitors.get(i);
                    double latitude =  point.getAttrDouble("LATITUDE", 0D);
                    double longitude = point.getAttrDouble("LONGITUDE", 0D);
                    length += GraphkitUtils.getDistance(
                            origLongitude, origLatitude, longitude, latitude);
                    origLatitude = latitude;
                    origLongitude = longitude;
                    if (dest.getCuid().equals(point.getCuid())) break;
                }
            }
        }
        return format2(length);
    }
    public double format2(double d) {
        return Double.parseDouble(df2.format(d));
    }
    
    private DataObjectList getRealSplitors(DataObjectList splitors) {
        DataObjectList realSplitors = new DataObjectList();
        for (GenericDO splitor : splitors) {
            if (!(splitor instanceof PresetPoint)) {
                realSplitors.add(splitor);
            }
        }
        return realSplitors;
    }
   
    /**
     * 拆分杆路段
     * @param seg
     * @param realSplitors
     * @return
     * @throws Exception
     */
    @SuppressWarnings("all")
    public DataObjectList spiltPolewaySeg(PolewaySeg seg, DataObjectList realSplitors) throws Exception {
    	DataObjectList splitors = getRealSplitors(realSplitors);
        addNewDMPoint(splitors);
        DataObjectList outlist = new DataObjectList();
        DataObjectList carrycablelist = null;
        DataObjectList newwire2dlineList = new DataObjectList();
        DataObjectList wire2dlbySegcuidList = new DataObjectList();
        try {
            //得到掉线信息
            carrycablelist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                CarryingCableBOHelper.ActionName.getCarryingCablesByPoleWaySeg,actionContext,seg.getCuid());

            //通过segcuid得到wiretoductline信息
            String segsql =WireToDuctline.AttrName.lineSystemCuid+"='"+((PolewaySeg)seg).getRelatedSystemCuid()+"' and "+WireToDuctline.AttrName.lineSegCuid+"='"+((PolewaySeg)seg).getCuid()+"'";

            wire2dlbySegcuidList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,actionContext,segsql);
//            wireToDuctlinelist.addAll(wire2dlbySegcuidList);
            BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.deleteWireToDuctlines,actionContext,wire2dlbySegcuidList);

            //这里删除了原有的段,但克隆出来了一个完全相同的段
            BoCmdFactory.getInstance().execBoCmd(
                PolewaySegBOHelper.ActionName.deletePolewaySeg,actionContext,(PolewaySeg) seg);

        } catch (Exception ex) {
            LogHome.getLog().error("得到吊线段错误", ex);
        }

        //循环得到拆分点,得到拆分点id
        Map<String, Map<String,String>> w2dInfos=new HashMap<String, Map<String,String>>();
          
        DataObjectList genericDOlist = new DataObjectList();

        DataObjectList cableDOlist = new DataObjectList();
        for (int i = 0; i < splitors.size() - 1; i++) {
            GenericDO gdo1 = splitors.get(i);
            GenericDO gdo2 = splitors.get(i+1);
            //得到第orig点
            String cuid1 = gdo1.getCuid(),
            name1 = gdo1.getAttrString(GenericDO.AttrName.labelCn);

            String cuid2 = gdo2.getCuid(),
            name2 = gdo2.getAttrString(GenericDO.AttrName.labelCn);
            
            PolewaySeg copyseg = new PolewaySeg();
            seg.copyTo(copyseg);
            copyseg.setAttrValue(PolewaySeg.AttrName.destPointCuid, gdo2);
            copyseg.setAttrValue(PolewaySeg.AttrName.origPointCuid, gdo1);
            long indexinb = (Long) copyseg.getAttrValue(PolewaySeg.AttrName.indexInBranch);
            //开始拆分
            if (i == 0) {
                copyseg.setAttrValue(PolewaySeg.AttrName.indexInBranch, indexinb);
                copyseg.setCuid();
            } else {
                copyseg.setAttrValue(PolewaySeg.AttrName.indexInBranch, indexinb + i);
                copyseg.setCuid();
            }
            
            Map<String, String> w2dInfo = new HashMap<String, String>();
            w2dInfos.put(copyseg.getCuid(), w2dInfo);
            w2dInfo.put(seg.getCuid(), copyseg.getCuid());
            
            copyseg.setLength(getSegLength(gdo1, gdo2, splitors));
            String segcuid = copyseg.getCuid();
            copyseg.setLabelCn(name1 + "--" + name2);
            genericDOlist.add(copyseg);

            if (carrycablelist != null) {
                for (int c = 0; c < carrycablelist.size(); c++) {
                    CarryingCable cable = new CarryingCable();
                    GenericDO carrygdo = carrycablelist.get(c);
                    carrygdo.copyTo(cable);
                    cable.setAttrValue(CarryingCable.AttrName.origPointCuid, cuid1);
                    cable.setAttrValue(CarryingCable.AttrName.destPointCuid, cuid2);
                    cable.setAttrValue(CarryingCable.AttrName.relatedSegCuid, segcuid);
                    cable.setCuid();
                    w2dInfo.put(carrygdo.getCuid(), cable.getCuid());
                    cableDOlist.add(cable);
                }
            }
        }
        
      wire2ductlineSpiltSeg(wire2dlbySegcuidList, genericDOlist, w2dInfos);
      DataObjectList togenericDOlist =new DataObjectList();
      try {
    	  if(genericDOlist != null && genericDOlist.size() > 0){
    		  genericDOlist = getRessList(genericDOlist);
    	  }
    	  BoCmdFactory.getInstance().execBoCmd(PolewaySegBOHelper.ActionName.modfiypolewaySegindexByStartSeg,
    			  actionContext, new Long(genericDOlist.size()-1),(PolewaySeg)seg);

          BoCmdFactory.getInstance().execBoCmd( CarryingCableBOHelper.ActionName.addCarryingCables,
    			  actionContext,cableDOlist);
          
          togenericDOlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(PolewaySegBOHelper.ActionName.addPolewaySegsNoCarrying,
                  actionContext,genericDOlist);

          if(genericDOlist.size()>0){
        	  //将拆分时新增加的段放到这个编辑范围中，用于后面判断资源是否可以删除
              DmDesignerTools.setAddres(genericDOlist.getCuidList());
          }
          createSegGroup(genericDOlist);
       } catch (Exception ex2) {
    	   LogHome.getLog().error(ex2, ex2);
       }
       outlist.addAll(togenericDOlist);
       
       syncWireSegDisplayRouteByW2D(newwire2dlineList);
       try {
//           splitDisplaySegs((PhysicalJoin) seg, splitors);
       } catch (Exception ex) {
           LogHome.getLog().error("修改杆路段编号错误!", ex);
       }
//        refreshSystemsBySeg(seg);
       return outlist;
    }
    
    private DataObjectList getRessList(DataObjectList list) {
    	try {
    		for(GenericDO dbo : list){
				if(sceneValue.equals("DesignScene")){
					dbo.setAttrValue(Manhle.AttrName.projectState, 1);
				}else if(sceneValue.equals("planScene")){
					dbo.setAttrValue(Manhle.AttrName.projectState, 8);
				}else if(sceneValue.equals("ConstructScene")){
					dbo.setAttrValue(Manhle.AttrName.projectState, 2);
			    }else if(sceneValue.equals("ViewpointScene")){
			    	dbo.setAttrValue(Manhle.AttrName.projectState, 1);
				}else if(sceneValue.equals("changeScene")){
			    	dbo.setAttrValue(FiberDp.AttrName.projectState, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
	}
	/**
     * 拆分直埋段、引上段、挂墙段
     * @param seg
     * @param realSplitors
     * @return
     * @throws Exception
     */
    @SuppressWarnings("all")
    public DataObjectList splitOtherSeg(GenericDO seg, DataObjectList realSplitors) throws Exception {
    	DataObjectList segList = null;
    	DataObjectList splitors = getRealSplitors(realSplitors);
        addNewDMPoint(splitors);

        if (segList == null) {
            String relatedBranchCuid = DMHelper.getRelatedCuid(seg.getAttrValue("RELATED_BRANCH_CUID"));
            if (seg instanceof UpLineSeg || seg instanceof HangWallSeg) {
                relatedBranchCuid = DMHelper.getRelatedCuid(seg.getAttrValue("RELATED_SYSTEM_CUID"));
            }
            DataObjectList segsInBranch = new DataObjectList();
            try {
                segsInBranch = getSegsBySystemCuid(relatedBranchCuid);
            } catch (Exception ex) {
                LogHome.getLog().error("加载分支下管线段错误!",ex);
            }
            segsInBranch.sort(PolewaySeg.AttrName.indexInBranch, true);
            segList = segsInBranch;
        }
        DataObjectList outlist = new DataObjectList();
        DataObjectList wireToDuctlinelist = null; //光缆敷设list
        DataObjectList newwire2dlineList = new DataObjectList(); //新生成的敷设list

        String segsql = "";
        segsql = WireToDuctline.AttrName.lineSegCuid + " ='" + seg.getCuid() + "'";
        DataObjectList wire2dlbySegcuidList = new DataObjectList();
        try {
            wire2dlbySegcuidList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,actionContext,segsql);
            //删除原有的wiretoductline
            BoCmdFactory.getInstance().execBoCmd(
                WireToDuctLineBOHelper.ActionName.deleteWireToDuctlines,actionContext,wire2dlbySegcuidList);
            if (seg.getCuid().contains(StonewaySeg.CLASS_NAME)) {
                //删除原来的标石路由段
                BoCmdFactory.getInstance().execBoCmd(
                    StonewaySegBOHelper.ActionName.deleteStonewaySeg,actionContext,(StonewaySeg) seg);
            } else if (seg.getCuid().contains(UpLineSeg.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                    UpLineSegBOHelper.ActionName.deleteUpLineSeg,actionContext,(UpLineSeg) seg);
            } else if (seg.getCuid().contains(HangWallSeg.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                    HangWallSegBOHelper.ActionName.deleteHangWallSeg,actionContext,(HangWallSeg) seg);

            }
        } catch (Exception ex) {
        }

        DataObjectList stonesegList = getnodelistbySeg(splitors, seg);

        DataObjectList segoutList = new DataObjectList();
        for (int i = 0; i < segList.size(); i++) {
            long indexinbranch = (Long) seg.getAttrValue(PolewaySeg.AttrName.indexInBranch);
            GenericDO gdo = segList.get(i);
            long indexinbranchi = (Long) gdo.getAttrValue(PolewaySeg.AttrName.indexInBranch);
            if (indexinbranch < indexinbranchi) {
                long indexss = indexinbranchi - indexinbranch;
                gdo.setAttrValue(PolewaySeg.AttrName.indexInBranch, indexinbranchi + stonesegList.size() - 1);
                segoutList.add(gdo);
            }
        }
        DataObjectList dyobjectlist = new DataObjectList();
        if(stonesegList != null && stonesegList.size() > 0){
        	stonesegList = getRessList(stonesegList);
        }
        try {
            seg.convAllObjAttrToCuid();
            if (seg.getCuid().contains(StonewaySeg.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                    StonewaySegBOHelper.ActionName.modfiyStonewaySegindexByStartSeg,
                    actionContext,new Long(stonesegList.size() - 1), seg);

                dyobjectlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    StonewaySegBOHelper.ActionName.addStonewaySegs,actionContext,stonesegList);

            } else if (seg.getCuid().contains(UpLineSeg.CLASS_NAME)) {
                BoCmdFactory.getInstance().execBoCmd(
                    UpLineSegBOHelper.ActionName.modfiyUplineSegindexByStartSeg,
                    actionContext,new Long(stonesegList.size() - 1), seg);
                dyobjectlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    UpLineSegBOHelper.ActionName.addUpLineSegs,actionContext,stonesegList);

            } else if (seg.getCuid().contains(HangWallSeg.CLASS_NAME)) {

                BoCmdFactory.getInstance().execBoCmd(
                    HangWallSegBOHelper.ActionName.modifyHangwallSegIndexByStartSeg,
                    actionContext,new Long(stonesegList.size() - 1), seg);
                dyobjectlist = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(
                    HangWallSegBOHelper.ActionName.addHangWallSegs,actionContext,stonesegList);

            }

            if(stonesegList.size()>0){
           	 //将拆分时新增加的段放到这个编辑范围中，用于后面判断资源是否可以删除
           	 DmDesignerTools.setAddres(stonesegList.getCuidList());
           }
            createSegGroup(stonesegList);
        } catch (Exception ex1) {
            LogHome.getLog().error("新增路由段错误", ex1);
        }
        //开始进行wiretoductline拆分工作
        wire2ductlineSpiltSeg(wire2dlbySegcuidList, stonesegList);
//        wire2ductlineSpiltseg(wire2dlbySegcuidList, StonesegList);
        DataObjectList returnlist = new DataObjectList();

        for (int i = 0; i < dyobjectlist.size(); i++) {
            String cuid = dyobjectlist.get(i).getCuid();
            long numid = dyobjectlist.get(i).getObjectNum();
            List<GenericDO> lists = stonesegList.getObjectByAttr("CUID", cuid);
            lists.get(0).setObjectNum(numid);
        }
        returnlist.addAll(segoutList);
        returnlist.addAll(stonesegList);
        returnlist.sort(PolewaySeg.AttrName.indexInBranch, true);
        try {
//            splitDisplaySegs((PhysicalJoin) seg, splitors);
        } catch (Exception ex) {
            LogHome.getLog().error("修改杆路分支显示路由出错!", ex);
        }
//        refreshSystemsBySeg(seg);
        return stonesegList;

    }
    
    private void wire2ductlineSpiltSeg(DataObjectList wire2dlbySegcuidList, DataObjectList StonesegList) {
    	wire2ductlineSpiltSeg(wire2dlbySegcuidList, StonesegList, null);
    }
    
    private DataObjectList getSegsBySystemCuid(String systemCuid) throws Exception {
    	DataObjectList segsBySystemCuid = getDuctManagerBO().getSegsBySystemCuid(actionContext, systemCuid);
        return segsBySystemCuid;
    }
    
    /**
     * 得到拆分的标石路由段.
     * @param nodelist LinkedList
     * @param seg GenericDO
     * @return DataObjectList
     */
    private DataObjectList getnodelistbySeg(DataObjectList splitors, GenericDO seg) {
        DataObjectList genericDOlist = new DataObjectList();
        for (int i = 0; i < splitors.size() - 1; i++) {
            GenericDO gdo1 = splitors.get(i);
            GenericDO gdo2 = splitors.get(i+1);
            //得到第orig点
            String name1 = gdo1.getAttrString(GenericDO.AttrName.labelCn);
            //得到dest点
            String name2 = gdo2.getAttrString(GenericDO.AttrName.labelCn);
            String segCuid = seg.getCuid();
            GenericDO copyseg = null;
            if(segCuid.startsWith(StonewaySeg.CLASS_NAME)){
            	copyseg = new StonewaySeg();
            }else if(segCuid.startsWith(UpLineSeg.CLASS_NAME)){
            	copyseg = new UpLineSeg();
            }else if(segCuid.startsWith(HangWallSeg.CLASS_NAME)){
            	copyseg = new HangWallSeg();
            }
            seg.copyTo(copyseg);
            copyseg.setAttrValue(PolewaySeg.AttrName.destPointCuid, gdo2);
            copyseg.setAttrValue(PolewaySeg.AttrName.origPointCuid, gdo1);
            long indexinb = (Long) copyseg.getAttrValue(PolewaySeg.AttrName.indexInBranch);
            //开始拆分
            if (i == 0) {
                copyseg.setAttrValue(PolewaySeg.AttrName.indexInBranch, indexinb);
                copyseg.setCuid();
            } else {
                copyseg.setAttrValue(PolewaySeg.AttrName.indexInBranch, indexinb + i);
                copyseg.setCuid();
            }
            copyseg.setAttrValue(PolewaySeg.AttrName.length, getSegLength(gdo1, gdo2, splitors));
            copyseg.setAttrValue(PolewaySeg.AttrName.labelCn,name1 + "--" + name2);
            copyseg.setAttrValue("ORIGOBJECTPOINT", gdo1);
            copyseg.setAttrValue("DESTOBJECTPOINT", gdo2);
            genericDOlist.add(copyseg);
        }
        genericDOlist.sort(PolewaySeg.AttrName.indexInBranch, true);
        return genericDOlist;
    }
    
    /**
     * 增加所属段落
     * @param objList
     * @throws Exception
     */
    private void createSegGroup(DataObjectList objList) throws Exception{
    	try {
    		if(StringUtils.isNotEmpty(relatedSegGroupCuid)){
    			DataObjectList segGroupToResList = DmDesignerTools.createSegGroupToReses(objList,relatedSegGroupCuid);
    			if(segGroupToResList != null && segGroupToResList.size()>0){
    				for(GenericDO gdo : segGroupToResList){
    					if("erratum".equals(sceneValue)){
    						gdo.setAttrValue(SeggroupToRes.AttrName.projectState, CustomEnum.DMProjectState._designDDM);
    					}
    				}
    				getDuctManagerBO().createDMDOs(actionContext, segGroupToResList);
    			}
    		}
		} catch (Exception e) {
			LogHome.getLog().info("创建段落错误", e);
			throw new UserException(e.getMessage());
		}
    }
}
