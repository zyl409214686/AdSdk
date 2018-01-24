/**
 * 
 */
package com.rrgame.sdk.systeminfo;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.rrgame.sdk.adutils.AderPublicUtils;

/**
 * 应用程序用的一些常用信息 必须在程序启动时初始化
 * 
 * @author com.rrgame
 */
public class AderAppInfo {

	private Activity mActivity;

	/** Current network is EVDO revision 0 */
	private static int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	private static int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is HSDPA */
	private static int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	private static int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	private static int NETWORK_TYPE_HSPA = 10;

	/** 系统版本 */
	private String mOsVersion = null;
	/** MD5(serialno + "_" + getIMEI() + "_" + macaddress) */
	private String mUUID = null;
	/** IMEI */
	private String mUUID2 = null;
	/**IMSI**/
	private static String mUUID3 = null;
	/** 所在地区 */
	private String mNation = null;
	/** 时区 */
	private String mTimezone = null;
	/** MAC地址 */
	private String mTid = null;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** 屏幕高度 */
	private int mScreenHeight = 0;
	
	
	/** 屏幕宽度-Pixel */
	private int mScreenPixelWidth = 0;
	/** 屏幕高度- Pixel */
	private int mScreenPixelHeight = 0;
	/** 设备品牌 */
	private String mUseragt = null;
	/** 应用名 */
	private String mAppName = null;
	/** 包名 */
	private String mBundleId = null;
	/** mLandscape，是否竖屏 true-竖屏；false横屏 */
	private String mLandscape = "false";
	// private Uri PREFERRED_APN_URI =
	// Uri.parse("content://telephony/carriers/preferapn");
	/** 网络连接类型 */
	private String mConnectType;
	/** 屏幕分辨率类型 */
	// private int SizeType;
	/** 设备信息，如I9300等 */
	private String mTerType = null;
	/** 网络提供商 */
	private String mNetOper = null;
	/** 广告appid */
	public String mAppId = null;
	/** 广告宽度(dip) */
	public String mWidth = null;
	/** 广告高度(dip) */
	public String mHeight = null;
	/** 广告请求模式，测试或正式 */
	public String mAdMode = null;
	/** 屏幕像素密度 */
	public float mDensity = 1.5f;
	/** 是否获取了root权限 */
	private String mEsc_prison = "false";
	
	
	/** 流量来源 调用SDK的渠道包名标识 */
	public static String channle_packageName=""; 
	/** 系统信息map */
	public HashMap<String, String> mConfigmap = new HashMap<String, String>();
	
	private static String mChannel = null;//渠道ID号

	/**
	 * 初始化应用程序用的一些常用信息
	 * 
	 * @param context
	 */
	public void init(Activity context) {
		if (context == null) {
			AderLogUtils.w("context is null");
			return;
		}
		mActivity=context;

		mOsVersion = android.os.Build.VERSION.RELEASE;
		if (TextUtils.isEmpty(mOsVersion)) {
			mOsVersion="unknownos";
		}
		// 像素密度
		DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
		float density = metrics.density;
		if (density <= 0) {
			density = 1.5f;
		}
		mDensity = density;
		
		resetOrientation();
		
		Display display = context.getWindowManager().getDefaultDisplay();
		
		mScreenPixelWidth = (int)(display.getWidth());
		mScreenPixelHeight = (int)(display.getHeight());
		
		mScreenWidth = (int)(display.getWidth()/mDensity);
		mScreenHeight = (int)(display.getHeight()/mDensity);

		mConnectType = getConnectInfo(mActivity);
		mUseragt = android.os.Build.BRAND;
		if (TextUtils.isEmpty(mUseragt)) {
			mUseragt="unknownuseragt";
		}
		mUseragt = mUseragt.replace(" ", "");

		mTerType = android.os.Build.MODEL;
		if (TextUtils.isEmpty(mTerType)) {
			mTerType="unknowntertype";
		}
		mTerType = mTerType.replace(" ", "");

		String serialno = getSerialNumber();
		// 获取wifi mac地址
		// user_permission_error
		String macaddress = null;
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		try {
			WifiInfo info = wifi.getConnectionInfo();
			if (info != null) {
				macaddress = info.getMacAddress();
			}
		} catch (Exception e) {
			Log.e(AderLogUtils.APP_NAME,
					"没有android.permission.ACCESS_WIFI_STATE权限");
		}

		if (TextUtils.isEmpty(macaddress)) {
			macaddress = "000000000000";
		} 
		macaddress = macaddress.replaceAll("[^0-9a-fA-F]", "").toUpperCase();

		mUUID = serialno + "_" + getIMEI(mActivity) + "_" + macaddress;

		mUUID = AderMD5.getMD5(mUUID);
		if (TextUtils.isEmpty(mUUID)) {
			//32位
			mUUID="00000000000000000000000000000000";
		}

		mUUID2 = getIMEI(mActivity);
		if (TextUtils.isEmpty(mUUID2)) {
			//15位
			mUUID2 = "000000000000000";
		}
		
		mUUID3 = getIMSI();
		
		if (TextUtils.isEmpty(mUUID3))
		{ 
			mUUID3 = "000000000000000";
		}

		mTid = macaddress;

		Locale locale = Locale.getDefault();
		mNation = locale.getCountry();
		if (TextUtils.isEmpty(mNation)) {
			mNation = "unknownlang";
		}

		TimeZone zone = TimeZone.getDefault();
		mTimezone = zone.getID();
		if (TextUtils.isEmpty(mTimezone)) {
			mTimezone = "unknowntzone";
		}

		mNetOper = getNetProvider(mActivity);

		getPackageName(context);

		mEsc_prison = AderDeviceRootAccess.isRootSystem() ? "true" : "false";
		
		//渠道号判断
		mChannel = AderPublicUtils.getAppLicationMetaData(context, "cid");
		
		if (TextUtils.isEmpty(mChannel)||!isNumer(mChannel))
		{
			mChannel = "0";
			Log.e(AderLogUtils.APP_NAME, "未设置渠道id或是非法id，请输入数字！");
		}

		buildMapForConfig();
		// configUrl = AderUtils.buildUrl("http://adbc.rrgame.com/confg/init");
		
		report_mUUID = mUUID;
		report_mUUID2 = mUUID2;
		report_mUUID3 = mUUID3;
		report_mTid = mTid;
	}
	
