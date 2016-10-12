package com.boco.irms.app.dm.action.schedule;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.boco.common.util.debug.LogHome;

/**
 * @author chengwenfeng
 * 实现了Job接口的抽象类
 *
 */
public abstract class JobWorking implements Job {

	private JobExecutionContext cte;
	
	public JobExecutionContext getContext() {
		return cte;
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		cte = context;
		//Object obj = context.getJobDetail().getJobDataMap().get("object");
		processJob();
		
		Date fireDate = context.getFireTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
		String fireTime = sdf.format(fireDate);
		String taskName = context.getJobDetail().getKey().getName();
		String nextFireTime = "";
		String previousFireTime = "";
		if(context.getNextFireTime()!=null){
			nextFireTime = sdf.format(context.getNextFireTime());
		}
		if(context.getPreviousFireTime()!=null){
			previousFireTime = sdf.format(context.getPreviousFireTime());
		}
		if(!nextFireTime.equals("") && !previousFireTime.equals("")){
			LogHome.getLog().info("任务执行时间:" + fireTime + "\n" + "任务名称:" + taskName + "\n"
					  + "任务上次执行时间:"+previousFireTime+"\n"+"任务下次执行时间:" + nextFireTime);
		}else{
			LogHome.getLog().info("任务执行时间:" + fireTime + "\n" + "任务名称:" + taskName + "\n");
		}
		
	}
	
	public abstract void processJob();//任务执行

}
