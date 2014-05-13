package com.espressif.iot.tasknet.discover;

import java.util.List;

import android.content.Context;

import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.net.lan.wifi.WifiAdmin;
import com.espressif.iot.net.lan.wifi.WifiScanResult;
import com.espressif.iot.tasknet.discover.lan.APSnifferLAN;
import com.espressif.iot.tasknet.discover.lan.STASnifferLAN;
import com.espressif.iot.tasknet.discover.wan.MonetSnifferWAN;
import com.espressif.iot.tasknet.discover.wan.WifiSnifferWAN;

/**
 * Proxy Pattern
 * @author afunx
 *
 */
public class Discoverer {
	
	private APSnifferLAN mAPSnifferLAN = APSnifferLAN.getInstance();
	private STASnifferLAN mSTASnifferLAN = STASnifferLAN.getInstance();
	
	private WifiSnifferWAN mWifiSnifferWAN = WifiSnifferWAN.getInstance();
	private MonetSnifferWAN mMonetSnifferMonet = MonetSnifferWAN.getInstance();
	
	private static Discoverer instance = new Discoverer();
	// Singleton
	public static Discoverer getInstance(){
		return instance;
	}
	
	/**
	 * @param context	the Context
	 * @return		whether Internet is accessible by wifi
	 */
	public boolean isInternetAccessedWifiWANSyn(Context context){
		return mWifiSnifferWAN.sniffer(context);
	}
	
	/**
	 * @param context	the Context
	 * @return		whether Internet is accessible by monet
	 */
	public boolean isInternetAccessedMonetWANSyn(Context context){
		return mMonetSnifferMonet.sniffer(context);
	}
	
	/**
	 * get the connected AP's BSSID
	 * @param wifiAdmin
	 * @return
	 */
	public String getBSSIDSyn(WifiAdmin wifiAdmin){
		return wifiAdmin.getBSSID();
	}
	
	/**
	 * find the APs in LAN synchronized(using wifi scan)
	 * @param wifiAdmin
	 * @param isEspDevice
	 * @return
	 */
	public List<WifiScanResult> findAPsLANSyn(WifiAdmin wifiAdmin, boolean isEspDevice){
		return mAPSnifferLAN.sniffSyn(wifiAdmin, isEspDevice);
	}
	
	/**
	 * find the STAs in LAN synchronized(using UDP broadcast socket)
	 * @return
	 */
	public List<IOTAddress> findSTAsLANSyn(){
		return mSTASnifferLAN.sniffSyn();
	}
	
	/**
	 * whether the STA is exist in LAN synchronized(using UDP socket)
	 * @param iotAddress
	 * @param timeoutSeconds
	 * @return
	 */
	public boolean isSTAExistLANSyn(IOTAddress iotAddress, int timeoutSeconds){
		return mSTASnifferLAN.isExistSyn(iotAddress, timeoutSeconds);
	}
	
}
