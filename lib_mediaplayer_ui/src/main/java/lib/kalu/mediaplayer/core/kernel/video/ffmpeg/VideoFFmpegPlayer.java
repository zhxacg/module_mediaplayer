package lib.kalu.mediaplayer.core.kernel.video.ffmpeg;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import lib.kalu.ffplayer.FFmpegPlayer;
import lib.kalu.ffplayer.inter.OnBufferingUpdateListener;
import lib.kalu.ffplayer.inter.OnCompletionListener;
import lib.kalu.ffplayer.inter.OnErrorListener;
import lib.kalu.ffplayer.inter.OnInfoListener;
import lib.kalu.ffplayer.inter.OnPreparedListener;
import lib.kalu.ffplayer.inter.OnSeekCompleteListener;
import lib.kalu.ffplayer.inter.OnVideoSizeChangedListener;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoFFmpegPlayer extends VideoBasePlayer {

    private boolean isVideoSizeChanged = false;
    private boolean isPrepared = false;
    private boolean isBuffering = false;
    private boolean mPlayWhenReadySeeking = false;
    private FFmpegPlayer mFFmpegPlayer = null;

    @Override
    public VideoFFmpegPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder() {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> releaseDecoder -> mFFmpegPlayer error: null");
                }
                return;
            }
            setEvent(null);
            unRegistListener();
            release();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> releaseDecoder -> " + e.getMessage());
            }
        }
    }

    @Override
    public void checkDecoder(Context context, StartArgs args) {
        try {
            if (null != mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> checkDecoder -> warning: null != mFFmpegPlayer");
                }
                return;
            }
            mFFmpegPlayer = new FFmpegPlayer();
            registListener();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> checkDecoder -> " + e.getMessage());
            }
        }
    }


    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> startDecoder -> error: mFFmpegPlayer null");
                }
                return;
            }
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> startDecoder -> error: args null");
                }
                return;
            }
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> startDecoder -> error: containsMainUrl false");
                }
                return;
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.READY);
            mFFmpegPlayer.setDataSource(context, Uri.parse(args.getUrl()), null);
            mFFmpegPlayer.prepare();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> startDecoder -> " + e.getMessage());
            }
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR_DECODE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> initOptions -> error: mFFmpegPlayer null");
                }
                return;
            }
            boolean mute = args.isMute();
            if (mute) {
                mFFmpegPlayer.setVolume(0f, 0f);
            } else {
                mFFmpegPlayer.setVolume(1f, 1f);
            }
            boolean looping = args.isLooping();
            mFFmpegPlayer.setLooping(looping);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> initOptions -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> registListener -> error: mFFmpegPlayer null");
                }
                return;
            }
            mFFmpegPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mFFmpegPlayer.setOnErrorListener(onErrorListener);
            mFFmpegPlayer.setOnCompletionListener(onCompletionListener);
            mFFmpegPlayer.setOnInfoListener(onInfoListener);
            mFFmpegPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mFFmpegPlayer.setOnPreparedListener(onPreparedListener);
            mFFmpegPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mFFmpegPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> registListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> unRegistListener -> mFFmpegPlayer error: null");
                }
                return;
            }
            mFFmpegPlayer.setOnErrorListener(null);
            mFFmpegPlayer.setOnCompletionListener(null);
            mFFmpegPlayer.setOnInfoListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
            mFFmpegPlayer.setOnPreparedListener(null);
            mFFmpegPlayer.setOnVideoSizeChangedListener(null);
            mFFmpegPlayer.setOnSeekCompleteListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> unRegistListener -> Exception " + e.getMessage());
            }
        }
    }

    //    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mFFmpegPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer -> " + e.getMessage());
