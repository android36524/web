package com.boco.irms.app.dm.action.multithread;

import java.util.concurrent.LinkedBlockingQueue;



public class ThreadPoolUtils {

	
	/**
	 * 调用例子
	 * SimpleThreadPoolUtils.synExecuteThreadPool(new IThreadPoolTaskHandler<String>() {
			int count=1;
			public void addTasks(ITaskThreadPool<String> taskThreadPool) {
				for(String cuid:wireSegCuids) {
					taskThreadPool.addTask(cuid);
				}
			}
			public void doTask(String wireSegCuid) {
				count++;
			}
			
		});
	 */

	/**
	 * 会等待执行完成
	 * @param <T>
	 * @param threadPoolTaskHandler
	 */
	public static <T> void  synExecuteThreadPool(final IThreadPoolTaskHandler<T> threadPoolTaskHandler) {
		executeThreadPool( threadPoolTaskHandler, true);
	}
	/**
	 * 不会等待执行完成
	 * @param <T>
	 * @param threadPoolTaskHandler
	 */
	public static <T> void  asynExecuteThreadPool(final IThreadPoolTaskHandler<T> threadPoolTaskHandler) {
		executeThreadPool( threadPoolTaskHandler, false);
	}
	
	public static <T> void  executeThreadPool(final IThreadPoolTaskHandler<T> threadPoolTaskHandler,final boolean syn) {
		executeThreadPool(10, threadPoolTaskHandler, syn);
	}
	
	public static <T> void  executeThreadPool(int maximumPoolSize,final IThreadPoolTaskHandler<T> threadPoolTaskHandler,final boolean syn) {
		executeThreadPoolWithSize(maximumPoolSize, threadPoolTaskHandler, syn, true);
	}
	
	public static <T> void  executeThreadPoolWithSize(int maximumPoolSize,final IThreadPoolTaskHandler<T> threadPoolTaskHandler,final boolean syn,final boolean withSize) {
		final Object lock=new Object();
		IThreadPoolHandler<T> threadPoolHandler=new DefaultThreadPoolHandler<T>() {
			public void allTaskFinished() {
				if (syn) {
					synchronized (lock) {
						lock.notify();
					}
				}
			}
			public void doTask(T task) {
				threadPoolTaskHandler.doTask(task);
			}
		};
		if(syn) {
			SimpleTaskThreadPool<T> taskThreadPool=null;
			if(withSize){
				taskThreadPool = new SimpleTaskThreadPool<T>(maximumPoolSize,threadPoolHandler);
			}else{
				taskThreadPool = new SimpleTaskThreadPool<T>(maximumPoolSize,threadPoolHandler,new LinkedBlockingQueue<Runnable>());

			}
			threadPoolTaskHandler.addTasks(taskThreadPool);
			taskThreadPool.setFinished();
//			if(taskThreadPool.taskQueue.size()==0) {
//				taskThreadPool.shutdown();
//				return;
//			}
		}else {//不限制队列长度
			final SimpleTaskThreadPool<T> taskThreadPool = new SimpleTaskThreadPool<T>(maximumPoolSize,threadPoolHandler,new LinkedBlockingQueue<Runnable>());
			threadPoolTaskHandler.addTasks(taskThreadPool);
			taskThreadPool.setFinished();
//			taskThreadPool.setIfFinishedShutdown(true);
//			if(taskThreadPool.taskQueue.size()==0) {
//				taskThreadPool.shutdown();
//				return;
//			}
		}

		if (syn) {
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
