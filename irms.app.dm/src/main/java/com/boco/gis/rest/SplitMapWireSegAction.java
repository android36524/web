package com.boco.gis.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.graphkit.ext.GenericNode;
import com.boco.graphkit.ext.gis.GraphkitUtils;
import com.boco.irms.app.utils.CustomEnum;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.TopoHelper;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.SeggroupToRes;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.DMCacheObjectName;
import com.boco.transnms.server.bo.helper.dm.AccessPointBOHelper;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.FcabportBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberCabBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberDpBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberDpPortBOHelper;
import com.boco.transnms.server.bo.helper.dm.FiberJointBoxBOHelper;
import com.boco.transnms.server.bo.helper.dm.FibercabmoduleBOHelper;
import com.boco.transnms.server.bo.helper.dm.InflexionBOHelper;
import com.boco.transnms.server.bo.helper.dm.ManhleBOHelper;
import com.boco.transnms.server.bo.helper.dm.PoleBOHelper;
import com.boco.transnms.server.bo.helper.dm.StoneBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireSegBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFcabportBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberCabBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpPortBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;
import com.boco.transnms.server.bo.ibo.dm.IFibercabmoduleBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireToDuctLineBO;

public class SplitMapWireSegAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static Map<String,String> addpointMap = new HashMap<String, String>();
	static{
		addpointMap.put(FiberCab.CLASS_NAME, FiberCabBOHelper.ActionName.addFiberCabs);
		addpointMap.put(FiberDp.CLASS_NAME, FiberDpBOHelper.ActionName.addFiberDps);
		addpointMap.put(FiberJointBox.CLASS_NAME, FiberJointBoxBOHelper.ActionName.addFiberJointBoxs);
		addpointMap.put(Manhle.CLASS_NAME, ManhleBOHelper.ActionName.addManhles);
		addpointMap.put(Pole.CLASS_NAME, PoleBOHelper.ActionName.addPoles);
		addpointMap.put(Stone.CLASS_NAME, StoneBOHelper.ActionName.addStones);
		addpointMap.put(Inflexion.CLASS_NAME, InflexionBOHelper.ActionName.addInflexions);
		addpointMap.put(Accesspoint.CLASS_NAME, AccessPointBOHelper.ActionName.addAccesspoints);
		addpointMap.put(Fibercabmodule.CLASS_NAME, FibercabmoduleBOHelper.ActionName.addFibercabmodule);
		addpointMap.put(Fcabport.CLASS_NAME, FcabportBOHelper.ActionName.addFcabports);
		addpointMap.put(FiberDpPort.CLASS_NAME, FiberDpPortBOHelper.ActionName.addFiberDpPorts);
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
	private IWireSegBO getWireSegBO(){
		return BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	}
	
	private IWireToDuctLineBO getWireToDuctLineBO(){
		return BoHomeFactory.getInstance().getBO(IWireToDuctLineBO.class);
	}
	
	private IFiberJointBoxBO getFiberJointBoxBO(){
		return BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class);
	}
	
	private IFiberDpBO getFiberDpBo(){
		return BoHomeFactory.getInstance().getBO(IFiberDpBO.class);
	}
	
	private IFiberCabBO getFiberCabBO(){
		return BoHomeFactory.getInstance().getBO(IFiberCabBO.class);
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	
    private IFibercabmoduleBO getFibercabmoduleBO() {
        return BoHomeFactory.getInstance().getBO(IFibercabmoduleBO.class);
    }

    private IFcabportBO getFcabPortBO() {
        return BoHomeFactory.getInstance().getBO(IFcabportBO.class);
    }
    
    private IFiberDpPortBO getFiberDpPortBO() {
        return BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class);
    }
	/**
	 * 判断光缆有没有敷设
	 * 如果有敷设，拆分光缆时须选择一个具体路由点和一个光交点
	 * 如果没有敷设 ，须选择一个光交点
	 * @param wireSegCuid
	 * @throws Exception 
	 */
	public int getWireSegRouteByWireSeg(String wireSegCuid) throws Exception{
		int count = 0;
		WireSeg wireseg = new WireSeg();
		wireseg.setCuid(wireSegCuid);
		List lines = getWireToDuctLineBO().getDuctLinesByWireSeg(new BoActionContext(),wireseg);
		if(lines != null){
			count = lines.size();
		}
//		Map<String,String> canSplitPoints=new HashMap<String, String>();
//		List<String> pointCuidList = getWireToDuctLineBO().getLayPointCuidsByWireSegCuid(new BoQueryContext(), wireSegCuid);
//        for(String key:pointCuidList) {
//            canSplitPoints.put(key, key);
//        }
		return count;
	}
	
	/**
	 * 光缆段拆分 --- 陕西
	 * @param request
	 * @param splitWireSegCuid
	 * @param pointList
	 * @param relatedSegGroupCuid
	 * @param scene
	 * @return
	 * @throws Exception
	 */
	public String splitWireSegRes(HttpServletRequest request,String splitWireSegCuid, List<Map> pointList,String relatedSegGroupCuid, String scene) throws Exception {
		if(scene != null && !scene.equals("")){
			sceneValue = scene;
		}
		return splitWireSeg(request, splitWireSegCuid, pointList, relatedSegGroupCuid,scene);
	}
	
	public String splitWireSeg(HttpServletRequest request,String splitWireSegCuid, List<Map> pointList,String relatedSegGroupCuid,String scene) throws Exception {
		String result = "";
		try {
			ServiceActionContext ac = new ServiceActionContext(request);
			setActionContext(ac);
			WireSeg wireSeg = getWireSegBO().getWireSegByCuid(actionContext, splitWireSegCuid);
			String actionName = WireSegBOHelper.ActionName.modifySplitWireSegsExt;
			ArrayList<GenericDO[]> list = new ArrayList<GenericDO[]>();
			DataObjectList points = new DataObjectList();
			if(pointList != null && pointList.size()>0){
				for(int i=0;i<pointList.size();i++){
					GenericDO fiberDbo = null;
					Map<String,Object> map = pointList.get(i);
					Iterator it = map.entrySet().iterator();
					for(Map.Entry entry : map.entrySet()){
						String cuid = String.valueOf(map.get("CUID"));
						String className = cuid.split("-")[0];
						if(fiberDbo == null){
							fiberDbo = DmDesignerTools.getPointByClassName(className);
						}
						
						String key = String.valueOf(entry.getKey());
						Object value = entry.getValue();
						
						fiberDbo.setAttrValue(key, value);
					}
					points.add(fiberDbo);
				}
			}
			
			GenericDO []dtos=new GenericDO[2];
			if(points.size()>0){
				GenericDO gdo1 = null;
				GenericDO gdo2 = null;
				if(points.size() == 1){
					gdo1 = points.get(0);
					setRealLongiLati(gdo1);
					dtos[0] = null;//0具体路由点
					dtos[1] = gdo1;//1是光交设备
				}else{
					gdo1 = points.get(0);
					setRealLongiLati(gdo1);
					gdo2 = points.get(1);
					setRealLongiLati(gdo2);
					
					String className1 = gdo1.getClassName();
					//光交点
					boolean fiberPointClassName = isFiberPointClassName(className1);
					if(fiberPointClassName){//如果第一个是光交点
						dtos[0] = gdo2;//0具体路由点
						dtos[1] = gdo1;//1是光交设备
					}else{
						dtos[0] = gdo1;//0具体路由点
						dtos[1] = gdo2;//1是光交设备
					}
				}
				GenericDO dbo = dtos[1];
				setNewPoints(dbo);
				if(gdo1 != null){
					gdo1.clearUnknowAttrs();
				}
				if(gdo2 != null){
					gdo2.clearUnknowAttrs();
				}
			}

			list.add(dtos);
			//拆分完光缆段后新增加的段需要设置relatedseggroup
			DataObjectList viewResult = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(actionName, actionContext, wireSeg, list);
			if(viewResult != null && viewResult.size()>0){
				DataObjectList groupList = new DataObjectList();
				for(GenericDO wireSegDbo : viewResult){
					if(wireSegDbo instanceof WireSeg){
						String cuid = wireSegDbo.getCuid();
						if(!splitWireSegCuid.equals(cuid)){
							groupList.add(wireSegDbo);
						}
					}
				}
				if(groupList.size()>0){
					createSegGroup(groupList, relatedSegGroupCuid,scene);
					//拆分的光缆段,计算光缆段长度和系统长度
					modifyWireSystemLengthByWireSeg(groupList.get(0));
				}
			}
			result = "光缆段拆分成功！";
		} catch (Exception e) {
			result = "光缆段拆分失败！";
			LogHome.getLog().info("光缆段拆分出错：",e);
			throw new UserException(e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * 如果是新增加的光交点，需要先入库
	 * @param dbo
	 */
	private void setNewPoints(GenericDO dbo){
		DataObjectList insertList = new DataObjectList();
		GenericDO cloneDbo = (GenericDO) dbo.deepClone();
		try {
			Object attrValue = dbo.getAttrValue("OBJECT_STATE");
			if(attrValue != null){
				String objectState = String.valueOf(attrValue);
				if(objectState.trim().equals("1")){//代表新增加的光交点
					if(sceneValue.equals("DesignScene")){
						dbo.setAttrValue(FiberDp.AttrName.projectState, 1);
					}else if(sceneValue.equals("planScene")){
						dbo.setAttrValue(FiberDp.AttrName.projectState, 8);
					}else if(sceneValue.equals("ConstructScene")){
						dbo.setAttrValue(FiberDp.AttrName.projectState, 2);
				    }else if(sceneValue.equals("ViewpointScene")){
				    	dbo.setAttrValue(FiberDp.AttrName.projectState, 1);
					}else if(sceneValue.equals("changeScene")){
				    	dbo.setAttrValue(FiberDp.AttrName.projectState, 1);
					}
					dbo.clearUnknowAttrs();
					insertList.add(dbo);
				}
			}
			
			if(insertList.size()>0){
				String actionName = addpointMap.get(dbo.getClassName());
				BoCmdFactory.getInstance().execBoCmd(actionName, actionContext, insertList);
				
				DataObjectList setModuleAndPorts = setModuleAndPorts(cloneDbo);
				cloneDbo.setAttrValue("DataChildren", setModuleAndPorts);
				DataObjectList flist = new DataObjectList();
				flist.add(cloneDbo);
				//处理光交接箱和光分纤箱模块和端子
				if(flist.size()>0){
					String cuid = cloneDbo.getCuid();
					String className = cuid.split("-")[0];
					if(className.equals(FiberCab.CLASS_NAME)){
						setFiberCabPort(flist);
					}else if(className.equals(FiberDp.CLASS_NAME)){
						setFiberDpPort(flist);
					}
				}
			}
		} catch (Exception e) {
			LogHome.getLog().info("新增加点错误!", e);
		}
	}
	
	/**
	 * 增加光分纤箱端子
	 * @param fiberDpList
	 */
	private void setFiberDpPort(DataObjectList fiberDpList){
		DataObjectList fdpports = new DataObjectList();
		for(GenericDO dbo : fiberDpList){
			DataObjectList ports = TopoHelper.getChildren(dbo);
			if(ports.size()>0){
				fdpports.addAll(ports);
			}
		}
		if(fdpports.size()>0){
			getFiberDpPortBO().addFiberDpPorts(actionContext, fdpports);
		}
	}
	
	/**
	 * 增加光交接箱端子
	 * @param fiberCabList
	 */
	private void setFiberCabPort(DataObjectList fiberCabList){
		DataObjectList fcbports = new DataObjectList();
		DataObjectList fcabModules = new DataObjectList();
		for(GenericDO dbo : fiberCabList){
			DataObjectList modules = TopoHelper.getChildren(dbo);
			if(modules != null && modules.size()>0){
				fcabModules.addAll(modules);
				for(GenericDO gdo:modules){
					DataObjectList ports = TopoHelper.getChildren(gdo);
					if(ports != null && ports.size()>0){
						fcbports.addAll(ports);
					}
				}
			}
		}
		if(fcabModules.size()>0){
			getFibercabmoduleBO().addFibercabmodules(actionContext, fcabModules);
		}
		if(fcbports.size()>0){
			getFcabPortBO().addFcabports(actionContext, fcbports);
		}
	}
	
	private DataObjectList setModuleAndPorts(GenericDO dbo){
		DataObjectList origModuleAndPorts = new DataObjectList();
		String cuid = dbo.getCuid();
		String relatedCuid = DMHelper.getRelatedCuid(dbo.getAttrValue(FiberCab.AttrName.relatedDistrictCuid));
		if(cuid.startsWith(FiberCab.CLASS_NAME) || cuid.startsWith(FiberDp.CLASS_NAME)){
			Object moduleCfg = dbo.getAttrValue("MODULE_CFG");
			if(moduleCfg != null && moduleCfg instanceof String){
				JSONArray jsonArray = JSONArray.parseArray(String.valueOf(moduleCfg));
				if(jsonArray != null && jsonArray.size()>0){
					String type = cuid.split("-")[0];
					origModuleAndPorts = setEquipModuleAndPorts(jsonArray,type,cuid,relatedCuid);
				}
			}
		}
		return origModuleAndPorts;
	}
	private DataObjectList setEquipModuleAndPorts(JSONArray pointModules,String type,String relatedDeviceCuid,String relatedDistrictCuid){
		DataObjectList moduleList = new DataObjectList();
		for(int i=0;i<pointModules.size();i++){
			GenericDO moduleDbo = null;
			DataObjectList modulePorts = new DataObjectList();
			JSONObject pointModule = (JSONObject) pointModules.get(i);
			if(type.equals(FiberCab.CLASS_NAME)){
				moduleDbo = new Fibercabmodule();
				moduleDbo.setCuid();
				moduleDbo.setAttrValue(Fibercabmodule.AttrName.moduleCol, 0);
				moduleDbo.setAttrValue(Fibercabmodule.AttrName.moduleRow, 0);
				
				String labelCn = String.valueOf(pointModule.get("LABEL_CN"));
				moduleDbo.setAttrValue(Fibercabmodule.AttrName.labelCn, labelCn);
				moduleDbo.setAttrValue(Fibercabmodule.AttrName.relatedDeviceCuid, relatedDeviceCuid);
				moduleList.add(moduleDbo);
				modulePorts = setPorts(pointModule, moduleDbo, relatedDeviceCuid, relatedDistrictCuid);
				moduleDbo.setAttrValue("DataChildren", modulePorts);
			}else{
				modulePorts = setPorts(pointModule, moduleDbo, relatedDeviceCuid, relatedDistrictCuid);
				moduleList.addAll(modulePorts);
			}
		}
		return moduleList;
	}
	
	/**
	 * 设置端子属性
	 * @param pointModule
	 * @param moduleDbo
	 * @param relatedDeviceCuid
	 * @param relatedDistrictCuid
	 * @return
	 */
	private DataObjectList setPorts(JSONObject pointModule,GenericDO moduleDbo,String relatedDeviceCuid,String relatedDistrictCuid){
		DataObjectList portList = new DataObjectList();
		String colNum = String.valueOf(pointModule.get("COL_NUM")),
			   rowNum = String.valueOf(pointModule.get("ROW_NUM"));
		String relatedModuleCuid = null,splitClassName = null;
		
		Object prifixObject = pointModule.get("PREFIX"),
		suffixObject = String.valueOf(pointModule.get("SUFFIX"));
		String prifix=null,suffix= null;
	
		if(prifixObject != null){
			prifix = String.valueOf(prifixObject);
		}
		if(suffixObject != null){
			suffix = String.valueOf(suffixObject);
		}
	
		int rows = Integer.parseInt(rowNum);
		int cols = Integer.parseInt(colNum);
		
		if(moduleDbo != null){//不等于null代表光交接箱
			relatedModuleCuid = moduleDbo.getCuid();
			splitClassName = relatedModuleCuid.split("-")[0];
			moduleDbo.setAttrValue(Fibercabmodule.AttrName.numInCol, 1);
			moduleDbo.setAttrValue(Fibercabmodule.AttrName.numInRow, 1);
			moduleDbo.setAttrValue(Fibercabmodule.AttrName.portRow, rows);
			moduleDbo.setAttrValue(Fibercabmodule.AttrName.portCol, cols);
		}
		
		int startIndex = 0;
		Object object = pointModule.get("START_CODE");
		if(object != null){
			startIndex = Integer.parseInt(String.valueOf(object));
		}
		
		for(int i=0;i<rows;i++){
			int portNum = i*cols;
			for(int j=0;j<cols;j++){
				String portName = "";
				if(prifix != null){
					portName = portName+prifix;
				}
				
				String pname = String.valueOf(startIndex+portNum+j);
				portName += pname;
				
				if(suffix != null){
					portName += suffix;
				}
				
				if(splitClassName != null && splitClassName.equals(Fibercabmodule.CLASS_NAME)){
					Fcabport fcabPort = new Fcabport();
					fcabPort.setCuid();
					fcabPort.setRelatedModuleCuid(relatedModuleCuid);
					fcabPort.setRelatedDeviceCuid(relatedDeviceCuid);
					fcabPort.setRelatedDistrictCuid(relatedDistrictCuid);
					fcabPort.setNumInMrow(i+1);
					fcabPort.setNumInMcol(j+1);
					fcabPort.setLabelCn(portName);
					portList.add(fcabPort);
				}else{
					FiberDpPort fdpPort = new FiberDpPort();
					fdpPort.setCuid();
					fdpPort.setLabelCn(portName);
					fdpPort.setRelatedDeviceCuid(relatedDeviceCuid);
					fdpPort.setRelatedDistrictCuid(relatedDistrictCuid);
					fdpPort.setRelatedModuleCuid(relatedModuleCuid);
					fdpPort.setNumInMrow(i+1);
					fdpPort.setNumInMcol(j+1);
					portList.add(fdpPort);
				}
			}
		}
		return portList;
	}
	/**
	 * 设置实际经纬度
	 * @param gdo
	 */
	private void setRealLongiLati(GenericDO gdo){
		double longitude =gdo.getAttrDouble("LONGITUDE");
		double latitude = gdo.getAttrDouble("LATITUDE");
		gdo.setAttrValue(Manhle.AttrName.realLongitude, longitude);
		gdo.setAttrValue(Manhle.AttrName.realLatitude, latitude);
	}
	
    private boolean isFiberPointClassName(String className) {
        return FiberJointBox.CLASS_NAME.equals(className) ||
            FiberDp.CLASS_NAME.equals(className) || FiberCab.CLASS_NAME.equals(className);
    }
    
    private void createSegGroup(DataObjectList objList,String relatedSegGroupCuid,String scene) throws Exception{
    	try {
    		if(StringUtils.isNotEmpty(relatedSegGroupCuid)){
    			Map map = new HashMap();
    			if(null != scene){
    				map.put("DesignScene", CustomEnum.DMProjectState._design);
    				map.put("ConstructScene", CustomEnum.DMProjectState._construction);
    				map.put("planScene", CustomEnum.DMProjectState._plan);
    				map.put("changeScene", CustomEnum.DMProjectState._design);
    			}

    			DataObjectList segGroupToResList = DmDesignerTools.createSegGroupToReses(objList,relatedSegGroupCuid);
    			if(segGroupToResList != null && segGroupToResList.size()>0){
    				for(GenericDO res : segGroupToResList){
    					res.setAttrValue(SeggroupToRes.AttrName.projectState, map.get(scene));
    				}
    				getDuctManagerBO().createDMDOs(actionContext, segGroupToResList);
    			}
    		}
		} catch (Exception e) {
			LogHome.getLog().info("创建段落错误!", e);
			throw new UserException(e.getMessage());
		}
    }
    
	/**
	 * 根据光缆段id得到光缆段下的具体路由点
	 * @param wireSegCuid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("all")
	public List getRoutePointsByWireSegCuid(String wireSegCuid) throws Exception{
		List<String> layPointCuids = getWireToDuctLineBO().getLayPointCuidsByWireSegCuid(new BoQueryContext(), wireSegCuid);
		return layPointCuids;
	}
	
	private void modifyWireSystemLengthByWireSeg(GenericDO wireSegDbo){
		try {
			Map tmpMap = new HashMap();
			Map map = new HashMap();
			DataObjectList datas = new DataObjectList();
			String relatedSysCuid = DMHelper.getRelatedCuid(wireSegDbo.getAttrValue(WireSeg.AttrName.relatedSystemCuid));
			String[] cuids = new String[] {relatedSysCuid };
			// 重新计算
			DataObjectList rtnList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getSystemRoutePositionByCuid,new BoActionContext(), cuids, false);
			setSegsLength(rtnList, tmpMap);
			Map mapBySystem = TopoHelper.getListMapByAttr(rtnList,DuctSeg.AttrName.relatedSystemCuid);
			if (mapBySystem != null) {
				Iterator it = mapBySystem.keySet().iterator();
				while (it.hasNext()) {
					String relatedSystemCuid = (String) it.next();
					DataObjectList segs = (DataObjectList) mapBySystem.get(relatedSystemCuid);
					if (segs != null && segs.size() > 0) {
						double len = 0.0;
						for (GenericDO seg : segs) {
							if (seg != null) {
								Double length = seg.getAttrDouble(DuctSeg.AttrName.length, 0.0);
								if (length != null && length >= 0) {
									len += length;
								}
							}
						}
						// 系统长度的计算，只需四舍五入到小数点后两位。
						DecimalFormat doubleformat = new DecimalFormat(".00");
						len = Double.parseDouble(doubleformat.format(len));
						if (map.get(relatedSystemCuid) != null) {
							GenericNode node = (GenericNode) map.get(relatedSystemCuid);
							Object obj = node.getClientProperty(DuctSystem.AttrName.length);
							if (obj instanceof Double) { // && (Double) obj <= 0
								if (len >= 0) {
									GenericDO system = (GenericDO) node.getNodeValue().deepClone();
									system.setAttrValue(DuctSystem.AttrName.length, len);
									rtnList.add(system);
								}
							}
						}
					}
				}
			}
			System.out.println();
			BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.modifySystems, new BoActionContext(), rtnList);

		}catch (Exception e) {
			LogHome.getLog().info("修改光缆长度出错!", e);
			e.printStackTrace();
		}
		
	}
    /**
     * 设置段的长短属性
     */
    private void setSegsLength(DataObjectList list, Map map) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                GenericDO gdo = (GenericDO) list.get(i);
                String cuid = gdo.getCuid();
                gdo.convAllObjAttrToCuid();
                if (cuid != null && !cuid.startsWith(WireSeg.CLASS_NAME)) {
                    double s = 0.0;
                    s = calculateSegLength(gdo);
                    gdo.setAttrValue(DuctSeg.AttrName.length, s);
                }
                if (map.isEmpty()) {
                    continue;
                }
                GenericNode gNode = (GenericNode) map.get(cuid);
                if (gNode != null) {
                    gNode.putClientProperty(DuctSeg.AttrName.length, gdo.getAttrValue(DuctSeg.AttrName.length));
                }
            }
        }
    }
    //根据经纬度计算段的长度
    private double calculateSegLength(GenericDO gdo) {
        double s = gdo.getAttrDouble(DuctSeg.AttrName.length, 0.0);
        double origLatitude = gdo.getAttrDouble(DMCacheObjectName.origRealLatitude, 0.0);
        double origLongitude = gdo.getAttrDouble(DMCacheObjectName.origRealLongitude, 0.0);
        double destLatitude = gdo.getAttrDouble(DMCacheObjectName.destRealLatitude, 0.0);
        double destLongitude = gdo.getAttrDouble(DMCacheObjectName.destRealLongitude, 0.0);
        if (DMHelper.isCoordAvailable(origLatitude, origLongitude) && DMHelper.isCoordAvailable(destLatitude, destLongitude)) {
            s = GraphkitUtils.getDistance(origLongitude, origLatitude, destLongitude, destLatitude);
            DecimalFormat formatter = new DecimalFormat(".00");
            String temp = formatter.format(s);
            s = Double.parseDouble(temp);
        }
        return s;
    }
}
