package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;

import java.util.List;

import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiBase {

    void setKernelApi(VideoKernelApiEvent eventApi);

    VideoKernelApiEvent getKernelApi();

    void setPlayerApi(VideoPlayerApi playerApi);

    VideoPlayerApi getPlayerApi();

    /*****/

    void setVolume(float left, float right);

    float getVolume();

    void seekTo(long position);

    void setSpeed(float speed);

    float getSpeed();

    void start();

    void pause();

    void stop();

    void release();

    boolean isBuffering();

    boolean isPlaying();

    long getPosition();

    long getDuration();

    boolean isPrepared();

    default boolean isUseCache() {
        return false;
    }

    default boolean toggleTrack(TrackInfo trackInfo) {
        return false;
    }

    default List<TrackInfo> getTrackInfo(int type) {
        return null;
    }

    default List<TrackInfo> getTrackInfoAll() {
        return getTrackInfo(-1);
    }

    default List<TrackInfo> getTrackInfoVideo() {
        return getTrackInfo(1);
    }

    default List<TrackInfo> getTrackInfoAudio() {
        return getTrackInfo(2);
    }

    default List<TrackInfo> getTrackInfoSubtitle() {
        return getTrackInfo(3);
    }

    default void initDecoderPlayWhenReadyDelayed(Context context) {
    }
}