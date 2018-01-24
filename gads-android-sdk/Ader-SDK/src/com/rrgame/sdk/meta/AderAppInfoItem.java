package com.rrgame.sdk.meta;

/**
 *app属性模块 
 * @author com.rrgame
 */
public class AderAppInfoItem {

	private String appName;  //app名称
	private String appBundle; //app包名
	private String processName;  //app进程名
	private int miniSDK; //最小版本号
	private int    versionCode;  //版本号

	
	public AderAppInfoItem(String appName,String appBundle,String processName,int miniSDK,int versionCode) {
		this.appName=appName;
		this.appBundle=appBundle;
		this.processName=processName;
		this.miniSDK=miniSDK;
		this.versionCode=versionCode;
	}


	public int getVersionCode() {
		return versionCode;
	}

	public String getappName() {
		return appName;
	}
	public String getprocessName() {
		return processName;
	}
	public String getappBundle() {
		return appBundle;
	}

	public int getMiniSDK() {
		return miniSDK;
	}
}
