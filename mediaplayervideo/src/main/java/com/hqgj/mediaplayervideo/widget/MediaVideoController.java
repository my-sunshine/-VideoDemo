package com.hqgj.mediaplayervideo.widget;

import android.content.Context;
import android.icu.util.MeasureUnit;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hqgj.mediaplayervideo.R;
import com.hqgj.mediaplayervideo.singletons.MediaPlayerUtil;

/**
 * Created by ly on 2017/3/16.
 */

public class MediaVideoController extends FrameLayout implements SeekBar.OnSeekBarChangeListener,View.OnClickListener {


    private Context context;

    private ImageView videoBack;
    private ImageView videoCollection;
    private ImageView videoPlay;
    private ImageView videoFillScale;
    private SeekBar videoProgress;
    private ProgressBar volumeProgressbar;
    private ProgressBar lightProgressbar;

    private TextView videoPlayTime;
    private TextView videoTotalTime;
    private TextView videoTitle;

    private View topContainer;
    private View bottomContainer;
    private View volumeController;
    private View lightController;

    private TextView skipTextView;

    private Timer updateTimer;
    private TimerTask timerTask;

    private static final int UPDATE_PLAY_TIME=1;
    private static final int HIDE_CONTROLLER=2;

    private  AlphaAnimation hideAnim;


    private class AnimListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setBottomContainerVisible(false);
            setTopContainerVisible(false);
            if(MediaPlayerUtil.isMediaPlayerPlaying()){
                setVideoPlayVisible(false);
            }

