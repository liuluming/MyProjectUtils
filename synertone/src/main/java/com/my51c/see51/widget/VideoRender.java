package com.my51c.see51.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.my51c.opengl.common.ESShader;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoRender implements GLSurfaceView.Renderer {
    private static final float[] VERTICES_DATA = {
            // Position 0
            -1.0f, 1.0f, 0.0f,
            // TexCoord 0
            0.0f, 0.0f,
            // Position 1
            -1.0f, -1.0f, 0.0f,
            // TexCoord 1
            0.0f, 1.0f,
            // Position 2
            1.0f, -1.0f, 0.0f,
            // TexCoord 2
            1.0f, 1.0f,
            // Position 3
            1.0f, 1.0f, 0.0f,
            // TexCoord 3
            1.0f, 0.0f};
    private static final short[] INDICES_DATA = {0, 1, 2, 0, 2, 3};
    // Handle to a program object
    private int programId;
    // Attribute locations
    private int mPositionLoc;
    private int mTexCoordLoc;
    // Sampler location
    private int mBaseMapLoc;
    // Texture handle
    private int mBaseMapTexId;
    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private ByteBuffer byteBuffer;
    private int lastWidth = 0, lastHeight = 0;
    public VideoRender() {
        mVertices = ByteBuffer.allocateDirect(VERTICES_DATA.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(VERTICES_DATA).position(0);
        mIndices = ByteBuffer.allocateDirect(INDICES_DATA.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(INDICES_DATA).position(0);
    }

    //
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr =
                // vertex shader script
                "attribute vec4 a_position;   \n" + "attribute vec2 a_texCoord;   \n"
                        + "varying vec2 v_texCoord;     \n"
                        + "void main()                  \n"
                        + "{                            \n"
                        + "   gl_Position = a_position; \n"
                        + "   v_texCoord = a_texCoord;  \n"
                        + "}                            \n";

        String fShaderStr =
                // fragment shader script
                "precision mediump float;                            \n"
                        + "varying vec2 v_texCoord;                            \n"
                        + "uniform sampler2D s_baseMap;                        \n"
                        // + "uniform sampler2D s_lightMap;                       \n"
                        + "void main()                                         \n"
                        + "{                                                   \n"
                        + "  vec4 baseColor;                                   \n"
                        + "  vec4 lightColor;                                  \n"
                        + "                                                    \n"
                        + "  baseColor = texture2D( s_baseMap, v_texCoord );   \n"
                        // + "  lightColor = texture2D( s_lightMap, v_texCoord ); \n"
                        + "  gl_FragColor = baseColor;   \n"
                        // baseColor * (lightColor + 0.25)
                        + "}                                                   \n";

        // Load the shaders and get a linked program object
        programId = ESShader.loadProgram(vShaderStr, fShaderStr);

        // Get the attribute locations
        mPositionLoc = GLES20.glGetAttribLocation(programId, "a_position");
        mTexCoordLoc = GLES20.glGetAttribLocation(programId, "a_texCoord");
        // Get the sampler locations
        mBaseMapLoc = GLES20.glGetUniformLocation(programId, "s_baseMap");
        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        mBaseMapTexId = textureId[0];

        GLES20.glClearColor(0.5f, 0.5f, 0.0f, 1.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        //dbg.i("drawFrame: size=" + mWidth + "x" + mHeight);
        // Set the viewport
        GLES20.glViewport(0, 0, mWidth, mHeight);
        // Clear the color buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Use the program object
        GLES20.glUseProgram(programId);
        // Load the vertex position
        mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionLoc, 3, GLES20.GL_FLOAT, false,
                5 * 4, mVertices);
        // Load the texture coordinate
        mVertices.position(3);
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false,
                5 * 4, mVertices);

        GLES20.glEnableVertexAttribArray(mPositionLoc);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        // draw bg
        if (byteBuffer != null) {
            synchronized (byteBuffer) {
                byteBuffer.position(0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBaseMapTexId);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
                        lastWidth, lastHeight, 0, GLES20.GL_RGB,
                        GLES20.GL_UNSIGNED_BYTE,// GL_UNSIGNED_SHORT_5_6_5/*GL_UNSIGNED_BYTE*/,
                        byteBuffer);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                // GLES20.glUniform1i(mBaseMapLoc, 0);
                // GLES20.glEnable(GLES20.GL_TEXTURE0);
            }
        } else {
            //dbg.e("empty byteBuffer...");
        }

        // Bind the base map
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBaseMapTexId);

        // Set the base map sampler to texture unit to 0
        GLES20.glUniform1i(mBaseMapLoc, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,
                mIndices);
    }

    /**
     * Handle surface changes
     */
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setScale(float scale) {
        // TODO Auto-generated method stub

    }

    public void update(byte[] pixels, int width, int height) {
        // TODO Auto-generated method stub
        if (lastWidth != width || lastHeight != height) {
            lastWidth = width;
            lastHeight = height;
//			byteBuffer = ByteBuffer.allocateDirect(1080 * 720 * 3);
        }
//		byteBuffer.put(pixels).position(0);
        byteBuffer = ByteBuffer.wrap(pixels, 0, height * width * 3);
    }

    private int loadTexture(InputStream is) {
        int[] textureId = new int[1];
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeStream(is);
        byte[] buffer = new byte[bitmap.getWidth() * bitmap.getHeight() * 3];

        for (int y = 0; y < bitmap.getHeight(); y++)
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);
                buffer[(y * bitmap.getWidth() + x) * 3 + 0] = (byte) ((pixel >> 16) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 3 + 1] = (byte) ((pixel >> 8) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 3 + 2] = (byte) ((pixel >> 0) & 0xFF);
            }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth()
                * bitmap.getHeight() * 3);
        byteBuffer.put(buffer).position(0);

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
                bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGB,
                GLES20.GL_UNSIGNED_BYTE, byteBuffer);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        return textureId[0];
    }
}