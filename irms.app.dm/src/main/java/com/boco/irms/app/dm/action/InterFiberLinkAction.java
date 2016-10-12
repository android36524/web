package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.Miscrack;
import com.boco.transnms.common.dto.Odf;
import com.boco.transnms.common.dto.Odfmodule;
import com.boco.transnms.common.dto.Odfport;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.equip.PhyOdfBO;
import com.boco.transnms.server.bo.helper.dm.FiberBOHelper;
import com.boco.transnms.server.bo.ibo.cm.IDistrictBO;
import com.boco.transnms.server.bo.ibo.cm.IInterWireBO;
import com.boco.transnms.server.bo.ibo.cm.IMiscRackBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfPortBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfmoduleBOX;
import com.boco.transnms.server.bo.ibo.cm.IRoomBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;

public class InterFiberLinkAction {
	public List getMsgChildrenByParentCuid(String parentCuid) {
		if (parentCuid.startsWith(Room.CLASS_NAME)) {
			return getinterWireChildren(parentCuid);
		} else if (parentCuid.startsWith(InterWire.CLASS_NAME)) {
			return getwireFiberChildren(parentCuid);
			}
//		}else if (parentCuid.startsWith(WireSeg.CLASS_NAME)) {
//		    return getwireFiberseg(parentCuid);
		else if (parentCuid.startsWith(Fiber.CLASS_NAME)) {
			return getFiberPortChildren(parentCuid);
		}
	
		return null;
	}

	private List getinterWireChildren(String parentCuid) {
		DataObjectList wireSeg = new DataObjectList();
		InterWire iwire=null;
//	InterWire owire=null;
				
		try {
		DboCollection wire=BoHomeFactory.getInstance().getBO(IInterWireBO.class).getInterWireBySql(new BoQueryContext(), "orig_point_cuid='"+parentCuid+"'");
		DboCollection wires=BoHomeFactory.getInstance().getBO(IInterWireBO.class).getInterWireBySql(new BoQueryContext(), "dest_point_cuid='"+parentCuid+"'");
//		List list = wire.getResultSet();
	    for(int i = 0; i < wire.size(); i++)
		{
			iwire = (InterWire) wire.getAttrField(InterWire.CLASS_NAME, i);
			wireSeg.add(iwire);
		}
	    for(int j = 0; j < wires.size(); j++)
		{
	    	iwire = (InterWire) wires.getAttrField(InterWire.CLASS_NAME, j);
	    	wireSeg.add(iwire);
		}
		
		
		} catch (Exception e) {
			LogHome.getLog().error(e);
		}
		return converToList(wireSeg);
	}


