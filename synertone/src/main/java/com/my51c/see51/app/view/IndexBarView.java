// @author Bhavya Mehta
package com.my51c.see51.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.my51c.see51.app.voipphone.IIndexBarFilter;
import com.synertone.netAssistant.R;

import java.util.ArrayList;


// Represents right side index bar view with unique first latter of list view row text 
public class IndexBarView extends View {

    // array list to store section positions
    public ArrayList<Integer> mListSections;
    String[] b = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;
    // index bar margin
    float mIndexbarMargin;
    // user touched Y axis coordinate value
    float mSideIndexY;
    // flag used in touch events manipulations
    boolean mIsIndexing = false;
    // holds current section position selected by user
    int mCurrentSectionPosition = -1;
    // array list to store listView data
    ArrayList<String> mListItems;

    // paint object
    Paint mIndexPaint;

    // context object
    Context mContext;

    // interface object used as bridge between list view and index bar view for
    // filtering list view content on touch event
    IIndexBarFilter mIndexBarFilter;


    public IndexBarView(Context context) {
        super(context);
        this.mContext = context;
    }


    public IndexBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public IndexBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }


    public void setData(PinnedHeaderListView listView, ArrayList<String> listItems, ArrayList<Integer> listSections) {
        this.mListItems = listItems;
        this.mListSections = listSections;

        // list view implements mIndexBarFilter interface
        mIndexBarFilter = listView;

        // set index bar margin from resources
        mIndexbarMargin = mContext.getResources().getDimension(R.dimen.index_bar_view_margin);

        // index bar item color and text size
        mIndexPaint = new Paint();
        mIndexPaint.setColor(mContext.getResources().getColor(R.color.color_black));
        mIndexPaint.setAntiAlias(true);
        mIndexPaint.setTextSize(mContext.getResources().getDimension(R.dimen.index_bar_view_text_size));
    }


    // draw view content on canvas using paint
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#40000000"));
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }
    }


    public String getSectionText(int sectionPosition) {
        return mListItems.get(sectionPosition);
    }


    boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the
        // right margin of the bar
        return (x >= getLeft() && y >= getTop() && y <= getTop() + getMeasuredHeight());
    }


    void filterListItem(float sideIndexY) {
        mSideIndexY = sideIndexY;

        // filter list items and get touched section position with in index bar
        mCurrentSectionPosition = (int) (((mSideIndexY) - getTop() - mIndexbarMargin) /
                ((getMeasuredHeight() - (2 * mIndexbarMargin)) / mListSections.size()));

        if (mCurrentSectionPosition >= 0 && mCurrentSectionPosition < mListSections.size()) {
            int position = mListSections.get(mCurrentSectionPosition);
            String previewText = mListItems.get(position);
            mIndexBarFilter.filterList(mSideIndexY, position, previewText);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing
                if (contains(ev.getX(), ev.getY())) {
                    // It demonstrates that the motion event started from index
                    // bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the
                    // list to
                    // that section
                    filterListItem(ev.getY());
                    return true;
                } else {
                    mCurrentSectionPosition = -1;
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY())) {
                        // Determine which section the point is in, and move the
                        // list to that section
                        filterListItem(ev.getY());
                        return true;
                    } else {
                        mCurrentSectionPosition = -1;
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSectionPosition = -1;
                }
                break;
        }
        return false;
    }
}
