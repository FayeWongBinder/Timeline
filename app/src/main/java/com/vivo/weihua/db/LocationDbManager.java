package com.vivo.weihua.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.vivo.weihua.WeiApplication;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.util.Util;

import java.util.ArrayList;

public class LocationDbManager {
    private static final String TAG = LocationDbManager.class.getSimpleName();
    private static LocationDbManager sInstance;
    private DbHelper mDbHelper;

    private LocationDbManager() {
        mDbHelper = new DbHelper(WeiApplication.getContext(), "location-db");
    }

    public static LocationDbManager getInstance() {
        if (sInstance == null) {
            synchronized (LocationDbManager.class) {
                if (sInstance == null) {
                    sInstance = new LocationDbManager();
                }
            }
        }
        return sInstance;
    }

    public void insert(LocationBean bean) {
        SQLiteDatabase sqliteDatabase = mDbHelper.getReadableDatabase();
        // 创建ContentValues对象
        ContentValues values = new ContentValues();
        // 向该对象中插入键值对
        values.put("day", bean.getDay());
        values.put("time", bean.getTime());
        values.put("address", bean.getAddress());
        values.put("latitude", bean.getLatitude());
        values.put("longitude", bean.getLongitude());
        sqliteDatabase.insert("location", null, values);
        sqliteDatabase.close();
    }

    /**
     * 查询间隔 1小时 100米
     *
     * @return
     */
    public int query(String cAddress, LatLng mLatLng) {
        int end = (int) (System.currentTimeMillis() / 1000);
        int start = end - 60 * 60 * 1;
        int count = 0;
        boolean has = false;
        SQLiteDatabase sqliteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query("location", new String[]{"address", "latitude", "longitude"},
                "time between? and?", new String[]{start + "", end + ""}, null, null, null);
        String address = null;
        LatLng latLng = null;

        while (cursor.moveToNext()) {
            address = cursor.getString(cursor.getColumnIndex("address"));
            String lat = cursor.getString(cursor.getColumnIndex("latitude"));
            String lng = cursor.getString(cursor.getColumnIndex("longitude"));
            latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
            if (cAddress.equals(address)) {
                has = true;
            }
        }
        count = cursor.getCount();
        //判断地址是否相等
        float value ;

        if (count > 0 && mLatLng != null) {
             value = AMapUtils.calculateLineDistance(mLatLng, latLng);
            Log.e(TAG, address + "----" + cAddress + "  距离：" + value);
            if (value >= 100.0) {
                count = 0;
                if (has == true) {
                    count = 1;
                }
            } else if (value < 100) {
                count = 1;
            }
        }

        cursor.close();
        sqliteDatabase.close();
        return count;
    }

    public ArrayList<LocationBean> queryAll() {
        ArrayList<LocationBean> data = new ArrayList<>();
        SQLiteDatabase sqliteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query("location", new String[]{"day", "time", "address", "latitude", "longitude"},
                null, null, null, null, null);
        if(cursor.isClosed()){
            return data;
        }
        while (cursor.moveToNext()) {
            String day = cursor.getString(cursor.getColumnIndex("day"));
            int time = cursor.getInt(cursor.getColumnIndex("time"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            LocationBean bean = new LocationBean(day, time, address, latitude, longitude);
            data.add(0, bean);
        }
        cursor.close();
        sqliteDatabase.close();
        return data;
    }


}
