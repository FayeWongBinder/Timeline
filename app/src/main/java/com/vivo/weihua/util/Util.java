package com.vivo.weihua.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.vivo.weihua.WeiApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    private static final String TAG = Util.class.getSimpleName();
    private static final String[] SELECTIMAGES = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.SIZE
    };

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public static String getCurrentHours() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:00");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 秒转换为指定格式的日期
     *
     * @param second yyyy-MM-dd hh:mm:ss
     * @return
     */
    public static String secondToDate(long second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String dateString = format.format(date);
        return dateString;
    }

    public static boolean crateFiles() {
        File folder = new File(Environment.getExternalStorageDirectory() + "TimeMap");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        return success;
    }

    /**
     * 根据用户的起点和终点经纬度计算两点间距离，此距离为相对较短的距离，单位米。
     *
     * @param start 起点的坐标
     * @param end   终点的坐标
     * @return
     */
    public static double calculateLineDistance(LatLng start, LatLng end) {
        if ((start == null) || (end == null)) {
            throw new IllegalArgumentException("非法坐标值，不能为null");
        }
        double d1 = 0.01745329251994329D;
        double d2 = start.longitude;
        double d3 = start.latitude;
        double d4 = end.longitude;
        double d5 = end.latitude;
        d2 *= d1;
        d3 *= d1;
        d4 *= d1;
        d5 *= d1;
        double d6 = Math.sin(d2);
        double d7 = Math.sin(d3);
        double d8 = Math.cos(d2);
        double d9 = Math.cos(d3);
        double d10 = Math.sin(d4);
        double d11 = Math.sin(d5);
        double d12 = Math.cos(d4);
        double d13 = Math.cos(d5);
        double[] arrayOfDouble1 = new double[3];
        double[] arrayOfDouble2 = new double[3];
        arrayOfDouble1[0] = (d9 * d8);
        arrayOfDouble1[1] = (d9 * d6);
        arrayOfDouble1[2] = d7;
        arrayOfDouble2[0] = (d13 * d12);
        arrayOfDouble2[1] = (d13 * d10);
        arrayOfDouble2[2] = d11;
        double d14 = Math.sqrt((arrayOfDouble1[0] - arrayOfDouble2[0]) * (arrayOfDouble1[0] - arrayOfDouble2[0])
                + (arrayOfDouble1[1] - arrayOfDouble2[1]) * (arrayOfDouble1[1] - arrayOfDouble2[1])
                + (arrayOfDouble1[2] - arrayOfDouble2[2]) * (arrayOfDouble1[2] - arrayOfDouble2[2]));

        return (Math.asin(d14 / 2.0D) * 12742001.579854401D);
    }


    private void getPhotoLocation(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                SELECTIMAGES,
                null,
                null,
                null);
        int i = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {

                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File file = new File(path);
//                if (!file.exists() || !file.canRead()) continue;

                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                long addDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                long modifyDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                float latitude = cursor.getFloat(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
                float longitude = cursor.getFloat(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                i++;
                Log.e(TAG + i, "onCreate: "
                        + "path:-------" + path + "\n"
                        + "name:-------" + name + "            "
                        + "title:------" + title + "     "
                        + "addDate:----" + addDate + "\n"
                        + "modifyDate:-" + modifyDate + "\n"
                        + "latitude:---" + latitude + "    "
                        + "longitude:--" + longitude + "       "
                        + "size:-------" + size
                );
            }
            cursor.close();
        }
    }

}
