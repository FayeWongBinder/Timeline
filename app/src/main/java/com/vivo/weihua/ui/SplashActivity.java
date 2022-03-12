package com.vivo.weihua.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.vivo.weihua.R;

public class SplashActivity extends AppCompatActivity {
    public static void setWindowStatusBarColor(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#A52D36"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowStatusBarColor(this);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        int value = SpUtil.getInt("agree");
        if (value == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 800);
        } else {
            customLayoutDialogBox(this);
        }

    }

    /**
     * 7.自定义布局对话框
     */
    protected void customLayoutDialogBox(final Activity activity) {
        final AlertDialog.Builder dialogBox = new AlertDialog.Builder(activity);
        View view = View.inflate(activity, R.layout.dialog_custom, null);
        dialogBox.setView(view);
        final Dialog dialog = dialogBox.create();
        TextView tvTxt = view.findViewById(R.id.tv_read);
        tvTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, PrivacyActivity.class);
                startActivity(intent);
            }
        });
        CheckBox checkBox = view.findViewById(R.id.cb_check);

        MaterialButton materialButton = view.findViewById(R.id.mb_submit);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    SpUtil.putInt("agree", 1);
                    dialog.dismiss();
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(activity, "请阅读和勾选《隐私政策》", Toast.LENGTH_SHORT).show();
                }
            }
        });
        MaterialButton materialRefuse = view.findViewById(R.id.mb_refuse);
        materialRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            customLayoutDialogBox(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
