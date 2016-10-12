package com.boco.transnms.dmma.task;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.boco.core.bean.SpringContextUtil;

public class TaskAction {
	public Map doDelete(HttpServletRequest request,String data) {
		TaskActionBO taskActionBO = (TaskActionBO) SpringContextUtil.getBean("TaskActionBO");
		return taskActionBO.doDelete(request, data);
	}
	public Map saveTask(HttpServletRequest request,String data) {
		TaskActionBO taskActionBO = (TaskActionBO) SpringContextUtil.getBean("TaskActionBO");
		return taskActionBO.saveTask(request, data);
	}
	public Map getMustPoints(HttpServletRequest request,String data) {
		TaskActionBO taskActionBO = (TaskActionBO) SpringContextUtil.getBean("TaskActionBO");
		return taskActionBO.getMustPoints(request, data);
	}
	public Map updateXY(HttpServletRequest request,String data) {
		TaskActionBO taskActionBO = (TaskActionBO) SpringContextUtil.getBean("TaskActionBO");
		return taskActionBO.updateXY(request, data);
	}
	public Map doDeleteSubTask(HttpServletRequest request,String data) {
		TaskActionBO taskActionBO = (TaskActionBO) SpringContextUtil.getBean("TaskActionBO");
		return taskActionBO.doDeleteSubTask(request, data);
	}
}
