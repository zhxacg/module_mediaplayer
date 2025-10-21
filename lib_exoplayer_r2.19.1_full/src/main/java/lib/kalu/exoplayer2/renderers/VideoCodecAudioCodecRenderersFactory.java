package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public final class VideoCodecAudioCodecRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioCodecRenderersFactory(Context context) {
        super(context);
        ExoLogUtil.log("DefaultRenderersFactory => VideoCodecAudioCodecRenderersFactory =>");
    }

    @Override
    protected void addVideoFFmpeg(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioFFmpeg(@NonNull ArrayList<Renderer> out) {
    }
}
