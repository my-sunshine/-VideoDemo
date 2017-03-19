package com.hqgj.mediaplayervideo.widget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hqgj.mediaplayervideo.R;
import com.hqgj.mediaplayervideo.singletons.MediaPlayerUtil;
import com.hqgj.mediaplayervideo.singletons.SurfaceViewCallbackInf;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ly on 2017/3/15.
 */

public class MediaVideoPlayer extends RelativeLayout implements View.OnTouchListener,MediaPlayer.OnPreparedListener {

    private Context context;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;

    //是否是全屏
    private boolean isFull;
    //视频是否启动了
    private boolean isVideoStated;
    private boolean isVideoPrepared;
    //视频的封面图、圆形进度条的容器
    private View videoProgressbarContainer;
    //视频的封面图
    private ImageView videoImg;
    //正在缓存加载视频时，圆形进度条
    private ProgressBar videoProgressBar;
    //音量
    private AudioManager audioManager;
    //当前音量
    private int currentVolume;
    //最大音量
    private int maxVolume;
    //系统的滑动常量来，判断此时是否属于滑动事件
    private int slop;
    //屏幕的方向
    private int rotation;
    //MediaVideoController
    private MediaVideoController controller;

    //屏幕的宽高
    private int screenHeight;
    private int screenWidth;
    //用于对亮度调节
    private WindowManager.LayoutParams lightParams;
    //当前屏幕的亮度，最大255
    private int currentLight=0;

    public MediaVideoPlayer(Context context) {
        super(context);
        this.context=context;
        isVideoStated=false;
        isFull=false;
        initView();
    }

