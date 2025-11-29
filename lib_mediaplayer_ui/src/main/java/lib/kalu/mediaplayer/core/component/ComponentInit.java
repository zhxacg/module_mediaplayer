
package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_init;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_init_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.INIT:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> INIT");
                }
                show();
                break;
            case PlayerType.EventType.INIT_READY:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> INIT_READY");
                }
                hide();
                break;
            case PlayerType.EventType.VIDEO_RENDERING_START:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> VIDEO_RENDERING_START");
                }
                hide();
                break;
            case PlayerType.EventType.ERROR:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> ERROR");
                }
                hide();
                break;
            case PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_START:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> INIT_PLAY_WHEN_READY_DELAYED_TIME_START");
                }
                break;
            case PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_COMPLETE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> INIT_PLAY_WHEN_READY_DELAYED_TIME_COMPLETE");
                }
                break;
            case PlayerType.EventType.START:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> START");
                }
                break;
            case PlayerType.EventType.PAUSE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> PAUSE");
                }
                break;
            case PlayerType.EventType.START_PLAY_WHEN_READY_TRUE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> START_PLAY_WHEN_READY_TRUE");
                }
                break;
            case PlayerType.EventType.START_PLAY_WHEN_READY_FALSE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> START_PLAY_WHEN_READY_FALSE");
                }
                break;
        }
    }

    @Override
    public void show() {
        ComponentApi.super.show();
        // 1
        String string = getResources().getString(R.string.module_mediaplayer_string_init);
        String title = getTitle();
        setComponentText(string + title);
    }

    @Override
    public void hide() {
        ComponentApi.super.hide();
        // 1
        setComponentText("");
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_init_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_init_txt;
    }
}