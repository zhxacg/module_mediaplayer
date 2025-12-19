package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentError extends RelativeLayout implements ComponentApi {

    public ComponentError(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_error;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_error_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.ERROR:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentError[show] -> playState = " + playState);
                }
                show();
                break;
            case PlayerType.EventType.INIT:
            case PlayerType.EventType.RESUME:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentError[gone] -> playState = " + playState);
                }
                hide();
                break;
        }
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_error_bg;
    }

    @Override
    public int initViewIdImage() {
        return R.id.module_mediaplayer_component_error_icon;
    }
}
