package com.boco.irms.app.dm.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.spring.SysProperty;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.OpticalCheckTask;
import com.boco.transnms.common.dto.SysUser;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.base.IOCGenericSchedulerBO;
import com.boco.transnms.server.bo.ibo.dm.IOpticalCheckBO;
/**
 * 核查计划管理
 * @author JiSc
 *
 */
public class OpticalCheckAction {
	
	private IOpticalCheckBO getOpticalCheckBO(){
		return BoHomeFactory.getInstance().getBO(IOpticalCheckBO.class);
	}
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataObjectList tempTaskList = new DataObjectList();
	private String taskName = "";//任务名称 
	private String district = "";//区域
	private String gatheringDesc = "";//任务说明 
	private String gatheringCelue = "";//任务频度
	private String exeType = "";//执行方式
	private String subDistrict = "";//是否县级区域细分
	private String taskWorkState = "";//任务状态
	private String taskType = "";//任务类型
	private String spinnerDate = "";//时间
	private String monthDoMonth = "";//月份
	private String monthDoDay = "";//月号
	private String weekDo = "";//星期(频度每周)
	private String monthDo = "";//月号(频度每月)
	SysUser user = new SysUser();
	
	/**
	 * 区域定时任务初始化
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String districtTaskOpticalCheck(HttpServletRequest request, Map<String,String> param) throws Exception{
		ServiceActionContext ac = new ServiceActionContext(request);
		user.setCuid(ac.getUserCuid());
		if (ac.getUserName().trim() != null)
			user.setUserName(ac.getUserName());
		else
			user.setUserName(ac.getUserId());
		gatheringDesc = param.get("gatheringDesc");//任务说明 
		gatheringCelue = param.get("gatheringCelue");//任务频度
		exeType = param.get("exeType");//执行方式
		subDistrict = param.get("subDistrict");//是否县级区域细分
		taskWorkState = param.get("taskWorkState");//任务状态
		taskType = param.get("taskType");//任务类型
		spinnerDate = param.get("spinnerDate");//时间
		monthDoMonth = param.get("monthDoMonth");//月份
		monthDoDay = param.get("monthDoDay");//月号
		weekDo = param.get("weekDo");//星期(频度每周)
		monthDo = param.get("monthDo");//月号(频度每月)

		//判断区域是否细分
		Boolean isContainSubDistrict = null;
		if(subDistrict!=null && "0".equals(subDistrict)){
			isContainSubDistrict = false;
		}else{
			isContainSubDistrict = true;
		}
		//根据是否区域细分取得系统区域下的所属区域
		DataObjectList subDistrictList = getDistrictList(isContainSubDistrict);
		String[] timeS = spinnerDate.split(":");
		
		getOpticalCheckBO().doDeleteOpticalCheckTaskByLabelcn("定时光纤区域核查__");
		
		Set<String> districtTaskNameSet = new HashSet<String>();//区域过滤
		if (exeType != null && "0".equals(exeType)) {// 立即执行 需要做的处理
			taskWorkState = "0";
			for(int i=0;i<subDistrictList.size();i++){
				District district = (District) subDistrictList.get(i);
				String taskName = getTaskName(district.getAttrString("LABEL_CN"),taskType);
				if(!districtTaskNameSet.contains(taskName)){
					districtTaskNameSet.add(taskName);
					inmidiaActionTask(ac,taskName,isContainSubDistrict,district,tempTaskList);
				}else{}
			}

		} else if (exeType != null && "1".equals(exeType)) {//定时执行需要做的处理
			for(int i=0;i<subDistrictList.size();i++){
				District district = (District) subDistrictList.get(i);
				String taskName = getTaskName(district.getAttrString("LABEL_CN"),taskType);
				if(!districtTaskNameSet.contains(taskName)){
					districtTaskNameSet.add(taskName);
					timingTaskAction(ac,taskName,isContainSubDistrict,district,timeS,tempTaskList);
				}else{}
			}
			
		} else if (exeType != null && "2".equals(exeType)) {//周期执行需要做的处理
			for(int i=0;i<subDistrictList.size();i++){
					District district = (District) subDistrictList.get(i);
					String taskName = getTaskName(district.getAttrString("LABEL_CN"),taskType);
					if(!districtTaskNameSet.contains(taskName)){
						districtTaskNameSet.add(taskName);
						cycleTaskAction(ac,taskName,isContainSubDistrict,district,timeS,tempTaskList);
					}
			}
		}
		return "0";
	}

	/**
     * 取得区域列表
     * @param isContainSubDistrict
     * @return
	 * @throws Exception 
     */
    private DataObjectList getDistrictList(boolean isContainSubDistrict) throws Exception{
    	String districtCuid = SysProperty.getInstance().getValue("district", "DISTRICT-00001");
    	DataObjectList subDistrictList = new DataObjectList();
    	if(!isContainSubDistrict){//不进行区域细化
    		subDistrictList.add(DistrictCacheModel.getInstance().getDistrictByCUID(districtCuid));
    	}else{
    		DataObjectList templist = DistrictCacheModel.getInstance().getChildDistrictByCuid(districtCuid);
    		for(int i=0;i<templist.size();i++){
    			District subDistrict = (District)templist.get(i);
    			if(!subDistrict.getCuid().equals(districtCuid)){
    				subDistrictList.add(subDistrict);
        			DataObjectList tempSubDistrict = DistrictCacheModel.getInstance().getChildDistrictByCuid(subDistrict.getCuid());
        			for(int j=0;j<tempSubDistrict.size();j++){
        				District tempsub = (District)tempSubDistrict.get(j);
        				if(!tempsub.getCuid().equals(subDistrict.getCuid())){
        					subDistrictList.add(tempsub);
        				}
        				
        			}
    			}
    			
    		}
    	}
    	return subDistrictList;
    }
    
