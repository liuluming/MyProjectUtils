package com.my51c.see51.yzxvoip;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by snt1179 on 2016/11/26.
 */
public class VoipUtils {

    public static int currVolume;
    private Context mContext;

    public VoipUtils(Context context) {
        mContext = context;
    }

    //打开扬声器
    public void OpenSpeaker() {
        try {
            /*int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL );
int current = mAudioManager.getStreamVolume( AudioManager.STREAM_VOICE_CALL );
Log.d(“VIOCE_CALL”, “max : ” + max + ” current : ” + current);*/
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(context,"揚聲器已經關閉",Toast.LENGTH_SHORT).show();
    }
}