	/**
	 * 将获取到的系统信息转换为hash map
	 * 
	 * @author com.rrgame
	 * @see buildDicForWebView,
	 */
	public void buildMapForConfig() {
		mConfigmap.put("landscape", mLandscape);
		mConfigmap.put("sdk_type", "2");
		mConfigmap.put("scrn_w", String.valueOf(mScreenWidth));
		mConfigmap.put("scrn_h", String.valueOf(mScreenHeight));
		mConfigmap.put("app_id", mAppId);
		if (mTerType.equals("sdk") || mTerType.equals("google_sdk")) {
			mConfigmap.put("dev_mode", "true");
			// mConfigmap.put("ter_type", "simulator");

		} else {
			mConfigmap.put("dev_mode", mAdMode);
			// mConfigmap.put("ter_type", mTerType);
		}
		mConfigmap.put("user_agt", mUseragt);
		mConfigmap.put("os", mOsVersion);
		mConfigmap.put("sdk_ver", AderPublicUtils.SDKVERSION);
		mConfigmap.put("net_oper", mNetOper);
		mConfigmap.put("esc_prison", mEsc_prison);
		mConfigmap.put("ad_h", mHeight);
		mConfigmap.put("ad_w", mWidth);
		mConfigmap.put("acc_point", mConnectType);
		mConfigmap.put("ter_type", mTerType);
		mConfigmap.put("uuid1", mUUID);
		mConfigmap.put("uuid2", mUUID2);
		mConfigmap.put("uuid3", mUUID3);
		mConfigmap.put("app_name", mAppName);
		mConfigmap.put("bndl_id", mBundleId);
		mConfigmap.put("tid", mTid);
		mConfigmap.put("language", mNation);
		mConfigmap.put("timezone", mTimezone);
		mConfigmap.put("channel", mChannel);

		DecimalFormat df = new DecimalFormat("###.00");
		String density = df.format(mDensity);
		if (TextUtils.isEmpty(density)) {
			density = "1.50";
		}
		mConfigmap.put("density", density);
	}

