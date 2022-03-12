package com.vivo.weihua.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import com.vivo.weihua.WeiApplication;

/**
 * SharedPreferences工具类
 *
 * @author Administrator
 */
public class SpUtil {
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static void putString(Context context, String name, String value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(name, value);
        edit.commit();
    }

    public static String getString(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences("config",
                context.MODE_PRIVATE);
        return sp.getString(name, "");
    }

    public static void putInt(String name, int value) {
        SharedPreferences sp = WeiApplication.getContext().getSharedPreferences("config", WeiApplication.getContext().MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt(name, value);
        edit.commit();
    }

    public static int getInt(String name) {
        SharedPreferences sp = WeiApplication.getContext().getSharedPreferences("config",
                WeiApplication.getContext().MODE_PRIVATE);
        return sp.getInt(name, 0);
    }


}