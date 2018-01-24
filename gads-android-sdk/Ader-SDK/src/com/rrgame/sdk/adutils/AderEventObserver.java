package com.rrgame.sdk.adutils;

import com.rrgame.sdk.adutils.AderPublicUtils.WebStatus;
import com.rrgame.sdk.adutils.AderPublicUtils.Webview_Type;
import com.rrgame.sdk.service.AderService;

import android.webkit.WebView;



/**
 * AD点击的事件观察者
 * 
 * @author com.rrgame 
 * @see ADEventListener,
 */
public class AderEventObserver implements AderEventListener {
	AderService mAdUtils;
	// 构造函数
	public AderEventObserver(Object adutil) {
		mAdUtils = (AderService) adutil;
	}

	/**
	 * 来自 ADEventListener 分发消息
	 */
	//@Override
	public void onEventOccured(AderEventArgs aEventArgs) {

		mAdUtils.getNextAction((String)aEventArgs.Data, (WebStatus)aEventArgs.Sender, (WebView)aEventArgs.MyView, (Webview_Type)aEventArgs.WebType);

	}

}
