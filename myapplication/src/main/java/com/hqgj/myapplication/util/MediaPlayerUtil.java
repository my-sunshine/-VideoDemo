package com.hqgj.myapplication.util;

import android.media.MediaPlayer;

/**
 * Created by ly on 2017/3/15.
 */

public class MediaPlayerUtil {

    private static MediaPlayer mediaPlayer=new MediaPlayer();

    public static MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

   /* public void releaseMediaPlayer(){
        if (mediaPlayer!=null){
            mediaPlayer.stop();;
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }*/

  /*  private static class GetMediaPlayerInner{
        private static MediaPlayer mediaPlayer;
        static MediaPlayer getMediaPlayer(){
             if(mediaPlayer==null){
                 return new MediaPlayer();
             }
            return mediaPlayer;
        }
    }*/
}
