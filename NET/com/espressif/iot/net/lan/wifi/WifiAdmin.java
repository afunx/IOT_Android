package com.espressif.iot.net.lan.wifi;

import java.util.ArrayList;
import java.util.List;

import com.espressif.iot.constants.CONSTANTS;
import com.espressif.iot.constants.WIFI_ENUM;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

/**
 * 
 * it should be considered that scan may miss sometime for SRP, it should be
 * processed in other class that is WifiScanResultManager
 * 
 * @author afunx
 * 
 */
public class WifiAdmin {
	
	private WifiManager mWifiManager;
	private ConnectivityManager mConnectivityManager;
	private WifiInfo mWifiInfo;
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;

	private WifiApManager wifiApManager;// = new WifiApManager();
	
	private static final String TAG = "WifiAdmin";

	private Context mContext;
	public WifiAdmin(Context context) {
		// get WifiManager
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// get WifiInfo
		mWifiInfo = mWifiManager.getConnectionInfo();
		
		mContext = context;
		
		wifiApManager = new WifiApManager(context);
	}

	public void openHot(){
		WifiConfiguration config = wifiApManager.getConfig("WPA_HotTest",
		"1234567890", SoftApPasswordType.WPA);
		wifiApManager.closeSoftAp();
//		wifiApManager.setWifiApEnabled(config, false);
		wifiApManager.setWifiApEnabled(config, true);
		
	}
	
	// open wifi
	public boolean openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
			for (int i = 0; i < CONSTANTS.WIFI_RETRY_TIMES; i++) {
				if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					return true;
				}
				try {
					Thread.sleep(CONSTANTS.WIFI_OPEN_SLEEP_MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			return true;
		}
		return false;
	}

	// close wifi
	public boolean closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
			for (int i = 0; i < CONSTANTS.WIFI_RETRY_TIMES; i++) {
				if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
					return true;
				}
				try {
					Thread.sleep(CONSTANTS.WIFI_CLOSE_SLEEP_MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			return true;
		}
		return false;
	}

	// get the wifi's current state
	public int getWifiState() {
		return mWifiManager.getWifiState();
	}
	
	public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	// lock wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// release wifiLock
	public void releaseWifiLock() {
		// whether is locked
		if (mWifiLock.isHeld()) {
			mWifiLock.release();
		}
	}

	// create a wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// get the configurations
	public List<WifiConfiguration> getConfigurationList() {
		return mWifiConfigurations;
	}

