package com.rrgame.sdk.crash;
/**
 * 崩溃log日志的数据
 * @author shaozhe
 *
 */
public class RRGCrashLogData {
   private String type;//错误类型
   private String info;//错误主体信息
   private long time;//崩溃时间
   private String date;//崩溃日期
   private String systemVersion;//系统版本
   private String device;//移动设备
   private String appVersion;//app的版本号
   private String sdkVersion;//SDK的版本号
   private String appName;//APP的名称
   public void setType(String type){
	   this.type = type;
   }
   public void setInfo(String info){
	   this.info = info;
   }
   public void setTime(long time){
	   this.time = time;
   }
   public void setSystemVersion(String systemVersion){
	   this.systemVersion = systemVersion;
   }
   public void setAppVersion(String appVersion){
	   this.appVersion = appVersion;
   }
   public void setSdkVersion(String sdkVersion){
	   this.sdkVersion = sdkVersion;
   }
   public void setDevice(String device){
	   this.device = device;
   }
   public void setAppName(String appName){
	   this.appName = appName;
   }
   public String getType(){
	   return type;
   }
   public String getInfo(){
	   return info;
   }
   public void setDate(String date){
	   this.date = date;
   }
   public long getTime(){
	   return time;
   }
   public String getSystemVersion(){
	   return systemVersion;
   }
   public String getSdkVersion(){
	   return sdkVersion;
   }
   public String getAppVersion(){
	   return appVersion;
   }
   public String getDevice(){
	   return device;
   }
   public String getDate(){
	   return date;
   }
   public String getAppName(){
	   return appName;
   }
}
