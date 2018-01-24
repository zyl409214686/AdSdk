package com.rrgame.sdk.reportinfo.utils;
/**
 * RSA相关
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

import android.content.Context;

public class RSAUtils {
	/**
     * 加密算法RSA
     */
	static final String KEY_ALGORITHM = "RSA/None/PKCS1Padding";
	/**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * RSA密钥长度
     */
    private static final int RSA_KEY_SIZE = 1024;
    
    
    
	/**
	 * 公钥的存储路径
	 */
//	static String keyPath = "/sdcard/ader/banner/";
	static String publicKeyName = "public_key.der";
	static String privateKeyName = "private_key.der";
	
	public RSAUtils(Context mContext){
		
	}
	
	
	/**
	 * 将数据转化为key对象
	 */
//	public static PublicKey getPublicKey(byte[] pubKeyBytes){
//		PublicKey publicKey = null;
//		KeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);
//		KeyFactory keyFactory;
//		try {
//			keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//			publicKey = keyFactory.generatePublic(keySpec);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return publicKey;
//	}
	/**
	 * 将数据转化为key对象(der编码格式的公钥)
	 */
	public static PublicKey getPublicKey(byte[] pubKeyBytes){
		PublicKey publicKey = null;
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
			Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(pubKeyBytes));
			publicKey = certificate.getPublicKey();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return publicKey;
	}
	
	public static PrivateKey getPrivateKey(byte[] privateKeyBytes) {
		PrivateKey privateKey = null;
		try {
			KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			
			
		}
		return privateKey;
	}
	
	/**
	 * 公钥加密数据
	 */
	public static byte[] pubEncrypt(byte[] data,byte[] pubKeyBytes){
		if(data == null || pubKeyBytes == null){
			return null;
		}
		PublicKey publicKey = getPublicKey(pubKeyBytes);
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
	        try {
	            // 对数据加密
	            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
	            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	            int inputLen = data.length;
	            int offSet = 0;
	            byte[] cache;
	            int i = 0;
	            // 对数据分段加密
	            while (inputLen - offSet > 0) {
	                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
	                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
	                } else {
	                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
	                }
	                out.write(cache, 0, cache.length);
	                i++;
	                offSet = i * MAX_ENCRYPT_BLOCK;
	            }
	            byte[] encryptedData = out.toByteArray();
	            return encryptedData;
	        } catch (Exception e) {
	        } finally {
	            try {
	                out.close();
	            } catch (IOException e) {
	            }
	        }
	        return null;
	}
	/**
	 * 私钥解密数据
	 */
	public static byte[] priDecrypt(byte[] data,byte[] privateKeyBytes){
		if(data == null || privateKeyBytes == null){
			return null;
		}
		PrivateKey privateKey = getPrivateKey(privateKeyBytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            return decryptedData;
        } catch (Exception e) {
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
	}
	
	/**
	 * 私钥加密数据
	 */
	public static byte[] priEncrypt(byte[] data,byte[] privateKeyBytes) {
		if(data == null || privateKeyBytes == null){
			return null;
		}
		PrivateKey privateKey = getPrivateKey(privateKeyBytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            return encryptedData;
        } catch (Exception e) {
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
	}
	/**
	 * 公钥解密数据
	 */
	public static byte[] pubDecrypt(byte[] data,byte[] pubKeyBytes){
		if(data == null || pubKeyBytes == null){
			return null;
		}
		PublicKey publicKey = getPublicKey(pubKeyBytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            return decryptedData;
        } catch (Exception e) {
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
	}
   
//	/**
//	 * 读取本地公钥
//	 * @return
//	 */
//	public static byte[] readPublilcKey(){
//		byte[] pKey = null;
//		pKey = readFile(keyPath+publicKeyName);
//		return pKey;
//	}
//	/**
//	 * 读取本地私钥
//	 * @return
//	 */
//	public static byte[] readPrivateKey(){
//		byte[] pKey = null;
//		pKey = readFile(keyPath+privateKeyName);
//		return pKey;
//	}
	
	private static byte[] readFile(String path){
		byte[] pKey = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			InputStream in = new FileInputStream(path);
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				byteOut.write(buffer, 0, len);
			}
			pKey = byteOut.toByteArray();
			in.close();
			byteOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pKey;
	}
//	/**
//	 * 储存公钥
//	 * @param message
//	 */
//	public static void savePublicKey(byte[] message){
//		WriteFileData(publicKeyName,message);
//	}
//	/**
//	 * 储存私钥
//	 * @param message
//	 */
//	public static void savePrivateKey(byte[] message){
//		WriteFileData(privateKeyName,message);
//	}
//	public static void WriteFileData(String fileName,byte[] message)  
//    {  
//        try  
//        {  
//           File dir = new File(keyPath);  
//           if (!dir.exists()) {  
//               dir.mkdirs();  
//           }  
//           FileOutputStream fos = new FileOutputStream(keyPath+fileName);  
//           fos.write(message);  
//           fos.close(); 
//           
//        }  
//        catch(Exception e){  
//         e.printStackTrace(); 
//        }  
//    } 
	
	//生成公钥私钥 并储存
//	public static void creatKeySave(){
//		try {
//			KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
//			kpg.initialize(RSA_KEY_SIZE);
//			KeyPair keyPair = kpg.generateKeyPair();
//			PublicKey publicKey = keyPair.getPublic();
//			PrivateKey privateKey = keyPair.getPrivate();
//			byte[] pubKeyBytes = publicKey.getEncoded();
//			byte[] priKeyBytes = privateKey.getEncoded();
//			savePublicKey(pubKeyBytes);
//			savePrivateKey(priKeyBytes);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
