package com.espressif.iot.net.lan.wifi;

import java.lang.reflect.Method;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;

enum SoftApPasswordType{
	OPEN,WPA,WPA2
}

public class WifiApManager {
	public static final int WIFI_AP_STATE_FAILED= 4;
	 
    private final WifiManager mWifiManager;

    public WifiApManager(Context context) {
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
    }

    public boolean closeSoftAp(){
    	return setWifiApEnabled(null,false);
    }
    
    public boolean setWifiApEnabled(WifiConfiguration config,
            boolean enabled) {

        try {
            if (enabled) {	// disable WiFi in any case
                mWifiManager.setWifiEnabled(false);
            }

            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class,
                    boolean.class);
            
            return (Boolean) method.invoke(mWifiManager, config, enabled);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "getWifiApState");
            return (Integer) method.invoke(mWifiManager);
        } catch(Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE_FAILED;
        }
    }
   
    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
    public boolean setWifiApConfiguration(WifiConfiguration config) {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApConfiguration",WifiConfiguration.class);
            return (Boolean) method.invoke(mWifiManager, config);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public int setWifiApConfig(WifiConfiguration config) {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApConfig",WifiConfiguration.class);
            return (Integer) method.invoke(mWifiManager, config);
        } catch(Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE_FAILED;
        }
    }
    
    public WifiConfiguration getConfig(String SSID, String password,
    		SoftApPasswordType type) {
        WifiConfiguration config = new WifiConfiguration();

		/**
		 * TODO: SSID in WifiConfiguration for soft ap is being stored as a raw
		 * string without quotes. This is not the case on the client side. We
		 * need to make things consistent and clean it up
		 */
        
		config.SSID = SSID;

		switch (type) {
		case OPEN:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			return config;

		case WPA:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (password.length() != 0) {
				config.preSharedKey = password;
			}
			return config;

		case WPA2:
			//KeyMgmt.WPA2_PSK = 4
			int wpa2_PSK = 0;
			try {
				wpa2_PSK = KeyMgmt.class.getField("WPA2_PSK").getInt(null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			config.allowedKeyManagement.set(wpa2_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (password.length() != 0) {
				config.preSharedKey = password;
			}
			return config;
		}
		return null;

    }
}
