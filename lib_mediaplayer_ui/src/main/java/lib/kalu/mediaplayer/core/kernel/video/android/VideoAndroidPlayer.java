package lib.kalu.mediaplayer.core.kernel.video.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoAndroidPlayer extends VideoBasePlayer {

    private boolean isVideoSizeChanged = false;
    private boolean isPrepared = false;
    private boolean isBuffering = false;
    private boolean mPlayWhenReadySeeking = false;
    private MediaPlayer mAndroidPlayer = null;

    @Override
    public <T> T getPlayer() {
        return (T) this;
    }

    @Override
    public void releaseDecoder() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> releaseDecoder -> error: mAndroidPlayer null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> releaseDecoder ->");
            }
            setEvent(null);
            unRegistListener();
            release();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> releaseDecoder -> " + e.getMessage());
            }
        }
    }

    @Override
    public void checkDecoder(Context context, StartArgs args) {
        try {
            if (null != mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> checkDecoder -> error: mAndroidPlayer not null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> checkDecoder ->");
            }
            mAndroidPlayer = new MediaPlayer();
            registListener();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> checkDecoder -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> startDecoder -> error: mAndroidPlayer null");
                }
                return;
            }
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> startDecoder -> error: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> startDecoder -> error: containsVideoUrl false");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> startDecoder ->");
            }
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.READY);

            mAndroidPlayer.setDataSource(context, Uri.parse(args.getUrl()), null);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mAndroidPlayer.prepareAsync();
            } else {
                mAndroidPlayer.prepare();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> startDecoder -> " + e.getMessage());
            }
            stop();
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.ERROR_DECODE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> initOptions -> mAndroidPlayer error: null");
                }
                return;
            }
            boolean mute = args.isMute();
            if (mute) {
                mAndroidPlayer.setVolume(0f, 0f);
            } else {
                mAndroidPlayer.setVolume(1f, 1f);
            }

            boolean looping = args.isLooping();
            mAndroidPlayer.setLooping(looping);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> initOptions -> " + e.getMessage());
            }
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> registListener -> mAndroidPlayer error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> registListener ->");
            }
            mAndroidPlayer.setOnErrorListener(onErrorListener);
            mAndroidPlayer.setOnCompletionListener(onCompletionListener);
            mAndroidPlayer.setOnInfoListener(onInfoListener);
            mAndroidPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mAndroidPlayer.setOnPreparedListener(mOnPreparedListener);
            mAndroidPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
            mAndroidPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> registListener -> " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> unRegistListener -> mAndroidPlayer error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> unRegistListener ->");
            }
            mAndroidPlayer.setOnErrorListener(null);
            mAndroidPlayer.setOnCompletionListener(null);
            mAndroidPlayer.setOnInfoListener(null);
            mAndroidPlayer.setOnBufferingUpdateListener(null);
            mAndroidPlayer.setOnPreparedListener(null);
            mAndroidPlayer.setOnSeekCompleteListener(null);
            mAndroidPlayer.setOnVideoSizeChangedListener(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> unRegistListener -> " + e.getMessage());
            }
        }
    }

    @Override
    public void release() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> release -> mAndroidPlayer error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> release ->");
            }
            mAndroidPlayer.setSurface(null);
            mAndroidPlayer.release();
            mAndroidPlayer = null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> release -> " + e.getMessage());
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
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> start -> mAndroidPlayer error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> start ->");
            }
            mAndroidPlayer.start();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> start -> " + e.getMessage());
            }
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> setVolume -> mAndroidPlayer error: null");
                }
                return;
            }
            float volume = Math.max(v1, v2);
            if (volume < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> setVolume -> error: volume < 0");
                }
                return;
            }
            mAndroidPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setVolume -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getVolume() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getVolume -> mAndroidPlayer error: null");
                }
                return 0f;
            }
            return 1f;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> getVolume -> " + e.getMessage());
            }
            return 0f;
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
                    LogUtil.log("VideoAndroidPlayer -> pause -> mPrepared warning: false");
                }
                return;
            }
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> pause -> mAndroidPlayer error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> pause ->");
            }
            mAndroidPlayer.pause();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> pause -> " + e.getMessage());
            }
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> stop -> mAndroidPlayer error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> stop ->");
            }
            mAndroidPlayer.stop();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> stop -> " + e.getMessage());
            }
        } finally {
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
                    LogUtil.log("VideoAndroidPlayer -> isPlaying -> mPrepared warning: false");
                }
                return false;
            }
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> isPlaying -> mAndroidPlayer error: null");
                }
                return false;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> isPlaying ->");
            }
            return mAndroidPlayer.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> isPlaying -> " + e.getMessage());
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
                    LogUtil.log("VideoAndroidPlayer -> seekTo -> error: seek < 0");
                }
                return;
            }
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> seekTo -> error: mAndroidPlayer null");
                }
                return;
            }
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> seekTo -> error: args null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> seekTo ->");
            }
            long duration = getDuration();
            if (duration > 0L && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            if (seek < position) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_REWIND);
            } else {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_FORWARD);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                mAndroidPlayer.seekTo((int) seek);
            } else {
                int seekType = args.getSeekType();
                switch (seekType) {
                    case PlayerType.SeekType.ANDROID_SEEK_CLOSEST:
                        mAndroidPlayer.seekTo(seek, MediaPlayer.SEEK_CLOSEST);
                        break;
                    case PlayerType.SeekType.ANDROID_SEEK_CLOSEST_SYNC:
                        mAndroidPlayer.seekTo(seek, MediaPlayer.SEEK_CLOSEST_SYNC);
                        break;
                    case PlayerType.SeekType.ANDROID_SEEK_PREVIOUS_SYNC:
                        mAndroidPlayer.seekTo(seek, MediaPlayer.SEEK_PREVIOUS_SYNC);
                        break;
                    case PlayerType.SeekType.ANDROID_SEEK_NEXT_SYNC:
                        mAndroidPlayer.seekTo(seek, MediaPlayer.SEEK_NEXT_SYNC);
                        break;
                    default:
                        mAndroidPlayer.seekTo((int) seek);
                        break;
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> seekTo -> Exception " + e.getMessage());
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
                    LogUtil.log("VideoAndroidPlayer -> getPosition -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getPosition -> mAndroidPlayer error: null");
                }
                return 0L;
            }
            int currentPosition = mAndroidPlayer.getCurrentPosition();
            if (currentPosition < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getPosition -> currentPosition warning: " + currentPosition);
                }
                return 0L;
            }
            return currentPosition;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> getPosition -> " + e.getMessage());
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
                    LogUtil.log("VideoAndroidPlayer -> getDuration -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getDuration -> mAndroidPlayer error: null");
                }
                return 0L;
            }
            int duration = mAndroidPlayer.getDuration();
            if (duration <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getDuration -> duration warning: " + duration);
                }
                return 0L;
            }
            return duration;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> getDuration -> " + e.getMessage());
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
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> setSurface -> mAndroidPlayer error: null");
                }
                return;
            }
            if (null == surface) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> setSurface -> surface error: null");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setSurface ->");
            }
            mAndroidPlayer.setSurface(surface);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setSurface -> " + e.getMessage());
            }
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> setSpeed -> mAndroidPlayer error: null");
                }
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> setSpeed -> only support above Android M");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setSpeed ->");
            }
            PlaybackParams playbackParams = mAndroidPlayer.getPlaybackParams();
            if (null == playbackParams) {
                playbackParams = new PlaybackParams();
            }
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
            playbackParams.setSpeed(speed);
            mAndroidPlayer.setPlaybackParams(playbackParams);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setSpeed -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getSpeed() {
        try {
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getSpeed -> mAndroidPlayer error: null");
                }
                return 1.0f;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getSpeed -> only support above Android M");
                }
                return 1.0f;
            }
            return mAndroidPlayer.getPlaybackParams().getSpeed();
        } catch (Exception e) {
            return 1.0f;
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> onError -> what = " + what + ", extra = " + extra);
            }
            try {
                if (what == -38) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onError -> what warning: " + what);
                    }
                } else if (what == -10005) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onError -> what warning: " + what);
                    }
                } else {
                    stop();
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.STOP);
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.ERROR_PLAY);
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> onError -> Exception " + e.getMessage());
                }
            }
            return true; // 若返回 true，错误已处理，不会触发 OnCompletion
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {

        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> onInfo -> what = " + what + ", extra = " + extra);
            }

            try {
                // 缓冲开始
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    if (!isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoAndroidPlayer -> onInfo -> warning: isPrepared false");
                        }
                        return true;
                    }
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_BUFFERING_START);
                }
                // 缓冲结束
                else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    if (!isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoAndroidPlayer -> onInfo -> warning: isPrepared false");
                        }
                        return true;
                    }
                    isBuffering = false;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP);
                }
                // 开始播放
                else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START || what == 903) {
                    if (isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoAndroidPlayer -> onInfo -> warning: mPrepared true");
                        }
                        return true;
                    }
                    isPrepared = true;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
                    long seek = getPlayWhenReadySeekToPosition();

                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onInfo -> seek = " + seek);
                    }
                    // 起播正常
                    if (seek <= 0L) {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.START);
                        boolean playWhenReady = isPlayWhenReady();
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoAndroidPlayer -> onInfo -> playWhenReady = " + playWhenReady);
                        }
                        if (playWhenReady) {
                            boolean playing = isPlaying();
                            if (playing) {
                                if (LogUtil.DEBUG) {
                                    LogUtil.log("VideoAndroidPlayer -> onInfo -> warning: isPlaying true");
                                }
                                return true;
                            }
                            start();
                        } else {
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                            pause();
                        }
                    }
                    // 起播快进
                    else {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_SEEK);
                        mPlayWhenReadySeeking = true;
                        seekTo(seek);
                    }
                }
                // not find
                else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onInfo -> warning: not find what = " + what);
                    }
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> onInfo -> Exception " + e.getMessage());
                }
            }
            return true;
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> onSeekComplete ->");
            }

            try {
                // 起播快进
                if (mPlayWhenReadySeeking) {
                    mPlayWhenReadySeeking = false;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.START);
                    boolean playWhenReady = isPlayWhenReady();
                    if (playWhenReady) {
                        boolean playing = isPlaying();
                        if (playing) {
                            if (LogUtil.DEBUG) {
                                LogUtil.log("VideoAndroidPlayer", "onSeekComplete -> warning: isPlaying true");
                            }
                            return;
                        }
                        start();
                    } else {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                        pause();
                    }
                }
                // 正常快进&快退
                else {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_FINISH);
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> onSeekComplete -> Exception " + e.getMessage());
                }
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> onPrepared ->");
            }

            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.MEDIA_INFO_PREPARE);
            start();
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> onCompletion ->");
            }
            stop();
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.END);
        }
    };


    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            try {
                if (null == mp) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onVideoSizeChanged -> error: MediaPlayer null");
                    }
                    return;
                }
                int videoWidth = mp.getVideoWidth();
                if (videoWidth <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onVideoSizeChanged -> error: videoWidth <= 0");
                    }
                    return;
                }
                int videoHeight = mp.getVideoHeight();
                if (videoHeight <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onVideoSizeChanged -> error: videoHeight <= 0");
                    }
                    return;
                }
                if (isVideoSizeChanged) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onVideoSizeChanged -> warning: isVideoSizeChanged true");
                    }
                    return;
                }
                StartArgs args = getStartArgs();
                if (null == args) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> onVideoSizeChanged -> error: args null");
                    }
                    return;
                }
                isVideoSizeChanged = true;
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
                onVideoFormatChanged(PlayerType.KernelType.ANDROID, rotation, scaleType, videoWidth, videoHeight, -1);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> onVideoSizeChanged -> " + e.getMessage());
                }
            }
        }
    };

    @Override
    public List<TrackInfo> getTrackInfo(int type) {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getTrackInfo -> warning: mBuild.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN");
                }
                return null;
            }
            if (null == mAndroidPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getTrackInfo -> error: mAndroidPlayer null");
                }
                return null;
            }
            MediaPlayer.TrackInfo[] trackInfos = mAndroidPlayer.getTrackInfo();
            if (null == trackInfos) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getTrackInfo -> error: trackInfos null");
                }
                return null;
            }
            if (trackInfos.length == 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoAndroidPlayer -> getTrackInfo -> warning: trackInfos.length == 0");
                }
                return null;
            }
            for (MediaPlayer.TrackInfo trackInfo : trackInfos) {
                if (null == trackInfo)
                    continue;
                String language = trackInfo.getLanguage();
                int trackType = trackInfo.getTrackType();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    MediaFormat format = trackInfo.getFormat();
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoAndroidPlayer -> getTrackInfo -> trackType = " + trackType + ", language = " + language + ", format = " + format);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> getTrackInfo -> " + e.getMessage());
            }
            return null;
        }
    }
}
