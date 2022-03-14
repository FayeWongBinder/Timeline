package com.vivo.weihua;

import android.app.Application;
import android.content.Context;

//import com.tencent.bugly.crashreport.CrashReport;
import com.vivo.weihua.util.Constant;

public class WeiApplication extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        CrashReport.initCrashReport(this, Constant.CRASH_ID, true);
    }
}
