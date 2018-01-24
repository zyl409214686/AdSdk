package com.dynamic.sdk.readcm;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
/**
 * 需要加密的类和方法
 * @author shaozhe
 *
 */
public class LoadClass {
   public String getJarStr(){
	   String str = "成功调用jar包方法";
	   return str;
   }
   
   /**
    * 读取安装app列表信息
    * @return
    */
   public static List<PackageInfo> getAL(Context con){
	   PackageManager pm = con.getPackageManager();
	   List<PackageInfo> pList = pm.getInstalledPackages(0); 
	   return pList;
   }
   /**
    * 根据包名检索包信息
    * @param con
    * @param flag
    * @return
    */
   public static PackageInfo getAI(Context con,int flag){
	   PackageManager pm = con.getPackageManager();
	   PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(con.getPackageName(), flag);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return pi;
   }
   /**
    * 获得内存中运行APP信息
    * @param con
    * @return
    */
   public static List<RunningAppProcessInfo> getRAL(Context con){
	   ActivityManager mActivityManager = (ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE); 
	   List<RunningAppProcessInfo> rList = mActivityManager  
               .getRunningAppProcesses();
	   return rList;
   }
}
