package com.onairm.tvbaselibrary.view;

import android.widget.TextView;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;


/**
 * @author jixiaolong<microjixl@gmail.com>
 */
public class BulletinView extends TextView implements Runnable{
	private int currentScrollX;// 当前滚动的位置
	private boolean isStop = false;
	private int textWidth;
	private final int REPEAT = 1;
	private int repeatCount = 0;
	private int currentNews = 0;

	public BulletinView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	public BulletinView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public BulletinView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init(){
		setClickable(true);
		setSingleLine(true);
		setEllipsize(TruncateAt.MARQUEE);
		setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
	}


	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		MeasureTextWidth();
	}
	public boolean isMarquee(){
		boolean result=false;
		if(textWidth>getWidth()){
			result=true;
		}
		return result;
	}
	@Override
	public void onScreenStateChanged(int screenState) {
		super.onScreenStateChanged(screenState);
		if(screenState == SCREEN_STATE_ON){
			startScroll();
		}else{
			stopScroll();
		}
	}

	/**
	 * 获取文字宽度
	 */
	private void MeasureTextWidth() {
		Paint paint = this.getPaint();
		String str = this.getText().toString();
		textWidth = (int) paint.measureText(str);
	}

	@Override
	public void run() {

		currentScrollX += 1;// 滚动速度
		scrollTo(currentScrollX, 0);
		if (isStop) {
			return;
		}
		if (getScrollX() >= textWidth) {
			currentScrollX = -getWidth();
			scrollTo(currentScrollX, 0);
		}

		postDelayed(this, 50);
	}


	// 开始滚动
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}
	// 停止滚动
	public void stopScroll() {
		isStop = true;
	}
}
