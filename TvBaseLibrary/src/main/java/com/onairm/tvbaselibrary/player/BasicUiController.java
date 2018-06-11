package com.onairm.tvbaselibrary.player;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.onairm.tvbaselibrary.R;
import com.onairm.tvbaselibrary.utils.DateUtils;
import com.onairm.tvbaselibrary.utils.NetWorkSpeedUtils;
import com.onairm.tvbaselibrary.utils.NoDoubleClickListener;

/**
 * 基本的ui，包含快进、暂停、几秒view的显示/隐藏、处理onKeyDown事件
 * 难点：onKeyDown在Activity中容易处理，在子view中不容易处理
 */

class BasicUiController extends UiController {
    private FrameLayout mLoadingLayout;
    private TextView mLoadingText;
    private MySeekBar videoSeekBar;
    private LiveConsole llBottomPanel;
    private View mCurrentFocusLayout;
    private String title="";
    private NetWorkSpeedUtils mNetWorkSpeedUtils;

    private boolean isVideoComplete = false;
    private boolean isPlaying = true;
    // 进度条最大值
    private static int MAX_VIDEO_SEEK = 0;
    // 目标进度
    private int mTargetPosition = 0;
    private int mTargetProgress = 0;
    private int mSpaceTime = 1000;//单位毫秒(多长时间更新一下进度条)
    private Handler mNetSpeedHandler;
    public BasicUiController(Context context) {
        super(context);
        setClipChildren(false);
    }

