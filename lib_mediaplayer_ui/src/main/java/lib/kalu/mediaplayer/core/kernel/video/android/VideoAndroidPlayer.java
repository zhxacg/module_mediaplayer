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
    public VideoAndroidPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mAndroidPlayer)
                throw new Exception("error: mAndroidPlayer null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> releaseDecoder ->");
            }
            if (isFromUser) {
                setEvent(null);
            }
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
            if (null != mAndroidPlayer)
                throw new Exception("error: mAndroidPlayer not null");
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
            if (null == mAndroidPlayer)
                throw new Exception("error: mAndroidPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> startDecoder ->");
            }
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.INIT_READY);

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
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.ERROR_PREPARE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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

    //    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mAndroidPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log("VideoAndroidPlayer -> " + e.getMessage());
//        }
//    }

    @Override
    public void release() {
        try {
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            float volume = Math.max(v1, v2);
            if (volume < 0)
                throw new Exception("error: volume < 0");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
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
            if (seek < 0L)
                throw new Exception("error: seek<0");
            if (null == mAndroidPlayer)
                throw new Exception("error: mAndroidPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> seekTo ->");
            }
            long duration = getDuration();
            if (duration > 0L && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            onEvent(PlayerType.KernelType.ANDROID, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);

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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            int currentPosition = mAndroidPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            //  LogUtil.log("VideoAndroidPlayer -> getPosition ->");
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            int duration = mAndroidPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            // LogUtil.log("VideoAndroidPlayer -> getDuration ->");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            if (null == surface)
                throw new Exception("surface error: null");
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setSpeed ->");
            }
            PlaybackParams playbackParams = mAndroidPlayer.getPlaybackParams();
            if (null != playbackParams) {
                playbackParams = new PlaybackParams();
            }
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
            if (null == mAndroidPlayer)
                throw new Exception("mAndroidPlayer error: null");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoAndroidPlayer -> setSpeed ->");
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
                    throw new Exception("what warning: " + what);
                } else if (what == -10005) {
                    throw new Exception("what warning: " + what);
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
                    if (!isPrepared)
                        throw new Exception("warning: isPrepared false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.BUFFERING_START);
                }
                // 缓冲结束
                else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    if (!isPrepared)
                        throw new Exception("warning: isPrepared false");
                    isBuffering = false;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.BUFFERING_STOP);
                }
                // 开始播放
                else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START || what == 903) {
                    if (isPrepared)
                        throw new Exception("warning: mPrepared true");
                    isPrepared = true;
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.VIDEO_RENDERING_START);
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
                        onEvent(PlayerType.KernelType.ANDROID, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                        if (playWhenReady) {
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.START);
                            boolean playing = isPlaying();
                            if (playing)
                                throw new Exception("warning: isPlaying true");
                            start();
                        } else {
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.PAUSE_PlAY_WHEN_READY);
                            pause();
                        }
                    }
                    // 起播快进
                    else {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.SEEK_START_REWIND);
                        mPlayWhenReadySeeking = true;
                        seekTo(seek);
                    }
                }
                // not find
                else {
                    throw new Exception("warning: not find");
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
                    onEvent(PlayerType.KernelType.ANDROID, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                    if (playWhenReady) {
                        boolean playing = isPlaying();
                        if (playing)
                            throw new Exception("warning: isPlaying true");
                        start();
                    } else {
                        pause();
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.PAUSE);
                    }
                }
                // 正常快进&快退
                else {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.SEEK_FINISH);
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

            // 解决部分盒子不回调 info code=3
            // sendMessageCheckPreparedPlaying(PlayerType.KernelType.ANDROID);

            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.PREPARE);
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
//            LogUtil.log("VideoAndroidPlayer -> onBufferingUpdate -> percent = " + percent);
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
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
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
                throw new Exception("warning: mBuild.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN");
            if (null == mAndroidPlayer)
                throw new Exception("error: mAndroidPlayer null");
            MediaPlayer.TrackInfo[] trackInfos = mAndroidPlayer.getTrackInfo();
            if (null == trackInfos)
                throw new Exception("error: trackInfos null");
            if (trackInfos.length == 0)
                throw new Exception("warning: trackInfos.length == 0");
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
