package com.boco.irms.app.dm.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.FileSystemResource;

import com.boco.common.util.debug.LogHome;
import com.boco.core.spring.SysProperty;
import com.boco.irms.app.dm.action.schedule.FaultThreshold;
import com.boco.irms.app.dm.action.schedule.IConfigInfo;
import com.boco.irms.app.dm.action.schedule.IJobScheduler;
import com.boco.irms.app.dm.action.schedule.JobScheduler;
import com.boco.irms.app.dm.action.schedule.LineStatDetailTask;
import com.boco.irms.app.dm.action.schedule.StatConst;
import com.boco.irms.app.dm.action.schedule.TaskInfo;
import com.boco.irms.app.dm.action.schedule.WireStatTask;

public class JobSchedulerServlet extends HttpServlet {
	
	public JobSchedulerServlet(){
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		LogHome.getLog().info("管线web任务调度开始启动");
		
		initJobScheduler();
		
		Map<String,String> map = new HashMap<String,String>();
		if(StatConst.isWireStatByDis){
			map.clear();
			map.put("taskName", StatConst.taskName);
			map.put("triggerName", StatConst.triggerName);
			map.put("scheduTactic",StatConst.wireStatCronExpression);
			buildTask(WireStatTask.class, map);
		}
		
		if(StatConst.isWireStatOfHenan){
			map.clear();
			map.put("taskName", "LineStatTask");
			map.put("triggerName", "LineStatTrigger");
			map.put("scheduTactic", StatConst.ductStatCronExpression);
			buildTask(LineStatDetailTask.class, map);
		}
		
		LogHome.getLog().info("管线web任务调度启动完成");
	}

	/**
	 * 初始化定时任务
	 */
	private void initJobScheduler(){
		initWireStatTask();
	}
	
	/**
	 * 初始化光缆统计定时任务
	 */
	private void initWireStatTask(){
		String isWireStatOfHenanStr = SysProperty.getInstance().getValue("isWireStatOfHenan","false");
		if(isWireStatOfHenanStr.equals("false")){
			StatConst.isWireStatOfHenan = false;
		}else{
			StatConst.isWireStatOfHenan = true;
		}
		
		StatConst.wireStatCronExpression = SysProperty.getInstance().getValue("wireStatCronExpression","");
		StatConst.ductStatCronExpression = SysProperty.getInstance().getValue("ductStatCronExpression","");
		
		InputStream inputStream = null;
		try {
			String filePath = getServerHome() + "/conf/FaultThreshole.xml";
			System.out.println(filePath);
			inputStream = new FileSystemResource(filePath).getInputStream();
			//inputStream = WebDMUtils.getInputStream(filePath);
		} catch (Exception e) {
			LogHome.getLog().error("读取FaultThreshole.xml出错"+e.getMessage(),e);
		}
		FaultThreshold.loadCfgFile(inputStream);
		StatConst.faultThreshold = FaultThreshold.getInstance();
		
		String isStatWireRemianStr = SysProperty.getInstance().getValue("isStatWireRemain", "false");
		if("false".equals(isStatWireRemianStr)){
			StatConst.isStatWireRemain = false;
		}else{
			StatConst.isStatWireRemain = true;
		}
		
		String isThresholdByDisStr = SysProperty.getInstance().getValue("isThresholdByDis", "false");
		if("false".equals(isThresholdByDisStr)){
			StatConst.isThresholdByDis = false;
		}else{
			StatConst.isThresholdByDis = true;
		}
		
		String isWireStatByDis = SysProperty.getInstance().getValue("isWireStatByDis", "false");
		if("false".equals(isWireStatByDis)){
			StatConst.isWireStatByDis = false;
		}else{
			StatConst.isWireStatByDis = true;
		}
		
		String isStatAddDelWireOfHenanStr = SysProperty.getInstance().getValue("isStatAddDelWireOfHenan", "false");
		if("false".equals(isStatAddDelWireOfHenanStr)){
			StatConst.isStatAddDelWireOfHenan = false;
		}else{
			StatConst.isStatAddDelWireOfHenan = true;
		}
		
	}
	
	
	private String getServerHome() throws ServletException{
		String env = System.getenv("IRMS_SERVER_HOME");
		if(env != null){
			env = env.replace('\\', '/');
		}else{
			LogHome.getLog().error("没有配置IRMS_SERVER_HOME环境变量");
			throw new ServletException("没有配置IRMS_SERVER_HOME环境变量");
		}
		return env;
	}
	
	
	@SuppressWarnings("rawtypes")
	private void buildTask(Class clazz,Map<String,String> map){
		try {
			IJobScheduler jobScheduler = new JobScheduler(map);
			IConfigInfo<Map<String,String>> taskInfo = new TaskInfo();
			jobScheduler.run(taskInfo,clazz);
		} catch (Exception e) {
			LogHome.getLog().error("添加定时任务出错:"+e.getMessage(),e);
		}
	}
	
}
