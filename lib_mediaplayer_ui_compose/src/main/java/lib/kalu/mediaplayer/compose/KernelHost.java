package lib.kalu.mediaplayer.compose;

import android.content.Context;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;

/** Only the kernel's host contract. No View is constructed or cast. */
final class KernelHost implements VideoPlayerApi {
    private final PlayerController controller;
    private final Context context;
    private VideoKernelApi kernel;
    private VideoRenderApi render;
    KernelHost(Context context, PlayerController controller) {
        this.context = context;
        this.controller = controller;
    }
    @Override public Context getBaseContext() { return context; }
    @Override public StartArgs getStartArgs() { return controller.getStartArgs(); }
    @Override public VideoKernelApi getVideoKernel() { return kernel; }
    @Override public void setVideoKernel(VideoKernelApi kernel) { this.kernel = kernel; }
    @Override public VideoRenderApi getVideoRender() { return render; }
    @Override public void setVideoRender(VideoRenderApi render) { this.render = render; }
    @Override public void start(StartArgs args) { controller.start(args); }
    @Override public void stop(boolean callEvent) { controller.stop(callEvent); }
    @Override public void callEvent(int event) { controller.sendSelfEvent(event); }
    @Override public void callEvent(boolean callPlayer, boolean callComponent, int event) {
        if (callPlayer) controller.sendSelfEvent(event);
    }
    @Override public void callScreenOrientation(boolean callPlayer, boolean callComponent, int value) {
        // The controller notifies the orientation listener after the Activity accepts the request.
    }
    @Override public void checkVideoVisibility() { }
    @Override public void initRenderView() { }
    @Override public void setScreenKeep(boolean enable) { }
    @Override public boolean isFull() { return controller.isFull(); }
    @Override public boolean isFloat() { return controller.isFloat(); }
}
