package com.espressif.iot.util;

//98:fe:34:77:ce:00
public class BSSIDUtil {
	
	// reverse method of restoreRealBSSID
//	public static String restoreLocalBSSID(String BSSID){
		
//	}
	
	/**
	 * for the reason, we use some bit to differ whether it is 
	 * softap or sta. so, we may get the polluted bssid.
	 * 
	 * softap bssid: 	1a:fe:34:77:c0:00
	 * sta bssid:		18:fe:34:77:c0:00
	 * 
	 * @param BSSID
	 * @return			the real BSSID(SoftAp's BSSID)
	 */
	public static String restoreRealBSSID(String BSSID){
		String pollutedBitStr = BSSID.substring(1,2);
		Integer pollutedBitInt = 0;
		if(pollutedBitStr.equals("a")){
			pollutedBitInt = 10;
		}
		else if(pollutedBitStr.equals("b")){
			pollutedBitInt = 11;
		}
		else if(pollutedBitStr.equals("c")){
			pollutedBitInt = 12;
		}
		else if(pollutedBitStr.equals("d")){
			pollutedBitInt = 13;
		}
		else if(pollutedBitStr.equals("e")){
			pollutedBitInt = 14;
		}
		else if(pollutedBitStr.equals("f")){
			pollutedBitInt = 15;
		}
		else{
			pollutedBitInt = Integer.parseInt(pollutedBitStr);
		}
		Integer cleanBitInt = pollutedBitInt | 0x02;
		String cleanBitStr = Integer.toHexString(cleanBitInt);
		return BSSID.substring(0, 1) + cleanBitStr + BSSID.substring(2);
	}
	
	
	// upper case if 'a' to 'z'
	private static String UpperCase(String str) {
		int len = str.length();
		String result = "";
		for (int i = 0; i < len; i++) {
			String subStr = str.substring(i, i + 1);
			char subChar = str.charAt(i);
			if (subChar >= 'a' && subChar <= 'z')
				result += subStr.toUpperCase();
			else
				result += subStr;
		}
		return result;
	}

	
	/**
	 * 
	 * generate Device Name By BSSID
	 * @param BSSID
	 * @return		"ESP_XXXXXX", "XXXXXX" is the last 6 of BSSID
	 */
	public static String genDeviceNameByBSSID(String BSSID) {
		// 1a:fe:34:77:c0:00 change to 77C000
		String tail = "";
		tail += UpperCase(BSSID.substring(9, 11));
		tail += BSSID.substring(12, 14).toUpperCase();
		tail += BSSID.substring(15, 17).toUpperCase();
		return "ESP_" + tail;
	}
}
