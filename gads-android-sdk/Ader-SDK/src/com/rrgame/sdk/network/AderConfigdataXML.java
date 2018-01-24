package com.rrgame.sdk.network;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.rrgame.sdk.meta.AderConfigItem;
import com.rrgame.sdk.systeminfo.AderLogUtils;

/**解析获取配置请求返回的xml数据
 * 解析由AderXMLDataNetwork获取到的xml数据
 * 解析后以AderConfigItem类型返回
 * 
 * @author com.rrgame
 * 
 */
public class AderConfigdataXML {

	public AderConfigItem getConfigData(InputStream ainputstream) {
		
		AderConfigItem mConfigItem = null;
		XmlPullParser mXmlPull = Xml.newPullParser();
//		HashMap<String, String> p_develop = new HashMap<String, String>();
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

					else if (mConfigItem != null) {
						String value = "";
						if (name.equalsIgnoreCase("tout_count")) {
							value = mXmlPull.nextText();
							mConfigItem.setTouchCount(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("tout_time")) {
							value = mXmlPull.nextText();
							mConfigItem.setTouchTime(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("tout_wait_time")) {
							value = mXmlPull.nextText();
							mConfigItem.setTouchWaitTime(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("valshw_time")) {
							value = mXmlPull.nextText();
							mConfigItem.setValidShowTime(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("getad_freq")) {
							value = mXmlPull.nextText();
							mConfigItem.setGetAdFreq(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("js_addr")) {
							value = mXmlPull.nextText();
							mConfigItem.setJsAddress(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("getad_host")) {
							value = mXmlPull.nextText();
							mConfigItem.setGetAdHost(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("getad_path")) {
							value = mXmlPull.nextText();
							mConfigItem.setGetAdPath(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("repo_addr")) {
							value = mXmlPull.nextText();
							mConfigItem.setRepoAddresse(value);
//							p_develop.put(name, value);
						} else if (name.equalsIgnoreCase("ad_animation")) {
//							value = mXmlPull.nextText();
							mConfigItem.setAd_animation(value);
//							p_develop.put(name, value);
						}else if (name.equalsIgnoreCase("ad_switch")) {
							value = mXmlPull.nextText();
							mConfigItem.setAd_switch(value);
//							p_develop.put(name, value);
						}else if (name.equalsIgnoreCase("getlbs_time")) {
							value = mXmlPull.nextText();
							mConfigItem.setLbs_time(Long.parseLong(value));
//							p_develop.put(name, value);
						}
						else {
							// throw
//							if(name.length() >= 2 && name.substring(0, 2).equals("p_")){
//								value = mXmlPull.nextText();
//								p_develop.put(name, value);
//							}
						}
						
					}
					break;

				case XmlPullParser.END_TAG: // 元素结束,

					break;
				default:
					break;
				}
				eventCode = mXmlPull.next();// 进入到一下一个元素.
			}
//			AderParameterDevelop.getInstance().updatePconfigMap(p_develop);
		} catch (XmlPullParserException e) {
			
			AderLogUtils.d(e.toString());

		} catch (IOException e) {

			AderLogUtils.d(e.toString());

		} catch (Exception e) {

			AderLogUtils.d(e.toString());
		}
		return mConfigItem;
	}

}
