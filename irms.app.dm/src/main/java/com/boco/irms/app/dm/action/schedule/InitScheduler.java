package com.boco.irms.app.dm.action.schedule;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;


/**
 * @author chengwenfeng
 * 单例模式，生成调度器
 *
 */
public class InitScheduler {

	public static SchedulerFactory df = new StdSchedulerFactory();
	public static Scheduler scheduler;
	
	public static Scheduler getScheduler() throws SchedulerException{
		if(scheduler == null){
			scheduler = df.getScheduler();
		}
		return scheduler;
	}
}
