package com.rrgame.sdk.reportinfo.manager;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.rrgame.sdk.systeminfo.AderBase64;

/**
 * LBS信息管理类
 * @author yulong
 *
 */
public class LBSManager implements LocationListener {
	 private static LBSManager mInstance = null;
	 private Context mContext;
	 private LocationManager locationManager;
	 /** 经度 */
	 private double _longitude = 400; 
	 /** 纬度 */
	 private double _latitude = 400; 
	 /** lbs搜索时间（秒）*/
	 private final static long SEARCH_TIME = 30;
	 /** lbs 间隔时间*/
	 private long lbs_time = 0;
	 /** 是否支持lbs*/
	 private boolean isApplyLBS = true;
	 /** gps搜索时间定时器*/
	 private Timer mTimer;
	 private TimerTask mTimerTask;
	 /** gps循环定时器*/
	 private Timer mLooperTimer;
	 private TimerTask mLooperTask;
	 /** gps信息*/
	 private String mInfo;
	 
	 /** 开启gps服务*/
	 private Handler mHandler = new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 startForLocation();
		 }
	 };
	 
	 /** 获取是否支持lbs*/
	 public boolean isApplyLBS() {
		return isApplyLBS;
	 }

	 /** 设置是否支持lbs*/
	 public void setApplyLBS(boolean isApplyLBS) {
		this.isApplyLBS = isApplyLBS;
	 }
	 
	 /** 开始检测lbs是否开启 */
	 public void startCheckLBS(boolean isApplyLBS){
		 if(!checkLbsPermission()){
			 return;
		 }
		mInstance.setApplyLBS(isApplyLBS);
		mInstance.isOpenGPS(isApplyLBS);
	 }

	 /** 获取gps信息*/
	 public String getInfo() {
		return mInfo;
	 }

	 /** 设置gps信息*/
	 public void setInfo(String mInfo) {
		this.mInfo = mInfo;
	}
	 
	 /** 私有声明，单例模式防止构造方法*/
	 private LBSManager(Context context){
		 this.mContext = context;
	 }
	 
	 /** 获取 LBSManager 实例 ,单例模式 */  
	 public static LBSManager getInstance(Context con) {  
		 if(mInstance == null){
		   	synchronized(LBSManager.class){
		   		if(mInstance == null){
		   			mInstance = new LBSManager(con);
		   		}
		   	}
		 }
	     return mInstance;  
	 }
	
	/**
	 * 判断是否已经开启gps
	 * @param isAlert 根据参数判断是否弹提示窗口.
	 * @return
	 */
	public boolean isOpenGPS(boolean isAlert) {
		try{
        LocationManager alm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return true;
        }
        if(isAlert){
	        //开启设置页面
	        new AlertDialog.Builder(mContext)
	        .setTitle("提示")
	        .setMessage("是否开启GPS功能")
	        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	          
	          public void onClick(DialogInterface dialog, int which) {
	        	  Toast.makeText(mContext, "请开启GPS！", Toast.LENGTH_SHORT).show();
	              Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	              ((Activity)mContext).startActivityForResult(intent,0); //此为设置完成后返回到获取界面
	          }
	        })
	        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int which) {
	               dialog.dismiss();
	          }
	        })
	        .show();
        }
        
		}catch(Exception e){
        	
        }
        
        return false;
       
    }
    
    /**
     * 移除locationlistener
     * @param listener
     */
    public void removeGPSListener(){
    	if(locationManager!=null&&this!=null){
    		locationManager.removeUpdates(this);
    	}
    }
   
	/**
	 * 定位location
	 */
	public void startForLocation(){
		try {
			if(isOpenGPS(false)){
			   String serviceName = Context.LOCATION_SERVICE;   
			   locationManager = (LocationManager) mContext.getSystemService(serviceName);
			   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			   startSearchTiming(SEARCH_TIME*1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送定位消息
	 * @param delayTime 延迟时间（秒）
	 */
	public void sendlocationMsg(final long delayTime){
		if(!checkLbsPermission()){
			 return;
		 }
		if(!isApplyLBS)
			return;
		this.lbs_time = delayTime;
		mLooperTimer = new Timer();
		mLooperTask = new TimerTask() {
				
			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		};
		mLooperTimer.schedule(mLooperTask, delayTime*1000);
	}
	
	/**
	 * 开始为gps搜索计时
	 * @param searchTime
	 */
	public void startSearchTiming(long searchTime){
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				 removeGPSListener();
				if(lbs_time>0)
					sendlocationMsg(lbs_time);
			}
		};
		mTimer.schedule(mTimerTask, searchTime);
	}
	
	/**
	 * 添加定位信息到系统信息
	 * 
	 */
	protected void addLBSToSysInfo(){
	    String string;
	    if (_longitude == 400 && _latitude == 400) {
	        string = "|";
	    }
	    else
	    {
	        string =  _longitude+"|"+_latitude; 
	    } 
	    mInfo = AderBase64.encode(string);
	}

	@Override
	public void onLocationChanged(Location location) {
		//满足条件更新定位信息
		if(location!=null){
			_longitude = location.getLongitude();
			_latitude = location.getLatitude();
			addLBSToSysInfo();
		}
		removeGPSListener();
		if(lbs_time>0)
			sendlocationMsg(lbs_time);
		mTimer.cancel();
		mTimer = null;
		mTimerTask = null;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 检测是否添加了GPS权限
	 */
	public boolean checkLbsPermission(){
		String packageNameString = mContext.getApplicationInfo().packageName;
		PackageManager packageManager = mContext.getPackageManager();
		if (packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION",
				packageNameString) == PackageManager.PERMISSION_DENIED) {
			return false;
		}
		
		return true;
	}

}
