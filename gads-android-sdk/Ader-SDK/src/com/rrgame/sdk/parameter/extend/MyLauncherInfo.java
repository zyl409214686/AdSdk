package com.rrgame.sdk.parameter.extend;
/**
 * launcher桌面信息
 */
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class MyLauncherInfo {
	private static final String LAUNCHERDB_ITEM_TYPE = "itemType";
	private static final String LAUNCHERDB_APPWIDGET_ID = "appWidgetId";
	private static final String LAUNCHERDB_TITLE = "title";
	
	/**
	 * 读取launcher.db来获取桌面信息
	 * @param con
	 */
	public static void readLauncherDB(Context con){
		if(checkPermission(con)){
			ContentResolver cr = con.getContentResolver(); 
			String AUTHORITY = getAuthorityFromPermission(con.getApplicationContext(), "com.android.launcher.permission.READ_SETTINGS");
			System.out.println("AUTHORITY="+AUTHORITY);
//	        /* 
//	         * 根据版本号设置Uri的AUTHORITY 
//	         */  
//	        if(Build.VERSION.SDK_INT>=8){  
//	            AUTHORITY = "com.android.launcher2.settings";  
//	        }else{  
//	            AUTHORITY = "com.android.launcher.settings";  
//	        }  
	          
	        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  
	                + "/favorites?notify=true");  
	        Cursor c = cr.query(CONTENT_URI,  
	                null, null,  
	                null, null);
	        String[] colums = c.getColumnNames();
	        for(String ss : colums){
	        	 System.out.println("ss="+ss);
	        }
	        c.moveToFirst();
	        while(!c.isAfterLast()){
	        	int itemType_id = c.getColumnIndex(LAUNCHERDB_ITEM_TYPE);
	        	int appWidgetId_id = c.getColumnIndex(LAUNCHERDB_APPWIDGET_ID);
	        	int title_id = c.getColumnIndex(LAUNCHERDB_TITLE);
	        	if(c.getInt(itemType_id) == 4){
//	        	   System.out.println("itemType="+c.getString(itemType_id));
//	        	   System.out.println("appWidgetId="+c.getInt(appWidgetId_id));
//	        	   System.out.println("title="+c.getString(title_id));
	        	}
	        	c.moveToNext();
	        }
	       if(c!=null){
	    	   c.close();
	       }
		}
	}
	/**
	 * 检测是否添加了GPS权限
	 */
	public static boolean checkPermission(Context con){
		String packageNameString = con.getApplicationInfo().packageName;
		PackageManager packageManager = con.getPackageManager();
		if (packageManager.checkPermission("com.android.launcher.permission.READ_SETTINGS",
				packageNameString) == PackageManager.PERMISSION_DENIED) {
			return false;
		}
		
		return true;
	}
	/**
	 * 获取launcher.db的路径
	 * @param context
	 * @param permission
	 * @return
	 */
	private static String getAuthorityFromPermission(Context context, String permission) {
		 if (TextUtils.isEmpty(permission)) {
             return null;
		 }
		 List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
		 if (packs == null) {
		      return null;
		}
		for (PackageInfo pack : packs) {
		  ProviderInfo[] providers = pack.providers;
		  if (providers != null) {
		     for (ProviderInfo provider : providers) {
		         if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
		                         return provider.authority;
		         }
		     }
	      }
		}
		 return null;
   }
	/**
	 * 获得所有安装的appWidget
	 * @param con
	 */
	public static void getAppWidget(Context con){ 
		AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(con);
		List<AppWidgetProviderInfo> widgetInfos = mAppWidgetManager.getInstalledProviders();
		for(AppWidgetProviderInfo info:widgetInfos){
//			System.out.println("=="+info.label);
//			System.out.println("=="+info.icon);
//			System.out.println("=="+info.minWidth);
//			System.out.println("=="+info.minHeight);
//			System.out.println("=="+info.configure);
//			System.out.println("=="+info.provider);
//			System.out.println("=="+mAppWidgetManager.getAppWidgetIds(info.configure).length);
			
		}
	}
}
