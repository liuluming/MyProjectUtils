package com.my51c.see51.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

import com.my51c.see51.ui.SDCalendarActivity;

import java.util.Calendar;

/**
 * 日历控件单元格绘制类
 *
 * @Description: 日历控件单元格绘制类
 * @FileName: DateWidgetDayCell.java
 * @Package com.goscam.ulife.ui.widget
 * @Author HuangTiebing
 * @Date 2014-6-05 下午03:19:34
 * @Version V1.0
 */
public class DateWidgetDayCell extends View {
    // 字体大小
    private static final int fTextSize = 28;
    public static int ANIM_ALPHA_DURATION = 100;
    // 基本元素
    private OnItemClick itemClick = null;
    private Paint pt = new Paint();
    private RectF rect = new RectF();
    private String sDate = "";
    // 当前日期
    private int iDateYear = 0;
    private int iDateMonth = 0;
    private int iDateDay = 0;
    // 布尔变量
    private boolean bSelected = false;
    private boolean bIsActiveMonth = false;
    private boolean bToday = false;
    private boolean bTouchedDown = false;
    private boolean bHoliday = false;
    private boolean hasRecord = false;
    private int eventCount = 0;
    private boolean bAllClick = false;

    // 构�?函数
    public DateWidgetDayCell(Context context, int iWidth, int iHeight) {
        super(context);
        setFocusable(true);
        setLayoutParams(new LayoutParams(iWidth, iHeight));
    }

    // 根据条件返回不同颜色�?
    public static int getColorBkg(boolean bHoliday, boolean bToday) {
        if (bToday)
            return SDCalendarActivity.isToday_BgColor;
        // if (bHoliday) //如需周末有特殊背景色，可去掉注释
        // return Calendar_TestActivity.isHoliday_BgColor;
        return SDCalendarActivity.Calendar_DayBgColor;
    }

    // 不�?明度渐变
    public static void startAlphaAnimIn(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
        anim.setDuration(ANIM_ALPHA_DURATION);
        anim.startNow();
        view.startAnimation(anim);
    }

    /**
     * @return the bAllClick
     */
    public boolean isbAllClick() {
        return bAllClick;
    }

    /**
     * @param bAllClick the bAllClick to set
     */
    public void setbAllClick(boolean bAllClick) {
        this.bAllClick = bAllClick;
    }

    // 取变量�?
    public Calendar getDate() {
        Calendar calDate = Calendar.getInstance();
        calDate.clear();
        calDate.set(Calendar.YEAR, iDateYear);
        calDate.set(Calendar.MONTH, iDateMonth);
        calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
        return calDate;
    }

    // 设置变量�?
    public void setData(int iYear, int iMonth, int iDay, Boolean bToday, Boolean bHoliday, int iActiveMonth, boolean hasRecord) {
        iDateYear = iYear;
        iDateMonth = iMonth;
        iDateDay = iDay;

        this.sDate = Integer.toString(iDateDay);
        this.bIsActiveMonth = (iDateMonth == iActiveMonth);
        this.bToday = bToday;
        this.bHoliday = bHoliday;
        this.hasRecord = hasRecord;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    // 重载绘制方法
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        rect.set(0, 0, this.getWidth(), this.getHeight());
        rect.inset(1, 1);

        final boolean bFocused = IsViewFocused();

        drawDayView(canvas, bFocused);
        drawDayNumber(canvas);
    }

    public boolean IsViewFocused() {
        return (this.isFocused() || bTouchedDown);
    }

    // 绘制日历方格
    private void drawDayView(Canvas canvas, boolean bFocused) {

        if (bSelected || bFocused) {
            LinearGradient lGradBkg = null;

            if (bFocused) {
                lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0, 0xffaa5500, 0xffffddbb, Shader.TileMode.CLAMP);
            }

            if (bSelected) {
                lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0, 0xff225599, 0xffbbddff, Shader.TileMode.CLAMP);
            }

            if (lGradBkg != null) {
                pt.setShader(lGradBkg);
                canvas.drawRect(rect, pt);
            }

