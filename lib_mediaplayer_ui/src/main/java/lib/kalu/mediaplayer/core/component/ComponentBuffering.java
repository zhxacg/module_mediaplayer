package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.PlayStateUtil;

public class ComponentBuffering extends RelativeLayout implements ComponentApi {

    public ComponentBuffering(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_buffering;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_buffering_root;
    }

    @Override
    public void onUpdateEvent(int playState) {

        boolean error = PlayStateUtil.isError(playState);
        if (error) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentBuffering -> callEvent -> show -> ERROR");
            }
            hide();
            return;
        }

        switch (playState) {
            case PlayerType.EventType.MEDIA_INFO_BUFFERING_START:
            case PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_FORWARD:
            case PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_REWIND:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentBuffering[show] -> callEvent -> playState = " + playState);
                }
                show();
                break;
            case PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP:
            case PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_FINISH:
            case PlayerType.EventType.INIT:
            case PlayerType.EventType.RELEASE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentBuffering[hide] -> callEvent -> playState = " + playState);
                }
                hide();
                break;
        }
    }
}
