package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.utils.exception.UserException;
import com.boco.gis.rest.DmDesignerTools;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.LinkPort;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.cm.JumpFiberBOHelper;
import com.boco.transnms.server.bo.ibo.cm.IDistrictBO;
import com.boco.transnms.server.bo.ibo.cm.IJumpFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFcabportBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpPortBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointPointBO;
import com.boco.transnms.server.bo.ibo.dm.IFibercabmoduleBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;

/**
 * @author Administrator
 * 跳纤管理
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class FiberManagerAction {
	private static Map<String,String> DEVICE_TYPE = new HashMap<String, String>();
	private static Map<String,String> ICON_MAP = new HashMap<String, String>();
	
	static {
		DEVICE_TYPE.put("ODF", "机架");
		DEVICE_TYPE.put("FIBER_CAB", "交接箱");
		DEVICE_TYPE.put("FIBER_DP", "分纤箱");
		DEVICE_TYPE.put("FIBER_JOINT_BOX", "接头盒");
		
		ICON_MAP.put("光缆", "/resources/map/WIRE_SEG.png");
		ICON_MAP.put("纤芯", "/resources/topo/dm/isfixed.gif");
	}
	
	/**
	 * 建立跳纤关系
	 */
	public List jumpFiberAdd(HttpServletRequest request,String aPortCuid,String zPortCuid,String labelCn,String isFixed){
		//跳纤
		List jumps = new ArrayList();
		DataObjectList jumpFibers = new DataObjectList();
		String aPortCuids [] = aPortCuid.split(",");
		String zPortCuids [] = zPortCuid.split(",");
		String labelCns [] = labelCn.split(",");
		for(int i = 0; i < aPortCuids.length; i++)
		{
			JumpFiber inJumpFiber=new JumpFiber();
			inJumpFiber.setOrigPointCuid(aPortCuids[i]+"");
	    	inJumpFiber.setDestPointCuid(zPortCuids[i]+"");
	    	inJumpFiber.setCuid();
	    	inJumpFiber.setLabelCn(labelCns[i]+"");
	    	boolean varIsFixed=false;
	    	if(isFixed.equals("1")){
	    		varIsFixed=true;
	    	}
	    	inJumpFiber.setIsFixed(varIsFixed);
	    	
	    	
	    	jumpFibers.add(inJumpFiber);
		}
		
    	
    	
    	try{
    		DataObjectList jumpfibers = null;
    		ServiceActionContext ac = new ServiceActionContext(request);
    		BoActionContext boContext = new BoActionContext();
    		String userCuid = ac.getUserCuid();
    		String userid = ac.getUserId();
    		boContext.setUserId(userCuid);
    		boContext.setUserName(userid);
    		jumpfibers =	BoHomeFactory.getInstance().getBO(IJumpFiberBO.class).addJumpFibers(boContext, jumpFibers);
    		for(int i = 0; i < jumpfibers.size(); i++)
    		{
    			GenericDO dbo = jumpfibers.get(i);
    			jumps.add(converToMap(dbo));
    		}
    	}catch(Exception e){
    		List result = new ArrayList();
    		result.add("ERROR");
    		result.add("保存跳纤失败:"+e.getMessage());
    		return result;
    	}
    	
    	
    	return jumps;
	}
	/**
	 * 删除跳纤关系
	 */
	public List jumpFiberDel(HttpServletRequest request,String jumpFiberCuid){
		DataObjectList jumps = new DataObjectList();
		String [] jumpFiberCuids = jumpFiberCuid.split(",");
		for(int i = 0; i < jumpFiberCuids.length;i++)
		{
			JumpFiber jump = new JumpFiber();
			jump.setCuid(jumpFiberCuids[i]);
			jumps.add(jump);
		}
		 
		try{
			ServiceActionContext ac = new ServiceActionContext(request);
    		BoActionContext boContext = new BoActionContext();
    		String userCuid = ac.getUserCuid();
    		String userid = ac.getUserId();
    		boContext.setUserId(userCuid);
    		boContext.setUserName(userid);
			BoHomeFactory.getInstance().getBO(IJumpFiberBO.class).deleteJumpFibers(boContext, jumps);
		}
		catch(Exception e){
			List result = new ArrayList();
    		result.add("ERROR");
    		result.add("删除跳线失败:"+e.getMessage());
    		return result;
		}
		return new ArrayList();
	}
	
	/**
	 * 判断跳纤是否被光路使用
	 * @param jumpfiberCuid
	 * @return
	 */
	public boolean isJumpFiberInOpticalwayRoute(String jumpfiberCuid){ 
		String [] jumpFiberCuids = jumpfiberCuid.split(",");
		String cuids = "";
		for(String cuid : jumpFiberCuids)
		{
			cuids +=",'"+cuid+"'";
		}
    	String actionName = "IOpticalWayBO.getOWBySql";
    	String sql = " CUID IN (SELECT RELATED_TRAPH_CUID FROM DFPORT_TO_TRAPH WHERE DF_PORT_CUID IN "
    			+ "(SELECT ORIG_POINT_CUID POINT_CUID FROM JUMP_FIBER WHERE CUID IN ("+cuids.substring(1)+") "
    					+ "UNION SELECT DEST_POINT_CUID POINT_CUID FROM JUMP_FIBER WHERE CUID IN ("+cuids.substring(1)+")))";
    	try {
			DboCollection dbos = (DboCollection) BoCmdFactory.getInstance().execBoCmd(actionName, new BoQueryContext(), sql);
			if(dbos != null && dbos.size() > 0){
				return true;
			}
		} catch (Exception e) {
			LogHome.getLog().error("根据跳纤查询光路出错",e);
		}
    	return false;
	}
	
	/**
	 * 根据光分纤箱cuid查询关联的光缆信息（光缆+光缆段）
	 * 
	 * @param fiberDpCuid 光分纤箱cuid
	 */
	public List getWireInfoByFiberDp(String fiberDpCuid) throws Exception{
		if (fiberDpCuid == null || fiberDpCuid.trim().length() == 0){
			return null;	
		}
		HashMap wireSystemMap = null;
		IDuctManagerBO IDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		GenericDO gdo = IDuctManBO.getObjByCuid(new BoActionContext(), fiberDpCuid);
		if(gdo!=null){
			String className = gdo.getClassName();
			String relatedSiteCuid = "";
			if(className.equals(FiberDp.CLASS_NAME)){
				relatedSiteCuid = (String) gdo.getAttrValue(FiberDp.AttrName.relatedSiteCuid);
			}else if(className.equals(FiberJointBox.CLASS_NAME)){
				relatedSiteCuid = (String) gdo.getAttrValue(FiberJointBox.AttrName.relatedLocationCuid);
				String type = relatedSiteCuid.split("-")[0];
				if(type.equals(Room.CLASS_NAME)){
					GenericDO room = IDuctManBO.getObjByCuid(new BoActionContext(), relatedSiteCuid);
					if(room != null){
						relatedSiteCuid = room.getAttrString(Room.AttrName.relatedSiteCuid);
					}
				}
			}
			DataObjectList wireSegsBySql = getWireSegsBySql(new BoActionContext(),relatedSiteCuid);
			wireSystemMap = getWireSystemMap(new BoActionContext(),wireSegsBySql);
		}
		
		String name = fiberDpCuid.split("-")[0];
		IWireSystemBO ibo = BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
		IFiberJointBoxBO iFjb = BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class);
		HashMap<WireSystem,DataObjectList> dataList = new HashMap<WireSystem,DataObjectList>();
		if(name.equals(FiberJointBox.CLASS_NAME )){
			 FiberJointBox fiberJointBox = iFjb.getFiberJointBoxByCuid(new BoActionContext(), fiberDpCuid);
			 String locationCuid = fiberJointBox.getAttrString(FiberJointBox.AttrName.relatedLocationCuid);			 
			 dataList = ibo.getWireSyetemByDevCuid(new BoActionContext(), locationCuid);
			 //update by YangNengjie at 2016-08-29: 修复接头盒纤芯关联时查询不到纤芯
			 dataList.putAll(ibo.getWireSyetemByDevCuid(new BoActionContext(), fiberDpCuid));
		}else{
			 dataList = ibo.getWireSyetemByDevCuid(new BoActionContext(), fiberDpCuid);
		}
		
		if(wireSystemMap!=null){
			dataList.putAll(wireSystemMap);
		}
		if (dataList == null || dataList.size() == 0) {
			List result = new ArrayList();
    		result.add("调用方法失败，没有查到光缆数据");
    		return result;
//			return null;
		}
		String icon = ICON_MAP.get("光缆");
		// 光缆集合
		List<Map<String, Object>> wireSystemList = new ArrayList<Map<String, Object>>();
		// 光缆段集合
		List<Map<String, Object>> wireSegList = new ArrayList<Map<String, Object>>();

		for(Map.Entry<WireSystem, DataObjectList> entry : dataList.entrySet()) {
			WireSystem ws = entry.getKey();
			boolean isAdd = false;
			for (int i = 0; i < wireSystemList.size(); i++ ){
				Map<String,Object> sys = wireSystemList.get(i);
				if (sys.get("CUID").equals(ws.getCuid())){
					isAdd = true;
					break;
				}
			}
			
			if (isAdd == false) {
				Map attr = ws.getAllAttr();
				attr.put("ICON", icon);
				wireSystemList.add(attr);
			}

			List segList = entry.getValue();
			if (segList == null || segList.size() ==0 ) {
				continue;
			}
			for(Object item : segList) {
				WireSeg seg = (WireSeg) item;
				Map attr = seg.getAllAttr();
				attr.put("ICON", icon);
				wireSegList.add(attr);
			}
		}

		List result = new ArrayList();
		result.add(wireSystemList);
		result.add(wireSegList);

		return result;
	}

	// 通过光缆段查询光缆系统 by wangqin
	private HashMap getWireSystemMap(BoActionContext actionContext, DataObjectList wireSegs) throws Exception {
		IWireSystemBO bo = BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
		HashMap<WireSystem, DataObjectList> wireSystemMap = new HashMap<WireSystem, DataObjectList>();
		if (wireSegs != null && wireSegs.size() > 0) {
			for (int i = 0; i < wireSegs.size(); i++) {
				WireSeg wireSeg = (WireSeg) wireSegs.get(i);
				WireSystem wireSystem = bo.getWireSystemByCuid(actionContext, wireSeg.getRelatedSystemCuid());
				if (wireSeg.getRelatedSystemCuid() != null && wireSystemMap.get(wireSystem) != null) {
					DataObjectList relatedWireSegs = (DataObjectList) wireSystemMap.get(wireSystem);
					if (relatedWireSegs.size() == 0) {
						relatedWireSegs.add(wireSeg);
					} else {
						boolean canAdd = true;
						for (int j = 0; j < relatedWireSegs.size(); j++) {
							WireSeg seg = (WireSeg) relatedWireSegs.get(j);
							if (seg.getCuid().equals(wireSeg.getCuid())) {
								canAdd = false;
								break;
							}
						}
						if (canAdd) {
							relatedWireSegs.add(wireSeg);
						}
					}
				} else {
					DataObjectList relatedWireSegs = new DataObjectList();
					relatedWireSegs.add(wireSeg);
					wireSystemMap.put(wireSystem, relatedWireSegs);
				}
			}
		}
		return wireSystemMap;
	}
	//通过起始点查询光缆段 by liuyumiao
	private DataObjectList getWireSegsBySql(BoActionContext actionContext, String pointCuid) throws Exception {
		DataObjectList wireSegdev = null; 
	    String sql = "("+WireSeg.AttrName.origPointCuid + "='" + pointCuid + "' or " + WireSeg.AttrName.destPointCuid + " ='" + pointCuid + "')"; 
	    IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	    wireSegdev = getDuctManBO.getObjectsBySql(sql, new WireSeg());
	    return wireSegdev;    
	}
	
	/**
	 * 根据光缆段查找相关的纤芯
	 * 
	 * @param segCuid 光缆段cuid
	 */
	public List getFiberByWireSeg(String segCuid) throws Exception{
		if (StringUtils.isBlank(segCuid)) {
			return null;
		}
		IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		DataObjectList fiberList = ibo.getFibersByWireSegCuid(new BoActionContext(), segCuid);
		if (fiberList == null || fiberList.size() == 0) {
			return null;
		}
		
		List result = new ArrayList();
		
		String icon = ICON_MAP.get("纤芯");
		for(int i=0;i<fiberList.size();i++)
		{
			GenericDO child = fiberList.get(i);
			Map attrMap = child.getAllAttr();
			attrMap.put("ICON", icon);
			result.add(attrMap);
		}
		//纤芯根据序号排序
		Collections.sort(result, new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				if(o1 instanceof Map && o2 instanceof Map)
				{
					Object no1 = ((Map) o1).get("WIRE_NO");
					Object no2 = ((Map) o2).get("WIRE_NO");
					if(no1 instanceof Long && no2 instanceof Long)
					{
						long l1= (Long) no1;
						long l2= (Long) no2;
						if(l1 > l2)
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
				}
				return 0;
			}
		});
		return result;
	}
	
	/**
	 * 根据纤芯查找端子
	 * 
	 * @param fiberCuid 纤芯cuid
	 */
	public List getSidePointsByFiberCuid(String fiberCuid) throws Exception {
		if (StringUtils.isBlank(fiberCuid)) {
			return null;
		}
		DataObjectList portList = _getSidePointsByFiberCuid(fiberCuid);

		if (portList == null || portList.size() == 0) {
			return null;
		}
		
		IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		Fiber fiber = ibo.getfiberByCuid(new BoActionContext(), fiberCuid);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (GenericDO dbo : portList) {
			LinkPort port = (LinkPort) dbo;
			String icon = "/resources/topo/alarm/c.png";
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
			
			port.setAttrValue("ICON", icon);
			GenericDO device = (GenericDO)port.getAttrValue("RELATED_DEVICE_CUID");
			String className = device.getClassName().split("-")[0];
			String typeName = getDeviceTypeLabel(className);
			if (port.getCuid().equals(fiber.getOrigPointCuid())) {
				port.setLabelCn("A端: " + port.getLabelCn() + "【" + typeName
						+ "：" + device.getAttrString("LABEL_CN") + "】");
			}
			if (port.getCuid().equals(fiber.getDestPointCuid())) {
				port.setLabelCn("Z端: " + port.getLabelCn() + "【" + typeName
						+ "：" + device.getAttrString("LABEL_CN") + "】");
			}

			result.add(port.getAllAttr());
		}

		return result;
	}
	
	private static String getDeviceTypeLabel(String className) {
		String typeName = DEVICE_TYPE.get(className);
		if (typeName == null) {
			typeName = "机架";
		}
		return typeName;
	}
	
	private DataObjectList _getSidePointsByFiberCuid(String fiberCuid) {
		IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		return ibo.getSidePointsByFiberCuid(new BoActionContext(), fiberCuid);
	}
	
	/**
	 * 纤芯关联---关联---操作处理
	 * 
	 * @param fiberCuids 以逗号分隔的纤芯cuid字符串
	 * @param portCuids 以逗号分隔的端子cuid字符串
	 * @return
	 */
	public List<String> addFibersConnect(HttpServletRequest request,String fiberCuids, String portCuids) throws Exception{
		if (StringUtils.isBlank(fiberCuids) || StringUtils.isBlank(portCuids)) {
			return new ArrayList();
		}
		IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		ServiceActionContext ac = new ServiceActionContext(request);
		BoActionContext context = new BoActionContext();
		String userCuid = ac.getUserCuid();
		String userid = ac.getUserId();
		context.setUserId(userCuid);
		context.setUserName(userid);
		List<String> result = new ArrayList<String>();
		try {
			List portlist=new ArrayList();
			String[] portcuids=portCuids.split(",");
			for(int i=0;i<portcuids.length;i++){
				portlist.add(portcuids[i]);
			}
			DataObjectList fibers = _getFiberByCuids(context, fiberCuids);
			DataObjectList ports = new DataObjectList();//_getFiberDpPortByCuid(context, portCuids);
			String portType = portcuids[0].split("-")[0];
			if(portType.equals(FiberDpPort.CLASS_NAME)){
				ports = _getFiberDpPortByCuid(context, portCuids);
			}else if(portType.equals(FiberJointPoint.CLASS_NAME)){
				ports = _getFiberJointPointByCuid(context, portCuids);
			}
			DataObjectList portsbyf = new DataObjectList();
			for(int i=0;i<fibers.size();i++){
				portsbyf.add((GenericDO) ports.getObjectByCuid((String) portlist.get(i)).get(0));
			}
			DataObjectList dataList = ibo.addFibersConnect(context, fibers,
					portsbyf);

			if (dataList == null || dataList.size() == 0) {
				return new ArrayList();
			}
			result.add(0, "SUCCESS");
			for (GenericDO dbo : dataList) {
				Fiber fiber = (Fiber) dbo;
				result.add(fiber.getCuid());
			}
		} catch (Exception ex) {
			result.add(0, ex.getMessage());
		}
		
		return result;
	}
	
	private DataObjectList _getFiberJointPointByCuid(BoActionContext context,String portCuids) {
		IFiberJointPointBO ibo = BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class);
		String sql = "cuid in ('" + portCuids.replace(",", "','") + "')";
		return ibo.getFiberJointPointBySql(context, sql);
	
	}
	private DataObjectList _getFiberDpPortByCuid(BoActionContext context, String portCuids) {
		IFiberDpPortBO ibo = BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class);
		String sql = "cuid in ('" + portCuids.replace(",", "','") + "')";
		return ibo.getFiberDpPortBySql(context, sql);
	}
	
	/**
	 * 查找纤芯
	 * 
	 * @param context
	 * @param fiberCuids 逗号分隔的纤芯cuid字符串，两头无逗号
	 * @return
	 */
	private DataObjectList _getFiberByCuids(BoActionContext context, String fiberCuids) {
		List<String> fiberCuidList = new ArrayList<String>();
		for (String cuid : fiberCuids.split(",")) {
			fiberCuidList.add(cuid);
		}
		IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		DataObjectList fibers = ibo.getfiberByCuids(context, fiberCuidList);
		
		return fibers;
	}
	
	/**
	 * 删除端子和纤芯的关系
	 * 
	 * @param fiberMap
	 * @return
	 * @throws Exception
	 */
	public Map deleteFibersConnect(HttpServletRequest request,Map<String, String> fiberMap) throws Exception{
		if (fiberMap == null || fiberMap.size() == 0) {
			return null;
		}
		HashMap params = new HashMap();
		IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
		ServiceActionContext ac = new ServiceActionContext(request);
		BoActionContext context = new BoActionContext();
		context.setUserId(ac.getUserCuid());
		context.setUserName(ac.getUserId());
		Map result = new HashMap();
		DataObjectList fiberPortsCL=new DataObjectList();
		try{
			for (Map.Entry<String, String> entry : fiberMap.entrySet()) {
				Fiber fiber = (Fiber) ibo.getfiberByCuid(context,entry.getKey());
				DataObjectList fiberPorts = new DataObjectList();
				String portType = entry.getValue().split("-")[0];
				if(portType.equals(FiberDpPort.CLASS_NAME)){
					fiberPorts = _getFiberDpPortByCuid(context,entry.getValue());
				}else if(portType.equals(FiberJointPoint.CLASS_NAME)){
					fiberPorts = _getFiberJointPointByCuid(context,entry.getValue());
				}
//				DataObjectList fiberPorts = _getFiberDpPortByCuid(context,entry.getValue());
				fiberPortsCL.addAll(fiberPorts);
				params.put(fiber, fiberPorts);
			}

			Map dataMap = ibo.deleteFibersConnect(context, params);
			//添加判断，如果是存量占用的端子，断开连接后状态任然是占用
			String[] portCuids = new String[fiberPortsCL.size()];
			for (int i = 0; i < portCuids.length; i++) {
//				FiberDpPort fcabport =(FiberDpPort)fiberPortsCL.get(i);
				GenericDO fcabport =fiberPortsCL.get(i);
				portCuids[i] = fcabport.getCuid();
			}
			DataObjectList list = new DataObjectList();
			IDuctManagerBO ductManagerBO=	BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			DataObjectList resGdo = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjsByCuids",new BoActionContext(),portCuids);
			for (int j=0;j<resGdo.size();j++){
				GenericDO child = resGdo.get(j);
				if(child!=null && child.getAttrBool("IS_CONNECTED_TO_FIBER")){
					//update 设计中的数据的字段为占用
					for(int i = 0; i < fiberPortsCL.size(); i++){
//						FiberDpPort fcabportSJ =(FiberDpPort)fiberPortsCL.get(i);
						GenericDO fcabportSJ =fiberPortsCL.get(i);
						if(fcabportSJ.getCuid().equals(child.getCuid())){
							fcabportSJ.setAttrValue("IS_CONNECTED_TO_FIBER", true);
							list.add(fcabportSJ);
							break;
						}
					}
				}
			}
			if(list.size()>0){
				ductManagerBO.updateDbos(new BoActionContext(), list);
			}
			// 只对断开失败的纤芯加以返回，以便页面撤消断开操作
			Map fail_map = (Map)dataMap.get("FAIL");
			Map<String, Object> fail_result = new HashMap<String, Object>();
			Iterator iter = fail_map.entrySet().iterator();
			while (iter.hasNext()) {
				Fiber fiber = (Fiber)iter.next();
				fail_result.put(fiber.getCuid(), fail_map.get(fiber));
			}
			result.put("_MESSAGE", "SUCCESS"); // 这里的SUCCESS仅仅表示ibo.deleteFibersConnect无异常
			result.put("_FAIL_DATA", fail_result); // 返回操作失败相关的纤芯和失败原因
		}catch(Exception ex) {
			LogHome.getLog().error("纤芯关联[断开]操作异常：", ex);
			result.put("_MESSAGE", ex.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 获得子节点信息
	 */
	public  List getChildrenByParentCuid(String parentCuid) {
		if(parentCuid != null && !parentCuid.equals("")){
			String className = parentCuid.split("-")[0];
			if (className.equals(FiberCab.CLASS_NAME)) {
	            return getFiberCabChildren(parentCuid);
	        }else if (className.equals(Fibercabmodule.CLASS_NAME)) {
	            return getFibercabmoduleChildren(parentCuid);
	        }else if(className.equals(Fcabport.CLASS_NAME)){
				return getFportChildren(parentCuid);
			}else if(className.equals(FiberDpPort.CLASS_NAME)){
				return getFportChildren(parentCuid);
			}else if (className.equals(FiberDp.CLASS_NAME)) {
	            return getFiberDpChildren(parentCuid);
	        }else if (className.equals(FiberJointBox.CLASS_NAME)) {//光接头盒
	            return getFiberJointBoxChildren(parentCuid);
	        }else if (className.equals(FiberJointPoint.CLASS_NAME)) {//光接头盒端子
	            return getFportChildren(parentCuid);
	        }
		}
		return null;
	}
	
	private List getFiberJointBoxChildren(String fiberJBCuid) {
		DataObjectList fiberjbports = null;
		try{
			String sql = "RELATED_DEVICE_CUID='"+fiberJBCuid+"' "
					+ " ORDER BY "+FiberJointPoint.AttrName.numInMrow+","+FiberJointPoint.AttrName.numInMcol;
			fiberjbports = BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class).getFiberJointPointBySql(new BoActionContext(), sql);
		}catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(fiberjbports);
	}
	private List getFiberCabChildren(String fiberCabCuid)
	{
		DataObjectList fibercabmodules =  null;
		try{
			fibercabmodules = BoHomeFactory.getInstance().getBO(IFibercabmoduleBO.class).getFibercabmoduleBySql(new BoActionContext(), 
					"RELATED_DEVICE_CUID='"+fiberCabCuid+"' ORDER BY "+Fibercabmodule.AttrName.numInRow+","+Fibercabmodule.AttrName.numInCol);
		}catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(fibercabmodules);
	}
	private List getFibercabmoduleChildren(String fiberCabMudlueCuid)
	{
		DataObjectList fibercabports = null;
		try{
			fibercabports = BoHomeFactory.getInstance().getBO(IFcabportBO.class).getFcabportBySql(new BoActionContext(), "RELATED_MODULE_CUID='"+fiberCabMudlueCuid+"'ORDER BY "+Fcabport.AttrName.labelCn+","+Fcabport.AttrName.numInMrow+","+Fcabport.AttrName.numInMcol);
		}catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(fibercabports);
	}
	private List getFiberDpChildren(String fiberDpCuid)
	{
		
		DataObjectList fiberdpports = null;
		try{
			fiberdpports = BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class).getFiberDpPortBySql(new BoActionContext(), "RELATED_DEVICE_CUID='"+fiberDpCuid+"' "
					+ "ORDER BY "+FiberDpPort.AttrName.numInMrow+","+FiberDpPort.AttrName.numInMcol);
		}catch(Exception e){
			LogHome.getLog().error(e);
		}
		return converToList(fiberdpports);
	}
	private List getFportChildren(String portCuid)
	{
		IDistrictBO districtBO = BoHomeFactory.getInstance().getBO(IDistrictBO.class);
		GenericDO parent = districtBO.getObjByCuid(new BoActionContext(), portCuid);
		if(parent == null)
		{
			parent = new GenericDO();
			parent.setCuid(portCuid);
		}
		DataObjectList jumperFibers = null;
		if (parent.getAttrValue(FiberJointPoint.AttrName.isConnected)!=null &&
		          (Boolean) parent.getAttrValue(FiberJointPoint.AttrName.isConnected)) {
		      try {
		    	  jumperFibers =  (DataObjectList) BoCmdFactory.getInstance().execBoCmd(JumpFiberBOHelper.ActionName.getJumpFiberByPointCuid,
		                  new BoActionContext(),parent.getCuid());
		      } catch (Exception e) {
		          LogHome.getLog().info(e);
		      }
		}
		return converToList(jumperFibers);
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
		if(child instanceof Fcabport){
			Fcabport port = (Fcabport)child;
			icon = "/resources/topo/alarm/c.png";
	        if (port.getIsConnectedToFiber() || port.getIsConnected()) {
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
	        if (port.getIsConnectedToFiber() || port.getIsConnected()) {
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
		if(child instanceof FiberJointPoint){
			FiberJointPoint port = (FiberJointPoint)child;
			icon = "/resources/topo/alarm/c.png";
	        if (port.getIsConnectedToFiber() || port.getIsConnected()) {
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
		map.put("ICON", icon);
		map.put("BM_CLASS_ID",child.getBmClassId());
		
		return map;
	}
	public String updateFiberDirection(HttpServletRequest request,Map param)throws Exception{
		String flag ="OK";
		String fiberCuids = (String)param.get("fiberCuids");
		String direction = (String)param.get("direction");
		try{
			if(StringUtils.isNotEmpty(fiberCuids)&&StringUtils.isNotEmpty(direction)){
				String sql = Fiber.AttrName.cuid+" IN('"+fiberCuids.replace(",", "','")+"')";
				IDuctManagerBO ductBo = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				DataObjectList fiberList = ductBo.getObjectsBySql(sql, new Fiber());
				if(fiberList!=null && fiberList.size()>0){
					for(int i=0;i<fiberList.size();i++){
						Fiber fiber = (Fiber)fiberList.get(i);
						fiber.setRemark(direction);
					}
					DmDesignerTools.setActionContext(new ServiceActionContext(request));
					BoActionContext actionContext = DmDesignerTools.getActionContext();
					ductBo.updateDbos(actionContext, fiberList);
				}
			}
		}catch(Exception e){
			flag = "更新纤芯的方向出错:"+e.getMessage();
			LogHome.getLog().error("更新纤芯的方向出错:"+e.getMessage());
			throw new UserException("更新纤芯的方向出错:"+e.getMessage());
		}
		return flag;
	}
	
}
