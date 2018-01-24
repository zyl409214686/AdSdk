package com.rrgame.sdk.reportinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {
	
	/**
	 * 写入long数据
	 * @param con
	 * @param fileName
	 * @param dataName
	 * @param data
	 */
	public static void writeLong(Context con,String fileName,String dataName,long data){
 		SharedPreferences sp = con.getSharedPreferences(fileName,Context.MODE_PRIVATE);
 		Editor editor=sp.edit();
 		editor.putLong(dataName, data);
     	editor.commit();
 	}
	/**
	 * 写入string 数据
	 * @param con
	 * @param fileName
	 * @param dataName
	 * @param data
	 */
	public static void writeString(Context con,String fileName,String dataName,String data){
 		SharedPreferences sp = con.getSharedPreferences(fileName,Context.MODE_PRIVATE);
 		Editor editor=sp.edit();
 		editor.putString(dataName, data);
     	editor.commit();
 	}
	/**
	 * 写入int数据
	 * @param con
	 * @param fileName
	 * @param dataName
	 * @param data
	 */
	public static void writeInt(Context con,String fileName,String dataName,int data){
 		SharedPreferences sp = con.getSharedPreferences(fileName,Context.MODE_PRIVATE);
 		Editor editor=sp.edit();
 		editor.putInt(dataName, data);
     	editor.commit();
 	}
	/**
	 * 得到int数据
	 * @param con
	 * @param fileName
	 * @param dataName
	 * @param defaultData
	 * @return
	 */
	public static int getInt(Context con,String fileName,String dataName,int defaultData){
 		int data = defaultData;
 		SharedPreferences sp = con.getSharedPreferences(fileName,Context.MODE_PRIVATE);
 		data = sp.getInt(dataName, defaultData);
 		return data;
 	}
	/**
	 * 得到String数据
	 * @param con
	 * @param fileName
	 * @param dataName
	 * @param defaultData
	 * @return
	 */
	public static String getString(Context con,String fileName,String dataName,String defaultData){
		String data = defaultData;
 		SharedPreferences sp = con.getSharedPreferences(fileName,Context.MODE_PRIVATE);
 		data = sp.getString(dataName, defaultData);
 		return data;
 	}
	/**
	 * 得到long数据
	 * @param con
	 * @param fileName
	 * @param dataName
	 * @param defaultData
	 * @return
	 */
	public static long getLong(Context con,String fileName,String dataName,long defaultData){
		long data = defaultData;
 		SharedPreferences sp = con.getSharedPreferences(fileName,Context.MODE_PRIVATE);
 		data = sp.getLong(dataName, defaultData);
 		return data;
 	}
}
