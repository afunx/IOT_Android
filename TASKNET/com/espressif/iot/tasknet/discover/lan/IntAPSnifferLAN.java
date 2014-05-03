package com.espressif.iot.tasknet.discover.lan;

import java.util.List;

import android.net.wifi.WifiInfo;

import com.espressif.iot.net.lan.wifi.WifiAdmin;

/**
 * @author afunx
 * 
 * @param <T>					type of the discovery
 */
public interface IntAPSnifferLAN<T> extends IntSnifferLAN{
	List<T> sniffSyn(WifiAdmin wifiAdmin, boolean isEspDevice);
}