    public MediaVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        isVideoStated=false;
        isFull=false;
        initView();
    }

    private void initView() {
        if(isInEditMode()){
            return;
        }
        // todo 得到一些常量
        slop= ViewConfiguration.get(context).getScaledTouchSlop();
        screenWidth=getResources().getDisplayMetrics().heightPixels;
        screenHeight=getResources().getDisplayMetrics().widthPixels/20;
        rotation=((Activity)context).getWindowManager().getDefaultDisplay().getRotation();

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        lightParams=((Activity)context).getWindow().getAttributes();

        try {
            currentLight=Math.round(Settings.System.getInt(((Activity)context).getContentResolver(),Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        // todo 初始化一些布局
        View.inflate(context, R.layout.layout_media_player,this);
        surfaceView= (SurfaceView) findViewById(R.id.surfaceView);

        videoProgressbarContainer=findViewById(R.id.video_progressbar_container);
        videoImg= (ImageView) findViewById(R.id.video_img);
        videoProgressBar= (ProgressBar) findViewById(R.id.video_progress_bar);

        controller= (MediaVideoController) findViewById(R.id.mediaVideoController);
        controller.setController(new MediaPlayerController());
        surfaceView.setOnTouchListener(this);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 拦截
                return true;
            }
        });

        initMediaPlayerController(false);
    }

    private void initMediaPlayerController(boolean isProgressBarVisible) {
        surfaceView.setVisibility(VISIBLE);
        videoProgressbarContainer.setVisibility(VISIBLE);
        videoImg.setVisibility(VISIBLE);
        if(isProgressBarVisible){
            videoProgressBar.setVisibility(VISIBLE);
        }else {
            videoProgressBar.setVisibility(GONE);
        }

    }


    private void play(String videoUrl) {
        try {
            controller.setVideoPlayVisible(false);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();
            // todo  设置在视频播放的时候是否使用 SurfaceHolder 保持屏幕亮起;
            mediaPlayer.setScreenOnWhilePlaying(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int startX;
    private int startY;
    private long currentTime;
    //是否是再拖动屏幕的时候，暂停了视频
    private boolean isPlayWhenDrag=false;
    //视频的总时间
    private long totalTime;
    //每单位的宽度，对应的视频的时长
    private int videoProgressUnit;
    private int type=0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int currentX= (int) event.getX();
        int currentY= (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=currentX;
                startY=currentY;
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                currentTime=MediaPlayerUtil.getMediaPlayerCurrentPosition();
                type=0;
                break;
            case MotionEvent.ACTION_MOVE:
                if(isFull){
                    int deX=currentX-startX;
                    int deY=currentY-startY;
                    //改变声音和亮度
                    if(Math.abs(deY)>slop && Math.abs(deY)>Math.abs(deX)){
                        if(type==0){
                            type=1;
                        }
                        if(type!=1){
                            return true;
                        }

                        if(startX<=screenWidth/2){
                            int nowVolume=0;
                            if(rotation== Surface.ROTATION_90){
                                nowVolume=currentVolume-deY/screenHeight;
                            }else {
                                nowVolume=currentVolume+deY/screenHeight;
                            }
                            //int now=currentVolume-deY/screenHeight;
                            Log.e("ACTION_MOVE","rotation"+rotation+",deX:"+deX+",deY:"+deY+",currentVolume"+currentVolume+",screenHeight"+screenHeight+",now"+nowVolume+",screenWidth"+screenWidth+",startX"+startX);
                            if(nowVolume>=maxVolume){
                                nowVolume=maxVolume;
                            } else if(nowVolume<=0){
                                nowVolume=0;
                            }
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nowVolume, 0);
                            controller.updateVolumeProgress(nowVolume);
                        }else {

                            int nowLight=0;
                            if(rotation== Surface.ROTATION_90){
                                nowLight=currentLight-deY*40/screenHeight;
                            }else {
                                nowLight=currentLight+deY*40/screenHeight;
                            }
                            if(nowLight>=255){
                                nowLight=255;
                            }else if(nowLight<0){
                                nowLight=0;
                            }
                            Log.e("ACTION_MOVE","rotation"+rotation+",deX:"+deX+",deY:"+deY+",currentLight:"+currentLight+",screenHeight"+screenHeight
                                    +",nowLight"+nowLight+",screenWidth"+screenWidth+",startX"+startX);

                            lightParams.screenBrightness=nowLight/255.0f;
                            ((Activity)context).getWindow().setAttributes(lightParams);
                            controller.updateLightProgress(nowLight);
                        }
                    }
                    //改变进度,改变进度的时候，视频暂停，
                    else if(Math.abs(deX)>slop && Math.abs(deY)<Math.abs(deX)){
                        if(type==0){
                            type=2;
                        }
                        if(type!=2){
                            return true;
                        }
                        //再进行拖动进度之前，视频是否暂停：用来决定，拖动事件结束后，是否暂停
                        if(MediaPlayerUtil.isMediaPlayerPlaying()){
                            isPlayWhenDrag=true;
                            pausePlay();
                        }
                        if(rotation== Surface.ROTATION_90){
                            mediaPlayer.seekTo((int) (currentTime+deX*videoProgressUnit));
                        }else {
                            mediaPlayer.seekTo((int) (currentTime-deX*videoProgressUnit));
                        }
                        controller.setSkipTextView(currentTime+deX*videoProgressUnit,totalTime);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                type=0;
                //todo 在 非拖拽，并且video准备好之后，显示或隐藏主Controller
                if(Math.abs(currentX-startX)<slop && Math.abs(currentY-startY)<slop && isVideoPrepared){
                    controller.showOrHideController(isFull);
                }
                //todo 隐藏次Controller
                controller.setSubControllerHide();
                if(isPlayWhenDrag){
                    startPlay();
                    isPlayWhenDrag=false;
                }
                //重置当前屏幕的亮度
                currentLight= (int) (lightParams.screenBrightness*255);
                break;
        }
        return true;
    }


    /**
     * 视频准备好回调
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("MediaVideoPlayer","onPrepared:"+context);
        onMediaPrepared();
    }

    private void onMediaPrepared(){
        mediaPlayer.setDisplay(surfaceView.getHolder());
        startPlay(false);
        surfaceView.requestLayout();
        surfaceView.invalidate();
        totalTime=MediaPlayerUtil.getMediaPlayerDuration();
        videoProgressUnit= (int) (totalTime*2/screenWidth);
        isVideoPrepared=true;
        videoImg.setVisibility(GONE);
        videoProgressBar.setVisibility(GONE);
        // todo 如果再play方法中调用，因为play方法只会调用一次：在第一个页面的时候调用，这时，OnCompletion会绑定第一个activity
        mediaPlayer.setOnCompletionListener(new MediaPlayerCompletion());
    }


    /**
     * 视频播放完成回调
     */
    class MediaPlayerCompletion implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.e("MediaVideoPlayer","onCompletion:"+context);
            mp.seekTo(100);
            controller.setPlayTime(100,totalTime,false,0);
            //MediaPlayerUtil.seekToMediaPlayer(100);
            pausePlay(true);
        }
    }


    /**
     * 当用户不可见的时候 ，SurfaceHolder 就会销毁，当再次可见的时候，会创建新的SurfaceHolder；
     * 可以使用SurfaceHolder.Callback()维护一个SurfaceHolder
     */
    class SurfaceViewCallback extends SurfaceViewCallbackInf {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("MediaVideoPlayer","surfaceCreated:"+context);
            Log.e("MediaVideoPlayer","surfaceCreated");
            if(mediaPlayer!=null && !mediaPlayer.isPlaying()){
                onMediaPrepared();
            }
        }
    }

    class MediaPlayerController implements MediaVideoController.MediaController{

        @Override
        public void onPlayStateChange() {
            //如果视频没有启动过，调用onPlay启动视频
            if(!isVideoStated){
                isVideoStated=true;
                callback.onPlay();
                return;
            }
            // 如果正在播放，则暂停；否则进行播放
            if(MediaPlayerUtil.isMediaPlayerPlaying()){
                pausePlay(true);
            }else {
                startPlay(true);
            }
        }

        @Override
        public void onCollectionStateChange() {
            Toast.makeText(context,"onCollectionStateChange",Toast.LENGTH_LONG).show();
            callback.onCollection();
        }

        @Override
        public void onProgressChange(boolean fromUser, int progress) {
            int currentTime= (int) (progress*totalTime/100);
            if(fromUser){
                MediaPlayerUtil.seekToMediaPlayer(currentTime);
            }else {
                currentTime=(int) MediaPlayerUtil.getMediaPlayerCurrentPosition();
                progress= (int) (currentTime*100/totalTime);
            }
            //更新播放进度
            controller.setPlayTime(currentTime,totalTime,fromUser,progress);
        }

        @Override
        public void onFullStateChange() {
            callback.onSwitchFull();
        }
    }


    /**
     * todo public
     * 设置图片
     */
    public void loadVideoImg(String imgUrl){
        initMediaPlayerController(false);
        Glide.with(context).load(imgUrl).into(videoImg);
    }

    /**
     * todo public
     * 加载视频
     */
    public void loadAndPlay(MediaPlayer mediaPlayer,String videoUrl,int seekTime,boolean isFull,String title){
        Log.e("MediaVideoPlayer","loadAndPlay:"+context);
        initMediaPlayerController(true);
        if(seekTime!=0){
            videoImg.setVisibility(GONE);
        }
        this.mediaPlayer=mediaPlayer;
        this.isFull=isFull;
        controller.setVideoFillScaleStatue(isFull);
        //  保持屏幕一直亮着，但是不好使？
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceViewCallback());

        if(!isFull){
            play(videoUrl);
        }
        controller.setVideoTitle(title);
    }

    /**
     * 全屏
     */
    public void loadAndPlay(String title){
        loadAndPlay(MediaPlayerUtil.getMediaPlayer(),"",0,true,title);
    }

    /**
     * 小屏
     */
    public void loadAndPlay(MediaPlayer mediaPlayer,String videoUrl){
        loadAndPlay(mediaPlayer,videoUrl,0,false,"");
    }


    /**
     * todo public
     * 销毁mediaPlayer，释放资源
     */
    public void destoryPlay(){
        controller.onDestory();
        MediaPlayerUtil.destoryMediaPlayer();
    }

    /**
     * todo public
     * 暂停 视频
     */
    public void pausePlay(boolean isVisible){
        Log.e("MediaVideoPlayer","pausePlay"+isVisible+context);
        pausePlay();
        controller.setVideoPlayVisible(isVisible);
    }

    private void pausePlay(){
        controller.stopTimer();
        controller.setVideoPlayStatue(false);
        //暂停播放的时候，停止计时器
        MediaPlayerUtil.pauseMediaPlayer();
    }

    /**
     * todo public
     * 开始播放 视频
     */
    public void startPlay(boolean isVisible){
        startPlay();
        controller.setVideoPlayVisible(isVisible);
    }

    private void startPlay(){
        MediaPlayerUtil.playMediaPlayer();
        controller.setVideoPlayStatue(true);
        //开始播放的时候，启动计时器
        controller.setTimeTask();
    }

    //供activity回调
    private VideoPlayerCallback callback;

    public void setVideoPlayerCallback(VideoPlayerCallback callback){
        this.callback=callback;
    }

    /**
     * todo
     *
     * 调用这个播放器的activity/fragment实现
     */
    public interface VideoPlayerCallback{
        //在改变 全屏/非全屏 状态时调用
        void onSwitchFull();
        //播放/暂停
        void onPlay();
        //收藏/取消收藏
        void onCollection();
    }
}
