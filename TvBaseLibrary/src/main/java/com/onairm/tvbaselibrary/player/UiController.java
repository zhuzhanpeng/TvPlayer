package com.onairm.tvbaselibrary.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;


import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Edison on 2018/3/5.
 */
//这种其实就是工厂方法
abstract class UiController extends RelativeLayout {
    protected IjkMediaPlayer player;
    public UiController(Context context) {
        this(context,null,0);
    }

    public UiController(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UiController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    protected void init(){
        View.inflate(getContext(), getLayout(),this);
    }
    protected abstract int getLayout();
    public abstract void setState(int state);
    public void setPlayer(IjkMediaPlayer player) {
        this.player = player;
        //可以做一些事情
    }

    public abstract void showBuffer();
}
