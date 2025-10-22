package lib.kalu.mediax.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.AudioRendererEventListener;
import androidx.media3.exoplayer.audio.AudioSink;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;

import java.util.ArrayList;

import lib.kalu.mediax.util.MediaLogUtil;

@UnstableApi
public class OnlyVideoCodecRenderersFactory extends BaseRenderersFactory {

    public OnlyVideoCodecRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("BaseRenderersFactory => BaseOnlyMediaCodecVideoRenderersFactory =>");
    }

    @Override
    protected void addAudioCodecRenderer(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull AudioSink audioSink, @NonNull Handler eventHandler, @NonNull AudioRendererEventListener eventListener, @NonNull ArrayList<Renderer> out) {
    }
}
