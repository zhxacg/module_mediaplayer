package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.PlayStateUtil;

public class ComponentPrepareGradient extends RelativeLayout implements ComponentApi {

    public ComponentPrepareGradient(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_prepare_gradient;
    }

    @Override
    public void onUpdateEvent(int playState) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentPrepareGradient -> onUpdateEvent -> playState = " + playState);
        }

        boolean error = PlayStateUtil.isError(playState);
        if (error) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentPrepareGradient -> callEvent -> show -> ERROR");
            }
            hide();
            return;
        }


        switch (playState) {
            case PlayerType.EventType.INIT:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPrepareGradient -> callEvent -> INIT");
                }
                show();
                break;
            case PlayerType.EventType.MEDIA_INFO_VIDEO_RENDERING_START:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPrepareGradient -> callEvent -> MEDIA_INFO_VIDEO_RENDERING_START");
                }
            case PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPrepareGradient -> callEvent -> MEDIA_INFO_UPDATE_PLAYBACLK_SPEED");
                }
                hide();
                break;
        }
    }

    @Override
    public void onUpdateBandwidth(int kernel, long totalLoadTimeMs, long netKBps, long curKBps) {

        TextView textView = findViewById(R.id.module_mediaplayer_component_prepare_gradient_net);
        textView.setText(netKBps + ":" + curKBps);
    }

    @Override
    public void show() {
        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPrepareGradient -> show -> warning: componentShowing true");
                }
                return;
            }
            // 1
            ComponentApi.super.show();
            // 2
            String title = getTitle();
            int playPos = getPlayPos();
            if (playPos < 0) {
                setComponentText(title);
            } else {
                String string = getResources().getString(R.string.module_mediaplayer_string_title, title, playPos + 1);
                setComponentText(string);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentPrepareGradient -> show -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void hide() {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentPrepareGradient -> hide -> warning: componentShowing false");
                }
                return;
            }
            // 1
            ComponentApi.super.hide();
            // 2
            setComponentText("");
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentPrepareGradient -> hide -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_prepare_gradient_root;
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_prepare_gradient_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_prepare_gradient_name;
    }

}