package com.boco.transnms.dmma.hitchtask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.lang.GenericEnum;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.dmma.server.bo.ibo.hitch.IHitchBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.transnms.client.model.base.UserSecurityModel;
import com.boco.transnms.common.bussiness.consts.AlarmEnum;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.Smsinfo;
import com.boco.transnms.common.dto.SysUser;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.dm.InterruptPoint;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;

public class HitchTaskActionBO {

	public Map viewHitchPicture(HttpServletRequest request, String data) {
		Map dataRes = new HashMap();
		//前台实现
		//dataRes.put("patch", "/dmma/hitchtask/111.png");
		return dataRes;
	}

	private void excuteDeleteSql(String taskCuid){
		ITaskBO taskbo = (ITaskBO) DmmaBoFactory.getInstance().getBo();
		taskbo.excuteSql("delete from pda_task  where task_type=3 and cuid='"+taskCuid+"'");
		taskbo.excuteSql("delete from task_to_pda  where related_task_cuid='"+taskCuid+"'");
		taskbo.excuteSql("delete from hitch_task_detail  where related_task_cuid='"+taskCuid+"'");
	}
	public Map doDelete(HttpServletRequest request, String data) {
		JSONArray jsonArray = JSONArray.fromObject(data);
		for(Object jsonObj:jsonArray){
			Map jsonMap = (Map) jsonObj;
			String state = (String) jsonMap.get("ISSAVE");
			String cuid = (String) jsonMap.get("CUID");
			if("是".equals(state)){
				
			}else{
				excuteDeleteSql(cuid);
			}
		}
	
		return new HashMap();
	}

	public Map saveResource(HttpServletRequest request, String data) {
		IHitchBO hitchBO = (IHitchBO) DmmaBoFactory.getInstance().getHitBo();
		JSONArray jsonArray = JSONArray.fromObject(data);
		for(Object jsonObj:jsonArray){
			Map jsonMap = (Map) jsonObj;
		String issave = (String) jsonMap.get("ISSAVE");
		Integer pdataskstate = Integer.valueOf(String.valueOf(DuctEnum.PDA_TASK_STATE_ENUM.getValue((String) jsonMap.get("PDA_TASK_STATE"))));
		String taskCuid = (String) jsonMap.get("CUID");
		if("是".equals(issave)){
			//MessagePane.showErrorMessage("该任务已归档!");
		}else if(pdataskstate!=3){
			//MessagePane.showErrorMessage("该任务正在处理，不能归档!");
		}else{
			Map<String,Object> updateMap = new HashMap<String, Object>();//修改任务归档状态
			updateMap.put("CUID", taskCuid);
			updateMap.put("ISSAVE", 1);
			updateMap.put("TASK_STATE", 2);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			updateMap.put("ARCHIVE_TIME", sdf.format(new Date()));
			hitchBO.updateTaskSaveState(updateMap);
			//MessagePane.showConfirmMessage("归档成功!");
		}
		}
		return new HashMap();
	}

