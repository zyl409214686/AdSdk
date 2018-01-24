package com.rrgame.sdk.webView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import android.widget.RelativeLayout;


import com.rrgame.sdk.adutils.AderEventObserver;
import com.rrgame.sdk.service.AderService;
import com.rrgame.sdk.systeminfo.AderLogUtils;
import com.rrgame.sdk.webView.AdWebView.AderWebView;

public class AderView extends RelativeLayout  {
	private AderEventObserver mEventObserver;
	private AderWebView _webView;
	private AderViewUtilsListener mListener;
	private int parentWidth;
	private Context mContext;
	public AderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.setBackgroundColor(Color.TRANSPARENT);
		// initViews(context);
	}

	public AderView(Context context) {
		super(context);
		// initViews(context);
		this.setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override
	public void removeAllViews()
	{
		if(_webView != null)
		{
			_webView.removeAllViews();
			_webView = null;
		}
		
		mListener = null;
		mEventObserver=null;
		super.removeAllViews();

	}

	/**
	 * 获取里面的广告webview
	 * */
	public AderWebView getWebView(){
		return _webView;
	}
	
	/**
	 * 为webview载入url
	 * 
	 */
	public void loadUrl(String url) {
		if (_webView!=null) {
			_webView.loadUrl(url);
		}
		
	}

	/**初始化Banner广告视图*/
	public void initBannerView(Context context,AderService aderUtils,int width,int height){
		AderLogUtils.d("width:"+width);
		mContext = context;
		if(mContext != null)
		{
			_webView = new AderWebView(mContext,width,height);
			// 设置监听器
			mEventObserver = new AderEventObserver(aderUtils);
			_webView.setListener(mEventObserver);
			this.addView(_webView);
		}
	}
	
	/**初始化插屏广告视图*/
	public void initInterstitialView(Context context,AderService aderUtils,int width,int height){
		AderLogUtils.d("width:"+width+"#height:"+height);
		mContext = context;
		if(mContext != null)
		{
			if (_webView==null) {
				_webView = new AderWebView(mContext,width,height);
				// 设置监听器
				mEventObserver = new AderEventObserver(aderUtils);
				_webView.setListener(mEventObserver);
				this.addView(_webView);
			}else {
				_webView.resetWebViewSize(width, height);
			}
		}
	}
	
	/**
	 * 设置Utils监听
	 */
	public void setUtilsListener(AderViewUtilsListener aListener) {
		mListener = aListener;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		
	   // int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	   // AderLogUtils.e("onMeasure parent width:"+parentWidth+"parentHeight:"+parentHeight);
	    if(mListener != null)
	    {
		    mListener.readyToGetConfig(parentWidth);
	    }

	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		AderLogUtils.i("");
		if (mListener!=null) {
			mListener.onDetachBannerAd();
		}
	}
}
