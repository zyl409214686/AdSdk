package com.rrgame.sdk.download;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * 应用下载文件存取管理类
 * 
 * @author com.rrgame
 */
public class AderDownloadFileHolder {

	/** 保存下载应用的文件目录 */
	private File theCachePath;
	private Boolean isHasSDCard = true;

	// private static AderDownloadFileHolder theFileHolder;
	//
	// public static AderDownloadFileHolder getInstance(Context context) {
	// if (theFileHolder == null) {
	// theFileHolder = new AderDownloadFileHolder(context);
	// }
	// return theFileHolder;
	// }

	public AderDownloadFileHolder(Context context) {
		File appCacheDir = null;
		// 判断 SDCard 是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (Build.VERSION.SDK_INT >= 8) // API Level 8
			{
				Method method = null;
				try {
					method = context.getClass()
							.getMethod("getExternalCacheDir");
				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (method != null) {
					try {
						String cacheDirPath = method.invoke(context,
								new Object[] {}).toString();
						appCacheDir = new File(cacheDirPath);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						AderLogUtils.e(e.toString());
						// e.printStackTrace();
					}
				}
			} else {
				appCacheDir = new File(
						Environment.getExternalStorageDirectory(),
						"/Android/data/" + context.getPackageName() + "/cache/");
			}

		}
		if (appCacheDir == null || !appCacheDir.exists()
				|| !appCacheDir.isDirectory()) {
			AderLogUtils.i("没有检测到外部存储空间，使用内部存储空间进行Cache");
			appCacheDir = context.getCacheDir();
			isHasSDCard = false;
		} else {
			AderLogUtils.i("使用外部存储空间Cache");
		}
		theCachePath = new File(appCacheDir, "rrgamedownloadapks");
		if (!theCachePath.isDirectory()) {
			AderLogUtils.i("不是文件夹，创建下载文件夹");
			theCachePath.mkdirs();
		}
		AderLogUtils.i("创建文件地址" + theCachePath.getAbsolutePath());
		// 为防止修改不成功，每次均进行修改权限
		if (!isHasSDCard) {
			changeFileMode(theCachePath.getAbsolutePath(), "771");
			AderLogUtils.i("修改权限完成");
		}
	}

	/** 拼接断点续传文件 */
	private File getTempFile(String packageName) {
		return new File(theCachePath, packageName + ".temp");
	}

	/**
	 * 判断断点续传文件是否存在
	 * 
	 * */
	public boolean existFileOfApp(AderDownloadItem item) {
		if (item != null) {
			File cachefile = getTempFile(item.fileName());
			return cachefile.exists();
		}
		return false;
	}

	/**
	 * 删除某应用的下载文件
	 * 
	 * */
	public void deleteFileOfApp(AderDownloadItem item) {
		if (item != null) {
			final String packageName = item.fileName();
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return filename.startsWith(packageName);
				}

			};
			File[] files = theCachePath.listFiles(filter);
			for (int i = 0; i < files.length; i++) {
				File fileItem = files[i];
				fileItem.delete();
			}
		}
	}

	//
	public void deleteFile(File file) {
		if (file != null) {
			file.delete();
		}
	}

	/** 修改文件权限为可写 */
	public void changeFileToWritable(File file) {
		changeFileMode(file.getAbsolutePath(), "705");
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 *            要创建的文件
	 * @param length
	 *            文件内容的字节数
	 * @return 是否创建成功
	 * @throws IOException
	 */
	public boolean createNewFile(File file, long length) throws IOException {
		// 创建文件
		if (file.exists()) {
			deleteFile(file);
		}
		if (file.createNewFile()) {
			// if (!isHasSDCard) {
			// // 设置文件权限
			// changeFileToWritable(file);
			// }
			// 设置文件长度
			if (length > 0) {
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(length);
				accessFile.close();
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取某应用的下载文件
	 * 
	 * */
	public File getFileOfApp(AderDownloadItem item) {
		if (item != null) {
			return getTempFile(item.fileName());
		}
		return null;
	}

	/**
	 * 文件包下载成功后，把文件改名，去掉.temp后缀
	 * 
	 * @return 新路径
	 * 
	 * */
	public String renameDownloadFile(AderDownloadItem item) {
		if (item != null) {
			File cacheFile = getTempFile(item.fileName());
			if (cacheFile.exists()) {
				File newFile = new File(theCachePath, item.fileName());
				if (newFile != null && cacheFile.renameTo(newFile)) {
					if (!isHasSDCard) {
						changeFileMode(newFile.getAbsolutePath(), "705");
					}
					return newFile.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * 获取某应用的已经下载的字节数 如果文件不存在，返回0
	 * 
	 * @param 应用信息
	 * 
	 * */
	public int getFileSizeOfApp(AderDownloadItem item) {
		if (item != null) {
			File cachefile = getTempFile(item.fileName());
			if (cachefile.exists()) {
				// cachefile.;
			}
		}
		return 0;
	}

	/**
	 * 更改文件权限
	 * 
	 * @param filePath
	 *            文件路径
	 * @param mode
	 *            目标权限，如705,777等
	 * */
	public boolean changeFileMode(String filePath, String mode) {
		boolean result = false;

		try {
			AderLogUtils.e("尝试更改权限");
			Process process = Runtime.getRuntime().exec(
					"chmod " + mode + " " + filePath);
			try {
				AderLogUtils.e("尝试更改权限");
				result = process.waitFor() == 0;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				AderLogUtils.e("线程被打断");
				e.printStackTrace();
			}

			// process.destroy();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			AderLogUtils.e("IO异常");
			e.printStackTrace();
		}
		return result;
	}

}
