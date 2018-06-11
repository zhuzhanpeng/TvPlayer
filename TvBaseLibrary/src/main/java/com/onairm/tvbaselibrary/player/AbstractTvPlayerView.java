package com.onairm.tvbaselibrary.player;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 1.创建player
 * 2.SurfaceView创建
 * 3.player状态变化通知Controller
 * <p>
 * //播放器state:
 * prepared,complete,buffering,playing,paused,error
 * <p>
 * 一进来就播放，不需要点击开始按钮
 */

/**
 * NewPlayer与NoPlayer
 * LoadingController与BasicUiController的自由组合
 * 可尝试，桥接模式
 */

public abstract class AbstractTvPlayerView extends RelativeLayout implements PlayCallBackListener {

    public static final int NEW_PLAYER = 1;
    public static final int NO_NEW_PLAYER = 2;
    private PlayerComplete playCompleteListener;


    @IntDef({NEW_PLAYER, NO_NEW_PLAYER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FsType {
    }


    private UiController controller;
    private TvPlayer tvPlayer;
    private long progress;

    private boolean isStopLoad = false;

    LayoutParams controllerParams;


/*
    public AbstractTvPlayer(Context context, @FsType int type, String path) {
        super(context);
        this.type = type;
        this.path = path;
        //focusable设置为false
        setFocusable(false);
        setClipChildren(false);
        init();
        //创建播放器
        createPlayer();

    }*/

    public AbstractTvPlayerView(Context context) {
        super(context);
        //focusable设置为false
        setFocusable(false);
        setClipChildren(false);
        init();
    }


    private void init() {

        setBackgroundColor(Color.parseColor("#000000"));
        controllerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        controllerParams.addRule(ALIGN_PARENT_BOTTOM);

        this.controller = createUi();
        tvPlayer = createPlayer();

        addView(this.controller, controllerParams);
    }


    public void setStopLoad(boolean value) {
        IjkMediaPlayer mediaPlayer = SinglePlayerUtil.getMediaPlayer();
        if (null != mediaPlayer) {
            mediaPlayer.pause();
        }
        isStopLoad = value;
    }


    public void pause() {
        if (null != tvPlayer) {
            tvPlayer.pause();
        }
    }

    // TODO: 2018/3/5 不再释放
    public void release() {
        if (null != tvPlayer) {
            tvPlayer.release();
        }
    }


    //播放列表切换视频源所设计的方法
    private void switchVideoPath(String path) {

        if (null != tvPlayer) {
            tvPlayer.createPlayer(path);
            tvPlayer.setController(this, controller, progress);
        }
    }

    public interface PlayerComplete {
        void playComplete();
    }


    public void setPlayCompleteListener(PlayerComplete playCompleteListener) {
        this.playCompleteListener = playCompleteListener;
    }

    public void handleKeyEvent(int keyCode, KeyEvent event) {
        if (null != controller) {
            if (controller instanceof BasicUiController) {
                ((BasicUiController) controller).onKeyDown(keyCode, event);
            } else if (controller instanceof LoadingUiController) {

            }
        }
    }

    //怎末区分呢
    public long getProgress() {
        long progress = 0;
        if (null != tvPlayer) {
            progress = tvPlayer.getProgress();
        }
        return progress;
    }


    public void recoverVideo(String path, long progress) {
        this.progress = progress;
        switchVideoPath(path);
    }

    protected abstract UiController createUi();

    protected abstract TvPlayer createPlayer();

    public void notifyController(int state) {
        if (null != controller) {
            controller.setState(state);
        }
    }

    public void playComplete() {
        if (null != playCompleteListener) {
            playCompleteListener.playComplete();
        }
    }


}

