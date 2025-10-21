package lib.kalu.mediaplayer;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.util.LogUtil;


public class PlayerLayout extends RelativeLayout {

    public PlayerLayout(Context context) {
        super(context);
        initPlayerView();
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPlayerView();
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlayerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPlayerView();
    }

    private void initPlayerView() {
        clearOnPlayerListener();
        try {
            int childCount = getChildCount();
            if (childCount > 0)
                throw new Exception("childCount warning: " + childCount);
            PlayerView playerView = new PlayerView(getContext());
            playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            addView(playerView);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => initPlayerView => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => dispatchKeyEvent => " + e.getMessage());
            return false;
        }
    }

    /**********/

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
            //  LogUtil.log("PlayerLayout => getPlayerView => childCount = " + childCount + ", this = " + this);
            // sample
            if (childCount == 1) {
                return (PlayerView) getChildAt(0);
            }
            // not
            else {
                ViewGroup decorView = findDecorView(this);
                // LogUtil.log("PlayerLayout => getPlayerView => decorView = " + decorView);
                if (null == decorView)
                    throw new Exception("decorView error: null");
                int decorChildCount = decorView.getChildCount();
                // LogUtil.log("PlayerLayout => getPlayerView => decorChildCount = " + decorChildCount);
                for (int i = 0; i < decorChildCount; i++) {
                    View childAt = decorView.getChildAt(i);
                    //   LogUtil.log("PlayerLayout => getPlayerView => childAt = " + childAt);
                    if (null == childAt)
                        continue;
                    if (childAt.getId() == R.id.module_mediaplayer_id_player) {
                        return (PlayerView) childAt;
                    }
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    /**********/

    protected boolean enableReleaseTag() {
        return false;
    }

    protected boolean enableDetachedFromWindowTodo() {
        return false;
    }

    protected boolean enableWindowVisibilityChangedTodo(int visibility) {
        return false;
    }

    protected boolean enableAttachedToWindowTodo() {
        return false;
    }

    /**********/

    public final boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => isFull => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => isFloat => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getStartArgs => " + e.getMessage());
            return null;
        }
    }

    public final void startFull() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            String mediaUrl = args.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            boolean startFull = playerView.startFull();
            LogUtil.log("PlayerLayout => startFull => status = " + startFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => startFull => " + e.getMessage());
        }
    }

    public final void stopFull() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            String mediaUrl = args.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            boolean stopFull = playerView.stopFull();
            LogUtil.log("PlayerLayout => stopFull => status = " + stopFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => stopFull => " + e.getMessage());
        }
    }

    public final void startFloat() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            String mediaUrl = args.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            boolean startFull = playerView.startFloat();
            LogUtil.log("PlayerLayout => startFloat => status = " + startFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => startFloat => " + e.getMessage());
        }
    }

    //
    public final void stopFloat() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            String mediaUrl = args.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            boolean stopFull = playerView.stopFloat();
            LogUtil.log("PlayerLayout => stopFloat => status = " + stopFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => stopFloat => " + e.getMessage());
        }
    }

    public long getPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getPosition();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getPosition => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getDuration => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => setVideoScaleType => " + e.getMessage());
        }
    }

    public final void setVideoRotation(@PlayerType.RotationType.Value int rotationType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVideoRotation(rotationType);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setVideoRotation => " + e.getMessage());
        }
    }

    public final void addComponent(ComponentApi componentApi) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.addComponent(componentApi);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => addComponent => " + e.getMessage());
        }
    }

    public final void addAllComponent(List<ComponentApi> componentApis) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.addAllComponent(componentApis);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => addAllComponent => " + e.getMessage());
        }
    }

    public final <T extends ComponentApi> T findComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.findComponent(cls);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => findComponent => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => showComponent => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => hideComponent => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => toggle => " + e.getMessage());
        }
    }

    public final void resume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => resume => " + e.getMessage());
        }
    }

    public final void resume(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume(callEvent);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => resume => " + e.getMessage());
        }
    }

    public final void pause() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => pause => " + e.getMessage());
        }
    }

    public final void pause(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause(callEvent);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => pause => " + e.getMessage());
        }
    }

    public final void release() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            boolean releaseTag = enableReleaseTag();
            playerView.release(releaseTag, false, true);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => release => " + e.getMessage());
        }
    }

    /**
     * @param callEvent 透传event
     * @param clearTag  清除tag
     */
    public final void release(boolean callEvent, boolean clearTag) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.release(callEvent, clearTag, false);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => release => " + e.getMessage());
        }
    }

    public final void stop() {
        stop(true, true);
    }

    /**
     * @param callEvent 透传event
     * @param clearTag  清除tag
     */
    public final void stop(boolean callEvent, boolean clearTag) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stop(callEvent, clearTag);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => stop => " + e.getMessage());
        }
    }

    public final boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPlaying();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => isPlaying => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => isPrepared => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getMediaUrl => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => restart => " + e.getMessage());
        }
    }

    public void start(StartArgs args) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.start(args);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => start => " + e.getMessage());
        }
    }

    public final void setVolume(float left, float right) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVolume(left, right);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setVolume => " + e.getMessage());
        }
    }

    public final void setMute(boolean enable) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setMute(enable);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setMute => " + e.getMessage());
        }
    }

    public final long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("warning: args null");
            return args.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getPlayWhenReadySeekToPosition => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getTrySeeDuration => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => seekTo => " + e.getMessage());
        }
    }

