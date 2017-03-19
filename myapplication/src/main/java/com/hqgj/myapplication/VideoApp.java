package com.hqgj.myapplication;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by bumu-zhz on 2017/1/10.
 */
public class VideoApp  extends Application {

	private static Context mContext;
	//public static MediaPlayer mPlayer;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	public static Context getAppContext() {
		return mContext;
	}

	/*public static MediaPlayer getMediaPlayer() {
		if (mPlayer == null) {
			mPlayer = new MediaPlayer();
		}
		return mPlayer;
	}

	public static void setMediaPlayerNull() {
		if (mPlayer != null) {
			try{
				mPlayer.stop();
				mPlayer.release();
			}catch (Exception e){
			}
			mPlayer = null;
		}
	}*/
}