            pt.setShader(null);

        } else {
            pt.setColor(getColorBkg(bHoliday, bToday));
            canvas.drawRect(rect, pt);
        }

        if (hasRecord) {
            CreateReminder(canvas, SDCalendarActivity.special_Reminder);
        }
        // else if (!hasRecord && !bToday && !bSelected) {
        // CreateReminder(canvas, Calendar_TestActivity.Calendar_DayBgColor);
        // }
    }

    // 绘制日历中的数字
    public void drawDayNumber(Canvas canvas) {
        // draw day number
        pt.setTypeface(null);
        pt.setAntiAlias(true);
        pt.setShader(null);
        pt.setFakeBoldText(true);
        pt.setTextSize(fTextSize);
        pt.setColor(SDCalendarActivity.isPresentMonth_FontColor);
        pt.setUnderlineText(false);

        if (!bIsActiveMonth)
            pt.setColor(SDCalendarActivity.unPresentMonth_FontColor);

        if (bToday)
            pt.setUnderlineText(true);

        final float iPosX = rect.left + ((int) rect.width() >> 1) - ((int) pt.measureText(sDate) >> 1);

        final float iPosY = (this.getHeight() - (this.getHeight() - getTextHeight()) / 2 - pt.getFontMetrics().bottom);

        canvas.drawText(sDate, iPosX, iPosY, pt);
        //
        if (eventCount > 0) {
            float radius = getHeight() * 3 / 16;// pt.measureText("1000") / 2;
            pt.setTextSize(radius * 2 / 3);
            float cx = rect.left + radius + 5.0f;
            float cy = rect.top + radius + 5.0f;
            pt.setColor(SDCalendarActivity.dayEventNumber_FontColor);
            canvas.drawCircle(cx, cy, radius, pt);
            pt.setColor(0xffffffff);
            canvas.drawText(eventCount + "", cx - pt.measureText(eventCount + "") / 2, cy + radius * 2 / 3 / 2, pt);
        }

        pt.setUnderlineText(false);

    }

    @Override
    public void setClickable(boolean clickable) {
        // TODO Auto-generated method stub
        super.setClickable(clickable);
    }

    /**
     * 绘制日历中事件的数量
     *
     * @param canvas
     */
    public void drawEventNumber(Canvas canvas) {
        // draw day number
        pt.setTypeface(null);
        pt.setAntiAlias(true);
        pt.setShader(null);
        pt.setFakeBoldText(true);
        pt.setTextSize(fTextSize);
        pt.setColor(SDCalendarActivity.isPresentMonth_FontColor);
        pt.setUnderlineText(false);

        if (!bIsActiveMonth)
            pt.setColor(SDCalendarActivity.unPresentMonth_FontColor);

        if (bToday)
            pt.setUnderlineText(true);

        final int iPosX = (int) rect.left + ((int) rect.width() >> 1) - ((int) pt.measureText(sDate) >> 1);

        final int iPosY = (int) (this.getHeight() - (this.getHeight() - getTextHeight()) / 2 - pt.getFontMetrics().bottom);

        canvas.drawText(sDate, iPosX, iPosY, pt);

        pt.setUnderlineText(false);
    }

    // 得到字体高度
    private int getTextHeight() {
        return (int) (-pt.ascent() + pt.descent());
    }

    // 设置是否被�?�?
    @Override
    public void setSelected(boolean bEnable) {
        if (this.bSelected != bEnable) {
            this.bSelected = bEnable;
            this.invalidate();
        }
    }

    public void setItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public void doItemClick() {
        if (itemClick != null)
            itemClick.OnClick(this);
    }

    // 点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (eventCount > 0 || bToday || bAllClick) {
            boolean bHandled = false;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bHandled = true;
                bTouchedDown = true;
                invalidate();
                startAlphaAnimIn(DateWidgetDayCell.this);
            }
            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                bHandled = true;
                bTouchedDown = false;
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                bHandled = true;
                bTouchedDown = false;
                invalidate();
                doItemClick();
            }
            return bHandled;
        }
        return false;
    }

    // 点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bResult = super.onKeyDown(keyCode, event);
        if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
            doItemClick();
        }
        return bResult;
    }

    public void CreateReminder(Canvas canvas, int Color) {
        pt.setStyle(Paint.Style.FILL_AND_STROKE);
        pt.setColor(Color);
        Path path = new Path();
        path.moveTo(rect.right - rect.width() / 4, rect.top);
        path.lineTo(rect.right, rect.top);
        path.lineTo(rect.right, rect.top + rect.width() / 4);
        path.lineTo(rect.right - rect.width() / 4, rect.top);
        path.close();
        canvas.drawPath(path, pt);
    }

    public interface OnItemClick {
        void OnClick(DateWidgetDayCell item);
    }
}