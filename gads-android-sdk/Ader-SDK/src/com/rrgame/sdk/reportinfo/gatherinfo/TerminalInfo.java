package com.rrgame.sdk.reportinfo.gatherinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.rrgame.sdk.systeminfo.AderDeviceRootAccess;
import com.rrgame.sdk.systeminfo.AderLogUtils;
/**
 * 终端信息
 * @author shaozhe
 *
 */
public class TerminalInfo {
	
	/** Current network is EVDO revision 0 */
	private static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	private static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is HSDPA */
	private static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	private static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	private static final int NETWORK_TYPE_HSPA = 10;
	
	/**
	* 构造终端信息字符串
	* @param con
	* @return 
	*/
   public static String bulidTerminalInfo(Context con){
	   String bulidStr = null;
	   try {
		   JSONObject infoObject = new JSONObject();
		   infoObject.put("scrn_w", getScreenWH(con)[0]); 
		   infoObject.put("scrn_h", getScreenWH(con)[1]); 
		   infoObject.put("density", getDensity(con));
		   infoObject.put("imei", getIMEI(con));
		   infoObject.put("imsi", getIMSI(con));
		   infoObject.put("serialno", getSerialNumber());
		   infoObject.put("root", AderDeviceRootAccess.isRootSystem());
		   infoObject.put("tz", getTimeZone());
		   infoObject.put("mac", getMacAdress(con));
	       infoObject.put("model", android.os.Build.DEVICE);
	       infoObject.put("brand", android.os.Build.BRAND);
	       infoObject.put("s_lv", Integer.parseInt(android.os.Build.VERSION.SDK));
	       infoObject.put("os_ver", android.os.Build.VERSION.RELEASE);
	       infoObject.put("custom_os_ver", android.os.Build.DISPLAY);
	       infoObject.put("lang", getLanuge());
		   infoObject.put("n_oper", getNetCode(con));
		   infoObject.put("n_type", getConnectInfo(con));
		   infoObject.put("s_status", getSimStatus(con));
	       infoObject.put("ram", getTotalMemory(con));
	       infoObject.put("s_space", getSDCardMemory(con));
	       infoObject.put("r_space", getRomMemroy(con));
	       infoObject.put("cpu_freq", getCpuFreq());
	       infoObject.put("proc", getCpuName());
	       infoObject.put("cpu_m", geCpuHardware());
	       infoObject.put("cpu_n", ""+getNumCores());
	       infoObject.put("gps", hasGPSDevice(con));
	       infoObject.put("m_touch", checkMultiTouch(con));
	       infoObject.put("camera", checkCameraHardware(con));
	       
	       bulidStr = infoObject.toString();
	       AderLogUtils.d("端信息收集info="+bulidStr);
		}
	   
	   catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return bulidStr;
   }
   
