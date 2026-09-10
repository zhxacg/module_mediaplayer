package lib.kalu.mediaplayer.core.render.glsurface;

import android.opengl.GLES20;

final class GLTool {

    private static volatile GLTool sInstance;

    private GLTool() {
    }

    static GLTool getInstance() {

        if (sInstance == null) {

            synchronized (GLTool.class) {

                if (sInstance == null) {
                    sInstance = new GLTool();
                }
            }
        }

        return sInstance;
    }

    int[] createTextureIds(int count) {

        if (count <= 0) {
            return new int[0];
        }

        int[] textureIds =
                new int[count];

        GLES20.glGenTextures(
                count,
                textureIds,
                0
        );

        return textureIds;
    }

    int createTextureId() {

        int[] textureIds =
                createTextureIds(1);

        if (textureIds.length == 0) {
            return -1;
        }

        return textureIds[0];
    }

    void deleteTexture(int textureId) {

        if (textureId <= 0) {
            return;
        }

        GLES20.glDeleteTextures(
                1,
                new int[]{
                        textureId
                },
                0
        );
    }

    int createFBOTexture(
            int width,
            int height
    ) {

        if (width <= 0
                || height <= 0) {
            return -1;
        }

        int textureId =
                createTextureId();

        if (textureId <= 0) {
            return -1;
        }

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                textureId
        );

        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                width,
                height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                null
        );

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
        );

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
        );

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
        );

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
        );

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                0
        );

        return textureId;
    }

    int createFrameBuffer() {

        int[] frameBuffers =
                new int[1];

        GLES20.glGenFramebuffers(
                1,
                frameBuffers,
                0
        );

        return frameBuffers[0];
    }

    boolean bindFBO(
            int frameBufferId,
            int textureId
    ) {

        if (frameBufferId <= 0
                || textureId <= 0) {
            return false;
        }

        GLES20.glBindFramebuffer(
                GLES20.GL_FRAMEBUFFER,
                frameBufferId
        );

        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                textureId,
                0
        );

        int status =
                GLES20.glCheckFramebufferStatus(
                        GLES20.GL_FRAMEBUFFER
                );

        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {

            GLES20.glBindFramebuffer(
                    GLES20.GL_FRAMEBUFFER,
                    0
            );

            return false;
        }

        return true;
    }

    void unbindFBO() {

        GLES20.glBindFramebuffer(
                GLES20.GL_FRAMEBUFFER,
                0
        );

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                0
        );
    }

    void deleteFBO(
            int frameBufferId,
            int textureId
    ) {

        GLES20.glBindFramebuffer(
                GLES20.GL_FRAMEBUFFER,
                0
        );

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                0
        );

        if (frameBufferId > 0) {

            GLES20.glDeleteFramebuffers(
                    1,
                    new int[]{
                            frameBufferId
                    },
                    0
            );
        }

        if (textureId > 0) {

            GLES20.glDeleteTextures(
                    1,
                    new int[]{
                            textureId
                    },
                    0
            );
        }
    }
}