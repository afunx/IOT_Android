package com.espressif.iot.ui.android.login;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.espressif.iot.db.device.IOTDeviceDBManager;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.STATUS;
import com.espressif.iot.model.internet.User;
import com.espressif.iot.tasknet.rest.RestGetHelper;
import com.espressif.iot.util.Logger;

/**
 * curl -H "Authorization: token be5077fe3222f165f16e0ecd7736131e0f9f4f6e"
 * http://114.215.177.97/v1/user/devices/ 
 * 
 * GET http://114.215.177.97/v1/user/devices/
 * HeaderKey: Authorization
 * HeaderValue: token be5077fe3222f165f16e0ecd7736131e0f9f4f6e
 * 
 * 
 * { "status": 200, 
 * 		"devices": 
 * 		[ 
 * 			{ 	
 * 				... 
 * 				"metadata": "ff:ff:ff:77:85:00 plug",
 * 				...
 * 				"key": 
 * 				{ 
 * 					..., 
 * 					"token": "ab2819caf9a87f61c2004097c251c8a010cca277",
 * 					... 
 * 				} 
 * 			} 
 * 		]
 * }
 * 
 * Synchronized the server data to local database
 * 
 * @author afunx
 * 
 */
public class SynchronizeServerLocalHelper {

	private static final String TAG = "SynchronizeSeverLocalHelper";
	private static RestGetHelper restGetHelper = RestGetHelper.getInstance();
	private static final String UrlUserDevices = "http://114.215.177.97/v1/user/devices/";
	private static final String Authorization = "Authorization";
	private static final String Token = "token";
	private static final String Key = "key";
	private static IOTDeviceDBManager iotDeviceDBManager;// = IOTDeviceDBManager.getInstance(context);
	
	/**
	 * it must init() before using
	 * @param context
	 */
	public static void init(Context context){
		iotDeviceDBManager = IOTDeviceDBManager.getInstance(context);
	}
	
	private static JSONArray getJSONArrayDevices(String userKey) {
		String headerKey = Authorization;
		String headerValue = Token+" "+userKey;
		JSONObject jsonObjectResult = restGetHelper.restGetJSONSyn(UrlUserDevices, headerKey, headerValue);
		if(jsonObjectResult!=null){
			try {
				int status = Integer.parseInt(jsonObjectResult.getString("status"));
				if(status==HttpStatus.SC_OK){
					Logger.d(TAG, "getJSONArrayDeviceInfo() suc");
					JSONArray jsonArray = jsonObjectResult.getJSONArray("devices");
					return jsonArray;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Logger.w(TAG, "getJSONArrayDeviceInfo() fail");
		return null;
	}

	
//	for(int i=0;i<jsonArray.length();i++){
//		TemHumData temHumData = new TemHumData();
//		JSONObject jsonObject = jsonArray.getJSONObject(i);
//		String at = jsonObject.getString("at");
//		int x = Integer.parseInt(jsonObject.getString("x"));
//		int y = Integer.parseInt(jsonObject.getString("y"));
//		temHumData.setAt(at);
//		temHumData.setX(x);
//		temHumData.setY(y);
//		resultList.add(temHumData);
//	}
	public static void synchronize(String userKey) {
		JSONArray devicesJsonArray = getJSONArrayDevices(userKey);
		if(devicesJsonArray!=null){
			for(int i=0;i<devicesJsonArray.length();i++){
				try {
					JSONObject deviceJsonObject = devicesJsonArray.getJSONObject(i);
					String metadata = deviceJsonObject.getString("metadata");
					// "metadata": "18:fe:34:77:85:00 plug"
					// "18:fe:34:77:85:00" 's length() is 17
					String bssid = metadata.substring(0, 17);
					String typeStr = metadata.substring(18);
					String status = IOTDevice.getIOTDeviceStatus(STATUS.INTERNET);
					long deviceId = Long.parseLong(deviceJsonObject.getString("id"));
					
					JSONObject keyJsonObject = deviceJsonObject.getJSONObject(Key);
					boolean isOwner = false;
					if(Integer.parseInt(keyJsonObject.getString("is_owner_key"))==1)
						isOwner = true;
					String deviceKey = keyJsonObject.getString(Token);
					
					iotDeviceDBManager.
					addDevice(bssid, typeStr, status, isOwner, deviceKey, true, deviceId
							,User.id );
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// mIOTDeviceDBManager.addDevice(BSSID, type, status, isOwner, deviceKey,
	// true, deviceId
	// ,User.id );
	// private static IOTDeviceDBManager sIOTDeviceDBManager;
}
