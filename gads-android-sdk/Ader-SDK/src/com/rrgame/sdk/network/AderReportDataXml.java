package com.rrgame.sdk.network;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.rrgame.sdk.meta.AderConfigItem;

import android.util.Xml;

/**解析汇报事件返回的xml数据
 * 解析由AderXMLDataNetwork获取到的xml数据
 * 解析后以AderConfigItem类型返回
 * 
 * @author com.rrgame
 * 
 */
public class AderReportDataXml {
	// private static final String XMLPULL_INFO = "XmlPull_Info";

	public AderConfigItem getConfigData(InputStream ainputstream) {
		AderConfigItem mConfigItem = null;
		// InputStream in =null;
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
					if (name.equalsIgnoreCase("state")) {
						mConfigItem = new AderConfigItem();
					} else if (name.equalsIgnoreCase("code")) {
						mConfigItem.setCode(mXmlPull.nextText());
					} else if (name.equalsIgnoreCase("desc")) {
						mConfigItem.setDesc(mXmlPull.nextText());
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

}
