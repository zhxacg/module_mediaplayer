package lib.kalu.mediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerPlaybackChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerScreenOrientationChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerVisibilityChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowVisibilityChangedListener;
import lib.kalu.mediaplayer.util.LogUtil;


public final class PlayerView extends RelativeLayout implements VideoPlayerApi {

    private final String TAG = "PlayerView";

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
        super.onDetachedFromWindow();
        callPlayerWindowAttachChanged(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        callPlayerWindowAttachChanged(true);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        callPlayerWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        callPlayerVisibilityChanged(visibility);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
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
                boolean dispatchTouchEvent = childAt.dispatchTouchEvent(ev);
                if (!dispatchTouchEvent)
                    continue;
                throw new Exception("warning: dispatchTouchEvent true, childAt = " + childAt);
            }
            // Component step2
            for (int i = 0; i < childCount; i++) {
                View childAt = componentGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                boolean assignableFrom = ComponentApi.class.isAssignableFrom(childAt.getClass());
                if (!assignableFrom)
                    continue;
                boolean dispatchTouchEvent = childAt.dispatchTouchEvent(ev);
                if (!dispatchTouchEvent)
                    continue;
                throw new Exception("warning: dispatchTouchEvent true, childAt = " + childAt);
            }

            // error
            return false;
        } catch (Exception e) {
            return true;
        }
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
            pause(true);
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

    @Override
    public void start(StartArgs args) {
        VideoPlayerApi.super.start(args);
    }

    /**************/

    public StartArgs getStartArgs() {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            StartArgs startArgs = videoKernel.getStartArgs();
            if (null == startArgs)
                throw new Exception("error: startArgs null");
            return startArgs;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getStartArgs -> Exception: " + e.getMessage());
            }
            return null;
        }
    }

    /**************/

    private OnPlayerWindowStateChangeListener mOnPlayerWindowStateChangeListener = null;

    @Override
    public OnPlayerWindowStateChangeListener getPlayerWindowStateChangeListener() {
        return mOnPlayerWindowStateChangeListener;
    }

    @Override
    public void setOnPlayerWindowStateChangeListener(OnPlayerWindowStateChangeListener l) {
        this.mOnPlayerWindowStateChangeListener = l;
    }

    /**************/

    private OnPlayerEventListener mOnPlayerEventListener = null;

    @Override
    public void setOnPlayerEventListener(OnPlayerEventListener l) {
        this.mOnPlayerEventListener = l;
    }

    @Override
    public OnPlayerEventListener getPlayerEventListener() {
        return mOnPlayerEventListener;
    }

    /**************/

    private OnPlayerProgressListener mOnPlayerProgressListener = null;

    @Override
    public OnPlayerProgressListener getPlayerProgressListener() {
        return mOnPlayerProgressListener;
    }

    @Override
    public void setOnPlayerProgressListener(OnPlayerProgressListener l) {
        this.mOnPlayerProgressListener = null;
    }

    /**************/

    private OnPlayerEpisodeListener mOnPlayerEpisodeListener = null;

    @Override
    public OnPlayerEpisodeListener getPlayerEpisodeListener() {
        return mOnPlayerEpisodeListener;
    }

    @Override
    public void setOnPlayerEpisodeListener(OnPlayerEpisodeListener l) {
        this.mOnPlayerEpisodeListener = l;
    }

    /**************/

    private OnPlayerVisibilityChangedListener mOnPlayerVisibilityChangedListener = null;

    @Override
    public OnPlayerVisibilityChangedListener getPlayerVisibilityChangedListener() {
        return mOnPlayerVisibilityChangedListener;
    }

    @Override
    public void setOnPlayerVisibilityChangedListener(OnPlayerVisibilityChangedListener l) {
        this.mOnPlayerVisibilityChangedListener = l;
    }

    /**************/

    private OnPlayerWindowVisibilityChangedListener mOnPlayerWindowVisibilityChangedListener = null;


    @Override
    public OnPlayerWindowVisibilityChangedListener getPlayerWindowVisibilityChangedListener() {
        return mOnPlayerWindowVisibilityChangedListener;
    }

    public void setmOnPlayerWindowVisibilityChangedListener(OnPlayerWindowVisibilityChangedListener l) {
        this.mOnPlayerWindowVisibilityChangedListener = l;
    }

    /**************/

    private OnPlayerPlaybackChangedListener mOnPlayerSpeedChangedListener = null;

    @Override
    public OnPlayerPlaybackChangedListener getOnPlayerPlaybackChangedListener() {
        return mOnPlayerSpeedChangedListener;
    }

    @Override
    public void setOnPlayerPlaybackChangedListener(OnPlayerPlaybackChangedListener l) {
        this.mOnPlayerSpeedChangedListener = l;
    }

    /**************/

    private OnPlayerScreenOrientationChangeListener mOnPlayerScreenOrientationChangeListener = null;

    @Override
    public OnPlayerScreenOrientationChangeListener getPlayerScreenOrientationChangeListener() {
        return mOnPlayerScreenOrientationChangeListener;
    }

    @Override
    public void setOnPlayerScreenOrientationChangeListener(OnPlayerScreenOrientationChangeListener l) {
        this.mOnPlayerScreenOrientationChangeListener = l;
    }
}