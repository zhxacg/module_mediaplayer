package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSurfaceCover extends RelativeLayout implements ComponentApi {

    public ComponentSurfaceCover(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_surface_cover;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.START:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentSurfaceCover => callEventListener => show => playState = " + playState);
                }
                hide();
                break;
            case PlayerType.EventType.INIT:
            case PlayerType.EventType.RELEASE:
            case PlayerType.EventType.ERROR:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentSurfaceCover => callEventListener => gone => playState = " + playState);
                }
                show();
                break;
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_surface_cover_root;
    }
}
