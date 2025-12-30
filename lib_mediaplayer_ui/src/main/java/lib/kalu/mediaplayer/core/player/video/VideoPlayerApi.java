package lib.kalu.mediaplayer.core.player.video;

public interface VideoPlayerApi extends
        VideoPlayerApiBase,
        VideoPlayerApiKernel,
        VideoPlayerApiDevice,
        VideoPlayerApiComponent,
        VideoPlayerApiCache,
        VideoPlayerApiRender,
        VideoPlayerApiWindow,
        VideoPlayerApiOrientation,
        VideoPlayerApiSubtitle,
        VideoPlayerApiCall {
}
