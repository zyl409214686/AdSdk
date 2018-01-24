package com.rrgame.sdk.service;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rrgame.sdk.adutils.AderEventObserver;
import com.rrgame.sdk.adutils.AderPublicUtils;
import com.rrgame.sdk.adutils.AderPublicUtils.WebStatus;
import com.rrgame.sdk.adutils.AderPublicUtils.Webview_Type;
import com.rrgame.sdk.download.AderDownloadItem;
import com.rrgame.sdk.download.AderDownloadService;
import com.rrgame.sdk.meta.AderConfigItem;
import com.rrgame.sdk.network.AderConfigdataXML;
import com.rrgame.sdk.network.AderReportDataXml;
import com.rrgame.sdk.network.AderXMLDataNetwork;
import com.rrgame.sdk.reportinfo.manager.LBSManager;
import com.rrgame.sdk.systeminfo.AderAppInfo;
import com.rrgame.sdk.systeminfo.AderBase64;
import com.rrgame.sdk.systeminfo.AderInterstitialObserver;
import com.rrgame.sdk.systeminfo.AderInterstitialObserver.AderScreenStateListener;
import com.rrgame.sdk.systeminfo.AderLogUtils;
import com.rrgame.sdk.systeminfo.AderMD5;
import com.rrgame.sdk.webView.AderBigWebView;
import com.rrgame.sdk.webView.AderView;

public class AderService implements Callback {
	/** 请求类型 */
	/** 请求配置信息 */
	protected static final int REQUEST_CONFIG_TYPE = 0;
	/** 请求广告 */
	protected static final int REQUEST_AD_TYPE = 1;

	/** 汇报类型 */
	/** 有效展示 */
	protected static final int REPORT_VALID_SHOW = 1;
	/** 有效点击 */
	protected static final int REPORT_VALID_CLICK = 2;
	/** 加载时间 */
	protected static final int REPORT_LOAD_TIME = 3;
	/** 停止服务 */
	protected static final int STOP_SERVICE_EVENT = 4;
	/** 暂停服务 */
	protected static final int PAUSE_SERVICE_EVENT = 5;
	/** 重启服务 */
	protected static final int RESUME_SERVICE_EVENT = 6;
	/** 进入前台 */
	protected static final int REPORT_START_TYPE = 9;
	/** 进入后台 */
	protected static final int REPORT_TERMINATE_TYPE = 10;
	/** 应用列表 */
	protected static final int REPORT_APPS_TYPE = 110;
	/** 汇报参数 */
//	protected static final int REPOR = 111;

	/** 请求配置文件地址，初始化时设定 */
	private final String configHead;

	/** 上下文 */
	protected Context mContext;

	/** 广告视图 */
	protected AderView adView;
	/** 大webview */
	protected AderBigWebView adBigWebView;
	/** Session ID */
	private String mSsnId = "";
	/** 广告ID */
	private String mAdId = "";

	/** 网络请求工具类 */
	private AderXMLDataNetwork mNetworkUtil;
	/** 获取到的配置信息 */
	protected AderConfigItem mConfigItem;
	/** 应用信息 */
	protected AderAppInfo mAppInfo;

	/** 请求配置或广告失败的超时次数 */
	protected int mTryCount = 0;
	/** 超时请求次数 */
	protected int mMaxTryCount = 3;
	/** 超过请求次数后重新再请求的等待时间 */
	protected int mTryWaitTime = 60000;

	/** 广告加载时间 */
	protected long loadTime;
	/** 请求一条广告的开始时间 */
	protected long startTime;

	/** 广告显示动画，有服务器传值过来 */
	protected int ad_animation = 0;

	/** 点击广告后的响应类型 */
	private String mClickResult = "";

	/**
	 * 下载有关
	 */
	private File theCachePathFile;
	/** 保存下载应用的文件路径 */
	private File thedownloadPath;

	/**
	 * 请求配置的时间 Banner广告在请求配置1天以后需要重新请求配置
	 * */
	protected long mStartTime = 0;
	/** 请求配置的间隔时间，现在为一天 */
	protected static final long SWITCH_TO_DAYS = 86400000;

	/** 后台Hanlder和Handler线程 */
	protected Handler mBackHandler;
	private HandlerThread mBackHandlerThread;
	/** 主线程处理Handler */
	protected Handler mMainUIHandler;
	/** 屏幕亮暗监听 */
	private AderInterstitialObserver mScreenObserver = null;
	protected Boolean isShow = true;

	/** 后台线程序数 */
	private static final AtomicInteger mBackThreadCount = new AtomicInteger(1);
	
	private int MAX_LOADTIME = 300;


	/**显示广告的dialog*/	 
	Dialog mDialog;
    
    /**位置管理器实例*/
	protected AderService(Context context, String configString) {
		AderLogUtils.d("adutilsnew");
		mContext = context;
		configHead = configString;
		/** 主线程Handler */
		mMainUIHandler = new Handler(new AderMainUIHandlerCallback());

		/** 网络请求实例 */
		mNetworkUtil = new AderXMLDataNetwork(mContext);
	}

