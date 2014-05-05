package com.espressif.iot.ui.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.espressif.iot.thread.AbsTaskAsyn;
import com.espressif.iot.thread.FixedThreadPool;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class UtilActivity {

	private static final String TAG = "UtilActivity";

	/**
	 * transfer from src activity to dest activity
	 * 
	 * @param srcActivity
	 *            source activity
	 * @param destActivityClass
	 *            dest activity
	 * @param finish
	 * 			  whether finish the srcActivity or not
	 * @return	the srcActivity
	 * 
	 */
	public static Activity transferActivity(Activity srcActivity,
			Class<?> destActivityClass, boolean finish) {
		Log.d(TAG, "transfer from " + srcActivity + ", to " + destActivityClass);
		Intent intent = new Intent();
		intent.setClass(srcActivity, destActivityClass);
		srcActivity.startActivity(intent);
		if(finish)
			srcActivity.finish();
		return srcActivity;
	}

	class handerUIPostTask extends AbsTaskAsyn{

		public handerUIPostTask(String taskName) {
			super(taskName);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
		}
		
	}
	
	/**
	 * it help Activity to process asynchronously
	 * 
	 * refresh() means refresh the IOT device
	 * refreshUI() means refresh the UI for the IOT device
	 * 
	 * !!!NOTE : object must use "XXXActivity.this" instead of "this"
	 * 
	 * @param handler			the Activity's handler
	 * @param refreshStr		the name of refresh() method
	 * @param refreshUIStr		the name of refreshUI() method
	 * @param object			the instance of the Activity
	 * @param clazz				the class of the Activity
	 * @param argsType			the class of the argsType for refresh method
	 * @param args				the arguments to the method
	 */
	public static void handlerUIPost(final Handler handler, String refreshStr, String refreshUIStr,
			final Object object, Class<?> clazz, Class<?> argsType,final Object... args) {
		
		try {
			final Method refresh;
			if(argsType==null)
				refresh = clazz.getDeclaredMethod(refreshStr, (Class<?>[])null);
			else
				refresh = clazz.getDeclaredMethod(refreshStr, argsType);
			refresh.setAccessible(true);
			
			final Method refreshUI;
			refreshUI = clazz.getDeclaredMethod(refreshUIStr, (Class<?>[])null);
			refreshUI.setAccessible(true);
			
			Runnable task = new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						refresh.invoke(object, args);
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									refreshUI.invoke(object, (Object[])null);
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						
						});
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
			
			FixedThreadPool.getInstance().execute(task);
			
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		

	}
}
