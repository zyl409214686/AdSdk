package com.rrgame.sdk.reportinfo.gatherinfo;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.rrgame.sdk.crash.RRGCrashLogData;
import com.rrgame.sdk.crash.RRGCrashLogManager;
/**
 * 崩溃日志信息
 * @author shaozhe
 *
 */
public class CrashLogInfo {
	public static String bulidCrashInfo(Context con){
		String bulidStr = "";
		List<RRGCrashLogData> datalist = RRGCrashLogManager.getInstance(con).getNewFiles();
		try {
		    
			if(datalist != null && datalist.size() > 0){
				JSONArray jsonarray = new JSONArray();
				for(RRGCrashLogData data:datalist){
					JSONObject infoObject = new JSONObject();
					
					infoObject.put("date", data.getDate());
					infoObject.put("device", data.getDevice());
					infoObject.put("sys_ver", data.getSystemVersion());
					infoObject.put("app_ver", data.getAppVersion());
					infoObject.put("error", data.getInfo());
					infoObject.put("sdk_ver", data.getSdkVersion());
					infoObject.put("app_name", data.getAppName());
					jsonarray.put(infoObject);
				}
				bulidStr = jsonarray.toString();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bulidStr;
	}
}
