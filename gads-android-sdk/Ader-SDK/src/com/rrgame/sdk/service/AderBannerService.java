package com.rrgame.sdk.service;

import java.util.Random;

import com.rrgame.sdk.adutils.AderListener;
import com.rrgame.sdk.adutils.AderPublicUtils;
import com.rrgame.sdk.adutils.AderPublicUtils.Webview_Type;
import com.rrgame.sdk.systeminfo.AderLogUtils;
import com.rrgame.sdk.webView.AderView;
import com.rrgame.sdk.webView.AderViewUtilsListener;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class AderBannerService extends AderService implements
		AderViewUtilsListener {

	/** Banner广告可能的状态值 */
	private static final int Service_Is_Stopped = 0;
	private static final int Service_Is_Running = 1;
	private static final int Service_Is_Paused = 2;
	private static final int Service_Is_ReadyToConfig = 3;
	/** banner广告状态 */
	private static int bannerServiceStatus = Service_Is_Stopped;

	/** Banner单例 */
	private static AderBannerService mBannerInstance = null;

	/** Banner广告回调 */
	private AderListener mBannerListener;

	/** 广告的展示条数 */
	protected int mShowCount = 0;

	public static AderBannerService getInstance(Context context) {
		synchronized (AderBannerService.class) {
			if (mBannerInstance == null) {
				AderLogUtils.d("BannerService getInstance");
				mBannerInstance = new AderBannerService(context);
			}
		}
		return mBannerInstance;
	}

	public static boolean serviceIsRuning()
	{
		return bannerServiceStatus != Service_Is_Stopped;
	}
	
	protected AderBannerService(Context context) {
		super(context, AderPublicUtils.bannerConfigHead);
	}

	/**
	 * 设置Banner广告事件监听
	 */
	public void setBannerListener(AderListener aListener) {
		mBannerListener = aListener;
	}

	/**
	 * 开启sdk服务 初始化系统信息及广告视图
	 * 
	 * @param sdkView
	 * @return 返回是否初始化成功
	 */
	@Override
	public Boolean startService(String appId, int width, int height,
			String mode, ViewGroup sdkView) {
		
		if (bannerServiceStatus == Service_Is_Stopped) {
			
			if (super.startService(appId, width, height, mode, sdkView)) {
				bannerServiceStatus = Service_Is_ReadyToConfig;

				/** 初始化广告视图 */
				adView = new AderView(mContext);
				adView.setUtilsListener(this);
				adView.setBackgroundColor(Color.TRANSPARENT);

				/** 把广告视图加到SDKView上 */
				sdkView.addView(adView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				sdkView.setVisibility(View.VISIBLE);

				/** 监听屏幕 */
				registerScreenObserver();
			} else {
				return false;
			}
		} else {
			AderLogUtils.d("bannerServiceStatus ===" + bannerServiceStatus);
		}
		return true;
	}

	/**
	 * 停止sdk服务
	 * 
	 */
	@Override
	public void stopService() {
		if (bannerServiceStatus != Service_Is_Stopped) {
			AderLogUtils.d("stopservice in");
			bannerServiceStatus = Service_Is_Stopped;
			if (adView != null) {
				//((ViewGroup)adView.getParent()).removeView(adView);
				adView.removeAllViews();
				adView = null;
			}
			resetService();
			mShowCount = 0;
			mBannerInstance = null;
			mContext = null;
			mBannerListener = null;
			super.stopService();
		}
	}

	/**
	 * 暂停sdk服务
	 * 
	 */
	@Override
	public void pauseService(boolean pause) {
		if (bannerServiceStatus == Service_Is_Stopped) {
			return;
		}
		if (bannerServiceStatus != Service_Is_Paused && pause) {
			AderLogUtils.i("pause");
			bannerServiceStatus = Service_Is_Paused;
			/** 暂停后台线程 */
			reportEvent(PAUSE_SERVICE_EVENT, null);
			mMainUIHandler.removeCallbacksAndMessages(null);
		} else if (bannerServiceStatus == Service_Is_Paused && !pause) {
			AderLogUtils.i("restart");
			bannerServiceStatus = Service_Is_Running;
			/** 恢复后台线程 */
			reportEvent(RESUME_SERVICE_EVENT, null);

			mMainUIHandler.removeCallbacksAndMessages(null);

			if (mConfigItem != null && mConfigItem.getGetAdFreq() != null) {
				mMainUIHandler.sendEmptyMessageDelayed(REQUEST_AD_TYPE,
						(Integer.parseInt(mConfigItem.getGetAdFreq())) * 1000);
				AderLogUtils.d("getadfreg", mConfigItem.getGetAdFreq());
			}
		}
	}

	/** AderViewUtilsListener接口方法 */
	@Override
	public void readyToGetConfig(int aWidth) {
		if (bannerServiceStatus == Service_Is_ReadyToConfig) {
			bannerServiceStatus = Service_Is_Running;

			AderLogUtils.d("");
			/** 重新设置广告的宽 */
			int width = (int) (Integer.valueOf(mAppInfo.mWidth) * mAppInfo.mDensity);
			int height = (int) (Integer.valueOf(mAppInfo.mHeight) * mAppInfo.mDensity);
			if (aWidth < width) {
				Log.e(AderLogUtils.APP_NAME,
						"您配置的AderSDKView的宽度小于调用接口startService中的宽度参数，有可能会导致广告显示不完全，敬请注意！");
				width = aWidth;
			}
			mAppInfo.mWidth = String
					.valueOf((int) ((float) width / mAppInfo.mDensity));
			mAppInfo.buildMapForConfig();
			adView.initBannerView(mContext, this, width,height);
			/** 请求配置信息 */
			requestConfig();
		}
	}

	@Override
	public void onDetachBannerAd() {
		if (bannerServiceStatus == Service_Is_Stopped) {
			//如果已经处理重置为初始状态，则不再处理
			return;
		}
		/** 恢复到初始状态 */
		bannerServiceStatus = Service_Is_Stopped;
		
		mBannerListener = null;
		/**取消事件监听和等待执行的任务*/
		if (mBackHandler!=null) {
			mBackHandler.removeCallbacksAndMessages(null);
		}
		if (mMainUIHandler!=null) {
			mMainUIHandler.removeCallbacksAndMessages(null);
		}
		/**注销屏幕亮暗监听*/
		unregisterScreenObserver();
		
	}
	
	/**
	 * 请求配置信息
	 * */
	@Override
	public void requestConfig() {
		if (bannerServiceStatus != Service_Is_Running) {
			AderLogUtils.i("sevice status not right:" + bannerServiceStatus);
			return;
		}
		/** 开启后台处理线程 */
		initBackHandler();

		super.requestConfig();
	}

	/**
	 * 请求配置失败
	 * 
	 * @param requestType
	 *            请求类型
	 * */
	@Override
	protected void onRequestConfigFailed(int requestType) {
		if(mBackHandler != null)
		{
			if (requestType == REQUEST_CONFIG_TYPE) {
				mTryCount++;
				if (mTryCount < mMaxTryCount) {
					mBackHandler.sendEmptyMessage(REQUEST_CONFIG_TYPE);
				} else {
					mBackHandler.sendEmptyMessageDelayed(REQUEST_CONFIG_TYPE,
							mTryWaitTime);
					mTryCount = 0;
				}
			}
		}
	}

	/**
	 * 请求单条广告
	 * 
	 */
	@Override
	public void requestAdInfo() {
		if (bannerServiceStatus != Service_Is_Running) {
			AderLogUtils.w("banner service is not running");
			return;
		}
		super.requestAdInfo();
	}

	/**
	 * app进入前台，进入后台
	 * 
	 */
	public void windowVisibilityChanged(int visibility, ViewGroup aderSDKView) {
		AderLogUtils.i("windowVisibilityChanged#########################"
				+ visibility);
		if (visibility == View.VISIBLE) {
			pauseService(false);
			isShow = true;
			if (bannerServiceStatus == Service_Is_Running) {
				long time = System.currentTimeMillis();
				long temp = (time - mStartTime) / SWITCH_TO_DAYS;
				if (temp >= 1) {
					/** 重置服务 */
					resetService();
					/** 重新请求配置 */
					requestConfig();
				} else {
					reportEvent(REPORT_START_TYPE, null);
				}
			}
		} else {
			AderLogUtils.i("window gone");
			if (aderSDKView.getParent() == null) {
				AderLogUtils.i("window gone and parent is null");
				stopService();
			} else {
				if (visibility == View.GONE&&bannerServiceStatus != Service_Is_Stopped){
					/** 汇报进入后台 */
					reportEvent(REPORT_TERMINATE_TYPE, null);
					/** 汇报进入前台 */
					pauseService(true);
				}
			}
			isShow = false;
		}
	}

	/**
	 * 屏幕开启后恢复sdk
	 * 
	 */
	@Override
	protected void doSomethingOnScreenOn() {
		AderLogUtils.i("view is show:" + isShow);
		if (isShow) 
		{
			if (bannerServiceStatus != Service_Is_Stopped) {
				/** 重启广告 */
				pauseService(false);
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
			if (bannerServiceStatus != Service_Is_Stopped) {
				/** 汇报进入后台 */
				reportEvent(REPORT_TERMINATE_TYPE, null);
				/** 暂停广告 */
				pauseService(true);
			}
		}
	}

	/**
	 * 广告载入成功
	 * 
	 */
	@Override
	protected void webViewAdLoadSuccess(Webview_Type webType) {
		AderLogUtils.d("webViewAdLoadSuccess");
		if (webType == Webview_Type.web_small_type) {
			if (bannerServiceStatus == Service_Is_Running) {
				/** 汇报有效展示 */
				reportEvent(REPORT_VALID_SHOW, null);
				/** 汇报加载时间 */
				loadTime = (System.currentTimeMillis() - startTime) / 1000;
				AderLogUtils.d("loadTime--->" + String.valueOf(loadTime));
				reportEvent(REPORT_LOAD_TIME, null);

				int animation = 0;
				if (ad_animation == 0) {
					Random r = new Random();
					animation = r.nextInt(4);
				} else {
					animation = ad_animation - 1;
				}
				adView.getWebView().webViewAnimation(animation);

				if (mConfigItem != null && mConfigItem.getGetAdFreq() != null) {
					/** 安排下次请求 */
					mMainUIHandler
							.sendEmptyMessageDelayed(REQUEST_AD_TYPE,
									(Integer.parseInt(mConfigItem
											.getGetAdFreq())) * 1000);
					AderLogUtils.d("getadfreg", mConfigItem.getGetAdFreq());

					mTryCount = 0;
					mShowCount++;
					if (mBannerListener != null) {
						mBannerListener.onReceiveAd(mShowCount);
					}
				}
			}
		}
	}

	/**
	 * 广告载入失败
	 * 
	 */
	@Override
	protected void webViewAdLoadFaild(Webview_Type webType) {
		AderLogUtils.w("广告请求失败");
		if (webType == Webview_Type.web_small_type) {
			AderLogUtils.d("webViewAdLoadFaild small");
			mTryCount++;
			if (bannerServiceStatus == Service_Is_Running) {
				if (mTryCount < mMaxTryCount) {
					/** 请求广告 */
					mMainUIHandler.sendEmptyMessage(REQUEST_AD_TYPE);
				} else {
					/** 暂停广告 */
					pauseService(true);

					/** 过一段时间后重新请求 */
					mMainUIHandler.sendEmptyMessageDelayed(
							RESUME_SERVICE_EVENT, mTryWaitTime);

					mTryCount = 0;
				}
			}
		}
		// else if (webType == Webview_Type.web_big_type) {
		// adBigWebView.setCloseButton();
		// }
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
		if (reason.equals("error")) {
			webViewAdLoadFaild(Webview_Type.web_small_type);
		}
		if (reason.equals("empty")) {
			hasNoErrorMsg("7");
		}
	}

	/** 收到错误信息 */
	@Override
	protected void didReceiveError(int errorCode, String errorMsg) {
		if (mBannerListener != null) {
			mBannerListener.onFailedToReceiveAd(errorCode, errorMsg);
		}
	}

}
