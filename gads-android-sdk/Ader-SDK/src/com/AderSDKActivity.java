package com;

/**
 * 本次更新时间2013.02.28 提交者：李敬磊
 * 1.修复潜在crash因素
 * 2.修复不合理请求
 * 3.修改所有代码为utf-8格式
 * 
 * 本次更新时间2012.10.02 提交者：马华
 * 本次更新内容包含：
 * 1.增加进入前台和后台的汇报接口
 * 2.增加24小时重新请求config的功能
 * 3.修改部分bug（mac地址字符替换在模拟器崩溃的bug），优化流程
 * 4.增加自动打包脚本
 */
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rrgame.sdk.AderScreen;
import com.rrgame.sdk.AderScreenListener;
import com.rrgame.sdk.R;
import com.rrgame.sdk.systeminfo.AderLogUtils;

public class AderSDKActivity extends Activity implements 
		AderScreenListener {

	private Boolean isTestMode = true;
	/** banner广告 
	private AderSDKView bannerAd;
	*/
	/** 插屏广告 */
	private AderScreen interstitialAd;
	//启动次数
	int count =0;
	/** Banner广告的宽高 
	private EditText editTextWidth;

	private EditText editTextHeight;
	 */
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		AderLogUtils.d("onCreate");
		/*
		bannerAd = (AderSDKView) findViewById(R.id.sdkview);
		editTextWidth = (EditText) findViewById(R.id.editBannerWidth);
		editTextHeight = (EditText) findViewById(R.id.editBannerHeight);
		 */
		//测试定时器
				final Handler handler=new Handler();
				final Runnable runnable=new Runnable() {
				    @Override
				    public void run() { 
				        // TODO Auto-generated method stub
				        //要做的事情
				    	/*
				    	bannerAd.setListener(null);
				    	bannerAd.stopService();
						
						bannerAd.startService("815028da298e49869acb37849abf2c15",
								320, 50, isTestMode, AderSDKActivity.this);
						bannerAd.setListener(AderSDKActivity.this);
						 handler.postDelayed(this, 500);
						 Log.e("xxxxxxx", "xxxxxxxxxxxxxStopService Count:"+String.valueOf(count++));
						 */
				    }
				};
		
		Button testbutton = (Button) findViewById(R.id.testmode);
		testbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				isTestMode = true;
				//handler.postDelayed(runnable, 500);
			}
		});

		Button releasebutton = (Button) findViewById(R.id.releasemode);
		releasebutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				isTestMode = false;
				//handler.removeCallbacks(runnable);
			}
		});
		
		/*
		Button startbutton = (Button) findViewById(R.id.service_start);
		startbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				
				 

				int editWidth = 0;
				int editHeight = 0;
				try {
					editWidth = Integer.parseInt(editTextWidth.getText()
							.toString());
					editHeight = Integer.parseInt(editTextHeight.getText()
							.toString());
				} catch (Exception e) {

				}
				int adWidth = editWidth > 0 ? editWidth : 320;
				int adHeight = editHeight > 0 ? editHeight : 50;
				AderLogUtils.d("adWidth:" + adWidth + "#adHeight:" + adHeight);
				bannerAd.startService("815028da298e49869acb37849abf2c15",
						adWidth, adHeight, isTestMode, AderSDKActivity.this);
				bannerAd.setListener(AderSDKActivity.this);

			}
		});

		Button stopbutton = (Button) findViewById(R.id.service_stop);

		stopbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (bannerAd != null) {
					bannerAd.stopService();
				}
			}
		});
		 */
		Button loadInterstitialButton = (Button) findViewById(R.id.btnLoadInterstitial);

		loadInterstitialButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (interstitialAd != null) {
					interstitialAd.load();
				}
			}
		});

		
		Button initInterstitialButton = (Button) findViewById(R.id.initInterstitial);

		initInterstitialButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/** 初始化插屏广告 */
				initInterstitialAd(isTestMode);
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		AderLogUtils.d("ondestroy");
		super.onDestroy();
		//bannerAd.stopService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		AderLogUtils.d("onpause");
	}

	@Override
	protected void onResume() {
		AderLogUtils.d("onResume");
		super.onResume();
	}

	/** 初始化插屏广告sdk */
	private void initInterstitialAd(Boolean testMode){
		if (interstitialAd!=null) {
			interstitialAd.setAdListener(null);
			interstitialAd=null;
		}
		interstitialAd = new AderScreen(this,
				"815028da298e49869acb37849abf2c15", testMode, false);
		interstitialAd.setAdListener(this);
		interstitialAd.setZoomType(AderScreen.ZOOM_100);
	}

	

	@Override
	public void onScreenReady() {
		Log.d("MY_LOG","onAderInterstitialAdReady");
		if (interstitialAd != null) {
			interstitialAd.show(this);
		}
	}

	@Override
	public void onScreenFailed(int errorCode, String errorMsg) {
		Log.d("MY_LOG","插屏收到错误*****************************************************errorCode:" + errorCode + "#errorMsg:" + errorMsg);
	}

	@Override
	public void onScreenPresent() {
		Log.d("MY_LOG","");
	}

	@Override
	public void onScreenDismiss() {
		Log.d("MY_LOG","");
	}

	@Override
	public void onScreenLeaveApplication() {
		Log.d("MY_LOG","");
	}
}
