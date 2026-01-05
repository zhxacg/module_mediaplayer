package lib.kalu.mediaplayer.core.kernel.video;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashMap;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
public interface VideoKernelApiHandler extends VideoKernelApiBase, VideoKernelApiEvent, VideoKernelApiStartArgs {

    int WHAT_PlayWhenReadyDelayedTime = 1000;
    int WHAT_ConnectTimeout = 2000;
    int WHAT_CheckPreparedPlaying = 3000;
    int WHAT_ProgressUpdate = 4000;
    int WHAT_BufferingTimeout = 5000;
    int WHAT_UPDATE_SPEED = 6000;

    HashMap<VideoKernelApiBase, android.os.Handler> mHandler = new HashMap<>();

    default void parseTimer(Message msg) {

        try {
            if (null == msg)
                throw new Exception("warning: msg null");
            // 延迟播放
            if (msg.what == WHAT_PlayWhenReadyDelayedTime) {
                onEvent(msg.arg1, PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_COMPLETE);
                initDecoderPlayWhenReadyDelayed((Context) msg.obj);
            }
            // 网络超时
            else if (msg.what == WHAT_ConnectTimeout) {
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
                if (isPrepared()) {
                    long position = getPosition();
                    long duration = getDuration();
                    long trySeeDuration = getTrySeeDuration();
                    onUpdateProgress(trySeeDuration, position, duration);
                }
                sendMessageProgressUpdate(msg.arg1, true);
            }
            // 缓冲超时
            else if (msg.what == WHAT_BufferingTimeout) {
                if (isPrepared())
                    throw new Exception("warning: isPrepared true");
                long timeout = ((long[]) msg.obj)[1];
                long start = ((long[]) msg.obj)[0];
                long cast = System.currentTimeMillis() - start;
                if (cast >= timeout) {
                    onEvent(msg.arg1, PlayerType.EventType.ERROR_TIMEOUT_BUFFERING);
                    //
                    removeMessagesBufferingTimeout();
                    //
                    getPlayerApi().stop(true);
                    //
                    if (msg.arg2 != 1)
                        throw new Exception("warning: bufferingTimeoutRetry false");
                    //
                    boolean live = isLive();
                    if (live) {
                        getPlayerApi().restart(false);
                    } else {
                        getPlayerApi().restart(true);
                    }
                } else {
                    boolean buffering = isBuffering();
                    if (buffering) {
                        sendMessageConnectTimeout(msg.arg1, start, timeout, true);
                    } else {
                        removeMessagesBufferingTimeout();
                    }
                }
            }
            // 更新网速
            else if (msg.what == WHAT_UPDATE_SPEED) {
                if (!isPrepared()) {
                    onUpdateNetSpeed(msg.arg1);
                    sendMessageSpeedUpdate(msg.arg1, true);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> parseTimer -> Exception " + e.getMessage());
            }
        }
    }

    default void releaseTimer() {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> releaseTimer ->");
            }
            handler.removeCallbacksAndMessages(null);
            mHandler.remove(this);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> releaseTimer -> " + e.getMessage());
            }
        }
    }

    default void createTimer() {
        try {
            Handler handler = mHandler.get(this);
            if (null != handler)
                throw new Exception("warning: handler not null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> createTimer ->");
            }
            mHandler.put(this, new android.os.Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    parseTimer(msg);
                }
            });
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> createTimer -> " + e.getMessage());
            }
        }
    }

    /***********/

    default void startPlayWhenReadyDelayedTime(Context context, @PlayerType.KernelType.Value int kernelType, long delayedTime) {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> startPlayWhenReadyDelayedTime ->");
            }
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
                LogUtil.log("VideoKernelApiHandler -> startPlayWhenReadyDelayedTime -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageConnectTimeout(@PlayerType.KernelType.Value int kernelType, long timeMillis, long timeout, boolean delay) {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageConnectTimeout ->");
            }
            Message message = Message.obtain();
            message.what = WHAT_ConnectTimeout;
            message.arg1 = kernelType;
            message.obj = new long[]{timeMillis, timeout};
            handler.sendMessageDelayed(message, delay ? 1000 : 0);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageConnectTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesConnectTimeout() {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesConnectTimeout ->");
            }
            handler.removeMessages(WHAT_ConnectTimeout);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesConnectTimeout -> Exception " + e.getMessage());
            }
        }
    }

    /***********/

    default void sendMessageCheckPreparedPlaying(@PlayerType.KernelType.Value int kernelType) {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageCheckPreparedPlaying ->");
            }
            Message message = Message.obtain();
            message.what = WHAT_CheckPreparedPlaying;
            message.arg1 = kernelType;
            handler.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageCheckPreparedPlaying -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageProgressUpdate(@PlayerType.KernelType.Value int kernelType, boolean delay) {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageProgressUpdate ->");
            }
            Message message = Message.obtain();
            message.what = WHAT_ProgressUpdate;
            message.arg1 = kernelType;
            handler.sendMessageDelayed(message, delay ? 1000 : 0);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageProgressUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesProgressUpdate() {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesProgressUpdate ->");
            }
            handler.removeMessages(WHAT_ProgressUpdate);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesProgressUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageSpeedUpdate(@PlayerType.KernelType.Value int kernelType, boolean delay) {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageSpeedUpdate ->");
            }
            Message message = Message.obtain();
            message.what = WHAT_UPDATE_SPEED;
            message.arg1 = kernelType;
            handler.sendMessageDelayed(message, delay ? 1000 : 0);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageSpeedUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesSpeedUpdate() {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesSpeedUpdate ->");
            }
            handler.removeMessages(WHAT_UPDATE_SPEED);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesSpeedUpdate -> Exception " + e.getMessage());
            }
        }
    }

    default void sendMessageBufferingTimeout(@PlayerType.KernelType.Value int kernelType, boolean bufferingTimeoutRetry, long timeMillis, long timeout) {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageBufferingTimeout ->");
            }
            Message message = Message.obtain();
            message.what = WHAT_BufferingTimeout;
            message.arg1 = kernelType;
            message.arg2 = bufferingTimeoutRetry ? 1 : 0;
            message.obj = new long[]{timeMillis, timeMillis};
            handler.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> sendMessageBufferingTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesBufferingTimeout() {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesBufferingTimeout ->");
            }
            handler.removeMessages(WHAT_BufferingTimeout);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesBufferingTimeout -> Exception " + e.getMessage());
            }
        }
    }

    default void removeMessagesAll() {
        try {
            Handler handler = mHandler.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesAll ->");
            }
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHandler -> removeMessagesAll -> Exception " + e.getMessage());
            }
        }
    }
}