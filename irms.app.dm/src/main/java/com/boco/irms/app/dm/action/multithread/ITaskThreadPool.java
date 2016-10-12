package com.boco.irms.app.dm.action.multithread;

public interface ITaskThreadPool<E> {

	public void addTask(E o);
}
