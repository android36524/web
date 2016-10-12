package com.boco.irms.app.dm.action.multithread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleTaskThreadPool<E> implements ITaskThreadPool<E>{

	IThreadPoolHandler<E> threadPoolHandler;
	LinkedBlockingQueue<Runnable> taskQueue;
	ThreadPoolExecutor threadPoolExecutor = null;
	int unDealTaskCount=0;
	private Object lock=new Object();
	public SimpleTaskThreadPool(IThreadPoolHandler<E> threadPoolHandler) {
		this(10, threadPoolHandler);
	}
	/**
	 * 主要用于同步等待，优化了数据内存保持固定大小。
	 * @param maximumPoolSize
	 * @param threadPoolHandler
	 */
	public SimpleTaskThreadPool(int maximumPoolSize,
			IThreadPoolHandler<E> threadPoolHandler) {
		this(maximumPoolSize,threadPoolHandler,new LinkedBlockingQueue<Runnable>(maximumPoolSize * 4));// 最大队列的两倍,固定长度
	}
	
	/**
	 * 该方法只要后台运行
	 * @param maximumPoolSize
	 * @param threadPoolHandler
	 * @param taskQueue
	 */
	public SimpleTaskThreadPool(int maximumPoolSize,
			IThreadPoolHandler<E> threadPoolHandler,LinkedBlockingQueue taskQueue) {
		this.threadPoolHandler = threadPoolHandler;
		this.taskQueue=taskQueue;
		threadPoolExecutor = new MyThreadPoolExecutor(maximumPoolSize, maximumPoolSize,taskQueue);
	}
	

//	public void shutdown() {
//		threadPoolExecutor.shutdown();
//	}
	public void setFinished(){
		try {
			taskQueue.put(new FinishedTask());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addTask(E o) {
		try {
			if(unDealTaskCount==0) {
				threadPoolExecutor.prestartAllCoreThreads();
			}
			synchronized (lock) {
				unDealTaskCount++;
			}
			taskQueue.put(new ThreadPoolTask(o,unDealTaskCount));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	class MyThreadPoolExecutor extends ThreadPoolExecutor {

		public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
				BlockingQueue<Runnable> workQueue) {
			// 十分钟
			super(corePoolSize, maximumPoolSize, 10, TimeUnit.SECONDS,
					workQueue);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
		}

		@Override
		protected void terminated() {
			if (threadPoolHandler != null) {
				threadPoolHandler.allTaskFinished();
			}
		}

	}
	class FinishedTask implements Runnable {


		public FinishedTask() {
		}

		public void run() {
			
			while(threadPoolExecutor.getActiveCount()>1){
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			threadPoolExecutor.shutdown();
			threadPoolHandler.allTaskFinished();
		}
	}
	class ThreadPoolTask implements Runnable {

		private final E task;
		private final int id;
		private Throwable error ;//以后可以拿来做判断

		public ThreadPoolTask(E task,int id) {
			this.task = task;
			this.id = id;
		}

		public void run() {
			try {
				if (threadPoolHandler != null) {
					threadPoolHandler.doTask(task);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				error=e;
			}
		}
	}
	
	
}
