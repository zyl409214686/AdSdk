package com.rrgame.sdk.reportinfo.utils;
/**
 * des相关
 */
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtils {
//	private byte[] key;
	private static final int keyStrLen = 16;
	/**
     * 加密算法DES
     */
	static final String KEY_ALGORITHM = "DES/CBC/PKCS5Padding";
	
	private static final byte[] DEFAULT_SALT = {(byte) 0xB9, (byte) 0xAB, (byte) 0xD8, (byte) 0x42, (byte) 0x66, (byte) 0x45, (byte) 0xF3, (byte) 0x13};
	
	/**
	 *  随机生成由数字和字母组成的长度16字节的字符串作为key
	 */
	public static byte[] creatSecretKey(){
		String keyStr = "";
		Random random = new Random();     
	    for(int i = 0; i < keyStrLen; i++)     
		    {     
	        String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字     
	                 
	        if("char".equalsIgnoreCase(charOrNum)) // 字符串     
	        {     
	            int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母     
	            keyStr += (char) (choice + random.nextInt(26));     
	        }     
	        else if("num".equalsIgnoreCase(charOrNum)) // 数字     
	        {     
	        	keyStr += String.valueOf(random.nextInt(10));     
	        }    
			
	    }
	    return keyStr.getBytes();
	}
	
	
	
	/**
	 * 加密数据
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,byte[] key) throws Exception {
		if(data == null || key == null){
			return null;
		}
		DESKeySpec desKey = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		IvParameterSpec zeroIv = new IvParameterSpec(DEFAULT_SALT);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, securekey,zeroIv);
		return cipher.doFinal(data);
	}
	
	/**
	 *  解密数据
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,byte[] key) throws Exception {
		if(data == null || key == null){
			return null;
		}
		DESKeySpec desKey = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		IvParameterSpec zeroIv = new IvParameterSpec(DEFAULT_SALT);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, securekey,zeroIv);
		return cipher.doFinal(data);
	}
}