	public Map saveTask(HttpServletRequest request, String data) {
		
		JSONObject jsonObject  =  JSONObject.fromObject(data);
		String taskName = (String) jsonObject.get("TASK_NAME");
		String remark = (String) jsonObject.get("REMARK");
		String smsText = (String) jsonObject.get("SMS_TEXT");
		String startTime = (String) jsonObject.get("START_TIME");
		String endTime = (String) jsonObject.get("END_TIME");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String districtCuid = new ServiceActionContext(request).getRelatedDistrictCuid();
		String districtLabelcn = "";
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = format.parse(startTime);
			endDate = format.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		GenericDO task = initTask( taskName, remark, districtCuid, districtLabelcn, startDate, endDate );
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray pdaDataArr = null;
		if(jsonObject.get("pdaData") != null){
			pdaDataArr = (JSONArray) jsonObject.get("pdaData");
		};
		String phoneno = "";
		for(Object obj:pdaDataArr){
			Map pda = (Map)obj;
			GenericDO pda_task = new GenericDO();
			pda_task.setClassName("TASK_TO_PDA");
			pda_task.setCuid();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("CUID", pda_task.getCuid());
			map.put("RELATED_TASK_CUID", task.getCuid());
			map.put("RELATED_DEVICE_CODE", pda.get("DEVICE_CODE"));
			map.put("TASK_STATE", 0);//任务状态--未接受
			list.add(map);
		}
		ITaskBO taskbo = ((ITaskBO) DmmaBoFactory.getInstance().getBo());
		taskbo.addToTaskToPda(list);
		addHitch(task,new ServiceActionContext(request).getRelatedDistrictCuid());
		sendSms(task,new ServiceActionContext(request),phoneno,smsText);//发送短信
		return new HashMap();
	}
	private IDuctManagerBO getDuctManagerBO() {
	      return (IDuctManagerBO)BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	  }
	private void sendSms(GenericDO task, ServiceActionContext serviceActionContext,String phoneno,String smsText) {
		 try {
	         	getDuctManagerBO().createDMDO(new BoActionContext(), getSmsInfo(serviceActionContext, phoneno, smsText));
	         } catch (Exception ex) {
	             LogHome.getLog().info("短信发送失败:"+ex);
	         }
		
	}	
	private Smsinfo getSmsInfo(ServiceActionContext serviceActionContext,String phoneno,String smsText) {
		Smsinfo smsInfo = new Smsinfo();
		java.sql.Timestamp createTime = new java.sql.Timestamp(
				new java.util.Date().getTime());
		smsInfo.setIntime(createTime);
		smsInfo.setMobilecode(phoneno);
		smsInfo.setMsg(smsText);
		smsInfo.setMsgType(AlarmEnum.SMS_TYPE._typeFault);
		smsInfo.setMsgstate(AlarmEnum.SMS_STATUS._needSend);
		smsInfo.setMobileuser(serviceActionContext.getUserName());
		smsInfo.setSendtimes(0L);
		return smsInfo;
	}
	
