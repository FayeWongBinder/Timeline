package com.vivo.weihua.ui;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.vivo.weihua.R;
import com.vivo.weihua.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutMeActivity extends AppCompatActivity {
    public static final String TAG = AboutMeActivity.class.getSimpleName();
    ViewPager viewPager;
    List<String> address = new ArrayList<>();
    MyAdapter myAdapter;
    boolean isClick=false;
    private BannerView banner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        setContentView(R.layout.layout_test);
        getSupportActionBar().hide();


        String imgFilePath1 = Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_1075af43ebf11095b97f4acca592b2ac.mp4";
        String imgFilePath2 = Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_2dd55d6957a39df2c2222142f499d2be.mp4";
        String imgFilePath3 = Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_fda6129dd0009bce39c5ade4fd0af162.mp4";
        String imgFilePath4= Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_0f629b75855ad7c951b0cb0d77cdd882.mp4";
        address.add(imgFilePath1);
//        address.add(imgFilePath2);
//        address.add(imgFilePath3);
//        address.add(imgFilePath4);

        banner = ((BannerView) findViewById(R.id.banner_view));
        banner.setAdapter(address);
//        banner.setStartbanner();

    }


    public void toBack(View view) {
        finish();
    }
}
