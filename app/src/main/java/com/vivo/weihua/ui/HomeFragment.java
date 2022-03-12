package com.vivo.weihua.ui;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.vivo.weihua.R;
import com.vivo.weihua.WeiApplication;
import com.vivo.weihua.adapter.MainAdapter;
import com.vivo.weihua.bean.CountBean;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.db.AutoServer;
import com.vivo.weihua.db.LocationDbManager;
import com.vivo.weihua.util.Constant;
import com.vivo.weihua.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    //AI记录每天
    RecyclerView recyclerView;
    MainAdapter adapter;
    Context mContext;
    List<CountBean> mCountBeanList;
    JobScheduler jobScheduler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doService();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.home_recycler);

        queryAndUpdateAdapter();

        LocationManager lm = (LocationManager) WeiApplication.getContext().getSystemService(mContext.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            initPermissionAddress();
        } else {
            Toast.makeText(mContext, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void doService() {
        jobScheduler = (JobScheduler) mContext.getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(mContext, AutoServer.class)); //指定哪个JobService执行操作
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(Constant.LATENCY_TIME)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(Constant.LATENCY_TIME)); //执行的最长延时时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING); //非漫游网络状态
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10000), JobInfo.BACKOFF_POLICY_LINEAR); //线性重试方案
        builder.setRequiresCharging(false); // 未充电状态
        jobScheduler.schedule(builder.build());

    }


    /**
     * 查询数据库并更新adapter
     */
    private void queryAndUpdateAdapter() {

        Observable.create(new ObservableOnSubscribe<List<CountBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CountBean>> emitter) throws Throwable {
                List<LocationBean> locationList = LocationDbManager.getInstance().queryAll();
                ArrayList<CountBean> countList = new ArrayList<>();
                for (int i = 0; i < locationList.size(); i++) {
                    String day = locationList.get(i).getDay();
                    String address = locationList.get(i).getAddress();
                    int size = 0;
                    CountBean bean = new CountBean();
                    bean.setDay(day);
                    List<LocationBean> tempData = new ArrayList<>();
                    //将某一天的数据 存入集合里
                    for (int j = i; j < locationList.size(); j++) {
                        String tDay = locationList.get(j).getDay();
                        if (day.equals(tDay)) {
                            tempData.add(locationList.get(j));
                            size = size + 1;
                        }

                    }
                    bean.setLocationList(tempData);
                    i = i + size - 1;
                    countList.add(bean);
                }

                emitter.onNext(countList);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CountBean>>() {
                    @Override
                    public void accept(List<CountBean> countBeanList) throws Throwable {
                        mCountBeanList = new ArrayList<>();
                        mCountBeanList = countBeanList;
                        adapter = new MainAdapter(mContext, mCountBeanList);
                        LinearLayoutManager manager = new LinearLayoutManager(mContext);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.getItemAnimator().setChangeDuration(0);
                        recyclerView.getItemAnimator().setAddDuration(0L);
                        recyclerView.getItemAnimator().setChangeDuration(0);
                        recyclerView.getItemAnimator().setMoveDuration(0);
                        recyclerView.getItemAnimator().setRemoveDuration(0);
                        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

                    }
                });


    }


    /**
     * 初始化权限和获取地址信息
     */
    private void initPermissionAddress() {
        String permissions[] = {
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECEIVE_BOOT_COMPLETED

        };
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        getLocation();

        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1315) {
            initPermissionAddress();
        }
    }

    /**
     * 获取定位信息
     */
    private void getLocation() {
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        AMapLocationClient mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        String address = amapLocation.getStreet() + "." + amapLocation.getPoiName();
                        double lat = amapLocation.getLatitude();
                        double lng = amapLocation.getLongitude();
                        LatLng latLng = new LatLng(lat, lng);

                        int count = LocationDbManager.getInstance().query(address,latLng);
                        if (count != 0) {
                            return;
                        }
                        if (!TextUtils.isEmpty(amapLocation.getStreet())) {
                            Log.e(TAG, address);
                            LocationBean bean = new LocationBean(Util.getCurrentData(), (int) (System.currentTimeMillis() / 1000), address, lat, lng);
                            LocationDbManager.getInstance().insert(bean);
                            if (mCountBeanList.isEmpty()) {
                                return;
                            }
                            List<LocationBean> tempData = mCountBeanList.get(0).getLocationList();
                            if (tempData == null || tempData.isEmpty()) {
                                tempData = new ArrayList<>();
                                tempData.add(0, bean);
                                CountBean countBean = new CountBean();
                                countBean.setDay(Util.getCurrentData());
                                countBean.setLocationList(tempData);
                                mCountBeanList.add(0, countBean);
                                adapter.notifyItemChanged(0);
                            }
                            tempData.add(0, bean);
                            CountBean countBean = new CountBean();
                            countBean.setDay(Util.getCurrentData());
                            countBean.setLocationList(tempData);
                            mCountBeanList.set(0, countBean);
                            adapter.notifyItemChanged(0);
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        jobScheduler.cancelAll();
    }
}
