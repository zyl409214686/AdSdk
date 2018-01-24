package com.rrgame.sdk.reportinfo.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rrgame.sdk.systeminfo.AderLogUtils;

public class HttpHelp {
	private static int TIME_OUT = 20000;
	private static final int HTTP_PORT = 8081;
	
	/**
	 * 上报数据
	 * @param url
	 * @param body
	 */
	public static InputStream postHttpStream(Context con,String url,HashMap<String, String> body){
		AderLogUtils.d("上报URL="+url);
		InputStream iBackStream = null;
		//设置超时
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, TIME_OUT);
		
		HttpClient httpClient = new DefaultHttpClient(params);
		//如果是2G网络用代理
		String proxyStr = getProxyUrl(con);
		if (proxyStr != null && proxyStr.trim().length() > 0) {
		    HttpHost httpHost = new HttpHost(proxyStr, HTTP_PORT);  
		    httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,httpHost);  
		}
		//
		HttpPost request = new HttpPost(url);
		//建立HttpPost对象
		List<NameValuePair> pairList=new ArrayList<NameValuePair>();
		//建立一个NameValuePair数组，用于存储欲传送的参数
		Iterator<?> iter = body.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Entry<?, ?> entry = (Entry<?, ?>) iter.next(); 
		    String key = (String)entry.getKey(); 
		    String val = (String)entry.getValue(); 
		    pairList.add(new BasicNameValuePair(key,val));
		    AderLogUtils.d("key="+key+":val="+val);
		} 
		try {
			UrlEncodedFormEntity enEntity = new UrlEncodedFormEntity(pairList,HTTP.UTF_8);
			request.setEntity(enEntity);
			HttpResponse response = httpClient.execute(request);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null && statusLine.getStatusCode() == 200) {//如果状态码为200,就是正常返回
				HttpEntity entity = response.getEntity();
				iBackStream = entity.getContent();
			} 
		} catch (Exception e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iBackStream;
	}
	
	/**
	 * 获取http请求数据
	 * @param con
	 * @param url
	 * @return
	 */
	public static InputStream getHttpStream(Context con,String url){ 
		InputStream iBackStream = null;
		//设置超时
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, TIME_OUT);
		
		HttpClient httpClient = new DefaultHttpClient(params);
		//设置代理//如果是2G网络用代理
		String proxyStr = getProxyUrl(con);
		if (proxyStr != null && proxyStr.trim().length() > 0) {
		    HttpHost httpHost = new HttpHost(proxyStr, HTTP_PORT);  
		    httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,httpHost);  
		}
		
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null && statusLine.getStatusCode() == 200){
				HttpEntity entity = response.getEntity();
				iBackStream = entity.getContent();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iBackStream;
	}
	
	/**
	 * 获取默认代理
	 * 
	 */
	public static String getProxyUrl(Context con) {

		String proxyUrl = null;
		if (!isWifi(con)) {
			proxyUrl = android.net.Proxy.getDefaultHost();
		}

		return proxyUrl;
	}
	
	/***
	 * 测试当前网络是否是wifi环境
	 * 
	 * @return
	 */
	public static boolean isWifi(Context con) {
		//user_permission_error
		ConnectivityManager connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo=null;
		try {
			activeNetInfo = connectivityManager.getActiveNetworkInfo();
		} catch (Exception e) {
			
		}
		return activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
}
