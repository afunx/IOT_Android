package com.espressif.iot.constants;

/**
 * some times, the constants change, 
 * e.g. UDP_BROADCAST_TIMEOUT should be less when the network is good,
 * 						      but be more when the network is bad
 * @author afunx
 *
 */
public class CONSTANTS_DYNAMIC {
	// *************STAConnectHelper.java*************
	public static int UDP_BROADCAST_TIMEOUT_DYNAMIC = 6000;
	static int UDP_UNICAST_TIMEOUT_DYNAMIC = 500;
	// *************STAConnectHelper.java*************
}
