package com.espressif.iot.constants;

public interface CONSTANTS {

	// *************WifiAdmin.java*************
	// timeout is 20 times as long as nexus4
	final int WIFI_RETRY_TIMES = 100;
	// nexus4 wifi open cost average 1643.55ms(100 times)
	final long WIFI_OPEN_SLEEP_MILLISECONDS = 400;
	// nexus4 wifi close cost average 465.68ms(100 times)
	final long WIFI_CLOSE_SLEEP_MILLISECONDS = 100;
	// nexus4 wifi scan 10ms or so
	final long WIFI_SCAN_SLEEP_MILLISECONDS = 5;
	// *************WifiAdmin.java*************

	// *************WifiScanResultManager.java*************
	final int AP_MAX_MISS_TIME = 10;
	// *************WifiScanResultManager.java*************

	// *************STAConnectHelper.java*************
	final int UDP_BROADCAST_TIMEOUT = 6000;
	final int UDP_UNICAST_TIMEOUT = 500;
	// *************STAConnectHelper.java*************

	// *************ReconnectAPTask.java*************
	final int IS_AP_CONNECTED_TIMEOUT_SECONDS = 30;
	// *************ReconnectAPTask.java*************

	// *************ReconnectAPTask.java*************
	final int CHECK_AP_CONNECTED_TIMEOUT_SECONDS = 30;
	// *************ReconnectAPTask.java*************

	// *************IOTDevicesManager.java*************
	final int STA_MAX_MISS_TIME = 1;
	// *************IOTDevicesManager.java*************

	// *************InternetAvailableHelper.java*************
	final int INTERNET_AVAILABLE_TIMEOUT_MILLISECONDS = 2000;
	// *************InternetAvailableHelper.java*************

	// *************Administrator.java*************
	// it is different from CHECK_AP_CONNECTED_TIMEOUT_SECONDS,
	// it is remained to change a better name
	final int CHECK_AP_CONNECTED_TIMEOUT_SECONDS_2 = 20;
	// *************Administrator.java*************
	
	// *************MonetSnifferWAN.java*************
	final int INTERNET_AVAILABLE_RETRY = 5;
	// *************MonetSnifferWAN.java*************

	// *************InternetAvailableHelper.java*************
	// it is questionable, "8.8.8.8" is more general but not steady in China
	final String PING_ADDRESS = "www.baidu.com";
	// *************InternetAvailableHelper.java*************

	// *************RestGetHelper.java***************
	final int REST_GET_JSON_TIMEOUT_MILLISECONDS = 20000;
	// *************RestGetHelper.java***************

	// *************RestPostHelper.java***************
	final int REST_POST_JSON_TIMEOUT_MILLISECONDS = 20000;
	// *************RestPostHelper.java***************
	
	// *************CheckAPConnectedForeverTask.java***************
	final int CHECK_AP_CONNECTED_FOREVER_SLEEP_MILLISECONDS = 1000;
	// *************CheckAPConnectedForeverTask.java***************
}
