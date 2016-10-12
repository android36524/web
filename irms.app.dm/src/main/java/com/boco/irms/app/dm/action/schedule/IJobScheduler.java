package com.boco.irms.app.dm.action.schedule;

/**
 * @author chengwenfeng
 *调度器执行Job的接口
 * @param <T>
 */
public interface IJobScheduler<T>{

	@SuppressWarnings("rawtypes")
	public String run(IConfigInfo taskInfo,Class<T> clazz) throws Exception;//实现任务调度
}
