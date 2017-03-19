package com.hqgj.mediaplayervideo.singletons;

import android.view.SurfaceHolder;

/**
 * Created by ly on 2017/3/17.
 */

public abstract class SurfaceViewCallbackInf implements SurfaceHolder.Callback  {
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
