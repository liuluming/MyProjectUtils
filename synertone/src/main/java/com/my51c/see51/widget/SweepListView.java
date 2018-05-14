package com.my51c.see51.widget;
//bm-add-1101


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

public class SweepListView extends ListView {

    private final static int TAN = 2;
    private final static int INIT_STATE = 0;
    private final static int HORIZONTAL_STATE = 1;
    private final static int VERTICAL_STATE = 2;
    private final static int NO_VIEW_SCROLLED = 3;
    private final static int CURRENT_VIEW_SCROLLED = 4;
    private final static int LAST_VIEW_SCROLLED = 5;
    private SweepLayout mDownView;
    private SweepLayout mLastDownView;
    private int mDownX, mDownY;
    private boolean mInterceptlEvent = false;
    private int mSlop;
    private int mState = INIT_STATE;
    private int mScrolledState = NO_VIEW_SCROLLED;
    private boolean mIsListViewCanPull = true;

    private int mScrollState = 0;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    private VelocityTracker mVelocityTracker;

    private int mFirstVisiblePosition;

//	private View mHeaderView;

    @SuppressLint("Recycle")
    public SweepListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        ViewConfiguration vc = ViewConfiguration.get(context);
        mSlop = vc.getScaledTouchSlop();

        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

        mVelocityTracker = VelocityTracker.obtain();

        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                mScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                mFirstVisiblePosition = firstVisibleItem;
            }

        });
    }

    public boolean canPull() {
        return (mFirstVisiblePosition == 0) && mIsListViewCanPull;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if(mPull) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mDownView = (SweepLayout) getPointTouchView(mDownX, mDownY);
                mState = INIT_STATE;
                mScrolledState = NO_VIEW_SCROLLED;
                if (mDownView != null) {
                    if (mDownView.getScrollState()) {
                        mScrolledState = CURRENT_VIEW_SCROLLED;
                    }
                }
                if (mDownView != mLastDownView) {
                    if (mLastDownView != null) {
                        if (mLastDownView.getScrollState()) {
                            mScrolledState = LAST_VIEW_SCROLLED;
                            mLastDownView.shrik(100);
                            mDownView = null;
                        }
                    }
                }
                if (mState == INIT_STATE)
                    mInterceptlEvent = false;
                mVelocityTracker.addMovement(event);
                if (mScrollState == OnScrollListener.SCROLL_STATE_FLING) {
                    return super.onTouchEvent(event);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mScrolledState == LAST_VIEW_SCROLLED || mScrolledState == CURRENT_VIEW_SCROLLED)
                    mInterceptlEvent = true;
                if (mDownView == null)
                    break;
                if (mScrollState == OnScrollListener.SCROLL_STATE_FLING)
                    break;
                int deltaX = (int) event.getX() - mDownX;
                int deltaY = (int) event.getY() - mDownY;
                if (Math.abs(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) >= Math.pow(mSlop, 2) && (float) Math.abs(deltaX) / Math.abs(deltaY) > TAN) {
                    mState = HORIZONTAL_STATE;
                    mInterceptlEvent = true;
                    mIsListViewCanPull = false;
                } else if (Math.abs(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) >= Math.pow(mSlop, 2) && (float) Math.abs(deltaX) / Math.abs(deltaY) <= TAN) {
                    if (mState != HORIZONTAL_STATE)
                        mState = VERTICAL_STATE;
                }
                if (mState != HORIZONTAL_STATE)
                    break;
                int distance = 0;
                if (mScrolledState == CURRENT_VIEW_SCROLLED) {
                    distance = mDownView.getHolderWidth() - deltaX;
                } else {
                    distance = 0 - deltaX;
                }
                if (distance > mDownView.getHolderWidth())
                    distance = mDownView.getHolderWidth();
                if (distance < 0)
                    distance = 0;
                mVelocityTracker.addMovement(event);
                mDownView.scrollTo(distance, 0);
                mDownView.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                boolean showSlide = false;
                showSlide = mDownView != null && mDownView.getScrollX() > 0.3 * mDownView.getHolderWidth();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float velocityY = mVelocityTracker.getYVelocity();
                if (mMinFlingVelocity <= Math.abs(velocityX) && Math.abs(velocityX) <= mMaxFlingVelocity && Math.abs(velocityY) < Math.abs(velocityX)) {
                    showSlide = velocityX <= 0;
                }
                if (mScrolledState == CURRENT_VIEW_SCROLLED && mState != HORIZONTAL_STATE) {
                    mInterceptlEvent = true;
                    showSlide = false;
                }
                if (mDownView != null && showSlide) {
                    mDownView.showSlide(100);
                } else if (mDownView != null) {
                    mDownView.shrik(100);
                }
                if (mScrolledState == LAST_VIEW_SCROLLED) {
                    mInterceptlEvent = true;
                }
                mLastDownView = mDownView;
                mIsListViewCanPull = mDownView == null || !mDownView.getScrollState();
                mDownView = null;
                mVelocityTracker.clear();
                break;
        }
        if (mInterceptlEvent) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private View getPointTouchView(int x, int y) {
        Rect rect = new Rect();
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);
            view.getHitRect(rect);
            if (rect.contains(x, y)) {
                return view;
            }
        }
        return null;
    }

    public void closeView() {
        mDownView.shrik(100);
    }
}
