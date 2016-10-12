package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Optical;
import com.boco.transnms.common.dto.OpticalWay;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.PresetPoint;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.AbstractDO;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class DeleteResInMapAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	static Map<String,String> actionMap = new HashMap<String,String>();
	static {
		actionMap.put(Manhle.CLASS_NAME, "IManhleBO.deleteManhles");
		actionMap.put(Pole.CLASS_NAME, "IPoleBO.deletePoles");
		actionMap.put(Stone.CLASS_NAME, "IStoneBO.deleteStones");
		actionMap.put(Inflexion.CLASS_NAME, "IInflexionBO.deleteInflexions");
		actionMap.put(FiberCab.CLASS_NAME, "IFiberCabBO.deleteFiberCabs");
		actionMap.put(FiberDp.CLASS_NAME, "IFiberDpBO.deleteFiberDps");
		actionMap.put(FiberJointBox.CLASS_NAME, "IFiberJointBoxBO.deleteFiberJointBoxs");
		actionMap.put(PresetPoint.CLASS_NAME, "IPresetPointBO.deletePresetPoint");
		actionMap.put(Accesspoint.CLASS_NAME, "IAccessPointBO.deleteAccesspoints");
	}
	
	public boolean deletePoints(List<String> cuids) throws UserException {

		BoActionContext context = ActionContextUtil.getActionContext();
		logger.debug("context:::deletePoints:::"+context.getUserId()+"======"+context.getUserName());
		Map<String, DataObjectList> pointsMap = new HashMap<String, DataObjectList>();
		pointsMap.put(Manhle.CLASS_NAME, new DataObjectList());
		pointsMap.put(Pole.CLASS_NAME, new DataObjectList());
		pointsMap.put(Stone.CLASS_NAME, new DataObjectList());
		pointsMap.put(Inflexion.CLASS_NAME, new DataObjectList());
		pointsMap.put(FiberCab.CLASS_NAME, new DataObjectList());
		pointsMap.put(FiberDp.CLASS_NAME, new DataObjectList());
		pointsMap.put(FiberJointBox.CLASS_NAME, new DataObjectList());
		pointsMap.put(PresetPoint.CLASS_NAME, new DataObjectList());
		pointsMap.put(Accesspoint.CLASS_NAME, new DataObjectList());

		DataObjectList pointList = new DataObjectList();
		try {
			IDuctManagerBO ductmanagerBO = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			pointList = ductmanagerBO.getAllPoints(cuids,AbstractDO.AttrName.objectId, GenericDO.AttrName.cuid,
					GenericDO.AttrName.labelCn);
			for (GenericDO point : pointList) {
				pointsMap.get(point.getClassName()).add(point);
			}
		} catch (Exception e) {
			logger.error("查询点设施失败");
		}
		try {
			for (String className : pointsMap.keySet()) {
				DataObjectList list = pointsMap.get(className);
				if (!list.isEmpty()) {
					BoCmdFactory.getInstance().execBoCmd(actionMap.get(className), context, list);
				}
			}
		} catch (Throwable ex) {
			logger.error("删除点设施对象出错" + ex.getMessage());
			throw new UserException(ex.getMessage());
		}
		return true;
	}
	 
	 
	 	/**
	 	 * 删除系统
	 	 * @param cuids
	 	 * @param isDeletePoint 是否删除点     true：删除 ， false： 只删除线
	 	 * @throws Exception
	 	 * @author gaoxf at 2014年4月1日 下午3:46:48
	 	 */
		public void deleteSystems(final List<String> cuids,boolean isDeletePoint) throws UserException {
			BoActionContext context = ActionContextUtil.getActionContext();
			logger.debug("context:::deleteSystems:::"+context.getUserId()+"======"+context.getUserName());
			List systems = getDuctManagerBO().getObjsByCuid(new BoActionContext(), cuids.toArray(new String[]{}));
			DataObjectList deletedSystemList = new DataObjectList();
			for(Object obj: systems){
				GenericDO  gdo = (GenericDO)obj;
				GenericDO  system = gdo;
				if(WireSystem.CLASS_NAME.equals(gdo.getClassName())){
					system  = new WireSystem();
				}else if(DuctSystem.CLASS_NAME.equals(gdo.getClassName())){
					system  = new DuctSystem();
				}else if(PolewaySystem.CLASS_NAME.equals(gdo.getClassName())){
					system  = new PolewaySystem();
				}else if(StonewaySystem.CLASS_NAME.equals(gdo.getClassName())){
					system  = new StonewaySystem();
				}else if(UpLine.CLASS_NAME.equals(gdo.getClassName())){
					system  = new UpLine();
				}else if(HangWall.CLASS_NAME.equals(gdo.getClassName())){
					system  = new HangWall();
				}
				system.setCuid(gdo.getCuid());
				system.setObjectNum(gdo.getObjectNum());
				system.setAttrValue(HangWall.AttrName.labelCn,gdo.getAttrString(HangWall.AttrName.labelCn));
				deletedSystemList.add(system);
			}
	        if (deletedSystemList != null && deletedSystemList.size() > 0) {
	        	 try {
	        		 getDuctManagerBO().deleteLocatedSystems(context, deletedSystemList, isDeletePoint);
				} catch (Exception e) {
					logger.error("删除系统出错",e);
					throw new UserException(e.getMessage());
				}
	        	
	        	if(cuids.get(0).startsWith("WIRE_SYSTEM")){
		        	new Thread(new Runnable(){
						@Override
						public void run() {
							updateMakeFlag(cuids);
						}
		        		
		        	}).start();
	        	}
	        }
	    }
		 
		 /**
		  * 判断光缆段是否有光路
		  * @param wireSystemCuids
		  * @return
		  * @throws Exception
		  * @author gaoxf at 2014年4月1日 下午4:06:12
		  */
		 public boolean isExistOpticalWay(List<String> wireSystemCuids) throws Exception{
				if(wireSystemCuids==null || wireSystemCuids.size()==0){
					 return false;
				}
				DataObjectList wireSegList=new DataObjectList();
				
				for(String cuid:wireSystemCuids){
					 DataObjectList tempWireSegs=new DataObjectList();
					 tempWireSegs=(DataObjectList)BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegsByWireSystemCuid,new BoActionContext(),cuid);
					 
					 if(tempWireSegs!=null && tempWireSegs.size()>0){
						 wireSegList.addAll(tempWireSegs);
					 }
				}		

		        DataObjectList opticalWays =(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IOpticalWayBOX.getOpticalWayByWireSegs",new BoActionContext(),wireSegList);
		        if(opticalWays!=null && opticalWays.size()>0){
		        	return true;
		             
		        }
		       return false;
			}
		 
	 /**
	  * 根据系统CUID更新核查状态
	  * @param wireSystemCuids
	  * @return
	  * @throws Exception
	  * @author gaoxf at 2014年4月1日 下午3:57:40
	  */
	 private boolean updateMakeFlag(List<String> wireSystemCuids){
		 try{
			//判断光缆段是否有光路,如果存在光路那么修改关联的光路和光纤的核查状态  
			if(wireSystemCuids==null || wireSystemCuids.size()==0){
				 return false;
			}
			DataObjectList wireSegList=new DataObjectList();
			
			for(String cuid:wireSystemCuids){
				 DataObjectList tempWireSegs=new DataObjectList();
				 tempWireSegs=(DataObjectList)BoCmdFactory.getInstance().execBoCmd(WireSegBOHelper.ActionName.getWireSegsByWireSystemCuid,new BoActionContext(),cuid);
				 
				 if(tempWireSegs!=null && tempWireSegs.size()>0){
					 wireSegList.addAll(tempWireSegs);
				 }
			}		

	        DataObjectList opticalWays=new DataObjectList();
	        if(wireSegList.size() == 0){
	        	return true;
	        }
	        opticalWays=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IOpticalWayBOX.getOpticalWayByWireSegs",new BoActionContext(),wireSegList);
	        updateOpticalWayMakeFlag(opticalWays);
	        
	        updateOpticalMakeFlag(wireSegList);
		 }catch(Exception e){
			 logger.error("更新核查状态出错",e);
		 }
	     return true;
	}
		 
	public void updateOpticalWayMakeFlag(DataObjectList opticalWays){
		 try {
			if(opticalWays!=null && opticalWays.size()>0){
			    	//修改光路的核查状态
			     for(GenericDO opticalWay:opticalWays){
			     	opticalWay.setAttrValue(OpticalWay.AttrName.makeFlag, 1L);
			     }
			     getDuctManagerBO().updateDbos(new BoActionContext(), opticalWays);
			 }
		} catch (Exception e) {
			 logger.error("更新核查状态出错",e);
			 throw new UserException("更新光路核查状态出错！");
		}
	}
	 
	private void updateOpticalMakeFlag(DataObjectList wireSegList) throws Exception{
		 //修改光纤的核查状态
        try {
			DataObjectList opticals=new DataObjectList();
     
			opticals=(DataObjectList)BoCmdFactory.getInstance().execBoCmd("IOpticalBOX.getOpticalByWireSegs",new BoActionContext(),wireSegList);
			
			for(GenericDO optical:opticals){
				optical.setAttrValue(Optical.AttrName.makeFlag, 1L);
			}
			getDuctManagerBO().updateDbos(new BoActionContext(), opticals);
		} catch (Exception e) {
			logger.error("更新光纤核查状态出错",e);
			throw new UserException("更新光纤核查状态出错！");
		}
	}
	
	@SuppressWarnings("all")
	/**
	 * 判断光缆系统下有光路
	 * @param wireSystemCuid
	 * @return
	 * @throws Exception
	 */
	public List<Map> isHaveOpticalWayByWireSystemCuid(String wireSystemCuid) throws Exception{
		List<Map> opticalWayList = new ArrayList<Map>();
		try {
			String actionName = "IOpticalWayBO.getOWBySql";
			String sql = " CUID IN (SELECT OPR.RELATED_SERVICE_CUID FROM OPTICAL_ROUTE OPR ,OPTICAL_ROUTE_TO_PATH OPRTP WHERE OPR.CUID = OPRTP.OPTICAL_ROUTE_CUID AND PATH_CUID IN (SELECT OPTICAL_CUID FROM OPTICAL_TO_FIBER WHERE WIRE_SYSTEM_CUID = '"+wireSystemCuid+"'))";
			DboCollection dbos = (DboCollection) BoCmdFactory.getInstance().execBoCmd(actionName, new BoQueryContext(), sql);
			if(dbos != null && dbos.size()>0){
				 DataObjectList lst = new DataObjectList();
				 TopoHelper.putDboCollectionToList(dbos, lst, OpticalWay.CLASS_NAME);
				 
				 if(lst.size()>0){
					 for(GenericDO opticalWay : lst){
						 Map opticalWayMap = new HashMap();
						 String cuid = opticalWay.getCuid();
						 String labelCn = opticalWay.getAttrString(Optical.AttrName.labelCn);
						 opticalWayMap.put("CUID", cuid);
						 opticalWayMap.put("LABEL_CN", labelCn);
						 opticalWayList.add(opticalWayMap);
					 }
				 }
			}
		} catch (Exception e) {
			logger.error("根据光缆查询光路出错！",e);
			throw new UserException("根据光缆查询光路出错！");
		}
		return opticalWayList;
	}
	
	@SuppressWarnings("all")
	/**
	 * 根据相关id得到光路
	 * @param cuids
	 * cuids包括：光缆系统id、光缆段id、纤芯id
	 * 此方法用于前台提示有光路
	 * @return
	 * @throws Exception 
	 */
	public List<Map> getOpticalWayByWireSegCuids(List<String>cuids) throws Exception{
		DataObjectList allList = new DataObjectList();
		List<Map> opticalWayList = new ArrayList<Map>();
		try {
			for(String cuid : cuids){
				DataObjectList opticalways = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.getOpticalWayByCuid", new BoActionContext(), cuid);
//				DataObjectList opticalways = getWireSystemBO().getOpticalWayByCuid(new BoActionContext(), cuid);
				if(opticalways != null && opticalways.size()>0){
					for(GenericDO opticalWay : opticalways){
						String oid = opticalWay.getCuid();
						if(!allList.getCuidList().contains(oid)){
							allList.add(opticalWay);
						}
					}
				}
			}
			
			if(allList.size()>0){
				 for(GenericDO opticalWay : allList){
					 Map opticalWayMap = new HashMap();
					 String cuid = opticalWay.getCuid();
					 String labelCn = opticalWay.getAttrString(Optical.AttrName.labelCn);
					 opticalWayMap.put("CUID", cuid);
					 opticalWayMap.put("LABEL_CN", labelCn);
					 opticalWayList.add(opticalWayMap);
				 }
			 }
		} catch (Exception e) {
			logger.error("得到光路出错！");
			throw new UserException(e.getMessage());
		}
		return opticalWayList;
	}
	
	@SuppressWarnings("all")
	/**
	 * 删除系统资源
	 * @param systemCuid
	 */
	public void deleteSystemForFlow(List<String> cuids){
		try {
		    BoActionContext actionContext = ActionContextUtil.getActionContext();
	        logger.debug("context:::deleteSystemForFlow:::"+actionContext.getUserId()+"======"+actionContext.getUserName());
			if(cuids  != null && cuids.size()>0){
				String cuid = cuids.get(0);
				if(cuid.startsWith(WireSystem.CLASS_NAME)){
					removeWireSystem(actionContext,cuids);
				}else if(cuid.startsWith(WireSeg.CLASS_NAME)){
					removeWireSegs(actionContext, cuids);
				}else if(cuid.startsWith(Fiber.CLASS_NAME)){
					removeFibers(actionContext, cuids);
				}else if(cuid.startsWith(Optical.CLASS_NAME)){
					removeOpticals(actionContext, cuids);
				}
			}
			
		} catch (Exception e) {
			logger.error("删除数据出错！",e);
			throw new UserException(e.getMessage());
		}
	}
	
	private void removeWireSystem(BoActionContext actionContext,List<String> systemCuids) throws Exception{
		try {
			for(String systemCuid : systemCuids){
				WireSystem wiresystem = (WireSystem) BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.getWireSystemByCuid", actionContext, systemCuid);
//				WireSystem wiresystem = getWireSystemBO().getWireSystemByCuid(actionContext, systemCuid);
				//BoActionContext如果在designer模块中要记录操作人此处需要注意
				if(wiresystem != null){
					BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.deleteSystemForFlow", actionContext, wiresystem);
//					getWireSystemBO().deleteSystemForFlow(actionContext,wiresystem);
				}
			}
		} catch (Exception e) {
			logger.error("删除系统出错！",e);
			throw new UserException(e.getMessage());
		}
	}
	
	private void removeWireSegs(BoActionContext actionContext,List<String> segCuids) throws Exception{
		try {
			DataObjectList segList = new DataObjectList();
			for(String segCuid : segCuids){
				WireSeg wireseg = (WireSeg) BoCmdFactory.getInstance().execBoCmd("IWireSegBO.getWireSegByCuid", actionContext, segCuid);
//				WireSeg wireseg = getWireSegBO().getWireSegByCuid(actionContext, segCuid);
				//BoActionContext如果在designer模块中要记录操作人此处需要注意
				if(wireseg != null){
					segList.add(wireseg);
				}
			}
			if(segList.size()>0){
				BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.deleteSegsForFlow", new BoActionContext(), segList);
//				getWireSystemBO().deleteSegsForFlow(actionContext, segList);
			}
		} catch (Exception e) {
			logger.error("删除光缆段出错！",e);
			throw new UserException(e.getMessage());
		}
	}
	
	private void removeFibers(BoActionContext actionContext,List<String> fiberCuids) throws Exception{
		try {
			DataObjectList fiberList = new DataObjectList();
			for(String fiberCuid : fiberCuids){
				Fiber fiber = (Fiber) BoCmdFactory.getInstance().execBoCmd("IFiberBO.getfiberByCuid", new BoActionContext(), fiberCuid);
//				Fiber fiber = getFiberBO().getfiberByCuid(actionContext, fiberCuid);
				//BoActionContext如果在designer模块中要记录操作人此处需要注意
				if(fiber != null){
					fiberList.add(fiber);
				}
			}
			if(fiberList.size()>0){
				BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.deleteFibersForFlow", new BoActionContext(), fiberList);
//				getWireSystemBO().deleteFibersForFlow(actionContext, fiberList);
				//管线所属光缆段纤芯数
				Fiber fiber = (Fiber)fiberList.get(0);
				WireSeg wireseg = (WireSeg) BoCmdFactory.getInstance().execBoCmd("IWireSegBO.getWireSegByCuid", new BoActionContext(), fiber.getRelatedSegCuid());
				long fibercount = wireseg.getFiberCount();
				wireseg.setFiberCount(fibercount-fiberList.size());
				DataObjectList segList = new DataObjectList();	
				segList.add(wireseg);
				BoCmdFactory.getInstance().execBoCmd("IWireSegBO.modifyWireSegs",new BoActionContext(),segList);
			}
		} catch (Exception e) {
			logger.error("删除纤芯出错！",e);
			throw new UserException(e.getMessage());
		}
	}
	
	private void removeOpticals(BoActionContext actionContext,List<String> opticalCuids) throws Exception{
		try {
			DataObjectList opticalList = new DataObjectList();
			for(String opticalCuid : opticalCuids){
				Optical optical = (Optical) BoCmdFactory.getInstance().execBoCmd("IOpticalBO.getOpticalByCUid", new BoActionContext(), opticalCuid);
//				Optical optical = getOpticalBO().getOpticalByCUid(actionContext, opticalCuid);
				//BoActionContext如果在designer模块中要记录操作人此处需要注意
				if(optical != null){
					opticalList.add(optical);
				}
			}
			if(opticalList.size()>0){
				BoCmdFactory.getInstance().execBoCmd("IWireSystemBO.deleteOpticalsForFlow", new BoActionContext(), opticalList);
//				getWireSystemBO().deleteOpticalsForFlow(actionContext, opticalList);
			}
		} catch (Exception e) {
			logger.error("删除纤芯出错！",e);
			throw new UserException(e.getMessage());
		}
	}
}