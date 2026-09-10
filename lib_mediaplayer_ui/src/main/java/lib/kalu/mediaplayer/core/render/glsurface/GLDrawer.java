package lib.kalu.mediaplayer.core.render.glsurface;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import lib.kalu.mediaplayer.util.LogUtil;

final class GLDrawer {

    private static final String TAG = "GLDrawer";

    interface SurfaceListener {

        void onSurfaceAvailable(
                @NonNull Surface surface
        );

        void onSurfaceDestroyed();

        void onFrameAvailable();
    }

    @Nullable
    private SurfaceListener mSurfaceListener;

    @Nullable
    private SurfaceTexture mSurfaceTexture;

    @Nullable
    private Surface mSurface;

    private int mTextureId = -1;

    private int mProgram = -1;
    private int mVertexShader = -1;
    private int mFragmentShader = -1;

    private int mVertexMatrixHandler = -1;
    private int mVertexPosHandler = -1;
    private int mTexturePosHandler = -1;
    private int mTextureHandler = -1;
    private int mAlphaHandler = -1;

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;

    private int mWorldWidth = -1;
    private int mWorldHeight = -1;

    private float mAlpha = 1F;

    private float mWidthRatio = 1F;
    private float mHeightRatio = 1F;

    @Nullable
    private float[] mMatrix;

    @Nullable
    private FloatBuffer mVertexBuffer;

    @Nullable
    private FloatBuffer mTextureBuffer;

    private static final int MATRIX_SIZE = 16;

    private static final float[] VERTEX_COORS = {
            -1F, -1F,
            1F, -1F,
            -1F, 1F,
            1F, 1F
    };

    private static final float[] TEXTURE_COORS = {
            0F, 1F,
            1F, 1F,
            0F, 0F,
            1F, 0F
    };

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;" +
                    "precision mediump float;" +
                    "uniform mat4 uMatrix;" +
                    "attribute vec2 aCoordinate;" +
                    "varying vec2 vCoordinate;" +
                    "attribute float alpha;" +
                    "varying float inAlpha;" +
                    "void main(){" +
                    "gl_Position=uMatrix*aPosition;" +
                    "vCoordinate=aCoordinate;" +
                    "inAlpha=alpha;" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "varying vec2 vCoordinate;" +
                    "varying float inAlpha;" +
                    "uniform samplerExternalOES uTexture;" +
                    "void main(){" +
                    "vec4 color=texture2D(uTexture,vCoordinate);" +
                    "gl_FragColor=vec4(color.rgb,color.a*inAlpha);" +
                    "}";

    GLDrawer() {
        initBuffer();
    }

    // -------------------------------------------------------------------------
    // Listener
    // -------------------------------------------------------------------------

    void addSurfaceListener(
            @NonNull SurfaceListener listener
    ) {

        if (mSurfaceListener == listener) {
            return;
        }

        mSurfaceListener = listener;

        /*
         * Listener后注册时补发现有Surface。
         */
        Surface surface = mSurface;

        if (surface != null
                && surface.isValid()) {

            try {

                listener.onSurfaceAvailable(
                        surface
                );

            } catch (Exception e) {

                LogUtil.log(
                        TAG,
                        "addSurfaceListener -> "
                                + e.getMessage()
                );
            }
        }
    }

    void removeSurfaceListener(
            @NonNull SurfaceListener listener
    ) {

        if (mSurfaceListener != listener) {
            return;
        }

        mSurfaceListener = null;
    }

    // -------------------------------------------------------------------------
    // Buffer
    // -------------------------------------------------------------------------

    private void initBuffer() {

        ByteBuffer vb =
                ByteBuffer.allocateDirect(
                        VERTEX_COORS.length * 4
                );

        vb.order(
                ByteOrder.nativeOrder()
        );

        mVertexBuffer =
                vb.asFloatBuffer();

        mVertexBuffer.put(
                VERTEX_COORS
        );

        mVertexBuffer.position(0);


        ByteBuffer tb =
                ByteBuffer.allocateDirect(
                        TEXTURE_COORS.length * 4
                );

        tb.order(
                ByteOrder.nativeOrder()
        );

        mTextureBuffer =
                tb.asFloatBuffer();

        mTextureBuffer.put(
                TEXTURE_COORS
        );

        mTextureBuffer.position(0);
    }

    // -------------------------------------------------------------------------
    // Texture
    // -------------------------------------------------------------------------

    void setTextureID(int textureId) {

        /*
         * EGL Context重建。
         */
        notifySurfaceDestroyed();

        releaseSurfaceOnly();

        mTextureId = textureId;

        if (textureId <= 0) {
            return;
        }

        try {

            GLES20.glBindTexture(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    textureId
            );

            GLES20.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
            );

            GLES20.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
            );

