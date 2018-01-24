package com.rrgame.sdk.adutils;

import java.lang.ref.WeakReference;

import com.rrgame.sdk.service.AderBannerService;
import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * SDK主layout，入口layout
 * 
 */
public class AderSDKView extends RelativeLayout {

	private WeakReference<Activity> activityReference = null;
	private AderBannerService mBannerService;

	public AderSDKView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setVisibility(View.GONE);
	}

	public AderSDKView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setVisibility(View.GONE);
	}

	public AderSDKView(Context context) {
		super(context);
		this.setVisibility(View.GONE);
	}

	/**
	 * 开启sdk服务
	 * 
	 */
	public void startService(String appId, int width, int height, Boolean isTestMode,
			Activity activity) {
		AderLogUtils.d("startservice-->" + Thread.currentThread().getId());
		
		if (TextUtils.isEmpty(appId) || activity == null)
		{
			Log.e(AderLogUtils.APP_NAME, "启动广告服务失败，请检查传入参数是否正确！");
		}
		else
		{
			if (AderBannerService.serviceIsRuning())
			{
				Log.e(AderLogUtils.APP_NAME, "广告后台服务已经启动！");
			}
			else
			{
				if (activityReference == null)
				{
					activityReference = new WeakReference<Activity>(activity);
				}
				
				mBannerService = AderBannerService.getInstance(activityReference.get());
				String mMode;
				if (isTestMode) {
					mMode = "true";
				} else {
					mMode = "false";
				}

				mBannerService.startService(appId, width, height, mMode, this);
			}
			
		}
		
	}

	/**
	 * 停止sdk服务
	 * 
	 */
	public void stopService() {
		if (mBannerService != null) {
			mBannerService.stopService();
			this.setVisibility(View.GONE);
			mBannerService = null;
		}
		activityReference = null;
	}

	/**
	 * 设置监听者
	 */
	public void setListener(AderListener aListener) {
		if (mBannerService!=null) {
			mBannerService.setBannerListener(aListener);
		}
	}

	/**
	 * 屏幕开关回调
	 * 
	 */
	protected void onWindowVisibilityChanged(int visibility) {
		AderLogUtils.d("onWindowVisibilityChanged#########################"
				+ visibility);
		if (mBannerService != null) 
		{
			mBannerService.windowVisibilityChanged(visibility,AderSDKView.this);
		}
		
	}
}
