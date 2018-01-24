package com.renren.ader.ane;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public abstract class RRGAderFunction implements FREFunction {
	public abstract FREObject invoke(FREContext context, FREObject[] passedArgs) throws Throwable;
	@Override
	public FREObject call(FREContext arg0, FREObject[] arg1) {
		// TODO Auto-generated method stub
		FREObject result = null;

		try
		{
			result = invoke(arg0, arg1);
		} catch (Throwable e)
		{
			e.printStackTrace();
			Log.e("Unhandled exception", e.toString());
			/*
			try
			{
				result = FREObject.newObject("Exception! " + e);
			} catch (FREWrongThreadException wte)
			{
				wte.printStackTrace();
			}
			*/
		}
		return result;
	}
}
