package com.espressif.iot.constants;

/**
 * describe the state of IntSniffer
 * 
 * when net is LAN, it is using wifi
 * when using WAN, it is in STA mode
 * 
 * sniffer is the tool of discoverer
 * 
 * 				  sniffer
 *               //      \\
 *              //        \\
 *             //          \\
 *         LAN(wifi)      WAN(STA)
 *          //   \\       //  \\ 
 *         //     \\     //    \\
 *        AP      STA   wifi  monet
 * @author afunx
 *
 */
public enum SNIFFER_ENUM {
	     	LAN,				   			WAN,
	LAN_WIFI_AP,LAN_WIFI_STA,		WAN_STA_WIFI,WAN_STA_MONET
}
