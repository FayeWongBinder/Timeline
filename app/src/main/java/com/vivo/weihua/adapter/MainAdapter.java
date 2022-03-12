package com.vivo.weihua.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.vivo.weihua.R;
import com.vivo.weihua.bean.CountBean;
import com.vivo.weihua.util.Constant;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainHolder> {
    Context mContext;
    List<CountBean> mData;
    MapView mMapView;
    AMap aMap;
    AddressAdapter addressAdapter;

    public MainAdapter(Context context, List<CountBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(mContext, R.layout.main_item, null);

        return new MainHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        mMapView = holder.mapView;
        mMapView.onCreate(null);

        aMap = mMapView.getMap();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setLogoBottomMargin(-70);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setAllGesturesEnabled(false);

        aMap.setCustomMapStyle(new com.amap.api.maps.model.CustomMapStyleOptions()
                .setEnable(true)
                .setStyleId(Constant.STYLE_ID));

        CountBean countBean = mData.get(position);
        holder.tvDay.setText(countBean.getDay());
        LatLng latLng = new LatLng(countBean.getLocationList().get(0).getLatitude(), countBean.getLocationList().get(0).getLongitude());
        Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(countBean.getLocationList().get(0).getAddress()));

        CameraPosition cameraPosition = new CameraPosition(latLng, 15, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        aMap.moveCamera(cameraUpdate);

        addressAdapter = new AddressAdapter(mContext, mData.get(position).getLocationList());
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        holder.recyclerView.setLayoutManager(manager);
        holder.recyclerView.setAdapter(addressAdapter);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MainHolder extends RecyclerView.ViewHolder {
        MapView mapView;
        TextView tvDay;
        RecyclerView recyclerView;

        public MainHolder(@NonNull View itemView) {
            super(itemView);
            mapView = itemView.findViewById(R.id.item_map);
            tvDay = itemView.findViewById(R.id.tv_day);
            recyclerView = itemView.findViewById(R.id.rv_address);
        }
    }


}
