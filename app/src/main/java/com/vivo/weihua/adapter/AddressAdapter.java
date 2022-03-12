package com.vivo.weihua.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivo.weihua.R;
import com.vivo.weihua.bean.LocationBean;
import com.vivo.weihua.util.Util;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MainHolder> {
    Context mContext;
    List<LocationBean> mData;

    public AddressAdapter(Context context, List<LocationBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(mContext, R.layout.home_address_item, null);

        return new MainHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        holder.tvAddress.setText(Util.secondToDate(mData.get(position).getTime()) + "  " + mData.get(position).getAddress());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MainHolder extends RecyclerView.ViewHolder {
        TextView tvAddress;

        public MainHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }

}
