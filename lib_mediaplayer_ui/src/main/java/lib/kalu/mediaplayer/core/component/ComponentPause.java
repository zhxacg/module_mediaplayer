package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

public class ComponentPause extends RelativeLayout implements ComponentApi {

    public ComponentPause(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_pause;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentPause -> dispatchKeyEvent -> action =  " + event.getAction() + ", keyCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                boolean prepared = isPrepared();
                if (!prepared)
                    throw new Exception("warning: prepared false");
                boolean playing = isPlaying();
                if (playing)
                    throw new Exception("warning: playing true");
                resume();
                return true;
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause -> dispatchKeyEvent -> Exception1 " + e.getMessage());
                }
            }
        }
        // keycode_enter
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            toggle();
            return true;
        }
        // keycode_dpad_center
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            toggle();
            return true;
        }
        return false;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.PAUSE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[show] -> PAUSE");
                }
                show();
                break;
            case PlayerType.EventType.RESUME:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[hide] -> RESUME");
                }
                hide();
                break;
            case PlayerType.EventType.COMPONENT_SEEK_SHOW:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[hide] -> COMPONENT_SEEK_SHOW");
                }
                try {
                    boolean componentShowing = isComponentShowing();
                    if (!componentShowing)
                        throw new Exception("warning: componentShowing false");
                    setActivated(true);
                    hide();
                } catch (Exception e) {
                }
                break;
            case PlayerType.EventType.COMPONENT_SEEK_HIDE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[show] -> COMPONENT_SEEK_HIDE");
                }
                break;
            case PlayerType.EventType.SEEK_FINISH:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[show] -> SEEK_FINISH");
                }
                try {
                    boolean activated = isActivated();
                    if (!activated)
                        throw new Exception("warning: activated false");
                    setActivated(false);
//                    show();
                } catch (Exception e) {
                }
                break;
            case PlayerType.EventType.COMPONENT_MENU_SHOW:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[show] -> playState = " + playState);
                }
                try {
                    boolean componentShowing = isComponentShowing();
                    if (!componentShowing)
                        throw new Exception("warning: componentShowing false");
                    setActivated(true);
                    hide();
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("ComponentPause -> callEventListener -> hide -> Exception2 " + playState);
                    }
                }
                break;
            case PlayerType.EventType.COMPONENT_MENU_HIDE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPause[gone] -> playState = " + playState);
                }
                try {
                    boolean activated = isActivated();
                    if (!activated)
                        throw new Exception("warning: activated false");
                    long trySeeDuration = getTrySeeDuration();
                    long position = getPosition();
                    long duration = getDuration();
                    onUpdateProgress(false, trySeeDuration, position, duration);
                    setActivated(false);
                    show();
                } catch (Exception e) {
                }
                break;
//            case PlayerType.EventType.START_PLAY_WHEN_READY_FALSE:
//                LogUtil.log("ComponentPause -> callEvent -> START_PLAY_WHEN_READY_FALSE");
//                try {
//                    boolean componentShowing = isComponentShowing();
//                    if (componentShowing)
//                        throw new Exception("warning: componentShowing true");
//                    setActivated(true);
//                    show();
//                } catch (Exception e) {
//                    LogUtil.log("ComponentPause -> callEvent -> Exception[START_PLAY_WHEN_READY_NO] " + e.getMessage());
//                }
//                break;
        }
    }


    @Override
    public void hide() {

        try {
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySee true");
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            ComponentApi.super.hide();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentPause -> hide -> Exception " + e.getMessage());
            }
        }

        try {
            setComponentText("");
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {

        try {
            long duration = getDuration();
//            LogUtil.log("ComponentPause -> show -> duration = " + duration);
            if (duration <= 0L) {
                duration = 0L;
            }
            long position = getPosition();
//            LogUtil.log("ComponentPause -> show -> position = " + position);
            if (position < 0L) {
                position = 0L;
            }
            long trySeeDuration = getTrySeeDuration();
//            LogUtil.log("ComponentPause -> show -> trySeeDuration = " + trySeeDuration);
            if (trySeeDuration < 0L) {
                trySeeDuration = 0L;
            }
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_pause_sb);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0L ? trySeeDuration : duration));
        } catch (Exception e) {
        }

        try {
            String mediaTitle = getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }

        try {
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySee true");
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            ComponentApi.super.show();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentPause -> show -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_pause_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_pause_title;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_pause_root;
    }
}