package com.rrgame.sdk.reportinfo.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.text.TextUtils;

import com.rrgame.sdk.adutils.AderPublicUtils;
import com.rrgame.sdk.reportinfo.data.CollectInfoConfigItem;
import com.rrgame.sdk.reportinfo.data.PostBackItem;
import com.rrgame.sdk.reportinfo.gatherinfo.AppListInfo;
import com.rrgame.sdk.reportinfo.gatherinfo.CrashLogInfo;
import com.rrgame.sdk.reportinfo.gatherinfo.TerminalInfo;
import com.rrgame.sdk.reportinfo.http.HttpHelp;
import com.rrgame.sdk.reportinfo.utils.DESUtils;
import com.rrgame.sdk.reportinfo.utils.HMACUtils;
import com.rrgame.sdk.reportinfo.utils.PreferencesUtils;
import com.rrgame.sdk.reportinfo.utils.RSAUtils;
import com.rrgame.sdk.reportinfo.utils.XmlUtils;
import com.rrgame.sdk.reportinfo.utils.ZipUtils;
import com.rrgame.sdk.systeminfo.AderAppInfo;
import com.rrgame.sdk.systeminfo.AderBase64;
import com.rrgame.sdk.systeminfo.AderLogUtils;


public class GatherInfoTask {

	private Context mContext;
	/**
	 * 请求config信息的地址
	 */
	private String configUrl;
	
	/**
	 * 协议版本号
	 */
	private int protocolVersion;
	/**
	 * 初始公钥版本号
	 */
	private int publicKeyVersion;
    
    /**
     * 获得的config信息
     */
    private CollectInfoConfigItem config;
    /**
     * 第一版公钥
     */
    private String publicKey;
    /**
     * 上报数据的类型 崩溃 终端信息 APP列表信息 
     */
    private int type;
    /**
     * 上报的数据
     */
    private String body;
    /**
	 * 存储文件名称
	 */
	private String saveName;
	/**
	 * appid
	 */
	private String appid;
	
	/**
     * 公钥储存的名称
     */
    private final String PUBLICKEY_NAME = "publickey";
   
    /**
     * 公钥版本号名称
     */
    private final String KEY_VERSION = "keyversion";
    /**
     * 收集信息的时间
     */
    private final String GATHER_TIME = "gathertime";
    /**
     * 经过的时间
     */
    private final String LATER_TIME = "latertime";
    
    /**
     * 最长时间一周(毫秒)
     */
    private final long maxLaterTime = 24*7*3600*1000;
    /**
     * 随机分钟数(一个小时)
     */
    private final int rndNum = 60;
    /**
     * 是否需要压缩
     */
    private boolean needCompress;
    /**
     * 是否需要加密
     */
    private boolean needEncrypt;
	GatherInfoListener listener;
	public GatherInfoTask(Context context,GatherInfoListener listener,
			int type,String configUrl,String saveName,String appid,
			String publicKey,int publicKeyVersion,int protocolVersion,boolean needCompress,boolean needEncrypt){
		this.mContext = context;
		this.listener = listener;
		this.type = type;
		this.configUrl = configUrl;
		this.saveName = saveName;
		this.appid = appid;
		this.publicKey = publicKey;
		this.publicKeyVersion = publicKeyVersion;
		this.protocolVersion = protocolVersion;
		this.needCompress = needCompress;
		this.needEncrypt = needEncrypt;
	}
	
