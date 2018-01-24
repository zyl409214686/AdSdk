package com.sample.rrgame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rrgame.sdk.RRGDevMode;
import com.rrgame.sdk.RRGScreen;
import com.rrgame.sdk.RRGScreenListener;


public class SampleActivity extends Activity implements RRGScreenListener
{
	   
	//public AderSDKView sdk; 
	/** 插屏广告 */
	private RRGScreen interstitialAd;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
          
        /*
        sdk = (AderSDKView) findViewById(R.id.sdkview);
        sdk.startService("815028da298e49869acb37849abf2c15", 320,50,
				AderDevMode.RELEASE_MODE, SampleActivity.this);
        sdk.setListener(this);
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
				initInterstitialAd(RRGDevMode.RELEASE_MODE);
			}
		});
    }

    /** 初始化插屏广告sdk */
	private void initInterstitialAd(Boolean testMode){
		if (interstitialAd!=null) {
			interstitialAd.setAdListener(null);
			interstitialAd=null;
		}
		interstitialAd = new RRGScreen(this,
				"815028da298e49869acb37849abf2c15", testMode, true);
		interstitialAd.setAdListener(this);
		interstitialAd.setZoomType(RRGScreen.ZOOM_100);
	}
     
	@Override
	protected void onDestroy() {
		Log.d("MY_LOG","ondestroy");
		super.onDestroy();
		//sdk.stopService();
	}

	@Override
	public void onScreenDismiss() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScreenFailed(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScreenLeaveApplication() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScreenPresent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScreenReady() {
		// TODO Auto-generated method stub
		if (interstitialAd != null) {
			interstitialAd.show(this);
		}
	}

}
