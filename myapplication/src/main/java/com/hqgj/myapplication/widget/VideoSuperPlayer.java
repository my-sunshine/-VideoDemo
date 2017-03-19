package com.hqgj.myapplication.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.hqgj.myapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class VideoSuperPlayer extends RelativeLayout {
	private final int TIME_SHOW_CONTROLLER = 3000;
	private final int TIME_UPDATE_PLAY_TIME = 1000;

	//隐藏
	private final int MSG_HIDE_CONTROLLER = 10;
	//更新播放时间
	private final int MSG_UPDATE_PLAY_TIME = 11;
	private VideoMediaController.PageType mCurrPageType = VideoMediaController.PageType.SHRINK;// 当前是横屏还是竖屏

	private Context mContext;
	private SurfaceView mSuperVideoView;
	private VideoMediaController mMediaController;
	private Timer mUpdateTimer;
	private TimerTask mTimerTask;
	private VideoPlayCallbackImpl mVideoPlayCallback;

	private View mProgressBarLayView;
	private View mProgressBarView;

	private MediaPlayer mPlayer;

	private boolean misfull;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_UPDATE_PLAY_TIME) {
				updatePlayTime();
				updatePlayProgress();
			} else if (msg.what == MSG_HIDE_CONTROLLER) {
				showOrHideController();
			}
		}
	};

	public VideoSuperPlayer(Context context) {
		super(context);
		initView(context);
	}

	public VideoSuperPlayer(Context context, AttributeSet attrs,
							int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public VideoSuperPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		View.inflate(context, R.layout.video_media_player_layout, this);
		mSuperVideoView = (SurfaceView) findViewById(R.id.videoinner_view);
		mSuperVideoView.setOnTouchListener(mOnTouchVideoListener);
		mSuperVideoView.getHolder().setKeepScreenOn(true);
		mSuperVideoView.getHolder().addCallback(new SurfaceViewLis());

		mMediaController = (VideoMediaController) findViewById(R.id.videoinner_controller);
		mMediaController.setMediaControl(mMediaControl);

		mProgressBarLayView = findViewById(R.id.videoinner_progressbar_lay);
		mProgressBarView = findViewById(R.id.videoinner_progressbar);

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 拦截
				return true;
			}
		});
	}

	/***
	 * 加载并开始播放视频
	 *
	 * @param videoUrl
	 */
	public void loadAndPlay(MediaPlayer player, String videoUrl, int seekTime,
							boolean isfull) {
		mProgressBarLayView.setVisibility(VISIBLE);
		mProgressBarView.setVisibility(VISIBLE);
		if (seekTime == 0) {
			mProgressBarLayView.setBackgroundResource(android.R.color.black);
		} else {
			mProgressBarLayView.setBackgroundResource(android.R.color.transparent);
		}
		if (TextUtils.isEmpty(videoUrl)) {
			Log.e("TAG", "videoUrl should not be null");
			return;
		}
		mSuperVideoView.setVisibility(VISIBLE);
		mPlayer = player;
		misfull = isfull;
		mMediaController.showHideFullBack(misfull);
		if (!misfull) {
			play(videoUrl);
		} else {
			mProgressBarLayView.setVisibility(View.GONE);
		}
		startPlayVideo(seekTime);
	}

	/**
	 * 播放视频 should called after loadVideo()
	 */
	private void startPlayVideo(int seekTime) {
		if (null == mUpdateTimer) {
			resetUpdateTimer();
		}
		resetUpdateTimer();
		resetHideTimer();
		if (seekTime > 0) {
			mPlayer.seekTo(seekTime);
		}
		mMediaController.setPlayState(VideoMediaController.PlayState.PLAY);
	}

	private void resetHideTimer() {
		mHandler.removeMessages(MSG_HIDE_CONTROLLER);
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER,
				TIME_SHOW_CONTROLLER);
	}

	private void resetUpdateTimer() {
		stopUpdateTimer();
		mUpdateTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);
			}
		};
		mUpdateTimer.schedule(mTimerTask, 0, TIME_UPDATE_PLAY_TIME);
	}

	/**
	 * 更新播放进度条
	 */
	private void updatePlayProgress() {
		if (mPlayer == null) {
			return;
		}
		try {
			int allTime = mPlayer.getDuration();
			int playTime = mPlayer.getCurrentPosition();
			int progress = playTime * 100 / allTime;
			mMediaController.setProgressBar(progress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新播放时间
	 */
	private void updatePlayTime() {
		if (mPlayer == null) {
			return;
		}
		try {
			int allTime = mPlayer.getDuration();
			int playTime = mPlayer.getCurrentPosition();
			mMediaController.setPlayProgressTxt(playTime, allTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void play(String url) {
		try {
//			 mPlayer.setOnCompletionListener(mOnCompletionListener);
			mPlayer.setOnPreparedListener(mOnPreparedListener);
			mPlayer.setDataSource(url);
			mPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
//			stopUpdateTimer();
//			mMediaController.playFinish(mPlayer.getDuration());
//			mVideoPlayCallback.onPlayFinish();
		}
	};

	/**
	 * 控制视频按钮工具条隐藏与显示
	 */
	private void showOrHideController() {
		if (mMediaController.getVisibility() == View.VISIBLE) {
			mMediaController.setVisibility(View.GONE);
		} else {
			mMediaController.setVisibility(View.VISIBLE);
			if(misfull){

			}
			resetHideTimer();
		}
	}

	class SurfaceViewLis implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
								   int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (mPlayer != null && !mPlayer.isPlaying()) {
				// 把视频输出到SurfaceView上
				mPlayer.setDisplay(mSuperVideoView.getHolder());
				playPlay();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	}

	/**
	 * SurfaceView触摸回调接口
	 */
	private OnTouchListener mOnTouchVideoListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				showOrHideController();
			}
			return mCurrPageType == VideoMediaController.PageType.EXPAND ? true
					: false;
		}
	};

	/**
	 * MediaPlayer预加载回调接口
	 */
	private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mediaPlayer) {
			try {
				mPlayer.setDisplay(mSuperVideoView.getHolder());
				mPlayer.start();
				mProgressBarLayView.setVisibility(View.GONE);
				mSuperVideoView.requestLayout();
				mSuperVideoView.invalidate();
				mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
					@Override
					public boolean onInfo(MediaPlayer mp, int what, int extra) {// TODO
						if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
							mProgressBarLayView.setVisibility(View.GONE);
							return true;
						}
						return false;
					}
				});
			}catch (Exception e){

			}
		}
	};

	/**
	 * 视频控制工具条按钮操作回调
	 */
	private VideoMediaController.MediaControlImpl mMediaControl = new VideoMediaController.MediaControlImpl() {
		@Override
		public void alwaysShowController() {
			VideoSuperPlayer.this.alwaysShowController();
		}

		@Override
		public void onPlayTurn() {
			if(mPlayer != null){
				if (mPlayer.isPlaying()) {
					pausePlay();
				} else {
					playPlay();
				}
			}
		}

		@Override
		public void onPageTurn() {
			mVideoPlayCallback.onSwitchPageType();
		}

		@Override
		public void onProgressTurn(VideoMediaController.ProgressState state,
								   int progress) {
			if (state.equals(VideoMediaController.ProgressState.START)) {
				mHandler.removeMessages(MSG_HIDE_CONTROLLER);
			} else if (state.equals(VideoMediaController.ProgressState.STOP)) {
				resetHideTimer();
			} else {
				if(mPlayer != null){
					int time = progress * mPlayer.getDuration() / 100;
					mPlayer.seekTo(time);
					updatePlayTime();
				}
			}
		}
	};

	private void alwaysShowController() {
		mHandler.removeMessages(MSG_HIDE_CONTROLLER);
		mMediaController.setVisibility(View.VISIBLE);
	}

	/**
	 * 视频暂停
	 */
	public void pausePlay() {
		mPlayer.pause();
		mMediaController.setPlayState(VideoMediaController.PlayState.PAUSE);
	}

	/**
	 * 视频播放
	 */
	public void playPlay() {
		mPlayer.start();
		mMediaController.setPlayState(VideoMediaController.PlayState.PLAY);
	}

	public void close() {
		mMediaController.setPlayState(VideoMediaController.PlayState.PAUSE);
		stopUpdateTimer();
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		mSuperVideoView.setVisibility(GONE);
	}

	private void stopUpdateTimer() {
		if (mUpdateTimer != null) {
			mUpdateTimer.cancel();
			mUpdateTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	public interface VideoPlayCallbackImpl {
		void onCloseVideo();

		void onSwitchPageType();

		void onPlayFinish();
	}

	public void setVideoPlayCallback(VideoPlayCallbackImpl videoPlayCallback) {
		mVideoPlayCallback = videoPlayCallback;
	}

	private class AnimationImp implements Animation.AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}
	}

	public void setPageType(VideoMediaController.PageType pageType) {
		mMediaController.setPageType(pageType);
		mCurrPageType = pageType;
	}

	public MediaPlayer getMediaPlayer(){
		return mPlayer;
	}
}