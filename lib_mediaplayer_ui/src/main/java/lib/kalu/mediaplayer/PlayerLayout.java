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
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerVisibilityChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowAttachChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowVisibilityChangedListener;
import lib.kalu.mediaplayer.util.LogUtil;


public class PlayerLayout extends RelativeLayout {

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
            int childCount = getChildCount();
            if (childCount > 0)
                throw new Exception("childCount warning: " + childCount);
            PlayerView playerView = new PlayerView(context);
            playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            addView(playerView);
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
            if (null == playerView)
                throw new Exception("warning: null == playerView");
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
            if (null == playerView)
                throw new Exception("warning: null == playerView");
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
        try {
            int childCount = getChildCount();
            // sample
            if (childCount == 1) {
                return (PlayerView) getChildAt(0);
            }
            // not
            else {
                ViewGroup decorView = findDecorView(this);
                if (null == decorView)
                    throw new Exception("decorView error: null");
                int decorChildCount = decorView.getChildCount();
                for (int i = 0; i < decorChildCount; i++) {
                    View childAt = decorView.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (childAt.getId() == R.id.module_mediaplayer_id_player) {
                        return (PlayerView) childAt;
                    }
                }
            }
            throw new Exception("not find");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs args = playerView.getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            return args;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> getStartArgs -> " + e.getMessage());
            }
            return null;
        }
    }

    public final void startFull() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == args)
                throw new Exception("warning: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == args)
                throw new Exception("warning: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == args)
                throw new Exception("warning: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == component)
                throw new Exception("component error: null");
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
            if (null == component)
                throw new Exception("component error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.toggle();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> toggle -> " + e.getMessage());
            }
        }
    }

    public final void resume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> resume -> " + e.getMessage());
            }
        }
    }

    public final void resume(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> resume -> " + e.getMessage());
            }
        }
    }

    public final void pause() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> pause -> " + e.getMessage());
            }
        }
    }

    public final void pause(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> pause -> " + e.getMessage());
            }
        }
    }

    public final void release() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.release(false, true, true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> release -> " + e.getMessage());
            }
        }
    }

    /**
     * @param callEvent 透传event
     */
    public final void release(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.release(callEvent, false, true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> release -> " + e.getMessage());
            }
        }
    }

    public final void stop() {
        stop(true, false);
    }

    /**
     * @param callEvent 透传event
     */
    public final void stop(boolean callEvent, boolean updateUrl) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            if (updateUrl) {
                StartArgs startArgs = getStartArgs();
                if (null == startArgs) {
                    playerView.stop(callEvent, false);
                } else {
                    playerView.stop(callEvent, true);
                }
            } else {
                playerView.stop(callEvent, false);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> stop -> " + e.getMessage());
            }
        }
    }

    public final boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == args)
                throw new Exception("warning: args null");
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
            if (null == args)
                throw new Exception("warning: args null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.closeVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> openVolume -> " + e.getMessage());
            }
        }
    }

    public final long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
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
            if (null == args)
                throw new Exception("error: args null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.seekTo(postion);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> seekTo -> " + e.getMessage());
            }
        }
    }

    public final void setPlayerBackgroundColor(@ColorInt int color) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            String screenshot = playerView.screenshot();
            if (null == screenshot || screenshot.length() == 0)
                throw new Exception("error: null == screenshot || screenshot.length() == 0");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerEpisodeListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerEpisodeListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerWindowStateChangeListener(OnPlayerWindowStateChangeListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerWindowStateChangeListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerWindowStateChangeListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerEventListener(OnPlayerEventListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerEventListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerEventListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerProgressListener(OnPlayerProgressListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerProgressListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerProgressListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerWindowVisibilityChangedListener(OnPlayerWindowVisibilityChangedListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerWindowVisibilityChangedListener(listener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> setOnPlayerWindowVisibilityChangedListener -> " + e.getMessage());
            }
        }
    }

    public final void setOnPlayerWindowAttachChangedListener(OnPlayerWindowAttachChangedListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.clearPlayerListener();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> removeAllPlayerListener -> " + e.getMessage());
            }
        }
    }

    public final boolean showOnlyComponent(Class<ComponentApi> clazz) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.showOnlyComponent(clazz);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> showOnlyComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    public final boolean isEmptyComponent() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isEmptyComponent();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> isEmptyComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    public final void clearAllComponent() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.clearAllComponent();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> clearAllComponent -> " + e.getMessage());
            }
        }
    }

    public final StartArgs.Builder newBuilderStartArgs() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs args = playerView.getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.newBuilder();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> newBuilderStartArgs -> " + e.getMessage());
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
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.requestScreenOrientation(getContext(), value, formatScreen);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> requestScreenOrientation -> " + e.getMessage());
            }
            return false;
        }
    }
}
