package lib.kalu.mediaplayer.compose;
import android.view.ViewGroup;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
/** Read-only adapter for kernel adaptive-track queries; Compose owns rendering. */
final class RenderMetadata implements VideoRenderApi {
    private VideoKernelApi kernel;
    @Override public void setVideoKernel(VideoKernelApi value) { kernel = value; }
    @Override public VideoKernelApi getVideoKernel() { return kernel; }
    private int width;
    @Override public void updateVideoWidth(int value) { width = value; }
    @Override public int getVideoWidth() { return width; }
    private int height;
    @Override public void updateVideoHeight(int value) { height = value; }
    @Override public int getVideoHeight() { return height; }
    private int bitrate;
    @Override public void updateVideoBitrate(int value) { bitrate = value; }
    @Override public int getVideoBitrate() { return bitrate; }
    private int rotation;
    @Override public void updateVideoRotation(int value) { rotation = value; }
    @Override public int getVideoRotation() { return rotation; }
    private int scaletype;
    @Override public void updateVideoScaleType(int value) { scaletype = value; }
    @Override public int getVideoScale() { return scaletype; }
    @Override public void registListener() { }
    @Override public void unRegistListener() { }
    @Override public void setSurface(boolean release) { }
    @Override public void reset() { }
    @Override public void release() { kernel = null; }
    @Override public void setLayoutParams(ViewGroup.LayoutParams params) { throw new UnsupportedOperationException("Compose layout"); }
    @Override public void setScaleX(float value) { throw new UnsupportedOperationException("Compose layout"); }
    @Override public String screenshot(String url, long position) { return null; }
    @Override public void setFixedSize(int width, int height) { }
}
