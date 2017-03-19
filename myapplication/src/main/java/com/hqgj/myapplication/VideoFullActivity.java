package com.hqgj.myapplication;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.hqgj.myapplication.util.MediaPlayerUtil;
import com.hqgj.myapplication.widget.VideoMediaController;
import com.hqgj.myapplication.widget.VideoSuperPlayer;

/**
 * Created by bumu-zhz on 2017/1/10.
 */
public class VideoFullActivity extends Activity {

	private VideoSuperPlayer video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_video_full);
		video = (VideoSuperPlayer) findViewById(R.id.textureview);
		video.loadAndPlay(MediaPlayerUtil.getMediaPlayer(), "url", 0, true);
		video.setPageType(VideoMediaController.PageType.EXPAND);
		video.setVideoPlayCallback(new VideoSuperPlayer.VideoPlayCallbackImpl() {

			@Override
			public void onSwitchPageType() {
				if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					finish();
				}
			}

			@Override
			public void onPlayFinish() {

			}

			@Override
			public void onCloseVideo() {

			}
		});
	}

	@Override
	protected void onPause() {
		if (MediaPlayerUtil.getMediaPlayer().isPlaying()) {
			video.pausePlay();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
