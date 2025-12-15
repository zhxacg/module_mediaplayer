package lib.kalu.mediaplayer.core.player.video;

public interface VideoPlayerApi extends VideoPlayerApiBuried,
        VideoPlayerApiBase,
        VideoPlayerApiKernel,
        VideoPlayerApiDevice,
        VideoPlayerApiComponent,
        VideoPlayerApiCache,
        VideoPlayerApiRender,
        VideoPlayerLifecycle,
        VideoPlayerApiWindow,
        VideoPlayerApiOrientation,
        VideoPlayerApiSubtitle {
}
