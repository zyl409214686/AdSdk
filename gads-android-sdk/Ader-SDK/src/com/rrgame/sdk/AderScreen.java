package com.rrgame.sdk;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.rrgame.sdk.adutils.AderPublicUtils;
import com.rrgame.sdk.crash.RRGCrashHandler;
import com.rrgame.sdk.reportinfo.manager.GatherInfoManager;
import com.rrgame.sdk.reportinfo.manager.LBSManager;
import com.rrgame.sdk.service.AderInterstitialService;
import com.rrgame.sdk.systeminfo.AderLogUtils;

/**
 * 接口类
 * */
public class AderScreen {

	//private Context mContext = null;
	private AderInterstitialService mService;
	public static final int ZOOM_100 = 0;
    public static final int ZOOM_75 = 1;
	public static final int ZOOM_50 = 2;
	/**
	 * 初始化实例
	 * 
	 * @param appid
	 *            应用id String
	 * @param activity
	 *            Activity
	 * */
	public AderScreen(Activity activity, String appid,
			Boolean isTestMode) {
		initialAder(activity, appid, isTestMode, true);
	}

	/**
	 * 初始化实例
	 * @param activity
	 * @param appid
	 * @param isTestMode
	 * @param isApplyLBS 是否支持lbs
	 */
	public AderScreen(Activity activity, String appid,
			Boolean isTestMode, boolean isApplyLBS){
		initialAder(activity, appid, isTestMode, isApplyLBS);
		AderPublicUtils.getCaller();
	}
	
	/**
	 * 初始化ader
	 * @param activity
	 * @param appid
	 * @param isTestMode
	 * @param isApplyLBS 
	 */
	private void initialAder(Activity activity, String appid,
			Boolean isTestMode, boolean isApplyLBS){
		if (TextUtils.isEmpty(appid)) {
			Log.e(AderLogUtils.APP_NAME, "初始化失败，请检查传入参数是否正确！");
		} else {
			String mMode;
			if (isTestMode) {
				mMode = "true";
			} else {
				mMode = "false";
			}
			mService = new AderInterstitialService(activity, appid,
					mMode);
			
			RRGCrashHandler.getInstance(activity).registerCrashHandler();
 			//启动信息收集
			GatherInfoManager.getInstance(activity, appid).start(GatherInfoManager.systemType);
			GatherInfoManager.getInstance(activity, appid).start(GatherInfoManager.crashType);
			GatherInfoManager.getInstance(activity, appid).start(GatherInfoManager.appType);

			//根据是否支持lbs启动定位服务
			LBSManager.getInstance(activity).startCheckLBS(isApplyLBS);
			//发送定位消息
			LBSManager.getInstance(activity).sendlocationMsg(0);
		}
	}

	/**
	 * 请求插屏广告
	 * */
	public void load() {
		if (mService != null) {
			mService.loadInterstitialAd();
		}
	}

	/**
	 * 显示 需要先请求，就绪后才能显示
	 * */
	public void show(Activity activity) {
		if (mService != null) {
			mService.showInterstitialAd(activity);
		}
	}


	/**
	 * 设置监听回调
	 * */
	public void setAdListener(AderScreenListener listener) {
		if (mService != null) {
			mService.setInterstitialListener(listener);
		}
	}

	/**
	 * 查询是否就绪
	 * */
	public boolean isReady() {
		if (mService != null) {
			return mService.isReady();
		}
		return false;
	}
	/**
	 * 根据类型设置缩放比例
	 * @param type
	 */
	public void setZoomType(int type){
		if (mService != null) {
		    mService.setZoomLv(type);
		}
	}
	
	/**
	 * 返回当前SDK版本号
	 * @return
	 */
	public String getSDKVersion(){
		return AderPublicUtils.SDKVERSION;
	}
}
