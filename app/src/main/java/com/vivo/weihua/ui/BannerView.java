package com.vivo.weihua.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.vivo.weihua.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供外部使用的bannerView
 */
public class BannerView extends RelativeLayout {
    String TAG = "BannerView";
    private Context mContent;
    private View view;
    private BannerViewPage viewPage;
    private LinearLayout bannerContainer;
    private BannerAdapter adapter;
    private int IndicatorPosition;
    private boolean isAccording = true;
    private Drawable checkIndication;
    private Drawable nokIndication;
    private int mCurrentPosition;
    private int mDosize;
    private OnClickListener listener;
    private int INTT_PAGE = Integer.MAX_VALUE / 2;
    private boolean isOk;
    Map<Integer,Boolean> key=new HashMap<>();
    MediaPlayer mediaPlayer[] = new MediaPlayer[]{new MediaPlayer(), new MediaPlayer()};
    TextureView textureView;
    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContent = context;
        removeAllViews();
        view = LayoutInflater.from(context).inflate(R.layout.bnner_view, this);
        initView();
        inttType(attrs);

    }

    private void inttType(AttributeSet attrs) {
        TypedArray array = mContent.obtainStyledAttributes(attrs, R.styleable.BannerView);
        IndicatorPosition = array.getInt(R.styleable.BannerView_doGravity, 0);
        mDosize = array.getInt(R.styleable.BannerView_dotSize, 6);
        checkIndication = array.getDrawable(R.styleable.BannerView_dotIndicationCheck);
        nokIndication = array.getDrawable(R.styleable.BannerView_dotIndicationNo);
        if (checkIndication == null || nokIndication == null) {
            checkIndication = new ColorDrawable(Color.RED);
            nokIndication = new ColorDrawable(Color.BLUE);
        }

        array.recycle();

    }

    /**
     * 初始圆点指示器
     */
    private void initDotIndicator() {
        if (adapter == null) {
            return;
        }
        int count = adapter.getContent();
        bannerContainer.removeAllViews();
        for (int i = 0; i < count; i++) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dip2px(mDosize), dip2px(mDosize));
            params.leftMargin = params.rightMargin = dip2px(2);
            DotIndicatorView dotIndicatorView = new DotIndicatorView(mContent);
            dotIndicatorView.setLayoutParams(params);
            dotIndicatorView.setDrawable(nokIndication);
            mCurrentPosition = 0;
            if (i == 0) {
                dotIndicatorView.setDrawable(checkIndication);

            } else {
                dotIndicatorView.setDrawable(nokIndication);


            }
            bannerContainer.addView(dotIndicatorView);

        }


    }

    private void initView() {
        viewPage = ((BannerViewPage) findViewById(R.id.banner_page));

        bannerContainer = ((LinearLayout) findViewById(R.id.dot_container));
        initViewPagerScroll();
        bannerContainer.setGravity(Gravity.CENTER);
        viewPage.setOffscreenPageLimit(1);
    }


    public void setAdapter(final List<String> imageList) {
        //  设置直接加载一张imageview的banner
        if (imageList == null || imageList.size() == 0) {
            view.setVisibility(GONE);
            return;
        }
        adapter = new BannerAdapter() {
            @Override
            public View getView(int position, View convertView) {

                View v = LayoutInflater.from(mContent).inflate(R.layout.pager_item, null, false);
                 textureView = v.findViewById(R.id.vvv_view);

                textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture1, int i, int i1) {
                        Surface face = new Surface(surfaceTexture1);
                        try {

                                mediaPlayer[position%2].reset();
                                mediaPlayer[position%2].setDataSource(imageList.get(0));
                                mediaPlayer[position%2].setSurface(face);
                                mediaPlayer[position%2].setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer[position%2].prepare();
                                mediaPlayer[position%2].setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start();

                                    }
                                });

                        } catch (IllegalArgumentException | IOException e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

                    }
                });
                return v;
            }

            @Override
            public int getContent() {
                return imageList.size();
            }

            @Override
            public int test(int index) {
                Log.e(TAG,"TEST:"+index+"===="+mediaPlayer[index%2].isPlaying());
                mediaPlayer[index%2].seekTo(0);
                mediaPlayer[index%2].start();
                return 0;
            }
        };
        viewPage.setAdapter(adapter);
        viewPage.clearOnPageChangeListeners();
        viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                    pageSelect(position);
                    adapter.test(mCurrentPosition);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (isAccording) {
            initDotIndicator();
        }


    }


    /**
     * 设置选中的状态
     */
    private void pageSelect(int position) {
        DotIndicatorView oldIndicatorView = (DotIndicatorView)
                bannerContainer.getChildAt(mCurrentPosition);
        oldIndicatorView.setDrawable(nokIndication);
        Log.d(this.getClass().getSimpleName(), "oldIndicatorView: " + mCurrentPosition);
        mCurrentPosition = position % adapter.getContent();
        DotIndicatorView currentIndicatorView = (DotIndicatorView)
                bannerContainer.getChildAt(mCurrentPosition);
        Log.d(this.getClass().getSimpleName(), "currentIndicatorView: " + mCurrentPosition);
        currentIndicatorView.setDrawable(checkIndication);


    }

    /**
     * 设置切换两个页的切换效率时间
     */

    public BannerView setRateTime(int duration) {
        BannerConfig.DEFAULT_DURATION = duration;
        initViewPagerScroll();
        return this;
    }


    /**
     * 设置是否显示指示器
     */
    public BannerView setIsAccording(boolean isAccording) {
        this.isAccording = isAccording;
        return this;
    }


    /**
     * 设置时间
     * *
     */

    public BannerView setDelayTime(int delayTime) {
        if (viewPage != null) {
            viewPage.setDelayTime(delayTime);
        }
        return this;

    }

    public BannerView setOnClickList(OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 设置指示器选中和没选中颜色（指示器为默认圆形）
     */
    public BannerView setIndicationColor(String check, String noCheck) {
        if (isColor(check) && isColor(noCheck)) {
            checkIndication = new ColorDrawable(Color.parseColor(check));
            nokIndication = new ColorDrawable(Color.parseColor(noCheck));
            initDotIndicator();

        }
        return this;

    }

    /**
     * 开始播放
     */
    public BannerView setStartbanner() {
        BannerConfig.mShuffling = true;
        viewPage.setStartbanner();
        return this;
    }


    /**
     * 圆点大小
     */
    public BannerView setmDosize(int mDosize) {
        this.mDosize = mDosize;
        return this;

    }

    /**
     * 设置viewpage切切换时间
     */
    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            BannerScroller mScroller = new BannerScroller(viewPage.getContext(), new AccelerateInterpolator());
            mField.set(viewPage, mScroller);
        } catch (Exception e) {
            Log.e("viewpage切换速率", e.getMessage());
        }
    }


    /**
     * 5.把dip转成px
     */
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip, getResources().getDisplayMetrics());
    }


    private boolean isColor(String string) {
        if (string != null && string.matches("#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})")) {
            return true;

        } else {
            return false;
        }
    }
}
