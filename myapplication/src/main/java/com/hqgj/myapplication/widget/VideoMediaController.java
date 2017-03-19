package com.hqgj.myapplication.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hqgj.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 视频播放器控制按钮
 */
public class VideoMediaController extends FrameLayout implements
		SeekBar.OnSeekBarChangeListener, View.OnClickListener {
	private ImageView mPlayImg;// 播放按钮
	private SeekBar mProgressSeekBar;// 播放进度条
	private TextView mTimeTxt;// 播放时间
	private TextView mAllTimeTxt;// 播放总时间
	private ImageView mExpandImg;// 最大化播放按钮
	private ImageView mShrinkImg;// 缩放播放按钮
	private ImageView mfullBack;// 全屏返回按钮
	private View fullBar;
	private View bar;

	private MediaControlImpl mMediaControl;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
								  boolean isFromUser) {
		if (isFromUser)
			mMediaControl.onProgressTurn(ProgressState.DOING, progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mMediaControl.onProgressTurn(ProgressState.START, 0);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mMediaControl.onProgressTurn(ProgressState.STOP, 0);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.videocontroller_bar_play) {
			mMediaControl.onPlayTurn();//暂停，播放
		} else if (view.getId() == R.id.videocontroller_bar_fill) {
			mMediaControl.onPageTurn();//全屏
		} else if (view.getId() == R.id.videocontroller_bar_scale) {
			mMediaControl.onPageTurn();//小屏
		} else if(view.getId() == R.id.videocontroller_full_bar_back){
			mMediaControl.onPageTurn();//退出全屏，返回小屏
		}
	}

	/**
	 * 是否显示全屏返回按钮
	 * @param isShow
	 */
	public void showHideFullBack(boolean isShow){
		if(isShow){
			fullBar.setVisibility(View.VISIBLE);
		}else{
			fullBar.setVisibility(View.GONE);
		}
	}

	public void setProgressBar(int progress) {
		if (progress < 0)
			progress = 0;
		if (progress > 100)
			progress = 100;
		mProgressSeekBar.setProgress(progress);
	}

	public void setProgressBar(int progress, int secondProgress) {
		if (progress < 0)
			progress = 0;
		if (progress > 100)
			progress = 100;
		if (secondProgress < 0)
			secondProgress = 0;
		if (secondProgress > 100)
			secondProgress = 100;
		mProgressSeekBar.setProgress(progress);
		mProgressSeekBar.setSecondaryProgress(secondProgress);
	}

	public void setPlayState(PlayState playState) {
		mPlayImg.setImageResource(playState.equals(PlayState.PLAY) ? R.drawable.media_pause
				: R.drawable.media_play);
	}

	public void setPageType(PageType pageType) {
		mExpandImg.setVisibility(pageType.equals(PageType.EXPAND) ? GONE
				: VISIBLE);
		mShrinkImg.setVisibility(pageType.equals(PageType.SHRINK) ? GONE
				: VISIBLE);
	}

	/**
	 * 修改播放时间
	 * @param nowSecond
	 * @param allSecond
	 */
	public void setPlayProgressTxt(int nowSecond, int allSecond) {
		//当前播放时间
		String playSecondStr = "00:00";
		if (nowSecond > 0) {
			playSecondStr = formatPlayTime(nowSecond);
		}
		mTimeTxt.setText(playSecondStr);

		//视频总时间
		String allSecondStr = "00:00";
		if (allSecond > 0) {
			allSecondStr = formatPlayTime(allSecond);
		}
		mAllTimeTxt.setText(allSecondStr);

	}

	public void playFinish(int allTime) {
		mProgressSeekBar.setProgress(0);
		setPlayProgressTxt(0, allTime);
		setPlayState(PlayState.PAUSE);
	}

	public void setMediaControl(MediaControlImpl mediaControl) {
		mMediaControl = mediaControl;
	}

	public VideoMediaController(Context context) {
		super(context);
		initView(context);
	}

	public VideoMediaController(Context context, AttributeSet attrs,
								int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public VideoMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.video_media_controller, this);
		mPlayImg = (ImageView) findViewById(R.id.videocontroller_bar_play);
		mProgressSeekBar = (SeekBar) findViewById(R.id.videocontroller_progress);
		mTimeTxt = (TextView) findViewById(R.id.videocontroller_bar_play_time);
		mAllTimeTxt = (TextView) findViewById(R.id.videocontroller_bar_total_time);
		mExpandImg = (ImageView) findViewById(R.id.videocontroller_bar_fill);
		mShrinkImg = (ImageView) findViewById(R.id.videocontroller_bar_scale);
		mfullBack =  (ImageView) findViewById(R.id.videocontroller_full_bar_back);
		fullBar = findViewById(R.id.videocontroller_full_bar);
		fullBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return true;
			}
		});
		bar = findViewById(R.id.videocontroller_bar);
		bar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return true;
			}
		});
		initData();
	}

	private void initData() {
		mProgressSeekBar.setOnSeekBarChangeListener(this);
		mPlayImg.setOnClickListener(this);
		mShrinkImg.setOnClickListener(this);
		mfullBack.setOnClickListener(this);
		mExpandImg.setOnClickListener(this);
		setPageType(PageType.SHRINK);
		setPlayState(PlayState.PAUSE);
	}

	private String formatPlayTime(long time) {
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	/**
	 * 播放样式 展开、缩放
	 */
	public enum PageType {
		EXPAND, SHRINK
	}

	/**
	 * 播放状态 播放 暂停
	 */
	public enum PlayState {
		PLAY, PAUSE
	}

	public enum ProgressState {
		START, DOING, STOP
	}

	public interface MediaControlImpl {
		void onPlayTurn();

		void onPageTurn();

		void onProgressTurn(ProgressState state, int progress);

		void alwaysShowController();
	}

}
