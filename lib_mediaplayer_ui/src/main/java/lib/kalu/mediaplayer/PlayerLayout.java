package lib.kalu.mediaplayer;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;

import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerBandwidthListener;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerStuckListener;
import lib.kalu.mediaplayer.listener.OnPlayerVisibilityChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowAttachChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowVisibilityChangedListener;
import lib.kalu.mediaplayer.util.LogUtil;


public class PlayerLayout extends RelativeLayout {

    private PlayerView mPlayerView;

    public PlayerLayout(Context context) {
        super(context);
        initPlayerView(context, null);
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPlayerView(context, attrs);
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlayerView(context, attrs);
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPlayerView(context, attrs);
    }

    private void initPlayerView(Context context, AttributeSet attrs) {
        try {
            if (null != mPlayerView) {
                return;
            }
            int childCount = getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = getChildAt(i);
                    if (childAt instanceof PlayerView) {
                        mPlayerView = (PlayerView) childAt;
                        return;
                    }
                }
            }
            mPlayerView = new PlayerView(context);
            mPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            addView(mPlayerView);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> initPlayerView -> " + e.getMessage());
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "dispatchTouchEvent -> warning: null == playerView");
                }
                return false;
            }
            return playerView.dispatchTouchEvent(ev);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> dispatchTouchEvent -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "dispatchKeyEvent -> warning: null == playerView");
                }
                return false;
            }
            return playerView.dispatchKeyEvent(event);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> dispatchKeyEvent -> " + e.getMessage());
            }
            return false;
        }
    }

    private ViewGroup findDecorView(View view) {
        try {
            View parent = (View) view.getParent();
            if (null == parent) {
                return (ViewGroup) view;
            } else {
                return findDecorView(parent);
            }
        } catch (Exception e) {
            return (ViewGroup) view;
        }
    }

    private PlayerView getPlayerView() {
        if (null != mPlayerView) {
            return mPlayerView;
        }
        try {
            int childCount = getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = getChildAt(i);
                    if (childAt instanceof PlayerView) {
                        mPlayerView = (PlayerView) childAt;
                        return mPlayerView;
                    }
                }
            }

            // fall back to DecorView search only if absolutely necessary
            ViewGroup decorView = findDecorView(this);
            if (null != decorView) {
                int decorChildCount = decorView.getChildCount();
                for (int i = 0; i < decorChildCount; i++) {
                    View childAt = decorView.getChildAt(i);
                    if (childAt instanceof PlayerView && childAt.getId() == R.id.module_mediaplayer_id_player) {
                        mPlayerView = (PlayerView) childAt;
                        return mPlayerView;
                    }
                }
            }

            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout", "getPlayerView -> not find");
            }
            return null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getPlayerView -> " + e.getMessage());
            }
            return null;
        }
    }

    /**********/

    public final boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isFull -> playerView error: null");
                }
                return false;
            }
            return playerView.isFull();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isFull -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean isFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isFloat -> playerView error: null");
                }
                return false;
            }
            return playerView.isFloat();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isFloat -> " + e.getMessage());
            }
            return false;
        }
    }

    public final StartArgs getStartArgs() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getStartArgs -> playerView error: null");
                }
                return null;
            }
            StartArgs args = playerView.getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getStartArgs -> warning: args null");
                }
                return null;
            }
            return args;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getStartArgs -> " + e.getMessage());
            }
            return null;
        }
    }

    public final void updateStartArgs(StartArgs startArgs) {
        try {
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "updateStartArgs -> error: startArgs null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "updateStartArgs -> playerView error: null");
                }
                return;
            }
            playerView.updateStartArgs(startArgs);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> updateStartArgs -> " + e.getMessage());
            }
        }
    }

    public final void startFull() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "startFull -> warning: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "startFull -> error: containsVideoUrl false");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "startFull -> playerView error: null");
                }
                return;
            }
            boolean startFull = playerView.startFull();
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> startFull -> status = " + startFull);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> startFull -> " + e.getMessage());
            }
        }
    }

    public final void stopFull() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stopFull -> warning: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stopFull -> error: containsVideoUrl false");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stopFull -> playerView error: null");
                }
                return;
            }
            boolean stopFull = playerView.stopFull();
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> stopFull -> status = " + stopFull);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> stopFull -> " + e.getMessage());
            }
        }
    }

    public final void startFloat() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "startFloat -> warning: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "startFloat -> error: containsVideoUrl false");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "startFloat -> playerView error: null");
                }
                return;
            }
            boolean startFull = playerView.startFloat();
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> startFloat -> status = " + startFull);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> startFloat -> " + e.getMessage());
            }
        }
    }

    //
    public final void stopFloat() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stopFloat -> warning: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stopFloat -> error: containsVideoUrl false");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stopFloat -> playerView error: null");
                }
                return;
            }
            boolean stopFull = playerView.stopFloat();
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> stopFloat -> status = " + stopFull);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> stopFloat -> " + e.getMessage());
            }
        }
    }

    public long getPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getPosition -> playerView error: null");
                }
                return 0;
            }
            return playerView.getPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getPosition -> " + e.getMessage());
            }
            return 0;
        }
    }

    public long getDuration() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getDuration -> playerView error: null");
                }
                return 0;
            }
            return playerView.getDuration();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getDuration -> " + e.getMessage());
            }
            return 0;
        }
    }

    public final void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setVideoScaleType -> playerView error: null");
                }
                return;
            }
            playerView.setVideoScaleType(scaleType);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setVideoScaleType -> " + e.getMessage());
            }
        }
    }

    public final void setVideoRotation(@PlayerType.RotationType.Value int rotationType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setVideoRotation -> playerView error: null");
                }
                return;
            }
            playerView.setVideoRotation(rotationType);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setVideoRotation -> " + e.getMessage());
            }
        }
    }

    public final void addComponent(ComponentApi componentApi) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "addComponent -> playerView error: null");
                }
                return;
            }
            playerView.addComponent(componentApi);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> addComponent -> " + e.getMessage());
            }
        }
    }

    public final void addAllComponent(List<ComponentApi> componentApis) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "addAllComponent -> playerView error: null");
                }
                return;
            }
            playerView.addAllComponent(componentApis);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> addAllComponent -> " + e.getMessage());
            }
        }
    }

    public final <T extends ComponentApi> T findComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "findComponent -> playerView error: null");
                }
                return null;
            }
            return playerView.findComponent(cls);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> findComponent -> " + e.getMessage());
            }
            return null;
        }
    }

    public final boolean showComponent(java.lang.Class<?> cls) {
        try {
            ComponentApi component = findComponent(cls);
            if (null == component) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "showComponent -> component error: null");
                }
                return false;
            }
            component.show();
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> showComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean hideComponent(java.lang.Class<?> cls) {
        try {
            ComponentApi component = findComponent(cls);
            if (null == component) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "hideComponent -> component error: null");
                }
                return false;
            }
            component.hide();
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> hideComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    public final void toggle() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "toggle -> playerView error: null");
                }
                return;
            }
            playerView.toggle();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> toggle -> " + e.getMessage());
            }
        }
    }

    public final void resume() {
        resume(true);
    }

    public final void resume(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "resume -> playerView error: null");
                }
                return;
            }
            playerView.resume(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> resume -> " + e.getMessage());
            }
        }
    }

    public final void pause() {
        pause(true);
    }

    public final void pause(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "pause -> playerView error: null");
                }
                return;
            }
            playerView.pause(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> pause -> " + e.getMessage());
            }
        }
    }

    public final void release() {
        release(true);
    }

    /**
     * @param callEvent 透传event
     */
    public final void release(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "release -> playerView error: null");
                }
                return;
            }
            playerView.release(callEvent, false);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> release -> " + e.getMessage());
            }
        }
    }

    public final void stop() {
        stop(true);
    }

    /**
     * @param callEvent 透传event
     */
    public final void stop(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "stop -> playerView error: null");
                }
                return;
            }
            playerView.stop(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> stop -> " + e.getMessage());
            }
        }
    }

    public final boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isPlaying -> playerView error: null");
                }
                return false;
            }
            return playerView.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isPlaying -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean isPrepared() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isPrepared -> playerView error: null");
                }
                return false;
            }
            return playerView.isPrepared();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isPrepared -> " + e.getMessage());
            }
            return false;
        }
    }

    public final String getUrl() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getUrl -> warning: args null");
                }
                return null;
            }
            return args.getUrl();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getUrl -> " + e.getMessage());
            }
            return null;
        }
    }

    public final UrlArgs getUrlArgs() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getUrlArgs -> warning: args null");
                }
                return null;
            }
            return args.getUrlArgs();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getUrlArgs -> " + e.getMessage());
            }
            return null;
        }
    }

    public void restart() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "restart -> playerView error: null");
                }
                return;
            }
            playerView.restart();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> restart -> " + e.getMessage());
            }
        }
    }

    public void restartSeekToPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "restartSeekToPosition -> playerView error: null");
                }
                return;
            }
            playerView.restartSeekToPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> restartSeekToPosition -> " + e.getMessage());
            }
        }
    }

    public void start(StartArgs args) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "start -> playerView error: null");
                }
                return;
            }
            playerView.start(args);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> start -> " + e.getMessage());
            }
        }
    }

    public final void setVolume(float left, float right) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setVolume -> playerView error: null");
                }
                return;
            }
            playerView.setVolume(left, right);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setVolume -> " + e.getMessage());
            }
        }
    }

    public final void closeVolume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "closeVolume -> playerView error: null");
                }
                return;
            }
            playerView.closeVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> closeVolume -> " + e.getMessage());
            }
        }
    }

    public final void openVolume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "openVolume -> playerView error: null");
                }
                return;
            }
            playerView.openVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> openVolume -> " + e.getMessage());
            }
        }
    }

    public final long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getPlayWhenReadySeekToPosition -> warning: args null");
                }
                return 0L;
            }
            return args.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getPlayWhenReadySeekToPosition -> " + e.getMessage());
            }
            return 0L;
        }
    }

    public final long getTrySeeDuration() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getTrySeeDuration -> error: args null");
                }
                return 0L;
            }
            return args.getTrySeeDuration();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getTrySeeDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    public final void seekTo(long postion) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "seekTo -> playerView error: null");
                }
                return;
            }
            playerView.seekTo(postion);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> seekTo -> " + e.getMessage());
            }
        }
    }

    public final void seekToDefaultPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "seekToDefaultPosition -> playerView error: null");
                }
                return;
            }
            playerView.seekToDefaultPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> seekToDefaultPosition -> " + e.getMessage());
            }
        }
    }

    public final boolean isLiveStream() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isLiveStream -> playerView error: null");
                }
                return false;
            }
            return playerView.isLiveStream();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isLiveStream -> " + e.getMessage());
            }
            return false;
        }
    }

    public final void setPlayerBackgroundColor(@ColorInt int color) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setPlayerBackgroundColor -> playerView error: null");
                }
                return;
            }
            playerView.setBackgroundColor(color);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setPlayerBackgroundColor -> " + e.getMessage());
            }
        }
    }

    public final void setSpeed(float speed) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setSpeed -> playerView error: null");
                }
                return;
            }
            playerView.setSpeed(speed);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setSpeed -> " + e.getMessage());
            }
        }
    }

    public final float getSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getSpeed -> playerView error: null");
                }
                return 1.0f;
            }
            return playerView.getSpeed();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setPlayerBackgroundColor -> " + e.getMessage());
            }
            return 1.0f;
        }
    }

    public final boolean toggleTrack(TrackInfo trackInfo) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "toggleTrack -> playerView error: null");
                }
                return false;
            }
            return playerView.toggleTrack(trackInfo);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> toggleTrack -> " + e.getMessage());
            }
            return false;
        }
    }

    public final List<TrackInfo> getTrackInfo() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getTrackInfo -> playerView error: null");
                }
                return null;
            }
            return playerView.getTrackInfoAll();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getTrackInfo -> " + e.getMessage());
            }
            return null;
        }
    }

    public final List<TrackInfo> getTrackInfoVideo() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getTrackInfoVideo -> playerView error: null");
                }
                return null;
            }
            return playerView.getTrackInfoVideo();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getTrackInfoVideo -> " + e.getMessage());
            }
            return null;
        }
    }

    public final List<TrackInfo> getTrackInfoAudio() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getTrackInfoAudio -> playerView error: null");
                }
                return null;
            }
            return playerView.getTrackInfoAudio();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getTrackInfoAudio -> " + e.getMessage());
            }
            return null;
        }
    }

    public final List<TrackInfo> getTrackInfoSubtitle() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getTrackInfoSubtitle -> playerView error: null");
                }
                return null;
            }
            return playerView.getTrackInfoSubtitle();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getTrackInfoSubtitle -> " + e.getMessage());
            }
            return null;
        }
    }

    public final List<HlsSpanInfo> getSegments() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getSegments -> playerView error: null");
                }
                return null;
            }
            return playerView.getSegments();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getSegments -> " + e.getMessage());
            }
            return null;
        }
    }

    public final String screenshot() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "screenshot -> playerView error: null");
                }
                return null;
            }
            String screenshot = playerView.screenshot();
            if (null == screenshot || screenshot.length() == 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "screenshot -> error: null == screenshot || screenshot.length() == 0");
                }
                return null;
            }
            return screenshot;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> screenshot -> " + e.getMessage());
            }
            return null;
        }
    }

    public final void sendSelfEvent(int playState) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "sendSelfEvent -> playerView error: null");
                }
                return;
            }
            playerView.callEvent(playState);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> sendSelfEvent -> " + e.getMessage());
            }
        }
    }

    public final boolean setPlaybackSubtitleOffsetMs(int offset) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setPlaybackSubtitleOffsetMs -> playerView error: null");
                }
                return false;
            }
            return playerView.subtitleOffsetMs(offset);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setPlaybackSubtitleOffsetMs -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean addSubtitleTrack(String url) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "addSubtitleTrack -> playerView error: null");
                }
                return false;
            }
            return playerView.addSubtitleTrack(url);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> addSubtitleTrack -> " + e.getMessage());
            }
            return false;
        }
    }

    public final void setOnPlayerEpisodeListener(OnPlayerEpisodeListener listener) {
        try {
            if (null == listener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerEpisodeListener -> listener error: null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerEpisodeListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerEpisodeListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerEpisodeListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerWindowStateChangeListener(OnPlayerWindowStateChangeListener listener) {
        try {
            if (null == listener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerWindowStateChangeListener -> listener error: null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerWindowStateChangeListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerWindowStateChangeListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerWindowStateChangeListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerEventListener(OnPlayerEventListener listener) {
        try {
            if (null == listener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerEventListener -> listener error: null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerEventListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerEventListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerEventListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerProgressListener(OnPlayerProgressListener listener) {
        try {
            if (null == listener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerProgressListener -> listener error: null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerProgressListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerProgressListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerProgressListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerBandwidthListener(OnPlayerBandwidthListener l) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerBandwidthListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerBandwidthListener(l);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerBandwidthListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerStuckListener(OnPlayerStuckListener l) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerStuckListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerStuckListener(l);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerStuckListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerWindowVisibilityChangedListener(OnPlayerWindowVisibilityChangedListener listener) {
        try {
            if (null == listener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerWindowVisibilityChangedListener -> listener error: null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerWindowVisibilityChangedListener -> playerView error: null");
                }
                return;
            }
            playerView.setmOnPlayerWindowVisibilityChangedListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerWindowVisibilityChangedListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerWindowAttachChangedListener(OnPlayerWindowAttachChangedListener listener) {
        try {
            if (null == listener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerWindowAttachChangedListener -> listener error: null");
                }
                return;
            }
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerWindowAttachChangedListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerWindowAttachChangedListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerWindowAttachChangedListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerVisibilityChangedListener(OnPlayerVisibilityChangedListener l) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "setOnPlayerVisibilityChangedListener -> playerView error: null");
                }
                return;
            }
            playerView.setOnPlayerVisibilityChangedListener(l);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerVisibilityChangedListener -> " + e.getMessage());
            }
        }
    }

    public final void removeAllPlayerListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "removeAllPlayerListener -> playerView error: null");
                }
                return;
            }
            playerView.clearPlayerListener();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> removeAllPlayerListener -> " + e.getMessage());
            }
        }
    }

    public final int getComponentCount() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "getComponentCount -> playerView error: null");
                }
                return 0;
            }
            return playerView.getComponentCount();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getComponentCount -> " + e.getMessage());
            }
            return 0;
        }
    }

    public final boolean showOnlyComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "showOnlyComponent -> playerView error: null");
                }
                return false;
            }
            return playerView.showOnlyComponent(cls);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> showOnlyComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean isComponentEmpty() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isComponentEmpty -> playerView error: null");
                }
                return false;
            }
            return playerView.isComponentEmpty();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isComponentEmpty -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean isComponentNotEmpty() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "isComponentNotEmpty -> playerView error: null");
                }
                return false;
            }
            return playerView.isComponentNotEmpty();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isComponentNotEmpty -> " + e.getMessage());
            }
            return false;
        }
    }

    public final void hideAllComponent() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "hideAllComponent -> playerView error: null");
                }
                return;
            }
            playerView.hideAllComponent();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> hideAllComponent -> " + e.getMessage());
            }
        }
    }

    public final void clearAllComponent() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "clearAllComponent -> playerView error: null");
                }
                return;
            }
            playerView.clearAllComponent();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> clearAllComponent -> " + e.getMessage());
            }
        }
    }

    public final StartArgs.Builder newBuilderCopy() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "newBuilderCopy -> error: playerView null");
                }
                return null;
            }
            StartArgs args = playerView.getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "newBuilderCopy -> error: args null");
                }
                return null;
            }
            return args.newBuilderCopy();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> newBuilderCopy -> " + e.getMessage());
            }
            return null;
        }
    }

//    public final boolean isScreenOrientationPortrait() {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.isScreenOrientationPortrait();
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log("ComponentApi -> isScreenOrientationPortrait -> " + e.getMessage());
//            }
//            return false;
//        }
//    }
//
//    public final boolean isScreenOrientationLandspace() {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.isScreenOrientationLandspace();
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log("ComponentApi -> isScreenOrientationLandspace -> " + e.getMessage());
//            }
//            return false;
//        }
//    }

    public final boolean requestScreenOrientation(@PlayerType.ScreenOrientation.Value int value) {
        return requestScreenOrientation(value, false);
    }

    public final boolean requestScreenOrientation(@PlayerType.ScreenOrientation.Value int value, boolean formatScreen) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerLayout", "requestScreenOrientation -> playerView error: null");
                }
                return false;
            }
            return playerView.requestScreenOrientation(getContext(), value, formatScreen);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> requestScreenOrientation -> " + e.getMessage());
            }
            return false;
        }
    }
}
