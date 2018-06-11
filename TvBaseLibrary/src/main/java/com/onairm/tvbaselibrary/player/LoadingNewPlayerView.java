package com.onairm.tvbaselibrary.player;

import android.content.Context;

/**
 * Created by edison on 2018/3/23.
 */

public class LoadingNewPlayerView extends AbstractTvPlayerView {
    /**
     * @param context
     */
    public LoadingNewPlayerView(Context context) {
        super(context);
    }

    @Override
    protected UiController createUi() {
        return new LoadingUiController(getContext());
    }

    @Override
    protected TvPlayer createPlayer() {
        return new NewTvPlayer(LoadingNewPlayerView.this);
    }
}
