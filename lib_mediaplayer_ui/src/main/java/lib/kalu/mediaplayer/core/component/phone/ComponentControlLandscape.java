package lib.kalu.mediaplayer.core.component.phone;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;

public class ComponentControlLandscape extends RelativeLayout implements ComponentApi {

    private static final String TAG = "ComponentControlLandscape22";

    public ComponentControlLandscape(Context context) {
        super(context);
        inflate();
        initListener();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_phone_component_control_landscape;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_phone_control_landscape_root;
    }

    @Override
    public void callOrientation(boolean isVt) {
        if (isVt) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public void callEvent(int state) {
        if (state == PlayerType.EventType.INIT) {
            initTitle();
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        boolean componentShowing = isComponentShowing();
        if (!componentShowing)
            return;

        SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_phone_control_landscape_seekbar);
        seekBar.setMax((int) duration);
        if (!isFromUser) {
            seekBar.setProgress((int) position);
        }

        String strPosition = TimeUtil.formatTime(position, true);
        TextView textPosition = findViewById(R.id.module_mediaplayer_component_phone_control_landscape_position);
        textPosition.setText(strPosition);


        String strDuration = TimeUtil.formatTime(duration, true);
        TextView textDuration = findViewById(R.id.module_mediaplayer_component_phone_control_landscape_duration);
        textDuration.setText(strDuration);
    }

    private void initTitle() {
        String title = getTitle();
        TextView textView = findViewById(R.id.module_mediaplayer_component_phone_control_landscape_title);
        textView.setText(title);
    }

    private void initListener() {
        findViewById(R.id.module_mediaplayer_component_phone_control_landscape_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean canBackPress = canBackPress(getContext());
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onClick -> close -> canBackPress = " + canBackPress);
                }

                if (!canBackPress) {
                    setRequestedOrientation(getContext(), true);
                }
            }
        });

        findViewById(R.id.module_mediaplayer_component_phone_control_landscape_rewind).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                long position = getPosition();
                long nextPosition = position - 10000;
                if (nextPosition < 0) {
                    nextPosition = 1;
                }
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onClick -> rewind -> position = " + position + ", nextPosition = " + nextPosition);
                }
                seekTo(nextPosition);
            }
        });

        findViewById(R.id.module_mediaplayer_component_phone_control_landscape_forward).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                long position = getPosition();
                long duration = getDuration();
                long nextPosition = position + 10000;
                if (nextPosition > duration) {
                    nextPosition = duration;
                }
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onClick -> forward -> position = " + position + ", duration = " + duration + ", nextPosition = " + nextPosition);
                }
                seekTo(nextPosition);
            }
        });

        ((CheckBox) findViewById(R.id.module_mediaplayer_component_phone_control_landscape_toggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onCheckedChanged -> voice -> b = " + b);
                }

                if (b) {
                    resume();
                } else {
                    pause();
                }
            }
        });


        ((SeekBar) findViewById(R.id.module_mediaplayer_component_phone_control_landscape_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
                seekTo(seekBar.getProgress());
            }
        });
    }
}
