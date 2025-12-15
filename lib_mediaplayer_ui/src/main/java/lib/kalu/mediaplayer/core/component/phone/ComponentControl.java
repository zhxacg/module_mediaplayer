package lib.kalu.mediaplayer.core.component.phone;

import android.app.Activity;
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
import lib.kalu.mediaplayer.init.PlayerInitProvider;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;

public class ComponentControl extends RelativeLayout implements ComponentApi {

    private static final String TAG = "ComponentControl22";

    public ComponentControl(Context context) {
        super(context);
        inflate();

        findViewById(R.id.module_mediaplayer_component_phone_control_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean canBackPress = canBackPress(getContext());
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onClick -> back -> canBackPress = " + canBackPress);
                }

                if (canBackPress) {
                    Activity activity = PlayerInitProvider.getCurrentActivity();
                    if (null != activity) {
                        activity.finish();
                    }
                } else {
                    setRequestedOrientation(getContext(), true);
                }
            }
        });

        ((CheckBox) findViewById(R.id.module_mediaplayer_component_phone_control_toggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onCheckedChanged -> toggle -> b = " + b);
                }

                if (b) {
                    resume();
                } else {
                    pause();
                }
            }
        });

        ((CheckBox) findViewById(R.id.module_mediaplayer_component_phone_control_voice)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onCheckedChanged -> voice -> b = " + b);
                }

                if (b) {
                    openVolume();
                } else {
                    closeVolume();
                }
            }
        });

        findViewById(R.id.module_mediaplayer_component_phone_control_full).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onClick -> full");
                }
                setRequestedOrientation(getContext(), false);
            }
        });
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_phone_component_control;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_phone_control_root;
    }

    @Override
    public void callEvent(int state) {
        if (state == PlayerType.EventType.VIDEO_RENDERING_START) {
            show();
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        boolean componentShowing = isComponentShowing();
        if (!componentShowing)
            return;

        SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_phone_control_seekbar);
        seekBar.setMax((int) duration);
        seekBar.setProgress((int) position);

        String time = TimeUtil.formatTime(position, duration, true);
        TextView textView = findViewById(R.id.module_mediaplayer_component_phone_control_time);
        textView.setText(time);
    }
}
