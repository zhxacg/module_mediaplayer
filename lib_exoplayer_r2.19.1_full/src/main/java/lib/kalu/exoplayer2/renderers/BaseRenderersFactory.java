package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegVideoRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.subtitle.OffsetMsTextRenderer;
import lib.kalu.exoplayer2.util.ExoLogUtil;

public class BaseRenderersFactory extends com.google.android.exoplayer2.DefaultRenderersFactory {

    public BaseRenderersFactory(Context context) {
        super(context);
        /**
         * EXTENSION_RENDERER_MODE_OFF, 扩展模块渲染器处于禁用状态。
         * EXTENSION_RENDERER_MODE_ON, 扩展渲染器处于启用状态（默认）。
         * EXTENSION_RENDERER_MODE_PREFER, 如果扩展渲染器支持相同的格式，则扩展渲染器优先于内置渲染器。
         * @return
         */
        setExtensionRendererMode(com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        ExoLogUtil.log("DefaultRenderersFactory => AllRenderersFactory =>");
    }

    @Override
    protected void buildTextRenderers(Context context, TextOutput output, Looper outputLooper, @ExtensionRendererMode int extensionRendererMode, ArrayList<Renderer> out) {
        // super.buildTextRenderers(context, output, outputLooper, extensionRendererMode, out);
        //
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
        log(out);
        addAudioCodec(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
        addAudioFFmpeg(out);
        log(out);
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
        log(out);
        addVideoCodec(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
        addVideoFFmpeg(allowedVideoJoiningTimeMs, eventHandler, eventListener, 50, out);
        log(out);
    }

    protected void addAudioCodec(@NonNull Context context,
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
            ExoLogUtil.log("AllRenderersFactory => addAudioCodec => " + e.getMessage());
        }
    }

    protected void addAudioFFmpeg(@NonNull ArrayList<Renderer> out) {
        try {
            FfmpegAudioRenderer ffmpegAudioRenderer = new FfmpegAudioRenderer();
            out.add(ffmpegAudioRenderer);
        } catch (Exception e) {
            ExoLogUtil.log("AllRenderersFactory => addAudioFFmpeg => " + e.getMessage());
        }
    }

    protected void addVideoCodec(@NonNull Context context,
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
            ExoLogUtil.log("AllRenderersFactory => addVideoCodec => " + e.getMessage());
        }
    }

    protected void addVideoFFmpeg(long allowedJoiningTimeMs,
                                  @Nullable Handler eventHandler,
                                  @Nullable VideoRendererEventListener eventListener,
                                  int maxDroppedFramesToNotify,
                                  @NonNull ArrayList<Renderer> out) {
        try {
            FfmpegVideoRenderer ffmpegVideoRenderer = new FfmpegVideoRenderer(allowedJoiningTimeMs, eventHandler, eventListener, maxDroppedFramesToNotify);
            out.add(ffmpegVideoRenderer);
        } catch (Exception e) {
            ExoLogUtil.log("AllRenderersFactory => addVideoFFmpeg => " + e.getMessage());
        }
    }

    private void log(ArrayList<Renderer> out) {
        try {
            if (null == out)
                throw new Exception("warning: out null");
            if (out.isEmpty())
                throw new Exception("warning: out empty");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                ExoLogUtil.log("AllRenderersFactory => log => r = " + r);
            }
        } catch (Exception e) {
            ExoLogUtil.log("AllRenderersFactory => log => " + e.getMessage());
        }
    }
}
