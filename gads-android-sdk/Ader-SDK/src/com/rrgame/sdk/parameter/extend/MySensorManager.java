package com.rrgame.sdk.parameter.extend;

import java.lang.reflect.Method;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;

/**
 * 传感器 管理类
 * @author shaozhe
 *
 */
public class MySensorManager{
	private static MySensorManager instance = null;
	SensorManager sm;
	//传感器
	Sensor gravitySensor;
	Sensor distanceSensor;
	Sensor gyroscopeSensor;
	//传感器监听
	gravitySensorEventListener gListener;
	distanceSensorEventListener dListener;
	gyroscopeSensorEventListener gyListener;
	
	//声音管理器
	AudioManager am;
	public MySensorManager(Context con){
		 sm = (SensorManager)con.getSystemService(Context.SENSOR_SERVICE);
		 gravitySensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		 distanceSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		 gyroscopeSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		 gListener = new gravitySensorEventListener();
		 dListener = new distanceSensorEventListener();
		 gyListener = new gyroscopeSensorEventListener();
		 am = (AudioManager) con.getSystemService(Context.AUDIO_SERVICE);
	}
	/**
	 * 单例模式
	 */
	public static MySensorManager getInstance(Context con) {  
	   	if(instance == null){
	   		synchronized(MySensorManager.class){
	   			if(instance == null){
	   				instance = new MySensorManager(con);
	   			}
	   		}
	   	}
	   	return instance;
	 }
	
	/**
	 * 注册重力传感器
	 */
	public void registerGravitySensor(){
		if(sm != null){
           sm.registerListener(gListener, gravitySensor ,SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	/**
	 * 注册陀螺仪传感器
	 */
	public void registerGyroscopeSensor(){
		if(sm != null){
           sm.registerListener(gyListener, gyroscopeSensor ,SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	/**
	 * 注册距离传感器
	 */
	public void registerDistanceSensor(){
		if(sm != null){
           sm.registerListener(dListener, distanceSensor ,SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	/**
	 * 注销重力传感器
	 */
	public void unregisterGravitySensor(){
		if(sm != null){
           sm.unregisterListener(gListener,gravitySensor);
		}
	}
	
	/**
	 * 注销距离传感器
	 */
	public void unregisterDistanceSensor(){
		if(sm != null){
           sm.unregisterListener(dListener,distanceSensor);
		}
	}
	
	/**
	 * 重力传感器监听类
	 * @author shaozhe
	 *
	 */
	 private final class gravitySensorEventListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			//获得x,y,z轴加速度
			float x = event.values[SensorManager.DATA_X];          
            float y = event.values[SensorManager.DATA_Y];          
            float z = event.values[SensorManager.DATA_Z];
//            for(int i=0;i<event.values.length;i++){
//            	 System.out.println(i+":"+event.values[i]);
//            }
            if(x > 1.5||y>1.5||z>1.5){
            	
            }
		}
		 
	 }
	 
	/**
	 * 近距离传感器监听类
	 * @author shaozhe
	 *
	 */
	 private final class distanceSensorEventListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			float distance = event.values[0];  //检测到手机和人体的距离
//			System.out.println("distance="+distance);    
		}
		 
	 }
	 /**
	  * 陀螺仪感应器
	  * @author shaozhe
	  *
	  */
	 private final class gyroscopeSensorEventListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
//			 for(int i=0;i<event.values.length;i++){
//            	 System.out.println(i+":="+event.values[i]);
//            }
		}
		 
	 }
	 
	 /**
	  * 获取当前通话音量
	  * @return
	  */
	 public int getVoiceLv(){
		 if(am != null){
		    int current = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
//		    System.out.println("current="+current);
		    return current;
		 }
		 return 0;
	 }
	 /**
	  * 获取当前是否插入耳机的状态
	  * @return
	  */
	 public boolean isWiredHeadsetOn(){
		//反射判断是否支持isWiredHeadsetOn方法调用
		 boolean isHeadsetOn = false;
		 try {
			Method isWiredHeadsetOn = am.getClass().getDeclaredMethod("isWiredHeadsetOn");
			isHeadsetOn = (Boolean)isWiredHeadsetOn.invoke(am);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		 System.out.println("isHeadsetOn="+isHeadsetOn);
		 return isHeadsetOn;
	 }
	
}
