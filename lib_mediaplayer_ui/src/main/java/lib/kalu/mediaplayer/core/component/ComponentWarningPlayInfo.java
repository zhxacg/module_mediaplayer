package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

public class ComponentWarningPlayInfo extends RelativeLayout implements ComponentApi {

    public ComponentWarningPlayInfo(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_warning_play_info;
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
//            hide();
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onUpdateEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.COMPONENT_SEEK_SHOW:
            case PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentWarningPlayInfo -> callEvent -> START_PLAY_WHEN_READY_FALSE");
                }
                hide();
                break;
            case PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentWarningPlayInfo -> callEvent -> MEDIA_INFO_UPDATE_PLAYBACLK_SPEED");
                }
                show();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        // 自动隐藏
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            long seek = getPlayWhenReadySeekToPosition();
            long cast = position - seek;
            if (cast < 2000L)
                throw new Exception("warning: cast < 2000");
            hide();
        } catch (Exception e) {
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
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_play_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0 ? trySeeDuration : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
//        LogUtil.log("ComponentWarningPlayInfo -> show");

        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySee true");
            // 1
            ComponentApi.super.show();
            // 2. 标题
            TextView titleView = findViewById(R.id.module_mediaplayer_component_warning_play_info_title);
            titleView.setText(getTitle());
            // 3. 播放记录提示
            long seek = getPlayWhenReadySeekToPosition();
            if (seek > 0L) {
                String millis = TimeUtil.formatTimeMillis(seek);
                String string = getResources().getString(R.string.module_mediaplayer_string_play_record, millis);
                TextView textView = findViewById(R.id.module_mediaplayer_component_warning_play_info_record);
                textView.setText(string);
            }
        } catch (Exception e) {
        }

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration<=0");
            long position = getPosition();
            long trySeeDuration = getTrySeeDuration();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_play_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0L ? trySeeDuration : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void hide() {
//        LogUtil.log("ComponentWarningPlayInfo -> hide");

        try {
            // 1
            ComponentApi.super.hide();
            // 2. 标题
            TextView titleView = findViewById(R.id.module_mediaplayer_component_warning_play_info_title);
            titleView.setText("");
            // 3. 播放记录提示
            TextView recordView = findViewById(R.id.module_mediaplayer_component_warning_play_info_record);
            recordView.setText("");
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentWarningPlayInfo -> hide -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_warning_play_info_root;
    }
}
