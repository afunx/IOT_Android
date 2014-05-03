package com.espressif.iot.tasknet.discover.lan;

import java.util.List;

import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.tasknet.udp.lan.STAConnectHelper;

public class STASnifferLAN implements IntSTASnifferLAN<IOTAddress> {

	private static STASnifferLAN instance = new STASnifferLAN();
	
	// Singleton Pattern
	private STASnifferLAN() {
	}
	public static STASnifferLAN getInstance() {
		return instance;
	}

	@Override
	public boolean isExistSyn(IOTAddress iotAddress, int timeoutSeconds) {
		// TODO Auto-generated method stub
		return STAConnectHelper.isConnectedSyn(iotAddress);
	}

	@Override
	public List<IOTAddress> sniffSyn() {
		// TODO Auto-generated method stub
		return STAConnectHelper.discoverIOTDevicesSyn();
	}
}
