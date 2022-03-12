package com.vivo.weihua.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.vivo.weihua.R;
import com.vivo.weihua.ui.AboutMeActivity;
import com.vivo.weihua.ui.PrivacyActivity;

public class MeFragment extends Fragment implements View.OnClickListener {
    MaterialButton mbAbout;
    MaterialButton mbPrivacy;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        mbAbout = view.findViewById(R.id.mb_about);
        mbPrivacy = view.findViewById(R.id.mb_privacy);
        mbAbout.setOnClickListener(this);
        mbPrivacy.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.mb_about:
                Intent intent = new Intent();
                intent.setClass(mContext, AboutMeActivity.class);
                startActivity(intent);
                break;
            case R.id.mb_privacy:
                Intent intent1 = new Intent();
                intent1.setClass(mContext, PrivacyActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
