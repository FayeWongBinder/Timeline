package com.vivo.weihua.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vivo.weihua.R;

public class AboutMeActivity extends AppCompatActivity {
    public static final String TAG = AboutMeActivity.class.getSimpleName();
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        linearLayout = findViewById(R.id.ll_test);

//        layoutParams.leftMargin=600;

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(linearLayout.getLayoutParams());
                layoutParams.gravity=Gravity.START;

                linearLayout.setLayoutParams(layoutParams);
                TextView textView= (TextView) linearLayout.getChildAt(0);
                textView.setText("2022神奇的一年");
//                textView.setGravity(Gravity.CENTER);

            }
        });


    }


    public void toBack(View view) {
        finish();
    }
}
