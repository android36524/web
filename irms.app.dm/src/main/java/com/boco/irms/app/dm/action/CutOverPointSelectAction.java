package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boco.common.util.debug.LogHome;
import com.boco.core.utils.exception.UserException;
import com.boco.topo.Icon;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.DMHelperX;
import com.boco.transnms.common.dto.CutoverTask;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.TempCutoverFiber;
import com.boco.transnms.common.dto.TempCutoverWireSeg;
import com.boco.transnms.common.dto.TempWireToDuctline;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.ICutoverTaskBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointPointBO;
import com.boco.transnms.server.bo.ibo.dm.ITempCutoverFiberBO;
import com.boco.transnms.server.bo.ibo.dm.ITempCutoverWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.ITempWireToDuctlineBO;
import com.boco.transnms.server.bo.ibo.dm.IWireToDuctLineBO;
import com.boco.transnms.server.dao.base.DaoHelper;

@Controller()
@Service("CutOverPointSelectAction")
@RequestMapping("/CutOverPointSelectAction")
public class CutOverPointSelectAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//Map map1 = new HashMap();

	// private static WireSeg wireSeg = null;

	public List<Map<String, String>> getPointTree(HttpServletRequest requset,String cuid) {
		HttpSession session = requset.getSession(false);
		DataObjectList fiberEqpList = new DataObjectList();
		Map<String, GenericDO> pointFjbMap = new HashMap<String, GenericDO>();
		DataObjectList preFiberEqpList = new DataObjectList();
		WireSeg wireSeg = null;

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		GenericDO tempcutoverTask = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
		String deviceCuid = getRelatedCuid(tempcutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid));
		if (deviceCuid != null && deviceCuid.trim().length() > 0) {
			if (deviceCuid.contains(",")) {
				String[] cuids = deviceCuid.trim().split(",");
				fiberEqpList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), cuids);
			} else {
				GenericDO deviceDbo = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid.trim());
				if (deviceDbo != null) {
					fiberEqpList.add(deviceDbo);
				}
			}
			for (GenericDO dbo : fiberEqpList) {
				GenericDO newDbo = (GenericDO) dbo.deepClone();
				preFiberEqpList.add(newDbo);
				pointFjbMap.put(dbo.getAttrString(FiberJointBox.AttrName.relatedLocationCuid),dbo);
			}
		}

		String wireSegCuid = getRelatedCuid(tempcutoverTask.getAttrValue(CutoverTask.AttrName.relatedWireSegCuid));
		GenericDO wireSegDto = null;

		if (wireSegCuid != null && !("").equals(wireSegCuid)) {
			wireSegDto = getDuctManagerBO().getObjByCuid(new BoActionContext(),	wireSegCuid);
			if (wireSegDto != null && wireSegDto instanceof WireSeg) {
				wireSeg = (WireSeg) wireSegDto;
				result = buildPointTree((WireSeg) wireSeg);
			}
		}
		session.setAttribute("fiberEqpList", fiberEqpList);
		session.setAttribute("preFiberEqpList", preFiberEqpList);
		session.setAttribute("pointFjbMap", pointFjbMap);
		session.setAttribute("wireSeg", wireSeg);
		session.setAttribute("cutOverTesk", tempcutoverTask);
		if (wireSegDto == null) {
			throw new UserException("割接任务的割接光缆段不存在,无法进行方案设计");
		}
		return result;
	}
	
	//已有接头盒
	@RequestMapping(value="/addFiberJointPointBoxAction/{pcuid}/{bcuid}",method = RequestMethod.GET)
	public void addFiberJointPointBoxAction(HttpServletRequest request,HttpServletResponse response,@PathVariable String pcuid,@PathVariable String bcuid) throws IOException{
         response.setCharacterEncoding("UTF-8");
		 HttpSession session = request.getSession(false);
		 GenericDO point = getDuctManagerBO().getObjByCuid(new BoActionContext(), pcuid);
	     GenericDO cutoverTask = (GenericDO) session.getAttribute("cutOverTesk");
	     DataObjectList fiberEqpList = (DataObjectList) session.getAttribute("fiberEqpList");
		 Map<String, GenericDO> map = (Map<String, GenericDO>) session.getAttribute("pointFjbMap");
		 DataObjectList preFiberEqpList = (DataObjectList) session.getAttribute("preFiberEqpList");
		 DataObjectList fjbList=getFiberjointboxByCutoverTaskCuid(cutoverTask.getCuid());
		  if(fjbList.size()>=3){
             response.getWriter().print("光缆段最多允许割接3次,不能再添加接头盒!");
             return;
		  }
		 if(pcuid.split("-").length != 2){
			 response.getWriter().print("请选择一个路由点!");
			 return;
		 }
	     if(pcuid.split("-").length != 2){
	    	 if(!DMHelperX.isNotPointOfFiber(point.getClassName())){
	    		 response.getWriter().print("不是具体路由点不能添加接头盒,请选择具体路由点!");
	    		 return;
	    	 }
	    	 if(isPointHasFiberJointBox(point,cutoverTask.getCuid())){
	    		 response.getWriter().print("该具体路由点已经添加了接头盒,请选择其他路由点或者将该具体路由点的接头盒删除之后再添加");
	    		 return;
	    	 }
	    	 	         
	         DataObjectList preList=getFiberjointboxByCutoverTaskCuid(cutoverTask.getCuid());
	         preFiberEqpList.clear();
	 		 for(GenericDO preDbo:preList){
	 			GenericDO newPreDbo=(GenericDO)preDbo.deepClone();
	 			preFiberEqpList.add(newPreDbo);
	 		 }
	         
	         FiberJointBox selFiberJointBox = getFiberJointBoxBO().getFiberJointBoxByCuid(new BoActionContext(), bcuid);
	         if(selFiberJointBox==null){
	        	 return;
	         }
	         String relatedLocationCuid=DMHelper.getRelatedCuid(selFiberJointBox.getAttrValue(FiberJointBox.AttrName.relatedLocationCuid));
	         if(relatedLocationCuid!=null && relatedLocationCuid.length()>0 && !point.getCuid().equals(relatedLocationCuid)){
	        	 response.getWriter().print("已选中的接头盒("+selFiberJointBox.getLabelCn()+")已经归属到其他路由点上，\n不能再次归属到该路由点上");
	        	 return;
	         }
	         for(GenericDO dbo:fiberEqpList){
	        	 if(selFiberJointBox.getCuid().equals(dbo.getCuid())){
	        		 response.getWriter().print("已选中的接头盒("+selFiberJointBox.getLabelCn()+")已经归属到路由点上，\n请选择其他接头盒!");
	        		 return;
	        	 }
	         }
	         if(DMHelperX.isNotPointOfFiber(point.getClassName())){
	        	 selFiberJointBox.setAttrValue(FiberJointBox.AttrName.relatedLocationCuid, point.getCuid());
	         }
	         selFiberJointBox.setLongitude(point.getAttrDouble(Manhle.AttrName.longitude,0.0));
	         selFiberJointBox.setLatitude(point.getAttrDouble(Manhle.AttrName.latitude,0.0));
	         selFiberJointBox.setRealLongitude(point.getAttrDouble(Manhle.AttrName.realLongitude,0.0));
	         selFiberJointBox.setRealLatitude(point.getAttrDouble(Manhle.AttrName.realLatitude,0.0));
	         selFiberJointBox=getFiberJointBoxBO().modifyFiberJointBox(new BoActionContext(), selFiberJointBox);

	         map.put(point.getCuid(),selFiberJointBox);
	         fiberEqpList.add(selFiberJointBox);
	         //cutoverTask.setAttrValue(CutoverTask.AttrName.cutoverState, DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign);
	         String oldRelatedDeviceCuid=cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid).toString();
	         String newRelatedDeviceCuid=new String();
	         if(oldRelatedDeviceCuid==null || "".equals(oldRelatedDeviceCuid)){
	        	 newRelatedDeviceCuid=selFiberJointBox.getCuid();
	         }else{
	        	 newRelatedDeviceCuid=oldRelatedDeviceCuid+","+selFiberJointBox.getCuid();
	         }
	         cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, newRelatedDeviceCuid);
	         getCutoverTaskBO().modifyCutoverTask(new BoActionContext(), cutoverTask);
	         
	         response.getWriter().print("添加已有接头盒("+selFiberJointBox.getLabelCn()+")完成!");
