package com.rrgame.sdk;


/**
 * 插屏广告回调接口
 * */
public interface AderScreenListener {

	/**请求成功，可以显示*/
	public void onScreenReady();
	
	/**返回错误报告*/
	public void onScreenFailed(int errorCode, String errorMsg);
	
	/**显示*/
	public void onScreenPresent();
	
	/**移除*/
	public void onScreenDismiss();
	
	/**点击，将启动新应用时调用此语句。*/
	public void onScreenLeaveApplication();
	
}
