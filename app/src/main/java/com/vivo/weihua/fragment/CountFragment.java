package com.vivo.weihua.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.loopeer.cardstack.CardStackView;
import com.vivo.weihua.R;
import com.vivo.weihua.adapter.TestStackAdapter;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.db.LocationDbManager;
import com.vivo.weihua.util.Constant;
import com.vivo.weihua.util.LocalDataPool;
import com.vivo.weihua.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CountFragment extends Fragment implements CardStackView.ItemExpendListener {
    private static final String TAG = CountFragment.class.getSimpleName();
    MapView mMapView;
    AMap aMap;
    public static String[] TEST_DATAS = new String[]{
            "2022-3-7",
            "2022-3-8",
            "2022-3-9",
            "2022-3-10",
            "2022-3-11",
            "2022-3-12",
            "2022-3-13",
            "2022-3-14",
    };
    CardStackView mStackView;
    TestStackAdapter mTestStackAdapter;
    private LinearLayout mActionButtonContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setWindowStatusBarColor(getActivity());
        View view = inflater.inflate(R.layout.fragment_count, container, false);

        mStackView = view.findViewById(R.id.cv_stack);
        mTestStackAdapter = new TestStackAdapter(getActivity());
        mStackView.setAdapter(mTestStackAdapter);
//        mTestStackAdapter.updateData(Arrays.asList(TEST_DATAS));
        mStackView.setItemExpendListener(this);
        mActionButtonContainer = view.findViewById(R.id.button_container);


        mMapView = view.findViewById(R.id.mv_count);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是 否显示
        aMap.getUiSettings().setLogoBottomMargin(-70);
        aMap.getUiSettings().setZoomControlsEnabled(false);

        aMap.setCustomMapStyle(new com.amap.api.maps.model.CustomMapStyleOptions()
                .setEnable(true)
                .setStyleId(Constant.STYLE_ID_TWO));
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
                        List<String> dataList = new ArrayList<>();
                        for (int i = 0; i < locationList.size(); i++) {
                            dataList.add(locationList.get(i).getDay());
                        }
//                        mTestStackAdapter = new TestStackAdapter(getActivity(),locationList);
//                        mStackView.setAdapter(mTestStackAdapter);
//                        mTestStackAdapter.updateData(dataList);

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


    @Override
    public void onItemExpend(boolean expend) {
        mActionButtonContainer.setVisibility(expend ? View.VISIBLE : View.GONE);
        int a = mStackView.getSelectPosition() % 10;
        Log.e(TAG, "------" + a);
        if (a >= 0 && a <= 10) {
            Util.setWindowStatusBarColor(getActivity(), LocalDataPool.COLOR_DATA[a]);
        }

    }

    public void onPreClick1(View view) {
        mStackView.pre();
    }

    public void onNextClick1(View view) {
        mStackView.next();
    }
}
