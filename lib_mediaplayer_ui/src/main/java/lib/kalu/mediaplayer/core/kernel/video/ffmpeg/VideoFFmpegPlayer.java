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
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayerCollects error: null");
            if (isFromUser) {
                setEvent(null);
            }
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> releaseDecoder -> " + e.getMessage());
            }
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mFFmpegPlayer)
                throw new Exception("warning: null != mFFmpegPlayer");
            mFFmpegPlayer = new FFmpegPlayer();
            registListener();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> createDecoder -> " + e.getMessage());
            }
        }
    }


    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.INIT_READY);
            mFFmpegPlayer.setDataSource(context, Uri.parse(args.getUrl()), null);
            mFFmpegPlayer.prepare();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> startDecoder -> " + e.getMessage());
            }
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
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
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
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
        clear();
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            return mFFmpegPlayer.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> stop -> " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            if (seek < 0L)
                throw new Exception("error: seek<0");
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0 && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            onEvent(PlayerType.KernelType.FFPLAYER, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            long currentPosition = mFFmpegPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            int duration = mFFmpegPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (null == surface)
                throw new Exception("surface error: null");
            mFFmpegPlayer.setSurface(surface);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> setSurface -> " + e.getMessage());
            }
        }
    }

    @Override
    public boolean setSpeed(float speed) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
            return false;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> setSpeed -> " + e.getMessage());
            }
            return false;
        }
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
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.BUFFERING_START);
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
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.BUFFERING_STOP);
                } else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoFFmpegPlayer -> onInfo -> what = " + what + ", mPrepared = false");
                    }
                }
            }
            // 开始播放
            else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                try {
                    if (isPrepared)
                        throw new Exception("warning: mPrepared true");
                    isPrepared = true;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.VIDEO_RENDERING_START);
                    long seek = getPlayWhenReadySeekToPosition();
                    if (seek <= 0) {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                        // 立即播放
                        boolean playWhenReady = isPlayWhenReady();
                        onEvent(PlayerType.KernelType.FFPLAYER, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                        if (!playWhenReady) {
                            pause();
                            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PAUSE_PlAY_WHEN_READY);
                        }
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_START_FORWARD);
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
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_FINISH);

            try {
                // 起播快进
                if (mPlayWhenReadySeeking) {
                    mPlayWhenReadySeeking = false;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                    boolean playWhenReady = isPlayWhenReady();
                    onEvent(PlayerType.KernelType.FFPLAYER, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                    if (playWhenReady) {
                        boolean playing = isPlaying();
                        if (playing)
                            throw new Exception("warning: isPlaying true");
                        start();
                    } else {
                        pause();
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PAUSE);
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
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.PREPARE);
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
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
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
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.COMPLETE);
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(FFmpegPlayer mp, int width, int height) {
            try {
                if (null == mp)
                    throw new Exception("error: MediaPlayer null");
                int videoWidth = mp.getVideoWidth();
                if (videoWidth <= 0)
                    throw new Exception("error: videoWidth <=0");
                int videoHeight = mp.getVideoHeight();
                if (videoHeight <= 0)
                    throw new Exception("error: videoHeight <=0");
                if (isVideoSizeChanged)
                    throw new Exception("warning: videoSizeChanged = true");
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            float volume = Math.max(v1, v2);
            if (volume < 0)
                throw new Exception("error: volume < 0");
            mFFmpegPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoFFmpegPlayer -> setVolume -> " + e.getMessage());
            }
        }
    }
}