   /**
    * 获得网络连接状态
    * @param con
    * @return 1:2g,2:3g,3:wifi
    */
   private static int getConnectInfo(Context con) {
		NetworkInfo info = null;
		ConnectivityManager conMan = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(conMan!=null&&conMan.getActiveNetworkInfo()!=null){
			try {
				info=conMan.getActiveNetworkInfo();
			} catch (Exception e) {
				
			}
		}
		if (info != null && info.isConnected()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return 3;
			} else {
				return solveType(info.getSubtype());
			}
		} else {
			return 0;
		}
	}
   
   /**
    * 获取网络类型
    * @return 1:2g,2:3g
    */
   private static int solveType(int subtype) {
		if (subtype == TelephonyManager.NETWORK_TYPE_UMTS
				|| subtype == NETWORK_TYPE_HSDPA
				|| subtype == NETWORK_TYPE_HSUPA
				|| subtype == NETWORK_TYPE_HSPA
				|| subtype == NETWORK_TYPE_EVDO_0
				|| subtype == NETWORK_TYPE_EVDO_A) {
			return 2;
		} else {
			return 1;
		}
	}
   
   /**
    * 获取系统语言
    * @return
    */
   private static String getLanuge(){
	   String loc = "";
	   if(Locale.getDefault().getLanguage()!=null){
		    loc = Locale.getDefault().getLanguage();
	   }
	   return loc;
   }
   
   /**
    * 获取sim卡状态
    * @param con
    * @return 0:锁定或未知，1：正常，2：无sim卡
    */
	 private static int getSimStatus(Context con){
		 int status = 0;
		 TelephonyManager tm=(TelephonyManager)con.getSystemService(Context.TELEPHONY_SERVICE);
		 if(tm!=null){
			 switch(tm.getSimState()){
			     case TelephonyManager.SIM_STATE_READY:
			    	 status = 1;
			     break;
			     case TelephonyManager.SIM_STATE_ABSENT:
			    	 status = 2;
			     break;
			     default:
			    	 status = 0;
			     break;
		     }
		 }
		 return status;
	 }
   
	/**
	 * 获得网络运行商编码
	 * @return
	 */
 	private static String getNetCode(Context con){
 		String iNumeric = "";
		TelephonyManager iPhoneManager = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
		if(iPhoneManager!=null&&iPhoneManager.getSimOperator()!=null){
			iNumeric = iPhoneManager.getSimOperator();
		} 
		return iNumeric;
 	}
   
   /**
    * 系统总内存
    * @param con
    * @return
    */
   private static int getTotalMemory(Context con){
		String str1="/proc/meminfo";
		String str2;
		String[] arrayOfString;
		int initial_memory=0;
		try{
			FileReader localFileReader=new FileReader(str1);
			BufferedReader localBufferedReader=new BufferedReader(localFileReader,8192);
			str2=localBufferedReader.readLine();
			arrayOfString=str2.split("\\s+");
			for(String num:arrayOfString){
				Log.i(str2, num+"\t");
				initial_memory =Integer.valueOf(arrayOfString[1]).intValue()/1024;
				localBufferedReader.close();
			}
		}
		catch(IOException e){
			
		}
		return initial_memory;		
	}
   
	/**
	 * 获取SD卡总内存
	 * @param context
	 * @return
	 */
	private static int getSDCardMemory(Context context) {  
		int memory = 0;
        String state = Environment.getExternalStorageState();  
        if (Environment.MEDIA_MOUNTED.equals(state)) {  
            File sdcardDir = Environment.getExternalStorageDirectory();  
            StatFs sf = new StatFs(sdcardDir.getPath());  
            long bSize = sf.getBlockSize();  
            long bCount = sf.getBlockCount();  
            memory = (int)((bSize * bCount)/1024/1024);
        }
        return memory;  
    }  
	
	/**
	 * 手机存储空间
	 * @param context
	 * @return
	 */
	private static int getRomMemroy(Context context) {  
		int memroy = 0;
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        memroy = (int)((totalBlocks * blockSize)/1024/1024);
        return memroy;  
    }  
	
	/**
	 * 获取CPU频率（单位KHZ）
	 * @param context
	 * @return
	 */ 
	private static int getCpuFreq() {
        String result = "";
        int freq = 0;
        ProcessBuilder cmd;
        try {
        	String faqName = "cpuinfo_max_freq";
        	
            String[] args = { "/system/bin/cat",
                              "/sys/devices/system/cpu/cpu0/cpufreq/"+faqName };
                cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[24];
                while (in.read(re) != -1) {
                        result = result + new String(re);
                }
                if(result.trim()=="")
                {
                	return freq;
                }
                int kValue = Integer.parseInt(result.trim());
                freq = kValue/1024;
                result = String.valueOf(freq);
                in.close();
        } catch (IOException ex) {
                ex.printStackTrace();
                result = "N/A";
        }
        return freq;
	}
	
  /**
   * 获取CPU硬件信息
   * @return
   */
   private static String geCpuHardware() {
     String str = "", hardwareStr = "";
     try {
             //读取CPU信息
             Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
             InputStreamReader ir = new InputStreamReader(pp.getInputStream());
             LineNumberReader input = new LineNumberReader(ir);
             //查找CPU硬件
             for (int i = 1; i < 100; i++) {
                     str = input.readLine();
                     if (str != null) {
                             //查找到序列号所在行
                             if (str.indexOf("Hardware") > -1) {
                             //提取序列号
                          	 hardwareStr = str.substring(str.indexOf(":") + 1,
                                           str.length());
                             //去空格
                          	 hardwareStr = hardwareStr.trim();
                                break;
                             }
                     }else{
                        //文件结尾
                        break;
                     }
                 }
     	} catch (IOException ex) {
             	//赋予默认值
     			ex.printStackTrace();
     	}
     	return hardwareStr;
    }
   
	/**
	 * CPU型号
	 * @return
	 */
	  private static String getCpuName() {
		  String cpuName ="";
	      try {
	             FileReader fr = new FileReader("/proc/cpuinfo");
	              BufferedReader br = new BufferedReader(fr);
	              String text = br.readLine();
	              String[] array = text.split(":\\s+", 2);
	              if(array!=null&&array.length>=2&&array[1]!=null)
	            	  cpuName = array[1];
	      } catch (FileNotFoundException e) {
	              e.printStackTrace();
	      } catch (IOException e) {
	              e.printStackTrace();
	      }
	      return cpuName;
	  }
	  
	/**
	 * CPU核数
	 * @return
	 */
	private static int getNumCores() {
	    //Private Class to display only CPU devices in the directory listing
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            //Check if filename is "cpu", followed by a single digit number
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }
	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new CpuFilter());
	        if(files==null)
	        	return 0;
	        Log.d("HardwareImformation:", "CPU Count: "+files.length);
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	        //Print exception
	        Log.d("HardwareImformation:", "CPU Count: Failed.");
	        e.printStackTrace();
	        //Default to return 1 core
	        return 1;
	    }
	}
	
	/**
	 * 反射判断是否有hasSystemFeature这个方法
	 * @param context,name
	 * @return
	 */
	private static boolean isHasSystemFeature(Context context,String name){
		PackageManager pm = context.getPackageManager();
		Boolean isSupport = false;
		if(pm != null){
			Method hasSystemFeature = null;
			try {
				hasSystemFeature = pm.getClass().getDeclaredMethod("hasSystemFeature",new Class[]{String.class});
				isSupport = (Boolean)hasSystemFeature.invoke(pm,new String[]{name});
			} catch (Exception e) {
				e.printStackTrace();
			} 
			if(hasSystemFeature == null){
				isSupport = false;
			}
			if(isSupport == null){
				isSupport = false;
			}
		}
		return isSupport;
	}
	
	/**
	 * 检查摄像头硬件
	 * @param context
	 * @return 
	 * 0：不支持前置和后置 
	 * 1：支持前置 
     * 2：支持后置 
     * 3：支持前置和后置 
	 * （系统版本2.1之后可以判断是否支持摄像头，之前的系统版本都为0 
	 * （系统版本2.3之后可以判断是否支持前置）
	 */
	private static int checkCameraHardware(Context context) {
		int type = 0;
		//2.1之后支持
		if (isHasSystemFeature(context,"android.hardware.camera")){
            // this device has a camera
			type = 2;
        } 
		try {
			//反射判断是否支持前置
			Method getNumberOfCameras = Camera.class.getDeclaredMethod("getNumberOfCameras");
			int num = (Integer) getNumberOfCameras.invoke(Camera.class);
			if(num > 1){
			   type = 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return type;
    }
	
	/**
	 * 检查多点触控
	 * @param context
	 * @return
	 */
	private static boolean checkMultiTouch(Context context) {
        if (isHasSystemFeature(context,"android.hardware.touchscreen.multitouch")){
            return true;
        } 
        return false;
    }
	
	/**
	 * 检查gps模块
	 * @param context
	 * @return
	 */
	private static boolean hasGPSDevice(Context context)
	{
		final LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if ( mgr == null ) 
			return false;
		final List<String> providers = mgr.getAllProviders();
		if ( providers == null ) 
			return false;
		return providers.contains(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 获得收集宽高（像素）
	 * @param context
	 * @return
	 */
	private static int[] getScreenWH(Context context){
		int[] screenWH = new int[2];
		if(context != null){
		   DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
		   screenWH[0] = metrics.widthPixels;
		   screenWH[1] = metrics.heightPixels;
		}
		return screenWH;
	}
	
	/**
	 * 获取像素密度
	 * @param con
	 * @return
	 */
	private static float getDensity(Context con){
		DisplayMetrics metrics = con.getResources().getDisplayMetrics();
		if(metrics==null)
			return 1.5f;
		return metrics.density;
	}
	
	/**
	 * 获得imei号
	 * @param con
	 * @return
	 */
	private static String getIMEI(Context con) {
		String deviceId = "";
		TelephonyManager telephonyManager=((TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE));
		try {
			if (telephonyManager!=null&&telephonyManager.getDeviceId()!=null) {
				deviceId = telephonyManager.getDeviceId();
			}
		} catch (Exception e) {
		}
		return deviceId;
	}
	
	/**
	 * 获取Sim卡IMSI号
	 * @return
	 */
    private static String getIMSI(Context con) {
    	String imsiStr = "";
		TelephonyManager telephonyManager=((TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE));
		try {
			if (telephonyManager!=null&&telephonyManager.getSubscriberId()!=null) {
					imsiStr = telephonyManager.getSubscriberId();
			}
		} catch (Exception e) {
		}
		return imsiStr;
	}
    
    /**
     * 获得mac地址
     * @return
     */
    private static String getMacAdress(Context con){
    	String macAdress = "";
    	// 获取wifi mac地址
		WifiManager wifi = (WifiManager) con
				.getSystemService(Context.WIFI_SERVICE);
		try {
			WifiInfo info = wifi.getConnectionInfo();
			if (info!=null) {
				macAdress = info.getMacAddress();
			}
		} catch (Exception e) {
		}
		if (TextUtils.isEmpty(macAdress))
		{
			macAdress = "";
		}
		else
		{
			macAdress = macAdress.replace(":", "");
		}
    	return macAdress;
    }
    
    /**
     * 获得串口号码
     * @return
     */
    private static String getSerialNumber() {
		String serial = "";
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			if(get!=null && get.invoke(c, "ro.serialno")!=null){
				serial = (String) get.invoke(c, "ro.serialno");
			}
		} catch (Exception ignored) {
		}
		return serial;
	}
    
    /**
     * 获取时区
     * @return
     */
    private static String getTimeZone(){
    	TimeZone zone = TimeZone.getDefault();
		String mTimezone = zone.getID()+"_"+zone.getDisplayName()+"_"+zone.getRawOffset()/1000;
		if (TextUtils.isEmpty(mTimezone))
		{
			mTimezone = "";
		}
		return mTimezone;
    }
}
