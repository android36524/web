package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boco.core.utils.exception.UserException;
import com.boco.topo.Icon;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.bussiness.helper.DMHelperX;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.CutoverScheme;
import com.boco.transnms.common.dto.CutoverTask;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Miscrack;
import com.boco.transnms.common.dto.Odf;
import com.boco.transnms.common.dto.Odfmodule;
import com.boco.transnms.common.dto.Odfport;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.cm.MiscrackBOHelper;
import com.boco.transnms.server.bo.helper.cm.PhyOdfBOHelper;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfPortBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdmBO;
import com.boco.transnms.server.bo.ibo.dm.ICutoverSchemeBO;
import com.boco.transnms.server.bo.ibo.dm.ICutoverTaskBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointPointBO;


@Controller()
@Service("WireFinishCutAction")
@RequestMapping("/WireFinishCutAction")
public class WireFinishCutAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	static int location;
	
	public String doExcute(String cutovertaskId){
		GenericDO cutoverTask = getDuctManagerBO().getObjByCuid(new BoActionContext(), cutovertaskId);
		try {
			 long state = (Long) cutoverTask.getAttrValue(CutoverTask.AttrName.cutoverState);
			 if(cutoverTask.getAttrValue(CutoverTask.AttrName.relatedWireSegCuid)==null ||cutoverTask.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid)==null ){
				 return null;
			 }
			 if(DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign !=state){//不是设计好的方案
				return "请选择设计好的方案执行割接!";
			 }
			 DataObjectList fiberCutList = getCutoverSchemeBO().getCutOverSchemeByTaskCuid(new BoQueryContext(), cutoverTask.getCuid());
		 	 if(fiberCutList==null || fiberCutList.size()==0){
		 		 return "未设计完成，不可进行执行割接!";
		 	 }
			 GenericDO dto = getCutoverTaskBO().executeCutoverTask(new BoActionContext(),(CutoverTask) cutoverTask,fiberCutList);
			 long afterState =(Long) dto.getAttrValue(CutoverTask.AttrName.cutoverState);
		 	 if(DuctEnum.WIRE_CUT_STATE_ENUM._cutsuccess==afterState){
		 		 return "割接成功";
		 	 }else{
		 		 return "割接失败!";
		 	 }
		} catch (Exception e) {
			logger.info("执行割接任务失败!",e);
			e.printStackTrace();
		}
		return null;
	}
	
	public void doRelevancy(String cutOverTaskId,String deviceCuid,String wireSegCuid,String fibers,String ports){
		GenericDO cutOverTask = getDuctManagerBO().getObjByCuid(new BoActionContext(), cutOverTaskId);
		GenericDO deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid);
		GenericDO wireSegDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
		String siteCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedSiteCuid);
		if(siteCuid==null || ("").equals(siteCuid)){//是接入点下的设备
			siteCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedAccessPoint);
		}
		if(siteCuid.equals(wireSegDto.getAttrValue(WireSeg.AttrName.origPointCuid))){
			location = 0;
		}else{
			location = 1;
		}
		List fibersDto = getDuctManagerBO().getObjsByCuid(new BoActionContext(), fibers.split(","));
		List portsDto = getDuctManagerBO().getObjsByCuid(new BoActionContext(), ports.split(","));
		for(int n = 0; n < portsDto.size(); n++){
			GenericDO object = (GenericDO)portsDto.get(n);
			 if(object.getAttrValue(Odfport.AttrName.isConnectedToFiber)!=null && (Boolean)object.getAttrValue(Odfport.AttrName.isConnectedToFiber)){
             	throw new UserException(object.getAttrValue("LABEL_CN")+"端子已连接了纤芯不能做预割接端子!");
             }
             if(object.getAttrValue(Odfport.AttrName.isConnected)!=null && (Boolean)object.getAttrValue(Odfport.AttrName.isConnected)){
             	throw new UserException(object.getAttrValue("LABEL_CN")+"端子已连接了跳纤不能做预割接端子!");
             }	
		}
		
		cutOverTask.setAttrValue(CutoverTask.AttrName.relatedWireSegCuid, wireSegDto);
		Object wireSystemCuid = wireSegDto.getAttrValue(WireSeg.AttrName.relatedSystemCuid);
		GenericDO wireSystemDto = null;
		if(wireSystemCuid instanceof String){
			wireSystemDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), (String) wireSystemCuid);
		}else if(wireSystemCuid instanceof GenericDO){
			wireSystemDto = (GenericDO) wireSystemCuid;
		}
		if(wireSystemDto!=null){
			cutOverTask.setAttrValue(CutoverTask.AttrName.relatedWireSystemCuid, wireSystemDto);//割接光缆系统
			Object districtCuid = wireSystemDto.getAttrValue(WireSystem.AttrName.relatedSpaceCuid);
			GenericDO district = null;
			if(districtCuid instanceof String){
				district = getDuctManagerBO().getObjByCuid(new BoActionContext(), (String) districtCuid);
			}else if(districtCuid instanceof GenericDO){
				district = (GenericDO) districtCuid;
			}
			cutOverTask.setAttrValue(CutoverTask.AttrName.relatedDistrictCuid, district);//割接所属区域
		}
		cutOverTask.setAttrValue(CutoverTask.AttrName.relatedDeviceCuid, deviceDto);
		cutOverTask.setAttrValue(CutoverTask.AttrName.cutoverState, DuctEnum.WIRE_CUT_STATE_ENUM._schemedesign);
		cutOverTask.setAttrValue(CutoverTask.AttrName.failReason, "");
		
		getCutoverTaskBO().modifyCutoverTask(new BoActionContext(), cutOverTask);
		if(fibersDto.size()>0 && portsDto.size()>0){
			for (int i = 0; i < portsDto.size(); i++){
				GenericDO portDto = (GenericDO) portsDto.get(i);//割接端子
				GenericDO fiber = (GenericDO) fibersDto.get(i);//割接纤芯
				GenericDO cutoverScheme = new CutoverScheme();
				cutoverScheme.setAttrValue(CutoverScheme.AttrName.relatedFiberCuid, fiber);
				cutoverScheme.setAttrValue(CutoverScheme.AttrName.relatedPortCuid, portDto);
				cutoverScheme.setAttrValue(CutoverScheme.AttrName.relatedTaskCuid, cutOverTask);
				cutoverScheme.setAttrValue(CutoverScheme.AttrName.cutoverLocation, location);
				getCutoverSchemeBO().saveCutoverScheme(new BoActionContext(), cutoverScheme);
				//设置该端子已经是预割接端子，
				//纤芯已经设置了预割接端子，以后不能再选择设计割接
			}
		}
		
	}
	
	public void doDisConnect(String fibers,String cutOverTaskId){
		String[] cuids = fibers.split(",");
		for(int i= 1;i < cuids.length;i++){
			getCutoverSchemeBO().deleteCutoverScheme(new BoActionContext(),cuids[i-1],cuids[i],cutOverTaskId,location);
		}
	}
	
	public List<Map<String,String>> getChildrenByCutFibers(String fiberCuid,String deviceCuid){
		DataObjectList children = new DataObjectList();
		Map<String,String> ports = new HashMap<String, String>();
		ArrayList<Map<String,String>> results = new ArrayList<Map<String,String>>();
		GenericDO fiberDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), fiberCuid);
		GenericDO deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid);
		String origEqpCuid = (String) fiberDto.getAttrValue(Fiber.AttrName.origEqpCuid);
		String destEqpCuid = (String) fiberDto.getAttrValue(Fiber.AttrName.destEqpCuid);
		GenericDO portDto = null;
		GenericDO relatedPort = null;
		if(deviceDto.getCuid().equals(origEqpCuid)){
			String origPointCuid = (String) fiberDto.getAttrValue(Fiber.AttrName.origPointCuid);
			portDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), origPointCuid);
