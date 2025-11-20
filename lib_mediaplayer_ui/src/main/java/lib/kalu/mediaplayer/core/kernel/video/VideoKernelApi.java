package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

public interface VideoKernelApi extends VideoKernelApiHandler,
        VideoKernelApiBase,
        VideoKernelApiEvent,
        VideoKernelApiStartArgs,
        VideoKernelApiHls,
        VideoKernelApiSubtitle {

    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser);

    void startDecoder(Context context, StartArgs args);

    void initOptions(Context context, StartArgs args);

    void registListener();

    void unRegistListener();

    default void clear() {
        //
        clearArgs();
        //
        // releaseTimer();
    }

    default void createDecoder(Context context, StartArgs args) {
    }

    default void initDecoder(Context context, StartArgs args) {
        clear();
        try {
            setStartArgs(args);
        } catch (Exception e) {
        }

        long playWhenReadyDelayedTime = args.getPlayWhenReadyDelayedTime();
        // 延迟播放
        if (playWhenReadyDelayedTime > 0L) {
            @PlayerType.KernelType.Value
            int kernelType = args.getKernelType();
            startPlayWhenReadyDelayedTime(context, kernelType, playWhenReadyDelayedTime);
        }
        // 立即播放
        else {
            // 2
            initOptions(context, args);
            startDecoder(context, args);
        }
    }

    @Override
    default void initDecoderPlayWhenReadyDelayed(Context context) {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            // 2
            initOptions(context, args);
            startDecoder(context, args);
        } catch (Exception e) {
            if(LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApi => callPlayWhenReadyDelayedTimeComplete => Exception " + e.getMessage());
            }
        }
    }

    /***********/

    void setSurface(Surface surface, int w, int h);
}