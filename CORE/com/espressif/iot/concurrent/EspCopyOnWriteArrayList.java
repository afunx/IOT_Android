package com.espressif.iot.concurrent;

import java.util.concurrent.CopyOnWriteArrayList;

public class EspCopyOnWriteArrayList<T>{
	
	private CopyOnWriteArrayList<EspObject<T>> list = new CopyOnWriteArrayList<EspObject<T>>();
	
	public synchronized int size(){
		return list.size();
	}
	
	public synchronized void clear(){
		list.clear();
	}
	
	public synchronized EspObject<T> get(int index){
		return list.get(index);
	}
	
	public synchronized void add(T t){
		EspObject<T> obj = new EspObject<T>(t);
		list.add(obj);
	}
	
	public synchronized boolean remove(EspObject<T> removeObj){
		for(int index=0;index<list.size();index++){
			EspObject<T> obj = list.get(index);
			if(obj.equals(removeObj)){
				list.remove(index);
				return true;
			}
		}
		return false;
	}
	
}
