package com.rrgame.sdk.webView;

import com.rrgame.sdk.systeminfo.AderBase64;
import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * 插屏广告视图类，视图中包含有显示广告的AderView和一个关闭按钮
 * 
 * */

public class AderInterstitialAdView extends RelativeLayout {
	private final static String Btn_background ="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";
	private int btn_W =39;
	private int btn_H =btn_W;

	/** 关闭按钮 */
	private Button mButtonClose;
	/** 广告视图监听 */
	private AderInterstitialAdViewListener mAdViewListener;

	public AderInterstitialAdView(Context context) {
		super(context);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		mButtonClose = new Button(context);
		mButtonClose.setLayoutParams(layoutParams);
		Resources res=getResources();
		Bitmap buttonBackBitmap= AderBase64.getBitmap(Btn_background);
		Drawable DrawbleBack = new BitmapDrawable(res, buttonBackBitmap);
		mButtonClose.setBackgroundDrawable(DrawbleBack);
		this.setOnClickListener(null);
		/**
		 * 定义关闭按钮buttonClose的点击响应方法
		 * 
		 */
		Button.OnClickListener button_listener = new Button.OnClickListener() {
			// @Override
			public void onClick(View v) {
				AderLogUtils.d("你点击了关闭");
				onCloseView();
				// notifyEvent("", WebStatus.CLOSE_WEBVIEW_JS, mWebView,
				// Webview_Type.web_big_type);
			}
		};
		mButtonClose.setOnClickListener(button_listener);
		this.addView(mButtonClose);
		mButtonClose.setVisibility(VISIBLE);
	}

	public void setAdViewListener(AderInterstitialAdViewListener listener) {
		mAdViewListener = listener;
	}

	public void setCloseButton(View adView,float mDensity) {
		AderLogUtils.i("设置关闭button");
		mButtonClose.setVisibility(VISIBLE);
		mButtonClose.bringToFront();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				(int)(btn_W*mDensity),
				(int)(btn_H*mDensity));
		//检查唯一
		if(adView.getTag() !=null && "adView".equals(adView.getTag().toString()))
		{
			if(this.findViewById(adView.getId()) != null)
			{
				 lp.addRule(RelativeLayout.ALIGN_RIGHT,adView.getId());
			     lp.addRule(RelativeLayout.ALIGN_TOP,adView.getId());
		    }
		}
		
		mButtonClose.setLayoutParams(lp);
	}

	/** 关闭视图 */
	private void onCloseView() {
		//this.removeAllViews();
		//this.setVisibility(GONE);
		if (mAdViewListener != null) {
			mAdViewListener.onCloseInterstitialAdView(this);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		AderLogUtils.i("");
		if (mAdViewListener!=null) {
			mAdViewListener.onDetachInterstitialAd(this);
		}
	}

	public interface AderInterstitialAdViewListener {
		/** 关闭广告视图 */
		public void onCloseInterstitialAdView(AderInterstitialAdView _adView);
		
		/**广告视图的前后台状态发生变化*/
		public void windowVisibilityChanged(int visibility,ViewGroup view);
		
		/**广告视图从window移除*/
		/**现在改为dialog显示后，点击返回键时，处理方式不同了*/
		public void onDetachInterstitialAd(AderInterstitialAdView _adView);
	}
	
	/**
	 * 屏幕开关回调
	 * 
	 */
	protected void onWindowVisibilityChanged(int visibility) {
		AderLogUtils.d("onWindowVisibilityChanged#########################"
				+ visibility);
		if (mAdViewListener != null) {
			mAdViewListener.windowVisibilityChanged(visibility,this);
		}
	}
}
