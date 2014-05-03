package com.espressif.iot.thread;

import java.util.concurrent.Callable;

public abstract class AbsTaskSyn<T> implements Callable<Boolean>{
	
	protected String reason;
	
	protected final String taskName;
	
	protected Object result;
	
	public AbsTaskSyn(String taskName){
		this.taskName = new String(taskName);
	}
	
	public String getTaskName(){
		return taskName;
	}
	
	public void setReason(String reason){
		this.reason = reason;
	}
	
	@Override
	public abstract Boolean call();
	
	@Override
	public abstract String toString();
	
	protected abstract void doAfterFailed();
	
	/**
	 * NOTE:
	 * this method must be called after call()
	 * @return
	 */
	public abstract T getResult();
}