	/**
	 * 组装请求config的url
	 * @return
	 */
	private String bulidConfigUrl(){
		String bulidUrl = null;
		long time = System.currentTimeMillis();
		publicKeyVersion = PreferencesUtils.getInt(mContext, saveName, KEY_VERSION, publicKeyVersion);
		AderLogUtils.d("publicKeyVersion="+publicKeyVersion);
		HashMap<String, String> params = new HashMap<String, String>();
		HashMap<String, String> sdkConfigAbout = AderAppInfo.getReportInfoThings();
		params.put("uuid1", sdkConfigAbout.get("uuid1"));
		params.put("uuid2", sdkConfigAbout.get("uuid2"));
		params.put("uuid3", sdkConfigAbout.get("uuid3"));
		params.put("tid", sdkConfigAbout.get("tid"));
		params.put("app_id", appid);
		params.put("sdk_ver", AderPublicUtils.SDKVERSION);
		params.put("sdk_type","1");
		params.put("v", String.valueOf(protocolVersion));
		params.put("kv", String.valueOf(publicKeyVersion));
		params.put("t", String.valueOf(time));
		params.put("dt", String.valueOf(type));
		params.put("p", String.valueOf(2));
		bulidUrl = configUrl+"?";
		for(String key : params.keySet()){					
			bulidUrl=bulidUrl+key+"="+URLEncoder.encode(params.get(key))+"&";
		}
		return bulidUrl;
	}
	private String getBody(){
		String info = null;
		switch(type){
			case GatherInfoManager.appType:
				info = AppListInfo.bulidAppListInfo(mContext);
			break;
			case GatherInfoManager.systemType:
				info = TerminalInfo.bulidTerminalInfo(mContext);
			break;
			case GatherInfoManager.crashType:
				info = CrashLogInfo.bulidCrashInfo(mContext);
			break;
		}
		return info;
	}
    /**
     * 获取配置信息
     */
    public void getConfig(){
      if(isHelpConfig()){
    	GatherInfoManager.executorService.submit(new Runnable() {
			@Override
			public void run() {
				String buildUrl = bulidConfigUrl();
				InputStream in = HttpHelp.getHttpStream(mContext, buildUrl);
				if(in != null){
					AderLogUtils.d("获取config信息");
				   //获得config信息并解析
				   config = XmlUtils.getInfoConfigData(in);
				   if(config != null){
					   AderLogUtils.d("解析config成功");
					   listener.getConfigCallBack(true, type);
					   int isGather = config.getIsCollectInfo();
					   String configPublicKey = config.getNewKey();
					   int keyVersion = config.getNewKeyVersion();
					   String postUrl = config.getPostUrl();
					   //判断是否收集
					   if(isGather == 0){
						   return;
					   }
					   //收集数据
						body = getBody();
						if(TextUtils.isEmpty(body)){
							return;
						}
					   if(TextUtils.isEmpty(configPublicKey)){
						   publicKey = PreferencesUtils.getString(mContext, saveName, PUBLICKEY_NAME, publicKey);
						   AderLogUtils.d("读取本地存储的公钥");
					   }
					   else{
						   publicKey = configPublicKey;
						   saveKey();
						   AderLogUtils.d("获取新公钥并存储");
					   }
					   
					   
					   if(keyVersion > 0){
						   publicKeyVersion = keyVersion;
						   saveKeyVersion();
						   AderLogUtils.d("获取新公钥版本");
					   }
					   
					   if(!TextUtils.isEmpty(postUrl)){
						   AderLogUtils.d("信息收集上报");
						   saveLaterTime();
						   //获得加密并封装好的上报数据
						   HashMap<String, String> postData = encryptData();
						   if(postData!=null){
							   //上报服务端
							   postData(postUrl,postData);
						   }
					   }
				   }
				   else {
					   listener.getConfigCallBack(false, type);
				   }
				}
				else {
					listener.getConfigCallBack(false, type);
				}
				
			}
		});
      }
    }
    
    /**
	 * 判断是否需要请求config
	 * @param type
	 * @return
	 */
    private boolean isHelpConfig(){
		long currentTime = System.currentTimeMillis();
		long getherTime = 0;
		long laterTime = 0;
		Random rnd = new Random();
		getherTime = PreferencesUtils.getLong(mContext, saveName, GATHER_TIME, 0);
		laterTime = PreferencesUtils.getLong(mContext, saveName, LATER_TIME, 0);
		int ranMinute = rnd.nextInt(rndNum);
		long sub = (currentTime - getherTime)+ranMinute*60*1000;
		AderLogUtils.d("sub="+sub/60000);
		AderLogUtils.d("laterTime="+laterTime/60000);
		if(sub >= laterTime){
			return true;
		}
		else if(sub >= maxLaterTime){
			return true;
		}
		return false;
	}
    
