package com.rrgame.sdk.download;

import com.rrgame.sdk.systeminfo.AderLogUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 应用下载数据库表增删改查工具类
 * 
 * @author com.rrgame
 */
public class AderDownloadDao {

	private static AderDownloadDBHelper theDbHelper;
	
	private static AderDownloadDao theSharedDao;
	
	public static AderDownloadDao getSharedDao(Context context){
		if (theSharedDao==null) {
			synchronized (AderDownloadDao.class) {
				if (theSharedDao==null) {
					theSharedDao=new AderDownloadDao(context);
				}
			}
			
		}
		return theSharedDao;
	}
	
	private AderDownloadDao(Context context) {
		if (theDbHelper==null) {
			theDbHelper = new AderDownloadDBHelper(context,
					"Aderapkdownload.db", null, 1);
		}
	}

	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		theDbHelper.close();
		//theDbHelper=null;
	}

	/**
	 * 查询某应用的下载记录是否存在
	 * 
	 * @param item
	 *            应用信息
	 * */
	public synchronized boolean existDownloadRecord(AderDownloadItem item) {
		if (item != null) {
			SQLiteDatabase database = theDbHelper.getReadableDatabase();
			String sql = "select count(*)  from download_info where url=?";
			Cursor cursor = database.rawQuery(sql,
					new String[] { item.getUrl() });
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			cursor.close();
			database.close();
			return count > 0;
		}
		return false;
	}

	/**
	 * 插入某应用的下载记录信息
	 * 
	 * @param item
	 *            应用信息
	 * */
	public synchronized void insertDownloadRecord(AderDownloadItem item) {
		if (item != null) {

			SQLiteDatabase database = theDbHelper.getWritableDatabase();
			database.beginTransaction();
			try {
				String sql = "replace into download_info(total_size,complete_size,url) values (?,?,?)";
				Object[] bindArgs = { item.getTotalSize(), item.getCompleteSize(),
						item.getUrl() };
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				database.endTransaction();
			}
			database.close();
		}
	}

	/**
	 * 更新某应用的下载记录信息
	 * 
	 * @param item
	 *            应用信息
	 * */
	public synchronized void updateDownloadRecord(AderDownloadItem item) {
		if (item != null) {
			SQLiteDatabase database = theDbHelper.getReadableDatabase();
			database.beginTransaction();
			try {
				String sql = "update download_info set total_size=?, complete_size=? where url=?";
				Object[] bindArgs = { item.getTotalSize(), item.getCompleteSize(),item.getUrl() };
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			}
			database.endTransaction();
			database.close();
		}
	}

	/**
	 * 删除某应用的下载记录
	 * 
	 * @param item
	 *            应用信息
	 */
	public synchronized void deleteDownloadRecord(AderDownloadItem item) {
		if (item != null) {
			SQLiteDatabase database = theDbHelper.getReadableDatabase();
			database.beginTransaction();
			try {
				database.delete("download_info", "url=?",
						new String[] { item.getUrl() });
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			}
			database.endTransaction();
			database.close();
		}
	}


	/** 读取某应用的下载记录信息 */
	public synchronized AderDownloadItem readDownloadRecord(AderDownloadItem item) {
		if (item != null) {
			SQLiteDatabase database = theDbHelper.getReadableDatabase();
			String sql = "select total_size,complete_size from download_info where url=?";
			Cursor cursor = database.rawQuery(sql, new String[] {
					item.getUrl()});
			if (cursor.moveToFirst()) {
				long total=cursor.getLong(0);
				long complete=cursor.getLong(1);
				AderLogUtils.i("存在记录"+total+"#complete:"+complete);
				item.setTotalSize(total);
				item.setCompleteSize(complete);
				//LogUtils.d("文件信息 #final complete:"+item.getCompleteSize()+"#final total:"+item.getTotalSize());
			}
			cursor.close();
			database.close();
		}
		return item;
	}

}

