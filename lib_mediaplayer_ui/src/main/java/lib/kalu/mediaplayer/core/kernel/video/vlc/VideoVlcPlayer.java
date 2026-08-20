package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.vlc.widget.OnVlcInfoChangeListener;
import lib.kalu.vlc.widget.VlcPlayer;


public final class VideoVlcPlayer extends VideoBasePlayer {

    private boolean isVideoSizeChanged = false;
    private boolean isPrepared = false;
    private boolean isBuffering = false;
    private lib.kalu.vlc.widget.VlcPlayer mVlcPlayer;

    @Override
    public VideoVlcPlayer getPlayer() {
        return this;
    }


    @Override
    public void releaseDecoder() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "releaseDecoder -> mVlcPlayer error: null");
                }
                return;
            }
            setEvent(null);
            unRegistListener();
            release();
        } catch (Exception e) {
        }
    }

    @Override
    public void checkDecoder(Context context, StartArgs args) {
        try {
            if (null != mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "checkDecoder -> warning: null != mVlcPlayer");
                }
                return;
            }
            mVlcPlayer = new VlcPlayer(context);
            registListener();
        } catch (Exception e) {
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "startDecoder -> error: mVlcPlayer null");
                }
                return;
            }
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "startDecoder -> error: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "startDecoder -> error: containsVideoUrl false");
                }
                return;
            }
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.READY);
            mVlcPlayer.setDataSource(Uri.parse(args.getUrl()), isPlayWhenReady());
            mVlcPlayer.play();
        } catch (Exception e) {
            stop();
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.ERROR_DECODE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {

        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "initOptions -> error: mVlcPlayer null");
                }
                return;
            }
            boolean mute = args.isMute();
            if (mute) {
                mVlcPlayer.setVolume(0f, 0f);
            } else {
                mVlcPlayer.setVolume(1f, 1f);
            }

            boolean looping = args.isLooping();
            mVlcPlayer.setLooping(looping);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> initOptions -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "registListener -> error: mVlcPlayer null");
                }
                return;
            }
            mVlcPlayer.setOnVlcInfoChangeListener(mVlcPlayerListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> registListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "unRegistListener -> error: mVlcPlayer null");
                }
                return;
            }
            mVlcPlayer.setOnVlcInfoChangeListener(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> unRegistListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void release() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "release -> mVlcPlayer error: null");
                }
                return;
            }
            mVlcPlayer.setSurface(null, 0, 0);
            mVlcPlayer.release();
            mVlcPlayer = null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> release -> " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isBuffering() {
        return isBuffering;
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "start -> mVlcPlayer error: null");
                }
                return;
            }
            mVlcPlayer.play();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> start -> " + e.getMessage());
            }
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "pause -> mPrepared warning: false");
                }
                return;
            }
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "pause -> mVlcPlayer error: null");
                }
                return;
            }
            mVlcPlayer.pause();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> pause -> " + e.getMessage());
            }
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "stop -> mVlcPlayer error: null");
                }
                return;
            }
            mVlcPlayer.stop();
//            mVlcPlayer.reset();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> stop -> " + e.getMessage());
            }
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "isPlaying -> mPrepared warning: false");
                }
                return false;
            }
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "isPlaying -> mVlcPlayer error: null");
                }
                return false;
            }
            return mVlcPlayer.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> isPlaying -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void seekToDefaultPosition() {
        seekTo(0);
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long position) {
//        try {
//            mVlcPlayer.seekTo((int) time);
//        } catch (IllegalStateException e) {
//        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getPosition -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getPosition -> mVlcPlayer error: null");
                }
                return 0L;
            }
            long position = mVlcPlayer.getPosition();
            if (position < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getPosition -> position warning: " + position);
                }
                return 0L;
            }
            return position;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> getPosition -> " + e.getMessage());
            }
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getDuration -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getDuration -> mVlcPlayer error: null");
                }
                return 0L;
            }
            long duration = mVlcPlayer.getDuration();
            if (duration <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getDuration -> duration warning: " + duration);
                }
                return 0L;
            }
            return duration;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> getDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void setSurface(Surface sf, int w, int h) {
        if (LogUtil.DEBUG) {
            LogUtil.log("VideoVlcPlayer -> setSurface -> sf = " + sf + ", mVlcPlayer = " + mVlcPlayer + ", w = " + w + ", h = " + h);
//        }
            if (null != sf && null != mVlcPlayer) {
                mVlcPlayer.setSurface(sf, w, h);
            }
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "setSpeed -> mIjkPlayer error: null");
                }
                return;
            }
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
            mVlcPlayer.setSpeed(speed);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> setSpeed -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getSpeed() {
        return 1.0f;
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "setVolume -> mVlcPlayer error: null");
                }
                return;
            }
            float volume = Math.max(v1, v2);
            if (volume < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "setVolume -> error: volume < 0");
                }
                return;
            }
            mVlcPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> setVolume -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public float getVolume() {
        try {
            if (null == mVlcPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoVlcPlayer", "getVolume -> mVlcPlayer error: null");
                }
                return 0f;
            }
            return mVlcPlayer.getVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoVlcPlayer -> getVolume -> Exception " + e.getMessage());
            }
            return 0f;
        }
    }

    private final OnVlcInfoChangeListener mVlcPlayerListener = new OnVlcInfoChangeListener() {
        @Override
        public void onStart() {
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.MEDIA_INFO_PREPARE);
        }

        @Override
        public void onPlay() {
//            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.LOADING_STOP);
//            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.VIDEO_START);
//
//            long seek = getSeek();
//            if (seek > 0) {
//                seekTo(seek);
//            }
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onEnd() {
            stop();
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.END);
        }

        @Override
        public void onError() {
            stop();
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.ERROR_PLAY);
        }
    };
}