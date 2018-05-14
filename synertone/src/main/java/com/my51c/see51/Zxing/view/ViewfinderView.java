/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.my51c.see51.Zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.my51c.see51.Zxing.camera.CameraManager;
import com.synertone.netAssistant.R;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
    private static final String TAG = "log";
    /**
     * 閸掗攱鏌婇悾宀勬桨閻ㄥ嫭妞傞梻锟�
     */
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;
    /**
     * 閸ユ稐閲滅紒鑳鏉堢顬戠�电懓绨查惃鍕啍鎼达拷
     */
    private static final int CORNER_WIDTH = 10;
    /**
     * 閹殿偅寮垮鍡曡厬閻ㄥ嫪鑵戦梻瀵稿殠閻ㄥ嫬顔旀惔锟�
     */
    private static final int MIDDLE_LINE_WIDTH = 6;
    /**
     * 閹殿偅寮垮鍡曡厬閻ㄥ嫪鑵戦梻瀵稿殠閻ㄥ嫪绗岄幍顐ｅ伎濡楀棗涔忛崣宕囨畱闂傛挳娈�
     */
    private static final int MIDDLE_LINE_PADDING = 5;
    /**
     * 娑擃參妫块柇锝嗘蒋缁炬寧鐦″▎鈥冲煕閺傛壆些閸斻劎娈戠捄婵堫湩
     */
    private static final int SPEEN_DISTANCE = 5;
    /**
     * 鐎涙ぞ缍嬫径褍鐨�
     */
    private static final int TEXT_SIZE = 16;
    /**
     * 鐎涙ぞ缍嬬捄婵堫湩閹殿偅寮垮鍡曠瑓闂堛垻娈戠捄婵堫湩
     */
    private static final int TEXT_PADDING_TOP = 30;
    /**
     * 閹靛婧�閻ㄥ嫬鐫嗛獮鏇炵槕鎼达拷
     */
    private static float density;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    boolean isFirst;
    /**
     * 閸ユ稐閲滅紒鑳鏉堢顬戠�电懓绨查惃鍕毐鎼达拷
     */
    private int ScreenRate;
    /**
     * 閻㈣崵鐟�电钖勯惃鍕穿閻拷
     */
    private Paint paint;
    /**
     * 娑擃參妫垮鎴濆З缁捐法娈戦張锟姐�婄粩顖欑秴缂冿拷
     */
    private int slideTop;
    /**
     * 娑擃參妫垮鎴濆З缁捐法娈戦張锟界俺缁旑垯缍呯純锟�
     */
    private int slideBottom;
    /**
     * 鐏忓棙澹傞幓蹇曟畱娴滃瞼娣惍浣瑰娑撳娼甸敍宀冪箹闁插本鐥呴張澶庣箹娑擃亜濮涢懗鏂ょ礉閺嗗倹妞傛稉宥堬拷閾忥拷
     */
    private Bitmap resultBitmap;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;
        //鐏忓棗鍎氱槐鐘烘祮閹广垺鍨歞p
        ScreenRate = (int) (20 * density);

        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);

        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        //閸掓繂顬婇崠鏍﹁厬闂傚鍤庡鎴濆З閻ㄥ嫭娓舵稉濠呯珶閸滃本娓舵稉瀣珶
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            //閻㈢粯澹傞幓蹇旑攱鏉堥�涚瑐閻ㄥ嫯顬戦敍灞撅拷閸忥拷娑擃亪鍎撮崚锟�
            paint.setColor(Color.GREEN);
            canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
                    frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
                    + ScreenRate, paint);
            canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
                    frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
                    + ScreenRate, paint);
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
                    + ScreenRate, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - ScreenRate,
                    frame.left + CORNER_WIDTH, frame.bottom, paint);
            canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
                    frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
                    frame.right, frame.bottom, paint);


            //缂佹ê鍩楁稉顓㈡？閻ㄥ嫮鍤�,濮ｅ繑顐奸崚閿嬫煀閻ｅ矂娼伴敍灞艰厬闂傚娈戠痪鍨窔娑撳些閸斺娍PEEN_DISTANCE
            slideTop += SPEEN_DISTANCE;
            if (slideTop >= frame.bottom) {
                slideTop = frame.top;
            }
            canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH / 2, frame.right - MIDDLE_LINE_PADDING, slideTop + MIDDLE_LINE_WIDTH / 2, paint);


            //閻㈢粯澹傞幓蹇旑攱娑撳娼伴惃鍕摟
            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setAlpha(0x40);
            paint.setTypeface(Typeface.create("System", Typeface.BOLD));
            //canvas.drawText(getResources().getString(R.string.scan_text), frame.left, (float) (frame.bottom + (float)TEXT_PADDING_TOP *density), paint);


            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }


            //閸欘亜鍩涢弬鐗堝閹诲繑顢嬮惃鍕敶鐎圭櫢绱濋崗鏈电铂閸︾増鏌熸稉宥呭煕閺傦拷
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);

        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
