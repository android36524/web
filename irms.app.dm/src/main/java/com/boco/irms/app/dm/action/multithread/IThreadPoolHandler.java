package com.boco.irms.app.dm.action.multithread;

public interface IThreadPoolHandler<E> {

	public void allTaskFinished();
	public void taskFinished(E task);

	public void doTask(E task);
}
