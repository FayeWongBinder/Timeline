package com.vivo.weihua.db;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.util.Constant;
import com.vivo.weihua.util.LocalThreadPool;
import com.vivo.weihua.util.Util;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AutoServer extends JobService {
    public static final String TAG=AutoServer.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {
//        Log.e("AutoServer","------------------");
        LocalThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        });

        doService();
        return false;
    }
    private void doService() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, AutoServer.class));  //指定哪个JobService执行操作
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(Constant.LATENCY_TIME)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(Constant.LATENCY_TIME));  //执行的最长延时时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10000), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
        builder.setRequiresCharging(false); // 未充电状态
        jobScheduler.schedule(builder.build());
    }
    private void getLocation() {
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        AMapLocationClient mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        String address = amapLocation.getStreet() + "." + amapLocation.getPoiName();
                        double lat = amapLocation.getLatitude();
                        double lng = amapLocation.getLongitude();
                        LatLng latLng = new LatLng(lat, lng);
                        Log.e(TAG,"服务数据:"+address);
                        int count = LocationDbManager.getInstance().query(address, latLng);
                        if (count != 0) {
                            return;
                        }
                        if (!TextUtils.isEmpty(amapLocation.getStreet())) {
                            LocationBean bean = new LocationBean(Util.getCurrentData(), (int) (System.currentTimeMillis() / 1000), address, lat, lng);
                            LocationDbManager.getInstance().insert(bean);
                        }
                    }
                }
            }
        });

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationClient.setLocationOption(option);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setLocationCacheEnable(false);
        mLocationOption.setMockEnable(true);

        mLocationClient.setLocationOption(mLocationOption);

        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
