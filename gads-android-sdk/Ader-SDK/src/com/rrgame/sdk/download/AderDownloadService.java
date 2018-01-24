package com.rrgame.sdk.download;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 应用下载安装引擎
 * 
 * @author com.rrgame
 */
public class AderDownloadService extends Service {

	private static Set<String> cachingTasks = new HashSet<String>();
	/**
	 * 同时运行线程最大值 最小为1，最大为5
	 * */
	private int maxThreadCount;
	/** 线程运行Service */
	private static ExecutorService downloadExecutor;

	/** 下载队列 capacity队列长度，超过capacity的 */
	private static BlockingQueue<Runnable> downloadQueue = new LinkedBlockingQueue<Runnable>(
			3);

	private static final ThreadFactory threadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "Download Async #"
					+ mCount.getAndIncrement());
			if (mCount.get() >= 10000) {
				mCount.set(1);
			}
			thread.setDaemon(false);
			return thread;
		}
	};

	/** 点击通知栏跳到相应的应用中 */
	private PendingIntent pendingIntent;

	public int getMaxThreadCount() {
		return maxThreadCount;
	}

	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}

	public void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		AderLogUtils.i("");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		AderLogUtils.i("AderDownloadService onCreate");
		try {
			pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		} catch (Exception e) {
			e.printStackTrace();
			pendingIntent = null;
		}
		// 设置点击通知栏时应该跳转到的app
		/*
		 * ActivityManager am = (ActivityManager)
		 * getSystemService(Context.ACTIVITY_SERVICE); Activity activity = null;
		 * pendingIntent = null; List<RunningTaskInfo> runningTasks=null; try {
		 * runningTasks = am.getRunningTasks(1); RunningTaskInfo rti =
		 * runningTasks.get(0); ComponentName component = rti.topActivity; try {
		 * activity = (Activity) (Class.forName(component.getClassName())
		 * .newInstance()); } catch (IllegalAccessException e) {
		 * e.printStackTrace(); } catch (InstantiationException e) {
		 * e.printStackTrace(); } catch (ClassNotFoundException e) {
		 * e.printStackTrace(); } if (activity != null) { pendingIntent =
		 * PendingIntent.getActivity(this, 0, new Intent(this,
		 * activity.getClass()), 0); } else {
		 * AderLogUtils.i("activity is null"); } } catch (Exception e) {
		 * 
		 * }
		 */
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		AderLogUtils.i("onStartCommand");
		startDownLoadService(intent);
		return 1;
	}

	public void startDownLoadService(Intent intent) {
		AderLogUtils.i("startDownLoadService");
		if (intent != null) {
			AderLogUtils.i("has intent");
			AderDownloadItem item = intent.getParcelableExtra("downloadItem");
			if (item != null) {
				if (downloadExecutor == null) {
					maxThreadCount = intent.getIntExtra("concurrentThreads", 3);
					if (maxThreadCount < 1) {
						maxThreadCount = 1;
					} else if (maxThreadCount > 5) {
						maxThreadCount = 5;
					}
					downloadExecutor = new ThreadPoolExecutor(maxThreadCount,
							maxThreadCount + 1, 1, TimeUnit.SECONDS,
							downloadQueue, threadFactory,
							new ThreadPoolExecutor.DiscardOldestPolicy());
				}
				String url = item.getUrl();
				AderLogUtils.i("threads:" + maxThreadCount + "#url:" + url);
				// LogUtils.d("cachingTasks:" + cachingTasks);
				synchronized (cachingTasks) {
					if (!cachingTasks.contains(url)) {
						AderLogUtils.i("开始下载");
						cachingTasks.add(url);
						addDownloadTask(item);
					}
				}
			}
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		try {
			Service.class.getMethod("onStartCommandMethod", new Class[] {
					Intent.class, int.class, int.class });
		} catch (Exception e1) {
			startDownLoadService(intent);
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AderLogUtils.i("onStart");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		AderLogUtils.i("");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AderLogUtils.i("");
	}

	/**
	 * 下载应用
	 * 
	 * @param item
	 *            应用信息
	 * */
	private void addDownloadTask(final AderDownloadItem item) {
		// 加入线程池
		downloadExecutor.execute(new Runnable() {

			@Override
			public void run() {
				AderDownloadRequest downloadItem = new AderDownloadRequest(
						AderDownloadService.this, item);
				try {
					downloadItem.startDownload();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// LogUtils.d("cachingTasks2:" + cachingTasks);
				synchronized (cachingTasks) {
					cachingTasks.remove(item.getUrl());
				}
				// LogUtils.d("cachingTasks3:" + cachingTasks);
			}
		});
	}

}
