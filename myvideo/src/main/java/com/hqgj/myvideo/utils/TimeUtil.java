package com.hqgj.myvideo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: ly
 * data: 2016/6/27
 */
public class TimeUtil {

  public  static String formatTime(long time){
      SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm:ss");
      return simpleDateFormat.format(new Date(time));
  }

}
