package com.rrgame.sdk.meta;

/**
 * 
 * @author com.rrgame
 * 
 */
public class AderConfigItem {

	private String mCode; // 返回状态
	private String mDesc; // 错误码
	private String mTouchCount; // 超时次数
	private String mTouchTime; // 超时时间
	private String mTouchWaitTime; // 网络超时等待时间
	private String mValidShowTime; // 有效展示时间
	private String mGetAdFreq; // 请求广告频率

	private String mJsAddress; // 请求JavaScript地址
	private String mGetAdHost; // 请求广告地址的host
	private String mGetAdPath; // 请求广告地址的path
	private String mRepoAddress; // 各类汇报地址

	private String mGetad_set; // 获取app列表开关
	private String mGetad_time; // 获取时间戳
	private String mAd_animation; // 获取动画
	private String mAd_switch; // 广告开关
	private long   mLbs_time;    //lbs间隔时间
	private String mLbs_info; //lbs信息
	/**
	 * 获取lbs信息
	 */
	public String getLbs_info() {
		return mLbs_info;
	}

	/**
	 * 设置lbs信息
	 */
	public void setLbs_info(String mLbs_info) {
		this.mLbs_info = mLbs_info;
	}
	
	/**
	 * 获取lbs间隔时间
	 */
	public long getLbs_time() {
		return mLbs_time;
	}

	/**
	 * 设置lbs间隔时间
	 */
	public void setLbs_time(long mLbs_time) {
		this.mLbs_time = mLbs_time;
	}
	/**
	 * 获取广告开关
	 */
	public String getAd_switch() {
		return mAd_switch;
	}

	/**
	 * 设置广告开关
	 */
	public void setAd_switch(String ad_switch) {
		mAd_switch = ad_switch;
	}

	/**
	 * 获取动画
	 */
	public String getAd_animation() {
		return mAd_animation;
	}

	/**
	 * 设置动画
	 */
	public void setAd_animation(String animation) {
		mAd_animation = animation;
	}

	/**
	 * 获取时间戳
	 */
	public String getad_time() {
		return mGetad_time;
	}

	/**
	 * 设置时间戳
	 */
	public void setad_time(String time) {
		mGetad_time = time;
	}

	/**
	 * 获取app列表开关
	 */
	public String getad_set() {
		return mGetad_set;
	}

	/**
	 * 设置app列表开关
	 */
	public void setad_set(String set) {
		mGetad_set = set;
	}

	/**
	 * 获取返回状态
	 */
	public String getCode() {
		return mCode;
	}

	/**
	 * 设置返回状态
	 */
	public void setCode(String code) {
		mCode = code;
	}

	/**
	 * 获取超时时间
	 */
	public String getDesc() {
		return mDesc;
	}

	/**
	 * 设置超时时间
	 */
	public void setDesc(String desc) {
		mDesc = desc;
	}

	/**
	 * 获取超时次数
	 */
	public String getTouchCount() {
		return mTouchCount;
	}

	/**
	 * 设置超时次数
	 */
	public void setTouchCount(String touchCount) {
		mTouchCount = touchCount;
	}

	/**
	 * 获取超时时间
	 */
	public String getTouchTime() {
		return mTouchTime;
	}

	/**
	 * 设置超时时间
	 */
	public void setTouchTime(String touchTime) {
		mTouchTime = touchTime;
	}

	/**
	 * 获取网络超时等待时间
	 */
	public String getTouchWaitTime() {
		return mTouchWaitTime;
	}

	/**
	 * 设置网络超时等待时间
	 */
	public void setTouchWaitTime(String touchWaitTime) {
		mTouchWaitTime = touchWaitTime;
	}

	/**
	 * 获取有效展示时间
	 */
	public String getValidShowTime() {
		return mValidShowTime;
	}

	/**
	 * 设置有效展示时间
	 */
	public void setValidShowTime(String validShowTime) {
		mValidShowTime = validShowTime;
	}

	/**
	 * 获取请求广告频率
	 */
	public String getGetAdFreq() {
		return mGetAdFreq;
	}

	/**
	 * 设置请求广告频率
	 */
	public void setGetAdFreq(String getAdFreq) {
		mGetAdFreq = getAdFreq;
	}

	/**
	 * 获取请求JavaScript地址
	 */
	public String getJsAddress() {
		return mJsAddress;
	}

	/**
	 * 设置请求JavaScript地址
	 */
	public void setJsAddress(String jsAddress) {
		mJsAddress = jsAddress;
	}

	/**
	 * 获取请求广告地址的host
	 */
	public String getGetAdHost() {
		return mGetAdHost;
	}

	/**
	 * 设置请求广告地址的host
	 */
	public void setGetAdHost(String getAdHost) {
		mGetAdHost = getAdHost;
	}

	/**
	 * 获得请求广告地址的path
	 */
	public String getRepoAddress() {
		return mRepoAddress;
	}

	/**
	 * 设置请求广告地址的path
	 */
	public void setRepoAddresse(String repoAddress) {
		mRepoAddress = repoAddress;
	}

	/**
	 * 获取各类汇报地址
	 */
	public String getGetAdPath() {
		return mGetAdPath;
	}

	/**
	 * 设置各类汇报地址
	 */
	public void setGetAdPath(String getAdPath) {
		mGetAdPath = getAdPath;
	}
}