    @Override
    protected void init() {
        super.init();
        mLoadingLayout = (FrameLayout) findViewById(R.id.flLoading);
        mLoadingText = (TextView) findViewById(R.id.tv_load_msg);
        llBottomPanel = (LiveConsole) findViewById(R.id.llConsole);
        videoSeekBar = llBottomPanel.getSeekBar();

        llBottomPanel.setTitle(title);
        mNetSpeedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 100:
                        mLoadingText.setText(msg.obj.toString());
                        break;
                }
                super.handleMessage(msg);
            }
        };
        mNetWorkSpeedUtils = new NetWorkSpeedUtils(getContext(), mNetSpeedHandler);
        mNetWorkSpeedUtils.startShowNetSpeed();


        llBottomPanel.setOnClickListeners(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                resetTimer();
                int id = v.getId();
                if (id == R.id.btnPause) {
                    if (!isVideoComplete) {
                        llBottomPanel.pauseImgSwitch(isPlaying);
                        startOrPause(isPlaying);
                        isPlaying = !isPlaying;
                    } else {
                        if (null != player) {
                            player.seekTo(0);
                            player.start();
                        }

                    }

                }
            }
        });

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (null == player) {
                    return;
                }
                long duration = player.getDuration();
                if (fromUser) {
                    mProgressHandler.removeMessages(10);
                    resetTimer();
                    // 计算目标位置
                    mTargetPosition = (int) (progress / (float) MAX_VIDEO_SEEK * duration);
                    mTargetProgress = progress;
                    updateProgressTip(progress, mTargetPosition);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
        videoSeekBar.setmOnKeySeekBarChangeListener(new MySeekBar.OnKeySeekBarChangeListener() {
            @Override
            public void onKeyStartTrackingTouch() {
                mProgressHandler.removeMessages(10);
            }

            @Override
            public void onKeyStopTrackingTouch() {
                if (mTargetPosition >= player.getDuration()) {
                    if (null != player) {
                        player.seekTo(player.getDuration());
                    }
                } else {
                    if (null != player) {
                        player.seekTo(mTargetPosition);
                    }
                }
                mProgressHandler.sendMessage(Message.obtain());
            }
        });
    }
    public void setTitle(String title){
        this.title=title;
        if(null != llBottomPanel){
            llBottomPanel.setTitle(title);
        }
    }
    @Override
    protected int getLayout() {
        return R.layout.layout_basic_controller;
    }

    private void startOrPause(boolean play) {
        if (null == player)
            return;

        if (play && player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    @Override
    public void setState(int state) {
        if (state == TvPlayerState.BUFFERING) {
            //缓冲中
            showBuffer();
        } else if (state == TvPlayerState.PAUSED) {
            //暂停中
            llBottomPanel.pauseImgSwitch(true);
        } else if (state == TvPlayerState.PLAYING) {
            //播放中
            hideBuffer();
        } else if (state == TvPlayerState.PREPARED) {
            //
           // hideBuffer();
            mProgressHandler.sendMessage(Message.obtain());
            setMaxProgress();

        } else if (state == TvPlayerState.ERROR) {
            //错误
        } else if (state == TvPlayerState.COMPLETE) {
            //完成
            isVideoComplete = true;
            resetProgressBar();
            rePlay();
        } else if (state == TvPlayerState.RELEASE) {
            if (null != mProgressHandler) {
                mProgressHandler.removeMessages(10);
                mProgressHandler = null;
            }

            if (null != timer) {
                timer.cancel();
            }

            if (null != mNetSpeedHandler) {
                mNetSpeedHandler.removeMessages(100);
            }
            if (null != mNetWorkSpeedUtils) {
                mNetWorkSpeedUtils.stopSpeed();
            }
        }
    }

    private void rePlay() {
        if (null == player) {
            return;
        }
        llBottomPanel.pauseRequestFocus();

        mProgressHandler.sendMessage(Message.obtain());

        player.seekTo(0);
        player.start();
        llBottomPanel.pauseImgSwitch(false);
        isVideoComplete = false;
    }

    private void resetProgressBar() {
        mTargetPosition = 0;
        mTargetProgress = 0;
        mProgressHandler.removeMessages(10);
        videoSeekBar.setProgress(0);
        llBottomPanel.pauseImgSwitch(true);
        llBottomPanel.setShowText(0, "", MAX_VIDEO_SEEK);
    }



    private Handler mProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long pos = _setProgress();
            Message msg2 = Message.obtain();
            msg2.what = 10;
            sendMessageDelayed(msg2, mSpaceTime - (pos % mSpaceTime));
        }
    };

    private void setMaxProgress() {
        if (null == player) {
            return;
        }

        long duration = player.getDuration();
        int seconds = (int) (duration / mSpaceTime);
        MAX_VIDEO_SEEK = seconds;
        videoSeekBar.setMax(MAX_VIDEO_SEEK);

        //如果时长大于20分钟，算长视频
        if (seconds > 60 * 20) {
            videoSeekBar.setKeyProgressIncrement(MAX_VIDEO_SEEK / 100);
        }
        llBottomPanel.initSeekBar("00:00/" + DateUtils.formatDuringToMinute(duration), MAX_VIDEO_SEEK);
    }

    @Override
    public void showBuffer() {
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideBuffer() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    private boolean isBuffering() {
        return isShowingBuffer();
    }

    private boolean isShowingBuffer() {
        return mLoadingLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * 更新进度条
     *
     * @return
     */
    private long _setProgress() {
        if (null == player) {
            return 0;
        }
        if (!player.isPlaying()) {
            return 0;
        }
        // 视频播放的当前进度
        long position = player.getCurrentPosition();
        // 视频总的时长
        long duration = player.getDuration();
        if (duration > 0) {
            // 转换为 Seek 显示的进度值
            int mProgress = (int) ((position / (float) duration) * MAX_VIDEO_SEEK);
            mTargetPosition = (mTargetPosition > player.getCurrentPosition()) ? mTargetPosition : (int) player.getCurrentPosition();
            if (mProgress >= mTargetProgress) {
                updateProgressTip(mProgress, mTargetPosition);
            } else {
                updateProgressTip(mTargetProgress, mTargetPosition);
            }
        }
        // 返回当前播放进度
        return position;
    }

    private void updateProgressTip(int progress, int position) {
        if (null == player) {
            return;
        }

        long duration = player.getDuration();
        llBottomPanel.setShowText(progress, DateUtils.formatDuringToMinute((position >= duration) ? duration : position) + "/"
                + DateUtils.formatDuringToMinute(duration), MAX_VIDEO_SEEK);
        videoSeekBar.setProgress(progress);

    }


    private void resetTimer() {
        timer.cancel();
        timer.start();
    }

    private CountDownTimer timer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (mCurrentFocusLayout != null) {
                mCurrentFocusLayout.setVisibility(View.GONE);
            }
        }
    };

    public boolean onKeyDown(int kCode, KeyEvent kEvent) {
        resetTimer();
        switch (kCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (isBuffering()) {
                    return true;
                } else {
                    return super.onKeyDown(kCode, kEvent);
                }
            case KeyEvent.KEYCODE_DPAD_UP:
                if (isBuffering()) {
                    return true;
                } else {
                    return super.onKeyDown(kCode, kEvent);
                }
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (isBuffering()) {
                    return true;
                } else {
                    return super.onKeyDown(kCode, kEvent);
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (isBuffering()) {
                    return true;
                } else {
                    return super.onKeyDown(kCode, kEvent);
                }
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (!isBuffering()) {
                    showLiveConsole();
                    llBottomPanel.pauseRequestFocus();
                    return true;
                } else {
                    return super.onKeyDown(kCode, kEvent);
                }
        }
        return super.onKeyDown(kCode, kEvent);
    }

    private void showLiveConsole() {
        llBottomPanel.setVisibility(View.VISIBLE);
        resetTimer();
        mCurrentFocusLayout = llBottomPanel;
    }
}