	/**
	 * 开启sdk服务 初始化系统信息及广告视图
	 * 
	 * @param sdkView
	 * 
	 */
	protected Boolean startService(String appId, int width, int height,
			String mode, ViewGroup sdkView) {
		/**
		 * 先判断是否添加了用户权限 没有添加则返回
		 * */
		if (!checkUserPermissions()) {
			Log.e(AderLogUtils.APP_NAME, "请添加相关权限");
			stopService();
			return false;
		}
		// getAdSize(aderAdSize);
		// 初始化系统信息
		mAppInfo = new AderAppInfo();
		mAppInfo.mAppId = appId;
		mAppInfo.mWidth = String.valueOf(width);
		mAppInfo.mHeight = String.valueOf(height);
		mAppInfo.mAdMode = mode;
		mAppInfo.init((Activity) mContext);
		return true;
	}
	
	/**注册屏幕亮暗监听*/
	protected void registerScreenObserver(){
		/** 注册屏幕亮暗监听 */
		if (mScreenObserver == null) {
			AderLogUtils.e("requestScreenStateUpdate#####");
			mScreenObserver = new AderInterstitialObserver(mContext);
			mScreenObserver
					.requestScreenStateUpdate(new AderScreenStateListener() {
						public void onScreenOn() {
							doSomethingOnScreenOn();
						}

						public void onScreenOff() {
							doSomethingOnScreenOff();
						}
					});
		}
	}
	
	/**注销屏幕亮暗监听*/
	protected void unregisterScreenObserver(){
		if (mScreenObserver != null) {
			mScreenObserver.stopScreenStateUpdate();
		}
		mScreenObserver = null;
	}

	/**
	 * 重新设置服务各参数默认值
	 * */
	protected void resetService() {
		AderLogUtils.i("resetService");
		if (mBackHandler != null) {
			reportEvent(STOP_SERVICE_EVENT, null);
		}
		if (mMainUIHandler != null) {
			mMainUIHandler.removeCallbacksAndMessages(null);
		}
		mTryCount = 0;
		mMaxTryCount = 3;
		mTryWaitTime = 60000;
		ad_animation = 0;
		mConfigItem = null;
	}

	/**
	 * 暂停sdk服务
	 * 
	 */
	protected void pauseService(boolean pause) {
		// 由子类实现，目前只有Banner广告有暂停逻辑
	}

	/**
	 * 停止sdk服务
	 * 
	 */
	public void stopService() {
		/**注销屏幕亮暗监听*/
		unregisterScreenObserver();
	}

	/**
	 * 后台线程上报事件
	 * 
	 * @param reportType
	 *            汇报类型
	 * 
	 * @param object
	 *            附件数据
	 * 
	 * */
	protected void reportEvent(int reportType, Object object) {
		AderLogUtils.i("report type:"+reportType);
		if (mBackHandler == null) {
			AderLogUtils.i("mBackHandler already destory");
			return;
		}
		switch (reportType) {
		case REPORT_VALID_SHOW: {
			AderLogUtils.d("validshow-->" + Thread.currentThread().getId());
			if (mConfigItem != null && mConfigItem.getValidShowTime() != null) {
				mBackHandler
						.sendEmptyMessageDelayed(
								REPORT_VALID_SHOW,
								Integer.parseInt(mConfigItem.getValidShowTime()) * 1000);
			}
		}
			break;
//		case REPOR:
//			Message repot_message = mBackHandler.obtainMessage(reportType,
//					object);
//			mBackHandler.sendMessage(repot_message);
//		break;
		case REPORT_VALID_CLICK:
			Message click_message = mBackHandler.obtainMessage(reportType,
					object);
			mBackHandler.sendMessage(click_message);
		break;
		case REPORT_START_TYPE:
		case REPORT_TERMINATE_TYPE:
		case REPORT_LOAD_TIME:
		case STOP_SERVICE_EVENT:
		case PAUSE_SERVICE_EVENT: {
			mBackHandler.sendEmptyMessage(reportType);
		}
			break;
		case RESUME_SERVICE_EVENT: {
			AderLogUtils.i("RESUME_SERVICE_EVENT");
			// 重启后台线程
			synchronized (mBackHandler) {
				mBackHandler.notify();
			}
		}
			break;
		case REPORT_APPS_TYPE: {
			Message message = mBackHandler.obtainMessage(REPORT_APPS_TYPE,
					object);
			mBackHandler.sendMessage(message);
		}
			break;
		default:
			break;
		}
	}

