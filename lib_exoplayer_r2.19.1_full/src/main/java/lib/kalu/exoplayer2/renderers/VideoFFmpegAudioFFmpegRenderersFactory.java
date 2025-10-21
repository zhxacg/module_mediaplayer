package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public final class VideoFFmpegAudioFFmpegRenderersFactory extends BaseRenderersFactory {

    public VideoFFmpegAudioFFmpegRenderersFactory(Context context) {
        super(context);
        ExoLogUtil.log("DefaultRenderersFactory => VideoFFmpegAudioFFmpegRenderersFactory =>");
    }

    @Override
    protected void addVideoCodec(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull Handler eventHandler, @NonNull VideoRendererEventListener eventListener, @NonNull long allowedVideoJoiningTimeMs, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioCodec(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull AudioSink audioSink, @NonNull Handler eventHandler, @NonNull AudioRendererEventListener eventListener, @NonNull ArrayList<Renderer> out) {
    }
}
