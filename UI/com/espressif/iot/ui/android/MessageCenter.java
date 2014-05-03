package com.espressif.iot.ui.android;

import android.os.Bundle;
import android.os.Message;

import com.espressif.iot.constants.WIFI_ENUM;

public class MessageCenter {
	
	public interface CONSTANTS {
		final int MSG_POP_PASSWORD_SETTING = 0;
		final int MSG_WIFI_CONNECTED_SUCCEED = 1;
	}
	
	/**
	 * generate the pop Message
	 * 
	 * when the activity receive the message, it will pop the dialog of wifi
	 * connect
	 * 
	 * @param SSID
	 *            the SSID which is to be connected
	 * @param type
	 *            the WIFI_ENUM.WifiCipherType of cipher
	 * @return the message to be sent
	 */
	public static Message genPopMessage(String SSID,WIFI_ENUM.WifiCipherType type) {
		Message msg = new Message();
		msg.what = CONSTANTS.MSG_POP_PASSWORD_SETTING;
		Bundle bundle = new Bundle();
		bundle.putString("SSID", SSID);
		bundle.putString("type", type.toString());
		msg.setData(bundle);
		return msg;
	}
	
	public static Message genPopMessage(String SSID){
		Message msg = new Message();
		msg.what = CONSTANTS.MSG_WIFI_CONNECTED_SUCCEED;
		Bundle bundle = new Bundle();
		bundle.putString("SSID", SSID);
		msg.setData(bundle);
		return msg;
	}
	
}
