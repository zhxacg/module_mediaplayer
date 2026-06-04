package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

/**
 * 试看
 */
public class ComponentWarningTrySee extends RelativeLayout implements ComponentApi {

    public ComponentWarningTrySee(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_warning_try_see_root;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_warning_try_see_title;
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_warning_try_see;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // keycode_enter || keycode_dpad_center
        if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER)) {
            try {
                long trySeeDuration = getTrySeeDuration();
                if (trySeeDuration <= 0L)
                    throw new Exception("warning: trySeeDuration <= 0L");
                toggle();
                return true;
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentWarningTrySee -> dispatchKeyEvent -> Exception " + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public void onUpdateEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.PAUSE:
                try {
                    ImageView imageView = findViewById(R.id.module_mediaplayer_component_warning_try_see_state);
                    imageView.setImageResource(R.drawable.lib_mp_ic_pause);
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("ComponentWarningTrySee -> callEvent -> PAUSE -> Exception " + e.getMessage());
                    }
                }
                break;
            case PlayerType.EventType.RESUME:
                try {
                    ImageView imageView = findViewById(R.id.module_mediaplayer_component_warning_try_see_state);
                    imageView.setImageResource(R.drawable.lib_mp_ic_resume);
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("ComponentWarningTrySee -> callEvent -> RESUME -> Exception " + e.getMessage());
                    }
                }
                break;
            case PlayerType.EventType.START_VIDEO_RENDERING:
                try {
                    long trySeeDuration = getTrySeeDuration();
                    if (trySeeDuration <= 0L)
                        throw new Exception("warning: trySee false");
                    boolean componentShowing = isComponentShowing();
                    if (componentShowing)
                        throw new Exception("warning: componentShowing true");
                    String mediaTitle = getTitle();
                    setComponentText(mediaTitle + " 试看开始...");
                    show();
                } catch (Exception e) {
                }
                break;
            case PlayerType.EventType.TRY_SEE_END:
                try {
                    long trySeeDuration = getTrySeeDuration();
                    if (trySeeDuration <= 0L)
                        throw new Exception("warning: trySee false");
                    boolean componentShowing = isComponentShowing();
                    if (!componentShowing)
                        throw new Exception("warning: componentShowing false");
                    String mediaTitle = getTitle();
                    setComponentText(mediaTitle + " 试看结束...");
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentWarningTrySee -> onUpdateProgress");
        }

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            if (position < 0) {
                position = 0;
            }
            if (duration < 0) {
                duration = 0;
            }
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_try_see_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0 ? trySeeDuration : duration));
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentWarningTrySee -> onUpdateProgress -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void show() {
        ComponentApi.super.show();

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration<=0");
            long position = getPosition();
            long trySeeDuration = getTrySeeDuration();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_try_see_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0L ? trySeeDuration : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void pause() {
        ComponentApi.super.pause();
    }

    @Override
    public void resume() {
        ComponentApi.super.resume();
    }
}
