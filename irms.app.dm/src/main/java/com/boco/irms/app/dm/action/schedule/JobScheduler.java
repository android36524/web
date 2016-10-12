package com.boco.irms.app.dm.action.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

import com.boco.common.util.debug.LogHome;


public class JobScheduler implements IJobScheduler<JobWorking> {

	private Map<String,String> map;

	public JobScheduler(Map<String,String> map) {
		this.map = map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String run(IConfigInfo taskInfo, Class<JobWorking> clazz)
			throws Exception {
		List<Map<String, String>> list = taskInfo.getTaskInfo(map);
		Map<String, String> map = list.get(0);
//		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = InitScheduler.getScheduler();
		String taskName = map.get("taskName");
		JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(taskName).build();
		//jobDetail.getJobDataMap().put("object", map);
		String cronstr = map.get("scheduTactic");
		String triggerName = map.get("triggerName");
		CronTrigger cronTrigger = newTrigger()
				.withIdentity(triggerName)
				.withSchedule(cronSchedule(cronstr)).build();
		Date date = sched.scheduleJob(jobDetail, cronTrigger);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
		String tasktime = sdf.format(date);
		LogHome.getLog().info("任务名称:"+taskName+"\n"+"触发器名称:"+triggerName+"\n"+"调度器执行时间"+tasktime);
		sched.start();
		
		return tasktime;

	}

}
