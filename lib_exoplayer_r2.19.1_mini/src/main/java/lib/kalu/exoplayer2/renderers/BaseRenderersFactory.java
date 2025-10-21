package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.subtitle.OffsetSubtitleRenderer;
import lib.kalu.exoplayer2.util.ExoLogUtil;

class BaseRenderersFactory extends com.google.android.exoplayer2.DefaultRenderersFactory {

    public BaseRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(initRendererMode());
        ExoLogUtil.log("BaseRenderersFactory => BaseRenderersFactory =>");
    }

    @Override
    protected void buildTextRenderers(Context context, TextOutput output, Looper outputLooper, @ExtensionRendererMode int extensionRendererMode, ArrayList<Renderer> out) {
        // super.buildTextRenderers(context, output, outputLooper, extensionRendererMode, out);
        //
        out.add(new OffsetSubtitleRenderer(output, outputLooper));
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
        return com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
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
            ExoLogUtil.log("BaseRenderersFactory => addAudioCodecRenderer => " + e.getMessage());
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
            ExoLogUtil.log("BaseRenderersFactory => addVideoCodecRenderers => " + e.getMessage());
        }
    }

    private void logAudioRenderer(@NonNull ArrayList<Renderer> out) {
        try {
            if (null == out || out.size() == 0)
                throw new Exception("out warning: " + out);
            ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => -------start---------");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => r = " + r);
            }
            ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => --------end---------");
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => " + e.getMessage());
        }
    }

    private void logVideoRenderer(@NonNull ArrayList<Renderer> out) {
        try {
            if (null == out || out.size() == 0)
                throw new Exception("out warning: " + out);
            ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => -------start---------");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => r = " + r);
            }
            ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => --------end---------");
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => " + e.getMessage());
        }
    }
}
