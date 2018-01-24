package com.dynamic.sdk.readcm;
/**
 * 读取jar包信息转换成base64字符串
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.os.Environment;

import com.rrgame.sdk.reportinfo.utils.DESUtils;
import com.rrgame.sdk.systeminfo.AderBase64;

/**还有一个步骤需要用android sdk中的dex工具将jar包中的class文件编译成android系统虚拟机识别的dex文件
 * 读取jar包
 * @author shaozhe
 *
 */
public class ReadJar {
	static final String path = Environment.getExternalStorageDirectory().getPath()+"/jarTest/temp.jar";
//	static final String desKey = "NXNtNTgwMWM2RTU5RXEzbA==";
	/**
	 * 读取jar包数据
	 * @return
	 */
	private static byte[] readAderSdk(){
		byte[] sdkJarData= null;
		File f = new File(path);
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			InputStream in = new FileInputStream(f);
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				byteOut.write(buffer, 0, len);
			}
			sdkJarData = byteOut.toByteArray();
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sdkJarData;
   }
	/**
	 * 将二进制jar包数据转成base64编码的字符串
	 * @param desKey
	 * @return
	 */
	public static String getEncryptJarStr(){
	    String enStr = "";
		try {
			byte[] data = readAderSdk();
			byte[] key = AderBase64.decode(LoadCM.jarKey);
			byte[] enJarData = DESUtils.encrypt(data, key);
			enStr = AderBase64.encode(enJarData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return enStr;
	}
}
