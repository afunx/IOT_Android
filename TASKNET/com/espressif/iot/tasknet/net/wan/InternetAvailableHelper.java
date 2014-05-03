package com.espressif.iot.tasknet.net.wan;

import java.util.concurrent.TimeUnit;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.thread.FixedThreadPool;

/**
 * this class use ping www.baidu.com to check whether the Internet is available
 * @author afunx
 *
 */
public class InternetAvailableHelper {

	private static FixedThreadPool mThreadPool = FixedThreadPool.getInstance();

	private static final TimeUnit TimeoutUnit = TimeUnit.MILLISECONDS;

	public static boolean isInternetAvailableSyn() {
		
//		boolean result = false;
		
		// try "www.baidu.com" first
		AbsTaskSyn<Boolean> checkInternetAvailable = new CheckInternetAvailableTask(
				"check wifi connect task", CONSTANTS.PING_ADDRESS);

//		result = 
		mThreadPool.executeSyn(checkInternetAvailable,
				CONSTANTS.INTERNET_AVAILABLE_TIMEOUT_MILLISECONDS, TimeoutUnit);
		
//		return result;
		return checkInternetAvailable.getResult();
		
		// if "www.baidu.com" fail, try "www.google.com"
//		if(result){
//			return true;
//		}else{
//			checkInternetAvailable = new CheckInternetAvailableTask(
//					"check wifi connect task", "www.google.com");
//
//			result = mThreadPool.executeSyn(checkInternetAvailable,
//					CONSTANTS.INTERNET_AVAILABLE_TIMEOUT_MILLISECONDS, TimeoutUnit);
//			return result;
//		}
	}
}