	// connect to the specified wifi network
	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// connect the specified wifi network
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	private WifiConfiguration createWifiInfo(String SSID, String password,
			WIFI_ENUM.WifiCipherType type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
//		config.BSSID = "\"" + BSSID + "\"";
		switch (type) {
		// it is tested
		case WIFICIPHER_NOPASS:
			config.wepKeys[0] = "\"" + "\"";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
			break;
		// not test yet
		case WIFICIPHER_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (password.length() != 0) {
				int length = password.length();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;
		// it is tested
		case WIFICIPHER_WPA:
			config.preSharedKey = "\"" + password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			// for WPA
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			// for WPA2
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			break;
		default:
			return null;
		}
		return config;
	}

	public void disconnectSyn() {
		mWifiManager.disconnect();
		// make sure the disconnect is finished by the android system
		while(isConnect()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * whether the SSID is in the wifi setting configuration
	 * @param SSID		the SSID
	 * @return			-1 if SSID doesn't exist or the index of it
	 */
	private int indexOfSSID(String SSID){
		boolean isExist = false;
		String ssid = "\"" + SSID + "\"";
		int index;
		// add by afunx
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
		
		for (index = 0;index<mWifiConfigurations.size();index++){
			WifiConfiguration wifiConfiguration = mWifiConfigurations.get(index);
			if (wifiConfiguration.SSID != null
					&& wifiConfiguration.SSID.equals(ssid)) {
				isExist = true;
				break;
			}
		}
		if(isExist)
			// add by afunx
			return mWifiConfigurations.get(index).networkId;
		else
			return -1;
	}
	
	
	
	public boolean connect( String SSID, String password, WIFI_ENUM.WifiCipherType type) {
		// readWepConfig();
		Log.d(TAG, "connect--SSID:" + SSID + ",Password:" + password + ",Type:"
				+ type);
		// mWifiManager.disconnect();
		// wifi is not open, return false
		if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
			return false;
		int netId = indexOfSSID(SSID);
		
		// it means that the old configuration's password is err
		// before add the new wifi configuration, we should remove it
		if(netId!=-1){
			boolean	bRemove = mWifiManager.removeNetwork(netId);
			if(!bRemove)
				Log.e(TAG, "old netId " + netId + " is removed " + "failed");
			else
				Log.d(TAG, "old netId " + netId + " is removed " + "succeed");
		}
		
		WifiConfiguration wifiConfig = createWifiInfo(SSID, password, type);
		if (wifiConfig == null)
			return false;
//		netId = mWifiManager.addNetwork(wifiConfig);
		
		netId = mWifiManager.addNetwork(wifiConfig);
		Log.d(TAG, "netId=" + netId + " is added into the network.");
		// return mWifiManager.enableNetwork(netID, true);
		// mWifiManager.disconnect();
		// mWifiManager.enableNetwork(netID, true);
		// mWifiManager.reassociate();
		// readWepConfig();
		return mWifiManager.enableNetwork(netId, true);
	}

	public boolean connect(String SSID, boolean isNoPassword) {
		Log.d(TAG, "connect--SSID:" + SSID);
		int netId = indexOfSSID(SSID);
		// SSID doesn't exist
		if (netId==-1) {
			if (isNoPassword) {
				WifiConfiguration wifiConfig = createWifiInfo(SSID, "",
						WIFI_ENUM.WifiCipherType.WIFICIPHER_NOPASS);
				if (wifiConfig == null) {
					Log.e(TAG, "the wifiConfig created failed.");
					return false;
				}
				netId = mWifiManager.addNetwork(wifiConfig);
				Log.d(TAG, "the new no password wifi configuration is added,netId = " + netId);
				return mWifiManager.enableNetwork(netId, true);
			}
			else {
			Log.d(TAG, "the AP isn't in mWificonfigurations");
				return false;
			}
		}
		else{
			Log.d(TAG, "netId="+netId);
			boolean result =  mWifiManager.enableNetwork(netId, true);
			return result;
		}
	}

	public boolean isConnect() {
		mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean result = mWifi.isConnected();
		return result;
	}

	public WIFI_ENUM.WifiStatus getStatus(String SSID) {
		String ssid = "\"" + SSID + "\"";
		// Add by afunx
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
		// if SSID doesn't exist, treat it as DISABLE
		WIFI_ENUM.WifiStatus status = WIFI_ENUM.WifiStatus.WIFISTATUS_DISABLE;
		for (WifiConfiguration wifiConfiguration : mWifiConfigurations) {
			if (wifiConfiguration.SSID != null
					&& wifiConfiguration.SSID.equals(ssid)) {
				Log.d(TAG,
						"isEnabled() status = "
								+ WifiConfiguration.Status.strings[wifiConfiguration.status]);
				switch (wifiConfiguration.status) {
				case (WifiConfiguration.Status.CURRENT):
					status = WIFI_ENUM.WifiStatus.WIFISTATUS_CURRENT;
					break;
				case (WifiConfiguration.Status.DISABLED):
					status = WIFI_ENUM.WifiStatus.WIFISTATUS_DISABLE;
					break;
				case (WifiConfiguration.Status.ENABLED):
					status = WIFI_ENUM.WifiStatus.WIFISTATUS_ENABLE;
					break;
				}
			}
		}
		return status;
	}

	public boolean isEnabled(String SSID) {
		String ssid = "\"" + SSID + "\"";

		for (WifiConfiguration wifiConfiguration : mWifiConfigurations) {
			if (wifiConfiguration.SSID != null
					&& wifiConfiguration.SSID.equals(ssid)) {
				Log.d(TAG,
						"isEnabled() status = "
								+ WifiConfiguration.Status.strings[wifiConfiguration.status]);
				if (wifiConfiguration.status == WifiConfiguration.Status.DISABLED)
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	/**
     * Return dynamic information about the current Wi-Fi connection, if any is active.
     * @return the Wi-Fi information, contained in {@link WifiInfo}.
     */
	public WifiInfo getConnectionInfo(){
		return mWifiManager.getConnectionInfo();
	}
	
	/**
	 * wifi scan
	 * @return		the List<ScanResult>, if scan fail, it will return null
	 */
	public synchronized List<ScanResult> scan() {
		List<ScanResult>mWifiList = null;
		// !!!NOTE: this is not good code here
		List<ScanResult>wifiListExceptEmpty = new ArrayList<ScanResult>();
		mWifiManager.startScan();
		for (int i = 0; i < CONSTANTS.WIFI_RETRY_TIMES; i++) {
			mWifiList = mWifiManager.getScanResults();
			// Log.i(TAG, "startScan(): scanlist = " + mWifiList);
			if (mWifiList != null && !mWifiList.isEmpty()){
				for(ScanResult scanResult: mWifiList){
					if(!scanResult.SSID.equals("")){
						wifiListExceptEmpty.add(scanResult);
					}
				}
				break;
			}
			else
				try {
					Thread.sleep(CONSTANTS.WIFI_SCAN_SLEEP_MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (wifiListExceptEmpty != null && !wifiListExceptEmpty.isEmpty()) {
			// get the configuration
//			mWifiConfigurations = mWifiManager.getConfiguredNetworks();
			return wifiListExceptEmpty;
		} else {
			return null;
		}
	}

	public String getSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? null : mWifiInfo.getBSSID();
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// get the connected ID
	public int getNetWorkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// get the wifi info
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	void readWepConfig() {
		WifiManager wifi = mWifiManager;
		List<WifiConfiguration> item = wifi.getConfiguredNetworks();
		int i = item.size();
		Log.d("WifiPreference", "NO OF CONFIG " + i);
		WifiConfiguration config = item.get(7);
		Log.d("WifiPreference", "SSID:" + config.SSID);
		Log.d("WifiPreference", "PASSWORD:" + config.preSharedKey);
		Log.d("WifiPreference", "--ALLOWED ALGORITHMS--");
		Log.d("WifiPreference",
				"LEAP:" + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
		Log.d("WifiPreference",
				"OPEN:" + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
		Log.d("WifiPreference",
				"SHARED:"
						+ config.allowedAuthAlgorithms
								.get(AuthAlgorithm.SHARED));
		Log.d("WifiPreference", "--GROUP CIPHERS--");
		Log.d("WifiPreference",
				"CCMP:" + config.allowedGroupCiphers.get(GroupCipher.CCMP));
		Log.d("WifiPreference",
				"TKIP:" + config.allowedGroupCiphers.get(GroupCipher.TKIP));
		Log.d("<WifiPreference",
				"WEP104:" + config.allowedGroupCiphers.get(GroupCipher.WEP104));
		Log.d("WifiPreference",
				"WEP40:" + config.allowedGroupCiphers.get(GroupCipher.WEP40));
		Log.d("WifiPreference", "--KEYMGMT--");
		Log.d("WifiPreference",
				"IEEE8021X"
						+ config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
		Log.d("WifiPreference",
				"NONE" + config.allowedKeyManagement.get(KeyMgmt.NONE));
		Log.d("WifiPreference",
				"WPA_EAP" + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
		Log.d("WifiPreference",
				"WPA_PSK" + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
		Log.d("WifiPreference", "--PairWiseCipher--");
		Log.d("WifiPreference",
				"CCMP" + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
		Log.d("WifiPreference",
				"NONE" + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
		Log.d("WifiPreference",
				"TKIP" + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));
		Log.d("WifiPreference", "--Protocols--");
		Log.d("WifiPreference",
				"RSN" + config.allowedProtocols.get(Protocol.RSN));
		Log.d("WifiPreference",
				"WPA" + config.allowedProtocols.get(Protocol.WPA));
		Log.d("WifiPreference", "--WEP Key Strings--");
		String[] wepKeys = config.wepKeys;
		Log.d("WifiPreference", "WEP KEY 0" + wepKeys[0]);
		Log.d("WifiPreference", "WEP KEY 1" + wepKeys[1]);
		Log.d("WifiPreference", "WEP KEY 2" + wepKeys[2]);
		Log.d("WifiPreference", "WEP KEY 3" + wepKeys[3]);
	}

}