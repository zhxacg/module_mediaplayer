package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;

public class ComponentGesture extends RelativeLayout implements ComponentApi {

    public ComponentGesture( Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_gesture;
    }

    @Override
    public int initViewIdRoot() {
        return 0;
    }

//    @Override
//    public final void onStartSlide() {
//        mControllerWrapper.hide();
//        View viewRoot = findViewById(R.id.module_mediaplayer_controller_gesture_root);
//        viewRoot.setVisibility(VISIBLE);
//        viewRoot.setAlpha(1f);
//    }
//
//    /**
//     * 结束滑动
//     * 这个是指，手指抬起或者意外结束事件的时候，调用这个方法
//     */
//    @Override
//    public final void onStopSlide() {
//        View viewRoot = findViewById(R.id.module_mediaplayer_controller_gesture_root);
//        viewRoot.animate()
//                .alpha(0f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public final void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        viewRoot.setVisibility(GONE);
//                    }
//                })
//                .start();
//    }
//
//    /**
//     * 滑动调整进度
//     *
//     * @param slidePosition   滑动进度
//     * @param currentPosition 当前播放进度
//     * @param duration        视频总长度
//     */
//    @Override
//    public final void onPositionChange(int slidePosition, int currentPosition, int duration) {
//        MPLogUtil.log("onPositionChange -> slidePosition = " + slidePosition + ", currentPosition = " + currentPosition + ", duration = " + duration);
//        TextView viewText = findViewById(R.id.module_mediaplayer_controller_gesture_text);
//        if (slidePosition > currentPosition) {
//            viewText.setText("快进\n" + String.format("%s/%s", PlayerUtils.formatTime(slidePosition), PlayerUtils.formatTime(duration)));
//        } else {
//            viewText.setText("快退\n" + String.format("%s/%s", PlayerUtils.formatTime(slidePosition), PlayerUtils.formatTime(duration)));
//        }
//        ProgressBar viewProgress = findViewById(R.id.module_mediaplayer_controller_gesture_progress);
//        viewProgress.setVisibility(GONE);
//    }
//
//    /**
//     * 滑动调整亮度
//     *
//     * @param percent 亮度百分比
//     */
//    @Override
//    public final void onBrightnessChange(int percent) {
//        TextView viewText = findViewById(R.id.module_mediaplayer_controller_gesture_text);
//        viewText.setText("亮度\n" + percent + "%");
//        ProgressBar viewProgress = findViewById(R.id.module_mediaplayer_controller_gesture_progress);
//        viewProgress.setVisibility(VISIBLE);
//        viewProgress.setProgress(percent);
//    }
//
//    /**
//     * 滑动调整音量
//     *
//     * @param percent 音量百分比
//     */
//    @Override
//    public final void onVolumeChange(int percent) {
//        TextView viewText = findViewById(R.id.module_mediaplayer_controller_gesture_text);
//        if (percent <= 0) {
//            viewText.setText("静音\n" + percent + "%");
//        } else {
//            viewText.setText("音量\n" + percent + "%");
//        }
//        ProgressBar viewProgress = findViewById(R.id.module_mediaplayer_controller_gesture_progress);
//        viewProgress.setVisibility(VISIBLE);
//        viewProgress.setProgress(percent);
//    }
//
//    @Override
//    public final void onPlayStateChanged(int playState) {
//        if (playState == PlayerType.StateType.INIT
//                || playState == PlayerType.StateType.START_ABORT
//                || playState == PlayerType.StateType.LOADING_START
//                || playState == PlayerType.StateType.LOADING_STOP
//                || playState == PlayerType.StateType.ERROR
//                || playState == PlayerType.StateType.BUFFERING_START
//                || playState == PlayerType.StateType.ONCE_LIVE) {
//            setVisibility(GONE);
//        } else {
//            setVisibility(VISIBLE);
//        }
//    }
//
//    @Override
//    public final void onLockStateChanged(boolean isLock) {
//
//    }
}
