package com.onairm.tvbaselibrary.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.onairm.tvbaselibrary.R;
import com.onairm.tvbaselibrary.player.AbstractTvPlayerView;


public class FirstVideoActivity extends AppCompatActivity{

    private FrameLayout root;
    private Button btnSwitch;
    private AbstractTvPlayerView fullScreenPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_video);
        root=findViewById(R.id.root);
        btnSwitch=findViewById(R.id.btnSwitch);
        /*fullScreenPlayer=new FullScreenPlayer(FirstVideoActivity.this,
                FullScreenPlayer.NEW_PLAYER,
                "http://1253332079.vod2.myqcloud.com/2b9deca2vodgzp1253332079/4e2c75e59031868222973415604/f0.mp4",
                );
         fullScreenPlayer.setController(null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(480,270);
        params.gravity= Gravity.CENTER_HORIZONTAL;
        root.addView(fullScreenPlayer,params);*/




        btnSwitch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnSwitch.setBackgroundColor(Color.parseColor("#f9a825"));
                }
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SimpleFSActivity.jumpToLiveActivity(FirstVideoActivity.this,
                        FullScreenPlayer.NO_NEW_PLAYER,
                        "http://joymedia.oss-cn-hangzhou.aliyuncs.com/joyMedia/live_id_41.m3u8",
                        "测试视频");*/
            }
        });
        btnSwitch.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fullScreenPlayer.pause();
    }
}
