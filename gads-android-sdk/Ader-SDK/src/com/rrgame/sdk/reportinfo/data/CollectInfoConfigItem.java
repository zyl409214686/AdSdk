package com.rrgame.sdk.reportinfo.data;

import java.io.Serializable;

public class CollectInfoConfigItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private int isCollectInfo;//是否收集信息
    private int newKeyVersion;//新公钥版本
    private String newKey;//新公钥
    private String postUrl;//上报url地址
    private int code;//0为失败 非0是成功
    private int nextHour;//下一次请求需要经过的小时数
    public void setIsCollectInfo(int isCollectInfo){
    	this.isCollectInfo = isCollectInfo;
    }
    public void setNewKeyVersion(int newKeyVersion){
    	this.newKeyVersion = newKeyVersion;
    }
    public void setNewKey(String newKey){
    	this.newKey = newKey;
    }
    public void setPostUrl(String postUrl){
    	this.postUrl = postUrl;
    }
    public void setCode(int code){
    	this.code = code;
    }
    public void setNextHour(int nextHour){
    	this.nextHour = nextHour;
    }
    
    public int getIsCollectInfo(){
    	return isCollectInfo;
    }
    
    public int getNewKeyVersion(){
    	return newKeyVersion;
    }
    
    public String getNewKey(){
    	return newKey;
    }
    
    public String getPostUrl(){
    	return postUrl;
    }
    public int getCode(){
    	return code;
    }
    public int getNextHour(){
    	return nextHour;
    }
}
