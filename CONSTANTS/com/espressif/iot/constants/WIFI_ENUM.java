package com.espressif.iot.constants;

public interface WIFI_ENUM {
	
	enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}
	
	enum WifiStatus {
		WIFISTATUS_CURRENT, WIFISTATUS_DISABLE, WIFISTATUS_ENABLE
	}
}
