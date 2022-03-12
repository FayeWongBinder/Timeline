package com.vivo.weihua.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.vivo.weihua.R;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.db.LocationDbManager;
import com.vivo.weihua.util.Constant;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CountFragment extends Fragment {
    private static final String TAG = CountFragment.class.getSimpleName();

    MapView mMapView;
    AMap aMap;

    public static void setWindowStatusBarColor(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#2a2a2a"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setWindowStatusBarColor(getActivity());
        View view = inflater.inflate(R.layout.fragment_count, container, false);

        mMapView = view.findViewById(R.id.mv_count);
        mMapView.onCreate(savedInstanceState);

        aMap = mMapView.getMap();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是 否显示
        aMap.getUiSettings().setLogoBottomMargin(-70);
        aMap.getUiSettings().setZoomControlsEnabled(false);

        aMap.setCustomMapStyle(new com.amap.api.maps.model.CustomMapStyleOptions()
                .setEnable(true)
                .setStyleId(Constant.STYLE_ID));

//        aMap.setCustomMapStyle(
//                new com.amap.api.maps.model.CustomMapStyleOptions()
//                        .setEnable(true)
//                        .setStyleDataPath("/mnt/sdcard/amap/style.data")
//                        .setStyleExtraPath("/mnt/sdcard/amap/style_extra.data")
//                        .setStyleTexturePath("/mnt/sdcard/amap/textures.zip")
//        );
//        File file=new File("/mnt/sdcard/amap/style.data");
//            boolean isFile=file.isFile();
//
//        File pic=new File("/mnt/sdcard/DCIM");
//        boolean isFiles=pic.exists();
//        Log.e(TAG,"------"+isFile+"-----"+isFiles);
        Observable.create(new ObservableOnSubscribe<List<LocationBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LocationBean>> emitter) throws Throwable {
                List<LocationBean> locationList = LocationDbManager.getInstance().queryAll();
                emitter.onNext(locationList);

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<LocationBean>>() {
                    @Override
                    public void accept(List<LocationBean> locationList) throws Throwable {
                        if (locationList.isEmpty()) {
                            return;
                        }
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        LatLngBounds bounds = null;
                        for (int i = 0; i < locationList.size(); i++) {
                            LatLng latLng = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
                            aMap.addMarker(new MarkerOptions().position(latLng).title(locationList.get(i).getAddress()));
                            builder.include(latLng);
                        }
                        bounds = builder.build();
                        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60));
                    }
                });
        return view;
    }
}