	/**
	 * 获取序列号:ro.serialno
	 * */
	public static String getSerialNumber() {
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
			AderLogUtils.d(serial);
		} catch (Exception e) {
			AderLogUtils.e(e.getMessage());
		}
		return serial;
	}

	/**重新设置屏幕方向*/
	public void resetOrientation(){
		// mSoftVersion = getCurrentVersion(context);
		if (mActivity!=null) {
			if (mActivity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				mLandscape = "true";
			} else {
				mLandscape = "false";
			}
		}
	}
	
	/**获取屏幕宽度*/
	public int getmScreenWidth() {
		return mScreenWidth;
	}

	/**获取屏幕高度*/
	public int getmScreenHeight() {
		return mScreenHeight;
	}

	/**获取屏幕宽度-Pixel*/
	public int getmScreenPixelWidth() {
		return mScreenPixelWidth;
	}

	/**获取屏幕高度-Pixel*/
	public int getmScreenPixelHeight() {
		return mScreenPixelHeight;
	}
	
	/**获取当前屏幕方向*/
	public String getmLandscape() {
		return mLandscape;
	}

	/**
	 * 获取设备id:DeviceId
	 */
	public static String getIMEI(Context context) {
		if (context == null) {
			AderLogUtils.w("context is null");
			return "";
		}
		// user_permission_error
		TelephonyManager telephonyManager = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE));
		try {
			if (telephonyManager != null) {
				return telephonyManager.getDeviceId();
			}
		} catch (Exception e) {
			Log.e(AderLogUtils.APP_NAME,
					"没有android.permission.READ_PHONE_STATE权限");
		}
		return "";
	}
	
	/**
	 * 获取Sim卡IMSI号
	 * @return
	 */
    private String getIMSI() {
		if (mActivity==null) {
			AderLogUtils.w("mActivity is null");
			return "";
		}
		TelephonyManager telephonyManager=((TelephonyManager) mActivity
				.getSystemService(Context.TELEPHONY_SERVICE));
		try {
			if (telephonyManager!=null) {
				return telephonyManager.getSubscriberId();
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 获取当前的安装包版本
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentVersion(Context context) {
		if (context == null) {
			AderLogUtils.w("context is null");
			return AderPublicUtils.SDKVERSION;
		}
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {

		}

		return AderPublicUtils.SDKVERSION;
	}

	/**
	 * 区分2g，3g网络
	 * 
	 */
	private static String solveType(int subtype) {
		if (subtype == TelephonyManager.NETWORK_TYPE_UMTS
				|| subtype == NETWORK_TYPE_HSDPA
				|| subtype == NETWORK_TYPE_HSUPA
				|| subtype == NETWORK_TYPE_HSPA
				|| subtype == NETWORK_TYPE_EVDO_0
				|| subtype == NETWORK_TYPE_EVDO_A) {
			return "3G";
		} else {
			return "2G";
		}

	}

	/**
	 * 获得网络连接类型
	 * 
	 */
	private static String getConnectInfo(Context context) {
		if (context == null) {
			AderLogUtils.w("context is null");
			return "unknownaccpoint";
		}
		// user_permission_error
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = null;
		try {
			info = conMan.getActiveNetworkInfo();
		} catch (Exception e) {
			Log.e(AderLogUtils.APP_NAME,
					"没有android.permission.ACCESS_NETWORK_STATE权限");
		}
		if (info != null && info.isConnected()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return "wifi";
			} else {
				return solveType(info.getSubtype());
			}
		} else {
			return "unknownaccpoint";
		}
	}

	/**
	 * 获取网络供应商
	 * 
	 */
	public static String getNetProvider(Context context) {
		if (context == null) {
			AderLogUtils.w("context is null");
			return "00000";
		}
		// user_permission_error
		TelephonyManager telephonyManager = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE));
		String IMSI = null;
		try {
			if (telephonyManager != null) {
				IMSI = telephonyManager.getSubscriberId();
			}
		} catch (Exception e) {
			Log.e(AderLogUtils.APP_NAME,
					"没有android.permission.READ_PHONE_STATE权限");
		}
		if (TextUtils.isEmpty(IMSI)) {
			IMSI="00000";
		}
		return IMSI;
	}

	/**
	 * 测试当前网络是否是wifi环境
	 */
	public static boolean isWifi(Context context) {
		if (context == null) {
			AderLogUtils.w("context is null");
			return false;
		}
		// user_permission_error
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = null;
		try {
			activeNetInfo = connectivityManager.getActiveNetworkInfo();
		} catch (Exception e) {
			Log.e(AderLogUtils.APP_NAME,
					"没有android.permission.ACCESS_NETWORK_STATE权限");
		}
		return activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 获取应用名及包名
	 * 
	 */
	public void getPackageName(Activity context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			// 当前版本的包名
			mBundleId = info.packageName;
			// 获取当前程序名称
			mAppName = (context.getPackageManager()
					.getApplicationLabel(info.applicationInfo)).toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(mBundleId)) {
			mBundleId="unknownbndlid";
		}
		if (TextUtils.isEmpty(mAppName)) {
			mAppName="unknownappname";
		}
	}

	/**
	 * 查询手机内非系统应用信息
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("static-access")
//	public List<AderAppInfoItem> getAllApps(Context context) {
//
//		List<AderAppInfoItem> apps = new ArrayList<AderAppInfoItem>();
//		PackageManager pManager = context.getPackageManager();
//		// 获取手机内所有应用
//		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
//
//		for (int i = 0; i < paklist.size(); i++) {
//			PackageInfo pak = (PackageInfo) paklist.get(i);
//
//			// 判断是否为非系统预装的应用程序
//			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
//				// customs applications
//				AderAppInfoItem infoItem = new AderAppInfoItem(
//						(pManager.getApplicationLabel(pak.applicationInfo)
//								.toString()),
//						pak.applicationInfo.packageName,
//						pak.applicationInfo.processName,
//						pak.applicationInfo.targetSdkVersion, pak.versionCode);
//				apps.add(infoItem);
//			}
//		}
//		return apps;
//	}
	
	 /**
     * 获得上报时需要的一些字段
     * @param appid
     * @return
     */
	private static String report_mUUID = "";
	private static String report_mUUID2 = "";
	private static String report_mUUID3 = "";
	private static String report_mTid = "";
    public static HashMap<String,String> getReportInfoThings(){
    	HashMap<String,String> info = new HashMap<String,String>();
    	info.put("uuid1", report_mUUID);
    	info.put("uuid2", report_mUUID2);
    	info.put("uuid3", report_mUUID3);
    	info.put("tid", report_mTid);
    	return info;
    }
    
    /**
   	 * 判断是否为数字
   	 * @param str
   	 * @return
   	 */
       private  boolean isNumer(String str){ 
           Pattern pattern = Pattern.compile("[0-9]*"); 
           return pattern.matcher(str).matches();    
        }

}
