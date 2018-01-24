package com.rrgame.sdk.reportinfo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils { 
    
    private static final int BUFFER = 1024 * 4;

    /**
     * 压缩
     *
     * @param data
     * @return
     * @throws java.io.IOException
     */
    public static byte[] compress(byte[] data) throws IOException {
    	if(data == null){
    		return null;
    	}
        byte[] output = null;
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gos = null;
        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);
            int count;
            byte[] buf = new byte[BUFFER];
            while ((count = bais.read(buf, 0, BUFFER)) != -1) {
                gos.write(buf, 0, count);
            }
            gos.finish();
            gos.flush();

            output = baos.toByteArray();
        } finally {
            bais.close();
        	baos.close();
        	gos.close();
        }

        return output;
    }

    /**
     * 解压
     *
     * @param data
     * @return
     * @throws java.io.IOException
     */
    public static byte[] decompress(byte[] data) throws IOException {
    	if(data == null){
    		return null;
    	}
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        GZIPInputStream gis = null;

        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();
            gis = new GZIPInputStream(bais);
            int count;
            byte buf[] = new byte[BUFFER];
            while ((count = gis.read(buf, 0, BUFFER)) != -1) {
                baos.write(buf, 0, count);
            }
            data = baos.toByteArray();
        } finally {
        	bais.close();
        	baos.close();
        	gis.close();
        }
        return data;
    }

}
