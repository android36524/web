package com.boco.transnms.dmma.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.dmma.server.bo.ibo.quartz.IQuartzBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.DMHelper;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.helper.dm.WireToDuctLineBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;



public class TaskActionBO {
	
	private DataObjectList getSubTaskByPTaskCuid(String pTaskCuid){
		ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
		String sql = "select pt.*,tp.task_state as pda_task_state,tp.accept_time as pda_accept_time," +
		"tp.action_time as pda_action_time, tp.actual_end_time,pd.user_name as pda_user_name " +
		"from pda_task pt, task_to_pda tp, pda_p_task ppt, pda_device pd " +
		"where tp.related_task_cuid = pt.cuid " +
		"and tp.related_device_code = pd.device_code " +
		"and pt.related_p_cuid = ppt.cuid and ppt.cuid = '"+pTaskCuid+"' " +
		"order by pt.task_time";
		return taskbo.querySubTask(sql);
	}
	
	private String generatDynamicSql(String tableName,String field,String taskCuid){
		return "delete from "+tableName+" where "+field+"='"+taskCuid+"'";
	}
	
	private void executeDeleteSubTask(String taskCuid){
		ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
		taskbo.excuteSql(generatDynamicSql("TEMP_TASK_RELATEDSYSTEM","taskcuid",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TEMP_TASK_RELATESEG","taskcuid",taskCuid));
		taskbo.excuteSql(generatDynamicSql("PDA_TASK","cuid",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TASK_TO_PDA","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("PDA_POINT_CHANG_LOC","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("PDA_LOC_POINT","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("HIDDEN_DANGER","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("INSPECT_TASK_DETAIL","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("PDA_INSPECT_TASK_DETAIL","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TEMP_INSPECT_TASK_DETAIL","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TEMP_PDA_POINT_CHANG_LOC","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TEMP_TASK_TO_PDA","RELATED_TASK_CUID",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TASKPOINT_TO_FIBERJOINTBOX","RELATED_TASK_CUID",taskCuid));
	}
	private void excuteDeleteSql(String taskCuid){
		ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
		DataObjectList ls = getSubTaskByPTaskCuid(taskCuid);
		for(GenericDO gdo:ls){
			executeDeleteSubTask(gdo.getCuid());
		}
		taskbo.excuteSql(generatDynamicSql("PDA_P_TASK","cuid",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TEMP_TASK_RELATEDSYSTEM","taskcuid",taskCuid));
		taskbo.excuteSql(generatDynamicSql("TEMP_TASK_RELATESEG","taskcuid",taskCuid));
	}
	
	public Map doDelete(HttpServletRequest request, String data) {
		JSONArray jsonArray = JSONArray.fromObject(data);
		for(Object obj:jsonArray){
			Map delData = (Map) obj;
			excuteDeleteSql((String) delData.get("CUID"));
		}
		return new HashMap();
	}

	private void relatedAllPdaToAllPoints(DataObjectList pdalist,List<GenericDO> pointList){
		List	pointMapList = new ArrayList();
		for(GenericDO pda:pdalist){
			Map<String,Object> map = new HashMap<String, Object>();
			String pdacode = pda.getAttrValue("DEVICE_CODE").toString();
			map.put("PDACODE", pdacode);
			map.put("USERNAME", pda.getAttrValue("USER_NAME").toString());
			map.put("POINTS", pointList);
			pointMapList.add(map);
		}
	}
private void addAndSendTask(GenericDO task, List<GenericDO> systemList,DataObjectList pdaList,String taskName,List<GenericDO>  segList,List<GenericDO> pointList2){
	ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> submap = new HashMap<String,Object>();
		map.put("CUID", task.getCuid());
		submap.put("REMARK", task.getAttrString("REMARK"));
		map.put("REMARK", task.getAttrString("REMARK"));
		submap.put("FINISH_PERSENT", task.getAttrValue("FINISH_PERSENT"));
		submap.put("TASK_STATE", task.getAttrLong("TASK_STATE"));
		map.put("TASK_TYPE", task.getAttrLong("TASK_TYPE"));
		submap.put("TASK_TYPE",task.getAttrLong("TASK_TYPE"));
		submap.put("RELATED_P_CUID", task.getCuid());
		map.put("IS_CYCLE", task.getAttrLong("IS_CYCLE"));
		submap.put("IS_CYCLE", task.getAttrLong("IS_CYCLE"));
		Long cycle = task.getAttrLong("IS_CYCLE");
		map.put("LABEL_CN", task.getAttrString("LABEL_CN"));
		if(cycle==0){//实时任务
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			submap.put("TASK_TIME", format.format(new Date()));
			map.put("TASK_TIME", format.format(new Date()));
			submap.put("START_TIME", format.format(task.getAttrDate("START_TIME")));
			submap.put("END_TIME", format.format(task.getAttrDate("END_TIME")));
		}else if(cycle==1){//周期任务
			Long frequencetype = task.getAttrLong("FREQUENCE_TYPE");
			if(frequencetype==1){
				map.put("FREQUENCE_TYPE", 1);
				String tempweek=task.getAttrString("FREQUENCE");
				int weekvalue=2;
				if("周一".equals(tempweek)){
					weekvalue=2;
				}else if("周二".equals(tempweek)){
					weekvalue=3;
				}else if("周三".equals(tempweek)){
					weekvalue=4;
				}else if("周四".equals(tempweek)){
					weekvalue=5;
				}else if("周五".equals(tempweek)){
					weekvalue=6;
				}else if("周六".equals(tempweek)){
					weekvalue=7;
				}else if("周日".equals(tempweek)){
					weekvalue=1;
				}
				map.put("FREQUENCE", weekvalue);
			}else if(frequencetype==2){
				map.put("FREQUENCE_TYPE", 2);
				map.put("FREQUENCE", Integer.valueOf(task.getAttrValue("FREQUENCE")+""));
			}else if(frequencetype==3){
				map.put("FREQUENCE_TYPE", 3);
				map.put("FREQUENCE", Integer.valueOf(task.getAttrValue("FREQUENCE")+""));
				map.put("SENDMONTH", Integer.valueOf(task.getAttrValue("SENDMONTH")+""));
			}else if(frequencetype==4){
				map.put("FREQUENCE_TYPE", 4);
				map.put("FREQUENCE", Integer.valueOf(task.getAttrValue("FREQUENCE")+""));
				map.put("SENDMONTH", Integer.valueOf(task.getAttrValue("SENDMONTH")+""));
			}else if(frequencetype==5){
				map.put("FREQUENCE_TYPE", 5);
				map.put("FREQUENCE", Integer.valueOf(task.getAttrValue("FREQUENCE")+""));
				map.put("SENDMONTH", Integer.valueOf(task.getAttrValue("SENDMONTH")+""));
			}
			submap.put("TASK_TIME", task.getAttrString("TASK_TIME"));
			map.put("TASK_TIME", task.getAttrString("TASK_TIME"));
		}
		
		map.put("DURATION", task.getAttrInt("DURATION"));
		map.put("RELATED_DISTRICT_CUID",task.getAttrValue("RELATED_DISTRICT_CUID"));
		taskbo.addTask(map);
		
		
		//派发任务到pdaserver
		if (cycle == 1) {
			Long frequencetype = task.getAttrLong("FREQUENCE_TYPE");
			if(frequencetype>2){
				int month = Integer.valueOf(map.get("SENDMONTH")+"");
				int day = Integer.valueOf(map.get("FREQUENCE")+"");
				sendTask(submap,month,day,task,segList,pdaList,systemList,pointList2);
			}else{
				
				if(frequencetype==1){
					Calendar temp = Calendar.getInstance();
					int weekday = Integer.valueOf(map.get("FREQUENCE")+"");
					temp.set(Calendar.DAY_OF_WEEK, weekday);
					int month = temp.get(Calendar.MONTH); 
					int day = temp.get(Calendar.DAY_OF_MONTH);
					sendTask(submap,month,day,task,segList,pdaList,systemList,pointList2);
				}else if(frequencetype==2){
					int month = Calendar.getInstance().get(Calendar.MONTH);
					int day = Integer.valueOf(map.get("FREQUENCE")+"");
					sendTask(submap,month,day,task,segList,pdaList,systemList,pointList2);
				}
			}
			
			addRelateSystem(systemList,task);//添加系统到关联系统表
		} else {
			sendRealTimeTask(submap,task,true,pdaList,systemList,taskName,segList);
		}
	}
private void addRelateSystem(List<GenericDO> systemList,GenericDO task){
	ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
	for(GenericDO gdo:systemList){
		Map<String,Object> sysmap = new HashMap<String,Object>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sysmap.put("SEND_TIME", format.format(new Date()));
		sysmap.put("TASKCUID", task.getCuid());
		sysmap.put("RELATED_SYSTEM", gdo.getCuid());
		taskbo.addRelatedSystem(sysmap);
	}
	
}
private List<GenericDO> getPointsBySendedPda(GenericDO pda,	List<GenericDO> systemList){
	List<GenericDO> points  = new ArrayList<GenericDO>();
	for(int i=0;i<systemList.size();i++){
		Map<String,Object> map = (Map<String, Object>) systemList.get(i);
		String pdacode = map.get("PDACODE").toString();
		String pdac = pda.getAttrString("DEVICE_CODE");
		if(pdacode.equals(pdac)){
			@SuppressWarnings("unchecked")
			List<GenericDO> pointList = (List<GenericDO>)map.get("POINTS");
			for(GenericDO point:pointList){
				if(!points.contains(point)){
					if(!point.containsAttr("PDACODE")){
						point.setAttrValue("PDACODE", pdacode);
					}
					points.add(point);
				}
				
			}
		}
	}
	return points;
}


private List<GenericDO> getSplitSegList(List<GenericDO> pointList,List<GenericDO>  segList){
	List<GenericDO> splitSegList = new ArrayList<GenericDO>();
	for(GenericDO point:pointList){
		String pointCuid = point.getCuid();
		for(GenericDO seg:segList){
			String orig_point_cuid = seg.getAttrString("ORIG_POINT_CUID");
			if(pointCuid.equals(orig_point_cuid)){
				splitSegList.add(seg);
			}
		}
	}
	return splitSegList;
}

/**
 * 计算巡线长度
 * @param segList
 * @return
 */
private double getTaskLength(List<GenericDO> segList){
	double length = 0.0d;
	for(GenericDO seg:segList){
		length+=seg.getAttrDouble("LENGTH");
	}
	String len = Double.valueOf(length)/1000+"";
	if(len.length()>7){
		return Double.valueOf(len.substring(0, 5));
	}else{
		return Double.valueOf(len);
	}
}


/**
 * 派发实时任务
 * @param submap
 */
private void sendRealTimeTask(Map<String,Object> submap,GenericDO task,boolean isrealtime,DataObjectList pdaList,List<GenericDO> systemList,String taskName,List<GenericDO>  segList){
	ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
	List<Map<String,Object>> pda_list = new ArrayList<Map<String,Object>>();
	List<String> subtaskcuidlist = new ArrayList<String>();
	for(GenericDO pda:pdaList){
		District district = (District)pda.getAttrValue("RELATED_DISTRICT_CUID");
		String pdaDistrictCuid = district.getCuid();
		List<GenericDO> pointList = getPointsBySendedPda(pda,systemList);
		String pTaskDistrictCuid = task.getAttrString("RELATED_DISTRICT_CUID");
		if(pointList.size()>0 && pdaDistrictCuid.startsWith(pTaskDistrictCuid)){
			GenericDO t = new GenericDO();
			t.setClassName("PDA_TASK");
			t.setCuid();
			String tasktempcuid =t.getCuid()+"-"+pda.getAttrValue("DEVICE_CODE");
			submap.put("CUID", tasktempcuid);
			if(isrealtime){
				submap.put("LABEL_CN", task.getAttrString("LABEL_CN")+"-"+pda.getAttrValue("USER_NAME"));
			}else{
				SimpleDateFormat formant_labelcn = new SimpleDateFormat("yyyy-MM-dd");
				submap.put("LABEL_CN", "巡检任务-"+formant_labelcn.format(new Date())+"-"+taskName+"-"+pda.getAttrValue("USER_NAME"));
			}
			
			submap.put("ROUTELENGTH", getTaskLength(getSplitSegList(pointList,segList)));//添加巡检长度
			submap.put("RELATED_DISTRICT_CUID", pdaDistrictCuid);
			taskbo.addSubTask(submap);
			
			
			subtaskcuidlist.add(tasktempcuid);
			
			GenericDO pda_task = new GenericDO();
			pda_task.setClassName("TASK_TO_PDA");
			pda_task.setCuid();
			
			Map<String,Object> pda_map = new HashMap<String,Object>();
			pda_map.put("CUID",  pda_task.getCuid());
			pda_map.put("RELATED_TASK_CUID", tasktempcuid);
			pda_map.put("RELATED_DEVICE_CODE", pda.getAttrValue("DEVICE_CODE"));
			pda_map.put("TASK_STATE", 0);//任务状态--未处理
			pda_list.add(pda_map);
			
			
			List<Map<String,Object>> resultlist = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> pdapointlist = new ArrayList<Map<String,Object>>();
			List<Map<String,String>> pointInfoList = new ArrayList<Map<String,String>>();
			for(GenericDO point:pointList){
				GenericDO inspect_task_detail = new GenericDO();
				inspect_task_detail.setClassName("INSPECT_TASK_DETAIL");
				inspect_task_detail.setCuid();
				GenericDO pda_point_change_loc = new GenericDO("PDA_POINT_CHANGE_LOC");
				pda_point_change_loc.setCuid();
				Map<String,Object> pdapointmap = new HashMap<String,Object>();
				pdapointmap.put("CUID", pda_point_change_loc.getCuid());
				pdapointmap.put("RELATED_TASK_CUID", tasktempcuid);
				pdapointmap.put("RELATED_POINT_CUID", point.getCuid());
				pdapointmap.put("ORIGINAL_LONGITUDE", point.getAttrDouble("LONGITUDE"));
				pdapointmap.put("ORIGINAL_LATITUDE", point.getAttrDouble("LATITUDE"));
				pdapointmap.put("POINT_LABEL_CN", point.getAttrString("LABEL_CN"));
				pdapointmap.put("RELATED_DEVICE_CODE",point.getAttrValue("PDACODE"));//关联设备编码修改2012-07-02
				pdapointlist.add(pdapointmap);
				Map<String,String> pointInfoMap = new HashMap<String, String>();
				pointInfoMap.put("pointid", point.getCuid());
				pointInfoMap.put("pointname", point.getAttrString("LABEL_CN"));
				pointInfoMap.put("taskid", tasktempcuid);
				pointInfoList.add(pointInfoMap);
				Map<String,Object> pointmap = new HashMap<String,Object>();
				pointmap.put("CUID", inspect_task_detail.getCuid());
				pointmap.put("RELATED_TASK_CUID", tasktempcuid);
				pointmap.put("POINT_CUID", point.getCuid());
				pointmap.put("RELATED_DEVICE_CODE",point.getAttrValue("PDACODE"));//关联设备编码修改2012-07-02
				String must_point=point.getAttrString("IS_MUST_POINT");
				if("是".equals(must_point)){
					pointmap.put("IS_MUST_REACH", 1);
				}else{
					pointmap.put("IS_MUST_REACH", 0);
				}
				resultlist.add(pointmap);
			}
			//入库管接头盒操作
			taskbo.insertFiberBoxTask(pointInfoList);
			taskbo.addToInspectTaskDetail(resultlist);
			taskbo.addToPDA_POINT_CHANG_LOC(pdapointlist);
		}
		
		
	}
	taskbo.addToTaskToPda(pda_list);
}
	public boolean currentTimeIsAfterSendTime(int month,int day,String task_time){
		try{
			Calendar sendTime = Calendar.getInstance();
			sendTime.set(Calendar.MONTH,month-1);
			sendTime.set(Calendar.DAY_OF_MONTH,day);
			Date sendDate = sendTime.getTime();
			String sendFormat = "yyyy-MM-dd "+task_time+":00";
			SimpleDateFormat sendDateFormat = new SimpleDateFormat(sendFormat);
			long sendtime = sendDateFormat.parse(sendDateFormat.format(sendDate)).getTime();
			SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			long currenttime = currentDateFormat.parse(currentDateFormat.format(new Date())).getTime();
			System.out.println(currenttime +"    "+sendtime);
			if(currenttime>sendtime){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 根据当前时间与任务派发时间选择派发周期任务还是实时任务
	 * @param submap
	 * @param month
	 * @param day
	 */
	private void sendTask(Map<String,Object> submap,int month,int day,GenericDO task,List<GenericDO> segList,DataObjectList pdaList,List<GenericDO> pointList2,List<GenericDO> systemList){
		Long frequencetype = task.getAttrLong("FREQUENCE_TYPE");
		String task_time = task.getAttrString("TASK_TIME");
		if(frequencetype>1 && currentTimeIsAfterSendTime(month, day,task_time)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				submap.put("TASK_TIME", sdf.format(new Date()));
				Calendar now = Calendar.getInstance();
				Date tempStart = now.getTime();
				Calendar cal = Calendar.getInstance(); 
				cal.setTime(new Date()); 
				cal.set(Calendar.DAY_OF_MONTH, 1); 
				cal.add(Calendar.MONTH, 1); 
				cal.add(Calendar.DATE, -1); 
				SimpleDateFormat endTimeSdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
				Date tempEnd;
				try {
					tempEnd = sdf.parse(endTimeSdf.format(cal.getTime()));
					submap.put("START_TIME", sdf.format(tempStart));
					submap.put("END_TIME", sdf.format(tempEnd));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				sendRealTimeTask(submap,task,false, null, null, task_time, null);
				sendCycleTask(task,segList,pdaList,pointList2,systemList);
		}else{
			sendCycleTask(task,segList,pdaList,pointList2,systemList);
		}
	}
	
	/**
	 * 派发周期任务
	 */

	private void sendCycleTask(GenericDO task,List<GenericDO> segList,DataObjectList pdaList,List<GenericDO> systemList,List<GenericDO> pointList2){
		Map<String,Object> taskmap = new HashMap<String,Object>();
		String temptime=task.getAttrString("TASK_TIME").toString();
		String time="";
		String secondTime="";
		String tempSecondTime=temptime.split(":")[1];
		if(tempSecondTime.startsWith("0")){
			secondTime=tempSecondTime.substring(1);
		}else{
			secondTime=tempSecondTime;
		}
		if(temptime.startsWith("0")){
			time=temptime.substring(1, 2);
		}else{
			time=temptime.substring(0, 2);
		}
		
		taskmap.put("pTask", task.getCuid());
		taskmap.put("taskName", task.getAttrString("LABEL_CN")+task.getCuid()+"task");
		taskmap.put("triggerName", task.getAttrString("LABEL_CN")+task.getCuid()+"trigger");
		taskmap.put("ROUTELENGTH", getTaskLength(segList));//添加巡检长度
		Long frequencetype = task.getAttrLong("FREQUENCE_TYPE");
		if(frequencetype==1){
			String tempweek=task.getAttrString("FREQUENCE");
			String weekvalue="";
			if("周一".equals(tempweek)){
				weekvalue="2";
			}else if("周二".equals(tempweek)){
				weekvalue="3";
			}else if("周三".equals(tempweek)){
				weekvalue="4";
			}else if("周四".equals(tempweek)){
				weekvalue="5";
			}else if("周五".equals(tempweek)){
				weekvalue="6";
			}else if("周六".equals(tempweek)){
				weekvalue="7";
			}else if("周日".equals(tempweek)){
				weekvalue="1";
			}
			
			String cron="0 "+secondTime+" "+time+" ? * "+weekvalue;
			System.out.println(cron);
			taskmap.put("scheduTactic", cron);
		}else if(frequencetype==3){
			String dayvalue=Integer.valueOf(task.getAttrValue("FREQUENCE")+"")+"";
			String month = task.getAttrValue("SENDMONTH")+"";
			String cron="0 "+secondTime+" "+time+" "+dayvalue+" "+month+"/2"+" ?" ;
			System.out.println(cron);
			taskmap.put("scheduTactic", cron);
		}else if(frequencetype==4){
			String dayvalue=Integer.valueOf(task.getAttrValue("FREQUENCE")+"")+"";
			String month = task.getAttrValue("SENDMONTH")+"";
			String cron="0 "+secondTime+" "+time+" "+dayvalue+" "+month+"/3"+" ?" ;
			System.out.println(cron);
			taskmap.put("scheduTactic", cron);
		}else if(frequencetype==5){
			String dayvalue=Integer.valueOf(task.getAttrValue("FREQUENCE")+"")+"";
			String month = task.getAttrValue("SENDMONTH")+"";
			String cron="0 "+secondTime+" "+time+" "+dayvalue+" "+month+"/6"+" ?" ;
			System.out.println(cron);
			taskmap.put("scheduTactic", cron);
		}else{
			String dayvalue=Integer.valueOf(task.getAttrValue("FREQUENCE")+"")+"";
			String cron="0 "+secondTime+" "+time+" "+dayvalue+" * ?";
			System.out.println(cron);
			taskmap.put("scheduTactic", cron);
		}
		ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();

		
		List<Map<String,Object>> pda_list = new ArrayList<Map<String,Object>>();
		for(GenericDO pda:pdaList){
			District district = (District)pda.getAttrValue("RELATED_DISTRICT_CUID");
			String pdaDistrictCuid = district.getCuid();
			List<GenericDO> pointList = getPointsBySendedPda(pda,systemList);
			String pTaskDistrictCuid = task.getAttrString("RELATED_DISTRICT_CUID");
			if(pointList.size()>0 && pdaDistrictCuid.startsWith(pTaskDistrictCuid)){
				GenericDO pda_task = new GenericDO();
				pda_task.setClassName("TASK_TO_PDA");
				pda_task.setCuid();
				
				Map<String,Object> pda_map = new HashMap<String,Object>();
				pda_map.put("CUID",  pda_task.getCuid());
				pda_map.put("RELATED_TASK_CUID", task.getCuid());
				pda_map.put("RELATED_DEVICE_CODE", pda.getAttrValue("DEVICE_CODE")+"&&"+getTaskLength(getSplitSegList(pointList2,segList)));
				pda_map.put("TASK_STATE", Integer.valueOf(0));//任务状态--未处理
				pda_list.add(pda_map);
				
				
				List<Map<String,Object>> resultlist = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> pdapointlist = new ArrayList<Map<String,Object>>();
				for(GenericDO point:pointList){
					GenericDO inspect_task_detail = new GenericDO();
					inspect_task_detail.setClassName("INSPECT_TASK_DETAIL");
					inspect_task_detail.setCuid();
					GenericDO pda_point_change_loc = new GenericDO("PDA_POINT_CHANGE_LOC");
					pda_point_change_loc.setCuid();
					Map<String,Object> pdapointmap = new HashMap<String,Object>();
					pdapointmap.put("CUID", pda_point_change_loc.getCuid());
					pdapointmap.put("RELATED_TASK_CUID", task.getCuid());
					pdapointmap.put("RELATED_POINT_CUID", point.getCuid());
					pdapointmap.put("ORIGINAL_LONGITUDE", point.getAttrDouble("LONGITUDE"));
					pdapointmap.put("ORIGINAL_LATITUDE", point.getAttrDouble("LATITUDE"));
					pdapointmap.put("POINT_LABEL_CN", point.getAttrString("LABEL_CN"));
					pdapointmap.put("RELATED_DEVICE_CODE",point.getAttrValue("PDACODE"));
					pdapointlist.add(pdapointmap);
			
					Map<String,Object> pointmap = new HashMap<String,Object>();
					pointmap.put("CUID", inspect_task_detail.getCuid());
					pointmap.put("RELATED_TASK_CUID", task.getCuid());
					pointmap.put("POINT_CUID", point.getCuid());
					pointmap.put("RELATED_DEVICE_CODE", point.getAttrValue("PDACODE"));
					String must_point=point.getAttrString("IS_MUST_POINT");
					if("是".equals(must_point)){
						pointmap.put("IS_MUST_REACH", 1);
					}else{
						pointmap.put("IS_MUST_REACH", 0);
					}
					resultlist.add(pointmap);
				}
				taskbo.addToTempInspectTaskDetail(resultlist);
				taskbo.addToTempPDA_POINT_CHANG_LOC(pdapointlist);
			}
			
			
		}
		taskbo.addToTempTaskToPda(pda_list);	
		IQuartzBO quartzbo =  (IQuartzBO) DmmaBoFactory.getInstance().getQuartzBO();
		quartzbo.sendTaskCycle(taskmap);
//		insertRelateSeg(task.getCuid(), segList);//将关联段信息临时添加到关联表，用于派发周期任务
		
	
	}
	public Map saveTask(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		long start=0;
		long end=0;
		String taskName = jsonObject.getString("TASK_NAME");
		DataObjectList pdaList = new DataObjectList();
		JSONArray pdaJsonArray = JSONArray.fromObject(jsonObject.get("PDADATAS"));
		for(Object pda:pdaJsonArray){
			GenericDO generDo = new GenericDO();
			generDo.getAllAttr().putAll((Map)pda);
			pdaJsonArray.add(generDo);
		}
		pdaJsonArray = JSONArray.fromObject(jsonObject.get("RES_PROJECTDATAS"));
		List<GenericDO> systemList = new ArrayList<GenericDO>();
		for(Object pda:pdaJsonArray){
			GenericDO generDo = new GenericDO();
			generDo.getAllAttr().putAll((Map)pda);
			systemList.add(generDo);
		}
		List<GenericDO>  pointList2 = new ArrayList<GenericDO>();
		pdaJsonArray = JSONArray.fromObject(jsonObject.get("POINTS"));
		for(Object pda:pdaJsonArray){
			GenericDO generDo = new GenericDO();
			generDo.getAllAttr().putAll((Map)pda);
			pointList2.add(generDo);
		}
		
		List<GenericDO>  segList = null;
		List pointMapList = null;
		
		Long IS_CYCLE = (Long) jsonObject.get("IS_CYCLE");
		JSONObject facttime = JSONObject.fromObject(jsonObject.get("FACTTIME"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date START_TIME = null;
		Date END_TIME  = null;
		String REMARK = null;
		Long FREQUENCE_TYPE = (Long) jsonObject.get("FREQUENCE_TYPE");
		String SENDMONTH  =  (String) jsonObject.get("SENDMONTH");
		String TASK_TIME  =  (String) jsonObject.get("TASK_TIME");
		String DURATION  =  (String) jsonObject.get("DURATION");
		String TASKTYPE  =  (String) jsonObject.get("TASK_TYPE");
		try {
			START_TIME = format.parse((String) facttime.get("fact_start_time"));
			END_TIME =  format.parse((String)facttime.get("fact_end_time"));
			REMARK = (String) jsonObject.get("REMARK");
		} catch (ParseException e) {
			
		}
		if(pdaList.size()==1){
			if(pointMapList.size()==0){
				relatedAllPdaToAllPoints(pdaList,pointList2);
				List<GenericDO> ptasks = pdaList;
				for(GenericDO task:ptasks){
					task.getAllAttr().put("IS_CYCLE", IS_CYCLE);
					task.getAllAttr().put("START_TIME", START_TIME);
					task.getAllAttr().put("END_TIME", END_TIME);
					task.getAllAttr().put("REMARK", REMARK);
					task.getAllAttr().put("TASK_TYPE", TASKTYPE);
					task.getAllAttr().put("SENDMONTH", SENDMONTH);
					task.getAllAttr().put("FREQUENCE_TYPE", FREQUENCE_TYPE);
					task.getAllAttr().put("TASK_TIME", TASK_TIME);
					task.getAllAttr().put("DURATION", DURATION);
					
					addAndSendTask(task,systemList,pdaList,taskName,segList,pointList2);
				}
			}else{
				List<GenericDO> ptasks = pdaList;
				for(GenericDO task:ptasks){
					task.getAllAttr().put("IS_CYCLE", IS_CYCLE);
					task.getAllAttr().put("START_TIME", START_TIME);
					task.getAllAttr().put("END_TIME", END_TIME);
					task.getAllAttr().put("REMARK", REMARK);
					task.getAllAttr().put("TASK_TYPE", TASKTYPE);
					task.getAllAttr().put("SENDMONTH", SENDMONTH);
					task.getAllAttr().put("FREQUENCE_TYPE", FREQUENCE_TYPE);
					task.getAllAttr().put("TASK_TIME", TASK_TIME);
					task.getAllAttr().put("DURATION", DURATION);
					addAndSendTask(task,systemList,pdaList,taskName,segList,pointList2);
				}
				
			}
		}else if(pdaList.size()>1){
			List<GenericDO> ptasks = pdaList;
			for(GenericDO task:ptasks){
				task.getAllAttr().put("IS_CYCLE", IS_CYCLE);
				task.getAllAttr().put("START_TIME", START_TIME);
				task.getAllAttr().put("END_TIME", END_TIME);
				task.getAllAttr().put("REMARK", REMARK);
				
				task.getAllAttr().put("SENDMONTH", SENDMONTH);
				task.getAllAttr().put("FREQUENCE_TYPE", FREQUENCE_TYPE);
				task.getAllAttr().put("TASK_TIME", TASK_TIME);
				task.getAllAttr().put("DURATION", DURATION);
				addAndSendTask(task,systemList,pdaList,taskName,segList,pointList2);
			}
			
		}
		return new HashMap();
	}
	
	private IDuctManagerBO getDuctManagerBO(){
		return (IDuctManagerBO) BoHomeFactory.getInstance().getBO(BoName.DuctManagerBO);
	}
	
	public List getPointsByWireSystemCuid(String wireSystemCuid,List<GenericDO>  segList){
		String sql=" wire_system_cuid = '"+wireSystemCuid+"' order by label_cn";
		List list = new ArrayList();
		try {
			DataObjectList wireToDuctLines = (DataObjectList)BoCmdFactory.getInstance().execBoCmd(
					WireToDuctLineBOHelper.ActionName.getWireToDuctLineBySql,new BoActionContext(),sql);
			Set<String> tempList = new HashSet<String>();
			
			if(wireToDuctLines.size()>0){
				for(GenericDO wireToDuctline : wireToDuctLines){
					tempList.add(wireToDuctline.getAttrString(WireToDuctline.AttrName.disPointCuid));
					tempList.add(wireToDuctline.getAttrString(WireToDuctline.AttrName.endPointCuid));
					String lineSegCuid = wireToDuctline.getAttrString(WireToDuctline.AttrName.lineSegCuid);
					GenericDO segGdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), lineSegCuid);
					segList.add(segGdo);
				}
			
			for(String str:tempList){
				GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), str);
				list.add(gdo);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	private void addHangWallOrUpLinePoints(DataObjectList values,String district,List<GenericDO> pointList){
		for(GenericDO gdo:values){
			String cuid = gdo.getCuid();
			try {
				DataObjectList dataObjectList = (DataObjectList)BoCmdFactory.getInstance().execBoCmd(DuctManagerBOHelper.ActionName.getPointsBySystemCuid,new BoActionContext(), cuid);
				int validatepoint = 0;
				for(GenericDO g:dataObjectList){
					boolean isMustReach = false;
					if (validatepoint % 3 == 0) {
						isMustReach = true;
					}
					convertObj(g, isMustReach,district,pointList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 添加点设施到必到点表
	 * @param values
	 */
	private void addPoints(DataObjectList values,List<GenericDO> systemList,List<GenericDO>  segList,String district,List<GenericDO> pointList,ServiceActionContext serviceActionContext) {
			for (GenericDO gdo : values) {

			List res = null;
			try {// 关联的系统入库
				systemList.add(gdo);

				if (gdo instanceof WireSystem) {
					res = getPointsByWireSystemCuid(gdo.getCuid(),segList);
				}else if(gdo.getClassName().equals("HANG_WALL") || gdo.getClassName().equals("UP_LINE")){
					addHangWallOrUpLinePoints(values,district,pointList);
				} else {
					res = (ArrayList) BoCmdFactory
							.getInstance()
							.execBoCmd(
									DuctManagerBOHelper.ActionName.getSystemRouteByCuid,
									new BoActionContext(serviceActionContext.getUserId()), gdo.getCuid());
				}

			} catch (Exception ex) {
				LogHome.getLog().error("获取系统点设施失败", ex);
			}
			if (res != null && res.size() > 0) {
				int validatepoint = 0;
				for (Object obj : res) {
					if (obj instanceof DataObjectList) {
						DataObjectList points = (DataObjectList) obj;
						int validatePointIndex = 0;
						for (int i = 0; i < points.size(); i++) {
							GenericDO point = (GenericDO) (points.get(i));
							if (point instanceof Manhle
									|| point instanceof Pole
									|| point instanceof Stone
									|| point instanceof Inflexion
									|| point instanceof FiberJointBox
									|| point instanceof FiberDp
									|| point instanceof FiberCab) {
								validatePointIndex++;
								boolean isMustReach = false;
								if (validatePointIndex % 3 == 0) {
									isMustReach = true;
								}
								convertObj(point,isMustReach,district,pointList);
							}
						}
					}

					if (obj instanceof GenericDO) {
						GenericDO point = (GenericDO) obj;
						if (point instanceof Manhle 
								|| point instanceof Pole
								|| point instanceof Stone
								|| point instanceof Inflexion
								|| point instanceof FiberJointBox
								|| point instanceof FiberDp
								|| point instanceof FiberCab) {
							validatepoint++;
							boolean isMustReach = false;
							if (validatepoint % 3 == 0) {
								isMustReach = true;
							}
							convertObj(point, isMustReach,district,pointList);

						}
					}
				}
			}

		}
	//doGetPoints(pointList);
		
	}
	/**
	 * 复制并转换对象
	 * @param point
	 * @param isMustReach
	 * @return
	 */
	private void convertObj(GenericDO point,boolean isMustReach,String district,List<GenericDO> pointList){
		GenericDO gdo = getDuctManagerBO().getObjByCuid(new BoActionContext(), point.getCuid());
		gdo.setAttrValue("RESOUCE_TYPE", DMHelper.getPointName(point.getClassName()));
		gdo.setAttrValue("IS_MUST_POINT", (isMustReach?"是":"否"));
		gdo.setAttrValue("DISTRICT", district);
		pointList.add(gdo);
	}
	/*private void doGetPoints(List<GenericDO> pointList){
		int colorPoint = 0;
		if(pointList.size()>0){
			GenericDO gdoFirst = pointList.get(pointoffset);
			mirrorPoints.add(gdoFirst);
			gdoFirst.setAttrValue("COLORPOINT", colorPoint);
			GenericNode node = new GenericNode(gdoFirst.getCuid(), gdoFirst);
			mustPointsTableBox.addElement(node);
			tempDistrict = gdoFirst.getAttrString("DISTRICT");
			for(int i=pointoffset+1;i<pointoffset+pointFeS;i++){
				if(i<pointList.size()){
					GenericDO p = pointList.get(i);
					String dis = p.getAttrString("DISTRICT");
					if(!dis.equals(tempDistrict)){
						if(colorPoint==colorsSize-1){
							colorPoint=0;
						}
						colorPoint =colorPoint+1;
						tempDistrict=dis;
					}
					p.setAttrValue("COLORPOINT", colorPoint);
					mirrorPoints.add(p);
					GenericNode n = new GenericNode(p.getCuid(), p);
					mustPointsTableBox.addElement(n);
				}else{
					return;
				}
				
			}
		}
		
	}*/
	private void getSegs(DataObjectList values,List<GenericDO>  segList){
		for (GenericDO gdo : values) {
			DataObjectList segs;
			try {
				segs = getDuctManagerBO().getSegsBySystemCuid(new BoActionContext(), gdo.getCuid());
				for(GenericDO seg:segs){
					segList.add(seg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}
	public Map getMustPoints(HttpServletRequest request, String data) {
		JSONArray jsonsArray = JSONArray.fromObject(data);
		DataObjectList  values =  new DataObjectList();
		for(Object obj :jsonsArray){
			Map objData = (Map)obj;
			String className = StringUtils.substring((String)objData.get("CUID"), 0,StringUtils.indexOf((String)objData.get("CUID"), "-"));
			GenericDO ger = null;
			if(StringUtils.isNotEmpty(className)){
				ger = new GenericDO(className);
			}
			ger.getAllAttr().putAll(objData);
			values.add(ger );
		}
		List<GenericDO> systemList = new ArrayList<GenericDO>() ;
		List<GenericDO>  segList = new ArrayList<GenericDO>() ;
		ServiceActionContext serviceActionContext = new ServiceActionContext(request);
		String district = serviceActionContext.getRelatedDistrictCuid() ;
		List<GenericDO> pointList = new ArrayList<GenericDO>() ;
		addPoints(values,systemList,segList,district,pointList, serviceActionContext);
		getSegs(values,segList);
		String len = getTaskLength(segList)+"";
		Map<String,Object> resault = new HashMap<String, Object>();
		if(len.length()>7){
			resault.put("MSG",(getTaskLength(segList)+"").substring(0, 7)+"(公里)");
		}else{
			resault.put("MSG",getTaskLength(segList)+"(公里)");
		}
		resault.put("DATA",pointList);
		return resault;
	}

	public Map updateXY(HttpServletRequest request, String data) {
		ITaskBO taskbo =  (ITaskBO) DmmaBoFactory.getInstance().getBo();
		JSONArray jsonArray = null;
		for(Object jsonObj:jsonArray){
			Map jsonMap = (Map) jsonObj;
			taskbo.updateOldXY((String) jsonMap.get("CUID"));
		}
		return new HashMap();
	}
	
	public Map doDeleteSubTask(HttpServletRequest request, String data) {
		JSONArray jsonArray = JSONArray.fromObject(data);
		for(Object jsonObj:jsonArray){
			Map dataMap = (Map) jsonObj;
			executeDeleteSubTask((String) dataMap.get("CUID"));
		}
		return new HashMap();
	}

}
