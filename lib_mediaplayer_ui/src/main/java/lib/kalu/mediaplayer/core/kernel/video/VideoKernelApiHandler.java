package lib.kalu.mediaplayer.core.kernel.video;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
public interface VideoKernelApiHandler extends VideoKernelApiBase, VideoKernelApiEvent, VideoKernelApiStartArgs {

    String TAG = "VideoKernelApiHandler22";
    int WHAT_PlayWhenReadyDelayedTime = 1000;
    int WHAT_ConnectTimeout = 2000;
    int WHAT_CheckPreparedPlaying = 3000;
    int WHAT_ProgressUpdate = 4000;
    int WHAT_BufferingTimeout = 5000;
    int WHAT_UPDATE_SPEED = 6000;

    /***********/


    default void startPlayWhenReadyDelayedTime(Context context, @PlayerType.KernelType.Value int kernelType, long delayedTime) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "startPlayWhenReadyDelayedTime ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            //
            onEvent(kernelType, PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_START);
            //
            Message message = Message.obtain();
            message.what = WHAT_PlayWhenReadyDelayedTime;
            message.arg1 = kernelType;
            message.obj = context;
            handler.sendMessageDelayed(message, delayedTime);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startPlayWhenReadyDelayedTime -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageConnectTimeout(@PlayerType.KernelType.Value int kernelType, long timeMillis, long timeout, boolean delay) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "sendMessageConnectTimeout ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            Message message = Message.obtain();
            message.what = WHAT_ConnectTimeout;
            message.arg1 = kernelType;
            message.obj = new long[]{timeMillis, timeout};
            handler.sendMessageDelayed(message, delay ? 1000 : 0);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "sendMessageConnectTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesConnectTimeout() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "removeMessagesConnectTimeout ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeMessages(WHAT_ConnectTimeout);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "removeMessagesConnectTimeout -> Exception " + e.getMessage());
            }
        }
    }

    /***********/

    default void sendMessageCheckPreparedPlaying(@PlayerType.KernelType.Value int kernelType) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "sendMessageCheckPreparedPlaying ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            Message message = Message.obtain();
            message.what = WHAT_CheckPreparedPlaying;
            message.arg1 = kernelType;
            handler.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "sendMessageCheckPreparedPlaying -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageProgressUpdate(@PlayerType.KernelType.Value int kernelType, boolean delay) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "sendMessageProgressUpdate ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            Message message = Message.obtain();
            message.what = WHAT_ProgressUpdate;
            message.arg1 = kernelType;
            handler.sendMessageDelayed(message, delay ? 1000 : 0);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "sendMessageProgressUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesProgressUpdate() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "removeMessagesProgressUpdate ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeMessages(WHAT_ProgressUpdate);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "removeMessagesProgressUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageSpeedUpdate(@PlayerType.KernelType.Value int kernelType, boolean delay) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "sendMessageSpeedUpdate ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            Message message = Message.obtain();
            message.what = WHAT_UPDATE_SPEED;
            message.arg1 = kernelType;
            handler.sendMessageDelayed(message, delay ? 1000 : 0);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "sendMessageSpeedUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesSpeedUpdate() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "removeMessagesSpeedUpdate ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeMessages(WHAT_UPDATE_SPEED);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "removeMessagesSpeedUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void startMessageBufferingTimeout(@PlayerType.KernelType.Value int kernelType, long maxTimeoutMs) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "startMessageBufferingTimeout -> kernelType = " + kernelType + ", maxTimeoutMs = " + maxTimeoutMs);
        }

        try {
            if (maxTimeoutMs <= 0)
                throw new Exception("warning: maxTimeoutMs <= 0, maxTimeoutMs = " + maxTimeoutMs);
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            Message message = Message.obtain();
            message.what = WHAT_BufferingTimeout;
            message.arg1 = kernelType;
            message.obj = new long[]{System.currentTimeMillis(), maxTimeoutMs};
            handler.sendMessageDelayed(message, 1000);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startMessageBufferingTimeout -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startMessageBufferingTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default void closeMessagesBufferingTimeout() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "closeMessagesBufferingTimeout ->");
        }

        try {
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeMessages(WHAT_BufferingTimeout);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "closeMessagesBufferingTimeout -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "closeMessagesBufferingTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default void nextMessageBufferingTimeout(@PlayerType.KernelType.Value int kernelType, long startTimeMillis, long maxTimeoutMs) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "nextMessageBufferingTimeout -> kernelType = " + kernelType + ", startTimeMillis = " + startTimeMillis + ", maxTimeoutMs = " + maxTimeoutMs);
        }

        try {
            if (maxTimeoutMs <= 0)
                throw new Exception("warning: maxTimeoutMs <= 0, maxTimeoutMs = " + maxTimeoutMs);
            Handler handler = getHandler();
            if (null == handler)
                throw new Exception("warning: handler null");
            Message message = Message.obtain();
            message.what = WHAT_BufferingTimeout;
            message.arg1 = kernelType;
            message.obj = new long[]{startTimeMillis, maxTimeoutMs};
            handler.sendMessageDelayed(message, 1000);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "nextMessageBufferingTimeout -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "nextMessageBufferingTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default Handler getHandler() {
        return null;
    }

    default void initHandler() {
    }

    default void stopHandler() {
    }

//    default void stopHandler() {
//
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "stopHandler ->");
//        }
//
//        try {
//            Handler handler = mHandler.get(this);
//            if (null == handler)
//                throw new Exception("warning: handler null");
//            handler.removeCallbacksAndMessages(null);
//            handler = null;
//            mHandler.remove(this);
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "stopHandler -> " + e.getMessage());
//            }
//        }
//    }
//
//    default void initHandler() {
//
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "initHandler ->");
//        }
//
//        try {
//            Handler handler = mHandler.get(this);
//            if (null != handler)
//                throw new Exception("warning: handler not null");
//            mHandler.put(this, new android.os.Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(@NonNull Message msg) {
//                    formatMessage(msg);
//                }
//            });
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "initHandler -> " + e.getMessage());
//            }
//        }
//    }

    default void formatMessage(Message msg) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "formatMessage ->");
        }

        try {
            if (null == msg)
                throw new Exception("warning: msg null");
            // 延迟播放
            if (msg.what == WHAT_PlayWhenReadyDelayedTime) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_PlayWhenReadyDelayedTime");
                }

                onEvent(msg.arg1, PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_COMPLETE);
                initDecoderPlayWhenReadyDelayed((Context) msg.obj);
            }
            // 网络超时
            else if (msg.what == WHAT_ConnectTimeout) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_ConnectTimeout");
                }

                if (isPrepared())
                    throw new Exception("warning: isPrepared true");
                long timeout = ((long[]) msg.obj)[1];
                long start = ((long[]) msg.obj)[0];
                long cast = System.currentTimeMillis() - start;
                if (cast >= timeout) {
                    onEvent(msg.arg1, PlayerType.EventType.ERROR);
                    getPlayerApi().stop(true);
                    throw new Exception("warning: connect timeout");
                } else {
                    sendMessageConnectTimeout(msg.arg1, start, timeout, true);
                }
            }
            // 解决部分盒子不回调 info code=3
            else if (msg.what == WHAT_CheckPreparedPlaying) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_CheckPreparedPlaying");
                }