	private void addHitch(GenericDO result,String districtCuid) {
		ITaskBO taskbo = ((ITaskBO) DmmaBoFactory.getInstance().getBo());
		Map<String, Object> submap = new HashMap<String, Object>();
		submap.put("CUID", result.getCuid());
		submap.put("LABEL_CN", result.getAttrValue("LABEL_CN"));
		submap.put("REMARK", result.getAttrValue("REMARK"));
		submap.put("FINISH_PERSENT", result.getAttrValue("FINISH_PERSENT"));
		submap.put("TASK_STATE", result.getAttrLong("TASK_STATE"));
		submap.put("TASK_TYPE", Long.valueOf("3"));
		submap.put("IS_CYCLE", result.getAttrLong("IS_CYCLE"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		submap.put("START_TIME", format
				.format(result.getAttrDate("START_TIME")));
		submap.put("END_TIME", format.format(result.getAttrDate("END_TIME")));
		submap.put("TASK_TIME", format.format(new Date()));
		submap.put("RELATED_DISTRICT_CUID", districtCuid);
		submap.put("ISSAVE", result.getAttrInt("ISSAVE"));
		taskbo.addSubTask(submap);
		
	}

	private GenericDO initTask(String taskName,String remark,String districtCuid,String districtLabelcn
			,Date startDate,Date endDate ) {
		GenericDO task = new GenericDO();
		task.setClassName("PDA_TASK");
		task.setCuid();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		task.setAttrValue("LABEL_CN", taskName+"-"+"故障处理"+"-"+format.format(new Date()));
		task.setAttrValue("REMARK", remark);
		task.setAttrValue("FINISH_PERSENT", 0);//任务完成百分比
		task.setAttrValue("TASK_STATE", Long.valueOf("0"));//任务状态--未开始
		task.setAttrValue("TASK_TYPE", Long.valueOf("3"));//任务类型--故障任务
		task.setAttrValue("RESULT_STATE", Long.valueOf("0"));//任务结果状态--未完成
		task.setAttrValue("IS_CYCLE", Long.valueOf("0"));//是否周期任务--否
		task.setAttrValue("RELATED_DISTRICT_CUID",districtCuid);
		task.setAttrValue("RELATED_DISTRICT_NAME",districtLabelcn);
		task.setAttrValue("ISSAVE", 0);//未归档
			Date currentDate = new Date();
			if((startDate.getDay()==currentDate.getDay())&&(startDate.getDay()==endDate.getDay())){
				task.setAttrValue("DURATION", 5);
				Calendar now = Calendar.getInstance();
				Date tempStart = now.getTime();
				now.add(Calendar.DAY_OF_YEAR, 5);
				Date tempEnd = now.getTime();
				task.setAttrValue("START_TIME",tempStart);
				task.setAttrValue("END_TIME",tempEnd);
			}else{
				task.setAttrValue("START_TIME",startDate);
				task.setAttrValue("END_TIME",endDate);
				task.setAttrValue("DURATION", endDate.getDay()-startDate.getDay());
			}
		return task;
		
	}

	public Map queryInterrupt(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		InterruptPoint fqc = new InterruptPoint();
		String relatedWireSystemCuid = jsonObject.getString("relatedWireSystemCuid");
		String relatedWireSegCuid =  jsonObject.getString("relatedWireSegCuid");
		String direction =  jsonObject.getString("direction");
		Double interruptDistance =  Double.valueOf(jsonObject.getString("cljl"));
		
		fqc.setRelatedWireSystemCuid(relatedWireSystemCuid);
		
		
		fqc.setRelatedWireSegCuid(direction.split(":")[0]);
        fqc.setDirection(Boolean.parseBoolean(direction.split(":")[1]));
		fqc.setInterruptDistance(interruptDistance);
		Map<String, WireSeg> wireSegMap = new HashMap<String, WireSeg>();
		DataObjectList wireSegs = getWireSegBO().getWireSegsByWireSystemCuid(
                       new BoActionContext(), relatedWireSystemCuid);
		for (GenericDO dto : wireSegs) {
	            if (!(dto instanceof WireSeg)) continue;
	            WireSeg segment = (WireSeg)dto;
	            wireSegMap.put(segment.getCuid(), segment);
		 }
		  String[] segmentsInfo = direction.split(";");
		  String segmentInfo = null;
	        if (segmentsInfo.length > 0) {
	            for (int i = 0; i < segmentsInfo.length; i++) {
	                String[] segment = segmentsInfo[i].split(":");
	                if (segment.length == 2) {
	                    WireSeg wireSegment = wireSegMap.get(segment[0]);
	                    if (wireSegment != null &&
	                        wireSegment.getLength() >= interruptDistance) {
	                        segmentInfo = segmentsInfo[i];
	                        break;
	                    } else if (wireSegment != null &&
	                            wireSegment.getLength() < interruptDistance){
	                        interruptDistance -= wireSegment.getLength();
	                    }
	                }
	            }
	        }
		
		InterruptPoint queryResult =  getWireSystemBO().getInterruptPoint(
                new BoActionContext(), fqc);
		 List<String> errInfo = (List<String>) queryResult.getAttrValue("ERROR_INFO");
		 Map resault = new HashMap();
		 if (errInfo != null && !errInfo.isEmpty()) {
	            String errorHint = errInfo.get(0);
	            for (int i = 1; i < errInfo.size(); i++) {
	                errorHint += "；\n" + errInfo.get(i);
	            }
	            resault.put("SUCCESS", false);
	            resault.put("MSG", errorHint);
	        }else{
	        	   resault.put("SUCCESS", true);
	        	   resault.put("DATA", formatQueryResult(queryResult));
	        }
		return resault;
	}

	 private Map formatQueryResult(InterruptPoint result) {
		    Map tempData = new HashMap();
		    tempData.put("ORIG_POINT_POSITION", result.getAttrValue("ORIG_POINT_POSITION"));
		    tempData.put("DEST_POINT_POSITION", result.getAttrValue("DEST_POINT_POSITION"));
		    tempData.put(InterruptPoint.AttrName.longitude, result.getAttrValue("LONGITUDE"));
		    tempData.put(InterruptPoint.AttrName.latitude, result.getAttrValue("LATITUDE"));
		    tempData.put("ROTE_ORIG_POINT_LONG", result.getAttrValue("ROTE_ORIG_POINT_LONG"));
		    tempData.put("ORIG_POINT_LONGTH", result.getAttrValue("ORIG_POINT_LONGTH"));
		    tempData.put("ROTE_DEST_POINT_LONG", result.getAttrValue("ROTE_DEST_POINT_LONG"));
		    tempData.put("DEST_POINT_LONGTH", result.getAttrValue("DEST_POINT_LONGTH"));
		    return tempData;
	}

	private IWireSystemBO getWireSystemBO() {
	        return (IWireSystemBO)BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
	    }

	public Map getFirstWireSegInfo(HttpServletRequest request, String data) {
		//取得第一条测量方向的信息
    	DataObjectList wireSegs = null;
    	String wireSystemCuid =data;
    	 GenericEnum<String>  resaultData = new GenericEnum<String>();
    	 try {
             wireSegs = getWireSegBO().getWireSegsByWireSystemCuid(
                     new BoActionContext(), wireSystemCuid);
             resaultData = registerFirstWireSeg(wireSegs);
         } catch (Exception ex) {
             LogHome.getLog().error("根据光缆系统获取光交设备信息出错！", ex);
         }
    	 String[] names = resaultData.getAllNames();
    	 if(names == null){
    		 names = new String[0];
    	 }
    	 List<List> datas = new ArrayList<List>(); 
    	 for(String name:names){
    		 List<String> d = new LinkedList<String>();
    		 d.add(name);
    		 d.add(resaultData.getValue(name));
    		 datas.add(d);
    	 }
    	Map dataMap =  new HashMap();
    	dataMap.put("_DATA", datas);
    	dataMap.put("_DATA", datas);
    	 return dataMap;
	}
	  private Map initWireSegEnum2(String wireSystemCuid,String pointDevice) {
	        // 该光缆已经加载过光缆段数据
	        	//start add  张岩 2012.11.12 当点击切换光交设备的时候，同时更新测量方向
		  GenericEnum  	genericEnum = null; 
		  try {
                 DataObjectList wireSegs = getWireSegBO().getWireSegsByWireSystemCuid(
                         new BoActionContext(), wireSystemCuid);
                 //取得测量方向列表
                 genericEnum = registerWireSegEnum(wireSegs,pointDevice);
             } catch (Exception ex) {
                 LogHome.getLog().error("根据光交设备获取测量方向出错！", ex);
             }
			 String[] names = genericEnum.getAllNames();
	    	 if(names == null){
	    		 names = new String[0];
	    	 }
	    	 List<List> datas = new ArrayList<List>(); 
	    	 for(String name:names){
	    		 List<String> d = new LinkedList<String>();
	    		 d.add(name);
	    		 d.add((String) genericEnum.getValue(name));
	    		 datas.add(d);
	    	 }
	    	Map dataMap =  new HashMap();
	    	dataMap.put("_DATA", datas);
	    	dataMap.put("_DATA", datas);
	         return dataMap;
	    }

	    /**
	     * 设置测量方向列表
	     * @param wireSegs 光缆段
	     * @param pointDevice 选择的光交设备
	     */
	    private GenericEnum registerWireSegEnum(DataObjectList wireSegs,String pointDevice) {
	        if (!(wireSegs != null && !wireSegs.isEmpty())) return null;
	        Map<String, String> endPointCuidMap = new HashMap<String, String>();
	        Map<String, WireSeg> wireSegMap = new HashMap<String, WireSeg>();
	        for (GenericDO dto : wireSegs) {
	            if (!(dto instanceof WireSeg)) continue;
	            WireSeg segment = (WireSeg)dto;
	            wireSegMap.put(segment.getCuid(), segment);
	            endPointCuidMap.put(segment.getOrigPointCuid(), segment.getOrigPointCuid());
	            endPointCuidMap.put(segment.getDestPointCuid(), segment.getDestPointCuid());
	           
	        }
	        DataObjectList endPoints = (DataObjectList) getDuctManagerBO().getObjsByCuid(
	                new BoActionContext(),
	                endPointCuidMap.keySet().toArray(new String[endPointCuidMap.size()]));
	        for (GenericDO endPoint : endPoints){
	            String labelCn = endPoint.getAttrString(GenericDO.AttrName.labelCn);
	            endPointCuidMap.put(endPoint.getCuid(), labelCn);
	            
	        }
	        //start modify 张岩 2012.11.9 断点查询
	        GenericEnum directionEnum = getQueryInfoEnum(wireSegs, endPointCuidMap,pointDevice,wireSegMap);
	        //end  modify 张岩 2012.11.9 断点查询
	        EnumTypeManager.getInstance().unRegisterEnumTypes("Fout_Scale");
	        EnumTypeManager.getInstance().registerEnumTypes("Fout_Scale", directionEnum);
	        return directionEnum;
	        
	        
	    }
	    
	    /**
	     * 设置测量距离列表的信息
	     * @param wireSegs 光缆段
	     * @param endPointMap 光交设备信息
	     * @param pointDevice 选择的点设备
	     */
	    private GenericEnum getQueryInfoEnum(DataObjectList wireSegs, Map<String, String> endPointMap,String pointDevice,Map<String, WireSeg> wireSegMap) {
	        GenericEnum<String> directionEnum = new GenericEnum<String>();
	        //start add  张岩 2012.11.11 判断光交设备在光缆中是起点还是终点，然后设置测量方向
	        String joinWireSegId = "" ;
	        for (GenericDO dto : wireSegs) {
	        	 WireSeg segment = (WireSeg)dto;
	        	 if(pointDevice.equals(segment.getOrigPointCuid())){
	        		 //光缆段ID+true,为了处理direction
	        		joinWireSegId =  segment.getCuid() + ":" + "true";
	        	} else if(pointDevice.equals(segment.getDestPointCuid())){
	        		//光缆段ID+false,为了处理direction
	        		joinWireSegId = segment.getCuid() + ":" + "false";
	            } else {
	            }
	        	 if(!"".equals(joinWireSegId.trim())){
	        		 directionEnum.putEnum(joinWireSegId, getPathName(joinWireSegId, endPointMap,pointDevice,wireSegMap));
	        	 } else {
	        		 
	        	 }
	        	  
	        }
	        //end add  张岩 2012.11.11 判断光交设备在光缆中是起点还是终点，然后设置测量方向
	        	 
	        //start modify  张岩 2012.11.11 去掉查询光缆系统中所有测量方向
//	        List<String> paths = getPaths(wireSegs);
//	        if (paths != null && !paths.isEmpty()) {
//	            for (String path : paths) {
//	                directionEnum.putEnum(path, getPathName(path, endPointMap));
//	            }
//	        }
	        //end modify  张岩 2012.11.11 去掉查询光缆系统中所有测量方向
	        return directionEnum;
	    }
	    
	    /**
	     * 取得测量方向列表内容
	     * @param path 光缆段信息
	     * @param endPointMap 光交设备列表
	     * @param pointDevice 选择的光交设备
	     */
	    private String getPathName(String path, Map<String, String> endPointMap,String pointDevice,Map<String, WireSeg> wireSegMap) {
	    	//start modify  张岩 2012.11.12 修改测量方向，根据传入的光交设备
//	        String origPointCuid = null;
//	        String destPointCuid = null;
	    	
	        String[] segments = path.split(";");
	        if (segments.length >= 1) {
	            String[] origSegment = segments[0].split(":");
	            WireSeg segment = wireSegMap.get(origSegment[0]);
	            //如果传入的光交设备，在光缆段的起点位置，则起点连接这个光缆段的终点构成一条测量方向
	            //如果传入的光交设备，在光缆段的终点位置，则终点连接这个光缆段的起点构成一条测量方向
	            if(pointDevice.equals(segment.getOrigPointCuid())){
	            	return endPointMap.get(segment.getOrigPointCuid()) + "--" +
	                	   endPointMap.get(segment.getDestPointCuid());
	            } else if(pointDevice.equals(segment.getDestPointCuid())){
	            	return  endPointMap.get(segment.getDestPointCuid())+ "--" +
	         	   endPointMap.get(segment.getOrigPointCuid());
	            } else {
	            	
	            }
	          //end modify  张岩 2012.11.12 修改测量方向，根据传入的光交设备
	          //start modify  张岩 2012.11.12 修改测量方向，根据传入的光交设备
//	            if (origSegment.length == 2) {
//	                WireSeg segment = wireSegMap.get(origSegment[0]);
//	                origPointCuid = Boolean.parseBoolean(origSegment[1]) ?
//	                    segment.getOrigPointCuid() : segment.getDestPointCuid();
//	            }
//	            String[] destSegment = segments[segments.length - 1].split(":");
//	            if (destSegment.length == 2) {
//	                WireSeg segment = wireSegMap.get(destSegment[0]);
//	                destPointCuid = Boolean.parseBoolean(destSegment[1]) ?
//	                    segment.getDestPointCuid() : segment.getOrigPointCuid();
//	            }
//	        if(cuidGTemp.equals(segment.getOrigPointCuid())){
//	        	String aa = segment.getOrigPointCuid() + "--" + segment.getDestPointCuid();
//	        } else if(cuidGTemp.equals(segment.getDestPointCuid())){
//	        	String aa = segment.getDestPointCuid() + "--" + segment.getOrigPointCuid();
//	        }
//	        if (origPointCuid != null && destPointCuid != null) {
//	            return endPointMap.get(origPointCuid) + "--" +
//	                   endPointMap.get(destPointCuid);
//	        }
	            //end modify  张岩 2012.11.12 修改测量方向，根据传入的光交设备
	            
	        } else {
	            LogHome.getLog().warn("There are no segments in the path!");
	        }
	        return path;
	    }
	private IWireSegBO getWireSegBO() {
		return DmmaBoFactory.getInstance().getWireSegBO();
	}
	 /**
	    * 初期化光交设备下拉列表
	    */
	   
	   private GenericEnum<String> registerFirstWireSeg(DataObjectList wireSegs){
		   // Map<String, WireSeg> wireSegMap = new HashMap<String, WireSeg>();
		   if (!(wireSegs != null && !wireSegs.isEmpty())) return null;
	       Map<String, String> endPointCuidMap = new HashMap<String, String>();
	       GenericEnum<String> firstFiberDeviceEnum = new GenericEnum<String>();
	       for (GenericDO dto : wireSegs) {
	           if (!(dto instanceof WireSeg)) continue;
	           WireSeg segment = (WireSeg)dto;
	           //wireSegMap.put(segment.getCuid(), segment);
	           endPointCuidMap.put(segment.getOrigPointCuid(), segment.getOrigPointCuid());
	           endPointCuidMap.put(segment.getDestPointCuid(), segment.getDestPointCuid());
	          
	       }
	       DataObjectList endPoints = (DataObjectList) getDuctManagerBO().getObjsByCuid(
	               new BoActionContext(),
	               endPointCuidMap.keySet().toArray(new String[endPointCuidMap.size()]));
	       for (GenericDO endPoint : endPoints){
	           String labelCn = endPoint.getAttrString(GenericDO.AttrName.labelCn);
	           endPointCuidMap.put(endPoint.getCuid(), labelCn);
	           firstFiberDeviceEnum.putEnum(endPoint.getCuid(), labelCn);
	       }
	       
	       //EnumTypeManager.getInstance().unRegisterEnumTypes("Fiber_Device");
	      // EnumTypeManager.getInstance().registerEnumTypes("Fiber_Device", firstFiberDeviceEnum);
	     return firstFiberDeviceEnum;
	   }

	public Map initWireSegEnum(HttpServletRequest request, String data) {
		JSONObject jonObject = JSONObject.fromObject(data);
		return initWireSegEnum2(jonObject.getString("wireSystemCuid"),jonObject.getString("pointDeviceCuid"));
	}

}
