package com.rrgame.sdk.webView.AdWebView;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHost;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.rrgame.sdk.adutils.AderPublicUtils.WebStatus;
import com.rrgame.sdk.adutils.AderPublicUtils.Webview_Type;
import com.rrgame.sdk.adutils.AderViewBase;
import com.rrgame.sdk.systeminfo.AderBase64;
import com.rrgame.sdk.systeminfo.AderLogUtils;

/**
 * 广告的webview
 * 
 */
public class AderWebView extends AderViewBase {

	/**
	 * 显示广告的webview 两个webview用于切换
	 * */
	private WebView mWebView;
	private WebView mWebView2;
	/** 请求配置的工具webview */
	private WebView mConfigWebView;
	private static final int WEBVIEW_ID = 100;
	private static final int CONFIGWEBVIEW_ID = 200;
	private static final int WEBVIEW_ID_2 = 300;
	/**当前webviewid*/
	private int Active_WebView_Id = 100;
	// private static HttpHost cmproxy = new HttpHost("10.2.11.79", 8081);
	/** 广告切换动画 */
	private static final int ANIMATION_ALPHA = 0;
	private static final int ANIMATION_SCALE = 1;
	private static final int ANIMATION_TRANSLATE = 2;
	private static final int ANIMATION_ROTATE = 3;

	/**webview是否加载完成*/
	private boolean loadingFinished = true;
	private boolean redirect = false;
	
	//点击广告区域的绝对坐标
	private int click_x,click_y;

	public AderWebView(Context context, int width) {
		super(context);
		mWebView = new WebView(context);
		mWebView.setLayoutParams(new android.view.ViewGroup.LayoutParams(width,
				LayoutParams.WRAP_CONTENT));
		WebView.enablePlatformNotifications();
		mWebView.setId(WEBVIEW_ID);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new myWebViewClient());
		// Horizontal水平方向，Vertical竖直方向
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);

		mWebView2 = new WebView(context);
		mWebView2.setLayoutParams(new android.view.ViewGroup.LayoutParams(
				width, LayoutParams.WRAP_CONTENT));
		mWebView2.setId(WEBVIEW_ID_2);
		mWebView2.getSettings().setJavaScriptEnabled(true);
		mWebView2.setWebViewClient(new myWebViewClient());
		// Horizontal水平方向，Vertical竖直方向
		mWebView2.setHorizontalScrollBarEnabled(false);
		mWebView2.setVerticalScrollBarEnabled(false);

		mConfigWebView = new WebView(getContext());
		mConfigWebView.setId(CONFIGWEBVIEW_ID);
		mConfigWebView.getSettings().setJavaScriptEnabled(true);
		mConfigWebView.addJavascriptInterface(new AdCheckJs(), "adCheckJs");
		mConfigWebView.setWebViewClient(new myWebViewClient());

		// 为内网设置代理，发布时去掉
		// setProxyHostField(cmproxy);
		this.addView(mWebView);
		this.addView(mWebView2);
		mWebView.setVisibility(GONE);
		mWebView2.setVisibility(GONE);
		mWebView.setBackgroundColor(Color.TRANSPARENT);
		mWebView2.setBackgroundColor(Color.TRANSPARENT);
		this.setBackgroundColor(Color.TRANSPARENT);
		AderLogUtils.d("AderWebView构造函数");
	}
	
	public AderWebView(Context context,int width,int height) {
		super(context);
		mWebView = new WebView(context);
		RelativeLayout.LayoutParams lParams=new RelativeLayout.LayoutParams(width, height);
//		mWebView.setLayoutParams(new android.view.ViewGroup.LayoutParams(width,
//				height));
		mWebView.setLayoutParams(lParams);
		WebView.enablePlatformNotifications();
		mWebView.setId(WEBVIEW_ID);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new myWebViewClient());
		// Horizontal水平方向，Vertical竖直方向
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);

		mWebView2 = new WebView(context);
		mWebView2.setLayoutParams(lParams);
