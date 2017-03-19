package com.hqgj.myvideo.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hqgj.myvideo.R;
import com.hqgj.myvideo.utils.DensityUtils;
import com.hqgj.myvideo.utils.NetUtils;
import com.hqgj.myvideo.utils.TimeUtil;

import java.io.IOException;

/**
 * author: ly
 * data: 2016/6/27
 */
public class VideoRelativeLayout extends RelativeLayout implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private Context context;
    private View titleContainer;
    private View bottomContainer;
    private View volumeContainer;
    private ImageView pauseImageView;
    private ProgressBar progressBar;
    private ImageView back;
    private ImageView collection;
    private TextView title;

    private SeekBar seekBar;

    private TextView playTime;
    private TextView totalTime;

    private String videoPath;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    private boolean isPrepared;
    private final int CHANGE_PLAY_TIME = 1;
    private final int HIDDEN = 2;
    private int GESTURE_FLAG = 4;
    private final int GESTURE_MODIFY_PROGRESS = 4;
    private final int GESTURE_MODIFY_VOLUME = 5;
    private final int STEP_PROGRESS = 2;

    //是否正在播放
    private boolean isPlaying;
    //title、bottom是否可见
    private boolean isVisible;
    //是否发生了滚动
    private boolean isScroll;

    private int currentPosition;


    private Animation showAnimation;
    private Animation hiddenAnimation;

    private GestureDetector gestureDetector;

    private AudioManager audioManager;

    private int currentVolume;
    private int maxVolume;

    private ProgressBar volumeProgressBar;
    private ImageView volumeAddImageView;
    private ImageView volumeMinusImageView;


    public VideoRelativeLayout(Context context) {
        this(context, null, 0);
    }

    public VideoRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if(isInEditMode()){
            return;
        }
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        initView();
    }

    private void initView() {
        //添加surfaceView
        RelativeLayout.LayoutParams surfaceViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView = new SurfaceView(context);
        this.addView(surfaceView, surfaceViewLayoutParams);

        //添加暂停键
        RelativeLayout.LayoutParams LayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        pauseImageView = new ImageView(context);
        pauseImageView.setImageResource(R.drawable.ic_pause);
        pauseImageView.setPadding(DensityUtils.dp2px(context, 18), DensityUtils.dp2px(context, 18), DensityUtils.dp2px(context, 18), DensityUtils.dp2px(context, 18));
        this.addView(pauseImageView, LayoutParams);
        progressBar = new ProgressBar(context);
        this.addView(progressBar, LayoutParams);

        //添加title
        titleContainer = LayoutInflater.from(context).inflate(R.layout.layout_title, null);
        titleContainer.setPadding(DensityUtils.dp2px(context, 10), 0, DensityUtils.dp2px(context, 10), 0);
        RelativeLayout.LayoutParams titleLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(context, 48));
        titleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        this.addView(titleContainer, titleLayoutParams);

        //添加bottom
        bottomContainer = LayoutInflater.from(context).inflate(R.layout.layout_bottom, null);
        bottomContainer.setPadding(DensityUtils.dp2px(context, 10), 0, DensityUtils.dp2px(context, 10), 0);
        RelativeLayout.LayoutParams bottomLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(context, 48));
        bottomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        this.addView(bottomContainer, bottomLayoutParams);

        //添加音量
        volumeContainer = LayoutInflater.from(context).inflate(R.layout.layout_volume, null);
        RelativeLayout.LayoutParams volumeLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        volumeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        volumeLayoutParams.leftMargin = DensityUtils.dp2px(context, 20);
        this.addView(volumeContainer, volumeLayoutParams);

        volumeMinusImageView = (ImageView) volumeContainer.findViewById(R.id.volumeMinusImageView);
        volumeAddImageView = (ImageView) volumeContainer.findViewById(R.id.volumeAddImageView);
        volumeProgressBar = (ProgressBar) volumeContainer.findViewById(R.id.volumeProgressBar);
        volumeProgressBar.setMax(maxVolume);
        volumeProgressBar.setProgress(currentVolume);
        back = (ImageView) titleContainer.findViewById(R.id.back);
        collection = (ImageView) titleContainer.findViewById(R.id.collection);
        title = (TextView) titleContainer.findViewById(R.id.title);
        playTime = (TextView) bottomContainer.findViewById(R.id.playTime);
        totalTime = (TextView) bottomContainer.findViewById(R.id.totalTime);
        seekBar = (SeekBar) bottomContainer.findViewById(R.id.seekBar);

        pauseImageView.setVisibility(View.GONE);
        titleContainer.setVisibility(View.GONE);
        bottomContainer.setVisibility(View.GONE);
        volumeContainer.setVisibility(View.GONE);

        initEvent();

        showAnimation = new AlphaAnimation(0, 1f);
        showAnimation.setDuration(500);

        hiddenAnimation = new AlphaAnimation(1f, 0);
        hiddenAnimation.setDuration(500);

        hiddenAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                titleContainer.setVisibility(View.GONE);
                bottomContainer.setVisibility(View.GONE);
                pauseImageView.setVisibility(View.GONE);

                if (volumeContainer.getVisibility() == View.VISIBLE) {
                    volumeContainer.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mediaPlayer == null || !isPrepared) {
                    return false;
                }
                isScroll = true;
                Log.i("actionX", "surfaceView---onScroll:" + distanceX + "--" + distanceY);
                if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                    GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;

                    handler.removeCallbacks(hiddenRunnable);

                    currentPosition = mediaPlayer.getCurrentPosition();

                    mediaPlayer.pause();

                    pauseImageView.setImageResource(R.drawable.ic_pause);
                    //后退
                    if (distanceX > DensityUtils.dp2px(context, STEP_PROGRESS)) {
                        currentPosition = currentPosition - 500;
                    }
                    //前进
                    else if (distanceX < -DensityUtils.dp2px(context, STEP_PROGRESS)) {
                        currentPosition = currentPosition + 500;
                    }
                    if (currentPosition < 0) {
                        currentPosition = 0;
                    } else if (currentPosition > mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getDuration();
                    }
                    mediaPlayer.seekTo(currentPosition);
                    playTime.setText(TimeUtil.formatTime(currentPosition));
                    seekBar.setProgress(currentPosition);

                } else {

                    GESTURE_FLAG = GESTURE_MODIFY_VOLUME;

                    if (volumeContainer.getVisibility() == View.GONE) {
                        volumeContainer.setVisibility(View.VISIBLE);
                        volumeContainer.setAnimation(showAnimation);
                        showAnimation.start();
                    }
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                    if (distanceY > DensityUtils.dp2px(context, STEP_PROGRESS)) {
                        if (currentVolume < maxVolume) {
                            currentVolume = currentVolume + 1;
                        }
                    } else if (distanceY < -DensityUtils.dp2px(context, STEP_PROGRESS)) {
                        if (currentVolume > 0) {
                            currentVolume = currentVolume - 1;
                        }
                    }
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    volumeProgressBar.setProgress(currentVolume);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

    }

    private void initEvent() {
        surfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("actionX", "surfaceView---ACTION_DOWN");
                        if (isPrepared && mediaPlayer != null) {
                            handler.removeCallbacks(hiddenRunnable);

                            volumeContainer.clearAnimation();

                            isPlaying = mediaPlayer.isPlaying();
                            isScroll = false;
                            if (mediaPlayer.isPlaying()) {
                                pauseImageView.setImageResource(R.drawable.ic_play);
                            } else {
                                pauseImageView.setImageResource(R.drawable.ic_pause);
                            }
                            if (titleContainer.getVisibility() == View.VISIBLE && bottomContainer.getVisibility() == View.VISIBLE) {
                                isVisible = true;
                            } else {
                                isVisible = false;
                                pauseImageView.setVisibility(View.VISIBLE);
                                titleContainer.setVisibility(View.VISIBLE);
                                bottomContainer.setVisibility(View.VISIBLE);

                                bottomContainer.setAnimation(showAnimation);
                                titleContainer.setAnimation(showAnimation);
                                pauseImageView.setAnimation(showAnimation);

                                showAnimation.start();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (mediaPlayer == null || !isPrepared) {
                            return false;
                        }
                        if (isPlaying) {
                            mediaPlayer.start();
                            pauseImageView.setImageResource(R.drawable.ic_play);
                            if (isVisible && !isScroll) {
                                handler.post(hiddenRunnable);
                            } else {
                                handler.postDelayed(hiddenRunnable, 3000);
                            }
                        }
                        Log.i("actionX", "surfaceView---ACTION_UP");
                        break;
                }

                gestureDetector.onTouchEvent(event);

                return true;
            }
        });

        volumeContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("action", "volumeContainer---ACTION_DOWN");
                        handler.removeCallbacks(hiddenRunnable);
                        handler.postDelayed(hiddenRunnable, 5000);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("action", "volumeContainer---ACTION_UP");
                        break;
                }

                return true;
            }
        });
        titleContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("action", "titleContainer---ACTION_DOWN");
                        handler.removeCallbacks(hiddenRunnable);
                        handler.postDelayed(hiddenRunnable, 5000);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("action", "titleContainer---ACTION_UP");
                        break;
                }

                return true;
            }
        });

        bottomContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("action", "bottomContainer---ACTION_DOWN");
                        handler.removeCallbacks(hiddenRunnable);
                        handler.postDelayed(hiddenRunnable, 5000);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("action", "bottomContainer---ACTION_UP");
                        break;
                }
                return true;
            }
        });


        seekBar.setOnSeekBarChangeListener(this);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideoListener.onClickBack();
            }
        });
        collection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickVideoListener.onClickCollection();

            }
        });
        pauseImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("action", "onClick");
                if (mediaPlayer != null && isPrepared) {
                    if (mediaPlayer.isPlaying()) {
                        pauseImageView.setAnimation(showAnimation);
                        showAnimation.start();
                        pauseImageView.setImageResource(R.drawable.ic_pause);
                        pauseImageView.setVisibility(View.VISIBLE);
                        mediaPlayer.pause();

                    } else {
                        bottomContainer.setAnimation(hiddenAnimation);
                        titleContainer.setAnimation(hiddenAnimation);
                        pauseImageView.setAnimation(hiddenAnimation);
                        hiddenAnimation.start();
                        pauseImageView.setImageResource(R.drawable.ic_play);
                        mediaPlayer.start();

                    }
                }
            }
        });

        volumeAddImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null || !isPrepared) {
                    return;
                }
                handler.removeCallbacks(hiddenRunnable);
                handler.postDelayed(hiddenRunnable, 3000);
                if (currentVolume < maxVolume) {
                    currentVolume = currentVolume + 1;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    volumeProgressBar.setProgress(currentVolume);
                }

            }
        });
        volumeMinusImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null || !isPrepared) {
                    return;
                }
                handler.removeCallbacks(hiddenRunnable);
                handler.postDelayed(hiddenRunnable, 3000);
                if (currentVolume > 0) {
                    currentVolume = currentVolume - 1;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    volumeProgressBar.setProgress(currentVolume);
                }
            }
        });

    }


    public void setVideoPath(String path, String titleStr) {
        this.videoPath = path;
        title.setText(titleStr);
        if (!NetUtils.isConnected(context)) {
            Toast.makeText(context, "网络未连接！", Toast.LENGTH_LONG).show();
            return;
        }
        initVideo();
    }


    private void initVideo() {

        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(new MyCallback());

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser && isPrepared) {
            mediaPlayer.seekTo(progress);
            playTime.setText("" + TimeUtil.formatTime(progress));
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(hiddenRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.postDelayed(hiddenRunnable, 5000);
    }

    // todo 网络流媒体的缓冲监听
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("onBufferingUpdate",""+percent);
    }

    class MyCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            Log.i("action", "surfaceCreated:" + currentPosition);

            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(videoPath);

                mediaPlayer.setLooping(false);

                mediaPlayer.prepareAsync();

                mediaPlayer.setDisplay(surfaceHolder);

                mediaPlayer.setScreenOnWhilePlaying(true);

                surfaceHolder.setKeepScreenOn(true);

                mediaPlayer.setOnPreparedListener(VideoRelativeLayout.this);

                mediaPlayer.setOnErrorListener(VideoRelativeLayout.this);

                mediaPlayer.setOnCompletionListener(VideoRelativeLayout.this);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (mediaPlayer != null && isPrepared) {

                currentPosition = mediaPlayer.getCurrentPosition();

                isPrepared = false;

                mediaPlayer.release();

                mediaPlayer = null;

                handler.removeCallbacksAndMessages(null);
            }

            Log.i("action", "surfaceDestroyed:" + currentPosition);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        Toast.makeText(context,"onPrepared",Toast.LENGTH_LONG).show();

        seekBar.setMax(mp.getDuration());

        isPrepared = true;

        pauseImageView.setVisibility(View.GONE);

        pauseImageView.setImageResource(R.drawable.ic_play);

        progressBar.setVisibility(View.GONE);

        mediaPlayer.seekTo(currentPosition);

        mediaPlayer.start();

        totalTime.setText("" + TimeUtil.formatTime(mp.getDuration()));

        handler.post(runnable);

        mediaPlayer.setOnBufferingUpdateListener(this);

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        isPrepared = false;

        mediaPlayer.release();

        mediaPlayer = null;

        pauseImageView.setImageResource(R.drawable.ic_pause);

        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        mediaPlayer.seekTo(0);

        pauseImageView.setImageResource(R.drawable.ic_pause);

        pauseImageView.setVisibility(View.VISIBLE);

        titleContainer.setVisibility(View.VISIBLE);

        bottomContainer.setVisibility(View.VISIBLE);

        bottomContainer.setAnimation(showAnimation);

        titleContainer.setAnimation(showAnimation);

        pauseImageView.setAnimation(showAnimation);

        showAnimation.start();

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handler.sendEmptyMessage(CHANGE_PLAY_TIME);

            handler.postDelayed(this, 100);

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer != null && isPrepared) {
                switch (msg.what) {
                    case CHANGE_PLAY_TIME:
                        if (mediaPlayer.isPlaying()) {
                            playTime.setText("" + TimeUtil.formatTime(mediaPlayer.getCurrentPosition()));
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                        break;

                    case HIDDEN:
                        bottomContainer.setAnimation(hiddenAnimation);
                        titleContainer.setAnimation(hiddenAnimation);
                        pauseImageView.setAnimation(hiddenAnimation);
                        if (volumeContainer.getVisibility() == View.VISIBLE) {
                            volumeContainer.setAnimation(hiddenAnimation);
                        }
                        hiddenAnimation.start();
                        break;
                }
            }
        }
    };


    private Runnable hiddenRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(HIDDEN);
        }
    };


    private OnClickVideoListener onClickVideoListener;

    public void setOnClickVideoListener(OnClickVideoListener onClickVideoListener) {
        this.onClickVideoListener = onClickVideoListener;
    }


    public interface OnClickVideoListener {
        void onClickBack();

        void onClickCollection();
    }


    public void onPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pauseImageView.setImageResource(R.drawable.ic_pause);
            }
            pauseImageView.setVisibility(View.VISIBLE);
            titleContainer.setVisibility(View.VISIBLE);
            bottomContainer.setVisibility(View.VISIBLE);
            pauseImageView.clearAnimation();
            titleContainer.clearAnimation();
            bottomContainer.clearAnimation();
        }
    }

    public void setIsCollection(boolean isCollection){
        if(isCollection){
            collection.setImageResource(R.drawable.ic_collection_press);
        }else {
            collection.setImageResource(R.drawable.ic_collection);
        }
    }


}
