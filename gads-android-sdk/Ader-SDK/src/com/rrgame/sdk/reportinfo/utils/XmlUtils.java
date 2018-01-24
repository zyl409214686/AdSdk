package com.rrgame.sdk.reportinfo.utils;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.rrgame.sdk.reportinfo.data.CollectInfoConfigItem;
import com.rrgame.sdk.reportinfo.data.PostBackItem;

public class XmlUtils {
/**
 * 解析收集信息config信息
 * @param ainputstream
 * @return
 */
   public static CollectInfoConfigItem getInfoConfigData(InputStream ainputstream) {
		
	    CollectInfoConfigItem mConfigItem = new CollectInfoConfigItem();
		XmlPullParser mXmlPull = Xml.newPullParser();
 
		try {

			mXmlPull.setInput(ainputstream, "UTF-8");
			int eventCode = mXmlPull.getEventType();
			 
			while (eventCode != XmlPullParser.END_DOCUMENT) {

				switch (eventCode) {
				case XmlPullParser.START_DOCUMENT: // 文档开始事件

					break;

				case XmlPullParser.START_TAG:// 元素开始.
					String name = mXmlPull.getName();
					if (name.equalsIgnoreCase("code")) {
						mConfigItem.setCode(Integer.parseInt(mXmlPull.nextText()));
					}
					else if (name.equalsIgnoreCase("a")) {
						mConfigItem.setIsCollectInfo(Integer.parseInt(mXmlPull.nextText()));
					} else if (name.equalsIgnoreCase("kv")) {
						mConfigItem.setNewKeyVersion(Integer.parseInt(mXmlPull.nextText()));
					} else if (name.equalsIgnoreCase("k")) {
						mConfigItem.setNewKey(mXmlPull.nextText());
					} else if (name.equalsIgnoreCase("u")) {
						mConfigItem.setPostUrl(mXmlPull.nextText());
					}else if (name.equalsIgnoreCase("nh")) {
						mConfigItem.setNextHour(Integer.parseInt(mXmlPull.nextText()));
					}
					break;

				case XmlPullParser.END_TAG: // 元素结束,

					break;
				default:
					break;
				}
				eventCode = mXmlPull.next();// 进入到一下一个元素.
			}
		} catch (XmlPullParserException e) {
			
			

		} catch (IOException e) {


		} catch (Exception e) {

		}
		return mConfigItem;
	}
   
   public static PostBackItem getPostBackData(InputStream ainputstream) {
		
	   PostBackItem postBackItem = new PostBackItem();
		XmlPullParser mXmlPull = Xml.newPullParser();

		try {

			mXmlPull.setInput(ainputstream, "UTF-8");
			int eventCode = mXmlPull.getEventType();
			 
			while (eventCode != XmlPullParser.END_DOCUMENT) {

				switch (eventCode) {
				case XmlPullParser.START_DOCUMENT: // 文档开始事件

					break;

				case XmlPullParser.START_TAG:// 元素开始.
					String name = mXmlPull.getName();
					if (name.equalsIgnoreCase("code")) {
						postBackItem.setCode(Integer.parseInt(mXmlPull.nextText()));
					}
					break;

				case XmlPullParser.END_TAG: // 元素结束,

					break;
				default:
					break;
				}
				eventCode = mXmlPull.next();// 进入到一下一个元素.
			}
		} catch (XmlPullParserException e) {
			
			

		} catch (IOException e) {


		} catch (Exception e) {

		}
		return postBackItem;
	}
  
}
