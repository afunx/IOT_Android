package com.espressif.iot.tasknet.rest;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.thread.FixedThreadPool;

public class RestGetHelper {

	private FixedThreadPool threadPool = FixedThreadPool.getInstance();

	private static final String TAG = "RestGetHelper";
	private static RestGetHelper instance = new RestGetHelper();

	// Singleton pattern
	private RestGetHelper() {
	}

	public static RestGetHelper getInstance() {
		return instance;
	}

	public JSONObject restGetJSONSyn(String uriString) {
		AbsTaskSyn<JSONObject> restGetJSONTask = new RestGetJsonTask(
				"rest get json task", uriString);
		threadPool.executeSyn(restGetJSONTask,
				CONSTANTS.REST_GET_JSON_TIMEOUT_MILLISECONDS,
				TimeUnit.MILLISECONDS);
		return restGetJSONTask.getResult();
	}

	public JSONObject restGetJSONSyn(String uriString, String headerKey,
			String headerValue) {
		AbsTaskSyn<JSONObject> restGetJSONTask = new RestGetJsonTask(
				"rest get json task", uriString, headerKey, headerValue);
		threadPool.executeSyn(restGetJSONTask,
				CONSTANTS.REST_GET_JSON_TIMEOUT_MILLISECONDS,
				TimeUnit.MILLISECONDS);
		return restGetJSONTask.getResult();
	}
}