    public String getTaskName(String districtName,String taskType){
    	String taskname="";
    	if(taskType!=null && ("光路核查".equals(taskType) || "1".equals(taskType))){
    		taskname = "定时光路区域核查__"+districtName;
    	}else if(taskType!=null && ("光纤核查".equals(taskType) || "2".equals(taskType))){
    		taskname = "定时光纤区域核查__"+districtName;
    	}
    	return taskname;
    }
    
    public Date doTime(String monthString,String haoshuString,String hour,String minute){
    	Calendar cal = Calendar.getInstance();
    	cal.set(GregorianCalendar.MONTH,Integer.parseInt(monthString)-1);
		cal.set(GregorianCalendar.DAY_OF_MONTH,Integer.parseInt(haoshuString));
		cal.set(GregorianCalendar.HOUR_OF_DAY,Integer.parseInt(hour));
		cal.set(GregorianCalendar.MINUTE,Integer.parseInt(minute));
		Date date = cal.getTime();
		return date;
    }
    
    /**
     * 使用任务公共属性初始化任务
     * @param task
     * @param taskName
     * @param isContainSubDistrict
     */
    private void initOpticalCheckTask(OpticalCheckTask task,String taskName,boolean isContainSubDistrict,District district){
    	task.setAttrValue(OpticalCheckTask.AttrName.createUser , user.getUserName());
    	task.setAttrValue(OpticalCheckTask.AttrName.taskName, taskName);//任务名称
    	task.setAttrValue(OpticalCheckTask.AttrName.taskType, 1+taskType);
		String gatheringDescString = gatheringDesc;
		task.setAttrValue(OpticalCheckTask.AttrName.taskRemark , gatheringDescString);//任务说明
		task.setAttrValue("DISTRICT_NAME", district.getAttrString("LABEL_CN"));
		if(isContainSubDistrict){//选择区县细分时
			task.setAttrValue(OpticalCheckTask.AttrName.isIncludeSubdis , 0L);//细分区域--不包含子区域
		}else{//不进行区县细分时
			task.setAttrValue(OpticalCheckTask.AttrName.isIncludeSubdis , 1L);//不细分区域--包含子区域
		}
		
    }
    
