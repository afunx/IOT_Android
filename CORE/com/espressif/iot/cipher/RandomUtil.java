package com.espressif.iot.cipher;

import java.util.Random;

public class RandomUtil {
	
	/**
	 * map int to String,
	 * 0-9:	"0"-"9"
	 * 10-25: "a"-"z"
	 * @param i
	 * @return
	 */
	private static String map(int i){
		if(i<10)
			return Integer.toString(i);
		else{
			char c = (char)('a' + i - 10);
			return Character.toString(c);
		}
	}
	
	/**
	 * random 40 places token
	 * @return
	 */
	public static String random40(){
		Random random = new Random();
		String token = "";
		for(int i=0;i<40;i++){
			int x = random.nextInt(36);
			token += map(x);
		}
		return token;
	}
}
