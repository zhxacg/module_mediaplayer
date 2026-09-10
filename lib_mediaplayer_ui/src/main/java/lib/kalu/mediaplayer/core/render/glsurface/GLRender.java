package lib.kalu.mediaplayer.core.render.glsurface;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.kalu.mediaplayer.util.LogUtil;

final class GLRender implements GLSurfaceView.Renderer {

    private static final String TAG = "GLRender";

    private final List<GLDrawer> mDrawers =
            new ArrayList<>();

    @Override
    public void onSurfaceCreated(
            GL10 gl,
            EGLConfig config
    ) {

        try {

            GLES20.glClearColor(
                    0F,
                    0F,
                    0F,
                    1F
            );

            /*
             * 普通视频渲染不需要 Blend。
             */
            GLES20.glDisable(
                    GLES20.GL_BLEND
            );

            int count =
                    mDrawers.size();

            if (count <= 0) {
                return;
            }

            int[] textureIds =
                    GLTool.getInstance()
                            .createTextureIds(
                                    count
                            );

            for (int i = 0; i < count; i++) {

                mDrawers
                        .get(i)
                        .setTextureID(
                                textureIds[i]
                        );
            }

        } catch (Exception e) {

            if (LogUtil.DEBUG) {
                LogUtil.log(
                        TAG,
                        "onSurfaceCreated -> "
                                + e.getMessage()
                );
            }
        }
    }

    @Override
    public void onSurfaceChanged(
            GL10 gl,
            int width,
            int height
    ) {

        if (width <= 0
                || height <= 0) {
            return;
        }

        try {

            GLES20.glViewport(
                    0,
                    0,
                    width,
                    height
            );

            for (GLDrawer drawer : mDrawers) {

                drawer.setWorldSize(
                        width,
                        height
                );
            }

        } catch (Exception e) {

            if (LogUtil.DEBUG) {
                LogUtil.log(
                        TAG,
                        "onSurfaceChanged -> "
                                + e.getMessage()
                );
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        try {

            GLES20.glClear(
                    GLES20.GL_COLOR_BUFFER_BIT
            );

            for (GLDrawer drawer : mDrawers) {
                drawer.draw();
            }

        } catch (Exception e) {

            if (LogUtil.DEBUG) {
                LogUtil.log(
                        TAG,
                        "onDrawFrame -> "
                                + e.getMessage()
                );
            }
        }
    }

    void addDrawer(
            @NonNull GLDrawer drawer
    ) {

        if (mDrawers.contains(drawer)) {
            return;
        }

        mDrawers.add(drawer);
    }

    void removeDrawer(
            @NonNull GLDrawer drawer
    ) {

        mDrawers.remove(drawer);
    }
}