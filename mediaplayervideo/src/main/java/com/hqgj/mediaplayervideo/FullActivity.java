package com.hqgj.mediaplayervideo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.hqgj.mediaplayervideo.singletons.MediaPlayerUtil;
import com.hqgj.mediaplayervideo.widget.MediaVideoPlayer;

public class FullActivity extends Activity {
    private MediaVideoPlayer player;
    private String url="http://7xrpiy.com1.z0.glb.clouddn.com/video7724E883-18AC-4104-B709-3839CD6E80C9.png";
    private String videoUrl="http://7xrpiy.com1.z0.glb.clouddn.com/video%2F1.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full);

        player= (MediaVideoPlayer) findViewById(R.id.player);



        player.loadAndPlay(MediaPlayerUtil.getMediaPlayer(),videoUrl,0,true,"haha");



        player.setVideoPlayerCallback(new MediaVideoPlayer.VideoPlayerCallback() {
            @Override
            public void onSwitchFull() {
                finish();
            }

            @Override
            public void onPlay() {

            }

            @Override
            public void onCollection() {

            }
        });
    }



    @Override
    protected void onPause() {
        player.pausePlay(true);
        super.onPause();
    }
}
