package com.espressif.iot.util;

public class MathUtil {
	
	/**
	 * 
	 * @param org	the original digit
	 * @param min	the min
	 * @param max	the max
	 * @return		org in the constrian of [min,max]
	 */
	public static int constrain(int org, int min, int max){
		return Math.min( max , Math.max(min, org));
	}
}
