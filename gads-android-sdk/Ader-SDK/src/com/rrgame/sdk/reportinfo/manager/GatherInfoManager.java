package com.rrgame.sdk.reportinfo.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.rrgame.sdk.systeminfo.AderBase64;
import com.rrgame.sdk.systeminfo.AderLogUtils;
/**
 * 收集信息管理器
 * @author shaozhe
 *
 */
public class GatherInfoManager implements GatherInfoListener{
	private static GatherInfoManager instance = null;
	private Context mContext;
	public static ExecutorService executorService = Executors.newCachedThreadPool();
   
	/**
	 * config url
	 */
	private final String configUrl = "aHR0cDovL2FkYmMyLmFkZXIubW9iaS9jb25mZzI=";
	/**
	 * 协议版本号
	 */
	private final int protocolVersion = 1;
	/**
	 * 初始公钥版本号
	 */
	private final int publicKeyVersion = 1;
	/**
     * 第一版公钥
     */
    private final String publicKey = "MIIDGTCCAoKgAwIBAgIJAKQp2GIWY/q7MA0GCSqGSIb3DQEBBQUAMGcxCzAJBgNVBAYTAkNOMRAwDgYDVQQIEwdCZWlqaW" +
    		"5nMREwDwYDVQQHEwhCZWlqaW5nIDEVMBMGA1UEChMMUmVuUmVuIEdhbWVzMQ0wCwYDVQQLEwRBZGVyMQ0wCwYDVQQDEwRhZGVyMB4XDTEzMDUyMTA4M" +
    		"jExNFoXDTEzMDYyMDA4MjExNFowZzELMAkGA1UEBhMCQ04xEDAOBgNVBAgTB0JlaWppbmcxETAPBgNVBAcTCEJlaWppbmcgMRUwEwYDVQQKEwxSZ" +
    		"W5SZW4gR2FtZXMxDTALBgNVBAsTBEFkZXIxDTALBgNVBAMTBGFkZXIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAOtzh6UgLUNpOxaxocUZbfOxO" +
    		"68gq/E9v/7RlBAE1ZD7ngxEiFQVAhulwE6F3xHyptPHDRuPQx0en8RBulOnZwo7ogUe6NIMLQBl7WqSwh6S+5dGlh8iSx8WurlikfyNFfhWKzVP1l" +
    		"1UAo8rxckQKVepl/wZZdRuX6kIakL33bv1AgMBAAGjgcwwgckwHQYDVR0OBBYEFJVWdYTPmBGcuy1+/cfTj1mKUlgNMIGZBgNVHSMEgZEwgY6AFJVW" +
    		"dYTPmBGcuy1+/cfTj1mKUlgNoWukaTBnMQswCQYDVQQGEwJDTjEQMA4GA1UECBMHQmVpamluZzERMA8GA1UEBxMIQmVpamluZyAxFTATBgNVBAoTD" +
    		"FJlblJlbiBHYW1lczENMAsGA1UECxMEQWRlcjENMAsGA1UEAxMEYWRlcoIJAKQp2GIWY/q7MAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgY" +
    		"EA5lguAldxtorlV00y/vTj7t5JIc44LX5F/j+levDmEYJpb6h09iVanHKMFtXGiOslqc47NuXEACN2+IRivjnRyWsZVoPPtEwFGxQjjEQjsPSslN" +
    		"SbxvwkPvZYrflfItXLjx/MBOuVgITdtbsbNNm02M9IWlSuiztqMAHPSuposh0=";
	
	/**
	 * 储存各种信息数据的文件名
	 */
	private final String appSaveName = "saveapp";
	private final String systemSaveName = "savesystem";
	private final String crashSaveName = "savecrash";
	
	/**
	 * 各种信息的类型
	 */
	public static final int systemType = 1;
	public static final int appType = 2;
	public static final int crashType = 3;
	
	private String mAppId;
	
	public GatherInfoManager(Context context,String appid){
		this.mContext = context;
		this.mAppId = appid;
	}
	/**
	 * 单例模式
	 */
	public static GatherInfoManager getInstance(Context con,String appid) {  
	   	if(instance == null){
	   		synchronized(GatherInfoTask.class){
	   			if(instance == null){
	   				instance = new GatherInfoManager(con,appid);
	   			}
	   		}
	   	}
	   	return instance;
	 }
	/**
	 * 启动各种任务 
	 */
	public void start(int type){
	    byte[] bytes = AderBase64.decode(configUrl);
	    String deConfigUrl = new String(bytes);
		switch(type){
		  case systemType:
			  AderLogUtils.d("启动终端信息收集");
			   new GatherInfoTask(mContext,this,type, deConfigUrl, 
					   systemSaveName,mAppId,publicKey,publicKeyVersion,protocolVersion,true,true).getConfig();
			  
		  break;
		  case appType:
			  AderLogUtils.d("启动app列表信息收集");
			   new GatherInfoTask(mContext,this,type, deConfigUrl, 
					   appSaveName,mAppId,publicKey,publicKeyVersion,protocolVersion,true,true).getConfig();
			  
		  break;
		  case crashType:
			  AderLogUtils.d("启动崩溃信息收集");
			   new GatherInfoTask(mContext,this,type, deConfigUrl, 
					   crashSaveName,mAppId,publicKey,publicKeyVersion,protocolVersion,true,true).getConfig();
			
		  break;
		}
	}
	
	
	
	@Override
	public void gatherInfoCallBack(boolean isSucess, int type) {
		// TODO Auto-generated method stub
		AderLogUtils.d("上报信息是否成功="+isSucess+":类型="+type);
	}
	@Override
	public void getConfigCallBack(boolean isSucess, int type) {
		// TODO Auto-generated method stub
		AderLogUtils.d("获取config是否成功="+isSucess+":类型="+type);
	}
}
