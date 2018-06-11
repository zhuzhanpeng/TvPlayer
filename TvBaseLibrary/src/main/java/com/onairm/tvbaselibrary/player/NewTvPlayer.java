package com.onairm.tvbaselibrary.player;

import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * NEW_PLAYER
 */

class NewTvPlayer extends TvPlayer/*AbstractTvPlayer */ {
    public NewTvPlayer(PlayCallBackListener playCallBackListener) {
        super(playCallBackListener);
    }

    @Override
    public void createPlayer(String path) {
        openVideo(path);
        isBuffering = true;
    }

    @Override
    public void setController(ViewGroup container, UiController controller, long progress) {
//初始化Surface
        if (null == surface) {
            surface = new SurfaceView(container.getContext());
            surface.setFocusable(false);
            surfaceHolder = surface.getHolder();
            surfaceHolder.addCallback(this);
            final RelativeLayout.LayoutParams surfaceParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(surface, surfaceParams);
        } else {

            surface.setVisibility(GONE);
            surface.setVisibility(VISIBLE);
        }

        //初始化Controller，ui状态的改变交给Controller
        if (null != controller) {
            container.removeView(controller);
            container.addView(controller, controllerParams);

            controller.showBuffer();

            controller.setPlayer(player);
        }

        addPlayerListener(progress);
    }

    private void openVideo(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        player = SinglePlayerUtil.getMediaPlayer();
        if (null != player) {
            player.reset();
        }
        try {

            player.setDataSource(path);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setScreenOnWhilePlaying(true);
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPlayerListener(final long progress) {
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (0 != progress) {
                    mp.seekTo(progress);
                }

                isPrepared = true;

                notifyController(TvPlayerState.PREPARED);
            }
        });

        player.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                       /* if (type == NEW_PLAYER) {*/
                        isBuffering = true;
                        /*}*/
                        notifyController(TvPlayerState.BUFFERING);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //isStartPlay = true;
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        /*if (type == NEW_PLAYER) {*/
                        isBuffering = false;
                       /* }*/
                        notifyController(TvPlayerState.PLAYING);
                        break;
                }
                return false;
            }
        });

        player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (System.currentTimeMillis() - startTime > 10 * 1000) {
                    isBuffering = false;
                    isPrepared = false;
                    startTime = System.currentTimeMillis();
                    Log.d("oopsize", "onCompletion:" + SinglePlayerUtil.getPlayCount());
                    notifyPlayComplete();
                    notifyController(TvPlayerState.COMPLETE);
                }

            }
        });

        player.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                //播放出错的处理
                notifyController(TvPlayerState.ERROR);
                return false;
            }
        });
    }
/*    public NewTvPlayer(Context context, int type, String path) {
        super(context, type, path);
    }

    public NewTvPlayer(Context context, int type) {
        super(context, type);
    }

    @Override
    protected void createPlayer() {

    }

    @Override
    public UiController createUi() {
        return new BasicUiController(getContext(),"");
    }*/
}
