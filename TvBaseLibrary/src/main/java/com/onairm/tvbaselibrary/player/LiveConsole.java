package com.onairm.tvbaselibrary.player;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.onairm.tvbaselibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edison on 2017/5/23.
 */
class LiveConsole extends FrameLayout {
    private ImageView btnPause, btnSwitchChapter;
    private TextView tvVideoTitle;
    private List<View> viewsList = new ArrayList<>();
    private MySeekBar mySeekBar;

    public LiveConsole(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setType();
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.layout_live_console, this);
        mySeekBar = (MySeekBar) findViewById(R.id.progressSeekView);
        btnPause = (ImageView) findViewById(R.id.btnPause);
        btnSwitchChapter = (ImageView) findViewById(R.id.btnSwitchChapter);
        tvVideoTitle = (TextView) findViewById(R.id.tvVideoTitle);

        viewsList.add(btnPause);
        viewsList.add(btnSwitchChapter);
    }

    public void setType() {
        btnPause.setNextFocusUpId(R.id.progressSeekView);
        mySeekBar.setNextFocusUpId(R.id.progressSeekView);
        mySeekBar.setNextFocusDownId(R.id.btnPause);
        btnSwitchChapter.setNextFocusUpId(R.id.progressSeekView);
    }

    public void pauseRequestFocus() {
        btnPause.requestFocus();
    }

    public void setOnClickListeners(OnClickListener onClickListener) {
        if (null != onClickListener) {
            int length = viewsList.size();
            for (int i = 0; i < length; i++) {
                viewsList.get(i).setOnClickListener(onClickListener);
            }
        }
    }

    public MySeekBar getSeekBar() {
        return mySeekBar;
    }

    public void pauseImgSwitch(boolean play) {
        if (play) {
            btnPause.setImageResource(R.drawable.pause_selector);

        } else {
            btnPause.setImageResource(R.drawable.play_selector);

        }
    }

    public void setShowText(int progress, String showText, int maxProgress) {
        mySeekBar.update(progress, showText, maxProgress);
    }

    public void initSeekBar(String showText,int maxProgress) {
        mySeekBar.init(showText,maxProgress);
    }

    public void setTitle(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        tvVideoTitle.setText(text);
    }
}