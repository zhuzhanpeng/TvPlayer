package com.onairm.tvbaselibrary.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Edison on 2018/3/19.
 */

class TvPlayerState {
    public static final int PREPARED = 1;
    public static final int COMPLETE = 2;
    public static final int BUFFERING = 3;
    public static final int PLAYING = 4;
    public static final int PAUSED = 5;
    public static final int ERROR = 6;
    public static final int RELEASE = 7;

    @IntDef({PREPARED, COMPLETE, BUFFERING, PLAYING, PAUSED, ERROR, RELEASE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerState {
    }
}