	private List getwireFiberChildren(String parentCuid) {
		DataObjectList wires=null;
		try{
			wires=BoHomeFactory.getInstance().getBO(IFiberBO.class).getInterWireSimpleFiber(new BoActionContext(), parentCuid);
		}
		catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(wires);
	}
	private List getFiberPortChildren(String portCuid) {
		IDistrictBO districtBO = BoHomeFactory.getInstance().getBO(IDistrictBO.class);
		GenericDO parent = districtBO.getObjByCuid(new BoActionContext(),portCuid);

		if (parent == null) {
			parent = new GenericDO();
			parent.setCuid(portCuid);
		}
		DataObjectList fibers = null;
		DataObjectList fibersPorts = null;
		if (parent.getAttrLong(Fiber.AttrName.usageState) == 1|| parent.getAttrLong(Fiber.AttrName.usageState) == 2) {
			try {
				fibers = BoHomeFactory.getInstance().getBO(IFiberBO.class).getFiberBySql(new BoActionContext(),"CUID='" + portCuid + "'");
				if (fibers != null && fibers.size() > 0) {
					GenericDO join = (GenericDO) fibers.get(0);
					String joinOFiber = join.getAttrString("ORIG_POINT_CUID");
					String joinDFiber = join.getAttrString("DEST_POINT_CUID");
					fibers = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getOdfportBySql(new BoActionContext(),"CUID='" + joinOFiber + "'");
					DataObjectList joinFiberd = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getOdfportBySql(new BoActionContext(),"CUID='" + joinDFiber + "'");
					fibers.addAll(joinFiberd);
				}

			} catch (Exception e) {
				LogHome.getLog().info(e);
			}
		}
		return converToList(fibers);
	}
/**
 * 根据楼内光缆查询A端机房
 */
	public String  queryARoom(String Intercuid){
		
		InterWire InterWire=BoHomeFactory.getInstance().getBO(IInterWireBO.class).getInterWireByCuid(new BoActionContext(),Intercuid );
		String RoomId=InterWire.getOrigPointCuid();
	    return RoomId;
	  }
	/**
	 * 根据楼内光缆查询Z端机房
	 */
		public String queryZRoom(String Intercuid){
			InterWire InterWire=BoHomeFactory.getInstance().getBO(IInterWireBO.class).getInterWireByCuid(new BoActionContext(),Intercuid);
			String RoomId=InterWire.getDestPointCuid();
			return RoomId;
		  }
	/**
	 * 纤芯关联操作
	 * @throws Throwable 
	 * @throws UserException 
	 */
	  //纤芯关联
		public List queryFiberLink(String azPortCuid,String fiberCuid) throws UserException, Throwable {
			DataObjectList retList = null;
			Fcabport fcab = new Fcabport();
			DataObjectList fiberPort = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getOdfportBySql(new BoActionContext(), "CUID='" + azPortCuid + "'");
			DataObjectList fiber = BoHomeFactory.getInstance().getBO(IFiberBO.class).getFiberBySql(new BoActionContext(), "CUID='" + fiberCuid + "'");
			try {
				retList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.addFibersConnect,new BoActionContext(), fiber,fiberPort);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				Map result = new HashMap();
				result.put("ERROR", "保存失败:" + e.getMessage());
				List a=new ArrayList();
				a.add(result);
				return a;
			}
			return converToList(retList);
		}
	
		 /**
		    * 断开纤芯
		    * @param parentCuid
		    * @return
		 * @throws Exception 
		 * @throws UserException 
		    */
		
		public Map deleteFiberLink(String azPortCuid,String fiberCuid) throws UserException, Exception {
			HashMap<GenericDO,DataObjectList> deletedMap=new HashMap<GenericDO, DataObjectList>();
			DataObjectList retList = null;
			GenericDO fcab = new GenericDO();
			GenericDO fibers=new GenericDO();
			DataObjectList fiberPort = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getOdfportBySql(new BoActionContext(), "CUID='" + azPortCuid + "'");
			Fiber fiber = BoHomeFactory.getInstance().getBO(IFiberBO.class).getfiberByCuid(new BoActionContext(),  fiberCuid );
			deletedMap.put(fiber, fiberPort);
			
			
			try {
				 BoCmdFactory.getInstance().execBoCmd(FiberBOHelper.ActionName.deleteFibersConnect,new BoActionContext(), deletedMap);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				Map result = new HashMap();
				result.put("ERROR", "断开失败:" + e.getMessage());
				
				return result;
			}
			return new HashMap();
		}
	
	
	/**
	 * 获得子节点信息
	 */
	public  List getChildrenByParentCuid(String parentCuid) {
		if (parentCuid.startsWith(Room.CLASS_NAME)) {
            return getRoomChildren(parentCuid);
        }
		else if(parentCuid.startsWith(Odfmodule.CLASS_NAME))
		{
			return getOdfPortChildren(parentCuid);
		}
		else if (parentCuid.startsWith(Odf.CLASS_NAME)) {
            return getOdfChildren(parentCuid);
        }
		else if (parentCuid.startsWith(Miscrack.CLASS_NAME)) {
            return getrackMouldChildren(parentCuid);
        }
		
//		else if(parentCuid.startsWith(FiberDpPort.CLASS_NAME))
//		{
//			return getFportChildren(parentCuid);
//		}
		return null;
	}
	private List getRoomChildren(String roomCuid)
	{
		DataObjectList odfs =  null;
	
		String OdmId=null;
		
		try{
		odfs = BoHomeFactory.getInstance().getBO(IPhyOdfBO.class).getODFByRoomCuid(new BoActionContext(), roomCuid);
		DataObjectList racks=BoHomeFactory.getInstance().getBO(IMiscRackBO.class).getMiscRackByCondition(new BoActionContext(), roomCuid);
		DataObjectList fiberJointBoxs=BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class).getFiberJointBoxBySql(new BoActionContext(), "related_room_cuid='"+roomCuid+"'");
		odfs.addAll(racks);
		odfs.addAll(fiberJointBoxs);
		}catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(odfs);
	}
	private List getOdfChildren(String odfId)
	{
		DataObjectList Odm = null;
		DataObjectList odfPort=null;
		String odmId=null;
		try{
			Odm = BoHomeFactory.getInstance().getBO(IPhyOdfmoduleBOX.class).getODMByODFCuid(new BoActionContext(), odfId);
		
		 }catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(Odm);
	}
	private List getrackMouldChildren(String rackCuid)
	{
		
		DataObjectList fiberdpports = null;
		try{
			fiberdpports=BoHomeFactory.getInstance().getBO(IPhyOdfmoduleBOX.class).getODMByMiscRackCuid(new BoActionContext(), rackCuid);
		}catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(fiberdpports);
	}
	private List getOdfPortChildren(String modId)
	{
	   DataObjectList ports = null;
try {
	ports=BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getODFPortByODMCuid(new BoActionContext(), modId); 
		      } catch (Exception e) {
		          LogHome.getLog().info(e);
		     
		}
		return converToList(ports);
	}
	private List converToList(DataObjectList list)
	{
		List result = new ArrayList();
		if(list != null && list.size()>0)
		{
			for(int i=0;i<list.size();i++)
			{
				GenericDO child = list.get(i);
				result.add(converToMap(child));
			}
		}
		return result;
	}
	/**
	 *将GenericDO转换为Map
	 */
	private Map converToMap(GenericDO child){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CUID", child.getCuid());
		map.put("LABEL_CN", child.getAttrValue("LABEL_CN"));
		String icon = "";
		if(child instanceof Fibercabmodule)
		{
			icon = "/resources/topo/dm/Ddfmodule.gif";
		}
		if(child instanceof Odfmodule)
		{
			icon = "/resources/topo/dm/Ddfmodule.gif";
		}
		if(child instanceof Fiber){
			icon = "/resources/topo/dm/isfixed.gif";
		}
		if(child instanceof InterWire)
		{
			icon = "/resources/topo/dm/HangWall.gif";
		}
		
		if(child instanceof Fcabport){
			Fcabport port = (Fcabport)child;
			icon = "/resources/topo/alarm/c.png";
	        if (port.getIsConnectedToFiber()) {
	        	icon = "/resources/topo/alarm/w.png";
	        }
	        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
	        	icon = "/resources/topo/alarm/m.png";
	        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
	        	icon = "/resources/topo/alarm/StopSound.png";
	        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
	        	icon = "/resources/topo/alarm/u.png";
	        }
	        long portRow=port.getNumInMrow();
	        long portCol=port.getNumInMcol();
	        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
		}
		if(child instanceof FiberDpPort){
			FiberDpPort port = (FiberDpPort)child;
			icon = "/resources/topo/alarm/c.png";
	        if (port.getIsConnectedToFiber()) {
	        	icon = "/resources/topo/alarm/w.png";
	        }
	        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
	        	icon = "/resources/topo/alarm/m.png";
	        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
	        	icon = "/resources/topo/alarm/StopSound.png";
	        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
	        	icon = "/resources/topo/alarm/u.png";
	        }
	        long portRow=port.getNumInMrow();
	        long portCol=port.getNumInMcol();
	        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
		}
		if(child instanceof Odfport){
			Odfport port = (Odfport)child;
			icon = "/resources/topo/alarm/c.png";
	        if (port.getIsConnectedToFiber()) {
	        	icon = "/resources/topo/alarm/w.png";
	        }
	        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
	        	icon = "/resources/topo/alarm/m.png";
	        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
	        	icon = "/resources/topo/alarm/StopSound.png";
	        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
	        	icon = "/resources/topo/alarm/u.png";
	        }
	        long portRow=port.getNumInMrow();
	        long portCol=port.getNumInMcol();
	        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
		}
		if(child instanceof JumpFiber)
		{
			JumpFiber jf = (JumpFiber)child;
            if ((Boolean) child.getAttrValue(JumpFiber.AttrName.isFixed)) {
                icon =  "/resources/topo/dm/fixed.png";
            } else {
                icon =  "/resources/topo/dm/isfixed.gif";
            }
            map.put("ORIG_POINT_CUID", jf.getOrigPointCuid());
            map.put("DEST_POINT_CUID", jf.getDestPointCuid());
		}
		map.put("ICON", icon);
		map.put("BM_CLASS_ID",child.getBmClassId());
		
		return map;
	}
}
