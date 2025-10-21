package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public final class OnlyVideoCodecRenderersFactory extends BaseRenderersFactory {

    public OnlyVideoCodecRenderersFactory(Context context) {
        super(context);
        ExoLogUtil.log("DefaultRenderersFactory => OnlyVideoCodecRenderersFactory =>");
    }

    @Override
    protected void addVideoFFmpeg(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioFFmpeg(@NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioCodec(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull AudioSink audioSink, @NonNull Handler eventHandler, @NonNull AudioRendererEventListener eventListener, @NonNull ArrayList<Renderer> out) {
    }
}
