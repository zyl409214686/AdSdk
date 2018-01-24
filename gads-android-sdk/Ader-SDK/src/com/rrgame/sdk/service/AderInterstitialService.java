package com.rrgame.sdk.service;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.rrgame.sdk.AderScreen;
import com.rrgame.sdk.AderScreenListener;
import com.rrgame.sdk.adutils.AderEventObserver;
import com.rrgame.sdk.adutils.AderPublicUtils;
import com.rrgame.sdk.adutils.AderPublicUtils.Webview_Type;
import com.rrgame.sdk.systeminfo.AderLogUtils;
import com.rrgame.sdk.webView.AderBigWebView;
import com.rrgame.sdk.webView.AderInterstitialAdView;
import com.rrgame.sdk.webView.AderInterstitialAdView.AderInterstitialAdViewListener;
import com.rrgame.sdk.webView.AderView;

public class AderInterstitialService extends AderService implements
		AderInterstitialAdViewListener {

	/** 插屏广告状态值 */
	/** 状态错误 */
	private static final int AderInterstitialAd_Error = 0;
	/** 初始化状态，未开始请求 */
	private static final int AderInterstitialAd_Initialized = 1;
	/** 正在广告请求中 */
	private static final int AderInterstitialAd_Requesting = 2;
	/** 请求成功，等待展示 */
	private static final int AderInterstitialAd_Ready = 3;
	/** 请求失败 */
	private static final int AderInterstitialAd_Fail = 4;
	/** 已经被显示 */
	private static final int AderInterstitialAd_Showed = 5;

	/** 插屏广告状态 */
	private int interstitialStatus = AderInterstitialAd_Error;
	private static final int AdView_Id = 1234321;
	/** 插屏广告回调 */
	private AderScreenListener mInterstitialAdListener;
	/** 插屏广告视图 */
	private AderInterstitialAdView mInterstitialAdView;

	private Activity mActivity;
	
	/**
	 * 广告缩小的比例
	 */
	private float zoomLv = 1.0f;

	public AderInterstitialService(Activity activity, String appId, String mode) {
		super(activity, AderPublicUtils.interstitialConfigHead);
		mActivity = activity;
		interstitialStatus = AderInterstitialAd_Error;
		/** 初始化广告视图 */
		mInterstitialAdView = new AderInterstitialAdView(activity);
		
		mInterstitialAdView.setBackgroundColor(Color.argb(180, 0, 0, 0));
		RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mDialog = new Dialog(mActivity,
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.setContentView(mInterstitialAdView, lparams);

		startService(appId, 320, 50, mode, mInterstitialAdView);
	}

	public void setInterstitialListener(AderScreenListener mListener) {
		mInterstitialAdListener = mListener;
	}

	/** 是否准备就绪 */
	public Boolean isReady() {
		return (interstitialStatus == AderInterstitialAd_Ready);
	}

	/** 显示插屏广告 */
	public void showInterstitialAd(Activity activity) {
		if (interstitialStatus == AderInterstitialAd_Showed) {
			Log.e(AderLogUtils.APP_NAME, "插屏广告已经展示");
		} else if (interstitialStatus != AderInterstitialAd_Ready) {
			Log.e(AderLogUtils.APP_NAME, "插屏广告还未准备好");
		} else {
			
			//关闭可能一已经打开的的webview
			closeBigWebView();
			
			if (activity != null || mActivity != null) {
				if (mActivity == null) {
					mActivity = activity;
				}
				try
				{
					/**插屏广告显示后注册屏幕亮暗监听*/
					registerScreenObserver();

					/**显示广告dialog*/
					mDialog.show();
					
					mInterstitialAdView.setCloseButton(adView,mAppInfo.mDensity);
					
					interstitialStatus = AderInterstitialAd_Showed;
					/**汇报加载时间*/
					reportEvent(REPORT_LOAD_TIME,null);
					/**汇报有效展示*/
					reportEvent(REPORT_VALID_SHOW,null);
					if (mInterstitialAdListener != null) {
						mInterstitialAdListener.onScreenPresent();
					}
					mInterstitialAdView.setAdViewListener(this);
				}
				catch(Exception e)
				{
					interstitialStatus = AderInterstitialAd_Error;
				}
			
			} else {
				Log.e(AderLogUtils.APP_NAME, "activity不正确，请传入正确的activity");
			}
		}
	}

	/**
	 * 开启sdk服务 初始化系统信息及广告视图
	 * 
	 * @param sdkView
	 * @return 返回是否初始化成功
	 */
	@Override
	protected Boolean startService(String appId, int width, int height,
			String mode, ViewGroup sdkView) {

		if (super.startService(appId, width, height, mode, sdkView)) {
			// 设置初始化状态
			if (interstitialStatus == AderInterstitialAd_Error) {
				interstitialStatus = AderInterstitialAd_Initialized;
			}

			/** 开启后台处理线程 */
			initBackHandler();

			/** 初始化广告视图 */
			adView = new AderView(mContext);
			adView.setId(AdView_Id);
			adView.setTag("adView");
			adView.setBackgroundColor(Color.TRANSPARENT);

			/** 把广告视图加到SDKView上 */
			LayoutParams lParams= new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			sdkView.addView(adView,lParams);
			
			sdkView.setVisibility(View.VISIBLE);
		} else {
			return false;
		}
		return true;
	}

	/** 加载插屏广告 */
	public void loadInterstitialAd() {
		if (interstitialStatus == AderInterstitialAd_Error) {
			Log.e(AderLogUtils.APP_NAME, "警告：没有初始化，请先初始化");
		} else if (interstitialStatus == AderInterstitialAd_Showed) {
			Log.e(AderLogUtils.APP_NAME, "警告：广告已经在展示中！");
		} else if (interstitialStatus == AderInterstitialAd_Ready) {
			Log.e(AderLogUtils.APP_NAME, "警告：广告已准备就绪，请直接调用showAd展示！");
		} else if (interstitialStatus == AderInterstitialAd_Requesting) {
			Log.e(AderLogUtils.APP_NAME, "警告：已存在正在请求的广告，请稍后");
		} else {
			interstitialStatus = AderInterstitialAd_Requesting;
			/**重新计算广告尺寸*/
			if(resetInterstitialAdSize())
			{
				if (mConfigItem != null && mConfigItem.getGetAdHost() != null
						&& mConfigItem.getGetAdPath() != null
						&& mConfigItem.getJsAddress() != null) {
					/**如果上次请求配置时间大于一天则重新请求*/
					long time = System.currentTimeMillis();
					long temp = (time - mStartTime) / SWITCH_TO_DAYS;
					if (temp >= 1) {
						/**重置服务*/
						resetService();
						/**初始化后台线程*/
						initBackHandler();
						/**重新请求配置*/
						requestConfig();
					}else{
						requestAdInfo();
					}
				} else {
					/** 请求配置信息 */
					requestConfig();
				}

			}
		}
	}

	/**
	 * 请求配置失败
	 * 
	 * @param requestType
	 *            请求类型
	 * */
	@Override
	protected void onRequestConfigFailed(int requestType) {
		if (requestType == REQUEST_CONFIG_TYPE) {
			AderLogUtils.w("请求配置失败了");
			// 回调错误信息:网络请求失败
			hasNoErrorMsg("101");
		}
	}

	/** 程序内打开webview */
	protected void openWebView(String url, int viewHeight)
	{
		if (url != null && viewHeight >0) 
		{
			if(interstitialStatus == AderInterstitialAd_Showed)
			{
				//关闭可能一已经打开的的webview
				closeBigWebView();
				
				adBigWebView = new AderBigWebView(mContext);
				adBigWebView.loadUrl(url);
				AderEventObserver mEventObserver;
				mEventObserver = new AderEventObserver(this);
				// 设置监听器
				adBigWebView.setListener(mEventObserver);
				// LayoutParams.FILL_PARENT
				AderLogUtils.d("mContext---->" + mContext);

				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT,
						RelativeLayout.LayoutParams.FILL_PARENT);

				adBigWebView.setLayoutParams(layoutParams);
				adBigWebView.setWebViewHeight(viewHeight);// Integer.parseInt(viewHeight)
				mInterstitialAdView.addView(adBigWebView);
				
			}
				
			
		}
	}

	
	/**
	 * 停止sdk服务
	 * 
	 */
	@Override
	public void stopService() {
		resetService();
		super.stopService();
	}

	/**
	 * 广告载入成功
	 * 
	 */
	@Override
	protected void webViewAdLoadSuccess(Webview_Type webType) {
		AderLogUtils.d("webViewAdLoadSuccess");
		if (webType == Webview_Type.web_small_type) {
			/** 记录加载时间 */
			loadTime = (System.currentTimeMillis() - startTime) / 1000;
			adView.getWebView().showWebViewWithoutAnimation();
			//adView.getWebView().webViewAnimation(ad_animation);

			mTryCount = 0;
			interstitialStatus = AderInterstitialAd_Ready;
			if (mInterstitialAdListener != null) {
				mInterstitialAdListener.onScreenReady();
			}
		}
	}

	/**
	 * 广告载入失败
	 * 
	 */
	@Override
	protected void webViewAdLoadFaild(Webview_Type webType) {
		if (webType == Webview_Type.web_small_type) {
			AderLogUtils.w("广告请求失败");
			hasNoErrorMsg("101");
		}
	}

	/**
	 * 广告载入失败
	 * 
	 * @param reason
	 *            失败原因
	 */
	@Override
	protected void getAdFailed(String reason) {
		AderLogUtils.w("广告载入失败");
		interstitialStatus = AderInterstitialAd_Fail;
		if (reason.equals("empty")) {
			hasNoErrorMsg("7");
		}
	}

	/** 收到错误信息 */
	@Override
	protected void didReceiveError(int errorCode, String errorMsg) {
		interstitialStatus = AderInterstitialAd_Fail;
		
		if (mInterstitialAdListener != null) {
			AderLogUtils.w("errorCode:" + errorCode + "#msg:" + errorMsg);
			mInterstitialAdListener.onScreenFailed(errorCode,
					errorMsg);
		}
	}
	
	
	private int[] getAdSize()
	{
		int[] adSize  ={0,0};
		
		int interstitialAdWidth;
		int interstitialAdHeight;
		String landSpaceString=mAppInfo.getmLandscape();
		int screenWidth = mAppInfo.getmScreenPixelWidth();
		int screenHeight= mAppInfo.getmScreenPixelHeight();
		int longSide = (screenWidth>=screenHeight) ? screenWidth : screenHeight;
		int shortSide = (screenWidth>=screenHeight) ? screenHeight : screenWidth;

		if(( ((float)longSide)/((float)shortSide) ) >= 1.5)
		{
			if(shortSide <320)
			{
				AderLogUtils.w("error:" + "像素不够,不能显示广告");
				return adSize;
			}
			else if(shortSide <480)
			{
				interstitialAdWidth =landSpaceString.equals("true") ?256 :384;
				interstitialAdHeight=landSpaceString.equals("true") ?384 :256;
			}
			else if(shortSide <720)
			{ 
				interstitialAdWidth =landSpaceString.equals("true") ?384 :576;
				interstitialAdHeight=landSpaceString.equals("true") ?576 :384;
			}
			else
			{
				interstitialAdWidth =landSpaceString.equals("true") ?576 :864;
				interstitialAdHeight=landSpaceString.equals("true") ?864 :576;
			}
		}
		else
		{
			if(longSide <480)
			{
				AderLogUtils.w("error:" + "像素不够,不能显示广告");
				return adSize;
			}
			else if(longSide <720)
			{
				interstitialAdWidth =landSpaceString.equals("true") ?256 :384;
				interstitialAdHeight=landSpaceString.equals("true") ?384 :256;
			}
			else if(longSide <1080)
			{
				interstitialAdWidth =landSpaceString.equals("true") ?384 :576;
				interstitialAdHeight=landSpaceString.equals("true") ?576 :384;
			}
			else
			{
				interstitialAdWidth =landSpaceString.equals("true") ?576 :864;
				interstitialAdHeight=landSpaceString.equals("true") ?864 :576;
			}
		}

		
		if(interstitialAdHeight >0 && interstitialAdWidth >0)
		{
			adSize[0] =(int)(interstitialAdWidth*zoomLv);
			adSize[1] =(int)(interstitialAdHeight*zoomLv);
		}
		return adSize;
	}

	
	
	/** 计算插屏广告尺寸 
	 * 规则如下
	 * 1 长边/短边≥1.5，按短边调度原图片，不缩放：
      短边<320，不展示广告；
      320≤短边<480，调取256*384，若是横屏，调取384*256
      480≤短边<720，调取384*576，若是横屏，调取576*384
      短边≥720，调取576*864，若是横屏，调取864*576。
             
     * 2 长边/短边<1.5，按长边调度原图片，不缩放：
      长边<480，不展示广告；
      480≤长边<720，调取256*384，若是横屏，调取384*256
      720≤长边<1080，调取384*576，若是横屏，调取576*384
      长边≥1080，调取576*864，若是横屏，调取864*576。
	 * 
	 * */
	private Boolean resetInterstitialAdSize() {
		//重新获取屏幕方向
		mAppInfo.resetOrientation();
		//index 0:宽度；1：高度；
		int[] adSize = getAdSize();
		if(adSize[0] >0 && adSize[1] >0 && mAppInfo.mDensity >0.0f)
		{
			int interstitialAdWidth = adSize[0];//宽度
			int interstitialAdHeight= adSize[1];//高度
			mAppInfo.mWidth=String.valueOf((int)(interstitialAdWidth/mAppInfo.mDensity));
			mAppInfo.mHeight=String.valueOf((int)(interstitialAdHeight/mAppInfo.mDensity));
			mAppInfo.buildMapForConfig();
			adView.initInterstitialView(mContext, this,(int)(interstitialAdWidth),(int)(interstitialAdHeight));
			
			return true;
		}
		return false;
	}



	@Override
	public void onCloseInterstitialAdView(AderInterstitialAdView _adView) {
		/**关闭广告dialog*/
		 closeInterstitialDialog();
	}
	
	/**
	 * app进入前台，进入后台
	 * 
	 * 改为dialog显示广告后，这一块只处理按home键进入后台和返回前台，不再处理按back键的动作
	 * 
	 */
	@Override
	public void windowVisibilityChanged(int visibility, ViewGroup aderSDKView) {
		AderLogUtils.d("windowVisibilityChanged#########################"
				+ visibility);
		if (visibility == View.VISIBLE) {
			isShow = true;
			if (interstitialStatus == AderInterstitialAd_Showed) {
				reportEvent(REPORT_START_TYPE, null);
			}
		} else {
			if (visibility==View.GONE) {
				//进入后台，只处理dialog显示的情况
				if (mDialog!=null&&mDialog.isShowing()) {
					AderLogUtils.i("windowVisibilityChanged view.GONE");
					if (interstitialStatus == AderInterstitialAd_Showed) {
						/** 汇报进入后台 */
						reportEvent(REPORT_TERMINATE_TYPE, null);
					}
				}
			}
			
			isShow = false;
		}
	}
	
	/**广告视图被移除，
	 * 
	 * */
	public void onDetachInterstitialAd(AderInterstitialAdView _adView){
		AderLogUtils.i("");
		if (interstitialStatus==AderInterstitialAd_Initialized) {
			//如果已经处理重置为初始状态，则不再处理
			return;
		}
		/** 恢复到初始状态 */
		interstitialStatus = AderInterstitialAd_Initialized;
		
		mInterstitialAdView.setAdViewListener(null);
		/**取消事件监听和等待执行的任务*/
		if (mBackHandler!=null) {
			mBackHandler.removeCallbacksAndMessages(null);
		}
		if (mMainUIHandler!=null) {
			mMainUIHandler.removeCallbacksAndMessages(null);
		}
		/**注销屏幕亮暗监听*/
		unregisterScreenObserver();
		if (mInterstitialAdListener != null) {
			mInterstitialAdListener.onScreenDismiss();
		}
	}
	
	/**
	 * 屏幕开启后恢复sdk
	 * 
	 */
	@Override
	protected void doSomethingOnScreenOn() {
		AderLogUtils.i("view is show:"+isShow);
		if (isShow) {
			if (interstitialStatus == AderInterstitialAd_Showed) {
				/** 汇报进入前台 */
				reportEvent(REPORT_START_TYPE, null);
			}
		}
	}

	/**
	 * 屏幕关闭后暂停sdk
	 * 
	 */
	@Override
	protected void doSomethingOnScreenOff() {
		AderLogUtils.i("view is show:" + isShow);
		if (isShow) {
			if (interstitialStatus == AderInterstitialAd_Showed) {
				/** 汇报进入后台 */
				reportEvent(REPORT_TERMINATE_TYPE, null);
			}
		}
	}
	/**
	 * 根据类型设置缩放比例
	 * @param zType
	 */
	public void setZoomLv(int zType){
		float zlv = 1.0f;
		switch(zType){
		  case AderScreen.ZOOM_75:
			  zlv = 0.75f;
		  break;
		  case AderScreen.ZOOM_50:
			  zlv = 0.5f;
		  break;
		}
		this.zoomLv = zlv;
	}

}
