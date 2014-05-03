package com.espressif.iot.tasknet.discover.lan;

import java.util.List;

/**
 * @author afunx
 * 
 * @param <T>					type of the discovery
 */
public interface IntSTASnifferLAN<T> extends IntSnifferLAN{
	boolean isExistSyn(T t,int timeoutSeconds);
	List<T> sniffSyn();
}
