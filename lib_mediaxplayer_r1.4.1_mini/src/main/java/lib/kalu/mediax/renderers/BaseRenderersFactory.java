package lib.kalu.mediax.renderers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.AudioRendererEventListener;
import androidx.media3.exoplayer.audio.AudioSink;
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;
import androidx.media3.exoplayer.text.TextOutput;
import androidx.media3.exoplayer.video.MediaCodecVideoRenderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.mediax.subtitle.OffsetMsTextRenderer;
import lib.kalu.mediax.util.MediaLogUtil;

@UnstableApi
public class BaseRenderersFactory extends DefaultRenderersFactory {



    public BaseRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(initRendererMode());
        MediaLogUtil.log("BaseRenderersFactory => BaseRenderersFactory =>");
    }

    @Override
    protected void buildTextRenderers(Context context, TextOutput output, Looper outputLooper, @ExtensionRendererMode int extensionRendererMode, ArrayList<Renderer> out) {
        // super.buildTextRenderers(context, output, outputLooper, extensionRendererMode, out);
        out.add(new OffsetMsTextRenderer(output, outputLooper));
    }

    @Override
    protected void buildAudioRenderers(@NonNull Context context,
                                       @ExtensionRendererMode int extensionRendererMode,
                                       @NonNull MediaCodecSelector mediaCodecSelector,
                                       @NonNull boolean enableDecoderFallback,
                                       @NonNull AudioSink audioSink,
                                       @NonNull Handler eventHandler,
                                       @NonNull AudioRendererEventListener eventListener,
                                       @NonNull ArrayList<Renderer> out) {
        logAudioRenderer(out);
        addAudioCodecRenderer(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
        logAudioRenderer(out);
    }


    @Override
    protected void buildVideoRenderers(@NonNull Context context,
                                       @ExtensionRendererMode int extensionRendererMode,
                                       @NonNull MediaCodecSelector mediaCodecSelector,
                                       @NonNull boolean enableDecoderFallback,
                                       @NonNull Handler eventHandler,
                                       @NonNull VideoRendererEventListener eventListener,
                                       @NonNull long allowedVideoJoiningTimeMs,
                                       @NonNull ArrayList<Renderer> out) {
        logVideoRenderer(out);
        addVideoCodecRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
        logVideoRenderer(out);
    }

    protected int initRendererMode() {
        return DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
    }

    protected void addAudioCodecRenderer(@NonNull Context context,
                                         @ExtensionRendererMode int extensionRendererMode,
                                         @NonNull MediaCodecSelector mediaCodecSelector,
                                         @NonNull boolean enableDecoderFallback,
                                         @NonNull AudioSink audioSink,
                                         @NonNull Handler eventHandler,
                                         @NonNull AudioRendererEventListener eventListener,
                                         @NonNull ArrayList<Renderer> out) {
        try {
            MediaCodecAudioRenderer audioRenderer = new MediaCodecAudioRenderer(
                    context,
                    getCodecAdapterFactory(),
                    mediaCodecSelector,
                    enableDecoderFallback,
                    eventHandler,
                    eventListener,
                    audioSink);
            out.add(audioRenderer);
        } catch (Exception e) {
            MediaLogUtil.log("BaseRenderersFactory => addAudioCodecRenderer => " + e.getMessage());
        }
    }

    protected void addVideoCodecRenderers(@NonNull Context context,
                                          @ExtensionRendererMode int extensionRendererMode,
                                          @NonNull MediaCodecSelector mediaCodecSelector,
                                          @NonNull boolean enableDecoderFallback,
                                          @NonNull Handler eventHandler,
                                          @NonNull VideoRendererEventListener eventListener,
                                          @NonNull long allowedVideoJoiningTimeMs,
                                          @NonNull ArrayList<Renderer> out) {
        try {
            MediaCodecVideoRenderer videoRenderer = new MediaCodecVideoRenderer(
                    context,
                    getCodecAdapterFactory(),
                    mediaCodecSelector,
                    allowedVideoJoiningTimeMs,
                    enableDecoderFallback,
                    eventHandler,
                    eventListener,
                    MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
            out.add(videoRenderer);
        } catch (Exception e) {
            MediaLogUtil.log("BaseRenderersFactory => addVideoCodecRenderers => " + e.getMessage());
        }
    }

    private void logAudioRenderer(@NonNull ArrayList<Renderer> out) {
        try {
            if (null == out || out.size() == 0)
                throw new Exception("out warning: " + out);
            MediaLogUtil.log("BaseRenderersFactory => logAudioRenderer => -------start---------");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                MediaLogUtil.log("BaseRenderersFactory => logAudioRenderer => r = " + r);
            }
            MediaLogUtil.log("BaseRenderersFactory => logAudioRenderer => --------end---------");
        } catch (Exception e) {
            MediaLogUtil.log("BaseRenderersFactory => logAudioRenderer => " + e.getMessage());
        }
    }

    private void logVideoRenderer(@NonNull ArrayList<Renderer> out) {
        try {
            if (null == out || out.size() == 0)
                throw new Exception("out warning: " + out);
            MediaLogUtil.log("BaseRenderersFactory => logVideoRenderer => -------start---------");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                MediaLogUtil.log("BaseRenderersFactory => logVideoRenderer => r = " + r);
            }
            MediaLogUtil.log("BaseRenderersFactory => logVideoRenderer => --------end---------");
        } catch (Exception e) {
            MediaLogUtil.log("BaseRenderersFactory => logVideoRenderer => " + e.getMessage());
        }
    }
}
