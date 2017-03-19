package com.hqgj.mediaplayervideo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hqgj.mediaplayervideo.singletons.MediaPlayerUtil;
import com.hqgj.mediaplayervideo.widget.MediaVideoPlayer;

public class MainActivity extends AppCompatActivity {

    private MediaVideoPlayer player;
    private String url="http://7xrpiy.com1.z0.glb.clouddn.com/video7724E883-18AC-4104-B709-3839CD6E80C9.png";
    private String videoUrl="http://7xrpiy.com1.z0.glb.clouddn.com/video%2F1.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player= (MediaVideoPlayer) findViewById(R.id.player);

        player.loadVideoImg(url);

        player.loadAndPlay(MediaPlayerUtil.getMediaPlayer(),videoUrl,0,false,"21");



        player.setVideoPlayerCallback(new MediaVideoPlayer.VideoPlayerCallback() {
            @Override
            public void onSwitchFull() {

                Intent intent=new Intent(MainActivity.this,FullActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPlay() {



            }

            @Override
            public void onCollection() {

                //player.
            }
        });
    }


    @Override
    protected void onPause() {
        player.pausePlay(true);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.destoryPlay();
        super.onDestroy();
    }
}