//        }
//    }

    @Override
    public void release() {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> release -> mFFmpegPlayer error: null");
                }
                return;
            }
            mFFmpegPlayer.setSurface(null);
            mFFmpegPlayer.release();
            mFFmpegPlayer = null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> start -> " + e.getMessage());
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
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> start -> mFFmpegPlayer error: null");
                }
                return;
            }
            mFFmpegPlayer.start();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> start -> " + e.getMessage());
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
                    LogUtil.log("VideoFFmpegPlayer -> pause -> mPrepared warning: false");
                }
                return;
            }
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> pause -> mFFmpegPlayer error: null");
                }
                return;
            }
            mFFmpegPlayer.pause();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> pause -> " + e.getMessage());
            }
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> stop -> mFFmpegPlayer error: null");
                }
                return;
            }
            mFFmpegPlayer.stop();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> stop -> " + e.getMessage());
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
                    LogUtil.log("VideoFFmpegPlayer -> isPlaying -> mPrepared warning: false");
                }
                return false;
            }
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> isPlaying -> mFFmpegPlayer error: null");
                }
                return false;
            }
            return mFFmpegPlayer.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> isPlaying -> " + e.getMessage());
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
    public void seekTo(long seek) {
        try {
            if (seek < 0L) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> seekTo -> error: seek<0");
                }
                return;
            }
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> seekTo -> error: mFFmpegPlayer null");
                }
                return;
            }
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> seekTo -> error: args null");
                }
                return;
            }

            long duration = getDuration();
            if (duration > 0 && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            if (seek < position) {
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_REWIND);
            } else {
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_FORWARD);
            }
            mFFmpegPlayer.seekTo((int) seek);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> seekTo ->");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> seekTo -> " + e.getMessage());
            }
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> getPosition -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> getPosition -> mFFmpegPlayer error: null");
                }
                return 0L;
            }
            long currentPosition = mFFmpegPlayer.getCurrentPosition();
            if (currentPosition < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> getPosition -> currentPosition warning: " + currentPosition);
                }
                return 0L;
            }
            return currentPosition;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> getPosition -> " + e.getMessage());
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
                    LogUtil.log("VideoFFmpegPlayer -> getDuration -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> getDuration -> mFFmpegPlayer error: null");
                }
                return 0L;
            }
            int duration = mFFmpegPlayer.getDuration();
            if (duration <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> getDuration -> duration warning: " + duration);
                }
                return 0L;
            }
            return duration;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> getDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> setSurface -> mFFmpegPlayer error: null");
                }
                return;
            }
            if (null == surface) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> setSurface -> surface error: null");
                }
                return;
            }
            mFFmpegPlayer.setSurface(surface);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> setSurface -> " + e.getMessage());
            }
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> setSpeed -> mFFmpegPlayer error: null");
                }
                return;
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> setSpeed -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getSpeed() {
        return 1.0f;
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(FFmpegPlayer mp, int what, int extra) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> onInfo -> what = " + what);
            }
            // 缓冲开始
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (isPrepared) {
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_BUFFERING_START);
                } else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onInfo -> what = " + what + ", mPrepared = false");
                    }
                }
            }
            // 缓冲结束
            else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                if (isPrepared) {
                    isBuffering = false;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP);
                } else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onInfo -> what = " + what + ", mPrepared = false");
                    }
                }
            }
            // 开始播放
            else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                try {
                    if (isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoFFmpegPlayer -> onInfo -> MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> warning: isPrepared true");
                        }
                        return true;
                    }
                    isPrepared = true;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
                    long seek = getPlayWhenReadySeekToPosition();
                    if (seek <= 0) {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                        // 立即播放
                        boolean playWhenReady = isPlayWhenReady();
                        if (!playWhenReady) {
                            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                            pause();
                        }
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_SEEK);
                        // 起播快进
                        mPlayWhenReadySeeking = true;
                        seekTo(seek);
                    }
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onInfo -> what = " + what + ", msg = " + e.getMessage());
                    }
                }
            }
            return true;
        }
    };

    private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(FFmpegPlayer mediaPlayer) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> onSeekComplete ->");
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_FINISH);

            try {
                // 起播快进
                if (mPlayWhenReadySeeking) {
                    mPlayWhenReadySeeking = false;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                    boolean playWhenReady = isPlayWhenReady();
                    if (playWhenReady) {
                        boolean playing = isPlaying();
                        if (playing) {
                            if (LogUtil.DEBUG) {
                                LogUtil.log("VideoFFmpegPlayer -> onSeekComplete -> warning: isPlaying true");
                            }
                            return;
                        }
                        start();
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                        pause();
                    }
                }
                // 正常快进&快退
                else {

                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> onSeekComplete -> Exception " + e.getMessage());
                }
            }
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(FFmpegPlayer mp) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> onPrepared ->");
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PREPARE);
            start();
        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(FFmpegPlayer mp, int percent) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> onBufferingUpdate -> percent = " + percent);
            }
        }
    };

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(FFmpegPlayer mp, int what, int extra) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> onError -> what = " + what);
            }
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                stop();
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR_PLAY);
            }
            return true;
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(FFmpegPlayer mp) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> onCompletion ->");
            }
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.END);
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(FFmpegPlayer mp, int width, int height) {
            try {
                if (null == mp) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onVideoSizeChanged -> error: MediaPlayer null");
                    }
                    return;
                }
                int videoWidth = mp.getVideoWidth();
                if (videoWidth <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onVideoSizeChanged -> error: videoWidth <= 0");
                    }
                    return;
                }
                int videoHeight = mp.getVideoHeight();
                if (videoHeight <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onVideoSizeChanged -> error: videoHeight <= 0");
                    }
                    return;
                }
                if (isVideoSizeChanged) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onVideoSizeChanged -> warning: isVideoSizeChanged true");
                    }
                    return;
                }
                StartArgs args = getStartArgs();
                if (null == args) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onVideoSizeChanged -> error: args null");
                    }
                    return;
                }
                isVideoSizeChanged = true;
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
                onVideoFormatChanged(PlayerType.KernelType.FFPLAYER, rotation, scaleType, videoWidth, videoHeight, -1);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> onVideoSizeChanged -> " + e.getMessage());
                }
            }
        }
    };

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> setVolume -> mFFmpegPlayer error: null");
                }
                return;
            }
            float volume = Math.max(v1, v2);
            if (volume < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> setVolume -> error: volume < 0");
                }
                return;
            }
            mFFmpegPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> setVolume -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getVolume() {
        try {
            if (null == mFFmpegPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoFFmpegPlayer -> getVolume -> mFFmpegPlayer error: null");
                }
                return 0f;
            }
            return 0f;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> getVolume -> " + e.getMessage());
            }
            return 0f;
        }
    }
}
