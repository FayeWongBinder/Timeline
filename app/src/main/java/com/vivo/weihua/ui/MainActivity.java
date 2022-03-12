package com.vivo.weihua.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.vivo.weihua.R;
import com.vivo.weihua.fragment.CountFragment;
import com.vivo.weihua.fragment.MeFragment;
import com.vivo.weihua.util.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    NoScrollViewPager viewPager;
    TabLayout tabLayout;
    List<Fragment> mFragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = findViewById(R.id.vp_page);
        tabLayout = findViewById(R.id.tabLayout);
        init();


    }



    private void init() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new CountFragment());
        mFragmentList.add(new MeFragment());
        int[] tabPicture = new int[]{R.drawable.tab_home_selector
                , R.drawable.tab_count_selector, R.drawable.tab_me_selector};
        String texts[] = {"首页", "统计", "我的"};
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }
        });
        viewPager.setNoScroll(false);

        tabLayout.setupWithViewPager(viewPager);
        LayoutInflater inflater = this.getLayoutInflater();
        for (int i = 0; i < mFragmentList.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View view = inflater.inflate(R.layout.tab_custom, null);
            TextView tvTitle = view.findViewById(R.id.tv_tab);
            ImageView imgTab = view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabPicture[i]);
            tvTitle.setText(texts[i]);
            tab.setCustomView(view);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}