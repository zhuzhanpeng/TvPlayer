package com.onairm.tvbaselibrary.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onairm.tvbaselibrary.R;


class MySeekBar extends android.support.v7.widget.AppCompatSeekBar {
    private OnKeySeekBarChangeListener mOnKeySeekBarChangeListener;

    /**
     * 文本的颜色
     */
    private int mTitleTextColor;
    /**
     * 文本的大小
     */
    private float mTitleTextSize;
    private String mTitleText="";//文字的内容
    private int myProgress;
    private int maxProgress;

    /**
     * 背景图片
     */
    private int img;
    private Bitmap bitmapCenter;
    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;

    //bitmap对应的宽高
    private float img_width, img_height;
    Paint paint;

    private float numTextWidth;
    //测量seekbar的规格
    private Rect rect_seek;
    private Paint.FontMetrics fm;

    public static final int TEXT_ALIGN_LEFT = 0x00000001;
    public static final int TEXT_ALIGN_RIGHT = 0x00000010;
    public static final int TEXT_ALIGN_CENTER_VERTICAL = 0x00000100;
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;
    public static final int TEXT_ALIGN_TOP = 0x00010000;
    public static final int TEXT_ALIGN_BOTTOM = 0x00100000;
    /**
     * 文本中轴线X坐标
     */
    private float textCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float textBaselineY;
    /**
     * 文字的方位
     */
    private int textAlign;

    public MySeekBar(Context context) {
        this(context, null);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       /* TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MySeekBar, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.MySeekBar_textsize:
                    mTitleTextSize = array.getDimension(attr, 15f);
                    break;
                case R.styleable.MySeekBar_textcolor:
                    mTitleTextColor = array.getColor(attr, Color.WHITE);
                    break;
            }
        }
        array.recycle();*/
        mTitleTextSize=24;
        mTitleTextColor= Color.WHITE;
        getImgWH();
        paint = new Paint();
        paint.setAntiAlias(true);//设置抗锯齿
        paint.setTextSize(img_height/2-4);//设置文字大小
        paint.setColor(mTitleTextColor);//设置文字颜色
        //设置控件的padding 给提示文字留出位置
//        setPadding((int) Math.ceil(img_width) / 2, (int) Math.ceil(img_height) + 10, (int) Math.ceil(img_height) / 2, 0);
        setPadding(0, (int) Math.ceil(img_height)+10, 0, 0);
        textAlign = TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL;
    }

    /**
     * 获取图片的宽高
     */
    private void getImgWH() {
        bitmapLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_left_pop);
        bitmapCenter = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_pop_center);
        bitmapRight = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_pop_right);
        img_width = bitmapCenter.getWidth();
        img_height = bitmapCenter.getHeight();


    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(hasFocus()){
            if(0==maxProgress){
                return;
            }
            if(TextUtils.isEmpty(mTitleText)){
                return;
            }
            setTextLocation();//定位文本绘制的位置
            if(null==rect_seek){
                rect_seek = this.getProgressDrawable().getBounds();
            }
            //定位文字背景图片的位置
            float bm_x = rect_seek.width() * myProgress / maxProgress;
            float bm_y = rect_seek.height();
//        //计算文字的中心位置在bitmap
            float text_x = rect_seek.width() * myProgress / maxProgress + img_width / 2 - numTextWidth / 2;
            float deltaX = 0;
            Bitmap destBitmap=null;
            if (bm_x < img_width / 2) {
                deltaX = 0;
                destBitmap=bitmapLeft;
            } else if (rect_seek.width() - bm_x < img_width / 2) {
                deltaX = -img_width;
                destBitmap=bitmapRight;
            } else {
                deltaX = -img_width / 2;
                destBitmap=bitmapCenter;
            }
            canvas.drawBitmap(destBitmap, bm_x + deltaX, bm_y, paint);//画背景图
            canvas.drawText(mTitleText, text_x + deltaX, (float) (textBaselineY+ bm_y), paint);//画文字
            //canvas.drawText(mTitleText, text_x + deltaX, (float) (textBaselineY /*+ bm_y + (0.16 * img_height / 2)*/), paint);//画文字
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();//监听手势滑动，不断重绘文字和背景图的显示位置
        return super.onTouchEvent(event);
    }

    /**
     * 定位文本绘制的位置
     */
    private void setTextLocation() {

        fm = paint.getFontMetrics();
        //文本的宽度
//        mTitleText = getProgress() + 10 + "℃";

        numTextWidth = paint.measureText(mTitleText);

        float textCenterVerticalBaselineY = img_height / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
        switch (textAlign) {
            case TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = img_width / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_LEFT | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = numTextWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_RIGHT | TEXT_ALIGN_CENTER_VERTICAL:
                textCenterX = img_width - numTextWidth / 2;
                textBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_CENTER_HORIZONTAL:
                textCenterX = img_width / 2;
                textBaselineY = img_height - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL:
                textCenterX = img_width / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_LEFT:
                textCenterX = numTextWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_LEFT:
                textCenterX = numTextWidth / 2;
                textBaselineY = img_height - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_RIGHT:
                textCenterX = img_width - numTextWidth / 2;
                textBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_RIGHT:
                textCenterX = img_width - numTextWidth / 2;
                textBaselineY = img_height - fm.bottom;
                break;
        }
    }
    public void setmOnKeySeekBarChangeListener(OnKeySeekBarChangeListener onKeySeekBarChangeListener) {
        this.mOnKeySeekBarChangeListener = onKeySeekBarChangeListener;
    }

    public void init(String showText,int maxProgress) {
        this.mTitleText=showText;
        this.maxProgress=maxProgress;
    }

    public interface OnKeySeekBarChangeListener {
        void onKeyStartTrackingTouch();

        void onKeyStopTrackingTouch();
    }

    void onKeyChange() {
        if (null != mOnKeySeekBarChangeListener) {
            mOnKeySeekBarChangeListener.onKeyStartTrackingTouch();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (null != mOnKeySeekBarChangeListener) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mOnKeySeekBarChangeListener.onKeyStopTrackingTouch();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void update(int progress,String mTitleText,int maxProgress){
        this.mTitleText=mTitleText;
        this.myProgress=progress;
        this.maxProgress=maxProgress;
        invalidate();
    }
}