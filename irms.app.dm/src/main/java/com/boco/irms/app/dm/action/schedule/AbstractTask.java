package com.boco.irms.app.dm.action.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.boco.common.util.debug.LogHome;

public abstract class AbstractTask implements Job {
	private JobExecutionContext context;

	public JobExecutionContext getContext() {
		return context;
	}
	
	@Override
	public void execute(JobExecutionContext paramJobExecutionContext) throws JobExecutionException {
		context = paramJobExecutionContext;
		Date fireTime = paramJobExecutionContext.getFireTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd H:m:s");
		String strDate = formatter.format(fireTime);
		JobDetail jobDetail = paramJobExecutionContext.getJobDetail();

		LogHome.getLog().info("开始执行任务======任务名称：" + jobDetail.getKey().getName() + "\n任务执行时间：" + strDate);
		process();

	}
	public abstract void process();
}
