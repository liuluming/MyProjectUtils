package com.my51c.see51.widget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.DisplayMetrics;
//import android.view.Display;


public class LocUtil {


    /**
     * 鑾峰彇鏈�澶ч煶閲�
     *
     * @param context
     * @return
     */
    public static int getMaxVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 鑾峰彇褰撳墠闊抽噺
     *
     * @param context
     * @return
     */
    public static int getCurVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 璁剧疆褰撳墠闊抽噺
     *
     * @param context
     * @param index
     */
    public static void setCurVolume(Context context, int index) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }

    /**
     * 鑾峰彇灞忓箷淇℃伅
     *
     * @param context
     * @return
     */
    public static ScreenBean getScreenPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return new ScreenBean(dm.widthPixels, dm.heightPixels);

//		Display display = ((VideoPlayerActivity) context).getWindowManager().getDefaultDisplay();
//		return new ScreenBean(display.getWidth(),display.getHeight());
    }
}