//    public final void callEvent(@PlayerType.StateType.Value int state) {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            playerView.callEvent(state);
//        } catch (Exception e) {
//            LogUtil.log("PlayerLayout => callEvent => " + e.getMessage());
//        }
//    }

    public final void setPlayerBackgroundColor(@ColorInt int color) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setBackgroundColor(color);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setPlayerBackgroundColor => " + e.getMessage());
        }
    }

    public final void setSpeed(@PlayerType.SpeedType.Value int speed) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setSpeed(speed);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setPlayerBackgroundColor => " + e.getMessage());
        }
    }

    @PlayerType.SpeedType.Value
    public final int getSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getSpeed();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setPlayerBackgroundColor => " + e.getMessage());
            return PlayerType.SpeedType.DEFAULT;
        }
    }

//    public final boolean toggleTrackLanguageSubtitle(String language) {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.toggleTrackLanguageSubtitle(language);
//        } catch (Exception e) {
//            LogUtil.log("PlayerLayout => toggleTrackLanguageSubtitle => " + e.getMessage());
//            return false;
//        }
//    }
//
//    public final boolean toggleTrackLanguageAudio(String language) {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.toggleTrackLanguageAudio(language);
//        } catch (Exception e) {
//            LogUtil.log("PlayerLayout => toggleTrackLanguageAudio => " + e.getMessage());
//            return false;
//        }
//    }
//
//    public final boolean toggleTrackRoleFlagSubtitle(int roleFlag) {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.toggleTrackRoleFlagSubtitle(roleFlag);
//        } catch (Exception e) {
//            LogUtil.log("PlayerLayout => toggleTrackRoleFlagSubtitle => " + e.getMessage());
//            return false;
//        }
//    }
//
//    public final boolean toggleTrackRoleFlagAudio(int roleFlag) {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.toggleTrackRoleFlagAudio(roleFlag);
//        } catch (Exception e) {
//            LogUtil.log("PlayerLayout => toggleTrackRoleFlagAudio => " + e.getMessage());
//            return false;
//        }
//    }
//
//    public final boolean toggleTrackRoleFlagVideo(int roleFlag) {
//        try {
//            PlayerView playerView = getPlayerView();
//            if (null == playerView)
//                throw new Exception("playerView error: null");
//            return playerView.toggleTrackRoleFlagVideo(roleFlag);
//        } catch (Exception e) {
//            LogUtil.log("PlayerLayout => toggleTrackRoleFlagVideo => " + e.getMessage());
//            return false;
//        }
//    }

    public final boolean toggleTrack(TrackInfo trackInfo) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.toggleTrack(trackInfo);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => toggleTrack => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getTrackInfo => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getTrackInfoVideo => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getTrackInfoAudio => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getTrackInfoSubtitle => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => getSegments => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => screenshot => " + e.getMessage());
            return null;
        }
    }

    public final boolean appendSubtitleOffsetMs(int offset) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.appendSubtitleOffsetMs(offset);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => appendSubtitleOffsetMs => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => addSubtitleTrack => " + e.getMessage());
            return false;
        }
    }

    public final OnPlayerEpisodeListener getOnPlayerEpisodeListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerEpisodeListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerEpisodeListener => " + e.getMessage());
            return null;
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
            LogUtil.log("PlayerLayout => setOnPlayerEpisodeListener => " + e.getMessage());
        }
    }

    public final OnPlayerEventListener getOnPlayerEventListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerEventListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerEventListener => " + e.getMessage());
            return null;
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
            LogUtil.log("PlayerLayout => setOnPlayerEventListener => " + e.getMessage());
        }
    }

    public final OnPlayerProgressListener getOnPlayerProgressListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerProgressListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerProgressListener => " + e.getMessage());
            return null;
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
            LogUtil.log("PlayerLayout => setOnPlayerProgressListener => " + e.getMessage());
        }
    }

    public final OnPlayerWindowListener getOnPlayerWindowListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerWindowListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerWindowListener => " + e.getMessage());
            return null;
        }
    }

    public final void setOnPlayerWindowListener(OnPlayerWindowListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerWindowListener(listener);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setOnPlayerWindowListener => " + e.getMessage());
        }
    }

    public final void clearOnPlayerListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.clearOnPlayerListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => clearOnPlayerListener => " + e.getMessage());
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
            LogUtil.log("PlayerLayout => newBuilderStartArgs => " + e.getMessage());
            return null;
        }
    }
}
