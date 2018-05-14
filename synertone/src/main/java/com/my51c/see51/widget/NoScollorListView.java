package com.my51c.see51.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class NoScollorListView extends ListView {

	private boolean haveScrollbar = false;
	private long lastClickTime = 0;

	public NoScollorListView(Context context) {
		this(context, null);
	}

	public NoScollorListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NoScollorListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (Build.VERSION.SDK_INT >= 9) {
			this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	/**
	 * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
	 * 
	 * @param haveScrollbar
	 */
	public void setHaveScrollbar(boolean haveScrollbar) {
		this.haveScrollbar = haveScrollbar;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!haveScrollbar) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (isFastDoubleClick()) {
				return true;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 判断用户是否快速连续点击
	 * 
	 * @return
	 */
	private boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (timeD >= 0 && timeD <= 500) {
			return true;
		} else {
			lastClickTime = time;
			return false;
		}
	}

}
