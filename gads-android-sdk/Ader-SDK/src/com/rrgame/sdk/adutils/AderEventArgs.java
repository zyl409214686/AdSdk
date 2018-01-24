package com.rrgame.sdk.adutils;

/**
 * 控件的事件相关参数
 * 
 * @author com.rrgame
 */
public class AderEventArgs {
	public Object Sender;
	public Object Data;
	public Object MyView;
	public Object WebType;

	public AderEventArgs(Object aSender, Object aData, Object aMyView,
			Object aWebType) {
		Sender = aSender;
		Data = aData;
		MyView = aMyView;
		WebType = aWebType;
	}
}