	/**
	 * 后台线程信息处理
	 * 
	 */
	public boolean handleMessage(Message msg) {
		int reportType = msg.what;
		AderLogUtils.i("reportType:"+reportType);
		switch (reportType) {
		case REQUEST_CONFIG_TYPE: {
			try {  
				AderLogUtils.d("handler-->" + Thread.currentThread().getId());
				AderLogUtils.d("tryCount", String.valueOf(mTryCount));
				String configUrl = buildUrl(configHead);
				configUrl = configUrl + "&aderTimestamp="+System.currentTimeMillis();
				AderLogUtils.d(configUrl);
				// configUrl =
				// "http://10.22.225.66:8083/confg/init?uuid1=f503af738805427aafeabed3493988df&app_id=9180e7310efe4db597e412d19f0b12e0&sdk_type=1&scrn_w=0&bndl_id=rrgame.GAds-SDK&dev_mode=true&user_agt=APPLE&os=IOS5.0&uuid2=a5aa66edfad91f5a78414b89850b2343&sdk_ver=1.0.0&net_oper=unknown&ad_h=50&ad_w=320&esc_prison=false&scrn_h=0&app_name=GAds-SDK&ter_type=x86_64&acc_point=WIFI&landscape=true";
				InputStream in = mNetworkUtil.getHttpStream(configUrl);
				AderConfigdataXML mConfigdataXML = new AderConfigdataXML();
				mConfigItem = mConfigdataXML.getConfigData(in);
				mMainUIHandler.sendEmptyMessage(REQUEST_CONFIG_TYPE);
				//获取到config后开启定位服务
				LBSManager.getInstance(mContext).sendlocationMsg(mConfigItem.getLbs_time());
			} catch (Exception e) {
				AderLogUtils.e(e);
				/**由主线程去进行处理*/
				mMainUIHandler.sendEmptyMessage(REQUEST_CONFIG_TYPE);
			}
		}
			break;
		case REPORT_VALID_SHOW:
		case REPORT_VALID_CLICK:
		case REPORT_START_TYPE:
		case REPORT_TERMINATE_TYPE:
		case REPORT_LOAD_TIME: {
			try {
				AderLogUtils.i("上报类型：" + reportType);
				if (mConfigItem == null
						|| TextUtils.isEmpty(mConfigItem.getRepoAddress())) {
					break;
				}

				String reportUrl = appendReportUrl(
						mConfigItem.getRepoAddress(), reportType);
				reportUrl = reportUrl + "&aderTimestamp="+System.currentTimeMillis();
				//增加点击坐标字段
				if(reportType == REPORT_VALID_CLICK){
					HashMap<String, String> map = (HashMap<String, String>)msg.obj;
					if(map != null){
						String position = map.get("xy");
						position = AderBase64.encode(position);
						if(!TextUtils.isEmpty(position)){
							reportUrl = reportUrl + "&xy="+URLEncoder.encode(position);
						}
					}
//					reportUrl = reportUrl + AderParameterDevelop.getInstance().buildPClickdata();
					
				}
//				reportUrl = reportUrl + AderParameterDevelop.getInstance().buildPdata();
				InputStream is = mNetworkUtil.getHttpStream(reportUrl);
				// 测试上报是否成功
				AderReportDataXml reportdataXML = new AderReportDataXml();
				AderConfigItem config = reportdataXML.getConfigData(is);
				if (config != null) {
					AderLogUtils.d("上报类型：" + reportType + "上报结果："
							+ config.getCode());
				}
			} catch (Exception e) {
				AderLogUtils.e(e);
			}
		}
			break;
		case REPORT_APPS_TYPE: {
			if (mConfigItem == null) {
				AderLogUtils.w("REPORT_APPS_TYPE: mConfigItem is null");
				break;
			}
			String url = mConfigItem.getRepoAddress();
			if (url != null && url.length() > 0) {
				String strList = (String) msg.obj;

				// String tid = AderAppInfo.mConfigmap.get("tid");
				String tid = "tid";
				String uuid = mAppInfo.mConfigmap.get("uuid1");
				long time = System.currentTimeMillis();

				HashMap<String, String> map = new HashMap<String, String>();

				map.put("m", tid);
				map.put("u", uuid);
				map.put("d", strList);
				map.put("t", String.valueOf(time));

				InputStream is = mNetworkUtil.getHttpStreamFromPost(url, map);
				// 测试上报是否成功
				AderReportDataXml reportdataXML = new AderReportDataXml();
				AderConfigItem config = reportdataXML.getConfigData(is);
				if (config != null) {
					AderLogUtils.d("上报app结果：" + config.getCode());
				}
			}
		}
			break;
		case STOP_SERVICE_EVENT: {
			AderLogUtils.i("STOP_SERVICE_EVENT");
			if (mBackHandler != null) {
				mBackHandler.removeCallbacksAndMessages(null);
			}
			if (mBackHandlerThread != null) {
				mBackHandlerThread.getLooper().quit();
			}
			mBackHandler = null;
			mBackHandlerThread = null;
		}
			break;
		case PAUSE_SERVICE_EVENT: {
			AderLogUtils.i("PAUSE_SERVICE_EVENT");
			// 暂停后台线程
			synchronized (mBackHandler) {
				try {
					AderLogUtils.d("sync_ThreadManager", "hold_Handeler");
					mBackHandler.wait();
					AderLogUtils.d("sync_ThreadManager", "wait_Handeler");
				} catch (InterruptedException e) {
					AderLogUtils.e(e);
				}
			}
		}
			break;
//		case REPOR:
//			try {
//				HashMap<String, String> map = (HashMap<String, String>)msg.obj;
//				
//				String reportUrl = AderParameterDevelop.getInstance().getReportUrl(mAppInfo.mConfigmap, map);
//
//				InputStream is = mNetworkUtil.getHttpStream(reportUrl);
//				// 测试上报是否成功
//				AderReportDataXml reportdataXML = new AderReportDataXml();
//				AderConfigItem config = reportdataXML.getConfigData(is);
//				if (config != null) {
//					AderLogUtils.d("上报类型：" + reportType + "上报结果："
//							+ config.getCode());
//				}
//			} catch (Exception e) {
//				AderLogUtils.e(e);
//			}
//			
//		break;
		default:
			break;
		}
		return false;
	}

