package lib.kalu.mediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.listener.OnPlayerBandwidthListener;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerPlaybackChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerScreenOrientationChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerVisibilityChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowVisibilityChangedListener;
import lib.kalu.mediaplayer.util.LogUtil;


public final class PlayerView extends FrameLayout implements VideoPlayerApi {

    private final String TAG = "PlayerView";

    // 视频解码
    private VideoKernelApi mVideoKernelApi;
    // 视频渲染
    private VideoRenderApi mVideoRenderApi;

    private final ViewGroup mVideoLayout;
    private final ViewGroup mComponentLayout;

    public PlayerView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setId(R.id.module_mediaplayer_id_player);
        // player
        mVideoLayout = new FrameLayout(getContext());
        mVideoLayout.setId(R.id.module_mediaplayer_video);
        LayoutParams playerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        playerLayoutParams.gravity = Gravity.CENTER;
        mVideoLayout.setLayoutParams(playerLayoutParams);
        addView(mVideoLayout, 0);
        // control
        mComponentLayout = new FrameLayout(getContext());
        mComponentLayout.setId(R.id.module_mediaplayer_component);
        LayoutParams controlLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        controlLayoutParams.gravity = Gravity.CENTER;
        mComponentLayout.setLayoutParams(controlLayoutParams);
        addView(mComponentLayout, 1);
    }

    @Override
    public ViewGroup getBaseVideoViewGroup() {
        return mVideoLayout;
    }

    @Override
    public ViewGroup getBaseComponentViewGroup() {
        return mComponentLayout;
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
            // Component distribution
            int childCount = mComponentLayout.getChildCount();
            if (childCount <= 0) {
                return false;
            }

            // Step 1: Priority to showing components
            for (int i = 0; i < childCount; i++) {
                View childAt = mComponentLayout.getChildAt(i);
                if (childAt instanceof ComponentApi && ((ComponentApi) childAt).isComponentShowing()) {
                    if (childAt.dispatchTouchEvent(ev)) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("PlayerView -> dispatchTouchEvent (Showing) true, childAt = " + childAt);
                        }
                        return true;
                    }
                }
            }

            // Step 2: Others (if needed, but usually only showing components consume touch)
            for (int i = 0; i < childCount; i++) {
                View childAt = mComponentLayout.getChildAt(i);
                if (childAt instanceof ComponentApi && !((ComponentApi) childAt).isComponentShowing()) {
                    if (childAt.dispatchTouchEvent(ev)) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("PlayerView -> dispatchTouchEvent (Hidden) true, childAt = " + childAt);
                        }
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerView -> dispatchTouchEvent Exception: " + e.getMessage());
            }
            return true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (LogUtil.DEBUG) {
            LogUtil.log("PlayerView -> dispatchKeyEvent -> action = " + event.getAction() + ", keyCode = " + event.getKeyCode());
        }
        try {
            int childCount = mComponentLayout.getChildCount();
            if (childCount <= 0) {
                return false;
            }

            // Step 1: Priority to showing components
            for (int i = 0; i < childCount; i++) {
                View childAt = mComponentLayout.getChildAt(i);
                if (childAt instanceof ComponentApi && ((ComponentApi) childAt).isComponentShowing()) {
                    if (childAt.dispatchKeyEvent(event)) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("PlayerView -> dispatchKeyEvent (Showing) true, childAt = " + childAt);
                        }
                        return true;
                    }
                }
            }

            // Step 2: Others
            for (int i = 0; i < childCount; i++) {
                View childAt = mComponentLayout.getChildAt(i);
                if (childAt instanceof ComponentApi && !((ComponentApi) childAt).isComponentShowing()) {
                    if (childAt.dispatchKeyEvent(event)) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("PlayerView -> dispatchKeyEvent (Hidden) true, childAt = " + childAt);
                        }
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerView -> dispatchKeyEvent Exception: " + e.getMessage());
            }
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
            if (visibility != View.VISIBLE) {
                pause(true);
            }
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
            if (null == videoKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getStartArgs -> error: videoKernel null");
                }
                return null;
            }
            StartArgs startArgs = videoKernel.getStartArgs();
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getStartArgs -> error: startArgs null");
                }
                return null;
            }
            return startArgs;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getStartArgs -> Exception: " + e.getMessage());
            }
            return null;
        }
    }

    public void updateStartArgs(StartArgs startArgs) {
        try {
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "updateStartArgs -> error: startArgs null");
                }
                return;
            }
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "updateStartArgs -> error: videoKernel null");
                }
                return;
            }
            videoKernel.setStartArgs(startArgs);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "updateStartArgs -> Exception: " + e.getMessage());
            }
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
        this.mOnPlayerProgressListener = l;
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

    /**************/

    private OnPlayerBandwidthListener mOnPlayerBandwidthListener = null;

    @Override
    public void setOnPlayerBandwidthListener(OnPlayerBandwidthListener l) {
        this.mOnPlayerBandwidthListener = l;
    }

    @Override
    public OnPlayerBandwidthListener getOnPlayerBandwidthListener() {
        return mOnPlayerBandwidthListener;
    }
}