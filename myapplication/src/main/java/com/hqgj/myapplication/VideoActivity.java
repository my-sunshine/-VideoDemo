package com.hqgj.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hqgj.myapplication.util.MediaPlayerUtil;
import com.hqgj.myapplication.util.NetWorkUtil;
import com.hqgj.myapplication.widget.VideoSuperPlayer;

/**
 * Created by bumu-zhz on 2017/1/10.
 */
public class VideoActivity extends Activity implements View.OnClickListener {

	private ImageView playView;
	private VideoSuperPlayer videoSuperPlayer;

	private Activity mActivity;
	private String url = "http://stream2.yunxuetang.com/knowledgefiles/ksw/videos/201412/0dc24f26d0e6f2b6326b1c3c9b203ba94472a680d9cebd9983e618ddf625003b.mp4";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_video);
		mActivity = this;
		initView();
	}

	private void initView() {
		playView = (ImageView) findViewById(R.id.traindetail_play);
		playView.setOnClickListener(this);

		videoSuperPlayer = (VideoSuperPlayer) findViewById(R.id.traindetail_surface_video);
		videoSuperPlayer.setVideoPlayCallback(new VideoSuperPlayer.VideoPlayCallbackImpl() {

			@Override
			public void onSwitchPageType() {
				Intent intent = new Intent(new Intent(mActivity, VideoFullActivity.class));
				startActivity(intent);
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
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.traindetail_play) {
			if (!NetWorkUtil.isNetWifi(mActivity)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("提示");
				builder.setMessage("您正在使用移动数据网络，会产生流量费用，建议使用Wifi网络。");
				builder.setPositiveButton("继续播放", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						play();
					}
				});
				builder.setNegativeButton("停止播放", null);
				builder.show();
			} else {
				play();
			}
		}
	}

	private void play() {
		playView.setVisibility(View.GONE);
		videoSuperPlayer.loadAndPlay(MediaPlayerUtil.getMediaPlayer(), url, 0, false);
	}

	@Override
	protected void onPause() {
		if (MediaPlayerUtil.getMediaPlayer().isPlaying()) {
			videoSuperPlayer.pausePlay();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		videoSuperPlayer.close();
		//VideoApp.setMediaPlayerNull();
		super.onDestroy();
	}

}
