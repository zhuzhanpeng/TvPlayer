package com.onairm.tvbaselibrary.player;

import android.content.Context;

/**
 * NoNewPlayer+BasicUiController的组合
 */

public class BasicNoNewPlayerView extends AbstractTvPlayerView {
    /**
     * @param context
     */
    public BasicNoNewPlayerView(Context context) {
        super(context);
    }

    @Override
    protected UiController createUi() {
        return new BasicUiController(getContext());
    }

    @Override
    protected TvPlayer createPlayer() {
        return new NoNewTvPlayer(BasicNoNewPlayerView.this);
    }
}