    /**
     * 任务立即执行情况
     * @param taskName
     * @param isContainSubDistrict
     * @param districtcuid
     */
    private void inmidiaActionTask(ServiceActionContext ac,String taskName,boolean isContainSubDistrict,District district,DataObjectList taskList){
		BoActionContext bocontext = new BoActionContext();
		bocontext.setUserId(ac.getUserCuid());
		bocontext.setUserName(ac.getUserName());
    	
    	OpticalCheckTask task = new OpticalCheckTask();
    	initOpticalCheckTask(task,taskName,isContainSubDistrict,district);
    	task.setAttrValue(OpticalCheckTask.AttrName.taskCircle , 0L);
    	task.setAttrValue(OpticalCheckTask.AttrName.taskSetStyle , 4L);//纤芯 光缆断 光缆 光纤
    	task.setAttrValue(OpticalCheckTask.AttrName.taskStartWay , 1L);//立即延时
    	task.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue , 1L);//激活挂起
    	task.setAttrValue(OpticalCheckTask.AttrName.taskState , 2L);//未核查
    	task.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid, district.getCuid());//关联区域
    	
    	try {
    		getOpticalCheckBO().insertDataNowDo(task);
			taskList.add(task);
		} catch (Exception e) {
			LogHome.getLog().error(e,e);
		}
		String sql = "select * from " + OpticalCheckTask.CLASS_NAME + " where task_name = '" + taskName + "'";
    	DataObjectList gdoList = null;
    	try {
    		gdoList = getOpticalCheckBO().getOCTBysql(sql);
		} catch (Exception e) {
			LogHome.getLog().error(e,e);
		}
		if(gdoList.size() == 0){
			return;
		}
		GenericDO gdo = gdoList.get(0);
		if(gdo instanceof OpticalCheckTask){
			getOpticalCheckBO().jobTimingAdd((OpticalCheckTask)gdo);
		}
    }
    
    /**
     * 定时任务执行情况
     * @param taskName
     * @param isContainSubDistrict
     * @param districtcuid
     * @param timeS
     */
	private void timingTaskAction(ServiceActionContext ac,String taskName,boolean isContainSubDistrict, District district, String[] timeS,DataObjectList taskList) {
	
		OpticalCheckTask task = new OpticalCheckTask();
		initOpticalCheckTask(task, taskName, isContainSubDistrict,district);
		task.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue,taskWorkState);// 任务状态
		// 定时 仅仅多了一个参数
		String monthString = String.valueOf(monthDoMonth);
		String haoshuString = String.valueOf(monthDoDay);
		Date date = doTime(monthString, haoshuString, timeS[0], timeS[1]);
		Calendar calendar = Calendar.getInstance();
		int yearNum = calendar.get(Calendar.YEAR);
		task.setAttrValue(OpticalCheckTask.AttrName.taskState, 1L);// 未核查
		task.setAttrValue(OpticalCheckTask.AttrName.taskWorkTime,new Timestamp(date.getTime()));// 执行时间
		task.setAttrValue(OpticalCheckTask.AttrName.taskCircle, 0L);// 0123无日月周
		task.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid,district.getCuid());// 0123无日月周
		task.setAttrValue(OpticalCheckTask.AttrName.taskSetStyle, 4L);// 纤芯

		task.setAttrValue(OpticalCheckTask.AttrName.taskStartWay, 2L);// 立即延时
		// 定时的话 区域直接存在光缆系统cuid的字段里面 定时任务时间到达以后 根据任务的名称取他的区域cuid 在加载他下面设计的光纤
		
		try {
			getOpticalCheckBO().insertData((GenericDO) task);
			taskList.add(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String exeTime = "0  0" + " " + timeS[0] + " " + haoshuString + " " + monthString + " ? " + yearNum;
		IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO) BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
		
		BoActionContext bocontext = new BoActionContext();
		bocontext.setUserId(ac.getUserCuid());
		bocontext.setUserName(ac.getUserName());
		schedulerkBo.addScheduler(bocontext, task.getTaskName(),"opticalMakeCheckTaskTiming", exeTime);
		
	}
	
    /**
     * 周期任务执行情况
     * @param taskName
     * @param isContainSubDistrict
     * @param districtcuid
     * @param timeS
     */
	private void cycleTaskAction(ServiceActionContext ac,String taskName,boolean isContainSubDistrict, District district, String[] timeS,DataObjectList taskList){
		BoActionContext bocontext = new BoActionContext();
		bocontext.setUserId(ac.getUserCuid());
		bocontext.setUserName(ac.getUserName());
		
		OpticalCheckTask task = new OpticalCheckTask();
		initOpticalCheckTask(task, taskName, isContainSubDistrict,district);
		task.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue,taskWorkState);// 任务状态
		Calendar cal = Calendar.getInstance();
		cal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(timeS[0]));
		cal.set(GregorianCalendar.MINUTE, Integer.parseInt(timeS[1]));
		Date date = cal.getTime();
		task.setAttrValue(OpticalCheckTask.AttrName.taskWorkTime,new Timestamp(date.getTime()));// 执行时间
		task.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid,district.getCuid());
		task.setAttrValue(OpticalCheckTask.AttrName.taskSetStyle, 4L);// 纤芯
														
		task.setAttrValue(OpticalCheckTask.AttrName.taskStartWay, 2L);// 立即延时
		task.setAttrValue(OpticalCheckTask.AttrName.taskState, 1L);// 1 未核查
		// 周期 又分为每日 每周 每月的不同
		if (gatheringCelue != null && "0".equals(gatheringCelue) ) {
			task.setAttrValue(OpticalCheckTask.AttrName.taskCircle, 1L);// 0123无日月周
			try {
				getOpticalCheckBO().insertData((GenericDO) task);
				taskList.add(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String exeTime = "0 0" + " " + timeS[0] + " " + "*" + " " + "*" + " ? ";
			IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO) BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
			schedulerkBo.addScheduler(bocontext, task.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);

		} else if (gatheringCelue != null && "2".equals(gatheringCelue)) {
			task.setAttrValue(OpticalCheckTask.AttrName.taskCircle, 3L);// 0123无日月周
			int weekNum = Integer.valueOf(weekDo);
			task.setAttrValue(OpticalCheckTask.AttrName.taskCirclePoint,weekNum);

			try {
				getOpticalCheckBO().insertData((GenericDO) task);
				taskList.add(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
			weekNum += 1;
			if (8 == weekNum) {
				weekNum = 1;
			}
			int year = cal.get(Calendar.YEAR);
			String exeTime = "0 0" + " " + timeS[0] + " " + "?" + " " + "*" + " " + weekNum + " " + year;
			IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO) BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
			schedulerkBo.addScheduler(bocontext, task.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);
		} else if (gatheringCelue != null && "1".equals(gatheringCelue)) {
			task.setAttrValue(OpticalCheckTask.AttrName.taskCircle, 2L);// 0123无日月周
			int monthNum = Integer.valueOf(monthDo);
			task.setAttrValue(OpticalCheckTask.AttrName.taskCirclePoint,monthNum);
			try {
				getOpticalCheckBO().insertData((GenericDO) task);
				taskList.add(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String exeTime = "0 0" + " " + timeS[0] + " " + monthNum + " " + "*" + " ? ";
			IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO) BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
			schedulerkBo.addScheduler(bocontext, task.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);
		}
	}
	
	public Map<String,String> getOpticalCheckTaskByCuid(HttpServletRequest request, Map<String,String> param){
		String cuid = param.get("cuid");//任务说明 
		String sql = "select * from " + OpticalCheckTask.CLASS_NAME + " where cuid = '" + cuid + "'";
    	DataObjectList gdoList = null;
    	try {
    		gdoList = getOpticalCheckBO().getOCTBysql(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(gdoList.size() == 0){
			return null;
		}else{
			OpticalCheckTask optical = (OpticalCheckTask)gdoList.get(0);
			Timestamp timeExe = optical.getTaskWorkTime();//optical.getAttrValue("TASK_WORK_TIME");
			Date d = new Date(timeExe.getTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int hourRe = cal.get(Calendar.HOUR_OF_DAY);
			int minuteRe = cal.get(Calendar.MINUTE);
			int monthRe = cal.get(Calendar.MONTH);
			int dayRe = cal.get(Calendar.DAY_OF_MONTH);
			String timeRe = hourRe + ":" + minuteRe;
			
			Map<String,String> map = new HashMap<String,String>();
			for(Object columnName : optical.getAllAttr().keySet()){
				String colName = columnName.toString();
				map.put(colName, optical.getAttrValue(colName)+"");
			}
			map.put("timeRe", timeRe);
			map.put("dayRe", dayRe+"");
			map.put("monthRe", monthRe+"");
			return map;
		}
	}
	
	/**
	 * 重启光纤核查任务
	 * @param request
	 * @param param
	 * @return
	 */
	public String OpticalCheckReboot(HttpServletRequest request, Map<String,String> param){
		ServiceActionContext ac = new ServiceActionContext(request);
		user.setCuid(ac.getUserCuid());
		if (ac.getUserName().trim() != null)
			user.setUserName(ac.getUserName());
		else
			user.setUserName(ac.getUserId());
		String cuid = param.get("cuid");//任务CUID 
		taskName = param.get("taskName");//任务名称
		gatheringDesc = param.get("gatheringDesc");//任务说明 
		gatheringCelue = param.get("gatheringCelue");//任务频度
		exeType = param.get("exeType");//执行方式
		String district = param.get("district");//区域CUID
		String districtName = param.get("districtName");//区域名称
		taskWorkState = param.get("taskWorkState");//任务状态
		taskType = param.get("taskType");//任务类型
		spinnerDate = param.get("spinnerDate");//时间
		monthDoMonth = param.get("monthDoMonth");//月份
		monthDoDay = param.get("monthDoDay");//月号
		weekDo = param.get("weekDo");//星期(频度每周)
		monthDo = param.get("monthDo");//月号(频度每月)
		String[] timeS = spinnerDate.split(":");
		
		OpticalCheckTask octNewClone = new OpticalCheckTask();
		
		String sql = "select * from " + OpticalCheckTask.CLASS_NAME + " where cuid = '" + cuid + "'";
    	DataObjectList gdoList = null;
    	try {
    		gdoList = getOpticalCheckBO().getOCTBysql(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(gdoList!=null && gdoList.size()>0){
    		octNewClone = (OpticalCheckTask)gdoList.get(0);
    	}else{
    		return "查不到该条任务";
    	}
		
    	octNewClone.setAttrValue(OpticalCheckTask.AttrName.createUser , user.getUserName());
    	octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskName, taskName);//任务名称
		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskRemark , gatheringDesc);//任务说明
		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskType, taskType);
		octNewClone.setAttrNull(OpticalCheckTask.AttrName.totalSubtask);
		octNewClone.setAttrNull(OpticalCheckTask.AttrName.succSubtask);
		octNewClone.setAttrNull(OpticalCheckTask.AttrName.failSubtask);
		octNewClone.setAttrNull(OpticalCheckTask.AttrName.checkEndTime);
		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskSetStyle , 4L);//纤芯 光缆断 光缆 光纤
		
		//区域ID
//		String districtID = disCuid.get(disCuid.size()-1);//???????  disCuid哪来的值？
//		octNewClone.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid , districtID);//关联区域
		octNewClone.setAttrValue(OpticalCheckTask.AttrName.relatedWireSystemCuid , district);//关联区域
		octNewClone.setAttrValue("DISTRICT_NAME",districtName);
		
		
    	if(null != exeType && "0".equals(exeType)){//立即执行
    		Date date = new Date();
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCircle , 0L);
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskStartWay , 1L);//立即延时
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue , 1L);//激活挂起
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskState , 2L);//正在核查
    		try {
    			getOpticalCheckBO().modifyCheckTaskNowDo(new BoActionContext(), octNewClone);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
			getOpticalCheckBO().jobTimingAdd(octNewClone);
			
    	}else if(null != exeType && "1".equals(exeType)){//定时执行
    		
			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue , taskWorkState);//任务状态
    		String monthString = String.valueOf(monthDoMonth);
    		String haoshuString = String.valueOf(monthDoDay);
    		Date date = doTime(monthString,haoshuString,timeS[0],timeS[1]);
    		Calendar calendar = Calendar.getInstance();
    		int yearNum = calendar.get(Calendar.YEAR);
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskState , 1L);//未核查
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskWorkTime , new Timestamp(date.getTime()));//执行时间
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCircle , 0L);//0123无日月周
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskStartWay , 2L);//立即延时
    		//定时的话 区域直接存在光缆系统CUID的字段里面 定时任务时间到达以后 根据任务的名称取他的区域CUID 在加载他下面设计的光纤
    		try {
    			getOpticalCheckBO().modifyCheckTaskForReboot(new BoActionContext(), octNewClone);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		String exeTime = "0 " + timeS[1] + " " + timeS[0] + " " + haoshuString +" " + monthString + " ? " + yearNum;
    		IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO)BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
    		schedulerkBo.deleteJob(octNewClone.getTaskName(), "opticalMakeCheckTaskTiming");
    		schedulerkBo.addScheduler(new BoActionContext(), octNewClone.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);
    		
    	}else if(null != exeType && "2".equals(exeType)){//周期执行
    		
			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskWorkStatue , taskWorkState);//任务状态
    		Calendar cal = Calendar.getInstance();
    		cal.set(GregorianCalendar.HOUR_OF_DAY,Integer.parseInt(timeS[0]));
    		cal.set(GregorianCalendar.MINUTE,Integer.parseInt(timeS[1]));
    		Date date = cal.getTime();
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskWorkTime , new Timestamp(date.getTime()));//执行时间
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskStartWay , 2L);//立即延时
    		octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskState , 1L);//1 未核查
    		//周期 又分为每日 每周 每月的不同
    		if(null != gatheringCelue && "0".equals(gatheringCelue)){
    			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCircle , 1L);//0123无日月周
    			try {
    				getOpticalCheckBO().modifyCheckTaskForReboot(new BoActionContext(), octNewClone);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			String exeTime = "0 " + timeS[1] + " " + timeS[0] + " " + "*" +" " + "*" + " ? ";
    			IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO)BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
        		schedulerkBo.deleteJob(octNewClone.getTaskName(), "opticalMakeCheckTaskTiming");
        		schedulerkBo.addScheduler(new BoActionContext(), octNewClone.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);
    		
    		}else if(null != gatheringCelue && "2".equals(gatheringCelue)){
    			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCircle , 3L);//0123无日月周
    			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCirclePoint , weekDo);
    			try {
    				getOpticalCheckBO().modifyCheckTaskForReboot(new BoActionContext(), octNewClone);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			String exeTime = "0 " + timeS[1] + " " + timeS[0] + " " + "?" +" " + "*" + " " + weekDo;
    			IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO)BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
        		schedulerkBo.deleteJob(octNewClone.getTaskName(), "opticalMakeCheckTaskTiming");
        		schedulerkBo.addScheduler(new BoActionContext(), octNewClone.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);
        		
    		}else if(null != gatheringCelue && "1".equals(gatheringCelue)){
    			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCircle , 2L);//0123无日月周
    			octNewClone.setAttrValue(OpticalCheckTask.AttrName.taskCirclePoint , monthDo);
    			try {
    				getOpticalCheckBO().modifyCheckTaskForReboot(new BoActionContext(), octNewClone);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			String exeTime = "0 " + timeS[1] + " " + timeS[0] + " " + monthDo +" " + "*" + " ? ";
    			IOCGenericSchedulerBO schedulerkBo = (IOCGenericSchedulerBO)BoHomeFactory.getInstance().getBO(IOCGenericSchedulerBO.class);
        		schedulerkBo.deleteJob(octNewClone.getTaskName(), "opticalMakeCheckTaskTiming");
        		schedulerkBo.addScheduler(new BoActionContext(), octNewClone.getTaskName(), "opticalMakeCheckTaskTiming", exeTime);
    		}
    	}
    	return "0";
	}

}
