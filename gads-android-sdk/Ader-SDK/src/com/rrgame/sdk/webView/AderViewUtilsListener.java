package com.rrgame.sdk.webView;


public interface AderViewUtilsListener {

	/*
	 * 准备好去获取配置信息
	 */
	public void readyToGetConfig(int aWidth);
	
	/**广告视图从window移除*/
	public void onDetachBannerAd();
	
}
