package com.my51c.see51.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.synertone.netAssistant.R;

/**
 * �е��Ե�ScrollView
 * ʵ���������غ���������
 *
 * @author zhangjg
 * @date Feb 13, 2014 6:11:33 PM
 */
public class ReboundScrollView extends ScrollView {

    private static final String TAG = "ElasticScrollView";

    //�ƶ�����, ��һ���ٷֱ�, ������ָ�ƶ���100px, ��ôView��ֻ�ƶ�50px
    //Ŀ���Ǵﵽһ���ӳٵ�Ч��
    private static final float MOVE_FACTOR = 0.5f;

    //�ɿ���ָ��, ����ص�����λ����Ҫ�Ķ���ʱ��
    private static final int ANIM_TIME = 300;

    //ScrollView����View�� Ҳ��ScrollView��Ψһһ����View
    private View contentView;

    //��ָ����ʱ��Yֵ, �������ƶ�ʱ�����ƶ�����
    //�������ʱ���������������� ������ָ�ƶ�ʱ����Ϊ��ǰ��ָ��Yֵ
    private float startY;

    //���ڼ�¼�����Ĳ���λ��
    private Rect originalRect = new Rect();

    //��ָ����ʱ��¼�Ƿ���Լ�������
    private boolean canPullDown = false;

    //��ָ����ʱ��¼�Ƿ���Լ�������
    private boolean canPullUp = false;

    //����ָ�����Ĺ����м�¼�Ƿ��ƶ��˲���
    private boolean isMoved = false;

    private boolean isPullDownPermision = true;


    public ReboundScrollView(Context context) {
        super(context);
    }

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ReboundScrollView);
        isPullDownPermision = ta.getBoolean(R.styleable.ReboundScrollView_isPullDownPermisioon, true);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {//������xml��ִ�и÷���
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        }
    }

    /*l,t,r,b�ֱ��Ƿ��ø��ؼ��ľ��ο��ÿռ䣨��ȥmargin��padding�Ŀռ䣩�����Ͻǵ�left��top�Լ����½�right��bottomֵ��*/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (contentView == null) return;

        //ScrollView�е�Ψһ�ӿؼ���λ����Ϣ, ���λ����Ϣ�������ؼ������������б��ֲ���
        originalRect.set(contentView.getLeft(), contentView.getTop(), contentView
                .getRight(), contentView.getBottom());//���ϣ�����
    }

    /**
     * �ڸ÷����л�ȡScrollView�е�Ψһ�ӿؼ���λ����Ϣ
     * ���λ����Ϣ�������ؼ������������б��ֲ���
     */

    /**
     * �ڴ����¼���, �����������������߼�
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        if (contentView == null) {
            return super.dispatchTouchEvent(ev);
        }

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                //�ж��Ƿ��������������
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();

                //��¼����ʱ��Yֵ
                startY = ev.getY();
                break;

            case MotionEvent.ACTION_UP:

                if (!isMoved) break;  //���û���ƶ����֣� ������ִ��

			/*	AlphaAnimation ͸���ȶ���Ч�� 
			  	ScaleAnimation ���Ŷ���Ч�� 
				TranslateAnimation λ�ƶ���Ч�� 
				RotateAnimation ��ת����Ч�� 
			 */
                // �������� params:formX,toX,fromY,toY                  contentView.getTop():��ǰYֵ��originalRect.top:δ����ʱYֵ
                TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(), originalRect.top);
                anim.setDuration(ANIM_TIME);

                contentView.startAnimation(anim);

                // ���ûص������Ĳ���λ��
                contentView.layout(originalRect.left, originalRect.top,
                        originalRect.right, originalRect.bottom);

                //����־λ���false
                canPullDown = false;
                canPullUp = false;
                isMoved = false;

                break;
            case MotionEvent.ACTION_MOVE:
                //�ж��Ƿ��������������
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();

                //���ƶ��Ĺ����У� ��û�й��������������ĳ̶ȣ� Ҳû�й��������������ĳ̶�
                if (!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();

                    break;
                }

                //������ָ�ƶ��ľ���
                float nowY = ev.getY();
                int deltaY = (int) (nowY - startY);

                //�Ƿ�Ӧ���ƶ�����
                boolean shouldMove =
                        (canPullDown && deltaY > 0)    //���������� ������ָ�����ƶ�
                                || (canPullUp && deltaY < 0)    //���������� ������ָ�����ƶ�
                                || (canPullUp && canPullDown); //�ȿ�������Ҳ�����������������������ScrollView�����Ŀؼ���ScrollView��С��

                if (shouldMove) {

                    //����ƫ����
                    int offset = (int) (deltaY * MOVE_FACTOR);

                    //������ָ���ƶ����ƶ�����
                    contentView.layout(originalRect.left, originalRect.top + offset,
                            originalRect.right, originalRect.bottom + offset);

                    isMoved = true;  //��¼�ƶ��˲���
                }

                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }


    /**
     * �ж��Ƿ����������                           getScrollY(): ScrollView�����ľ���
     */
    private boolean isCanPullDown() {
        if (isPullDownPermision) {
            return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
        } else {
            return false;
        }

    }

    /**
     * �ж��Ƿ�������ײ�
     */
    private boolean isCanPullUp() {
        return contentView.getHeight() <= getHeight() + getScrollY();
    }

    public void setPullDown(boolean isPullDownPermision) {
        this.isPullDownPermision = isPullDownPermision;
    }

}
