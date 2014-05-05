package com.espressif.iot.mediator;

import java.net.InetAddress;

import org.json.JSONObject;

import android.util.Log;

import com.espressif.iot.mediator.json.JsonParser;
import com.espressif.iot.mediator.json.model.IOTSwitch;
import com.espressif.iot.mediator.json.model.Info;
import com.espressif.iot.mediator.json.model.WifiConfigure;
import com.espressif.iot.mediator.json.model.softap.WifiConfigureSoftAP;
import com.espressif.iot.mediator.json.model.sta.WifiConfigureSta;
import com.espressif.iot.mediator.json.model.stasoftap.WifiConfigureStaSoftAP;
import com.espressif.iot.model.device.IOTActionEnum;
import com.espressif.iot.model.device.IOTDevice;
import com.espressif.iot.model.device.softap.IOTSoftAP;
import com.espressif.iot.model.device.sta.IOTSta;
import com.espressif.iot.model.device.sta_softap.IOTCommonStatus;
import com.espressif.iot.tasknet.rest.RestGetHelper;
import com.espressif.iot.tasknet.rest.RestPostHelper;

/**
 * Mediator Pattern
 * 
 * It is used to intermediate something about IOT
 * 
 * @author afunx
 * 
 */
public class IntermediatorIOT {

	private static final String TAG = "IntermediatorIOT";

	private static IntermediatorIOT instance = new IntermediatorIOT();

	private RestGetHelper restGetHelper = RestGetHelper.getInstance();
	private RestPostHelper restPostHelper = RestPostHelper.getInstance();

	private final JSONObject iotSwtichOnJSON;
	private final JSONObject iotSwtichOffJSON;

	// Singleton Pattern
	private IntermediatorIOT() {
		// generate the jsonObject of iotSwitchOn and iotSwitchOff
		IOTSwitch iotSwitchOn = new IOTSwitch();
		IOTSwitch iotSwitchOff = new IOTSwitch();
		iotSwitchOn.setStatus(1);
		iotSwitchOff.setStatus(0);
		iotSwtichOnJSON = JsonParser.parse(iotSwitchOn);
		iotSwtichOffJSON = JsonParser.parse(iotSwitchOff);
	}

	public static IntermediatorIOT getInstance() {
		return instance;
	}

	// parse the Rest Uri according to IOTDevice and IOTActionEnum
	private String parseRestUri(IOTDevice device, IOTActionEnum action) {
		InetAddress addr = device.getIOTAddress().getInetAddress();
		switch (action) {
		case IOT_ACTION_SWITCH_ON:
		case IOT_ACTION_SWITCH_OFF:
			return "http:/" + addr + "/" + "config?command=switch";
		case IOT_ACTION_GET_SWITCH:
			return "http:/" + addr + "/" + "config?command=switch";
		case IOT_ACTION_GET_INFO:
			return "http:/" + addr + "/" + "client?command=info";
		case IOT_ACTION_SET_WIFI_CONFIGURE:
			return "http:/" + addr + "/" + "config?command=wifi";
		case IOT_ACTION_GET_WIFI_CONFIGURE:
			return "http:/" + addr + "/" + "config?command=wifi";
		case IOT_ACTION_SET_STA_CONFIGURE:
			return "http:/" + addr + "/" + "config?command=wifi";
		case IOT_ACTION_SET_SOFTAP_CONFIGURE:
			return "http:/" + addr + "/" + "config?command=wifi";
		case IOT_ACTION_GET_STA_SOFTAP_CONFIGURE:
			return "http:/" + addr + "/" + "config?command=wifi";
		default:
			Log.e(TAG, "parseRestUri() don't support it");
			break;
		}
		return null;
	}

	// parse the JSONObject used by Post according to IOTDevice and
	// IOTActionEnum
	private JSONObject parsePostJsonObject(IOTDevice device,
			IOTActionEnum action) {
		switch (action) {
		case IOT_ACTION_SWITCH_ON:
			return iotSwtichOnJSON;
		case IOT_ACTION_SWITCH_OFF:
			return iotSwtichOffJSON;
		case IOT_ACTION_SET_WIFI_CONFIGURE:
			WifiConfigure wifiConfigure = new WifiConfigure();
			wifiConfigure.setSSID(device.getIOTCommonStatus().getSSID());
			wifiConfigure
					.setPassword(device.getIOTCommonStatus().getPassword());
			return JsonParser.parse(wifiConfigure);
		case IOT_ACTION_SET_STA_CONFIGURE:
			WifiConfigureSta wifiConfigureSta = new WifiConfigureSta();
			wifiConfigureSta.setSSID(device.getIOTSta().getSSID());
			wifiConfigureSta.setPassword(device.getIOTSta().getPassword());
			wifiConfigureSta.setToken(device.getIOTSta().getToken());
			return JsonParser.parse(wifiConfigureSta);
		case IOT_ACTION_SET_SOFTAP_CONFIGURE:
			WifiConfigureSoftAP wifiConfigureSoftAp = new WifiConfigureSoftAP();
			wifiConfigureSoftAp.setSSID(device.getIOTSoftAP().getSSID());
			wifiConfigureSoftAp.setPassword(device.getIOTSoftAP().getPassword());
			wifiConfigureSoftAp.setAuthmode(device.getIOTSoftAP().getAuthmode());
			wifiConfigureSoftAp.setChannel(device.getIOTSoftAP().getChannel());
			return JsonParser.parse(wifiConfigureSoftAp);
		default:
			Log.e(TAG, "parsePostJsonObject() don't support it");
			break;
		}
		return null;
	}

