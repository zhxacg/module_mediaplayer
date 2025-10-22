package lib.kalu.mediax.renderers;

import android.content.Context;

import androidx.media3.common.util.UnstableApi;

import lib.kalu.mediax.util.MediaLogUtil;

@UnstableApi
public class VideoCodecAudioCodecRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioCodecRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("BaseRenderersFactory => BaseOnlyMediaCodecRenderersFactory =>");
    }
}
