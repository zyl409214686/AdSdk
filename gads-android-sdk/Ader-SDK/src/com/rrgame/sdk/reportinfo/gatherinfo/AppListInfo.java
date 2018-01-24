package com.rrgame.sdk.reportinfo.gatherinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.rrgame.sdk.systeminfo.AderLogUtils;
/**
 * APP列表信息
 * @author shaozhe
 *
 */
public class AppListInfo {
   /**
	 * 组装app列表信息
	 * @return
	 */
	public static String bulidAppListInfo(Context con){
		String bulidStr = "";
		try {
		   JSONArray jsonarray = new JSONArray();
		   //得到PackageManager对象 
		   PackageManager pm = con.getPackageManager();
		   Map<String,String> runAppName = getRunApp(con);
		   List<PackageInfo> packs = pm.getInstalledPackages(0);
		   for(PackageInfo pi:packs) 
		   { 
			  JSONObject infoObject = new JSONObject();
			  String appName = "";  
			  String packageName = "";
			  if( pi.applicationInfo.loadLabel(pm)!=null )
				  appName = pi.applicationInfo.loadLabel(pm).toString().trim();
			  if( pi.applicationInfo.packageName!=null )
				  packageName = pi.applicationInfo.packageName.toString().trim();
		      infoObject.put("app_name", appName);//应用程序名称 
		      infoObject.put("pkg_name", packageName); //应用程序包名 
		      if(runAppName != null&&!TextUtils.isEmpty(packageName)){
		    	  String str = runAppName.get(packageName);
		    	  if(!TextUtils.isEmpty(str)){
		    		  infoObject.put("run", 1);
		    	  }
		    	  else{
		    		  infoObject.put("run", 0);
		    	  }
		      }
		      else{
		    	  infoObject.put("run", 0);
		      }
		      
		      ApplicationInfo appInfo = pi.applicationInfo;
		      int isSysApp = sysApp(appInfo);
		      infoObject.put("sys", isSysApp);
		       
		      jsonarray.put(infoObject);
		   }
		   bulidStr = jsonarray.toString();
		   AderLogUtils.d("app列表收集applist="+bulidStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return bulidStr;
	}
	
	/**
	 * 获取内存中运行的APP 包名
	 * @param con
	 * @return
	 */
	private static Map<String,String> getRunApp(Context con){
		Map<String,String> pgkProcessAppMap = new HashMap<String, String>();
		ActivityManager mActivityManager = (ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE); 
		List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager  
                .getRunningAppProcesses();  
		for (RunningAppProcessInfo appProcess : appProcessList) {  
            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包  
            if(pkgNameList==null)
            	continue;
            // 输出所有应用程序的包名  
            for (int i = 0; i < pkgNameList.length; i++) {  
                String pkgName = "";
                if(pkgNameList[i]!=null)
                	pkgName = pkgNameList[i];  
                // 加入至map对象里  
                pgkProcessAppMap.put(pkgName, "pkgname");
            }  
        }
		
		return pgkProcessAppMap;
		
	}
	
	/**
	 * 判断某个应用程序是 不是系统应用程序
	 * @param info
	 * @return 如果是系统应用程序则返回true，如果不是系统程序则返回false
	 */
	private static int sysApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return 0;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return 0;
        }
        return 1;
	}
}
