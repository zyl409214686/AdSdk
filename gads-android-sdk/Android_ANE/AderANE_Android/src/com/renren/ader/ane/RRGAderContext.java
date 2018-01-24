package com.renren.ader.ane;

import java.util.HashMap;
import java.util.Map;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.renren.ader.ane.RRGAderExtension;
import com.renren.ader.ane.RRGAderFunction;
import com.rrgame.sdk.RRGScreen;
import com.rrgame.sdk.RRGScreenListener;

public class RRGAderContext extends FREContext implements RRGScreenListener
{

	/*
	private AderSDKView aderView=null;
	private int _width=0;
	private int _height=0;
	*/
	private RRGScreen interstitialAd;
	@Override
	public void dispose() {
		/*
		if(aderView!=null)
		{
	        aderView.stopService();
	        aderView=null;
		}
		*/
		if (interstitialAd!=null) {
			interstitialAd.setAdListener(null);
			interstitialAd=null;
		}
	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		// TODO Auto-generated method stub
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		/*
		functionMap.put("RRGAder_start", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if(passedArgs.length<6)
				{
					Log.e(RRGAderExtension.TAG, "start args count error");
					return null;
				}
				String appid=passedArgs[0].getAsString();
				if(appid==null||appid.length()==0)
				{
					Log.e(RRGAderExtension.TAG, "appid not correct");
					return null;
				}
				int xMargin=passedArgs[1].getAsInt();
				int yMargin=passedArgs[2].getAsInt();
				_width=passedArgs[3].getAsInt();
				_height=passedArgs[4].getAsInt();
				if(_height<=0||_width<=0)
				{
					Log.e(RRGAderExtension.TAG, "height or width not correct");
					return null;
				}
				int _mode=passedArgs[5].getAsInt();
				//Log.i(AderExtension.TAG, "aaaaax:"+xMargin+" y:"+yMargin+" width:"+_width+" height:"+_height);
				Boolean mode=false;
				if(_mode==0)
				{
					mode=true;
				}
	
				if(aderView==null)
				{
					RelativeLayout.LayoutParams root=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
					RelativeLayout rootView=new RelativeLayout(context.getActivity().getApplicationContext());
					rootView.setLayoutParams(root); 
					context.getActivity().addContentView(rootView,root);
					
					aderView=new AderSDKView(context.getActivity().getApplicationContext());
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					layoutParams.alignWithParent=true;
					int topMargin=0;
					int leftMargin=0;
					int rightMargin=0;
					int bottomMargin=0;
					if(xMargin==10000)
					{
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					}
					else if(xMargin<0)
					{
						rightMargin=0-xMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					}
					else
					{
						leftMargin=xMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					}
					
					if(yMargin==10000)
					{
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					}
					else if(yMargin<0)
					{
						bottomMargin=0-yMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					}
					else
					{
						topMargin=yMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					}
					layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
					aderView.setLayoutParams(layoutParams);
					rootView.addView(aderView);
				}
				aderView.startService(appid, _width, _height, mode, context.getActivity());
				aderView.setListener(RRGAderContext.this);
				return null;
			}
		});
		
		functionMap.put("RRGAder_move", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if(passedArgs.length<2)
				{
					Log.e(RRGAderExtension.TAG, "move args count error");
					return null;
				}
				if(aderView!=null)
				{	
				
					int xMargin=passedArgs[0].getAsInt();
					int yMargin=passedArgs[1].getAsInt();
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
						    RelativeLayout.LayoutParams.WRAP_CONTENT);
					layoutParams.alignWithParent=true;
					int topMargin=0;
					int leftMargin=0;
					int rightMargin=0;
					int bottomMargin=0;
					if(xMargin==10000)
					{
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					}
					else if(xMargin<0)
					{
						rightMargin=0-xMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					}
					else
					{
						leftMargin=xMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					}
					
					if(yMargin==10000)
					{
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					}
					else if(yMargin<0)
					{
						bottomMargin=0-yMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					}
					else
					{
						topMargin=yMargin;
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					}
					layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
					aderView.setLayoutParams(layoutParams);
				}
				return null;
			}
		});
		
		functionMap.put("RRGAder_stop", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if(aderView!=null)
				{
			        aderView.stopService();
			        aderView=null;
				}
				return null;
			}
		});
		*/
		functionMap.put("RRGAder_DeviceType", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				String dt = Build.MODEL;
				if (!TextUtils.isEmpty(dt)) {
					return FREObject.newObject(dt);
				}
				return null;
			}
		});
		
		functionMap.put("RRGAder_initInterstitialAd", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if(passedArgs.length<4)
				{
					//Log.e(RRGAderExtension.TAG, "RRGAder_initInterstitialAd args count error锛�+passedArgs.length);
					return null;
				}
				String appid=passedArgs[0].getAsString();
				if(TextUtils.isEmpty(appid))
				{
					Log.e(RRGAderExtension.TAG, "appid not correct");
					return null;
				}
				
				int _mode=passedArgs[2].getAsInt();
				Boolean mode=false;
				if(_mode==0)
				{
					mode=true;
				}
				if (interstitialAd!=null) {
					interstitialAd.setAdListener(null);
					interstitialAd=null;
				}
				Boolean isApplyLBS = true;
				isApplyLBS = passedArgs[3].getAsBool();
				interstitialAd = new RRGScreen(context.getActivity(),appid, mode, isApplyLBS);
				interstitialAd.setAdListener(RRGAderContext.this);
				int _size = passedArgs[1].getAsInt();
				interstitialAd.setZoomType(_size);
				return null;
			}
		});
		
		functionMap.put("RRGAder_requestInterstitialAd", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if (interstitialAd != null) {
					interstitialAd.load();
				}
				return null;
			}
		});
		
		functionMap.put("RRGAder_isInterstitialAdReady", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if (interstitialAd != null) {
					return FREObject.newObject(interstitialAd.isReady());
				}
				return FREObject.newObject(false);
			}
		});
		
		functionMap.put("RRGAder_presentInterstitialAd", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if (interstitialAd != null) {
					interstitialAd.show(context.getActivity());
				}
				return null;
			}
		});
		
		functionMap.put("RRGAder_getSDKVersion", new RRGAderFunction() {
			@Override
			public FREObject invoke(FREContext context, FREObject[] passedArgs)
					throws Exception {
				if (interstitialAd != null) {
					String versionStr = interstitialAd.getSDKVersion();
					return FREObject.newObject(versionStr);
				}
				return null;
			}
		});
		
		return functionMap;
	}

	/*
	@Override
	public void onFailedToReceiveAd(int arg0, String arg1) {
		this.dispatchStatusEventAsync("RRG_ADER_FAIL_EVENT", arg0+"#"+arg1);
	}

	@Override
	public void onReceiveAd(int arg0) {
		this.dispatchStatusEventAsync("RRG_ADER_SUCCESS_EVENT", ""+arg0);
	}

    */
	@Override
	public void onScreenDismiss() {
		this.dispatchStatusEventAsync("RRG_ADER_INTERSTITIAL_DID_DISMISS_EVENT", "RRG_ADER_INTERSTITIAL_DID_DISMISS_EVENT");
	}

	@Override
	public void onScreenFailed(int arg0, String arg1) {
		this.dispatchStatusEventAsync("RRG_ADER_INTERSTITIAL_FAIL_EVENT", arg0+"#"+arg1);
	}

	@Override
	public void onScreenLeaveApplication() {
		//this.dispatchStatusEventAsync("RRG_ADER_INTERSTITIAL_DID_RECEIVE_AD_EVENT", "RRG_ADER_INTERSTITIAL_DID_RECEIVE_AD_EVENT");
	}

	@Override
	public void onScreenPresent() {
		this.dispatchStatusEventAsync("RRG_ADER_INTERSTITIAL_WILL_PRESENT_EVENT", "RRG_ADER_INTERSTITIAL_WILL_PRESENT_EVENT");
	}

	@Override
	public void onScreenReady() {
		this.dispatchStatusEventAsync("RRG_ADER_INTERSTITIAL_DID_RECEIVE_AD_EVENT", "RRG_ADER_INTERSTITIAL_DID_RECEIVE_AD_EVENT");
	}
	
}
