package com.renren.ader.ane;

//import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;
import com.renren.ader.ane.RRGAderContext;

public class RRGAderExtension implements FREExtension {
	public static final String TAG = "renren.ader.android";

	@Override
	public FREContext createContext(String arg0) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "createContext");
		return new RRGAderContext();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
	}
}