	// update the IOTDevice according to the JsonObject gotten from GET
	private void parseGetJsonObject(IOTDevice device, IOTActionEnum action,
			JSONObject jsonObject) {
		IOTCommonStatus iotCommonStatus= device.getIOTCommonStatus();
		switch (action) {
		case IOT_ACTION_GET_INFO:
			Info info = new Info();
			JsonParser.unparse(jsonObject, info);
			iotCommonStatus.setManufacturer(info.getManufacturer());
			iotCommonStatus.setVersionMajor(info.getVersionMajor());
			iotCommonStatus.setVersionMinor(info.getVersionMinor());
			break;
		case IOT_ACTION_GET_SWITCH:
			IOTSwitch iotSwitch = new IOTSwitch();
			JsonParser.unparse(jsonObject, iotSwitch);
			iotCommonStatus.setStatus(iotSwitch.getStatus());
			break;
		case IOT_ACTION_GET_WIFI_CONFIGURE:
			WifiConfigure wifiConfigure = new WifiConfigure();
			JsonParser.unparse(jsonObject, wifiConfigure);
			iotCommonStatus.setSSID(wifiConfigure.getSSID());
			iotCommonStatus.setPassword(wifiConfigure.getPassword());
			break;
		case IOT_ACTION_GET_STA_SOFTAP_CONFIGURE:
			WifiConfigureStaSoftAP wifiConfigureStaSoftAp = new WifiConfigureStaSoftAP();
			JsonParser.unparse(jsonObject, wifiConfigureStaSoftAp);
			
			IOTSoftAP iotSoftAp = device.getIOTSoftAP();
			iotSoftAp.setAuthmode(wifiConfigureStaSoftAp.getSoftAPAuthmode());
			iotSoftAp.setChannel(wifiConfigureStaSoftAp.getSoftAPChannel());
			iotSoftAp.setGateWay(wifiConfigureStaSoftAp.getSoftAPGetway());
			iotSoftAp.setIP(wifiConfigureStaSoftAp.getSoftAPIP());
			iotSoftAp.setMask(wifiConfigureStaSoftAp.getSoftAPMask());
			iotSoftAp.setPassword(wifiConfigureStaSoftAp.getSoftAPPassword());
			iotSoftAp.setSSID(wifiConfigureStaSoftAp.getSoftAPSSID());
			
			IOTSta iotSta = device.getIOTSta();
			iotSta.setGateWay(wifiConfigureStaSoftAp.getStaGetway());
			iotSta.setIP(wifiConfigureStaSoftAp.getStaIP());
			iotSta.setMask(wifiConfigureStaSoftAp.getStaMask());
			iotSta.setPassword(wifiConfigureStaSoftAp.getStaPassword());
			iotSta.setSSID(wifiConfigureStaSoftAp.getStaSSID());
			break;
		default:
			Log.e(TAG, "parseGetJsonObject() don't support it");
			break;
		}
	}

	/**
	 * execute an action of the iot device
	 * 
	 * @param device
	 *            the device
	 * @param action
	 *            the action to be done
	 * @return whether the action executed succeed
	 */
	public boolean executeIOTAction(IOTDevice device, IOTActionEnum action) {
		String uri = parseRestUri(device, action);
		JSONObject jsonObject;
		boolean succeed = false;
		Log.d(TAG, "executeIOTAction action: " + action);
		Log.d(TAG, "executeIOTAction uri: " + uri);
		switch (action) {
		case IOT_ACTION_SWITCH_ON:
		case IOT_ACTION_SWITCH_OFF:
		case IOT_ACTION_SET_WIFI_CONFIGURE:
		case IOT_ACTION_SET_STA_CONFIGURE:
		case IOT_ACTION_SET_SOFTAP_CONFIGURE:
			jsonObject = parsePostJsonObject(device, action);
			if(restPostHelper.restPostJSONSyn(uri, jsonObject,null,null)!=null)
				succeed = true;
//			succeed = restPostHelper.restPostJSONSyn(uri, jsonObject,null,null);
			break;
		case IOT_ACTION_GET_INFO:
		case IOT_ACTION_GET_SWITCH:
		case IOT_ACTION_GET_WIFI_CONFIGURE:
		case IOT_ACTION_GET_STA_SOFTAP_CONFIGURE:
			jsonObject = restGetHelper.restGetJSONSyn(uri);
			if(jsonObject!=null){
				parseGetJsonObject(device, action, jsonObject);
				succeed = true;
			}
			break;
		default:
			Log.e(TAG, "executeIOTAction() don't support it");
			break;
		}
		return succeed;
	}

}
