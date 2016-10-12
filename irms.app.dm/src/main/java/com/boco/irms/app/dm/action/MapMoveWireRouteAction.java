package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twaver.TDataBox;

import com.boco.common.util.debug.LogHome;
import com.boco.core.utils.exception.UserException;
import com.boco.graphkit.ext.GenericNode;
import com.boco.irms.app.utils.NmsUtils;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IWireToDuctLineBO;

/**
 * @author Administrator
 *	光缆改迁
 */
public class MapMoveWireRouteAction {

	private IDuctManagerBO getDuctManagerBO() {
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

	@SuppressWarnings("all")
	public Map getOrigAndDestPointCuidBySegCuid(String cuid) throws Exception{
		Map map = new HashMap();
		try {
			GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
			if(gdo != null){
				String direction = String.valueOf(gdo.getAttrValue(DuctSeg.AttrName.direction)); 
				map.put("DIRECTION", direction);
				
				String origPointCuid = DMHelper.getRelatedCuid(gdo.getAttrValue(DuctSeg.AttrName.origPointCuid));
				GenericDO origDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), origPointCuid);
				String origLabelCn = origDbo.getAttrString(DuctSeg.AttrName.labelCn);
				
				map.put("ORIG_POINT_CUID", origPointCuid);
				map.put("ORIG_POINT_NAME", origLabelCn);
				String destPointCuid = DMHelper.getRelatedCuid(gdo.getAttrValue(DuctSeg.AttrName.destPointCuid));
				GenericDO destDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), destPointCuid);
				String destLabelCn = destDbo.getAttrString(DuctSeg.AttrName.labelCn);
				map.put("DEST_POINT_CUID", destPointCuid);
				map.put("DEST_POINT_NAME", destLabelCn);
				
			}
		} catch (UserException e) {
			throw new UserException("根据id得到起止点出错！");
		}
		return map;
	}
	
	/*@SuppressWarnings("all")
	public Map getSegAndSystemByBranchCuid(String Branchcuid) throws Exception {
		
		Map map = new HashMap();
		DataObjectList segsByBranchCuid = getDuctManagerBO().getSegsByBranchCuid(new BoActionContext(), Branchcuid);
		if (segsByBranchCuid != null && segsByBranchCuid.size() > 0) {
			GenericDO genericDO = segsByBranchCuid.get(0);
			String relatedSystemCuid = DMHelper.getRelatedCuid(genericDO.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
			GenericDO systemDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), relatedSystemCuid);
			String systemCuid = systemDbo.getCuid(), systemLabel = systemDbo.getAttrString(DuctSeg.AttrName.labelCn);

			map.put("SYSTEM_CUID", systemCuid);
			map.put("SYSTEM_LABEL_CN", systemLabel);
			List segList = new ArrayList();
			for (GenericDO segDbo : segsByBranchCuid) {
				Map segMap = new HashMap();
				String segCuid = segDbo.getCuid(), segLabel = segDbo.getAttrString(DuctSeg.AttrName.labelCn);
				segMap.put("SEG_CUID", segCuid);
				segMap.put("SEG_LABEL_CN", segCuid);
				segList.add(segMap);
			}
			if (segList.size() > 0) {
				map.put("SEGS", segList);
			}
		}
		return map;
	}*/

	/**
	 * 根据id查分支和系统
	 * 
	 * @param cuid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("all")
	public Map getRelatedSystemAndBranchByCuid(String branChcuid,String segCuid,boolean isSeg) throws Exception {
		Map map = new HashMap();
		try {
			String className = branChcuid.split("-")[0];
			boolean branchClassName = DMHelper.isBranchClassName(className);
			if (branchClassName) {
				map = getRelatedSystemAndBranchByBranchCuid(branChcuid,segCuid,isSeg);
			} else {
				map = getRelatedSystemAndBranchBySystemCuid(branChcuid,segCuid,isSeg);
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) {
				message = "查询值错误！";
			}
			e.printStackTrace();
			throw new UserException(message);
		}
		return map;
	}

	@SuppressWarnings("all")
	private Map getRelatedSystemAndBranchBySystemCuid(String systemCuid,String segCuid,boolean isSeg) throws Exception {
		Map map = new HashMap();
		String cid = null;
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid", new BoActionContext(),systemCuid);
			String cuid = gdo.getCuid(), labelCn = gdo.getAttrString(DuctSeg.AttrName.labelCn);
			map.put("SYSTEM_CUID", cuid);
			map.put("SYSTEM_LABEL_CN", labelCn);
			if(isSeg){
				cid = segCuid;
			}else{
				cid = systemCuid;
			}
			List segsList = getSegsMapByBranchCuid(cid,isSeg);
			map.put("segs", segsList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException("查询错误");
		}
		return map;
	}

	@SuppressWarnings("all")
	private Map getRelatedSystemAndBranchByBranchCuid(String branchCuid,String segCuid,boolean isSeg)throws Exception {
		Map map = new HashMap();
		try {
			String cid = null;
			// branchCuid可能是分支，也可能是系统
			String className = branchCuid.split("-")[0];
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid", new BoActionContext(),branchCuid);
			if (gdo != null) {
				String cuid = gdo.getCuid();
				map.put("BRANCH_CUID", cuid);
				String branchLabelCn = gdo.getAttrString(DuctSeg.AttrName.labelCn);
				map.put("BRANCH_LABEL_CN", branchLabelCn);
				Object attrValue = gdo.getAttrValue(DuctSeg.AttrName.relatedSystemCuid);
				if (attrValue != null) {
					GenericDO systemDbo = null;
					if (attrValue instanceof GenericDO) {
						systemDbo = (GenericDO) attrValue;
					} else {
						systemDbo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(),String.valueOf(attrValue));
					}
					String systemCuid = systemDbo.getCuid();
					String systemLabel = systemDbo.getAttrString(DuctSeg.AttrName.labelCn);
					map.put("SYSTEM_CUID", systemCuid);
					map.put("SYSTEM_LABEL_CN", systemLabel);
				}
				if(isSeg){
					cid = segCuid;
				}else{
					cid = branchCuid;
				}
				List segsList = getSegsMapByBranchCuid(cid,isSeg);
				map.put("segs", segsList);
			}
		} catch (Exception e) {
			throw new UserException("查询错误");
		}
		return map;
	}
	
	@SuppressWarnings("all")
	private List getSegsMapByBranchCuid(String branchCuid,boolean isSeg){
		List segList = new ArrayList();
		try {
			DataObjectList segsList = new DataObjectList();
			if(isSeg){
				GenericDO objByCuid = getDuctManagerBO().getObjByCuid(new BoActionContext(), branchCuid);
				segsList.add(objByCuid);
			}else{
				segsList = getDuctManagerBO().getSegsByBranchCuid(new BoActionContext(), branchCuid);
			}
			if(segsList != null && segsList.size()>0){
				for(GenericDO dbo : segsList){
					Map map = new HashMap();
					String cuid = dbo.getCuid();
					String labelCn = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.labelCn));
					map.put(DuctSeg.AttrName.cuid, cuid);
					map.put(DuctSeg.AttrName.labelCn, labelCn);
					
					String direction = String.valueOf(dbo.getAttrValue(DuctSeg.AttrName.direction)); 
					map.put("DIRECTION", direction);
					
					String origPointCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.origPointCuid));
					GenericDO origDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), origPointCuid);
					String origLabelCn = origDbo.getAttrString(DuctSeg.AttrName.labelCn);
					
					map.put("ORIG_POINT_CUID", origPointCuid);
					map.put("ORIG_POINT_NAME", origLabelCn);
					String destPointCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.destPointCuid));
					GenericDO destDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), destPointCuid);
					String destLabelCn = destDbo.getAttrString(DuctSeg.AttrName.labelCn);
					map.put("DEST_POINT_CUID", destPointCuid);
					map.put("DEST_POINT_NAME", destLabelCn);
					
					segList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return segList;
	}
	
	@SuppressWarnings("all")
	public Map doSycWireSeg(List resList) throws Exception {
		Map rtnMap = new HashMap();
		try {
			List isSkipNext = getIsSkipNext(resList);
			List segList = getWireSegByDataBox(isSkipNext);
			rtnMap.put("WIRE_SEG", segList);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return rtnMap;
	}

	public List getIsSkipNext(List resList){
		TDataBox wireListDataBox = new TDataBox();
		ArrayList list = MoveWireRouteHelper.getWireSegByDuctLineExt(resList);
		List systemCuids = new ArrayList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				DataObjectList okWires = (DataObjectList) map.get("OK_WIRE");
				List jointBoxCuids = (ArrayList) map.get("JOINT_BOX");
				if (okWires != null && okWires.size() > 0) {
					for (GenericDO wireSeg : okWires) {
						String systemCuid = (String) wireSeg.getAttrValue(DuctSeg.AttrName.relatedSystemCuid);
						if (!systemCuids.contains(systemCuid)) {
							systemCuids.add(systemCuid);
						}
						GenericNode node = new GenericNode(wireSeg);
						wireListDataBox.addElement(node);
					}
				}
				ArrayList needMergeWires = (ArrayList) map.get("NEED_MERGE_WIRE");
				if (needMergeWires != null && needMergeWires.size() > 0) {
					for (int j = 0; j < needMergeWires.size(); j++) {
						DataObjectList tmpWires = (DataObjectList) needMergeWires.get(j);
						if (tmpWires != null && tmpWires.size() > 0) {
							GenericDO dto1 = tmpWires.get(0);
							GenericDO dto2 = tmpWires.get(1);

							WireSeg wireseg = new WireSeg();
							wireseg.setAttrValue("IS_NEED_MERGE", true);
							TopoHelper.setChildren(wireseg, tmpWires);
							GenericNode node = new GenericNode(wireseg);
							node.setName(dto1.getAttrValue(Manhle.AttrName.labelCn) + "-" + dto2.getAttrValue(Manhle.AttrName.labelCn));
							// node.setIcon(DMSystemUtils.getIcon(wireseg));
							wireListDataBox.addElement(node);
						}
					}

					for (int j = 0; j < needMergeWires.size(); j++) {
						DataObjectList wireSegs = (DataObjectList) needMergeWires.get(j);
						for (GenericDO wireSeg : wireSegs) {
							String relatedSysCuid = DMHelper.getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
							if (!systemCuids.contains(relatedSysCuid)) {
								systemCuids.add(relatedSysCuid);
							}
						}
					}
				}
			}
		}
		if (wireListDataBox.getAllElements() == null || wireListDataBox.getAllElements().size() < 1) {
			throw new UserException("没有光缆段连续敷设在选定的管线段上！");
			// MessagePane.showConfirmMessage("没有光缆段连续敷设在选定的管线段上！");
		}
		List allElements = wireListDataBox.getAllElements();
		return allElements;
	}
	
	public List getWireSegByDataBox(List allElements){
		List segList = new ArrayList();
		if (allElements != null && allElements.size() > 0) {
			for (Object element : allElements) {
				if (element instanceof GenericNode) {
					GenericDO wireSegDbo = ((GenericNode) element).getNodeValue();
					// WIRE_SEG[INDEX_IN_BRANCH<Long>=1, DIRECTION<Long>=2,
					// WIRE_SEG_TYPE<Long>=1, ORIG_POINT_CUID<String>=FIBER_JOINT_BOX-8a9f82a24b0f8520014b0f85ffe8000b,
					// CUID<String>=WIRE_SEG-8a8a81134acdc7ea014b0f83c801018d,WIRE_TYPE<String>=,CREATOR<String>=, ORIG_POINT_DIRECTION<Long>=0,
					// LABEL_CN<String>=test2015012201号光接头盒--test2015012202号光接头盒,
					// RELATED_BRANCH_CUID<String>=WIRE_BRANCH-8a9f82a24b0f8520014b0f86000b000e,
					// DEST_POINT_CUID<String>=FIBER_JOINT_BOX-8a9f82a24b0f8520014b0f85ffe8000c,
					// RELATED_SYSTEM_CUID<String>=WIRE_SYSTEM-8a9f82a24b0f8520014b0f86000a000d]
					String cuid = wireSegDbo.getCuid();
					String labelCn = wireSegDbo.getAttrString(WireSeg.AttrName.labelCn);
					String relatedSystemCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
					Map segMap = new HashMap();
					segMap.put(WireSeg.AttrName.cuid, cuid);
					segMap.put(WireSeg.AttrName.labelCn, labelCn);
					segMap.put(WireSeg.AttrName.relatedSystemCuid, relatedSystemCuid);
					segList.add(segMap);
				}
			}
		}
		return segList;
	}
	@SuppressWarnings("all")
	public List saveMoveWireSeg(Map<String,List<Map>> map,boolean isImport,boolean isDelete) throws Exception{
		List wireSegCuidList = new ArrayList();
		try {
			if(map != null && map.size()>0){
				List<String> oldLayingOutCuids = null;
				List<String> oldSystemCuids = null;
				List<String> wireSegCuids = null;
				List<String> wireSystemCuids = null;
				List<String> newLayingOutObjs = null;
				List<String> jointBoxCuids = null;
				
				DataObjectList wireSegs = new DataObjectList();
				DataObjectList newLayingLines = new DataObjectList();
				
				Object object = map.get("oldLayingOutCuids");
				if(object != null){
					List<Map> oldLayingMap = (List<Map>) object;
					oldLayingOutCuids = getCuidMapToList(oldLayingMap);
				}
				Object object2 = map.get("oldSystemCuids");
				if(object2 != null){
					List<Map> oldSystemMap = (List<Map>) object2;
					oldSystemCuids = getCuidMapToList(oldSystemMap);
				}
				Object object3 = map.get("wireSegCuids");
				if(object3 != null){
					List<Map> wireSegCuidsMap = (List<Map>) object3;
//				wireSegCuids = (List<Map>) object3;
					wireSegCuids = getCuidMapToList(wireSegCuidsMap);
					wireSegs = NmsUtils.getObjectsByCuids(wireSegCuids.toArray(new String [wireSegCuids.size()]));
				}
				Object object4 = map.get("wireSystemCuids");
				if(object4 != null){
					List<Map> wireSystemMap = (List<Map>) object4;
					wireSystemCuids = getCuidMapToList(wireSystemMap);
				}
				Object object5 = map.get("newLayingOutObjs");
				if(object5 != null){
					List<Map> newLayingMap = (List<Map>) object5;
					newLayingOutObjs = getCuidMapToList(newLayingMap);
					DataObjectList layingLines = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), newLayingOutObjs.toArray(new String[newLayingOutObjs.size()]));
					if(layingLines != null && layingLines.size()>0){
						layingLines.sort(DuctSeg.AttrName.indexInBranch, true);
						for(GenericDO dbo : layingLines){
							GenericDO gdo = new GenericDO();
							String cuid = dbo.getCuid();
							String origPointCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.origPointCuid));
							gdo.setCuid(cuid);
							gdo.setAttrValue("DIS_POINT_CUID", origPointCuid);
							 
							String destPointCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.destPointCuid));
							
							gdo.setAttrValue("END_POINT_CUID", destPointCuid);
//							Object attrValue2 = dbo.getAttrValue(DuctSeg.AttrName.direction);
//							if(attrValue2 == null){
//								attrValue2 = 1L;
//							}
							gdo.setAttrValue(DuctSeg.AttrName.direction, 1L);
							String sysCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.relatedSystemCuid));
							gdo.setAttrValue("DUCTLINE_CUID", sysCuid);
							String braCuid = null;
				            if (cuid.startsWith(UpLineSeg.CLASS_NAME) || cuid.startsWith(HangWallSeg.CLASS_NAME)) {
				                braCuid = sysCuid;
				            } else {
				            	braCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(DuctSeg.AttrName.relatedBranchCuid));
				            }
				            
				            gdo.setAttrValue("LINE_BRANCH_CUID", braCuid);
				            gdo.setAttrValue("LINE_SEG_CUID", cuid);
				            if(!newLayingLines.getCuidList().contains(cuid)){
				            	newLayingLines.add(gdo);
				            }
						}
					}
				}
				Object object6 = map.get("jointBoxCuids");
				if(object6 != null){
					List<Map> jointBoxCuidsMap = (List<Map>) object6;
					jointBoxCuids = getCuidMapToList(jointBoxCuidsMap);
				}
				
				wireSegCuidList = doMoveWireRoute(oldLayingOutCuids, oldSystemCuids, wireSegs, wireSystemCuids, jointBoxCuids, newLayingLines, isDelete, isImport);
				
			}
		} catch (Exception e) {
			LogHome.getLog().info("光缆改迁出错！",e);
			throw new UserException(e.getMessage());
		}
		return wireSegCuidList;
	}
	
	@SuppressWarnings("all")
	private List<String> getCuidMapToList(List<Map> cuids){
		List<String> scuidList = new ArrayList();
		for(Map segMap : cuids){
			String cid = String.valueOf(segMap.get("CUID"));
			scuidList.add(cid);
		}
		return scuidList;
	}
	
	@SuppressWarnings("all")
    private List doMoveWireRoute(List<String> oldLayingOutCuids,List<String> oldSystemCuids,DataObjectList wireSegCuids,
    		List<String> wireSystemCuids,List<String> jointBoxCuids,DataObjectList newLayingOutObjs,boolean isDelete,boolean isImport) throws Exception {
    	
//        final List <Map<String,Workbook>>fileList =null;//newLayingOutManager.getFiles();
		List cuidList = new ArrayList();
        DataObjectList wireSegList = null;
        
//      	 boolean isDelete=radioButton.isSelected();
//      	 List oldSystemCuids=oldLayingOutManager.getSystemCuids();
      	if(!isImport){
      		wireSegList=MoveWireRouteHelper.doMoveWireRouteExt(wireSegCuids, wireSystemCuids,jointBoxCuids,oldLayingOutCuids,oldSystemCuids,newLayingOutObjs,isDelete,new ArrayList(),"","",new Boolean(true),new Boolean(false));	
      	}/*else{
      		List<Map<String,DataObjectList>> importList=new ArrayList<Map<String,DataObjectList>>();
      		DataObjectList allManhles=new DataObjectList();
      		DataObjectList allPoles=new DataObjectList();
      		DataObjectList allStones=new DataObjectList();
      		DataObjectList allInflexions=new DataObjectList();
      		
      		for(int i=0;i<fileList.size();i++){
          		Map<String,Workbook> map=fileList.get(i);
          		String key=(String) map.keySet().toArray()[0];
          		Workbook workbook=map.get(key);
          		Map<String,DataObjectList>importMap=new HashMap<String,DataObjectList>();
          		DataObjectList tempList=null;
          		if(key.startsWith(Manhle.CLASS_NAME)){
          			 tempList=ImportConverter.checkManhle(workbook);
          			 importMap.put(Manhle.CLASS_NAME, tempList);
          			 if(tempList!=null&&tempList.size()>1){
          				allManhles.addAll(tempList.subList(1, tempList.size()-1));
          			 }
          		}else if(key.startsWith(Pole.CLASS_NAME)){
          			 tempList=ImportConverter.checkPole(workbook);
          			 importMap.put(Pole.CLASS_NAME, tempList);
          			if(tempList!=null&&tempList.size()>1){
          				allPoles.addAll(tempList.subList(1, tempList.size()-1));
          			 }
          		}else if(key.startsWith(Stone.CLASS_NAME)){
          			 tempList=ImportConverter.checkStone(workbook);
          			 importMap.put(Stone.CLASS_NAME, tempList);
          			if(tempList!=null&&tempList.size()>1){
          				allStones.addAll(tempList.subList(1, tempList.size()-1));
          			 }
          		}else if(key.startsWith(Inflexion.CLASS_NAME)){
          			 tempList=ImportConverter.checkInflexion(workbook);
          			 importMap.put(Inflexion.CLASS_NAME, tempList);
          			 if(tempList!=null&&tempList.size()>1){
          				allInflexions.addAll(tempList.subList(1, tempList.size()-1));
          			 }
          		}
          		if(map.size()>0){
          			importList.add(importMap);
          		}
      		}
      		check(allManhles,"人手井");
      		check(allPoles,"电杆");
      		check(allStones,"标石");
      		check(allInflexions,"拐点");
      		String curMapCuid = MapModel.getInstance().getCurSystemMap().getCuid();
      		wireSegList=MoveWireRouteHelper.doMoveWireRouteExt(wireSegCuids,systemCuids,jointBoxCuids,oldLayingOutCuids,oldSystemCuids,new DataObjectList(),isDelete,importList,curMapCuid,districtCuid,new Boolean(true),new Boolean(true));
      	}*/
