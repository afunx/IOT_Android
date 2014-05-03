package com.espressif.iot.tasknet.discover.lan;

import com.espressif.iot.tasknet.discover.IntSniffer;

/**
 * 
 * IntAPSnifferLAN:
 *  we could use the wifi's scan getting all APs
 *  
 *  there's no necessary to get whether the AP is exist for the moment,
 *  so there's no method to check whether the AP is exist
 * 
 * IntSTASnifferLAN: 
 * 	when receiving "Are You Espressif IOT Smart Plug?" in port 1025,
 * 	iot device will reply like "I'm Plug.98:fe:34:77:ce:00 192.168.1.100"
 *   
 * 	therefore, we could use 255.255.255.255 getting all STAs,
 * 	and use its ip address getting whether it is exist
 * 
 * @author afunx
 *
 */
public interface IntSnifferLAN extends IntSniffer{
}