//		mWebView2.setLayoutParams(new android.view.ViewGroup.LayoutParams(width,
//				height));
		mWebView2.setId(WEBVIEW_ID_2);
		mWebView2.getSettings().setJavaScriptEnabled(true);
		mWebView2.setWebViewClient(new myWebViewClient());
		// Horizontal水平方向，Vertical竖直方向
		mWebView2.setHorizontalScrollBarEnabled(false);
		mWebView2.setVerticalScrollBarEnabled(false);

		mConfigWebView = new WebView(getContext());
		mConfigWebView.setId(CONFIGWEBVIEW_ID);
		mConfigWebView.getSettings().setJavaScriptEnabled(true);
		mConfigWebView.addJavascriptInterface(new AdCheckJs(), "adCheckJs");
		mConfigWebView.setWebViewClient(new myWebViewClient());

		// 为内网设置代理，发布时去掉
		// setProxyHostField(cmproxy);
		this.addView(mWebView);
		this.addView(mWebView2);
		mWebView.setVisibility(GONE);
		mWebView2.setVisibility(GONE);
		mWebView.setBackgroundColor(Color.TRANSPARENT);
		mWebView2.setBackgroundColor(Color.TRANSPARENT);
		this.setBackgroundColor(Color.TRANSPARENT);
		AderLogUtils.d("AderWebView构造函数");
	}

	/**重新设置webView的尺寸*/
	public void resetWebViewSize(int width,int height){
		RelativeLayout.LayoutParams lParams=new RelativeLayout.LayoutParams(width, height);
		if (mWebView!=null) {
			mWebView.setLayoutParams(lParams);
		}
		if (mWebView2!=null) {
			mWebView2.setLayoutParams(lParams);
		}
	}
	
	@Override
	public void removeAllViews() {
		AderLogUtils.d("");
		if (mConfigWebView != null) {
			AderLogUtils.d("mConfigWebView is not null.");
			mConfigWebView.loadData("", "text/html", "utf-8");
			mConfigWebView.clearCache(true);
			mConfigWebView.setWebViewClient(null);
			mConfigWebView.stopLoading();
			mConfigWebView.destroy();
			mConfigWebView = null;
		}
		if (mWebView != null) {
			mWebView.setWebViewClient(null);
			mWebView.stopLoading();
			mWebView.removeAllViews();
		}
		if (mWebView2 != null) {
			mWebView2.setWebViewClient(null);
			mWebView2.stopLoading();
			mWebView2.removeAllViews();
		}

		mContext = null;
		super.removeAllViews();
		if (mWebView != null) {
			mWebView.destroy();
			mWebView = null;
		}
		if (mWebView2 != null) {
			mWebView2.destroy();
			mWebView2 = null;
		}
	}

	/**
	 * 获取js是否加载成功
	 */
	class AdCheckJs {
		public void checkJs(String data) {
			AderLogUtils.d("onPageFinished:" + data);
			if (data.equals("false")) {
				notifyEvent("", WebStatus.FAILED_WEBVIEW_JS, mWebView,
						Webview_Type.web_small_type);
			}
		}
	}

	public void loadUrl(String url) {
		AderLogUtils.d("mConfigWebView loadUrl");
		if (mConfigWebView != null) {
			AderLogUtils.d("mConfigWebView is not null");
			mConfigWebView.loadDataWithBaseURL(null, url, "text/html", "utf-8",
					null);

			/*
			 * mConfigWebView.setWebViewClient(new myWebViewClient());
			 * mConfigWebView.setVisibility(GONE);
			 */
		} else {
			AderLogUtils.d("mConfigWebView is null");
		}
	}

	class myWebViewClient extends WebViewClient {

		@Override
		/**
		 * webview和js通信处理
		 */
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (!loadingFinished) 
			{

				redirect = true;
			}
			loadingFinished = false;

			//AderLogUtils.d("###"+URLDecoder.decode(url));
			AderLogUtils.d("AdWebView:"+url);
			Uri uri = Uri.parse(url);
			
			if (uri.getScheme().equals("amsg")) 
			{
				//如果是点击增加点击坐标字段
				String temp_data = url.replaceFirst("amsg", "http");
				URL temp_url;
				try {
					temp_url = new URL(temp_data);
					if(temp_url.getPath().equals("/reportClick")){
						String postionBase64 = click_x+"|"+click_y;
			        	url = url+"&xy="+postionBase64;
			        }
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //
				
				if (Active_WebView_Id == 100) 
				{
					AderLogUtils.d("System.out", "AdWebView");
					AderLogUtils.d("System.out", "100");
					notifyEvent(url, WebStatus.GETAMSG_WEBVIEW_JS, mWebView2,
							Webview_Type.web_small_type);
				} else if (Active_WebView_Id == 300) 
				{
					AderLogUtils.d("System.out", "AdWebView");
					AderLogUtils.d("System.out", "300");
					notifyEvent(url, WebStatus.GETAMSG_WEBVIEW_JS, mWebView,
							Webview_Type.web_small_type);
				}
				return true;
			}
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			loadingFinished = false;
			AderLogUtils.d("onPageStarted");
		}

		/**
		 * webview加载完成
		 * 
		 */
		public void onPageFinished(WebView webView, String url) {

			AderLogUtils.d("onPageFinished" + Integer.toString(webView.getId())
					+ ":" + url);
			if (webView.getId() == CONFIGWEBVIEW_ID) {
				webView.loadUrl("javascript:window.adCheckJs.checkJs(!!window['AD_Notify']);");
			}
			AderLogUtils.d("onPageFinished in");
			if (!redirect) {

				loadingFinished = true;
			}
			if (loadingFinished && !redirect) {
				AderLogUtils.d(Integer.toString(webView.getId()));
				AderLogUtils.d("onPageFinished");
				Uri uri = Uri.parse(url);
				if (webView.getId() == WEBVIEW_ID
						|| webView.getId() == WEBVIEW_ID_2) {
					if (uri.getHost() == null) {
						// 加载失败
						notifyEvent(url, WebStatus.FAILED_WEBVIEW_JS, mWebView,
								Webview_Type.web_small_type);
					}
				}
			} else {
				redirect = false;
			}
		}

		/**
		 * webview加载失败
		 * 
		 */
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			AderLogUtils.d(Integer.toString(view.getId()));
			AderLogUtils.d("failed: " + failingUrl + ", error code: "
					+ errorCode + " [" + description + "]");
			AderLogUtils.d("onReceivedError");

			Uri uri = Uri.parse(failingUrl);
			if (uri.getScheme() != null) {
				if (!(uri.getScheme().equals("amsg"))) {
					notifyEvent(failingUrl, WebStatus.FAILED_WEBVIEW_JS,
							mWebView, Webview_Type.web_small_type);
				}
			} else {
				notifyEvent(failingUrl, WebStatus.FAILED_WEBVIEW_JS, mWebView,
						Webview_Type.web_small_type);
			}
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	/**
	 * 为webview设置代理，内网使用
	 * 
	 */
	public boolean setProxyHostField(HttpHost proxyServer) {
		// Getting network
		Class<?> networkClass = null;
		Object network = null;
		try {
			networkClass = Class.forName("android.webkit.Network");
			Field networkField = networkClass.getDeclaredField("sNetwork");
			network = getFieldValueSafely(networkField, null);
		} catch (Exception ex) {
			AderLogUtils.e("setProxyHostField", "error getting network");
			return false;
		}
		if (network == null) {
			AderLogUtils.e("setProxyHostField", "error getting network : null");
			return false;
		}
		Object requestQueue = null;
		try {
			Field requestQueueField = networkClass
					.getDeclaredField("mRequestQueue");
			requestQueue = getFieldValueSafely(requestQueueField, network);
		} catch (Exception ex) {
			AderLogUtils.e("setProxyHostField", "error getting field value");
			return false;
		}
		if (requestQueue == null) {
			AderLogUtils.e("setProxyHostField", "Request queue is null");
			return false;
		}
		Field proxyHostField = null;
		try {
			Class<?> requestQueueClass = Class
					.forName("android.net.http.RequestQueue");
			proxyHostField = requestQueueClass.getDeclaredField("mProxyHost");
		} catch (Exception ex) {
			AderLogUtils.e("setProxyHostField",
					"error getting proxy host field");
			return false;
		}
		synchronized (this) {
			boolean temp = proxyHostField.isAccessible();
			try {
				proxyHostField.setAccessible(true);
				proxyHostField.set(requestQueue, proxyServer);
			} catch (Exception ex) {
				AderLogUtils.e("setProxyHostField", "error setting proxy host");
			} finally {
				proxyHostField.setAccessible(temp);
			}
		}
		return true;
	}

	/**
	 * 获取网络
	 * 
	 */
	private Object getFieldValueSafely(Field field, Object classInstance)
			throws IllegalArgumentException, IllegalAccessException {
		boolean oldAccessibleValue = field.isAccessible();
		field.setAccessible(true);
		Object result = field.get(classInstance);
		field.setAccessible(oldAccessibleValue);
		return result;
	}
	
	/**
	 * 显示插屏广告，不要动画
	 * */
	public void showWebViewWithoutAnimation(){
		if (Active_WebView_Id == WEBVIEW_ID) {
			mWebView2.setVisibility(VISIBLE);
			mWebView.setVisibility(GONE);
		} else {
			mWebView.setVisibility(VISIBLE);
			mWebView2.setVisibility(GONE);
		}	
	}
	

	/**
	 * webview动画效果
	 * 
	 */

	public void webViewAnimation(int v) {

		if (Active_WebView_Id == WEBVIEW_ID) {

			excuteAnimation(v, mWebView2, mWebView);
			Active_WebView_Id = WEBVIEW_ID_2;
		} else {
			excuteAnimation(v, mWebView, mWebView2);
			Active_WebView_Id = WEBVIEW_ID;
		}

	}

	/**执行动画工具函数*/
	public static void excuteAnimation(int v, WebView webview1, WebView webview2) {
		AnimationSet animationSet = new AnimationSet(true);
		AnimationSet animationSet2 = new AnimationSet(true);
		if (v == ANIMATION_ALPHA) {

			// 创建一个AlphaAnimation对象
			AderLogUtils.d("animation");
			AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);// 1表示不透明，0表示透明
			// 设置动画执行的时间（单位：毫秒）
			alphaAnimation.setDuration(1000);
			// 将AlphaAnimation对象添加到AnimationSet当中
			animationSet.addAnimation(alphaAnimation);
			// 使用ImageView的startAnimation方法开始执行动画
			AlphaAnimation alphaAnimation2 = new AlphaAnimation(1, 0);// 1表示不透明，0表示透明
			// 设置动画执行的时间（单位：毫秒）
			alphaAnimation.setDuration(1000);
			// 将AlphaAnimation对象添加到AnimationSet当中
			animationSet2.addAnimation(alphaAnimation2);
			webview1.setVisibility(VISIBLE);
			webview1.startAnimation(alphaAnimation);
			webview2.startAnimation(alphaAnimation2);
			webview2.setVisibility(GONE);
		} else if (v == ANIMATION_SCALE) {
			ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(1000);
			animationSet.addAnimation(scaleAnimation);
			webview1.setVisibility(VISIBLE);
			webview1.startAnimation(scaleAnimation);
			webview2.setVisibility(GONE);
		} else if (v == ANIMATION_TRANSLATE) {
			TranslateAnimation translationAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, -1f,
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 0f);
			translationAnimation.setDuration(1000);
			animationSet.addAnimation(translationAnimation);
			TranslateAnimation translationAnimation2 = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					1f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f);
			translationAnimation2.setDuration(1000);
			animationSet2.addAnimation(translationAnimation);
			webview1.setVisibility(VISIBLE);
			webview1.startAnimation(translationAnimation);
			webview2.startAnimation(translationAnimation2);
			webview2.setVisibility(GONE);
		}

		else if (v == ANIMATION_ROTATE) {
			RotateAnimation rotateAnimation = new RotateAnimation(-90, 0,
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					1f);
			rotateAnimation.setDuration(1000);
			animationSet.addAnimation(rotateAnimation);
			RotateAnimation rotateAnimation2 = new RotateAnimation(0, 270,
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					1f);
			rotateAnimation2.setDuration(1000);
			animationSet2.addAnimation(rotateAnimation);
			webview1.setVisibility(VISIBLE);
			webview1.startAnimation(rotateAnimation);
			webview2.startAnimation(rotateAnimation2);
			webview2.setVisibility(GONE);
		}

	}

	@Override  
    public boolean onInterceptTouchEvent(MotionEvent event) { 
		if(event.getAction() == MotionEvent.ACTION_DOWN){
		   int[] location = new  int[2];
		   getLocationInWindow(location);
		   click_x = (int)(event.getX() + location[0]);
		   click_y = (int)(event.getY() + location[1]);
		}
        return super.onInterceptTouchEvent(event);  
    }  
	
	
}
