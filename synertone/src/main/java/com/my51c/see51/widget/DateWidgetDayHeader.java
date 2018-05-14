package com.my51c.see51.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.my51c.see51.ui.SDCalendarActivity;
import com.synertone.netAssistant.R;

import java.util.Calendar;

/**
 * 日历控件头部绘制�?
 *
 * @Descriptio.n: 日历控件头部绘制�?
 * @FileName: DateWidgetDayHeader.java
 * @Package com.calendar.demo
 * @Author HuangTiebing
 * @Date 2012-3-19 下午03:28:39
 * @Version V1.0
 */
public class DateWidgetDayHeader extends View {
    // 字体大小
    private final static int fTextSize = 22;
    private final String[] vecStrWeekDayNames;
    private Paint pt = new Paint();
    private RectF rect = new RectF();
    private int iWeekDay = -1;
    private Context mContext;

    public DateWidgetDayHeader(Context context, int iWidth, int iHeight) {
        super(context);
        this.mContext = context;
        setLayoutParams(new LayoutParams(iWidth, iHeight));
        vecStrWeekDayNames = getWeekDayNames();
    }


    private String[] getWeekDayNames() {
        String[] vec = new String[10];
        vec[Calendar.SUNDAY] = mContext.getResources().getString(R.string.Sunday);
        vec[Calendar.MONDAY] = mContext.getResources().getString(R.string.Monday);
        vec[Calendar.TUESDAY] = mContext.getResources().getString(R.string.Tuesday);
        vec[Calendar.WEDNESDAY] = mContext.getResources().getString(R.string.Wednesday);
        vec[Calendar.THURSDAY] = mContext.getResources().getString(R.string.Thursday);
        vec[Calendar.FRIDAY] = mContext.getResources().getString(R.string.Friday);
        vec[Calendar.SATURDAY] = mContext.getResources().getString(R.string.Saturday);
        return vec;
    }

    public String getWeekDayName(int iDay) {
        return vecStrWeekDayNames[iDay];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置矩形大小
        rect.set(0, 0, this.getWidth(), this.getHeight());
        rect.inset(1, 1);

        // 绘制日历头部
        drawDayHeader(canvas);
    }

    private void drawDayHeader(Canvas canvas) {
        // 画矩形，并设置矩形画笔的颜色
        pt.setColor(SDCalendarActivity.Calendar_WeekBgColor);
        canvas.drawRect(rect, pt);

        // 写入日历头部，设置画笔参�?
        pt.setTypeface(null);
        pt.setTextSize(fTextSize);
        pt.setAntiAlias(true);
        pt.setFakeBoldText(true);
        pt.setColor(SDCalendarActivity.Calendar_WeekFontColor);

        // draw day name
        final String sDayName = getWeekDayName(iWeekDay);
        final int iPosX = (int) rect.left + ((int) rect.width() >> 1)
                - ((int) pt.measureText(sDayName) >> 1);
        final int iPosY = (int) (this.getHeight()
                - (this.getHeight() - getTextHeight()) / 2 - pt
                .getFontMetrics().bottom);
        canvas.drawText(sDayName, iPosX, iPosY, pt);
    }

    // 得到字体高度
    private int getTextHeight() {
        return (int) (-pt.ascent() + pt.descent());
    }

    // 得到�?��期的第几天的文本标记
    public void setData(int iWeekDay) {
        this.iWeekDay = iWeekDay;
    }
}