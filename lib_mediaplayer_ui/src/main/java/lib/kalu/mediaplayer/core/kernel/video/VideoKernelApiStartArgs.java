package lib.kalu.mediaplayer.core.kernel.video;

import java.util.HashMap;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiStartArgs extends VideoKernelApiBase {

    String TAG = "VideoKernelApiStartArgs22";


    /***************/

    default void setStartArgs(StartArgs args) {
    }

    default StartArgs getStartArgs() {
        return null;
    }

    /***************/

    default long getTrySeeDuration() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiBase -> getTrySeeDuration -> error: args null");
                }
                return 0L;
            }
            return args.getTrySeeDuration();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiBase -> getTrySeeDuration -> Exception " + e.getMessage());
            }
            return 0L;
        }
    }

    default boolean isLiveStream() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiBase -> isLiveStream -> error: startArgs null");
                }
                return false;
            }
            return startArgs.isLiveStream();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiBase -> isLiveStream -> Exception " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isPlayWhenReady() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiBase -> isPlayWhenReady -> error: args null");
                }
                return false;
            }
            return args.isPlayWhenReady();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiBase -> isPlayWhenReady -> Exception " + e.getMessage());
            }
            return false;
        }
    }

    default long getPlayWhenReadyDelayedTime() {
        try {
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiBase -> getPlayWhenReadyDelayedTime -> error: args null");
                }
                return 0L;
            }
            return args.getPlayWhenReadyDelayedTime();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiBase -> getPlayWhenReadyDelayedTime -> Exception " + e.getMessage());
            }
            return 0L;
        }
    }

    default long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiBase -> getPlayWhenReadySeekToPosition -> args = " + args);
            }
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiBase -> getPlayWhenReadySeekToPosition -> error: args null");
                }
                return 0L;
            }
            return args.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiBase -> getPlayWhenReadySeekToPosition -> Exception " + e.getMessage());
            }
            return 0L;
        }
    }

    /***************/

    HashMap<VideoKernelApiBase, Boolean> mDoWindowing = new HashMap<>();

    default void setDoWindowing(boolean v) {
        mDoWindowing.put(this, v);
    }

    default boolean isDoWindowing() {
        try {
            Boolean aBoolean = mDoWindowing.get(this);
            if (null == aBoolean) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiStartArgs -> isDoWindowing -> warning: aBoolean null");
                }
                return false;
            }
            return aBoolean;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiStartArgs -> isDoWindowing -> Exception " + e.getMessage());
            }
            return false;
        }
    }
}