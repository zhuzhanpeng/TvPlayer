package com.onairm.tvbaselibrary.player;

/**
 * Created by edison on 2018/3/23.
 */

interface PlayCallBackListener {
    void notifyController(int state);

    void playComplete();
}