//			relatedPort = getCutoverSchemeBO().getRelatedPort(new BoQueryContext(), fiberDto.getCuid(), cutoverTask.getCuid(), 0);
		}else if(deviceDto.getCuid().equals(destEqpCuid)){
			String destPointCuid = (String) fiberDto.getAttrValue(Fiber.AttrName.destPointCuid);
			portDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), destPointCuid);
//			relatedPort = getCutoverSchemeBO().getRelatedPort(new BoQueryContext(), fiberDto.getCuid(), cutoverTask.getCuid(), 1);
		}
		if(portDto!=null){
			children.add(portDto);
		}
		if(relatedPort!=null){
			children.add(relatedPort);
//			preCutPort.add(relatedPort);
//			preCutFiber.add(fiberDto);
		}
		ports.put("CUID", portDto.getCuid());
		ports.put("LABEL_CN", portDtoName(portDto, fiberDto));
		if(portDto.getAttrValue(Odfport.AttrName.isConnectedToFiber)!=null && (Boolean)portDto.getAttrValue(Odfport.AttrName.isConnectedToFiber)){
			ports.put("ICON", "1");//黄色
		}else{
			ports.put("ICON", "2");//蓝色
		}
		ports.put("PARENTCIUD", fiberDto.getCuid());
		results.add(ports);
		//TODO 从割接方案表中获取预割接端子，添加为纤芯的子节点，将获得的预割接端子加到preCutPort
		//将预割接端子加到children里面
		//如果fiber的children为两个的话就加到preCutFiber中
		return results;
	}
	
	@RequestMapping(value = "/getRoomList/{cuid}", method = RequestMethod.GET)
	public void getRoomList(@PathVariable String cuid,HttpServletResponse response) throws IOException{
		StringBuffer result = new StringBuffer();
		try {
			GenericDO deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), cuid);
			String accessPointCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedAccessPoint);
			String roomCuid="";
			if(accessPointCuid== null || ("").equals(accessPointCuid)){
				roomCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedRoomCuid);
			}else{
				roomCuid = accessPointCuid;
			}
			GenericDO roomDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), roomCuid);
			DataObjectList roomList = new DataObjectList();
			roomList.add(roomDto);
		        result.append("{\"roomList\":[");
		    	for(int i =0 ;i<roomList.size();i++)
		    	{
		    		GenericDO room = roomList.get(i);
		    		
		    		result.append("{");
		    		result.append("\"CUID\":").append("\""+room.getCuid()+"\",");
		    		result.append("\"ICON\":").append("\""+Icon.getIconByType(room)+"\",");
		    		result.append("\"LABEL_CN\":").append("\""+room.getAttrValue("LABEL_CN")+"\"");
		    		result.append("}");
					if(i < roomList.size()-1)
						result.append(",");
		    	}
				result.append("]}");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		}
	}
	
	public List<Map<String,String>> initPostList(String deviceCuid){
		
		List<Map<String,String>> results = new ArrayList<Map<String,String>>();
		DataObjectList roomList = new DataObjectList();
		try {
			GenericDO deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid);
			String roomCuid="";
			// 设备是接入点的设备
			String accessPointCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedAccessPoint);
			if(accessPointCuid== null || ("").equals(accessPointCuid)){
				roomCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedRoomCuid);
			}else{
				roomCuid = accessPointCuid;
			}
			if(roomCuid.startsWith(Accesspoint.CLASS_NAME)){
				//ODF
				DataObjectList odfList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(PhyOdfBOHelper.ActionName.getOdfsByAccessCuid,new BoActionContext(), roomCuid);
			    roomList.addAll(odfList);
				//光接头盒
			    String sql=FiberJointBox.AttrName.relatedLocationCuid+ " = '" + roomCuid + "'";
			    DataObjectList boxList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IFiberJointBoxBO.getFiberJointBoxsBySql",
			            new BoActionContext(), sql);
			    roomList.addAll(roomList.size(),boxList);
			}else{
				//ODF
				DataObjectList odfList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(PhyOdfBOHelper.ActionName.getODFByRoomCuid,new BoActionContext(), roomCuid);
				roomList.addAll(odfList);
			    //MISCRACK
			    DataObjectList miscrackList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(MiscrackBOHelper.ActionName.getMiscRackByCondition,
			        new BoActionContext(), roomCuid);
			    roomList.addAll(roomList.size(),miscrackList);
			    //终端盒
			    String sql=FiberJointBox.AttrName.relatedRoomCuid+ " = '" + roomCuid + "'";
			    DataObjectList boxList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IFiberJointBoxBO.getFiberJointBoxsBySql",
			            new BoActionContext(), sql);
			    roomList.addAll(roomList.size(),boxList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int i = 0; i < roomList.size();i++){
			Map<String,String> map = new HashMap<String, String>();
			 map.put("CUID", roomList.get(i).getCuid());
			 map.put("LABEL_CN", roomList.get(i).getAttrString("LABEL_CN"));
			 map.put("ICON", Icon.getIconByType(roomList.get(i)));
			 results.add(map);
		}
		
		return results;
	}
    
	public List<Map<String,String>> getchildrenbyParentCuid(String Cuid){
		DataObjectList children = new DataObjectList();
		List<Map<String,String>> results = new ArrayList<Map<String,String>>();
	    GenericDO parent = getDuctManagerBO().getObjByCuid(new BoActionContext(), Cuid);
		if(parent instanceof Odf){
   		 children = getPhyOdmBO().getODMByODFCuid(new BoActionContext(), parent.getCuid());
		 }else if(parent instanceof Miscrack){
			 children = getPhyOdmBO().getODMByMiscRackCuid(new BoActionContext(),parent.getCuid());
		 }else if(parent instanceof FiberJointBox){//得到焊点
			 children= getFiberJointPointBO().getFiberJointPointByJoinBoxCuid(new BoActionContext(),parent.getCuid());
		 }else if(parent instanceof Odfmodule){//根据odm得到端子
			 children=getPhyOdfPortBO().getODFPortByODMCuid(new BoActionContext(), parent.getCuid());
		 }
		if(children.size() == 0)
			throw new UserException("没有设备");
		for(int i = 0;i < children.size(); i++){
			Map<String,String> map = new HashMap<String, String>();
			map.put("CUID", children.get(i).getCuid());
			map.put("LABEL_CN", children.get(i).getAttrString("LABEL_CN"));
			if(children.get(i) instanceof Odfport){
				if(children.get(i).getAttrValue(Odfport.AttrName.isConnectedToFiber)!=null && (Boolean)children.get(i).getAttrValue(Odfport.AttrName.isConnectedToFiber)){
					map.put("ICON", "1");//黄色
				}else{
					map.put("ICON", "2");//蓝色
				}
			}
			else{
				 map.put("ICON", Icon.getIconByType(children.get(i)));
			}
			map.put("PARENTCIUD", Cuid);
			results.add(map);
		}
		return results;
	}
	
	public void ChangeSchemeStatus(String Fiber_Cuid,String Wire_Seg_Cuid,String Device_Cuid,String Task_Cuid){
    }
    
    private String portDtoName(GenericDO portDto,GenericDO parent){
    	
        if(parent != null && parent instanceof Fiber){
        	String origCuid = (String) parent.getAttrValue(Fiber.AttrName.origPointCuid);
        	String destCuid = (String) parent.getAttrValue(Fiber.AttrName.destPointCuid);
        	if ((origCuid!= null && origCuid.equals(portDto.getCuid()))||(destCuid!= null && destCuid.equals(portDto.getCuid()))) {
        		String name = "";
        		name = name + "割接前端子:"+portDto.getAttrValue(Odfport.AttrName.labelCn);
        		String deviceCuid = (String) portDto.getAttrValue(Odfport.AttrName.relatedDeviceCuid);
        		if(deviceCuid!=null && !("").equals(deviceCuid)){
        			GenericDO deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid);
        			name = name + "  : " +deviceDto.getAttrValue(Odf.AttrName.labelCn);
        		}
        		String moduleCuid = (String) portDto.getAttrValue(Odfport.AttrName.relatedModuleCuid);
        		if(moduleCuid!=null && !("").equals(moduleCuid)){
        			GenericDO moduleDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), moduleCuid);    
        			name = name + " || :" + moduleDto.getAttrValue(Odfmodule.AttrName.labelCn);  
        		}
        		return name;
        	}else{
        		String name = "";
        		name = name + "预割接端子:"+portDto.getAttrValue(Odfport.AttrName.labelCn);
        		String deviceCuid = (String) portDto.getAttrValue(Odfport.AttrName.relatedDeviceCuid);
        		if(deviceCuid!=null && !("").equals(deviceCuid)){
        			GenericDO deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid);
        			name = name + "  : " +deviceDto.getAttrValue(Odf.AttrName.labelCn);
        		}
        		String moduleCuid = (String) portDto.getAttrValue(Odfport.AttrName.relatedModuleCuid);
        		if(moduleCuid!=null && !("").equals(moduleCuid)){
        			GenericDO moduleDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), moduleCuid);    
        			name = name + " || :" + moduleDto.getAttrValue(Odfmodule.AttrName.labelCn);  
        		}
        		return name;
        	}
        }
    	return null;
    }
	private IPhyOdfPortBO getPhyOdfPortBO() {
		return BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class);
	}

	private IFiberJointPointBO getFiberJointPointBO() {
		return BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class);
	}

	private IPhyOdmBO getPhyOdmBO() {
		return BoHomeFactory.getInstance().getBO(IPhyOdmBO.class);
	}

	private ICutoverSchemeBO getCutoverSchemeBO() {
		return BoHomeFactory.getInstance().getBO(ICutoverSchemeBO.class);
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

	private ICutoverTaskBO getCutoverTaskBO() {
		return BoHomeFactory.getInstance().getBO(ICutoverTaskBO.class);
	}

	private IFiberBO getFiberBo() {
		return BoHomeFactory.getInstance().getBO(IFiberBO.class);
	}
}