            GLES20.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE
            );

            GLES20.glTexParameteri(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
            );

            GLES20.glBindTexture(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    0
            );

            mSurfaceTexture =
                    new SurfaceTexture(
                            textureId
                    );

            mSurfaceTexture.setOnFrameAvailableListener(
                    surfaceTexture -> {

                        SurfaceListener listener =
                                mSurfaceListener;

                        if (listener == null) {
                            return;
                        }

                        try {

                            listener.onFrameAvailable();

                        } catch (Exception e) {

                            if (LogUtil.DEBUG) {
                                LogUtil.log(
                                        TAG,
                                        "onFrameAvailable -> "
                                                + e.getMessage()
                                );
                            }
                        }
                    }
            );

            mSurface =
                    new Surface(
                            mSurfaceTexture
                    );

            SurfaceListener listener =
                    mSurfaceListener;

            if (listener != null
                    && mSurface.isValid()) {

                listener.onSurfaceAvailable(
                        mSurface
                );
            }

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "setTextureID -> "
                            + e.getMessage()
            );
        }
    }

    @Nullable
    Surface getSurface() {
        return mSurface;
    }

    // -------------------------------------------------------------------------
    // Size
    // -------------------------------------------------------------------------

    void setVideoSize(
            int width,
            int height
    ) {

        if (width <= 0 || height <= 0) {
            return;
        }

        if (mVideoWidth == width
                && mVideoHeight == height) {
            return;
        }

        mVideoWidth = width;
        mVideoHeight = height;

        mMatrix = null;
    }

    void setWorldSize(
            int width,
            int height
    ) {

        if (width <= 0 || height <= 0) {
            return;
        }

        if (mWorldWidth == width
                && mWorldHeight == height) {
            return;
        }

        mWorldWidth = width;
        mWorldHeight = height;

        mMatrix = null;
    }

    void setAlpha(float alpha) {

        mAlpha = Math.max(
                0F,
                Math.min(
                        1F,
                        alpha
                )
        );
    }

    // -------------------------------------------------------------------------
    // Matrix
    // -------------------------------------------------------------------------

    private void initMatrix() {

        if (mMatrix != null) {
            return;
        }

        mMatrix =
                new float[MATRIX_SIZE];

        if (mVideoWidth <= 0
                || mVideoHeight <= 0
                || mWorldWidth <= 0
                || mWorldHeight <= 0) {

            Matrix.setIdentityM(
                    mMatrix,
                    0
            );

            return;
        }

        mWidthRatio = 1F;
        mHeightRatio = 1F;

        float videoRatio =
                mVideoWidth /
                        (float) mVideoHeight;

        float worldRatio =
                mWorldWidth /
                        (float) mWorldHeight;

        if (videoRatio > worldRatio) {

            mHeightRatio =
                    videoRatio /
                            worldRatio;

        } else {

            mWidthRatio =
                    worldRatio /
                            videoRatio;
        }

        float[] projection =
                new float[MATRIX_SIZE];

        Matrix.orthoM(
                projection,
                0,
                -mWidthRatio,
                mWidthRatio,
                -mHeightRatio,
                mHeightRatio,
                3F,
                5F
        );

        float[] view =
                new float[MATRIX_SIZE];

        Matrix.setLookAtM(
                view,
                0,
                0F,
                0F,
                5F,
                0F,
                0F,
                0F,
                0F,
                1F,
                0F
        );

        Matrix.multiplyMM(
                mMatrix,
                0,
                projection,
                0,
                view,
                0
        );
    }

    // -------------------------------------------------------------------------
    // Draw
    // -------------------------------------------------------------------------

    void draw() {

        SurfaceTexture surfaceTexture =
                mSurfaceTexture;

        if (mTextureId <= 0
                || surfaceTexture == null) {
            return;
        }

        try {

            initMatrix();

            createGLProgram();

            if (mProgram <= 0) {
                return;
            }

            /*
             * 消费MediaCodec输出的新帧。
             */
            surfaceTexture.updateTexImage();

            GLES20.glActiveTexture(
                    GLES20.GL_TEXTURE0
            );

            GLES20.glBindTexture(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    mTextureId
            );

            GLES20.glUniform1i(
                    mTextureHandler,
                    0
            );

            doDraw();

            GLES20.glBindTexture(
                    GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    0
            );

        } catch (Exception e) {

            if (LogUtil.DEBUG) {
                LogUtil.log(
                        TAG,
                        "draw -> "
                                + e.getMessage()
                );
            }
        }
    }

    private void doDraw() {

        if (mMatrix == null
                || mVertexBuffer == null
                || mTextureBuffer == null) {
            return;
        }

        GLES20.glEnableVertexAttribArray(
                mVertexPosHandler
        );

        GLES20.glEnableVertexAttribArray(
                mTexturePosHandler
        );

        GLES20.glUniformMatrix4fv(
                mVertexMatrixHandler,
                1,
                false,
                mMatrix,
                0
        );

        GLES20.glVertexAttribPointer(
                mVertexPosHandler,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                mVertexBuffer
        );

        GLES20.glVertexAttribPointer(
                mTexturePosHandler,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                mTextureBuffer
        );

        GLES20.glVertexAttrib1f(
                mAlphaHandler,
                mAlpha
        );

        GLES20.glDrawArrays(
                GLES20.GL_TRIANGLE_STRIP,
                0,
                4
        );

        GLES20.glDisableVertexAttribArray(
                mVertexPosHandler
        );

        GLES20.glDisableVertexAttribArray(
                mTexturePosHandler
        );
    }

    // -------------------------------------------------------------------------
    // Shader
    // -------------------------------------------------------------------------

    private void createGLProgram() {

        if (mProgram > 0) {

            GLES20.glUseProgram(
                    mProgram
            );

            return;
        }

        mVertexShader =
                loadShader(
                        GLES20.GL_VERTEX_SHADER,
                        VERTEX_SHADER
                );

        mFragmentShader =
                loadShader(
                        GLES20.GL_FRAGMENT_SHADER,
                        FRAGMENT_SHADER
                );

        if (mVertexShader <= 0
                || mFragmentShader <= 0) {
            return;
        }

        int program =
                GLES20.glCreateProgram();

        GLES20.glAttachShader(
                program,
                mVertexShader
        );

        GLES20.glAttachShader(
                program,
                mFragmentShader
        );

        GLES20.glLinkProgram(
                program
        );

        int[] status = new int[1];

        GLES20.glGetProgramiv(
                program,
                GLES20.GL_LINK_STATUS,
                status,
                0
        );

        if (status[0] != GLES20.GL_TRUE) {

            GLES20.glDeleteProgram(
                    program
            );

            return;
        }

        mProgram = program;

        mVertexMatrixHandler =
                GLES20.glGetUniformLocation(
                        program,
                        "uMatrix"
                );

        mVertexPosHandler =
                GLES20.glGetAttribLocation(
                        program,
                        "aPosition"
                );

        mTextureHandler =
                GLES20.glGetUniformLocation(
                        program,
                        "uTexture"
                );

        mTexturePosHandler =
                GLES20.glGetAttribLocation(
                        program,
                        "aCoordinate"
                );

        mAlphaHandler =
                GLES20.glGetAttribLocation(
                        program,
                        "alpha"
                );

        GLES20.glUseProgram(
                program
        );
    }

    private int loadShader(
            int type,
            String code
    ) {

        int shader =
                GLES20.glCreateShader(
                        type
                );

        if (shader == 0) {
            return -1;
        }

        GLES20.glShaderSource(
                shader,
                code
        );

        GLES20.glCompileShader(
                shader
        );

        int[] status = new int[1];

        GLES20.glGetShaderiv(
                shader,
                GLES20.GL_COMPILE_STATUS,
                status,
                0
        );

        if (status[0] != GLES20.GL_TRUE) {

            GLES20.glDeleteShader(
                    shader
            );

            return -1;
        }

        return shader;
    }

    // -------------------------------------------------------------------------
    // Transform
    // -------------------------------------------------------------------------

    void translate(
            float dx,
            float dy
    ) {

        initMatrix();

        if (mMatrix == null) {
            return;
        }

        Matrix.translateM(
                mMatrix,
                0,
                dx * mWidthRatio * 2F,
                -dy * mHeightRatio * 2F,
                0F
        );
    }

    void scale(
            float sx,
            float sy
    ) {

        if (sx == 0F || sy == 0F) {
            return;
        }

        initMatrix();

        if (mMatrix == null) {
            return;
        }

        Matrix.scaleM(
                mMatrix,
                0,
                sx,
                sy,
                1F
        );

        mWidthRatio /= sx;
        mHeightRatio /= sy;
    }

    // -------------------------------------------------------------------------
    // Release
    // -------------------------------------------------------------------------

    private void notifySurfaceDestroyed() {

        if (mSurface == null) {
            return;
        }

        SurfaceListener listener =
                mSurfaceListener;

        if (listener == null) {
            return;
        }

        try {

            listener.onSurfaceDestroyed();

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "notifySurfaceDestroyed -> "
                            + e.getMessage()
            );
        }
    }

    private void releaseSurfaceOnly() {

        Surface surface =
                mSurface;

        mSurface = null;

        if (surface != null) {

            try {
                surface.release();
            } catch (Exception ignored) {
            }
        }

        SurfaceTexture surfaceTexture =
                mSurfaceTexture;

        mSurfaceTexture = null;

        if (surfaceTexture != null) {

            try {

                surfaceTexture.setOnFrameAvailableListener(
                        null
                );

                surfaceTexture.release();

            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 必须GL线程调用。
     */
    void release() {

        notifySurfaceDestroyed();

        releaseSurfaceOnly();

        if (mProgram > 0) {

            GLES20.glDeleteProgram(
                    mProgram
            );

            mProgram = -1;
        }

        if (mVertexShader > 0) {

            GLES20.glDeleteShader(
                    mVertexShader
            );

            mVertexShader = -1;
        }

        if (mFragmentShader > 0) {

            GLES20.glDeleteShader(
                    mFragmentShader
            );

            mFragmentShader = -1;
        }

        if (mTextureId > 0) {

            GLES20.glDeleteTextures(
                    1,
                    new int[]{
                            mTextureId
                    },
                    0
            );

            mTextureId = -1;
        }

        mMatrix = null;

        mVertexBuffer = null;
        mTextureBuffer = null;

        mSurfaceListener = null;
    }
}