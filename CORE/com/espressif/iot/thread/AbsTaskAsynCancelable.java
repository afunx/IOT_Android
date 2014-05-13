package com.espressif.iot.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class AbsTaskAsynCancelable <T> implements Callable<T>{

private static ExecutorService exec = FixedThreadPool.getInstance().getExecutorService();
	
	protected final String taskName;
	
	private Future<T> mFuture;
	
	public AbsTaskAsynCancelable(String taskName){
		this.taskName = taskName+" AsynCancellable";
	}
	
	public void executeAsyn(){
		mFuture = exec.submit(this);
	}
	
	public boolean cancel(boolean mayInterruptIfRunning){
		return mFuture.cancel(mayInterruptIfRunning);
	}
	
	public boolean isDone(){
		return mFuture.isDone();
	}
	
}
