package com.rrgame.sdk.adutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.rrgame.sdk.systeminfo.AderAppInfo;
import com.rrgame.sdk.systeminfo.AderLogUtils;

public class AderPublicUtils {
	/** SDK版本号，升级时需要修改 */
	public final static String SDKVERSION = "1.1.2";
	 
	public static final Boolean DEBUG = false;
	
	public static final String bannerConfigHead = DEBUG?"http://test.tech6.rrgdev.com/banner/test/api/confg/init":"http://adbc.renren.com/confg/init";
	//lbs测试地址:"http://10.248.50.56:8087/full/config/init"; 
	public static final String interstitialConfigHead = DEBUG?"http://test.tech6.rrgdev.com/adfull/dev/api/full/config/init":"http://interval.adbc.renren.com/full/config/init";
//	public static final String interstitialConfigHead = "http://10.248.51.50:8087/full/config/init";
	/**webview通信状态枚举*/
	public enum WebStatus { 
		SUCCESS_WEBVIEW_JS, FAILED_WEBVIEW_JS, GETAMSG_WEBVIEW_JS, CLOSE_WEBVIEW_JS;
	}

	/**广告类型*/
	public enum Webview_Type {
		// 广告webview
		web_small_type,
		// 弹出的大webview
		web_big_type
	}
	
	private static final String emailHintTitle = "请选择邮箱";
	
	
	/**
	 * 跳转到email
	 * 
	 */
	public static void openEmail(Context context,HashMap<String, String> map) {
		if (context==null) {
			AderLogUtils.w("context is null");
			return;
		}
		String title = map.get("title");
		String msg = map.get("msg");
		String consignee = map.get("consignee");
		AderLogUtils.d("consignee--->" + consignee);

		if (title != null && msg != null && consignee != null) {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			// 设置文本格式
			emailIntent.setType("plain/text");
			// 设置对方邮件地址
			String[] reciver = new String[] { consignee };
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
			// 设置标题内容
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
			// 设置邮件文本内容
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
			// 启动一个新的ACTIVITY,"Sending mail..."是在启动这个ACTIVITY的等待时间时所显示的文字
			try {
				context.startActivity(Intent.createChooser(emailIntent,
						emailHintTitle));
			} catch (Exception e) {
				Toast.makeText(context, "email不可用", Toast.LENGTH_SHORT).show();
				return;
			}

		}
	}
	
	/**
	 *发短信 
	 * 
	 */
	public static void sendSMS(Context context,String phone,String msg){
		if (context==null) {
			AderLogUtils.w("context is null");
			return;
		}
		Intent intent = new Intent(Intent.ACTION_SENDTO,
				Uri.parse("smsto:" + phone));
		intent.putExtra("sms_body", msg);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "短信不可用", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	/**
	 *打电话
	 * 
	 */
	public static void phoneCall(Context context,String phone){
		if (context==null) {
			AderLogUtils.w("context is null");
			return;
		}
		Intent intentTelephony = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + phone));
		intentTelephony.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intentTelephony);
		} catch (Exception e) {
			Toast.makeText(context, "电话不可用", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}
	
	/**
	 *打开地图
	 */
	public static void openMap(Context context,String latitude,String longitude){
		if (context==null) {
			AderLogUtils.w("context is null");
			return;
		}
		try {
			// Class.forName("com.google.android.maps");
			Uri uri = Uri
					.parse("geo:" + latitude + "," + longitude);
			Intent intentMap = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(intentMap);
		} catch (Exception e) {
			Toast.makeText(context, "地图不可用", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	/**
	 * 跳转到Android Market 或者打开浏览器
	 * 
	 */
	public static void openUrl(Context context,HashMap<String, String> map) {
		if (context==null) {
			AderLogUtils.w("context is null");
			return;
		} 
		String strUrl = map.get("url");
		if (strUrl != null) {
			Uri uri = Uri.parse(strUrl);
			if(!"no".equals(map.get("encode"))){
				if(strUrl.indexOf("?")>0&&!TextUtils.isEmpty(uri.getQuery())){
					String domainStr = strUrl.substring(0, strUrl.indexOf("?"));
					List<NameValuePair> paramList = buildListForWebView(uri.getEncodedQuery());
					strUrl = domainStr+"?"+URLEncodedUtils.format(paramList, "UTF-8");
				}
			}
			String urlString = "^(http|https|ftp)://[/\\w\\.\\-\\+\\?%=&;:,#]+";
			Pattern urlPattern = Pattern.compile(urlString);
			Matcher urlMatcher = urlPattern.matcher(strUrl);
			if(!urlMatcher.find()){
				strUrl = "http://" + strUrl;
			}
			AderLogUtils.d("System.out", "打开浏览器" + uri.getPath());
			try {
				uri = Uri.parse(strUrl);
				Intent intentBrowser = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(intentBrowser);
			} catch (Exception e) {
				 Toast.makeText(context, "浏览器不可用", Toast.LENGTH_SHORT)
				 .show();
				return;
			}
		}
	}
	
	/**
	 * 将url.query解析成list
	 * 
	 */

	private static List<NameValuePair> buildListForWebView(String str) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();  
		String[] array = str.split("&");
		for (int i = 0; i < array.length; i++) {
			String[] arrayTemp = array[i].split("=");
			if (arrayTemp.length > 1) { 
					formParams.add(new BasicNameValuePair(arrayTemp[0], java.net.URLDecoder.decode(arrayTemp[1])));   
			}
		}
		return formParams;
	}
	
	
	/**
	 * 添加日历提醒
	 * 
	 */
	public static void addCalendar(Context context,HashMap<String, String> map) {
		if (context==null) {
			AderLogUtils.w("context is null");
			return;
		}
		String title = map.get("title");
		String notes = map.get("notes");
		String startDate = map.get("startDate");
		String endDate = map.get("endDate");
		String warnTime = map.get("warnTime");

		if (title != null && notes != null && startDate != null
				&& endDate != null && warnTime != null) {
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", Long.valueOf(startDate));
			intent.putExtra("rrule", "FREQ=YEARLY");
			intent.putExtra("endTime", Long.valueOf(endDate));
			intent.putExtra("title", title);
			intent.putExtra("description", notes);
			try {
				context.startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(context, "日历提醒不可用", Toast.LENGTH_SHORT).show();
				return;
			}

		}
	}
	
	/**
	 * 获得application数据标签的值
	 * @param context
	 * @param name
	 * @return
	 */
	public static String getAppLicationMetaData(Context context,String name){
		ApplicationInfo appInfo = null;
		String metaDataStr = null;
		Context appContext = context.getApplicationContext();
		try {
			appInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(),PackageManager.GET_META_DATA);
			if(appInfo != null&&appInfo.metaData!=null){
			   metaDataStr = String.valueOf(appInfo.metaData.get(name));
			}
			else{
				Log.e(AderLogUtils.APP_NAME, "数据标签未正确设置，应放到applaction下");
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metaDataStr;
	}
	/**
	 * 获取调用该方法的类和方法数据
	 */
	 public static void getCaller(){  
		   StackTraceElement stack[] = (new Throwable()).getStackTrace();  
		   if(stack.length > 2){
			   StackTraceElement ste = stack[2];
			   String channelStr = ste.getClassName()+"."+ste.getMethodName();
			   AderAppInfo.channle_packageName = channelStr;
		   }
	 }

}
