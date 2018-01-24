package com.rrgame.sdk.reportinfo.data;

public class PostBackItem {
	private int code;//0为失败 非0是成功
	public void setCode(int code){
    	this.code = code;
    }
	public int getCode(){
    	return code;
    }
}
