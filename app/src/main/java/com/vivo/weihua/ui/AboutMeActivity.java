package com.vivo.weihua.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vivo.weihua.R;

public class AboutMeActivity extends AppCompatActivity {
    public static final String TAG = AboutMeActivity.class.getSimpleName();
    ScrollView scrollView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();

        scrollView = findViewById(R.id.sv);


    }


    public void toBack(View view) {
        finish();
    }
}
