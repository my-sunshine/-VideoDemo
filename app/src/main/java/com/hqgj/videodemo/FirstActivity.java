package com.hqgj.videodemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;

public class FirstActivity extends Activity {

    private Bitmap bitmap;
    private Bitmap tempBitmap;

    private TextView frameLayout;
    int bitmapWidth;
    int bitmapHeight;
    float roateY;
    float viewHeight;

    private ImageView imgBottom;
    private PercentRelativeLayout percentRelativeLayout;
    private ImageView remark;
    private ImageView play;
    private ImageView back;

    private Animation bgAnimation;
    private Animation bottomToTopAnimation;
    private Animation topTobottomAnimation;

    /*private Animation alphaShowAnimation;
    private Animation alphaHiddenAnimation;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ImageView bg = (ImageView) findViewById(R.id.bg);

        bgAnimation = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        bgAnimation.setDuration(10000);
        bgAnimation.setRepeatMode(Animation.REVERSE);
        bgAnimation.setRepeatCount(Animation.INFINITE);
        bg.setAnimation(bgAnimation);
        bgAnimation.start();

        bottomToTopAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        bottomToTopAnimation.setDuration(500);

        topTobottomAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        topTobottomAnimation.setDuration(500);

        /*alphaShowAnimation=new AlphaAnimation(0,1);
        alphaShowAnimation.setDuration(500);
        alphaHiddenAnimation=new AlphaAnimation(1,0);
        alphaHiddenAnimation.setDuration(500);

        */

        topTobottomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (frameLayout.getVisibility() == View.INVISIBLE) {
                    imgBottom.setVisibility(View.INVISIBLE);
                    frameLayout.setVisibility(View.VISIBLE);
                    frameLayout.setAnimation(bottomToTopAnimation);
                    bottomToTopAnimation.start();
                } else {
                    imgBottom.setAnimation(bottomToTopAnimation);
                    bottomToTopAnimation.start();
                    imgBottom.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        imgBottom = (ImageView) findViewById(R.id.imgBottom);
        percentRelativeLayout = (PercentRelativeLayout) findViewById(R.id.percentRelativeLayout);
        frameLayout = (TextView) findViewById(R.id.fragment_container);

        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        mwasureHeight();

        remark = (ImageView) findViewById(R.id.remark);
        play = (ImageView) findViewById(R.id.play);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE) {

                    frameLayout.setAnimation(topTobottomAnimation);
                    topTobottomAnimation.start();

                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
            }
        });

        remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgBottom.setAnimation(topTobottomAnimation);
                topTobottomAnimation.start();

                play.setVisibility(View.GONE);

                back.setVisibility(View.VISIBLE);


                int y = (int) (frameLayout.getTop());

                int start = (int) (y * roateY);

                tempBitmap = Bitmap.createBitmap(bitmap, 0, start, bitmapWidth, bitmapHeight - start);

                tempBitmap = NativeStackBlur.process(tempBitmap, 100);

                frameLayout.setBackground(new BitmapDrawable(getResources(), tempBitmap));


            }
        });

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void mwasureHeight() {

        ViewTreeObserver observer = imgBottom.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewHeight = percentRelativeLayout.getMeasuredHeight();

                roateY = (float) (bitmapHeight * 1.0 / viewHeight);

                int imaViewY = (int) (imgBottom.getTop());

                int start = (int) (imaViewY * roateY);

                tempBitmap = Bitmap.createBitmap(bitmap, 0, start, bitmapWidth, bitmapHeight - start);

                tempBitmap = NativeStackBlur.process(tempBitmap, 100);

                imgBottom.setImageBitmap(tempBitmap);


            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
        tempBitmap.recycle();
    }


    @Override
    public void onBackPressed() {
        if (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE) {

            frameLayout.setAnimation(topTobottomAnimation);
            topTobottomAnimation.start();
            return;
        }
        super.onBackPressed();
    }
}
