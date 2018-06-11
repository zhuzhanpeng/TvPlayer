package com.onairm.tvbaselibrary.giftext;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Edison on 2018/3/7.
 */

public class GifTextView extends android.support.v7.widget.AppCompatTextView {
    private Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    private FaceManager fm;

    public GifTextView(Context context) {
        this(context,null,0);
    }

    public GifTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GifTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        fm = FaceManager.getInstance();
    }
    public void setGifText(String text){
        setText(convertNormalStringToSpannableString(text,this));
    }
    private SpannableString convertNormalStringToSpannableString(String message , final TextView tv) {
        SpannableString value = SpannableString.valueOf(message);
        Matcher localMatcher = EMOTION_URL.matcher(value);
        while (localMatcher.find()) {
            String str2 = localMatcher.group(0);
            int k = localMatcher.start();
            int m = localMatcher.end();
            if (m - k < 8) {
                int face = fm.getFaceId(str2);
                if(-1!=face){//wrapping with weakReference
                    WeakReference<AnimatedImageSpan> localImageSpanRef = new WeakReference<AnimatedImageSpan>(new AnimatedImageSpan(new AnimatedGifDrawable(getContext().getResources().openRawResource(face), new AnimatedGifDrawable.UpdateListener() {
                        @Override
                        public void update() {//update the textview
                            tv.postInvalidate();
                        }
                    })));
                    value.setSpan(localImageSpanRef.get(), k, m, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
        return value;
    }
}
