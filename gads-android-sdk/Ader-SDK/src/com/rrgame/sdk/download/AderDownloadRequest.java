package com.rrgame.sdk.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 执行单个下载过程工具类
 * 
 * @author com.rrgame
 */
public class AderDownloadRequest {

	/** 数据库访问类 */
	private AderDownloadDao theDao;
	/** 下载文件存取工具类 */
	private AderDownloadFileHolder theFileHolder;
	/** 通知栏管理器 */
	private NotificationManager notificationManager;
	/** 通知id的基数 */
	private static final int notificationBase = 3015000;
	/** http请求超时时长 15秒 */
	private static final int httptimeout = 30000;
	/** 通知id的增量 */
	private static final AtomicInteger notificationCount = new AtomicInteger(1);
	private Notification notification;
	private AderDownloadItem downloadItem;
	private static AderDownloadService downlaodService;
	/** 通知id */
	private int notificationId;
	/** 执行下载的次数，如果失败3次，放弃下载 */
	private int downlaodtimes = 0;
	 
	/** 下载connection */
	HttpURLConnection connection=null;
	
	public AderDownloadRequest(AderDownloadService downlaodService,
			AderDownloadItem item) {
		AderLogUtils.i("AderDownloadRequest new");
		AderDownloadRequest.downlaodService = downlaodService;
		theDao = AderDownloadDao.getSharedDao(downlaodService);
		theFileHolder =new AderDownloadFileHolder(downlaodService);
		AderLogUtils.i("theFileHolder new");
		notificationManager = (NotificationManager) downlaodService
				.getSystemService(Context.NOTIFICATION_SERVICE);
		this.downloadItem = item;
		notification = new Notification(android.R.drawable.stat_sys_download,
				"开始下载", System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_LIGHTS;
		//判断是否断点
		try {
			checkDBRecord();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notificationId = notificationBase
				+ notificationCount.getAndIncrement();
		// 重置notificationCount，避免无限增长
		if (notificationCount.get() > 10000) {
			notificationCount.set(1);
		}
	}

	/**
	 * 修改downloadItem和数据库中的记录
	 * */
	private void updateDBRecord(long complete){
		downloadItem.setCompleteSize(complete);
		theDao.updateDownloadRecord(downloadItem);
	}
	
	/**
	 * 读取DB记录，判断是否断点续传
	 * */
	private void checkDBRecord() throws Exception{
		downloadItem = theDao.readDownloadRecord(downloadItem);
		File theTempFile = null;
		if (downloadItem.getCompleteSize() <= 0) {// 不是断点续传
			theDao.insertDownloadRecord(downloadItem);
			theTempFile = theFileHolder.getFileOfApp(downloadItem);
			
			theFileHolder.createNewFile(theTempFile,
					downloadItem.getTotalSize());
		} else {// 断点续传
			AderLogUtils.i("断点续传url:" + downloadItem.getUrl() + "#totalSize:"
					+ downloadItem.getTotalSize() + "#completeSize:"
					+ downloadItem.getCompleteSize());
			// 检查本地文件是否存在
			theTempFile = theFileHolder.getFileOfApp(downloadItem);
			if (theTempFile.exists()) {
//				theFileHolder.changeFileMode(theTempFile.getAbsolutePath(),
//						"705");
			} else {
				updateDBRecord(0);
				theFileHolder.createNewFile(theTempFile,
						downloadItem.getTotalSize());
			}
		}
	}
	
	/** 一个下载回合 */
	private boolean downloadProcess(){
		AderLogUtils.e("开始一个下载回合"); 
		//Toast.makeText(downlaodService.getApplicationContext(), "开始下载",Toast.LENGTH_SHORT).show();
		boolean success=false;
		long completeSize = 0;
		long totalSize=downloadItem.getTotalSize();
		URL url;
		String newPath = null;
		HttpURLConnection connection=null;
		InputStream inputStream = null;
		BufferedOutputStream bout = null;
		try {
			url = new URL(downloadItem.getUrl());
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(httptimeout);
			connection.setReadTimeout(httptimeout);
			connection.setRequestMethod("GET");
			if (totalSize<=0) {
				totalSize = connection.getContentLength();
				downloadItem.setTotalSize(totalSize);
			}
		} catch (Exception e) {
            if (connection!=null) {
            	connection.disconnect();
			}
            e.printStackTrace();
            return success;
		} 
		if (totalSize <= 0) {
			totalSize = -1;
		}
		
		
		try {
			File theTempFile = theFileHolder.getFileOfApp(downloadItem);
			
			completeSize = (long) theTempFile.length();
			
			int originPercent = 0;
			if (totalSize>0&&completeSize>0) {
				connection.setRequestProperty(
						"Range",
						"bytes=" + (completeSize) + "-"
								+ downloadItem.getTotalSize());
				
				originPercent = (int) (((double) (completeSize) / totalSize) * 100);
			}
			notification.setLatestEventInfo(downlaodService,
					downloadItem.getAppName(), "已完成" + originPercent + "%",
					downlaodService.getPendingIntent());
			// 将下载任务添加到任务栏中
			notificationManager.notify(notificationId, notification);
			startForeground();
			
			inputStream = connection.getInputStream();
			FileOutputStream fo = new java.io.FileOutputStream(theTempFile,true);
			bout = new BufferedOutputStream(fo,4096*4); 
			
			//开始下载
			byte[] buffer = new byte[4096*4];
			int length = -1;
			int percent = 0;
			int origin_percent = 0;
			while ((length = inputStream.read(buffer)) != -1) {
				bout.write(buffer,0,length);
				completeSize += length;
				// 更新数据库中的下载信息，0为线程编号，为以后多线程做扩展参数
				updateDBRecord(completeSize);
				if (totalSize > 0) {
					percent = (int) (((double) (completeSize) / totalSize) * 100);
				}
				
				if (percent > 100)
				{
					notificationManager.cancel(notificationId);
				}
				// 每下载完成1%就通知任务栏进行修改下载进度
				if (percent - origin_percent >= 1) {
					origin_percent = percent;
					notification.setLatestEventInfo(downlaodService,
							downloadItem.getAppName(), "已完成" + percent
									+ "%", downlaodService.getPendingIntent());
					// 将下载任务添加到任务栏中
					notificationManager.notify(notificationId, notification);
					AderLogUtils.d("文件下载进度", downloadItem.getAppName() + "#:"
							+ percent + " totalSize:" + String.valueOf(totalSize) + " completeSize:" + String.valueOf(completeSize));
				}
			}
			if (percent == 100 || completeSize == downloadItem.getTotalSize()
					|| downloadItem.getTotalSize() == -1) {
				success=true;
				newPath = theFileHolder.renameDownloadFile(downloadItem);
				theDao.deleteDownloadRecord(downloadItem);
				
			}
		}catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (connection != null) 
			{
				connection.disconnect();
			}
			connection = null;
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			inputStream = null;
			
			if (bout != null)
			{
				try {
					bout.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			} 
			
			if (success) 
			{
				// 安装应用
				installApp(newPath);
			}
		}
		return success;
	}

	/** 下载失败有3次机会重下 */
	public boolean startDownload() throws Exception {
		boolean downFlished = false;
		while(downlaodtimes <3&&!(downFlished=downloadProcess()))
		{
			downlaodtimes ++;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		notificationManager.cancel(notificationId);
		stopForeground();
		return downFlished;
	}

	/** 安装文件 */
	private void installApp(String filePath) {
		if (downloadItem.isAutoInstall() && downlaodService != null) {
			Intent intent1 = new Intent(Intent.ACTION_VIEW);
			File file = new File(filePath);
			if(file != null){
				intent1.setDataAndType(Uri.fromFile(file),
	                    "application/vnd.android.package-archive");
				intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				downlaodService.startActivity(intent1);
			}
		}
	}
	
	/** 设置service 为Foreground 状态*/
	private void startForeground(){
		int version = 0;
	       try {
	    	   version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
	       } catch (NumberFormatException e) {
	    	   version = 0;
	       }
		if(version>=5){
			try {
				Class<?> rel = null;
				rel = Class.forName("android.app.Service");
				 Method method = rel.getDeclaredMethod("startForeground", new Class[]{int.class, Notification.class});
		         method.invoke(downlaodService,new Object[]{notificationId, notification});
			 }catch (Exception e) {
				 e.printStackTrace();
			 }
		}
		else{
			downlaodService.setForeground(true);
		}
	}
	
	/**  设置service 为Background状态*/
	private void stopForeground(){
		int version = 0;
	       try {
	    	   version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
	       } catch (NumberFormatException e) {
	    	   version = 0;
	       }
		if(version>=5){
			try {
				Class<?> rel = null;
				rel = Class.forName("android.app.Service");
				 Method method = rel.getDeclaredMethod("stopForeground", new Class[]{boolean.class});
		         method.invoke(downlaodService,new Object[]{true});
			 }catch (Exception e) {
				    e.printStackTrace();
			 }
		}
		else{
			downlaodService.setForeground(false);
		}
	}
}