    /**
     * 对封装好的数据进行加密
     * @param pubKey 加密用的公钥
     * @param body 要加密的数据
     * @return
     */
    private HashMap<String, String> encryptData(){
    	HashMap<String, String> params = null;
    	try {
    		if(publicKey==null||body==null){
    			return null;
    		}
    		//base64解码
        	byte[] pubKeyData = AderBase64.decode(publicKey);
        	
        	//数据压缩
        	byte[] zipData = null;
        	if(needCompress){
			  zipData = ZipUtils.compress(body.getBytes());
        	}
        	else {
        		zipData = body.getBytes();
        	}
        	
			//创建密钥
			byte[] desKey = DESUtils.creatSecretKey();
			
			//加密数据
			byte[] encodeData = null;
			if(needEncrypt){
				encodeData = DESUtils.encrypt(zipData,desKey);
			}
			else {
				encodeData = zipData;
			}
			
			//加密密钥
			byte[] encodeSecretKey = RSAUtils.pubEncrypt(desKey,pubKeyData);
			
			//收集信息组装
			byte[] infoData = infoDataAssembly(encodeData,encodeSecretKey);
			
			//摘要
			byte[] summaryData = HMACUtils.encryptHMAC(infoData, desKey);
			
			//数据组装
			params = postStr(encodeSecretKey,infoData,summaryData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	return params;
    }
       
    /**
     * 上报数据
     * @param url
     * @param data
     */
    private void postData(final String url,final HashMap<String, String> data){
    	GatherInfoManager.executorService.submit(new Runnable() {

			@Override
			public void run() {
				InputStream in = HttpHelp.postHttpStream(mContext, url, data);
				if(in != null){
					PostBackItem item = XmlUtils.getPostBackData(in);
					if(item != null){
						listener.gatherInfoCallBack(true, type);
					}
					else{
						listener.gatherInfoCallBack(false, type);
					}
				}
				else {
					listener.gatherInfoCallBack(false, type);
				}
				
			}
		});
    }
    
   /**
    * 更新本地key以及版本号
    */
   private void saveKey(){
	   PreferencesUtils.writeString(mContext, saveName, PUBLICKEY_NAME, publicKey);
   }
   private void saveKeyVersion(){
	   PreferencesUtils.writeInt(mContext, saveName, KEY_VERSION, config.getNewKeyVersion());
   }
   
   /**
    * 存储当前时间和延后时间
    */
   private void saveLaterTime(){
	   long time = System.currentTimeMillis();
	   PreferencesUtils.writeLong(mContext, saveName, GATHER_TIME, time);
	   PreferencesUtils.writeLong(mContext, saveName, LATER_TIME, config.getNextHour()*3600*1000);
   }
   
   /**
    * 组成上报字符串
    * @return
    */
   private HashMap<String, String> postStr(byte[] secretkeyData,byte[] assemblyInfoData,byte[] summaryData){
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			//时间戳
			long time = System.currentTimeMillis();
//			//收集信息
			String assemblyInfo = AderBase64.encode(assemblyInfoData);
//			//摘要
			String summary = AderBase64.encode(summaryData);
//			//密钥 
			String secretkeyStr = AderBase64.encode(secretkeyData);
			
			if(TextUtils.isEmpty(assemblyInfo)){
				assemblyInfo = "";
			}
			if(TextUtils.isEmpty(summary)){
				summary = "";
			}
			if(TextUtils.isEmpty(secretkeyStr)){
				secretkeyStr = "";
			}
			HashMap<String, String> sdkConfigAbout = AderAppInfo.getReportInfoThings();
			params.put("uuid1", sdkConfigAbout.get("uuid1")); 
			params.put("uuid2", sdkConfigAbout.get("uuid2")); 
			params.put("uuid3", sdkConfigAbout.get("uuid3")); 
			params.put("tid", sdkConfigAbout.get("tid"));
			params.put("app_id", appid);
			params.put("sdk_ver", AderPublicUtils.SDKVERSION);
			params.put("sdk_type","2");
			params.put("v", String.valueOf(protocolVersion));
			params.put("md", summary);
			params.put("kv", String.valueOf(publicKeyVersion));
			params.put("k", secretkeyStr);
			params.put("t", String.valueOf(time));
			params.put("dt", String.valueOf(type));
			params.put("p", "2");
			params.put("d", assemblyInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return params;
	  }
   
   /**
    * 收集到的信息数据组装
    * @param info  加密后的信息数据
    * @param secretKey 加密的密钥
    * @param needCompress 是否需要压缩
    * @param needEncrypt  是否需要加密
    * @return
    */
    private byte[] infoDataAssembly(byte desInfo[],byte[] ensecretKey){
    	if(desInfo == null || ensecretKey == null){
    		return null;
    	}
  		ByteArrayOutputStream out = new ByteArrayOutputStream();
  		try {
  			//写入标志位信息 表示加密和压缩
  			byte flag = 0;
  			if(needEncrypt){//需要加密
  			   flag |= 0x80; 
  			}
  			if(needCompress){//需要压缩
  			   flag |= 0x40; 
  			}
  			out.write(flag);
  			//标志位附属信息
  			if(needEncrypt){//需要加密
  				out.write(1);
  			}
  			if(needCompress){//需要压缩
  			   out.write(1);
  			}
  			//写入数据类型 1：设备基础信息 2：应用列表 3：SDK日志
  			out.write(type);
  			
  			//写入密钥类型
  			short keyType = 1;
  			out.write(shortToByte(keyType));
  			
  			//写入密钥长度
  			int keyLen = ensecretKey.length;
  			out.write(intToByte(keyLen));
  			
  			//写入密钥
  			out.write(ensecretKey);
  			
  			//写入主体数据类型
  			short infoType = 2;
  			out.write(shortToByte(infoType));
  			
  			//主体数据长度
  			int infoLen = desInfo.length;
  			
  			//写入主体数据
  			out.write(intToByte(infoLen));
  			//写入主体数据
  			out.write(desInfo);
  			out.close();
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		return out.toByteArray();
    }
    
    private static byte[] intToByte(int number) { 
        int temp = number; 
        byte[] b = new byte[4]; 
        for (int i = b.length-1; i >= 0; i--) { 
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    } 
    
    private static byte[] shortToByte(short number) { 
        int temp = number; 
        byte[] b = new byte[2]; 
        for (int i = b.length-1; i >= 0; i--) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    } 
    
}
