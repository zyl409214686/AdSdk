package com.renren.aderunityad;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.rrgame.sdk.RRGDevMode;
import com.rrgame.sdk.RRGScreen;
import com.rrgame.sdk.RRGScreenListener;
import com.unity3d.player.UnityPlayerActivity;


public class MainActivity extends UnityPlayerActivity implements RRGScreenListener {

	//设置你的appid
	static final String appId ="815028da298e49869acb37849abf2c15";
	private RRGScreen interstitialAd;
	/** Called when the activity is first created. */

	@Override

	public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	
	//请求插屏广告
	setupInterstitialAds();
	}
	
	
	public void setupInterstitialAds() {

		// And this is the same, but done programmatically
		if(interstitialAd == null)
		{
			interstitialAd = new RRGScreen(this,
					appId, RRGDevMode.RELEASE_MODE,true);
			interstitialAd.setAdListener(this);
			interstitialAd.setZoomType(RRGScreen.ZOOM_100);
		}
		interstitialAd.load();
		Toast.makeText(this, interstitialAd.getSDKVersion(), Toast.LENGTH_LONG);
		System.out.println("sdk_version="+interstitialAd.getSDKVersion());
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
