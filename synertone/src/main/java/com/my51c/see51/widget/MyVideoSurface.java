package com.my51c.see51.widget;


import android.content.Context;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class MyVideoSurface extends GLSurfaceView {
    // public final static int COLOR_SPACE_COUNT = 4;
    // public final static Config IMG_CFG_SAVED = Config.ARGB_8888;
    // public final static int TEX_INNER_FMT = GL10.GL_RGBA;
    // public final static int TEX_OUTTER_FMT = GL10.GL_RGBA;
    // public final static int TEX_TYPE = GL10.GL_UNSIGNED_BYTE;
    public final static int COLOR_SPACE_COUNT = 3;
    public final static int TEX_INNER_FMT = GLES20.GL_RGB;
    public final static int TEX_OUTTER_FMT = GLES20.GL_RGB;
    public final static int TEX_TYPE = GLES20.GL_UNSIGNED_BYTE;// GL10.GL_UNSIGNED_SHORT_5_6_5;
    public final static Config IMG_CFG_SAVED = Config.ARGB_4444;// Config.RGB_565;
    private final static String TAG = MyVideoSurface.class.getSimpleName();
    private VideoRender glRender;

    private float zoomedScale;
    private float scale = 1.0f;
    private int frameWidth = 1, frameHeight = 1;
    private int surfaceWidth = 0, surfaceHeight = 0;
    private boolean needInit = true;

    public MyVideoSurface(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        // ����������Ⱦ��
        glRender = new VideoRender();
        setEGLContextClientVersion(2); // ����ʹ��OPENGL ES2.0
        setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
        setBackgroundColor(Color.TRANSPARENT);
        // ������Ⱦ��
        setRenderer(glRender);
        // ������Ⱦģʽ
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void zoomIn() {
        scale += 0.2f;
        if (scale >= 4.0f) {
            scale = 4.0f;
        }
        glRender.setScale(scale);
    }

    public void zoomOut() {
        scale -= 0.2f;
        if (scale <= 1.0f) {
            scale = 1.0f;
        }
        glRender.setScale(scale);
    }

    public void resetRatio(int w, int h) {
        if (surfaceWidth != w || surfaceHeight != h) {
            needInit = true;
            surfaceWidth = w;
            surfaceHeight = h;
        }
        float vRatio, hRatio;
        if (frameWidth * frameHeight > 0) {
            vRatio = (float) surfaceHeight / frameHeight;
            hRatio = (float) surfaceWidth / frameWidth;
        } else {
            vRatio = 1;
            hRatio = (float) surfaceWidth / (float) surfaceHeight;
        }
        if (hRatio > vRatio) {
            setZoomedScale(vRatio);
        } else {
            setZoomedScale(hRatio);
        }
    }

    /**
     * @return the zoomedScale
     */
    public float getZoomedScale() {
        return zoomedScale;
    }

    /**
     * @param zoomedScale the zoomedScale to set
     */
    public void setZoomedScale(float scale) {
        zoomedScale = scale;
    }

    private int getAlignedSize(int i_size) {
        /* Return the nearest power of 2 */
        int i_result = 1;
        while (i_result < i_size)
            i_result *= 2;

        return i_result;
    }

    public void update(byte[] pixels, int width, int height) {
        frameWidth = width;
        frameHeight = height;
        glRender.update(pixels, width, height);
        requestRender();
    }
}