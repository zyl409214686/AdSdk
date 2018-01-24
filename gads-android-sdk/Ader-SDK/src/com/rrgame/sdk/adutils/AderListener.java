package com.rrgame.sdk.adutils;

/**
 * 广告的事件监听接口
 * 
 * @author com.renren
 */

public interface AderListener {

	// 监听事件发生
	//收到广告
	public void onReceiveAd(int count);
	//获取广告失败
    public void onFailedToReceiveAd(int errorCode , String errorMsg);
    
}
