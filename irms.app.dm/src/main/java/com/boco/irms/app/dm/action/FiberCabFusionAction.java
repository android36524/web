package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.common.util.db.TransactionFactory;
import com.boco.common.util.db.UserTransaction;
import com.boco.common.util.except.UserException;
import com.boco.ext.gis.log.Logger;
import com.boco.topo.Icon;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.Odfport;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfPortBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfmoduleBOX;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFcabportBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpPortBO;
import com.boco.transnms.server.bo.ibo.dm.IOpticalBOX;
import com.boco.transnms.server.bo.ibo.dm.IOpticalToFiber;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;

/**
 * @author lizongyu
 * 光交接箱端子直熔
 */
public class FiberCabFusionAction {

	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
	private IWireSystemBO getWireSystemBO(){
		return BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
	}
	
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	private IFiberBO getFiberBO(){
		return BoHomeFactory.getInstance().getBO(IFiberBO.class);
	}
	private IPhyOdfPortBO getPhyODfPortBO(){
		return BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class);
	}
	private IFcabportBO getFcabportBO(){
		return BoHomeFactory.getInstance().getBO(IFcabportBO.class);
	}
	private IFiberDpPortBO getFiberDpPortBO(){
		return BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class);
	}
    private IOpticalToFiber getOpticalToFiberBO() {
    	return BoHomeFactory.getInstance().getBO(IOpticalToFiber.class);
    }
    private IOpticalBOX getOpticalBO() {
    	return BoHomeFactory.getInstance().getBO(IOpticalBOX.class);
    }
    private IPhyOdfmoduleBOX getPhyOdfmoduleBO() {
    	return BoHomeFactory.getInstance().getBO(IPhyOdfmoduleBOX.class);
    }
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public List<Map> getWireSegAndWireSystemByCuid(String fcabCuid) {
		List lst = new ArrayList();
		HashMap<WireSystem, DataObjectList> wireSystemMap = new HashMap<WireSystem, DataObjectList>();
		String device = fcabCuid.split("-")[0];
		String sql = new String();
		if (device.equals("ODF")) {
//			IDuctManagerBO IDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			GenericDO fiberCabObj = getDuctManagerBO().getObjByCuid(new BoActionContext(),fcabCuid);
			if(fiberCabObj!=null){
				fcabCuid =  fiberCabObj.getAttrString(FiberCab.AttrName.relatedSiteCuid);
//			sql = "CUID IN (SELECT RELATED_SEG_CUID FROM FIBER WHERE ORIG_EQP_CUID='"+SiteCuid+"' OR DEST_EQP_CUID ='"+fcabCuid+"')";
//			sql = "EXISTS((SELECT RELATED_SITE_CUID FROM ODF WHERE CUID  ='"+ fcabCuid +"' AND RELATED_SITE_CUID = orig_Point_Cuid)) UNION 	"
//					+ "SELECT * FROM WIRE_SEG WHERE EXISTS((SELECT RELATED_SITE_CUID  FROM ODF  WHERE CUID = '"+ fcabCuid +"' AND RELATED_SITE_CUID = dest_Point_Cuid))";
			}
		}
		sql = "(" + WireSeg.AttrName.origPointCuid + "='" + fcabCuid
				+ "' or " + WireSeg.AttrName.destPointCuid + " ='"
				+ fcabCuid + "')";
		DataObjectList wireSegs = (DataObjectList) getWireSegBO()
				.getWireSegsBySql(new BoActionContext(), sql);
		if (wireSegs != null && wireSegs.size() > 0) {
			Map<String, DataObjectList> map = new HashMap<String, DataObjectList>();
			DataObjectList systemlist = new DataObjectList();
			Map<String, String> sysNameMap = new HashMap();
			for (GenericDO wireseg : wireSegs) {
				String segCuid = wireseg.getCuid();
				String wsCuid = DMHelper.getRelatedCuid(wireseg
						.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
				WireSystem wsDbo = getWireSystemBO().getWireSystemByCuid(
						new BoActionContext(), wsCuid);
				if (wsDbo != null) {
					String labelCn = wsDbo.getLabelCn();
					if (wsDbo != null
							&& !systemlist.getCuidList().contains(wsCuid)) {
						systemlist.add(wsDbo);
						sysNameMap.put(wsCuid, labelCn);
					}
				}

				DataObjectList list = map.get(wsCuid);
				if (list == null) {
					list = new DataObjectList();
					map.put(wsCuid, list);
				}
				list.add(wireseg);
			}

			if (map.size() > 0) {
				for (Map.Entry<String, DataObjectList> entry : map.entrySet()) {
					Map sysMap = new HashMap();
					List sysListSeg = new ArrayList();
					String key = entry.getKey();
					String labelCn = sysNameMap.get(key);
					sysMap.put("CUID", key);
					sysMap.put("LABEL_CN", labelCn);
					sysMap.put("SEGS", sysListSeg);
					lst.add(sysMap);
					DataObjectList segs = entry.getValue();
					if (segs != null && segs.size() > 0) {
						for (GenericDO seg : segs) {
							Map segMap = new HashMap();
							String sCuid = seg.getCuid();
							String segLabel = seg
									.getAttrString(WireSeg.AttrName.labelCn);
							segMap.put("CUID", sCuid);
							segMap.put("LABEL_CN", segLabel);
							sysListSeg.add(segMap);
						}
					}
				}
			}
		}
		return lst;
	}
    /**
     * 纤芯两端的端子
     * @param fiberCuid bmClassId设备的类型
     * @return
     */
    public DataObjectList getSidePointsByFiberCuid(String fiberCuid,String bmClassId){
    	DataObjectList portList = new DataObjectList();
		try {
			String sql =new String();
			if(bmClassId.equals("ODF")){
				sql = "cuid in (select DEST_POINT_CUID from fiber where cuid = '"
						+ fiberCuid+ "' union select ORIG_POINT_CUID from fiber where cuid = '"
	//					+ fiberCuid+ "') and RELATED_MODULE_CUID is null";
						+ fiberCuid+ "')";
				portList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IPhyOdfPortBO.getOdfportBySql",new BoActionContext(), sql);
			}
			if(bmClassId.equals("FIBER_DP")){
				sql = "cuid in (select DEST_POINT_CUID from fiber where cuid = '"
						+ fiberCuid+ "' union select ORIG_POINT_CUID from fiber where cuid = '"
//						+ fiberCuid+ "') and RELATED_MODULE_CUID is null";
						+ fiberCuid+ "')";
				portList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IFiberDpPortBO.getFiberDpPortBySql",new BoActionContext(), sql);
			}
			if(bmClassId.equals("FIBER_CAB")){
				sql = "cuid in (select DEST_POINT_CUID from fiber where cuid = '"
						+ fiberCuid+ "' union select ORIG_POINT_CUID from fiber where cuid = '"
						+ fiberCuid+ "') and RELATED_MODULE_CUID is null";
				portList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IFcabportBO.getFcabportBySql",new BoActionContext(), sql);
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return portList;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getChildrenByParentCuid(String cuid,String bmClassId) throws IOException{
		Map map = new HashMap();
		try{
			DataObjectList children = getSidePointsByFiberCuid(cuid,bmClassId);
			
//			list.add(map);
			map.put("CUID",cuid);
			if(children != null && children.size() > 0)
			{
				List portList = new ArrayList();
				map.put("PORTS", portList);
				for(GenericDO child : children){
					String portCuid = child.getCuid();
					String portLabelCn = child.getAttrString(Fcabport.AttrName.labelCn);
					Map portMap = new HashMap();
					portMap.put("CUID", portCuid);
					portMap.put("LABEL_CN", portLabelCn);
					//如果父类是纤芯，子类需要增加一封装的名称属性portName
					portMap.put("PORTNAME", portLabelCn);
					String icon = Icon.getIconByType(child);
					portMap.put("ICON", icon);
					portMap.put("BM_CLASS_ID", child.getBmClassId());
					portList.add(portMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public String addFibersConnect(List leftFiberList,List<Map<String,String>> fcabPortList,List rightFiberList) throws Exception{
		
		String flag = "error";
		DataObjectList leftFibers = getDuctManagerBO().getGenericDOListByCuids(leftFiberList);		
		DataObjectList rightFibers = getDuctManagerBO().getGenericDOListByCuids(rightFiberList);
		DataObjectList fports = new DataObjectList();
		DataObjectList cabList = new DataObjectList();
		String relatedDistrctCuid = "";	
		String device = fcabPortList.get(0).get("RELATED_DEVICE_CUID");
		String bmClassId = fcabPortList.get(0).get("RELATED_DEVICE_CUID").split("-")[0];
		for(int i =0;i<fcabPortList.size();i++){
			Map<String,String> portMap =fcabPortList.get(i);
			String cuid = portMap.get("CUID");
			String labelCn = portMap.get("LABEL_CN");

			if(!cabList.getCuidList().contains(device)){
				GenericDO objByCuid = getDuctManagerBO().getObjByCuid(new BoActionContext(), device);
				if(objByCuid != null){
					relatedDistrctCuid = DMHelper.getRelatedCuid(objByCuid.getAttrValue(FiberCab.AttrName.relatedDistrictCuid));
					cabList.add(objByCuid);
				}
			}
//			GenericDO fport = new GenericDO();
			if(bmClassId.equals("FIBER_CAB")){
				Fcabport fport = new Fcabport();
//				fport = (Fcabport)fport;
				fport.setAttrNull("RELATED_MODULE_CUID");//隐藏直熔端子
				fport.setCuid(cuid);
				fport.setAttrValue("RELATED_DEVICE_CUID", device);
				fport.setAttrValue("RELATED_DISTRICT_CUID", relatedDistrctCuid);
				fport.setLabelCn(labelCn);
				fport.setIsConnected(false);
				fport.setIsConnectedToFiber(false);
				fport.setServiceState(1);
				fports.add(fport);
			}
			else if(bmClassId.equals("ODF")){
				Odfport fport = new Odfport();
//				fport = (Odfport)fport;
				String odfModuleCuid = portMap.get("RELATED_MODULE_CUID");
				fport.setAttrValue("RELATED_MODULE_CUID",odfModuleCuid);
				fport.setCuid(cuid);				
				fport.setAttrValue("RELATED_DEVICE_CUID", device);
				fport.setAttrValue("RELATED_DISTRICT_CUID", relatedDistrctCuid);
				fport.setLabelCn(labelCn);
				fport.setIsConnected(false);
				fport.setIsConnectedToFiber(false);
				fport.setServiceState(1);
				fports.add(fport);
			}
			else if(bmClassId.equals("FIBER_DP")){
				FiberDpPort fport = new FiberDpPort();
//				fport = (FiberDpPort)fport;
				fport.setCuid(cuid);
				fport.setAttrValue("RELATED_DEVICE_CUID", device);
				fport.setAttrValue("RELATED_DISTRICT_CUID", relatedDistrctCuid);
				fport.setLabelCn(labelCn);
				fport.setIsConnected(false);
				fport.setIsConnectedToFiber(false);
				fport.setServiceState(1);
				fports.add(fport);
			}

		}		
		 try {
	        	BoCmdFactory.getInstance().execBoCmd("IFiberBO.addFcabPortFibersConnect",new BoActionContext(), leftFibers,fports,rightFibers);
	        	flag = "true";
	        } catch (Exception e) {
	        	flag = "error"+"-"+e.getMessage();	        	
	        	Logger.getLog().info("端子直熔增加失败！");
	        	System.out.println(e.getMessage());
//	            throw new UserException(e.getMessage());
	        }
		return flag;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean deleteFcabPortFibersConnect(List<String> fiberList,String deviceType) throws Exception{
		boolean flag = false;
		try {
			HashMap toDelElementMap = new HashMap<GenericDO, DataObjectList>();
			DataObjectList genericDOListByCuids = getDuctManagerBO().getGenericDOListByCuids(fiberList);
			if(genericDOListByCuids != null && genericDOListByCuids.size()>0){
				for(GenericDO dbo : genericDOListByCuids){
					toDelElementMap.put(dbo, new DataObjectList());
				}
			}
//			BoCmdFactory.getInstance().execBoCmd("IFiberBO.deleteFcabPortFibersConnect",new BoActionContext(), toDelElementMap);
			deleteFcabPortFibersConnectT(new BoActionContext(),toDelElementMap,deviceType);
			flag = true;
		} catch (Exception e) {
			flag = false;
			Logger.getLog().info("删除端子直熔错误！",e);
			e.printStackTrace();
		}
		return flag;
	}
    /**
     * 更新纤芯两端的信息，删除直熔端子，更新光纤状态
     * @param actionContext
     * @param map    key:value (Fiber:DataObjectList<FcabPort>)
     * @throws UserException
     * @author xuwb
     */
    @SuppressWarnings("rawtypes")
	public void deleteFcabPortFibersConnectT(BoActionContext actionContext,
			HashMap map,String deviceType) throws UserException {
		if(map != null && map.size() > 0){
			//获取所有的直熔端子
			DataObjectList fcabportList = new DataObjectList();
			for (Object obj : map.keySet()) {
                GenericDO fiber = (GenericDO) obj;
                if (map.get(fiber) != null) {
                    DataObjectList ports = (DataObjectList) map.get(fiber);
                    if (ports.size() > 0) {
                    	fcabportList.addAll(ports);
                    }else{
                    	 if (fiber.getAttrValue(Fiber.AttrName.origPointCuid) instanceof String) {
                             String origPort = (String) fiber.getAttrValue(Fiber.AttrName.origPointCuid);
                             GenericDO point = new GenericDO();
                             if(origPort.trim().length() > 0&&(deviceType.equals("FIBER_CAB"))){
                            	 point = getSimplePointBycuid(actionContext, origPort);
                            	 if (point != null) {
                            		 fcabportList.add(point);
                            	 }
                             }
                             if(origPort.trim().length() > 0&&(deviceType.equals("ODF"))){
                            	 point = getSimplePointBycuid(actionContext, origPort);
                            	 if (point != null) {
                            		 fcabportList.add(point);
                            	 }
                             }
                             if(origPort.trim().length() > 0&&(deviceType.equals("FIBER_DP"))){
                            	 point = getSimplePointBycuid(actionContext, origPort);
                            	 if (point != null) {
                            		 fcabportList.add(point);
                            	 }
                             }
//                             if (origPort.trim().length() > 0 && (origPort.startsWith(Fcabport.CLASS_NAME)||
//                            		 origPort.startsWith(FiberDpPort.CLASS_NAME)||origPort.startsWith(Odfport.CLASS_NAME))) {
                                 
                             }
                         }
//                         if (fiber.getAttrValue(Fiber.AttrName.destPointCuid) instanceof String) {
//                             String destPort = (String) fiber.getAttrValue(Fiber.AttrName.destPointCuid);
////                             if (destPort.trim().length() > 0) {
//                             if (destPort.trim().length() > 0 && destPort.startsWith(Fcabport.CLASS_NAME)||
//                            		 destPort.startsWith(FiberDpPort.CLASS_NAME)||destPort.startsWith(Odfport.CLASS_NAME)) {
//                                 GenericDO point = getSimplePointBycuid(actionContext, destPort);
//                                 if (point != null) {
//                                     fcabportList.add(point);
//                                 }
//                             }
//                         }
	               	 if (fiber.getAttrValue(Fiber.AttrName.destPointCuid) instanceof String) {
	                     String destPort = (String) fiber.getAttrValue(Fiber.AttrName.destPointCuid);
	                     GenericDO point = new GenericDO();
	                     if(destPort.trim().length() > 0&&(deviceType.equals("FIBER_CAB"))){
	                    	 point = getSimplePointBycuid(actionContext, destPort);
	                    	 if (point != null) {
	                    		 fcabportList.add(point);
	                    	 }
	                     }
	                     if(destPort.trim().length() > 0&&(deviceType.equals("ODF"))){
	                    	 point = getSimplePointBycuid(actionContext, destPort);
	                    	 if (point != null) {
	                    		 fcabportList.add(point);
	                    	 }
	                     }
	                     if(destPort.trim().length() > 0&&(deviceType.equals("FIBER_DP"))){
	                    	 point = getSimplePointBycuid(actionContext, destPort);
	                    	 if (point != null) {
	                    		 fcabportList.add(point);
	                    	 }
	                     }
//                     if (origPort.trim().length() > 0 && (origPort.startsWith(Fcabport.CLASS_NAME)||
//                    		 origPort.startsWith(FiberDpPort.CLASS_NAME)||origPort.startsWith(Odfport.CLASS_NAME))) {
                                             
	               	 }
                }
            }
			//获取所有的直熔端子相关的纤芯
			DataObjectList fiberList = new DataObjectList();
			List<String> fcabCuidList = new ArrayList<String>();
			//ODF直熔模块
			List<String> odmCuidList = new ArrayList<String>();
			for(Object obj : fcabportList){
				//这句			
				GenericDO port = (GenericDO) obj;
				String bmClassId = port.getCuid().split("-")[0];
				if(bmClassId.equals("FCABPORT")){
					fiberList.addAll(getFiberBO().getFiberByFcabport(actionContext, ((Fcabport)port)));
				}
				if(bmClassId.equals("FIBER_DP_PORT")){
					fiberList.addAll(getFiberBO().getFiberByFiberDpPort(actionContext, ((FiberDpPort)port)));
				}

				if(bmClassId.equals("ODFPORT")){
					fiberList.addAll(getFiberBO().getFiberByOdfport(actionContext, ((Odfport)port)));
				}
				//如果一个端子连接了两个纤芯  则这个端子我们判断为直熔端子 这个判断在ODF可能不严谨
				if(fiberList.size()%2!=0){
					throw new UserException("端子[" + port.getAttrValue("LABEL_CN") + "]非直熔端子");
				}
				else{
					fcabCuidList.add((port).getCuid());
		
				}
				
			}
			//去重 
			for (int i = 0; i < fcabCuidList.size(); i++)  //外循环是循环的次数
            {
                for (int j = fcabCuidList.size()-1; j > i; j--)  //内循环是 外循环一次比较的次数
                {
                    if (fcabCuidList.get(i).equals(fcabCuidList.get(j)))
                    {
                    	fcabCuidList.remove(j);
                    }
                }
            }
			//根据直熔端子判断两端纤芯的 使用状态   若在用/预占 则不能删除该直熔端子
			for(Object obj : fiberList){
				if (((Fiber) obj).getUsageState() == DuctEnum.DMSUSAGESTATE._use ||
                        ((Fiber) obj).getUsageState() == DuctEnum.DMSUSAGESTATE._preuse) {
                        throw new UserException("纤芯[" + ((Fiber) obj).getLabelCn() + "]在用/预占！");
				}
			}
			for(Object obj : fiberList){
				Fiber fiber = (Fiber) obj;
				if(fcabCuidList.contains(fiber.getAttrValue(Fiber.AttrName.origPointCuid))){
					fiber.setAttrNull(Fiber.AttrName.origPointCuid);
				}
				if(fcabCuidList.contains(fiber.getAttrValue(Fiber.AttrName.destPointCuid))){
					fiber.setAttrNull(Fiber.AttrName.destPointCuid);
				}
			}
			UserTransaction trx = TransactionFactory.getInstance().createTransaction();
	        try {
	            trx.begin();
	            if(fiberList.size() > 0){
	            	//更新纤芯
	            	getFiberBO().updateFibers(actionContext, fiberList);
//	            	getRelationGenericDAO().modifyDMDOs(actionContext, fiberList);
	            	//更新光纤状态	            	
//	            	for(Object obj : fiberList){
//	    				Fiber fiber = (Fiber) obj;
//	    				DataObjectList dol = getOpticalToFiberBO().getOpticalToFiberByFiberCuid(actionContext, fiber.getCuid());
//		                if (dol != null && dol.size() > 0) {
//		                    for (int i = 0; i < dol.size(); i++) {
//		                        OpticalToFiber opticalToFiber = (OpticalToFiber) dol.get(i);
//								String opticalCuid = opticalToFiber.getOpticalCuid();								
//								getOpticalBO().modifyOpticalMakeFlag(actionContext, opticalCuid,TransPathEnum.PathMakeFlag.waitcheck);
//							}
//		                }
//					}
	            }
				//删除直熔端子swx
	            if(fcabCuidList.size() > 0){
	            	String sqlCuid = "";				
	            	for(String fcabCuid : fcabCuidList){
	            		sqlCuid += ",'"+fcabCuid+"'";
	            	}
	            	sqlCuid = " CUID in ("+sqlCuid.substring(1)+")";
					String bmClassId = fcabCuidList.get(0).split("-")[0];
					if(bmClassId.equals("FCABPORT")){
						DataObjectList list = getFcabportBO().getFcabportBySql(actionContext, sqlCuid);
						getFcabportBO().deleteFcabports(actionContext, list);
					}
					if(bmClassId.equals("FIBER_DP_PORT")){
						DataObjectList list = getFiberDpPortBO().getFiberDpPortBySql(actionContext, sqlCuid);
						getDuctManagerBO().deleteObjects(actionContext, list);
//						getFiberDpPortBO().deleteFiberDpPorts(actionContext, list);
					}
					if(bmClassId.equals("ODFPORT")){
//						DataObjectList odfmodules = new DataObjectList();
						
//						odfmodules.add(e)getPhyOdfmoduleBO().getODMByODFCuid(actionContext, fcabCuidList.get(0));
//						for(i=0;i<fcabCuidList.size();i++){
//							
//						}
//						if(odfmodules.size()>0&&odfmodules!=null){
//							getDMGenericDAO().deleteObjects(actionContext, odfmodules);
//						}
						DataObjectList list = getPhyODfPortBO().getOdfportBySql(actionContext, sqlCuid);
						
						getPhyODfPortBO().deletePhyOdfports(actionContext, list);
//						if(null != list && list.size()>0){
//							getDMGenericDAO().deleteObjects(actionContext, list);
//						}
						
					}
	            }
				trx.commit();
			} catch (Exception e) {
				trx.rollback();
	            throw new UserException("更新纤芯、删除端子、更新光纤出错" + e.getMessage());
			}
		}
			
	}
    public GenericDO getSimplePointBycuid(BoActionContext actionContext, String pointCuid) {
        if (pointCuid != null) {
            if (pointCuid.startsWith(Odfport.CLASS_NAME)) { //如果是odfport
                Odfport odfport = getPhyODfPortBO().getOdfportByCuid(actionContext, pointCuid);
                return odfport;
            } else if (pointCuid.startsWith(Fcabport.CLASS_NAME)) {
                Fcabport fcabport = getFcabportBO().getFcabportByCuid(actionContext, pointCuid);
                return fcabport;
            } else if (pointCuid.startsWith(FiberDpPort.CLASS_NAME)) {
                FiberDpPort fiberDpPort = getFiberDpPortBO().getFiberDpPortByCuid(actionContext, pointCuid);
                return fiberDpPort;
            }
        } 
        return null;
    }
    
    public List<Map<String, Object>> getPortByParentCuid(String portCuid, String fiberCuid) throws Exception {
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	try {
			if(!portCuid.equals("") && !fiberCuid.equals("")){
				String sql = Fiber.AttrName.cuid + "!='" + fiberCuid + "' AND (" + Fiber.AttrName.origPointCuid + "='" + portCuid
						+ "' OR " + Fiber.AttrName.destPointCuid + "='" + portCuid + "')";
				DataObjectList fibersList = getDuctManagerBO().getObjectsBySql(sql, new Fiber());
				if(fibersList != null && fibersList.size() > 0){
					GenericDO fiber = fibersList.get(0);
					result.add(fiber.getAllAttr());
				}
			}
		} catch (UserException e) {
			e.printStackTrace();
		}
    	return result;
    }
}
