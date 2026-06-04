
package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.PlayStateUtil;

public class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_init;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_init_root;
    }

    @Override
    public void onUpdateEvent(int playState) {

        boolean error = PlayStateUtil.isError(playState);
        if (error) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentInit -> callEvent -> show -> ERROR");
            }
            hide();
            return;
        }

        switch (playState) {
            case PlayerType.EventType.INIT:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> INIT");
                }
                show();
                break;
            case PlayerType.EventType.READY:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> INIT_READY");
                }
                hide();
                break;
            case PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> MEDIA_INFO_UPDATE_PLAYBACLK_SPEED");
                }
                hide();
                break;
            case PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_START:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_START");
                }
                break;
            case PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_END:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_END");
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
            case PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentInit -> callEvent -> MEDIA_INFO_PLAY_WHEN_READY_PAUSE");
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