//                if (isPrepared())
//                    throw new Exception("warning: isPrepared true");
//                boolean playing = isPlaying();
//                if (playing) {
//                    setPrepared(true);
//                    onEvent(msg.arg1, PlayerType.EventType.PREPARE_COMPLETE);
//                    long seek = getPlayWhenReadySeekToPosition();
//                    if (seek <= 0) {
//                        onEvent(msg.arg1, PlayerType.EventType.START);
//                    } else {
//                        onEvent(msg.arg1, PlayerType.EventType.START);
//                        // 起播快进
//                        onEvent(msg.arg1, PlayerType.EventType.SEEK_START_FORWARD);
//                        //  setPlayWhenReadySeekFinish(true);
//                        seekTo(seek);
//                    }
//                } else {
//                    sendMessageCheckPreparedPlaying(msg.arg1);
//                }
            }
            // 更新进度条
            else if (msg.what == WHAT_ProgressUpdate) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_ProgressUpdate");
                }

                if (isPrepared()) {
                    long position = getPosition();
                    long duration = getDuration();
                    long trySeeDuration = getTrySeeDuration();
                    onUpdateProgress(trySeeDuration, position, duration);
                    sendMessageProgressUpdate(msg.arg1, true);
                }
            }
            // 缓冲超时
            else if (msg.what == WHAT_BufferingTimeout) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout");
                }

                boolean prepared = isPrepared();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, prepared = " + prepared);
                }
                if (!prepared)
                    throw new Exception("warning: prepared false");

                boolean buffering = isBuffering();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, buffering = " + buffering);
                }
                if (!buffering)
                    throw new Exception("warning: buffering false");

                long startTimeMillis = ((long[]) msg.obj)[0];
                long maxTimeoutMs = ((long[]) msg.obj)[1];
                long currentTimeMillis = System.currentTimeMillis();
                long castTimeMs = currentTimeMillis - startTimeMillis;

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, startTimeMillis = " + startTimeMillis + ", currentTimeMillis = " + currentTimeMillis + ", maxTimeoutMs = " + maxTimeoutMs + ", castTimeMs = " + castTimeMs);
                }

                if (castTimeMs >= maxTimeoutMs) {

                    //
                    boolean live = isLive();
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, live = " + live);
                    }

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, retry");
                    }

                    closeMessagesBufferingTimeout();
                    onEvent(msg.arg1, PlayerType.EventType.ERROR_TIMEOUT_BUFFERING);

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, retry stop");
                    }
                    getPlayerApi().stop(true);

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, retry restart");
                    }
                    if (live) {
                        getPlayerApi().restart(false);
                    } else {
                        getPlayerApi().restart(true);
                    }
                } else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "formatMessage -> WHAT_BufferingTimeout, next");
                    }
                    nextMessageBufferingTimeout(msg.arg1, currentTimeMillis, maxTimeoutMs);
                }
            }
            // 更新网速
            else if (msg.what == WHAT_UPDATE_SPEED) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatMessage -> WHAT_UPDATE_SPEED");
                }

                if (!isPrepared()) {
                    onUpdateNetSpeed(msg.arg1);
                    sendMessageSpeedUpdate(msg.arg1, true);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "formatMessage -> Exception " + e.getMessage());
            }
        }
    }
}