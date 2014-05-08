package com.espressif.iot.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.espressif.iot.util.Logger;

import android.util.Log;

public class FixedThreadPool {

	private static final int POLLZ_SIZE = 8;

	private static final String TAG = "FixedThreadPoll";

	private static FixedThreadPool singleton = new FixedThreadPool();

	private ExecutorService exec = Executors.newFixedThreadPool(POLLZ_SIZE);

	// singleton pattern
	private FixedThreadPool() {
	}

	public static FixedThreadPool getInstance() {
		return singleton;
	}
	
	public ExecutorService getExecutorService(){
		return exec;
	}

	public void execute(Runnable runnable) {
		Logger.e(TAG,
				"this method is exist while debugging, before releasing, it should delete.");
		exec.execute(runnable);
	}

	/**
	 * run the task in the some time future
	 * 
	 * @param task
	 *            the asyn task
	 */
	public void executeAsyn(AbsTaskAsyn task) {
		Logger.d(TAG + ":" + task.getClass(), task.getTaskName());
		exec.execute(task);
	}

	/**
	 * it is a synchronous method
	 * 
	 * @param task
	 *            the task to be done
	 * @param timeout
	 *            time out
	 * @param unit
	 *            the unit of time out
	 * @return
	 */
	public boolean executeSyn(AbsTaskSyn<?> task, int timeout, TimeUnit unit) {
		Logger.d(TAG + ":" + task.getClass(), task.getTaskName() + ",submit():"
				+ "timeout=" + timeout + ",unit is " + unit);
		boolean taskResult = false;
		Future<Boolean> future = exec.submit(task);
		try {
			taskResult = future.get(timeout, unit);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Logger.w(TAG + ":" + task.getTaskName(), "InterruptedException");
			task.setReason("InterruptedException");
			// e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Logger.w(TAG + ":" + task.getTaskName(), "ExecutionException");
			task.setReason("ExecutionException");
			// e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			Logger.w(TAG + ":" + task.getTaskName(), "TimeoutException");
			task.setReason("TimeoutException");
			// e.printStackTrace();
		}
		if (taskResult) {
			Logger.d(TAG + ":" + task.getTaskName(), "taskResult = " + taskResult);
		} else {
			task.doAfterFailed();
			Logger.w(TAG + ":" + task.getTaskName(), "taskResult = " + taskResult);
		}
		return taskResult;
	}
}
