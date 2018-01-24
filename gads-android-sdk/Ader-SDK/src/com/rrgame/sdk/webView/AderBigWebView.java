package com.rrgame.sdk.webView;

import com.rrgame.sdk.adutils.AderViewBase;
import com.rrgame.sdk.adutils.AderPublicUtils.WebStatus;
import com.rrgame.sdk.adutils.AderPublicUtils.Webview_Type;
import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * 大的webview
 * 
 */
//@SuppressLint("SetJavaScriptEnabled")
public class AderBigWebView extends AderViewBase {
	/**内容webview*/
	private WebView mWebView;
	private Button mButtonClose;
	private Button mButtonBackGround;

	public AderBigWebView(Context context) {

		super(context);
		AderLogUtils.i("创建新的大webview");
		this.setBackgroundColor(Color.TRANSPARENT);
		RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);

		mButtonBackGround = new Button(context);
		mButtonBackGround.setLayoutParams(layoutParam);
		mButtonBackGround.setFocusable(true);
		mButtonBackGround.setBackgroundColor(Color.TRANSPARENT);
		this.addView(mButtonBackGround);

		mWebView = new WebView(context);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new myWebViewClient());
		mWebView.setLayoutParams(layoutParam);
		this.addView(mWebView);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		mButtonClose = new Button(context);
		mButtonClose.setText("X");
		mButtonClose.setLayoutParams(layoutParams);

		/**
		 * 定义关闭按钮buttonClose的点击响应方法
		 * 
		 */
		Button.OnClickListener button_listener = new Button.OnClickListener() {
			// @Override
			public void onClick(View v) {
				AderLogUtils.d("你点击了关闭");
				notifyEvent("", WebStatus.CLOSE_WEBVIEW_JS, mWebView,
						Webview_Type.web_big_type);
			}
		};
		mButtonClose.setOnClickListener(button_listener);
		this.addView(mButtonClose);
		mButtonClose.setVisibility(VISIBLE);
	}

	public void setWebViewHeight(int height) {
		RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT, height);
		mWebView.setLayoutParams(layoutParam);
	}

	public void setCloseButton() {
		
		AderLogUtils.i("设置关闭button");
		mButtonClose.setVisibility(VISIBLE);

	}

	public void loadUrl(String url) {
		mWebView.loadUrl(url);
	}

	public void removeAllViews() {
		
		if (mWebView!=null) {
			mWebView.stopLoading();
			mWebView.destroy();
			mWebView=null;
		}
		super.removeAllViews();
	}
	
	class myWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			AderLogUtils.d("System.out", "AdBigWebView");
			AderLogUtils.d("System.out", url);
			Uri uri = Uri.parse(url);

			if (uri.getScheme().equals("amsg")) {
				notifyEvent(url, WebStatus.GETAMSG_WEBVIEW_JS, mWebView,
						Webview_Type.web_big_type);
			}

			return false;
		}

		public void onPageFinished(WebView webView, String url) {
			AderLogUtils.d("onPageFinished");

			Uri uri = Uri.parse(url);
			if (uri.getHost() == null) {
				// 加载失败
				notifyEvent(url, WebStatus.FAILED_WEBVIEW_JS, mWebView,
						Webview_Type.web_big_type);
			} else {
				// 加载成功
				if (mWebView != null) {
					notifyEvent(url, WebStatus.SUCCESS_WEBVIEW_JS, mWebView,
							Webview_Type.web_big_type);
				}

			}
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			AderLogUtils.d("failed: " + failingUrl
					+ ", error code: " + errorCode + " [" + description + "]");
			AderLogUtils.d("onReceivedError");

			Uri uri = Uri.parse(failingUrl);
			if (uri.getScheme() != null) {
				if (!(uri.getScheme().equals("amsg"))) {
					notifyEvent(failingUrl, WebStatus.FAILED_WEBVIEW_JS,
							mWebView, Webview_Type.web_big_type);
				}
			} else {
				notifyEvent(failingUrl, WebStatus.FAILED_WEBVIEW_JS, mWebView,
						Webview_Type.web_big_type);
			}
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

}
