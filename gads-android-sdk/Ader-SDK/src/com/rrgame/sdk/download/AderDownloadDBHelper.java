package com.rrgame.sdk.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 应用下载数据库表工具类
 * 
 * @author com.rrgame
 */
public class AderDownloadDBHelper extends SQLiteOpenHelper {

	public AderDownloadDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * total_size 下载包总字节数
	 * complete_size 已经下载的字节数
	 * url 下载地址
	 * 
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, "
                + "total_size integer, complete_size integer,url char)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
