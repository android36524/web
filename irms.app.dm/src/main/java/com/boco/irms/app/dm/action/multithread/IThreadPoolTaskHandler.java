package com.boco.irms.app.dm.action.multithread;


public interface IThreadPoolTaskHandler<T> {

	public void doTask(T task) ;

	public void addTasks(ITaskThreadPool<T> taskThreadPool);

}
