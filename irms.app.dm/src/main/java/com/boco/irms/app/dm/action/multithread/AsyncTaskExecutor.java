package com.boco.irms.app.dm.action.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author zhl
 * @param <V>
 */
public class AsyncTaskExecutor <V>{

	private Callable<V> callable;  
	private int threadSize ;   
	private static ExecutorService service;  
	public AsyncTaskExecutor(Callable<V> callable, int threadSize) {  
	        super();  
	        this.callable = callable;  
	        this.threadSize = threadSize;  
	    }  
	  
	    public List<Future<V>> start()  
	    {  
	        service = Executors.newFixedThreadPool(threadSize);  
	        List<Future<V>> list = new ArrayList<Future<V>>(threadSize);  
	        for (int i = 0; i < threadSize; i++)  
	        {  
	            list.add(service.submit(callable));  
	        }  
	        return list;  
	    }  
	  
	    public  ExecutorService getService() {  
	        return service;  
	    }  

}
