package com.renren.aderunityad;

import com.renren.sdk.AderDevMode;
import com.renren.sdk.AderInterstitialAd;
import com.renren.sdk.AderInterstitialAdListener;
import com.renren.sdk.AderListener;
import com.renren.sdk.AderSDKView;
import com.unity3d.player.UnityPlayerActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MainActivity extends UnityPlayerActivity implements AderListener,AderInterstitialAdListener {

	//�������appid
	static final String appId ="815028da298e49869acb37849abf2c15";
	private AderInterstitialAd interstitialAd;
	/** Called when the activity is first created. */

	@Override

	public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	setupBannerAds();
	
	//����������
	//setupInterstitialAds();
	}
	
	public void setupBannerAds() {

		// And this is the same, but done programmatically

		LinearLayout layout = new LinearLayout(this);
		
		layout.setBackgroundColor(Color.TRANSPARENT);
		
		layout.setOrientation(LinearLayout.VERTICAL);

		addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,

		LayoutParams.FILL_PARENT));

		AderSDKView adView = new AderSDKView(this);
		adView.setBackgroundColor(Color.TRANSPARENT);
		layout.addView(adView, new LayoutParams(LayoutParams.FILL_PARENT,

		LayoutParams.WRAP_CONTENT));
	
		//����appi�����ߴ磬�Ѿ�����ģʽ
		adView.startService(appId, 320,50,
					AderDevMode.RELEASE_MODE, this);
		
		adView.setListener(this);

	}
	
	public void setupInterstitialAds() {

		// And this is the same, but done programmatically
		if(interstitialAd == null)
		{
			interstitialAd = new AderInterstitialAd(this,
					appId, AderDevMode.RELEASE_MODE);
			interstitialAd.setAdListener(this);
		}
		interstitialAd.loadAd();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /*
     * Banner�ص�
     * */
    //�յ�������ʧ�ܵĻص�
	@Override
	public void onFailedToReceiveAd(int errorCode, String msg) {
		// TODO Auto-generated method stub
		
	}

	//�ɹ��յ����
	@Override
	public void onReceiveAd(int count) {
		// TODO Auto-generated method stub
		
	}
	
	/*
     * InterstitialAd�ص�
     * */
	@Override
	public void onAderInterstitialAdDismiss() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAderInterstitialAdFailed(int errorCode, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAderInterstitialAdLeaveApplication() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAderInterstitialAdPresent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAderInterstitialAdReady() {
		// TODO Auto-generated method stub
		interstitialAd.showAd(this);
	}
}