//	         if(fiberEqpList!=null && fiberEqpList.size()>0){	        	 
//	        	 String str=label.getText();
//	        	 if(str.indexOf(":")==str.length()-2){
//	        		 label.setText(str+selFiberJointBox.getAttrValue(FiberJointBox.AttrName.labelCn));
//	        	 }else{
//	        		 label.setText(str+","+selFiberJointBox.getAttrValue(FiberJointBox.AttrName.labelCn));
//	        	 }
//	 		 }
	      }		
	
	}

	@RequestMapping(value = "/addNewFiberjointboxAction/{cuid}", method = RequestMethod.GET)
	public void addNewFiberjointboxAction(HttpServletRequest request,HttpServletResponse response,@PathVariable String cuid) throws IOException {
        HttpSession session = request.getSession(false);
        response.setCharacterEncoding("UTF-8");
        StringBuffer result = new StringBuffer();
        GenericDO point = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
        GenericDO cutoverTask = (GenericDO) session.getAttribute("cutOverTesk");
        DataObjectList fiberEqpList = (DataObjectList) session.getAttribute("fiberEqpList");
		Map<String, GenericDO> pointFjbMap = (Map<String, GenericDO>) session.getAttribute("pointFjbMap");
		DataObjectList preFiberEqpList = (DataObjectList) session.getAttribute("preFiberEqpList");
		DataObjectList fjbList = getFiberjointboxByCutoverTaskCuid(cutoverTask.getCuid());
		if (fjbList.size() >= 3) {
			response.getWriter().print("光缆段最多允许割接3次,不能再添加接头盒!");
			return;
		}
		if (cuid.split("-").length != 2) {
			response.getWriter().print("请选择一个路由点!");
			return;
		}
		if (cuid.split("-").length == 2) {
			if (!DMHelperX.isNotPointOfFiber(point.getClassName())) {
				response.getWriter().print("不是具体路由点不能添加接头盒,请选择具体路由点!");
				return;
			}
			if (isPointHasFiberJointBox(point, cutoverTask.getCuid())) {
				response.getWriter().print("该具体路由点已经添加了接头盒,请选择其他路由点或者将该具体路由点的接头盒删除之后再添加");
				return;
			}

			// if(isPointHasFiberJointBox(point)){
			// throw new UserException("该具体路由点已经添加了接头盒,请选择其他路由点或者将该具体路由点的接头盒删除之后再添加");
			// return;
			// }

			/*int ri = JOptionPane.showConfirmDialog(view.getViewPanel(),
					"是否拆分光缆段!", "确认", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);
			if (ri != 0) {
				return;
			}*/

			DataObjectList preList = getFiberjointboxByCutoverTaskCuid(cutoverTask.getCuid());
			preFiberEqpList.clear();
			for (GenericDO preDbo : preList) {
				GenericDO newPreDbo = (GenericDO) preDbo.deepClone();
				preFiberEqpList.add(newPreDbo);
			}
			String districtCuid = DaoHelper.getRelatedCuid(point.getAttrValue(Manhle.AttrName.relatedDistrictCuid));
			FiberJointBox fiberjointbox = new FiberJointBox();
			String labelCn = point.getAttrString(FiberJointBox.AttrName.labelCn) + "-接头盒";
			fiberjointbox.setLabelCn(getUniqueLabelCn(new BoActionContext(),labelCn));
			fiberjointbox.setOwnership(1);
			fiberjointbox.setMaintMode(1);
			fiberjointbox.setJunctionType(1);
			fiberjointbox.setConnectType(1);
			if (DMHelperX.isNotPointOfFiber(point.getClassName())) {
				fiberjointbox.setAttrValue(
						FiberJointBox.AttrName.relatedLocationCuid,
						point.getCuid());
			}
			fiberjointbox.setKind(1);
			fiberjointbox.setLongitude(point.getAttrDouble(
					Manhle.AttrName.longitude, 0.0));
			fiberjointbox.setLatitude(point.getAttrDouble(
					Manhle.AttrName.latitude, 0.0));
			fiberjointbox.setRealLongitude(point.getAttrDouble(
					Manhle.AttrName.realLongitude, 0.0));
			fiberjointbox.setRealLatitude(point.getAttrDouble(
					Manhle.AttrName.realLatitude, 0.0));
			fiberjointbox.setAttrValue(FiberJointBox.AttrName.relatedDistrictCuid, districtCuid);
			fiberjointbox.setCuid();
			fiberjointbox = getFiberJointBoxBO().addFiberJointBox(new BoActionContext(), fiberjointbox);
			result = result.append("{\"CUID\":\""+ fiberjointbox.getCuid() +"\",\"LABEL_CN\":\""+ fiberjointbox.getLabelCn() +"\"}");;
			pointFjbMap.put(point.getCuid(), fiberjointbox);
			fiberEqpList.add(fiberjointbox);
			//cutoverTask.setAttrValue(CutoverTask.AttrName.cutoverState,	DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign);
			Object oldRelatedDeviceObj = cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid);
			String oldRelatedDeviceCuid = getRelatedCuid(oldRelatedDeviceObj);
			String newRelatedDeviceCuid = new String();

			if (oldRelatedDeviceCuid == null || "".equals(oldRelatedDeviceCuid)) {
				newRelatedDeviceCuid = fiberjointbox.getCuid();
			} else {
				newRelatedDeviceCuid = oldRelatedDeviceCuid + "," + fiberjointbox.getCuid();
			}

			cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid,newRelatedDeviceCuid);
			getCutoverTaskBO().modifyCutoverTask(new BoActionContext(),cutoverTask);
		}
		
		session.removeAttribute("cutOverTask");
		session.setAttribute("cutOverTask", cutoverTask);
		session.removeAttribute("pointFjbMap");
        session.setAttribute("pointFjbMap", pointFjbMap);
        session.removeAttribute("fiberEqpList");
        session.setAttribute("fiberEqpList", fiberEqpList);
        session.removeAttribute("preFiberEqpList");
        session.setAttribute("preFiberEqpList", preFiberEqpList);
        
        response.getWriter().print(result.toString());
	}
  
	@RequestMapping(value = "/deleteFiberjointboxAction/{pcuid}",method = RequestMethod.GET)
	public void deleteFiberjointboxAction(HttpServletRequest request,HttpServletResponse response,@PathVariable String pcuid) throws IOException{
		HttpSession session = request.getSession(false);
		response.setCharacterEncoding("UTF-8");
		GenericDO cutoverTask = (GenericDO) session.getAttribute("cutOverTask");
		DataObjectList preFiberEqpList = (DataObjectList) session.getAttribute("preFiberEqpList");
		DataObjectList fiberEqpList = (DataObjectList) session.getAttribute("fiberEqpList");
		Map<String, GenericDO> map = (Map<String, GenericDO>) session.getAttribute("pointFjbMap");
		String result = null;
		DataObjectList preList=getFiberjointboxByCutoverTaskCuid(cutoverTask.getCuid());
		GenericDO point = getDuctManagerBO().getObjByCuid(new BoActionContext(), pcuid);
		preFiberEqpList.clear();
		for(GenericDO preDbo:preList){
			GenericDO newPreDbo=(GenericDO)preDbo.deepClone();
			preFiberEqpList.add(newPreDbo);
		}
        
		if(pcuid.split("-").length != 2){
			response.getWriter().print("请选择一个路由点!");
			return;
		}
		if(pcuid.split("-").length == 2){
			if(map.containsKey(point.getCuid())){
				GenericDO dbo=map.get(point.getCuid());
				FiberJointBox fiberjointbox=(FiberJointBox)dbo;
				fiberjointbox.setAttrValue(FiberJointBox.AttrName.relatedLocationCuid, "");
				getFiberJointBoxBO().modifyFiberJointBox(new BoActionContext(), fiberjointbox);
				String oldRelatedDeviceCuid=getRelatedCuid(cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid));					
				StringBuilder newRelatedDeviceCuid=new StringBuilder();
				if(oldRelatedDeviceCuid!=null && oldRelatedDeviceCuid.length()>0){
					String[] oldRelatedDeviceArrays=oldRelatedDeviceCuid.trim().split(",");
					for(int i=0;i<oldRelatedDeviceArrays.length;i++){
						String cuid=oldRelatedDeviceArrays[i];
						if(!cuid.equals(fiberjointbox.getCuid())){
							newRelatedDeviceCuid.append(cuid+",");
						}
					}
				}
				if(newRelatedDeviceCuid.length()>0){
					cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, newRelatedDeviceCuid.substring(0, newRelatedDeviceCuid.length()-1));
				}else{
					cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, newRelatedDeviceCuid.toString());
				}
				cutoverTask.setAttrValue(CutoverTask.AttrName.cutoverState, DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign);
				
				getCutoverTaskBO().modifyCutoverTask(new BoActionContext(), cutoverTask);
				map.remove(point.getCuid());
				DataObjectList tempList=new DataObjectList();
				for(GenericDO gdo:fiberEqpList){
					tempList.add(gdo);
				}
				fiberEqpList.clear();
				for(GenericDO tempDbo:tempList){
					if(!tempDbo.getCuid().equals(fiberjointbox.getCuid())){
						fiberEqpList.add(tempDbo);
					}
				}
				//修改labelText
				StringBuilder sb=new StringBuilder();
				for(GenericDO g:fiberEqpList){
					sb.append(g.getAttrString(FiberJointBox.AttrName.labelCn)+",");
				}
				result = "删除接头盒("+fiberjointbox.getLabelCn()+")完成!";
			}else{
				result = "该路由点没有关联的接头盒!";
			}			
		}
		
		session.removeAttribute("cutOverTask");
		session.setAttribute("cutOverTask", cutoverTask);
		session.removeAttribute("preFiberEqpList");
		session.setAttribute("preFiberEqpList", preFiberEqpList);
		session.removeAttribute("fiberEqpList");
		session.setAttribute("fiberEqpList", fiberEqpList);
		session.removeAttribute("pointFjbMap");
		session.setAttribute("pointFjbMap", map);
		
		response.getWriter().print(result);
	}
	
	public boolean saveInfos(HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession(false);
		boolean flag = false;
		WireSeg wireSeg = (WireSeg) session.getAttribute("wireSeg");
		DataObjectList fiberEqpList = (DataObjectList) session.getAttribute("fiberEqpList");
		DataObjectList preFiberEqpList = (DataObjectList) session.getAttribute("preFiberEqpList");
		GenericDO cutOverTesk = (GenericDO) session.getAttribute("cutOverTesk");
		//map1.clear();
		 if(wireSeg!=null && preFiberEqpList.size()>0 && fiberEqpList.size()>0){
			 deleteSaveInfos(wireSeg,  preFiberEqpList, fiberEqpList, cutOverTesk);
		     saveInfoIntoDB(wireSeg, fiberEqpList, cutOverTesk);
			 flag = true;
		 }else if(wireSeg!=null && preFiberEqpList.size()>0 && fiberEqpList.size()==0){
			 deleteSaveInfos(wireSeg,  preFiberEqpList, fiberEqpList, cutOverTesk);
			 throw new UserException("请添加割接点");
		 }else if(wireSeg!=null && preFiberEqpList.size()==0 && fiberEqpList.size()==0){
			 throw new UserException("请添加割接点");
		 }else if(wireSeg!=null && preFiberEqpList.size()==0 && fiberEqpList.size()>0){
			 deleteSaveInfos(wireSeg,  preFiberEqpList, fiberEqpList, cutOverTesk);
			 saveInfoIntoDB(wireSeg, fiberEqpList, cutOverTesk);
			 flag = true;
		 }
		 
		session.removeAttribute("cutOverTask");
		session.removeAttribute("preFiberEqpList");
		session.removeAttribute("fiberEqpList");
		session.removeAttribute("pointFjbMap");
        session.removeAttribute("wireSeg");		 
		return flag;
	 }
	
	private void saveInfoIntoDB(WireSeg wireSeg,DataObjectList fiberEqpList,GenericDO cutoverTask){
		//保存光缆段下的纤芯
		 DataObjectList fibers=getFiberBO().getFibersByWireSeg(new BoActionContext(), wireSeg);
		 if(fibers!=null && fibers.size()>0){
			 long count=fibers.size();
			 wireSeg.setFiberCount(count);   //光缆段有纤芯但是纤芯数量为0的情况
			 DataObjectList allJointPoints=saveFiberJointPoint(fibers,fiberEqpList);
			// map1.put("JOINT_POINT",allJointPoints);
			 DataObjectList fiberPointList=getFiberPointList(wireSeg,fiberEqpList);  //用于保存所有的光接头盒和光缆段的起止点
			 
			 DataObjectList tempCutoverWireSegList=new DataObjectList();
			 for(int i=0,size=fiberPointList.size();i<size-1;i++){
				 GenericDO origDbo=fiberPointList.get(i);
				 GenericDO destDbo=fiberPointList.get(i+1);
				 GenericDO tempCutoverWireSeg=saveTempCutoverWireSeg(origDbo, destDbo,wireSeg,cutoverTask);	
				 tempCutoverWireSegList.add(tempCutoverWireSeg);
				 saveTempCutoverFibers(origDbo, destDbo, tempCutoverWireSeg, fibers, allJointPoints);
			 }
			// map1.put("TEMP_SEG", tempCutoverWireSegList);
			 //保存临时敷设信息
			 saveTempWireToDuctLine(fiberPointList,cutoverTask,wireSeg,fiberEqpList);
			 //修改光接头盒的属性
			 getFiberJointBoxBO().modifyFiberJointBoxs(new BoActionContext(), fiberEqpList);
			 //修改cutovertask属性
			 StringBuilder sb=new StringBuilder();
			 for(GenericDO dbo:fiberEqpList){
				 sb.append(dbo.getCuid()+",");				 
			 }
			 if(sb.length()>0){
				 cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, sb.substring(0, sb.length()-1));
			 }else{
				 cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, "");
			 }
			 cutoverTask.setAttrValue(CutoverTask.AttrName.cutoverState, DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign);
			 getCutoverTaskBO().modifyCutoverTask(new BoActionContext(), cutoverTask);
		 }
	 }
	 
	 private void deleteSaveInfos(WireSeg wireSeg,DataObjectList preFiberEqpList,DataObjectList fiberEqpList,GenericDO cutoverTask){		 
		 try {
			 DataObjectList tempCutoverWireSegs=getTempCutoverWireSegBO().getTempCutoverWireSegByTaskCuid(new BoQueryContext(), cutoverTask.getCuid());
			 
			 if(tempCutoverWireSegs!=null && tempCutoverWireSegs.size()>0){
				//删除纤芯
				 DataObjectList tempCutoverFibers=new DataObjectList();
				 for(GenericDO tempWireseg:tempCutoverWireSegs){
					 DataObjectList list=getTempCutoverFiberBO().getTempCutoverFiberBySegCuid(new BoQueryContext(), tempWireseg.getCuid());
					 if(list!=null && list.size()>0){
						 tempCutoverFibers.addAll(list);
					 }
				 }
				 getDuctManagerBO().deleteObjects(new BoActionContext(), tempCutoverFibers);
				 
				 //删除敷设
				 DataObjectList tempWireToDuctLines=new DataObjectList();
				 for(GenericDO dbo:tempCutoverWireSegs){
					 TempCutoverWireSeg tempCutoverWireSeg=(TempCutoverWireSeg)dbo;
					 DataObjectList list=getTempWireToDuctlineBO().getTempWireToDuctlinesBySeg(new BoQueryContext(), tempCutoverWireSeg);
					 if(list!=null && list.size()>0){				 
						 tempWireToDuctLines.addAll(list);
					 }
				 }
				 getDuctManagerBO().deleteObjects(new BoActionContext(), tempWireToDuctLines);	
				 //删除光缆段
				 getDuctManagerBO().deleteObjects(new BoActionContext(), tempCutoverWireSegs);	
			 }

			 //删除焊点
			 DataObjectList fiberJointPoints=new DataObjectList();
			 for(GenericDO fiberjointbox:preFiberEqpList){
				 DataObjectList list=getFiberJointPointBO().getFiberJointPointByJoinBoxCuid(new BoActionContext(), fiberjointbox.getCuid());
				 if(list!=null && list.size()>0){
					 fiberJointPoints.addAll(list);
				 }
			 }
			 if(fiberJointPoints.size()>0){
				 getFiberJointPointBO().deleteFiberJointPoints(new BoActionContext(), fiberJointPoints);
			 }				 
			 //修改光接头盒
			 for(GenericDO fiberJointBox:preFiberEqpList){
				 fiberJointBox.setAttrValue(FiberJointBox.AttrName.relatedLocationCuid, "");
			 }
			 getFiberJointBoxBO().modifyFiberJointBoxs(new BoActionContext(), preFiberEqpList);
			 preFiberEqpList.clear(); 
			 cutoverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, "");
			 getCutoverTaskBO().modifyCutoverTask(new BoActionContext(), cutoverTask);
		 } catch (Exception e) {
			// TODO: handle exception
			 LogHome.getLog().error("deleteSaveInfos出错", e);
		 }
	 }
	 
	 private DataObjectList saveTempCutoverFibers(GenericDO origDbo,GenericDO destDbo,GenericDO tempCutoverWireSeg,DataObjectList fibers,DataObjectList allJointPoints){
		 DataObjectList tempCutoverFibers=new DataObjectList();
		 DataObjectList jointPointsByorigDbo=getFiberJointPointByDevice(allJointPoints, origDbo);
		 DataObjectList jointPointsBydestDbo=getFiberJointPointByDevice(allJointPoints, destDbo);
		 
		 if(fibers!=null && fibers.size()>0){
			 for(int i=0;i<fibers.size();i++){
				 GenericDO dbo=fibers.get(i);
				 if(dbo instanceof Fiber){
					 Fiber fiber=(Fiber)dbo;
					 //String origSiteCuid=getRelatedCuid(fiber.getOrigSiteCuid());
					 //String destSiteCuid=getRelatedCuid(fiber.getDestSiteCuid());
					 String origSiteCuid=getRelatedCuid(fiber.getAttrValue(Fiber.AttrName.origSiteCuid));
					 String destSiteCuid=getRelatedCuid(fiber.getAttrValue(Fiber.AttrName.destSiteCuid));
					 
					 
					 TempCutoverFiber tempCutoverFiber=new TempCutoverFiber();
					 
					 if(origSiteCuid.equals(origDbo.getCuid())){
						 //String origEqpCuid=getRelatedCuid(fiber.getOrigEqpCuid());
						 //String origPointCuid=getRelatedCuid(fiber.getOrigPointCuid());
						 String origEqpCuid=getRelatedCuid(fiber.getAttrValue(Fiber.AttrName.origEqpCuid));
						 String origPointCuid=getRelatedCuid(fiber.getAttrValue(Fiber.AttrName.origPointCuid));
						 tempCutoverFiber.setOrigSiteCuid(origSiteCuid);
						 tempCutoverFiber.setOrigEqpCuid(origEqpCuid);
						 tempCutoverFiber.setOrigPointCuid(origPointCuid);
					 }else{
						 tempCutoverFiber.setOrigSiteCuid(origDbo.getCuid());
						 tempCutoverFiber.setOrigEqpCuid(origDbo.getCuid());
						 tempCutoverFiber.setOrigPointCuid(jointPointsByorigDbo.get(i).getCuid());
					 }
					 
					 if(destSiteCuid.equals(destDbo.getCuid())){
						 //String destEqpCuid=getRelatedCuid(fiber.getDestEqpCuid());
						 //String destPointCuid=getRelatedCuid(fiber.getDestPointCuid());
						 String destEqpCuid=getRelatedCuid(fiber.getAttrValue(Fiber.AttrName.destEqpCuid));
						 String destPointCuid=getRelatedCuid(fiber.getAttrValue(Fiber.AttrName.destPointCuid));
						 tempCutoverFiber.setDestSiteCuid(destSiteCuid);
						 tempCutoverFiber.setDestEqpCuid(destEqpCuid);
						 tempCutoverFiber.setDestPointCuid(destPointCuid);						 
					 }else{
						 tempCutoverFiber.setDestSiteCuid(destDbo.getCuid());
						 tempCutoverFiber.setDestEqpCuid(destDbo.getCuid());
						 tempCutoverFiber.setDestPointCuid(jointPointsBydestDbo.get(i).getCuid());
					 }
					 					 
					 tempCutoverFiber.setDirection(fiber.getDirection());
					 tempCutoverFiber.setWireNo(fiber.getWireNo());
					 tempCutoverFiber.setLabelCn(fiber.getLabelCn());
					 tempCutoverFiber.setRelatedSegCuid(tempCutoverWireSeg.getCuid());
					 tempCutoverFiber.setRelatedSystemCuid(fiber.getRelatedSystemCuid());
					 tempCutoverFiber.setRelatedPathCuid(fiber.getRelatedPathCuid());
					 tempCutoverFiber.setFiberColor(fiber.getFiberColor());
					 tempCutoverFiber.setFiberLevel(fiber.getFiberLevel());
					 tempCutoverFiber.setUsageState(fiber.getUsageState());
					 tempCutoverFibers.add(tempCutoverFiber);
				 }
			 }
			//临时纤芯入库：：tempCutoverFibers
			 tempCutoverFibers = getTempCutoverFiberBO().addtempCutoverFibers(new BoActionContext(), tempCutoverFibers);
			 //map1.put(tempCutoverWireSeg.getCuid(), tempCutoverFibers);
		 }
		 return tempCutoverFibers;
	 }
	 
	 private void saveTempWireToDuctLine(DataObjectList fiberPointList,GenericDO cutoverTask,WireSeg wireSeg1,DataObjectList fiberEqpList){		 
		 DataObjectList wireSegList=getTempCutoverWireSegBO().getTempCutoverWireSegByTaskCuid(new BoQueryContext(), cutoverTask.getCuid());
		 
		 DataObjectList sortedTempWireSegs=getSortedTempWireSegs(wireSegList, fiberPointList);		 
		 DataObjectList wiretoductlines=getWireToDuctline(wireSeg1);		
		 wiretoductlines.sort(WireToDuctline.AttrName.indexInRoute, true);
		 DataObjectList newTempWiredls=new DataObjectList();
		 //获取修改割接之后的敷设分组情况
		 List<DataObjectList> cutoverWdlList=getCutOverWireToDuctLineList(wiretoductlines,fiberEqpList,wireSeg1);
		
	    for(int i=0,size=cutoverWdlList.size();i<size;i++){
	    	GenericDO wireSeg=sortedTempWireSegs.get(i);
	    	DataObjectList list=cutoverWdlList.get(i);
	    	for(int j=0;j<list.size();j++){
	    		WireToDuctline oldWtdl=(WireToDuctline)list.get(j);
	    		TempWireToDuctline tempWiretd = new TempWireToDuctline();
	    		tempWiretd.setCuid();
	    		tempWiretd.setLabelCn(oldWtdl.getLabelCn());
	    		tempWiretd.setEndPointCuid(oldWtdl.getEndPointCuid());
	    		tempWiretd.setWireSystemCuid(oldWtdl.getWireSystemCuid());
	    		tempWiretd.setDuctlineCuid(oldWtdl.getDuctlineCuid());
	    		tempWiretd.setLineSystemCuid(oldWtdl.getLineSystemCuid());
	    		tempWiretd.setLineBranchCuid(oldWtdl.getLineBranchCuid());
	    		tempWiretd.setDisPointCuid(oldWtdl.getDisPointCuid());
	    		tempWiretd.setLineSegCuid(oldWtdl.getLineSegCuid());
	    		tempWiretd.setDirection(oldWtdl.getDirection());
	    		tempWiretd.setHoleCuid(oldWtdl.getHoleCuid());
	    		tempWiretd.setChildHoleCuid(oldWtdl.getChildHoleCuid());
	    		tempWiretd.setWireSegCuid(getRelatedCuid(wireSeg));
	    		tempWiretd.setIndexInRoute(j+1);
	    		newTempWiredls.add(tempWiretd);
	    	}
	    }
	    getTempWireToDuctlineBO().addTempWireToDuctlines(new BoActionContext(), newTempWiredls);
	 }
	 
	 private GenericDO saveTempCutoverWireSeg(GenericDO origDbo,GenericDO destDbo,WireSeg wireSeg,GenericDO cutoverTask){
		 TempCutoverWireSeg tempCutoverWireSeg = new TempCutoverWireSeg();
		 String tempSegLabelCn1="";
		 String systemCuid = getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
		 String branchCuid = getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.relatedBranchCuid));
		 long fiberCount = (Long) wireSeg.getAttrValue(WireSeg.AttrName.fiberCount);
		 String origLabelCn = (String) origDbo.getAttrValue(FiberJointBox.AttrName.labelCn);
		 String fiberEqpLabelCn = (String) destDbo.getAttrValue(FiberJointBox.AttrName.labelCn);
		 tempSegLabelCn1= origLabelCn+"-"+fiberEqpLabelCn;
		 //设置临时光缆段的属性值
		 tempCutoverWireSeg.setLabelCn(tempSegLabelCn1);
		 tempCutoverWireSeg.setOrigPointCuid(origDbo.getCuid());
		 tempCutoverWireSeg.setDestPointCuid(destDbo.getCuid());
		 tempCutoverWireSeg.setRelatedSystemCuid(systemCuid);
		 tempCutoverWireSeg.setRelatedBranchCuid(branchCuid);
		 tempCutoverWireSeg.setFiberCount(fiberCount);
		 tempCutoverWireSeg.setRelatedCutoverTaskCuid(cutoverTask.getCuid());
		 //调服务，入库。  
		 tempCutoverWireSeg = (TempCutoverWireSeg) getTempCutoverWireSegBO().addtempCutoverWireSeg(new BoActionContext(), tempCutoverWireSeg);

		 return tempCutoverWireSeg;
	 }
	 
	 private DataObjectList getFiberJointPointByDevice(DataObjectList allJointPoints,GenericDO device){
		 DataObjectList list=new DataObjectList();
		
		 for(int i=0,size=allJointPoints.size();i<size;i++){
			 GenericDO dbo=allJointPoints.get(i);
			 String deviceCuid=dbo.getAttrString(FiberJointPoint.AttrName.relatedDeviceCuid);
			 if(deviceCuid!=null && device.getCuid().equals(deviceCuid)){
				 list.add(dbo);
			 }
		 }
		 DataObjectList resultList=new DataObjectList();
		 //对list按照名称进行排序
		 for(int j=0;j<list.size();j++){
			 int index=j+1;
			 for(int k=0;k<list.size();k++){
				 GenericDO dbo=list.get(k);
				 String labelCn=dbo.getAttrString(FiberJointPoint.AttrName.labelCn);
				 if(labelCn.equals(String.valueOf(index))){
					 resultList.add(dbo);
				 }
			 }
		 }
		 return resultList;
	 }
	 
	 private DataObjectList getSortedTempWireSegs(DataObjectList wireSegList,DataObjectList fiberPointList){
		 DataObjectList sortedTempWireSegs=new DataObjectList();		 
		 for(int i=0;i<fiberPointList.size()-1;i++){
			 GenericDO origGdo=fiberPointList.get(i);
			 GenericDO destGdo=fiberPointList.get(i+1);
			 String origCuid=origGdo.getCuid();
			 String destCuid=destGdo.getCuid();
			 
			 for(int j=0;j<wireSegList.size();j++){
				 GenericDO wireSeg=wireSegList.get(j);
				 String wireSegOrigCuid=getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.origPointCuid));
				 String wireSegDestCuid=getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.destPointCuid));
				 if((origCuid.equals(wireSegOrigCuid) && destCuid.equals(wireSegDestCuid)) || 
				    (origCuid.equals(wireSegDestCuid) && destCuid.equals(wireSegOrigCuid))){
					 sortedTempWireSegs.add(wireSeg);
				 }
			 }
		 }
		 return sortedTempWireSegs;
	 }
	 
	 private DataObjectList getWireToDuctline(WireSeg wireSeg){
			//获取敷设信息
			 String sql=WireToDuctline.AttrName.wireSegCuid+"='"+wireSeg.getCuid()+"'";
			 DataObjectList wiretoductlines = new DataObjectList();
			 try {
				 wiretoductlines = getWireToDuctLineBO().getWireToDuctLineBySql(new BoActionContext(), sql);
			} catch (Exception e) {
				LogHome.getLog().error("根据光缆段获取敷设信息失败!",e);
			}
			return wiretoductlines;
		 }
	 
	 private List<String> getSortedSelPoints(DataObjectList fiberjointboxList,DataObjectList points){
		 List<String> sortedSelPoints=new ArrayList<String>();
			
			for(int m=0;m<points.size();m++){
				GenericDO point=points.get(m);
				String pointCuid=point.getCuid();
				for(int n=0;n<fiberjointboxList.size();n++){
					GenericDO fiberjointbox=fiberjointboxList.get(n);
					Object relatedLocationObj=fiberjointbox.getAttrValue(FiberJointBox.AttrName.relatedLocationCuid);
					String relatedLocationCuid=getRelatedCuid(relatedLocationObj);
					if(pointCuid.equals(relatedLocationCuid)){
						sortedSelPoints.add(relatedLocationCuid);
					}
				}
			}
		 return sortedSelPoints;
	 }
	 
	 private List<DataObjectList> getCutOverWireToDuctLineList(DataObjectList wiretoductlines,DataObjectList fiberEqpList,WireSeg wireSeg){
		    DataObjectList fiberjointboxList=fiberEqpList;
			DataObjectList points=getRoutePointBySeg(wireSeg);			
			
			List<String> sortedSelPoints=getSortedSelPoints(fiberjointboxList, points);
		    
			List<DataObjectList> list=new ArrayList<DataObjectList>();
			List<String> usedWtdlList=new ArrayList<String>();			
			
			for(int j=0;j<sortedSelPoints.size();j++){
				String cuid=sortedSelPoints.get(j);				
				DataObjectList tempList=new DataObjectList();
				for(int i=0,size=wiretoductlines.size();i<size;i++){
					WireToDuctline wireToDuctLine=(WireToDuctline)wiretoductlines.get(i);
					if(usedWtdlList.contains(wireToDuctLine.getCuid())){
						continue;
					}
					String disPtCuid=wireToDuctLine.getDisPointCuid();
					String endPtCuid=wireToDuctLine.getEndPointCuid();
				    if(cuid.equals(disPtCuid) || cuid.equals(endPtCuid)){
				    	tempList.add(wireToDuctLine);
				    	usedWtdlList.add(wireToDuctLine.getCuid());
				    	break;
				    }else{
				    	tempList.add(wireToDuctLine);
				    	usedWtdlList.add(wireToDuctLine.getCuid());
				    }
				}
				list.add(tempList);
			}
			
			DataObjectList lasttempList=new DataObjectList();
			for(int k=0;k<wiretoductlines.size();k++){
				GenericDO gdo=wiretoductlines.get(k);
				if(usedWtdlList.contains(gdo.getCuid())){
					continue;
				}
				lasttempList.add(gdo);
			}
			list.add(lasttempList);
			return list;
	 }	
	 
	 private DataObjectList saveFiberJointPoint(DataObjectList fibers,DataObjectList fiberEqpList){
		 /**
		  * 目前新建的接头盒和已有的接头盒 全部都是 新建的焊点，已有的接头盒的焊点 是不是 需要另外一种处理方式呢，后续需要重新考虑一下。。。
		  */
		 DataObjectList jointPoints=new DataObjectList();		 
		 DataObjectList fiberjointboxList=fiberEqpList;
		 for(int i=0;i<fiberjointboxList.size();i++){
			 GenericDO dbo=fiberjointboxList.get(i);
			 String districtCuid=getRelatedCuid(dbo.getAttrValue(FiberJointBox.AttrName.relatedDistrictCuid));
			 for(int j=0;j<fibers.size();j++){
				 FiberJointPoint fiberJointPoint=new FiberJointPoint();
				 fiberJointPoint.setLabelCn((j+1)+"");
				 fiberJointPoint.setRelatedDeviceCuid(dbo.getCuid());
				 fiberJointPoint.setIsConnected(false);
				 fiberJointPoint.setRelatedDistrictCuid(districtCuid);
				 fiberJointPoint.setServiceState(1);
				 fiberJointPoint.setCuid();
				 jointPoints.add(fiberJointPoint);
			 }				 
		 }	
		 //焊点入库
		 jointPoints=getFiberJointPointBO().addJointPoints(new BoActionContext(), jointPoints);
		 return jointPoints;
	} 
	 
	 private DataObjectList getFiberPointList(WireSeg wireSeg,DataObjectList fiberEqpList){
		 DataObjectList fiberPointList=new DataObjectList();
		 //光缆段的起点
		 String origCuid=getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.origPointCuid));
		 fiberPointList.add(getDuctManagerBO().getObjByCuid(new BoActionContext(), origCuid));
		 
		 DataObjectList jointBoxList=fiberEqpList;
		 //添加的光接头盒，需要按路由点的顺序进行排列
		 DataObjectList pointList= getRoutePointBySeg(wireSeg);
		 for(GenericDO dbo:pointList){
			 for(int i=0,size=jointBoxList.size();i<size;i++){
				 GenericDO jointBox=jointBoxList.get(i);
				 Object relatedLocationObj=jointBox.getAttrValue(FiberJointBox.AttrName.relatedLocationCuid);
				 if(relatedLocationObj==null){
					 continue;
				 }
				 String relatedLocationCuid=getRelatedCuid(relatedLocationObj);
				 if(dbo.getCuid().equals(relatedLocationCuid)){
					 fiberPointList.add(jointBox);
					 break;
				 }
			 }
		 }
		 //光缆段的终点
		 String destCuid=getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.destPointCuid));
		 fiberPointList.add(getDuctManagerBO().getObjByCuid(new BoActionContext(),destCuid));
		 return fiberPointList;
	 }
	
	private List<Map<String, String>> buildPointTree(WireSeg wireSeg) {
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		DataObjectList routePoints = getRoutePointBySeg(wireSeg);
		String wireCuid = wireSeg.getCuid();
		map.put("CUID", wireCuid);
		map.put("LABEL_CN", wireSeg.getAttrString("LABEL_CN"));
		map.put("ICON", Icon.getIconByType(wireSeg));
		map.put("PARENTCUID", null);
		result.add(map);
		Iterator<GenericDO> iterator = routePoints.iterator();
		while (iterator.hasNext()) {
			HashMap<String, String> map1 = new HashMap<String, String>();
			GenericDO genericDO = iterator.next();
			map1.put("CUID", genericDO.getCuid());
			map1.put("LABEL_CN", genericDO.getAttrString("LABEL_CN"));
			map1.put("ICON", getIconByCuid(genericDO.getCuid()));
			map1.put("PARENTCUID", wireCuid);
			result.add(map1);
		}
		return result;
	}

	private DataObjectList getRoutePointBySeg(WireSeg wireSeg) {
		DataObjectList wiretoductlines = null;
		String wireSegOrigPointCuid = getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.origPointCuid));
		String wireSegDestPointCuid = getRelatedCuid(wireSeg.getAttrValue(WireSeg.AttrName.destPointCuid));
		if (wireSegOrigPointCuid == null || wireSegDestPointCuid == null) {
			return null;
		}
		try {
			wiretoductlines = (DataObjectList) (List) BoCmdFactory.getInstance().execBoCmd(WireToDuctLineBOHelper.ActionName.getDuctLinesByWireSeg,new BoActionContext(), wireSeg);
		} catch (Exception ex) {
			logger.info(("il8nKey_com.boco.transnms.client.view.dm.system.RouteHandler.java1")
					+ ex.getMessage());
		}
		if (wiretoductlines != null && wiretoductlines.size() > 0) {
			List<String> pointCuids = DMHelper
					.getPointCuidListByWireToDuctlines(wiretoductlines);
			DataObjectList list = new DataObjectList();
			for (String pointCuid : pointCuids) {
				GenericDO g = new GenericDO();
				g.setCuid(pointCuid);
				list.add(g);
			}
			DataObjectList res = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(),pointCuids.toArray(new String[pointCuids.size()]));
			return res;
		} else {
			return null;
		}
	}

	private String getIconByCuid(String cuid) {
		if (cuid != null) {
			if (cuid.startsWith("MANHLE")) {
				return "/resources/map/MANHLE.png";
			} else if (cuid.startsWith("POLE")) {
				return "/resources/map/POLE.png";
			} else if (cuid.startsWith("STONE")) {
				return "/resources/map/STONE.png";
			} else if (cuid.startsWith("INFLEXION")) {
				return "/resources/map/INFLEXION.png";
			} else if (cuid.startsWith("SITE")) {
				return "/resources/map/SITE.png";
			} else if (cuid.startsWith("FIBER_JOINT_BOX")) {
				return "/resources/topo/dm/FiberJointBox.png";
			} else if (cuid.startsWith("FIBER_CAB")) {
				return "/resources/topo/dm/FiberCab.png";
			} else if (cuid.startsWith("FIBER_DP")) {
				return "/resources/topo/dm/FiberDp.png";
			} else if (cuid.startsWith("ACCESSPOINT")) {
				return "/resources/topo/dm/Accesspoint.png";
			}
		}
		return null;
	}
	
	private final String getUniqueLabelCn(BoActionContext actionContext, String labelCn) {
    	String sql = " " + FiberJointBox.AttrName.labelCn + " like '" + labelCn + "%'";
    	DataObjectList list = getFiberJointBoxBO().getFiberJointBoxBySql(actionContext, sql);
    	int index = 1;
    	if(list != null) {
    		index = list.size() + 1;
    	}
    	return getUnique(actionContext, labelCn, index);
    }
	private final String getUnique(BoActionContext actionContext, String labelCn, int index) {
    	String rLabelCn = labelCn + index;
    	FiberJointBox dupNameBox = getFiberJointBoxBO().getFiberJointBoxCuidBylabcn(actionContext, rLabelCn);
    	if(null == dupNameBox) {
    		return rLabelCn;
    	} else {
    		return getUnique(actionContext, labelCn, ++index);
    	}
    }
	
	private DataObjectList getFiberjointboxByCutoverTaskCuid(String cutoverTaskCuid){
		 String sql=" CUID='"+cutoverTaskCuid+"'";
		 DboCollection collection=getCutoverTaskBO().getCutoverTaskBySql(new BoQueryContext(),sql);
		 GenericDO cutoverTask=(GenericDO)collection.getAttrField(CutoverTask.CLASS_NAME, 0);
		 String relatedDeviceCuid=getRelatedCuid(cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid));
		 
		 if(relatedDeviceCuid==null || relatedDeviceCuid.length()==0){
			 return new DataObjectList();
		 }else{
			 String[] deviceArrays=relatedDeviceCuid.trim().split(",");
			 return (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), deviceArrays);
		 }
	 }
	
	private boolean isPointHasFiberJointBox(GenericDO point,String cutoverTaskCuid){
		 try {
			String sql=" CUID='"+cutoverTaskCuid+"'";
			 DboCollection collection=getCutoverTaskBO().getCutoverTaskBySql(new BoQueryContext(),sql);
			 GenericDO cutoverTask=collection.getAttrField(CutoverTask.CLASS_NAME, 0);
			 String relatedDeviceCuid=cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid).toString().trim();
			 if(relatedDeviceCuid==null || relatedDeviceCuid.length()==0){
				 return false;
			 }else{
				 String[] deviceCuids=relatedDeviceCuid.split(",");
				 List<String> lst = Arrays.asList(deviceCuids);
				 DataObjectList genericDOListByCuids = getDuctManagerBO().getGenericDOListByCuids(lst);//.getObjsByCuid(new BoActionContext(), deviceCuids);
				 if(null != genericDOListByCuids && genericDOListByCuids.size()>0){
					 for(GenericDO device : genericDOListByCuids){
						 String relatedLocationCuid=DMHelper.getRelatedCuid(device.getAttrValue(FiberJointBox.AttrName.relatedLocationCuid));
						 if(point.getCuid().equals(relatedLocationCuid)){
							 return true;
						 }
					 }
				 }
			 }
			
		} catch (com.boco.common.util.except.UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private String getRelatedCuid(Object obj) {
		if (obj instanceof GenericDO) {
			return ((GenericDO) obj).getCuid();
		} else if (obj instanceof String) {
			return (String) obj;
		} else if (obj != null) {
			return (String) obj.toString();
		} else {
			return null;
		}
	}

	private ICutoverTaskBO getCutoverTaskBO() {
		return BoHomeFactory.getInstance().getBO(ICutoverTaskBO.class);
	}
	
	private IFiberJointBoxBO getFiberJointBoxBO(){
		 return BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class);
	}

	private IDuctManagerBO getDuctManagerBO() {
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

	private IFiberBO getFiberBO(){
		 return BoHomeFactory.getInstance().getBO(IFiberBO.class);
	}
	 
	private IFiberJointPointBO getFiberJointPointBO(){
		 return BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class);
	}
	
	private ITempCutoverFiberBO getTempCutoverFiberBO(){
		 return BoHomeFactory.getInstance().getBO(ITempCutoverFiberBO.class);
	 }
	 
	private ITempWireToDuctlineBO getTempWireToDuctlineBO(){
		 return BoHomeFactory.getInstance().getBO(ITempWireToDuctlineBO.class);
	}
	
	private ITempCutoverWireSegBO getTempCutoverWireSegBO(){
		 return BoHomeFactory.getInstance().getBO(ITempCutoverWireSegBO.class);
	}
	
	private IWireToDuctLineBO getWireToDuctLineBO() {
		return BoHomeFactory.getInstance().getBO(IWireToDuctLineBO.class);
 }
}
