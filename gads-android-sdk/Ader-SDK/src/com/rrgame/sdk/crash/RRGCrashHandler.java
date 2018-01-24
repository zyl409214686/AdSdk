package com.rrgame.sdk.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

/**
 * 
 * @author shaozhe
 *
 */
public class RRGCrashHandler implements UncaughtExceptionHandler{
   private static RRGCrashHandler instance = null;
   
   Context mContext;
   /**
    * 之前的handler
    */
   private UncaughtExceptionHandler mBeforeHandler;  
   
   public RRGCrashHandler(Context con){
	   mContext = con;
   }
   /** 获取 CrashHandler 实例 ,单例模式 */  
   public static RRGCrashHandler getInstance(Context con) {  
   	if(instance == null){
   		synchronized(RRGCrashHandler.class){
   			if(instance == null){
   				instance = new RRGCrashHandler(con);
   			}
   		}
   	}
       return instance;  
   }
   
   /**
    * 注册ExceptionHandler
    */
   public void registerCrashHandler() {  
     
       // 获取之前的 UncaughtException 处理器  
	   mBeforeHandler = Thread.getDefaultUncaughtExceptionHandler();  
     
       // 设置 RRGCrashHandler 为程序的默认处理器  
       Thread.setDefaultUncaughtExceptionHandler(this);  
   }  
   /**
    * 注销 恢复为以前的ExceptionHandler
    */
   public void unregisterCrashHandler(){
	   if(mBeforeHandler != null){
	      Thread.setDefaultUncaughtExceptionHandler(mBeforeHandler);  
	   }
   }
   
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		RRGCrashLogManager.getInstance(mContext).saveCrashLog(ex);
		//传给之前的handler
		if(mBeforeHandler != null){
			mBeforeHandler.uncaughtException(thread, ex);
    	}
	}
}
