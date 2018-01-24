package com.rrgame.sdk.crash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Xml;

import com.dynamic.sdk.readcm.LoadCM;
import com.rrgame.sdk.adutils.AderPublicUtils;

/**
 /**
 * 负责异常信息的存储和提取
 * @author shaozhe
 *
 */
/**
崩溃日志文件构成：
一条日志一个文件:命名规则为yyyy-MM-dd-HH-mm-ss.SSS-z(年-月-日 时:分:秒.毫秒 时区)，如2013-04-27-19-13-46.683 GMT+0800
*/
public class RRGCrashLogManager {
	private static RRGCrashLogManager instance = null;
	private Context mContext;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String s_name = "b_file";
	private String sdkPath;
	private final String META_TIME = "meta_time";
	private final String META_INFO = "meta_info";
	private final String META_DATE = "meta_date";
	private final String META_SYSVERSION = "meta_sysversion";
	private final String META_DEVICE = "meta_device";
	private final String META_APPVERSION = "meta_appversion";
	private final String META_APPNAME = "meta_appname";
	private final String META_SDKVERSION = "meta_sdkversion";
	private int fileMaxNum = 3;
	public RRGCrashLogManager(Context mContext){
		this.mContext = mContext;
		File file=mContext.getFilesDir();    
		sdkPath=file.getAbsolutePath()+"/"+s_name+"/";
//		sdkPath = "/sdcard/aderCrash/"+sdkName+"/";
	}
	/**
	 * 单例模式
	 */
	public static RRGCrashLogManager getInstance(Context con) {  
	   	if(instance == null){
	   		synchronized(RRGCrashLogManager.class){
	   			if(instance == null){
	   				instance = new RRGCrashLogManager(con);
	   			}
	   		}
	   	}
	   	return instance;
	 }
	/**
	 * 设置crash文件最大数
	 */
	public void setFileMaxNum(int num){
		fileMaxNum = num;
	}
	/**
	 * 存储异常日志信息
	 * @param ex
	 */
	 public void saveCrashLog(Throwable ex){
		 if(ex != null){
			RRGCrashLogData crashInfo = new RRGCrashLogData();
			long time = System.currentTimeMillis();
			crashInfo.setTime(time);
			String versionName = "";
			String appName = "";
			//设备信息
			try {  
	            PackageManager pm = mContext.getPackageManager();  
	            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES); 
	  
	            if (pi != null) {  
	                versionName = pi.versionName == null ? "" : pi.versionName;  
	                appName = pm.getApplicationLabel(pi.applicationInfo).toString();
	            }  
	           
	        } catch (Exception e) {  
	           
	        }  
			crashInfo.setAppVersion(versionName);
			crashInfo.setDevice(android.os.Build.BRAND+"_"+android.os.Build.DEVICE);
			crashInfo.setSystemVersion(android.os.Build.VERSION.RELEASE);
			crashInfo.setSdkVersion(AderPublicUtils.SDKVERSION);
			crashInfo.setAppName(appName);
			//获取异常信息
	    	Writer writer = new StringWriter();  
	        PrintWriter printWriter = new PrintWriter(writer);  
	        ex.printStackTrace(printWriter);  
	        crashInfo.setType(ex.getMessage());
	        Throwable cause = ex.getCause(); 
	        while (cause != null) {
	            cause.printStackTrace(printWriter); 
	            cause = cause.getCause();  
	        }  
	        printWriter.close();
	        crashInfo.setInfo(writer.toString());
	        
	        
	        try {   
	        	TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");  
				formatter.setTimeZone(tz);   
	            String date = formatter.format(new Date());  
	            crashInfo.setDate(date);
	            String fileName = String.valueOf(time);  
	            WriteFileData(fileName,WriteXmlStr(crashInfo)); 
	            keepThreeAndDeleteOther();
	        } catch (Exception e) {  
	           
	        }   
		 }
	 }
	 
	
    
    /*生成xml格式字符串*/  
    private String WriteXmlStr(RRGCrashLogData crashInfo)  
    {  
        XmlSerializer serializer = Xml.newSerializer();  
        StringWriter writer = new StringWriter();  
        try  
        {  
            serializer.setOutput(writer);  
            serializer.startDocument("UTF-8",true);  
            serializer.startTag("","crashinfo");  
            
            serializer.startTag("",META_DATE);  
            serializer.text(crashInfo.getDate());
            serializer.endTag("",META_DATE);
            
            serializer.startTag("",META_SYSVERSION);  
            serializer.text(crashInfo.getSystemVersion());
            serializer.endTag("",META_SYSVERSION);
            
            serializer.startTag("",META_DEVICE);  
            serializer.text(crashInfo.getDevice());
            serializer.endTag("",META_DEVICE);
            
            serializer.startTag("",META_APPVERSION);  
            serializer.text(crashInfo.getAppVersion());
            serializer.endTag("",META_APPVERSION);
            
            serializer.startTag("",META_INFO);  
            serializer.text(crashInfo.getInfo());
            serializer.endTag("",META_INFO);
            
            serializer.startTag("",META_APPNAME);  
            serializer.text(crashInfo.getAppName());
            serializer.endTag("",META_APPNAME);
            
            serializer.startTag("",META_SDKVERSION);  
            serializer.text(crashInfo.getSdkVersion());
            serializer.endTag("",META_SDKVERSION);
            
            serializer.startTag("",META_TIME);  
            serializer.text(String.valueOf(crashInfo.getTime()));
            serializer.endTag("",META_TIME);
            
            serializer.endTag("","crashinfo");  
              
            serializer.endDocument();  
            return writer.toString();  
        }  
        catch(Exception e)  
        {  
          throw new RuntimeException(e);  
        }  
    }  
    
    /*将字符串写入私有文件夹下 文件存放在data/data/package/files/  (context.getFilesDir())
    /*调用格式WriteFileData("xmlTest.xml",WriteXmlStr())*/ 
    private void WriteFileData(String fileName,String message)  
    {  
        try  
        {  
           File dir = new File(sdkPath);  
           if (!dir.exists()) {  
               dir.mkdirs();  
           }  
           FileOutputStream fos = new FileOutputStream(sdkPath+fileName);  
           fos.write(message.toString().getBytes());  
           fos.close(); 
           
        }  
        catch(Exception e){  
         e.printStackTrace(); 
        }  
    } 
    
    
    /**
     * 从crash文件中取出crash信息
     * @param fileName
     * @return
     */
    public RRGCrashLogData getCrashToFile(String fileName) {
    	File file = new File(sdkPath+fileName);  
    	InputStream inStream = null;
    	RRGCrashLogData crashLogData = null;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(inStream != null){
    	   crashLogData = getCrashToStream(inStream);
		}
		return crashLogData;
	}
    /**
     * 对XML中数据解析
     * @param inStream
     * @return
     */
    public RRGCrashLogData getCrashToStream(InputStream inStream) {
    	RRGCrashLogData crashLogData = new RRGCrashLogData();
		XmlPullParser mXmlPull = Xml.newPullParser();
		try {
			mXmlPull.setInput(inStream, "UTF-8");
			int eventCode = mXmlPull.getEventType();
			 
			while (eventCode != XmlPullParser.END_DOCUMENT) {

				switch (eventCode) {
				case XmlPullParser.START_DOCUMENT: // 文档开始事件

					break;

				case XmlPullParser.START_TAG:// 元素开始.
					String name = mXmlPull.getName();
					if (name.equalsIgnoreCase(META_TIME)) {
						Long time = Long.parseLong(mXmlPull.nextText());
						crashLogData.setTime(time);
					} 
					else if (name.equalsIgnoreCase(META_INFO)) {
						crashLogData.setInfo(mXmlPull.nextText());
					}
					else if (name.equalsIgnoreCase(META_DATE)) {
						crashLogData.setDate(mXmlPull.nextText());
					}
					else if (name.equalsIgnoreCase(META_SYSVERSION)) {
						crashLogData.setSystemVersion(mXmlPull.nextText());
					}
					else if (name.equalsIgnoreCase(META_DEVICE)) {
						crashLogData.setDevice(mXmlPull.nextText());
					}
					else if (name.equalsIgnoreCase(META_APPVERSION)) {
						crashLogData.setAppVersion(mXmlPull.nextText());
					}
					else if (name.equalsIgnoreCase(META_APPNAME)) {
						crashLogData.setAppName(mXmlPull.nextText());
					}
					else if (name.equalsIgnoreCase(META_SDKVERSION)) {
						crashLogData.setSdkVersion(mXmlPull.nextText());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crashLogData;
	}
      
    
    /**
     * 删除单个文件
     * @param filename
     */
    public void deletCrashLog(String filename){
    	File file = new File(sdkPath+filename); 
        if(file.exists()){  
            file.delete();  
        } 
    }
    /**
     * 删除所有文件
     */
    public void deletAllCrashLog(){
    	File root = new File(sdkPath);
    	if(root.exists()){
	    	File files[] = root.listFiles();  
	        if(files != null){  
	            for (File f : files){  
	                if(!f.isDirectory()){  
	                	 f.delete();  
	                }
	            }  
	        } 
    	}
        if(root.exists()){ 
        	root.delete();  
        } 
    }
  
    public List<Long> getAllFilesName(){
    	List<Long> filesName = new ArrayList<Long>();
    	File root = new File(sdkPath);
    	if(root.exists()){
	    	File files[] = root.listFiles();  
	        if(files != null){  
	            for (File f : files){  
	               String name = f.getName();
                   try{
                	   if(!f.isDirectory()){
	                       long time = Long.parseLong(name);
	                       filesName.add(time);
                	   }
                   }
                   catch(Exception e){
                	   
                   }
	            }  
	        } 
    	}
    	return filesName;
    }
    public RRGCrashLogData getCrashData(String fileName){
    	RRGCrashLogData data = null;
    	InputStream inStream = null;
    	File f = new File(sdkPath+fileName);
		try {
			inStream = new FileInputStream(f);
			if(inStream != null){
               data = getCrashToStream(inStream);
            }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
    }
    /**
     * 获得最新的MAX个Crash文件
     * @return
     */
    public List<RRGCrashLogData> getNewFiles(){
    	List<RRGCrashLogData> crashList = new ArrayList<RRGCrashLogData>();
    	List<Long> filesTime = getAllFilesName();
    	CrashTimeComparator comp=new CrashTimeComparator();
    	Collections.sort(filesTime,comp);
    	int len = filesTime.size();
	    if(len > fileMaxNum){
	       len = fileMaxNum; 
	    }
	    for(int i=0;i<len;i++){
	    	RRGCrashLogData data = getCrashData(String.valueOf(filesTime.get(i)));
	    	if(data != null){
	    	   crashList.add(data);
	    	}
	    }
	    deletAllCrashLog();
	    return crashList;
    }
    /**
     * 比较器 按时间排序
     * @author shaozhe
     *
     */
    public class CrashTimeComparator implements Comparator<Long> {


		@Override
		public int compare(Long lhs, Long rhs) {
			// TODO Auto-generated method stub
			if(lhs > rhs){
				return -1;
			}
			else if(lhs < rhs){
				return 1;
			}
			return 0;
		}
    	
    }
    /**
     * 保留最新的三个文件其他删除
     */
    public void keepThreeAndDeleteOther(){
    	List<Long> filesTime = getAllFilesName();
    	int len = filesTime.size();
    	if(len > fileMaxNum){
	    	CrashTimeComparator comp=new CrashTimeComparator();
	    	Collections.sort(filesTime,comp);
		    for(int i=fileMaxNum;i<len;i++){
		    	deletCrashLog(String.valueOf(filesTime.get(i)));
		    }
    	}
    }
}
