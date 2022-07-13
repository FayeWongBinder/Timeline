package com.vivo.weihua.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vivo.weihua.R;
import com.vivo.weihua.util.Util;

public class AddActivity extends AppCompatActivity {
    TextView tvTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().hide();
        tvTime = findViewById(R.id.tv_time);
        tvTime.setText(Util.getCurrentData());


    }

}
