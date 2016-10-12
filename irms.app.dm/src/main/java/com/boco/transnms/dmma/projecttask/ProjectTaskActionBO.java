package com.boco.transnms.dmma.projecttask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.dmma.server.bo.ibo.project.ICreateResourceBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.RackEnum;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.MapToObject;
import com.boco.transnms.common.dto.ProjectManagement;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.BoName;
import com.boco.transnms.server.bo.helper.dm.DuctManagerBOHelper;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class ProjectTaskActionBO {
	private GenericDO initTask(String labelCn,String projectName
			,String remark,ServiceActionContext ac,Date startDate ,Date endDate) throws ParseException{
		GenericDO task = new GenericDO();
		task.setClassName("PDA_TASK");
		task.setCuid();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		task.setAttrValue("LABEL_CN",labelCn+"-"+projectName+"-"+format.format(new Date()));
		task.setAttrValue("REMARK", remark);
		task.setAttrValue("FINISH_PERSENT", 0);//任务完成百分比
		task.setAttrValue("TASK_STATE", Long.valueOf("0"));//任务状态--未开始
		task.setAttrValue("TASK_TYPE", Long.valueOf("2"));//任务类型--新增任务
		task.setAttrValue("RESULT_STATE", Long.valueOf("0"));//任务结果状态--未完成
		task.setAttrValue("IS_CYCLE", Long.valueOf("0"));//是否周期任务--否
		task.setAttrValue("RELATED_DISTRICT_CUID",ac.getRelatedDepartmentCuid());
		task.setAttrValue("RELATED_DISTRICT_NAME",ac.getDistrictName());
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
	private void addProject(GenericDO result,List<Map<String,Integer>> resourceNumList) {

		Map<String, Object> submap = new HashMap<String, Object>();
		submap.put("CUID", result.getCuid());
		submap.put("LABEL_CN", result.getAttrValue("LABEL_CN"));
		submap.put("REMARK", result.getAttrValue("REMARK"));
		submap.put("FINISH_PERSENT", result.getAttrValue("FINISH_PERSENT"));
		submap.put("TASK_STATE", result.getAttrLong("TASK_STATE"));
		submap.put("TASK_TYPE", Long.valueOf("2"));
		submap.put("IS_CYCLE", result.getAttrLong("IS_CYCLE"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		submap.put("START_TIME", format
				.format(result.getAttrDate("START_TIME")));
		submap.put("END_TIME", format.format(result.getAttrDate("END_TIME")));
		submap.put("TASK_TIME", format.format(new Date()));
		submap.put("RELATED_DISTRICT_CUID", result.getAttrValue("RELATED_DISTRICT_CUID"));
		submap.put("ISSAVE", result.getAttrInt("ISSAVE"));
		
		ITaskBO taskbo = ((ITaskBO) DmmaBoFactory.getInstance().getBo());
		for(Map<String,Integer> resourceNumMap:resourceNumList){
			Map<String,Object> dataMap = new HashMap<String, Object>();
			dataMap.put("RELATED_TASK_CUID", result.getCuid());
			dataMap.put("RESOURCE_TYPE", resourceNumMap.keySet().toArray()[0].toString());
			dataMap.put("RESOURCE_NUM", resourceNumMap.get(resourceNumMap.keySet().toArray()[0].toString()));
			dataMap.put("RELATED_PROJECT_CUID", "");
			taskbo.insertResourceNum(dataMap);
		}
		taskbo.addSubTask(submap);

	}
	
	public Map addProjectTask(HttpServletRequest request,String data) {
		JSONObject jsonObject  =  JSONObject.fromObject(data);
		String taskName = (String) jsonObject.get("TASK_NAME");
		String projectName = (String) jsonObject.get("PROJECT_NAME");
		String remark = (String) jsonObject.get("REMARK");
		
		String startTime = (String) jsonObject.get("START_TIME");
		String endTime = (String) jsonObject.get("END_TIME");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ITaskBO taskBO = ((ITaskBO) DmmaBoFactory.getInstance().getBo());
		try {
			GenericDO	task = initTask(taskName,projectName,remark,new ServiceActionContext(request),format.parse(startTime),format.parse(endTime));
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String padData = (String) jsonObject.get("pdaData");
			JSONArray pdaDataArr = null;
			if(StringUtils.isNotEmpty(padData)){
				pdaDataArr = JSONArray.fromObject(padData);
			};
			for(Object obj:pdaDataArr){
				Map node = (Map)obj;
				GenericDO pda_task = new GenericDO();
				pda_task.setClassName("TASK_TO_PDA");
				pda_task.setCuid();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("CUID", pda_task.getCuid());
				map.put("RELATED_TASK_CUID", task.getCuid());
				map.put("RELATED_DEVICE_CODE", node.get("DEVICE_CODE"));
				map.put("TASK_STATE", 0);
				list.add(map);
			}
			taskBO.addToTaskToPda(list);
			List<Map<String,Integer>> resourceNumList = new ArrayList<Map<String,Integer>>();
			addProject(task,resourceNumList);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return  new HashMap();
	}
	
	private DataObjectList addProjectRelation(DataObjectList gdos,GenericDO projectManagement) {
		DataObjectList rlist = new DataObjectList();
		if (projectManagement != null) {
			for (GenericDO gdo : gdos) {
				gdo.setAttrValue(DuctSeg.AttrName.relatedProjectCuid,
						projectManagement.getCuid());
				gdo.setAttrValue(DuctSeg.AttrName.projectState,
						projectManagement
								.getAttrLong(ProjectManagement.AttrName.state));
				rlist.add(gdo);
			}
			return rlist;
		} else {
			return gdos;
		}
	}
	
	public void saveAllPoints(DataObjectList allPoints){
		try{
			GenericDO projectManagement = new GenericDO();
			DataObjectList mapToObjects = new DataObjectList();
			String curMapCuid = "";
			for (GenericDO dto : allPoints) {
				MapToObject mapToObject = new MapToObject();
				mapToObject.setMapCuid(curMapCuid);
				mapToObject.setObjectCuid(dto.getCuid());
				mapToObjects.add(mapToObject);
			}
			DataObjectList newNewPoints = addProjectRelation(allPoints,projectManagement);
			IDuctManagerBO ductManagerBO = (IDuctManagerBO) BoHomeFactory
					.getInstance().getBO(BoName.DuctManagerBO);
			ductManagerBO.createPoints(new BoActionContext(), newNewPoints);
			BoCmdFactory.getInstance().execBoCmd(
					DuctManagerBOHelper.ActionName.createDMObjects,
					new BoActionContext(), "", mapToObjects);
		}catch (Exception e) {
			LogHome.getLog().error(e, e);
		}
		
	}
	private Map excuteDeleteSql(String delObjects){
	   JSONArray jsonArray = JSONArray.fromObject(delObjects);
		for(Object jsonObject:jsonArray){
					Map dataObject = (Map) jsonObject;
					String taskCuid = (String) dataObject.get("CUID");
					Integer taskSatus = (Integer) dataObject.get("TASK_STATE");
					if( 2 == Long.valueOf(taskSatus)){
						continue;
					}
					ITaskBO taskbo = ((ITaskBO) DmmaBoFactory.getInstance().getBo());
					taskbo.excuteSql("delete from pda_task  where task_type=2 and cuid='"+taskCuid+"'");
					taskbo.excuteSql("delete from task_to_pda  where related_task_cuid='"+taskCuid+"'");
					taskbo.excuteSql("delete from t_pda_newwork_point_temp  where taskcuid='"+taskCuid+"'");
					taskbo.excuteSql("delete from t_pda_newwork_seg_temp  where taskcuid='"+taskCuid+"'");
					taskbo.excuteSql("delete from project_related_resourcenum  where related_task_cuid='"+taskCuid+"'");
					taskbo.excuteSql("delete from projecttask_resource where related_task_cuid='"+taskCuid+"'");
		}
		return new HashMap();
	}
	public Map saveResource(HttpServletRequest request,String data){
		 JSONArray jsonArray = JSONArray.fromObject(data);
			for(Object jsonObject:jsonArray){
						Map dataObject = (Map) jsonObject;
						saveResource((String)dataObject.get("CUID"),(String)dataObject.get("ISSAVE"),Long.valueOf((Integer)dataObject.get("PDA_TASK_STATE")),Long.valueOf((Integer)dataObject.get("TASK_STATE")),(String)dataObject.get("RELATED_DISTRICT_CUID"));
			}
		return new HashMap();
	}
	private void saveResource(String taskCuid,String issave,Long pdataskstate,Long taskstate,String districtCuid){
		if("是".equals(issave)){
			//MessagePane.showErrorMessage("该任务已归档!");
		}else if(pdataskstate!=3){
			//MessagePane.showErrorMessage("该任务正在进行采集，不能归档!");
		}else{
			if(taskstate!=0){
				createResourceBO = DmmaBoFactory.getInstance().getICreateResourceBO();
				DataObjectList allPoints = createResourceBO.getAllWillSavePoints(taskCuid);
				saveAllPoints(allPoints);//先将所有的点归档
				List<Map<String,Object>> pointList = createResourceBO.getPoints(taskCuid);
				for(Map<String,Object> map:pointList){//点归档完后归档段和分支、系统
					int key = Integer.valueOf(map.get("segtype").toString());
					DataObjectList points = (DataObjectList)map.get("pointlist");
					List<Map<String,Object>> segList = (List<Map<String,Object>>)map.get("seglist");
					/*if(key==0){
						//管到段归档
						addDMResource(points, new DuctSystem(), new DuctSeg(), districtCuid, task,segList);
					}else if(key==3){
						//杆路段归档
						addDMResource(points, new PolewaySystem(), new PolewaySeg(), districtCuid,  task,segList);
					}else if(key==1){
						//直埋段归档
						addDMResource(points, new StonewaySystem(), new StonewaySeg(), districtCuid, task,segList);
					}else if(key==2){
						//引上归档
						addDMResource(points, new UpLine(), new UpLineSeg(), districtCuid, task,segList);
					}else if(key==4){
						//挂墙归档
						addDMResource(points, new HangWall(), new HangWallSeg(), districtCuid, task,segList);
					}else if(key==5){
						CreateWireResource wireResource = new CreateWireResource(new WireSystem(), new WireSeg(), points, districtCuid);
						DataObjectList seglist = wireResource.segList;
						for(GenericDO seg:seglist){
							Map<String,Object> mapResource = new HashMap<String, Object>();
							mapResource.put("RELATED_TASK_CUID", task.getCuid());
							mapResource.put("RELATED_RESOURCE_SEG", seg.getCuid());
							mapResource.put("RELATED_RESOURCE_SYSTEM", wireResource.systemList.get(0).getCuid());
							createResourceBO.insertProjectTaskReource(mapResource);
						}
					}*/
					
				}
			Map<String,Object> updateMap = new HashMap<String, Object>();//修改任务归档状态
			updateMap.put("CUID",taskCuid);
			updateMap.put("ISSAVE", 1);
			updateMap.put("TASK_STATE", 2);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			updateMap.put("ARCHIVE_TIME", sdf.format(new Date()));
			createResourceBO.updateTaskSaveState(updateMap);
			
			//MessagePane.showConfirmMessage("归档完成！");
			}else{
			 //MessagePane.showErrorMessage("归档出错");
			}
		}
		
	}
	public Map<String,String > doDelete(HttpServletRequest request,String delObjects){
		Map<String,String> dataMap = new HashMap<String, String>();
		dataMap  = excuteDeleteSql(delObjects);
		return  new HashMap();
	}
	
	private ICreateResourceBO createResourceBO;
}
