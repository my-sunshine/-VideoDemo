package com.hqgj.videodemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hqgj.myvideo.ui.VideoRelativeLayout;

public class MainActivity extends Activity {

    private VideoRelativeLayout videoRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_m);

        /*
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.test);
        bitmap=NativeStackBlur.process(bitmap,200);

        ImageView imageView= (ImageView) findViewById(R.id.img);

        imageView.setImageBitmap(bitmap);
        */


        videoRelativeLayout= (VideoRelativeLayout) findViewById(R.id.videoRelativeLayout);
        String path="http://7xrpiy.com1.z0.glb.clouddn.com/video%2F1.mp4";
        videoRelativeLayout.setVideoPath(path,"长在花盆和111111111111111111");

        videoRelativeLayout.setOnClickVideoListener(new VideoRelativeLayout.OnClickVideoListener() {
            @Override
            public void onClickBack() {
                Toast.makeText(MainActivity.this, "onClickBack", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onClickCollection() {
                Toast.makeText(MainActivity.this,"onClickCollection",Toast.LENGTH_LONG).show();
            }
        });





    }

    @Override
    protected void onPause() {
        super.onPause();
        videoRelativeLayout.onPause();
    }

}
