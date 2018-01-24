package com.rrgame.sdk.systeminfo;

import com.rrgame.sdk.adutils.AderPublicUtils;

import android.util.Log;

/**
 * Log工具类
 * 
 * 封装系统Log工具类，便于统一Log的输出和关闭
 * 
 * @author com.rrgame
 * 
 */

public class AderLogUtils {

	private static final boolean LOGGABLE = AderPublicUtils.DEBUG;
	public static final String APP_NAME = "com.rrgame.sdk";

	public static int v(String msg) {
		return v("", msg);
	}

	public static int d(String msg) {
		return d("", msg); 
	}

	public static int i(String msg) {
		return i("", msg);
	}

	public static int w(String msg) {
		return w("", msg);
	}

	public static int e(String msg) {
		return e("", msg);
	}

	public static int v(String tag, String msg) {
		return LOGGABLE ? Log.v(APP_NAME, getTracePrefix("v") + msg) : 0;
	}

	public static int d(String tag, String msg) {
		return LOGGABLE ? Log
				.d(APP_NAME, getTracePrefix("d") + tag + " " + msg) : 0;
	}

	public static int i(String tag, String msg) {
		return LOGGABLE ? Log
				.i(APP_NAME, getTracePrefix("i") + tag + " " + msg) : 0;
	}

	public static int w(String tag, String msg) {
		return LOGGABLE ? Log
				.w(APP_NAME, getTracePrefix("w") + tag + " " + msg) : 0;
	}

	public static int e(String tag, String msg) {
		return LOGGABLE ? Log
				.e(APP_NAME, getTracePrefix("e") + tag + " " + msg) : 0;
	}

	public static int e(Exception exception) {
		StackTraceElement[] ste = exception.getStackTrace();
		StringBuilder sb = new StringBuilder();
		try {
			String name = exception.getClass().getName();
			String message = exception.getMessage();
			String content = (message != null) ? (name + ":" + message) : name;
			sb.append(content + "\n");
			for (StackTraceElement s : ste) {
				sb.append(s.toString() + "\n");
			}
		} catch (Exception e) {
		}
		return w("", sb.toString());
	}
	
	public static int v(String tag, String msg, Throwable tr) {
		return LOGGABLE ? Log.v(APP_NAME, tag + ": " + msg, tr) : 0;
	}

	public static int d(String tag, String msg, Throwable tr) {
		return LOGGABLE ? Log.d(APP_NAME, tag + ": " + msg, tr) : 0;
	}

	public static int i(String tag, String msg, Throwable tr) {
		return LOGGABLE ? Log.i(APP_NAME, tag + ": " + msg, tr) : 0;
	}

	public static int w(String tag, String msg, Throwable tr) {
		return LOGGABLE ? Log.w(APP_NAME, tag + ": " + msg, tr) : 0;
	}

	public static int e(String tag, String msg, Throwable tr) {
		return LOGGABLE ? Log.e(APP_NAME, tag + ": " + msg, tr) : 0;
	}

	private static String getTracePrefix(String logLevel) {
		StackTraceElement[] sts = new Throwable().getStackTrace();
		StackTraceElement st = null;
		for (int i = 0; i < sts.length; i++) {
			if (sts[i].getMethodName().equalsIgnoreCase(logLevel)
					&& i + 2 < sts.length) {

				if (sts[i + 1].getMethodName().equalsIgnoreCase(logLevel)) {
					st = sts[i + 2];
					break;
				} else {
					st = sts[i + 1];
					break;
				}
			}
		}
		if (st == null) {
			return "";
		}

		String clsName = st.getClassName();
		if (clsName.contains("$")) {
			clsName = clsName.substring(clsName.lastIndexOf(".") + 1,
					clsName.indexOf("$"));
		} else {
			clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
		}
		return clsName + "-> " + st.getMethodName() + "():";
	}
}