	/**
	 * 主线程Handler信息处理类
	 * */
	private class AderMainUIHandlerCallback implements Callback {

		@Override
		public boolean handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_CONFIG_TYPE: {
				AderLogUtils.d("Config_Success in");
				if (mConfigItem == null) {
					AderLogUtils.d("Config_Success in mConfigItem == break");
					onRequestConfigFailed(REQUEST_CONFIG_TYPE);
				} else {
					if (mConfigItem.getCode() != null) {
						if (hasNoErrorMsg(mConfigItem.getCode())) {
							String ad_switch = mConfigItem.getAd_switch();
							if (ad_switch == null || !ad_switch.equals("1")) {
								hasNoErrorMsg("102");
								return false;
							}
							/** 设置超时次数 */
							if (mConfigItem.getTouchCount() != null) {
								mMaxTryCount = Integer.parseInt(mConfigItem
										.getTouchCount());
								AderLogUtils.d("mtouchcount",
										mConfigItem.getTouchCount());
							}
							/** 设置超时时间 */
							if (mConfigItem.getTouchTime() != null) {
								mNetworkUtil.TIME_OUT = Integer
										.parseInt(mConfigItem.getTouchTime()) * 1000;
							}
							/** 设置超时等待时间 */
							if (mConfigItem.getTouchWaitTime() != null) {
								mTryWaitTime = Integer.parseInt(mConfigItem
										.getTouchWaitTime()) * 1000;
							}
							/** android有5种动画 */
							if (mConfigItem.getAd_animation() != null
									&& !mConfigItem.getAd_animation()
											.equals("")
									&& Integer.parseInt(mConfigItem
											.getAd_animation()) > 0
									&& Integer.parseInt(mConfigItem
											.getAd_animation()) < 5) {
								ad_animation = Integer.parseInt(mConfigItem
										.getAd_animation());
							} else {
								ad_animation = 0;
							}
							/** 检查配置协议关键字段是否有效 */
							if (mConfigItem.getGetAdHost() != null
									&& mConfigItem.getGetAdPath() != null
									&& mConfigItem.getJsAddress() != null) {
								AderLogUtils.d("config success");
								requestAdInfo();
							} else {
								onRequestConfigFailed(REQUEST_CONFIG_TYPE);
							}
						}
					} else {
						onRequestConfigFailed(REQUEST_CONFIG_TYPE);
					}
				}
			}
				break;
			case REQUEST_AD_TYPE: {
				requestAdInfo();
			}
				break;
			case RESUME_SERVICE_EVENT: {
				// 重新请求
				pauseService(false);
			}
				break;
			default:
				break;
			}
			return false;
		}
	}

	/**
	 * 开启后台处理线程
	 **/
	protected void initBackHandler(){
		
		mBackHandlerThread = new HandlerThread("AdBackHandler"
				+ mBackThreadCount.getAndIncrement(),
				Process.THREAD_PRIORITY_BACKGROUND);
		/** 后台线程序数太大则重新设置 */
		if (mBackThreadCount.get() >= 10000) {
			mBackThreadCount.set(1);
		}
		mBackHandlerThread.start();
		mBackHandler = new Handler(mBackHandlerThread.getLooper(), this);
	}
	
	/**
	 * 首次进入从服务器获取配置信息
	 * 
	 */
	protected void requestConfig() {
		AderLogUtils.d("");
		mBackHandler.sendEmptyMessage(REQUEST_CONFIG_TYPE);
		mStartTime = System.currentTimeMillis();
	}

	/**
	 * 请求配置失败
	 * 
	 * @param requestType
	 *            请求类型
	 * */
	protected void onRequestConfigFailed(int requestType) {
		// 由子类实现
	}

	/**
	 * 请求单条广告
	 * 
	 */
	public void requestAdInfo() {
		AderLogUtils.d("requestAdInfo");
		if (mAppInfo.mConfigmap == null) {
			AderLogUtils.w("mAppInfo.mConfigmap is null");
			return;
		}
		/** 检查配置信息是否正确 */
		if (mConfigItem != null && mConfigItem.getGetAdHost() != null
				&& mConfigItem.getGetAdPath() != null
				&& mConfigItem.getJsAddress() != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			Iterator<Entry<String, String>> entrySetIterator = mAppInfo.mConfigmap
					.entrySet().iterator();
			while (entrySetIterator.hasNext()) {
				Entry<String, String> entry = entrySetIterator.next();
				map.put(entry.getKey(), entry.getValue());
			}
			map.put("_host", mConfigItem.getGetAdHost());
			map.put("_path", mConfigItem.getGetAdPath());
			if(LBSManager.getInstance(mContext).getInfo()!=null)
				map.put("info", LBSManager.getInstance(mContext).getInfo());
			else
				map.put("info", "");
			/** 构建广告html */
			map.put("f", AderAppInfo.channle_packageName);
			String strHtml = buildAdHtml(map);
			/** 请求广告 */
			if (adView != null) {
				adView.loadUrl(strHtml);
			}
			AderLogUtils.d(strHtml);
		} else {
			AderLogUtils
					.w("mConfigItem is null or getad_host getad_path is null");
		}
	}

	/**
	 * 解析链接，获取下一步动作
	 * 
	 */
	public void getNextAction(String data, WebStatus type, WebView webview,
			Webview_Type webType) {
		AderLogUtils.d(data);
		if (webview == null) {
			return;
		}
		switch (type) {
		case GETAMSG_WEBVIEW_JS:
			AderLogUtils.d("GETAMSG_WEBVIEW_JS");
			handleMsg(data, webview);
			break;
		case SUCCESS_WEBVIEW_JS:
			AderLogUtils.d("GETAMSG_WEBVIEW_JS");
			webViewAdLoadSuccess(webType);
			break;
		case FAILED_WEBVIEW_JS:
			AderLogUtils.d("FAILED_WEBVIEW_JS");
			webViewAdLoadFaild(webType);
			break;
		case CLOSE_WEBVIEW_JS:
			closeBigWebView();
			break;
		default:
			break;
		}
	}
	
	
	/** 程序内打开webview */
	protected void openWebView(String url, int viewHeight)
	{
		if (url != null && viewHeight >0) 
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
			((Activity) mContext).addContentView(adBigWebView,
					layoutParams);
			
		}
	}

	/**
	 * 根据类型进行事件响应
	 * 
	 */
	private void handleMsg(String data, WebView webview) {
		data = data.replaceFirst("amsg", "http");
		try {
			URL url = new URL(data);
			HashMap<String, String> map = buildDicForWebView(url.getQuery());

			AderLogUtils.d("System.out", url.getQuery());
			AderLogUtils.d("System.out", data);
			String path = url.getPath();
			if (path.equals("/loadAd")) {
				String strUrl = map.get("url");
				AderLogUtils.d("/loadAd", strUrl);
				if (strUrl != null && webview != null) {
					startTime = System.currentTimeMillis();
					webview.loadUrl(strUrl);
				}
			} else if (path.equals("/reportClick")) {
				mClickResult = map.get("preClickResult");
				AderLogUtils.d("reportClick---->mClickResult", mClickResult);
				if (mClickResult == null || mClickResult.equals("")) {
					mClickResult = "2.2.1";
				}
//				AderParameterDevelop.getInstance().updatePclickMap(map);
				/** 上报有效点击 */
				reportEvent(REPORT_VALID_CLICK, map);
			} else if (path.equals("/openBrowser")) {
				// 打开浏览器
				AderPublicUtils.openUrl(mContext, map);
			} else if (path.equals("/openStore")) {
				mClickResult = map.get("preClickResult");
				AderLogUtils.d("openStore---->mClickResult", mClickResult);
				if (mClickResult == null || mClickResult.equals("")) {
					mClickResult = "2.2.1";
				}
				if (mClickResult.equals("2.2.3")) {
					String downloadUrl = map.get("url");
					String packageName = map.get("packageName");
					if (downloadUrl != null && packageName != null) {
						downLoadAPK(downloadUrl, packageName);
					}
				} else if (mClickResult.equals("2.2.1")) {
					AderPublicUtils.openUrl(mContext, map);
				}
			} else if (path.equals("/call")) {
				String phone = map.get("phone");
				if (phone != null) {
					AderPublicUtils.phoneCall(mContext, phone);
				}
			} else if (path.equals("/sms")) {
				String phone = map.get("phone");
				String msg = map.get("msg");
				if (phone != null && msg != null) {
					AderPublicUtils.sendSMS(mContext, phone, msg);
				}
			} else if (path.equals("/email")) {
				AderPublicUtils.openEmail(mContext, map);
			} else if (path.equals("/openMap")) {
				String latitude = map.get("latitude");
				String longitude = map.get("longitude");
				if (latitude != null && longitude != null) {
					AderPublicUtils.openMap(mContext, latitude, longitude);
				}
			} else if (path.equals("/calendar")) {
				AderPublicUtils.addCalendar(mContext, map);
			} else if (path.equals("/openWebView")) {
				AderLogUtils.d("openWebView");
				String strUrl = map.get("url");
				String viewHeight = map.get("viewHeight");
				if(strUrl !=null && viewHeight!= null)
				{
					AderLogUtils.d("viewHeight---->" + viewHeight);
					openWebView(strUrl,  Integer.parseInt(viewHeight));
				}
			} else if (path.equals("/notify")) {
				webview.clearCache(true);
				String nType = map.get("nt");
				if (nType.equals("adload")) {
					String stage = map.get("stage");
					if (stage.equals("loaded")) {
						mClickResult = map.get("preClickResult");
						if (mClickResult == null || mClickResult.equals("")) {
							mClickResult = "2.2.1";
						}
						if (map.get("ssnId") != null && map.get("aId") != null) {
							mSsnId = map.get("ssnId");
							mAdId = map.get("aId");
						}
						
//						AderParameterDevelop.getInstance().updatePloadedMap(map);

						webViewAdLoadSuccess(Webview_Type.web_small_type);
					}
				} else if (nType.equals("getadfail")) {
					String reason = map.get("reason");
					AderLogUtils.w("getAdFailed reason:" + reason + "#desc:"
							+ map.get("desc"));
					getAdFailed(reason);
				}
			}
			//指定地址汇报参数
//			else if (path.equals("/repo")) {
//				reportEvent(REPOR, map);
//			}
		} catch (MalformedURLException e) {
			AderLogUtils.e(e);
		}
	}

	/**
	 * 处理错误信息
	 * 
	 * */
	public Boolean hasNoErrorMsg(String errorCode) {
		int iErrorCode = Integer.parseInt(errorCode);
		if (iErrorCode == 0) {
			return true;
		}
		String msgStr = null;
		switch (iErrorCode) {
		case 1:// 参数错误
		{
			msgStr = "参数错误";
		}
			break;
		case 2:// 服务端错误
		{
			msgStr = "服务端错误";
		}
			break;
		case 3:// 应用被冻结
		{
			msgStr = "应用被冻结";
		}
			break;
		case 4:// 无合适广告
		{
			msgStr = "无合适广告";
		}
			break;
		case 5: // 应用账户不存在
		{
			msgStr = "应用账户不存在";
		}
			break;
		case 6: // 频繁请求
		{
			msgStr = "频繁请求";
		}
			break;
		case 7: // 广告为空
		{
			msgStr = "广告为空";
		}
			break;
		case 8: // SDK禁用
		{
			msgStr = "其他错误";
		}
			break;
		case 101: // 网络请求失败
		{
			msgStr = "网络请求失败";
		}
			break;
		case 102: // 广告关闭
		{
			msgStr = "广告关闭";
		}
			break;
		default:
			break;
		}
		if (msgStr != null) {
			didReceiveError(iErrorCode, msgStr);
		} else {
			didReceiveError(8, "其他错误");
		}
		return false;
	}

	/** 收到错误信息 */
	protected void didReceiveError(int errorCode, String errorMsg) {
		// 由子类实现
	}

	/**
	 * 广告载入成功
	 * 
	 */
	protected void webViewAdLoadSuccess(Webview_Type webType) {
		// 由子类实现
	}

	/**
	 * 广告载入失败
	 * 
	 */
	protected void webViewAdLoadFaild(Webview_Type webType) {
		// 由子类实现
	}

	/**
	 * 广告载入失败
	 * 
	 * @param reason
	 *            失败原因
	 */
	protected void getAdFailed(String reason) {
		// 由子类实现
	}

	/**
	 * 构建单条广告请求的html
	 * 
	 */
	private String buildAdHtml(HashMap<String, String> map) {
		AderLogUtils.d("buildAdHtml");
		String strHtml = "<html><head><script type=\"text/javascript\" src=\""
				+ mConfigItem.getJsAddress()
				+ "\"></script><script type=\"text/javascript\">Build_ADURL({";
		Iterator<Entry<String, String>> entrySetIterator = map.entrySet()
				.iterator();
		while (entrySetIterator.hasNext()) {
			Entry<String, String> entry = entrySetIterator.next();
			strHtml = strHtml + "\"" + entry.getKey() + "\":\""
					+ entry.getValue() + "\",";
		}

		strHtml = strHtml.substring(0, strHtml.length() - 1);
		strHtml = strHtml + "});</script></head><body></body></html>";
		return strHtml;
	}

	/**
	 * 将webview和js通信的url.query解析成HashMap
	 * 
	 */

	private static HashMap<String, String> buildDicForWebView(String str) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] array = str.split("&");
		for (int i = 0; i < array.length; i++) {
			String[] arrayTemp = array[i].split("=");
			if (arrayTemp.length > 1) {
				try {
					map.put(arrayTemp[0],
							java.net.URLDecoder.decode(arrayTemp[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}
		}
		return map;
	}

	/**
	 * 关闭大webview
	 * 
	 */
	protected void closeBigWebView() {
		AderLogUtils.d("closeBigWebView");
		if (adBigWebView != null) {
			adBigWebView.removeAllViews();
			adBigWebView.setVisibility(View.GONE);
			adBigWebView = null;
		}
	}

	/**
	 * 为数据请求构建url
	 * 
	 */
	private String buildUrl(String address) {

		AderLogUtils.d("buildUrl");
		String strUrl = address + "?";
		if (mAppInfo.mConfigmap == null) {
			return strUrl;
		}
		Iterator<Entry<String, String>> entrySetIterator = mAppInfo.mConfigmap
				.entrySet().iterator();
		while (entrySetIterator.hasNext()) {
			Entry<String, String> entry = entrySetIterator.next();
			strUrl = strUrl + entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue()) + "&";
		}

		strUrl = strUrl.substring(0, strUrl.length() - 1);
		return strUrl;
	}

	/**
	 * 拼装汇报请求连接
	 * 
	 */
	private String appendReportUrl(String address, int type) {
		address = buildUrl(address);
		StringBuilder localStringBuilder;

		String strResult = "";
		if (mClickResult != null && !mClickResult.equals("")) {
			strResult = mClickResult;
		}

		switch (type) {
		case REPORT_VALID_SHOW:
			localStringBuilder = new StringBuilder(address).append("&ad_id=")
					.append(URLEncoder.encode(mAdId)).append("&repo_type=")
					.append("2").append("&ssn_id=")
					.append(URLEncoder.encode(mSsnId))
					.append("&pre_click_result=")
					.append(URLEncoder.encode(strResult))
					.append("&f=").append(URLEncoder.encode(AderAppInfo.channle_packageName));
			address = localStringBuilder.toString();
			AderLogUtils.d(address);
			break;
		case REPORT_VALID_CLICK:
			localStringBuilder = new StringBuilder(address).append("&ad_id=")
					.append(URLEncoder.encode(mAdId)).append("&repo_type=")
					.append("3").append("&ssn_id=")
					.append(URLEncoder.encode(mSsnId)).append("&click_result=")
					.append(URLEncoder.encode(strResult))
					.append("&f=").append(URLEncoder.encode(AderAppInfo.channle_packageName));
			address = localStringBuilder.toString();
			break;
		case REPORT_LOAD_TIME:
			long time = loadTime>MAX_LOADTIME?MAX_LOADTIME:loadTime;
			localStringBuilder = new StringBuilder(address).append("&ad_id=")
					.append(URLEncoder.encode(mAdId)).append("&repo_type=")
					.append("8").append("&ssn_id=")
					.append(URLEncoder.encode(mSsnId)).append("&load_time=")
					.append(URLEncoder.encode(String.valueOf(time)))
					.append("&pre_click_result=")
					.append(URLEncoder.encode(strResult))
					.append("&f=").append(URLEncoder.encode(AderAppInfo.channle_packageName));
			address = localStringBuilder.toString();
			break;
		case REPORT_START_TYPE:
			localStringBuilder = new StringBuilder(address).append("&ad_id=")
					.append(URLEncoder.encode(mAdId)).append("&repo_type=")
					.append("9").append("&ssn_id=")
					.append(URLEncoder.encode(mSsnId));
			address = localStringBuilder.toString();
			break;
		case REPORT_TERMINATE_TYPE:
			localStringBuilder = new StringBuilder(address).append("&ad_id=")
					.append(URLEncoder.encode(mAdId)).append("&repo_type=")
					.append("10").append("&ssn_id=")
					.append(URLEncoder.encode(mSsnId));
			address = localStringBuilder.toString();
			break;
		default:
			break;
		}
		return address;
	}

	/**
	 * 获取app信息的设置并汇报
	 * 
	 */
//	protected void getInfoWithSet() {
//		if (mConfigItem.getad_set() != null
//				&& mConfigItem.getad_set().equals("1")) {
//			// 获取SharedPreferences对象
//			SharedPreferences sp = mContext.getSharedPreferences("Setting_Msg", 0);
//
//			String spTime = sp.getString("Setting_time", "");
//			if (spTime == null
//					|| spTime.equals("")
//					|| ((mConfigItem.getad_time() != null) && Long
//							.parseLong(mConfigItem.getad_time()) > Long
//							.parseLong(spTime))) {
//
//				// 存入数据
//				Editor editor = sp.edit();
//				editor.putString("Setting_time", mConfigItem.getad_time());
//				editor.commit();
//
//				new Thread(new Runnable() {
//					public void run() {
//						StringBuilder localStringBuilder = new StringBuilder();
//						AderAppInfo app = new AderAppInfo();
//
//						List<AderAppInfoItem> list = app.getAllApps(mContext);
//						for (int i = 0; i < list.size(); i++) {
//							AderAppInfoItem apps = list.get(i);
//
//							String appName = apps.getappName();
//							String bundle = apps.getappBundle();
//
//							localStringBuilder.append(appName).append(";")
//									.append(bundle).append("|");
//						}
//
//						String string = localStringBuilder.toString();
//						string = string.substring(0, string.length() - 1);
//
//						AderLogUtils.d("原字符串=" + string);
//						AderLogUtils.d("原长=" + string.length());
//						String temp = null;
//						try {
//							temp = AderZipUtil.compress(string);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						AderLogUtils.d("压缩后的字符串=" + temp);
//						AderLogUtils.d("压缩后的长=" + temp.length());
//
//						String strEncode = AderBase64.encode(temp);
//
//						byte[] byteDecode = AderBase64.decode(strEncode);
//
//						String strDecode = new String(byteDecode);
//
//						try {
//							AderLogUtils.d("解压后的字符串="
//									+ AderZipUtil.uncompress(strDecode));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						/** 汇报应用 */
//						reportEvent(REPORT_APPS_TYPE, strEncode);
//					}
//				}).start();
//			}
//		}
//	}

	/**
	 * 获取下载应用存放的位置
	 * */
	private File getDownloadAPKDir() {
		File appCacheDir = null;
		// 判断 SDCard 是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (Build.VERSION.SDK_INT >= 8) // API Level 8
			{
				Method method = null;
				try {
					method = mContext.getClass().getMethod(
							"getExternalCacheDir");
				} catch (SecurityException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				}
				if (method != null) {
					try {
						String cacheDirPath = method.invoke(mContext,
								new Object[] {}).toString();
						appCacheDir = new File(cacheDirPath);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (Exception e) {
						AderLogUtils.e(e);
					}
				}
			} else {
				appCacheDir = new File(
						Environment.getExternalStorageDirectory(),
						"/Android/data/" + mContext.getPackageName()
								+ "/cache/");
			}
		}
		if (appCacheDir == null || !appCacheDir.exists()
				|| !appCacheDir.isDirectory()) {
			AderLogUtils.i("没有检测到外部存储空间，使用内部存储空间进行Cache");
			appCacheDir = mContext.getCacheDir();
		} else {
			AderLogUtils.i("使用外部存储空间Cache");
		}

		File receiceFile = new File(appCacheDir, "rrgamedownloadapks");
		AderLogUtils.i("得到存储路径" + receiceFile);
		return receiceFile;
	}

	/**
	 * 下载安装 apkUrl-下载url地址 fileName - 文件名 appname - app名字
	 */
	private void downLoadAPK(String apkUrl, String appName) {
		if (TextUtils.isEmpty(apkUrl) || TextUtils.isEmpty(appName)) {
			AderLogUtils.i("apkUrl or appName is empty");
			return;
		}
		// 获取路径下下载文件
		theCachePathFile = getDownloadAPKDir();
		String filename = AderMD5.getMD5(apkUrl) + ".apk";
		if (!isHasAPK(filename)) {
			// String appName = filename;
			// 下载地址
			String packagePath = apkUrl;
			Intent intent = new Intent(mContext, AderDownloadService.class);
			// 下载项信息
			AderDownloadItem item = new AderDownloadItem(null, packagePath,
					appName);
			intent.putExtra("downloadItem", item);

			// 同时运行的线程数，可以不设置
			intent.putExtra("concurrentThreads", 1);
			mContext.startService(intent);
			AderLogUtils.i("startService");
			Toast.makeText(mContext, "程序开始下载...", Toast.LENGTH_SHORT).show();
			closeInterstitialDialog();
		} else { // 已经下载过就直接启动
			AderLogUtils.i("得到下载文件路径为：" + thedownloadPath.getAbsolutePath());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(thedownloadPath),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);
		}

	}

	/**
	 * 判断是否有下载文件
	 * */
	private Boolean isHasAPK(String packageName) {
		AderLogUtils.i("theCachePathFile=" + theCachePathFile);
		thedownloadPath = new File(theCachePathFile + "/" + packageName);
		if (thedownloadPath.exists()) {
			AderLogUtils.i("找到了已经下载的文件包");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查用户权限是否添加
	 * */
	private boolean checkUserPermissions() {
		boolean ret = true;
		if (mContext != null) {
			String packageNameString = mContext.getApplicationInfo().packageName;
			PackageManager packageManager = mContext.getPackageManager();
			if (packageManager.checkPermission("android.permission.INTERNET",
					packageNameString) == PackageManager.PERMISSION_DENIED) {
				Log.e(AderLogUtils.APP_NAME,
						"必须添加android.permission.INTERNET权限");
				ret = false;
			}
			// 网络状态
			if (packageManager.checkPermission(
					"android.permission.ACCESS_NETWORK_STATE",
					packageNameString) == PackageManager.PERMISSION_DENIED) {
				Log.e(AderLogUtils.APP_NAME,
						"必须添加android.permission.ACCESS_NETWORK_STATE权限");
				ret = false;
			}
			// mac地址
			if (packageManager.checkPermission(
					"android.permission.ACCESS_WIFI_STATE", packageNameString) == PackageManager.PERMISSION_DENIED) {
				Log.e(AderLogUtils.APP_NAME,
						"必须添加android.permission.ACCESS_WIFI_STATE权限");
				ret = false;
			}
			// imei/获取网络供应商
			if (packageManager.checkPermission(
					"android.permission.READ_PHONE_STATE", packageNameString) == PackageManager.PERMISSION_DENIED) {
				Log.e(AderLogUtils.APP_NAME,
						"必须添加android.permission.READ_PHONE_STATE权限");
				ret = false;
			}
			// 允许程序写入外部存储，如SD卡上写文件
			if (packageManager.checkPermission(
					"android.permission.WRITE_EXTERNAL_STORAGE",
					packageNameString) == PackageManager.PERMISSION_DENIED) {
				Log.e(AderLogUtils.APP_NAME,
						"必须添加android.permission.WRITE_EXTERNAL_STORAGE权限");
				ret = false;
			}
		} else {
			ret = false;
		}
		return ret;
	}

	/**
	 * 屏幕开启后恢复sdk
	 * 
	 */
	protected void doSomethingOnScreenOn() {
		//由子类实现
	}

	/**
	 * 屏幕关闭后暂停sdk
	 * 
	 */
	protected void doSomethingOnScreenOff() {
		//由子类实现
	}
	
	/**
	 * 关闭显示广告的dialog
	 * 
	 */
    void closeInterstitialDialog(){ 
	    if (mDialog!=null&&mDialog.isShowing()) {	 
	      mDialog.dismiss();	 
	    }	 
    }
    
	
}
