package com.espressif.iot.tasknet.udp.lan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.constants.CONSTANTS_DYNAMIC;
import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.thread.AbsTaskSyn;
import com.espressif.iot.thread.FixedThreadPool;

// Discover IOT Sta in LAN
public class STAConnectHelper {

	private static FixedThreadPool mThreadPool = FixedThreadPool.getInstance();

	private static InetAddress broadcastAddress;

	private static final String data = "Are You Espressif IOT Smart Device?";

	private static final TimeUnit TimeoutUnit = TimeUnit.MILLISECONDS;

	static{
		try {
			broadcastAddress = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private STAConnectHelper() {
	}

	/**
	 * check whether the IOT is still connected to the apk synchronized
	 * 
	 * @param iotAddress
	 *            iot device IP address
	 * @return whether it is connected
	 */
	public static boolean isConnectedSyn(IOTAddress iotAddress) {

		AbsTaskSyn<List<IOTAddress>> udpSocketTask = new UDPSocketTask(
				"connect task", -1, false, broadcastAddress, data);

		mThreadPool.executeSyn(udpSocketTask, CONSTANTS.UDP_UNICAST_TIMEOUT, TimeoutUnit);

		List<IOTAddress> singleResponseList = udpSocketTask.getResult();

		if (singleResponseList.isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * discover the IOT devices in the same LAN synchronized
	 * 
	 * @return the list of exist IOT devices
	 */
	public static List<IOTAddress> discoverIOTDevicesSyn() {

		AbsTaskSyn<List<IOTAddress>> udpSocketTask = new UDPSocketTask(
				"connect task", -1, true, broadcastAddress, data);

		mThreadPool.executeSyn(udpSocketTask, CONSTANTS_DYNAMIC.UDP_BROADCAST_TIMEOUT_DYNAMIC, TimeoutUnit);

		List<IOTAddress>responseList = udpSocketTask.getResult();

		return responseList;
	}
}
