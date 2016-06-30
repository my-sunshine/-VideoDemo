# VideoDemo

####1、
```Java

	compile 'com.hqgj:myvideo:0.3.4'

####2、
	<com.hqgj.myvideo.ui.VideoRelativeLayout
		android:id="@+id/videoRelativeLayout"
        	android:layout_width="match_parent"
        	android:layout_height="match_parent"
        	/>

####3、
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
```
