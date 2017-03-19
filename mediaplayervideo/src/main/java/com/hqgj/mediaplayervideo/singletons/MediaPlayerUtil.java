package com.hqgj.mediaplayervideo.singletons;

import android.media.MediaPlayer;


public class MediaPlayerUtil {

    private static MediaPlayer mediaPlayer;

    public static MediaPlayer hasMediaPlayer(){

        return mediaPlayer;
    }

    public static MediaPlayer getMediaPlayer(){
        if(mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setLooping(false);
            return mediaPlayer;
        }
        return mediaPlayer;
    }



    public static void destoryMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public static void pauseMediaPlayer(){
        if(mediaPlayer!=null /*&& mediaPlayer.isPlaying()*/){
            mediaPlayer.pause();
        }
    }

    public static void playMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    public static void seekToMediaPlayer(int mes){
        if(mediaPlayer!=null){
            mediaPlayer.seekTo(mes);
        }
    }

    public static boolean isMediaPlayerPlaying(){
        return mediaPlayer!=null && mediaPlayer.isPlaying();
    }

    public static long getMediaPlayerDuration(){
        if(mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public static long getMediaPlayerCurrentPosition(){
        if(mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

   /* public void releaseMediaPlayer(){
        if (mediaPlayer!=null){
            mediaPlayer.stop();;
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }*/

    /*private static class GetMediaPlayerInner{
        public static MediaPlayer getMediaPlayer(){
            return new MediaPlayer();
        }
    }*/
}
