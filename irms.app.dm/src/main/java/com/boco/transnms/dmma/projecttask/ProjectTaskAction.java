package com.boco.transnms.dmma.projecttask;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.boco.core.bean.SpringContextUtil;

public class ProjectTaskAction {
	
	public Map addProjectTask(HttpServletRequest request,String data) {
		ProjectTaskActionBO projectTaskActionBO = (ProjectTaskActionBO) SpringContextUtil.getBean("ProjectTaskActionBO");
		return projectTaskActionBO.addProjectTask(request, data);
	}
	
	public Map<String,String > doDelete(HttpServletRequest request,String delObjts){
		ProjectTaskActionBO projectTaskActionBO = (ProjectTaskActionBO) SpringContextUtil.getBean("ProjectTaskActionBO");
		return projectTaskActionBO.doDelete(request, delObjts);
	}
	public Map saveResource(HttpServletRequest request,String data) {
		ProjectTaskActionBO projectTaskActionBO = (ProjectTaskActionBO) SpringContextUtil.getBean("ProjectTaskActionBO");
		return projectTaskActionBO.saveResource(request, data);
	}
}
