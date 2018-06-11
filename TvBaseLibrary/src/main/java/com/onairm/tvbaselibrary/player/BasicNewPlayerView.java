package com.onairm.tvbaselibrary.player;

import android.content.Context;

/**
 * NewPlayer+BasicUiController的组合
 */

public class BasicNewPlayerView extends AbstractTvPlayerView {
    /**
     * @param context
     */
    public BasicNewPlayerView(Context context) {
        super(context);
    }

    @Override
    protected UiController createUi() {
        return new BasicUiController(getContext());
    }

    @Override
    protected TvPlayer createPlayer() {
        return new NewTvPlayer(BasicNewPlayerView.this);
    }
}
