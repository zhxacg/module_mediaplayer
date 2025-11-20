package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentComplete extends RelativeLayout implements ComponentApi {

    public ComponentComplete(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_complete;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_complete_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.COMPLETE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentComplete[show] => playState = " + playState);
                }
                show();
                break;
            case PlayerType.EventType.INIT:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentComplete[gone] => playState = " + playState);
                }
                hide();
                break;
        }
    }

    /*************/

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_complete_bg;
    }

    @Override
    public int initViewIdImage() {
        return R.id.module_mediaplayer_component_complete_icon;
    }
}
