package com.rrgame.sdk.parameter.extend;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.text.TextUtils;
import android.webkit.WebView;

import com.rrgame.sdk.systeminfo.AderAppInfo;

public class MyParameterExtend {
	private static MyParameterExtend instance = null;
	/**config loaded repoclick中下发的p_开头的参数**/
	private HashMap<String, String> p_configMap = new  HashMap<String, String>();
	private HashMap<String, String> p_loadedMap = new  HashMap<String, String>();
	private HashMap<String, String> p_clickMap = new  HashMap<String, String>();
	
	   /**
		* 单例模式
	    */
	  public static MyParameterExtend getInstance() {  
		   	if(instance == null){
		   		synchronized(MyParameterExtend.class){
		   			if(instance == null){
		   				instance = new MyParameterExtend();
		   			}
		   		}
		   	}
		   	return instance;
	  }
	/**
	 * 将参数字段 map转换成url字符串
	 * @param paraMap
	 * @return
	 */
	public String buildParameterUrl(HashMap<String, String> paraMap) {
		String strUrl = "";
		Iterator<Entry<String, String>> entrySetIterator = paraMap.entrySet().iterator();
		while (entrySetIterator.hasNext()) {
			Entry<String, String> entry = entrySetIterator.next();
			strUrl = strUrl + entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue()) + "&";
		}
		if(strUrl.length() >= 1){
			strUrl = strUrl.substring(0, strUrl.length() - 1);
		}
		return strUrl;
	}
	
	/**
	 * 为数据请求构建url
	 * 
	 */
	public String buildRepoUrl(String address,HashMap<String, String> repoMap) {
		String strUrl = address + "?";
		if (repoMap == null) {
			return strUrl;
		}
		strUrl = strUrl + buildParameterUrl(repoMap);
		return strUrl;
	}
	
	/**
	 * 构建下发p_开头必须上报的参数字符串
	 * @return
	 */
	public String buildPdata(){
		String p_str = "";
		p_str = p_str + buildUtils(buildParameterUrl(p_configMap));
		p_str = p_str + buildUtils(buildParameterUrl(p_loadedMap));
		return p_str;
	}
	/**
	 * 构建下发p_开头click必须上报的参数字符串
	 * @return
	 */
	public String buildPClickdata(){
		String p_str = "";
		p_str = p_str + buildUtils(buildParameterUrl(p_clickMap));
		return p_str;
	}
	
	public String buildUtils(String build_str){
		if(TextUtils.isEmpty(build_str)){
			return "";
		}
		return "&"+build_str;
	}
	
	 /**
     * 参数扩展调用js方法
     * @param view
     * @param js_name
     * @param repoMap
     */
	public void parameterDevelopJS(WebView view,String js_name,HashMap<String, String> configMap,HashMap<String, String> map){
    	
    	HashMap<String, String> p_map = new HashMap<String, String>();
		//获取rect_all参数
		String ret_all = map.get("ret_all");
    	if (configMap != null){
			Iterator<Entry<String, String>> entrySetIterator = configMap.entrySet().iterator();
			while (entrySetIterator.hasNext()) {
				Entry<String, String> entry = entrySetIterator.next();
				if(ret_all.equals("1")){//汇报所有参数
					p_map.put(entry.getKey(), entry.getValue());
                }
			    else if(ret_all.equals("0")){//汇报下发的参数
			    	//获取需要汇报的参数
			    	String p_report = map.get("ret_key");
                    String[] p_reports = p_report.split(",");
                    for(String tempstr:p_reports){
                    	if(tempstr.equals(entry.getKey())){
                    		p_map.put(entry.getKey(), entry.getValue());
                    	}
                    }
			    }
				
			}
		}
//    	view.loadUrl("javascript:"+js_name+"('"+str+"')");
    }
	/**
	 * 获取report url
	 * @param configMap
	 * @param map
	 * @return
	 */
	public String getReportUrl(HashMap<String, String> configMap,HashMap<String, String> map){
		String reportUrl = "";
		if(map != null){
			HashMap<String, String> p_map = new HashMap<String, String>();
			
//			//获取下发参数
			String args_str = map.get("args");
			String dargs_str = URLDecoder.decode(args_str);
			p_map.put("args", dargs_str);
			
			//获取rect_all参数
			String ret_all = map.get("ret_all");
			
			if (configMap != null){
				Iterator<Entry<String, String>> entrySetIterator = configMap
						.entrySet().iterator();
				while (entrySetIterator.hasNext()) {
					Entry<String, String> entry = entrySetIterator.next();
					if(ret_all.equals("1")){//汇报所有参数
						p_map.put(entry.getKey(), entry.getValue());
	                }
				    else if(ret_all.equals("0")){//汇报下发的参数
				    	//获取需要汇报的参数
				    	String p_report = map.get("ret_key");
	                    String[] p_reports = p_report.split(",");
	                    for(String tempstr:p_reports){
	                    	if(tempstr.equals(entry.getKey())){
	                    		p_map.put(entry.getKey(), entry.getValue());
	                    	}
	                    }
				    }
					
				}
			}
			p_map.put("aderTimestamp", ""+System.currentTimeMillis());
			p_map.put("f", AderAppInfo.channle_packageName);
			
//			//获取上报地址
			String url = map.get("url");
			
			reportUrl = buildRepoUrl(url,p_map);
		    
	  }
		return reportUrl;
	}
    
	public void updatePconfigMap(HashMap<String, String> p_map){
		if(p_configMap != null){
		   p_configMap.clear();
		   updatePMap(p_map,p_configMap);
		}
	}
	public void updatePloadedMap(HashMap<String, String> p_map){
		if(p_loadedMap != null){
    	   p_loadedMap.clear();
    	   updatePMap(p_map,p_loadedMap);
		}
	}
	public void updatePclickMap(HashMap<String, String> p_map){
		if(p_clickMap != null){
		   p_clickMap.clear();
    	   updatePMap(p_map,p_clickMap);
		}
    }
	
	public void clearPconfigMap(){
		if(p_configMap != null){
		   p_configMap.clear();
		}
	}
	public void clearPloadedMap(){
		if(p_loadedMap != null){
		   p_loadedMap.clear();
		}
	}
	public void clearPclickMap(){
		if(p_clickMap != null){
		   p_clickMap.clear();
		}
	}
	/**
	 * 更新map
	 * @param p_map
	 * @param pMap
	 */
	public void updatePMap(HashMap<String, String> p_map,HashMap<String, String> pMap){
		if(pMap != null&&p_map!=null){
			Iterator<Entry<String, String>> entrySetIterator = p_map.entrySet().iterator();
			while (entrySetIterator.hasNext()) {
				Entry<String, String> entry = entrySetIterator.next();
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.length() >= 2 && key.substring(0, 2).equals("p_")){
					pMap.put(key, value);
				}
			}
		}
	}
}
