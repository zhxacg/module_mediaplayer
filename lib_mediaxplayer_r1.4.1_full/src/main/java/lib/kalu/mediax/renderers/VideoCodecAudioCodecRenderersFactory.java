package lib.kalu.mediax.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.mediax.util.MediaLogUtil;

@UnstableApi
public final class VideoCodecAudioCodecRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioCodecRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("DefaultRenderersFactory => VideoCodecAudioCodecRenderersFactory =>");
    }

    @Override
    protected void addVideoFFmpeg(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioFFmpeg(@NonNull ArrayList<Renderer> out) {
    }
}
