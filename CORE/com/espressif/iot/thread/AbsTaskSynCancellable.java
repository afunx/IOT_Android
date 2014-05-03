package com.espressif.iot.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public abstract class AbsTaskSynCancellable<T> implements Callable<T>{

	private static ExecutorService exec = FixedThreadPool.getInstance().getExecutorService();
	
	protected final String taskName;
	
	private Future<T> mFuture;

	private T result;
	
	public AbsTaskSynCancellable(String taskName){
		this.taskName = taskName+" SynCancellable";
	}
	
	public boolean cancel(boolean mayInterruptIfRunning){
		return mFuture.cancel(mayInterruptIfRunning);
	}

	public void executeSyn(){
		mFuture = exec.submit(this);
		try {
			result = mFuture.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void executeSyn(int timeout, TimeUnit unit){
		mFuture = exec.submit(this);
		try {
			result = mFuture.get(timeout, unit);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * !!!NOTE	must check whether the result is null
	 * @return	the result
	 */
	public T getResult(){
		return result;
	}
	
}
