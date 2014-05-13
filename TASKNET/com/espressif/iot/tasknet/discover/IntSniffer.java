package com.espressif.iot.tasknet.discover;


/**
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
public interface IntSniffer{
//	enum NET_STATE{
//		LAN,LAN_AP,LAN_STA,
//		WAN,WAN_WIFI,WAN_MONET
//	}
}
