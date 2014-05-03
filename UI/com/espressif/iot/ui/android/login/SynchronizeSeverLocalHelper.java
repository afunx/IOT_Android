package com.espressif.iot.ui.android.login;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.IOTDevice.STATUS;
import com.espressif.iot.model.internet.TemHumData;
import com.espressif.iot.tasknet.rest.RestGetHelper;

/**
 * GET http://114.215.177.97/v1/user/devices/
 * 
 * {"status": 200, "devices": [{"bssid": "7c:80:88:33:44:66", "visibly": 1,
 * "activate_status": 1, "serial": "7f064717", "id": 1, "last_active":
 * "2014-04-29 19:03:22", "last_pull": "2014-04-29 19:03:22", "last_push":
 * "2014-04-29 19:03:22", "location": "", "metadata": "", "status": 2,
 * "updated": "2014-04-29 19:03:22", "description":
 * "description Of device(serial: 7f064717)", "activated_at":
 * "2014-04-29 19:03:22", "key": {"updated": "2014-04-24 20:38:00", "user_id":
 * 1, "product_id": 0, "name": "device activate share token", "created":
 * "2014-04-24 20:38:00", "source_ip": "*", "visibly": 1, "id": 39,
 * "datastream_tmpl_id": 0, "token": "36443931f53f8f863b81044415caa1753540d57d",
 * "access_methods": "*", "is_owner_key": 1, "scope": 3, "device_id": 1,
 * "activate_status": 1, "datastream_id": 0, "expired_at":
 * "2288-02-05 22:24:04"}, "datastreamTmpls": [{"updated":
 * "2014-04-22 19:11:24", "description": "plug-status1:on,0:off", "name":
 * "plug-status", "created": "2014-04-22 19:11:24", "symbol": "on/off", "tags":
 * "plug", "stream_type": 1, "visibly": 1, "id": 1, "dimension": 1, "unit":
 * "boolean", "product_id": 1}, {"updated": "2014-04-25 15:53:09",
 * "description": "record temperature and humidity ", "name": "tem_hum",
 * "created": "2014-04-25 15:53:09", "symbol": "", "tags": "tem, hum",
 * "stream_type": 1, "visibly": 1, "id": 3, "dimension": 2, "unit": "",
 * "product_id": 1}, {"updated": "2014-04-28 09:42:22", "description":
 * "light up", "name": "light", "created": "2014-04-28 09:42:22", "symbol": "",
 * "tags": "light,color", "stream_type": 1, "visibly": 1, "id": 4, "dimension":
 * 5, "unit": "", "product_id": 1}], "is_private": 1, "product_id": 1, "name":
 * "deviceplug1", "created": "2014-04-22 19:11:24", "is_frozen": 0, "key_id":
 * 2}, {"bssid":...
 * 
 * Synchronized the server data to local database
 * 
 * @author afunx
 * 
 */
public class SynchronizeSeverLocalHelper {

	private static final String TAG = "SynchronizeSeverLocalHelper";
	private static RestGetHelper restGetHelper = RestGetHelper.getInstance();
	private static final String UrlUserDevices = "http://114.215.177.97/v1/user/devices/";
	private static final String Authorization = "Authorization";
	private static final String Token = "Token";
	
	private static JSONArray getJSONArrayDeviceInfo(String userKey) {
		String headerKey = Authorization;
		String headerValue = Token+" "+userKey;
		JSONObject jsonObjectResult = restGetHelper.restGetJSONSyn(UrlUserDevices, headerKey, headerValue);
		if(jsonObjectResult!=null){
			try {
				int status = Integer.parseInt(jsonObjectResult.getString("status"));
				if(status==HttpStatus.SC_OK){
					Log.d(TAG, "getJSONArrayDeviceInfo() suc");
					JSONArray jsonArray = jsonObjectResult.getJSONArray("devices");
					return jsonArray;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.w(TAG, "getJSONArrayDeviceInfo() fail");
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
		JSONArray devicesJsonArray = getJSONArrayDeviceInfo(userKey);
		if(devicesJsonArray!=null){
			for(int i=0;i<devicesJsonArray.length();i++){
				try {
					JSONObject deviceJsonObject = devicesJsonArray.getJSONObject(i);
					String bssid = deviceJsonObject.getString("bssid");
					String type;
					String status = IOTDevice.getIOTDeviceStatus(STATUS.INTERNET);;
					boolean isOwner = false;
					if(Integer.parseInt(deviceJsonObject.getString("is_owner_key"))==1)
						isOwner = true;
//					String deviceKey = ;
					long deviceId = Long.parseLong(deviceJsonObject.getString("id"));
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
