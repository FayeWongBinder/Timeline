package com.vivo.weihua.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;
import com.vivo.weihua.R;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.util.Constant;
import com.vivo.weihua.util.LocalDataPool;

import java.util.List;

public class TestStackAdapter extends StackAdapter<String> {
    static List<LocationBean> mLocationList = null;

    public TestStackAdapter(Context context) {
        super(context);
    }

    public TestStackAdapter(Context context, List<LocationBean> data) {
        super(context);
        mLocationList = data;
    }


    @Override
    public void bindView(String data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view);

    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;
        MapView mapView;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = view.findViewById(R.id.text_list_card_title);
            mapView = view.findViewById(R.id.mv_card);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(String data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), LocalDataPool.COLOR_DATA[position % 10]), PorterDuff.Mode.SRC_IN);
            String value = data.replaceFirst("-", "年");
            mTextTitle.setText(value.replaceFirst("-", "月"));
            AMap aMap = mapView.getMap();
            mapView.onCreate(null);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setLogoBottomMargin(-70);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setAllGesturesEnabled(false);

            aMap.setCustomMapStyle(new com.amap.api.maps.model.CustomMapStyleOptions()
                    .setEnable(true)
                    .setStyleId(Constant.STYLE_ID));
            LocationBean countBean = mLocationList.get(position);
            LatLng latLng = new LatLng(countBean.getLatitude(), countBean.getLongitude());
            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(countBean.getAddress()));

            CameraPosition cameraPosition = new CameraPosition(latLng, 15, 0, 30);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            aMap.moveCamera(cameraUpdate);

        }

    }


}