            videoPlay.clearAnimation();
            bottomContainer.clearAnimation();
            topContainer.clearAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_PLAY_TIME){
                //计时器，非手动条件下，调整进度条和显示的时间
                controller.onProgressChange(false,0);
            }else if(msg.what==HIDE_CONTROLLER){
                hideController();
            }
        }
    };



    public MediaVideoController(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public MediaVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    private void initView() {
        View.inflate(context, R.layout.layout_media_controller,this);
        videoBack= (ImageView) findViewById(R.id.videoControllerFullBarBack);
        videoCollection= (ImageView) findViewById(R.id.videoControllerFullBarCollection);
        videoPlay= (ImageView) findViewById(R.id.videoControllerBarPlay);
        videoFillScale= (ImageView) findViewById(R.id.videoControllerBarFillScale);

        videoProgress= (SeekBar) findViewById(R.id.videoControllerProgress);
        volumeProgressbar= (ProgressBar) findViewById(R.id.volumeProgressbar);
        lightProgressbar= (ProgressBar) findViewById(R.id.lightProgressbar);

        lightProgressbar.setMax(255);

        videoPlayTime= (TextView) findViewById(R.id.videoControllerBarPlayTime);
        videoTotalTime= (TextView) findViewById(R.id.videoControllerBarTotalTime);
        skipTextView= (TextView) findViewById(R.id.skipTextView);
        videoTitle= (TextView) findViewById(R.id.videoTitle);

        topContainer=findViewById(R.id.videoControllerFullBar);
        bottomContainer=findViewById(R.id.videoControllerBar);
        volumeController=findViewById(R.id.volumeControllerBar);
        lightController=findViewById(R.id.lightControllerBar);
        initControllerStatue();
        initEvent();


        hideAnim=new AlphaAnimation(1,0);
        hideAnim.setAnimationListener(new AnimListener());
        hideAnim.setDuration(300);
    }

    /**
     * 设置Controller的初始状态
     */
    private void initControllerStatue() {
        setBottomContainerVisible(false);
        setTopContainerVisible(false);
        lightController.setVisibility(GONE);
        volumeController.setVisibility(GONE);
        skipTextView.setVisibility(GONE);
        //暂停 图标
        setVideoPlayStatue(false);
        setVideoPlayVisible(true);
    }

    private void initEvent() {
        videoProgress.setOnSeekBarChangeListener(this);
        videoBack.setOnClickListener(this);
        videoPlay.setOnClickListener(this);
        videoFillScale.setOnClickListener(this);
        videoCollection.setOnClickListener(this);

        topContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        bottomContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        volumeController.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        lightController.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    /** todo public
     * 更新 音量进度条
     */
    public void updateVolumeProgress(int currentVolume){
        setVolumeControllerVisible(true);
        volumeProgressbar.setProgress(currentVolume);
    }

    /** todo public
     * 更新 亮度进度条
     */
    public void updateLightProgress(int currentVolume){
        setLightControllerVisible(true);
        lightProgressbar.setProgress(currentVolume);
    }

    /** todo public
     *  非进度条拖动页面的时候，skip的action
     */
    public void setSkipTextView(long start,long total){
        if(start<=0){
            start=0;
        }
        setSkipTextViewVisible(true);
        skipTextView.setText(formatPlayTime(start)+"/"+formatPlayTime(total));
        setVideoProgress((int) (start*100/total));
    }

    /**
     *
     *
     */
    public void setVideoFillScaleStatue(boolean isFull){
        if(isFull){
            videoFillScale.setImageResource(R.drawable.jc_shrink);
        }else {
            videoFillScale.setImageResource(R.drawable.jc_enlarge);
        }
    }

    // todo 启动,动画
    public void setVideoPlayStatue(boolean isPlay){
        if(isPlay){
            videoPlay.setImageResource(R.drawable.jc_pause_normal);
        }else {
            videoPlay.setImageResource(R.drawable.jc_play_normal);
        }
    }


    /**
     * 隐藏或者显示Controller
     */
    public void showOrHideController(boolean isFull){
        if(bottomContainer.getVisibility()==VISIBLE || topContainer.getVisibility()==VISIBLE){
            if(MediaPlayerUtil.isMediaPlayerPlaying()){
                videoPlay.clearAnimation();
                videoPlay.setAnimation(hideAnim);
            }else {
                setVideoPlayVisible(true);
            }
            bottomContainer.clearAnimation();
            bottomContainer.clearAnimation();

            bottomContainer.setAnimation(hideAnim);
            topContainer.setAnimation(hideAnim);
            hideAnim.start();
        }else {
            setBottomContainerVisible(true);
            setVideoPlayVisible(true);
            if(isFull){
                setTopContainerVisible(true);
            }else {
                setTopContainerVisible(false);
            }

            resetHandler();

        }
    }


    //隐藏Controller，根据调节
    private void hideController() {
        if(bottomContainer.getVisibility()==VISIBLE || topContainer.getVisibility()==VISIBLE || videoPlay.getVisibility()==VISIBLE){
            if(MediaPlayerUtil.isMediaPlayerPlaying()){
                videoPlay.clearAnimation();
                videoPlay.setAnimation(hideAnim);
            }else {
                setVideoPlayVisible(true);
            }
            bottomContainer.clearAnimation();
            bottomContainer.clearAnimation();

            bottomContainer.setAnimation(hideAnim);
            topContainer.setAnimation(hideAnim);
            hideAnim.start();
        }
    }

    public void resetHandler() {
        handler.removeMessages(HIDE_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_CONTROLLER,5000);
    }

    public void setBottomContainerVisible(boolean visible){
        if(visible){
            bottomContainer.setVisibility(VISIBLE);
        }else {
            bottomContainer.setVisibility(GONE);
        }
    }

    public void setTopContainerVisible(boolean visible){
        if(visible){
            topContainer.setVisibility(VISIBLE);
        }else {
            topContainer.setVisibility(GONE);
        }
    }

    private void setVolumeControllerVisible(boolean visible){
        if(visible){
            volumeController.setVisibility(VISIBLE);
        }else {
            volumeController.setVisibility(GONE);
        }
    }

    private void setLightControllerVisible(boolean visible){
        if(visible){
            lightController.setVisibility(VISIBLE);
        }else {
            lightController.setVisibility(GONE);
        }
    }

    /**
     * 设置skip是否可见
     */
    private void setSkipTextViewVisible(boolean visible){
        if(visible){
            skipTextView.setVisibility(VISIBLE);
        }else {
            skipTextView.setVisibility(GONE);
        }
    }

    public void setVideoTitle(String title){
        videoTitle.setText(title);
    }


    public void setSubControllerHide(){
        setVolumeControllerVisible(false);
        setLightControllerVisible(false);
        setSkipTextViewVisible(false);
    }


    public void setVideoPlayVisible(boolean visible){
        if(visible){
            videoPlay.setVisibility(VISIBLE);
        }else {
            videoPlay.setVisibility(GONE);
        }
    }

    /**
     * todo key,public
     *
     * 设置播放进度, textview
     */
    public void setPlayTime(long currentTime,long duration,boolean fromUserTouch, int progress){
        String playTime=formatPlayTime(currentTime);
        String totalTime=formatPlayTime(duration);
        videoPlayTime.setText(playTime);
        videoTotalTime.setText(totalTime);
        if(!fromUserTouch){
            setVideoProgress(progress);
        }
    }

    private String formatPlayTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private void setVideoProgress(int progress){
        videoProgress.setProgress(progress);
    }



    //实时更新进度条、播放时间
    public void setTimeTask(){
        resetUpdateTimer();
    }


    private void resetUpdateTimer() {
        stopTimer();
        updateTimer=new Timer();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(UPDATE_PLAY_TIME);
            }
        };

        updateTimer.schedule(timerTask,0,200);
    }

    public void stopTimer() {
        handler.removeMessages(UPDATE_PLAY_TIME);
        if(updateTimer!=null){
            updateTimer.cancel();
            updateTimer=null;
        }
        if(timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        controller.onProgressChange(fromUser,progress);
        setVideoProgress(progress);
    }

    private boolean isPlayWhenDrag=false;
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(MediaPlayerUtil.isMediaPlayerPlaying()){
            isPlayWhenDrag=true;
            handler.removeMessages(HIDE_CONTROLLER);
            MediaPlayerUtil.pauseMediaPlayer();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (isPlayWhenDrag){
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER,5000);
            MediaPlayerUtil.playMediaPlayer();
            isPlayWhenDrag=false;
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.videoControllerFullBarBack:
                controller.onFullStateChange();
                break;
            case R.id.videoControllerFullBarCollection:
                controller.onCollectionStateChange();
                resetHandler();
                break;
            case R.id.videoControllerBarPlay:
                controller.onPlayStateChange();
                resetHandler();
                break;
            case R.id.videoControllerBarFillScale:
                controller.onFullStateChange();
                break;
        }

    }


    private MediaController controller;

    public void setController(MediaController controller) {
        this.controller = controller;
    }

    public interface MediaController{
        // todo 点击按钮，播放或暂停视频
        void onPlayStateChange();
        void onCollectionStateChange();
        void onProgressChange( boolean fromUser,int progress);
        void onFullStateChange();
    }

    public void onDestory(){
        stopTimer();
        handler.removeCallbacksAndMessages(null);
    }

}
