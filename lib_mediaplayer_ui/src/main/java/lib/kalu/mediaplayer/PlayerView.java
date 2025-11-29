package lib.kalu.mediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;


public final class PlayerView extends RelativeLayout implements VideoPlayerApi {

    // 视频解码
    private VideoKernelApi mVideoKernelApi;
    // 视频渲染
    private VideoRenderApi mVideoRenderApi;

    public PlayerView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setId(R.id.module_mediaplayer_id_player);
        // player
        RelativeLayout playerLayout = new RelativeLayout(getContext());
        playerLayout.setId(R.id.module_mediaplayer_video);
        LayoutParams playerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        playerLayout.setLayoutParams(playerLayoutParams);
        addView(playerLayout, 0);
        // control
        RelativeLayout controlLayout = new RelativeLayout(getContext());
        controlLayout.setId(R.id.module_mediaplayer_component);
        LayoutParams controlLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        controlLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        controlLayout.setLayoutParams(controlLayoutParams);
        addView(controlLayout, 1);
    }

    @Override
    protected void onDetachedFromWindow() {
        detachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        attachedToWindow();
        super.onAttachedToWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        windowVisibilityChanged(visibility);
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (LogUtil.DEBUG) {
            LogUtil.log("PlayerView -> dispatchKeyEvent0 -> action = " + event.getAction() + ", ketCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());
        }
        try {

            // Component step1
            ViewGroup componentGroup = getBaseComponentViewGroup();
            int childCount = componentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = componentGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                boolean assignableFrom = ComponentApi.class.isAssignableFrom(childAt.getClass());
                if (!assignableFrom)
                    continue;
                boolean componentShowing = ((ComponentApi) childAt).isComponentShowing();
                if (!componentShowing)
                    continue;
                boolean dispatchKeyEvent = childAt.dispatchKeyEvent(event);
                if (!dispatchKeyEvent)
                    continue;
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerView -> dispatchKeyEvent1 -> i = " + i + ", childAt = " + childAt);
                }
                throw new Exception("warning: dispatchKeyEvent1 true, childAt = " + childAt);
            }
            // Component step2
            for (int i = 0; i < childCount; i++) {
                View childAt = componentGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                boolean assignableFrom = ComponentApi.class.isAssignableFrom(childAt.getClass());
                if (!assignableFrom)
                    continue;
                boolean dispatchKeyEvent = childAt.dispatchKeyEvent(event);
                if (!dispatchKeyEvent)
                    continue;
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerView -> dispatchKeyEvent2 -> i = " + i + ", childAt = " + childAt);
                }
                throw new Exception("warning: dispatchKeyEvent2 true, childAt = " + childAt);
            }

            // error
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public VideoRenderApi getVideoRender() {
        return mVideoRenderApi;
    }

    @Override
    public void setVideoRender(VideoRenderApi render) {
        mVideoRenderApi = render;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return mVideoKernelApi;
    }

    @Override
    public void setVideoKernel(VideoKernelApi kernel) {
        mVideoKernelApi = kernel;
    }

    @Override
    public void checkVideoVisibility() {
        try {
            int visibility = getVisibility();
            if (visibility == View.VISIBLE)
                throw new Exception("warning: visibility == View.VISIBLE");
            pause();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerView -> checkVideoVisibility -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void setScreenKeep(boolean enable) {
        setKeepScreenOn(enable);
    }
}