package com.vivo.weihua.adapter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.vivo.weihua.R;
import com.vivo.weihua.ui.AboutMeActivity;

import java.io.IOException;
import java.util.List;

public class MyAdapter extends PagerAdapter {
    public static final String TAG = AboutMeActivity.class.getSimpleName();
    List<String> mData;
    Context mContext;
    OnPlayCompletionListener mPlayCompletionListener;
    String[] item = {"王菲", "范冰冰", "韩佳人", "孙艺珍", "章卫华"};
    boolean isClick=false;
    int index;
    public MyAdapter(Context context, List<String> address) {
        this.mContext = context;
        this.mData = address;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
        TextureView textureView = v.findViewById(R.id.vvv_view);
        Log.e(TAG,"----COMING----"+index);
        MediaPlayer mediaPlayer= new MediaPlayer();
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                Surface face = new Surface(surfaceTexture);
                try {
                    mediaPlayer.setDataSource(mData.get(position));
                    mediaPlayer.setSurface(face);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(isClick==false) {
                    mPlayCompletionListener.onResult(position);

                }
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                isClick=true;
                mediaPlayer.stop();
            }
        });
        container.addView(v);
        return v;
    }
    public void setReStart(int position){
        notifyDataSetChanged();

    }


    public void setOnPlayCompletionListener(OnPlayCompletionListener onPlayCompletionListener) {
        this.mPlayCompletionListener = onPlayCompletionListener;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return item[position];
    }

    public interface OnPlayCompletionListener {
        void onResult(int index);
    }
}
