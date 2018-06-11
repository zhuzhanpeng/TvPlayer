package com.onairm.tvbaselibrary.player;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

/**
 * Player的抽象类
 */

abstract class TvPlayer implements SurfaceHolder.Callback{
    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        SinglePlayerUtil.getMediaPlayer();

    }
    protected IjkMediaPlayer player;
    protected SurfaceView surface;
    protected SurfaceHolder surfaceHolder;
    protected PlayCallBackListener playCallBackListener;
    protected long startTime;
    RelativeLayout.LayoutParams controllerParams;



    public static boolean isBuffering;
    public static boolean isPrepared;
    public abstract void createPlayer(String path);
    public abstract void setController(ViewGroup container, UiController controller,long progress);
    public TvPlayer(PlayCallBackListener playCallBackListener){
        this.playCallBackListener=playCallBackListener;
        controllerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        controllerParams.addRule(ALIGN_PARENT_BOTTOM);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (null != player) {
            player.setDisplay(holder);
           /* if (!isStopLoad) {*/
                player.start();
            /*}*/
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    protected void notifyController(int state){
        if(null != playCallBackListener){
            playCallBackListener.notifyController(state);
        }
    }

    protected void notifyPlayComplete(){
        if(null != playCallBackListener){
            playCallBackListener.playComplete();
        }
    }

    public void pause(){
        if(null != player){
            player.pause();
            notifyController(TvPlayerState.PAUSED);
        }
    }

    public void release() {
        notifyController(TvPlayerState.RELEASE);
    }
    //怎末区分呢
    public long getProgress() {
        long progress = 0;
        try {
            progress = player.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
            progress = 0;
        }
        return progress;
    }
}
