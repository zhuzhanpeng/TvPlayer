package com.onairm.tvbaselibrary.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.onairm.tvbaselibrary.R;

/**
 * Created by Edison on 2018/3/5.
 */

class LoadingUiController extends UiController {
    private FrameLayout mLoadingLayout;
    private TextView mLoadingText;
    public LoadingUiController(@NonNull Context context) {
       super(context);
    }

    public LoadingUiController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingUiController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void init(){
        super.init();
        mLoadingLayout = (FrameLayout) findViewById(R.id.flLoading);
        mLoadingText = (TextView) findViewById(R.id.tv_load_msg);
    }

    @Override
    protected int getLayout() {
        return R.layout.layout_loading_uicontroller;
    }

    @Override
    public void setState(int state) {
        if (state == TvPlayerState.BUFFERING) {
            //缓冲中
            showBuffer();
        } else if (state == TvPlayerState.PLAYING) {
            //播放中
            //throw new RuntimeException("init哪里隐藏了loading");
            hideBuffer();
        }
    }
    @Override
    public void showBuffer() {
        mLoadingLayout.setVisibility(View.VISIBLE);
    }
    private void hideBuffer() {
        mLoadingLayout.setVisibility(View.GONE);
    }
}
