package com.rrgame.sdk.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.content.Context;

import com.rrgame.sdk.systeminfo.AderAppInfo;
import com.rrgame.sdk.systeminfo.AderLogUtils;

/**
 * XML数据连网方式
 * 
 * 网络请求类
 * 
 * @author com.rrgame
 * 
 */
public class AderXMLDataNetwork {

	/**超时时间，默认20秒*/
	public int TIME_OUT = 20000;
	private static final int HTTP_PORT = 8081;
	
	private Context mContext;
	
	public AderXMLDataNetwork(Context context) {
		mContext=context;
	}

	public InputStream getHttpStream(String paramString)
			throws SocketTimeoutException, MalformedURLException, IOException {

		URL localURL = new URL(paramString);

		HttpURLConnection localHttpURLConnection;

		String proxyStr = getProxyUrl();
		if (proxyStr != null && proxyStr.trim().length() > 0) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					proxyStr, HTTP_PORT));
			localHttpURLConnection = (HttpURLConnection) localURL
					.openConnection(proxy);
			AderLogUtils.e("proxy", proxyStr);
		} else {
			localHttpURLConnection = (HttpURLConnection) localURL
					.openConnection();
			AderLogUtils.e("no proxy");
			// 内部测试代理用
			// Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
			// "10.248.50.68", HTTP_PORT));
			// localHttpURLConnection = (HttpURLConnection) localURL
			// .openConnection(proxy);
			localHttpURLConnection
					.setRequestProperty("Accept-Encoding", "gzip");
		}

		localHttpURLConnection.setConnectTimeout(TIME_OUT);
		localHttpURLConnection.setReadTimeout(TIME_OUT);
		InputStream iStream = null;
		InputStream iBackStream = null;

		String encoding = localHttpURLConnection
				.getHeaderField("content-encoding");
		if (encoding != null && encoding.equals("gzip")) {
			iStream = localHttpURLConnection.getInputStream();
			iBackStream = getUnzippedStream(iStream);
		} else {
			iBackStream = localHttpURLConnection.getInputStream();
		}

		return iBackStream;

	}

	/**
	 * POST请求
	 * 
	 * @param POST_URL
	 * @param content
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	public boolean getUrlStatus(final String POST_URL) {

		try {
			URL url = new URL(POST_URL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(TIME_OUT);
			connection.setReadTimeout(TIME_OUT);
			int code = connection.getResponseCode();
			if (code == 200) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public URLConnection getHttpConnection(String paramString1,
			String paramString2) throws MalformedURLException, IOException {
		URL localURL = new URL(paramString1);

		HttpURLConnection localHttpURLConnection;
		try {
			String proxyStr = getProxyUrl();
			if (proxyStr != null && proxyStr.trim().length() > 0) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						proxyStr, HTTP_PORT));
				localHttpURLConnection = (HttpURLConnection) localURL
						.openConnection(proxy);
			} else {
				localHttpURLConnection = (HttpURLConnection) localURL
						.openConnection();

			}
			localHttpURLConnection.setRequestMethod(paramString2);
			localHttpURLConnection.setConnectTimeout(TIME_OUT);
			// int i1 = 5000;
			localHttpURLConnection.setReadTimeout(TIME_OUT);
			return localHttpURLConnection;
		} catch (SocketTimeoutException localSocketTimeoutException) {

		}
		return null;
	}
	 
	
	public URLConnection getHttpPostConnection(String paramString1,
			String paramString2, String paramString3)
			throws MalformedURLException, IOException {
		URL localURL = new URL(paramString1);
		HttpURLConnection localHttpURLConnection;
		try {
			localHttpURLConnection = (HttpURLConnection) localURL
					.openConnection();
			localHttpURLConnection.setRequestMethod(paramString2);
			localHttpURLConnection.setConnectTimeout(TIME_OUT);
			localHttpURLConnection
					.setRequestProperty("Accept-Encoding", "gzip");
			localHttpURLConnection.setReadTimeout(TIME_OUT);
			localHttpURLConnection.setDoInput(true);
			localHttpURLConnection.setDoOutput(true);
			OutputStream localOutputStream = localHttpURLConnection
					.getOutputStream();
			byte[] arrayOfByte = paramString3.getBytes("UTF-8");
			localOutputStream.write(arrayOfByte);
			localOutputStream.close();
			return localHttpURLConnection;
		} catch (SocketTimeoutException localSocketTimeoutException) {

		}
		return null;
	}

	/**
	 * 获取gzip解压流
	 * 
	 */
	public InputStream getUnzippedStream(InputStream ainputstream) {
		InputStream in = null;
		BufferedReader bf;
		try {
			bf = new BufferedReader(new InputStreamReader(new GZIPInputStream(
					ainputstream)));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = bf.readLine()) != null) {
				buffer.append(line);
			}

			String buff = buffer.toString();
			in = new ByteArrayInputStream(buff.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;

	}

	/**
	 * 获取默认代理
	 * 
	 */
	public String getProxyUrl() {

		String proxyUrl = null;
		if (!AderAppInfo.isWifi(mContext)) {
			proxyUrl = android.net.Proxy.getDefaultHost();
		}

		return proxyUrl;
	}

	public InputStream getHttpStreamFromPost(String address,
			HashMap<String, String> map) {
		InputStream iStream = null;
		InputStream iBackStream = null;

		URL url = null;
		try {
			// 构造一个URL对象
			url = new URL(address);
		} catch (MalformedURLException e) {
			AderLogUtils.e("", "IOException");
		}
		if (url != null) {
			try {
				// 使用HttpURLConnection打开连接
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				// 因为这个是post请求,设立需要设置为true
				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				// 设置以POST方式
				urlConn.setRequestMethod("POST");
				// Post 请求不能使用缓存
				urlConn.setUseCaches(false);
				urlConn.setInstanceFollowRedirects(true);
				// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
				// 要注意的是connection.getOutputStream会隐含的进行connect。
				urlConn.connect();
				// DataOutputStream流
				DataOutputStream out = new DataOutputStream(
						urlConn.getOutputStream());
				// 要上传的参数

				String content = "";

				for (Map.Entry<String, String> m : map.entrySet()) {
					content = content + m.getKey() + "="
							+ URLEncoder.encode(m.getValue(), "UTF-8") + "&";
				}

				if (content.length() > 0) {
					content = content.substring(0, content.length() - 1);
				}
				// 将要上传的内容写入流中
				out.writeBytes(content);

				// 刷新、关闭
				out.flush();
				out.close();
				// 获取数据
				String encoding = urlConn.getHeaderField("content-encoding");
				if (encoding != null && encoding.equals("gzip")) {
					iStream = urlConn.getInputStream();
					iBackStream = getUnzippedStream(iStream);
				} else {
					iBackStream = urlConn.getInputStream();
				}

				return iBackStream;
			} catch (IOException e) {
				AderLogUtils.e("", "IOException");
			}
		} else {
			AderLogUtils.e("", "IOException");
		}

		return iBackStream;
	}
}
