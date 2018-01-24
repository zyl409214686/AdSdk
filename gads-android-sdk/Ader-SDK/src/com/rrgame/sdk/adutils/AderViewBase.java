package com.rrgame.sdk.adutils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * UI 基类
 * 
 * @author com.rrgame
 */
public class AderViewBase extends RelativeLayout {

	protected Context mContext; // reference to parent
	protected AderEventListener mListener;

	/**
	 * 构造器
	 */
	public AderViewBase(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 构造器
	 */
	public AderViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/**
	 * 设置控件事件监听
	 */
	public void setListener(AderEventListener aListener) {
		mListener = aListener;
	}

	/**
	 * 移除监听器
	 */
	public void removeListener() {
		mListener = null;
	}

	/**
	 * 把事件通知给监听器
	 */
	protected void notifyEvent(Object aData, Object type, Object myView,
			Object webType) {
		if (mListener != null) {
			mListener.onEventOccured(new AderEventArgs(type, aData, myView,
					webType));
		}
	}

}
