package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public final class OnlyAudioCodecRenderersFactory extends BaseRenderersFactory {

    public OnlyAudioCodecRenderersFactory(Context context) {
        super(context);
        ExoLogUtil.log("DefaultRenderersFactory => OnlyAudioCodecRenderersFactory =>");
    }

    @Override
    protected void addAudioFFmpeg(@NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addVideoCodec(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull Handler eventHandler, @NonNull VideoRendererEventListener eventListener, @NonNull long allowedVideoJoiningTimeMs, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addVideoFFmpeg(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }
}
