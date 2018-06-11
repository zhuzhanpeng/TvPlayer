package com.onairm.tvbaselibrary.utils;

import android.view.View;

import java.util.Calendar;

public abstract class NoDoubleClickListener implements View.OnClickListener {
    private int mCurrentId = -1;
    public static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if ((id == mCurrentId) &&
                (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME)) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }else if(id != mCurrentId){
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
        mCurrentId = id;
    }

    public abstract void onNoDoubleClick(View v);
}