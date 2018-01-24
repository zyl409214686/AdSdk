package com.rrgame.sdk.reportinfo.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HMACUtils {
	
	/**
	 * hmac摘要算法
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptHMAC(byte[] data, byte[] key) throws Exception {  
		if(data == null || key == null){
			return null;
		}
	    SecretKey secretKey = new SecretKeySpec(key, "HmacMD5");  
	    Mac mac = Mac.getInstance(secretKey.getAlgorithm());  
	    mac.init(secretKey);  
	  
	    return mac.doFinal(data);  
	  
	}  
}
