package com.my51c.see51.widget;

import android.view.View;
import android.view.animation.RotateAnimation;

public class RoatAnimUtil {
    //�����Ķ��������ӳ�ʱ��
    public static void startAnimationOut(View view) {
        startAnimationOut(view, 0);

    }

    //����Ķ��������ӳ�ʱ��
    public static void startAnimationIn(View view) {
        startAnimationIn(view, 0);
    }

    //�����Ķ���
    //delayΪ�����ӳٵ�ʱ�䣬��λ�Ǻ���
    public static void startAnimationOut(View view, long delay) {
        RotateAnimation animation = new RotateAnimation(240, 180,
                view.getWidth() / 2, view.getHeight() / 2);
        animation.setDuration(200);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        view.startAnimation(animation);

    }

    //����Ķ���
    //delayΪ�����ӳٵ�ʱ�䣬��λ�Ǻ���
    public static void startAnimationIn(View view, long delay) {
        RotateAnimation animation = new RotateAnimation(180, 240,
                view.getWidth() / 2, view.getHeight() / 2);
        animation.setDuration(200);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

}