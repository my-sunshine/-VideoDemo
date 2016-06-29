package com.hqgj.myvideo.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * author: ly
 * data: 2016/6/27
 */
public class DensityUtils {

    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
