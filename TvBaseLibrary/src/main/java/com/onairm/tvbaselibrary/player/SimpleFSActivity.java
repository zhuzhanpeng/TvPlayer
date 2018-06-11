package com.onairm.tvbaselibrary.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static com.onairm.tvbaselibrary.player.AbstractTvPlayerView.NEW_PLAYER;
import static com.onairm.tvbaselibrary.player.AbstractTvPlayerView.NO_NEW_PLAYER;

/**
 * 简单的全屏Activity,注意区分长视频和短视频
 */
// TODO: 2018/2/26 以后统计功能怎末写
public class SimpleFSActivity extends AppCompatActivity {
    private String path = "http://joymedia.oss-cn-hangzhou.aliyuncs.com/joyMedia/live_id_41.m3u8";
    private String title;
    AbstractTvPlayerView fullScreenPlayer;
    private int type;
    private int beanKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initIntent();
        super.onCreate(savedInstanceState);
        if (type == NEW_PLAYER) {
            //            fullScreenPlayer=new BasicNewPlayerView(SimpleFSActivity.this);
            fullScreenPlayer = PlayerFactory.getInstance().newInstance(BasicNewPlayerView.class, this);
        } else if (type == NO_NEW_PLAYER) {
            //            fullScreenPlayer=new BasicNoNewPlayerView(SimpleFSActivity.this);
            fullScreenPlayer = PlayerFactory.getInstance().newInstance(BasicNoNewPlayerView.class, this);
        }

        fullScreenPlayer.recoverVideo(path, 0);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(fullScreenPlayer, params);
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            //此处重写
            type = intent.getIntExtra("type", 0);
            path = intent.getStringExtra("url");
            title = intent.getStringExtra("title");
            beanKey = intent.getIntExtra("beanKey", 0);
        }
        if (type == 0) {
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (null != fullScreenPlayer) {
            fullScreenPlayer.handleKeyEvent(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void jumpToActivityForResult(Context context, @AbstractTvPlayerView.FsType int type
            , String url, String title, int beanKey, int requestCode) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, SimpleFSActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("beanKey", beanKey);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * @param type 全屏还是从小窗进全屏
     */
    public static void jumpToLiveActivity(Context context, @AbstractTvPlayerView.FsType int type
            , String url, String title, int beanKey) {

        Intent intent = new Intent(context, SimpleFSActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("beanKey", beanKey);
        context.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (type == NO_NEW_PLAYER) {
            PlayerBean playerBean = SinglePlayerUtil.getPlayerBean(beanKey);
            if (null != playerBean) {
                long progress = fullScreenPlayer.getProgress();
                playerBean.setProgress(progress);
            }
        }
        if (null != fullScreenPlayer) {
            fullScreenPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != fullScreenPlayer) {
            fullScreenPlayer.release();
        }
    }
}