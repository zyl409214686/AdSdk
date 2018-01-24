package com.rrgame.sdk.download;

import com.rrgame.sdk.systeminfo.AderMD5;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 应用下载项，标识下载信息
 * 
 * @author com.rrgame
 */
public class AderDownloadItem implements Parcelable{

	/**应用id标识*/
	private String id;
	/** 应用下载地址 */
	private String url;
	/** 应用包名 */
	private String appName;
	/** 应用文件名 */
	private String fileName;
	/**下载包总大小*/
	private long totalSize;
	/**已下载文件大小*/
	private long completeSize;
	/**是否自动安装，默认自动安装*/
	private boolean autoInstall;

	/**下载进度广播最小增量，即下载完成多少百分比发出进度改变广播
	 * 默认最小为1个百分点，可根据需要修改
	 * */
	private int  minPercent;

	public AderDownloadItem(String id,String url, String appName) {
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(appName)) {
			throw new IllegalArgumentException();
		}
		this.id=id;
		this.url = url;
		this.appName = appName;
		this.autoInstall=true;
		this.minPercent=1;
		this.fileName = AderMD5.getMD5(url)+".apk";
	}

	public int getMinPercent() {
		return minPercent;
	}

	public void setMinPercent(int minPercent) {
		if (minPercent>0&&minPercent<=100) {
			this.minPercent = minPercent;
		}
	}

	public String getId() {
		return id;
	}
	
	public String fileName() {
		return fileName;
	}
	
	public String getUrl() {
		return url;
	}

	public String getAppName() {
		return appName;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(long completeSize) {
		this.completeSize = completeSize;
	}

	public boolean isAutoInstall() {
		return autoInstall;
	}

	public void setAutoInstall(boolean autoInstall) {
		this.autoInstall = autoInstall;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(url);
		dest.writeString(appName);
		dest.writeLong(totalSize);
		dest.writeLong(completeSize);
		dest.writeByte((byte)(autoInstall?1:0));
	}

	public static final Parcelable.Creator<AderDownloadItem> CREATOR = new Parcelable.Creator<AderDownloadItem>() {

		@Override
		public AderDownloadItem createFromParcel(Parcel source) {
			AderDownloadItem item= new AderDownloadItem(source.readString(),source.readString(),source.readString());
			item.setTotalSize(source.readLong());
			item.setCompleteSize(source.readLong());
			item.setAutoInstall(source.readByte()==1);
			return item;
		}

		@Override
		public AderDownloadItem[] newArray(int size) {
			return new AderDownloadItem[size];
		}
	};
}