//        MessagePane.showConfirmMessage("改迁光缆成功！");
      	doRefreshWireDisplay(wireSegList);
      	//下面这段用于返回前台设置接头盒
      	if(wireSegList != null && wireSegList.size()>0){
      		Map segMap = new HashMap();
      		for(GenericDO gdo:wireSegList){
      			String sCuid = gdo.getCuid();
      			String labelCN = DMHelper.getRelatedCuid(gdo.getAttrValue(WireSeg.AttrName.labelCn));
      			String relatedSystemCuid = DMHelper.getRelatedCuid(gdo.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
      			String relatedBranchCuid = DMHelper.getRelatedCuid(gdo.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
      			segMap.put(WireSeg.AttrName.cuid, sCuid);
      			segMap.put(WireSeg.AttrName.labelCn, labelCN);
      			segMap.put(WireSeg.AttrName.relatedSystemCuid, relatedSystemCuid);
      			segMap.put(WireSeg.AttrName.relatedBranchCuid, relatedBranchCuid);
      			cuidList.add(segMap);
      		}
      	}
      	return cuidList;
    }

	public List setFiberJointBox(List<String> wireSegCuids){
		Map<String,DataObjectList> wireToPointsMap = null;
		List rtnList = new ArrayList();
		if(wireSegCuids!=null&&wireSegCuids.size()>0){
			String actionName = "IWireToDuctLineBO.getLayPointCuidsByWireSegCuids";
			try {
				wireToPointsMap=(Map) BoCmdFactory.getInstance().execBoCmd(actionName, new BoQueryContext(), wireSegCuids);
//				changeRoutePoints(node);
			} catch (Exception e) {
				LogHome.getLog().error(e.getMessage(),e);
				wireToPointsMap=new HashMap();
			}
		}
		if(wireToPointsMap != null && wireToPointsMap.size()>0){
			for(Map.Entry<String,DataObjectList> entry : wireToPointsMap.entrySet()){
				Map pointMap = new HashMap();
				String wiresegCuid = entry.getKey();
				DataObjectList pointList = entry.getValue();
				List points = setPointAttrValue(pointList);
				pointMap.put(WireSeg.AttrName.cuid, wiresegCuid);
				pointMap.put("points", points);
				rtnList.add(pointMap);
			}
		}
		
		return rtnList;
		/*
    	if(MessagePane.showYesNoMessage("是否需要在路由点上设置接头盒？")==JOptionPane.OK_OPTION){
        		SplitWireByRouteView splitView = (SplitWireByRouteView) ViewFactory.getInstance().createView("SplitWireByRouteView",
                        ShellFactoryName.MainModalDialogShellFactory);
        		try {
					splitView.openView(wireSegList);
					ArrayList <String>list=new ArrayList<String>();
					if(wireSegList!=null&&wireSegList.size()>0){
						for(GenericDO dto:wireSegList){
							String origPointCuid=DMHelper.getRelatedCuid(dto.getAttrValue(WireSeg.AttrName.origPointCuid));
							if(origPointCuid!=null&&!list.contains(origPointCuid)){
								list.add(origPointCuid);
							}
							String destPointCuid=DMHelper.getRelatedCuid(dto.getAttrValue(WireSeg.AttrName.destPointCuid));
							if(destPointCuid!=null&&!list.contains(destPointCuid)){
								list.add(destPointCuid);
							}
						}
					}
					DataObjectList rtnWireSegs=splitView.getWireSegs();

					if(rtnWireSegs!=null&&rtnWireSegs.size()>0){
	                    doRefreshWireDisplay(rtnWireSegs);	
					}else{
//						doRefreshWireDisplay(wireSegList);
					}
				} catch (Exception e) {
//					LogHome.getLog().error(e.getMessage(),e);
				}	
//    		}
    	}else{
//    		doRefreshWireDisplay(wireSegList);
    	}
	*/}
	
	private List setPointAttrValue(DataObjectList pointList){
		List list = new ArrayList();
		if(pointList != null && pointList.size()>0){
			for(GenericDO gdo : pointList){
				Map map = new HashMap();
				String cuid = gdo.getCuid();
				String labelCn = gdo.getAttrString(WireSeg.AttrName.labelCn);
				map.put(WireSeg.AttrName.cuid, cuid);
				map.put(WireSeg.AttrName.labelCn, labelCn);
				list.add(map);
			}
		}
		return list;
	}
    private void doRefreshWireDisplay(DataObjectList wireSegs) throws Exception {
        try {
			for (GenericDO dto : wireSegs) {
			    if (!(dto instanceof WireSeg)) continue;
			    WireSeg wireSeg = (WireSeg)dto;
			    WebDMUtils.syschWireSegDisplayRoute(wireSeg, false);
			}
		} catch (Exception e) {
			throw new UserException(e.getMessage());
		}
    }
    
    private DataObjectList setWireSegFiberJoints(DataObjectList wireSegList){
    	DataObjectList rtnWireSegList = new DataObjectList();
    	if(wireSegList!=null){
			 rtnWireSegList.addAll(rtnWireSegList); 
		}
    	
    	return rtnWireSegList;
    }
}
