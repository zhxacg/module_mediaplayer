package lib.kalu.mediaplayer.core.component.phone;

import android.content.Context;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentBufferingLottie extends RelativeLayout implements ComponentApi {

    public ComponentBufferingLottie(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_common_component_buffering_lottie;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.lib_mp_common_component_buffering_lottie_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.BUFFERING_START:
            case PlayerType.EventType.SEEK_START_FORWARD:
            case PlayerType.EventType.SEEK_START_REWIND:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentBuffering[show] -> callEvent -> playState = " + playState);
                }
                show();
                break;
            case PlayerType.EventType.BUFFERING_STOP:
            case PlayerType.EventType.SEEK_FINISH:
            case PlayerType.EventType.INIT:
            case PlayerType.EventType.ERROR:
            case PlayerType.EventType.RELEASE:
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentBuffering[hide] -> callEvent -> playState = " + playState);
                }
                hide();
                break;
        }
    }

    @Override
    public final void show() {
        ComponentApi.super.show();

        //
        LottieAnimationView lottieAnimationView = findViewById(R.id.lib_mp_common_component_buffering_lottie_view);
        startLottie(lottieAnimationView);
    }

    @Override
    public final void hide() {

        //
        LottieAnimationView lottieAnimationView = findViewById(R.id.lib_mp_common_component_buffering_lottie_view);
        closeLottie(lottieAnimationView);
    }

    public void startLottie(LottieAnimationView lottieAnimationView) {

    }

    public void closeLottie(LottieAnimationView lottieAnimationView) {

    }
}
