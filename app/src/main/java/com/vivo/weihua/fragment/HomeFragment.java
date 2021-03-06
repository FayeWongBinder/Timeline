package com.vivo.weihua.fragment;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

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
import android.widget.ImageView;
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
import com.vivo.weihua.ui.AddActivity;
import com.vivo.weihua.util.Constant;
import com.vivo.weihua.util.LocalDataPool;
import com.vivo.weihua.util.Util;

import org.feezu.liuli.timeselector.TimeSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();
    //AI????????????
    RecyclerView recyclerView;
    MainAdapter adapter;
    Context mContext;
    List<CountBean> mCountBeanList;
    JobScheduler jobScheduler;
    ImageView calendarItem;
    ImageView addItem;

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
        calendarItem = view.findViewById(R.id.calendar_item);
        addItem = view.findViewById(R.id.add_item);
        calendarItem.setOnClickListener(this);
        addItem.setOnClickListener(this);

        queryAndUpdateAdapter();
        LocationManager lm = (LocationManager) WeiApplication.getContext().getSystemService(mContext.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//??????????????????
            initPermissionAddress();
        } else {
            Toast.makeText(mContext, "????????????????????????GPS????????????", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void doService() {
        jobScheduler = (JobScheduler) mContext.getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(mContext, AutoServer.class)); //????????????JobService????????????
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(Constant.LATENCY_TIME)); //???????????????????????????
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(Constant.LATENCY_TIME)); //???????????????????????????
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING); //?????????????????????
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10000), JobInfo.BACKOFF_POLICY_LINEAR); //??????????????????
        builder.setRequiresCharging(false); // ???????????????
        jobScheduler.schedule(builder.build());

    }


    /**
     * ????????????????????????adapter
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
                    //????????????????????? ???????????????
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
                        recyclerView.getItemAnimator().setMoveDuration(0);
                        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

                    }
                });


    }


    /**
     * ????????????????????????????????????
     */
    private void initPermissionAddress() {
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : LocalDataPool.getPermissions()) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, perm)) {
                toApplyList.add(perm);
                //?????????????????????????????????.
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
     * ??????????????????
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

        //???????????????????????????????????????stop????????????start???????????????????????????
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calendar_item:
                TimeSelector timeSelector = new TimeSelector(mContext, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        Toast.makeText(mContext, time, Toast.LENGTH_LONG).show();
                    }
                }, "2021-03-15 00:00", Util.getCurrentData() + " 00:00");
                timeSelector.setMode(TimeSelector.MODE.YMDHM);//?????? ??????????????????????????????
                timeSelector.setMode(TimeSelector.MODE.YMD);//????????? ?????????
                timeSelector.show();
                break;
            case R.id.add_item:
                Intent intent = new Intent();
                intent.setClass(mContext, AddActivity.class);
                startActivity(intent);
                break;
        }


    }
}
