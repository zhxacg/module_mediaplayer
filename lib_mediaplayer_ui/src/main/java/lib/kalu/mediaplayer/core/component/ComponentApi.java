package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.PlayerView;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.menu.Menu;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApi {

    /******************/

    default void onUpdateEvent(@PlayerType.EventType.Value int state) {
    }

    default void onUpdateWindow(@PlayerType.WindowType.Value int state) {
    }

    default void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {
    }

    default void onUpdateVolume(float volume) {
    }

    default void onUpdateScreenOrientation(@PlayerType.ScreenOrientation int value) {
    }

    default void onUpdateSubtitle(int kernel, CharSequence result) {
    }

    default void onUpdateNetSpeed(int kernel, CharSequence value) {
    }

    /******************/

    @LayoutRes
    int initLayoutId();

    @IdRes
    int initViewIdRoot();

    @IdRes
    default int initViewIdBackground() {
        return -1;
    }

    @IdRes
    default int initViewIdImage() {
        return -1;
    }

    @IdRes
    default int initViewIdText() {
        return -1;
    }

    default void inflate() {
        try {
            ViewGroup viewGroup = (ViewGroup) this;
            Context context = viewGroup.getContext();
            LayoutInflater.from(context).inflate(initLayoutId(), viewGroup, true);
        } catch (Exception e) {
        }
    }

    default void setComponentVisibility(@IdRes int id, int visibility) {
        try {
            View viewById = ((View) this).findViewById(id);
            if (null == viewById)
                throw new Exception("error: viewById null");
            viewById.setVisibility(visibility);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentVisibility -> " + e.getMessage());
            }
        }
    }

    default boolean isComponentShowing() {
        try {
            int rootId = initViewIdRoot();
            View viewById = ((View) this).findViewById(rootId);
            if (null == viewById)
                throw new Exception("error: viewById null");
            return viewById.getVisibility() == View.VISIBLE;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isComponentShowing -> " + e.getMessage());
            }
            return false;
        }
    }

    /******************/

    default void setComponentBackgroundColorInt(@ColorInt int v) {
        try {
            int layoutId = initViewIdBackground();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            View view = ((View) this).findViewById(layoutId);
            view.setBackgroundColor(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentBackgroundColorInt -> " + e.getMessage());
            }
        }
    }

    default void setComponentBackgroundColorRes(@ColorRes int v) {
        try {
            int layoutId = initViewIdBackground();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            View view = ((View) this).findViewById(layoutId);
            int color = ((View) this).getResources().getColor(v);
            view.setBackgroundColor(color);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentBackgroundColorRes -> " + e.getMessage());
            }
        }
    }

    default void setComponentBackgroundDrawableRes(@DrawableRes int v) {
        try {
            int layoutId = initViewIdBackground();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            View view = ((View) this).findViewById(layoutId);
            view.setBackgroundResource(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentBackgroundDrawableRes -> " + e.getMessage());
            }
        }
    }

    /******************/

    default void setComponentImageDrawableRes(@DrawableRes int v) {
        try {
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageResource(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentImageDrawableRes -> " + e.getMessage());
            }
        }
    }

    default void setComponentImageDrawable(Drawable drawable) {
        try {
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageDrawable(drawable);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentImageDrawable -> " + e.getMessage());
            }
        }
    }

    default void setComponentImageBitmap(Bitmap bitmap) {
        try {
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentImageBitmap -> " + e.getMessage());
            }
        }
    }

    default void setComponentImageUrl(@NonNull String imgUrl) {
        try {
            if (null == imgUrl || imgUrl.length() <= 0)
                throw new Exception("error: imgUrl null");
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageURI(Uri.parse(imgUrl));
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentImageUrl -> " + e.getMessage());
            }
        }
    }

    /******************/

    default void setComponentText(String v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setText(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentText -> " + e.getMessage());
            }
        }
    }

    default void setComponentText(@StringRes int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setText(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentText -> " + e.getMessage());
            }
        }
    }

    default void setComponentTextSize(@DimenRes int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            int offset = ((View) this).getResources().getDimensionPixelOffset(v);
            textView.setTextSize(offset);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentTextSize -> " + e.getMessage());
            }
        }
    }

    default void setComponentTextSize(float v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setTextSize(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentTextSize -> " + e.getMessage());
            }
        }
    }

    default void setComponentTextColorInt(@ColorInt int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setTextColor(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentTextColorInt -> " + e.getMessage());
            }
        }
    }

    default void setComponentTextColorRes(@ColorRes int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            int color = ((View) this).getResources().getColor(v);
            textView.setTextColor(color);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setComponentTextColorRes -> " + e.getMessage());
            }
        }
    }

    default void show() {
        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            int rootId = initViewIdRoot();
            setComponentVisibility(rootId, View.VISIBLE);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> show -> Exception " + e.getMessage());
            }
        }
    }

    default void hide() {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            int rootId = initViewIdRoot();
            setComponentVisibility(rootId, View.GONE);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> hide -> Exception " + e.getMessage());
            }
        }
    }

    /*******************/

    default PlayerView getPlayerView() {
        try {
            PlayerView playerView = null;
            View view = (View) this;
            while (true) {
                ViewParent parent = view.getParent();
                if (null == parent) {
                    break;
                } else if (parent instanceof PlayerView) {
                    playerView = (PlayerView) parent;
                    break;
                } else {
                    view = (View) parent;
                }
            }
            if (null == playerView)
                new Exception("not find");
            return playerView;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getPlayerView -> " + e.getMessage());
            }
            return null;
        }
    }

    default <T extends ComponentApi> T findComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            ComponentApi component = playerView.findComponent(cls);
            if (null == component)
                throw new Exception("warning: component null");
            return (T) component;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> findComponent -> " + e.getMessage());
            }
            return null;
        }
    }

    default void superCallEvent(boolean callPlayer, boolean callComponent, @PlayerType.EventType.Value int state) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            playerView.callEvent(callPlayer, callComponent, state);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> superCallEvent -> " + e.getMessage());
            }
        }
    }

    default boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isFull -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFloat();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isFloat -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isPlaying -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isPrepared() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPrepared();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isPrepared -> " + e.getMessage());
            }
            return false;
        }
    }

    default void seekTo(long position) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.seekTo(position);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> seekTo -> " + e.getMessage());
            }
        }
    }

    default void fastRewind(long step) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.fastRewind(step);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> fastRewind -> " + e.getMessage());
            }
        }
    }

    default void fastForward(long step) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.fastForward(step);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> fastForward -> " + e.getMessage());
            }
        }
    }

    default void resume() {
        resume(true);
    }

    default void resume(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> resume -> " + e.getMessage());
            }
        }
    }

    default void pause() {
        pause(true);
    }

    default void pause(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> pause -> " + e.getMessage());
            }
        }
    }

    default void toggle() {
        toggle(true);
    }

    default void toggle(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.toggle(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> toggle -> " + e.getMessage());
            }
        }
    }

    default void stop() {
        stop(true);
    }

    default void stop(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stop(callEvent);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> toggle -> " + e.getMessage());
            }
        }
    }

    default long getDuration() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            long duration = playerView.getDuration();
            if (duration < 0)
                throw new Exception("warning: duration<0");
            return duration;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    default long getPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            long position = playerView.getPosition();
            if (position < 0)
                throw new Exception("warning: position<0");
            return position;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getPosition -> " + e.getMessage());
            }
            return 0L;
        }
    }

    default void setPlaybackSpeed(float speed) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setSpeed(speed);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setSpeed -> " + e.getMessage());
            }
        }
    }

    default float getPlaybackSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getSpeed();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getSpeed -> " + e.getMessage());
            }
            return 1.0f;
        }
    }

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVideoScaleType(scaleType);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setVideoScaleType -> " + e.getMessage());
            }
        }
    }

    @PlayerType.ScaleType.Value
    default int getVideoScale() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getVideoScale();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getVideoScale -> " + e.getMessage());
            }
            return PlayerType.ScaleType.DEFAULT;
        }
    }

    default StartArgs getStartArgs() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs args = playerView.getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getStartArgs -> " + e.getMessage());
            }
            return null;
        }
    }

    default int getPlayPos() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            Menu menu = args.getMenu();
            if (null == menu)
                throw new Exception("error: menu null");
            return menu.getPlayPos();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getPlayPos -> " + e.getMessage());
            }
            return -1;
        }
    }

    default int getPlayCount() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            Menu menu = args.getMenu();
            if (null == menu)
                throw new Exception("error: menu null");
            return menu.getPlayCount();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getPlayCount -> " + e.getMessage());
            }
            return -1;
        }
    }

    default String getTitle() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getTitle();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getTitle -> " + e.getMessage());
            }
            return null;
        }
    }

    default long getTrySeeDuration() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getTrySeeDuration();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getTrySeeDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    default boolean isPlayWhenReady() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.isPlayWhenReady();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isPlayWhenReady -> " + e.getMessage());
            }
            return true;
        }
    }

    default long getPlayWhenReadyDelayedTime() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getPlayWhenReadyDelayedTime();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getPlayWhenReadyDelayedTime -> " + e.getMessage());
            }
            return 0L;
        }
    }

    default long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getPlayWhenReadySeekToPosition -> " + e.getMessage());
            }
            return 0L;
        }
    }

    default void closeVolume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.closeVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> closeVolume -> " + e.getMessage());
            }
        }
    }

    default void openVolume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.openVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> openVolume -> " + e.getMessage());
            }
        }
    }

    default void setVolume(@FloatRange(from = 0f, to = 1f) float left, @FloatRange(from = 0f, to = 1f) float right) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVolume(left, right);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setVolume -> " + e.getMessage());
            }
        }
    }

    default float getVolume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> getVolume -> " + e.getMessage());
            }
            return 0f;
        }
    }

    default boolean canBackPress(Context context) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.canBackPress(context);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> canBackPress -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isScreenOrientationPortrait() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isScreenOrientationPortrait();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isScreenOrientationPortrait -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isScreenOrientationLandspace() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isScreenOrientationLandspace();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> isScreenOrientationLandspace -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean setScreenOrientationPortrait() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.setScreenOrientationPortrait();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setScreenOrientationPortrait -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean setScreenOrientationLandspace() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.setScreenOrientationLandspace();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setScreenOrientationLandspace -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean setPlaybackSubtitleOffsetMs(int offsetMs) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.subtitleOffsetMs(offsetMs);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> setPlaybackSubtitleOffsetMs -> " + e.getMessage());
            }
            return false;
        }
    }

    default void restart(boolean isPlaySeek) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.restart(isPlaySeek);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> restart -> " + e.getMessage());
            }
        }
    }

    default void callPlayerEpisode(int position, int count) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.callPlayerEpisode(position, count);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentApi -> callPlayerEpisode -> " + e.getMessage());
            }
        }
    }
}