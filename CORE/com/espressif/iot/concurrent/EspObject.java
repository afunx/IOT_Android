package com.espressif.iot.concurrent;

public class EspObject<T> {
	
	/**
	 * !NOTE
	 * the long is so large, so we don't think it will overflow
	 */
	private static long idAllocator = 0;
	private T t;
	private long id;
	
	public EspObject(T t){
		this.t = t;
		this.id = idAllocator++;
	}
	
	public boolean equals(EspObject<?> espObj){
		return espObj.id==this.id;
	}
	
	public T getT(){
		return t;
	}
	
}
