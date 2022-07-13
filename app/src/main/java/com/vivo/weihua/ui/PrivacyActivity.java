package com.vivo.weihua.ui;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vivo.weihua.R;

import java.io.File;
import java.io.IOException;

public class PrivacyActivity extends AppCompatActivity {
    TextureView textureView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.layout_hide);
        getSupportActionBar().hide();
        String imgFilePath1 = Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_1075af43ebf11095b97f4acca592b2ac.mp4";
        String imgFilePath2 = Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_2dd55d6957a39df2c2222142f499d2be.mp4";
        String imgFilePath3 = Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_fda6129dd0009bce39c5ade4fd0af162.mp4";
        String imgFilePath4= Environment.getExternalStorageDirectory() + "/DCIM/Camera/share_0f629b75855ad7c951b0cb0d77cdd882.mp4";


        textureView = findViewById(R.id.vvv_view);
         mediaPlayer = new MediaPlayer();
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                Surface face = new Surface(surfaceTexture);
                try {
                    mediaPlayer.setDataSource(imgFilePath1);
                    mediaPlayer.setSurface(face);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
//                    mediaPlayer.release();
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });


    }

    public void toBack(View view) {
        finish();
    }


    public void reStart(View view) {
        mediaPlayer.start();
    }
}
