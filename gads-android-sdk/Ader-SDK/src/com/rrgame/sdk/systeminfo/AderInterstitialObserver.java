package com.rrgame.sdk.systeminfo;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

public class AderInterstitialObserver {
	private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
	private AderScreenStateListener mScreenStateListener;
	private static Method mReflectScreenState;

	public AderInterstitialObserver(Context context) {
		mContext = context;
		mScreenReceiver = new ScreenBroadcastReceiver();
		try {
			mReflectScreenState = PowerManager.class.getMethod("isScreenOn",
					new Class[] {});
		} catch (NoSuchMethodException nsme) {
			// Log.d(TAG, "API < 7," + nsme);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * screen状态广播接收者
	 * 
	 */
	private class ScreenBroadcastReceiver extends BroadcastReceiver {
		private String action = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				mScreenStateListener.onScreenOn();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mScreenStateListener.onScreenOff();
			}
		}
	}

	/**
	 * 请求screen状态更新
	 * 
	 * @param listener
	 */
	public void requestScreenStateUpdate(AderScreenStateListener listener) {
		mScreenStateListener = listener;
		startScreenBroadcastReceiver();

		firstGetScreenState();
	}

	/**
	 * 第一次请求screen状态
	 */
	private void firstGetScreenState() {
		PowerManager manager = (PowerManager) mContext
				.getSystemService(Activity.POWER_SERVICE);
		if (isScreenOn(manager)) {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOn();
			}
		} else {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOff();
			}
		}
	}

	/**
	 * 停止screen状态更新
	 */
	public void stopScreenStateUpdate() {
		AderLogUtils.d("unregisterreceiver"+mContext);	
		if (mContext != null && mScreenReceiver != null)
		{ 
			try {
				mContext.unregisterReceiver(mScreenReceiver);
				AderLogUtils.e("rrgame","unregisterReceiver"+" "+mScreenReceiver+" "+mContext);	
			} catch (Exception e) {
				AderLogUtils.e(e);
			}
		}
		AderLogUtils.d("unregisterreceiverzoubudao"+mScreenReceiver);	
	}

	/**
	 * 启动screen状态广播接收器
	 */
	private void startScreenBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		AderLogUtils.e("registerreceiver"+mScreenReceiver);
		if (mContext != null && mScreenReceiver != null)
		{
			mContext.registerReceiver(mScreenReceiver, filter);
			AderLogUtils.e("rrgame","registerreceiver"+" "+mScreenReceiver+" "+mContext);
		}
	}

	/**
	 * screen是否打开状态
	 * 
	 * @param pm
	 * @return
	 */
	private static boolean isScreenOn(PowerManager pm) {
		boolean screenState;
		try {
			screenState = (Boolean) mReflectScreenState.invoke(pm); 
		} catch (Exception e) {
			screenState = false;
		}
		return screenState;
	}

	public interface AderScreenStateListener {
		public void onScreenOn();

		public void onScreenOff();
	}
}
