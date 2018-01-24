package com.dynamic.sdk.readcm;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.rrgame.sdk.reportinfo.utils.DESUtils;
import com.rrgame.sdk.systeminfo.AderBase64;

import dalvik.system.DexClassLoader;
import dalvik.system.VMStack;

public class LoadCM {
	public static Class<?> loadClass;
	public static Method alMethod;
	public static Method aiMethod;
	public static Method ralMethod;
	
	static final String jarDataStr = "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";
	
	static final String jarKey = "NXNtNTgwMWM2RTU5RXEzbA==";
	
	static String jarPath ;
//	= Environment.getExternalStorageDirectory().getPath()+"/jarTest/";
	
	static final String jarName = "temp.jar";
	
	static final String className = "com.dynamic.sdk.readcm.LoadClass";
	
	static final String alMethodName = "getAL";
	
	static final String aiMethodName = "getAI";
	
	static final String ralMethodName = "getRAL";
	
	public static void LoadLogic(Context con){
		File file=con.getFilesDir();
		jarPath = file.getAbsolutePath()+"/jarTest/";
		//解密数据
		byte[] jard = decrpytJar();
//		
//		//生成jar包文件
		writeSdk(jard);
		
		//加载类和方法
		initClass(con);
		
		//删除jar包
		deletJar();
		
	}
	
	/**
 	 * 解密jar包数据
 	 * @return
 	 */
 	private static byte[] decrpytJar(){
 		
		byte[] decrpytData = null;
		try {
			byte[] encryptJar = AderBase64.decode(jarDataStr);
			byte[] key = AderBase64.decode(jarKey);
			decrpytData = DESUtils.decrypt(encryptJar, key);
//			System.out.println("成功读取加密包数据");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decrpytData;
 	}
 	
 	 /**
	  * 生成jar包
	  * @param data
	  */
 	private static void writeSdk(byte[] data){
   	 FileOutputStream fos = null;
   	 File dir = new File(jarPath); 
		 try  
	        {  
	           if (!dir.exists()) {  
	               dir.mkdirs();  
	           }  
	           dir = new File(jarPath,jarName);
	           fos = new FileOutputStream(dir);  
	           fos.write(data);  
//	           System.out.println("成功生成jar包");
	        }  
	        catch(Exception e){  
	         e.printStackTrace(); 
	        }
           finally{
	            try {
	            	fos.close();
	            } catch (Exception e2) {
	                // TODO: handle exception
	                e2.printStackTrace();
	            }
	       }
	      
    }
    
    /**
 	 * 从jar包中加载类和方法 
 	 */
 	private static void initClass(Context con){
		try {
			File dexOutputDir = con.getDir("jar", 0);
	    	File localFile1 = new File(jarPath+jarName);
	    	if(localFile1 != null){
	           DexClassLoader localDexClassLoader = 
	         		   new DexClassLoader(localFile1.getAbsolutePath(), dexOutputDir.getAbsolutePath(),
	         				   null, VMStack.getCallingClassLoader());
	           loadClass = localDexClassLoader.loadClass(className);
	           alMethod = loadClass.getDeclaredMethod(alMethodName,new Class[]{Context.class});
	           aiMethod = loadClass.getDeclaredMethod(aiMethodName,new Class[]{Context.class,int.class});
	           ralMethod = loadClass.getDeclaredMethod(ralMethodName,new Class[]{Context.class});
//	           System.out.println("读取类="+loadClass);
//	           System.out.println("读取方法="+alMethod+":="+aiMethod);
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 	

 	
 	/**
 	 * 删除jar包
 	 */
 	private static void deletJar(){
     	File file = new File(jarPath+jarName); 
         if(file.exists()){  
             file.delete();  
//             System.out.println("成功删除jar包");
         } 
    }
 	
 	/**
 	 * 获得列表
 	 * @param con
 	 * @return
 	 */
// 	public static List<PackageInfo> getAL(Context con){
// 		List<PackageInfo> pList = new ArrayList<PackageInfo>();
// 		if(alMethod != null&&loadClass!=null){
// 			try {
//				pList = (List<PackageInfo>)alMethod.invoke(loadClass, new Object[]{con});
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
// 		}
// 		return pList;
// 	}
// 	/**
// 	 * 获得启动列表
// 	 * @param con
// 	 * @return
// 	 */
// 	public static List<RunningAppProcessInfo> getRAL(Context con){
// 		List<RunningAppProcessInfo> rList = new ArrayList<RunningAppProcessInfo>();
// 		if(ralMethod != null&&loadClass!=null){
// 			try {
//				rList = (List<RunningAppProcessInfo>)ralMethod.invoke(loadClass, new Object[]{con});
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
// 		}
// 		return rList;
// 	}
// 	/**
// 	 * 获得信息
// 	 * @param con
// 	 * @param flag
// 	 * @return
// 	 */
// 	public static PackageInfo getAI(Context con,int flag){
// 	    PackageInfo pi = null;
// 	    if(aiMethod != null&&loadClass!=null){
// 			try {
//				pi = (PackageInfo)aiMethod.invoke(loadClass, new Object[]{con,flag});
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
// 		}
// 	     return pi;
// 	}
}
