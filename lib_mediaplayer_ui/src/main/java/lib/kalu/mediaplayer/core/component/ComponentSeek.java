package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSeek extends RelativeLayout implements ComponentApi {

    public ComponentSeek(Context context) {
        super(context);
        inflate();
        lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    hide();
                    int progress = seekBar.getProgress();
                    seekTo(progress);
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_seek_root;
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_seek;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSeek -> dispatchKeyEvent -> action =  " + event.getAction() + ", keyCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());
        }

        try {
            // 试看
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySeeDuration > 0L");
            // seekForward -> start
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getRepeatCount() == 0) {
                onUpdateProgress(true, -2, -2, -2);
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                //
                show();
                //
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                int duration = seekBar.getMax();
                if (progress >= duration)
                    throw new Exception("warning: progress >= duration");
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress += 8000;
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress += 4000;
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress += 1000;
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress += 400;
                }
                // 时间太短了
                else {
                    progress += 100;
                }
                if (progress >= duration) {
                    progress = duration;
                }
                onUpdateProgress(true, trySeeDuration, progress, duration);
                return true;
            }
            // seekForward -> longPress
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                //
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                int duration = seekBar.getMax();
                if (progress >= duration)
                    throw new Exception("warning: progress > duration");
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress += 60000;
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress += 30000;
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress += 10000;
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress += 5000;
                }
                // 时间太短了
                else {
                    progress += 1000;
                }
                if (progress >= duration) {
                    progress = duration;
                }
                onUpdateProgress(true, trySeeDuration, progress, duration);
                return true;
            }
            // seekForward -> stop
            else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                //
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                seekTo(progress);
                //
                onUpdateProgress(true, -1, -1, -1);
                //
                hide();
                return true;
            }
            // seekRewind -> start
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getRepeatCount() == 0) {
                onUpdateProgress(true, -2, -2, -2);
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                //
                show();
                //
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                if (progress <= 0)
                    throw new Exception("warning: progress <=0");
                int duration = seekBar.getMax();
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress -= 8000;
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress -= 4000;
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress -= 1000;
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress -= 400;
                }
                // 时间太短了
                else {
                    progress -= 100;
                }
                if (progress <= 0) {
                    progress = 0;
                }
                onUpdateProgress(true, trySeeDuration, progress, duration);
                return true;
            }
            // seekRewind -> longPress
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                //
                //
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                if (progress <= 0)
                    throw new Exception("warning: progress <=0");
                int duration = seekBar.getMax();
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress -= 60000;
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress -= 30000;
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress -= 10000;
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress -= 5000;
                }
                // 时间太短了
                else {
                    progress -= 1000;
                }
                if (progress <= 0) {
                    progress = 0;
                }
                onUpdateProgress(true, trySeeDuration, progress, duration);
                return true;
            }
            // seekRewind -> stop
            else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                //
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                seekTo(progress);
                //
                onUpdateProgress(true, -1, -1, -1);
                //
                hide();
                return true;
            }

            throw new Exception("warning: not find");
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentSeek -> dispatchKeyEvent -> Exception " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
//            case PlayerType.EventType.SEEK_START_FORWARD:
//                LogUtil.log("ComponentSeek -> callEvent -> SEEK_START_FORWARD");
//                show();
//                break;
//            case PlayerType.EventType.SEEK_START_REWIND:
//                LogUtil.log("ComponentSeek -> callEvent -> SEEK_START_REWIND");
//                show();
//                break;
//            case PlayerType.EventType.SEEK_FINISH:
//                LogUtil.log("ComponentSeek -> callEvent -> SEEK_FINISH");
//                hide();
//                long duration = getDuration();
//                long position = getPosition();
//                long trySeeDuration = getTrySeeDuration();
//                onUpdateProgress(false, trySeeDuration, position, duration);
//                break;
//            case PlayerType.EventType.INIT:
//            case PlayerType.EventType.ERROR:
//            case PlayerType.EventType.COMPLETE:
//                onUpdateProgress(false, 0, 0, 0);
//                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long progress, long duration) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSeek -> onUpdateProgress -> isFromUser = " + isFromUser + ", trySeeDuration = " + trySeeDuration + ", progress = " + progress + ", duration = " + duration);
        }

        lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);

        if (isFromUser && progress == -2 && duration == -2) {
            seekBar.setHovered(true);
        } else if (isFromUser && progress == -1 && duration == -1) {
            seekBar.setHovered(false);
        } else if (isFromUser) {
            seekBar.setMax((int) duration);
            seekBar.setProgress((int) progress);
        } else {
            seekBar.setMax((int) duration);
            boolean hovered = seekBar.isHovered();
            if (!hovered) {
                seekBar.setProgress((int) progress);
            }
            seekBar.setTextInfo(progress, duration);
        }
    }
}
