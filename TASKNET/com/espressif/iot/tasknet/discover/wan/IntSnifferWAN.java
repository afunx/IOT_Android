package com.espressif.iot.tasknet.discover.wan;

import com.espressif.iot.tasknet.discover.IntSniffer;

/**
 * 
 * in WAN, we would use wifi firstly.
 * if Internet is not accessible to wifi,
 * we would try MONET (mobile net, e.g. 2G,3G,4G)
 * 
 * IntWifiSnifferWAN: 
 * (connect to Internet via wifi)
 * Is wifi accessed to the Internet?
 * 
 * IntMonetSnifferWAN:
 * (connect to Internet via mobile information flow)
 * Is monet accessed to the Internet?
 * 
 * @author afunx
 *
 */
public interface IntSnifferWAN extends IntSniffer{
}
