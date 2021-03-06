package com.jimmydaddy.imagemarker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jimmydaddy on 2018/4/8.
 */

public class Utils {
    public static String TAG = "[ImageMarker]";
    /**
     * 获取最大内存使用
     * @return
     */
    public  static int getMaxMemory () {
        return (int)Runtime.getRuntime().maxMemory()/1024;
    }


    /**
     * read stream from remote
     * @param url
     * @return
     */
    private InputStream getStreamFromInternet(String url) {
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.setRequestMethod("GET");
            // 10 秒超时时间
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            connection.connect();

            int responeseCode = connection.getResponseCode();

            if (responeseCode == 200) {
                InputStream is = connection.getInputStream();
                return is;
            } else {
                Log.d(TAG, "getStreamFromInternet: read stream from remote: "+url+ " failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }


    public static Bitmap getBlankBitmap(int width, int height){
        Bitmap icon = null;
        try {
            icon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            System.out.print(e.getMessage());
            while(icon == null) {
                System.gc();
                System.runFinalization();
                icon = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            }
        }

        return icon;
    }

    public static int readDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static Bitmap rotateBitmap(String path, Float scale) {
        int degree = readDegree(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap prePhoto = null;
        try {
            prePhoto = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            System.out.print(e.getMessage());
            while(prePhoto == null) {
                System.gc();
                System.runFinalization();
                prePhoto = BitmapFactory.decodeFile(path, options);
            }
        }
//

        if(prePhoto == null)
            return null ;

        int w = options.outWidth;
        int h = options.outHeight;

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        if (scale != 1 && scale >= 0) {
            mtx.postScale(scale, scale);
        }

        Bitmap rotatedBitmap = null;

        try {
            rotatedBitmap = Bitmap.createBitmap(prePhoto, 0, 0, w, h, mtx, true);
        } catch (OutOfMemoryError e) {
            System.out.print(e.getMessage());
            while(rotatedBitmap == null) {
                System.gc();
                System.runFinalization();
                rotatedBitmap = Bitmap.createBitmap(prePhoto, 0, 0, w, h, mtx, true);
            }
        }
        return rotatedBitmap;
    }

//
//    public static BitmapFactory.Options getOptions () {
//
//    }


}
